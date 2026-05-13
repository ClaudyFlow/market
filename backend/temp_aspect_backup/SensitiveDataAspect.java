package com.market.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 敏感数据脱敏切面
 */
@Aspect
@Component
public class SensitiveDataAspect {
    
    private static final Logger log = LoggerFactory.getLogger(SensitiveDataAspect.class);
    
    @Before("execution(* com.market.controller.*Controller.*(..))")
    public void logRequest(JoinPoint joinPoint) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();
            String method = request.getMethod();
            String uri = request.getRequestURI();
            String ip = request.getRemoteAddr();
            Object[] args = joinPoint.getArgs();
            
            log.info("【请求】{} {} from IP:{} 参数：{}", method, uri, ip, formatArgs(args));
        }
    }
    
    @AfterReturning(pointcut = "execution(* com.market.controller.*Controller.*(..))", returning = "result")
    public void logResponse(JoinPoint joinPoint, Object result) {
        if (result != null) {
            String resultStr = result.toString();
            if (resultStr.length() > 200) {
                resultStr = resultStr.substring(0, 200) + "...";
            }
            log.info("【响应】返回结果：{}", resultStr);
        } else {
            log.info("【响应】返回结果：null");
        }
    }
    
    @AfterThrowing(pointcut = "execution(* com.market.controller.*Controller.*(..))", throwing = "ex")
    public void logException(JoinPoint joinPoint, Throwable ex) {
        log.error("【异常】方法：{} 错误：{}", joinPoint.getSignature().getName(), ex.getMessage(), ex);
    }
    
    private String formatArgs(Object[] args) {
        if (args == null || args.length == 0) return "[]";
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0) sb.append(", ");
            Object arg = args[i];
            if (arg != null) {
                String argStr = arg.toString();
                if (argStr.length() > 100) {
                    argStr = argStr.substring(0, 100) + "...";
                }
                sb.append(argStr);
            } else {
                sb.append("null");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}