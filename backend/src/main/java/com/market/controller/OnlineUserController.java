package com.market.controller;

import com.market.common.Result;
import com.market.entity.User;
import com.market.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

/**
 * 在线用户控制器
 * 提供在线用户统计功能，使用 Redis Set 存储在线用户 ID。
 * 权限要求：需要登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/online
 */
@RestController
@RequestMapping("/api/online")
@CrossOrigin(origins = "*")
public class OnlineUserController {

    private static final String ONLINE_USERS_KEY = "online_users";
    private static final int ONLINE_EXPIRE_SECONDS = 300;

    @Autowired
    private RedisService redisService;

    /**
     * 上报在线状态
     * API路径：POST /api/online/heartbeat
     * 权限：需要登录
     */
    @PostMapping("/heartbeat")
    public Result<Void> heartbeat(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        redisService.sSetAndTime(ONLINE_USERS_KEY, ONLINE_EXPIRE_SECONDS, user.getId());
        return Result.success(null);
    }

    /**
     * 获取在线用户数量
     * API路径：GET /api/online/count
     * 权限：需要登录
     */
    @GetMapping("/count")
    public Result<Map<String, Object>> getOnlineCount() {
        Set<Object> onlineUsers = redisService.sGet(ONLINE_USERS_KEY);
        int count = onlineUsers != null ? onlineUsers.size() : 0;
        Map<String, Object> result = new java.util.HashMap<>();
        result.put("count", count);
        return Result.success(result);
    }
}