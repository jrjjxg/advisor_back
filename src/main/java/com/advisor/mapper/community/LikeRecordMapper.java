package com.advisor.mapper.community;

import com.advisor.entity.community.LikeRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 点赞记录Mapper
 */
@Mapper
public interface LikeRecordMapper extends BaseMapper<LikeRecord> {
}