package com.lingshu.zhikang.entity.po;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 会员订阅表
 * 用于管理用户的会员订阅信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName("member_subscription")
public class MemberSubscription {
    
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    
    /**
     * 用户ID
     */
    @TableField("user_id")
    private Long userId;
    
    /**
     * 会员套餐类型: FREE(免费版), STANDARD(标准版), PROFESSIONAL(专业版), ENTERPRISE(企业版)
     */
    @TableField("plan_type")
    private String planType;
    
    /**
     * 订阅价格
     */
    @TableField("price")
    private BigDecimal price;
    
    /**
     * 开始日期
     */
    @TableField("start_date")
    private LocalDate startDate;
    
    /**
     * 结束日期
     */
    @TableField("end_date")
    private LocalDate endDate;
    
    /**
     * 订阅状态: ACTIVE(激活), EXPIRED(过期), CANCELLED(已取消)
     */
    @TableField("status")
    private String status;
    
    /**
     * 支付方式: ALIPAY(支付宝), WECHAT(微信), BANK_CARD(银行卡)
     */
    @TableField("payment_method")
    private String paymentMethod;
    
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
