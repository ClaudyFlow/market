package com.market.controller;

import com.market.common.Result;
import com.market.entity.PlatformActivity;
import com.market.service.PlatformActivityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/activity")
@CrossOrigin(origins = "*")
public class ActivityPublicController {

    @Autowired
    private PlatformActivityService platformActivityService;

    /**
     * 获取当前进行中的平台活动（用户端展示用）
     */
    @GetMapping("/active")
    public Result<List<PlatformActivity>> getActiveActivities() {
        return Result.success(platformActivityService.getActiveActivities());
    }

    /**
     * 获取活动详情
     */
    @GetMapping("/{id}")
    public Result<PlatformActivity> getById(@PathVariable Long id) {
        PlatformActivity activity = platformActivityService.getById(id);
        return activity != null ? Result.success(activity) : Result.error(404, "活动不存在");
    }
}
