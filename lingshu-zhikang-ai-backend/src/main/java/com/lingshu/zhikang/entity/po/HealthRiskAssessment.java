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
 * AI健康风险评估表
 * 用于存储用户的健康风险评估结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("health_risk_assessment")
public class HealthRiskAssessment {
    
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
     * 企业客户ID(如果是企业员工)
     */
    @TableField("enterprise_id")
    private Long enterpriseId;
    
    /**
     * 健康评分(0-100)
     */
    @TableField("health_score")
    private Integer healthScore;
    
    /**
     * 风险等级: LOW(低风险), MEDIUM(中风险), HIGH(高风险), CRITICAL(极高风险)
     */
    @TableField("risk_level")
    private String riskLevel;
    
    /**
     * 风险因素(JSON格式)
     */
    @TableField("risk_factors")
    private String riskFactors;
    
    /**
     * 疾病预测(JSON格式)
     */
    @TableField("disease_predictions")
    private String diseasePredictions;
    
    /**
     * 干预建议(JSON格式)
     */
    @TableField("intervention_suggestions")
    private String interventionSuggestions;
    
    /**
     * 生活方式建议
     */
    @TableField("lifestyle_recommendations")
    private String lifestyleRecommendations;
    
    /**
     * AI分析详情
     */
    @TableField("ai_analysis_detail")
    private String aiAnalysisDetail;
    
    /**
     * 评估类型: PERSONAL(个人), ENTERPRISE(企业)
     */
    @TableField("assessment_type")
    private String assessmentType;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
