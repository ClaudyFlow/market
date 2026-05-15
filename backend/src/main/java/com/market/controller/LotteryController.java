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
 * 提供用户抽奖、抽奖记录查询、奖品列表等功能。
 * 权限要求：抽奖和记录查询需要登录，奖品列表公开
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/lottery
 */
@RestController
@RequestMapping("/api/lottery")
@CrossOrigin(origins = "*")
public class LotteryController {

    @Autowired
    private LotteryService lotteryService;

    /**
     * 执行抽奖
     * API路径：POST /api/lottery/draw
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 抽奖结果
     */
    @PostMapping("/draw")
    public ResponseEntity<LotteryService.LotteryResult> draw(@AuthenticationPrincipal User user) {
        LotteryService.LotteryResult result = lotteryService.draw(user);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取抽奖记录
     * API路径：GET /api/lottery/records
     * 权限：需要登录
     *
     * @param user 当前登录用户
     * @return 用户抽奖记录列表
     */
    @GetMapping("/records")
    public ResponseEntity<List<LotteryService.LotteryRecordDto>> getRecords(@AuthenticationPrincipal User user) {
        List<LotteryService.LotteryRecordDto> records = lotteryService.getRecords(user.getId());
        return ResponseEntity.ok(records);
    }

    /**
     * 获取奖品列表
     * API路径：GET /api/lottery/prizes
     * 权限：公开
     *
     * @return 奖品列表
     */
    @GetMapping("/prizes")
    public ResponseEntity<List<LotteryService.LotteryPrizeDto>> getPrizes() {
        List<LotteryService.LotteryPrizeDto> prizes = lotteryService.getPrizes();
        return ResponseEntity.ok(prizes);
    }
}
