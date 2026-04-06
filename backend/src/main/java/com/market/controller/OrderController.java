package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.CartItem;
import com.market.entity.Order;
import com.market.entity.OrderItem;
import com.market.entity.PaymentRefund;
import com.market.entity.Review;
import com.market.entity.User;
import com.market.service.OrderService;
import com.market.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 用户端订单控制器
 */
@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {

    @Autowired
    private OrderService orderService;
    
    @Autowired
    private ReviewService reviewService;

    /**
     * 创建订单
     */
    @PostMapping
    @Idempotent(key = "'create_order_' + #user.id", expire = 3600, message = "订单正在创建中，请勿重复提交")
    @DistributedLock(key = "'create_order_' + #user.id", waitTime = 5000)
    @AuditLog(module = "订单管理", action = "创建订单", recordParams = true, recordResult = true)
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Order> createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> data) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        List<Map<String, Object>> items = (List<Map<String, Object>>) data.get("items");
        Long addressId = data.get("addressId") != null ? Long.valueOf(data.get("addressId").toString()) : null;
        Long couponId = data.get("couponId") != null ? Long.valueOf(data.get("couponId").toString()) : null;
        String remark = (String) data.get("remark");

        Order order = orderService.createOrder(user, items, addressId, couponId);
        return Result.success(order);
    }

    /**
     * 获取订单列表
     */
    @GetMapping
    @Cacheable(key = "'order_list_' + #user.id + '_' + #page + '_' + #status", 
               cacheName = "orders", expire = 300)
    @AuditLog(module = "订单管理", action = "查询订单列表")
    @DataScope(scopeType = DataScope.ScopeType.SELF)
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage = orderService.getUserOrders(user, status, pageable);

        List<Map<String, Object>> orderList = orderPage.getContent().stream()
            .map(this::convertOrderToMap)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", orderList);
        response.put("total", orderPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 获取订单详情
     */
    @GetMapping("/{id}")
    @Cacheable(key = "'order_detail_' + #id", cacheName = "orders", expire = 600)
    @AuditLog(module = "订单管理", action = "查询订单详情")
    @SensitiveData(type = SensitiveData.SensitiveType.DEFAULT)
    public Result<Map<String, Object>> getOrderDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Order order = orderService.getOrderById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            return Result.error(403, "无权访问该订单");
        }

        return Result.success(convertOrderToMap(order));
    }

    /**
     * 取消订单
     */
    @PutMapping("/{id}/cancel")
    @Idempotent(key = "'cancel_order_' + #id", expire = 3600)
    @DistributedLock(key = "'cancel_order_' + #id", waitTime = 5000)
    @AuditLog(module = "订单管理", action = "取消订单")
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Void> cancelOrder(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        orderService.cancelOrder(id, user);
        return Result.success(null);
    }

    /**
     * 删除订单
     */
    @DeleteMapping("/{id}")
    @Idempotent(key = "'delete_order_' + #id", expire = 3600)
    @AuditLog(module = "订单管理", action = "删除订单")
    public Result<Void> deleteOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        orderService.deleteOrder(id, user);
        return Result.success(null);
    }

    /**
     * 支付订单
     */
    @PostMapping("/{id}/pay")
    @Idempotent(key = "'pay_order_' + #id", expire = 3600, message = "正在支付中，请勿重复提交")
    @DistributedLock(key = "'pay_order_' + #id", waitTime = 10000)
    @AuditLog(module = "订单管理", action = "支付订单", recordParams = true)
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Order> payOrder(
            @PathVariable Long id,
            @RequestParam(defaultValue = "alipay") String paymentMethod,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Order order = orderService.payOrder(id, user, paymentMethod);
        return Result.success(order);
    }

    /**
     * 获取支付状态
     */
    @GetMapping("/{id}/pay-status")
    @Cacheable(key = "'pay_status_' + #id", cacheName = "orders", expire = 60)
    @AuditLog(module = "订单管理", action = "查询支付状态")
    public Result<Map<String, Object>> getPayStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Order order = orderService.getOrderById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        Map<String, Object> result = new HashMap<>();
        result.put("paid", order.getPaidAt() != null);
        result.put("payTime", order.getPaidAt());
        
        return Result.success(result);
    }

    /**
     * 确认收货
     */
    @PutMapping("/{id}/confirm")
    @Idempotent(key = "'confirm_order_' + #id", expire = 3600)
    @DistributedLock(key = "'confirm_order_' + #id", waitTime = 5000)
    @AuditLog(module = "订单管理", action = "确认收货")
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Order> confirmReceive(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Order order = orderService.confirmReceive(id, user);
        return Result.success(order);
    }

    /**
     * 申请退款
     */
    @PostMapping("/{id}/refund")
    @Idempotent(key = "'refund_order_' + #id", expire = 3600)
    @DistributedLock(key = "'refund_order_' + #id", waitTime = 5000)
    @AuditLog(module = "订单管理", action = "申请退款", recordParams = true)
    public Result<Void> applyRefund(
            @PathVariable Long id,
            @RequestParam String reason,
            @RequestParam(required = false) List<String> images,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        orderService.applyRefund(id, user, reason, images);
        return Result.success(null);
    }

    /**
     * 获取退款详情
     */
    @GetMapping("/{id}/refund")
    @Cacheable(key = "'refund_detail_' + #id", cacheName = "orders", expire = 300)
    @AuditLog(module = "订单管理", action = "查询退款详情")
    public Result<Map<String, Object>> getRefundDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        PaymentRefund refund = orderService.getRefundDetail(id);
        if (refund == null) {
            return Result.error(404, "退款记录不存在");
        }

        Map<String, Object> result = new HashMap<>();
        result.put("id", refund.getId());
        result.put("refundNo", refund.getRefundNo());
        result.put("status", refund.getStatus());
        result.put("amount", refund.getAmount());
        result.put("reason", refund.getReason());
        result.put("createdAt", refund.getCreatedAt());
        result.put("handledAt", refund.getRefundedAt());

        return Result.success(result);
    }

    /**
     * 提交评价
     */
    @PostMapping("/{id}/review")
    @Idempotent(key = "'review_order_' + #id", expire = 3600)
    @AuditLog(module = "订单管理", action = "提交评价", recordParams = true)
    public Result<Void> submitReview(
            @PathVariable Long id,
            @RequestBody Map<String, Object> data,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Order order = orderService.getOrderById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            return Result.error(403, "无权评价该订单");
        }

        if (!"COMPLETED".equals(order.getStatus())) {
            return Result.error(400, "订单未完成，无法评价");
        }

        // 检查是否已评价
        if (orderService.isOrderReviewed(id)) {
            return Result.error(400, "该订单已评价");
        }

        // 提取评价数据
        Integer score = data.get("score") != null ? Integer.valueOf(data.get("score").toString()) : null;
        String content = data.get("content") != null ? data.get("content").toString() : "";
        
        if (score == null || score < 1 || score > 5) {
            return Result.error(400, "评分必须为 1-5 星");
        }

        // 对订单中的每个商品提交评价
        for (OrderItem item : order.getItem()) {
            try {
                reviewService.addReviewFromOrder(
                    user.getId(),
                    item.getProduct().getId(),
                    score,
                    content,
                    id
                );
            } catch (Exception e) {
                // 如果某个商品已评价，跳过
            }
        }

        return Result.success(null);
    }

    /**
     * 检查是否已评价
     */
    @GetMapping("/{id}/reviewed")
    @Cacheable(key = "'order_reviewed_' + #id", cacheName = "orders", expire = 600)
    @AuditLog(module = "订单管理", action = "检查评价状态")
    public Result<Map<String, Boolean>> checkReviewed(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        boolean reviewed = orderService.isOrderReviewed(id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("reviewed", reviewed);
        return Result.success(result);
    }

    /**
     * 获取订单物流
     */
    @GetMapping("/{id}/logistics")
    @Cacheable(key = "'order_logistics_' + #id", cacheName = "orders", expire = 300)
    @AuditLog(module = "订单管理", action = "查询物流信息")
    public Result<Map<String, Object>> getOrderLogistics(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Order order = orderService.getOrderById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        if (!order.getUser().getId().equals(user.getId())) {
            return Result.error(403, "无权访问该订单");
        }

        Map<String, Object> logistics = new HashMap<>();
        logistics.put("trackingNo", order.getTrackingNo());
        logistics.put("carrier", order.getCarrier());
        logistics.put("records", orderService.getTrackingRecords(order));

        return Result.success(logistics);
    }

    /**
     * 修改订单地址
     */
    @PutMapping("/{id}/address")
    @Idempotent(key = "'update_order_address_' + #id", expire = 3600)
    @AuditLog(module = "订单管理", action = "修改订单地址")
    public Result<Void> updateOrderAddress(
            @PathVariable Long id,
            @RequestParam Long addressId,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            orderService.updateOrderAddress(id, addressId, user);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 修改订单备注
     */
    @PutMapping("/{id}/remark")
    @Idempotent(key = "'update_order_remark_' + #id", expire = 3600)
    @AuditLog(module = "订单管理", action = "修改订单备注")
    public Result<Void> updateOrderRemark(
            @PathVariable Long id,
            @RequestParam String remark,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            orderService.updateOrderRemark(id, remark, user);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 再次购买
     */
    @PostMapping("/{id}/repurchase")
    @Idempotent(key = "'repurchase_order_' + #id", expire = 600)
    @AuditLog(module = "订单管理", action = "再次购买")
    public Result<List<CartItem>> repurchase(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            List<CartItem> addedItems = orderService.repurchase(id, user);
            return Result.success(addedItems);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/stats")
    @Cacheable(key = "'order_stats_' + #user.id", cacheName = "orders", expire = 300)
    @AuditLog(module = "订单管理", action = "查询订单统计")
    public Result<Map<String, Object>> getOrderStats(@AuthenticationPrincipal User user) {
        
        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Map<String, Object> stats = orderService.getUserOrderStats(user);
        return Result.success(stats);
    }

    /**
     * 模拟发货（测试用）
     */
    @PostMapping("/{id}/mock-ship")
    @Idempotent(key = "'mock_ship_' + #id", expire = 600)
    @AuditLog(module = "订单管理", action = "模拟发货", logLevel = AuditLog.LogLevel.WARNING)
    public Result<Void> mockShip(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        try {
            orderService.mockShip(id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 转换 Order 对象为 Map
     */
    private Map<String, Object> convertOrderToMap(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("status", order.getStatus());
        map.put("totalAmount", order.getTotalAmount());
        map.put("shippingAddress", order.getShippingAddress());
        map.put("trackingNo", order.getTrackingNo());
        map.put("carrier", order.getCarrier());
        map.put("createdAt", order.getCreatedAt());
        map.put("paidAt", order.getPaidAt());
        map.put("shippedAt", order.getShippedAt());
        map.put("completedAt", order.getCompletedAt());

        // 商户信息
        if (order.getMerchant() != null) {
            map.put("merchantName", order.getMerchant().getShopName());
        }

        // 订单项
        if (order.getItem() != null) {
            List<Map<String, Object>> items = order.getItem().stream()
                .map(item -> {
                    Map<String, Object> itemMap = new HashMap<>();
                    itemMap.put("id", item.getId());
                    itemMap.put("productId", item.getProduct().getId());
                    itemMap.put("productName", item.getProduct().getName());
                    itemMap.put("productImage", item.getProduct().getImage());
                    itemMap.put("price", item.getPrice());
                    itemMap.put("quantity", item.getQuantity());
                    itemMap.put("subtotal", item.getSubtotal());
                    return itemMap;
                })
                .collect(Collectors.toList());
            map.put("items", items);
        }

        return map;
    }
}
