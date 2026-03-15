package com.market.service;

import com.market.entity.*;
import com.market.repository.OrderRepository;
import com.market.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

/**
 * 订单服务类
 */
@Service
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private CartService cartService;
    
    @Autowired
    private PointsService pointsService;
    
    public List<Order> getUserOrders(User user) {
        return orderRepository.findByUserOrderByCreatedAtDesc(user);
    }
    
    public Optional<Order> getOrderById(Long id) {
        return orderRepository.findById(id);
    }
    
    public Optional<Order> getOrderByOrderNo(String orderNo) {
        return orderRepository.findByOrderNo(orderNo);
    }
    
    @Transactional
    public Order createOrder(User user, String shippingAddress) {
        List<CartItem> cartItems = cartService.getCartItems(user);
        
        if (cartItems.isEmpty()) {
            throw new RuntimeException("购物车为空");
        }
        
        Order order = new Order();
        order.setOrderNo(generateOrderNo());
        order.setUser(user);
        order.setShippingAddress(shippingAddress);
        order.setStatus("PENDING");
        
        BigDecimal totalAmount = BigDecimal.ZERO;
        
        for (CartItem cartItem : cartItems) {
            Product product = cartItem.getProduct();
            Integer quantity = cartItem.getQuantity();
            
            if (product.getStock() < quantity) {
                throw new RuntimeException("商品 " + product.getName() + " 库存不足");
            }
            
            OrderItem orderItem = new OrderItem(product, quantity, product.getPrice());
            order.addItem(orderItem);
            
            totalAmount = totalAmount.add(orderItem.getSubtotal());
            
            product.setStock(product.getStock() - quantity);
            productRepository.save(product);
        }
        
        order.setTotalAmount(totalAmount);
        Order savedOrder = orderRepository.save(order);
        
        cartService.clearCart(user);
        
        int pointsToAdd = totalAmount.intValue() / 10;
        if (pointsToAdd > 0) {
            pointsService.addPoints(user.getId(), pointsToAdd, "订单奖励：" + savedOrder.getOrderNo());
        }
        
        return savedOrder;
    }
    
    @Transactional
    public Order cancelOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> o.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("只能取消待支付订单");
        }
        
        order.setStatus("CANCELLED");
        
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.setStock(product.getStock() + item.getQuantity());
            productRepository.save(product);
        }
        
        return orderRepository.save(order);
    }
    
    @Transactional
    public Order payOrder(Long orderId, User user) {
        Order order = orderRepository.findById(orderId)
            .filter(o -> o.getUser().getId().equals(user.getId()))
            .orElseThrow(() -> new RuntimeException("订单不存在"));
        
        if (!"PENDING".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不正确");
        }
        
        order.setStatus("PAID");
        return orderRepository.save(order);
    }
    
    private String generateOrderNo() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        String timestamp = LocalDateTime.now().format(formatter);
        int random = new Random().nextInt(9000) + 1000;
        return timestamp + random;
    }
}
