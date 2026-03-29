package com.market.config;

import com.market.interceptor.JwtAuthenticationInterceptor;
import com.market.interceptor.RateLimiterInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 配置
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Autowired
    private JwtAuthenticationInterceptor jwtAuthenticationInterceptor;

    @Autowired
    private RateLimiterInterceptor rateLimiterInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // JWT 认证拦截器
        registry.addInterceptor(jwtAuthenticationInterceptor)
            .addPathPatterns("/api/**")
            .excludePathPatterns(
                "/api/auth/login",
                "/api/auth/register",
                "/api/home/**",
                "/api/product/**"
            );

        // 限流拦截器
        registry.addInterceptor(rateLimiterInterceptor)
            .addPathPatterns(
                "/api/upload/**",
                "/api/lottery/**",
                "/api/auth/**"
            );
    }
}
