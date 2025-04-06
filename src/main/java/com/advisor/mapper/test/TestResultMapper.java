package com.advisor.mapper.test;

import com.advisor.entity.test.TestResult;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Mapper
public interface TestResultMapper extends BaseMapper<TestResult> {
    
    @Select("SELECT COUNT(*) FROM test_result " +
            "WHERE user_id = #{userId} AND create_time BETWEEN #{startDate} AND #{endDate}")
    int countTestsByDateRange(@Param("userId") String userId, 
                              @Param("startDate") LocalDateTime startDate, 
                              @Param("endDate") LocalDateTime endDate);
    
    @Select("SELECT test_type_id, COUNT(*) as count FROM test_result " +
            "WHERE user_id = #{userId} AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "GROUP BY test_type_id ORDER BY count DESC")
    List<Map<String, Object>> getTestTypeDistribution(@Param("userId") String userId, 
                                                     @Param("startDate") LocalDateTime startDate, 
                                                     @Param("endDate") LocalDateTime endDate);
    
    @Select("SELECT DATE_FORMAT(create_time, '%Y-%m-%d') as date, total_score " +
            "FROM test_result WHERE user_id = #{userId} AND test_type_id = #{testTypeId} " +
            "AND create_time BETWEEN #{startDate} AND #{endDate} " +
            "ORDER BY create_time ASC")
    List<Map<String, Object>> getTestScoresByType(@Param("userId") String userId, 
                                                 @Param("testTypeId") String testTypeId,
                                                 @Param("startDate") LocalDateTime startDate, 
                                                 @Param("endDate") LocalDateTime endDate);
}