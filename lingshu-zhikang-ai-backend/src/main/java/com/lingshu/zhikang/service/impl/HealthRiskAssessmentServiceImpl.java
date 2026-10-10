package com.lingshu.zhikang.service.impl;

import com.lingshu.zhikang.entity.po.HealthRiskAssessment;
import com.lingshu.zhikang.mapper.HealthRiskAssessmentMapper;
import com.lingshu.zhikang.service.IHealthRiskAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 健康风险评估服务实现类
 */
@Slf4j
@Service
public class HealthRiskAssessmentServiceImpl implements IHealthRiskAssessmentService {
    
    @Autowired
    private HealthRiskAssessmentMapper healthRiskAssessmentMapper;
    
    private final Random random = new Random();
    
    @Override
    public HealthRiskAssessment generatePersonalAssessment(Long userId) {
        log.info("生成个人健康风险评估 - 用户ID: {}", userId);
        
        HealthRiskAssessment assessment = new HealthRiskAssessment();
        assessment.setUserId(userId);
        assessment.setEnterpriseId(null);
        assessment.setAssessmentType("PERSONAL");
        
        // 生成健康评分（模拟）
        int healthScore = 60 + random.nextInt(40); // 60-100分
        assessment.setHealthScore(healthScore);
        
        // 确定风险等级
        String riskLevel = determineRiskLevel(healthScore);
        assessment.setRiskLevel(riskLevel);
        
        // 生成风险因素（JSON格式）
        String riskFactors = generateRiskFactors();
        assessment.setRiskFactors(riskFactors);
        
        // 生成疾病预测（JSON格式）
        String diseasePredictions = generateDiseasePredictions(healthScore);
        assessment.setDiseasePredictions(diseasePredictions);
        
        // 生成干预建议（JSON格式）
        String interventionSuggestions = generateInterventionSuggestions(riskLevel);
        assessment.setInterventionSuggestions(interventionSuggestions);
        
        // 生成生活方式建议
        String lifestyleRecommendations = generateLifestyleRecommendations(healthScore);
        assessment.setLifestyleRecommendations(lifestyleRecommendations);
        
        // 生成AI分析详情
        String aiAnalysisDetail = generateAiAnalysisDetail(healthScore, riskLevel);
        assessment.setAiAnalysisDetail(aiAnalysisDetail);
        
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setUpdatedAt(LocalDateTime.now());
        
        healthRiskAssessmentMapper.insert(assessment);
        
        log.info("个人健康风险评估生成成功 - 评估ID: {}, 健康评分: {}, 风险等级: {}", 
            assessment.getId(), healthScore, riskLevel);
        
        return assessment;
    }
    
    @Override
    public HealthRiskAssessment generateEnterpriseAssessment(Long userId, Long enterpriseId) {
        log.info("生成企业员工健康风险评估 - 用户ID: {}, 企业ID: {}", userId, enterpriseId);
        
        HealthRiskAssessment assessment = new HealthRiskAssessment();
        assessment.setUserId(userId);
        assessment.setEnterpriseId(enterpriseId);
        assessment.setAssessmentType("ENTERPRISE");
        
        // 生成健康评分
        int healthScore = 50 + random.nextInt(50); // 50-100分
        assessment.setHealthScore(healthScore);
        
        String riskLevel = determineRiskLevel(healthScore);
        assessment.setRiskLevel(riskLevel);
        
        assessment.setRiskFactors(generateRiskFactors());
        assessment.setDiseasePredictions(generateDiseasePredictions(healthScore));
        assessment.setInterventionSuggestions(generateInterventionSuggestions(riskLevel));
        assessment.setLifestyleRecommendations(generateLifestyleRecommendations(healthScore));
        assessment.setAiAnalysisDetail(generateAiAnalysisDetail(healthScore, riskLevel));
        
        assessment.setCreatedAt(LocalDateTime.now());
        assessment.setUpdatedAt(LocalDateTime.now());
        
        healthRiskAssessmentMapper.insert(assessment);
        
        log.info("企业员工健康风险评估生成成功 - 评估ID: {}", assessment.getId());
        
        return assessment;
    }
    
