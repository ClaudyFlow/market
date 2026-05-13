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
 * 商品模块前后端联调测试
 * 测试商品查询、创建、更新、删除等核心API
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("integration")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // 从认证测试获取token（实际运行时应有依赖关系）
    private String merchantToken = "test-merchant-token";
    private String userToken = "test-user-token";
    private String adminToken = "test-admin-token";

    @BeforeAll
    void setup() throws Exception {
        // 先执行登录获取token
        setupTokens();
    }

    private void setupTokens() throws Exception {
        var loginRequest = objectMapper.createObjectNode();
        loginRequest.put("username", "merchant1");
        loginRequest.put("password", "testpassword");

        MvcResult result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        String response = result.getResponse().getContentAsString();
        var json = objectMapper.readTree(response);
        merchantToken = json.get("data").get("token").asText();

        // 获取用户token
        loginRequest.put("username", "testuser");
        result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        response = result.getResponse().getContentAsString();
        json = objectMapper.readTree(response);
        userToken = json.get("data").get("token").asText();

        // 获取管理员token
        loginRequest.put("username", "admin");
        result = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn();

        response = result.getResponse().getContentAsString();
        json = objectMapper.readTree(response);
        adminToken = json.get("data").get("token").asText();
    }

    @Test
    @Order(1)
    @DisplayName("1. 获取商品列表 - 验证分页和筛选")
    void testGetProductList() throws Exception {
        mockMvc.perform(get("/api/product")
                .param("page", "0")
                .param("size", "10")
                .param("categoryId", "1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.totalElements").isNumber())
                .andExpect(jsonPath("$.data.totalPages").isNumber());
    }

    @Test
    @Order(2)
    @DisplayName("2. 获取商品详情 - 验证商品详细信息")
    void testGetProductDetail() throws Exception {
        mockMvc.perform(get("/api/product/1")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").exists())
                .andExpect(jsonPath("$.data.price").isNumber())
                .andExpect(jsonPath("$.data.images").isArray());
    }

    @Test
    @Order(3)
    @DisplayName("3. 搜索商品 - 验证搜索功能")
    void testSearchProducts() throws Exception {
        mockMvc.perform(get("/api/product/search")
                .param("keyword", "iPhone")
                .param("page", "0")
                .param("size", "10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @Order(4)
    @DisplayName("4. 获取推荐商品 - 验证推荐算法")
    void testGetRecommendedProducts() throws Exception {
        mockMvc.perform(get("/api/product/recommended")
                .param("page", "0")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.content").isArray());
    }

    @Test
    @Order(5)
    @DisplayName("5. 获取商品分类 - 验证分类列表")
    void testGetCategories() throws Exception {
        mockMvc.perform(get("/api/product/categories")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].name").exists());
    }

    @Test
    @Order(6)
    @DisplayName("6. 商家创建商品 - 验证商品创建流程")
    void testCreateProduct() throws Exception {
        Map<String, Object> request = Map.of(
            "title", "测试商品 - iPad Air",
            "description", "99新，配件齐全",
            "price", 3999.00,
            "originalPrice", 4999.00,
            "stock", 1,
            "categoryId", 1,
            "images", List.of("https://example.com/ipad1.jpg")
        );

        mockMvc.perform(post("/api/product")
                .header("Authorization", "Bearer " + merchantToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.title").value("测试商品 - iPad Air"));
    }

    @Test
    @Order(7)
    @DisplayName("7. 商家更新商品 - 验证商品更新流程")
    void testUpdateProduct() throws Exception {
        Map<String, Object> request = Map.of(
            "title", "测试商品 - 更新版",
            "description", "更新描述",
            "price", 4099.00,
            "originalPrice", 4999.00,
            "stock", 2,
            "categoryId", 1,
            "images", List.of("https://example.com/ipad1.jpg")
        );

        mockMvc.perform(put("/api/product/1")
                .header("Authorization", "Bearer " + merchantToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(8)
    @DisplayName("8. 用户收藏商品 - 验证收藏功能")
    void testFavoriteProduct() throws Exception {
        mockMvc.perform(post("/api/product/1/favorite")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(9)
    @DisplayName("9. 检查收藏状态 - 验证收藏状态查询")
    void testCheckFavoriteStatus() throws Exception {
        mockMvc.perform(get("/api/product/1/favorite")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data.favorited").value(true));
    }

    @Test
    @Order(10)
    @DisplayName("10. 添加浏览记录 - 验证浏览历史")
    void testAddBrowseHistory() throws Exception {
        mockMvc.perform(post("/api/product/1/browse")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200));
    }

    @Test
    @Order(11)
    @DisplayName("11. 未登录收藏商品 - 验证权限拦截")
    void testFavoriteWithoutLogin() throws Exception {
        mockMvc.perform(post("/api/product/1/favorite")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @Order(12)
    @DisplayName("12. 获取热门商品 - 验证热门商品列表")
    void testGetHotProducts() throws Exception {
        mockMvc.perform(get("/api/product/hot")
                .param("size", "5")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray());
    }

    @Test
    @Order(13)
    @DisplayName("13. 批量获取商品 - 验证批量查询")
    void testBatchGetProducts() throws Exception {
        List<Long> ids = List.of(1L, 2L, 3L);

        mockMvc.perform(post("/api/product/batch")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(ids)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(3));
    }
}
