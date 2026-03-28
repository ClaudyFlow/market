package com.market.controller;

import com.market.common.Result;
import com.market.entity.Product;
import com.market.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商品控制器（用户端）
 */
@RestController
@RequestMapping("/api/product")
@CrossOrigin(origins = "*")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * 获取商品列表
     */
    @GetMapping("/list")
    public Result<Map<String, Object>> getProducts(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String sortBy) {

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if ("sales".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "sales");
        } else if ("price_asc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.ASC, "price");
        } else if ("price_desc".equals(sortBy)) {
            sort = Sort.by(Sort.Direction.DESC, "price");
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        Page<Product> productPage;

        if (category != null && !category.isEmpty()) {
            productPage = productService.getProductsByCategory(category, pageable);
        } else {
            productPage = productService.getProducts(pageable);
        }

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
    public Result<Map<String, Object>> getProductDetail(@PathVariable Long id) {
        Product product = productService.getProductDetail(id);
        Map<String, Object> result = convertProductToMap(product);
        result.put("description", product.getDescription());
        result.put("images", product.getImageUrls());
        return Result.success(result);
    }

    /**
     * 获取分类列表
     */
    @GetMapping("/categories")
    public Result<List<String>> getCategories() {
        List<String> categories = productService.getCategories();
        return Result.success(categories);
    }

    /**
     * 获取热销商品
     */
    @GetMapping("/hot")
    public Result<List<Map<String, Object>>> getHotProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "sales"));
        Page<Product> productPage = productService.getProducts(pageable);

        List<Map<String, Object>> productList = productPage.getContent().stream()
            .map(this::convertProductToMap)
            .collect(Collectors.toList());

        return Result.success(productList);
    }

    /**
     * 获取新品
     */
    @GetMapping("/new")
    public Result<List<Map<String, Object>>> getNewProducts(
            @RequestParam(defaultValue = "10") Integer limit) {
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Product> productPage = productService.getProducts(pageable);

        List<Map<String, Object>> productList = productPage.getContent().stream()
            .map(this::convertProductToMap)
            .collect(Collectors.toList());

        return Result.success(productList);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/search")
    public Result<Map<String, Object>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage = productService.searchProducts(keyword, pageable);

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

    private Map<String, Object> convertProductToMap(Product product) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", product.getId());
        map.put("name", product.getName());
        map.put("price", product.getPrice());
        map.put("image", product.getImage());
        map.put("category", product.getCategory());
        map.put("sales", product.getSales());
        map.put("stock", product.getStock());
        map.put("status", product.getStatus());
        map.put("createdAt", product.getCreatedAt());
        return map;
    }
}
