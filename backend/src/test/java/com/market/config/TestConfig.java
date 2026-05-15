package com.market.config;

import com.market.annotation.ApiAvailabilityDetector;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 测试配置类
 * 用于模拟各种服务状态
 */
@Configuration
@Profile("test")
public class TestConfig {

    /**
     * 模拟数据库不可用的检测器
     */
    @Bean
    @Profile("database-down")
    public ApiAvailabilityDetector databaseDownDetector() {
        return () -> ApiAvailabilityDetector.DetectionResult.failure("数据库连接失败（模拟）");
    }

    /**
     * 模拟 Redis 不可用的检测器
     */
    @Bean
    @Profile("redis-down")
    public ApiAvailabilityDetector redisDownDetector() {
        return () -> ApiAvailabilityDetector.DetectionResult.failure("Redis 连接失败（模拟）");
    }
}
