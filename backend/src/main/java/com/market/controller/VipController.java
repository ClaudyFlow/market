package com.market.controller;

import com.market.common.Result;
import com.market.entity.User;
import com.market.entity.VipGift;
import com.market.entity.VipGiftRecord;
import com.market.entity.VipLevel;
import com.market.entity.VipRechargeOrder;
import com.market.service.VipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * VIP 中心控制器
 */
@RestController
@RequestMapping("/api/user/vip")
@CrossOrigin(origins = "*")
public class VipController {

    @Autowired
    private VipService vipService;

    /**
     * 获取 VIP 等级列表
     */
    @GetMapping("/levels")
    public Result<List<Map<String, Object>>> getVipLevels() {
        List<VipLevel> levels = vipService.getAllVipLevels();
        
        List<Map<String, Object>> levelList = levels.stream()
            .map(this::convertLevelToMap)
            .collect(Collectors.toList());
        
        return Result.success(levelList);
    }

    /**
     * 获取我的 VIP 信息
     */
    @GetMapping("/my")
    public Result<Map<String, Object>> getMyVipInfo(@AuthenticationPrincipal User user) {
        Map<String, Object> info = vipService.getLevelProgress(user);
        
        // 添加用户信息
        info.put("userId", user.getId());
        info.put("userName", user.getName());
        info.put("userAvatar", user.getAvatarUrl());
        
        return Result.success(info);
    }

    /**
     * 获取 VIP 权益详情
     */
    @GetMapping("/privileges")
    public Result<Map<String, Object>> getPrivileges(@AuthenticationPrincipal User user) {
        Map<String, Object> privileges = new HashMap<>();
        
        VipLevel currentLevel = vipService.getCurrentLevel(user.getGrowthValue());
        privileges.put("currentLevel", convertLevelToMap(currentLevel));
        
        // 权益列表
        Map<String, Object> benefits = new HashMap<>();
        benefits.put("discountRate", currentLevel.getDiscountRate());
        benefits.put("dailyCredit", currentLevel.getDailyCredit());
        benefits.put("monthlyCredit", currentLevel.getMonthlyCredit());
        benefits.put("freeShippingCount", currentLevel.getFreeShippingCount());
        benefits.put("refundPriority", currentLevel.getRefundPriority());
        benefits.put("exclusiveService", currentLevel.getExclusiveService());
        
        privileges.put("benefits", benefits);
        
        return Result.success(privileges);
    }

    /**
     * 获取每日礼包列表
     */
    @GetMapping("/gifts/daily")
    public Result<List<Map<String, Object>>> getDailyGifts(@AuthenticationPrincipal User user) {
        List<VipGift> gifts = vipService.getDailyGifts(user.getVipLevel());
        
        List<Map<String, Object>> giftList = gifts.stream()
            .map(gift -> {
                Map<String, Object> map = convertGiftToMap(gift);
                map.putAll(vipService.getGiftClaimStatus(user, gift));
                return map;
            })
            .collect(Collectors.toList());
        
        return Result.success(giftList);
    }

    /**
     * 获取每月礼包列表
     */
    @GetMapping("/gifts/monthly")
    public Result<List<Map<String, Object>>> getMonthlyGifts(@AuthenticationPrincipal User user) {
        List<VipGift> gifts = vipService.getMonthlyGifts(user.getVipLevel());
        
        List<Map<String, Object>> giftList = gifts.stream()
            .map(gift -> {
                Map<String, Object> map = convertGiftToMap(gift);
                map.putAll(vipService.getGiftClaimStatus(user, gift));
                return map;
            })
            .collect(Collectors.toList());
        
        return Result.success(giftList);
    }

    /**
     * 获取所有礼包
     */
    @GetMapping("/gifts")
    public Result<List<Map<String, Object>>> getAllGifts(@AuthenticationPrincipal User user) {
        List<VipGift> gifts = vipService.getAvailableGifts(user.getVipLevel());
        
        List<Map<String, Object>> giftList = gifts.stream()
            .map(gift -> {
                Map<String, Object> map = convertGiftToMap(gift);
                map.putAll(vipService.getGiftClaimStatus(user, gift));
                return map;
            })
            .collect(Collectors.toList());
        
        return Result.success(giftList);
    }

    /**
     * 领取礼包
     */
    @PostMapping("/gifts/{id}/claim")
    public Result<Map<String, Object>> claimGift(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        
        Map<String, Object> result = vipService.claimGift(user, id);
        return Result.success(result);
    }

