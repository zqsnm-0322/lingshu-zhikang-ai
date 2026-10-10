package com.lingshu.zhikang.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AI健康管家晨报实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthButlerReport {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 报告日期
     */
    private LocalDate reportDate;

    /**
     * 睡眠质量评分(0-10)
     */
    private Double sleepQuality;

    /**
     * 睡眠时长(小时)
     */
    private Double sleepDuration;

    /**
     * 今日用药数量
     */
    private Integer medicationCount;

    /**
     * 今日健康任务(JSON格式)
     */
    private String healthTasks;

    /**
     * 饮食建议
     */
    private String dietSuggestions;

    /**
     * 运动建议
     */
    private String exerciseSuggestions;

    /**
     * 健康评分(0-100)
     */
    private Integer healthScore;

    /**
     * 健康等级：优秀/良好/一般/较差
     */
    private String healthLevel;

    /**
     * AI个性化建议
     */
    private String aiSuggestions;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}

