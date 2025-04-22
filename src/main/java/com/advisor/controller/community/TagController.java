package com.advisor.controller.community;

import com.advisor.common.Result;
import com.advisor.service.community.TagService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 标签控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/community/tag")
@Api(tags = "社区标签接口")
public class TagController {
    
    @Autowired
    private TagService tagService;
    
    @ApiOperation("获取所有标签")
    @GetMapping("/all")
    public Result<List<String>> getAllTags() {
        List<String> tags = tagService.getAllTags();
        return Result.success(tags);
    }
    
    @ApiOperation("获取热门标签")
    @GetMapping("/hot")
    public Result<List<Map<String, Object>>> getHotTags(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> hotTags = tagService.getHotTags(limit);
        return Result.success(hotTags);
    }
} 