    /**
     * 获取礼包领取记录
     */
    @GetMapping("/gifts/records")
    public Result<List<Map<String, Object>>> getGiftRecords(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String type) {
        
        List<VipGiftRecord> records = vipService.getGiftRecords(user, type);
        
        List<Map<String, Object>> recordList = records.stream()
            .map(record -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", record.getId());
                map.put("giftName", record.getGift().getName());
                map.put("giftImage", record.getGift().getImage());
                map.put("giftType", record.getGift().getType());
                map.put("rewardType", record.getRewardType());
                map.put("rewardValue", record.getRewardValue());
                map.put("claimedAt", record.getClaimedAt());
                return map;
            })
            .collect(Collectors.toList());
        
        return Result.success(recordList);
    }

    /**
     * 创建充值订单
     */
    @PostMapping("/recharge")
    public Result<Map<String, Object>> createRechargeOrder(
            @AuthenticationPrincipal User user,
            @RequestParam BigDecimal amount) {
        
        VipRechargeOrder order = vipService.createRechargeOrder(user, amount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("orderNo", order.getOrderNo());
        result.put("amount", order.getAmount());
        result.put("growthValue", order.getGrowthValue());
        result.put("status", order.getStatus());
        result.put("createdAt", order.getCreatedAt());
        
        return Result.success(result);
    }

    /**
     * 支付充值订单
     */
    @PostMapping("/recharge/{orderNo}/pay")
    public Result<VipRechargeOrder> payRechargeOrder(
            @PathVariable String orderNo,
            @RequestParam String paymentMethod) {
        
        VipRechargeOrder order = vipService.payRechargeOrder(orderNo, paymentMethod);
        return Result.success(order);
    }

    /**
     * 获取充值记录
     */
    @GetMapping("/recharge/records")
    public Result<List<Map<String, Object>>> getRechargeRecords(@AuthenticationPrincipal User user) {
        List<VipRechargeOrder> orders = vipService.getRechargeHistory(user);
        
        List<Map<String, Object>> orderList = orders.stream()
            .map(order -> {
                Map<String, Object> map = new HashMap<>();
                map.put("orderNo", order.getOrderNo());
                map.put("amount", order.getAmount());
                map.put("growthValue", order.getGrowthValue());
                map.put("status", order.getStatus());
                map.put("paymentMethod", order.getPaymentMethod());
                map.put("paidAt", order.getPaidAt());
                map.put("createdAt", order.getCreatedAt());
                return map;
            })
            .collect(Collectors.toList());
        
        return Result.success(orderList);
    }

    /**
     * 获取充值统计
     */
    @GetMapping("/recharge/stats")
    public Result<Map<String, Object>> getRechargeStats(@AuthenticationPrincipal User user) {
        Map<String, Object> stats = vipService.getRechargeStats(user);
        return Result.success(stats);
    }

    /**
     * 转换 VIP 等级为 Map
     */
    private Map<String, Object> convertLevelToMap(VipLevel level) {
        Map<String, Object> map = new HashMap<>();
        map.put("level", level.getLevel());
        map.put("name", level.getName());
        map.put("icon", level.getIcon());
        map.put("growthValueRequired", level.getGrowthValueRequired());
        map.put("discountRate", level.getDiscountRate());
        map.put("dailyCredit", level.getDailyCredit());
        map.put("monthlyCredit", level.getMonthlyCredit());
        map.put("freeShippingCount", level.getFreeShippingCount());
        map.put("refundPriority", level.getRefundPriority());
        map.put("exclusiveService", level.getExclusiveService());
        map.put("description", level.getDescription());
        map.put("backgroundColor", level.getBackgroundColor());
        map.put("textColor", level.getTextColor());
        return map;
    }

    /**
     * 转换礼包为 Map
     */
    private Map<String, Object> convertGiftToMap(VipGift gift) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", gift.getId());
        map.put("name", gift.getName());
        map.put("type", gift.getType());
        map.put("vipLevelRequired", gift.getVipLevelRequired());
        map.put("creditReward", gift.getCreditReward());
        map.put("claimType", gift.getClaimType());
        map.put("claimIntervalHours", gift.getClaimIntervalHours());
        map.put("description", gift.getDescription());
        map.put("image", gift.getImage());
        map.put("status", gift.getStatus());
        return map;
    }
}
