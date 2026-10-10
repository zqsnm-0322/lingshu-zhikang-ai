package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.ApiCallLog;

import java.util.Map;

/**
 * AI医疗知识库API服务接口
 * 提供SaaS模式的API服务
 */
public interface IApiService {
    
    /**
     * 医疗问答API
     * @param tenantId 租户ID
     * @param question 问题
     * @return 回答
     */
    String medicalQA(Long tenantId, String question);
    
    /**
     * 症状分析API
     * @param tenantId 租户ID
     * @param symptoms 症状描述
     * @return 分析结果
     */
    Map<String, Object> symptomAnalysis(Long tenantId, String symptoms);
    
    /**
     * 药物查询API
     * @param tenantId 租户ID
     * @param medicineName 药物名称
     * @return 药物信息
     */
    Map<String, Object> medicineQuery(Long tenantId, String medicineName);
    
    /**
     * 健康建议API
     * @param tenantId 租户ID
     * @param userProfile 用户画像
     * @return 健康建议
     */
    String healthAdvice(Long tenantId, Map<String, Object> userProfile);
    
    /**
     * 验证API密钥
     * @param apiKey API密钥
     * @return 租户ID
     */
    Long validateApiKey(String apiKey);
    
    /**
     * 检查API调用配额
     * @param tenantId 租户ID
     * @return 是否有配额
     */
    Boolean checkApiQuota(Long tenantId);
    
    /**
     * 记录API调用
     * @param log API调用日志
     */
    void logApiCall(ApiCallLog log);
    
    /**
     * 获取API调用统计
     * @param tenantId 租户ID
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 统计数据
     */
    Map<String, Object> getApiStatistics(Long tenantId, String startDate, String endDate);
}
