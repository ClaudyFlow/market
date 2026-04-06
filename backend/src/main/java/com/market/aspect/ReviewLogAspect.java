package com.market.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * 评价日志切面
 */
@Slf4j
@Aspect
@Component
public class ReviewLogAspect {

    @Around("execution(* com.market.service.ReviewService.*(..)) || " +
            "execution(* com.market.service.UserAccountService.createReview(..)) || " +
            "execution(* com.market.service.UserAccountService.updateReview(..)) || " +
            "execution(* com.market.service.UserAccountService.replyToReview(..)) || " +
            "execution(* com.market.controller.*Controller.*review*(..))")
    public Object logReviewOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            log.info("评价操作开始：{}, 参数：{}", methodName, formatArgs(args));
            result = joinPoint.proceed();
            log.info("评价操作成功：{}, 耗时：{}ms", methodName, System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            log.error("评价操作失败：{}, 错误：{}", methodName, e.getMessage(), e);
            throw e;
        }
    }

    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            String argStr = args[i].toString();
            if (argStr.length() > 100) {
                argStr = argStr.substring(0, 100) + "...";
            }
            sb.append(argStr);
        }
        sb.append("]");
        return sb.toString();
    }
}
