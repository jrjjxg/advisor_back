// src/main/java/com/advisor/entity/baidu/TokenResponse.java
package com.advisor.entity.baidu;

import lombok.Data;

@Data
public class TokenResponse {
    private String accessToken;
    private String expiresIn;
    private String error;
    private String errorDescription;
}