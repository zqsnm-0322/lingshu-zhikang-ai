package com.lingshu.zhikang.mapper;

import com.lingshu.zhikang.entity.po.HealthButlerConversation;
import com.lingshu.zhikang.entity.po.HealthButlerReport;
import com.lingshu.zhikang.entity.po.HealthPlan;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;

/**
 * AI健康管家Mapper
 */
@Mapper
public interface HealthButlerMapper {

    /**
     * 创建或更新健康晨报
     */
    @Insert("INSERT INTO health_butler_report (user_id, report_date, sleep_quality, sleep_duration, " +
            "medication_count, health_tasks, diet_suggestions, exercise_suggestions, health_score, " +
            "health_level, ai_suggestions) VALUES (#{userId}, #{reportDate}, #{sleepQuality}, " +
            "#{sleepDuration}, #{medicationCount}, #{healthTasks}, #{dietSuggestions}, " +
            "#{exerciseSuggestions}, #{healthScore}, #{healthLevel}, #{aiSuggestions}) " +
            "ON DUPLICATE KEY UPDATE sleep_quality=#{sleepQuality}, sleep_duration=#{sleepDuration}, " +
            "medication_count=#{medicationCount}, health_tasks=#{healthTasks}, " +
            "diet_suggestions=#{dietSuggestions}, exercise_suggestions=#{exerciseSuggestions}, " +
            "health_score=#{healthScore}, health_level=#{healthLevel}, ai_suggestions=#{aiSuggestions}")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int saveOrUpdateReport(HealthButlerReport report);

    /**
     * 获取用户最新晨报
     */
    @Select("SELECT * FROM health_butler_report WHERE user_id = #{userId} ORDER BY report_date DESC LIMIT 1")
    HealthButlerReport getLatestReport(@Param("userId") Long userId);

    /**
     * 获取用户指定日期的晨报
     */
    @Select("SELECT * FROM health_butler_report WHERE user_id = #{userId} AND report_date = #{reportDate}")
    HealthButlerReport getReportByDate(@Param("userId") Long userId, @Param("reportDate") LocalDate reportDate);

    /**
     * 获取用户历史晨报
     */
    @Select("SELECT * FROM health_butler_report WHERE user_id = #{userId} ORDER BY report_date DESC LIMIT #{limit}")
    List<HealthButlerReport> getReportHistory(@Param("userId") Long userId, @Param("limit") int limit);

    /**
     * 保存对话记录
     */
    @Insert("INSERT INTO health_butler_conversation (user_id, conversation_id, message_type, " +
            "message_content, emotion, intent) VALUES (#{userId}, #{conversationId}, #{messageType}, " +
            "#{messageContent}, #{emotion}, #{intent})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int saveConversation(HealthButlerConversation conversation);

    /**
     * 获取对话历史
     */
    @Select("SELECT * FROM health_butler_conversation WHERE conversation_id = #{conversationId} " +
            "ORDER BY created_time ASC")
    List<HealthButlerConversation> getConversationHistory(@Param("conversationId") String conversationId);

    /**
     * 创建健康计划
     */
    @Insert("INSERT INTO health_plan (user_id, plan_name, plan_type, start_date, end_date, " +
            "target_goal, daily_tasks, progress, status) VALUES (#{userId}, #{planName}, " +
            "#{planType}, #{startDate}, #{endDate}, #{targetGoal}, #{dailyTasks}, #{progress}, #{status})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int createPlan(HealthPlan plan);

    /**
     * 更新健康计划
     */
    @Update("UPDATE health_plan SET plan_name=#{planName}, plan_type=#{planType}, " +
            "start_date=#{startDate}, end_date=#{endDate}, target_goal=#{targetGoal}, " +
            "daily_tasks=#{dailyTasks}, progress=#{progress}, status=#{status} WHERE id=#{id}")
    int updatePlan(HealthPlan plan);

    /**
     * 获取用户的健康计划列表
     */
    @Select("SELECT * FROM health_plan WHERE user_id = #{userId} AND status != '已取消' " +
            "ORDER BY created_time DESC")
    List<HealthPlan> getUserPlans(@Param("userId") Long userId);

    /**
     * 获取健康计划详情
     */
    @Select("SELECT * FROM health_plan WHERE id = #{id}")
    HealthPlan getPlanById(@Param("id") Long id);

    /**
     * 更新计划进度
     */
    @Update("UPDATE health_plan SET progress = #{progress} WHERE id = #{id}")
    int updatePlanProgress(@Param("id") Long id, @Param("progress") Integer progress);

    /**
     * 删除健康计划
     */
    @Delete("DELETE FROM health_plan WHERE id = #{id}")
    int deletePlan(@Param("id") Long id);
}

