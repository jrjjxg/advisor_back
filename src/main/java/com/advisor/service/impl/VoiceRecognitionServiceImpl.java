package com.advisor.service.impl;
import com.advisor.util.UserUtil;
import com.alibaba.dashscope.audio.asr.transcription.*;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.advisor.config.VoiceRecognitionConfig;
import com.advisor.constants.VoiceConstants;
import com.advisor.dto.voice.VoiceAnalysisDTO;
import com.advisor.entity.VoiceAnalysis;
import com.advisor.mapper.VoiceAnalysisMapper;
import com.advisor.service.FileService;
import com.advisor.service.VoiceRecognitionService;
import com.advisor.vo.VoiceAnalysisResult;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 语音识别服务实现类
 */
@Service
@Slf4j
public class VoiceRecognitionServiceImpl implements VoiceRecognitionService {

    @Autowired
    private VoiceRecognitionConfig config;

    @Autowired
    private FileService fileService;

    @Autowired
    private VoiceAnalysisMapper voiceAnalysisMapper;

    // 用于匹配标记的正则表达式
    private static final Pattern TAG_PATTERN = Pattern.compile("<\\|([^|]+)\\|>");
    private static final Pattern CLOSING_TAG_PATTERN = Pattern.compile("</\\|([^|]+)\\|>");

    @Override
    public VoiceAnalysisResult uploadAndAnalyze(MultipartFile file, VoiceAnalysisDTO dto) {
        try {
            // 1. 上传文件到七牛云
            String audioUrl = fileService.uploadFile(file, "voice");
            log.info("语音文件上传成功：{}", audioUrl);

            // 2. 调用阿里云SenseVoice进行语音识别
            TranscriptionParam param = TranscriptionParam.builder()
                    .apiKey(config.getApiKey())
                    .model("sensevoice-v1")
                    .fileUrls(Collections.singletonList(audioUrl))
                    .parameter("language_hints", new String[]{dto.getLanguageCode()})
                    // 明确启用情感分析和音频事件检测
                    .parameter("emotion_analysis", true)
                    .parameter("audio_event_detection", true)
                    .parameter("format", "rich_text") // 确保返回带情感和事件标记的富文本格式
                    .build();

            Transcription transcription = new Transcription();
            log.info("发送语音识别请求参数: {}", param.toString());

            // 提交异步任务
            TranscriptionResult result = transcription.asyncCall(param);
            log.info("语音识别任务提交成功，taskId: {}", result.getTaskId());

            // 3. 保存任务信息到数据库
            VoiceAnalysis analysis = new VoiceAnalysis();
            analysis.setUserId(UserUtil.getCurrentUserId());
            analysis.setAudioUrl(audioUrl);
            analysis.setTaskId(result.getTaskId());
            analysis.setLanguageCode(dto.getLanguageCode());
            analysis.setJournalId(dto.getJournalId());
            analysis.setMoodId(dto.getMoodId());

            voiceAnalysisMapper.insert(analysis);

            // 4. 返回初始结果
            VoiceAnalysisResult analysisResult = new VoiceAnalysisResult();
            analysisResult.setId(analysis.getId());
            analysisResult.setAudioUrl(audioUrl);
            analysisResult.setTaskId(result.getTaskId());
            analysisResult.setLanguageCode(dto.getLanguageCode());
            analysisResult.setJournalId(dto.getJournalId());
            analysisResult.setMoodId(dto.getMoodId());
            analysisResult.setAnalysisTime(LocalDateTime.now());
            analysisResult.setTaskStatus("PENDING"); // 初始状态为等待处理

            return analysisResult;
        } catch (Exception e) {
            log.error("语音文件上传或分析失败", e);
            throw new RuntimeException("语音文件上传或分析失败: " + e.getMessage());
        }
    }

