package com.lingshu.zhikang.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 健康计划实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthPlan {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 计划名称
     */
    private String planName;

    /**
     * 计划类型：饮食/运动/睡眠/用药/综合
     */
    private String planType;

    /**
     * 开始日期
     */
    private LocalDate startDate;

    /**
     * 结束日期
     */
    private LocalDate endDate;

    /**
     * 目标描述
     */
    private String targetGoal;

    /**
     * 每日任务(JSON格式)
     */
    private String dailyTasks;

    /**
     * 完成进度(0-100)
     */
    private Integer progress;

    /**
     * 状态：进行中/已完成/已暂停/已取消
     */
    private String status;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;

    /**
     * 更新时间
     */
    private LocalDateTime updatedTime;
}

