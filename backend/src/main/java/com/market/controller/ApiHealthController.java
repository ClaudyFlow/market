package com.market.controller;

import com.market.annotation.ApiAvailable;
import com.market.aspect.ApiAvailabilityAspect;
import com.market.common.ApiStatusResult;
import com.market.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * API 健康状态监控控制器
 * 
 * 提供系统服务健康状态查询接口
 */
@Slf4j
@RestController
@RequestMapping("/api/health")
@CrossOrigin(origins = "*")
public class ApiHealthController {

    @Autowired
    private ApiAvailabilityAspect availabilityAspect;

    /**
     * 获取所有服务的健康状态
     */
    @GetMapping("/status")
    @ApiAvailable(timeout = 3000)
    public Result<Map<String, Object>> getAllHealthStatus() {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", LocalDateTime.now());
        response.put("status", "UP");
        response.put("services", getServicesStatus());
        return Result.success(response);
    }

    /**
     * 获取指定服务的健康状态
     */
    @GetMapping("/status/{serviceName}")
    @ApiAvailable(timeout = 3000)
    public Result<ApiStatusResult> getServiceStatus(@PathVariable String serviceName) {
        ApiStatusResult status = availabilityAspect.getServiceHealth(serviceName);
        return Result.success(status);
    }

    /**
     * 健康检查（简化版）
     */
    @GetMapping("/check")
    @ApiAvailable(timeout = 2000)
    public Result<String> healthCheck() {
        return Result.success("OK");
    }

    /**
     * 详细健康检查
     */
    @GetMapping("/check/detailed")
    @ApiAvailable(
        timeout = 5000,
        dependencies = {"database", "redis"},
        onFailure = ApiAvailable.FailureAction.RETURN_ERROR
    )
    public Result<Map<String, Object>> detailedHealthCheck() {
        Map<String, Object> details = new HashMap<>();
        details.put("status", "UP");
        details.put("timestamp", LocalDateTime.now());
        details.put("components", getComponentsHealth());
        return Result.success(details);
    }

    /**
     * 获取服务列表
     */
    private Map<String, Object> getServicesStatus() {
        Map<String, Object> services = new HashMap<>();
        
        // 添加已知服务状态
        services.put("api", createServiceStatus("API 服务", true));
        services.put("database", createServiceStatus("数据库", 
            availabilityAspect.getServiceHealth("database").getStatusCode() >= 2000));
        services.put("redis", createServiceStatus("Redis", 
            availabilityAspect.getServiceHealth("redis").getStatusCode() >= 2000));
        
        return services;
    }

    /**
     * 创建服务状态
     */
    private Map<String, Object> createServiceStatus(String name, boolean healthy) {
        Map<String, Object> status = new HashMap<>();
        status.put("name", name);
        status.put("status", healthy ? "UP" : "DOWN");
        status.put("healthy", healthy);
        return status;
    }

    /**
     * 获取组件健康状态
     */
    private Map<String, Object> getComponentsHealth() {
        Map<String, Object> components = new HashMap<>();
        
        // API 组件
        Map<String, Object> api = new HashMap<>();
        api.put("status", "UP");
        components.put("api", api);
        
        // 数据库组件
        Map<String, Object> db = new HashMap<>();
        try {
            // 这里可以添加实际的数据库检查逻辑
            db.put("status", "UP");
        } catch (Exception e) {
            db.put("status", "DOWN");
            db.put("error", e.getMessage());
        }
        components.put("database", db);
        
        // Redis 组件
        Map<String, Object> redis = new HashMap<>();
        try {
            // 这里可以添加实际的 Redis 检查逻辑
            redis.put("status", "UP");
        } catch (Exception e) {
            redis.put("status", "DOWN");
            redis.put("error", e.getMessage());
        }
        components.put("redis", redis);
        
        return components;
    }
}
