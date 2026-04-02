package com.market.controller;

import com.market.common.Result;
import com.market.entity.Shop;
import com.market.service.ShopService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * 店铺控制器测试
 */
@ExtendWith(MockitoExtension.class)
class ShopControllerTest {

    @Mock
    private ShopService shopService;

    @InjectMocks
    private ShopController shopController;

    private Shop testShop;
    private List<Shop> shopList;
    private Page<Shop> shopPage;

    @BeforeEach
    void setUp() {
        testShop = new Shop();
        testShop.setId(1L);
        testShop.setName("测试店铺");
        testShop.setDescription("这是一个测试店铺");
        testShop.setRating(4.5);
        testShop.setCreatedAt(LocalDateTime.now());

        shopList = Arrays.asList(testShop);
        shopPage = new PageImpl<>(shopList);
    }

    @Test
    void testGetShops() {
        // Arrange
        when(shopService.getShops(any())).thenReturn(shopPage);

        // Act
        Result<Page<Shop>> result = shopController.getShops(1, 10, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().getContent().size());
        assertEquals("测试店铺", result.getData().getContent().get(0).getName());
        verify(shopService, times(1)).getShops(any());
    }

    @Test
    void testGetShopDetail() {
        // Arrange
        when(shopService.getShopDetail(1L)).thenReturn(testShop);

        // Act
        Result<Shop> result = shopController.getShopDetail(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("测试店铺", result.getData().getName());
        verify(shopService, times(1)).getShopDetail(1L);
    }

    @Test
    void testGetShopStats() {
        // Arrange
        when(shopService.getShopStats(1L)).thenReturn(Map.of(
            "totalProducts", 100,
            "totalSales", 500,
            "rating", 4.5
        ));

        // Act
        Result<Map<String, Object>> result = shopController.getShopStats(1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(100, result.getData().get("totalProducts"));
        verify(shopService, times(1)).getShopStats(1L);
    }
}
