package com.lingshu.zhikang.entity.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 健康预警记录实体类
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthWarning {
    /**
     * 主键ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 触发规则ID
     */
    private Long ruleId;

    /**
     * 预警类型：急性/慢性/预测性
     */
    private String warningType;

    /**
     * 预警级别：绿色/黄色/橙色/红色
     */
    private String warningLevel;

    /**
     * 指标类型
     */
    private String indicatorType;

    /**
     * 指标值
     */
    private String indicatorValue;

    /**
     * 预警标题
     */
    private String warningTitle;

    /**
     * 预警内容
     */
    private String warningContent;

    /**
     * 建议措施
     */
    private String suggestions;

    /**
     * 风险评分(0-100)
     */
    private Integer riskScore;

    /**
     * 是否紧急
     */
    private Boolean isUrgent;

    /**
     * 状态：pending/viewed/handled/dismissed
     */
    private String status;

    /**
     * 处理时间
     */
    private LocalDateTime handledTime;

    /**
     * 处理备注
     */
    private String handledNote;

    /**
     * 创建时间
     */
    private LocalDateTime createdTime;
}

