package com.advisor.service.impl.community;

import com.advisor.entity.community.PostTag;
import com.advisor.mapper.community.PostTagMapper;
import com.advisor.service.community.TagService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 标签服务实现类
 */
@Slf4j
@Service
public class TagServiceImpl implements TagService {
    
    @Autowired
    private PostTagMapper postTagMapper;
    
    @Override
    public List<String> getAllTags() {
        // 查询所有标签
        LambdaQueryWrapper<PostTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.select(PostTag::getTagName).groupBy(PostTag::getTagName);
        
        List<PostTag> tagList = postTagMapper.selectList(queryWrapper);
        return tagList.stream().map(PostTag::getTagName).collect(Collectors.toList());
    }
    
    @Override
    public List<Map<String, Object>> getHotTags(Integer limit) {
        // 获取标签及其帖子数量
        List<Map<String, Object>> tagCountList = postTagMapper.getTagCount();
        
        // 按照帖子数量倒序排序
        tagCountList.sort((a, b) -> {
            Long countA = (Long) a.get("count");
            Long countB = (Long) b.get("count");
            return countB.compareTo(countA);
        });
        
        // 限制返回数量
        if (tagCountList.size() > limit) {
            tagCountList = tagCountList.subList(0, limit);
        }
        
        return tagCountList;
    }
    
    @Override
    public List<String> getPostIdsByTag(String tagName) {
        // 查询包含指定标签的帖子ID
        LambdaQueryWrapper<PostTag> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(PostTag::getTagName, tagName);
        
        List<PostTag> tagList = postTagMapper.selectList(queryWrapper);
        return tagList.stream().map(PostTag::getPostId).collect(Collectors.toList());
    }
} 