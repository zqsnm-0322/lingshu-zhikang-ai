package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.DiagnosisReport;

/**
 * AI智能诊断服务接口
 * 提供付费的专业级AI诊断服务
 */
public interface IAiDiagnosisService {
    
    /**
     * 生成AI诊断报告
     * @param userId 用户ID
     * @param symptoms 症状描述
     * @param isPaid 是否付费
     * @return 诊断报告
     */
    DiagnosisReport generateDiagnosisReport(Long userId, String symptoms, Boolean isPaid);
    
    /**
     * 获取用户的诊断报告
     * @param userId 用户ID
     * @param reportId 报告ID
     * @return 诊断报告
     */
    DiagnosisReport getDiagnosisReport(Long userId, Long reportId);
    
    /**
     * 检查用户是否有权限访问付费诊断
     * @param userId 用户ID
     * @return 是否有权限
     */
    Boolean checkDiagnosisPermission(Long userId);
    
    /**
     * 分析医学影像
     * @param userId 用户ID
     * @param imageUrl 影像URL
     * @param imageType 影像类型(X-RAY, CT, MRI)
     * @return 分析结果
     */
    String analyzeMedicalImage(Long userId, String imageUrl, String imageType);
}
