package com.advisor.mapper.community;

import com.advisor.entity.community.Post;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 帖子Mapper
 */
@Mapper
public interface PostMapper extends BaseMapper<Post> {
    
    /**
     * 增加浏览数
     */
    @Update("UPDATE post SET view_count = view_count + 1 WHERE id = #{postId}")
    int incrementViewCount(@Param("postId") String postId);
    
    /**
     * 增加点赞数
     */
    @Update("UPDATE post SET like_count = like_count + #{increment} WHERE id = #{postId}")
    int updateLikeCount(@Param("postId") String postId, @Param("increment") int increment);
    
    /**
     * 增加评论数
     */
    @Update("UPDATE post SET comment_count = comment_count + #{increment} WHERE id = #{postId}")
    int updateCommentCount(@Param("postId") String postId, @Param("increment") int increment);
}