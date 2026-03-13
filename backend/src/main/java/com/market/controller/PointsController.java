package com.market.controller;

import com.market.entity.PointsHistory;
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
@RequestMapping("/api/points")
@CrossOrigin(origins = "*")
public class PointsController {
    
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
    public ResponseEntity<List<PointsHistory>> getHistory(@AuthenticationPrincipal User user) {
        List<PointsHistory> history = pointsService.getUserHistory(user);
        return ResponseEntity.ok(history);
    }
    
    @PostMapping("/exchange")
    public ResponseEntity<?> exchangePoints(
            @AuthenticationPrincipal User user,
            @RequestParam Integer points,
            @RequestParam String couponType) {
        try {
            pointsService.exchangePoints(user, points, couponType);
            return ResponseEntity.ok("兑换成功");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
