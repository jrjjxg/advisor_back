package com.advisor.service.community;

import com.advisor.dto.community.PostCreateRequest;
import com.advisor.dto.community.PostQueryRequest;
import com.advisor.vo.community.PostVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 帖子服务接口
 */
public interface PostService {
    
    /**
     * 创建帖子
     *
     * @param request 创建帖子请求
     * @param userId  用户ID
     * @return 帖子ID
     */
    String createPost(PostCreateRequest request, String userId);
    
    /**
     * 获取帖子详情
     *
     * @param postId 帖子ID
     * @param userId 当前用户ID
     * @return 帖子详情
     */
    PostVO getPostDetail(String postId, String userId);
    
    /**
     * 获取帖子列表
     *
     * @param request 查询请求
     * @param userId  当前用户ID
     * @return 帖子列表分页结果
     */
    Page<PostVO> getPostList(PostQueryRequest request, String userId);
    
    /**
     * 获取用户发布的帖子
     *
     * @param targetUserId 目标用户ID
     * @param userId       当前用户ID
     * @param pageNum      页码
     * @param pageSize     每页大小
     * @return 帖子列表分页结果
     */
    Page<PostVO> getUserPosts(String targetUserId, String userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取用户点赞的帖子
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 帖子列表分页结果
     */
    Page<PostVO> getUserLikedPosts(String userId, Integer pageNum, Integer pageSize);
    
    /**
     * 删除帖子
     *
     * @param postId 帖子ID
     * @param userId 用户ID
     */
    void deletePost(String postId, String userId);
    

    /**
     * 增加帖子浏览量
     *
     * @param postId 帖子ID
     */
    void incrementViewCount(String postId);
}