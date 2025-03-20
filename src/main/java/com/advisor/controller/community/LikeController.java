package com.advisor.controller.community;

import com.advisor.common.Result;
import com.advisor.service.community.LikeService;
import com.advisor.util.UserUtil;
import com.advisor.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 点赞控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community/like")
@Api(tags = "社区点赞接口")
public class LikeController {
    
    @Autowired
    private LikeService likeService;
    
    @ApiOperation("点赞/取消点赞")
    @PostMapping("/{type}/{targetId}")
    public Result<Boolean> toggleLike(
            @PathVariable Integer type,
            @PathVariable String targetId) {
        String userId = UserUtil.getCurrentUserId();
        boolean isLiked = likeService.toggleLike(targetId, type, userId);
        return Result.success(isLiked);
    }
    
    @ApiOperation("检查是否点赞")
    @GetMapping("/check/{type}/{targetId}")
    public Result<Boolean> checkUserLiked(
            @PathVariable Integer type,
            @PathVariable String targetId) {
        String userId = UserUtil.getCurrentUserId();
        boolean isLiked = likeService.checkUserLiked(targetId, type, userId);
        return Result.success(isLiked);
    }
    
    @ApiOperation("获取点赞用户列表")
    @GetMapping("/users/{type}/{targetId}")
    public Result<Page<UserVO>> getLikeUsers(
            @PathVariable Integer type,
            @PathVariable String targetId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<UserVO> page = likeService.getLikeUsers(targetId, type, pageNum, pageSize);
        return Result.success(page);
    }
}