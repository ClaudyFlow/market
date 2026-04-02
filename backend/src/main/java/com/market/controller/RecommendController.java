package com.market.controller;

import com.market.annotation.AuditLog;
import com.market.annotation.Cacheable;
import com.market.common.Result;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.RecommendService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 推荐控制器
 */
@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "*")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取推荐商品列表（猜你喜欢）
     */
    @GetMapping("/products")
    @Cacheable(key = "'recommend_products_' + #user.id + '_' + #limit", cacheName = "recommend", expire = 600)
    @AuditLog(module = "推荐系统", action = "获取推荐商品")
    public Result<List<Map<String, Object>>> getRecommendProducts(
            @RequestParam(defaultValue = "10") Integer limit,
            @AuthenticationPrincipal User user) {

        Long userId = user != null ? user.getId() : null;
        List<Product> products = recommendService.getRecommendProducts(userId, limit);
        return Result.success(convertProductsToMap(products));
    }

    /**
     * 获取热门商品
     */
    @GetMapping("/hot")
    @Cacheable(key = "'hot_products_' + #limit", cacheName = "recommend", expire = 600)
    @AuditLog(module = "推荐系统", action = "获取热门商品")
    public Result<List<Map<String, Object>>> getHotProducts(
            @RequestParam(defaultValue = "10") Integer limit) {

        List<Product> products = recommendService.getHotProducts(limit);
        return Result.success(convertProductsToMap(products));
    }

    /**
     * 获取看了又看
     */
    @GetMapping("/viewed-also-viewed")
    @Cacheable(key = "'viewed_also_viewed_' + #productId + '_' + #limit", cacheName = "recommend", expire = 600)
    @AuditLog(module = "推荐系统", action = "获取看了又看")
    public Result<List<Map<String, Object>>> getViewedAlsoViewed(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "6") Integer limit,
            @AuthenticationPrincipal User user) {

        Long userId = user != null ? user.getId() : null;
        List<Product> products = recommendService.getViewedAlsoViewed(userId, productId, limit);
        return Result.success(convertProductsToMap(products));
    }

    /**
     * 获取买了又买
     */
    @GetMapping("/bought-also-bought")
    @Cacheable(key = "'bought_also_bought_' + #productId + '_' + #limit", cacheName = "recommend", expire = 600)
    @AuditLog(module = "推荐系统", action = "获取买了又买")
    public Result<List<Map<String, Object>>> getBoughtAlsoBought(
            @RequestParam Long productId,
            @RequestParam(defaultValue = "6") Integer limit,
            @AuthenticationPrincipal User user) {

        Long userId = user != null ? user.getId() : null;
        List<Product> products = recommendService.getBoughtAlsoBought(userId, productId, limit);
        return Result.success(convertProductsToMap(products));
    }

    /**
     * 获取店铺推荐商品
     */
    @GetMapping("/shop")
    @Cacheable(key = "'shop_recommend_' + #merchantId + '_' + #limit", cacheName = "recommend", expire = 600)
    @AuditLog(module = "推荐系统", action = "获取店铺推荐")
    public Result<List<Map<String, Object>>> getShopRecommend(
            @RequestParam Long merchantId,
            @RequestParam(defaultValue = "6") Integer limit) {

        List<Product> products = recommendService.getShopRecommend(merchantId, limit);
        return Result.success(convertProductsToMap(products));
    }

    /**
     * 转换 Product 列表为 Map 列表
     */
    private List<Map<String, Object>> convertProductsToMap(List<Product> products) {
        return products.stream().map(product -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", product.getId());
            map.put("name", product.getName());
            map.put("description", product.getDescription());
            map.put("price", product.getPrice());
            map.put("originalPrice", product.getOriginalPrice());
            map.put("image", product.getImage());
            map.put("category", product.getCategory());
            map.put("brand", product.getBrand());
            map.put("sales", product.getSales());
            map.put("rating", product.getRating());
            map.put("reviewCount", product.getReviewCount());
            map.put("stock", product.getStock());
            map.put("available", product.getAvailable());
            map.put("status", product.getStatus());
            map.put("merchantId", product.getMerchant() != null ? product.getMerchant().getId() : null);
            map.put("merchantName", product.getMerchant() != null ? product.getMerchant().getShopName() : null);
            return map;
        }).collect(Collectors.toList());
    }
}
