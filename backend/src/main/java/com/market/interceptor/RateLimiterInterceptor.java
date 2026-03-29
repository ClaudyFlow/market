package com.market.interceptor;

import com.market.annotation.RateLimiter;
import com.market.common.Result;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.PrintWriter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * 限流拦截器
 */
@Component
public class RateLimiterInterceptor implements HandlerInterceptor {

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 存储每个 key 的请求记录：key -> (时间戳，计数)
    private final Map<String, RateLimitRecord> rateLimitMap = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        HandlerMethod handlerMethod = (HandlerMethod) handler;
        RateLimiter rateLimiter = handlerMethod.getMethodAnnotation(RateLimiter.class);

        // 检查方法上的注解
        if (rateLimiter == null) {
            // 检查类上的注解
            rateLimiter = handlerMethod.getBeanType().getAnnotation(RateLimiter.class);
        }

        if (rateLimiter == null) {
            return true;
        }

        // 获取限流 key
        String key = getRateLimitKey(request, rateLimiter.key());

        // 检查是否超过限制
        if (!tryAcquire(key, rateLimiter)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(429);
            PrintWriter writer = response.getWriter();
            writer.write(objectMapper.writeValueAsString(
                Result.error(rateLimiter.message())
            ));
            writer.flush();
            return false;
        }

        return true;
    }

    /**
     * 获取限流 key
     */
    private String getRateLimitKey(HttpServletRequest request, String keyExpression) {
        // 支持简单的 key 表达式
        if (keyExpression.contains("#remoteAddr")) {
            return request.getRemoteAddr();
        }
        if (keyExpression.contains("#userId")) {
            String userId = (String) request.getAttribute("userId");
            return userId != null ? userId : request.getRemoteAddr();
        }
        return request.getRemoteAddr();
    }

    /**
     * 尝试获取许可
     */
    private synchronized boolean tryAcquire(String key, RateLimiter rateLimiter) {
        long now = System.currentTimeMillis();
        long timeoutMillis = rateLimiter.timeUnit().toMillis(rateLimiter.timeout());
        int maxRequests = rateLimiter.maxRequests();

        RateLimitRecord record = rateLimitMap.get(key);

        if (record == null) {
            // 新记录
            rateLimitMap.put(key, new RateLimitRecord(now, 1));
            return true;
        }

        // 检查时间窗口是否过期
        if (now - record.windowStart > timeoutMillis) {
            // 重置窗口
            record.windowStart = now;
            record.count = 1;
            return true;
        }

        // 检查是否超过限制
        if (record.count >= maxRequests) {
            return false;
        }

        // 增加计数
        record.count++;
        return true;
    }

    /**
     * 清理过期的记录（定期调用）
     */
    public void cleanupExpiredRecords(long defaultTimeoutMillis) {
        long now = System.currentTimeMillis();
        rateLimitMap.entrySet().removeIf(entry -> 
            now - entry.getValue().windowStart > defaultTimeoutMillis
        );
    }

    /**
     * 限流记录内部类
     */
    private static class RateLimitRecord {
        long windowStart;  // 窗口开始时间
        int count;         // 当前窗口内的请求数

        RateLimitRecord(long windowStart, int count) {
            this.windowStart = windowStart;
            this.count = count;
        }
    }
}
