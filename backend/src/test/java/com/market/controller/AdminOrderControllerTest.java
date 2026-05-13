package com.market.controller;

import com.market.common.Result;
import com.market.entity.Order;
import com.market.entity.OrderItem;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 管理员订单控制器测试
 */
@ExtendWith(MockitoExtension.class)
class AdminOrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private AdminOrderController adminOrderController;

    private User testUser;
    private User testMerchant;
    private Order testOrder;
    private List<Order> orderList;
    private Page<Order> orderPage;

    @BeforeEach
    void setUp() {
        // 创建普通用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
        testUser.setRole("USER");

        // 创建商家用户
        testMerchant = new User();
        testMerchant.setId(2L);
        testMerchant.setName("testmerchant");
        testMerchant.setRole("MERCHANT");
        testMerchant.setIsMerchant(true);
        testMerchant.setShopName("测试店铺");

        // 创建商品
        Product testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));

        // 创建订单项
        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("99.99"));

        // 创建订单
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD20260403001");
        testOrder.setUser(testUser);
        testOrder.setMerchant(testMerchant);
        testOrder.setStatus("PAID");
        testOrder.setTotalAmount(new BigDecimal("199.98"));
        testOrder.setPaymentMethod("ALIPAY");
        testOrder.setShippingAddress("测试地址");
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.addItem(orderItem);

        orderList = Arrays.asList(testOrder);
        orderPage = new PageImpl<>(orderList);
    }

    @Test
    void testGetOrderList() {
        // Arrange
        when(orderService.getAllOrders(any(), any(), any(), any(), any(), any(), any(), any(), any(), any()))
            .thenReturn(orderPage);

        // Act
        Result<Map<String, Object>> result = adminOrderController.getOrderList(
            1, 10, null, null, null, null, null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().get("total"));
        verify(orderService, times(1)).getAllOrders(
            any(), any(), any(), any(), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testGetOrderDetail() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Result<Map<String, Object>> result = adminOrderController.getOrderDetail(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("ORD20260403001", result.getData().get("orderNo"));
        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testGetOrderDetailNotFound() {
        // Arrange
        when(orderService.getOrderById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            adminOrderController.getOrderDetail(999L);
        });
    }

    @Test
    void testGetOrderStats() {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 1000L);
        stats.put("totalAmount", new BigDecimal("100000.00"));
        stats.put("pending", 50L);
        stats.put("paid", 500L);
        stats.put("shipped", 300L);
        stats.put("completed", 150L);
        when(orderService.getAdminOrderStats()).thenReturn(stats);

        // Act
        Result<Map<String, Object>> result = adminOrderController.getOrderStats();

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1000L, result.getData().get("total"));
        verify(orderService, times(1)).getAdminOrderStats();
    }

    @Test
    void testGetProductRank() {
        // Arrange
        List<Map<String, Object>> rank = new ArrayList<>();
        Map<String, Object> product1 = new HashMap<>();
        product1.put("productId", 1L);
        product1.put("productName", "测试商品");
        product1.put("salesCount", 100L);
        rank.add(product1);
        when(orderService.getProductSalesRank(10)).thenReturn(rank);

        // Act
        Result<List<Map<String, Object>>> result = adminOrderController.getProductRank(10);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(orderService, times(1)).getProductSalesRank(10);
    }

    @Test
    void testGetShopRank() {
        // Arrange
        List<Map<String, Object>> rank = new ArrayList<>();
        Map<String, Object> shop1 = new HashMap<>();
        shop1.put("shopId", 1L);
        shop1.put("shopName", "测试店铺");
        shop1.put("salesCount", 200L);
        rank.add(shop1);
        when(orderService.getShopSalesRank(10)).thenReturn(rank);

        // Act
        Result<List<Map<String, Object>>> result = adminOrderController.getShopRank(10);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(orderService, times(1)).getShopSalesRank(10);
    }

    @Test
    void testGetRefundList() {
        // Arrange
        testOrder.setStatus("REFUNDING");
        Page<Order> refundPage = new PageImpl<>(orderList);
        when(orderService.getRefundOrders((String) any(), any())).thenReturn(refundPage);

        // Act
        Result<Map<String, Object>> result = adminOrderController.getRefundList(1, 10, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().get("total"));
        verify(orderService, times(1)).getRefundOrders((String) any(), any());
    }

    @Test
    void testHandleRefund() {
        // Arrange
        doNothing().when(orderService).handleRefund(1L, true, "同意退款");

        // Act
        Result<Void> result = adminOrderController.handleRefund(1L, true, "同意退款");

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(orderService, times(1)).handleRefund(1L, true, "同意退款");
    }

    @Test
    void testUpdateOrderStatus() {
        // Arrange
        when(orderService.updateOrderStatus(1L, "SHIPPED", "已发货")).thenReturn(testOrder);

        // Act
        Result<Order> result = adminOrderController.updateOrderStatus(1L, "SHIPPED", "已发货");

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1L, result.getData().getId());
        verify(orderService, times(1)).updateOrderStatus(1L, "SHIPPED", "已发货");
    }

    @Test
    void testGetOrderTrend() {
        // Arrange
        List<Map<String, Object>> trend = new ArrayList<>();
        Map<String, Object> day1 = new HashMap<>();
        day1.put("date", "2026-04-01");
        day1.put("count", 50L);
        day1.put("amount", new BigDecimal("5000.00"));
        trend.add(day1);
        when(orderService.getOrderTrend(7)).thenReturn(trend);

        // Act
        Result<List<Map<String, Object>>> result = adminOrderController.getOrderTrend(7);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(orderService, times(1)).getOrderTrend(7);
    }
}
