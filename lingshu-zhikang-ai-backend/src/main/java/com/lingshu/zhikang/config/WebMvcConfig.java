package com.lingshu.zhikang.config;

import com.lingshu.zhikang.interceptor.QuotaInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private QuotaInterceptor quotaInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns("*")
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("*")
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 添加配额拦截器
        registry.addInterceptor(quotaInterceptor)
                .addPathPatterns(
                        "/ai/chat",                  // AI对话（实际路径）
                        "/pdf/chat",                 // PDF对话
                        "/api/ai-chat/**",           // AI对话（API路径）
                        "/api/ai-diagnosis/**",      // AI诊断
                        "/api/symptom-checker/**",   // 症状检查
                        "/api/medical-diagnosis/**", // 医学诊断
                        "/api/doctor-simulator/**",  // 医生模拟
                        "/api/health-report/**",     // 健康报告
                        "/api/medicine/query"        // AI药品查询
                )
                .excludePathPatterns(
                        "/api/user/login",           // 登录
                        "/api/user/register",        // 注册
                        "/api/admin/**"              // 管理员接口
                );
    }
}
