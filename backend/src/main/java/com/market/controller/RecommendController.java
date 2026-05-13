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
 * 提供推荐商品、热门商品、看了又看、买了又买、店铺推荐等个性化推荐接口。
 * 权限要求：大部分接口公开，推荐商品需要登录可获得更精准结果
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/recommend
 */
@RestController
@RequestMapping("/api/recommend")
@CrossOrigin(origins = "*")
public class RecommendController {

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取推荐商品列表（猜你喜欢）
     * API路径：GET /api/recommend/products
     * 权限：公开（登录后可获得更精准推荐）
     *
     * @param limit 返回数量，默认10
     * @param user 当前登录用户（可选）
     * @return 推荐商品列表
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
     * API路径：GET /api/recommend/hot
     * 权限：公开
     *
     * @param limit 返回数量，默认10
     * @return 热门商品列表
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
     * 获取看了又看（浏览该商品的用户还看了哪些商品）
     * API路径：GET /api/recommend/viewed-also-viewed
     * 权限：公开
     *
     * @param productId 商品ID
     * @param limit 返回数量，默认6
     * @param user 当前登录用户（可选）
     * @return 推荐商品列表
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
     * 获取买了又买（购买该商品的用户还购买了哪些商品）
     * API路径：GET /api/recommend/bought-also-bought
     * 权限：公开
     *
     * @param productId 商品ID
     * @param limit 返回数量，默认6
     * @param user 当前登录用户（可选）
     * @return 推荐商品列表
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
     * API路径：GET /api/recommend/shop
     * 权限：公开
     *
     * @param merchantId 商家ID
     * @param limit 返回数量，默认6
     * @return 店铺推荐商品列表
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
