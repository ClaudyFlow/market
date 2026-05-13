package com.market.service;

import com.market.dto.mq.CreditMessage;
import com.market.dto.mq.NotificationMessage;
import com.market.dto.mq.OrderDelayMessage;
import com.market.entity.*;
import com.market.mq.MQProducer;
import com.market.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 订单服务类
 */
@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private CreditService creditService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PaymentRefundRepository refundRepository;

    @Autowired
    private ProductReviewRepository reviewRepository;

    @Autowired
    private UserAddressRepository addressRepository;

    @Autowired(required = false)
    private MQProducer mqProducer;

    @Value("${market.mq.order-timeout-enabled:true}")
    private boolean orderTimeoutEnabled;

    @Value("${market.mq.order-timeout-ms:1800000}")
    private long orderTimeoutMs;

    // ==================== 用户端订单服务 ====================

    /**
     * 获取用户订单列表
     */
    public Page<Order> getUserOrders(User user, String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            return orderRepository.findByUserAndStatus(user, status, pageable);
        }
        return orderRepository.findByUser(user, pageable);
    }

    /**
     * 获取订单详情
     */
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }

    /**
     * 获取订单统计
     */
    public Map<String, Object> getUserOrderStats(User user) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", orderRepository.countByUser(user));
        stats.put("pending", orderRepository.countByUserAndStatus(user, "PENDING"));
        stats.put("paid", orderRepository.countByUserAndStatus(user, "PAID"));
        stats.put("shipped", orderRepository.countByUserAndStatus(user, "SHIPPED"));
        stats.put("completed", orderRepository.countByUserAndStatus(user, "COMPLETED"));
        stats.put("cancelled", orderRepository.countByUserAndStatus(user, "CANCELLED"));
        stats.put("refunding", orderRepository.countByUserAndStatus(user, "REFUNDING"));
        return stats;
    }

    /**
     * 获取用户消费统计（今日/本月/本年）
     */
    public Map<String, Object> getUserSpendingStats(User user) {
        Map<String, Object> stats = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();

        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime todayEnd = todayStart.plusDays(1);
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();
        LocalDateTime yearStart = now.toLocalDate().withDayOfYear(1).atStartOfDay();

        BigDecimal todaySpending = orderRepository.sumTotalAmountByUserAndStatusAndCreatedAtBetween(
            user, "COMPLETED", todayStart, todayEnd);
        BigDecimal monthSpending = orderRepository.sumTotalAmountByUserAndStatusAndCreatedAtBetween(
            user, "COMPLETED", monthStart, now);
        BigDecimal yearSpending = orderRepository.sumTotalAmountByUserAndStatusAndCreatedAtBetween(
            user, "COMPLETED", yearStart, now);
        BigDecimal totalSpending = orderRepository.sumTotalAmountByUserAndStatusAndCreatedAtBetween(
            user, "COMPLETED", LocalDateTime.of(2000, 1, 1, 0, 0), now);

        stats.put("today", todaySpending != null ? todaySpending : BigDecimal.ZERO);
        stats.put("month", monthSpending != null ? monthSpending : BigDecimal.ZERO);
        stats.put("year", yearSpending != null ? yearSpending : BigDecimal.ZERO);
        stats.put("total", totalSpending != null ? totalSpending : BigDecimal.ZERO);

        return stats;
    }

    /**
     * 创建订单
     */
    @Transactional
    public Order createOrder(User user, List<Map<String, Object>> items, Long addressId, Long couponId) {
        if (items == null || items.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }

        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setStatus("PENDING");

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Map<String, Object> itemData : items) {
            Long productId = Long.valueOf(itemData.get("productId").toString());
            Integer quantity = Integer.valueOf(itemData.get("quantity").toString());

            Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("商品不存在"));

            if (product.getStock() < quantity) {
                throw new RuntimeException("商品 " + product.getName() + " 库存不足");
            }

            OrderItem orderItem = new OrderItem(product, quantity, product.getPrice());
            order.addItem(orderItem);

            totalAmount = totalAmount.add(orderItem.getSubtotal());

            product.setStock(product.getStock() - quantity);
            productRepository.save(product);

            // 设置商户信息
            if (order.getMerchant() == null && product.getUser() != null) {
                order.setMerchant(product.getUser());
            }
        }

        order.setTotalAmount(totalAmount);

        // 应用优惠券
        if (couponId != null) {
            try {
                couponService.useCoupon(couponId, user);
                
                // 计算优惠金额
                List<Coupon> availableCoupons = couponService.getAvailableCouponsForOrder(
                    user, 
                    order.getMerchant() != null ? order.getMerchant().getId() : null,
                    totalAmount,
                    items.stream().map(i -> Long.valueOf(i.get("productId").toString())).collect(Collectors.toList()),
                    new ArrayList<>()
                );
                
                Coupon coupon = availableCoupons.stream()
                    .filter(c -> c.getId().equals(couponId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("优惠券不可用"));
                
                BigDecimal discount = coupon.calculateDiscount(totalAmount);
                order.setTotalAmount(totalAmount.subtract(discount));
                
            } catch (Exception e) {
                throw new RuntimeException("优惠券使用失败：" + e.getMessage());
            }
        }

        Order savedOrder = orderRepository.save(order);

        // 清空购物车
        cartItemRepository.deleteByUser(user);

        // 奖励积分 (异步 - 通过消息队列)
        int creditToAdd = order.getTotalAmount().intValue() / 10;
        if (creditToAdd > 0 && mqProducer != null) {
            CreditMessage creditMsg = CreditMessage.orderReward(
                    user.getId(),
                    order.getTotalAmount(),
                    savedOrder.getOrderNo()
            );
            mqProducer.sendCredit(creditMsg);
            log.info("订单积分奖励消息已发送到队列: userId={}, credit={}", user.getId(), creditToAdd);
        } else if (creditToAdd > 0) {
            // 降级为直接调用
            creditService.addCredit(user.getId(), creditToAdd, "订单奖励：" + savedOrder.getOrderNo());
        }

        // 发送订单超时取消消息 (延迟队列)
        if (orderTimeoutEnabled && mqProducer != null && "PENDING".equals(savedOrder.getStatus())) {
            OrderDelayMessage delayMessage = OrderDelayMessage.of(
                    savedOrder.getId(),
                    savedOrder.getOrderNo(),
                    user.getId()
            );
            mqProducer.sendOrderDelay(delayMessage, orderTimeoutMs);
            log.info("订单超时取消消息已发送: orderId={}, timeout={}ms", savedOrder.getId(), orderTimeoutMs);
        }

        return savedOrder;
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> o.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付订单");
        }

        order.setStatus("CANCELLED");
        order.setCancelledAt(LocalDateTime.now());

        // 恢复库存
        for (OrderItem item : order.getItem()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }

        orderRepository.save(order);

        // 取消订单延迟消息 (防止用户手动取消时延迟队列还未触发)
        if (mqProducer != null) {
            mqProducer.cancelOrderDelay(orderId);
            log.info("订单取消消息已发送到队列: orderId={}", orderId);
        }
    }

    /**
     * 删除订单
     */
    @Transactional
    public void deleteOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> o.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"CANCELLED".equals(order.getStatus()) && !"COMPLETED".equals(order.getStatus())) {
            throw new RuntimeException("只能删除已取消或已完成的订单");
        }

        orderRepository.delete(order);
    }

    /**
     * 支付订单
     */
    @Transactional
    public Order payOrder(Long orderId, User user, String paymentMethod) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> o.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("PAID");
        order.setPaymentMethod(paymentMethod);
        order.setPaidAt(LocalDateTime.now());

        Order paidOrder = orderRepository.save(order);

        // 发送支付成功通知 (异步)
        if (mqProducer != null) {
            NotificationMessage notifyMsg = NotificationMessage.paymentSuccess(
                    user.getId(),
                    order.getOrderNo(),
                    order.getTotalAmount().doubleValue()
            );
            mqProducer.sendNotification(notifyMsg);
            log.info("支付成功通知已发送到队列: userId={}, orderNo={}", user.getId(), order.getOrderNo());
        }

        return paidOrder;
    }

    /**
     * 确认收货
     */
    @Transactional
    public Order confirmReceive(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> o.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"SHIPPED".equals(order.getStatus())) {
            throw new RuntimeException("只能确认已发货的订单");
        }

        order.setStatus("COMPLETED");
        order.setCompletedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    /**
     * 申请退款
     */
    @Transactional
    public void applyRefund(Long orderId, User user, String reason, List<String> images) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> o.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PAID".equals(order.getStatus()) && !"SHIPPED".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("REFUNDING");
        order.setRefundReason(reason);
        if (images != null && !images.isEmpty()) {
            order.setRefundImages(String.join(",", images));
        }

        orderRepository.save(order);
    }

    /**
     * 获取物流记录
     */
    public List<Map<String, String>> getTrackingRecords(Order order) {
        List<Map<String, String>> records = new ArrayList<>();
        
        if (order.getTrackingNo() == null || order.getTrackingNo().isEmpty()) {
            return records;
        }

        // 添加发货记录
        if (order.getShippedAt() != null) {
            Map<String, String> record = new HashMap<>();
            record.put("time", order.getShippedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
            record.put("desc", "已发货，物流单号：" + order.getTrackingNo());
            records.add(record);
        }

        // 模拟物流跟踪信息
        if (order.getShippedAt() != null && order.getTrackingNo() != null) {
            LocalDateTime shippedAt = order.getShippedAt();
            records.add(Map.of(
                "time", shippedAt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                "desc", "订单已发货，等待配送"
            ));
            if (shippedAt.isBefore(LocalDateTime.now().minusHours(24))) {
                records.add(Map.of(
                    "time", shippedAt.plusHours(12).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "desc", "包裹已到达中转站"
                ));
            }
            if (shippedAt.isBefore(LocalDateTime.now().minusHours(48))) {
                records.add(Map.of(
                    "time", shippedAt.plusHours(36).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "desc", "包裹正在配送途中"
                ));
            }
            if (order.getCompletedAt() != null) {
                records.add(Map.of(
                    "time", order.getCompletedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
                    "desc", "已签收"
                ));
            }
        }

        return records;
    }

    // ==================== 商户端订单服务 ====================

    /**
     * 获取商户订单列表（支持复杂查询）
     */
    public Page<Order> getMerchantOrders(User merchant, String status, String orderNo,
                                         String productName, LocalDateTime startDate,
                                         LocalDateTime endDate, Pageable pageable) {
        return orderRepository.findOrders(
            orderNo, status, null, merchant.getId(), null,
            startDate, endDate, pageable
        );
    }

    /**
     * 获取商户订单统计
     */
    public Map<String, Object> getMerchantOrderStats(User merchant) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", orderRepository.countByMerchant(merchant));
        stats.put("pending", orderRepository.countByMerchantAndStatus(merchant, "PENDING"));
        stats.put("paid", orderRepository.countByMerchantAndStatus(merchant, "PAID"));
        stats.put("shipped", orderRepository.countByMerchantAndStatus(merchant, "SHIPPED"));
        stats.put("completed", orderRepository.countByMerchantAndStatus(merchant, "COMPLETED"));
        stats.put("cancelled", orderRepository.countByMerchantAndStatus(merchant, "CANCELLED"));
        stats.put("refunding", orderRepository.countByMerchantAndStatus(merchant, "REFUNDING"));
        return stats;
    }

    /**
     * 获取商户销售趋势
     */
    public Map<String, Object> getMerchantSalesTrend(User merchant, int days) {
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
            orderCounts.add(orderRepository.countByMerchantAndCreatedAtBetween(merchant, dayStart, dayEnd));
            orderAmounts.add(orderRepository.sumTotalAmountByMerchantAndStatusAndCreatedAtBetween(merchant, dayStart, dayEnd));
        }

        trend.put("dates", dates);
        trend.put("orderCounts", orderCounts);
        trend.put("orderAmounts", orderAmounts);
        return trend;
    }

    /**
     * 获取商户订单统计（增强版）
     */
    public Map<String, Object> getMerchantOrderStatsEnhanced(User merchant) {
        Map<String, Object> stats = getMerchantOrderStats(merchant);

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime todayStart = now.toLocalDate().atStartOfDay();
        LocalDateTime monthStart = now.toLocalDate().withDayOfMonth(1).atStartOfDay();

        BigDecimal todayAmount = orderRepository.sumTotalAmountByMerchantAndStatusAndCreatedAtBetween(merchant, todayStart, now);
        BigDecimal monthAmount = orderRepository.sumTotalAmountByMerchantAndStatusAndCreatedAtBetween(merchant, monthStart, now);

        stats.put("todayAmount", todayAmount != null ? todayAmount : BigDecimal.ZERO);
        stats.put("monthAmount", monthAmount != null ? monthAmount : BigDecimal.ZERO);
        stats.put("totalAmount", orderRepository.sumTotalAmountByMerchantAndStatusAndCreatedAtBetween(merchant, LocalDateTime.of(2000, 1, 1, 0, 0), now));

        return stats;
    }

    /**
     * 判断订单是否属于该商户
     */
    public boolean isMerchantOrder(Order order, User merchant) {
        return order.getMerchant() != null && order.getMerchant().getId().equals(merchant.getId());
    }

    /**
     * 发货
     */
    @Transactional
    public Order shipOrder(Long orderId, User merchant, String trackingNo, String carrier, String remark) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> isMerchantOrder(o, merchant))
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PAID".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        order.setStatus("SHIPPED");
        order.setTrackingNo(trackingNo);
        order.setCarrier(carrier);
        order.setShippedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    /**
     * 获取退款订单列表
     */
    public Page<Order> getRefundOrders(User merchant, Pageable pageable) {
        return orderRepository.findRefundOrders(merchant.getId(), pageable);
    }

    /**
     * 处理退款申请
     */
    @Transactional
    public void handleRefund(Long orderId, User merchant, boolean approved, String reason) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> isMerchantOrder(o, merchant))
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"REFUNDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        if (approved) {
            order.setStatus("REFUNDED");
            // 恢复库存
            for (OrderItem item : order.getItem()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
            // 退还积分
            int creditToRefund = order.getTotalAmount().intValue() / 10;
            if (creditToRefund > 0) {
                creditService.addCredit(order.getUser().getId(), creditToRefund, "退款：" + order.getOrderNo());
            }
        } else {
            order.setStatus("PAID");
            order.setRefundReason(reason);
        }

        orderRepository.save(order);
    }

    // ==================== 管理端订单服务 ====================

    /**
     * 获取所有订单列表
     */
    public Page<Order> getAllOrders(String orderNo, String status, Long userId, Long merchantId,
                                    String shopName, String productName, LocalDateTime startDate,
                                    LocalDateTime endDate, String paymentMethod, Pageable pageable) {
        return orderRepository.findOrders(orderNo, status, userId, merchantId, shopName, 
                                          startDate, endDate, pageable);
    }

    /**
     * 获取管理端订单统计
     */
    public Map<String, Object> getAdminOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Order> allOrders = orderRepository.findAll();
        
        stats.put("total", allOrders.size());
        stats.put("totalAmount", allOrders.stream()
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        stats.put("pending", allOrders.stream().filter(o -> "PENDING".equals(o.getStatus())).count());
        stats.put("paid", allOrders.stream().filter(o -> "PAID".equals(o.getStatus())).count());
        stats.put("shipped", allOrders.stream().filter(o -> "SHIPPED".equals(o.getStatus())).count());
        stats.put("completed", allOrders.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count());
        stats.put("cancelled", allOrders.stream().filter(o -> "CANCELLED".equals(o.getStatus())).count());
        stats.put("refunding", allOrders.stream().filter(o -> "REFUNDING".equals(o.getStatus())).count());
        
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        stats.put("todayOrders", allOrders.stream().filter(o -> o.getCreatedAt().isAfter(today)).count());
        stats.put("todayAmount", allOrders.stream()
            .filter(o -> o.getCreatedAt().isAfter(today))
            .map(Order::getTotalAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add));
        
        return stats;
    }

    /**
     * 获取订单趋势
     */
    public List<Map<String, Object>> getOrderTrend(int days) {
        List<Map<String, Object>> trend = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (int i = days - 1; i >= 0; i--) {
            LocalDateTime dayStart = now.minusDays(i).toLocalDate().atStartOfDay();
            LocalDateTime dayEnd = dayStart.plusDays(1);
            
            List<Order> dayOrders = orderRepository.findAll().stream()
                .filter(o -> (o.getCreatedAt().isAfter(dayStart) || o.getCreatedAt().isEqual(dayStart)) 
                          && o.getCreatedAt().isBefore(dayEnd))
                .collect(Collectors.toList());
            
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", dayStart.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            dayData.put("orders", dayOrders.size());
            dayData.put("amount", dayOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add));
            
            trend.add(dayData);
        }
        
        return trend;
    }

    /**
     * 获取商品排行
     */
    public List<Map<String, Object>> getProductRank(String type, int limit) {
        if ("sales".equals(type)) {
            return getProductSalesRank(limit);
        }
        return new ArrayList<>();
    }

    /**
     * 获取商品销量排行
     */
    public List<Map<String, Object>> getProductSalesRank(int limit) {
        List<Object[]> results = orderRepository.findProductSalesRank(limit);
        List<Map<String, Object>> rank = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i + 1);
            item.put("productId", row[0]);
            item.put("productName", row[1]);
            item.put("productImage", row[2]);
            item.put("sales", row[3]);
            item.put("revenue", row[4]);
            rank.add(item);
        }
        return rank;
    }

    /**
     * 获取店铺排行
     */
    public List<Map<String, Object>> getShopRank(int limit) {
        return getShopSalesRank(limit);
    }

    /**
     * 获取店铺销量排行
     */
    public List<Map<String, Object>> getShopSalesRank(int limit) {
        List<Object[]> results = orderRepository.findShopSalesRank(limit);
        List<Map<String, Object>> rank = new ArrayList<>();
        for (int i = 0; i < results.size(); i++) {
            Object[] row = results.get(i);
            Map<String, Object> item = new HashMap<>();
            item.put("rank", i + 1);
            item.put("shopId", row[0]);
            item.put("shopName", row[1]);
            item.put("revenue", row[2]);
            rank.add(item);
        }
        return rank;
    }

    /**
     * 更新订单状态
     */
    @Transactional
    public Order updateOrderStatus(Long orderId, String status, String remark) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        order.setStatus(status);
        
        switch (status) {
            case "PAID":
                order.setPaidAt(LocalDateTime.now());
                break;
            case "SHIPPED":
                order.setShippedAt(LocalDateTime.now());
                break;
            case "COMPLETED":
                order.setCompletedAt(LocalDateTime.now());
                break;
            case "CANCELLED":
                order.setCancelledAt(LocalDateTime.now());
                break;
        }

        return orderRepository.save(order);
    }

    /**
     * 获取全部退款订单列表
     */
    public Page<Order> getRefundOrders(String status, Pageable pageable) {
        if (status != null && !status.isEmpty()) {
            // TODO: 实现带状态过滤的查询
        }
        return orderRepository.findAllRefundOrders(pageable);
    }

    /**
     * 管理端处理退款
     */
    @Transactional
    public void handleRefund(Long orderId, boolean approved, String reason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"REFUNDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }

        if (approved) {
            order.setStatus("REFUNDED");
            for (OrderItem item : order.getItem()) {
                Product product = item.getProduct();
                product.setStock(product.getStock() + item.getQuantity());
                productRepository.save(product);
            }
            int creditToRefund = order.getTotalAmount().intValue() / 10;
            if (creditToRefund > 0) {
                creditService.addCredit(order.getUser().getId(), creditToRefund, "退款：" + order.getOrderNo());
            }
        } else {
            order.setStatus("PAID");
        }

        orderRepository.save(order);
    }

    // ==================== 工具方法 ====================

    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = new Random().nextInt(9000) + 1000;
        return timestamp + random;
    }

    // ==================== 补充功能 ====================

    /**
     * 获取退款详情
     */
    public PaymentRefund getRefundDetail(Long orderId) {
        List<PaymentRefund> refunds = refundRepository.findByOrderId(orderId);
        return refunds.isEmpty() ? null : refunds.get(0);
    }

    /**
     * 检查订单是否已评价
     */
    public boolean isOrderReviewed(Long orderId) {
        return reviewRepository.existsByOrderId(orderId);
    }

    /**
     * 修改订单地址
     */
    @Transactional
    public Order updateOrderAddress(Long orderId, Long addressId, User user) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权修改该订单");
        }

        if (!"PENDING".equals(order.getStatus()) && !"PAID".equals(order.getStatus())) {
            throw new RuntimeException("订单已发货，无法修改地址");
        }

        UserAddress address = addressRepository.findById(addressId)
            .orElseThrow(() -> new RuntimeException("地址不存在"));

        String fullAddress = address.getProvince() + address.getCity() + address.getDistrict() + address.getDetailAddress();
        order.setShippingAddress(fullAddress);
        return orderRepository.save(order);
    }

    /**
     * 修改订单备注
     */
    @Transactional
    public Order updateOrderRemark(Long orderId, String remark, User user) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权修改该订单");
        }

        // TODO: 添加 remark 字段到 Order 实体后启用
        return order;
    }

    /**
     * 再次购买
     */
    @Transactional
    public List<CartItem> repurchase(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("无权操作该订单");
        }

        List<CartItem> addedItems = new ArrayList<>();
        for (OrderItem item : order.getItem()) {
            CartItem cartItem = new CartItem(user, item.getProduct(), item.getQuantity());
            addedItems.add(cartItemRepository.save(cartItem));
        }
        return addedItems;
    }

    /**
     * 模拟发货
     */
    @Transactional
    public Order mockShip(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!"PAID".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确，仅已支付订单可发货");
        }

        order.setStatus("SHIPPED");
        order.setTrackingNo("MOCK" + System.currentTimeMillis());
        order.setCarrier("模拟物流");
        order.setShippedAt(LocalDateTime.now());
        return orderRepository.save(order);
    }
}
