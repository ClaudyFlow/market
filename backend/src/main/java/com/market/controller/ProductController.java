package com.market.controller;

import com.market.annotation.*;
import com.market.common.Result;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.FavoriteService;
import com.market.service.ProductService;
import com.market.service.UserBrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品控制器
 */
@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private FavoriteService favoriteService;

    @Autowired
    private UserBrowseHistoryService browseHistoryService;

    /**
     * 获取商品列表
     */
    @GetMapping
    @Cacheable(key = "'product_list_' + #page + '_' + #size + '_' + #category", 
               cacheName = "products", expire = 300)
    @AuditLog(module = "商品管理", action = "查询商品列表")
    @DataScope(scopeType = DataScope.ScopeType.ALL)
    public Result<Page<Product>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "default") String sort) {
        
        Sort sortConfig = getSortConfig(sort);
        Pageable pageable = PageRequest.of(page - 1, size, sortConfig);
        
        Page<Product> products;
        if (category != null && !category.isEmpty()) {
            products = productService.getProductsByCategory(category, pageable);
        } else {
            products = productService.getProducts(pageable);
        }
        
        return Result.success(products);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    @Cacheable(key = "'product_detail_' + #id", cacheName = "products", expire = 600)
    @AuditLog(module = "商品管理", action = "查询商品详情")
    @SensitiveData(type = SensitiveData.SensitiveType.DEFAULT)
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        // 添加浏览记录（异步）
        return Result.success(product);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    @Cacheable(key = "'product_search_' + #keyword + '_' + #page", 
               cacheName = "products", expire = 300)
    @AuditLog(module = "商品管理", action = "搜索商品")
    public Result<Page<Product>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(defaultValue = "default") String sort) {
        
        Sort sortConfig = getSortConfig(sort);
        Pageable pageable = PageRequest.of(page - 1, size, sortConfig);
        Page<Product> products = productService.searchProducts(keyword, pageable);
        return Result.success(products);
    }

    /**
     * 获取推荐商品
     */
    @GetMapping("/recommended")
    @Cacheable(key = "'product_recommended_' + #limit", cacheName = "products", expire = 600)
    @AuditLog(module = "商品管理", action = "查询推荐商品")
    public Result<List<Product>> getRecommendedProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        // 暂时返回热销商品
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sales"));
        Page<Product> products = productService.getProducts(pageable);
        return Result.success(products.getContent());
    }

    /**
     * 获取热销商品
     */
    @GetMapping("/hot")
    @Cacheable(key = "'product_hot_' + #limit", cacheName = "products", expire = 600)
    @AuditLog(module = "商品管理", action = "查询热销商品")
    public Result<List<Product>> getHotProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sales"));
        Page<Product> products = productService.getProducts(pageable);
        return Result.success(products.getContent());
    }

    /**
     * 获取新品
     */
    @GetMapping("/new")
    @Cacheable(key = "'product_new_' + #limit", cacheName = "products", expire = 600)
    @AuditLog(module = "商品管理", action = "查询新品")
    public Result<List<Product>> getNewProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productService.getProducts(pageable);
        return Result.success(products.getContent());
    }

    /**
     * 获取促销商品
     */
    @GetMapping("/sale")
    @Cacheable(key = "'product_sale_' + #limit", cacheName = "products", expire = 600)
    @AuditLog(module = "商品管理", action = "查询促销商品")
    public Result<List<Product>> getSaleProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        // 返回有原价的商品（促销）
        Pageable pageable = PageRequest.of(0, limit);
        Page<Product> products = productService.getProducts(pageable);
        List<Product> saleProducts = products.getContent().stream()
            .filter(p -> p.getOriginalPrice() != null && p.getOriginalPrice().compareTo(p.getPrice()) > 0)
            .limit(limit)
            .toList();
        return Result.success(saleProducts);
    }

    /**
     * 获取商品分类
     */
    @GetMapping("/categories")
    @Cacheable(key = "'product_categories'", cacheName = "products", expire = 3600)
    @AuditLog(module = "商品管理", action = "查询商品分类")
    public Result<List<String>> getCategories() {
        List<String> categories = productService.getCategories();
        return Result.success(categories);
    }

    /**
     * 获取商品品牌
     */
    @GetMapping("/brands")
    @Cacheable(key = "'product_brands_' + #categoryId", cacheName = "products", expire = 3600)
    @AuditLog(module = "商品管理", action = "查询商品品牌")
    public Result<List<String>> getBrands(
            @RequestParam(required = false) String categoryId) {
        // 暂时返回固定品牌列表
        List<String> brands = Arrays.asList(
            "华为", "小米", "苹果", "三星", "OPPO", "vivo",
            "联想", "戴尔", "惠普", "华硕", "索尼", "尼康"
        );
        return Result.success(brands);
    }

    /**
     * 批量获取商品信息
     */
    @PostMapping("/batch")
    @Cacheable(key = "'product_batch_' + #ids", cacheName = "products", expire = 600)
    @AuditLog(module = "商品管理", action = "批量查询商品")
    public Result<List<Product>> getProductsBatch(@RequestBody List<Long> ids) {
        List<Product> products = productService.getProductsByIds(ids);
        return Result.success(products);
    }

    /**
     * 获取商品库存
     */
    @GetMapping("/{id}/stock")
    @Cacheable(key = "'product_stock_' + #id + '_' + #skuId", cacheName = "products", expire = 60)
    @AuditLog(module = "商品管理", action = "查询商品库存")
    public Result<Map<String, Integer>> getProductStock(
            @PathVariable Long id,
            @RequestParam(required = false) Long skuId) {
        Product product = productService.getProductDetail(id);
        Map<String, Integer> result = new HashMap<>();
        result.put("stock", product.getStock());
        return Result.success(result);
    }

    /**
     * 收藏商品
     */
    @PostMapping("/{id}/favorite")
    @Idempotent(key = "'favorite_product_' + #id + '_' + #user.id", expire = 600)
    @AuditLog(module = "商品管理", action = "收藏商品")
    public Result<Void> favoriteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        try {
            favoriteService.addFavorite(user.getId(), id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 取消收藏商品
     */
    @DeleteMapping("/{id}/favorite")
    @Idempotent(key = "'unfavorite_product_' + #id + '_' + #user.id", expire = 600)
    @AuditLog(module = "商品管理", action = "取消收藏商品")
    public Result<Void> unfavoriteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        try {
            favoriteService.removeFavorite(user.getId(), id);
            return Result.success(null);
        } catch (RuntimeException e) {
            return Result.error(400, e.getMessage());
        }
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/{id}/favorite")
    @Cacheable(key = "'product_favorite_' + #id + '_' + #user.id", cacheName = "products", expire = 60)
    @AuditLog(module = "商品管理", action = "检查收藏状态")
    public Result<Map<String, Boolean>> checkFavorite(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }

        boolean isFavorite = favoriteService.isFavorite(user.getId(), id);
        Map<String, Boolean> result = new HashMap<>();
        result.put("favorite", isFavorite);
        return Result.success(result);
    }

    /**
     * 添加浏览记录
     */
    @PostMapping("/{id}/browse")
    @AuditLog(module = "商品管理", action = "添加浏览记录")
    @RateLimiter(key = "'browse_product_' + #id", maxRequests = 60, timeout = 60, timeUnit = java.util.concurrent.TimeUnit.SECONDS)
    public Result<Void> addBrowseHistory(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        if (user == null) {
            return Result.error(401, "请先登录");
        }
        
        try {
            com.market.entity.Product product = productService.getProductById(id);
            if (product != null && product.getMerchant() != null) {
                browseHistoryService.addBrowseHistory(
                    user.getId(), id, product.getName(), product.getImage(),
                    product.getPrice(), product.getMerchant().getId(), product.getMerchant().getShopName()
                );
            }
            return Result.success(null);
        } catch (Exception e) {
            return Result.success(null); // 浏览记录失败不报错
        }
    }

    /**
     * 创建商品（商家）
     */
    @PostMapping
    @Idempotent(key = "'create_product_' + #merchant.id", expire = 3600, message = "商品正在创建中，请勿重复提交")
    @DistributedLock(key = "'create_product_' + #merchant.id", waitTime = 5000)
    @AuditLog(module = "商品管理", action = "创建商品", recordParams = true, recordResult = true)
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Product> createProduct(
            @RequestBody Product product,
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Product createdProduct = productService.createProduct(merchant, product);
        return Result.success(createdProduct);
    }

    /**
     * 更新商品（商家）
     */
    @PutMapping("/{id}")
    @Idempotent(key = "'update_product_' + #id", expire = 3600)
    @DistributedLock(key = "'update_product_' + #id", waitTime = 5000)
    @AuditLog(module = "商品管理", action = "更新商品", recordParams = true)
    public Result<Product> updateProduct(
            @PathVariable Long id,
            @RequestBody Product product,
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Product updatedProduct = productService.updateProduct(id, merchant, product);
        return Result.success(updatedProduct);
    }

    /**
     * 删除商品（商家）
     */
    @DeleteMapping("/{id}")
    @Idempotent(key = "'delete_product_' + #id", expire = 3600)
    @DistributedLock(key = "'delete_product_' + #id", waitTime = 5000)
    @AuditLog(module = "商品管理", action = "删除商品")
    @Retryable(maxAttempts = 3, delay = 1000)
    public Result<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User merchant) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        productService.deleteProduct(id, merchant);
        return Result.success(null);
    }

    /**
     * 审核商品（管理员）
     */
    @PostMapping("/{id}/audit")
    @Idempotent(key = "'audit_product_' + #id", expire = 3600)
    @DistributedLock(key = "'audit_product_' + #id", waitTime = 5000)
    @AuditLog(module = "商品管理", action = "审核商品", recordParams = true, logLevel = AuditLog.LogLevel.INFO)
    public Result<Product> auditProduct(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String rejectReason,
            @AuthenticationPrincipal User admin) {
        
        Product product = productService.auditProduct(id, approved, rejectReason);
        return Result.success(product);
    }

    /**
     * 下架商品（管理员）
     */
    @PostMapping("/{id}/offline")
    @Idempotent(key = "'offline_product_' + #id", expire = 3600)
    @AuditLog(module = "商品管理", action = "下架商品", logLevel = AuditLog.LogLevel.WARNING)
    public Result<Product> takeProductOffline(
            @PathVariable Long id,
            @RequestParam(required = false) String reason,
            @AuthenticationPrincipal User admin) {
        
        Product product = productService.takeProductOffline(id, reason != null ? reason : "违规商品");
        return Result.success(product);
    }

    /**
     * 商家获取商品列表
     */
    @GetMapping("/merchant/list")
    @Cacheable(key = "'merchant_products_' + #merchant.id + '_' + #page", 
               cacheName = "products", expire = 300)
    @AuditLog(module = "商品管理", action = "查询商家商品")
    public Result<Page<Product>> getMerchantProducts(
            @AuthenticationPrincipal User merchant,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer auditStatus) {
        
        if (merchant == null) {
            return Result.error(401, "请先登录");
        }
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productService.getMerchantProducts(merchant, status, auditStatus, pageable);
        return Result.success(products);
    }

    /**
     * 获取待审核商品（管理员）
     */
    @GetMapping("/audit/list")
    @Cacheable(key = "'audit_products_' + #page + '_' + #status", cacheName = "products", expire = 60)
    @AuditLog(module = "商品管理", action = "查询待审核商品")
    public Result<Page<Product>> getAuditProducts(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> products = productService.getAuditProducts(status, null, pageable);
        return Result.success(products);
    }

    /**
     * 获取审核统计（管理员）
     */
    @GetMapping("/audit/stats")
    @Cacheable(key = "'audit_stats'", cacheName = "products", expire = 60)
    @AuditLog(module = "商品管理", action = "查询审核统计")
    public Result<Map<String, Object>> getAuditStats() {
        Map<String, Object> stats = productService.getAuditStats();
        return Result.success(stats);
    }

    /**
     * 排序配置
     */
    private Sort getSortConfig(String sort) {
        return switch (sort) {
            case "sales" -> Sort.by(Sort.Direction.DESC, "sales");
            case "price_asc" -> Sort.by(Sort.Direction.ASC, "price");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "price");
            case "new" -> Sort.by(Sort.Direction.DESC, "createdAt");
            case "rating" -> Sort.by(Sort.Direction.DESC, "rating");
            default -> Sort.by(Sort.Direction.DESC, "createdAt");
        };
    }
}
