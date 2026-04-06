package com.market.controller;

import com.market.dto.UserAddressRequest;
import com.market.dto.UserAddressResponse;
import com.market.entity.User;
import com.market.service.UserAddressService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户地址控制器
 * 提供用户收货地址的增删改查、默认地址设置、地址数量统计等功能。
 * 权限要求：需要登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/address
 */
@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = "*")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取用户地址列表
     * API路径：GET /api/address/list
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 用户地址列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserAddressResponse>> getUserAddresses(
            @AuthenticationPrincipal User user) {
        List<UserAddressResponse> addresses = userAddressService.getUserAddresses(user.getId());
        return ResponseEntity.ok(addresses);
    }

    /**
     * 获取用户地址列表（分页）
     * API路径：GET /api/address/page
     * 权限：需要登录
     *
     * @param page 页码，默认0
     * @param size 每页大小，默认10
     * @param user 当前登录用户
     * @return 分页的用户地址列表
     */
    @GetMapping("/page")
    public ResponseEntity<Page<UserAddressResponse>> getUserAddressesPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<UserAddressResponse> addresses = userAddressService.getUserAddressesPage(
                user.getId(), pageable);
        return ResponseEntity.ok(addresses);
    }

    /**
     * 获取默认地址
     * API路径：GET /api/address/default
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 默认地址
     */
    @GetMapping("/default")
    public ResponseEntity<UserAddressResponse> getDefaultAddress(
            @AuthenticationPrincipal User user) {
        UserAddressResponse address = userAddressService.getDefaultAddress(user.getId());
        return ResponseEntity.ok(address);
    }

    /**
     * 获取地址详情
     * API路径：GET /api/address/{id}
     * 权限：需要登录
     *
     * @param id 地址ID
     * @param user 当前登录用户
     * @return 地址详情
     */
    @GetMapping("/{id}")
    public ResponseEntity<UserAddressResponse> getAddressDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        UserAddressResponse address = userAddressService.getAddressDetail(user.getId(), id);
        return ResponseEntity.ok(address);
    }

    /**
     * 创建地址
     * API路径：POST /api/address
     * 权限：需要登录
     *
     * @param request 地址信息
     * @param user 当前登录用户
     * @return 创建的地址
     */
    @PostMapping
    public ResponseEntity<UserAddressResponse> createAddress(
            @Valid @RequestBody UserAddressRequest request,
            @AuthenticationPrincipal User user) {
        UserAddressResponse address = userAddressService.createAddress(user.getId(), request);
        return ResponseEntity.ok(address);
    }

    /**
     * 更新地址
     * API路径：PUT /api/address/{id}
     * 权限：需要登录
     *
     * @param id 地址ID
     * @param request 更新的地址信息
     * @param user 当前登录用户
     * @return 更新后的地址
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserAddressResponse> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody UserAddressRequest request,
            @AuthenticationPrincipal User user) {
        UserAddressResponse address = userAddressService.updateAddress(
                user.getId(), id, request);
        return ResponseEntity.ok(address);
    }

    /**
     * 设置默认地址
     * API路径：POST /api/address/{id}/default
     * 权限：需要登录
     *
     * @param id 地址ID
     * @param user 当前登录用户
     * @return 设置结果
     */
    @PostMapping("/{id}/default")
    public ResponseEntity<Map<String, Object>> setDefaultAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userAddressService.setDefaultAddress(user.getId(), id);
        result.put("success", success);
        result.put("message", "默认地址设置成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 删除地址
     * API路径：DELETE /api/address/{id}
     * 权限：需要登录
     *
     * @param id 地址ID
     * @param user 当前登录用户
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userAddressService.deleteAddress(user.getId(), id);
        result.put("success", success);
        result.put("message", "地址删除成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 获取地址数量
     * API路径：GET /api/address/count
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 地址数量
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getAddressCount(
            @AuthenticationPrincipal User user) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userAddressService.getAddressCount(user.getId()));
        return ResponseEntity.ok(result);
    }
}
