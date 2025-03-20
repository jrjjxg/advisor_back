package com.advisor.vo.test;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class TestResultVO {
    private String id;
    private String userId;
    private String testTypeId;
    private String testTypeName;
    private Integer totalScore;
    private String resultLevel;
    private String resultDescription;
    private String suggestions;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private LocalDateTime createTime;
    private List<Map<String, Object>> answerDetails;
}