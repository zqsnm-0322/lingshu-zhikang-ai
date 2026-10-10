package com.lingshu.zhikang.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lingshu.zhikang.entity.po.ApiCallLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;
import java.util.List;

/**
 * API调用记录Mapper接口
 */
@Mapper
public interface ApiCallLogMapper extends BaseMapper<ApiCallLog> {
    
    /**
     * 统计租户当月API调用次数
     */
    @Select("SELECT COUNT(*) FROM api_call_log WHERE tenant_id = #{tenantId} AND DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    Long countMonthlyCallsByTenant(Long tenantId);
    
    /**
     * 统计租户当月API调用费用
     */
    @Select("SELECT COALESCE(SUM(billing_amount), 0) FROM api_call_log WHERE tenant_id = #{tenantId} AND DATE_FORMAT(created_at, '%Y-%m') = DATE_FORMAT(NOW(), '%Y-%m')")
    BigDecimal sumMonthlyBillingByTenant(Long tenantId);
    
    /**
     * 根据租户ID和日期范围查询API调用记录
     */
    @Select("SELECT * FROM api_call_log WHERE tenant_id = #{tenantId} AND created_at BETWEEN #{startDate} AND #{endDate} ORDER BY created_at DESC")
    List<ApiCallLog> selectByTenantIdAndDateRange(Long tenantId, String startDate, String endDate);
}
