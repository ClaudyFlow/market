package com.market.aspect;

import com.market.annotation.ApiAvailable;
import com.market.annotation.ApiHealthCheck;
import com.market.common.Result;

import java.lang.annotation.Annotation;

/**
 * API 可用性检测注解测试（纯单元测试，不需要 Spring 容器）
 * 
 * 注意：此测试文件仅用于验证注解的基本功能
 * 如需完整的集成测试，请添加以下依赖到 pom.xml:
 * 
 * &lt;dependency&gt;
 *     &lt;groupId&gt;org.springframework.boot&lt;/groupId&gt;
 *     &lt;artifactId&gt;spring-boot-starter-test&lt;/artifactId&gt;
 *     &lt;scope&gt;test&lt;/scope&gt;
 * &lt;/dependency&gt;
 */
public class ApiAvailabilityAnnotationTest {

    /**
     * 测试 @ApiAvailable 注解基本属性
     */
    public static void testApiAvailableAnnotation() throws NoSuchMethodException {
        // 获取测试类上的注解
        var method = TestController.class.getMethod("testMethod");
        var annotation = method.getAnnotation(ApiAvailable.class);
        
        if (annotation == null) {
            throw new AssertionError("@ApiAvailable 注解未找到");
        }
        if (annotation.timeout() != 3000) {
            throw new AssertionError("timeout 默认值应为 3000");
        }
        if (annotation.retryCount() != 0) {
            throw new AssertionError("retryCount 默认值应为 0");
        }
        if (!annotation.enabled()) {
            throw new AssertionError("enabled 默认值应为 true");
        }
        
        System.out.println("✓ testApiAvailableAnnotation 通过");
    }

    /**
     * 测试 @ApiHealthCheck 注解
     */
    public static void testApiHealthCheckAnnotation() throws NoSuchMethodException {
        var method = TestController.class.getMethod("testMethod");
        var annotation = method.getAnnotation(ApiHealthCheck.class);
        
        if (annotation == null) {
            throw new AssertionError("@ApiHealthCheck 注解未找到");
        }
        if (!annotation.critical()) {
            throw new AssertionError("critical 默认值应为 true");
        }
        if (annotation.checkInterval() != 60) {
            throw new AssertionError("checkInterval 默认值应为 60");
        }
        
        System.out.println("✓ testApiHealthCheckAnnotation 通过");
    }

    /**
     * 测试带有依赖配置的注解
     */
    public static void testApiAvailableWithDependencies() throws NoSuchMethodException {
        var method = TestController.class.getMethod("methodWithDependencies");
        var annotation = method.getAnnotation(ApiAvailable.class);
        
        if (annotation == null) {
            throw new AssertionError("@ApiAvailable 注解未找到");
        }
        String[] expected = {"database", "redis"};
        String[] actual = annotation.dependencies();
        if (actual.length != expected.length || 
            !actual[0].equals(expected[0]) || 
            !actual[1].equals(expected[1])) {
            throw new AssertionError("dependencies 配置不正确");
        }
        if (annotation.onFailure() != ApiAvailable.FailureAction.RETURN_ERROR) {
            throw new AssertionError("onFailure 配置不正确");
        }
        
        System.out.println("✓ testApiAvailableWithDependencies 通过");
    }

    /**
     * 测试失败处理策略枚举
     */
    public static void testFailureActionEnum() {
        var actions = ApiAvailable.FailureAction.values();
        if (actions.length != 3) {
            throw new AssertionError("FailureAction 应有 3 个枚举值");
        }
        
        System.out.println("✓ testFailureActionEnum 通过");
    }

    /**
     * 测试控制器类
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
    
    /**
     * 主函数，运行所有测试
     */
    public static void main(String[] args) {
        System.out.println("开始运行 API 可用性注解测试...\n");
        
        try {
            testApiAvailableAnnotation();
            testApiHealthCheckAnnotation();
            testApiAvailableWithDependencies();
            testFailureActionEnum();
            
            System.out.println("\n✓ 所有测试通过!");
        } catch (Exception e) {
            System.err.println("\n✗ 测试失败：" + e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }
}
