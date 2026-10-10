package com.lingshu.zhikang.service.impl;

import com.lingshu.zhikang.entity.po.ChronicDiseaseRecord;
import com.lingshu.zhikang.entity.po.HealthDiary;
import com.lingshu.zhikang.entity.po.HealthStatistics;
import com.lingshu.zhikang.entity.po.DiagnosisReport;
import com.lingshu.zhikang.mapper.ChronicDiseaseMapper;
import com.lingshu.zhikang.mapper.DiagnosisReportMapper;
import com.lingshu.zhikang.mapper.HealthDiaryMapper;
import com.lingshu.zhikang.mapper.HealthStatisticsMapper;
import com.lingshu.zhikang.service.IHealthStatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 健康数据统计服务实现
 */
@Slf4j
@Service
public class HealthStatisticsServiceImpl implements IHealthStatisticsService {

    @Autowired
    private HealthStatisticsMapper statisticsMapper;

    @Autowired
    private ChronicDiseaseMapper chronicDiseaseMapper;

    @Autowired
    private HealthDiaryMapper healthDiaryMapper;

    @Autowired
    private DiagnosisReportMapper diagnosisReportMapper;

    @Override
    public HealthStatistics generateDailyStatistics(Long userId, LocalDate date) {
        log.info("生成用户 {} 的每日统计 - {}", userId, date);

        HealthStatistics statistics = new HealthStatistics();
        statistics.setUserId(userId);
        statistics.setStatDate(date);
        statistics.setStatType("daily");

        // 获取当天的健康数据
        List<ChronicDiseaseRecord> dayRecords = chronicDiseaseMapper.getByUserIdAndDate(userId, date.toString());
        
        // 计算各项指标平均值
        calculateAverageValues(statistics, dayRecords);

        // 统计异常次数
        long abnormalCount = dayRecords.stream()
                .filter(r -> r.getIsAbnormal() != null && r.getIsAbnormal())
                .count();
        statistics.setAbnormalCount((int) abnormalCount);

        // 计算健康评分
        statistics.setHealthScore(calculateHealthScore(dayRecords));

        // 保存统计数据
        statisticsMapper.saveOrUpdateStatistics(statistics);

        return statistics;
    }

    @Override
    public HealthStatistics generateWeeklyStatistics(Long userId, LocalDate weekStart) {
        log.info("生成用户 {} 的周统计 - {}", userId, weekStart);

        LocalDate weekEnd = weekStart.plusDays(6);

        HealthStatistics statistics = new HealthStatistics();
        statistics.setUserId(userId);
        statistics.setStatDate(weekStart);
        statistics.setStatType("weekly");

        // 获取一周的数据
        List<ChronicDiseaseRecord> weekRecords = chronicDiseaseMapper.getByUserIdAndDateRange(
                userId, weekStart.toString(), weekEnd.toString());

        // 计算平均值
        calculateAverageValues(statistics, weekRecords);

        // 统计异常次数
        long abnormalCount = weekRecords.stream()
                .filter(r -> r.getIsAbnormal() != null && r.getIsAbnormal())
                .count();
        statistics.setAbnormalCount((int) abnormalCount);

        // 计算健康评分
        statistics.setHealthScore(calculateHealthScore(weekRecords));

        statisticsMapper.saveOrUpdateStatistics(statistics);

        return statistics;
    }

    @Override
    public HealthStatistics generateMonthlyStatistics(Long userId, LocalDate month) {
        log.info("生成用户 {} 的月统计 - {}", userId, month);

        LocalDate monthStart = month.withDayOfMonth(1);
        LocalDate monthEnd = month.withDayOfMonth(month.lengthOfMonth());

        HealthStatistics statistics = new HealthStatistics();
        statistics.setUserId(userId);
        statistics.setStatDate(monthStart);
        statistics.setStatType("monthly");

        // 获取一个月的数据
        List<ChronicDiseaseRecord> monthRecords = chronicDiseaseMapper.getByUserIdAndDateRange(
                userId, monthStart.toString(), monthEnd.toString());

        // 计算平均值
        calculateAverageValues(statistics, monthRecords);

        // 统计异常次数
        long abnormalCount = monthRecords.stream()
                .filter(r -> r.getIsAbnormal() != null && r.getIsAbnormal())
                .count();
        statistics.setAbnormalCount((int) abnormalCount);

        // 计算健康评分
        statistics.setHealthScore(calculateHealthScore(monthRecords));

        statisticsMapper.saveOrUpdateStatistics(statistics);

        return statistics;
    }

