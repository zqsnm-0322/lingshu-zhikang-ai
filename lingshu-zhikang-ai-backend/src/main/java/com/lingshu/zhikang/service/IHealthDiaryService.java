package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.HealthDiary;

import java.util.List;
import java.util.Map;

/**
 * 健康日记服务接口
 */
public interface IHealthDiaryService {
    
    /**
     * 添加健康日记
     */
    HealthDiary addDiary(HealthDiary diary);
    
    /**
     * 更新健康日记
     */
    HealthDiary updateDiary(HealthDiary diary);
    
    /**
     * 删除健康日记
     */
    void deleteDiary(Long id, Long userId);
    
    /**
     * 获取用户的日记列表
     */
    List<HealthDiary> getDiariesByUserId(Long userId);
    
    /**
     * 根据日期获取日记
     */
    HealthDiary getDiaryByDate(Long userId, String date);
    
    /**
     * 获取统计信息
     */
    Map<String, Object> getStatistics(Long userId);

    /**
     * 智能分析健康日志（语音转文字后的文本）
     * @param userId 用户ID
     * @param text 日志文本内容
     * @return 分析处理结果
     */
    Map<String, Object> analyzeAndSaveLog(Long userId, String text);
}

