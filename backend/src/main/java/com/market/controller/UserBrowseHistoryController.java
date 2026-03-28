package com.market.controller;

import com.market.dto.UserBrowseHistoryResponse;
import com.market.entity.User;
import com.market.service.UserBrowseHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户浏览历史控制器
 *
 * @author Market Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class UserBrowseHistoryController {

    @Autowired
    private UserBrowseHistoryService userBrowseHistoryService;

    /**
     * 获取浏览历史（分页）
     */
    @GetMapping("/list")
    public ResponseEntity<Page<UserBrowseHistoryResponse>> getBrowseHistory(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "browseTime"));
        Page<UserBrowseHistoryResponse> history = userBrowseHistoryService.getBrowseHistory(
                user.getId(), pageable);
        return ResponseEntity.ok(history);
    }

    /**
     * 获取浏览历史列表
     */
    @GetMapping("/all")
    public ResponseEntity<List<UserBrowseHistoryResponse>> getAllHistory(
            @AuthenticationPrincipal User user) {
        List<UserBrowseHistoryResponse> history = userBrowseHistoryService
                .getBrowseHistoryList(user.getId());
        return ResponseEntity.ok(history);
    }

    /**
     * 获取最近浏览记录
     */
    @GetMapping("/recent")
    public ResponseEntity<List<UserBrowseHistoryResponse>> getRecentHistory(
            @RequestParam(defaultValue = "10") int limit,
            @AuthenticationPrincipal User user) {
        List<UserBrowseHistoryResponse> history = userBrowseHistoryService
                .getRecentHistory(user.getId(), limit);
        return ResponseEntity.ok(history);
    }

    /**
     * 删除浏览历史
     */
    @DeleteMapping("/product/{productId}")
    public ResponseEntity<Map<String, Object>> deleteHistory(
            @PathVariable Long productId,
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userBrowseHistoryService.deleteHistory(user.getId(), productId);
        result.put("success", success);
        result.put("message", "浏览记录删除成功");
        return ResponseEntity.ok(result);
    }

    /**
     * 清空浏览历史
     */
    @DeleteMapping("/clear")
    public ResponseEntity<Map<String, Object>> clearHistory(
            @AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        boolean success = userBrowseHistoryService.clearHistory(user.getId());
        result.put("success", success);
        result.put("message", "浏览历史已清空");
        return ResponseEntity.ok(result);
    }

    /**
     * 获取浏览历史数量
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Long>> getHistoryCount(
            @AuthenticationPrincipal User user) {
        Map<String, Long> result = new HashMap<>();
        result.put("count", userBrowseHistoryService.getHistoryCount(user.getId()));
        return ResponseEntity.ok(result);
    }

    /**
     * 搜索浏览历史
     */
    @GetMapping("/search")
    public ResponseEntity<Page<UserBrowseHistoryResponse>> searchHistory(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "browseTime"));
        Page<UserBrowseHistoryResponse> history = userBrowseHistoryService.searchHistory(
                user.getId(), keyword, pageable);
        return ResponseEntity.ok(history);
    }
}
