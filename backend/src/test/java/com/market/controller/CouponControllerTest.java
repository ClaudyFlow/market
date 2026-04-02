package com.market.controller;

import com.market.common.Result;
import com.market.entity.Coupon;
import com.market.entity.User;
import com.market.entity.UserCoupon;
import com.market.service.CouponService;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 优惠券控制器测试
 */
@ExtendWith(MockitoExtension.class)
class CouponControllerTest {

    @Mock
    private CouponService couponService;

    @InjectMocks
    private CouponController couponController;

    private User testUser;
    private UserCoupon testUserCoupon;
    private List<UserCoupon> couponList;
    private Page<UserCoupon> couponPage;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        testUserCoupon = new UserCoupon();
        testUserCoupon.setId(1L);
        testUserCoupon.setUser(testUser);
        testUserCoupon.setStatus("UNUSED");

        couponList = Arrays.asList(testUserCoupon);
        couponPage = new PageImpl<>(couponList);
    }

    @Test
    void testGetCoupons() {
        // Arrange
        when(couponService.getUserCoupons(eq(testUser), any(), any())).thenReturn(couponPage);

        // Act
        Result<Page<UserCoupon>> result = couponController.getCoupons(testUser, 1, 10, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().getContent().size());
        verify(couponService, times(1)).getUserCoupons(eq(testUser), any(), any());
    }

    @Test
    void testGetCouponsWithNullUser() {
        // Act
        Result<Page<UserCoupon>> result = couponController.getCoupons(null, 1, 10, null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetCouponTemplates() {
        // Arrange - 返回空页面
        Page<Coupon> emptyPage = Page.empty();
        
        // Act
        Result<Page<Coupon>> result = couponController.getCouponTemplates(1, 10, null);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
    }

    @Test
    void testReceiveCoupon() {
        // Arrange
        when(couponService.takeCoupon(anyLong(), eq(testUser))).thenReturn(testUserCoupon);

        // Act
        Result<UserCoupon> result = couponController.receiveCoupon(1L, testUser);

        // Assert
        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(couponService, times(1)).takeCoupon(anyLong(), eq(testUser));
    }

    @Test
    void testReceiveCouponWithNullUser() {
        // Act
        Result<UserCoupon> result = couponController.receiveCoupon(1L, null);

        // Assert
        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }
}
