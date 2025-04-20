package com.advisor.controller;

import com.advisor.dto.EmotionRecordDto;
import com.advisor.entity.FacialEmotionHistory;
import com.advisor.service.EmotionHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/emotion")
public class EmotionController {

    private static final Logger log = LoggerFactory.getLogger(EmotionController.class);

    @Autowired
    private EmotionHistoryService service;

    @PostMapping("/history")
    public ResponseEntity<?> saveEmotionHistory(@RequestBody EmotionRecordDto recordDto) {
        log.info("Received emotion record save request for emotion: {}", recordDto.getPredictedEmotion());
        try {
            FacialEmotionHistory savedRecord = service.saveEmotionRecord(recordDto);
            log.info("Emotion record saved successfully with ID: {}", savedRecord.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(savedRecord);
        } catch (Exception e) {
            log.error("Error saving emotion record", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body("Error saving record: " + e.getMessage());
        }
    }
} 