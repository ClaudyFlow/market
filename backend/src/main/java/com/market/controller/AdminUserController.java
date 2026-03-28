package com.market.controller;

import com.market.common.Result;
import com.market.entity.User;
import com.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端用户控制器
 */
@RestController
@RequestMapping("/api/admin/user")
@CrossOrigin(origins = "*")
public class AdminUserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getUserList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userPage = userService.getAllUsers(userId, userName, phone, status, startDate, endDate, pageable);

        List<Map<String, Object>> userList = userPage.getContent().stream()
            .map(this::convertUserToMap)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", userList);
        response.put("total", userPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getUserDetail(@PathVariable Long id) {
        User user = userService.getUserById(id);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }

        return Result.success(convertUserToMap(user));
    }

    /**
     * 创建用户
     */
    @PostMapping
    public Result<User> createUser(@RequestBody User user) {
        User created = userService.createUser(user);
        return Result.success(created);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    public Result<User> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {

        User updated = userService.updateUser(id, updates);
        return Result.success(updated);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success();
    }

    /**
     * 封禁用户
     */
    @PutMapping("/{id}/ban")
    public Result<User> banUser(@PathVariable Long id) {
        User user = userService.banUser(id);
        return Result.success(user);
    }

    /**
     * 解封用户
     */
    @PutMapping("/{id}/unban")
    public Result<User> unbanUser(@PathVariable Long id) {
        User user = userService.unbanUser(id);
        return Result.success(user);
    }

    /**
     * 获取用户统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = userService.getUserStats();
        return Result.success(stats);
    }

    /**
     * 转换 User 对象为 Map
     */
    private Map<String, Object> convertUserToMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("name", user.getName());
        map.put("email", user.getEmail());
        map.put("phone", user.getPhone());
        map.put("avatar", user.getAvatarUrl());
        map.put("avatarUrl", user.getAvatarUrl());
        map.put("credit", user.getCredit());
        map.put("totalCredit", user.getTotalCredit());
        map.put("growthValue", user.getGrowthValue());
        map.put("vipLevel", user.getVipLevel());
        map.put("status", user.getStatus());
        map.put("role", user.getRole());
        map.put("registerTime", user.getCreatedAt());
        map.put("lastLoginAt", user.getLastLoginAt());

        // VIP 信息
        if (user.getVipInfo() != null) {
            map.put("vipInfo", user.getVipInfo());
        }

        return map;
    }
}
