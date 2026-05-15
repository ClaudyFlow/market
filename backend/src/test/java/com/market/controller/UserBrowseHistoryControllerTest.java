package com.market.controller;

import com.market.dto.UserBrowseHistoryResponse;
import com.market.entity.User;
import com.market.service.UserBrowseHistoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 用户浏览历史控制器测试
 */
@ExtendWith(MockitoExtension.class)
class UserBrowseHistoryControllerTest {

    @Mock
    private UserBrowseHistoryService userBrowseHistoryService;

    @InjectMocks
    private UserBrowseHistoryController userBrowseHistoryController;

    private User testUser;
    private UserBrowseHistoryResponse testHistory;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");

        testHistory = new UserBrowseHistoryResponse();
        testHistory.setId(1L);
        testHistory.setUserId(1L);
        testHistory.setProductId(100L);
        testHistory.setProductName("测试商品");
        testHistory.setProductImage("/product.jpg");
        testHistory.setBrowseTime(LocalDateTime.now());
    }

    @Test
    void testGetBrowseHistory() {
        List<UserBrowseHistoryResponse> histories = Arrays.asList(testHistory);
        Page<UserBrowseHistoryResponse> historyPage = new PageImpl<>(histories);
        when(userBrowseHistoryService.getBrowseHistory(eq(1L), any(Pageable.class))).thenReturn(historyPage);

        ResponseEntity<Page<UserBrowseHistoryResponse>> response = userBrowseHistoryController.getBrowseHistory(
            0, 20, testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        verify(userBrowseHistoryService, times(1)).getBrowseHistory(eq(1L), any(Pageable.class));
    }

    @Test
    void testGetAllHistory() {
        List<UserBrowseHistoryResponse> histories = Arrays.asList(testHistory);
        when(userBrowseHistoryService.getBrowseHistoryList(1L)).thenReturn(histories);

        ResponseEntity<List<UserBrowseHistoryResponse>> response = userBrowseHistoryController.getAllHistory(testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(userBrowseHistoryService, times(1)).getBrowseHistoryList(1L);
    }

    @Test
    void testGetRecentHistory() {
        List<UserBrowseHistoryResponse> histories = Arrays.asList(testHistory);
        when(userBrowseHistoryService.getRecentHistory(1L, 10)).thenReturn(histories);

        ResponseEntity<List<UserBrowseHistoryResponse>> response = userBrowseHistoryController.getRecentHistory(
            10, testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals(1, response.getBody().size());
        verify(userBrowseHistoryService, times(1)).getRecentHistory(1L, 10);
    }

    @Test
    void testDeleteHistory() {
        when(userBrowseHistoryService.deleteHistory(1L, 100L)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = userBrowseHistoryController.deleteHistory(
            100L, testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        verify(userBrowseHistoryService, times(1)).deleteHistory(1L, 100L);
    }

    @Test
    void testClearHistory() {
        when(userBrowseHistoryService.clearHistory(1L)).thenReturn(true);

        ResponseEntity<Map<String, Object>> response = userBrowseHistoryController.clearHistory(testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(true, response.getBody().get("success"));
        verify(userBrowseHistoryService, times(1)).clearHistory(1L);
    }

    @Test
    void testGetHistoryCount() {
        when(userBrowseHistoryService.getHistoryCount(1L)).thenReturn(50L);

        ResponseEntity<Map<String, Long>> response = userBrowseHistoryController.getHistoryCount(testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(50L, response.getBody().get("count"));
    }

    @Test
    void testSearchHistory() {
        List<UserBrowseHistoryResponse> histories = Arrays.asList(testHistory);
        Page<UserBrowseHistoryResponse> historyPage = new PageImpl<>(histories);
        when(userBrowseHistoryService.searchHistory(eq(1L), eq("测试"), any(Pageable.class))).thenReturn(historyPage);

        ResponseEntity<Page<UserBrowseHistoryResponse>> response = userBrowseHistoryController.searchHistory(
            "测试", 0, 20, testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().getContent().size());
        verify(userBrowseHistoryService, times(1)).searchHistory(eq(1L), eq("测试"), any(Pageable.class));
    }
}
