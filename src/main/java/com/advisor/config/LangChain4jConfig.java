package com.advisor.config;

import dev.langchain4j.model.dashscope.QwenChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;

@Configuration
public class LangChain4jConfig {

    @Value("${dashscope.api-key}")
    private String apiKey;

    @Value("${dashscope.model-name:qwen-max}")
    private String modelName;

    @Bean
    public QwenChatModel qwenChatModel() {
        return QwenChatModel.builder()
                .apiKey(apiKey)
                .modelName(modelName)
                .maxTokens(4096)
                .build();
    }
}
