package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.Order;
import com.market.entity.User;
import com.market.service.OrderService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端订单控制器
 */
@RestController
@RequestMapping("/api/admin/order")
@CrossOrigin(origins = "*")
public class AdminOrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取订单列表
     */
    @GetMapping("/list")
    @Cacheable(key = "'admin_order_list_' + #page + '_' + #status", cacheName = "admin_orders", expire = 300)
    @AuditLog(module = "管理端订单", action = "查询订单列表")
    @DataScope(scopeType = DataScope.ScopeType.ALL)
    public Result<Map<String, Object>> getOrderList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long merchantId,
            @RequestParam(required = false) String shopName,
            @RequestParam(required = false) String productName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate,
            @RequestParam(required = false) String paymentMethod) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage = orderService.getAllOrders(orderNo, status, userId, merchantId, shopName, productName, startDate, endDate, paymentMethod, pageable);

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
    @Cacheable(key = "'admin_order_detail_' + #id", cacheName = "admin_orders", expire = 600)
    @AuditLog(module = "管理端订单", action = "查询订单详情")
    @SensitiveData(type = SensitiveData.SensitiveType.DEFAULT)
    public Result<Map<String, Object>> getOrderDetail(@PathVariable Long id) {
        Order order = orderService.getOrderById(id)
            .orElseThrow(() -> new RuntimeException("订单不存在"));

        return Result.success(convertOrderToMap(order));
    }

    /**
     * 获取订单统计
     */
    @GetMapping("/stats")
    @Cacheable(key = "'admin_order_stats'", cacheName = "admin_orders", expire = 300)
    @AuditLog(module = "管理端订单", action = "查询订单统计")
    public Result<Map<String, Object>> getOrderStats() {
        Map<String, Object> stats = orderService.getAdminOrderStats();
        return Result.success(stats);
    }

    /**
     * 获取商品销量排行
     */
    @GetMapping("/rank/product")
    @Cacheable(key = "'admin_product_rank_' + #limit", cacheName = "admin_orders", expire = 600)
    @AuditLog(module = "管理端订单", action = "查询商品排行")
    public Result<List<Map<String, Object>>> getProductRank(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> rank = orderService.getProductSalesRank(limit);
        return Result.success(rank);
    }

    /**
     * 获取店铺销量排行
     */
    @GetMapping("/rank/shop")
    @Cacheable(key = "'admin_shop_rank_' + #limit", cacheName = "admin_orders", expire = 600)
    @AuditLog(module = "管理端订单", action = "查询店铺排行")
    public Result<List<Map<String, Object>>> getShopRank(
            @RequestParam(defaultValue = "10") Integer limit) {
        List<Map<String, Object>> rank = orderService.getShopSalesRank(limit);
        return Result.success(rank);
    }

    /**
     * 获取退款申请列表
     */
    @GetMapping("/refund/list")
    @Cacheable(key = "'admin_refund_list_' + #page + '_' + #status", cacheName = "admin_orders", expire = 300)
    @AuditLog(module = "管理端订单", action = "查询退款列表")
    public Result<Map<String, Object>> getRefundList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Order> refundPage = orderService.getRefundOrders(status, pageable);

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
    @Idempotent(key = "'admin_handle_refund_' + #id", expire = 3600)
    @DistributedLock(key = "'admin_handle_refund_' + #id", waitTime = 5000)
    @AuditLog(module = "管理端订单", action = "处理退款申请", recordParams = true, logLevel = AuditLog.LogLevel.INFO)
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Void> handleRefund(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String reason) {

        orderService.handleRefund(id, approved, reason);
        return Result.success();
    }

    /**
     * 导出订单
     */
    @GetMapping("/export")
    @AuditLog(module = "管理端订单", action = "导出订单")
    @RateLimiter(key = "'export_orders'", maxRequests = 5, timeout = 60, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public void exportOrders(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String orderNo,
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            HttpServletResponse response) throws Exception {

        // TODO: 实现订单导出功能
        response.setContentType("application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment;filename=orders.xlsx");
    }

    /**
     * 获取订单趋势
     */
    @GetMapping("/trend")
    @Cacheable(key = "'admin_order_trend_' + #days", cacheName = "admin_orders", expire = 300)
    @AuditLog(module = "管理端订单", action = "查询订单趋势")
    public Result<List<Map<String, Object>>> getOrderTrend(
            @RequestParam(defaultValue = "7") Integer days) {

        List<Map<String, Object>> trend = orderService.getOrderTrend(days);
        return Result.success(trend);
    }

    /**
     * 更新订单状态
     */
    @PutMapping("/{id}/status")
    @Idempotent(key = "'admin_update_order_status_' + #id", expire = 3600)
    @AuditLog(module = "管理端订单", action = "更新订单状态", recordParams = true)
    public Result<Order> updateOrderStatus(
            @PathVariable Long id,
            @RequestParam String status,
            @RequestParam(required = false) String remark) {

        Order order = orderService.updateOrderStatus(id, status, remark);
        return Result.success(order);
    }

    /**
     * 转换 Order 对象为 Map
     */
    private Map<String, Object> convertOrderToMap(Order order) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", order.getId());
        map.put("orderNo", order.getOrderNo());
        map.put("status", order.getStatus());
        map.put("amount", order.getTotalAmount());
        map.put("paymentMethod", order.getPaymentMethod());
        map.put("shippingAddress", order.getShippingAddress());
        map.put("trackingNo", order.getTrackingNo());
        map.put("carrier", order.getCarrier());
        map.put("createTime", order.getCreatedAt());
        map.put("paidTime", order.getPaidAt());
        map.put("shippedTime", order.getShippedAt());
        map.put("completedTime", order.getCompletedAt());
        map.put("cancelledTime", order.getCancelledAt());

        // 用户信息
        if (order.getUser() != null) {
            map.put("userId", order.getUser().getId());
            map.put("userName", order.getUser().getName());
            map.put("userPhone", order.getUser().getPhone());
            map.put("userAvatar", order.getUser().getAvatar());
        }

        // 商户信息
        if (order.getMerchant() != null) {
            map.put("merchantId", order.getMerchant().getId());
            map.put("merchantName", order.getMerchant().getName());
            map.put("shopName", order.getMerchant().getShopName());
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