    @Override
    public VoiceAnalysisResult getAnalysisResult(String taskId) {
        try {
            // 1. 从数据库查询任务信息
            LambdaQueryWrapper<VoiceAnalysis> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(VoiceAnalysis::getTaskId, taskId);
            VoiceAnalysis analysis = voiceAnalysisMapper.selectOne(queryWrapper);

            if (analysis == null) {
                log.error("找不到taskId为{}的语音分析任务", taskId);
                throw new RuntimeException("找不到对应的语音分析任务");
            }

            // 如果已经有结果了，直接返回
            if (analysis.getTranscription() != null) {
                VoiceAnalysisResult result = convertToVo(analysis);
                result.setTaskStatus("SUCCEEDED"); // 设置任务状态为成功
                return result;
            }

            // 2. 查询阿里云任务执行结果
            Transcription transcription = new Transcription();
            TranscriptionQueryParam queryParam = TranscriptionQueryParam.builder()
                    .apiKey(config.getApiKey())
                    .taskId(taskId)
                    .build();

            TranscriptionResult result = transcription.fetch(queryParam);

            // 3. 如果任务还未完成，返回当前状态
            VoiceAnalysisResult analysisResult = new VoiceAnalysisResult();
            BeanUtils.copyProperties(analysis, analysisResult);
            
            // 设置任务状态，与阿里云保持一致，但需要转换枚举为字符串
            analysisResult.setTaskStatus(result.getTaskStatus().toString());
            
            if (!"SUCCEEDED".equals(result.getTaskStatus().toString())) {
                return analysisResult;
            }

            // 4. 任务已完成，解析结果
            List<TranscriptionTaskResult> taskResultList = result.getResults();
            if (taskResultList == null || taskResultList.isEmpty()) {
                log.error("语音识别任务结果为空");
                throw new RuntimeException("语音识别任务结果为空");
            }

            TranscriptionTaskResult taskResult = taskResultList.get(0);
            String transcriptionUrl = taskResult.getTranscriptionUrl();

            // 5. 获取详细结果
            HttpURLConnection connection = (HttpURLConnection) new URL(transcriptionUrl).openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            Gson gson = new Gson();
            JsonObject jsonResult = gson.fromJson(reader, JsonObject.class);
            String resultJson = jsonResult.toString();

            // 详细打印SenseVoice返回的原始JSON响应
            log.info("语音分析原始结果JSON: \n{}", gson.toJson(jsonResult));
            
            // 6. 解析结果并更新数据库
            updateAnalysisWithResult(analysis, resultJson);

            // 7. 返回结果
            VoiceAnalysisResult finalResult = convertToVo(analysis);
            finalResult.setTaskStatus("SUCCEEDED"); // 设置任务状态为成功
            return finalResult;
        } catch (Exception e) {
            log.error("获取语音分析结果失败", e);
            throw new RuntimeException("获取语音分析结果失败: " + e.getMessage());
        }
    }

    @Override
    public List<VoiceAnalysisResult> getAnalysisByJournalId(String journalId) {
        LambdaQueryWrapper<VoiceAnalysis> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoiceAnalysis::getJournalId, journalId);
        List<VoiceAnalysis> analysisList = voiceAnalysisMapper.selectList(queryWrapper);

        return analysisList.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public List<VoiceAnalysisResult> getAnalysisByMoodId(String moodId) {
        LambdaQueryWrapper<VoiceAnalysis> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(VoiceAnalysis::getMoodId, moodId);
        List<VoiceAnalysis> analysisList = voiceAnalysisMapper.selectList(queryWrapper);

        return analysisList.stream()
                .map(this::convertToVo)
                .collect(Collectors.toList());
    }

