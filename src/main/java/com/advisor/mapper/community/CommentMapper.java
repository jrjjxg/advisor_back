package com.advisor.mapper.community;

import com.advisor.entity.community.Comment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 评论Mapper
 */
@Mapper
public interface CommentMapper extends BaseMapper<Comment> {
    
    /**
     * 更新点赞数
     */
    @Update("UPDATE comment SET like_count = like_count + #{increment} WHERE id = #{commentId}")
    int updateLikeCount(@Param("commentId") String commentId, @Param("increment") int increment);
}