package com.advisor.mapper.mood;

import com.advisor.entity.mood.MoodRecord;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 情绪记录Mapper接口
 */
@Mapper
public interface MoodRecordMapper extends BaseMapper<MoodRecord> {
    
    @Select("SELECT emotion_type, COUNT(*) as count FROM mood_record " +
           "WHERE user_id = #{userId} AND create_time BETWEEN #{startTime} AND #{endTime} " +
           "GROUP BY emotion_type")
    List<Map<String, Object>> countEmotionTypesByUserId(
        @Param("userId") String userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime);
    
    @Select("SELECT AVG(intensity) FROM mood_record " +
           "WHERE user_id = #{userId} AND create_time BETWEEN #{startTime} AND #{endTime}")
    Double getAverageIntensityByUserIdAndDateRange(
        @Param("userId") String userId, 
        @Param("startTime") LocalDateTime startTime, 
        @Param("endTime") LocalDateTime endTime);
        
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, COUNT(*) as count, AVG(intensity) as avg_score " +
            "FROM mood_record WHERE user_id = #{userId} AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY DATE_FORMAT(create_time, '%Y-%m-%d')")
    List<Map<String, Object>> getMoodStatsByDateRange(@Param("userId") String userId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Select("SELECT emotion_type, COUNT(*) as count FROM mood_record " +
            "WHERE user_id = #{userId} AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY emotion_type ORDER BY count DESC")
    List<Map<String, Object>> getEmotionTypeDistribution(@Param("userId") String userId, 
                                                        @Param("startDate") LocalDateTime startDate, 
                                                        @Param("endDate") LocalDateTime endDate);
    
    @Select("SELECT mt.name as tag_name, COUNT(*) as count " +
            "FROM mood_record mr " +
            "JOIN mood_record_tag mrt ON mr.id = mrt.mood_record_id " +
            "JOIN mood_tag mt ON mrt.tag_id = mt.id " +
            "WHERE mr.user_id = #{userId} AND mr.create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY mt.name " +
            "ORDER BY count DESC LIMIT 5")
    List<Map<String, Object>> getCommonEmotionTags(@Param("userId") String userId, 
                                                  @Param("startDate") LocalDateTime startDate, 
                                                  @Param("endDate") LocalDateTime endDate);

    @Select("SELECT id, user_id, emotion_type, intensity, create_time " +
            "FROM mood_record " +
            "WHERE user_id = #{userId} " +
            "AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY create_time")
    List<Map<String, Object>> getMoodRecordsByDateRange(
        @Param("userId") String userId, 
        @Param("startDate") LocalDateTime startDate, 
        @Param("endDate") LocalDateTime endDate);
}