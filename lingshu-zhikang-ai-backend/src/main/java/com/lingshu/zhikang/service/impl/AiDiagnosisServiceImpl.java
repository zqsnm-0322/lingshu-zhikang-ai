package com.lingshu.zhikang.service.impl;

import com.lingshu.zhikang.entity.po.DiagnosisReport;
import com.lingshu.zhikang.entity.po.MemberSubscription;
import com.lingshu.zhikang.mapper.DiagnosisReportMapper;
import com.lingshu.zhikang.mapper.MemberSubscriptionMapper;
import com.lingshu.zhikang.service.IAiDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * AI智能诊断服务实现
 */
@Slf4j
@Service
public class AiDiagnosisServiceImpl implements IAiDiagnosisService {
    
    @Autowired
    private DiagnosisReportMapper diagnosisReportMapper;
    
    @Autowired
    private MemberSubscriptionMapper memberSubscriptionMapper;
    
    @Autowired
    private ChatClient chatClient;
    
    @Override
    public DiagnosisReport generateDiagnosisReport(Long userId, String symptoms, Boolean isPaid) {
        log.info("生成AI诊断报告 - 用户ID: {}, 症状: {}, 付费: {}", userId, symptoms, isPaid);
        
        // 构建AI诊断提示词
        String prompt = buildDiagnosisPrompt(symptoms, isPaid);
        
        // 调用AI生成诊断结果
        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();
        
        // 解析AI响应并构建诊断报告
        DiagnosisReport report = parseDiagnosisResponse(userId, symptoms, aiResponse, isPaid);
        
        // 保存诊断报告
        diagnosisReportMapper.insert(report);
        
        log.info("诊断报告生成成功 - 报告ID: {}", report.getId());
        return report;
    }
    
    @Override
    public DiagnosisReport getDiagnosisReport(Long userId, Long reportId) {
        return diagnosisReportMapper.selectById(reportId);
    }
    
    @Override
    public Boolean checkDiagnosisPermission(Long userId) {
        // 检查用户是否有有效的会员订阅
        MemberSubscription subscription = memberSubscriptionMapper.selectActiveSubscription(userId);
        
        if (subscription == null) {
            return false;
        }
        
        // 检查订阅是否在有效期内
        LocalDate now = LocalDate.now();
        return !now.isBefore(subscription.getStartDate()) && 
               !now.isAfter(subscription.getEndDate()) &&
               "ACTIVE".equals(subscription.getStatus());
    }
    
    @Override
    public String analyzeMedicalImage(Long userId, String imageUrl, String imageType) {
        log.info("分析医学影像 - 用户ID: {}, 影像类型: {}", userId, imageType);
        
        String prompt = String.format(
            "作为专业的医学影像分析AI，请分析以下%s影像，提供详细的分析报告，包括：\n" +
            "1. 影像质量评估\n" +
            "2. 异常发现\n" +
            "3. 可能的诊断\n" +
            "4. 建议的进一步检查\n" +
            "影像URL: %s",
            imageType, imageUrl
        );
        
        return chatClient.prompt()
                .user(prompt)
                .call()
                .content();
    }
    
    /**
     * 构建诊断提示词
     */
    private String buildDiagnosisPrompt(String symptoms, Boolean isPaid) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("作为专业的AI医疗诊断助手，请根据以下症状提供");
        
        if (isPaid) {
            prompt.append("详细的专业级诊断报告：\n\n");
            prompt.append("症状描述：").append(symptoms).append("\n\n");
            prompt.append("请提供以下内容（JSON格式）：\n");
            prompt.append("1. disease_probabilities: 可能的疾病及概率（至少3种）\n");
            prompt.append("2. recommended_tests: 推荐的检查项目（详细说明）\n");
            prompt.append("3. treatment_suggestions: 治疗建议（包括药物和非药物治疗）\n");
            prompt.append("4. urgency_level: 紧急程度（LOW/MEDIUM/HIGH/URGENT）\n");
            prompt.append("5. detailed_analysis: 详细的医学分析\n");
            prompt.append("6. accuracy_score: 诊断准确率（0-1之间）\n");
        } else {
            prompt.append("基础诊断建议：\n\n");
            prompt.append("症状描述：").append(symptoms).append("\n\n");
            prompt.append("请提供简要的诊断建议和就医指导。");
        }
        
        return prompt.toString();
    }
    
    /**
     * 解析AI诊断响应
     */
    private DiagnosisReport parseDiagnosisResponse(Long userId, String symptoms, 
                                                   String aiResponse, Boolean isPaid) {
        // 这里简化处理，实际应该解析JSON格式的响应
        return DiagnosisReport.builder()
                .userId(userId)
                .symptoms(symptoms)
                .aiAnalysis(aiResponse)
                .isPaid(isPaid)
                .accuracyScore(isPaid ? 0.95 : 0.75)
                .urgencyLevel("MEDIUM")
                .createdAt(LocalDateTime.now())
                .build();
    }
}
