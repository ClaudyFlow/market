package com.market.controller;

import com.market.service.EmailService;
import com.market.service.EmailValidatorService;
import com.market.service.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 邮箱验证控制器
 * 处理邮箱验证码的发送和验证请求，集成邮箱验证服务进行格式和域名验证。
 * 权限要求：公开接口，无需登录（仅在 spring.mail.enabled=true 时启用）
 *
 * @author market-team
 * @since 1.0
 * @RequestMapping /api/auth
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@ConditionalOnProperty(name = "spring.mail.enabled", havingValue = "true", matchIfMissing = false)
public class EmailController {

    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    private final EmailService emailService;

    private final VerificationCodeService verificationCodeService;

    private final EmailValidatorService emailValidatorService;

    public EmailController(
            EmailService emailService,
            VerificationCodeService verificationCodeService,
            EmailValidatorService emailValidatorService) {
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
        this.emailValidatorService = emailValidatorService;
    }

    /**
     * 发送邮箱验证码
     * API路径：POST /api/auth/send-code
     * 权限：公开
     *
     * @param request 请求体（包含邮箱地址 email 字段）
     * @return 发送结果
     */
    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "邮箱不能为空"));
        }

        String validationError = emailValidatorService.getEmailValidationError(email);
        if (!validationError.isEmpty()) {
            log.warn("邮箱验证失败: {}, 邮箱: {}", validationError, email);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", validationError));
        }

        String code = emailService.generateVerificationCode();

        boolean sent = emailService.sendVerificationCode(email, code);

        if (sent) {
            verificationCodeService.storeCode(email, code);
            log.info("验证码已发送到邮箱: {}", email);
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "验证码已发送，请查收邮箱"
            ));
        } else {
            log.error("验证码发送失败: {}", email);
            return ResponseEntity.ok(Map.of("success", false, "message", "验证码发送失败，请稍后重试"));
        }
    }

    /**
     * 验证邮箱验证码
     * API路径：POST /api/auth/verify-code
     * 权限：公开
     *
     * @param request 请求体（包含邮箱 email 和验证码 verificationCode 字段）
     * @return 验证结果
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("verificationCode");

        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "邮箱和验证码不能为空"));
        }

        boolean isValid = verificationCodeService.verifyCode(email, code);

        if (!isValid) {
            return ResponseEntity.ok(Map.of("success", false, "message", "验证码错误或已过期，请重新获取"));
        }

        log.info("验证码验证成功: {}", email);
        return ResponseEntity.ok(Map.of("success", true, "message", "验证码验证成功"));
    }
}
