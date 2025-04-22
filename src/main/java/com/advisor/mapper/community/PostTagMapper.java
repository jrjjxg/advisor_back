package com.advisor.mapper.community;

import com.advisor.entity.community.PostTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 帖子标签Mapper
 */
@Mapper
public interface PostTagMapper extends BaseMapper<PostTag> {
    
    /**
     * 根据帖子ID查询标签
     */
    @Select("SELECT tag_name FROM post_tag WHERE post_id = #{postId}")
    List<String> findTagsByPostId(@Param("postId") String postId);
    
    /**
     * 获取标签及其帖子数量
     */
    @Select("SELECT tag_name as tagName, COUNT(post_id) as count FROM post_tag GROUP BY tag_name")
    List<Map<String, Object>> getTagCount();
}