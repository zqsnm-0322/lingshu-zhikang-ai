package com.lingshu.zhikang.common;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域配置
 */
@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration();
        // 使用 addAllowedOriginPattern 替代 addAllowedOrigin 以支持 allowCredentials
        corsConfiguration.addAllowedOriginPattern("http://localhost:*"); // 1 设置访问源地址
        corsConfiguration.addAllowedOriginPattern("http://127.0.0.1:*");
        corsConfiguration.addAllowedOriginPattern("https://localhost:*");
        corsConfiguration.addAllowedOriginPattern("https://127.0.0.1:*");
        // 允许 ngrok 域名访问
        corsConfiguration.addAllowedOriginPattern("https://*.ngrok-free.app");
        corsConfiguration.addAllowedOriginPattern("https://*.ngrok.io");
        // 明确指定允许的头部，避免使用 * 与 allowCredentials 冲突
        corsConfiguration.addAllowedHeader("Content-Type");
        corsConfiguration.addAllowedHeader("Authorization");
        corsConfiguration.addAllowedHeader("X-Requested-With");
        corsConfiguration.addAllowedHeader("Accept");
        corsConfiguration.addAllowedHeader("Origin");
        corsConfiguration.addAllowedHeader("Access-Control-Request-Method");
        corsConfiguration.addAllowedHeader("Access-Control-Request-Headers");
        corsConfiguration.addAllowedMethod("*"); // 3 设置访问源请求方法
        corsConfiguration.setAllowCredentials(true); // 允许携带凭证
        source.registerCorsConfiguration("/**", corsConfiguration); // 4 对接口配置跨域设置
        return new CorsFilter(source);
    }
}
