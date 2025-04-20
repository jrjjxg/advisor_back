package com.advisor.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@TableName("facial_emotion_history") // 修正表名，与数据库一致
public class FacialEmotionHistory {

    @TableId(value = "id", type = IdType.AUTO) // 指定主键及自增策略
    private Long id;

    @TableField("user_id") // 明确指定数据库字段名（如果开启了驼峰转换且命名一致则可省略）
    private String userId;

    @TableField("predicted_emotion")
    private String predictedEmotion;

    @TableField("probability")
    private Double probability;

    @TableField("all_probabilities_json")
    private String allProbabilitiesJson; // 存储 JSON 字符串

    @TableField("image_url") // 将字段从 image_base64 改为 image_url
    private String imageUrl; // 存储图片URL

    @TableField("recognition_time")
    private LocalDateTime recognitionTime; // MP 默认会自动处理时间类型插入

} 