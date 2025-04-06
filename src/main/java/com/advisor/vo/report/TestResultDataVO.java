package com.advisor.vo.report;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class TestResultDataVO {
    private int totalTests;
    private List<Map<String, Object>> testTypeDistribution;
    private Map<String, List<Map<String, Object>>> scoreChanges;
} 