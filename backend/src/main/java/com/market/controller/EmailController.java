package com.market.controller;

import com.market.service.EmailService;
import com.market.service.EmailValidatorService;
import com.market.service.VerificationCodeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 邮箱验证控制器 - 已禁用
 * <p>
 * 处理邮箱验证码的发送和验证请求。
 * 集成邮箱验证服务进行格式和域名验证。
 * 所有接口允许跨域访问（@CrossOrigin(origins = "*")）。
 * </p>
 *
 * @author Market Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
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