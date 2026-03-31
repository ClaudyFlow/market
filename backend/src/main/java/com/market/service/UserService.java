package com.market.service;

import com.market.dto.AuthResponse;
import com.market.dto.LoginRequest;
import com.market.dto.RegisterRequest;
import com.market.dto.MerchantRegisterRequest;
import com.market.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

/**
 * 用户服务接口
 */
public interface UserService {

    /**
     * 用户注册
     */
    AuthResponse register(RegisterRequest request);

    /**
     * 商家注册
     */
    AuthResponse registerMerchant(MerchantRegisterRequest request);

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
    UserCreditInfo getUserCreditInfo(Long userId);

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
    boolean consumeCredit(Long userId, Integer amount);

    /**
     * 添加积分
     */
    boolean addCredit(Long userId, Integer amount);

    /**
     * 获取所有用户（分页）
     */
    Page<User> getAllUsers(String userId, String userName, String phone, String status,
                           LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 创建用户
     */
    User createUser(User user);

    /**
     * 更新用户
     */
    User updateUser(Long id, Map<String, Object> updates);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 封禁用户
     */
    User banUser(Long id);

    /**
     * 解封用户
     */
    User unbanUser(Long id);

    /**
     * 获取用户统计
     */
    Map<String, Object> getUserStats();

    /**
     * 获取所有商家（分页）
     */
    Page<User> getAllMerchants(String merchantId, String shopName, String status,
                               LocalDateTime startDate, LocalDateTime endDate, Pageable pageable);

    /**
     * 封禁商家
     */
    User banMerchant(Long id);

    /**
     * 解封商家
     */
    User unbanMerchant(Long id);

    /**
     * 获取商家统计
     */
    Map<String, Object> getMerchantStats();

    /**
     * 更新商家信息
     */
    void updateMerchant(User merchant);

    /**
     * 获取商家店铺统计
     */
    Map<String, Object> getMerchantShopStats(User merchant);
}
