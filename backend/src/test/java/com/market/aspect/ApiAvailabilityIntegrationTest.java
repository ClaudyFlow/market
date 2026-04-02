package com.market.aspect;

import com.market.annotation.ApiAvailable;
import com.market.annotation.ApiHealthCheck;
import com.market.common.Result;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API 可用性注解功能测试
 */
class ApiAvailabilityIntegrationTest {

    /**
     * 测试用控制器类
     */
    static class TestController {

        @ApiAvailable(
            timeout = 5000,
            retryCount = 3,
            retryInterval = 1000,
            dependencies = {"database", "redis"},
            onFailure = ApiAvailable.FailureAction.RETURN_ERROR,
            errorMessage = "测试错误消息",
            enabled = true
        )
        public Result<String> testMethod() {
            return Result.success("OK");
        }

        @ApiHealthCheck(
            critical = true,
            checkInterval = 60,
            alertEnabled = true,
            alertThreshold = 3
        )
        public Result<String> healthCheckMethod() {
            return Result.success("OK");
        }
    }

    @Test
    void testAnnotationExists() {
        // 验证所有相关类都存在
        assertDoesNotThrow(() -> Class.forName("com.market.annotation.ApiAvailable"), "ApiAvailable 类应存在");
        assertDoesNotThrow(() -> Class.forName("com.market.annotation.ApiHealthCheck"), "ApiHealthCheck 类应存在");
        assertDoesNotThrow(() -> Class.forName("com.market.annotation.ApiAvailabilityDetector"), "ApiAvailabilityDetector 类应存在");
        assertDoesNotThrow(() -> Class.forName("com.market.aspect.ApiAvailabilityAspect"), "ApiAvailabilityAspect 类应存在");
        assertDoesNotThrow(() -> Class.forName("com.market.exception.ApiAvailabilityException"), "ApiAvailabilityException 类应存在");
    }

    @Test
    void testAnnotationAttributes() throws NoSuchMethodException {
        var method = TestController.class.getMethod("testMethod");
        var annotation = method.getAnnotation(ApiAvailable.class);

        assertNotNull(annotation, "@ApiAvailable 注解未找到");

        assertEquals(5000, annotation.timeout(), "timeout 应为 5000");
        assertEquals(3, annotation.retryCount(), "retryCount 应为 3");
        assertTrue(annotation.enabled(), "enabled 应为 true");

        String[] deps = annotation.dependencies();
        assertEquals(2, deps.length, "dependencies 应有 2 个元素");
        assertEquals("database", deps[0], "第一个依赖应为 database");
        assertEquals("redis", deps[1], "第二个依赖应为 redis");

        assertEquals(ApiAvailable.FailureAction.RETURN_ERROR, annotation.onFailure(), "onFailure 配置不正确");
    }

    @Test
    void testFailureActionEnum() {
        var actions = ApiAvailable.FailureAction.values();
        assertEquals(3, actions.length, "应有 3 个枚举值");
    }

    @Test
    void testHealthCheckAnnotation() throws NoSuchMethodException {
        var method = TestController.class.getMethod("healthCheckMethod");
        var annotation = method.getAnnotation(ApiHealthCheck.class);

        assertNotNull(annotation, "@ApiHealthCheck 注解未找到");
        assertTrue(annotation.critical(), "critical 应为 true");
        assertEquals(60, annotation.checkInterval(), "checkInterval 应为 60");
    }
}
