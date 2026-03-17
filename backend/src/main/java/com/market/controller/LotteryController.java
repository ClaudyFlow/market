package com.market.controller;

import com.market.entity.User;
import com.market.service.LotteryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 抽奖控制器
 */
@RestController
@RequestMapping("/api/lottery")
@CrossOrigin(origins = "*")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    /**
     * 抽奖
     */
    @PostMapping("/draw")
    public ResponseEntity<LotteryService.LotteryResult> draw(@AuthenticationPrincipal User user) {
        LotteryService.LotteryResult result = lotteryService.draw(user);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取抽奖记录
     */
    @GetMapping("/records")
    public ResponseEntity<List<LotteryService.LotteryRecordDto>> getRecords(@AuthenticationPrincipal User user) {
        List<LotteryService.LotteryRecordDto> records = lotteryService.getRecords(user.getId());
        return ResponseEntity.ok(records);
    }

    /**
     * 获取奖品列表
     */
    @GetMapping("/prizes")
    public ResponseEntity<List<LotteryService.LotteryPrizeDto>> getPrizes() {
        List<LotteryService.LotteryPrizeDto> prizes = lotteryService.getPrizes();
        return ResponseEntity.ok(prizes);
    }
}
