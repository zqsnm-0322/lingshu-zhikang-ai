package com.lingshu.zhikang.service.impl;

import com.lingshu.zhikang.entity.po.ChronicDiseaseRecord;
import com.lingshu.zhikang.entity.po.HealthWarning;
import com.lingshu.zhikang.mapper.ChronicDiseaseMapper;
import com.lingshu.zhikang.mapper.HealthWarningMapper;
import com.lingshu.zhikang.service.IHealthWarningService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 智能预警服务实现
 */
@Slf4j
@Service
public class HealthWarningServiceImpl implements IHealthWarningService {

    @Autowired
    private HealthWarningMapper warningMapper;

    @Autowired
    private ChronicDiseaseMapper chronicDiseaseMapper;

    @Override
    public HealthWarning createWarning(HealthWarning warning) {
        warning.setStatus("pending");
        warning.setCreatedTime(LocalDateTime.now());
        warningMapper.createWarning(warning);
        log.info("创建预警: 用户={}, 级别={}, 类型={}", warning.getUserId(), warning.getWarningLevel(), warning.getWarningType());
        return warning;
    }

    @Override
    public List<HealthWarning> checkAndGenerateWarnings(Long userId) {
        log.info("检查用户 {} 的健康数据并生成预警", userId);

        List<HealthWarning> warnings = new ArrayList<>();

        // 获取最近的健康数据
        List<ChronicDiseaseRecord> recentRecords = chronicDiseaseMapper.getRecentRecords(userId, 7);

        for (ChronicDiseaseRecord record : recentRecords) {
            HealthWarning warning = checkSingleRecord(userId, record);
            if (warning != null) {
                createWarning(warning);
                warnings.add(warning);
            }
        }

        return warnings;
    }

    /**
     * 检查单条记录是否需要预警
     */
    private HealthWarning checkSingleRecord(Long userId, ChronicDiseaseRecord record) {
        if (record == null || !record.getIsAbnormal()) {
            return null;
        }

        HealthWarning warning = new HealthWarning();
        warning.setUserId(userId);
        warning.setIndicatorType(record.getType());
        warning.setWarningType("急性");

        switch (record.getType()) {
            case "blood_pressure":
                return checkBloodPressure(warning, record);
            case "blood_sugar":
                return checkBloodSugar(warning, record);
            case "heart_rate":
                return checkHeartRate(warning, record);
            case "weight":
                return checkWeight(warning, record);
            default:
                return null;
        }
    }

    /**
     * 检查血压
     */
    private HealthWarning checkBloodPressure(HealthWarning warning, ChronicDiseaseRecord record) {
        Integer systolic = record.getSystolic();
        Integer diastolic = record.getDiastolic();

        if (systolic == null || diastolic == null) {
            return null;
        }

        warning.setIndicatorValue(systolic + "/" + diastolic);

        if (systolic >= 180 || diastolic >= 110) {
            warning.setWarningLevel("红色");
            warning.setWarningTitle("血压危险预警");
            warning.setWarningContent("您的血压异常升高（" + systolic + "/" + diastolic + " mmHg），已达到危险水平！");
            warning.setSuggestions("1. 立即停止当前活动，坐下休息\n2. 如有不适，立即拨打120\n3. 尽快就医检查");
            warning.setRiskScore(95);
            warning.setIsUrgent(true);
        } else if (systolic >= 160 || diastolic >= 100) {
            warning.setWarningLevel("橙色");
            warning.setWarningTitle("血压偏高预警");
            warning.setWarningContent("您的血压偏高（" + systolic + "/" + diastolic + " mmHg），需要注意！");
            warning.setSuggestions("1. 注意休息，避免劳累\n2. 监测血压变化\n3. 如持续偏高，建议就医");
            warning.setRiskScore(75);
            warning.setIsUrgent(false);
        } else if (systolic >= 140 || diastolic >= 90) {
            warning.setWarningLevel("黄色");
            warning.setWarningTitle("血压偏高提醒");
            warning.setWarningContent("您的血压略高（" + systolic + "/" + diastolic + " mmHg）。");
            warning.setSuggestions("1. 注意休息\n2. 减少盐分摄入\n3. 保持监测");
            warning.setRiskScore(60);
            warning.setIsUrgent(false);
        } else {
            return null;
        }

        return warning;
    }

