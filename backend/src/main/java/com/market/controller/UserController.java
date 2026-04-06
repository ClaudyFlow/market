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
 * 提供用户信息管理、签到、积分消费等用户端接口，以及用户/商家列表、审核、封禁等管理员接口。
 * 权限要求：用户端需要登录，管理员端需要 ADMIN 角色
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/user
 */
@RestController
@RequestMapping("/api/user")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取当前用户信息
     * API路径：GET /api/user/info
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 用户基本信息
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
     * API路径：GET /api/user/vip
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return VIP 信息
     */
    @GetMapping("/vip")
    public ResponseEntity<VipInfo> getVipInfo(@AuthenticationPrincipal User user) {
        VipInfo vipInfo = userService.getVipInfo(user.getId());
        return ResponseEntity.ok(vipInfo);
    }

    /**
     * 获取用户积分信息
     * API路径：GET /api/user/credit
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 积分信息
     */
    @GetMapping("/credit")
    public ResponseEntity<UserCreditInfo> getUserCredit(@AuthenticationPrincipal User user) {
        UserCreditInfo creditInfo = userService.getUserCreditInfo(user.getId());
        return ResponseEntity.ok(creditInfo);
    }

    /**
     * 用户签到
     * API路径：POST /api/user/checkin
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 签到结果
     */
    @PostMapping("/checkin")
    public ResponseEntity<CheckInResult> checkIn(@AuthenticationPrincipal User user) {
        CheckInResult result = userService.checkIn(user.getId());
        return ResponseEntity.ok(result);
    }

    /**
     * 使用积分
     * API路径：POST /api/user/credit/consume
     * 权限：需要登录
     *
     * @param amount 消费积分数
     * @param user 当前登录用户
     * @return 消费结果
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
     * API路径：GET /api/user/list
     * 权限：需要 ADMIN 角色
     *
     * @param userId 用户ID筛选（可选）
     * @param userName 用户名筛选（可选）
     * @param phone 手机号筛选（可选）
     * @param status 状态筛选（可选）
     * @param startDate 开始日期筛选（可选）
     * @param endDate 结束日期筛选（可选）
     * @param page 页码，默认0
     * @param size 每页大小，默认10
     * @return 分页的用户列表
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
     * API路径：GET /api/user/{id}
     * 权限：需要 ADMIN 角色
     *
     * @param id 用户ID
     * @return 用户详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserDetail(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 创建用户
     * API路径：POST /api/user
     * 权限：需要 ADMIN 角色
     *
     * @param user 用户信息
     * @return 创建的用户
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> createUser(@Valid @RequestBody User user) {
        User createdUser = userService.createUser(user);
        return ResponseEntity.ok(createdUser);
    }

    /**
     * 更新用户
     * API路径：PUT /api/user/{id}
     * 权限：需要 ADMIN 角色
     *
     * @param id 用户ID
     * @param updates 更新字段映射
     * @return 更新后的用户
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
     * API路径：DELETE /api/user/{id}
     * 权限：需要 ADMIN 角色
     *
     * @param id 用户ID
     * @return 删除结果
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
     * API路径：POST /api/user/{id}/ban
     * 权限：需要 ADMIN 角色
     *
     * @param id 用户ID
     * @return 封禁后的用户
     */
    @PostMapping("/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> banUser(@PathVariable Long id) {
        User user = userService.banUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 解封用户
     * API路径：POST /api/user/{id}/unban
     * 权限：需要 ADMIN 角色
     *
     * @param id 用户ID
     * @return 解封后的用户
     */
    @PostMapping("/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> unbanUser(@PathVariable Long id) {
        User user = userService.unbanUser(id);
        return ResponseEntity.ok(user);
    }

    /**
     * 获取用户统计
     * API路径：GET /api/user/stats
     * 权限：需要 ADMIN 角色
     *
     * @return 用户统计数据
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
     * API路径：GET /api/user/merchant/list
     * 权限：需要 ADMIN 角色
     *
     * @param merchantId 商家ID筛选（可选）
     * @param shopName 店铺名称筛选（可选）
     * @param status 状态筛选（可选）
     * @param startDate 开始日期筛选（可选）
     * @param endDate 结束日期筛选（可选）
     * @param page 页码，默认0
     * @param size 每页大小，默认10
     * @return 分页的商家列表
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
     * API路径：POST /api/user/merchant/{id}/ban
     * 权限：需要 ADMIN 角色
     *
     * @param id 商家ID
     * @return 封禁后的商家
     */
    @PostMapping("/merchant/{id}/ban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> banMerchant(@PathVariable Long id) {
        User merchant = userService.banMerchant(id);
        return ResponseEntity.ok(merchant);
    }

    /**
     * 解封商家
     * API路径：POST /api/user/merchant/{id}/unban
     * 权限：需要 ADMIN 角色
     *
     * @param id 商家ID
     * @return 解封后的商家
     */
    @PostMapping("/merchant/{id}/unban")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> unbanMerchant(@PathVariable Long id) {
        User merchant = userService.unbanMerchant(id);
        return ResponseEntity.ok(merchant);
    }

    /**
     * 获取商家统计
     * API路径：GET /api/user/merchant/stats
     * 权限：需要 ADMIN 角色
     *
     * @return 商家统计数据
     */
    @GetMapping("/merchant/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getMerchantStats() {
        Map<String, Object> stats = userService.getMerchantStats();
        return ResponseEntity.ok(stats);
    }
}
