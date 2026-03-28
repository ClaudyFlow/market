package com.market.controller;

import com.market.entity.ForumPost;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 搜索控制器
 */
@RestController
@RequestMapping("/api/search")
@CrossOrigin(origins = "*")
public class SearchController {

    @Autowired
    private SearchService searchService;

    /**
     * 综合搜索
     */
    @GetMapping("/all")
    public ResponseEntity<Map<String, Object>> searchAll(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        SearchService.SearchResult result = searchService.searchAll(keyword, pageable);

        Map<String, Object> response = new HashMap<>();

        // 商品结果
        List<Map<String, Object>> productList = result.getProducts().getContent().stream()
            .map(product -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", product.getId());
                map.put("name", product.getName());
                map.put("price", product.getPrice());
                map.put("image", product.getImage());
                map.put("category", product.getCategory());
                map.put("sales", product.getSales());
                map.put("type", "product");
                return map;
            })
            .collect(Collectors.toList());

        // 帖子结果
        List<Map<String, Object>> postList = result.getPosts().getContent().stream()
            .map(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", post.getId());
                map.put("title", post.getTitle());
                map.put("content", post.getContent().substring(0, Math.min(100, post.getContent().length())));
                map.put("userName", post.getUserName());
                map.put("likeCount", post.getLikeCount());
                map.put("viewCount", post.getViewCount());
                map.put("type", "post");
                return map;
            })
            .collect(Collectors.toList());

        // 用户结果
        List<Map<String, Object>> userList = result.getUsers().getContent().stream()
            .map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("name", user.getName());
                map.put("avatar", user.getAvatarUrl());
                map.put("type", "user");
                return map;
            })
            .collect(Collectors.toList());

        response.put("products", productList);
        response.put("posts", postList);
        response.put("users", userList);
        response.put("total", result.getTotal());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    /**
     * 搜索商品
     */
    @GetMapping("/products")
    public ResponseEntity<Map<String, Object>> searchProducts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<Product> productPage = searchService.searchProducts(keyword, pageable);

        List<Map<String, Object>> productList = productPage.getContent().stream()
            .map(product -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", product.getId());
                map.put("name", product.getName());
                map.put("price", product.getPrice());
                map.put("image", product.getImage());
                map.put("category", product.getCategory());
                map.put("sales", product.getSales());
                map.put("description", product.getDescription());
                return map;
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", productList);
        response.put("total", productPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    /**
     * 搜索帖子
     */
    @GetMapping("/posts")
    public ResponseEntity<Map<String, Object>> searchPosts(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<ForumPost> postPage = searchService.searchPosts(keyword, pageable);

        List<Map<String, Object>> postList = postPage.getContent().stream()
            .map(post -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", post.getId());
                map.put("title", post.getTitle());
                map.put("content", post.getContent());
                map.put("userName", post.getUserName());
                map.put("userAvatar", post.getUserAvatar());
                map.put("likeCount", post.getLikeCount());
                map.put("commentCount", post.getCommentCount());
                map.put("viewCount", post.getViewCount());
                map.put("createdAt", post.getCreatedAt());
                return map;
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", postList);
        response.put("total", postPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    /**
     * 搜索用户
     */
    @GetMapping("/users")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {

        Pageable pageable = PageRequest.of(page - 1, size);
        Page<User> userPage = searchService.searchUsers(keyword, pageable);

        List<Map<String, Object>> userList = userPage.getContent().stream()
            .map(user -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", user.getId());
                map.put("name", user.getName());
                map.put("avatar", user.getAvatarUrl());
                map.put("vipLevel", user.getVipLevel());
                return map;
            })
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", userList);
        response.put("total", userPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return ResponseEntity.ok(response);
    }

    /**
     * 获取热门搜索词
     */
    @GetMapping("/hot")
    public ResponseEntity<List<String>> getHotKeywords() {
        List<String> hotKeywords = searchService.getHotKeywords();
        return ResponseEntity.ok(hotKeywords);
    }

    /**
     * 获取搜索建议
     */
    @GetMapping("/suggestions")
    public ResponseEntity<List<String>> getSuggestions(@RequestParam String keyword) {
        List<String> suggestions = searchService.getSuggestions(keyword);
        return ResponseEntity.ok(suggestions);
    }
}
