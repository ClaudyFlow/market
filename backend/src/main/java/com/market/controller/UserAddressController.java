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
 *
 * @author Market Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/address")
@CrossOrigin(origins = "*")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     * 获取用户地址列表
     */
    @GetMapping("/list")
    public ResponseEntity<List<UserAddressResponse>> getUserAddresses(
            @AuthenticationPrincipal User user) {
        List<UserAddressResponse> addresses = userAddressService.getUserAddresses(user.getId());
        return ResponseEntity.ok(addresses);
    }

    /**
     * 获取用户地址列表（分页）
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
     */
    @GetMapping("/default")
    public ResponseEntity<UserAddressResponse> getDefaultAddress(
            @AuthenticationPrincipal User user) {
        UserAddressResponse address = userAddressService.getDefaultAddress(user.getId());
        return ResponseEntity.ok(address);
    }

    /**
     * 获取地址详情
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
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getAddressCount(
            @AuthenticationPrincipal User user) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userAddressService.getAddressCount(user.getId()));
        return ResponseEntity.ok(result);
    }
}