    @Override
    public Map<String, Object> getHealthDashboard(Long userId) {
        log.info("获取用户 {} 的健康数据大屏", userId);

        Map<String, Object> dashboard = new HashMap<>();

        // 获取最新统计数据
        HealthStatistics latestDaily = statisticsMapper.getLatestStatistics(userId, "daily");

        // 健康评分和等级
        int healthScore = latestDaily != null ? latestDaily.getHealthScore() : 85;
        dashboard.put("healthScore", healthScore);
        dashboard.put("healthLevel", getHealthLevel(healthScore));

        // 获取近30天趋势
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(30);
        List<HealthStatistics> monthlyData = statisticsMapper.getStatisticsByDateRange(
                userId, "daily", startDate, endDate);

        dashboard.put("trends", buildTrendsSummary(monthlyData));

        // 生命体征数据
        dashboard.put("vitalSigns", getVitalSigns(userId));

        // 近期数据概览
        List<ChronicDiseaseRecord> recentRecords = chronicDiseaseMapper.getRecentRecords(userId, 7);
        dashboard.put("recentData", buildRecentDataSummary(recentRecords));

        // 统计数据
        dashboard.put("statistics", buildStatisticsSummary(monthlyData));
        dashboard.put("dailyStatistics", monthlyData);

        // 返回大屏渲染所需的原始记录，前端仍会与本地数据合并。
        List<ChronicDiseaseRecord> chronicRecords = chronicDiseaseMapper.selectByUserId(userId);
        List<HealthDiary> diaries = healthDiaryMapper.selectByUserId(userId);
        List<DiagnosisReport> reports = diagnosisReportMapper.getReportsByUserId(userId);
        dashboard.put("chronicRecords", chronicRecords);
        dashboard.put("diaries", diaries);
        dashboard.put("symptomRecords", reports);
        dashboard.put("reports", reports);

        return dashboard;
    }

    @Override
    public Map<String, Object> getTrendData(Long userId, String indicatorType, String period) {
        Map<String, Object> trendData = new HashMap<>();

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (period) {
            case "week":
                startDate = endDate.minusDays(7);
                break;
            case "month":
                startDate = endDate.minusDays(30);
                break;
            case "quarter":
                startDate = endDate.minusDays(90);
                break;
            case "year":
                startDate = endDate.minusDays(365);
                break;
            default:
                startDate = endDate.minusDays(30);
        }

        // 获取指定时间范围的数据
        List<ChronicDiseaseRecord> records = chronicDiseaseMapper.getByUserIdAndDateRange(
                userId, startDate.toString(), endDate.toString());

        // 按类型筛选
        List<ChronicDiseaseRecord> filteredRecords = records.stream()
                .filter(r -> r.getType().equals(indicatorType))
                .collect(Collectors.toList());

        // 构建趋势数据
        List<Map<String, Object>> dataPoints = new ArrayList<>();
        for (ChronicDiseaseRecord record : filteredRecords) {
            Map<String, Object> point = new HashMap<>();
            point.put("date", record.getDate());
            point.put("value", getRecordValue(record));
            point.put("isAbnormal", record.getIsAbnormal());
            dataPoints.add(point);
        }

        trendData.put("indicatorType", indicatorType);
        trendData.put("period", period);
        trendData.put("data", dataPoints);
        trendData.put("trendDirection", analyzeTrendDirection(dataPoints));

        return trendData;
    }

    @Override
    public Map<String, Object> getComparisonData(Long userId) {
        Map<String, Object> comparison = new HashMap<>();

        // 本周 vs 上周
        LocalDate thisWeekStart = LocalDate.now().minusDays(LocalDate.now().getDayOfWeek().getValue() - 1);
        LocalDate lastWeekStart = thisWeekStart.minusDays(7);

        HealthStatistics thisWeek = statisticsMapper.getStatistics(userId, thisWeekStart, "weekly");
        HealthStatistics lastWeek = statisticsMapper.getStatistics(userId, lastWeekStart, "weekly");

        if (thisWeek != null && lastWeek != null) {
            comparison.put("weekComparison", compareStatistics(thisWeek, lastWeek));
        }

        // 本月 vs 上月
        LocalDate thisMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate lastMonth = thisMonth.minusMonths(1);

        HealthStatistics thisMonthStat = statisticsMapper.getStatistics(userId, thisMonth, "monthly");
        HealthStatistics lastMonthStat = statisticsMapper.getStatistics(userId, lastMonth, "monthly");

        if (thisMonthStat != null && lastMonthStat != null) {
            comparison.put("monthComparison", compareStatistics(thisMonthStat, lastMonthStat));
        }

        return comparison;
    }

