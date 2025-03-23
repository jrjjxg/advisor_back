package com.advisor.config;

import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
public class LangChain4jConfig {

    @Value("${openai.chat.base-url}")
    private String baseUrl;

    @Value("${openai.chat.model:deepseek-chat}")
    private String model;

    @Value("${openai.chat.api-key}")
    private String apiKey;

    @Bean
    OpenAiChatModel chatLanguageModel() {
        return OpenAiChatModel.builder()
                .baseUrl(baseUrl)
                .modelName(model)
                .apiKey(apiKey)
                .build();
    }

}
