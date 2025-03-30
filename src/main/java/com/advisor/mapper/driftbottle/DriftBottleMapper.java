package com.advisor.mapper.driftbottle;

import com.advisor.entity.driftbottle.DriftBottle;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface DriftBottleMapper extends BaseMapper<DriftBottle> {
    
    @Select("SELECT * FROM drift_bottle WHERE status = 1 ORDER BY RAND() LIMIT 1")
    DriftBottle randomPickOneBottle();
    
    @Update("UPDATE drift_bottle SET status = 2, pick_user_id = #{userId}, pick_time = NOW() WHERE id = #{bottleId}")
    int updateBottlePickStatus(@Param("bottleId") String bottleId, @Param("userId") String userId);
} 