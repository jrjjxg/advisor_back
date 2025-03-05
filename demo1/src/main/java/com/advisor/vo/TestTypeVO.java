package com.advisor.vo;

import lombok.Data;

@Data
public class TestTypeVO {
    private String id;
    private String name;
    private String description;
    private String icon;
    private String category;
    private Integer timeMinutes;
    private Integer questionCount;
}