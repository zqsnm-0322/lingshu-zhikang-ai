package com.lingshu.zhikang.service;

import java.util.List;
import java.util.Map;

/**
 * AI智能问诊服务接口
 * 
 * @author ZQ
 */
public interface IAIConsultationService {
    
    /**
     * 开始智能问诊会话
     * @param userId 用户ID
     * @param initialSymptoms 初始症状描述
     * @return 问诊会话ID和首个问题
     */
    Map<String, Object> startConsultation(Long userId, String initialSymptoms);
    
    /**
     * 回答问诊问题
     * @param sessionId 会话ID
     * @param questionId 问题ID
     * @param answer 用户答案
     * @return 下一个问题或诊断结果
     */
    Map<String, Object> answerQuestion(String sessionId, String questionId, String answer);
    
    /**
     * 生成最终诊断报告
     * @param sessionId 会话ID
     * @return 完整的诊断报告
     */
    Map<String, Object> generateDiagnosisReport(String sessionId);
    
    /**
     * 获取问诊历史
     * @param userId 用户ID
     * @return 历史问诊记录
     */
    List<Map<String, Object>> getConsultationHistory(Long userId);
    
    /**
     * 获取问诊详情
     * @param sessionId 会话ID
     * @return 问诊详情
     */
    Map<String, Object> getConsultationDetail(String sessionId);
    
    /**
     * 紧急症状检测
     * @param symptoms 症状描述
     * @return 紧急程度评估
     */
    Map<String, Object> emergencyCheck(String symptoms);
    
    /**
     * 获取专科推荐
     * @param symptoms 症状
     * @param diagnosis 初步诊断
     * @return 推荐的专科科室
     */
    Map<String, Object> getDepartmentRecommendation(String symptoms, String diagnosis);
}