package com.market.service;

import com.market.entity.Order;
import com.market.entity.OrderItem;
import com.market.entity.Product;
import com.market.entity.UserCoupon;
import com.market.repository.OrderRepository;
import com.market.repository.ProductRepository;
import com.market.repository.UserCouponRepository;
import com.market.entity.User;
import com.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 定时任务服务
 */
@Service
public class ScheduledService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserCouponRepository userCouponRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * 每天凌晨 2 点执行
     * 检查 VIP 过期时间
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void checkVipExpiration() {
        LocalDateTime now = LocalDateTime.now();
        Date nowDate = new Date();
        List<User> users = userRepository.findAll();

        for (User user : users) {
            if (user.getVipExpireTime() != null &&
                user.getVipExpireTime().before(nowDate) &&
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
        LocalDateTime now = LocalDateTime.now();
        Pageable pageable = PageRequest.of(0, 1000);
        
        while (true) {
            Page<UserCoupon> page = userCouponRepository.findAll(pageable);
            if (page.getContent().isEmpty()) {
                break;
            }

            List<UserCoupon> expiredCoupons = page.getContent().stream()
                .filter(coupon -> !"USED".equals(coupon.getStatus()) && !"EXPIRED".equals(coupon.getStatus()))
                .filter(coupon -> {
                    LocalDateTime validTo = coupon.getCoupon().getValidTo();
                    return validTo != null && validTo.isBefore(now);
                })
                .toList();

            for (UserCoupon coupon : expiredCoupons) {
                coupon.setStatus("EXPIRED");
                userCouponRepository.save(coupon);
            }

            if (!page.hasNext()) {
                break;
            }
            pageable = page.nextPageable();
        }
    }

    /**
     * 每天凌晨 4 点执行
     * 自动确认收货（发货后 15 天）
     */
    @Scheduled(cron = "0 0 4 * * ?")
    @Transactional
    public void autoConfirmDelivery() {
        LocalDateTime fifteenDaysAgo = LocalDateTime.now().minusDays(15);
        Pageable pageable = PageRequest.of(0, 1000);

        while (true) {
            Page<Order> page = orderRepository.findByStatus("SHIPPED", pageable);
            if (page.getContent().isEmpty()) {
                break;
            }

            List<Order> toConfirm = page.getContent().stream()
                .filter(order -> order.getShippedAt() != null && order.getShippedAt().isBefore(fifteenDaysAgo))
                .toList();

            for (Order order : toConfirm) {
                order.setStatus("COMPLETED");
                order.setCompletedAt(LocalDateTime.now());
                orderRepository.save(order);

                // 恢复库存（如果需要）
                for (OrderItem item : order.getItem()) {
                    Product product = item.getProduct();
                    // 注意：这里根据业务需求决定是否需要减少已售库存等
                }
            }

            if (!page.hasNext()) {
                break;
            }
            pageable = page.nextPageable();
        }
    }

    /**
     * 每小时执行
     * 取消超时未支付订单（30 分钟）
     */
    @Scheduled(cron = "0 0 * * * ?")
    @Transactional
    public void cancelTimeoutOrders() {
        LocalDateTime thirtyMinutesAgo = LocalDateTime.now().minusMinutes(30);
        Pageable pageable = PageRequest.of(0, 1000);

        while (true) {
            Page<Order> page = orderRepository.findByStatus("PENDING", pageable);
            if (page.getContent().isEmpty()) {
                break;
            }

            List<Order> toCancel = page.getContent().stream()
                .filter(order -> order.getCreatedAt() != null && order.getCreatedAt().isBefore(thirtyMinutesAgo))
                .toList();

            for (Order order : toCancel) {
                order.setStatus("CANCELLED");
                orderRepository.save(order);

                // 恢复库存
                for (OrderItem item : order.getItem()) {
                    Product product = item.getProduct();
                    product.setStock(product.getStock() + item.getQuantity());
                    productRepository.save(product);
                }
            }

            if (!page.hasNext()) {
                break;
            }
            pageable = page.nextPageable();
        }
    }
}
