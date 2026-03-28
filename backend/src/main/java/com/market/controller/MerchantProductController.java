package com.market.controller;

import com.market.common.Result;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家端商品控制器
 */
@RestController
@RequestMapping("/api/merchant/product")
@CrossOrigin(origins = "*")
public class MerchantProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     */
    @GetMapping
    public Result<Map<String, Object>> getProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Integer status,
            @AuthenticationPrincipal User user) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage = productService.getMerchantProducts(user, keyword, status, pageable);

        List<Map<String, Object>> productList = productPage.getContent().stream()
            .map(this::convertProductToMap)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", productList);
        response.put("total", productPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Map<String, Object>> getProductDetail(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        Product product = productService.getProductById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        // 验证商品是否属于该商户
        if (!productService.isMerchantProduct(product, user)) {
            throw new RuntimeException("无权访问该商品");
        }

        return Result.success(convertProductToMap(product));
    }

    /**
     * 创建商品
     */
    @PostMapping
    public Result<Product> createProduct(
            @AuthenticationPrincipal User user,
            @RequestBody Product product) {

        Product created = productService.createProduct(user, product);
        return Result.success(created);
    }

    /**
     * 更新商品
     */
    @PutMapping("/{id}")
    public Result<Product> updateProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestBody Product product) {

        Product updated = productService.updateProduct(id, user, product);
        return Result.success(updated);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteProduct(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        productService.deleteProduct(id, user);
        return Result.success();
    }

    /**
     * 上架/下架商品
     */
    @PutMapping("/{id}/status")
    public Result<Product> toggleProductStatus(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @RequestParam Integer status) {

        Product product = productService.getProductById(id)
            .orElseThrow(() -> new RuntimeException("商品不存在"));

        if (!productService.isMerchantProduct(product, user)) {
            throw new RuntimeException("无权操作该商品");
        }

        product.setStatus(status);
        Product updated = productService.updateProduct(id, product);
        return Result.success(updated);
    }

    /**
     * 转换 Product 对象为 Map
     */
    private Map<String, Object> convertProductToMap(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", product.getId());
        map.put("name", product.getName());
        map.put("description", product.getDescription());
        map.put("price", product.getPrice());
        map.put("originalPrice", product.getOriginalPrice());
        map.put("image", product.getImage());
        map.put("imageUrls", product.getImageUrls());
        map.put("category", product.getCategory());
        map.put("brand", product.getBrand());
        map.put("stock", product.getStock());
        map.put("sales", product.getSales());
        map.put("status", product.getStatus());
        map.put("rating", product.getRating());
        map.put("reviewCount", product.getReviewCount());
        map.put("createdAt", product.getCreatedAt());
        map.put("updatedAt", product.getUpdatedAt());

        // 商户信息
        if (product.getMerchant() != null) {
            map.put("merchantId", product.getMerchant().getId());
            map.put("merchantName", product.getMerchant().getShopName());
            map.put("shopName", product.getMerchant().getShopName());
        }

        return map;
    }
}
