package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.EnterpriseCustomer;
import com.lingshu.zhikang.entity.po.HealthRiskAssessment;
import com.lingshu.zhikang.mapper.EnterpriseCustomerMapper;
import com.lingshu.zhikang.service.IHealthRiskAssessmentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 企业健康管理服务控制器
 * 提供B2B企业健康管理服务
 */
@Slf4j
@RestController
@RequestMapping("/api/enterprise")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class EnterpriseServiceController {
    
    @Autowired
    private EnterpriseCustomerMapper enterpriseCustomerMapper;
    
    @Autowired
    private IHealthRiskAssessmentService healthRiskAssessmentService;
    
    /**
     * 企业员工健康风险评估
     */
    @PostMapping("/assessment")
    public Map<String, Object> createEmployeeAssessment(
            @RequestParam Long userId,
            @RequestParam Long enterpriseId,
            @RequestHeader("X-API-Key") String apiKey) {
        
        log.info("企业员工健康评估 - 企业ID: {}, 员工ID: {}", enterpriseId, userId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证企业API密钥
            EnterpriseCustomer enterprise = enterpriseCustomerMapper.selectById(enterpriseId);
            if (enterprise == null || !enterprise.getApiKey().equals(apiKey)) {
                result.put("success", false);
                result.put("message", "API密钥验证失败");
                return result;
            }
            
            // 检查企业状态
            if (!"ACTIVE".equals(enterprise.getStatus())) {
                result.put("success", false);
                result.put("message", "企业服务已过期或暂停");
                return result;
            }
            
            // 生成健康风险评估
            HealthRiskAssessment assessment = healthRiskAssessmentService
                .generateEnterpriseAssessment(userId, enterpriseId);
            
            result.put("success", true);
            result.put("message", "健康风险评估完成");
            result.put("data", assessment);
            
        } catch (Exception e) {
            log.error("企业员工健康评估失败", e);
            result.put("success", false);
            result.put("message", "评估服务异常");
        }
        
        return result;
    }
    
    /**
     * 获取企业健康数据统计
     */
    @GetMapping("/statistics")
    public Map<String, Object> getEnterpriseStatistics(
            @RequestParam Long enterpriseId,
            @RequestHeader("X-API-Key") String apiKey) {
        
        log.info("获取企业健康统计 - 企业ID: {}", enterpriseId);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证API密钥
            EnterpriseCustomer enterprise = enterpriseCustomerMapper.selectById(enterpriseId);
            if (enterprise == null || !enterprise.getApiKey().equals(apiKey)) {
                result.put("success", false);
                result.put("message", "API密钥验证失败");
                return result;
            }
            
            // 获取统计数据
            String statistics = healthRiskAssessmentService
                .getEnterpriseHealthStatistics(enterpriseId);
            
            result.put("success", true);
            result.put("data", statistics);
            result.put("enterpriseName", enterprise.getCompanyName());
            result.put("employeeCount", enterprise.getEmployeeCount());
            
        } catch (Exception e) {
            log.error("获取企业统计失败", e);
            result.put("success", false);
            result.put("message", "获取统计数据失败");
        }
        
        return result;
    }
    
    /**
     * 企业服务信息查询
     */
    @GetMapping("/info")
    public Map<String, Object> getEnterpriseInfo(
            @RequestParam Long enterpriseId,
            @RequestHeader("X-API-Key") String apiKey) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            EnterpriseCustomer enterprise = enterpriseCustomerMapper.selectById(enterpriseId);
            
            if (enterprise == null || !enterprise.getApiKey().equals(apiKey)) {
                result.put("success", false);
                result.put("message", "API密钥验证失败");
                return result;
            }
            
            Map<String, Object> info = new HashMap<>();
            info.put("companyName", enterprise.getCompanyName());
            info.put("servicePlan", enterprise.getServicePlan());
            info.put("employeeCount", enterprise.getEmployeeCount());
            info.put("apiQuota", enterprise.getApiQuota());
            info.put("contractEndDate", enterprise.getContractEndDate());
            info.put("status", enterprise.getStatus());
            
            result.put("success", true);
            result.put("data", info);
            
        } catch (Exception e) {
            log.error("获取企业信息失败", e);
            result.put("success", false);
            result.put("message", "获取企业信息失败");
        }
        
        return result;
    }
    
    /**
     * 生成企业健康报告
     */
    @PostMapping("/report")
    public Map<String, Object> generateEnterpriseReport(
            @RequestParam Long enterpriseId,
            @RequestParam String reportType,
            @RequestHeader("X-API-Key") String apiKey) {
        
        log.info("生成企业健康报告 - 企业ID: {}, 报告类型: {}", enterpriseId, reportType);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证API密钥
            EnterpriseCustomer enterprise = enterpriseCustomerMapper.selectById(enterpriseId);
            if (enterprise == null || !enterprise.getApiKey().equals(apiKey)) {
                result.put("success", false);
                result.put("message", "API密钥验证失败");
                return result;
            }
            
            // 生成报告（这里简化处理）
            Map<String, Object> report = new HashMap<>();
            report.put("enterpriseName", enterprise.getCompanyName());
            report.put("reportType", reportType);
            report.put("generatedAt", System.currentTimeMillis());
            report.put("summary", "企业员工整体健康状况良好，建议加强慢性病预防");
            
            result.put("success", true);
            result.put("message", "报告生成成功");
            result.put("data", report);
            
        } catch (Exception e) {
            log.error("生成企业报告失败", e);
            result.put("success", false);
            result.put("message", "报告生成失败");
        }
        
        return result;
    }
}
