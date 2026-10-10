package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.ChronicDiseaseRecord;

import java.util.List;
import java.util.Map;

/**
 * 慢性病管理服务接口
 */
public interface IChronicDiseaseService {
    
    /**
     * 添加慢性病记录
     */
    ChronicDiseaseRecord addRecord(ChronicDiseaseRecord record);

    ChronicDiseaseRecord updateRecord(ChronicDiseaseRecord record);
    
    /**
     * 删除慢性病记录
     */
    void deleteRecord(Long id, Long userId);
    
    /**
     * 获取用户某个类型的记录列表
     */
    List<ChronicDiseaseRecord> getRecordsByType(Long userId, String type);
    
    /**
     * 获取用户所有记录
     */
    List<ChronicDiseaseRecord> getAllRecords(Long userId);
    
    /**
     * 获取统计信息
     */
    Map<String, Object> getStatistics(Long userId, String type);
    
    /**
     * 导出数据
     */
    List<ChronicDiseaseRecord> exportData(Long userId, String type);

    /**
     * 分析健康趋势
     * @param userId 用户ID
     * @param type 指标类型
     * @return 分析结果
     */
    Map<String, Object> analyzeHealthTrend(Long userId, String type);
}

