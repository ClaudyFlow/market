package com.market.controller;

import com.market.common.Result;
import com.market.entity.PlatformActivity;
import com.market.service.PlatformActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/activity")
@CrossOrigin(origins = "*")
public class AdminPlatformActivityController {

    @Autowired
    private PlatformActivityService platformActivityService;

    /**
     * 获取所有平台活动
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<PlatformActivity>> getAllActivities() {
        return Result.success(platformActivityService.getAllActivities());
    }

    /**
     * 获取进行中的活动
     */
    @GetMapping("/active")
    public Result<List<PlatformActivity>> getActiveActivities() {
        return Result.success(platformActivityService.getActiveActivities());
    }

    /**
     * 按状态获取活动
     */
    @GetMapping("/status/{status}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<PlatformActivity>> getByStatus(@PathVariable String status) {
        return Result.success(platformActivityService.getActivitiesByStatus(status));
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public Result<PlatformActivity> getById(@PathVariable Long id) {
        PlatformActivity activity = platformActivityService.getById(id);
        return activity != null ? Result.success(activity) : Result.error(404, "活动不存在");
    }

    /**
     * 创建平台活动（管理端只能设置折扣率）
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PlatformActivity> create(@RequestBody PlatformActivity activity) {
        // 管理端强制折扣类型
        activity.setType("DISCOUNT");
        PlatformActivity created = platformActivityService.create(activity);
        return Result.success(created);
    }

    /**
     * 更新平台活动
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PlatformActivity> update(@PathVariable Long id, @RequestBody PlatformActivity updates) {
        PlatformActivity updated = platformActivityService.update(id, updates);
        return updated != null ? Result.success(updated) : Result.error(404, "活动不存在");
    }

    /**
     * 发布活动
     */
    @PutMapping("/{id}/publish")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PlatformActivity> publish(@PathVariable Long id) {
        PlatformActivity activity = platformActivityService.getById(id);
        if (activity == null) return Result.error(404, "活动不存在");
        activity.setStatus("ACTIVE");
        PlatformActivity updated = platformActivityService.update(id, activity);
        return Result.success(updated);
    }

    /**
     * 暂停活动
     */
    @PutMapping("/{id}/pause")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PlatformActivity> pause(@PathVariable Long id) {
        PlatformActivity activity = platformActivityService.getById(id);
        if (activity == null) return Result.error(404, "活动不存在");
        activity.setStatus("PAUSED");
        PlatformActivity updated = platformActivityService.update(id, activity);
        return Result.success(updated);
    }

    /**
     * 结束活动
     */
    @PutMapping("/{id}/end")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<PlatformActivity> end(@PathVariable Long id) {
        PlatformActivity activity = platformActivityService.getById(id);
        if (activity == null) return Result.error(404, "活动不存在");
        activity.setStatus("ENDED");
        PlatformActivity updated = platformActivityService.update(id, activity);
        return Result.success(updated);
    }

    /**
     * 删除活动
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> delete(@PathVariable Long id) {
        platformActivityService.delete(id);
        return Result.success(null);
    }
}