    @Override
    public Map<String, Object> generateHealthReport(Long userId, String reportType) {
        log.info("生成用户 {} 的健康报告 - 类型: {}", userId, reportType);

        Map<String, Object> report = new HashMap<>();
        report.put("reportType", reportType);
        report.put("generateDate", LocalDate.now());

        LocalDate endDate = LocalDate.now();
        LocalDate startDate;

        switch (reportType) {
            case "weekly":
                startDate = endDate.minusDays(7);
                break;
            case "monthly":
                startDate = endDate.minusDays(30);
                break;
            case "quarterly":
                startDate = endDate.minusDays(90);
                break;
            case "yearly":
                startDate = endDate.minusDays(365);
                break;
            default:
                startDate = endDate.minusDays(7);
        }

        // 获取统计数据
        List<HealthStatistics> statistics = statisticsMapper.getStatisticsByDateRange(
                userId, "daily", startDate, endDate);

        // 计算平均健康评分
        double avgHealthScore = statistics.stream()
                .mapToInt(HealthStatistics::getHealthScore)
                .average()
                .orElse(85.0);

        report.put("healthScore", (int) avgHealthScore);
        report.put("healthLevel", getHealthLevel((int) avgHealthScore));

        // 关键发现
        report.put("keyFindings", generateKeyFindings(statistics));

        // 改善建议
        report.put("improvementAreas", generateImprovementAreas(statistics));

        // 进步成就
        report.put("achievements", generateAchievements(statistics));

        // AI建议
        report.put("aiRecommendations", generateAIRecommendations(statistics));

        return report;
    }

    /**
     * 计算指标平均值
     */
    private void calculateAverageValues(HealthStatistics statistics, List<ChronicDiseaseRecord> records) {
        if (records == null || records.isEmpty()) {
            return;
        }

        // 血压平均
        List<ChronicDiseaseRecord> bpRecords = records.stream()
                .filter(r -> "blood_pressure".equals(r.getType()) && r.getSystolic() != null && r.getDiastolic() != null)
                .collect(Collectors.toList());
        if (!bpRecords.isEmpty()) {
            int avgSystolic = (int) bpRecords.stream().mapToInt(ChronicDiseaseRecord::getSystolic).average().orElse(0);
            int avgDiastolic = (int) bpRecords.stream().mapToInt(ChronicDiseaseRecord::getDiastolic).average().orElse(0);
            statistics.setBloodPressureAvg(avgSystolic + "/" + avgDiastolic);
        }

        // 血糖平均
        double avgBloodSugar = records.stream()
                .filter(r -> "blood_sugar".equals(r.getType()) && r.getValue() != null)
                .mapToDouble(ChronicDiseaseRecord::getValue)
                .average()
                .orElse(0);
        if (avgBloodSugar > 0) {
            statistics.setBloodSugarAvg(avgBloodSugar);
        }

        // 体重平均
        double avgWeight = records.stream()
                .filter(r -> "weight".equals(r.getType()) && r.getValue() != null)
                .mapToDouble(ChronicDiseaseRecord::getValue)
                .average()
                .orElse(0);
        if (avgWeight > 0) {
            statistics.setWeightAvg(avgWeight);
        }

        // 心率平均
        double avgHeartRate = records.stream()
                .filter(r -> "heart_rate".equals(r.getType()) && r.getValue() != null)
                .mapToDouble(ChronicDiseaseRecord::getValue)
                .average()
                .orElse(0);
        if (avgHeartRate > 0) {
            statistics.setHeartRateAvg((int) avgHeartRate);
        }
    }

    /**
     * 计算健康评分
     */
    private Integer calculateHealthScore(List<ChronicDiseaseRecord> records) {
        if (records == null || records.isEmpty()) {
            return 85;
        }

        int score = 100;

        // 根据异常记录扣分
        long abnormalCount = records.stream()
                .filter(r -> r.getIsAbnormal() != null && r.getIsAbnormal())
                .count();

        score -= (int) (abnormalCount * 5);

        return Math.max(0, Math.min(100, score));
    }

    /**
     * 获取健康等级
     */
    private String getHealthLevel(int score) {
        if (score >= 90) return "优秀";
        if (score >= 75) return "良好";
        if (score >= 60) return "一般";
        return "较差";
    }

    /**
     * 构建趋势摘要
     */
    private Map<String, Object> buildTrendsSummary(List<HealthStatistics> statistics) {
        Map<String, Object> trends = new HashMap<>();

        if (statistics == null || statistics.isEmpty()) {
            return trends;
        }

        // 分析各指标趋势
        trends.put("bloodPressure", analyzeTrend(statistics, "bloodPressure"));
        trends.put("bloodSugar", analyzeTrend(statistics, "bloodSugar"));
        trends.put("weight", analyzeTrend(statistics, "weight"));
        trends.put("sleep", analyzeTrend(statistics, "sleep"));

        return trends;
    }

    /**
     * 分析单个指标趋势
     */
    private Map<String, Object> analyzeTrend(List<HealthStatistics> statistics, String indicator) {
        Map<String, Object> trend = new HashMap<>();
        trend.put("indicator", indicator);
        trend.put("direction", "稳定");
        trend.put("status", "正常");
        return trend;
    }

