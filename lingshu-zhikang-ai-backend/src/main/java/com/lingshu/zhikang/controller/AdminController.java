package com.lingshu.zhikang.controller;

import com.lingshu.zhikang.entity.po.User;
import com.lingshu.zhikang.entity.po.UserUsageLog;
import com.lingshu.zhikang.mapper.UserMapper;
import com.lingshu.zhikang.mapper.UserUsageLogMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 管理员控制器
 * 功能: 查看用户信息、使用统计、权限管理
 */
@RestController
@RequestMapping("/api/admin")
@CrossOrigin(originPatterns = "*", allowCredentials = "true")
public class AdminController {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserUsageLogMapper userUsageLogMapper;

    /**
     * 获取所有用户列表
     */
    @GetMapping("/users")
    public Map<String, Object> getAllUsers(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String memberLevel,
            @RequestParam(required = false) String status
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            // 计算偏移量
            int offset = (page - 1) * pageSize;
            
            // 查询用户列表
            List<User> users = userMapper.selectUserList(keyword, memberLevel, status, offset, pageSize);
            
            // 查询总数
            int total = userMapper.countUsers(keyword, memberLevel, status);
            
            // 隐藏密码
            users.forEach(user -> user.setPassword("******"));
            
            result.put("success", true);
            result.put("data", users);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户详细信息
     */
    @GetMapping("/users/{userId}")
    public Map<String, Object> getUserDetail(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 隐藏密码
            user.setPassword("******");
            
            // 获取使用记录
            List<UserUsageLog> usageLogs = userUsageLogMapper.selectByUserId(userId, 30);
            
            result.put("success", true);
            result.put("data", user);
            result.put("usageLogs", usageLogs);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 获取用户统计数据
     */
    @GetMapping("/statistics")
    public Map<String, Object> getUserStatistics() {
        Map<String, Object> result = new HashMap<>();
        try {
            // 总用户数
            int totalUsers = userMapper.countUsers(null, null, null);
            
            // 各等级用户数
            int freeUsers = userMapper.countUsers(null, "FREE", null);
            int standardUsers = userMapper.countUsers(null, "STANDARD", null);
            int professionalUsers = userMapper.countUsers(null, "PROFESSIONAL", null);
            int enterpriseUsers = userMapper.countUsers(null, "ENTERPRISE", null);
            
            // 活跃用户数(最近7天有使用记录)
            int activeUsers = userUsageLogMapper.countActiveUsers(7);
            
            // 今日新增用户
            int todayNewUsers = userMapper.countTodayNewUsers();
            
            Map<String, Object> data = new HashMap<>();
            data.put("totalUsers", totalUsers);
            data.put("freeUsers", freeUsers);
            data.put("standardUsers", standardUsers);
            data.put("professionalUsers", professionalUsers);
            data.put("enterpriseUsers", enterpriseUsers);
            data.put("activeUsers", activeUsers);
            data.put("todayNewUsers", todayNewUsers);
            data.put("payingRate", totalUsers > 0 ? 
                String.format("%.1f%%", (standardUsers + professionalUsers + enterpriseUsers) * 100.0 / totalUsers) : "0%");
            
            result.put("success", true);
            result.put("data", data);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 更新用户状态
     */
    @PutMapping("/users/{userId}/status")
    public Map<String, Object> updateUserStatus(
            @PathVariable Long userId,
            @RequestParam String status
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            user.setStatus(status);
            userMapper.updateById(user);
            
            result.put("success", true);
            result.put("message", "状态更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 调整用户配额
     */
    @PutMapping("/users/{userId}/quota")
    public Map<String, Object> updateUserQuota(
            @PathVariable Long userId,
            @RequestParam Integer dailyQuota
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            user.setDailyQuota(dailyQuota);
            userMapper.updateById(user);
            
            result.put("success", true);
            result.put("message", "配额更新成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 设置用户等级
     */
    @PutMapping("/users/{userId}/member-level")
    public Map<String, Object> updateUserMemberLevel(
            @PathVariable Long userId,
            @RequestParam String memberLevel
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            User user = userMapper.selectById(userId);
            if (user == null) {
                result.put("success", false);
                result.put("message", "用户不存在");
                return result;
            }
            
            // 验证会员等级是否有效
            if (!isValidMemberLevel(memberLevel)) {
                result.put("success", false);
                result.put("message", "无效的会员等级");
                return result;
            }
            
            // 根据会员等级设置对应的配额
            int dailyQuota = getDailyQuotaByLevel(memberLevel);
            
            // 使用专门的方法更新会员等级和配额
            int updated = userMapper.updateMemberLevel(userId, memberLevel, dailyQuota);
            
            if (updated > 0) {
                // 获取更新后的用户信息
                User updatedUser = userMapper.selectById(userId);
                result.put("success", true);
                result.put("message", "用户等级更新成功");
                result.put("data", updatedUser);
            } else {
                result.put("success", false);
                result.put("message", "更新失败");
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新失败: " + e.getMessage());
        }
        return result;
    }

    /**
     * 验证会员等级是否有效
     */
    private boolean isValidMemberLevel(String memberLevel) {
        return "FREE".equals(memberLevel) || 
               "STANDARD".equals(memberLevel) || 
               "PROFESSIONAL".equals(memberLevel) || 
               "ENTERPRISE".equals(memberLevel);
    }

    /**
     * 根据会员等级获取对应的每日配额
     */
    private int getDailyQuotaByLevel(String memberLevel) {
        switch (memberLevel) {
            case "FREE":
                return 5;
            case "STANDARD":
                return 50;
            case "PROFESSIONAL":
                return 200;
            case "ENTERPRISE":
                return 999999;
            default:
                return 5;
        }
    }

    /**
     * 获取使用趋势数据
     */
    @GetMapping("/usage-trend")
    public Map<String, Object> getUsageTrend(@RequestParam(defaultValue = "30") Integer days) {
        Map<String, Object> result = new HashMap<>();
        try {
            List<Map<String, Object>> trendData = userUsageLogMapper.selectUsageTrend(days);
            
            result.put("success", true);
            result.put("data", trendData);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "查询失败: " + e.getMessage());
        }
        return result;
    }
}
