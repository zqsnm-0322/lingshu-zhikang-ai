package com.lingshu.zhikang.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康数据统计实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthStatistics {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 统计类型：daily/weekly/monthly/yearly
     */
    private String statType;

    /**
     * 健康评分
     */
    private Integer healthScore;

    /**
     * 平均血压
     */
    private String bloodPressureAvg;

    /**
     * 平均血糖
     */
    private Double bloodSugarAvg;

    /**
     * 平均体重
     */
    private Double weightAvg;

    /**
     * 平均心率
     */
    private Integer heartRateAvg;

    /**
     * 平均睡眠时长
     */
    private Double sleepHoursAvg;

    /**
     * 总运动时长(分钟)
     */
    private Integer exerciseMinutesTotal;

    /**
     * 用药依从性(%)
     */
    private Integer medicationCompliance;

    /**
     * 异常数据次数
     */
    private Integer abnormalCount;

    /**
     * 数据摘要(JSON格式)
     */
    private String dataSummary;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}

