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
 * 提供用户浏览记录的查询、搜索、删除、清空和统计等功能。
 * 权限要求：需要登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/history
 */
@RestController
@RequestMapping("/api/history")
@CrossOrigin(origins = "*")
public class UserBrowseHistoryController {

    @Autowired
    private UserBrowseHistoryService userBrowseHistoryService;

    /**
     * 获取浏览历史（分页）
     * API路径：GET /api/history/list
     * 权限：需要登录
     *
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @param user 当前登录用户
     * @return 分页的浏览历史记录
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
     * 获取所有浏览历史列表
     * API路径：GET /api/history/all
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 全部浏览历史记录
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
     * API路径：GET /api/history/recent
     * 权限：需要登录
     *
     * @param limit 数量限制，默认10
     * @param user 当前登录用户
     * @return 最近浏览记录
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
     * 删除单条浏览历史
     * API路径：DELETE /api/history/product/{productId}
     * 权限：需要登录
     *
     * @param productId 商品ID
     * @param user 当前登录用户
     * @return 删除结果
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
     * API路径：DELETE /api/history/clear
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 清空结果
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
     * API路径：GET /api/history/count
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 浏览历史记录数量
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
     * API路径：GET /api/history/search
     * 权限：需要登录
     *
     * @param keyword 搜索关键词
     * @param page 页码，默认0
     * @param size 每页大小，默认20
     * @param user 当前登录用户
     * @return 分页的浏览历史搜索结果
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
