package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.Order;
import com.market.entity.User;
import com.market.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商户端订单控制器
 */
@RestController
@RequestMapping("/api/merchant/order")
@CrossOrigin(origins = "*")
public class MerchantOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    @Cacheable(key = "'merchant_order_list_' + #user.id + '_' + #page + '_' + #status", 
               cacheName = "merchant_orders", expire = 300)
    @AuditLog(module = "商户端订单", action = "查询订单列表")
    @DataScope(scopeType = DataScope.ScopeType.DEPT)
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage = orderService.getMerchantOrders(user, status, orderNo, productName, startDate, endDate, pageable);

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
    @Cacheable(key = "'merchant_order_detail_' + #id", cacheName = "merchant_orders", expire = 600)
    @AuditLog(module = "商户端订单", action = "查询订单详情")
    @SensitiveData(type = SensitiveData.SensitiveType.DEFAULT)
    public Result<Map<String, Object>> getOrderDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Order order = orderService.getOrderById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        // 验证订单是否属于该商户
        if (!orderService.isMerchantOrder(order, user)) {
            return Result.error(403, "无权访问该订单");
        }

        return Result.success(convertOrderToMap(order));
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/stats")
    @Cacheable(key = "'merchant_order_stats_' + #user.id", cacheName = "merchant_orders", expire = 300)
    @AuditLog(module = "商户端订单", action = "查询订单统计")
    public Result<Map<String, Object>> getOrderStats(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        Map<String, Object> stats = orderService.getMerchantOrderStats(user);
        return Result.success(stats);
    }

    /**
     * 发货
     */
    @PostMapping("/{id}/ship")
    @Idempotent(key = "'merchant_ship_order_' + #id", expire = 3600, message = "正在发货，请勿重复提交")
    @DistributedLock(key = "'merchant_ship_order_' + #id", waitTime = 5000)
    @AuditLog(module = "商户端订单", action = "订单发货", recordParams = true)
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Order> shipOrder(
            @PathVariable Long id,
            @RequestParam String trackingNo,
            @RequestParam String carrier,
            @RequestParam(required = false) String remark,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Order order = orderService.shipOrder(id, user, trackingNo, carrier, remark);
        return Result.success(order);
    }

    /**
     * 获取退款申请列表
     */
    @GetMapping("/refund/list")
    @Cacheable(key = "'merchant_refund_list_' + #user.id + '_' + #page", 
               cacheName = "merchant_orders", expire = 300)
    @AuditLog(module = "商户端订单", action = "查询退款列表")
    public Result<Map<String, Object>> getRefundList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> refundPage = orderService.getRefundOrders(user, pageable);

        List<Map<String, Object>> orderList = refundPage.getContent().stream()
            .map(this::convertOrderToMap)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", orderList);
        response.put("total", refundPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 处理退款申请
     */
    @PostMapping("/{id}/refund")
    @Idempotent(key = "'merchant_handle_refund_' + #id", expire = 3600)
    @DistributedLock(key = "'merchant_handle_refund_' + #id", waitTime = 5000)
    @AuditLog(module = "商户端订单", action = "处理退款申请", recordParams = true, logLevel = AuditLog.LogLevel.INFO)
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Void> handleRefund(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        orderService.handleRefund(id, user, approved, reason);
        return Result.success();
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

        // 用户信息
        if (order.getUser() != null) {
            map.put("customerName", order.getUser().getName());
            map.put("customerPhone", order.getUser().getPhone());
            map.put("customerAvatar", order.getUser().getAvatar());
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
