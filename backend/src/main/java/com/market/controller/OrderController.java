package com.market.controller;

import com.market.entity.Order;
import com.market.entity.User;
import com.market.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 订单控制器
 */
@RestController
@RequestMapping("/api/order")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    @GetMapping
    public ResponseEntity<List<Order>> getUserOrders(@AuthenticationPrincipal User user) {
        List<Order> order = orderService.getUserOrders(user);
        return ResponseEntity.ok(order);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        return orderService.getOrderById(id)
            .filter(order -> order.getUser().getId().equals(user.getId()))
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PostMapping
    public ResponseEntity<?> createOrder(
            @AuthenticationPrincipal User user,
            @RequestParam String shippingAddress) {
        try {
            Order order = orderService.createOrder(user, shippingAddress);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/pay")
    public ResponseEntity<?> payOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            Order order = orderService.payOrder(id, user);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @PostMapping("/{id}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {
        try {
            Order order = orderService.cancelOrder(id, user);
            return ResponseEntity.ok(order);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getOrderStats(@AuthenticationPrincipal User user) {
        List<Order> order = orderService.getUserOrders(user);

        Map<String, Object> stats = new HashMap<>();
        stats.put("totalOrders", order.size());

        long pendingCount = order.stream().filter(o -> "PENDING".equals(o.getStatus())).count();
        long paidCount = order.stream().filter(o -> "PAID".equals(o.getStatus())).count();
        long completedCount = order.stream().filter(o -> "COMPLETED".equals(o.getStatus())).count();

        stats.put("pendingOrders", pendingCount);
        stats.put("paidOrders", paidCount);
        stats.put("completedOrders", completedCount);

        return ResponseEntity.ok(stats);
    }
}
