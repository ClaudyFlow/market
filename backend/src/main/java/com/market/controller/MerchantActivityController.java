package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.Activity;
import com.market.entity.User;
import com.market.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动控制器
 * 提供活动的 CRUD、发布、暂停、结束等商家端管理功能。
 * 权限要求：需要商家角色
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/merchant/activity
 */
@RestController
@RequestMapping("/api/merchant/activity")
@CrossOrigin(origins = "*")
public class MerchantActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 创建活动
     * API路径：POST /api/merchant/activity
     * 权限：需要商家角色
     *
     * @param activity 活动信息
     * @param user 当前登录商家
     * @return 创建的活动
     */
    @PostMapping
    @Idempotent(key = "'create_activity_' + #user.id", expire = 3600)
    @AuditLog(module = "活动管理", action = "创建活动", recordParams = true)
    public Result<Activity> createActivity(
            @RequestBody Activity activity,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        activity.setMerchantId(user.getId());
        try {
            Activity created = activityService.createActivity(activity);
            return Result.success(created);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 更新活动
     * API路径：PUT /api/merchant/activity/{id}
     * 权限：需要商家角色
     *
     * @param id 活动ID
     * @param activity 活动信息
     * @param user 当前登录商家
     * @return 更新后的活动
     */
    @PutMapping("/{id}")
    @AuditLog(module = "活动管理", action = "更新活动", recordParams = true)
    public Result<Activity> updateActivity(
            @PathVariable Long id,
            @RequestBody Activity activity,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            Activity updated = activityService.updateActivity(id, activity);
            return Result.success(updated);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 删除活动
     * API路径：DELETE /api/merchant/activity/{id}
     * 权限：需要商家角色
     *
     * @param id 活动ID
     * @param user 当前登录商家
     * @return 操作结果
     */
    @DeleteMapping("/{id}")
    @AuditLog(module = "活动管理", action = "删除活动")
    public Result<Void> deleteActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            activityService.deleteActivity(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取活动详情
     * API路径：GET /api/merchant/activity/{id}
     * 权限：需要商家角色
     *
     * @param id 活动ID
     * @param user 当前登录商家
     * @return 活动详情
     */
    @GetMapping("/{id}")
    public Result<Activity> getActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return Result.error(404, "活动不存在");
        }
        return Result.success(activity);
    }

    /**
     * 获取商家活动列表
     * API路径：GET /api/merchant/activity
     * 权限：需要商家角色
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param status 状态筛选（可选）
     * @param type 类型筛选（可选）
     * @param user 当前登录商家
     * @return 分页的活动列表
     */
    @GetMapping
    @Cacheable(key = "'merchant_activities_' + #user.id + '_' + #page + '_' + #status", cacheName = "activities", expire = 300)
    @AuditLog(module = "活动管理", action = "查询活动列表")
    public Result<Map<String, Object>> getActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String type,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Activity> activityPage;
        if (status != null && !status.isEmpty()) {
            activityPage = activityService.getActivitiesByStatus(status, pageable);
        } else if (type != null && !type.isEmpty()) {
            activityPage = activityService.getActivitiesByType(type, pageable);
        } else {
            activityPage = activityService.getActivitiesByMerchant(user.getId(), pageable);
        }

        List<Map<String, Object>> list = activityPage.getContent().stream()
            .map(this::convertActivityToMap)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", activityPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 发布活动
     * API路径：POST /api/merchant/activity/{id}/publish
     * 权限：需要商家角色
     *
     * @param id 活动ID
     * @param user 当前登录商家
     * @return 发布后的活动
     */
    @PostMapping("/{id}/publish")
    @AuditLog(module = "活动管理", action = "发布活动")
    public Result<Activity> publishActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            Activity activity = activityService.publishActivity(id);
            return Result.success(activity);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 暂停活动
     * API路径：POST /api/merchant/activity/{id}/pause
     * 权限：需要商家角色
     *
     * @param id 活动ID
     * @param user 当前登录商家
     * @return 暂停后的活动
     */
    @PostMapping("/{id}/pause")
    @AuditLog(module = "活动管理", action = "暂停活动")
    public Result<Activity> pauseActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            Activity activity = activityService.pauseActivity(id);
            return Result.success(activity);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 结束活动
     * API路径：POST /api/merchant/activity/{id}/end
     * 权限：需要商家角色
     *
     * @param id 活动ID
     * @param user 当前登录商家
     * @return 结束后的活动
     */
    @PostMapping("/{id}/end")
    @AuditLog(module = "活动管理", action = "结束活动")
    public Result<Activity> endActivity(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            Activity activity = activityService.endActivity(id);
            return Result.success(activity);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    private Map<String, Object> convertActivityToMap(Activity activity) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", activity.getId());
        map.put("name", activity.getName());
        map.put("description", activity.getDescription());
        map.put("type", activity.getType());
        map.put("image", activity.getImage());
        map.put("startTime", activity.getStartTime());
        map.put("endTime", activity.getEndTime());
        map.put("status", activity.getStatus());
        map.put("discount", activity.getDiscount());
        map.put("discountType", activity.getDiscountType());
        map.put("maxQuantity", activity.getMaxQuantity());
        map.put("usedQuantity", activity.getUsedQuantity());
        map.put("maxPerUser", activity.getMaxPerUser());
        map.put("merchantId", activity.getMerchantId());
        map.put("productId", activity.getProductId());
        map.put("tags", activity.getTags());
        map.put("sortOrder", activity.getSortOrder());
        map.put("createdAt", activity.getCreatedAt());
        return map;
    }
}