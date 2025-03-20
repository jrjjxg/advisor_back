package com.advisor.service.community;

import com.advisor.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 点赞服务接口
 */
public interface LikeService {
    
    /**
     * 点赞/取消点赞
     *
     * @param targetId 目标ID
     * @param type     类型：1-帖子，2-评论
     * @param userId   用户ID
     * @return 是否点赞
     */
    boolean toggleLike(String targetId, Integer type, String userId);
    
    /**
     * 检查用户是否点赞
     *
     * @param targetId 目标ID
     * @param type     类型：1-帖子，2-评论
     * @param userId   用户ID
     * @return 是否已点赞
     */
    boolean checkUserLiked(String targetId, Integer type, String userId);
    
    /**
     * 获取点赞用户列表
     *
     * @param targetId  目标ID
     * @param type      类型：1-帖子，2-评论
     * @param pageNum   页码
     * @param pageSize  每页大小
     * @return 用户列表分页结果
     */
    Page<UserVO> getLikeUsers(String targetId, Integer type, Integer pageNum, Integer pageSize);
}