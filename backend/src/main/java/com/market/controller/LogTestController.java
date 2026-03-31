package com.market.controller;

import com.market.common.Result;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

/**
 * 日志测试控制器
 */
@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class LogTestController {

    private static final Logger log = LoggerFactory.getLogger(LogTestController.class);

    /**
     * 生成测试日志
     */
    @GetMapping("/logs")
    public Result<String> generateLogs(
            @RequestParam(defaultValue = "10") Integer count,
            @RequestParam(defaultValue = "test") String type) {
        
        log.info("========== 开始生成测试日志：count={}, type={} ==========", count, type);
        
        for (int i = 1; i <= count; i++) {
            switch (i % 5) {
                case 1:
                    log.debug("[TEST-{}] 调试日志 {} - 这是一条调试信息", type, i);
                    break;
                case 2:
                    log.info("[TEST-{}] 信息日志 {} - 用户操作成功", type, i);
                    break;
                case 3:
                    log.warn("[TEST-{}] 警告日志 {} - 资源使用率超过 80%", type, i);
                    break;
                case 4:
                    log.error("[TEST-{}] 错误日志 {} - 数据库连接失败", type, i, 
                        new RuntimeException("连接超时"));
                    break;
                case 0:
                    log.info("[TEST-{}] 信息日志 {} - 订单创建成功 orderId={}", type, i, 1000 + i);
                    break;
            }
        }
        
        log.info("========== 测试日志生成完成：{} 条 ==========", count);
        
        return Result.success("日志生成成功：" + count + "条，类型：" + type);
    }
    
    /**
     * 模拟用户操作流程
     */
    @GetMapping("/user-flow")
    public Result<String> simulateUserFlow() {
        String userId = "user_" + System.currentTimeMillis() % 10000;
        
        log.info("【用户流程】开始 userId={}", userId);
        
        // 1. 登录
        log.info("【用户流程】用户登录 userId={}", userId);
        
        // 2. 浏览商品
        log.debug("【用户流程】浏览商品 productId=1001");
        log.debug("【用户流程】浏览商品 productId=1002");
        log.debug("【用户流程】浏览商品 productId=1003");
        
        // 3. 加入购物车
        log.info("【用户流程】加入购物车 productId=1001, quantity=2");
        
        // 4. 创建订单
        log.info("【用户流程】创建订单 orderId=5001, amount=299.00");
        
        // 5. 支付
        log.info("【用户流程】支付成功 orderId=5001");
        
        log.info("【用户流程】完成 userId={}", userId);
        
        return Result.success("用户流程模拟完成：" + userId);
    }
}
