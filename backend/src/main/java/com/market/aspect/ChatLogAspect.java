package com.market.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 客服聊天日志切面
 */
@Slf4j
@Aspect
@Component
public class ChatLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ChatLogAspect.class);

    @Around("execution(* com.market.service.ChatService.*(..)) || " +
            "execution(* com.market.controller.ChatController.*(..))")
    public Object logChatOperation(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        
        long startTime = System.currentTimeMillis();
        Object result;
        
        try {
            log.info("聊天操作开始：{}, 参数：{}", methodName, formatArgs(args));
            result = joinPoint.proceed();
            log.info("聊天操作成功：{}, 耗时：{}ms", methodName, System.currentTimeMillis() - startTime);
            return result;
        } catch (Exception e) {
            log.error("聊天操作失败：{}, 错误：{}", methodName, e.getMessage(), e);
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
