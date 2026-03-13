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
 * 邮箱验证控制器
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

    /**
     * 日志记录器
     */
    private static final Logger log = LoggerFactory.getLogger(EmailController.class);

    /**
     * 邮箱服务
     */
    private final EmailService emailService;

    /**
     * 验证码服务
     */
    private final VerificationCodeService verificationCodeService;

    /**
     * 邮箱验证服务
     */
    private final EmailValidatorService emailValidatorService;

    /**
     * 构造函数
     *
     * @param emailService 邮箱服务
     * @param verificationCodeService 验证码服务
     * @param emailValidatorService 邮箱验证服务
     */
    public EmailController(
            EmailService emailService,
            VerificationCodeService verificationCodeService,
            EmailValidatorService emailValidatorService) {
        this.emailService = emailService;
        this.verificationCodeService = verificationCodeService;
        this.emailValidatorService = emailValidatorService;
    }

    /**
     * 发送验证码接口
     * <p>
     * POST /api/auth/send-code
     * 接收邮箱地址，验证邮箱格式和域名，生成验证码并发送到用户邮箱。
     * </p>
     *
     * @param request 请求参数，包含email字段
     * @return 发送结果，包含success和message字段
     */
    @PostMapping("/send-code")
    public ResponseEntity<?> sendVerificationCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        if (email == null || email.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "邮箱不能为空"));
        }

        // 使用邮箱验证服务验证邮箱
        String validationError = emailValidatorService.getEmailValidationError(email);
        if (!validationError.isEmpty()) {
            log.warn("邮箱验证失败: {}, 邮箱: {}", validationError, email);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", validationError));
        }

        // 生成验证码
        String code = emailService.generateVerificationCode();

        // 发送验证码
        boolean sent = emailService.sendVerificationCode(email, code);

        if (sent) {
            // 存储验证码到共享服务
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
     * 验证验证码接口
     * <p>
     * POST /api/auth/verify-code
     * 接收邮箱地址和验证码，验证验证码是否正确且未过期。
     * </p>
     *
     * @param request 请求参数，包含email和verificationCode字段
     * @return 验证结果，包含success和message字段
     */
    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("verificationCode");

        if (email == null || code == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "邮箱和验证码不能为空"));
        }

        // 使用验证码服务验证
        boolean isValid = verificationCodeService.verifyCode(email, code);

        if (!isValid) {
            return ResponseEntity.ok(Map.of("success", false, "message", "验证码错误或已过期，请重新获取"));
        }

        log.info("验证码验证成功: {}", email);
        return ResponseEntity.ok(Map.of("success", true, "message", "验证码验证成功"));
    }
}