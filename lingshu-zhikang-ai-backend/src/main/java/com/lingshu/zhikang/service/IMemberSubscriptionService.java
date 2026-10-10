package com.lingshu.zhikang.service;

import com.lingshu.zhikang.entity.po.MemberSubscription;

/**
 * 会员订阅服务接口
 */
public interface IMemberSubscriptionService {
    
    /**
     * 获取用户当前有效的会员订阅
     */
    MemberSubscription getActiveSubscription(Long userId);
    
    /**
     * 创建会员订阅
     */
    MemberSubscription createSubscription(Long userId, String planType, Integer months);
    
    /**
     * 检查用户是否有有效会员
     */
    boolean hasActiveMembership(Long userId);
    
    /**
     * 获取会员等级
     */
    String getMemberLevel(Long userId);
}
