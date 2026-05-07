package com.market.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Random;

/**
 * 日志测试服务
 * 模拟用户操作并写入日志到 log 目录
 * 注意：仅在 dev/profile 环境下启用
 */
@Service
@RequiredArgsConstructor
@Profile("dev")
public class LogTestService {

    private static final Logger log = LoggerFactory.getLogger(LogTestService.class);
    private final Random random = new Random();
    
    /**
     * 模拟用户登录到支付的完整流程
     */
    public void simulateUserFlow(int userId) {
        String thread = "user-" + userId;
        
        // 1. 用户登录
        log.info("[{}] 用户登录 userId={}", thread, userId);
        sleep(100);
        
        // 2. 浏览商品
        for (int i = 0; i < 5; i++) {
            log.debug("[{}] 浏览商品 productId={}", thread, 1000 + i);
            sleep(50);
        }
        
        // 3. 加入购物车
        log.info("[{}] 加入购物车 productId=1001, quantity=2", thread);
        sleep(100);
        
        // 4. 创建订单
        log.info("[{}] 创建订单 orderId=5001, amount=299.00", thread);
        sleep(200);
        
        // 5. 支付
        log.info("[{}] 支付成功 orderId=5001, payTime={}", thread, System.currentTimeMillis());
        sleep(100);
        
        // 6. 可能的错误 (10% 概率)
        if (random.nextInt(10) == 0) {
            log.error("[{}] 支付失败 orderId=5001, error=余额不足", thread, 
                new RuntimeException("余额不足"));
        }
        
        // 7. 警告日志 (20% 概率)
        if (random.nextInt(5) == 0) {
            log.warn("[{}] 库存不足 productId=1002, current=5, required=10", thread);
        }
    }
    
    /**
     * 批量测试
     */
    public void batchTest(int userCount) {
        long start = System.currentTimeMillis();
        
        log.info("========== 开始压力测试：{} 用户 ==========", userCount);
        
        for (int i = 0; i < userCount; i++) {
            simulateUserFlow(i);
        }
        
        long duration = System.currentTimeMillis() - start;
        log.info("========== 测试完成：{} 用户，耗时 {}ms, QPS={} ==========", 
            userCount, duration, userCount * 1000L / Math.max(1, duration));
    }
    
    /**
     * 定时测试：已禁用
     * 如需测试请手动调用 batchTest()
     */
    /*
    @Scheduled(fixedRate = 60000)
    public void scheduledTest() {
        int userCount = 10 + random.nextInt(6);
        log.info("【定时任务】开始第 {} 轮压力测试", (System.currentTimeMillis() / 60000));
        batchTest(userCount);
    }
    */
    
    /**
     * 启动时执行一次测试
     */
    @Scheduled(fixedDelay = Long.MAX_VALUE)
    public void initTest() {
        log.info("===========================================");
        log.info("日志系统测试启动");
        log.info("日志目录：log/");
        log.info("测试场景：用户登录 → 浏览 → 加购 → 下单 → 支付");
        log.info("===========================================");
        
        // 执行一次用户测试
        log.info("执行一次用户测试 (user=0)");
        simulateUserFlow(0);
    }
    
    private void sleep(int ms) {
        try {
            Thread.sleep(ms);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
