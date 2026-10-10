package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.HealthWarning;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.service.IHealthWarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 智能预警控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health-warning")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class HealthWarningController {

    @Autowired
    private IHealthWarningService warningService;

    /**
     * 创建预警
     */
    @PostMapping
    public ResponseResult<HealthWarning> createWarning(@RequestBody HealthWarning warning) {
        try {
            HealthWarning created = warningService.createWarning(warning);
            return ResponseResult.ok(created);
        } catch (Exception e) {
            log.error("创建预警失败", e);
            return ResponseResult.error("创建预警失败: " + e.getMessage());
        }
    }

    /**
     * 检查并生成预警
     */
    @PostMapping("/check")
    public ResponseResult<List<HealthWarning>> checkAndGenerateWarnings(@RequestParam Long userId) {
        try {
            List<HealthWarning> warnings = warningService.checkAndGenerateWarnings(userId);
            return ResponseResult.ok(warnings);
        } catch (Exception e) {
            log.error("检查预警失败", e);
            return ResponseResult.error("检查预警失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户的预警列表
     */
    @GetMapping("/list")
    public ResponseResult<List<HealthWarning>> getUserWarnings(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "20") int limit) {
        try {
            List<HealthWarning> warnings = warningService.getUserWarnings(userId, limit);
            return ResponseResult.ok(warnings);
        } catch (Exception e) {
            log.error("获取预警列表失败", e);
            return ResponseResult.error("获取预警列表失败: " + e.getMessage());
        }
    }

    /**
     * 获取未处理的预警
     */
    @GetMapping("/pending")
    public ResponseResult<List<HealthWarning>> getPendingWarnings(@RequestParam Long userId) {
        try {
            List<HealthWarning> warnings = warningService.getPendingWarnings(userId);
            return ResponseResult.ok(warnings);
        } catch (Exception e) {
            log.error("获取未处理预警失败", e);
            return ResponseResult.error("获取未处理预警失败: " + e.getMessage());
        }
    }

    /**
     * 更新预警状态
     */
    @PutMapping("/{id}/status")
    public ResponseResult<Boolean> updateWarningStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String note) {
        try {
            boolean success = warningService.updateWarningStatus(id, status, note);
            return ResponseResult.ok(success);
        } catch (Exception e) {
            log.error("更新预警状态失败", e);
            return ResponseResult.error("更新预警状态失败: " + e.getMessage());
        }
    }

    /**
     * 获取预警详情
     */
    @GetMapping("/{id}")
    public ResponseResult<HealthWarning> getWarningById(@PathVariable Long id) {
        try {
            HealthWarning warning = warningService.getWarningById(id);
            return ResponseResult.ok(warning);
        } catch (Exception e) {
            log.error("获取预警详情失败", e);
            return ResponseResult.error("获取预警详情失败: " + e.getMessage());
        }
    }

    /**
     * 获取预警统计
     */
    @GetMapping("/statistics")
    public ResponseResult<Map<String, Object>> getWarningStatistics(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "30") int days) {
        try {
            Map<String, Object> statistics = warningService.getWarningStatistics(userId, days);
            return ResponseResult.ok(statistics);
        } catch (Exception e) {
            log.error("获取预警统计失败", e);
            return ResponseResult.error("获取预警统计失败: " + e.getMessage());
        }
    }

    /**
     * 健康风险评估
     */
    @GetMapping("/risk-assessment")
    public ResponseResult<Map<String, Object>> assessHealthRisk(@RequestParam Long userId) {
        try {
            Map<String, Object> assessment = warningService.assessHealthRisk(userId);
            return ResponseResult.ok(assessment);
        } catch (Exception e) {
            log.error("健康风险评估失败", e);
            return ResponseResult.error("健康风险评估失败: " + e.getMessage());
        }
    }
}

