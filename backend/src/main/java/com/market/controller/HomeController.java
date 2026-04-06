package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.Product;
import com.market.entity.Shop;
import com.market.entity.User;
import com.market.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 首页控制器
 * 提供首页轮播图、分类、推荐商品、推荐店铺、活动信息、秒杀、品牌专区、猜你喜欢等首页数据接口。
 * 权限要求：大部分接口公开，部分需要登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/home
 */
@RestController
@RequestMapping("/api/home")
@CrossOrigin(origins = "*")
public class HomeController {

    @Autowired
    private HomeService homeService;

    /**
     * 获取轮播图
     * API路径：GET /api/home/banners
     * 权限：公开
     *
     * @return 轮播图列表
     */
    @GetMapping("/banners")
    @Cacheable(key = "'home_banners'", cacheName = "home", expire = 3600)
    public Result<List<Map<String, Object>>> getBanners() {
        List<Map<String, Object>> banners = homeService.getBanners();
        return Result.success(banners);
    }

    /**
     * 获取首页分类
     * API路径：GET /api/home/categories
     * 权限：公开
     *
     * @return 首页分类列表
     */
    @GetMapping("/categories")
    @Cacheable(key = "'home_categories'", cacheName = "home", expire = 3600)
    public Result<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = homeService.getCategories();
        return Result.success(categories);
    }

    /**
     * 获取楼层商品
     * API路径：GET /api/home/floor-products
     * 权限：公开
     *
     * @return 按分类分组的楼层商品
     */
    @GetMapping("/floor-products")
    @Cacheable(key = "'home_floor_products'", cacheName = "home", expire = 300)
    public Result<Map<String, List<Product>>> getFloorProducts() {
        Map<String, List<Product>> floorProducts = homeService.getFloorProducts();
        return Result.success(floorProducts);
    }

    /**
     * 获取推荐店铺
     * API路径：GET /api/home/recommended-shops
     * 权限：公开
     *
     * @param limit 店铺数量限制，默认6
     * @return 推荐店铺列表
     */
    @GetMapping("/recommended-shops")
    @Cacheable(key = "'home_recommended_shops'", cacheName = "home", expire = 600)
    public Result<List<Shop>> getRecommendedShops(
            @RequestParam(defaultValue = "6") Integer limit) {
        List<Shop> shops = homeService.getRecommendedShops(limit);
        return Result.success(shops);
    }

    /**
     * 获取活动信息
     * API路径：GET /api/home/activities
     * 权限：公开
     *
     * @return 活动信息列表
     */
    @GetMapping("/activities")
    @Cacheable(key = "'home_activities'", cacheName = "home", expire = 600)
    public Result<List<Map<String, Object>>> getActivities() {
        List<Map<String, Object>> activities = homeService.getActivities();
        return Result.success(activities);
    }

    /**
     * 获取秒杀活动
     * API路径：GET /api/home/flash-sales
     * 权限：公开
     *
     * @return 秒杀活动信息
     */
    @GetMapping("/flash-sales")
    @Cacheable(key = "'home_flash_sales'", cacheName = "home", expire = 60)
    public Result<Map<String, Object>> getFlashSales() {
        Map<String, Object> flashSales = homeService.getFlashSales();
        return Result.success(flashSales);
    }

    /**
     * 获取品牌专区
     * API路径：GET /api/home/brands
     * 权限：公开
     *
     * @return 品牌列表
     */
    @GetMapping("/brands")
    @Cacheable(key = "'home_brands'", cacheName = "home", expire = 3600)
    public Result<List<Map<String, Object>>> getBrands() {
        List<Map<String, Object>> brands = homeService.getBrands();
        return Result.success(brands);
    }

    /**
     * 获取新人专享
     * API路径：GET /api/home/new-user
     * 权限：需要登录（可选）
     *
     * @param user 当前登录用户
     * @return 新人专享商品和信息
     */
    @GetMapping("/new-user")
    @Cacheable(key = "'home_new_user'", cacheName = "home", expire = 600)
    public Result<Map<String, Object>> getNewUserProducts(@AuthenticationPrincipal User user) {
        Map<String, Object> newUserProducts = homeService.getNewUserProducts(user);
        return Result.success(newUserProducts);
    }

    /**
     * 首页猜你喜欢
     * API路径：GET /api/home/recommend
     * 权限：需要登录（可选）
     *
     * @param user 当前登录用户
     * @param page 页码，默认1
     * @param size 每页大小，默认10
     * @return 推荐商品列表
     */
    @GetMapping("/recommend")
    @Cacheable(key = "'home_recommend_' + #page", cacheName = "home", expire = 300)
    public Result<List<Product>> getHomeRecommend(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        List<Product> products = homeService.getHomeRecommend(user, page, size);
        return Result.success(products);
    }

    /**
     * 首页统计概览
     * API路径：GET /api/home/stats
     * 权限：公开
     *
     * @return 首页统计数据
     */
    @GetMapping("/stats")
    @Cacheable(key = "'home_stats'", cacheName = "home", expire = 300)
    public Result<Map<String, Object>> getHomeStats() {
        Map<String, Object> stats = homeService.getHomeStats();
        return Result.success(stats);
    }

    /**
     * 用户首页统计
     * API路径：GET /api/home/user/stats
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 用户个人首页统计数据
     */
    @GetMapping("/user/stats")
    @Cacheable(key = "'home_user_stats_' + #user.id", cacheName = "home", expire = 300)
    public Result<Map<String, Object>> getUserHomeStats(@AuthenticationPrincipal User user) {
        if (user == null) {
            return Result.error(401, "请先登录");
        }
        Map<String, Object> stats = homeService.getUserHomeStats(user);
        return Result.success(stats);
    }
}
