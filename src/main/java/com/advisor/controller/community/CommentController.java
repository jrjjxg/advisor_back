package com.advisor.controller.community;

import com.advisor.common.Result;
import com.advisor.dto.CommentCreateRequest;
import com.advisor.service.community.CommentService;
import com.advisor.util.UserUtil;
import com.advisor.vo.community.CommentVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 评论控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community/comment")
@Api(tags = "社区评论接口")
public class CommentController {
    
    @Autowired
    private CommentService commentService;
    
    @ApiOperation("创建评论")
    @PostMapping("/create")
    public Result<String> createComment(@Valid @RequestBody CommentCreateRequest request) {
        String userId = UserUtil.getCurrentUserId();
        String commentId = commentService.createComment(request, userId);
        return Result.success(commentId);
    }
    
    @ApiOperation("获取帖子评论列表")
    @GetMapping("/post/{postId}")
    public Result<Page<CommentVO>> getPostComments(
            @PathVariable String postId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String userId = UserUtil.getCurrentUserId();
        Page<CommentVO> page = commentService.getPostComments(postId, userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("获取评论的回复列表")
    @GetMapping("/replies/{commentId}")
    public Result<Page<CommentVO>> getCommentReplies(
            @PathVariable String commentId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String userId = UserUtil.getCurrentUserId();
        Page<CommentVO> page = commentService.getCommentReplies(commentId, userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("删除评论")
    @DeleteMapping("/{commentId}")
    public Result<Void> deleteComment(@PathVariable String commentId) {
        String userId = UserUtil.getCurrentUserId();
        commentService.deleteComment(commentId, userId);
        return Result.success();
    }
}