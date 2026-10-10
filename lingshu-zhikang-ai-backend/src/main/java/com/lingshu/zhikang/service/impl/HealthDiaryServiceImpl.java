package com.lingshu.zhikang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingshu.zhikang.entity.po.HealthDiary;
import com.lingshu.zhikang.mapper.HealthDiaryMapper;
import com.lingshu.zhikang.service.IHealthDiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 健康日记服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class HealthDiaryServiceImpl implements IHealthDiaryService {
    
    private final HealthDiaryMapper healthDiaryMapper;
    
    @Override
    public HealthDiary addDiary(HealthDiary diary) {
        log.info("添加健康日记: userId={}, date={}", diary.getUserId(), diary.getDate());
        
        // 检查当天是否已有日记
        HealthDiary existing = healthDiaryMapper.selectByUserIdAndDate(diary.getUserId(), diary.getDate());
        if (existing != null) {
            throw new RuntimeException("该日期已有日记记录，请使用更新功能");
        }
        
        diary.setCreateTime(LocalDateTime.now());
        diary.setUpdateTime(LocalDateTime.now());
        
        healthDiaryMapper.insert(diary);
        return diary;
    }
    
    @Override
    public HealthDiary updateDiary(HealthDiary diary) {
        log.info("更新健康日记: id={}, userId={}", diary.getId(), diary.getUserId());
        
        // 验证日记是否存在且属于当前用户
        LambdaQueryWrapper<HealthDiary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthDiary::getId, diary.getId())
                   .eq(HealthDiary::getUserId, diary.getUserId());
        
        HealthDiary existing = healthDiaryMapper.selectOne(queryWrapper);
        if (existing == null) {
            throw new RuntimeException("日记不存在或无权限操作");
        }
        
        diary.setUpdateTime(LocalDateTime.now());
        healthDiaryMapper.updateById(diary);
        return diary;
    }
    
    @Override
    public void deleteDiary(Long id, Long userId) {
        log.info("删除健康日记: id={}, userId={}", id, userId);
        
        LambdaQueryWrapper<HealthDiary> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(HealthDiary::getId, id)
                   .eq(HealthDiary::getUserId, userId);
        
        healthDiaryMapper.delete(queryWrapper);
    }
    
    @Override
    public List<HealthDiary> getDiariesByUserId(Long userId) {
        log.info("查询健康日记列表: userId={}", userId);
        return healthDiaryMapper.selectByUserId(userId);
    }
    
    @Override
    public HealthDiary getDiaryByDate(Long userId, String date) {
        log.info("查询指定日期的日记: userId={}, date={}", userId, date);
        return healthDiaryMapper.selectByUserIdAndDate(userId, date);
    }
    
    private final org.springframework.ai.chat.client.ChatClient chatClient;

    @Override
    public Map<String, Object> analyzeAndSaveLog(Long userId, String text) {
        log.info("智能分析健康日志: userId={}, text={}", userId, text);
        
        Map<String, Object> result = new HashMap<>();
        result.put("originalText", text);
        
        try {
            // 1. 调用AI分析文本
            String prompt = """
                请分析以下用户的健康日志文本，并提取关键信息。
                请以JSON格式返回，包含以下字段：
                - symptoms: 症状列表（字符串数组）
                - foods: 饮食内容（字符串数组）
                - mood: 心情（优秀/良好/一般/差）
                - suggestion: 简短的健康建议（50字以内）
                
                用户文本：
                %s
                """.formatted(text);
                
            String aiResponse = chatClient.prompt().user(prompt).call().content();
            
            // 2. 解析AI返回的JSON（这里简化处理，实际应使用JSON库解析）
            // 假设AI返回的是合法的JSON字符串
            // 这里为了演示，我们直接把AI的原始回复返回，前端可以展示或进一步处理
            // 在实际生产环境中，应该解析JSON并保存到数据库
            
            result.put("aiAnalysis", aiResponse);
            result.put("success", true);
            
            // TODO: 解析JSON并自动保存到HealthDiary表和DietRecord表
            // saveToDiary(userId, aiResponse);
            
        } catch (Exception e) {
            log.error("AI分析失败", e);
            result.put("success", false);
            result.put("error", "AI分析服务暂时不可用：" + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getStatistics(Long userId) {
        log.info("获取统计信息: userId={}", userId);
        
        Map<String, Object> stats = healthDiaryMapper.selectStatsByUserId(userId);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalDays", 0L);
            stats.put("avgMood", 0.0);
            stats.put("avgSleep", 0.0);
        }
        
        // 计算连续记录天数
        List<HealthDiary> diaries = healthDiaryMapper.selectByUserId(userId);
        int consecutiveDays = calculateConsecutiveDays(diaries);
        stats.put("consecutiveDays", consecutiveDays);
        
        return stats;
    }
    
    /**
     * 计算连续记录天数
     */
    private int calculateConsecutiveDays(List<HealthDiary> diaries) {
        if (diaries.isEmpty()) {
            return 0;
        }
        
        // 获取所有唯一日期并排序
        Set<String> uniqueDates = new TreeSet<>(Collections.reverseOrder());
        for (HealthDiary diary : diaries) {
            uniqueDates.add(diary.getDate());
        }
        
        List<String> sortedDates = new ArrayList<>(uniqueDates);
        
        // 从今天开始计算连续天数
        int consecutive = 0;
        String today = java.time.LocalDate.now().toString();
        
        for (String date : sortedDates) {
            long daysDiff = calculateDaysDiff(date, today);
            
            if (daysDiff == consecutive) {
                consecutive++;
            } else {
                break;
            }
        }
        
        return consecutive;
    }
    
    /**
     * 计算两个日期之间的天数差
     */
    private long calculateDaysDiff(String date1, String date2) {
        try {
            java.time.LocalDate d1 = java.time.LocalDate.parse(date1);
            java.time.LocalDate d2 = java.time.LocalDate.parse(date2);
            return Math.abs(java.time.temporal.ChronoUnit.DAYS.between(d1, d2));
        } catch (Exception e) {
            log.error("日期计算失败", e);
            return Long.MAX_VALUE;
        }
    }
}

