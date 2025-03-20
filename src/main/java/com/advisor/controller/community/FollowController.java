package com.advisor.controller.community;

import com.advisor.common.Result;
import com.advisor.service.community.FollowService;
import com.advisor.util.UserUtil;
import com.advisor.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 关注控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community/follow")
@Api(tags = "社区关注接口")
public class FollowController {
    
    @Autowired
    private FollowService followService;
    
    @ApiOperation("关注/取消关注")
    @PostMapping("/{targetUserId}")
    public Result<Boolean> toggleFollow(@PathVariable String targetUserId) {
        String userId = UserUtil.getCurrentUserId();
        boolean isFollowed = followService.toggleFollow(targetUserId, userId);
        return Result.success(isFollowed);
    }
    
    @ApiOperation("检查是否关注")
    @GetMapping("/check/{targetUserId}")
    public Result<Boolean> checkFollowed(@PathVariable String targetUserId) {
        String userId = UserUtil.getCurrentUserId();
        boolean isFollowed = followService.checkFollowed(targetUserId, userId);
        return Result.success(isFollowed);
    }
    
    @ApiOperation("获取关注列表")
    @GetMapping("/following/{userId}")
    public Result<Page<UserVO>> getFollowingList(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<UserVO> page = followService.getFollowingList(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("获取粉丝列表")
    @GetMapping("/followers/{userId}")
    public Result<Page<UserVO>> getFollowerList(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<UserVO> page = followService.getFollowerList(userId, pageNum, pageSize);
        return Result.success(page);
    }
}