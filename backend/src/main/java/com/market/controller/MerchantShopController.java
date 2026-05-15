package com.market.controller;

import com.market.common.Result;
import com.market.entity.User;
import com.market.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 商家端店铺管理控制器
 */
@RestController
@RequestMapping("/api/merchant/shop")
@CrossOrigin(origins = "*")
public class MerchantShopController {

    @Autowired
    private UserService userService;

    /**
     * 获取店铺信息
     */
    @GetMapping("/info")
    public Result<Map<String, Object>> getShopInfo(@AuthenticationPrincipal User merchant) {
        Map<String, Object> shopInfo = new HashMap<>();
        shopInfo.put("id", merchant.getId());
        shopInfo.put("name", merchant.getShopName());
        shopInfo.put("description", merchant.getShopDescription());
        shopInfo.put("logo", merchant.getAvatarUrl());
        shopInfo.put("isOpen", "ACTIVE".equals(merchant.getMerchantStatus()));
        shopInfo.put("level", getMerchantLevel(merchant));
        shopInfo.put("phone", merchant.getPhone());
        shopInfo.put("email", merchant.getEmail());
        shopInfo.put("status", merchant.getMerchantStatus());
        return Result.success(shopInfo);
    }

    /**
     * 更新店铺信息
     */
    @PutMapping("/info")
    public Result<Map<String, Object>> updateShopInfo(
            @AuthenticationPrincipal User merchant,
            @RequestBody Map<String, Object> updates) {

        if (updates.containsKey("name")) {
            merchant.setShopName((String) updates.get("name"));
        }
        if (updates.containsKey("description")) {
            merchant.setShopDescription((String) updates.get("description"));
        }
        if (updates.containsKey("logo")) {
            merchant.setAvatarUrl((String) updates.get("logo"));
        }
        if (updates.containsKey("phone")) {
            merchant.setPhone((String) updates.get("phone"));
        }
        if (updates.containsKey("email")) {
            merchant.setEmail((String) updates.get("email"));
        }

        userService.updateMerchant(merchant);

        Map<String, Object> shopInfo = new HashMap<>();
        shopInfo.put("id", merchant.getId());
        shopInfo.put("name", merchant.getShopName());
        shopInfo.put("description", merchant.getShopDescription());
        shopInfo.put("logo", merchant.getAvatarUrl());
        shopInfo.put("phone", merchant.getPhone());
        shopInfo.put("email", merchant.getEmail());
        return Result.success(shopInfo);
    }

    /**
     * 获取店铺统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getShopStats(@AuthenticationPrincipal User merchant) {
        Map<String, Object> stats = userService.getMerchantShopStats(merchant);
        return Result.success(stats);
    }

    /**
     * 开关店铺
     */
    @PutMapping("/status")
    public Result<Void> toggleShopStatus(
            @AuthenticationPrincipal User merchant,
            @RequestParam Boolean isOpen) {

        merchant.setMerchantStatus(isOpen ? "ACTIVE" : "INACTIVE");
        userService.updateMerchant(merchant);
        return Result.success();
    }

    /**
     * 获取商家等级
     */
    private String getMerchantLevel(User merchant) {
        // 根据商户的订单量、评分等计算等级
        // 简化实现，实际应该根据业务逻辑计算
        return "普通商家";
    }
}