    @Override
    public VoiceAnalysisResult analyzeByUrl(String audioUrl, VoiceAnalysisDTO dto) {
        try {
            // 1. 调用阿里云SenseVoice进行语音识别
            TranscriptionParam param = TranscriptionParam.builder()
                    .apiKey(config.getApiKey())
                    .model("sensevoice-v1")
                    .fileUrls(Collections.singletonList(audioUrl))
                    .parameter("language_hints", new String[]{dto.getLanguageCode()})
                    // 明确启用情感分析和音频事件检测
                    .parameter("emotion_analysis", true)
                    .parameter("audio_event_detection", true)
                    .parameter("format", "rich_text") // 确保返回带情感和事件标记的富文本格式
                    .build();

            Transcription transcription = new Transcription();
            log.info("发送语音识别请求参数: {}", param.toString());

            // 提交异步任务
            TranscriptionResult result = transcription.asyncCall(param);
            log.info("语音识别任务提交成功，taskId: {}", result.getTaskId());

            // 2. 保存任务信息到数据库
            VoiceAnalysis analysis = new VoiceAnalysis();
            analysis.setUserId(UserUtil.getCurrentUserId());
            analysis.setAudioUrl(audioUrl);
            analysis.setTaskId(result.getTaskId());
            analysis.setLanguageCode(dto.getLanguageCode());
            analysis.setJournalId(dto.getJournalId());
            analysis.setMoodId(dto.getMoodId());

            voiceAnalysisMapper.insert(analysis);

            // 3. 返回初始结果
            VoiceAnalysisResult analysisResult = new VoiceAnalysisResult();
            analysisResult.setId(analysis.getId());
            analysisResult.setAudioUrl(audioUrl);
            analysisResult.setTaskId(result.getTaskId());
            analysisResult.setLanguageCode(dto.getLanguageCode());
            analysisResult.setJournalId(dto.getJournalId());
            analysisResult.setMoodId(dto.getMoodId());
            analysisResult.setAnalysisTime(LocalDateTime.now());
            analysisResult.setTaskStatus("PENDING"); // 初始状态为等待处理

            return analysisResult;
        } catch (Exception e) {
            log.error("语音分析失败", e);
            throw new RuntimeException("语音分析失败: " + e.getMessage());
        }
    }

