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
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 商家订单控制器测试
 */
@ExtendWith(MockitoExtension.class)
class MerchantOrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private MerchantOrderController merchantOrderController;

    private User testMerchant;
    private User testCustomer;
    private Order testOrder;
    private List<Order> orderList;
    private Page<Order> orderPage;

    @BeforeEach
    void setUp() {
        // 创建商家用户
        testMerchant = new User();
        testMerchant.setId(1L);
        testMerchant.setName("testmerchant");
        testMerchant.setRole("MERCHANT");
        testMerchant.setIsMerchant(true);
        testMerchant.setShopName("测试店铺");

        // 创建普通用户
        testCustomer = new User();
        testCustomer.setId(2L);
        testCustomer.setName("testcustomer");
        testCustomer.setRole("USER");

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
        testOrder.setUser(testCustomer);
        testOrder.setMerchant(testMerchant);
        testOrder.setStatus("PAID");
        testOrder.setTotalAmount(new BigDecimal("199.98"));
        testOrder.setShippingAddress("测试地址");
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.addItem(orderItem);

        orderList = Arrays.asList(testOrder);
        orderPage = new PageImpl<>(orderList);
    }

    @Test
    void testGetOrderList() {
        // Arrange
        when(orderService.getMerchantOrders(eq(testMerchant), any(), any(), any(), any(), any(), any()))
            .thenReturn(orderPage);

        // Act
        Result<Map<String, Object>> result = merchantOrderController.getOrderList(
            1, 10, null, null, null, null, null, testMerchant);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().get("total"));
        verify(orderService, times(1)).getMerchantOrders(
            eq(testMerchant), any(), any(), any(), any(), any(), any());
    }

    @Test
    void testGetOrderListWithNullUser() {
        // Act
        Result<Map<String, Object>> result = merchantOrderController.getOrderList(
            1, 10, null, null, null, null, null, null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetOrderDetail() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));
        when(orderService.isMerchantOrder(testOrder, testMerchant)).thenReturn(true);

        // Act
        Result<Map<String, Object>> result = merchantOrderController.getOrderDetail(1L, testMerchant);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("ORD20260403001", result.getData().get("orderNo"));
        verify(orderService, times(1)).getOrderById(1L);
    }

    @Test
    void testGetOrderDetailUnauthorized() {
        // Act
        Result<Map<String, Object>> result = merchantOrderController.getOrderDetail(1L, null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetOrderDetailForbidden() {
        // Arrange
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));
        when(orderService.isMerchantOrder(testOrder, testMerchant)).thenReturn(false);

        // Act
        Result<Map<String, Object>> result = merchantOrderController.getOrderDetail(1L, testMerchant);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
    }

    @Test
    void testGetOrderStats() {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 100L);
        stats.put("pending", 10L);
        stats.put("paid", 50L);
        stats.put("shipped", 30L);
        stats.put("completed", 10L);
        when(orderService.getMerchantOrderStats(testMerchant)).thenReturn(stats);

        // Act
        Result<Map<String, Object>> result = merchantOrderController.getOrderStats(testMerchant);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(100L, result.getData().get("total"));
        verify(orderService, times(1)).getMerchantOrderStats(testMerchant);
    }

    @Test
    void testGetOrderStatsWithNullUser() {
        // Act
        Result<Map<String, Object>> result = merchantOrderController.getOrderStats(null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testShipOrder() {
        // Arrange
        when(orderService.shipOrder(eq(1L), eq(testMerchant), eq("SF123456"), eq("顺丰"), any()))
            .thenReturn(testOrder);

        // Act
        Result<Order> result = merchantOrderController.shipOrder(1L, "SF123456", "顺丰", "测试备注", testMerchant);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
        verify(orderService, times(1)).shipOrder(1L, testMerchant, "SF123456", "顺丰", "测试备注");
    }

    @Test
    void testShipOrderWithNullUser() {
        // Act
        Result<Order> result = merchantOrderController.shipOrder(1L, "SF123456", "顺丰", null, null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetRefundList() {
        // Arrange
        testOrder.setStatus("REFUNDING");
        Page<Order> refundPage = new PageImpl<>(orderList);
        when(orderService.getRefundOrders(eq(testMerchant), any())).thenReturn(refundPage);

        // Act
        Result<Map<String, Object>> result = merchantOrderController.getRefundList(1, 10, testMerchant);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().get("total"));
        verify(orderService, times(1)).getRefundOrders(eq(testMerchant), any());
    }

    @Test
    void testGetRefundListWithNullUser() {
        // Act
        Result<Map<String, Object>> result = merchantOrderController.getRefundList(1, 10, null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testHandleRefund() {
        // Arrange
        doNothing().when(orderService).handleRefund(1L, testMerchant, true, "同意退款");

        // Act
        Result<Void> result = merchantOrderController.handleRefund(1L, true, "同意退款", testMerchant);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(orderService, times(1)).handleRefund(1L, testMerchant, true, "同意退款");
    }

    @Test
    void testHandleRefundWithNullUser() {
        // Act
        Result<Void> result = merchantOrderController.handleRefund(1L, true, null, null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }
}
