package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.HealthStatistics;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.service.IHealthStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

/**
 * 健康数据大屏控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health-dashboard")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class HealthDashboardController {

    @Autowired
    private IHealthStatisticsService statisticsService;

    /**
     * 获取健康数据大屏
     */
    @GetMapping
    public ResponseResult<Map<String, Object>> getHealthDashboard(@RequestParam Long userId) {
        try {
            Map<String, Object> dashboard = statisticsService.getHealthDashboard(userId);
            return ResponseResult.ok(dashboard);
        } catch (Exception e) {
            log.error("获取健康数据大屏失败", e);
            return ResponseResult.error("获取健康数据大屏失败: " + e.getMessage());
        }
    }

    /**
     * 生成每日统计
     */
    @PostMapping("/statistics/daily")
    public ResponseResult<HealthStatistics> generateDailyStatistics(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        try {
            HealthStatistics statistics = statisticsService.generateDailyStatistics(userId, date);
            return ResponseResult.ok(statistics);
        } catch (Exception e) {
            log.error("生成每日统计失败", e);
            return ResponseResult.error("生成每日统计失败: " + e.getMessage());
        }
    }

    /**
     * 生成周统计
     */
    @PostMapping("/statistics/weekly")
    public ResponseResult<HealthStatistics> generateWeeklyStatistics(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate weekStart) {
        try {
            HealthStatistics statistics = statisticsService.generateWeeklyStatistics(userId, weekStart);
            return ResponseResult.ok(statistics);
        } catch (Exception e) {
            log.error("生成周统计失败", e);
            return ResponseResult.error("生成周统计失败: " + e.getMessage());
        }
    }

    /**
     * 生成月统计
     */
    @PostMapping("/statistics/monthly")
    public ResponseResult<HealthStatistics> generateMonthlyStatistics(
            @RequestParam Long userId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate month) {
        try {
            HealthStatistics statistics = statisticsService.generateMonthlyStatistics(userId, month);
            return ResponseResult.ok(statistics);
        } catch (Exception e) {
            log.error("生成月统计失败", e);
            return ResponseResult.error("生成月统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取趋势数据
     */
    @GetMapping("/trend")
    public ResponseResult<Map<String, Object>> getTrendData(
            @RequestParam Long userId,
            @RequestParam String indicatorType,
            @RequestParam(defaultValue = "month") String period) {
        try {
            Map<String, Object> trendData = statisticsService.getTrendData(userId, indicatorType, period);
            return ResponseResult.ok(trendData);
        } catch (Exception e) {
            log.error("获取趋势数据失败", e);
            return ResponseResult.error("获取趋势数据失败: " + e.getMessage());
        }
    }

    /**
     * 获取对比数据
     */
    @GetMapping("/comparison")
    public ResponseResult<Map<String, Object>> getComparisonData(@RequestParam Long userId) {
        try {
            Map<String, Object> comparison = statisticsService.getComparisonData(userId);
            return ResponseResult.ok(comparison);
        } catch (Exception e) {
            log.error("获取对比数据失败", e);
            return ResponseResult.error("获取对比数据失败: " + e.getMessage());
        }
    }

    /**
     * 生成健康报告
     */
    @PostMapping("/report")
    public ResponseResult<Map<String, Object>> generateHealthReport(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "weekly") String reportType) {
        try {
            Map<String, Object> report = statisticsService.generateHealthReport(userId, reportType);
            return ResponseResult.ok(report);
        } catch (Exception e) {
            log.error("生成健康报告失败", e);
            return ResponseResult.error("生成健康报告失败: " + e.getMessage());
        }
    }
}

