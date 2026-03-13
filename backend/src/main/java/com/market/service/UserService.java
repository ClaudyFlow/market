package com.market.service;

import com.market.entity.*;

/**
 * 用户服务接口
 */
public interface UserService {
    
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
