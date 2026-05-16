package com.market.controller;

import com.market.common.Result;
import com.market.entity.MerchantActivityOptOut;
import com.market.entity.PlatformActivity;
import com.market.entity.User;
import com.market.service.MerchantActivityOptOutService;
import com.market.service.PlatformActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/merchant/activity")
@CrossOrigin(origins = "*")
public class MerchantActivitySettingController {

    @Autowired
    private PlatformActivityService platformActivityService;

    @Autowired
    private MerchantActivityOptOutService merchantActivityOptOutService;

    /**
     * 获取商家参与的平台活动列表（包含自己的设置）
     */
    @GetMapping
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<List<Map<String, Object>>> getMerchantActivities(@AuthenticationPrincipal User user) {
        List<PlatformActivity> allActivities = platformActivityService.getActiveActivities();
        List<Map<String, Object>> result = allActivities.stream().map(activity -> {
            MerchantActivityOptOut optOut = merchantActivityOptOutService
                    .getByMerchantAndActivity(user.getId(), activity.getId())
                    .orElse(null);
            return Map.of(
                    "activity", activity,
                    "optedOut", optOut != null && Boolean.TRUE.equals(optOut.getOptedOut()),
                    "customDiscountRate", optOut != null ? optOut.getCustomDiscountRate() : null,
                    "customDiscountAmount", optOut != null ? optOut.getCustomDiscountAmount() : null,
                    "discountType", optOut != null ? optOut.getDiscountType() : null,
                    "remark", optOut != null ? optOut.getRemark() : null
            );
        }).toList();
        return Result.success(result);
    }

    /**
     * 商家设置参与方式（金额或折扣）或退出活动
     */
    @PutMapping("/{activityId}/setting")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<MerchantActivityOptOut> setActivitySetting(
            @PathVariable Long activityId,
            @RequestBody Map<String, Object> data,
            @AuthenticationPrincipal User user) {

        PlatformActivity activity = platformActivityService.getById(activityId);
        if (activity == null) return Result.error(404, "活动不存在");

        MerchantActivityOptOut optOut = merchantActivityOptOutService
                .getByMerchantAndActivity(user.getId(), activityId)
                .orElse(new MerchantActivityOptOut());

        optOut.setMerchantId(user.getId());
        optOut.setActivityId(activityId);

        Boolean optedOut = data.get("optedOut") != null ? (Boolean) data.get("optedOut") : false;
        optOut.setOptedOut(optedOut);

        if (!optedOut) {
            String discountType = (String) data.get("discountType");
            optOut.setDiscountType(discountType);
            if ("MONEY".equals(discountType) && data.get("customDiscountAmount") != null) {
                optOut.setCustomDiscountAmount(new java.math.BigDecimal(data.get("customDiscountAmount").toString()));
            } else if ("DISCOUNT".equals(discountType) && data.get("customDiscountRate") != null) {
                optOut.setCustomDiscountRate(new java.math.BigDecimal(data.get("customDiscountRate").toString()));
            }
        }

        if (data.get("remark") != null) {
            optOut.setRemark((String) data.get("remark"));
        }

        MerchantActivityOptOut saved = merchantActivityOptOutService.saveOrUpdate(optOut);
        return Result.success(saved);
    }

    /**
     * 商家快速退出活动
     */
    @PostMapping("/{activityId}/optout")
    @PreAuthorize("hasRole('MERCHANT')")
    public Result<Void> optOut(
            @PathVariable Long activityId,
            @RequestBody Map<String, Object> data,
            @AuthenticationPrincipal User user) {

        MerchantActivityOptOut optOut = merchantActivityOptOutService
                .getByMerchantAndActivity(user.getId(), activityId)
                .orElse(new MerchantActivityOptOut());

        optOut.setMerchantId(user.getId());
        optOut.setActivityId(activityId);
        optOut.setOptedOut(true);
        if (data.get("remark") != null) {
            optOut.setRemark((String) data.get("remark"));
        }

        merchantActivityOptOutService.saveOrUpdate(optOut);
        return Result.success(null);
    }
}
