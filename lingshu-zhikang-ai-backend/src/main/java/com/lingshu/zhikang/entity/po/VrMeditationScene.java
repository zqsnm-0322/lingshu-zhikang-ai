package com.lingshu.zhikang.entity.po;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * VR冥想场景实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("vr_meditation_scene")
public class VrMeditationScene {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 场景名称
     */
    private String sceneName;
    
    /**
     * 场景类型：nature-自然, beach-海滩, forest-森林, mountain-山脉, space-太空, temple-寺庙, garden-花园
     */
    private String sceneType;
    
    /**
     * VR场景URL
     */
    private String vrSceneUrl;
    
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    
    /**
     * 场景描述
     */
    private String description;
    
    /**
     * 背景音乐URL
     */
    private String backgroundMusicUrl;
    
    /**
     * 环境音效列表（JSON数组）
     */
    private String ambientSounds;
    
    /**
     * 引导语音URL
     */
    private String guidedAudioUrl;
    
    /**
     * 建议时长（分钟）
     */
    private Integer durationMinutes;
    
    /**
     * 适用人群
     */
    private String suitableFor;
    
    /**
     * 难度等级：beginner-初级, intermediate-中级, advanced-高级
     */
    private String difficultyLevel;
    
    /**
     * 标签
     */
    private String tags;
    
    /**
     * 平均评分
     */
    private BigDecimal ratingAvg;
    
    /**
     * 使用次数
     */
    private Integer usageCount;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}

