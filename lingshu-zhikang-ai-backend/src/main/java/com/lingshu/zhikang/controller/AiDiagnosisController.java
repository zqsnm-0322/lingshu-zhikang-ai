package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.DiagnosisReport;
import com.lingshu.zhikang.service.IAiDiagnosisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI智能诊断控制器
 * 提供付费的专业级AI诊断服务
 */
@Slf4j
@RestController
@RequestMapping("/api/ai-diagnosis")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AiDiagnosisController {
    
    @Autowired
    private IAiDiagnosisService aiDiagnosisService;
    
    /**
     * 生成AI诊断报告（付费服务）
     */
    @PostMapping("/generate")
    public Map<String, Object> generateDiagnosisReport(
            @RequestParam Long userId,
            @RequestParam String symptoms,
            @RequestParam(defaultValue = "false") Boolean isPaid) {
        
        log.info("收到诊断请求 - 用户ID: {}, 付费: {}", userId, isPaid);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查权限
            if (isPaid && !aiDiagnosisService.checkDiagnosisPermission(userId)) {
                result.put("success", false);
                result.put("message", "您需要开通会员才能使用专业诊断服务");
                result.put("code", "PERMISSION_DENIED");
                return result;
            }
            
            // 生成诊断报告
            DiagnosisReport report = aiDiagnosisService.generateDiagnosisReport(
                userId, symptoms, isPaid
            );
            
            result.put("success", true);
            result.put("message", "诊断报告生成成功");
            result.put("data", report);
            result.put("serviceType", isPaid ? "专业版诊断" : "基础版诊断");
            
        } catch (Exception e) {
            log.error("生成诊断报告失败", e);
            result.put("success", false);
            result.put("message", "诊断服务暂时不可用，请稍后重试");
        }
        
        return result;
    }
    
    /**
     * 获取诊断报告详情
     */
    @GetMapping("/report/{reportId}")
    public Map<String, Object> getDiagnosisReport(
            @PathVariable Long reportId,
            @RequestParam Long userId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            DiagnosisReport report = aiDiagnosisService.getDiagnosisReport(userId, reportId);
            
            if (report == null) {
                result.put("success", false);
                result.put("message", "报告不存在");
                return result;
            }
            
            result.put("success", true);
            result.put("data", report);
            
        } catch (Exception e) {
            log.error("获取诊断报告失败", e);
            result.put("success", false);
            result.put("message", "获取报告失败");
        }
        
        return result;
    }
    
    /**
     * 医学影像分析（高级功能）
     */
    @PostMapping("/analyze-image")
    public Map<String, Object> analyzeMedicalImage(
            @RequestParam Long userId,
            @RequestParam String imageUrl,
            @RequestParam String imageType) {
        
        log.info("收到影像分析请求 - 用户ID: {}, 影像类型: {}", userId, imageType);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 检查权限（影像分析需要专业版会员）
            if (!aiDiagnosisService.checkDiagnosisPermission(userId)) {
                result.put("success", false);
                result.put("message", "医学影像分析需要专业版会员");
                result.put("upgradeRequired", true);
                return result;
            }
            
            String analysis = aiDiagnosisService.analyzeMedicalImage(
                userId, imageUrl, imageType
            );
            
            result.put("success", true);
            result.put("message", "影像分析完成");
            result.put("analysis", analysis);
            result.put("imageType", imageType);
            
        } catch (Exception e) {
            log.error("影像分析失败", e);
            result.put("success", false);
            result.put("message", "影像分析失败，请稍后重试");
        }
        
        return result;
    }
    
    /**
     * 检查诊断服务权限
     */
    @GetMapping("/check-permission")
    public Map<String, Object> checkPermission(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        Boolean hasPermission = aiDiagnosisService.checkDiagnosisPermission(userId);
        
        result.put("success", true);
        result.put("hasPermission", hasPermission);
        result.put("message", hasPermission ? "您可以使用专业诊断服务" : "请开通会员使用专业诊断");
        
        return result;
    }
}
