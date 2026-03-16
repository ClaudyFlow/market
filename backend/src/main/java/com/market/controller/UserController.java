package com.market.controller;

import com.market.entity.CheckInResult;
import com.market.entity.User;
import com.market.entity.UserPointsInfo;
import com.market.entity.VipInfo;
import com.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public ResponseEntity<Map<String, Object>> getUserInfo(@AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("id", user.getId());
        result.put("name", user.getName());
        result.put("email", user.getEmail());
        result.put("avatarUrl", user.getAvatarUrl());
        result.put("points", user.getPoints());
        result.put("vipLevel", user.getVipLevel());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取用户 VIP 信息
     */
    @GetMapping("/vip")
    public ResponseEntity<VipInfo> getVipInfo(@AuthenticationPrincipal User user) {
        VipInfo vipInfo = userService.getVipInfo(user.getId());
        return ResponseEntity.ok(vipInfo);
    }

    /**
     * 获取用户积分信息
     */
    @GetMapping("/credit")
    public ResponseEntity<UserPointsInfo> getUserPoints(@AuthenticationPrincipal User user) {
        UserPointsInfo pointsInfo = userService.getUserPointsInfo(user.getId());
        return ResponseEntity.ok(pointsInfo);
    }

    /**
     * 用户签到
     */
    @PostMapping("/checkin")
    public ResponseEntity<CheckInResult> checkIn(@AuthenticationPrincipal User user) {
        CheckInResult result = userService.checkIn(user.getId());
        return ResponseEntity.ok(result);
    }

    /**
     * 使用积分
     */
    @PostMapping("/credit/consume")
    public ResponseEntity<Boolean> consumePoints(
            @RequestParam Integer amount,
            @AuthenticationPrincipal User user) {
        boolean success = userService.consumePoints(user.getId(), amount);
        return ResponseEntity.ok(success);
    }
}
