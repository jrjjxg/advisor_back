package com.advisor.controller.community;
import com.advisor.common.Result;
import com.advisor.dto.MessageSendRequest;
import com.advisor.service.community.MessageService;
import com.advisor.util.UserUtil;
import com.advisor.vo.community.PrivateMessageVO;
import com.advisor.vo.community.MessageSessionVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * 消息控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community/message")
@Api(tags = "社区消息接口")
public class MessageController {
    
    @Autowired
    private MessageService messageService;
    
    @ApiOperation("发送私信")
    @PostMapping("/send")
    public Result<String> sendPrivateMessage(@Valid @RequestBody MessageSendRequest request) {
        String userId = UserUtil.getCurrentUserId();
        String messageId = messageService.sendPrivateMessage(request, userId);
        return Result.success(messageId);
    }
    
    @ApiOperation("获取私信会话列表")
    @GetMapping("/sessions")
    public Result<Page<MessageSessionVO>> getMessageSessions(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String userId = UserUtil.getCurrentUserId();
        Page<MessageSessionVO> page = messageService.getMessageSessions(userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("获取与指定用户的私信记录")
    @GetMapping("/chat/{targetUserId}")
    public Result<Page<PrivateMessageVO>> getPrivateMessages(
            @PathVariable String targetUserId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        String userId = UserUtil.getCurrentUserId();
        Page<PrivateMessageVO> page = messageService.getPrivateMessages(targetUserId, userId, pageNum, pageSize);
        return Result.success(page);
    }
    
    @ApiOperation("标记私信为已读")
    @PostMapping("/read/{messageId}")
    public Result<Void> markMessageRead(@PathVariable String messageId) {
        String userId = UserUtil.getCurrentUserId();
        messageService.markMessageRead(messageId, userId);
        return Result.success();
    }
    
    @ApiOperation("标记与指定用户的所有私信为已读")
    @PostMapping("/read/all/{targetUserId}")
    public Result<Integer> markAllMessagesRead(@PathVariable String targetUserId) {
        String userId = UserUtil.getCurrentUserId();
        int count = messageService.markAllMessagesRead(targetUserId, userId);
        return Result.success(count);
    }
}