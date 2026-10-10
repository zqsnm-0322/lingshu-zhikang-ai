package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.HealthButlerConversation;
import com.lingshu.zhikang.entity.po.HealthButlerReport;
import com.lingshu.zhikang.entity.po.HealthPlan;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.service.IHealthButlerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AI健康管家控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health-butler")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class HealthButlerController {

    private final IHealthButlerService healthButlerService;

    // ===== 晨报 CRUD =====

    /**
     * 生成每日健康晨报
     */
    @PostMapping("/report/generate")
    public ResponseResult<HealthButlerReport> generateReport(@RequestParam Long userId) {
        try {
            HealthButlerReport report = healthButlerService.generateDailyReport(userId);
            return ResponseResult.ok(report);
        } catch (Exception e) {
            log.error("生成晨报失败", e);
            return ResponseResult.error("生成失败: " + e.getMessage());
        }
    }

    /**
     * 获取最新晨报
     */
    @GetMapping("/report/latest")
    public ResponseResult<HealthButlerReport> getLatestReport(@RequestParam Long userId) {
        try {
            HealthButlerReport result = healthButlerService.getLatestReport(userId);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("获取最新晨报失败", e);
            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取指定日期晨报
     */
    @GetMapping("/report")
    public ResponseResult<HealthButlerReport> getReportByDate(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate reportDate) {
        try {
            HealthButlerReport result = healthButlerService.getReportByDate(userId, reportDate);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("获取晨报失败", e);
            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取晨报历史
     */
    @GetMapping("/report/history")
    public ResponseResult<List<HealthButlerReport>> getReportHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "30") int limit) {
        try {
            List<HealthButlerReport> result = healthButlerService.getReportHistory(userId, limit);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("获取晨报历史失败", e);
            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }

    // ===== 对话 =====

    /**
     * 健康管家对话
     */
    @PostMapping("/chat")
    public ResponseResult<String> chat(
            @RequestParam Long userId,
            @RequestParam String conversationId,
            @RequestParam String message) {
        try {
            String reply = healthButlerService.chat(userId, conversationId, message);
            return ResponseResult.ok(reply);
        } catch (Exception e) {
            log.error("对话失败", e);
            return ResponseResult.error("对话失败: " + e.getMessage());
        }
    }

    /**
     * 获取对话历史
     */
    @GetMapping("/chat/history")
    public ResponseResult<List<HealthButlerConversation>> getConversationHistory(
            @RequestParam String conversationId) {
        try {
            List<HealthButlerConversation> result = healthButlerService.getConversationHistory(conversationId);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("获取对话历史失败", e);
            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }

    // ===== 健康计划 CRUD =====

    /**
     * 创建健康计划
     */
    @PostMapping("/plan")
    public ResponseResult<HealthPlan> createPlan(@RequestBody HealthPlan plan) {
        try {
            HealthPlan result = healthButlerService.createPlan(plan);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("创建健康计划失败", e);
            return ResponseResult.error("创建失败: " + e.getMessage());
        }
    }

    /**
     * 更新健康计划
     */
    @PutMapping("/plan")
    public ResponseResult<HealthPlan> updatePlan(@RequestBody HealthPlan plan) {
        try {
            HealthPlan result = healthButlerService.updatePlan(plan);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("更新健康计划失败", e);
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户健康计划列表
     */
    @GetMapping("/plan/list")
    public ResponseResult<List<HealthPlan>> getUserPlans(@RequestParam Long userId) {
        try {
            List<HealthPlan> result = healthButlerService.getUserPlans(userId);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("查询健康计划列表失败", e);
            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 获取健康计划详情
     */
    @GetMapping("/plan/{id}")
    public ResponseResult<HealthPlan> getPlanById(@PathVariable Long id) {
        try {
            HealthPlan result = healthButlerService.getPlanById(id);
            if (result == null) {
                return ResponseResult.error(404, "计划不存在");
            }
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("查询计划详情失败", e);
            return ResponseResult.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 更新计划进度
     */
    @PutMapping("/plan/{id}/progress")
    public ResponseResult<Boolean> updatePlanProgress(
            @PathVariable Long id,
            @RequestParam Integer progress) {
        try {
            boolean success = healthButlerService.updatePlanProgress(id, progress);
            return ResponseResult.ok(success);
        } catch (Exception e) {
            log.error("更新计划进度失败", e);
            return ResponseResult.error("更新失败: " + e.getMessage());
        }
    }

    /**
     * 删除健康计划
     */
    @DeleteMapping("/plan/{id}")
    public ResponseResult<Boolean> deletePlan(@PathVariable Long id) {
        try {
            boolean success = healthButlerService.deletePlan(id);
            return ResponseResult.ok(success);
        } catch (Exception e) {
            log.error("删除健康计划失败", e);
            return ResponseResult.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * AI个性化建议生成
     */
    @PostMapping("/report/analyze")
    public ResponseResult<Map<String, Object>> analyze(@RequestParam Long userId) {
        try {
            Map<String, Object> result = healthButlerService.generatePersonalizedSuggestions(userId);
            return ResponseResult.ok(result);
        } catch (Exception e) {
            log.error("AI分析失败", e);
            return ResponseResult.error("分析失败: " + e.getMessage());
        }
    }
}
