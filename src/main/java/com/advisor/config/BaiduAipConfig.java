// src/main/java/com/advisor/config/BaiduAipConfig.java
package com.advisor.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
@Slf4j
public class BaiduAipConfig {
    
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
    
    @Bean
    public String accessToken() {
        // 直接使用提供的token
        String token = "24.eec376359d9bf84922998850fbf08d5e.2592000.1746717422.282335-118009849";
        log.info("使用提供的百度API access_token");
        return token;
    }
}