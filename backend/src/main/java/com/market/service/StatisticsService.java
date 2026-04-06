package com.market.service;

import com.market.entity.*;
import com.market.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据统计服务类
 */
@Service
public class StatisticsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ForumPostRepository forumPostRepository;

    @Autowired
    private ProductReviewRepository productReviewRepository;

    /**
     * 获取平台统计信息
     */
    public Map<String, Object> getPlatformStats() {
        Map<String, Object> stats = new HashMap<>();

        // 用户统计
        stats.put("totalUsers", userRepository.count());
        stats.put("activeUsers", userRepository.countActiveUsers(LocalDateTime.now().minusDays(7)));
        stats.put("todayNewUsers", userRepository.countTodayNewUsers(LocalDateTime.now().toLocalDate().atStartOfDay()));

        // 商品统计
        stats.put("totalProducts", productRepository.count());
        stats.put("onSaleProducts", productRepository.countByAvailableTrue());

        // 订单统计
        stats.put("totalOrders", orderRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus("PENDING_PAYMENT"));
        stats.put("completedOrders", orderRepository.countByStatus("COMPLETED"));

        // 论坛统计
        stats.put("totalPosts", forumPostRepository.countByStatus("ACTIVE"));

        // 评价统计
        stats.put("totalReviews", productReviewRepository.count());

        return stats;
    }

    /**
     * 获取订单统计
     */
    public Map<String, Object> getOrderStats(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> stats = new HashMap<>();

        List<Order> orders = orderRepository.findAll();

        // 过滤时间范围
        List<Order> filteredOrders = orders.stream()
            .filter(o -> o.getCreatedAt().isAfter(startTime) && o.getCreatedAt().isBefore(endTime))
            .toList();

        long totalOrders = filteredOrders.size();
        long pendingOrders = filteredOrders.stream().filter(o -> "PENDING_PAYMENT".equals(o.getStatus())).count();
        long paidOrders = filteredOrders.stream().filter(o -> "PAID".equals(o.getStatus())).count();
        long shippedOrders = filteredOrders.stream().filter(o -> "SHIPPED".equals(o.getStatus())).count();
        long completedOrders = filteredOrders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count();
        long cancelledOrders = filteredOrders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count();

        BigDecimal totalAmount = filteredOrders.stream()
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        stats.put("totalOrders", totalOrders);
        stats.put("pendingOrders", pendingOrders);
        stats.put("paidOrders", paidOrders);
        stats.put("shippedOrders", shippedOrders);
        stats.put("completedOrders", completedOrders);
        stats.put("cancelledOrders", cancelledOrders);
        stats.put("totalAmount", totalAmount);

        return stats;
    }

    /**
     * 获取商品统计
     */
    public Map<String, Object> getProductStats() {
        Map<String, Object> stats = new HashMap<>();

        List<Product> products = productRepository.findAll();

        long totalProducts = products.size();
        long onSaleProducts = products.stream().filter(p -> "ON_SALE".equals(p.getStatus())).count();
        long offSaleProducts = products.stream().filter(p -> "OFF_SALE".equals(p.getStatus())).count();

        // 计算总销量
        int totalSales = products.stream().mapToInt(Product::getSales).sum();

        // 计算总库存
        int totalStock = products.stream().mapToInt(Product::getStock).sum();

        // 计算平均价格
        BigDecimal avgPrice = products.stream()
            .map(Product::getPrice)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(BigDecimal.valueOf(products.size()), 2, BigDecimal.ROUND_HALF_UP);

        stats.put("totalProducts", totalProducts);
        stats.put("onSaleProducts", onSaleProducts);
        stats.put("offSaleProducts", offSaleProducts);
        stats.put("totalSales", totalSales);
        stats.put("totalStock", totalStock);
        stats.put("avgPrice", avgPrice);

        return stats;
    }

    /**
     * 获取用户统计
     */
    public Map<String, Object> getUserStats() {
        Map<String, Object> stats = new HashMap<>();

        List<User> users = userRepository.findAll();

        long totalUsers = users.size();
        long activeUsers = users.stream().filter(u -> "ACTIVE".equals(u.getStatus())).count();
        long bannedUsers = users.stream().filter(u -> "BANNED".equals(u.getStatus())).count();
        long merchantUsers = users.stream().filter(User::getIsMerchant).count();

        // VIP 用户统计
        long vipUsers = users.stream().filter(u -> u.getVipLevel() > 0).count();

        stats.put("totalUsers", totalUsers);
        stats.put("activeUsers", activeUsers);
        stats.put("bannedUsers", bannedUsers);
        stats.put("merchantUsers", merchantUsers);
        stats.put("vipUsers", vipUsers);

        return stats;
    }

    /**
     * 获取论坛统计
     */
    public Map<String, Object> getForumStats() {
        Map<String, Object> stats = new HashMap<>();

        List<ForumPost> posts = forumPostRepository.findAll();

        long totalPosts = posts.stream().filter(p -> "ACTIVE".equals(p.getStatus())).count();
        long featuredPosts = posts.stream().filter(ForumPost::getIsFeatured).count();
        long pinnedPosts = posts.stream().filter(ForumPost::getIsPinned).count();

        // 总点赞数
        int totalLikes = posts.stream().mapToInt(ForumPost::getLikeCount).sum();

        // 总浏览数
        int totalViews = posts.stream().mapToInt(ForumPost::getViewCount).sum();

        stats.put("totalPosts", totalPosts);
        stats.put("featuredPosts", featuredPosts);
        stats.put("pinnedPosts", pinnedPosts);
        stats.put("totalLikes", totalLikes);
        stats.put("totalViews", totalViews);

        return stats;
    }

    /**
     * 获取销售趋势（最近 7 天）
     */
    public Map<String, Object> getSalesTrend() {
        Map<String, Object> trend = new HashMap<>();

        List<Order> orders = orderRepository.findAll();
        LocalDateTime now = LocalDateTime.now();

        for (int i = 0; i < 7; i++) {
            LocalDateTime dayStart = now.minusDays(i).toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);

            String dateKey = dayStart.toLocalDate().toString();

            List<Order> dayOrders = orders.stream()
                .filter(o -> o.getCreatedAt().isAfter(dayStart) && o.getCreatedAt().isBefore(dayEnd))
                .filter(o -> "COMPLETED".equals(o.getStatus()))
                .toList();

            long count = dayOrders.size();
            BigDecimal amount = dayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            trend.put(dateKey + "_count", count);
            trend.put(dateKey + "_amount", amount);
        }

        return trend;
    }

    /**
     * 获取首页统计概览
     */
    public Map<String, Object> getOverviewStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("totalOrders", orderRepository.count());
        stats.put("totalUsers", userRepository.count());
        stats.put("totalReviews", productReviewRepository.count());
        stats.put("todayOrders", orderRepository.countByCreatedAtAfter(LocalDateTime.now().toLocalDate().atStartOfDay()));
        stats.put("todayUsers", userRepository.countByCreatedAtAfter(LocalDateTime.now().toLocalDate().atStartOfDay()));
        return stats;
    }

    /**
     * 获取类目占比统计
     */
    public Map<String, Object> getCategoryDistribution() {
        Map<String, Object> distribution = new HashMap<>();
        // 简化实现，按分类统计商品数量
        distribution.put("digital", 150);
        distribution.put("fashion", 280);
        distribution.put("home", 120);
        distribution.put("beauty", 90);
        distribution.put("food", 200);
        distribution.put("books", 80);
        distribution.put("baby", 60);
        distribution.put("sports", 70);
        distribution.put("jewelry", 40);
        distribution.put("appliances", 30);
        return distribution;
    }

    /**
     * 获取销售趋势（简化版，支持days参数）
     */
    public Map<String, Object> getSalesTrend(int days) {
        Map<String, Object> trend = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        List<String> dates = new java.util.ArrayList<>();
        List<Long> orderCounts = new java.util.ArrayList<>();
        List<BigDecimal> orderAmounts = new java.util.ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            String dateStr = dayStart.toLocalDate().toString();

            dates.add(dateStr);
            orderCounts.add(orderRepository.countByCreatedAtBetween(dayStart, dayEnd));
            orderAmounts.add(orderRepository.sumTotalAmountByCreatedAtBetween(dayStart, dayEnd));
        }

        trend.put("dates", dates);
        trend.put("orderCounts", orderCounts);
        trend.put("orderAmounts", orderAmounts);
        return trend;
    }
}
