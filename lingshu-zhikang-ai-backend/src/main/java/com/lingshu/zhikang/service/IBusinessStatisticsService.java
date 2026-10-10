package com.lingshu.zhikang.service;

import java.util.Map;

/**
 * 商业数据统计服务接口
 * 用于展示项目的商业价值和运营数据
 */
public interface IBusinessStatisticsService {
    
    /**
     * 获取整体运营数据
     * @return 运营数据统计
     */
    Map<String, Object> getOverallStatistics();
    
    /**
     * 获取收入统计
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 收入统计数据
     */
    Map<String, Object> getRevenueStatistics(String startDate, String endDate);
    
    /**
     * 获取用户统计
     * @return 用户统计数据
     */
    Map<String, Object> getUserStatistics();
    
    /**
     * 获取AI服务统计
     * @return AI服务统计数据
     */
    Map<String, Object> getAiServiceStatistics();
    
    /**
     * 获取企业客户统计
     * @return 企业客户统计数据
     */
    Map<String, Object> getEnterpriseStatistics();
    
    /**
     * 获取API调用统计
     * @return API调用统计数据
     */
    Map<String, Object> getApiCallStatistics();
}
