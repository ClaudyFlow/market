package com.market.controller;

import com.market.common.Result;
import com.market.entity.Product;
import com.market.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 商品控制器测试
 */
@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    @InjectMocks
    private ProductController productController;

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
        when(productService.getProducts(any(PageRequest.class))).thenReturn(productPage);

        // Act
        Result<Page<Product>> result = productController.getProducts(1, 10, null, null, null, null, "default");

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().getContent().size());
        assertEquals("测试商品", result.getData().getContent().get(0).getName());
        verify(productService, times(1)).getProducts(any(PageRequest.class));
    }

    @Test
    void testGetProductsByCategory() {
        // Arrange
        when(productService.getProductsByCategory(eq("数码电子"), any(PageRequest.class))).thenReturn(productPage);

        // Act
        Result<Page<Product>> result = productController.getProducts(1, 10, "数码电子", null, null, null, "default");

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(productService, times(1)).getProductsByCategory(eq("数码电子"), any(PageRequest.class));
    }

    @Test
    void testGetProductDetail() {
        // Arrange
        when(productService.getProductDetail(1L)).thenReturn(testProduct);

        // Act
        Result<Product> result = productController.getProductDetail(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("测试商品", result.getData().getName());
        assertEquals(new BigDecimal("99.99"), result.getData().getPrice());
        verify(productService, times(1)).getProductDetail(1L);
    }

    @Test
    void testSearchProducts() {
        // Arrange
        when(productService.searchProducts(eq("测试"), any(PageRequest.class))).thenReturn(productPage);

        // Act
        Result<Page<Product>> result = productController.searchProducts("测试", 1, 10, "default");

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(productService, times(1)).searchProducts(eq("测试"), any(PageRequest.class));
    }
}
