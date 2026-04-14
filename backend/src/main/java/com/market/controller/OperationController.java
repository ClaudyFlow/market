package com.market.controller;

import com.market.common.Result;
import com.market.entity.SensitiveWord;
import com.market.service.ForumService;
import com.market.service.OperationAnalysisService;
import com.market.service.ReviewService;
import com.market.service.SensitiveWordFilterService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 运营功能控制器
 * 提供运营数据分析、敏感词管理、内容审核等功能
 */
@RestController
@RequestMapping("/api/operations")
@CrossOrigin(origins = "*")
public class OperationController {

    @Autowired
    private OperationAnalysisService operationAnalysisService;

    @Autowired
    private SensitiveWordFilterService sensitiveWordFilterService;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private ForumService forumService;

    // ==================== 数据分析 ====================

    /**
     * 获取漏斗分析数据
     * GET /api/operations/funnel?days=30
     */
    @GetMapping("/funnel")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getFunnelAnalysis(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> data = operationAnalysisService.getFunnelAnalysis(days);
        return Result.success(data);
    }

    /**
     * 获取留存分析数据
     * GET /api/operations/retention?days=30
     */
    @GetMapping("/retention")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getRetentionAnalysis(
            @RequestParam(defaultValue = "30") int days) {
        Map<String, Object> data = operationAnalysisService.getRetentionAnalysis(days);
        return Result.success(data);
    }

    /**
     * 获取商品转化率分析
     * GET /api/operations/product-conversion?limit=50
     */
    @GetMapping("/product-conversion")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Map<String, Object>>> getProductConversionAnalysis(
            @RequestParam(defaultValue = "50") int limit) {
        List<Map<String, Object>> data = operationAnalysisService.getProductConversionAnalysis(limit);
        return Result.success(data);
    }

    // ==================== 敏感词管理 ====================

    /**
     * 获取敏感词列表
     * GET /api/operations/sensitive-words?page=0&size=20&type=CUSTOM
     */
    @GetMapping("/sensitive-words")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getSensitiveWords(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type) {
        
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<SensitiveWord> wordPage;
        
        if (type != null && !type.isEmpty()) {
            wordPage = sensitiveWordFilterService.getSensitiveWordRepository().findByType(type, pageRequest);
        } else {
            wordPage = sensitiveWordFilterService.getSensitiveWordRepository().findAll(pageRequest);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("words", wordPage.getContent());
        result.put("total", wordPage.getTotalElements());
        result.put("pages", wordPage.getTotalPages());

        return Result.success(result);
    }

    /**
     * 添加敏感词
     * POST /api/operations/sensitive-words
     */
    @PostMapping("/sensitive-words")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<SensitiveWord> addSensitiveWord(@RequestBody Map<String, String> request) {
        String word = request.get("word");
        String type = request.getOrDefault("type", "CUSTOM");
        String level = request.getOrDefault("level", "MEDIUM");
        String replacement = request.get("replacement");

        SensitiveWord saved = sensitiveWordFilterService.addSensitiveWord(word, type, level, replacement);
        return Result.success(saved);
    }

    /**
     * 删除敏感词
     * DELETE /api/operations/sensitive-words/{id}
     */
    @DeleteMapping("/sensitive-words/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> deleteSensitiveWord(@PathVariable Long id) {
        sensitiveWordFilterService.deleteSensitiveWord(id);
        return Result.success(null);
    }

    /**
     * 批量导入敏感词
     * POST /api/operations/sensitive-words/batch
     */
    @PostMapping("/sensitive-words/batch")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> batchImportSensitiveWords(@RequestBody List<SensitiveWord> words) {
        int count = sensitiveWordFilterService.batchImportSensitiveWords(words);
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("importedCount", count);
        result.put("totalWords", sensitiveWordFilterService.getSensitiveWordStats().get("totalWords"));
        return Result.success(result);
    }

    /**
     * 获取敏感词统计
     * GET /api/operations/sensitive-words/stats
     */
    @GetMapping("/sensitive-words/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getSensitiveWordStats() {
        Map<String, Object> stats = sensitiveWordFilterService.getSensitiveWordStats();
        return Result.success(stats);
    }

    /**
     * 测试敏感词过滤
     * POST /api/operations/sensitive-words/test
     */
    @PostMapping("/sensitive-words/test")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> testSensitiveWordFilter(@RequestBody Map<String, String> request) {
        String text = request.get("text");
        SensitiveWordFilterService.DetectionResult result = sensitiveWordFilterService.detectSensitiveWords(text);
        
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("originalText", text);
        response.put("hasSensitive", result.hasSensitive());
        response.put("foundWords", result.getFoundWords());
        response.put("filteredText", result.getFilteredText());
        
        return Result.success(response);
    }

    // ==================== 内容审核 ====================

    /**
     * 获取评价审核统计
     * GET /api/operations/review-audit/stats
     */
    @GetMapping("/review-audit/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getReviewAuditStats() {
        Map<String, Object> stats = reviewService.getAuditStats();
        return Result.success(stats);
    }

    /**
     * 获取待审核评价列表
     * GET /api/operations/review-audit/pending?limit=50
     */
    @GetMapping("/review-audit/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<List<Map<String, Object>>> getPendingReviews(
            @RequestParam(defaultValue = "50") int limit) {
        List<Map<String, Object>> reviews = reviewService.getPendingReviews(limit).stream()
                .map(r -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("id", r.getId());
                    map.put("userId", r.getUserId());
                    map.put("userName", r.getUserName());
                    map.put("productId", r.getProductId());
                    map.put("productName", r.getProductName());
                    map.put("rating", r.getRating());
                    map.put("content", r.getContent());
                    map.put("auditStatus", r.getAuditStatus());
                    map.put("auditReason", r.getAuditReason());
                    map.put("createdAt", r.getCreatedAt());
                    return map;
                })
                .toList();
        return Result.success(reviews);
    }

