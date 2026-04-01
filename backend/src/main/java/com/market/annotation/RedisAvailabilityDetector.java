package com.market.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 可用性检测器
 */
@Component
public class RedisAvailabilityDetector implements ApiAvailabilityDetector {

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public DetectionResult detect() {
        try {
            if (redisTemplate != null) {
                redisTemplate.opsForValue().set("__health_check__", "ping", java.time.Duration.ofSeconds(1));
                String value = (String) redisTemplate.opsForValue().get("__health_check__");
                if ("ping".equals(value)) {
                    return DetectionResult.success("Redis connection OK");
                }
            }
            return DetectionResult.failure("No Redis connection available");
        } catch (Exception e) {
            return DetectionResult.failure("Redis check failed: " + e.getMessage());
        }
    }
}
