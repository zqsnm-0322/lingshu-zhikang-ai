package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 企业客户表
 * 用于B2B企业健康管理服务
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("enterprise_customer")
public class EnterpriseCustomer {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 企业名称
     */
    @TableField("company_name")
    private String companyName;
    
    /**
     * 联系人姓名
     */
    @TableField("contact_name")
    private String contactName;
    
    /**
     * 联系电话
     */
    @TableField("contact_phone")
    private String contactPhone;
    
    /**
     * 联系邮箱
     */
    @TableField("contact_email")
    private String contactEmail;
    
    /**
     * 员工数量
     */
    @TableField("employee_count")
    private Integer employeeCount;
    
    /**
     * 服务套餐: BASIC(基础版), STANDARD(标准版), PREMIUM(高级版)
     */
    @TableField("service_plan")
    private String servicePlan;
    
    /**
     * 年费金额
     */
    @TableField("annual_fee")
    private BigDecimal annualFee;
    
    /**
     * 合同开始日期
     */
    @TableField("contract_start_date")
    private LocalDate contractStartDate;
    
    /**
     * 合同结束日期
     */
    @TableField("contract_end_date")
    private LocalDate contractEndDate;
    
    /**
     * API密钥
     */
    @TableField("api_key")
    private String apiKey;
    
    /**
     * API调用配额(每月)
     */
    @TableField("api_quota")
    private Integer apiQuota;
    
    /**
     * API已使用次数(当月)
     */
    @TableField("api_used")
    private Integer apiUsed;
    
    /**
     * 状态: ACTIVE(激活), EXPIRED(过期), SUSPENDED(暂停)
     */
    @TableField("status")
    private String status;
    
    /**
     * 创建时间
     */
    @TableField("created_at")
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @TableField("updated_at")
    private LocalDateTime updatedAt;
}
