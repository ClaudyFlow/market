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
 */
@RestController
@RequestMapping("/api/credit")
@CrossOrigin(origins = "*")
public class CreditController {
    
    @Autowired
    private CreditService creditService;

    @GetMapping
    public ResponseEntity<Map<String, Object>> getPoints(@AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("credit", user.getCredit());
        result.put("totalCredit", user.getTotalCredit());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<CreditHistory>> getHistory(@AuthenticationPrincipal User user) {
        List<CreditHistory> history = creditService.getCreditHistory(user.getId());
        return ResponseEntity.ok(history);
    }

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
