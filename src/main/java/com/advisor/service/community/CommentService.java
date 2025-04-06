package com.advisor.service.community;


import com.advisor.dto.CommentCreateRequest;
import com.advisor.entity.community.Comment;
import com.advisor.vo.community.CommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 评论服务接口
 */
public interface CommentService {
    
    /**
     * 创建评论
     *
     * @param request 创建评论请求
     * @param userId  用户ID
     * @return 评论ID
     */
    String createComment(CommentCreateRequest request, String userId);
    
    /**
     * 获取帖子评论列表
     *
     * @param postId   帖子ID
     * @param userId   当前用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 评论列表分页结果
     */
    Page<CommentVO> getPostComments(String postId, String userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取评论的回复列表
     *
     * @param commentId 评论ID
     * @param userId    当前用户ID
     * @param pageNum   页码
     * @param pageSize  每页大小
     * @return 回复列表分页结果
     */
    Page<CommentVO> getCommentReplies(String commentId, String userId, Integer pageNum, Integer pageSize);
    
    /**
     * 删除评论
     *
     * @param commentId 评论ID
     * @param userId    用户ID
     */
    void deleteComment(String commentId, String userId);
    
    /**
     * 获取评论详情，主要用于获取评论所属的帖子ID
     *
     * @param commentId 评论ID
     * @return 评论实体
     */
    Comment getCommentInfo(String commentId);
}