    /**
     * 审核评价
     * PUT /api/operations/review-audit/{reviewId}
     */
    @PutMapping("/review-audit/{reviewId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> auditReview(@PathVariable Long reviewId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        String reason = request.get("reason");
        reviewService.auditReview(reviewId, status, reason);
        return Result.success(null);
    }

    /**
     * 获取论坛帖子审核统计
     * GET /api/operations/forum-audit/stats
     */
    @GetMapping("/forum-audit/stats")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getForumAuditStats() {
        Map<String, Object> stats = forumService.getAuditStats();
        return Result.success(stats);
    }

    /**
     * 获取待审核论坛帖子
     * GET /api/operations/forum-audit/pending?page=0&size=20
     */
    @GetMapping("/forum-audit/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getPendingPosts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Map<String, Object> result = forumService.getPendingPosts(page, size);
        return Result.success(result);
    }

    /**
     * 审核论坛帖子
     * PUT /api/operations/forum-audit/{postId}
     */
    @PutMapping("/forum-audit/{postId}")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Void> auditForumPost(@PathVariable Long postId, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        String reason = request.get("reason");
        forumService.auditPost(postId, status, reason);
        return Result.success(null);
    }

    // ==================== 综合运营数据 ====================

    /**
     * 获取运营数据总览
     * GET /api/operations/dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public Result<Map<String, Object>> getOperationsDashboard() {
        Map<String, Object> dashboard = new LinkedHashMap<>();
        
        // 漏斗分析 (近7天)
        dashboard.put("funnel", operationAnalysisService.getFunnelAnalysis(7));
        
        // 留存分析
        dashboard.put("retention", operationAnalysisService.getRetentionAnalysis(30));
        
        // 评价审核统计
        dashboard.put("reviewAudit", reviewService.getAuditStats());
        
        // 论坛审核统计
        dashboard.put("forumAudit", forumService.getAuditStats());
        
        // 敏感词统计
        dashboard.put("sensitiveWord", sensitiveWordFilterService.getSensitiveWordStats());
        
        return Result.success(dashboard);
    }
}