    /**
     * 根据SenseVoice返回的结果更新分析记录
     *
     * @param analysis 分析记录
     * @param resultJson 结果JSON
     */
    private void updateAnalysisWithResult(VoiceAnalysis analysis, String resultJson) {
        try {
            JSONObject result = JSON.parseObject(resultJson);
            
            // 详细记录解析过程
            log.info("开始解析语音分析结果...");
            log.info("原始JSON: {}", resultJson);
            
            // 记录预期的关键字段是否存在
            log.info("检查关键字段:");
            log.info("- transcripts字段是否存在: {}", result.containsKey("transcripts"));
            log.info("- properties字段是否存在: {}", result.containsKey("properties"));
            log.info("- file_url字段是否存在: {}", result.containsKey("file_url"));
            
            // 如果有properties，记录其内容
            if (result.containsKey("properties")) {
                JSONObject properties = result.getJSONObject("properties");
                log.info("properties内容: {}", properties);
                log.info("- audio_format: {}", properties.getString("audio_format"));
                log.info("- original_sampling_rate: {}", properties.getIntValue("original_sampling_rate"));
                log.info("- original_duration_in_milliseconds: {}", properties.getLongValue("original_duration_in_milliseconds"));
            }

            // 存储原始JSON
            analysis.setRawResultJson(resultJson);

            // 提取转录结果
            if (result.containsKey("transcripts") && result.getJSONArray("transcripts").size() > 0) {
                JSONObject transcript = result.getJSONArray("transcripts").getJSONObject(0);
                log.info("提取到transcript: {}", transcript);

                // 提取原始文本（包含标记）
                String rawText = transcript.getString("text");
                log.info("提取到rawText: {}", rawText);
                analysis.setRawText(rawText);

                // 提取纯文本（去除标记）
                String transcription = removeMarkups(rawText);
                log.info("去除标记后的文本: {}", transcription);
                analysis.setTranscription(transcription);

                // 提取音频时长
                if (transcript.containsKey("content_duration_in_milliseconds")) {
                    Long duration = transcript.getLong("content_duration_in_milliseconds");
                    log.info("提取到音频时长: {}毫秒", duration);
                    analysis.setDuration(duration);
                } else if (result.containsKey("properties") && result.getJSONObject("properties").containsKey("original_duration_in_milliseconds")) {
                    Long duration = result.getJSONObject("properties").getLong("original_duration_in_milliseconds");
                    log.info("从properties提取到音频时长: {}毫秒", duration);
                    analysis.setDuration(duration);
                }

                // 提取分句信息
                if (transcript.containsKey("sentences")) {
                    JSONArray sentencesArray = transcript.getJSONArray("sentences");
                    log.info("原始sentences数组内容: {}", sentencesArray);
                    log.info("共检测到{}个句子", sentencesArray.size());
                    
                    List<VoiceAnalysisResult.SentenceInfo> sentences = new ArrayList<>();

                    // 情感分析和音频事件统计
                    Map<String, Integer> eventCounts = new HashMap<>();
                    for (String event : VoiceConstants.AudioEvents.ALL) {
                        eventCounts.put(event, 0);
                    }

                    List<String> detectedEmotions = new ArrayList<>();
                    log.info("开始分析句子情感和音频事件...");
                    
                    for (int i = 0; i < sentencesArray.size(); i++) {
                        JSONObject sentenceObj = sentencesArray.getJSONObject(i);
                        String sentenceText = sentenceObj.getString("text");
                        log.info("句子[{}]文本: {}", i, sentenceText);

                        VoiceAnalysisResult.SentenceInfo sentenceInfo = new VoiceAnalysisResult.SentenceInfo();
                        sentenceInfo.setRawText(sentenceText);
                        sentenceInfo.setText(removeMarkups(sentenceText));
                        sentenceInfo.setBeginTime(sentenceObj.getLong("begin_time"));
                        sentenceInfo.setEndTime(sentenceObj.getLong("end_time"));

                        // 提取该句中的情感和事件
                        List<String> sentenceEvents = new ArrayList<>();
                        String sentenceEmotion = null;

                        // 使用正则表达式查找情感标记
                        Matcher emotionMatcher = Pattern.compile("<\\|("+String.join("|", VoiceConstants.Emotions.ALL)+")\\|>").matcher(sentenceText);
                        if (emotionMatcher.find()) {
                            sentenceEmotion = emotionMatcher.group(1);
                            detectedEmotions.add(sentenceEmotion);
                            log.info("句子[{}]通过正则表达式检测到情感: {}, 匹配内容: {}", 
                                i, sentenceEmotion, emotionMatcher.group(0));
                        } else {
                            log.info("句子[{}]未检测到标准情感标记", i);
                            // 尝试使用简单字符串匹配
                            for (String emotion : VoiceConstants.Emotions.ALL) {
                                String emotionMarker = VoiceConstants.Markers.START + emotion + VoiceConstants.Markers.END;
                                if (sentenceText.contains(emotionMarker)) {
                                    sentenceEmotion = emotion;
                                    detectedEmotions.add(emotion);
                                    log.info("句子[{}]通过字符串匹配检测到情感: {}, 匹配内容: {}", 
                                        i, emotion, emotionMarker);
                                    break;
                                }
                            }
                        }

                        // 查找音频事件标记
                        for (String event : VoiceConstants.AudioEvents.ALL) {
                            String startTag = VoiceConstants.Markers.START + event + VoiceConstants.Markers.END;
                            String closeTag = VoiceConstants.Markers.CLOSE_START + event + VoiceConstants.Markers.CLOSE_END;
                            
                            // 检查开始标记
                            if (sentenceText.contains(startTag)) {
                                sentenceEvents.add(event);
                                eventCounts.put(event, eventCounts.get(event) + 1);
                                log.info("句子[{}]检测到音频事件: {}, 开始标记: {}", i, event, startTag);
                            }
                            
                            // 检查闭合标记
                            if (sentenceText.contains(closeTag)) {
                                log.info("句子[{}]检测到音频事件闭合标记: {}", i, closeTag);
                            }
                        }

                        sentenceInfo.setEmotion(sentenceEmotion);
                        sentenceInfo.setEvents(sentenceEvents);

                        sentences.add(sentenceInfo);
                    }

                    // 保存分句信息
                    analysis.setSentencesJson(JSON.toJSONString(sentences));

                    // 确定主要情感
                    if (!detectedEmotions.isEmpty()) {
                        Map<String, Long> emotionFrequency = detectedEmotions.stream()
                                .collect(Collectors.groupingBy(e -> e, Collectors.counting()));
                        
                        log.info("情感频率统计: {}", emotionFrequency);

                        String dominantEmotion = emotionFrequency.entrySet().stream()
                                .max(Map.Entry.comparingByValue())
                                .map(Map.Entry::getKey)
                                .orElse(null);

                        log.info("主要情感: {}", dominantEmotion);
                        analysis.setDominantEmotion(dominantEmotion);
                    }

                    // 保存音频事件统计
                    log.info("音频事件统计: {}", eventCounts);
                    analysis.setAudioEventsJson(JSON.toJSONString(eventCounts));
                }
            }

            // 更新数据库
            voiceAnalysisMapper.updateById(analysis);
        } catch (Exception e) {
            log.error("解析语音识别结果失败", e);
            throw new RuntimeException("解析语音识别结果失败: " + e.getMessage());
        }
    }

