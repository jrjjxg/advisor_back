package com.advisor.constants;

import java.util.Arrays;
import java.util.List;

/**
 * 语音分析相关常量
 */
public class VoiceConstants {
    
    /**
     * 情感类型常量
     */
    public static final class Emotions {
        /**
         * 生气
         */
        public static final String ANGRY = "ANGRY";
        
        /**
         * 高兴
         */
        public static final String HAPPY = "HAPPY";
        
        /**
         * 伤心
         */
        public static final String SAD = "SAD";
        
        /**
         * 中性
         */
        public static final String NEUTRAL = "NEUTRAL";
        
        /**
         * 所有支持的情感类型列表
         */
        public static final String[] ALL = {ANGRY, HAPPY, SAD, NEUTRAL};
    }
    
    /**
     * 音频事件类型常量
     */
    public static final class AudioEvents {
        /**
         * 掌声
         */
        public static final String APPLAUSE = "Applause";
        
        /**
         * 背景音乐
         */
        public static final String BGM = "BGM";
        
        /**
         * 笑声
         */
        public static final String LAUGHTER = "Laughter";
        
        /**
         * 说话声
         */
        public static final String SPEECH = "Speech";
        
        /**
         * 所有支持的音频事件类型列表
         */
        public static final String[] ALL = {APPLAUSE, BGM, LAUGHTER, SPEECH};
    }
    
    /**
     * 标记相关常量
     */
    public static final class Markers {
        /**
         * 标记开始符号
         */
        public static final String START = "<|";
        
        /**
         * 标记结束符号
         */
        public static final String END = "|>";
        
        /**
         * 标记结束符号（闭合标签）
         */
        public static final String CLOSE_START = "</|";
        
        /**
         * 标记结束符号（闭合标签）
         */
        public static final String CLOSE_END = "|>";
    }
} 