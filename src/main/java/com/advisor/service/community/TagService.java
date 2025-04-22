package com.advisor.service.community;

import java.util.List;
import java.util.Map;

/**
 * 标签服务接口
 */
public interface TagService {
    
    /**
     * 获取所有标签
     *
     * @return 标签列表
     */
    List<String> getAllTags();
    
    /**
     * 获取热门标签
     *
     * @param limit 限制数量
     * @return 标签列表，包含标签名和帖子数量
     */
    List<Map<String, Object>> getHotTags(Integer limit);
    
    /**
     * 根据标签名获取帖子ID列表
     *
     * @param tagName 标签名
     * @return 帖子ID列表
     */
    List<String> getPostIdsByTag(String tagName);
} 