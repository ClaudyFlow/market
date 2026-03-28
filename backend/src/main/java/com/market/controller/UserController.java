package com.market.controller;

import com.market.entity.CheckInResult;
import com.market.entity.User;
import com.market.entity.UserCreditInfo;
import com.market.entity.VipInfo;
import com.market.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 用户控制器
 * <p>
 * 提供用户管理相关的 API 接口，包括用户信息管理、用户列表、用户审核等功能。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
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
        result.put("credit", user.getCredit());
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
    public ResponseEntity<UserCreditInfo> getUserCredit(@AuthenticationPrincipal User user) {
        UserCreditInfo creditInfo = userService.getUserCreditInfo(user.getId());
        return ResponseEntity.ok(creditInfo);
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
    public ResponseEntity<Boolean> consumeCredit(
            @RequestParam Integer amount,
            @AuthenticationPrincipal User user) {
        boolean success = userService.consumeCredit(user.getId(), amount);
        return ResponseEntity.ok(success);
    }

    // ==================== 用户管理（管理员） ====================

    /**
     * 获取用户列表（分页）
     */
    @GetMapping("/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getUserList(
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String userName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> userList = userService.getAllUsers(userId, userName, phone, status,
                startDate, endDate, pageable);
        return ResponseEntity.ok(userList);
    }

    /**
     * 获取用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserDetail(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 创建用户
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * 更新用户
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(
            @PathVariable Long id,
            @RequestBody Map<String, Object> updates) {
        User updatedUser = userService.updateUser(id, updates);
        return ResponseEntity.ok(updatedUser);
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        userService.deleteUser(id);
        result.put("success", true);
        result.put("message", "用户删除成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 封禁用户
     */
    @PostMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> banUser(@PathVariable Long id) {
        User user = userService.banUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 解封用户
     */
    @PostMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> unbanUser(@PathVariable Long id) {
        User user = userService.unbanUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 获取用户统计
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserStats() {
        Map<String, Object> stats = userService.getUserStats();
        return ResponseEntity.ok(stats);
    }

    // ==================== 商家管理（管理员） ====================

    /**
     * 获取商家列表（分页）
     */
    @GetMapping("/merchant/list")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getMerchantList(
            @RequestParam(required = false) String merchantId,
            @RequestParam(required = false) String shopName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> merchantList = userService.getAllMerchants(merchantId, shopName, status,
                startDate, endDate, pageable);
        return ResponseEntity.ok(merchantList);
    }

    /**
     * 封禁商家
     */
    @PostMapping("/merchant/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> banMerchant(@PathVariable Long id) {
        User merchant = userService.banMerchant(id);
        return ResponseEntity.ok(merchant);
    }

    /**
     * 解封商家
     */
    @PostMapping("/merchant/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> unbanMerchant(@PathVariable Long id) {
        User merchant = userService.unbanMerchant(id);
        return ResponseEntity.ok(merchant);
    }

    /**
     * 获取商家统计
     */
    @GetMapping("/merchant/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMerchantStats() {
        Map<String, Object> stats = userService.getMerchantStats();
        return ResponseEntity.ok(stats);
    }
}
