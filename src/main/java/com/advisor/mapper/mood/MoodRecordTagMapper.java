package com.advisor.mapper.mood;

import com.advisor.dto.TagStatDTO;
import com.advisor.entity.mood.MoodRecordTag;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface MoodRecordTagMapper extends BaseMapper<MoodRecordTag> {
    
    @Select("SELECT t.name FROM mood_record_tag rt " +
           "JOIN mood_tag t ON rt.tag_id = t.id " +
           "WHERE rt.mood_record_id = #{moodRecordId}")
    List<String> findTagNamesByMoodRecordId(@Param("moodRecordId") Long moodRecordId);

    @Select("SELECT t.name as tagName,COUNT(*) as count " +
            "FROM mood_record_tag mrt " +
            "LEFT JOIN mood_record mr ON mrt.mood_record_id = mr.id " +
            "LEFT JOIN mood_tag t ON mrt.tag_id = t.id " +
            "WHERE mr.user_id = #{userId} " +
            "AND (#{startDate} IS NULL OR DATE(mr.create_time) >= #{startDate}) " +
            "AND (#{endDate} IS NULL OR DATE(mr.create_time) <= #{endDate}) " +
            "GROUP BY t.id, t.name, t.category " +
            "ORDER BY count DESC")
    List<TagStatDTO> selectTagStats(@Param("userId") String userId, 
                                    @Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate);
}