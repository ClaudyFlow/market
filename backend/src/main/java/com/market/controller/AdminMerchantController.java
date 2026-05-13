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
 * 管理端商家控制器
 */
@RestController
@RequestMapping("/api/admin/merchant")
@CrossOrigin(origins = "*")
public class AdminMerchantController {

    @Autowired
    private UserService userService;

    /**
     * 获取商家列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getMerchantList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String merchantId,
            @RequestParam(required = false) String shopName,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<User> merchantPage = userService.getAllMerchants(merchantId, shopName, status, startDate, endDate, pageable);

        List<Map<String, Object>> merchantList = merchantPage.getContent().stream()
            .map(this::convertMerchantToMap)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", merchantList);
        response.put("total", merchantPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 获取商家详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getMerchantDetail(@PathVariable Long id) {
        User merchant = userService.getUserById(id);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }

        return Result.success(convertMerchantToMap(merchant));
    }

    /**
     * 审核商家
     */
    @PutMapping("/{id}/audit")
    public Result<User> auditMerchant(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String comment) {

        User merchant = userService.getUserById(id);
        if (merchant == null) {
            throw new RuntimeException("商家不存在");
        }

        if ("APPROVED".equals(status)) {
            merchant.setMerchantStatus("ACTIVE");
            merchant.setIsMerchant(true);
        } else if ("REJECTED".equals(status)) {
            merchant.setMerchantStatus("REJECTED");
        } else if ("PENDING".equals(status)) {
            merchant.setMerchantStatus("PENDING");
        }

        return Result.success(userService.updateUser(id, Map.of("merchantStatus", merchant.getMerchantStatus(), "isMerchant", merchant.getIsMerchant())));
    }

    /**
     * 封禁商家
     */
    @PutMapping("/{id}/ban")
    public Result<User> banMerchant(@PathVariable Long id) {
        User merchant = userService.banMerchant(id);
        return Result.success(merchant);
    }

    /**
     * 解封商家
     */
    @PutMapping("/{id}/unban")
    public Result<User> unbanMerchant(@PathVariable Long id) {
        User merchant = userService.unbanMerchant(id);
        return Result.success(merchant);
    }

    /**
     * 获取商家统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getMerchantStats() {
        Map<String, Object> stats = userService.getMerchantStats();
        return Result.success(stats);
    }

    /**
     * 转换 User 对象为 Map（商家信息）
     */
    private Map<String, Object> convertMerchantToMap(User merchant) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", merchant.getId());
        map.put("shopName", merchant.getShopName());
        map.put("ownerName", merchant.getName());
        map.put("phone", merchant.getPhone());
        map.put("email", merchant.getEmail());
        map.put("category", "综合"); // 可以根据实际情况扩展
        map.put("joinTime", merchant.getCreatedAt());
        map.put("status", merchant.getMerchantStatus());
        map.put("logo", merchant.getAvatarUrl());
        map.put("description", merchant.getShopDescription());
        map.put("isMerchant", merchant.getIsMerchant());

        return map;
    }
}
