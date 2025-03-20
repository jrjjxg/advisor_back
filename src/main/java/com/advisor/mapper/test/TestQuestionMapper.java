package com.advisor.mapper.test;

import com.advisor.entity.test.TestQuestion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TestQuestionMapper extends BaseMapper<TestQuestion> {
}