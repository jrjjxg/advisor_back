package com.advisor.service.impl.community;


import com.advisor.dto.CommentCreateRequest;
import com.advisor.entity.base.User;
import com.advisor.entity.community.Comment;
import com.advisor.entity.community.Post;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.community.CommentMapper;
import com.advisor.mapper.community.LikeRecordMapper;
import com.advisor.mapper.community.PostMapper;
import com.advisor.service.community.CommentService;
import com.advisor.service.community.NotificationService;
import com.advisor.vo.community.CommentVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 评论服务实现类
 */
@Slf4j
@Service
public class CommentServiceImpl extends ServiceImpl<CommentMapper, Comment> implements CommentService {
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public String createComment(CommentCreateRequest request, String userId) {
        // 查询帖子是否存在
        Post post = postMapper.selectById(request.getPostId());
        if (post == null || post.getStatus() == 0) {
            throw new RuntimeException("帖子不存在或已删除");
        }
        
        // 创建评论
        Comment comment = new Comment();
        comment.setPostId(request.getPostId());
        comment.setUserId(userId);
        comment.setContent(request.getContent());
        comment.setParentId(request.getParentId());
        comment.setReplyUserId(request.getReplyUserId());
        comment.setLikeCount(0);
        comment.setStatus(1);
        comment.setCreateTime(LocalDateTime.now());
        
        // 保存评论
        commentMapper.insert(comment);
        
        // 更新帖子评论数
        postMapper.updateCommentCount(request.getPostId(), 1);
        
        // 发送通知
        if (request.getParentId() == null) {
            // 一级评论，通知帖子作者
            if (!post.getUserId().equals(userId)) {
                notificationService.createNotification(
                        post.getUserId(),
                        userId,
                        "comment",
                        comment.getId(),
                        "评论了你的帖子"
                );
            }
        } else {
            // 回复评论，通知被回复的用户
            if (request.getReplyUserId() != null && !request.getReplyUserId().equals(userId)) {
                notificationService.createNotification(
                        request.getReplyUserId(),
                        userId,
                        "reply",
                        comment.getId(),
                        "回复了你的评论"
                );
            }
        }
        
        return comment.getId();
    }
    
    @Override
    public Page<CommentVO> getPostComments(String postId, String userId, Integer pageNum, Integer pageSize) {
        // 查询一级评论
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getPostId, postId)
                .isNull(Comment::getParentId)
                .eq(Comment::getStatus, 1)
                .orderByDesc(Comment::getCreateTime);
        
        // 分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        return convertToCommentVOPage(commentPage, userId);
    }
    
    @Override
    public Page<CommentVO> getCommentReplies(String commentId, String userId, Integer pageNum, Integer pageSize) {
        // 查询回复
        LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Comment::getParentId, commentId)
                .eq(Comment::getStatus, 1)
                .orderByAsc(Comment::getCreateTime);
        
        // 分页查询
        Page<Comment> page = new Page<>(pageNum, pageSize);
        Page<Comment> commentPage = commentMapper.selectPage(page, queryWrapper);
        
        // 转换为VO
        return convertToCommentVOPage(commentPage, userId);
    }
    
    @Override
    @Transactional
    public void deleteComment(String commentId, String userId) {
        // 查询评论
        Comment comment = commentMapper.selectById(commentId);
        if (comment == null || !comment.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除该评论");
        }
        
        // 逻辑删除评论
        comment.setStatus(0);
        commentMapper.updateById(comment);
        
        // 更新帖子评论数
        postMapper.updateCommentCount(comment.getPostId(), -1);
        
        // 如果是一级评论，还需要删除其下的所有回复
        if (comment.getParentId() == null) {
            LambdaQueryWrapper<Comment> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(Comment::getParentId, commentId)
                    .eq(Comment::getStatus, 1);
            
            List<Comment> replies = commentMapper.selectList(queryWrapper);
            if (!replies.isEmpty()) {
                for (Comment reply : replies) {
                    reply.setStatus(0);
                    commentMapper.updateById(reply);
                    
                    // 更新帖子评论数
                    postMapper.updateCommentCount(comment.getPostId(), -1);
                }
            }
        }
    }
    
    /**
     * 将Comment实体转换为CommentVO
     */
    private CommentVO convertToCommentVO(Comment comment, String userId) {
        CommentVO commentVO = new CommentVO();
        BeanUtils.copyProperties(comment, commentVO);
        
        // 查询评论用户信息
        User user = userMapper.selectById(comment.getUserId());
        if (user != null) {
            commentVO.setUsername(user.getUsername());
            commentVO.setAvatar(user.getAvatar());
        }
        
        // 查询回复用户信息
        if (comment.getReplyUserId() != null) {
            User replyUser = userMapper.selectById(comment.getReplyUserId());
            if (replyUser != null) {
                commentVO.setReplyUsername(replyUser.getUsername());
            }
        }
        
        // 查询当前用户是否点赞
        if (userId != null) {
            LambdaQueryWrapper<com.advisor.entity.community.LikeRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(com.advisor.entity.community.LikeRecord::getUserId, userId)
                    .eq(com.advisor.entity.community.LikeRecord::getTargetId, comment.getId())
                    .eq(com.advisor.entity.community.LikeRecord::getType, 2); // 2-评论
            
            commentVO.setIsLiked(likeRecordMapper.selectCount(queryWrapper) > 0);
        } else {
            commentVO.setIsLiked(false);
        }
        
        // 初始化子评论列表
        commentVO.setChildren(new ArrayList<>());
        
        return commentVO;
    }
    
    /**
     * 将Comment分页结果转换为CommentVO分页结果
     */
    private Page<CommentVO> convertToCommentVOPage(Page<Comment> commentPage, String userId) {
        Page<CommentVO> voPage = new Page<>(commentPage.getCurrent(), commentPage.getSize(), commentPage.getTotal());
        
        List<CommentVO> voList = commentPage.getRecords().stream()
                .map(comment -> convertToCommentVO(comment, userId))
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
}