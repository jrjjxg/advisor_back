package com.advisor.service.impl.community;

import com.advisor.entity.base.User;
import com.advisor.entity.community.Comment;
import com.advisor.entity.community.LikeRecord;
import com.advisor.entity.community.Post;
import com.advisor.mapper.base.UserMapper;
import com.advisor.mapper.community.CommentMapper;
import com.advisor.mapper.community.LikeRecordMapper;
import com.advisor.mapper.community.PostMapper;
import com.advisor.service.community.LikeService;
import com.advisor.service.community.NotificationService;
import com.advisor.vo.UserVO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 点赞服务实现类
 */
@Slf4j
@Service
public class LikeServiceImpl extends ServiceImpl<LikeRecordMapper, LikeRecord> implements LikeService {
    
    @Autowired
    private LikeRecordMapper likeRecordMapper;
    
    @Autowired
    private PostMapper postMapper;
    
    @Autowired
    private CommentMapper commentMapper;
    
    @Autowired
    private UserMapper userMapper;
    
    @Autowired
    private NotificationService notificationService;
    
    @Override
    @Transactional
    public boolean toggleLike(String targetId, Integer type, String userId) {
        // 查询是否已点赞
        LambdaQueryWrapper<LikeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetId, targetId)
                .eq(LikeRecord::getType, type);
        
        LikeRecord record = likeRecordMapper.selectOne(queryWrapper);
        
        if (record != null) {
            // 已点赞，取消点赞
            likeRecordMapper.deleteById(record.getId());
            
            // 更新点赞数
            if (type == 1) { // 帖子
                postMapper.updateLikeCount(targetId, -1);
            } else if (type == 2) { // 评论
                commentMapper.updateLikeCount(targetId, -1);
            }
            
            return false;
        } else {
            // 未点赞，添加点赞
            record = new LikeRecord();
            record.setUserId(userId);
            record.setTargetId(targetId);
            record.setType(type);
            record.setCreateTime(LocalDateTime.now());
            likeRecordMapper.insert(record);
            
            // 更新点赞数
            if (type == 1) { // 帖子
                postMapper.updateLikeCount(targetId, 1);
                
                // 发送通知
                Post post = postMapper.selectById(targetId);
                if (post != null && !post.getUserId().equals(userId)) {
                    notificationService.createNotification(
                            post.getUserId(),
                            userId,
                            "like",
                            targetId,
                            "点赞了你的帖子"
                    );
                }
            } else if (type == 2) { // 评论
                commentMapper.updateLikeCount(targetId, 1);
                
                // 发送通知
                Comment comment = commentMapper.selectById(targetId);
                if (comment != null && !comment.getUserId().equals(userId)) {
                    notificationService.createNotification(
                            comment.getUserId(),
                            userId,
                            "like",
                            targetId,
                            "点赞了你的评论"
                    );
                }
            }
            
            return true;
        }
    }
    
    @Override
    public boolean checkUserLiked(String targetId, Integer type, String userId) {
        LambdaQueryWrapper<LikeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LikeRecord::getUserId, userId)
                .eq(LikeRecord::getTargetId, targetId)
                .eq(LikeRecord::getType, type);
        
        return likeRecordMapper.selectCount(queryWrapper) > 0;
    }
    
    @Override
    public Page<UserVO> getLikeUsers(String targetId, Integer type, Integer pageNum, Integer pageSize) {
        // 查询点赞记录
        LambdaQueryWrapper<LikeRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(LikeRecord::getTargetId, targetId)
                .eq(LikeRecord::getType, type)
                .orderByDesc(LikeRecord::getCreateTime);
        
        Page<LikeRecord> page = new Page<>(pageNum, pageSize);
        Page<LikeRecord> recordPage = likeRecordMapper.selectPage(page, queryWrapper);
        
        // 转换为UserVO
        Page<UserVO> voPage = new Page<>(recordPage.getCurrent(), recordPage.getSize(), recordPage.getTotal());
        
        List<UserVO> voList = recordPage.getRecords().stream()
                .map(record -> {
                    User user = userMapper.selectById(record.getUserId());
                    UserVO vo = new UserVO();
                    if (user != null) {
                        BeanUtils.copyProperties(user, vo);
                    }
                    return vo;
                })
                .collect(Collectors.toList());
        
        voPage.setRecords(voList);
        return voPage;
    }
}