    /**
     * 获取生命体征
     */
    private Map<String, Object> getVitalSigns(Long userId) {
        Map<String, Object> vitalSigns = new HashMap<>();

        List<ChronicDiseaseRecord> recent = chronicDiseaseMapper.getRecentRecords(userId, 1);

        for (ChronicDiseaseRecord record : recent) {
            Map<String, Object> sign = new HashMap<>();
            sign.put("value", getRecordValue(record));
            sign.put("isNormal", !record.getIsAbnormal());
            sign.put("time", record.getTime());

            vitalSigns.put(record.getType(), sign);
        }

        return vitalSigns;
    }

    /**
     * 构建近期数据摘要
     */
    private List<Map<String, Object>> buildRecentDataSummary(List<ChronicDiseaseRecord> records) {
        List<Map<String, Object>> summary = new ArrayList<>();

        for (ChronicDiseaseRecord record : records) {
            Map<String, Object> item = new HashMap<>();
            item.put("type", record.getType());
            item.put("value", getRecordValue(record));
            item.put("date", record.getDate());
            item.put("isAbnormal", record.getIsAbnormal());
            summary.add(item);
        }

        return summary;
    }

    /**
     * 构建统计摘要
     */
    private Map<String, Object> buildStatisticsSummary(List<HealthStatistics> statistics) {
        Map<String, Object> summary = new HashMap<>();

        if (statistics == null || statistics.isEmpty()) {
            return summary;
        }

        // 计算平均健康评分
        double avgScore = statistics.stream()
                .mapToInt(HealthStatistics::getHealthScore)
                .average()
                .orElse(85.0);

        summary.put("avgHealthScore", (int) avgScore);

        // 统计总异常次数
        int totalAbnormal = statistics.stream()
                .mapToInt(s -> s.getAbnormalCount() != null ? s.getAbnormalCount() : 0)
                .sum();

        summary.put("totalAbnormalCount", totalAbnormal);

        return summary;
    }

    /**
     * 获取记录值
     */
    private String getRecordValue(ChronicDiseaseRecord record) {
        if ("blood_pressure".equals(record.getType())) {
            return record.getSystolic() + "/" + record.getDiastolic();
        } else {
            return String.valueOf(record.getValue());
        }
    }

    /**
     * 分析趋势方向
     */
    private String analyzeTrendDirection(List<Map<String, Object>> dataPoints) {
        if (dataPoints == null || dataPoints.size() < 2) {
            return "数据不足";
        }

        // 简化：比较第一个和最后一个值
        Object firstValue = dataPoints.get(0).get("value");
        Object lastValue = dataPoints.get(dataPoints.size() - 1).get("value");

        if (firstValue instanceof Number && lastValue instanceof Number) {
            double diff = ((Number) lastValue).doubleValue() - ((Number) firstValue).doubleValue();
            if (Math.abs(diff) < 0.1) {
                return "平稳";
            } else if (diff > 0) {
                return "上升";
            } else {
                return "下降";
            }
        }

        return "稳定";
    }

    /**
     * 对比统计数据
     */
    private Map<String, Object> compareStatistics(HealthStatistics current, HealthStatistics previous) {
        Map<String, Object> comparison = new HashMap<>();

        int scoreDiff = current.getHealthScore() - previous.getHealthScore();
        comparison.put("scoreDiff", scoreDiff);
        comparison.put("scoreImproved", scoreDiff > 0);

        return comparison;
    }

    /**
     * 生成关键发现
     */
    private List<String> generateKeyFindings(List<HealthStatistics> statistics) {
        List<String> findings = new ArrayList<>();
        findings.add("整体健康状况保持稳定");
        findings.add("各项指标在正常范围内");
        return findings;
    }

    /**
     * 生成改善建议
     */
    private List<String> generateImprovementAreas(List<HealthStatistics> statistics) {
        List<String> areas = new ArrayList<>();
        areas.add("建议增加运动时长");
        areas.add("注意控制饮食");
        return areas;
    }

    /**
     * 生成成就
     */
    private List<String> generateAchievements(List<HealthStatistics> statistics) {
        List<String> achievements = new ArrayList<>();
        achievements.add("坚持记录健康数据");
        achievements.add("健康评分保持良好");
        return achievements;
    }

    /**
     * 生成AI建议
     */
    private List<String> generateAIRecommendations(List<HealthStatistics> statistics) {
        List<String> recommendations = new ArrayList<>();
        recommendations.add("保持良好的生活习惯");
        recommendations.add("继续坚持健康监测");
        recommendations.add("适当增加户外活动");
        return recommendations;
    }
}

