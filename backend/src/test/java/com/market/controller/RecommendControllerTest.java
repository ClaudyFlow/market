package com.market.controller;

import com.market.common.Result;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.RecommendService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 推荐控制器测试
 */
@ExtendWith(MockitoExtension.class)
class RecommendControllerTest {

    @Mock
    private RecommendService recommendService;

    @InjectMocks
    private RecommendController recommendController;

    private User testUser;
    private Product testProduct;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        User merchant = new User();
        merchant.setId(10L);
        merchant.setShopName("测试店铺");

        testProduct = new Product();
        testProduct.setId(1L);
        testProduct.setName("推荐商品");
        testProduct.setDescription("这是一个推荐商品");
        testProduct.setPrice(new BigDecimal("99.99"));
        testProduct.setOriginalPrice(new BigDecimal("199.99"));
        testProduct.setImageUrl("/product.jpg");
        testProduct.setCategory("数码电子");
        testProduct.setBrand("测试品牌");
        testProduct.setSales(100);
        testProduct.setRating(4.5);
        testProduct.setReviewCount(50);
        testProduct.setStock(200);
        testProduct.setAvailable(true);
        testProduct.setStatus(1);
        testProduct.setMerchant(merchant);
        testProduct.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetRecommendProducts() {
        when(recommendService.getRecommendProducts(eq(1L), eq(10))).thenReturn(Arrays.asList(testProduct));

        Result<List<Map<String, Object>>> result = recommendController.getRecommendProducts(10, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(1, result.getData().size());
        assertEquals("推荐商品", result.getData().get(0).get("name"));
        verify(recommendService, times(1)).getRecommendProducts(eq(1L), eq(10));
    }

    @Test
    void testGetRecommendProductsNoUser() {
        when(recommendService.getRecommendProducts(isNull(), eq(10))).thenReturn(Arrays.asList(testProduct));

        Result<List<Map<String, Object>>> result = recommendController.getRecommendProducts(10, null);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(recommendService, times(1)).getRecommendProducts(isNull(), eq(10));
    }

    @Test
    void testGetHotProducts() {
        when(recommendService.getHotProducts(10)).thenReturn(Arrays.asList(testProduct));

        Result<List<Map<String, Object>>> result = recommendController.getHotProducts(10);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(recommendService, times(1)).getHotProducts(10);
    }

    @Test
    void testGetViewedAlsoViewed() {
        when(recommendService.getViewedAlsoViewed(eq(1L), eq(100L), eq(6))).thenReturn(Arrays.asList(testProduct));

        Result<List<Map<String, Object>>> result = recommendController.getViewedAlsoViewed(100L, 6, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(recommendService, times(1)).getViewedAlsoViewed(eq(1L), eq(100L), eq(6));
    }

    @Test
    void testGetViewedAlsoViewedNoUser() {
        when(recommendService.getViewedAlsoViewed(isNull(), eq(100L), eq(6))).thenReturn(Arrays.asList(testProduct));

        Result<List<Map<String, Object>>> result = recommendController.getViewedAlsoViewed(100L, 6, null);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
    }

    @Test
    void testGetBoughtAlsoBought() {
        when(recommendService.getBoughtAlsoBought(eq(1L), eq(100L), eq(6))).thenReturn(Arrays.asList(testProduct));

        Result<List<Map<String, Object>>> result = recommendController.getBoughtAlsoBought(100L, 6, testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(recommendService, times(1)).getBoughtAlsoBought(eq(1L), eq(100L), eq(6));
    }

    @Test
    void testGetShopRecommend() {
        when(recommendService.getShopRecommend(eq(10L), eq(6))).thenReturn(Arrays.asList(testProduct));

        Result<List<Map<String, Object>>> result = recommendController.getShopRecommend(10L, 6);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
        verify(recommendService, times(1)).getShopRecommend(eq(10L), eq(6));
    }
}
