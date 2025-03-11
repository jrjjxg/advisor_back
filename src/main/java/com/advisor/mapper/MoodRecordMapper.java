package com.advisor.mapper;

import com.advisor.entity.MoodRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface MoodRecordMapper extends BaseMapper<MoodRecord> {
    
    @Select("SELECT emotion_type, COUNT(*) as count FROM mood_record " +
           "WHERE user_id = #{userId} GROUP BY emotion_type")
    List<Map<String, Object>> countEmotionTypesByUserId(@Param("userId") String userId);
    
    @Select("SELECT AVG(intensity) FROM mood_record " +
           "WHERE user_id = #{userId} AND create_time BETWEEN #{startTime} AND #{endTime}")
    Double getAverageIntensityByUserIdAndDateRange(
        @Param("userId") String userId, 
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime);
}