package com.market.service;

import com.market.entity.Order;
import com.market.entity.OrderItem;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.repository.*;
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
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 订单服务测试
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private CreditService creditService;

    @Mock
    private CouponService couponService;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private OrderService orderService;

    private User testUser;
    private Product testProduct;
    private Order testOrder;
    private OrderItem testOrderItem;
    private List<Order> orderList;
    private Page<Order> orderPage;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setStock(100);

        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setOrderNo("ORD20260402001");
        testOrder.setUser(testUser);
        testOrder.setStatus("PENDING");
        testOrder.setTotalAmount(new BigDecimal("199.98"));

        testOrderItem = new OrderItem();
        testOrderItem.setId(1L);
        testOrderItem.setOrder(testOrder);
        testOrderItem.setProduct(testProduct);
        testOrderItem.setQuantity(2);
        testOrderItem.setPrice(new BigDecimal("99.99"));

        orderList = Arrays.asList(testOrder);
        orderPage = new PageImpl<>(orderList);
    }

    @Test
    void testGetUserOrders() {
        // Arrange
        when(orderRepository.findByUser(eq(testUser), any())).thenReturn(orderPage);

        // Act
        Page<Order> result = orderService.getUserOrders(testUser, null, PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("PENDING", result.getContent().get(0).getStatus());
        verify(orderRepository, times(1)).findByUser(eq(testUser), any());
    }

    @Test
    void testGetUserOrdersWithStatus() {
        // Arrange
        when(orderRepository.findByUserAndStatus(eq(testUser), eq("PAID"), any())).thenReturn(orderPage);

        // Act
        Page<Order> result = orderService.getUserOrders(testUser, "PAID", PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(orderRepository, times(1)).findByUserAndStatus(eq(testUser), eq("PAID"), any());
    }

    @Test
    void testGetOrderById() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Optional<Order> result = orderService.getOrderById(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals("ORD20260402001", result.get().getOrderNo());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testGetOrderByIdNotFound() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act
        Optional<Order> result = orderService.getOrderById(999L);

        // Assert
        assertFalse(result.isPresent());
        verify(orderRepository, times(1)).findById(999L);
    }

    @Test
    void testGetUserOrderStats() {
        // Arrange
        when(orderRepository.countByUser(testUser)).thenReturn(5L);
        when(orderRepository.countByUserAndStatus(testUser, "PENDING")).thenReturn(1L);
        when(orderRepository.countByUserAndStatus(testUser, "PAID")).thenReturn(2L);
        when(orderRepository.countByUserAndStatus(testUser, "SHIPPED")).thenReturn(1L);
        when(orderRepository.countByUserAndStatus(testUser, "COMPLETED")).thenReturn(1L);
        when(orderRepository.countByUserAndStatus(testUser, "CANCELLED")).thenReturn(0L);
        when(orderRepository.countByUserAndStatus(testUser, "REFUNDING")).thenReturn(0L);

        // Act
        Map<String, Object> stats = orderService.getUserOrderStats(testUser);

        // Assert
        assertNotNull(stats);
        assertEquals(5L, stats.get("total"));
        assertEquals(1L, stats.get("pending"));
        assertEquals(2L, stats.get("paid"));
    }

    @Test
    void testCreateOrderWithEmptyItems() {
        // Arrange
        List<Map<String, Object>> emptyItems = new ArrayList<>();

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            orderService.createOrder(testUser, emptyItems, 1L, null));
    }

    @Test
    void testCreateOrderWithInsufficientStock() {
        // Arrange
        testProduct.setStock(1);
        List<Map<String, Object>> items = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("productId", "1");
        item.put("quantity", "5");
        items.add(item);

        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> 
            orderService.createOrder(testUser, items, 1L, null));
    }
}
