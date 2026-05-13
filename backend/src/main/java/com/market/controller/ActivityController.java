package com.market.controller;

import com.market.annotation.AuditLog;
import com.market.annotation.Cacheable;
import com.market.common.Result;
import com.market.entity.Activity;
import com.market.service.ActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 活动控制器（用户端）
 * 提供活动查询、列表、详情等公开接口。
 * 权限要求：公开接口
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/activity
 */
@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*")
public class ActivityController {

    @Autowired
    private ActivityService activityService;

    /**
     * 获取活动列表（用户端）
     * API路径：GET /api/activity
     * 权限：公开
     *
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @param type 类型筛选（可选）
     * @return 分页的活动列表
     */
    @GetMapping
    @Cacheable(key = "'activity_list_' + #page + '_' + #size + '_' + #type", cacheName = "activities", expire = 300)
    @AuditLog(module = "活动管理", action = "查询活动列表")
    public Result<Map<String, Object>> getActivities(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String type) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "sortOrder"));
        Page<Activity> activityPage = activityService.getActivitiesByStatus("ACTIVE", pageable);

        if (type != null && !type.isEmpty()) {
            activityPage = activityService.getActivitiesByType(type, pageable);
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
     * 获取进行中的活动
     * API路径：GET /api/activity/active
     * 权限：公开
     *
     * @return 进行中的活动列表
     */
    @GetMapping("/active")
    @Cacheable(key = "'active_activities'", cacheName = "activities", expire = 300)
    @AuditLog(module = "活动管理", action = "查询进行中活动")
    public Result<List<Map<String, Object>>> getActiveActivities() {
        List<Activity> activities = activityService.getActiveActivities();
        List<Map<String, Object>> list = activities.stream()
            .map(this::convertActivityToMap)
            .collect(Collectors.toList());
        return Result.success(list);
    }

    /**
     * 获取活动详情
     * API路径：GET /api/activity/{id}
     * 权限：公开
     *
     * @param id 活动ID
     * @return 活动详情
     */
    @GetMapping("/{id}")
    @Cacheable(key = "'activity_detail_' + #id", cacheName = "activities", expire = 600)
    @AuditLog(module = "活动管理", action = "查询活动详情")
    public Result<Activity> getActivityDetail(@PathVariable Long id) {
        Activity activity = activityService.getActivityById(id);
        if (activity == null) {
            return Result.error(404, "活动不存在");
        }
        return Result.success(activity);
    }

    /**
     * 获取商品关联的活动
     * API路径：GET /api/activity/product/{productId}
     * 权限：公开
     *
     * @param productId 商品ID
     * @return 商品关联的活动列表
     */
    @GetMapping("/product/{productId}")
    @Cacheable(key = "'product_activities_' + #productId", cacheName = "activities", expire = 300)
    @AuditLog(module = "活动管理", action = "查询商品活动")
    public Result<List<Map<String, Object>>> getProductActivities(@PathVariable Long productId) {
        List<Activity> activities = activityService.getProductActivities(productId);
        List<Map<String, Object>> list = activities.stream()
            .map(this::convertActivityToMap)
            .collect(Collectors.toList());
        return Result.success(list);
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
        map.put("productId", activity.getProductId());
        map.put("tags", activity.getTags());
        return map;
    }
}