package com.market.service;

import com.market.entity.CartItem;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.repository.CartItemRepository;
import com.market.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 购物车服务测试
 */
@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private CartService cartService;

    private User testUser;
    private Product testProduct;
    private CartItem testCartItem;
    private List<CartItem> existingCartItems;

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

        existingCartItems = Arrays.asList(testCartItem);
    }

    @Test
    void testGetCartItems() {
        // Arrange
        when(cartItemRepository.findByUser(testUser)).thenReturn(existingCartItems);

        // Act
        List<CartItem> result = cartService.getCartItems(testUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(cartItemRepository, times(1)).findByUser(testUser);
    }

    @Test
    void testAddToCartNewItem() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByUser(testUser)).thenReturn(Arrays.asList());
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        // Act
        CartItem result = cartService.addToCart(testUser, 1L, 2);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(productRepository, times(1)).findById(1L);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddToCartExistingItem() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));
        when(cartItemRepository.findByUser(testUser)).thenReturn(existingCartItems);
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        // Act
        CartItem result = cartService.addToCart(testUser, 1L, 2);

        // Assert
        assertNotNull(result);
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testAddToCartProductNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.addToCart(testUser, 999L, 2));
    }

    @Test
    void testAddToCartInsufficientStock() {
        // Arrange
        testProduct.setStock(1);
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> cartService.addToCart(testUser, 1L, 5));
    }

    @Test
    void testUpdateQuantity() {
        // Arrange
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenReturn(testCartItem);

        // Act
        CartItem result = cartService.updateQuantity(testUser, 1L, 5);

        // Assert
        assertNotNull(result);
        assertEquals(5, result.getQuantity());
        verify(cartItemRepository, times(1)).save(any(CartItem.class));
    }

    @Test
    void testUpdateQuantityZeroRemoves() {
        // Arrange
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));

        // Act
        CartItem result = cartService.updateQuantity(testUser, 1L, 0);

        // Assert
        assertNull(result);
        verify(cartItemRepository, times(1)).delete(testCartItem);
    }

    @Test
    void testRemoveFromCart() {
        // Arrange
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(testCartItem));

        // Act
        cartService.removeFromCart(testUser, 1L);

        // Assert
        verify(cartItemRepository, times(1)).delete(testCartItem);
    }

    @Test
    void testClearCart() {
        // Act
        cartService.clearCart(testUser);

        // Assert
        verify(cartItemRepository, times(1)).deleteByUser(testUser);
    }
}
