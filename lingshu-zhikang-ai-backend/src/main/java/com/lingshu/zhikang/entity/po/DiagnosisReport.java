package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * AI智能诊断报告表
 * 用于存储AI生成的专业诊断报告
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("diagnosis_report")
public class DiagnosisReport {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 症状描述
     */
    @TableField("symptoms")
    private String symptoms;
    
    /**
     * 疾病概率(JSON格式)
     */
    @TableField("disease_probabilities")
    private String diseaseProbabilities;
    
    /**
     * 推荐检查项目(JSON格式)
     */
    @TableField("recommended_tests")
    private String recommendedTests;
    
    /**
     * 治疗建议(JSON格式)
     */
    @TableField("treatment_suggestions")
    private String treatmentSuggestions;
    
    /**
     * 紧急程度: LOW(低), MEDIUM(中), HIGH(高), URGENT(紧急)
     */
    @TableField("urgency_level")
    private String urgencyLevel;
    
    /**
     * AI分析结果
     */
    @TableField("ai_analysis")
    private String aiAnalysis;
    
    /**
     * 诊断准确率
     */
    @TableField("accuracy_score")
    private Double accuracyScore;
    
    /**
     * 是否付费: 0-免费, 1-付费
     */
    @TableField("is_paid")
    private Boolean isPaid;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
