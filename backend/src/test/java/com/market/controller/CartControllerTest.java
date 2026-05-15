package com.market.controller;

import com.market.common.Result;
import com.market.entity.CartItem;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.CartService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 购物车控制器测试
 */
@ExtendWith(MockitoExtension.class)
class CartControllerTest {

    @Mock
    private CartService cartService;

    @InjectMocks
    private CartController cartController;

    private User testUser;
    private Product testProduct;
    private CartItem testCartItem;
    private List<CartItem> cartItems;

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

        testCartItem = new CartItem();
        testCartItem.setId(1L);
        testCartItem.setUser(testUser);
        testCartItem.setProduct(testProduct);
        testCartItem.setQuantity(2);

        cartItems = Arrays.asList(testCartItem);
    }

    @Test
    void testGetCartItems() {
        // Arrange
        when(cartService.getCartItems(testUser)).thenReturn(cartItems);

        // Act
        Result<List<CartItem>> result = cartController.getCartItems(testUser);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        assertEquals(1L, result.getData().get(0).getId());
        verify(cartService, times(1)).getCartItems(testUser);
    }

    @Test
    void testGetCartItemsWithNullUser() {
        // Act
        Result<List<CartItem>> result = cartController.getCartItems(null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testAddToCart() {
        // Arrange
        when(cartService.addToCart(eq(testUser), eq(1L), eq(2))).thenReturn(testCartItem);

        // Act
        Result<CartItem> result = cartController.addToCart(testUser, 1L, 2);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1L, result.getData().getId());
        verify(cartService, times(1)).addToCart(eq(testUser), eq(1L), eq(2));
    }

    @Test
    void testAddToCartWithNullUser() {
        // Act
        Result<CartItem> result = cartController.addToCart(null, 1L, 2);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testUpdateQuantity() {
        // Arrange
        when(cartService.updateQuantity(eq(testUser), eq(1L), eq(5))).thenReturn(testCartItem);

        // Act
        Result<CartItem> result = cartController.updateQuantity(testUser, 1L, 5);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(cartService, times(1)).updateQuantity(eq(testUser), eq(1L), eq(5));
    }

    @Test
    void testRemoveFromCart() {
        // Arrange
        doNothing().when(cartService).removeFromCart(testUser, 1L);

        // Act
        Result<Void> result = cartController.removeFromCart(testUser, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(cartService, times(1)).removeFromCart(testUser, 1L);
    }

    @Test
    void testClearCart() {
        // Arrange
        doNothing().when(cartService).clearCart(testUser);

        // Act
        Result<Void> result = cartController.clearCart(testUser);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(cartService, times(1)).clearCart(testUser);
    }
}
