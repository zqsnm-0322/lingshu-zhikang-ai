package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.HealthButlerConversation;
import com.lingshu.zhikang.entity.po.HealthButlerReport;
import com.lingshu.zhikang.entity.po.HealthPlan;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * AI健康管家服务接口
 */
public interface IHealthButlerService {

    /**
     * 生成每日健康晨报
     */
    HealthButlerReport generateDailyReport(Long userId);

    /**
     * 获取用户最新晨报
     */
    HealthButlerReport getLatestReport(Long userId);

    /**
     * 获取指定日期的晨报
     */
    HealthButlerReport getReportByDate(Long userId, LocalDate reportDate);

    /**
     * 获取晨报历史
     */
    List<HealthButlerReport> getReportHistory(Long userId, int limit);

    /**
     * 健康管家对话
     */
    String chat(Long userId, String conversationId, String message);

    /**
     * 获取对话历史
     */
    List<HealthButlerConversation> getConversationHistory(String conversationId);

    /**
     * 创建健康计划
     */
    HealthPlan createPlan(HealthPlan plan);

    /**
     * 更新健康计划
     */
    HealthPlan updatePlan(HealthPlan plan);

    /**
     * 获取用户的健康计划列表
     */
    List<HealthPlan> getUserPlans(Long userId);

    /**
     * 获取健康计划详情
     */
    HealthPlan getPlanById(Long id);

    /**
     * 更新计划进度
     */
    boolean updatePlanProgress(Long id, Integer progress);

    /**
     * 删除健康计划
     */
    boolean deletePlan(Long id);

    /**
     * AI分析并生成个性化建议
     */
    Map<String, Object> generatePersonalizedSuggestions(Long userId);
}

