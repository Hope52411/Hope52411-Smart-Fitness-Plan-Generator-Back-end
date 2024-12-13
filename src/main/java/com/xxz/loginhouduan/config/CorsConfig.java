package com.xxz.loginhouduan.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**") // 匹配所有接口
                .allowedOriginPatterns("*") // 允许所有来源
                .allowedHeaders("*") // 允许所有请求头
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 允许的请求方法
                .allowCredentials(false) // 不允许携带 Cookie 或认证信息
                .maxAge(3600); // 缓存预检请求结果，单位为秒
    }
}

