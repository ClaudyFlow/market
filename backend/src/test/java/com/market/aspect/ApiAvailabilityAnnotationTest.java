package com.market.aspect;

import com.market.annotation.ApiAvailable;
import com.market.annotation.ApiHealthCheck;
import com.market.common.Result;
import org.junit.jupiter.api.Test;

import java.lang.annotation.Annotation;

import static org.junit.jupiter.api.Assertions.*;

/**
 * API 可用性检测注解测试（纯单元测试，不需要 Spring 容器）
 */
class ApiAvailabilityAnnotationTest {

    /**
     * 测试用控制器类
     */
    static class TestController {

        @ApiAvailable(timeout = 3000)
        @ApiHealthCheck(critical = true)
        public Result<String> testMethod() {
            return Result.success("OK");
        }

        @ApiAvailable(
            timeout = 5000,
            retryCount = 2,
            dependencies = {"database", "redis"},
            onFailure = ApiAvailable.FailureAction.RETURN_ERROR
        )
        public Result<String> methodWithDependencies() {
            return Result.success("OK");
        }
    }

    @Test
    void testApiAvailableAnnotation() throws NoSuchMethodException {
        // 获取测试类上的注解
        var method = TestController.class.getMethod("testMethod");
        var annotation = method.getAnnotation(ApiAvailable.class);

        assertNotNull(annotation, "@ApiAvailable 注解未找到");
        assertEquals(3000, annotation.timeout(), "timeout 默认值应为 3000");
        assertEquals(0, annotation.retryCount(), "retryCount 默认值应为 0");
        assertTrue(annotation.enabled(), "enabled 默认值应为 true");
    }

    @Test
    void testApiHealthCheckAnnotation() throws NoSuchMethodException {
        var method = TestController.class.getMethod("testMethod");
        var annotation = method.getAnnotation(ApiHealthCheck.class);

        assertNotNull(annotation, "@ApiHealthCheck 注解未找到");
        assertTrue(annotation.critical(), "critical 默认值应为 true");
        assertEquals(60, annotation.checkInterval(), "checkInterval 默认值应为 60");
    }

    @Test
    void testApiAvailableWithDependencies() throws NoSuchMethodException {
        var method = TestController.class.getMethod("methodWithDependencies");
        var annotation = method.getAnnotation(ApiAvailable.class);

        assertNotNull(annotation, "@ApiAvailable 注解未找到");
        String[] expected = {"database", "redis"};
        String[] actual = annotation.dependencies();
        assertArrayEquals(expected, actual, "dependencies 配置不正确");
        assertEquals(ApiAvailable.FailureAction.RETURN_ERROR, annotation.onFailure(), "onFailure 配置不正确");
    }

    @Test
    void testFailureActionEnum() {
        var actions = ApiAvailable.FailureAction.values();
        assertEquals(3, actions.length, "FailureAction 应有 3 个枚举值");
    }
}
