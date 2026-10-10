package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.HealthWarning;

import java.util.List;
import java.util.Map;

/**
 * 智能预警服务接口
 */
public interface IHealthWarningService {

    /**
     * 创建预警
     */
    HealthWarning createWarning(HealthWarning warning);

    /**
     * 检查并生成预警（根据最新健康数据）
     */
    List<HealthWarning> checkAndGenerateWarnings(Long userId);

    /**
     * 获取用户的预警列表
     */
    List<HealthWarning> getUserWarnings(Long userId, int limit);

    /**
     * 获取用户未处理的预警
     */
    List<HealthWarning> getPendingWarnings(Long userId);

    /**
     * 更新预警状态
     */
    boolean updateWarningStatus(Long warningId, String status, String note);

    /**
     * 获取预警详情
     */
    HealthWarning getWarningById(Long id);

    /**
     * 统计用户预警情况
     */
    Map<String, Object> getWarningStatistics(Long userId, int days);

    /**
     * 风险评估
     */
    Map<String, Object> assessHealthRisk(Long userId);
}

