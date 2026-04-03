package com.market.controller;

import com.market.entity.MessageReceive;
import com.market.entity.SystemMessage;
import com.market.entity.User;
import com.market.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 消息控制器测试（包含用户和管理员操作）
 */
@ExtendWith(MockitoExtension.class)
class MessageControllerTest {

    @Mock
    private MessageService messageService;

    @InjectMocks
    private MessageController messageController;

    private User testUser;
    private User testAdmin;
    private SystemMessage testMessage;
    private MessageReceive testReceive;
    private List<MessageReceive> receiveList;
    private Page<MessageReceive> messagePage;

    @BeforeEach
    void setUp() {
        // 创建普通用户
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
        testUser.setRole("USER");

        // 创建管理员用户
        testAdmin = new User();
        testAdmin.setId(999L);
        testAdmin.setName("admin");
        testAdmin.setRole("ADMIN");

        // 创建系统消息
        testMessage = new SystemMessage();
        testMessage.setId(1L);
        testMessage.setTitle("测试消息");
        testMessage.setContent("这是一条测试消息");
        testMessage.setType("SYSTEM");
        testMessage.setPriority(3);
        testMessage.setJumpUrl("/test");
        testMessage.setImageUrl("/images/test.jpg");
        testMessage.setSendTime(LocalDateTime.now());

        // 创建消息接收记录
        testReceive = new MessageReceive();
        testReceive.setId(1L);
        testReceive.setUserId(testUser.getId());
        testReceive.setMessageId(testMessage.getId());
        testReceive.setIsRead(false);
        testReceive.setCreatedAt(LocalDateTime.now());

        receiveList = Arrays.asList(testReceive);
        messagePage = new PageImpl<>(receiveList);
    }

    // ==================== 用户端消息操作测试 ====================

    @Test
    void testGetMessages() {
        // Arrange
        when(messageService.getUserMessages(eq(testUser), any())).thenReturn(messagePage);
        when(messageService.getMessageDetail(1L)).thenReturn(testMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.getMessages(1, 10, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().get("total"));
        verify(messageService, times(1)).getUserMessages(eq(testUser), any());
    }

    @Test
    void testGetUnreadMessages() {
        // Arrange
        when(messageService.getUnreadMessages(eq(testUser), any())).thenReturn(messagePage);
        when(messageService.getMessageDetail(1L)).thenReturn(testMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.getUnreadMessages(1, 10, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1L, response.getBody().get("total"));
        verify(messageService, times(1)).getUnreadMessages(eq(testUser), any());
    }

    @Test
    void testGetUnreadCount() {
        // Arrange
        when(messageService.getUnreadCount(testUser)).thenReturn(5L);

        // Act
        ResponseEntity<Map<String, Long>> response = messageController.getUnreadCount(testUser);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(5L, response.getBody().get("count"));
        verify(messageService, times(1)).getUnreadCount(testUser);
    }

    @Test
    void testMarkAsRead() {
        // Arrange
        List<Long> receiveIds = Arrays.asList(1L, 2L, 3L);
        when(messageService.markAsRead(eq(testUser), eq(receiveIds))).thenReturn(3);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.markAsRead(receiveIds, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(3, response.getBody().get("count"));
        verify(messageService, times(1)).markAsRead(eq(testUser), eq(receiveIds));
    }

    @Test
    void testMarkAllAsRead() {
        // Arrange
        when(messageService.markAllAsRead(testUser)).thenReturn(10);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.markAllAsRead(testUser);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(10, response.getBody().get("count"));
        verify(messageService, times(1)).markAllAsRead(testUser);
    }

    @Test
    void testGetMessageDetail() {
        // Arrange
        when(messageService.getMessageDetail(1L)).thenReturn(testMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.getMessageDetail(1L, testUser);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("测试消息", response.getBody().get("title"));
        assertEquals("这是一条测试消息", response.getBody().get("content"));
        verify(messageService, times(1)).getMessageDetail(1L);
    }

    @Test
    void testGetMessageStats() {
        // Arrange
        Map<String, Object> stats = new HashMap<>();
        stats.put("total", 50L);
        stats.put("unread", 5L);
        stats.put("read", 45L);
        when(messageService.getMessageStats(testUser)).thenReturn(stats);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.getMessageStats(testUser);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(50L, response.getBody().get("total"));
        verify(messageService, times(1)).getMessageStats(testUser);
    }

    // ==================== 管理员端消息操作测试 ====================

    @Test
    void testSendMessage() {
        // Arrange
        when(messageService.sendMessage(eq("系统通知"), eq("这是一条系统通知"), eq("SYSTEM"), any(), any(), any()))
            .thenReturn(testMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.sendMessage(
            "系统通知", "这是一条系统通知", "SYSTEM", 3, "/test", "/images/test.jpg", testAdmin);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(1L, response.getBody().get("messageId"));
        verify(messageService, times(1)).sendMessage(
            eq("系统通知"), eq("这是一条系统通知"), eq("SYSTEM"), any(), any(), any());
    }

    @Test
    void testSendMessageToUser() {
        // Arrange
        when(messageService.sendMessageToUser(eq("个人通知"), eq("这是一条个人通知"), eq("SYSTEM"), eq(1L), any(), any()))
            .thenReturn(testMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.sendMessageToUser(
            1L, "个人通知", "这是一条个人通知", "SYSTEM", 3, "/test", testAdmin);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(1L, response.getBody().get("messageId"));
        verify(messageService, times(1)).sendMessageToUser(
            eq("个人通知"), eq("这是一条个人通知"), eq("SYSTEM"), eq(1L), any(), any());
    }

    @Test
    void testSendMessageToUsers() {
        // Arrange
        List<Long> userIds = Arrays.asList(1L, 2L, 3L);
        when(messageService.sendMessageToUsers(eq("批量通知"), eq("这是一条批量通知"), eq("SYSTEM"), eq(userIds), any(), any()))
            .thenReturn(testMessage);

        // Act
        ResponseEntity<Map<String, Object>> response = messageController.sendMessageToUsers(
            userIds, "批量通知", "这是一条批量通知", "SYSTEM", 3, "/test", testAdmin);

        // Assert
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        assertEquals(1L, response.getBody().get("messageId"));
        verify(messageService, times(1)).sendMessageToUsers(
            eq("批量通知"), eq("这是一条批量通知"), eq("SYSTEM"), eq(userIds), any(), any());
    }
}
