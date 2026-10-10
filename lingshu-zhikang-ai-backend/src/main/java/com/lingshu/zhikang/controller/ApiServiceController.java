package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.ApiCallLog;
import com.lingshu.zhikang.service.IApiService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * AI医疗知识库API服务控制器
 * 提供SaaS模式的RESTful API服务
 */
@Slf4j
@RestController
@RequestMapping("/api/v1")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ApiServiceController {
    
    @Autowired
    private IApiService apiService;
    
    /**
     * 医疗问答API
     */
    @PostMapping("/medical-qa")
    public Map<String, Object> medicalQA(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody Map<String, String> request) {
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证API密钥
            Long tenantId = apiService.validateApiKey(apiKey);
            if (tenantId == null) {
                result.put("success", false);
                result.put("error", "Invalid API key");
                result.put("code", "AUTH_FAILED");
                return result;
            }
            
            // 检查配额
            if (!apiService.checkApiQuota(tenantId)) {
                result.put("success", false);
                result.put("error", "API quota exceeded");
                result.put("code", "QUOTA_EXCEEDED");
                return result;
            }
            
            String question = request.get("question");
            String answer = apiService.medicalQA(tenantId, question);
            
            result.put("success", true);
            result.put("answer", answer);
            result.put("timestamp", System.currentTimeMillis());
            
            // 记录API调用
            logApiCall(tenantId, "/medical-qa", request.toString(), 
                      200, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("医疗问答API调用失败", e);
            result.put("success", false);
            result.put("error", "Internal server error");
        }
        
        return result;
    }
    
    /**
     * 症状分析API
     */
    @PostMapping("/symptom-analysis")
    public Map<String, Object> symptomAnalysis(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody Map<String, String> request) {
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long tenantId = apiService.validateApiKey(apiKey);
            if (tenantId == null) {
                result.put("success", false);
                result.put("error", "Invalid API key");
                return result;
            }
            
            if (!apiService.checkApiQuota(tenantId)) {
                result.put("success", false);
                result.put("error", "API quota exceeded");
                return result;
            }
            
            String symptoms = request.get("symptoms");
            Map<String, Object> analysis = apiService.symptomAnalysis(tenantId, symptoms);
            
            result.put("success", true);
            result.put("data", analysis);
            
            logApiCall(tenantId, "/symptom-analysis", request.toString(), 
                      200, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("症状分析API调用失败", e);
            result.put("success", false);
            result.put("error", "Internal server error");
        }
        
        return result;
    }
    
    /**
     * 药物查询API
     */
    @GetMapping("/medicine-query")
    public Map<String, Object> medicineQuery(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestParam String medicineName) {
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long tenantId = apiService.validateApiKey(apiKey);
            if (tenantId == null) {
                result.put("success", false);
                result.put("error", "Invalid API key");
                return result;
            }
            
            if (!apiService.checkApiQuota(tenantId)) {
                result.put("success", false);
                result.put("error", "API quota exceeded");
                return result;
            }
            
            Map<String, Object> medicineInfo = apiService.medicineQuery(tenantId, medicineName);
            
            result.put("success", true);
            result.put("data", medicineInfo);
            
            logApiCall(tenantId, "/medicine-query", medicineName, 
                      200, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("药物查询API调用失败", e);
            result.put("success", false);
            result.put("error", "Internal server error");
        }
        
        return result;
    }
    
    /**
     * 健康建议API
     */
    @PostMapping("/health-advice")
    public Map<String, Object> healthAdvice(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestBody Map<String, Object> userProfile) {
        
        long startTime = System.currentTimeMillis();
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long tenantId = apiService.validateApiKey(apiKey);
            if (tenantId == null) {
                result.put("success", false);
                result.put("error", "Invalid API key");
                return result;
            }
            
            if (!apiService.checkApiQuota(tenantId)) {
                result.put("success", false);
                result.put("error", "API quota exceeded");
                return result;
            }
            
            String advice = apiService.healthAdvice(tenantId, userProfile);
            
            result.put("success", true);
            result.put("advice", advice);
            
            logApiCall(tenantId, "/health-advice", userProfile.toString(), 
                      200, System.currentTimeMillis() - startTime);
            
        } catch (Exception e) {
            log.error("健康建议API调用失败", e);
            result.put("success", false);
            result.put("error", "Internal server error");
        }
        
        return result;
    }
    
    /**
     * 获取API调用统计
     */
    @GetMapping("/statistics")
    public Map<String, Object> getStatistics(
            @RequestHeader("X-API-Key") String apiKey,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Long tenantId = apiService.validateApiKey(apiKey);
            if (tenantId == null) {
                result.put("success", false);
                result.put("error", "Invalid API key");
                return result;
            }
            
            Map<String, Object> statistics = apiService.getApiStatistics(
                tenantId, startDate, endDate
            );
            
            result.put("success", true);
            result.put("data", statistics);
            
        } catch (Exception e) {
            log.error("获取API统计失败", e);
            result.put("success", false);
            result.put("error", "Internal server error");
        }
        
        return result;
    }
    
    /**
     * 记录API调用日志
     */
    private void logApiCall(Long tenantId, String endpoint, String params, 
                           int status, long responseTime) {
        try {
            ApiCallLog callLog = new ApiCallLog();
            callLog.setTenantId(tenantId);
            callLog.setApiEndpoint(endpoint);
            callLog.setRequestParams(params);
            callLog.setResponseStatus(status);
            callLog.setResponseTimeMs((int) responseTime);
            callLog.setBillingAmount(new BigDecimal("0.10")); // 0.1元/次
            callLog.setCreatedAt(LocalDateTime.now());
            
            apiService.logApiCall(callLog);
        } catch (Exception e) {
            log.error("记录API调用日志失败", e);
        }
    }
}
