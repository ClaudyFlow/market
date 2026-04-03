package com.market.controller;

import com.market.common.Result;
import com.market.entity.Payment;
import com.market.entity.PaymentRefund;
import com.market.entity.User;
import com.market.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * 支付控制器测试
 */
@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    @InjectMocks
    private PaymentController paymentController;

    private User testUser;
    private Payment testPayment;
    private PaymentRefund testRefund;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setName("testuser");
        testUser.setRole("USER");

        testPayment = new Payment();
        testPayment.setId(1L);
        testPayment.setPaymentNo("PAY20260403001");
        testPayment.setOrderNo("ORD20260403001");
        testPayment.setAmount(new BigDecimal("199.98"));
        testPayment.setPaymentMethod("ALIPAY");
        testPayment.setStatus("PENDING");

        testRefund = new PaymentRefund();
        testRefund.setId(1L);
        testRefund.setRefundNo("REF20260403001");
        testRefund.setPaymentNo("PAY20260403001");
        testRefund.setOrderId(1L);
        testRefund.setAmount(new BigDecimal("199.98"));
        testRefund.setStatus("PENDING");
        testRefund.setReason("质量问题");
    }

    @Test
    void testCreatePayment() {
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", "ORD20260403001");
        data.put("amount", "199.98");
        data.put("paymentMethod", "ALIPAY");

        when(paymentService.createPayment(eq(testUser), anyString(), any(), anyString())).thenReturn(testPayment);

        Result<Map<String, Object>> result = paymentController.createPayment(testUser, data);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertNotNull(result.getData());
        assertEquals("PAY20260403001", result.getData().get("paymentNo"));
        verify(paymentService, times(1)).createPayment(eq(testUser), anyString(), any(), anyString());
    }

    @Test
    void testCreatePaymentWithNullUser() {
        Map<String, Object> data = new HashMap<>();
        data.put("orderNo", "ORD20260403001");
        data.put("amount", "199.98");
        data.put("paymentMethod", "ALIPAY");

        Result<Map<String, Object>> result = paymentController.createPayment(null, data);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(401, result.getCode());
    }

    @Test
    void testPaymentCallback() {
        Map<String, String> callbackData = new HashMap<>();
        callbackData.put("paymentNo", "PAY20260403001");
        callbackData.put("status", "SUCCESS");
        callbackData.put("transactionId", "TXN123456");

        testPayment.setStatus("PAID");
        when(paymentService.simulatePaymentSuccess(anyString(), anyString())).thenReturn(testPayment);

        Result<Map<String, String>> result = paymentController.paymentCallback("ALIPAY", callbackData);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("success", result.getData().get("return_code"));
    }

    @Test
    void testGetPaymentStatus() {
        when(paymentService.getPaymentByPaymentNo("PAY20260403001")).thenReturn(testPayment);

        Result<Map<String, Object>> result = paymentController.getPaymentStatus("PAY20260403001");

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("PAY20260403001", result.getData().get("paymentNo"));
    }

    @Test
    void testGetPaymentByOrderNo() {
        when(paymentService.getPaymentByOrderNo("ORD20260403001")).thenReturn(testPayment);

        Result<Map<String, Object>> result = paymentController.getPaymentByOrderNo("ORD20260403001");

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("PAY20260403001", result.getData().get("paymentNo"));
    }

    @Test
    void testGetPaymentByOrderNoNotFound() {
        when(paymentService.getPaymentByOrderNo("NOTEXIST")).thenReturn(null);

        Result<Map<String, Object>> result = paymentController.getPaymentByOrderNo("NOTEXIST");

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(404, result.getCode());
    }

    @Test
    void testApplyRefund() {
        Map<String, Object> data = new HashMap<>();
        data.put("orderId", "1");
        data.put("paymentNo", "PAY20260403001");
        data.put("amount", "199.98");
        data.put("reason", "质量问题");
        data.put("images", new ArrayList<>());

        when(paymentService.createRefund(eq(testUser), eq(1L), anyString(), any(), anyString(), any())).thenReturn(testRefund);

        Result<Map<String, Object>> result = paymentController.applyRefund(testUser, data);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("REF20260403001", result.getData().get("refundNo"));
    }

    @Test
    void testGetRefundStatus() {
        when(paymentService.getRefundByRefundNo("REF20260403001")).thenReturn(testRefund);

        Result<Map<String, Object>> result = paymentController.getRefundStatus("REF20260403001");

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("REF20260403001", result.getData().get("refundNo"));
    }

    @Test
    void testGetUserRefunds() {
        when(paymentService.getUserRefunds(1L)).thenReturn(Arrays.asList(testRefund));

        Result<List<Map<String, Object>>> result = paymentController.getUserRefunds(testUser);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals(1, result.getData().size());
    }

    @Test
    void testApproveRefund() {
        User merchant = new User();
        merchant.setId(2L);
        merchant.setRole("MERCHANT");

        Map<String, Object> data = new HashMap<>();
        data.put("refundNo", "REF20260403001");
        data.put("approved", "true");
        data.put("remark", "同意退款");

        testRefund.setStatus("SUCCESS");
        when(paymentService.approveRefund(anyString(), eq(true), anyString())).thenReturn(testRefund);

        Result<Map<String, String>> result = paymentController.approveRefund(merchant, data);

        assertNotNull(result);
        assertTrue(result.isSuccess());
        verify(paymentService, times(1)).approveRefund(anyString(), eq(true), anyString());
    }

    @Test
    void testApproveRefundUnauthorized() {
        Map<String, Object> data = new HashMap<>();
        data.put("refundNo", "REF20260403001");
        data.put("approved", "true");

        Result<Map<String, String>> result = paymentController.approveRefund(testUser, data);

        assertNotNull(result);
        assertFalse(result.isSuccess());
        assertEquals(403, result.getCode());
    }

    @Test
    void testMockPayment() {
        testPayment.setStatus("PAID");
        testPayment.setPaidAt(LocalDateTime.now());
        when(paymentService.simulatePaymentSuccess(anyString(), anyString())).thenReturn(testPayment);

        Result<Map<String, Object>> result = paymentController.mockPayment("PAY20260403001");

        assertNotNull(result);
        assertTrue(result.isSuccess());
        assertEquals("PAID", result.getData().get("status"));
    }
}
