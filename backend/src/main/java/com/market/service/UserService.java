package com.market.service;

import com.market.dto.AuthResponse;
import com.market.dto.LoginRequest;
import com.market.dto.RegisterRequest;
import com.market.entity.*;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest request);

    /**
     * 用户登录
     */
    AuthResponse login(LoginRequest request);

    /**
     * 获取用户 VIP 信息
     */
    VipInfo getVipInfo(Long userId);

    /**
     * 获取用户积分信息
     */
    UserPointsInfo getUserPointsInfo(Long userId);

    /**
     * 获取用户信息
     */
    User getUserById(Long userId);

    /**
     * 用户签到
     */
    CheckInResult checkIn(Long userId);

    /**
     * 使用积分
     */
    boolean consumePoints(Long userId, Integer amount);

    /**
     * 添加积分
     */
    boolean addPoints(Long userId, Integer amount);
}
