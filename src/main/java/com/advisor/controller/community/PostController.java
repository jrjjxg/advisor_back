package com.advisor.controller.community;

import com.advisor.common.Result;
import com.advisor.dto.PostCreateRequest;
import com.advisor.dto.PostQueryRequest;
import com.advisor.service.community.PostService;
import com.advisor.util.UserUtil;
import com.advisor.vo.community.PostVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 帖子控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community/post")
@Api(tags = "社区帖子接口")
public class PostController {
    
    @Autowired
    private PostService postService;
    
    @ApiOperation("创建帖子")
    @PostMapping("/create")
    public Result<String> createPost(@Valid @RequestBody PostCreateRequest request) {
        String userId = UserUtil.getCurrentUserId();
        String postId = postService.createPost(request, userId);
        return Result.success(postId);
    }
    
    @ApiOperation("获取帖子详情")
    @GetMapping("/detail/{postId}")
    public Result<PostVO> getPostDetail(@PathVariable String postId) {
        String userId = UserUtil.getCurrentUserId();
        PostVO post = postService.getPostDetail(postId, userId);
        
        // 增加浏览数
        postService.incrementViewCount(postId);
        
        return Result.success(post);
    }
    
    @ApiOperation("获取帖子列表")
    @PostMapping("/list")
    public Result<Page<PostVO>> getPostList(@RequestBody PostQueryRequest request) {
        String userId = UserUtil.getCurrentUserId();
        Page<PostVO> page = postService.getPostList(request, userId);
        return Result.success(page);
    }
    
    @ApiOperation("获取用户发布的帖子")
    @GetMapping("/user/{userId}")
    public Result<Page<PostVO>> getUserPosts(
            @PathVariable String userId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String currentUserId = UserUtil.getCurrentUserId();
        Page<PostVO> page = postService.getUserPosts(userId, currentUserId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("获取用户点赞的帖子")
    @GetMapping("/liked")
    public Result<Page<PostVO>> getUserLikedPosts(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String userId = UserUtil.getCurrentUserId();
        Page<PostVO> page = postService.getUserLikedPosts(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("删除帖子")
    @DeleteMapping("/{postId}")
    public Result<Void> deletePost(@PathVariable String postId) {
        String userId = UserUtil.getCurrentUserId();
        postService.deletePost(postId, userId);
        return Result.success();
    }
    
    @ApiOperation("从情绪记录创建帖子")
    @PostMapping("/create/mood/{moodRecordId}")
    public Result<String> createPostFromMoodRecord(
            @PathVariable Long moodRecordId,
            @RequestParam(required = false) String content,
            @RequestParam(defaultValue = "0") Integer isAnonymous) {
        String userId = UserUtil.getCurrentUserId();
        String postId = postService.createPostFromMoodRecord(moodRecordId, content, isAnonymous, userId);
        return Result.success(postId);
    }
    
    @ApiOperation("从测试结果创建帖子")
    @PostMapping("/create/test/{testResultId}")
    public Result<String> createPostFromTestResult(
            @PathVariable String testResultId,
            @RequestParam(required = false) String content,
            @RequestParam(defaultValue = "0") Integer isAnonymous) {
        String userId = UserUtil.getCurrentUserId();
        String postId = postService.createPostFromTestResult(testResultId, content, isAnonymous, userId);
        return Result.success(postId);
    }
}