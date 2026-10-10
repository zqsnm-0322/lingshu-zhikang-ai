package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.HealthStatistics;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 健康数据统计服务接口
 */
public interface IHealthStatisticsService {

    /**
     * 生成每日统计
     */
    HealthStatistics generateDailyStatistics(Long userId, LocalDate date);

    /**
     * 生成周统计
     */
    HealthStatistics generateWeeklyStatistics(Long userId, LocalDate weekStart);

    /**
     * 生成月统计
     */
    HealthStatistics generateMonthlyStatistics(Long userId, LocalDate month);

    /**
     * 获取健康数据大屏数据
     */
    Map<String, Object> getHealthDashboard(Long userId);

    /**
     * 获取趋势数据
     */
    Map<String, Object> getTrendData(Long userId, String indicatorType, String period);

    /**
     * 获取对比数据
     */
    Map<String, Object> getComparisonData(Long userId);

    /**
     * 生成健康分析报告
     */
    Map<String, Object> generateHealthReport(Long userId, String reportType);
}

