package com.market.aspect;

import com.market.annotation.ApiAvailable;
import com.market.annotation.ApiHealthCheck;
import com.market.common.Result;
import com.market.exception.ApiAvailabilityException;

/**
 * API 可用性注解功能测试
 * 
 * 测试方法：
 * 1. 直接运行 main 方法（无需 Spring 容器）
 * 2. 验证注解属性是否正确
 */
public class ApiAvailabilityIntegrationTest {

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  API 可用性检测注解 - 功能测试");
        System.out.println("========================================\n");

        boolean allPassed = true;

        // 测试 1: 注解存在性
        allPassed &= testAnnotationExists();

        // 测试 2: 注解属性值
        allPassed &= testAnnotationAttributes();

        // 测试 3: 失败策略枚举
        allPassed &= testFailureActionEnum();

        // 测试 4: 健康检查注解
        allPassed &= testHealthCheckAnnotation();

        System.out.println("\n========================================");
        if (allPassed) {
            System.out.println("  ✓ 所有测试通过!");
        } else {
            System.out.println("  ✗ 部分测试失败!");
            System.exit(1);
        }
        System.out.println("========================================");
    }

    /**
     * 测试 1: 注解是否存在
     */
    private static boolean testAnnotationExists() {
        System.out.println("测试 1: 验证注解存在性...");
        try {
            Class.forName("com.market.annotation.ApiAvailable");
            Class.forName("com.market.annotation.ApiHealthCheck");
            Class.forName("com.market.annotation.ApiAvailabilityDetector");
            Class.forName("com.market.aspect.ApiAvailabilityAspect");
            Class.forName("com.market.exception.ApiAvailabilityException");
            System.out.println("  ✓ 所有类都存在\n");
            return true;
        } catch (ClassNotFoundException e) {
            System.out.println("  ✗ 类不存在：" + e.getMessage() + "\n");
            return false;
        }
    }

    /**
     * 测试 2: 注解属性值
     */
    private static boolean testAnnotationAttributes() {
        System.out.println("测试 2: 验证注解属性值...");
        try {
            var method = TestController.class.getMethod("testMethod");
            var annotation = method.getAnnotation(ApiAvailable.class);

            if (annotation == null) {
                System.out.println("  ✗ @ApiAvailable 注解未找到\n");
                return false;
            }

            boolean passed = true;

            if (annotation.timeout() != 5000) {
                System.out.println("  ✗ timeout 应为 5000, 实际：" + annotation.timeout());
                passed = false;
            }

            if (annotation.retryCount() != 3) {
                System.out.println("  ✗ retryCount 应为 3, 实际：" + annotation.retryCount());
                passed = false;
            }

            if (annotation.enabled() != true) {
                System.out.println("  ✗ enabled 应为 true, 实际：" + annotation.enabled());
                passed = false;
            }

            String[] deps = annotation.dependencies();
            if (deps.length != 2 || !deps[0].equals("database") || !deps[1].equals("redis")) {
                System.out.println("  ✗ dependencies 配置不正确");
                passed = false;
            }

            if (annotation.onFailure() != ApiAvailable.FailureAction.RETURN_ERROR) {
                System.out.println("  ✗ onFailure 配置不正确");
                passed = false;
            }

            if (passed) {
                System.out.println("  ✓ 所有属性值正确\n");
            }
            return passed;
        } catch (Exception e) {
            System.out.println("  ✗ 测试失败：" + e.getMessage() + "\n");
            return false;
        }
    }

    /**
     * 测试 3: 失败策略枚举
     */
    private static boolean testFailureActionEnum() {
        System.out.println("测试 3: 验证失败策略枚举...");
        var actions = ApiAvailable.FailureAction.values();

        if (actions.length != 3) {
            System.out.println("  ✗ 应有 3 个枚举值，实际：" + actions.length);
            return false;
        }

        System.out.println("  枚举值:");
        for (var action : actions) {
            System.out.println("    - " + action.name());
        }
        System.out.println("  ✓ 枚举定义正确\n");
        return true;
    }

    /**
     * 测试 4: 健康检查注解
     */
    private static boolean testHealthCheckAnnotation() {
        System.out.println("测试 4: 验证健康检查注解...");
        try {
            var method = TestController.class.getMethod("healthCheckMethod");
            var annotation = method.getAnnotation(ApiHealthCheck.class);

            if (annotation == null) {
                System.out.println("  ✗ @ApiHealthCheck 注解未找到\n");
                return false;
            }

            if (annotation.critical() != true) {
                System.out.println("  ✗ critical 应为 true");
                return false;
            }

            if (annotation.checkInterval() != 60) {
                System.out.println("  ✗ checkInterval 应为 60");
                return false;
            }

            System.out.println("  ✓ 健康检查注解配置正确\n");
            return true;
        } catch (Exception e) {
            System.out.println("  ✗ 测试失败：" + e.getMessage() + "\n");
            return false;
        }
    }

    // 测试用控制器类
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
}
