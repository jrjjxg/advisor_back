package com.advisor.service.community;

import com.advisor.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 关注服务接口
 */
public interface FollowService {
    
    /**
     * 关注/取消关注
     *
     * @param targetUserId 目标用户ID
     * @param userId       当前用户ID
     * @return 是否关注
     */
    boolean toggleFollow(String targetUserId, String userId);
    
    /**
     * 检查是否已关注
     *
     * @param targetUserId 目标用户ID
     * @param userId       当前用户ID
     * @return 是否已关注
     */
    boolean checkFollowed(String targetUserId, String userId);
    
    /**
     * 获取关注列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 用户列表分页结果
     */
    Page<UserVO> getFollowingList(String userId, Integer pageNum, Integer pageSize);
    
    /**
     * 获取粉丝列表
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页大小
     * @return 用户列表分页结果
     */
    Page<UserVO> getFollowerList(String userId, Integer pageNum, Integer pageSize);
}