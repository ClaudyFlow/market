package com.market.controller;

import com.market.entity.CreditHistory;
import com.market.entity.User;
import com.market.service.PointsService;
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
    private PointsService pointsService;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> getPoints(@AuthenticationPrincipal User user) {
        Map<String, Object> result = new HashMap<>();
        result.put("points", user.getPoints());
        result.put("totalPoints", user.getTotalPoints());
        return ResponseEntity.ok(result);
    }
    
    @GetMapping("/history")
    public ResponseEntity<List<CreditHistory>> getHistory(@AuthenticationPrincipal User user) {
        List<CreditHistory> history = pointsService.getUserPointsHistory(user.getId());
        return ResponseEntity.ok(history);
    }

    @PostMapping("/redeem")
    public ResponseEntity<?> redeemPoints(
            @AuthenticationPrincipal User user,
            @RequestParam Integer points,
            @RequestParam String reason) {
        try {
            boolean success = pointsService.deductPoints(user.getId(), points, reason);
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
