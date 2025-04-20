package com.advisor.mapper;

import com.advisor.entity.NotebookEntry;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记本数据访问接口
 */
@Mapper
public interface NotebookMapper extends BaseMapper<NotebookEntry> {
} 