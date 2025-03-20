package com.advisor.mapper.community;

import com.advisor.entity.community.FollowRelation;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 关注关系Mapper
 */
@Mapper
public interface FollowRelationMapper extends BaseMapper<FollowRelation> {
}