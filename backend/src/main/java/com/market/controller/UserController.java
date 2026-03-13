package com.market.controller;

import com.market.entity.User;
import com.market.entity.VipInfo;
import com.market.service.UserService;
import com.market.common.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 获取用户 VIP 信息
     */
    @GetMapping("/vip")
    public Result<VipInfo> getVipInfo(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        VipInfo vipInfo = userService.getVipInfo(user.getId());
        return Result.success(vipInfo);
    }

    /**
     * 获取用户积分信息
     */
    @GetMapping("/points")
    public Result<UserPointsInfo> getUserPoints(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        UserPointsInfo pointsInfo = userService.getUserPointsInfo(user.getId());
        return Result.success(pointsInfo);
    }

    /**
     * 获取用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        User userInfo = userService.getUserById(user.getId());
        return Result.success(userInfo);
    }

    /**
     * 用户签到
     */
    @PostMapping("/checkin")
    public Result<CheckInResult> checkIn(HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        CheckInResult result = userService.checkIn(user.getId());
        return Result.success(result);
    }

    /**
     * 使用积分
     */
    @PostMapping("/points/consume")
    public Result<Boolean> consumePoints(
            @RequestParam Integer amount,
            HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        boolean success = userService.consumePoints(user.getId(), amount);
        return Result.success(success);
    }
}
