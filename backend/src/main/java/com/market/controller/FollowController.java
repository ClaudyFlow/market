package com.market.controller;

import com.market.dto.FollowRequest;
import com.market.dto.FollowResponse;
import com.market.entity.Follow;
import com.market.entity.User;
import com.market.service.FollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 关注控制器
 */
@RestController
@RequestMapping("/api/follow")
@CrossOrigin(origins = "*")
public class FollowController {

    @Autowired
    private FollowService followService;

    /**
     * 获取用户的关注列表
     */
    @GetMapping
    public ResponseEntity<List<FollowResponse>> getFollows(@AuthenticationPrincipal User user) {
        List<Follow> followList = followService.getFavorites(user.getId());

        List<FollowResponse> response = followList.stream()
            .map(follow -> FollowResponse.builder()
                .id(follow.getId())
                .userId(follow.getUserId())
                .shopId(follow.getShopId())
                .shopName(follow.getShopName())
                .shopAvatar(follow.getShopAvatar())
                .createdAt(follow.getCreatedAt())
                .build())
            .collect(Collectors.toList());

        return ResponseEntity.ok(response);
    }

    /**
     * 添加关注
     */
    @PostMapping
    public ResponseEntity<?> addFavorite(
            @AuthenticationPrincipal User user,
            @RequestBody FollowRequest request) {
        try {
            Follow favorite = followService.addFavorite(
                user.getId(), 
                request.getShopId(), 
                request.getShopName(),
                request.getShopAvatar()
            );
            return ResponseEntity.ok(favorite);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 取消关注
     */
    @DeleteMapping("/{shopId}")
    public ResponseEntity<Void> removeFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long shopId) {
        followService.removeFavorite(user.getId(), shopId);
        return ResponseEntity.ok().build();
    }

    /**
     * 检查是否已关注
     */
    @GetMapping("/check/{shopId}")
    public ResponseEntity<Map<String, Boolean>> checkFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long shopId) {
        boolean isFavorite = followService.isFavorite(user.getId(), shopId);
        Map<String, Boolean> response = new HashMap<>();
        response.put("isFavorite", isFavorite);
        return ResponseEntity.ok(response);
    }

    /**
     * 切换关注状态
     */
    @PostMapping("/toggle/{shopId}")
    public ResponseEntity<Map<String, Object>> toggleFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long shopId,
            @RequestBody FollowRequest request) {
        boolean isAdded = followService.toggleFavorite(
            user.getId(), 
            shopId, 
            request.getShopName(),
            request.getShopAvatar()
        );

        Map<String, Object> response = new HashMap<>();
        response.put("isFavorite", isAdded);
        response.put("message", isAdded ? "已添加关注" : "已取消关注");

        return ResponseEntity.ok(response);
    }

    /**
     * 获取关注数量
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Integer>> getFavoriteCount(@AuthenticationPrincipal User user) {
        int count = followService.getFavoriteCount(user.getId());
        Map<String, Integer> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }
}
