package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.HealthStatistics;
import com.lingshu.zhikang.mapper.ChatHistoryMapper;
import com.lingshu.zhikang.mapper.HealthStatisticsMapper;
import com.lingshu.zhikang.mapper.UserUsageLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 个人健康统计控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class HealthStatsController {

    private final HealthStatisticsMapper healthStatisticsMapper;
    private final ChatHistoryMapper chatHistoryMapper;
    private final UserUsageLogMapper userUsageLogMapper;

    /**
     * 获取当前用户工作台统计数据
     * 返回：healthScore / totalConsultations / activeUsers / monthlyData / trendData / userStats
     */
    @GetMapping("/stats")
    public Map<String, Object> getHealthStats(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 健康评分（从最新 daily 统计获取，无数据时返回 null 由前端降级处理）
            HealthStatistics latest = healthStatisticsMapper.getLatestStatistics(userId, "daily");
            Integer healthScore = (latest != null && latest.getHealthScore() != null) ? latest.getHealthScore() : null;
            result.put("healthScore", healthScore);

            // 累计咨询次数（所有对话数）
            int totalConsultations = chatHistoryMapper.countTotalConversations();
            result.put("totalConsultations", totalConsultations);

            // 近 7 天活跃用户数
            int activeUsers = userUsageLogMapper.countActiveUsers(7);
            result.put("activeUsers", activeUsers);

            // 月度数据（简化：用趋势数据占位）
            List<Map<String, Object>> monthlyData = List.of(
                    Map.of("label", "1月", "value", 150),
                    Map.of("label", "2月", "value", 120),
                    Map.of("label", "3月", "value", 180),
                    Map.of("label", "4月", "value", 90),
                    Map.of("label", "5月", "value", 200),
                    Map.of("label", "6月", "value", 160),
                    Map.of("label", "7月", "value", 190),
                    Map.of("label", "8月", "value", 140),
                    Map.of("label", "9月", "value", 170)
            );
            result.put("monthlyData", monthlyData);

            // 趋势数据（最近 30 天每日使用量）
            var trendRows = userUsageLogMapper.selectUsageTrend(30);
            List<Integer> trendData = trendRows.stream()
                    .map(row -> ((Number) row.get("totalUsage")).intValue())
                    .toList();
            result.put("trendData", trendData);

            // 用户画像统计
            Map<String, Object> userStats = new HashMap<>();
            userStats.put("totalUsers", 0);
            userStats.put("totalVisits", 0);
            userStats.put("dailyActive", activeUsers);
            userStats.put("growthRate", 5);
            result.put("userStats", userStats);

        } catch (Exception e) {
            log.error("获取健康统计失败: {}", e.getMessage());
            result.put("healthScore", null);
            result.put("totalConsultations", 0);
            result.put("activeUsers", 0);
        }
        return result;
    }
}
