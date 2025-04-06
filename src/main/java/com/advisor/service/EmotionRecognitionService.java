package com.advisor.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class EmotionRecognitionService {

    private final RestTemplate restTemplate;

    @Value("${emotion.recognition.api.url}")
    private String apiUrl;

    public EmotionRecognitionService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Map<String, Object> recognizeEmotion(MultipartFile imageFile) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        body.add("file", imageFile.getResource());

        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        return restTemplate.postForObject(apiUrl + "/api/predict", requestEntity, Map.class);
    }

    public boolean isServiceHealthy() {
        try {
            Map<String, Object> response = restTemplate.getForObject(apiUrl + "/api/health", Map.class);
            return response != null && "UP".equals(response.get("status"));
        } catch (Exception e) {
            return false;
        }
    }
}