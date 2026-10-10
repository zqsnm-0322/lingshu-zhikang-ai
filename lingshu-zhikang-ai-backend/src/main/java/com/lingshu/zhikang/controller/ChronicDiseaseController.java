package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.ChronicDiseaseRecord;
import com.lingshu.zhikang.entity.vo.ResponseResult;
import com.lingshu.zhikang.service.IChronicDiseaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 慢性病管理控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/chronic-disease")
@RequiredArgsConstructor
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class ChronicDiseaseController {
    
    private final IChronicDiseaseService chronicDiseaseService;
    
    /**
     * 添加慢性病记录
     */
    @PostMapping("/add")
    public ResponseResult<ChronicDiseaseRecord> addRecord(@RequestBody ChronicDiseaseRecord record) {
        try {
            log.info("添加慢性病记录: userId={}, type={}", record.getUserId(), record.getType());
            ChronicDiseaseRecord result = chronicDiseaseService.addRecord(record);
            return ResponseResult.success(result);
        } catch (Exception e) {
            log.error("添加慢性病记录失败", e);
            return ResponseResult.error("添加记录失败：" + e.getMessage());
        }
    }

    @PutMapping("/update")
    public ResponseResult<ChronicDiseaseRecord> updateRecord(@RequestBody ChronicDiseaseRecord record) {
        try {
            return ResponseResult.success(chronicDiseaseService.updateRecord(record));
        } catch (Exception e) {
            log.error("更新慢性病记录失败", e);
            return ResponseResult.error("更新记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 删除慢性病记录
     */
    @DeleteMapping("/delete/{id}")
    public ResponseResult<Void> deleteRecord(@PathVariable Long id, @RequestParam Long userId) {
        try {
            log.info("删除慢性病记录: id={}, userId={}", id, userId);
            chronicDiseaseService.deleteRecord(id, userId);
            return ResponseResult.success(null);
        } catch (Exception e) {
            log.error("删除慢性病记录失败", e);
            return ResponseResult.error("删除记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户某个类型的记录列表
     */
    @GetMapping("/list")
    public ResponseResult<List<ChronicDiseaseRecord>> getRecordsByType(
            @RequestParam Long userId, 
            @RequestParam String type) {
        try {
            log.info("查询慢性病记录: userId={}, type={}", userId, type);
            List<ChronicDiseaseRecord> records = chronicDiseaseService.getRecordsByType(userId, type);
            return ResponseResult.success(records);
        } catch (Exception e) {
            log.error("查询慢性病记录失败", e);
            return ResponseResult.error("查询记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取用户所有记录
     */
    @GetMapping("/all")
    public ResponseResult<List<ChronicDiseaseRecord>> getAllRecords(@RequestParam Long userId) {
        try {
            log.info("查询所有慢性病记录: userId={}", userId);
            List<ChronicDiseaseRecord> records = chronicDiseaseService.getAllRecords(userId);
            return ResponseResult.success(records);
        } catch (Exception e) {
            log.error("查询所有慢性病记录失败", e);
            return ResponseResult.error("查询记录失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取统计信息
     */
    @GetMapping("/statistics")
    public ResponseResult<Map<String, Object>> getStatistics(
            @RequestParam Long userId, 
            @RequestParam String type) {
        try {
            log.info("获取统计信息: userId={}, type={}", userId, type);
            Map<String, Object> stats = chronicDiseaseService.getStatistics(userId, type);
            return ResponseResult.success(stats);
        } catch (Exception e) {
            log.error("获取统计信息失败", e);
            return ResponseResult.error("获取统计信息失败：" + e.getMessage());
        }
    }
    
    /**
     * 导出数据
     */
    @GetMapping("/export")
    public ResponseResult<List<ChronicDiseaseRecord>> exportData(
            @RequestParam Long userId, 
            @RequestParam String type) {
        try {
            log.info("导出数据: userId={}, type={}", userId, type);
            List<ChronicDiseaseRecord> data = chronicDiseaseService.exportData(userId, type);
            return ResponseResult.success(data);
        } catch (Exception e) {
            log.error("导出数据失败", e);
            return ResponseResult.error("导出数据失败：" + e.getMessage());
        }
    }

    /**
     * 分析健康趋势
     */
    @GetMapping("/analyze")
    public ResponseResult<Map<String, Object>> analyzeHealthTrend(
            @RequestParam Long userId, 
            @RequestParam String type) {
        try {
            log.info("分析健康趋势: userId={}, type={}", userId, type);
            Map<String, Object> analysis = chronicDiseaseService.analyzeHealthTrend(userId, type);
            return ResponseResult.success(analysis);
        } catch (Exception e) {
            log.error("分析健康趋势失败", e);
            return ResponseResult.error("分析健康趋势失败：" + e.getMessage());
        }
    }
}

