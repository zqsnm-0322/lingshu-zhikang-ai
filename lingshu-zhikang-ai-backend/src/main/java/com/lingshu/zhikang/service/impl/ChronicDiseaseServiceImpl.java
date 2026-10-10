package com.lingshu.zhikang.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.lingshu.zhikang.entity.po.ChronicDiseaseRecord;
import com.lingshu.zhikang.mapper.ChronicDiseaseMapper;
import com.lingshu.zhikang.service.IChronicDiseaseService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 慢性病管理服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChronicDiseaseServiceImpl implements IChronicDiseaseService {
    
    private final ChronicDiseaseMapper chronicDiseaseMapper;
    
    @Override
    public ChronicDiseaseRecord addRecord(ChronicDiseaseRecord record) {
        log.info("添加慢性病记录: userId={}, type={}", record.getUserId(), record.getType());
        
        // 设置创建时间
        record.setCreateTime(LocalDateTime.now());
        record.setUpdateTime(LocalDateTime.now());
        
        // 判断是否异常
        if (record.getIsAbnormal() == null) {
            record.setIsAbnormal(checkAbnormal(record));
        }
        
        chronicDiseaseMapper.insert(record);
        return record;
    }

    @Override
    public ChronicDiseaseRecord updateRecord(ChronicDiseaseRecord record) {
        if (record.getId() == null || record.getUserId() == null) {
            throw new IllegalArgumentException("记录ID和用户ID不能为空");
        }
        ChronicDiseaseRecord existing = chronicDiseaseMapper.selectOne(
                new LambdaQueryWrapper<ChronicDiseaseRecord>()
                        .eq(ChronicDiseaseRecord::getId, record.getId())
                        .eq(ChronicDiseaseRecord::getUserId, record.getUserId()));
        if (existing == null) {
            throw new RuntimeException("记录不存在或无权限操作");
        }
        record.setCreateTime(existing.getCreateTime());
        record.setUpdateTime(LocalDateTime.now());
        record.setIsAbnormal(checkAbnormal(record));
        chronicDiseaseMapper.updateById(record);
        return record;
    }
    
    @Override
    public void deleteRecord(Long id, Long userId) {
        log.info("删除慢性病记录: id={}, userId={}", id, userId);
        
        LambdaQueryWrapper<ChronicDiseaseRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChronicDiseaseRecord::getId, id)
                   .eq(ChronicDiseaseRecord::getUserId, userId);
        
        chronicDiseaseMapper.delete(queryWrapper);
    }
    
    @Override
    public List<ChronicDiseaseRecord> getRecordsByType(Long userId, String type) {
        log.info("查询慢性病记录: userId={}, type={}", userId, type);
        return chronicDiseaseMapper.selectByUserIdAndType(userId, type);
    }
    
    @Override
    public List<ChronicDiseaseRecord> getAllRecords(Long userId) {
        log.info("查询所有慢性病记录: userId={}", userId);
        return chronicDiseaseMapper.selectByUserId(userId);
    }
    
    @Override
    public Map<String, Object> getStatistics(Long userId, String type) {
        log.info("获取统计信息: userId={}, type={}", userId, type);
        
        // 获取基础统计
        Map<String, Object> stats = chronicDiseaseMapper.selectStatsByUserIdAndType(userId, type);
        if (stats == null) {
            stats = new HashMap<>();
            stats.put("totalRecords", 0L);
            stats.put("abnormalCount", 0L);
        }
        
        // 获取记录列表用于计算连续天数
        List<ChronicDiseaseRecord> records = chronicDiseaseMapper.selectByUserIdAndType(userId, type);
        
        // 计算连续记录天数
        int consecutiveDays = calculateConsecutiveDays(records);
        stats.put("consecutiveDays", consecutiveDays);
        
        // 获取最近记录时间
        String lastRecord = "暂无记录";
        if (!records.isEmpty()) {
            ChronicDiseaseRecord latest = records.get(0);
            lastRecord = latest.getDate() + " " + latest.getTime();
        }
        stats.put("lastRecord", lastRecord);
        
        return stats;
    }
    
    @Override
    public List<ChronicDiseaseRecord> exportData(Long userId, String type) {
        log.info("导出数据: userId={}, type={}", userId, type);
        return chronicDiseaseMapper.selectByUserIdAndType(userId, type);
    }
    
    /**
     * 判断指标是否异常
     */
    private boolean checkAbnormal(ChronicDiseaseRecord record) {
        switch (record.getType()) {
            case "blood_pressure":
                // 血压: 正常范围 收缩压 90-140, 舒张压 60-90
                if (record.getSystolic() != null && record.getDiastolic() != null) {
                    return record.getSystolic() < 90 || record.getSystolic() > 140 
                        || record.getDiastolic() < 60 || record.getDiastolic() > 90;
                }
                break;
                
            case "blood_sugar":
                // 血糖: 空腹 3.9-6.1, 餐后2小时 <7.8
                if (record.getValue() != null && record.getMealType() != null) {
                    if ("空腹".equals(record.getMealType())) {
                        return record.getValue() < 3.9 || record.getValue() > 6.1;
                    } else if (record.getMealType().contains("餐后")) {
                        return record.getValue() > 7.8;
                    }
                }
                break;
                
            case "heart_rate":
                // 心率: 正常范围 60-100
                if (record.getValue() != null) {
                    return record.getValue() < 60 || record.getValue() > 100;
                }
                break;
                
            case "weight":
                // 体重暂不判断异常
                return false;
                
            default:
                return false;
        }
        return false;
    }
    
    /**
     * 计算连续记录天数
     */
    private int calculateConsecutiveDays(List<ChronicDiseaseRecord> records) {
        if (records.isEmpty()) {
            return 0;
        }
        
        // 获取所有唯一日期并排序
        Set<String> uniqueDates = new TreeSet<>(Collections.reverseOrder());
        for (ChronicDiseaseRecord record : records) {
            uniqueDates.add(record.getDate());
        }
        
        List<String> sortedDates = new ArrayList<>(uniqueDates);
        
        // 从今天开始计算连续天数
        int consecutive = 0;
        String today = java.time.LocalDate.now().toString();
        
        for (String date : sortedDates) {
            // 计算日期差
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

    @Override
    public Map<String, Object> analyzeHealthTrend(Long userId, String type) {
        log.info("分析健康趋势: userId={}, type={}", userId, type);
        
        Map<String, Object> result = new HashMap<>();
        List<ChronicDiseaseRecord> records = chronicDiseaseMapper.selectByUserIdAndType(userId, type);
        
        if (records.isEmpty()) {
            result.put("trend", "暂无数据");
            result.put("warning", "请先添加健康记录");
            result.put("advice", "建议每天定时记录健康数据");
            return result;
        }
        
        // 取最近7次记录进行分析
        int size = Math.min(records.size(), 7);
        List<ChronicDiseaseRecord> recentRecords = records.subList(0, size);
        
        // 简单的趋势分析逻辑
        String trend = analyzeTrendDirection(recentRecords, type);
        String warning = generateWarning(trend, recentRecords.get(0), type);
        String advice = generateAdvice(trend, type);
        
        result.put("trend", trend);
        result.put("warning", warning);
        result.put("advice", advice);
        
        return result;
    }

    /**
     * 分析趋势方向
     */
    private String analyzeTrendDirection(List<ChronicDiseaseRecord> records, String type) {
        if (records.size() < 2) {
            return "数据不足";
        }
        
        // 比较最近一次和最远一次记录
        ChronicDiseaseRecord latest = records.get(0);
        ChronicDiseaseRecord oldest = records.get(records.size() - 1);
        
        if ("blood_pressure".equals(type)) {
            if (latest.getSystolic() > oldest.getSystolic() + 10) {
                return "上升趋势";
            } else if (latest.getSystolic() < oldest.getSystolic() - 10) {
                return "下降趋势";
            }
        } else if (latest.getValue() != null && oldest.getValue() != null) {
            if (latest.getValue() > oldest.getValue() * 1.05) {
                return "上升趋势";
            } else if (latest.getValue() < oldest.getValue() * 0.95) {
                return "下降趋势";
            }
        }
        
        return "保持平稳";
    }

    /**
     * 生成预警信息
     */
    private String generateWarning(String trend, ChronicDiseaseRecord latest, String type) {
        if ("blood_pressure".equals(type)) {
            if (latest.getSystolic() > 140 || latest.getDiastolic() > 90) {
                return "您的血压偏高，请注意控制！";
            }
            if ("上升趋势".equals(trend)) {
                return "近期血压有上升趋势，请密切关注。";
            }
        } else if ("blood_sugar".equals(type)) {
             if (latest.getValue() > 7.8) {
                 return "血糖值偏高，建议控制饮食。";
             }
        }
        return "各项指标目前正常。";
    }

    /**
     * 生成健康建议
     */
    private String generateAdvice(String trend, String type) {
        if ("blood_pressure".equals(type)) {
            if ("上升趋势".equals(trend)) {
                return "建议减少盐分摄入，避免情绪激动，保持充足睡眠。";
            } else {
                return "请继续保持良好的生活习惯，定期测量血压。";
            }
        } else if ("blood_sugar".equals(type)) {
            return "建议少吃高糖高脂食物，适量运动，定期监测血糖。";
        } else if ("weight".equals(type)) {
            if ("上升趋势".equals(trend)) {
                return "建议控制饮食总量，增加有氧运动。";
            }
        }
        return "保持健康的生活方式是预防慢性病的关键。";
    }
}

