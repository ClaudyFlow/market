package com.market.service;

import com.market.entity.User;
import com.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务服务
 */
@Service
public class ScheduledService {

    @Autowired
    private UserRepository userRepository;

    /**
     * 每天凌晨 2 点执行
     * 检查 VIP 过期时间
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void checkVipExpiration() {
        LocalDateTime now = LocalDateTime.now();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getVipExpireTime() != null && 
                user.getVipExpireTime().before(new java.util.Date()) &&
                user.getVipLevel() > 0) {
                user.setVipLevel(0);
                userRepository.save(user);
            }
        }
    }

    /**
     * 每天凌晨 3 点执行
     * 清理过期优惠券
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanExpiredCoupons() {
        // TODO: 清理过期优惠券逻辑
    }

    /**
     * 每天凌晨 4 点执行
     * 自动确认收货（发货后 15 天）
     */
    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void autoConfirmDelivery() {
        // TODO: 自动确认收货逻辑
    }

    /**
     * 每小时执行
     * 取消超时未支付订单（30 分钟）
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void cancelTimeoutOrders() {
        // TODO: 取消超时订单逻辑
    }
}
