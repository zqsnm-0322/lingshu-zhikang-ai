package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.service.IBusinessStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商业数据统计控制器
 * 提供项目商业价值和运营数据的API接口
 */
@Slf4j
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class BusinessStatisticsController {
    
    @Autowired
    private IBusinessStatisticsService businessStatisticsService;
    
    /**
     * 获取整体运营数据概览
     */
    @GetMapping("/overview")
    public Map<String, Object> getOverview() {
        log.info("获取整体运营数据概览");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> statistics = businessStatisticsService.getOverallStatistics();
            
            result.put("success", true);
            result.put("data", statistics);
            result.put("message", "数据获取成功");
            
        } catch (Exception e) {
            log.error("获取运营数据失败", e);
            result.put("success", false);
            result.put("message", "数据获取失败");
        }
        
        return result;
    }
    
    /**
     * 获取收入统计数据
     */
    @GetMapping("/revenue")
    public Map<String, Object> getRevenue(
            @RequestParam(defaultValue = "2024-01-01") String startDate,
            @RequestParam(defaultValue = "2024-12-31") String endDate) {
        
        log.info("获取收入统计 - 时间范围: {} 至 {}", startDate, endDate);
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> revenue = businessStatisticsService
                .getRevenueStatistics(startDate, endDate);
            
            result.put("success", true);
            result.put("data", revenue);
            result.put("message", "收入数据获取成功");
            
        } catch (Exception e) {
            log.error("获取收入统计失败", e);
            result.put("success", false);
            result.put("message", "收入数据获取失败");
        }
        
        return result;
    }
    
    /**
     * 获取用户统计数据
     */
    @GetMapping("/users")
    public Map<String, Object> getUsers() {
        log.info("获取用户统计数据");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> userStats = businessStatisticsService.getUserStatistics();
            
            result.put("success", true);
            result.put("data", userStats);
            result.put("message", "用户数据获取成功");
            
        } catch (Exception e) {
            log.error("获取用户统计失败", e);
            result.put("success", false);
            result.put("message", "用户数据获取失败");
        }
        
        return result;
    }
    
    /**
     * 获取AI服务统计数据
     */
    @GetMapping("/ai-service")
    public Map<String, Object> getAiService() {
        log.info("获取AI服务统计数据");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> aiStats = businessStatisticsService.getAiServiceStatistics();
            
            result.put("success", true);
            result.put("data", aiStats);
            result.put("message", "AI服务数据获取成功");
            
        } catch (Exception e) {
            log.error("获取AI服务统计失败", e);
            result.put("success", false);
            result.put("message", "AI服务数据获取失败");
        }
        
        return result;
    }
    
    /**
     * 获取企业客户统计数据
     */
    @GetMapping("/enterprise")
    public Map<String, Object> getEnterprise() {
        log.info("获取企业客户统计数据");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> enterpriseStats = businessStatisticsService
                .getEnterpriseStatistics();
            
            result.put("success", true);
            result.put("data", enterpriseStats);
            result.put("message", "企业数据获取成功");
            
        } catch (Exception e) {
            log.error("获取企业统计失败", e);
            result.put("success", false);
            result.put("message", "企业数据获取失败");
        }
        
        return result;
    }
    
    /**
     * 获取API调用统计数据
     */
    @GetMapping("/api-calls")
    public Map<String, Object> getApiCalls() {
        log.info("获取API调用统计数据");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> apiStats = businessStatisticsService.getApiCallStatistics();
            
            result.put("success", true);
            result.put("data", apiStats);
            result.put("message", "API调用数据获取成功");
            
        } catch (Exception e) {
            log.error("获取API调用统计失败", e);
            result.put("success", false);
            result.put("message", "API调用数据获取失败");
        }
        
        return result;
    }
    
    /**
     * 获取商业价值报告（综合数据）
     */
    @GetMapping("/business-value")
    public Map<String, Object> getBusinessValue() {
        log.info("获取商业价值报告");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Object> report = new HashMap<>();
            
            // 整体数据
            report.put("overview", businessStatisticsService.getOverallStatistics());
            
            // 收入数据
            report.put("revenue", businessStatisticsService
                .getRevenueStatistics("2024-01-01", "2024-12-31"));
            
            // 用户数据
            report.put("users", businessStatisticsService.getUserStatistics());
            
            // AI服务数据
            report.put("aiService", businessStatisticsService.getAiServiceStatistics());
            
            // 企业客户数据
            report.put("enterprise", businessStatisticsService.getEnterpriseStatistics());
            
            // API调用数据
            report.put("apiCalls", businessStatisticsService.getApiCallStatistics());
            
            // 商业模式说明
            Map<String, Object> businessModel = new HashMap<>();
            businessModel.put("revenueStreams", new String[]{
                "C端用户付费（会员订阅、单次服务）- 40%",
                "B端企业服务（企业健康管理、API服务）- 35%",
                "数据服务（健康数据洞察报告）- 15%",
                "广告与推广（医疗设备、健康产品）- 10%"
            });
            businessModel.put("targetMarket", new String[]{
                "主要市场：C端个人用户（健康管理需求人群）",
                "次要市场：B端企业客户（员工健康管理）",
                "潜在市场：医疗机构（AI辅助诊断工具）"
            });
            businessModel.put("competitiveAdvantages", new String[]{
                "技术优势：多模态AI交互，RAG增强分析",
                "服务优势：7x24小时在线，响应速度快",
                "数据优势：积累用户健康数据，形成数据壁垒",
                "生态优势：完整的健康管理闭环"
            });
            
            report.put("businessModel", businessModel);
            
            result.put("success", true);
            result.put("data", report);
            result.put("message", "商业价值报告生成成功");
            result.put("generatedAt", System.currentTimeMillis());
            
        } catch (Exception e) {
            log.error("获取商业价值报告失败", e);
            result.put("success", false);
            result.put("message", "报告生成失败");
        }
        
        return result;
    }
}