    /**
     * 检查血糖
     */
    private HealthWarning checkBloodSugar(HealthWarning warning, ChronicDiseaseRecord record) {
        Double value = record.getValue();
        if (value == null) {
            return null;
        }

        warning.setIndicatorValue(value + " mmol/L");

        if (value >= 16.7 || value <= 3.9) {
            warning.setWarningLevel("红色");
            warning.setWarningTitle("血糖危险预警");
            warning.setWarningContent("您的血糖异常（" + value + " mmol/L），请立即采取措施！");
            warning.setSuggestions(value >= 16.7 ? 
                "1. 检查是否按时用药\n2. 大量饮水\n3. 立即就医" :
                "1. 立即进食糖分\n2. 监测血糖\n3. 如有不适立即就医");
            warning.setRiskScore(95);
            warning.setIsUrgent(true);
        } else if (value >= 11.1 || value <= 4.4) {
            warning.setWarningLevel("橙色");
            warning.setWarningTitle("血糖异常预警");
            warning.setWarningContent("您的血糖偏" + (value >= 11.1 ? "高" : "低") + "（" + value + " mmol/L）");
            warning.setSuggestions("1. 注意饮食\n2. 监测血糖\n3. 如持续异常请就医");
            warning.setRiskScore(75);
            warning.setIsUrgent(false);
        } else {
            return null;
        }

        return warning;
    }

    /**
     * 检查心率
     */
    private HealthWarning checkHeartRate(HealthWarning warning, ChronicDiseaseRecord record) {
        Double value = record.getValue();
        if (value == null) {
            return null;
        }

        int heartRate = value.intValue();
        warning.setIndicatorValue(heartRate + " 次/分");

        if (heartRate >= 120 || heartRate <= 40) {
            warning.setWarningLevel("红色");
            warning.setWarningTitle("心率异常预警");
            warning.setWarningContent("您的心率异常（" + heartRate + " 次/分）！");
            warning.setSuggestions("1. 立即停止活动\n2. 坐下休息\n3. 如有不适立即就医");
            warning.setRiskScore(90);
            warning.setIsUrgent(true);
        } else if (heartRate >= 100 || heartRate <= 50) {
            warning.setWarningLevel("黄色");
            warning.setWarningTitle("心率偏" + (heartRate >= 100 ? "快" : "慢"));
            warning.setWarningContent("您的心率偏" + (heartRate >= 100 ? "快" : "慢") + "（" + heartRate + " 次/分）");
            warning.setSuggestions("1. 注意休息\n2. 避免剧烈运动\n3. 持续监测");
            warning.setRiskScore(60);
            warning.setIsUrgent(false);
        } else {
            return null;
        }

        return warning;
    }

    /**
     * 检查体重
     */
    private HealthWarning checkWeight(HealthWarning warning, ChronicDiseaseRecord record) {
        // 体重变化需要历史数据对比，这里简化处理
        return null;
    }

    @Override
    public List<HealthWarning> getUserWarnings(Long userId, int limit) {
        return warningMapper.getUserWarnings(userId, limit);
    }

    @Override
    public List<HealthWarning> getPendingWarnings(Long userId) {
        return warningMapper.getPendingWarnings(userId);
    }

    @Override
    public boolean updateWarningStatus(Long warningId, String status, String note) {
        HealthWarning warning = warningMapper.getWarningById(warningId);
        if (warning == null) {
            return false;
        }

        warning.setStatus(status);
        warning.setHandledTime(LocalDateTime.now());
        warning.setHandledNote(note);

        return warningMapper.updateWarningStatus(warning) > 0;
    }

    @Override
    public HealthWarning getWarningById(Long id) {
        return warningMapper.getWarningById(id);
    }

