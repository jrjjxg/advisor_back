package com.advisor.mapper.video;

import com.advisor.entity.video.UserViewHistory;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

// 用户观看历史Mapper
@Mapper
public interface UserViewHistoryMapper extends BaseMapper<UserViewHistory> {
    @Select("SELECT h.*, v.* FROM user_view_history h " +
            "LEFT JOIN video_info v ON h.video_id = v.id " +
            "WHERE h.user_id = #{userId} " +
            "ORDER BY h.update_time DESC")
    List<UserViewHistory> selectHistoryWithVideoByUserId(@Param("userId") String userId);
}