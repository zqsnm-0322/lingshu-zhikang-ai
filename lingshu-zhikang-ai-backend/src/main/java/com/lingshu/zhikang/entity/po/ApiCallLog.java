package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * API调用记录表
 * 用于记录API服务的调用情况和计费信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("api_call_log")
public class ApiCallLog {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 租户ID(企业客户ID)
     */
    @TableField("tenant_id")
    private Long tenantId;
    
    /**
     * API端点
     */
    @TableField("api_endpoint")
    private String apiEndpoint;
    
    /**
     * 请求参数(JSON格式)
     */
    @TableField("request_params")
    private String requestParams;
    
    /**
     * 响应状态码
     */
    @TableField("response_status")
    private Integer responseStatus;
    
    /**
     * 响应时间(毫秒)
     */
    @TableField("response_time_ms")
    private Integer responseTimeMs;
    
    /**
     * 计费金额
     */
    @TableField("billing_amount")
    private BigDecimal billingAmount;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
}
