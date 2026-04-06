package com.market.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis 可用性检测器
 * 
 * 实现 ApiAvailabilityDetector 接口，用于检测 Redis 服务的可用性
 * 通过执行 SET/GET 操作验证 Redis 连接和读写功能是否正常
 * 
 * 使用场景：
 * - API 健康检查时检测 Redis 依赖
 * - 缓存服务故障自动发现和告警
 * - Redis 集群节点健康监控
 * 
 * 检测逻辑：
 * 1. 使用 RedisTemplate 向 "__health_check__" key 写入 "ping" 值（1秒过期）
 * 2. 读取该 key 的值，验证是否为 "ping"
 * 3. 读写一致则返回成功，否则返回失败
 * 4. 若 RedisTemplate 未注入或发生异常，返回失败结果
 *
 * @author market-team
 * @since 1.0
 */
@Component
public class RedisAvailabilityDetector implements ApiAvailabilityDetector {

    /**
     * Redis 模板操作类
     * 标记为 required = false，允许在 Redis 未配置时正常启动
     * 通过 RedisTemplate 执行 SET/GET 操作验证可用性
     */
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
