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

    private final Map<String, VerificationCodeInfo> verificationCodes = new ConcurrentHashMap<>();

    @Value("${verification.code.expire.minutes:5}")
    private int codeExpireMinutes;

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

    public void storeCode(String email, String code) {
        verificationCodes.put(email, new VerificationCodeInfo(code));
        log.info("验证码已存储，邮箱: {}, 有效期: {} 分钟", email, codeExpireMinutes);
    }

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

        verificationCodes.remove(email);
        log.info("验证码验证成功，邮箱: {}", email);
        return true;
    }

    public boolean isCodeValid(String email) {
        VerificationCodeInfo info = verificationCodes.get(email);
        if (info == null) {
            return false;
        }
        return !info.isExpired(codeExpireMinutes);
    }

    public void removeCode(String email) {
        verificationCodes.remove(email);
    }

    public void cleanExpiredCodes() {
        verificationCodes.entrySet().removeIf(entry ->
            entry.getValue().isExpired(codeExpireMinutes)
        );
        log.info("过期验证码已清理");
    }

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