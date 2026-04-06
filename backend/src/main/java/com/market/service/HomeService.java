package com.market.service;

import com.market.entity.Product;
import com.market.entity.Shop;
import com.market.entity.User;
import com.market.repository.ProductRepository;
import com.market.repository.ShopRepository;
import com.market.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 首页服务
 */
@Service
public class HomeService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ShopRepository shopRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RecommendService recommendService;

    /**
     * 获取轮播图
     */
    public List<Map<String, Object>> getBanners() {
        List<Map<String, Object>> banners = new ArrayList<>();
        banners.add(Map.of(
            "id", 1,
            "title", "新品上市",
            "image", "/images/banner1.jpg",
            "link", "/sale",
            "type", "activity"
        ));
        banners.add(Map.of(
            "id", 2,
            "title", "限时秒杀",
            "image", "/images/banner2.jpg",
            "link", "/sale",
            "type", "flash_sale"
        ));
        banners.add(Map.of(
            "id", 3,
            "title", "品牌特惠",
            "image", "/images/banner3.jpg",
            "link", "/brands",
            "type", "brand"
        ));
        return banners;
    }

    /**
     * 获取首页分类
     */
    public List<Map<String, Object>> getCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        String[] names = {"数码", "服装", "家居", "美妆", "食品", "图书", "母婴", "运动", "珠宝", "家电"};
        String[] icons = {"📱", "👕", "🏠", "💄", "🍎", "📚", "👶", "⚽", "💍", "📺"};
        String[] links = {"/digital", "/fashion", "/home", "/beauty", "/food", "/books", "/baby", "/sports", "/jewelry", "/appliances"};
        
        for (int i = 0; i < names.length; i++) {
            Map<String, Object> cat = new HashMap<>();
            cat.put("id", i + 1);
            cat.put("name", names[i]);
            cat.put("icon", icons[i]);
            cat.put("link", links[i]);
            categories.add(cat);
        }
        return categories;
    }

    /**
     * 获取楼层商品
     */
    public Map<String, List<Product>> getFloorProducts() {
        Map<String, List<Product>> floorProducts = new HashMap<>();
        
        Pageable pageable = PageRequest.of(0, 4);
        
        // 按分类获取商品
        floorProducts.put("digital", productRepository.findTop4ByOrderBySalesDesc());
        floorProducts.put("fashion", productRepository.findTop4ByOrderByCreatedAtDesc());
        floorProducts.put("home", productRepository.findTop4ByOrderByRatingDesc());
        floorProducts.put("beauty", productRepository.findTop4ByOrderBySalesDesc());
        
        return floorProducts;
    }

    /**
     * 获取推荐店铺
     */
    public List<Shop> getRecommendedShops(Integer limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return shopRepository.findTopShops(pageable);
    }

    /**
     * 获取活动信息
     */
    public List<Map<String, Object>> getActivities() {
        List<Map<String, Object>> activities = new ArrayList<>();
        activities.add(Map.of(
            "id", 1,
            "title", "双11大促",
            "subtitle", "全场5折起",
            "image", "/images/activity1.jpg",
            "startTime", LocalDateTime.now().toString(),
            "endTime", LocalDateTime.now().plusDays(7).toString(),
            "status", "ongoing"
        ));
        activities.add(Map.of(
            "id", 2,
            "title", "新品首发",
            "subtitle", "限量抢购",
            "image", "/images/activity2.jpg",
            "startTime", LocalDateTime.now().toString(),
            "endTime", LocalDateTime.now().plusDays(3).toString(),
            "status", "ongoing"
        ));
        return activities;
    }

    /**
     * 获取秒杀活动
     */
    public Map<String, Object> getFlashSales() {
        Map<String, Object> flashSales = new HashMap<>();
        List<Product> products = productRepository.findTop6ByOrderBySalesDesc();
        
        flashSales.put("startTime", LocalDateTime.now().withHour(10).withMinute(0).withSecond(0));
        flashSales.put("endTime", LocalDateTime.now().withHour(22).withMinute(0).withSecond(0));
        flashSales.put("products", products);
        flashSales.put("status", "ongoing");
        
        return flashSales;
    }

    /**
     * 获取品牌专区
     */
    public List<Map<String, Object>> getBrands() {
        List<Map<String, Object>> brands = new ArrayList<>();
        String[] brandNames = {"Apple", "Nike", "Adidas", "Sony", "Samsung", "Huawei", "Xiaomi", "Dell"};
        
        for (int i = 0; i < brandNames.length; i++) {
            Map<String, Object> brand = new HashMap<>();
            brand.put("id", i + 1);
            brand.put("name", brandNames[i]);
            brand.put("logo", "/images/brand_" + (i + 1) + ".png");
            brand.put("productCount", 100 + i * 50);
            brands.add(brand);
        }
        return brands;
    }

    /**
     * 获取新人专享
     */
    public Map<String, Object> getNewUserProducts(User user) {
        Map<String, Object> result = new HashMap<>();
        
        boolean isNewUser = user != null && user.getCreatedAt().isAfter(LocalDateTime.now().minusDays(7));
        result.put("isNewUser", isNewUser);
        
        if (isNewUser) {
            List<Product> products = productRepository.findTop8ByOrderByCreatedAtDesc();
            result.put("products", products);
            result.put("coupon", Map.of(
                "id", 1,
                "title", "新人专享50元优惠券",
                "amount", 50,
                "minSpend", 100
            ));
        } else {
            result.put("products", new ArrayList<>());
        }
        
        return result;
    }

    /**
     * 首页猜你喜欢
     */
    public List<Product> getHomeRecommend(User user, Integer page, Integer size) {
        if (user != null) {
            // 基于用户行为推荐
            return recommendService.getRecommendProducts(user.getId(), size);
        }
        
        // 默认返回热销商品
        return productRepository.findTop10ByOrderBySalesDesc();
    }

    /**
     * 首页统计概览
     */
    public Map<String, Object> getHomeStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProducts", productRepository.count());
        stats.put("totalShops", shopRepository.count());
        stats.put("flashSaleCount", 10);
        stats.put("activityCount", 5);
        return stats;
    }

    /**
     * 用户首页统计
     */
    public Map<String, Object> getUserHomeStats(User user) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("unreadNotifications", 0);
        stats.put("pendingOrders", 0);
        stats.put("cartCount", 0);
        stats.put("couponCount", 0);
        return stats;
    }
}
