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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 管理端商品审核控制器
 */
@RestController
@RequestMapping("/api/admin/product")
@CrossOrigin(origins = "*")
public class AdminProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取待审核商品列表
     */
    @GetMapping("/audit/list")
    public Result<Map<String, Object>> getAuditProductList(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) Long merchantId) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage = productService.getAuditProducts(status, merchantId, pageable);

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
     * 审核商品
     */
    @PutMapping("/{id}/audit")
    public Result<Product> auditProduct(
            @PathVariable Long id,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String rejectReason,
            @AuthenticationPrincipal User admin) {

        Product product = productService.auditProduct(id, approved, rejectReason);
        return Result.success(product);
    }

    /**
     * 获取商品详情
     */
    @GetMapping("/{id}")
    public Result<Product> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductById(id);
        return Result.success(product);
    }

    /**
     * 删除商品
     */
    @DeleteMapping("/{id}")
    public Result<Void> removeProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return Result.success();
    }

    /**
     * 获取商品审核统计
     */
    @GetMapping("/audit/stats")
    public Result<Map<String, Object>> getAuditStats() {
        Map<String, Object> stats = productService.getAuditStats();
        return Result.success(stats);
    }

    /**
     * 强制下架商品
     */
    @PutMapping("/{id}/offline")
    public Result<Product> takeProductOffline(
            @PathVariable Long id,
            @RequestParam String reason,
            @AuthenticationPrincipal User admin) {

        Product product = productService.takeProductOffline(id, reason);
        return Result.success(product);
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
        map.put("auditStatus", product.getAuditStatus());
        map.put("rejectReason", product.getRejectReason());

        // 商户信息
        if (product.getMerchant() != null) {
            map.put("merchantId", product.getMerchant().getId());
            map.put("merchantName", product.getMerchant().getName());
            map.put("shopName", product.getMerchant().getShopName());
        }

        return map;
    }
}
