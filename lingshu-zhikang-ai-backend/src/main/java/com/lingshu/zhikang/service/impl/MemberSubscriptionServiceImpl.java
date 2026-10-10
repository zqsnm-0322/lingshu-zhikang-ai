package com.lingshu.zhikang.service.impl;

import com.lingshu.zhikang.entity.po.MemberSubscription;
import com.lingshu.zhikang.mapper.MemberSubscriptionMapper;
import com.lingshu.zhikang.service.IMemberSubscriptionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员订阅服务实现
 */
@Slf4j
@Service
public class MemberSubscriptionServiceImpl implements IMemberSubscriptionService {
    
    @Autowired
    private MemberSubscriptionMapper memberSubscriptionMapper;
    
    @Override
    public MemberSubscription getActiveSubscription(Long userId) {
        return memberSubscriptionMapper.selectActiveSubscription(userId);
    }
    
    @Override
    public MemberSubscription createSubscription(Long userId, String planType, Integer months) {
        log.info("创建会员订阅 - 用户ID: {}, 套餐: {}, 时长: {}个月", userId, planType, months);
        
        // 计算价格
        BigDecimal price = calculatePrice(planType, months);
        
        // 计算日期
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusMonths(months);
        
        MemberSubscription subscription = new MemberSubscription();
        subscription.setUserId(userId);
        subscription.setPlanType(planType);
        subscription.setPrice(price);
        subscription.setStartDate(startDate);
        subscription.setEndDate(endDate);
        subscription.setStatus("ACTIVE");
        subscription.setPaymentMethod("ALIPAY"); // 默认支付方式
        subscription.setCreatedAt(LocalDateTime.now());
        subscription.setUpdatedAt(LocalDateTime.now());
        
        memberSubscriptionMapper.insert(subscription);
        
        log.info("会员订阅创建成功 - 订阅ID: {}", subscription.getId());
        return subscription;
    }
    
    @Override
    public boolean hasActiveMembership(Long userId) {
        MemberSubscription subscription = getActiveSubscription(userId);
        
        if (subscription == null) {
            return false;
        }
        
        LocalDate now = LocalDate.now();
        return "ACTIVE".equals(subscription.getStatus()) &&
               !now.isBefore(subscription.getStartDate()) &&
               !now.isAfter(subscription.getEndDate());
    }
    
    @Override
    public String getMemberLevel(Long userId) {
        MemberSubscription subscription = getActiveSubscription(userId);
        
        if (subscription == null || !hasActiveMembership(userId)) {
            return "FREE";
        }
        
        return subscription.getPlanType();
    }
    
    /**
     * 获取订阅详细信息（扩展方法）
     */
    public Map<String, Object> getSubscriptionInfo(Long userId) {
        Map<String, Object> info = new HashMap<>();
        
        MemberSubscription subscription = getActiveSubscription(userId);
        
        if (subscription == null) {
            info.put("hasSubscription", false);
            info.put("planType", "FREE");
            info.put("features", getFreeFeatures());
            return info;
        }
        
        info.put("hasSubscription", true);
        info.put("planType", subscription.getPlanType());
        info.put("startDate", subscription.getStartDate());
        info.put("endDate", subscription.getEndDate());
        info.put("status", subscription.getStatus());
        info.put("features", getPlanFeatures(subscription.getPlanType()));
        info.put("daysRemaining", calculateDaysRemaining(subscription.getEndDate()));
        
        return info;
    }
    
    /**
     * 续费订阅（扩展方法）
     */
    public Boolean renewSubscription(Long subscriptionId, Integer durationMonths) {
        log.info("续费会员 - 订阅ID: {}, 时长: {}个月", subscriptionId, durationMonths);
        
        MemberSubscription subscription = memberSubscriptionMapper.selectById(subscriptionId);
        if (subscription == null) {
            return false;
        }
        
        LocalDate newEndDate = subscription.getEndDate().plusMonths(durationMonths);
        subscription.setEndDate(newEndDate);
        subscription.setStatus("ACTIVE");
        subscription.setUpdatedAt(LocalDateTime.now());
        
        memberSubscriptionMapper.updateById(subscription);
        
        log.info("会员续费成功 - 新到期日期: {}", newEndDate);
        return true;
    }
    
    /**
     * 取消订阅（扩展方法）
     */
    public Boolean cancelSubscription(Long subscriptionId) {
        log.info("取消会员订阅 - 订阅ID: {}", subscriptionId);
        
        MemberSubscription subscription = memberSubscriptionMapper.selectById(subscriptionId);
        if (subscription == null) {
            return false;
        }
        
        subscription.setStatus("CANCELLED");
        subscription.setUpdatedAt(LocalDateTime.now());
        
        memberSubscriptionMapper.updateById(subscription);
        
        log.info("会员订阅已取消");
        return true;
    }
    
    /**
     * 计算订阅价格
     */
    private BigDecimal calculatePrice(String planType, Integer durationMonths) {
        BigDecimal monthlyPrice;
        
        switch (planType) {
            case "STANDARD":
                monthlyPrice = new BigDecimal("99.00");
                break;
            case "PROFESSIONAL":
                monthlyPrice = new BigDecimal("299.00");
                break;
            case "ENTERPRISE":
                monthlyPrice = new BigDecimal("999.00");
                break;
            default:
                monthlyPrice = new BigDecimal("0.00");
        }
        
        BigDecimal totalPrice = monthlyPrice.multiply(new BigDecimal(durationMonths));
        
        // 年付优惠（12个月打8折）
        if (durationMonths >= 12) {
            totalPrice = totalPrice.multiply(new BigDecimal("0.8"));
        }
        
        return totalPrice;
    }
    
    /**
     * 获取免费版功能
     */
    private String[] getFreeFeatures() {
        return new String[]{
            "基础AI咨询（每日5次）",
            "健康日记记录",
            "基础健康数据统计"
        };
    }
    
    /**
     * 获取套餐功能
     */
    private String[] getPlanFeatures(String planType) {
        switch (planType) {
            case "STANDARD":
                return new String[]{
                    "无限AI对话",
                    "病历分析",
                    "健康报告生成",
                    "慢性病管理",
                    "用药提醒"
                };
            case "PROFESSIONAL":
                return new String[]{
                    "标准版所有功能",
                    "专家级AI诊断",
                    "医学影像分析",
                    "优先响应",
                    "健康风险评估"
                };
            case "ENTERPRISE":
                return new String[]{
                    "专业版所有功能",
                    "API接入",
                    "企业健康数据看板",
                    "定制化服务",
                    "专属技术支持"
                };
            default:
                return getFreeFeatures();
        }
    }
    
    /**
     * 计算剩余天数
     */
    private long calculateDaysRemaining(LocalDate endDate) {
        LocalDate now = LocalDate.now();
        return now.until(endDate).getDays();
    }
}
