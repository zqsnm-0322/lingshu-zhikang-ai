package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.RechargeOrder;
import com.lingshu.zhikang.entity.po.User;
import com.lingshu.zhikang.mapper.RechargeOrderMapper;
import com.lingshu.zhikang.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 充值升级控制器
 */
@RestController
@RequestMapping("/api/recharge")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class RechargeController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RechargeOrderMapper rechargeOrderMapper;

    /**
     * 获取会员套餐价格
     */
    @GetMapping("/plans")
    public Map<String, Object> getMemberPlans() {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> plans = rechargeOrderMapper.selectMemberLevelConfigs();
            result.put("success", true);
            result.put("data", plans);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 创建充值订单
     */
    @PostMapping("/create-order")
    public Map<String, Object> createOrder(
            @RequestParam Long userId,
            @RequestParam String targetLevel,
            @RequestParam String paymentType
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            // 获取套餐价格
            Map<String, Object> levelConfig = rechargeOrderMapper.selectMemberLevelConfig(targetLevel);
            if (levelConfig == null) {
                result.put("success", false);
                result.put("message", "套餐不存在");
                return result;
            }

            // 计算金额
            BigDecimal amount;
            LocalDateTime expireAt;
            if ("MONTHLY".equals(paymentType)) {
                amount = (BigDecimal) levelConfig.get("monthly_price");
                expireAt = LocalDateTime.now().plusMonths(1);
            } else {
                amount = (BigDecimal) levelConfig.get("yearly_price");
                expireAt = LocalDateTime.now().plusYears(1);
            }

            // 创建订单
            RechargeOrder order = new RechargeOrder();
            order.setOrderNo("RO" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 8));
            order.setUserId(userId);
            order.setTargetLevel(targetLevel);
            order.setPaymentType(paymentType);
            order.setAmount(amount);
            order.setPaymentStatus("PENDING");
            order.setExpireAt(expireAt);

            rechargeOrderMapper.insert(order);

            result.put("success", true);
            result.put("data", order);
            result.put("message", "订单创建成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建订单失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 模拟支付(实际项目中应该对接真实支付接口)
     */
    @PostMapping("/pay")
    public Map<String, Object> pay(
            @RequestParam String orderNo,
            @RequestParam String paymentMethod
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            RechargeOrder order = rechargeOrderMapper.selectByOrderNo(orderNo);
            if (order == null) {
                result.put("success", false);
                result.put("message", "订单不存在");
                return result;
            }

            if (!"PENDING".equals(order.getPaymentStatus())) {
                result.put("success", false);
                result.put("message", "订单状态异常");
                return result;
            }

            // 更新订单状态
            order.setPaymentMethod(paymentMethod);
            order.setPaymentStatus("PAID");
            order.setPaidAt(LocalDateTime.now());
            rechargeOrderMapper.updateById(order);

            // 升级用户会员等级
            User user = userMapper.selectById(order.getUserId());
            user.setMemberLevel(order.getTargetLevel());
            
            // 更新配额
            Map<String, Object> levelConfig = rechargeOrderMapper.selectMemberLevelConfig(order.getTargetLevel());
            user.setDailyQuota((Integer) levelConfig.get("daily_quota"));
            
            userMapper.updateById(user);

            result.put("success", true);
            result.put("message", "支付成功,会员已升级");
            result.put("data", user);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "支付失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 查询用户订单列表
     */
    @GetMapping("/orders")
    public Map<String, Object> getUserOrders(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<RechargeOrder> orders = rechargeOrderMapper.selectByUserId(userId);
            result.put("success", true);
            result.put("data", orders);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户当前会员信息
     */
    @GetMapping("/member-info")
    public Map<String, Object> getMemberInfo(@RequestParam Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }

            Map<String, Object> memberInfo = new HashMap<>();
            memberInfo.put("memberLevel", user.getMemberLevel());
            memberInfo.put("dailyQuota", user.getDailyQuota());
            memberInfo.put("dailyUsed", user.getDailyUsed());
            memberInfo.put("remainingQuota", user.getDailyQuota() - user.getDailyUsed());
            memberInfo.put("totalUsed", user.getTotalUsed());

            result.put("success", true);
            result.put("data", memberInfo);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }
}