    /**
     * 去除文本中的标记
     *
     * @param text 原始文本
     * @return 去除标记后的文本
     */
    private String removeMarkups(String text) {
        if (text == null) {
            return null;
        }

        log.info("开始去除标记，原始文本: {}", text);
        
        // 去除所有开始标记，包括<|标记|>格式
        String result = text.replaceAll("<\\|[^|]+\\|>", "");
        log.info("去除开始标记后: {}", result);

        // 去除所有结束标记，包括</|标记|>格式
        result = result.replaceAll("</\\|[^|]+\\|>", "");
        log.info("去除结束标记后: {}", result);

        // 如果还有其他格式的标记，可以在这里添加额外的处理

        return result.trim();
    }

    /**
     * 将实体转换为VO
     *
     * @param analysis 分析记录
     * @return VO对象
     */
    private VoiceAnalysisResult convertToVo(VoiceAnalysis analysis) {
        if (analysis == null) {
            return null;
        }

        VoiceAnalysisResult result = new VoiceAnalysisResult();
        result.setId(analysis.getId());
        result.setAudioUrl(analysis.getAudioUrl());
        result.setRawText(analysis.getRawText());
        result.setTranscription(analysis.getTranscription());
        result.setDominantEmotion(analysis.getDominantEmotion());
        result.setDuration(analysis.getDuration());
        result.setLanguageCode(analysis.getLanguageCode());
        result.setTaskId(analysis.getTaskId());
        result.setJournalId(analysis.getJournalId());
        result.setMoodId(analysis.getMoodId());
        result.setAnalysisTime(analysis.getCreateTime());

        // 解析音频事件JSON
        if (analysis.getAudioEventsJson() != null) {
            try {
                Map<String, Integer> audioEvents = JSON.parseObject(
                        analysis.getAudioEventsJson(),
                        new com.alibaba.fastjson2.TypeReference<Map<String, Integer>>() {}
                );
                result.setAudioEvents(audioEvents);
            } catch (Exception e) {
                log.error("解析音频事件JSON失败", e);
            }
        }

        // 解析分句信息JSON
        if (analysis.getSentencesJson() != null) {
            try {
                List<VoiceAnalysisResult.SentenceInfo> sentences = JSON.parseObject(
                        analysis.getSentencesJson(),
                        new com.alibaba.fastjson2.TypeReference<List<VoiceAnalysisResult.SentenceInfo>>() {}
                );
                result.setSentences(sentences);
            } catch (Exception e) {
                log.error("解析分句信息JSON失败", e);
            }
        }

        return result;
    }
}
