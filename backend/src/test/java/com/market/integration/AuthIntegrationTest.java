package com.market.integration;

import com.market.common.Result;
import com.market.dto.LoginRequest;
import com.market.dto.RegisterRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 认证模块前后端联调测试
 * 测试注册、登录、获取用户信息等核心API
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class AuthIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String userToken;
    private String merchantToken;
    private String adminToken;

    @Test
    @Order(1)
    @DisplayName("1. 用户注册 - 验证前后端数据格式兼容性")
    void testUserRegister() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("integration_user");
        request.setPassword("Test123456!");
        request.setEmail("integration@test.com");
        request.setConfirmPassword("Test123456!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.message").value("success"))
                .andExpect(jsonPath("$.data").exists())
                .andExpect(jsonPath("$.data.name").value("integration_user"))
                .andExpect(jsonPath("$.data.email").value("integration@test.com"));
    }

    @Test
    @Order(2)
    @DisplayName("2. 用户登录 - 验证JWT token生成")
    void testUserLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setName("testuser");
        request.setPassword("testpassword");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.token").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists())
                .andReturn();

        // 保存token供后续测试使用
        String responseContent = result.getResponse().getContentAsString();
        var response = objectMapper.readTree(responseContent);
        userToken = response.get("data").get("token").asText();
        
        // 验证token格式
        Assertions.assertNotNull(userToken);
        Assertions.assertTrue(userToken.length() > 50);
    }

    @Test
    @Order(3)
    @DisplayName("3. 商家登录 - 验证角色权限")
    void testMerchantLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setName("merchant1");
        request.setPassword("testpassword");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        var response = objectMapper.readTree(responseContent);
        merchantToken = response.get("data").get("token").asText();
    }

    @Test
    @Order(4)
    @DisplayName("4. 管理员登录 - 验证管理员权限")
    void testAdminLogin() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setName("admin");
        request.setPassword("testpassword");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andReturn();

        String responseContent = result.getResponse().getContentAsString();
        var response = objectMapper.readTree(responseContent);
        adminToken = response.get("data").get("token").asText();
    }

    @Test
    @Order(5)
    @DisplayName("5. 获取当前用户信息 - 验证认证拦截器")
    void testGetCurrentUser() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.name").value("testuser"))
                .andExpect(jsonPath("$.data.password").doesNotExist()); // 密码不应返回
    }

    @Test
    @Order(6)
    @DisplayName("6. 未认证访问 - 验证安全拦截")
    void testUnauthenticatedAccess() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(7)
    @DisplayName("7. 错误密码登录 - 验证错误处理")
    void testLoginWithWrongPassword() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setName("testuser");
        request.setPassword("wrongpassword");

        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(401));
    }

    @Test
    @Order(8)
    @DisplayName("8. 重复用户名注册 - 验证业务逻辑校验")
    void testDuplicateUsername() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setName("testuser");
        request.setPassword("Test123456!");
        request.setEmail("duplicate@test.com");
        request.setConfirmPassword("Test123456!");

        mockMvc.perform(post("/api/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(400));
    }

    // Getter methods for tokens (used by other integration tests)
    public String getUserToken() {
        return userToken;
    }

    public String getMerchantToken() {
        return merchantToken;
    }

    public String getAdminToken() {
        return adminToken;
    }
}
