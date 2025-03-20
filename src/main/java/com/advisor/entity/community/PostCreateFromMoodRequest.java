package com.advisor.dto.community;

import lombok.Data;

@Data
public class PostCreateFromMoodRequest {
    private String moodRecordId;
    private String content;
    private Integer isAnonymous = 0;
}