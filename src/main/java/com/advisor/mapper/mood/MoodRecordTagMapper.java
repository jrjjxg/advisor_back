package com.advisor.mapper.mood;

import com.advisor.entity.mood.MoodRecordTag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MoodRecordTagMapper extends BaseMapper<MoodRecordTag> {
    
    @Select("SELECT t.name FROM mood_record_tag rt " +
           "JOIN mood_tag t ON rt.tag_id = t.id " +
           "WHERE rt.mood_record_id = #{moodRecordId}")
    List<String> findTagNamesByMoodRecordId(@Param("moodRecordId") Long moodRecordId);
}