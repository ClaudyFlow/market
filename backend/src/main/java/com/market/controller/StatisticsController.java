package com.market.controller;

import com.market.common.Result;
import com.market.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 数据统计控制器
 * 提供平台概览、类目占比、销售趋势等统计接口，以及管理员专属的详细统计接口。
 * 权限要求：概览接口公开，管理员接口需要 ADMIN 角色
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/statistics
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取首页统计概览
     * API路径：GET /api/statistics/overview
     * 权限：公开
     *
     * @return 首页统计数据
     */
    @GetMapping("/overview")
    public Result<Map<String, Object>> getOverviewStats() {
        Map<String, Object> stats = statisticsService.getOverviewStats();
        return Result.success(stats);
    }

    /**
     * 获取类目占比统计
     * API路径：GET /api/statistics/category-distribution
     * 权限：公开
     *
     * @return 类目占比统计数据
     */
    @GetMapping("/category-distribution")
    public Result<Map<String, Object>> getCategoryDistribution() {
        Map<String, Object> stats = statisticsService.getCategoryDistribution();
        return Result.success(stats);
    }

    /**
     * 获取销售趋势
     * API路径：GET /api/statistics/sales-trend
     * 权限：公开
     *
     * @param days 天数范围，默认7
     * @return 销售趋势数据
     */
    @GetMapping("/sales-trend")
    public Result<Map<String, Object>> getSalesTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        Map<String, Object> trend = statisticsService.getSalesTrend(days);
        return Result.success(trend);
    }

    /**
     * 获取用户增长趋势
     * API路径：GET /api/statistics/user-growth
     * 权限：公开
     *
     * @param days 天数范围，默认7
     * @return 用户增长趋势数据
     */
    @GetMapping("/user-growth")
    public Result<Map<String, Object>> getUserGrowthTrend(
            @RequestParam(defaultValue = "7") Integer days) {
        Map<String, Object> trend = statisticsService.getUserGrowthTrend(days);
        return Result.success(trend);
    }

    /**
     * 获取平台统计信息（管理员）
     * API路径：GET /api/statistics/platform
     * 权限：需要 ADMIN 角色
     *
     * @return 平台统计数据
     */
    @GetMapping("/platform")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        Map<String, Object> stats = statisticsService.getPlatformStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取订单统计（管理员）
     * API路径：GET /api/statistics/orders
     * 权限：需要 ADMIN 角色
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 订单统计数据
     */
    @GetMapping("/orders")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getOrderStats(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Map<String, Object> stats = statisticsService.getOrderStats(startTime, endTime);
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取商品统计（管理员）
     * API路径：GET /api/statistics/products
     * 权限：需要 ADMIN 角色
     *
     * @return 商品统计数据
     */
    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        Map<String, Object> stats = statisticsService.getProductStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取用户统计（管理员）
     * API路径：GET /api/statistics/users
     * 权限：需要 ADMIN 角色
     *
     * @return 用户统计数据
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = statisticsService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取论坛统计（管理员）
     * API路径：GET /api/statistics/forum
     * 权限：需要 ADMIN 角色
     *
     * @return 论坛统计数据
     */
    @GetMapping("/forum")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getForumStats() {
        Map<String, Object> stats = statisticsService.getForumStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取销售趋势（管理员）
     * API路径：GET /api/statistics/sales/trend
     * 权限：需要 ADMIN 角色
     *
     * @return 销售趋势数据
     */
    @GetMapping("/sales/trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSalesTrend() {
        Map<String, Object> trend = statisticsService.getSalesTrend();
        return ResponseEntity.ok(trend);
    }
}
