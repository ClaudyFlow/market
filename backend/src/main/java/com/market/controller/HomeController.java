package com.market.controller;

import com.market.common.Result;
import com.market.entity.User;
import com.market.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 首页数据统计控制器
 */
@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "*")
public class HomeController {

    @Autowired
    private StatisticsService statisticsService;

    /**
     * 获取首页数据
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getHomeStats() {
        Map<String, Object> stats = new HashMap<>();

        // 获取平台统计
        Map<String, Object> platformStats = statisticsService.getPlatformStats();
        stats.putAll(platformStats);

        return Result.success(stats);
    }

    /**
     * 获取用户个人统计
     */
    @GetMapping("/user/stats")
    public Result<Map<String, Object>> getUserStats(@AuthenticationPrincipal User user) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("userId", user.getId());
        stats.put("userName", user.getName());
        stats.put("credit", user.getCredit());
        stats.put("vipLevel", user.getVipLevel());
        stats.put("growthValue", user.getGrowthValue());

        return Result.success(stats);
    }
}
