package com.market.controller;

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
 */
@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取平台统计信息（管理员）
     */
    @GetMapping("/platform")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getPlatformStats() {
        Map<String, Object> stats = statisticsService.getPlatformStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取订单统计（管理员）
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
     */
    @GetMapping("/products")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getProductStats() {
        Map<String, Object> stats = statisticsService.getProductStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取用户统计（管理员）
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = statisticsService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取论坛统计（管理员）
     */
    @GetMapping("/forum")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getForumStats() {
        Map<String, Object> stats = statisticsService.getForumStats();
        return ResponseEntity.ok(stats);
    }

    /**
     * 获取销售趋势（管理员）
     */
    @GetMapping("/sales/trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getSalesTrend() {
        Map<String, Object> trend = statisticsService.getSalesTrend();
        return ResponseEntity.ok(trend);
    }
}
