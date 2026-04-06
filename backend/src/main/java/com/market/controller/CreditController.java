package com.market.controller;

import com.market.entity.CreditHistory;
import com.market.entity.User;
import com.market.service.CreditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 积分控制器
 * 提供用户积分查询、历史记录、积分兑换等功能。
 * 权限要求：需要登录
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/credit
 */
@RestController
@RequestMapping("/api/credit")
@CrossOrigin(origins = "*")
public class CreditController {

    @Autowired
    private CreditService creditService;

    /**
     * 获取用户积分信息
     * API路径：GET /api/credit
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 当前积分和总积分
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPoints(@AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("credit", user.getCredit());
        result.put("totalCredit", user.getTotalCredit());
        return ResponseEntity.ok(result);
    }

    /**
     * 获取积分历史记录
     * API路径：GET /api/credit/history
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 积分历史记录列表
     */
    @GetMapping("/history")
    public ResponseEntity<List<CreditHistory>> getHistory(@AuthenticationPrincipal User user) {
        List<CreditHistory> history = creditService.getCreditHistory(user.getId());
        return ResponseEntity.ok(history);
    }

    /**
     * 积分兑换
     * API路径：POST /api/credit/redeem
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @param credit 兑换消耗的积分数
     * @param reason 兑换原因
     * @return 兑换结果
     */
    @PostMapping("/redeem")
    public ResponseEntity<?> redeemPoints(
            @AuthenticationPrincipal User user,
            @RequestParam Integer credit,
            @RequestParam String reason) {
        try {
            boolean success = creditService.deductCredit(user.getId(), credit, reason);
            if (success) {
                return ResponseEntity.ok("兑换成功");
            } else {
                return ResponseEntity.badRequest().body("积分不足");
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
