package com.market.exception;

import com.market.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一捕获并处理控制器层抛出的各类异常，返回标准化的错误响应
 *
 * @author market-team
 * @since 1.0
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * 处理业务异常
     *
     * @param e 业务异常
     * @return 错误响应结果
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public Result<?> handleBusinessException(BusinessException e) {
        logger.error("业务异常：{}", e.getMessage());
        return Result.error(e.getCode() != null ? e.getCode() : 500, e.getMessage());
    }

    /**
     * 处理参数验证异常
     *
     * @param e 参数验证异常
     * @return 错误响应结果
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleValidationException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("参数验证失败");
        logger.error("参数验证异常：{}", message);
        return Result.error(400, message);
    }

    /**
     * 处理参数绑定异常
     *
     * @param e 绑定异常
     * @return 错误响应结果
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Result<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
            .map(error -> error.getField() + ": " + error.getDefaultMessage())
            .findFirst()
            .orElse("参数绑定失败");
        logger.error("参数绑定异常：{}", message);
        return Result.error(400, message);
    }

    /**
     * 处理认证异常（用户名或密码错误）
     *
     * @param e 认证异常
     * @return 错误响应结果
     */
    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public Result<?> handleBadCredentialsException(BadCredentialsException e) {
        logger.error("认证异常：{}", e.getMessage());
        return Result.error(401, "用户名或密码错误");
    }

    /**
     * 处理权限不足异常
     *
     * @param e 权限不足异常
     * @return 错误响应结果
     */
    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public Result<?> handleAccessDeniedException(AccessDeniedException e) {
        logger.error("权限不足：{}", e.getMessage());
        return Result.error(403, "权限不足");
    }

    /**
     * 处理其他未捕获的异常
     *
     * @param e 异常
     * @return 错误响应结果
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Result<?> handleException(Exception e) {
        logger.error("系统异常：{}", e.getMessage(), e);
        return Result.error("系统繁忙，请稍后再试");
    }
}
