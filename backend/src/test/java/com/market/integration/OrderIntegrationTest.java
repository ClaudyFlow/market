package com.market.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 订单模块前后端联调测试
 * 测试订单创建、支付、取消、确认收货等核心API
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class OrderIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private String merchantToken;
    private Long createdOrderId;

    @BeforeAll
    void setup() throws Exception {
        setupTokens();
    }

    private void setupTokens() throws Exception {
        var loginRequest = objectMapper.createObjectNode();
        loginRequest.put("username", "testuser");
        loginRequest.put("password", "testpassword");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        var json = objectMapper.readTree(response);
        userToken = json.get("data").get("token").asText();

        loginRequest.put("username", "merchant1");
        result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        response = result.getResponse().getContentAsString();
        json = objectMapper.readTree(response);
        merchantToken = json.get("data").get("token").asText();
    }

    @Test
    @Order(1)
    @DisplayName("1. 创建订单 - 验证订单创建流程")
    void testCreateOrder() throws Exception {
        Map<String, Object> items = Map.of(
            "productId", 1,
            "quantity", 1
        );
        
        Map<String, Object> request = Map.of(
            "items", List.of(items),
            "addressId", 1,
            "remark", "联调测试订单"
        );

        MvcResult result = mockMvc.perform(post("/api/order")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.orderNo").exists())
                .andExpect(jsonPath("$.data.status").exists())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        var json = objectMapper.readTree(response);
        createdOrderId = json.get("data").get("id").asLong();
    }

    @Test
    @Order(2)
    @DisplayName("2. 获取订单列表 - 验证分页查询")
    void testGetOrderList() throws Exception {
        mockMvc.perform(get("/api/order")
                .header("Authorization", "Bearer " + userToken)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").isNumber());
    }

    @Test
    @Order(3)
    @DisplayName("3. 获取订单详情 - 验证订单详细信息")
    void testGetOrderDetail() throws Exception {
        mockMvc.perform(get("/api/order/" + createdOrderId)
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(createdOrderId))
                .andExpect(jsonPath("$.data.orderNo").exists())
                .andExpect(jsonPath("$.data.productList").exists());
    }

    @Test
    @Order(4)
    @DisplayName("4. 支付订单 - 验证支付流程")
    void testPayOrder() throws Exception {
        mockMvc.perform(post("/api/order/" + createdOrderId + "/pay")
                .header("Authorization", "Bearer " + userToken)
                .param("payMethod", "ALIPAY")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.payStatus").exists());
    }

    @Test
    @Order(5)
    @DisplayName("5. 查询支付状态 - 验证支付状态查询")
    void testGetPayStatus() throws Exception {
        mockMvc.perform(get("/api/order/" + createdOrderId + "/pay-status")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.paid").exists());
    }

    @Test
    @Order(6)
    @DisplayName("6. 商家查看订单 - 验证商家订单权限")
    void testMerchantGetOrders() throws Exception {
        mockMvc.perform(get("/api/merchant/orders")
                .header("Authorization", "Bearer " + merchantToken)
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @Order(7)
    @DisplayName("7. 商家发货 - 验证发货流程")
    void testMerchantShipOrder() throws Exception {
        mockMvc.perform(put("/api/merchant/order/" + createdOrderId + "/ship")
                .header("Authorization", "Bearer " + merchantToken)
                .param("logisticsCompany", "SF Express")
                .param("logisticsNo", "SF1234567890")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(8)
    @DisplayName("8. 用户确认收货 - 验证确认收货流程")
    void testConfirmReceive() throws Exception {
        mockMvc.perform(put("/api/order/" + createdOrderId + "/confirm")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(9)
    @DisplayName("9. 用户提交评价 - 验证评价功能")
    void testSubmitReview() throws Exception {
        var reviewRequest = objectMapper.createObjectNode();
        reviewRequest.put("rating", 5);
        reviewRequest.put("content", "商品很好，符合描述");
        reviewRequest.put("images", objectMapper.createArrayNode()
                .add("https://example.com/review1.jpg"));

        mockMvc.perform(post("/api/order/" + createdOrderId + "/review")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reviewRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(10)
    @DisplayName("10. 创建新订单用于取消测试")
    void testCreateOrderForCancel() throws Exception {
        Map<String, Object> items = Map.of(
            "productId", 2,
            "quantity", 1
        );
        
        Map<String, Object> request = Map.of(
            "items", List.of(items),
            "addressId", 1
        );

        MvcResult result = mockMvc.perform(post("/api/order")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String response = result.getResponse().getContentAsString();
        var json = objectMapper.readTree(response);
        Long newOrderId = json.get("data").get("id").asLong();

        // 取消订单
        mockMvc.perform(put("/api/order/" + newOrderId + "/cancel")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(11)
    @DisplayName("11. 获取订单统计 - 验证统计数据")
    void testGetOrderStats() throws Exception {
        mockMvc.perform(get("/api/order/stats")
                .header("Authorization", "Bearer " + userToken)
                .param("type", "MONTH")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").exists());
    }

    @Test
    @Order(12)
    @DisplayName("12. 未登录访问订单接口 - 验证权限拦截")
    void testGetOrderWithoutLogin() throws Exception {
        mockMvc.perform(get("/api/order/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(13)
    @DisplayName("13. 访问他人订单 - 验证数据权限")
    void testGetOthersOrder() throws Exception {
        // 使用商家token访问用户订单，应该被拒绝或返回空
        mockMvc.perform(get("/api/order/1")
                .header("Authorization", "Bearer " + merchantToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isForbidden());
    }
}
