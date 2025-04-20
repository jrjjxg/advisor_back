package com.advisor.controller;

import com.advisor.dto.NotebookEntryDTO;
import com.advisor.service.NotebookService;
import com.advisor.util.UserUtil;
import com.advisor.common.Result;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 笔记本控制器
 */
@RestController
@RequestMapping("/api/notebook")
public class NotebookController {

    @Autowired
    private NotebookService notebookService;

    /**
     * 保存笔记条目
     */
    @PostMapping("/entries")
    public Result<Boolean> saveNotebookEntry(@RequestBody NotebookEntryDTO dto) {
        // 从认证信息中获取userId
        String userId = UserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }
        
        // 设置用户ID
        dto.setUserId(userId);
        
        boolean success = notebookService.saveNotebookEntry(dto);
        return success ? Result.success(true) : Result.fail(500, "保存失败");
    }

    /**
     * 获取用户的笔记条目列表
     */
    @GetMapping("/entries")
    public Result<Page<NotebookEntryDTO>> getNotebookEntries(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "contentType", required = false) String contentType) {
        // 从认证信息中获取userId
        String userId = UserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }

        Page<NotebookEntryDTO> pageResult = notebookService.getNotebookEntriesByPage(userId, page, pageSize);
        return Result.success(pageResult);
    }

    /**
     * 获取笔记条目详情
     */
    @GetMapping("/entries/{id}")
    public Result<NotebookEntryDTO> getNotebookEntryDetail(@PathVariable("id") Long id) {
        // 从认证信息中获取userId
        String userId = UserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }

        NotebookEntryDTO dto = notebookService.getNotebookEntryDetail(id, userId);
        if (dto == null) {
            return Result.fail(404, "笔记不存在或无权限访问");
        }

        return Result.success(dto);
    }

    /**
     * 更新笔记条目
     */
    @PutMapping("/entries/{id}")
    public Result<Boolean> updateNotebookEntry(
            @PathVariable("id") Long id,
            @RequestBody NotebookEntryDTO dto) {
        // 从认证信息中获取userId
        String userId = UserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }
        
        // 设置ID和用户ID
        dto.setId(id);
        dto.setUserId(userId);

        boolean success = notebookService.updateNotebookEntry(dto);
        return success ? Result.success(true) : Result.fail(404, "更新失败，可能笔记不存在或无权限");
    }

    /**
     * 删除笔记条目
     */
    @DeleteMapping("/entries/{id}")
    public Result<Boolean> deleteNotebookEntry(@PathVariable("id") Long id) {
        // 从认证信息中获取userId
        String userId = UserUtil.getCurrentUserId();
        if (userId == null) {
            return Result.fail(401, "用户未登录");
        }

        boolean success = notebookService.deleteNotebookEntry(id, userId);
        return success ? Result.success(true) : Result.fail(404, "删除失败，可能笔记不存在或无权限");
    }
} 