package com.advisor.mapper;

import com.advisor.entity.VoiceAnalysis;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 语音分析结果Mapper接口
 */
@Mapper
public interface VoiceAnalysisMapper extends BaseMapper<VoiceAnalysis> {
} 