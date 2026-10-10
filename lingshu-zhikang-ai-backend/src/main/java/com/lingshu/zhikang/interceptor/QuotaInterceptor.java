package com.lingshu.zhikang.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lingshu.zhikang.entity.po.User;
import com.lingshu.zhikang.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用配额拦截器
 * 功能: 检查用户每日使用配额,限制免费用户使用次数
 */
@Component
public class QuotaInterceptor implements HandlerInterceptor {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求中获取用户ID (实际项目中应该从JWT token或session中获取)
        String userIdStr = request.getHeader("X-User-Id");
        if (userIdStr == null || userIdStr.isEmpty()) {
            // 如果没有用户ID,允许通过(可能是登录等不需要认证的接口)
            return true;
        }

        Long userId = Long.parseLong(userIdStr);
        User user = userMapper.selectById(userId);

        if (user == null) {
            sendErrorResponse(response, 401, "用户不存在");
            return false;
        }

        // 检查账号状态
        if (!"ACTIVE".equals(user.getStatus())) {
            sendErrorResponse(response, 403, "账号已被禁用");
            return false;
        }

        // 检查是否需要重置每日使用次数
        LocalDate today = LocalDate.now();
        if (user.getLastUseDate() == null || !user.getLastUseDate().equals(today)) {
            // 新的一天,重置使用次数
            user.setDailyUsed(0);
            user.setLastUseDate(today);
        }

        // 检查配额
        if (user.getDailyUsed() >= user.getDailyQuota()) {
            // 配额已用完
            Map<String, Object> errorData = new HashMap<>();
            errorData.put("memberLevel", user.getMemberLevel());
            errorData.put("dailyQuota", user.getDailyQuota());
            errorData.put("dailyUsed", user.getDailyUsed());
            errorData.put("message", "今日使用次数已达上限,请升级会员或明天再试");
            errorData.put("upgradeUrl", "/membership");
            
            sendErrorResponse(response, 429, "配额不足", errorData);
            return false;
        }

        // 增加使用次数
        userMapper.incrementUsage(userId);

        // 将用户信息放入request attribute,供后续使用
        request.setAttribute("currentUser", user);

        return true;
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message) throws Exception {
        sendErrorResponse(response, status, message, null);
    }

    private void sendErrorResponse(HttpServletResponse response, int status, String message, Map<String, Object> data) throws Exception {
        response.setStatus(status);
        response.setContentType("application/json;charset=UTF-8");
        
        Map<String, Object> result = new HashMap<>();
        result.put("success", false);
        result.put("message", message);
        if (data != null) {
            result.put("data", data);
        }
        
        response.getWriter().write(objectMapper.writeValueAsString(result));
    }
}