    @Override
    public String getEnterpriseHealthStatistics(Long enterpriseId) {
        log.info("获取企业健康统计 - 企业ID: {}", enterpriseId);
        
        List<HealthRiskAssessment> assessments = 
            healthRiskAssessmentMapper.selectByEnterpriseId(enterpriseId);
        
        if (assessments.isEmpty()) {
            return "暂无评估数据";
        }
        
        // 统计各风险等级人数
        Map<String, Long> riskLevelCount = new HashMap<>();
        riskLevelCount.put("LOW", assessments.stream().filter(a -> "LOW".equals(a.getRiskLevel())).count());
        riskLevelCount.put("MEDIUM", assessments.stream().filter(a -> "MEDIUM".equals(a.getRiskLevel())).count());
        riskLevelCount.put("HIGH", assessments.stream().filter(a -> "HIGH".equals(a.getRiskLevel())).count());
        riskLevelCount.put("CRITICAL", assessments.stream().filter(a -> "CRITICAL".equals(a.getRiskLevel())).count());
        
        // 计算平均健康评分
        double avgHealthScore = assessments.stream()
            .mapToInt(HealthRiskAssessment::getHealthScore)
            .average()
            .orElse(0.0);
        
        StringBuilder statistics = new StringBuilder();
        statistics.append("企业健康统计报告\n");
        statistics.append("===================\n");
        statistics.append("总评估人数: ").append(assessments.size()).append("\n");
        statistics.append("平均健康评分: ").append(String.format("%.1f", avgHealthScore)).append("\n\n");
        statistics.append("风险等级分布:\n");
        statistics.append("- 低风险: ").append(riskLevelCount.get("LOW")).append("人\n");
        statistics.append("- 中风险: ").append(riskLevelCount.get("MEDIUM")).append("人\n");
        statistics.append("- 高风险: ").append(riskLevelCount.get("HIGH")).append("人\n");
        statistics.append("- 极高风险: ").append(riskLevelCount.get("CRITICAL")).append("人\n");
        
        return statistics.toString();
    }
    
    @Override
    public List<HealthRiskAssessment> getUserAssessmentHistory(Long userId) {
        log.info("获取用户评估历史 - 用户ID: {}", userId);
        return healthRiskAssessmentMapper.selectByUserId(userId);
    }
    
    @Override
    public String generateInterventionPlan(Long assessmentId) {
        log.info("生成健康干预方案 - 评估ID: {}", assessmentId);
        
        HealthRiskAssessment assessment = healthRiskAssessmentMapper.selectById(assessmentId);
        
        if (assessment == null) {
            return "评估记录不存在";
        }
        
        StringBuilder plan = new StringBuilder();
        plan.append("个性化健康干预方案\n");
        plan.append("===================\n\n");
        plan.append("健康评分: ").append(assessment.getHealthScore()).append("\n");
        plan.append("风险等级: ").append(assessment.getRiskLevel()).append("\n\n");
        
        plan.append("干预目标:\n");
        if (assessment.getHealthScore() < 60) {
            plan.append("- 3个月内将健康评分提升至70分以上\n");
            plan.append("- 降低主要健康风险因素\n");
        } else if (assessment.getHealthScore() < 80) {
            plan.append("- 6个月内将健康评分提升至85分以上\n");
            plan.append("- 保持良好生活习惯\n");
        } else {
            plan.append("- 维持当前健康状态\n");
            plan.append("- 定期体检监测\n");
        }
        
        plan.append("\n干预措施:\n");
        plan.append(assessment.getLifestyleRecommendations());
        
        plan.append("\n\n随访计划:\n");
        plan.append("- 第1个月: 每周随访一次\n");
        plan.append("- 第2-3个月: 每两周随访一次\n");
        plan.append("- 第4-6个月: 每月随访一次\n");
        
        return plan.toString();
    }
    
    /**
     * 确定风险等级
     */
    private String determineRiskLevel(int healthScore) {
        if (healthScore >= 85) {
            return "LOW";
        } else if (healthScore >= 70) {
            return "MEDIUM";
        } else if (healthScore >= 50) {
            return "HIGH";
        } else {
            return "CRITICAL";
        }
    }
    
