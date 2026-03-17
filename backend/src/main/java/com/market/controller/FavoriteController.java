package com.market.controller;

import com.market.dto.FavoriteRequest;
import com.market.dto.FavoriteResponse;
import com.market.entity.Favorite;
import com.market.entity.Product;
import com.market.entity.User;
import com.market.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏控制器
 */
@RestController
@RequestMapping("/api/favorite")
@CrossOrigin(origins = "*")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    /**
     * 获取用户的收藏列表
     */
    @GetMapping
    public ResponseEntity<List<FavoriteResponse>> getFavorites(@AuthenticationPrincipal User user) {
        List<Favorite> favoriteList = favoriteService.getFavorites(user.getId());

        List<FavoriteResponse> response = favoriteList.stream()
            .map(fav -> {
                Product product = fav.getProduct();
                return FavoriteResponse.builder()
                    .id(fav.getId())
                    .userId(fav.getUserId())
                    .productId(fav.getProductId())
                    .productName(product.getName())
                    .productImage(product.getImageUrl())
                    .productPrice(product.getPrice().toString())
                    .createdAt(fav.getCreatedAt())
                    .build();
            })
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 添加收藏
     */
    @PostMapping
    public ResponseEntity<?> addFavorite(
            @AuthenticationPrincipal User user,
            @RequestBody FavoriteRequest request) {
        try {
            Favorite favorite = favoriteService.addFavorite(user.getId(), request.getProductId());
            return ResponseEntity.ok(favorite);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 取消收藏
     */
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        favoriteService.removeFavorite(user.getId(), productId);
        return ResponseEntity.ok().build();
    }

    /**
     * 检查是否已收藏
     */
    @GetMapping("/check/{productId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        boolean isFavorite = favoriteService.isFavorite(user.getId(), productId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFavorite);
        return ResponseEntity.ok(response);
    }

    /**
     * 切换收藏状态
     */
    @PostMapping("/toggle/{productId}")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long productId) {
        boolean isAdded = favoriteService.toggleFavorite(user.getId(), productId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("isFavorite", isAdded);
        response.put("message", isAdded ? "已添加收藏" : "已取消收藏");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取收藏数量
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getFavoriteCount(@AuthenticationPrincipal User user) {
        int count = favoriteService.getFavoriteCount(user.getId());
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
