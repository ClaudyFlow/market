package com.market.controller;

import com.market.annotation.ApiAvailable;
import com.market.annotation.ApiHealthCheck;
import com.market.common.Result;
import org.springframework.web.bind.annotation.*;

/**
 * API 可用性测试控制器
 * 专门用于测试 @ApiAvailable 注解功能
 */
@RestController
@RequestMapping("/api/test/availability")
@CrossOrigin(origins = "*")
public class ApiAvailabilityTestController {

    /**
     * 测试 1：基础超时控制
     * 正常情况应该快速返回
     */
    @GetMapping("/basic")
    @ApiAvailable(timeout = 5000)
    public Result<String> basicTest() {
        return Result.success("基础测试通过");
    }

    /**
     * 测试 2：数据库依赖检测
     * 当数据库不可用时，应该返回错误
     */
    @GetMapping("/database")
    @ApiAvailable(
        timeout = 3000,
        dependencies = {"database"},
        onFailure = ApiAvailable.FailureAction.RETURN_ERROR,
        errorMessage = "数据库服务不可用"
    )
    public Result<String> databaseTest() {
        return Result.success("数据库测试通过");
    }

    /**
     * 测试 3：Redis 依赖检测
     */
    @GetMapping("/redis")
    @ApiAvailable(
        timeout = 3000,
        dependencies = {"redis"},
        onFailure = ApiAvailable.FailureAction.RETURN_ERROR
    )
    public Result<String> redisTest() {
        return Result.success("Redis 测试通过");
    }

    /**
     * 测试 4：多依赖检测
     */
    @GetMapping("/multi-dependencies")
    @ApiAvailable(
        timeout = 5000,
        dependencies = {"database", "redis"},
        onFailure = ApiAvailable.FailureAction.RETURN_ERROR
    )
    @ApiHealthCheck(critical = true)
    public Result<String> multiDependenciesTest() {
        return Result.success("多依赖测试通过");
    }

    /**
     * 测试 5：失败时抛出异常
     */
    @GetMapping("/throw-exception")
    @ApiAvailable(
        timeout = 3000,
        dependencies = {"database"},
        onFailure = ApiAvailable.FailureAction.THROW
    )
    public Result<String> throwExceptionTest() {
        return Result.success("应该抛出异常");
    }

    /**
     * 测试 6：失败时继续执行
     */
    @GetMapping("/continue-on-failure")
    @ApiAvailable(
        timeout = 3000,
        dependencies = {"database"},
        onFailure = ApiAvailable.FailureAction.CONTINUE
    )
    public Result<String> continueOnFailureTest() {
        return Result.success("继续执行测试通过");
    }

    /**
     * 测试 7：超时测试（模拟慢方法）
     */
    @GetMapping("/timeout-test")
    @ApiAvailable(timeout = 1000)  // 1 秒超时
    public Result<String> timeoutTest(
            @RequestParam(defaultValue = "2000") long delayMs) throws InterruptedException {
        Thread.sleep(delayMs);  // 默认延迟 2 秒，超过超时时间
        return Result.success("超时测试通过（不应该到达这里）");
    }

    /**
     * 测试 8：重试机制测试
     */
    @GetMapping("/retry-test")
    @ApiAvailable(
        timeout = 10000,
        retryCount = 2,
        retryInterval = 500,
        dependencies = {"database"},
        onFailure = ApiAvailable.FailureAction.RETURN_ERROR
    )
    public Result<String> retryTest() {
        return Result.success("重试测试通过");
    }

    /**
     * 测试 9：健康检查配置测试
     */
    @GetMapping("/health-check")
    @ApiAvailable(timeout = 3000)
    @ApiHealthCheck(
        critical = false,
        checkInterval = 30,
        alertEnabled = true,
        alertThreshold = 3
    )
    public Result<String> healthCheckTest() {
        return Result.success("健康检查测试通过");
    }

    /**
     * 测试 10：注解未启用测试
     */
    @GetMapping("/disabled")
    @ApiAvailable(
        timeout = 3000,
        enabled = false  // 禁用检测
    )
    public Result<String> disabledTest() {
        return Result.success("禁用检测测试通过");
    }
}
