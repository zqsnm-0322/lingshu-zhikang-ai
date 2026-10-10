package com.lingshu.zhikang.service.impl;

import com.lingshu.zhikang.entity.po.*;
import com.lingshu.zhikang.mapper.*;
import com.lingshu.zhikang.service.IHealthButlerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * AI健康管家服务实现
 */
@Slf4j
@Service
public class HealthButlerServiceImpl implements IHealthButlerService {

    @Autowired
    private HealthButlerMapper healthButlerMapper;

    @Autowired
    private ChronicDiseaseMapper chronicDiseaseMapper;

    @Autowired
    private HealthDiaryMapper healthDiaryMapper;

    @Autowired
    private ChatClient.Builder chatClientBuilder;

    @Override
    public HealthButlerReport generateDailyReport(Long userId) {
        log.info("生成用户 {} 的每日健康晨报", userId);

        LocalDate today = LocalDate.now();
        LocalDate yesterday = today.minusDays(1);

        // 获取昨日健康日记
        HealthDiary diary = healthDiaryMapper.getByUserIdAndDate(userId, yesterday.toString());

        // 获取近期慢性病数据
        List<ChronicDiseaseRecord> recentRecords = chronicDiseaseMapper.getRecentRecords(userId, 7);

        // 统计今日用药数量（这里简化处理，实际应该查询用药提醒表）
        int medicationCount = 0;

        // 构建AI提示词
        String prompt = buildReportPrompt(diary, recentRecords);

        // 调用AI生成建议
        String aiResponse = callAI(prompt);

        // 解析AI响应并构建报告
        HealthButlerReport report = new HealthButlerReport();
        report.setUserId(userId);
        report.setReportDate(today);
        report.setSleepQuality(diary != null ? diary.getMood() * 2.0 : 7.5);
        report.setSleepDuration(diary != null ? diary.getSleep() : 7.0);
        report.setMedicationCount(medicationCount);
        report.setHealthTasks(buildHealthTasks());
        report.setDietSuggestions("建议低盐低脂饮食，多吃蔬菜水果");
        report.setExerciseSuggestions("建议每天步行30分钟，适度运动");
        report.setHealthScore(calculateHealthScore(diary, recentRecords));
        report.setHealthLevel(getHealthLevel(report.getHealthScore()));
        report.setAiSuggestions(aiResponse);

        // 保存到数据库
        healthButlerMapper.saveOrUpdateReport(report);

        return report;
    }

    @Override
    public HealthButlerReport getLatestReport(Long userId) {
        return healthButlerMapper.getLatestReport(userId);
    }

    @Override
    public HealthButlerReport getReportByDate(Long userId, LocalDate reportDate) {
        return healthButlerMapper.getReportByDate(userId, reportDate);
    }

    @Override
    public List<HealthButlerReport> getReportHistory(Long userId, int limit) {
        return healthButlerMapper.getReportHistory(userId, limit);
    }

    @Override
    public String chat(Long userId, String conversationId, String message) {
        log.info("健康管家对话 - 用户: {}, 会话: {}, 消息: {}", userId, conversationId, message);

        // 保存用户消息
        HealthButlerConversation userConv = new HealthButlerConversation();
        userConv.setUserId(userId);
        userConv.setConversationId(conversationId);
        userConv.setMessageType("USER");
        userConv.setMessageContent(message);
        healthButlerMapper.saveConversation(userConv);

        // 获取历史对话
        List<HealthButlerConversation> history = healthButlerMapper.getConversationHistory(conversationId);

        // 构建对话上下文
        StringBuilder context = new StringBuilder();
        context.append("你是一个专业的AI健康管家，负责为用户提供健康建议和指导。\n");
        context.append("请根据对话历史和用户的健康数据，给出专业、温暖、个性化的建议。\n");
        context.append("格式要求：使用数字编号（1. 2. 3.），不要使用星号(*)或其他特殊符号。\n");
        context.append("回答应该简洁明了，直接给出具体的健康指导。\n\n");
        
        for (int i = Math.max(0, history.size() - 10); i < history.size(); i++) {
            HealthButlerConversation conv = history.get(i);
            context.append(conv.getMessageType()).append(": ").append(conv.getMessageContent()).append("\n");
        }
        context.append("USER: ").append(message).append("\n");

        // 调用AI
        String aiResponse = callAI(context.toString());

        // 保存AI响应
        HealthButlerConversation assistantConv = new HealthButlerConversation();
        assistantConv.setUserId(userId);
        assistantConv.setConversationId(conversationId);
        assistantConv.setMessageType("ASSISTANT");
        assistantConv.setMessageContent(aiResponse);
        healthButlerMapper.saveConversation(assistantConv);

        return aiResponse;
    }

    @Override
    public List<HealthButlerConversation> getConversationHistory(String conversationId) {
        return healthButlerMapper.getConversationHistory(conversationId);
    }

    @Override
    public HealthPlan createPlan(HealthPlan plan) {
        plan.setProgress(0);
        plan.setStatus("进行中");
        healthButlerMapper.createPlan(plan);
        return plan;
    }

    @Override
    public HealthPlan updatePlan(HealthPlan plan) {
        healthButlerMapper.updatePlan(plan);
        return healthButlerMapper.getPlanById(plan.getId());
    }

