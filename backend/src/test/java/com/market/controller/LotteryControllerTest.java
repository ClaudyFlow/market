package com.market.controller;

import com.market.entity.User;
import com.market.service.LotteryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 抽奖控制器测试
 */
@ExtendWith(MockitoExtension.class)
class LotteryControllerTest {

    @Mock
    private LotteryService lotteryService;

    @InjectMocks
    private LotteryController lotteryController;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
    }

    @Test
    void testDraw() {
        LotteryService.LotteryResult drawResult = new LotteryService.LotteryResult();
        drawResult.setPrizeName("一等奖");
        drawResult.setPrizeType(1);
        drawResult.setSuccess(true);

        when(lotteryService.draw(testUser)).thenReturn(drawResult);

        ResponseEntity<LotteryService.LotteryResult> response = lotteryController.draw(testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("一等奖", response.getBody().getPrizeName());
        assertEquals(1, response.getBody().getPrizeType());
        assertTrue(response.getBody().isSuccess());
        verify(lotteryService, times(1)).draw(testUser);
    }

    @Test
    void testDrawNoPrize() {
        LotteryService.LotteryResult drawResult = new LotteryService.LotteryResult();
        drawResult.setPrizeName("未中奖");
        drawResult.setPrizeType(0);
        drawResult.setSuccess(false);

        when(lotteryService.draw(testUser)).thenReturn(drawResult);

        ResponseEntity<LotteryService.LotteryResult> response = lotteryController.draw(testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals("未中奖", response.getBody().getPrizeName());
        assertFalse(response.getBody().isSuccess());
    }

    @Test
    void testGetRecords() {
        LotteryService.LotteryRecordDto record = new LotteryService.LotteryRecordDto();
        record.setId(1L);
        record.setPrizeName("二等奖");
        record.setPrizeType(2);
        record.setCost(10);
        record.setCreatedAt(LocalDateTime.now());

        when(lotteryService.getRecords(1L)).thenReturn(Arrays.asList(record));

        ResponseEntity<List<LotteryService.LotteryRecordDto>> response = lotteryController.getRecords(testUser);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("二等奖", response.getBody().get(0).getPrizeName());
        verify(lotteryService, times(1)).getRecords(1L);
    }

    @Test
    void testGetPrizes() {
        LotteryService.LotteryPrizeDto prize = new LotteryService.LotteryPrizeDto();
        prize.setId(1L);
        prize.setName("一等奖");
        prize.setDescription("珍贵大奖");
        prize.setType(1);
        prize.setImage("/prize.jpg");

        when(lotteryService.getPrizes()).thenReturn(Arrays.asList(prize));

        ResponseEntity<List<LotteryService.LotteryPrizeDto>> response = lotteryController.getPrizes();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(1, response.getBody().size());
        assertEquals("一等奖", response.getBody().get(0).getName());
        verify(lotteryService, times(1)).getPrizes();
    }
}