    @Override
    public Map<String, Object> getWarningStatistics(Long userId, int days) {
        List<Map<String, Object>> levelCounts = warningMapper.countWarningsByLevel(userId, days);

        Map<String, Object> statistics = new HashMap<>();
        statistics.put("period", days + "天");
        statistics.put("levelCounts", levelCounts);

        // 计算总预警数
        int total = levelCounts.stream()
                .mapToInt(m -> ((Number) m.get("count")).intValue())
                .sum();
        statistics.put("total", total);

        // 获取未处理预警
        List<HealthWarning> pending = warningMapper.getPendingWarnings(userId);
        statistics.put("pendingCount", pending.size());

        return statistics;
    }

    @Override
    public Map<String, Object> assessHealthRisk(Long userId) {
        log.info("评估用户 {} 的健康风险", userId);

        Map<String, Object> assessment = new HashMap<>();

        // 获取近期健康数据
        List<ChronicDiseaseRecord> records = chronicDiseaseMapper.getRecentRecords(userId, 30);

        // 计算风险评分
        int riskScore = calculateRiskScore(records);

        // 确定风险等级
        String riskLevel;
        if (riskScore >= 80) {
            riskLevel = "极高";
        } else if (riskScore >= 60) {
            riskLevel = "高";
        } else if (riskScore >= 40) {
            riskLevel = "中";
        } else {
            riskLevel = "低";
        }

        assessment.put("riskScore", riskScore);
        assessment.put("riskLevel", riskLevel);
        assessment.put("assessmentDate", LocalDateTime.now());
        assessment.put("riskFactors", identifyRiskFactors(records));
        assessment.put("recommendations", generateRiskRecommendations(riskLevel));

        return assessment;
    }

    /**
     * 计算风险评分
     */
    private int calculateRiskScore(List<ChronicDiseaseRecord> records) {
        if (records == null || records.isEmpty()) {
            return 30; // 无数据，默认低风险
        }

        long abnormalCount = records.stream()
                .filter(r -> r.getIsAbnormal() != null && r.getIsAbnormal())
                .count();

        double abnormalRate = (double) abnormalCount / records.size();

        return (int) (abnormalRate * 100);
    }

    /**
     * 识别风险因素
     */
    private List<String> identifyRiskFactors(List<ChronicDiseaseRecord> records) {
        List<String> factors = new ArrayList<>();

        Map<String, Long> abnormalByType = new HashMap<>();
        for (ChronicDiseaseRecord record : records) {
            if (record.getIsAbnormal() != null && record.getIsAbnormal()) {
                abnormalByType.merge(record.getType(), 1L, Long::sum);
            }
        }

        if (abnormalByType.getOrDefault("blood_pressure", 0L) > 0) {
            factors.add("血压不稳定");
        }
        if (abnormalByType.getOrDefault("blood_sugar", 0L) > 0) {
            factors.add("血糖控制不佳");
        }
        if (abnormalByType.getOrDefault("heart_rate", 0L) > 0) {
            factors.add("心率异常");
        }

        if (factors.isEmpty()) {
            factors.add("暂无明显风险因素");
        }

        return factors;
    }

    /**
     * 生成风险建议
     */
    private List<String> generateRiskRecommendations(String riskLevel) {
        List<String> recommendations = new ArrayList<>();

        switch (riskLevel) {
            case "极高":
                recommendations.add("建议尽快就医进行全面检查");
                recommendations.add("密切监测健康指标变化");
                recommendations.add("严格遵医嘱用药");
                break;
            case "高":
                recommendations.add("建议近期安排体检");
                recommendations.add("加强健康监测频率");
                recommendations.add("注意生活方式调整");
                break;
            case "中":
                recommendations.add("保持定期监测");
                recommendations.add("注意饮食和运动");
                recommendations.add("如有不适及时就医");
                break;
            default:
                recommendations.add("保持良好生活习惯");
                recommendations.add("定期进行健康检查");
                recommendations.add("继续保持健康状态");
        }

        return recommendations;
    }
}