    @Override
    public List<HealthPlan> getUserPlans(Long userId) {
        return healthButlerMapper.getUserPlans(userId);
    }

    @Override
    public HealthPlan getPlanById(Long id) {
        return healthButlerMapper.getPlanById(id);
    }

    @Override
    public boolean updatePlanProgress(Long id, Integer progress) {
        int result = healthButlerMapper.updatePlanProgress(id, progress);
        
        // 如果进度达到100%，自动更新状态为已完成
        if (progress >= 100) {
            HealthPlan plan = healthButlerMapper.getPlanById(id);
            plan.setStatus("已完成");
            healthButlerMapper.updatePlan(plan);
        }
        
        return result > 0;
    }

    @Override
    public boolean deletePlan(Long id) {
        return healthButlerMapper.deletePlan(id) > 0;
    }

    @Override
    public Map<String, Object> generatePersonalizedSuggestions(Long userId) {
        Map<String, Object> suggestions = new HashMap<>();
        
        // 获取用户最新数据
        List<ChronicDiseaseRecord> recentRecords = chronicDiseaseMapper.getRecentRecords(userId, 7);
        HealthDiary latestDiary = healthDiaryMapper.getLatestByUserId(userId);
        
        // 生成建议
        suggestions.put("diet", generateDietSuggestions(recentRecords));
        suggestions.put("exercise", generateExerciseSuggestions(latestDiary));
        suggestions.put("sleep", generateSleepSuggestions(latestDiary));
        suggestions.put("medication", generateMedicationSuggestions(recentRecords));
        
        return suggestions;
    }

    /**
     * 构建晨报提示词
     */
    private String buildReportPrompt(HealthDiary diary, List<ChronicDiseaseRecord> records) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("请根据用户的健康数据生成今日健康建议：\n");
        
        if (diary != null) {
            prompt.append("昨日睡眠: ").append(diary.getSleep()).append("小时\n");
            prompt.append("心情: ").append(diary.getMood()).append("/5\n");
            if (diary.getExercise() != null) {
                prompt.append("运动: ").append(diary.getExercise()).append("\n");
            }
        }
        
        if (records != null && !records.isEmpty()) {
            prompt.append("\n近期健康指标:\n");
            for (ChronicDiseaseRecord record : records) {
                prompt.append(record.getType()).append(": ");
                if (record.getValue() != null) {
                    prompt.append(record.getValue());
                }
                prompt.append("\n");
            }
        }
        
        prompt.append("\n请给出简洁的健康建议（200字以内）。");
        prompt.append("格式要求：使用数字编号（1. 2. 3.），不要使用星号(*)或其他特殊符号。");
        prompt.append("每条建议应该简洁明了，直接给出具体的健康指导。");
        
        return prompt.toString();
    }

    /**
     * 调用AI
     */
    private String callAI(String prompt) {
        try {
            ChatClient chatClient = chatClientBuilder.build();
            return chatClient.prompt()
                    .user(prompt)
                    .call()
                    .content();
        } catch (Exception e) {
            log.error("调用AI失败", e);
            return "健康管家暂时无法提供建议，请稍后再试。";
        }
    }

    /**
     * 构建健康任务列表
     */
    private String buildHealthTasks() {
        return "[\"测量血压\",\"记录饮食\",\"步行30分钟\"]";
    }

    /**
     * 计算健康评分
     */
    private Integer calculateHealthScore(HealthDiary diary, List<ChronicDiseaseRecord> records) {
        int score = 85; // 基础分
        
        // 根据睡眠质量调整
        if (diary != null && diary.getSleep() != null) {
            if (diary.getSleep() >= 7 && diary.getSleep() <= 9) {
                score += 5;
            } else {
                score -= 5;
            }
        }
        
        // 根据异常记录调整
        if (records != null) {
            long abnormalCount = records.stream().filter(r -> r.getIsAbnormal() != null && r.getIsAbnormal()).count();
            score -= (int) (abnormalCount * 3);
        }
        
        return Math.max(0, Math.min(100, score));
    }

    /**
     * 获取健康等级
     */
    private String getHealthLevel(Integer score) {
        if (score >= 90) return "优秀";
        if (score >= 75) return "良好";
        if (score >= 60) return "一般";
        return "较差";
    }

    /**
     * 生成饮食建议
     */
    private List<String> generateDietSuggestions(List<ChronicDiseaseRecord> records) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("保持清淡饮食，少油少盐");
        suggestions.add("多吃新鲜蔬菜水果");
        suggestions.add("适量摄入优质蛋白");
        return suggestions;
    }

    /**
     * 生成运动建议
     */
    private List<String> generateExerciseSuggestions(HealthDiary diary) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("每天步行30分钟");
        suggestions.add("适度进行有氧运动");
        suggestions.add("注意运动强度不要过大");
        return suggestions;
    }

    /**
     * 生成睡眠建议
     */
    private List<String> generateSleepSuggestions(HealthDiary diary) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("保持规律作息");
        suggestions.add("每晚23点前入睡");
        suggestions.add("睡前避免使用电子设备");
        return suggestions;
    }

    /**
     * 生成用药建议
     */
    private List<String> generateMedicationSuggestions(List<ChronicDiseaseRecord> records) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add("按时服药，不要遗漏");
        suggestions.add("注意观察药物反应");
        suggestions.add("定期复查调整用药");
        return suggestions;
    }
}

