package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 康复游戏实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("rehabilitation_game")
public class RehabilitationGame {
    
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 游戏名称
     */
    private String gameName;
    
    /**
     * 游戏类型：puzzle-益智, action-动作, rhythm-节奏, balance-平衡, coordination-协调, memory-记忆
     */
    private String gameType;
    
    /**
     * 目标部位
     */
    private String targetBodyPart;
    
    /**
     * 难度等级：easy-简单, medium-中等, hard-困难
     */
    private String difficultyLevel;
    
    /**
     * 游戏URL
     */
    private String gameUrl;
    
    /**
     * 缩略图URL
     */
    private String thumbnailUrl;
    
    /**
     * 游戏描述
     */
    private String description;
    
    /**
     * 游戏说明
     */
    private String instructions;
    
    /**
     * 治疗益处
     */
    private String therapeuticBenefits;
    
    /**
     * 预计时长（分钟）
     */
    private Integer estimatedDuration;
    
    /**
     * 是否支持VR
     */
    private Boolean isVrSupported;
    
    /**
     * 是否支持AR
     */
    private Boolean isArSupported;
    
    /**
     * 最小年龄
     */
    private Integer minAge;
    
    /**
     * 最大年龄
     */
    private Integer maxAge;
    
    /**
     * 标签
     */
    private String tags;
    
    /**
     * 平均评分
     */
    private BigDecimal ratingAvg;
    
    /**
     * 游玩次数
     */
    private Integer playCount;
    
    /**
     * 是否启用
     */
    private Boolean isActive;
    
    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}

