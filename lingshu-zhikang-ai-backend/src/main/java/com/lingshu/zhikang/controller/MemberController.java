package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.MemberSubscription;
import com.lingshu.zhikang.entity.po.User;
import com.lingshu.zhikang.mapper.UserMapper;
import com.lingshu.zhikang.service.IMemberSubscriptionService;
import com.lingshu.zhikang.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

/**
 * 会员与定价接口（对应修改建议 2.2 商业模式、2.1 AI业务价值）
 */
@RestController
@RequestMapping("/api/member")
public class MemberController {

    @Autowired
    private IMemberSubscriptionService memberSubscriptionService;
    
    @Autowired
    private IUserService userService;
    
    @Autowired
    private UserMapper userMapper;

    /**
     * 获取当前用户会员订阅状态
     * 请求头可带 X-User-Id（前端登录后存储的 userId）
     */
    @GetMapping("/subscription")
    public Map<String, Object> getSubscription(@RequestHeader(value = "X-User-Id", required = false) Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            System.out.println("=== 获取会员信息 ===");
            System.out.println("用户ID: " + userId);
            
            // 先查询订阅记录表
            MemberSubscription sub = memberSubscriptionService.getActiveSubscription(userId);
            
            if (sub != null) {
                // 如果有订阅记录，返回订阅信息
                System.out.println("✓ 找到订阅记录: " + sub.getPlanType());
                Map<String, Object> data = new HashMap<>();
                data.put("planType", sub.getPlanType());
                data.put("endDate", sub.getEndDate() != null ? sub.getEndDate().format(DateTimeFormatter.ISO_LOCAL_DATE) : null);
                data.put("status", sub.getStatus());
                result.put("code", "200");
                result.put("msg", "获取成功");
                result.put("data", data);
                return result;
            }
            
            // 如果没有订阅记录，从用户表中读取会员等级
            if (userId != null) {
                User user = userService.findById(userId);
                if (user != null && user.getMemberLevel() != null && !"FREE".equals(user.getMemberLevel())) {
                    System.out.println("✓ 从用户表读取会员等级: " + user.getMemberLevel());
                    Map<String, Object> data = new HashMap<>();
                    data.put("planType", user.getMemberLevel());
                    data.put("endDate", null); // 用户表中的会员等级没有到期时间
                    data.put("status", "ACTIVE");
                    result.put("code", "200");
                    result.put("msg", "获取成功");
                    result.put("data", data);
                    return result;
                }
            }
            
            // 如果都没有，返回 null（前端会显示免费版）
            System.out.println("⚠ 没有找到会员信息，返回免费版");
            result.put("code", "200");
            result.put("msg", "获取成功");
            result.put("data", null);
        } catch (Exception e) {
            System.err.println("❌ 获取会员信息失败: " + e.getMessage());
            e.printStackTrace();
            result.put("code", "500");
            result.put("msg", "获取会员信息失败");
            result.put("data", null);
        }
        return result;
    }

    /**
     * 套餐列表（可静态返回，与前端一致）
     */
    @GetMapping("/plans")
    public Map<String, Object> getPlans() {
        Map<String, Object> result = new HashMap<>();
        result.put("code", "200");
        result.put("msg", "获取成功");
        result.put("data", null); // 前端使用本地静态套餐，此处可扩展为数据库配置
        return result;
    }
    
    /**
     * 模拟支付升级会员
     */
    @PostMapping("/upgrade")
    public Map<String, Object> upgradeMembership(@RequestBody Map<String, Object> request) {
        Map<String, Object> result = new HashMap<>();
        try {
            Long userId = Long.parseLong(request.get("userId").toString());
            String planType = request.get("planType").toString();
            String paymentType = request.get("paymentType").toString(); // MONTHLY 或 YEARLY
            
            System.out.println("=== 会员升级请求 ===");
            System.out.println("用户ID: " + userId);
            System.out.println("套餐类型: " + planType);
            System.out.println("支付类型: " + paymentType);
            
            // 获取用户信息
            User user = userService.findById(userId);
            if (user == null) {
                result.put("code", "404");
                result.put("msg", "用户不存在");
                return result;
            }
            
            // 设置配额
            int dailyQuota;
            switch (planType) {
                case "STANDARD":
                    dailyQuota = 50;
                    break;
                case "PROFESSIONAL":
                    dailyQuota = 200;
                    break;
                case "ENTERPRISE":
                    dailyQuota = 999999;
                    break;
                default:
                    dailyQuota = 5;
            }
            
            // 更新用户会员等级和配额
            user.setMemberLevel(planType);
            user.setDailyQuota(dailyQuota);
            user.setDailyUsed(0); // 重置今日使用次数
            
            // 更新数据库
            userMapper.updateById(user);
            
            // 创建订阅记录
            int months = "YEARLY".equals(paymentType) ? 12 : 1;
            memberSubscriptionService.createSubscription(userId, planType, months);
            
            System.out.println("✓ 会员升级成功");
            
            result.put("code", "200");
            result.put("msg", "升级成功");
            Map<String, Object> data = new HashMap<>();
            data.put("memberLevel", planType);
            data.put("dailyQuota", dailyQuota);
            result.put("data", data);
            
        } catch (Exception e) {
            System.err.println("❌ 会员升级失败: " + e.getMessage());
            e.printStackTrace();
            result.put("code", "500");
            result.put("msg", "升级失败: " + e.getMessage());
        }
        return result;
    }
}
