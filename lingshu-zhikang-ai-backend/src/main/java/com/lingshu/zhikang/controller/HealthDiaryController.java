package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.HealthDiary;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.service.IHealthDiaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 健康日记控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/health-diary")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class HealthDiaryController {
    
    private final IHealthDiaryService healthDiaryService;
    
    /**
     * 添加健康日记
     */
    @PostMapping("/add")
    public ResponseResult<HealthDiary> addDiary(@RequestBody HealthDiary diary) {
        try {
            log.info("添加健康日记: userId={}, date={}", diary.getUserId(), diary.getDate());
            HealthDiary result = healthDiaryService.addDiary(diary);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("添加健康日记失败", e);
            return ResponseResult.error("添加日记失败：" + e.getMessage());
        }
    }
    
    /**
     * 更新健康日记
     */
    @PutMapping("/update")
    public ResponseResult<HealthDiary> updateDiary(@RequestBody HealthDiary diary) {
        try {
            log.info("更新健康日记: id={}, userId={}", diary.getId(), diary.getUserId());
            HealthDiary result = healthDiaryService.updateDiary(diary);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("更新健康日记失败", e);
            return ResponseResult.error("更新日记失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除健康日记
     */
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> deleteDiary(@PathVariable Long id, @RequestParam Long userId) {
        try {
            log.info("删除健康日记: id={}, userId={}", id, userId);
            healthDiaryService.deleteDiary(id, userId);
            return ResponseResult.success(null);
        } catch (Exception e) {
            log.error("删除健康日记失败", e);
            return ResponseResult.error("删除日记失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户的日记列表
     */
    @GetMapping("/list")
    public ResponseResult<List<HealthDiary>> getDiaries(@RequestParam Long userId) {
        try {
            log.info("查询健康日记列表: userId={}", userId);
            List<HealthDiary> diaries = healthDiaryService.getDiariesByUserId(userId);
            return ResponseResult.success(diaries);
        } catch (Exception e) {
            log.error("查询健康日记列表失败", e);
            return ResponseResult.error("查询日记列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 根据日期获取日记
     */
    @GetMapping("/get")
    public ResponseResult<HealthDiary> getDiaryByDate(@RequestParam Long userId, @RequestParam String date) {
        try {
            log.info("查询指定日期的日记: userId={}, date={}", userId, date);
            HealthDiary diary = healthDiaryService.getDiaryByDate(userId, date);
            return ResponseResult.success(diary);
        } catch (Exception e) {
            log.error("查询指定日期的日记失败", e);
            return ResponseResult.error("查询日记失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseResult<Map<String, Object>> getStatistics(@RequestParam Long userId) {
        try {
            log.info("获取统计信息: userId={}", userId);
            Map<String, Object> stats = healthDiaryService.getStatistics(userId);
            return ResponseResult.success(stats);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return ResponseResult.error("获取统计信息失败：" + e.getMessage());
        }
    }

    /**
     * 智能分析健康日志
     */
    @PostMapping("/analyze")
    public ResponseResult<Map<String, Object>> analyzeLog(@RequestBody Map<String, Object> request) {
        try {
            Long userId = Long.valueOf(request.get("userId").toString());
            String text = (String) request.get("text");
            log.info("智能分析日志: userId={}", userId);
            Map<String, Object> result = healthDiaryService.analyzeAndSaveLog(userId, text);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("智能分析失败", e);
            return ResponseResult.error("智能分析失败：" + e.getMessage());
        }
    }
}