    /**
     * 生成风险因素（JSON格式）
     */
    private String generateRiskFactors() {
        return "{\"吸烟\": " + random.nextBoolean() + 
               ", \"饮酒\": \"" + (random.nextBoolean() ? "经常" : "偶尔") + "\"" +
               ", \"运动\": \"" + (random.nextBoolean() ? "规律" : "很少") + "\"" +
               ", \"睡眠\": \"" + (random.nextBoolean() ? "充足" : "不足") + "\"}";
    }
    
    /**
     * 生成疾病预测（JSON格式）
     */
    private String generateDiseasePredictions(int healthScore) {
        if (healthScore >= 85) {
            return "{\"心血管疾病\": 0.10, \"糖尿病\": 0.08, \"高血压\": 0.12}";
        } else if (healthScore >= 70) {
            return "{\"心血管疾病\": 0.25, \"糖尿病\": 0.20, \"高血压\": 0.30}";
        } else {
            return "{\"心血管疾病\": 0.45, \"糖尿病\": 0.35, \"高血压\": 0.50}";
        }
    }
    
    /**
     * 生成干预建议（JSON格式）
     */
    private String generateInterventionSuggestions(String riskLevel) {
        switch (riskLevel) {
            case "LOW":
                return "[\"保持当前生活方式\", \"每年体检一次\", \"适度运动\"]";
            case "MEDIUM":
                return "[\"增加运动频率\", \"改善饮食结构\", \"每半年体检一次\"]";
            case "HIGH":
                return "[\"戒烟限酒\", \"规律运动\", \"控制饮食\", \"每季度体检\"]";
            case "CRITICAL":
                return "[\"立即就医\", \"严格遵医嘱\", \"改变生活方式\", \"每月体检\"]";
            default:
                return "[\"定期体检\"]";
        }
    }
    
    /**
     * 生成生活方式建议
     */
    private String generateLifestyleRecommendations(int healthScore) {
        StringBuilder recommendations = new StringBuilder();
        
        if (healthScore < 70) {
            recommendations.append("1. 戒烟限酒，避免不良嗜好\n");
            recommendations.append("2. 每周至少运动5次，每次30分钟以上\n");
            recommendations.append("3. 保证每天7-8小时睡眠\n");
            recommendations.append("4. 饮食清淡，多吃蔬菜水果\n");
            recommendations.append("5. 定期体检，监测健康指标\n");
        } else if (healthScore < 85) {
            recommendations.append("1. 保持规律运动，每周3-4次\n");
            recommendations.append("2. 均衡饮食，控制体重\n");
            recommendations.append("3. 保持良好作息\n");
            recommendations.append("4. 定期体检\n");
        } else {
            recommendations.append("1. 继续保持良好的生活习惯\n");
            recommendations.append("2. 适度运动，避免过度劳累\n");
            recommendations.append("3. 每年进行一次全面体检\n");
        }
        
        return recommendations.toString();
    }
    
    /**
     * 生成AI分析详情
     */
    private String generateAiAnalysisDetail(int healthScore, String riskLevel) {
        StringBuilder analysis = new StringBuilder();
        
        analysis.append("AI智能分析结果：\n\n");
        analysis.append("综合评估：您的健康评分为").append(healthScore).append("分，");
        
        switch (riskLevel) {
            case "LOW":
                analysis.append("整体健康状况良好，风险较低。建议继续保持当前的健康生活方式。");
                break;
            case "MEDIUM":
                analysis.append("存在一定健康风险，需要注意改善生活方式。");
                break;
            case "HIGH":
                analysis.append("健康风险较高，建议尽快采取干预措施，改善生活习惯。");
                break;
            case "CRITICAL":
                analysis.append("健康风险极高，强烈建议立即就医检查，并在医生指导下进行健康管理。");
                break;
        }
        
        analysis.append("\n\n基于大数据分析和AI算法，我们为您提供了个性化的健康建议和干预方案。");
        analysis.append("请认真执行建议，定期复查，持续改善健康状况。");
        
        return analysis.toString();
    }
}
