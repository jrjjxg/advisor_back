package com.advisor.entity.test;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("test_score_level")
public class TestScoreLevel {
    @TableId
    private String id;
    private String testTypeId;    // 测试类型ID
    private String levelName;     // 级别名称（如"正常"、"轻度"等）
    private Integer minScore;     // 该级别的最小分数（包含）
    private Integer maxScore;     // 该级别的最大分数（包含）
    private String description;   // 该级别的描述模板
    private String suggestions;   // 该级别的建议模板
    private Integer orderNum;     // 排序号
} 