package com.market.service;

import com.market.entity.Product;
import com.market.repository.ProductRepository;
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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * 商品服务测试
 */
@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private Product testProduct;
    private List<Product> productList;
    private Page<Product> productPage;

    @BeforeEach
    void setUp() {
        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("测试商品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setCategory("数码电子");
        testProduct.setStatus(1);

        productList = Arrays.asList(testProduct);
        productPage = new PageImpl<>(productList);
    }

    @Test
    void testGetProducts() {
        // Arrange
        when(productRepository.findByStatus(eq(1), any(PageRequest.class))).thenReturn(productPage);

        // Act
        Page<Product> result = productService.getProducts(PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        assertEquals("测试商品", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findByStatus(eq(1), any(PageRequest.class));
    }

    @Test
    void testGetProductsByCategory() {
        // Arrange
        when(productRepository.findByCategoryAndStatus(eq("数码电子"), eq(1), any(PageRequest.class))).thenReturn(productPage);

        // Act
        Page<Product> result = productService.getProductsByCategory("数码电子", PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).findByCategoryAndStatus(eq("数码电子"), eq(1), any(PageRequest.class));
    }

    @Test
    void testGetProductDetail() {
        // Arrange
        when(productRepository.findById(1L)).thenReturn(Optional.of(testProduct));

        // Act
        Product result = productService.getProductDetail(1L);

        // Assert
        assertNotNull(result);
        assertEquals("测试商品", result.getName());
        assertEquals(new BigDecimal("99.99"), result.getPrice());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetProductDetailNotFound() {
        // Arrange
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> productService.getProductDetail(999L));
        verify(productRepository, times(1)).findById(999L);
    }

    @Test
    void testGetCategories() {
        // Act
        List<String> categories = productService.getCategories();

        // Assert
        assertNotNull(categories);
        assertTrue(categories.size() > 0);
        assertTrue(categories.contains("数码电子"));
        assertTrue(categories.contains("服装鞋帽"));
    }

    @Test
    void testSearchProducts() {
        // Arrange
        when(productRepository.searchProducts(eq("测试"), any(PageRequest.class))).thenReturn(productPage);

        // Act
        Page<Product> result = productService.searchProducts("测试", PageRequest.of(0, 10));

        // Assert
        assertNotNull(result);
        assertEquals(1, result.getContent().size());
        verify(productRepository, times(1)).searchProducts(eq("测试"), any(PageRequest.class));
    }

    @Test
    void testGetProductsByIds() {
        // Arrange
        List<Long> ids = Arrays.asList(1L, 2L);
        when(productRepository.findAllById(ids)).thenReturn(productList);

        // Act
        List<Product> result = productService.getProductsByIds(ids);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        verify(productRepository, times(1)).findAllById(ids);
    }
}
