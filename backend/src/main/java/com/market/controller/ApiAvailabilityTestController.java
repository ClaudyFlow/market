package com.market.controller;

import com.market.annotation.ApiAvailable;
import com.market.annotation.ApiHealthCheck;
import com.market.common.Result;
import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.*;

/**
 * API 可用性测试控制器
 * 专门用于测试 @ApiAvailable 注解功能，提供多种依赖检测和超时测试接口。
 * 权限要求：仅开发环境可用（@Profile("dev")），公开接口，无需登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/test/availability
 */
@RestController
@RequestMapping("/api/test/availability")
@Profile("dev")
@CrossOrigin(origins = "*")
public class ApiAvailabilityTestController {

    /**
     * 基础超时控制测试
     * API路径：GET /api/test/availability/basic
     * 权限：公开
     *
     * @return 测试结果消息
     */
    @GetMapping("/basic")
    @ApiAvailable(timeout = 5000)
    public Result<String> basicTest() {
        return Result.success("基础测试通过");
    }

    /**
     * 数据库依赖检测测试
     * API路径：GET /api/test/availability/database
     * 权限：公开
     *
     * @return 测试结果消息
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
     * Redis 依赖检测测试
     * API路径：GET /api/test/availability/redis
     * 权限：公开
     *
     * @return 测试结果消息
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
     * 多依赖检测测试
     * API路径：GET /api/test/availability/multi-dependencies
     * 权限：公开
     *
     * @return 测试结果消息
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
     * 失败时抛出异常测试
     * API路径：GET /api/test/availability/throw-exception
     * 权限：公开
     *
     * @return 测试结果消息
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
     * 失败时继续执行测试
     * API路径：GET /api/test/availability/continue-on-failure
     * 权限：公开
     *
     * @return 测试结果消息
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
     * 超时测试
     * API路径：GET /api/test/availability/timeout-test
     * 权限：公开
     *
     * @param delayMs 模拟延迟毫秒数，默认 2000
     * @return 测试结果消息（不应到达此处）
     * @throws InterruptedException 线程中断异常
     */
    @GetMapping("/timeout-test")
    @ApiAvailable(timeout = 1000)
    public Result<String> timeoutTest(
            @RequestParam(defaultValue = "2000") long delayMs) throws InterruptedException {
        Thread.sleep(delayMs);  // 默认延迟 2 秒，超过超时时间
        return Result.success("超时测试通过（不应该到达这里）");
    }

    /**
     * 重试机制测试
     * API路径：GET /api/test/availability/retry-test
     * 权限：公开
     *
     * @return 测试结果消息
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
     * 健康检查配置测试
     * API路径：GET /api/test/availability/health-check
     * 权限：公开
     *
     * @return 测试结果消息
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
     * 注解未启用测试
     * API路径：GET /api/test/availability/disabled
     * 权限：公开
     *
     * @return 测试结果消息
     */
    @GetMapping("/disabled")
    @ApiAvailable(
        timeout = 3000,
        enabled = false
    )
    public Result<String> disabledTest() {
        return Result.success("禁用检测测试通过");
    }
}
