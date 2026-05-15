package com.market.service;

import com.market.entity.*;
import com.market.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 运营数据分析服务
 * 提供漏斗分析、留存分析、商品转化率分析等功能
 */
@Service
public class OperationAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(OperationAnalysisService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserBrowseHistoryRepository browseHistoryRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private FavoriteRepository favoriteRepository;

    @Autowired
    private ProductReviewRepository reviewRepository;

    /**
     * 漏斗分析 - 用户行为转化漏斗
     * @param days 分析天数
     * @return 漏斗数据
     */
    public Map<String, Object> getFunnelAnalysis(int days) {
        LocalDateTime startTime = LocalDateTime.now().minusDays(days);
        Map<String, Object> funnel = new LinkedHashMap<>();

        // 1. 访问用户数（去重）
        long visitors = browseHistoryRepository.findAll().stream()
                .filter(h -> h.getBrowseTime().isAfter(startTime))
                .map(UserBrowseHistory::getUserId)
                .distinct()
                .count();

        // 2. 加购用户数
        long cartUsers = cartItemRepository.findAll().stream()
                .map(CartItem::getUser)
                .filter(u -> u != null && u.getCreatedAt() != null && u.getCreatedAt().isAfter(startTime))
                .map(User::getId)
                .distinct()
                .count();

        // 3. 收藏用户数
        long favoriteUsers = favoriteRepository.findAll().stream()
                .filter(f -> f.getCreatedAt() != null && f.getCreatedAt().isAfter(startTime))
                .map(Favorite::getUserId)
                .distinct()
                .count();

        // 4. 下单用户数
        long orderUsers = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt().isAfter(startTime))
                .map(Order::getUser)
                .map(User::getId)
                .distinct()
                .count();

        // 5. 支付用户数
        long paidUsers = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt().isAfter(startTime) && "PAID".equals(o.getStatus()))
                .map(Order::getUser)
                .map(User::getId)
                .distinct()
                .count();

        // 6. 完成订单用户数
        long completedUsers = orderRepository.findAll().stream()
                .filter(o -> o.getCreatedAt().isAfter(startTime) && "COMPLETED".equals(o.getStatus()))
                .map(Order::getUser)
                .map(User::getId)
                .distinct()
                .count();

        funnel.put("visitors", visitors);
        funnel.put("cartUsers", cartUsers);
        funnel.put("favoriteUsers", favoriteUsers);
        funnel.put("orderUsers", orderUsers);
        funnel.put("paidUsers", paidUsers);
        funnel.put("completedUsers", completedUsers);

        // 计算转化率
        funnel.put("visitToCartRate", calculateRate(visitors, cartUsers));
        funnel.put("cartToFavoriteRate", calculateRate(cartUsers, favoriteUsers));
        funnel.put("favoriteToOrderRate", calculateRate(favoriteUsers, orderUsers));
        funnel.put("orderToPaidRate", calculateRate(orderUsers, paidUsers));
        funnel.put("paidToCompletedRate", calculateRate(paidUsers, completedUsers));
        funnel.put("overallConversionRate", calculateRate(visitors, completedUsers));

        return funnel;
    }

    /**
     * 留存分析 - 用户留存率
     * @param days 分析天数
     * @return 留存数据
     */
    public Map<String, Object> getRetentionAnalysis(int days) {
        Map<String, Object> retention = new LinkedHashMap<>();
        LocalDateTime now = LocalDateTime.now();

        // 获取每一天新增的用户
        List<Map<String, Object>> dailyRetention = new ArrayList<>();

        for (int i = days - 1; i >= 0; i--) {
            LocalDate date = now.toLocalDate().minusDays(i);
            LocalDateTime dayStart = date.atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);

            // 当天新增用户
            long newUsers = userRepository.findAll().stream()
                    .filter(u -> u.getCreatedAt() != null 
                            && !u.getCreatedAt().isBefore(dayStart) 
                            && u.getCreatedAt().isBefore(dayEnd))
                    .count();

            if (newUsers == 0) continue;

            Map<String, Object> dayData = new LinkedHashMap<>();
            dayData.put("date", date.toString());
            dayData.put("newUsers", newUsers);

            // 计算次日、3日、7日、30日留存
            dayData.put("nextDayRetention", calculateRetention(newUsers, getRetainedUsers(dayEnd, 1)));
            dayData.put("day3Retention", calculateRetention(newUsers, getRetainedUsers(dayEnd, 3)));
            dayData.put("day7Retention", calculateRetention(newUsers, getRetainedUsers(dayEnd, 7)));
            dayData.put("day30Retention", calculateRetention(newUsers, getRetainedUsers(dayEnd, 30)));

            dailyRetention.add(dayData);
        }

        retention.put("dailyRetention", dailyRetention);

        // 总体留存
        long totalUsers = userRepository.count();
        long activeUsers7d = userRepository.countActiveUsers(now.minusDays(7));
        long activeUsers30d = userRepository.countActiveUsers(now.minusDays(30));

        retention.put("totalUsers", totalUsers);
        retention.put("activeUsers7d", activeUsers7d);
        retention.put("activeUsers30d", activeUsers30d);
        retention.put("activeRate7d", calculateRate(totalUsers, activeUsers7d));
        retention.put("activeRate30d", calculateRate(totalUsers, activeUsers30d));

        return retention;
    }

    /**
     * 商品转化率分析
     * @param limit 返回商品数量
     * @return 商品转化率列表
     */
    public List<Map<String, Object>> getProductConversionAnalysis(int limit) {
        List<Product> products = productRepository.findAll();
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startTime = now.minusDays(30);

        List<Map<String, Object>> conversionList = new ArrayList<>();

        for (Product product : products) {
            // 浏览次数
            long views = browseHistoryRepository.findAll().stream()
                    .filter(h -> h.getProductId().equals(product.getId()) 
                            && h.getBrowseTime().isAfter(startTime))
                    .count();

            if (views == 0) continue;

            // 收藏次数
            long favorites = favoriteRepository.findAll().stream()
                    .filter(f -> f.getProductId().equals(product.getId())
                            && f.getCreatedAt() != null
                            && f.getCreatedAt().isAfter(startTime))
                    .count();

            // 加购次数
            long carts = cartItemRepository.findAll().stream()
                    .filter(c -> c.getProduct() != null 
                            && c.getProduct().getId().equals(product.getId()))
                    .count();

            // 订单数
            long orders = orderRepository.findAll().stream()
                    .filter(o -> o.getCreatedAt().isAfter(startTime))
                    .flatMap(o -> o.getItem().stream())
                    .filter(item -> item.getProduct().getId().equals(product.getId()))
                    .count();

            // 成交量
            long completedOrders = orderRepository.findAll().stream()
                    .filter(o -> o.getCreatedAt().isAfter(startTime) && "COMPLETED".equals(o.getStatus()))
                    .flatMap(o -> o.getItem().stream())
                    .filter(item -> item.getProduct().getId().equals(product.getId()))
                    .count();

            Map<String, Object> productData = new LinkedHashMap<>();
            productData.put("productId", product.getId());
            productData.put("productName", product.getName());
            productData.put("category", product.getCategory());
            productData.put("price", product.getPrice());
            productData.put("views", views);
            productData.put("favorites", favorites);
            productData.put("carts", carts);
            productData.put("orders", orders);
            productData.put("completedOrders", completedOrders);
            productData.put("sales", product.getSales());
            
            // 计算转化率
            productData.put("viewToCartRate", calculateRate(views, carts));
            productData.put("viewToOrderRate", calculateRate(views, orders));
            productData.put("orderToCompletedRate", calculateRate(orders, completedOrders));
            
            conversionList.add(productData);
        }

        // 按浏览量排序
        conversionList.sort((a, b) -> Long.compare((long) b.get("views"), (long) a.get("views")));

        return conversionList.stream().limit(limit).collect(Collectors.toList());
    }

    /**
     * 获取指定天数后仍活跃的用户数
     */
    private long getRetainedUsers(LocalDateTime fromDay, int daysAfter) {
        LocalDateTime checkTime = fromDay.plusDays(daysAfter);
        LocalDateTime checkEnd = checkTime.plusDays(1);

        // 在指定时间后有活跃行为的用户（浏览、加购、下单等）
        Set<Long> activeUserIds = new HashSet<>();

        // 浏览活跃
        browseHistoryRepository.findAll().stream()
                .filter(h -> !h.getBrowseTime().isBefore(checkTime) && h.getBrowseTime().isBefore(checkEnd))
                .map(UserBrowseHistory::getUserId)
                .forEach(activeUserIds::add);

        // 订单活跃
        orderRepository.findAll().stream()
                .filter(o -> !o.getCreatedAt().isBefore(checkTime) && o.getCreatedAt().isBefore(checkEnd))
                .map(o -> o.getUser().getId())
                .forEach(activeUserIds::add);

        return activeUserIds.size();
    }

    /**
     * 计算转化率
     */
    private double calculateRate(long total, long part) {
        if (total == 0) return 0.0;
        return BigDecimal.valueOf(part)
                .multiply(BigDecimal.valueOf(100))
                .divide(BigDecimal.valueOf(total), 2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * 计算留存率
     */
    private double calculateRetention(long total, long retained) {
        return calculateRate(total, retained);
    }
}
