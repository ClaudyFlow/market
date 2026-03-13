package com.market.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VerificationCodeService {

    private static final Logger log = LoggerFactory.getLogger(VerificationCodeService.class);

    // 存储验证码：key是邮箱，value是验证码信息
    private final Map<String, VerificationCodeInfo> verificationCodes = new ConcurrentHashMap<>();

    @Value("${verification.code.expire.minutes:5}")
    private int codeExpireMinutes;

    /**
     * 验证码信息内部类
     */
    public static class VerificationCodeInfo {
        private final String code;
        private final LocalDateTime createdAt;

        public VerificationCodeInfo(String code) {
            this.code = code;
            this.createdAt = LocalDateTime.now();
        }

        public String getCode() {
            return code;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }

        public boolean isExpired(int expireMinutes) {
            return createdAt.plusMinutes(expireMinutes).isBefore(LocalDateTime.now());
        }
    }

    /**
     * 存储验证码
     */
    public void storeCode(String email, String code) {
        verificationCodes.put(email, new VerificationCodeInfo(code));
        log.info("验证码已存储，邮箱: {}, 有效期: {} 分钟", email, codeExpireMinutes);
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String email, String code) {
        VerificationCodeInfo info = verificationCodes.get(email);

        if (info == null) {
            log.warn("验证码不存在，邮箱: {}", email);
            return false;
        }

        if (info.isExpired(codeExpireMinutes)) {
            log.warn("验证码已过期，邮箱: {}", email);
            verificationCodes.remove(email);
            return false;
        }

        if (!info.getCode().equals(code)) {
            log.warn("验证码错误，邮箱: {}", email);
            return false;
        }

        // 验证成功，删除验证码
        verificationCodes.remove(email);
        log.info("验证码验证成功，邮箱: {}", email);
        return true;
    }

    /**
     * 检查验证码是否存在且未过期
     */
    public boolean isCodeValid(String email) {
        VerificationCodeInfo info = verificationCodes.get(email);
        if (info == null) {
            return false;
        }
        return !info.isExpired(codeExpireMinutes);
    }

    /**
     * 删除验证码
     */
    public void removeCode(String email) {
        verificationCodes.remove(email);
    }

    /**
     * 清理过期验证码
     */
    public void cleanExpiredCodes() {
        verificationCodes.entrySet().removeIf(entry -> 
            entry.getValue().isExpired(codeExpireMinutes)
        );
        log.info("过期验证码已清理");
    }

    /**
     * 获取验证码剩余有效时间（秒）
     */
    public long getRemainingSeconds(String email) {
        VerificationCodeInfo info = verificationCodes.get(email);
        if (info == null) {
            return 0;
        }

        LocalDateTime expireTime = info.getCreatedAt().plusMinutes(codeExpireMinutes);
        long seconds = java.time.Duration.between(LocalDateTime.now(), expireTime).getSeconds();
        return Math.max(0, seconds);
    }
}