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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户端订单控制器测试
 */
@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    private User testUser;
    private Order testOrder;
    private List<Order> orderList;
    private Page<Order> orderPage;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
        testUser.setRole("USER");

        Product testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setProduct(testProduct);
        orderItem.setQuantity(2);
        orderItem.setPrice(new BigDecimal("99.99"));

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD20260403001");
        testOrder.setUser(testUser);
        testOrder.setStatus("PENDING");
        testOrder.setTotalAmount(new BigDecimal("199.98"));
        testOrder.setCreatedAt(LocalDateTime.now());
        testOrder.addItem(orderItem);

        orderList = Arrays.asList(testOrder);
        orderPage = new PageImpl<>(orderList);
    }

    @Test
    void testCreateOrder() {
        Map<String, Object> data = new HashMap<>();
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", "1");
        item.put("quantity", "2");
        items.add(item);
        data.put("items", items);
        data.put("addressId", "1");

        when(orderService.createOrder(eq(testUser), eq(items), eq(1L), isNull())).thenReturn(testOrder);

        Result<Order> result = orderController.createOrder(testUser, data);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1L, result.getData().getId());
        verify(orderService, times(1)).createOrder(eq(testUser), eq(items), eq(1L), isNull());
    }

    @Test
    void testCreateOrderWithNullUser() {
        Map<String, Object> data = new HashMap<>();
        Result<Order> result = orderController.createOrder(null, data);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetOrderList() {
        when(orderService.getUserOrders(eq(testUser), any(), any())).thenReturn(orderPage);

        Result<Map<String, Object>> result = orderController.getOrderList(1, 10, null, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1L, result.getData().get("total"));
        verify(orderService, times(1)).getUserOrders(eq(testUser), any(), any());
    }

    @Test
    void testGetOrderListWithNullUser() {
        Result<Map<String, Object>> result = orderController.getOrderList(1, 10, null, null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetOrderDetail() {
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        Result<Map<String, Object>> result = orderController.getOrderDetail(1L, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("ORD20260403001", result.getData().get("orderNo"));
    }

    @Test
    void testGetOrderDetailForbidden() {
        User otherUser = new User();
        otherUser.setId(999L);
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        Result<Map<String, Object>> result = orderController.getOrderDetail(1L, otherUser);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
    }

    @Test
    void testCancelOrder() {
        doNothing().when(orderService).cancelOrder(1L, testUser);

        Result<Void> result = orderController.cancelOrder(1L, "不想要了", testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(orderService, times(1)).cancelOrder(1L, testUser);
    }

    @Test
    void testDeleteOrder() {
        doNothing().when(orderService).deleteOrder(1L, testUser);

        Result<Void> result = orderController.deleteOrder(1L, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(orderService, times(1)).deleteOrder(1L, testUser);
    }

    @Test
    void testPayOrder() {
        testOrder.setStatus("PAID");
        when(orderService.payOrder(1L, testUser, "alipay")).thenReturn(testOrder);

        Result<Order> result = orderController.payOrder(1L, "alipay", testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(orderService, times(1)).payOrder(1L, testUser, "alipay");
    }

    @Test
    void testGetPayStatus() {
        testOrder.setPaidAt(LocalDateTime.now());
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));

        Result<Map<String, Object>> result = orderController.getPayStatus(1L, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(true, result.getData().get("paid"));
    }

    @Test
    void testConfirmReceive() {
        testOrder.setStatus("SHIPPED");
        when(orderService.confirmReceive(1L, testUser)).thenReturn(testOrder);

        Result<Order> result = orderController.confirmReceive(1L, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(orderService, times(1)).confirmReceive(1L, testUser);
    }

    @Test
    void testApplyRefund() {
        doNothing().when(orderService).applyRefund(eq(1L), eq(testUser), eq("质量问题"), any());

        Result<Void> result = orderController.applyRefund(1L, "质量问题", null, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(orderService, times(1)).applyRefund(eq(1L), eq(testUser), eq("质量问题"), any());
    }

    @Test
    void testGetOrderLogistics() {
        testOrder.setTrackingNo("SF123456");
        testOrder.setCarrier("顺丰");
        when(orderService.getOrderById(1L)).thenReturn(Optional.of(testOrder));
        when(orderService.getTrackingRecords(testOrder)).thenReturn(new ArrayList<>());

        Result<Map<String, Object>> result = orderController.getOrderLogistics(1L, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("SF123456", result.getData().get("trackingNo"));
    }

    @Test
    void testGetOrderStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 10L);
        stats.put("pending", 2L);
        stats.put("paid", 5L);
        when(orderService.getUserOrderStats(testUser)).thenReturn(stats);

        Result<Map<String, Object>> result = orderController.getOrderStats(testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(10L, result.getData().get("total"));
    }
}
