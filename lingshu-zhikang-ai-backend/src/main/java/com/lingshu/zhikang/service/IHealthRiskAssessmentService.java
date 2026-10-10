package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.HealthRiskAssessment;

import java.util.List;

/**
 * AI健康风险评估服务接口
 * 提供个人和企业的健康风险评估服务
 */
public interface IHealthRiskAssessmentService {
    
    /**
     * 生成个人健康风险评估
     * @param userId 用户ID
     * @return 评估报告
     */
    HealthRiskAssessment generatePersonalAssessment(Long userId);
    
    /**
     * 生成企业员工健康风险评估
     * @param userId 用户ID
     * @param enterpriseId 企业ID
     * @return 评估报告
     */
    HealthRiskAssessment generateEnterpriseAssessment(Long userId, Long enterpriseId);
    
    /**
     * 获取企业员工健康风险统计
     * @param enterpriseId 企业ID
     * @return 统计数据
     */
    String getEnterpriseHealthStatistics(Long enterpriseId);
    
    /**
     * 获取用户历史评估记录
     * @param userId 用户ID
     * @return 评估记录列表
     */
    List<HealthRiskAssessment> getUserAssessmentHistory(Long userId);
    
    /**
     * 生成健康干预方案
     * @param assessmentId 评估ID
     * @return 干预方案
     */
    String generateInterventionPlan(Long assessmentId);
}
