package com.market.controller;

import com.market.common.Result;
import com.market.dto.UserNotificationResponse;
import com.market.entity.User;
import com.market.service.UserNotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户通知控制器测试
 */
@ExtendWith(MockitoExtension.class)
class UserNotificationControllerTest {

    @Mock
    private UserNotificationService notificationService;

    @InjectMocks
    private UserNotificationController userNotificationController;

    private User testUser;
    private UserNotificationResponse testNotification;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        testNotification = new UserNotificationResponse();
        testNotification.setId(1L);
        testNotification.setUserId(1L);
        testNotification.setTitle("系统通知");
        testNotification.setContent("这是一条测试通知");
        testNotification.setType("SYSTEM");
        testNotification.setIsRead(false);
        testNotification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void testGetNotificationsNoUser() {
        Result<Page<UserNotificationResponse>> result = userNotificationController.getNotifications(
            null, 1, 10, null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetNotificationDetailNoUser() {
        Result<UserNotificationResponse> result = userNotificationController.getNotificationDetail(1L, null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testMarkAsReadNoUser() {
        Result<Integer> result = userNotificationController.markAsRead(1L, null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testBatchMarkAsReadNoUser() {
        Result<Integer> result = userNotificationController.batchMarkAsRead(Arrays.asList(1L, 2L), null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testMarkAllAsReadNoUser() {
        Result<Integer> result = userNotificationController.markAllAsRead(null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testDeleteNotificationNoUser() {
        Result<Void> result = userNotificationController.deleteNotification(1L, null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testBatchDeleteNotificationsNoUser() {
        Result<Integer> result = userNotificationController.batchDeleteNotifications(
            Arrays.asList(1L, 2L), null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testClearNotificationsNoUser() {
        Result<Integer> result = userNotificationController.clearNotifications(null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetUnreadCount() {
        Result<Map<String, Long>> result = userNotificationController.getUnreadCount(null);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(0L, result.getData().get("count"));
    }

    @Test
    void testGetNotificationStats() {
        Result<Map<String, Object>> result = userNotificationController.getNotificationStats(testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().get("total"));
        assertEquals(0, result.getData().get("unread"));
    }

    @Test
    void testGetNotificationStatsNoUser() {
        Result<Map<String, Object>> result = userNotificationController.getNotificationStats(null);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(0, result.getData().get("total"));
        assertEquals(0, result.getData().get("unread"));
    }

    @Test
    void testGetSystemNotificationsNoUser() {
        Result<Page<UserNotificationResponse>> result = userNotificationController.getSystemNotifications(
            null, 1, 10);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetActivityNotificationsNoUser() {
        Result<Page<UserNotificationResponse>> result = userNotificationController.getActivityNotifications(
            null, 1, 10);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetOrderNotificationsNoUser() {
        Result<Page<UserNotificationResponse>> result = userNotificationController.getOrderNotifications(
            null, 1, 10);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetLatestNotificationsNoUser() {
        Result<List<UserNotificationResponse>> result = userNotificationController.getLatestNotifications(
            null, 5);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testGetNotificationPreference() {
        Result<Map<String, Boolean>> result = userNotificationController.getNotificationPreference(
            testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(true, result.getData().get("systemNotify"));
        assertEquals(true, result.getData().get("activityNotify"));
        assertEquals(true, result.getData().get("orderNotify"));
        assertEquals(true, result.getData().get("promoNotify"));
    }

    @Test
    void testGetNotificationPreferenceNoUser() {
        Result<Map<String, Boolean>> result = userNotificationController.getNotificationPreference(
            null);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals(false, result.getData().get("systemNotify"));
        assertEquals(false, result.getData().get("activityNotify"));
    }

    @Test
    void testSetNotificationPreferenceNoUser() {
        Map<String, Boolean> preference = Map.of(
            "systemNotify", true,
            "activityNotify", false
        );

        Result<Void> result = userNotificationController.setNotificationPreference(preference, null);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }
}
