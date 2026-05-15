package com.market.controller;

import com.market.common.Result;
import com.market.entity.ProductReview;
import com.market.entity.User;
import com.market.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 商家端商品评价控制器
 */
@RestController
@RequestMapping("/api/merchant/review")
@CrossOrigin(origins = "*")
public class MerchantReviewController {

    @Autowired
    private UserAccountService userAccountService;

    /**
     * 获取商品评价列表（商家端）
     */
    @GetMapping("/product/list")
    public Result<Map<String, Object>> getProductReviews(
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size,
            @RequestParam(required = false) Long productId,
            @RequestParam(required = false) Integer rating,
            @AuthenticationPrincipal User merchant) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ProductReview> reviewPage;

        if (productId != null) {
            reviewPage = userAccountService.getMerchantProductReviews(merchant, productId, pageable);
        } else {
            reviewPage = userAccountService.getMerchantAllReviews(merchant, pageable);
        }

        List<Map<String, Object>> reviewList = reviewPage.getContent().stream()
            .map(this::convertReviewToMap)
            .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("list", reviewList);
        response.put("total", reviewPage.getTotalElements());
        response.put("page", page);
        response.put("size", size);

        return Result.success(response);
    }

    /**
     * 回复商品评价
     */
    @PostMapping("/{id}/reply")
    public Result<Void> replyReview(
            @PathVariable Long id,
            @RequestParam String content,
            @AuthenticationPrincipal User merchant) {

        userAccountService.replyMerchantReview(id, merchant, content);
        return Result.success();
    }

    /**
     * 获取评价统计
     */
    @GetMapping("/stats")
    public Result<Map<String, Object>> getReviewStats(@AuthenticationPrincipal User merchant) {
        Map<String, Object> stats = userAccountService.getMerchantReviewStats(merchant);
        return Result.success(stats);
    }

    /**
     * 转换评价对象为 Map
     */
    private Map<String, Object> convertReviewToMap(ProductReview review) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", review.getId());
        map.put("userName", review.getUserName());
        map.put("userAvatar", review.getUserAvatar());
        map.put("productName", review.getProductName());
        map.put("productId", review.getProductId());
        map.put("rating", review.getRating());
        map.put("content", review.getContent());
        map.put("images", review.getImages());
        map.put("merchantReply", review.getMerchantReply());
        map.put("createTime", review.getCreatedAt());
        map.put("replyTime", review.getReplyTime());
        map.put("status", review.getStatus());
        return map;
    }
}
