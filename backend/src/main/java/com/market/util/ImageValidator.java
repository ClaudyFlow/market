package com.market.util;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * 图片安全验证工具类
 */
public class ImageValidator {

    // Base64 图片格式正则
    private static final Pattern BASE64_IMAGE_PATTERN = Pattern.compile(
        "^data:image/(png|jpg|jpeg|gif|webp);base64,[A-Za-z0-9+/]+=*$"
    );

    // 恶意内容检测
    private static final String[] DANGEROUS_PATTERNS = {
        "<script",
        "</script>",
        "javascript:",
        "onerror=",
        "onload=",
        "onclick=",
        "onmouseover=",
        "<img",
        "<iframe",
        "<svg",
        "expression(",
        "url(",
        "vbscript:"
    };

    /**
     * 验证 Base64 图片格式
     */
    public static boolean isValidImageBase64(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return false;
        }
        return BASE64_IMAGE_PATTERN.matcher(base64).matches();
    }

    /**
     * 检查是否包含恶意内容
     */
    public static boolean containsMaliciousContent(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return false;
        }

        String lowerCase = base64.toLowerCase();
        for (String pattern : DANGEROUS_PATTERNS) {
            if (lowerCase.contains(pattern)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 验证图片大小
     * @param base64 Base64 字符串
     * @param maxSizeMB 最大大小（MB）
     */
    public static boolean validateSize(String base64, long maxSizeMB) {
        if (base64 == null || base64.isEmpty()) {
            return false;
        }

        try {
            long sizeBytes = Base64Util.getSizeInBytes(base64);
            long maxBytes = maxSizeMB * 1024 * 1024;
            return sizeBytes <= maxBytes;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证图片类型
     */
    public static boolean validateType(String base64, String[] allowedTypes) {
        if (base64 == null || base64.isEmpty()) {
            return false;
        }

        if (allowedTypes == null || allowedTypes.length == 0) {
            allowedTypes = new String[]{"png", "jpg", "jpeg", "gif", "webp"};
        }

        try {
            String mimeType = Base64Util.getMimeType(base64);
            for (String allowedType : allowedTypes) {
                if (mimeType.contains(allowedType)) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 完整的图片验证
     * @param base64 Base64 字符串
     * @param maxSizeMB 最大大小（MB）
     * @param allowedTypes 允许的类型
     * @return 验证结果
     */
    public static ValidationResult validate(String base64, double maxSizeMB, String[] allowedTypes) {
        ValidationResult result = new ValidationResult();

        // 检查空值
        if (base64 == null || base64.isEmpty()) {
            result.valid = false;
            result.message = "图片数据不能为空";
            return result;
        }

        // 检查 Base64 格式
        if (!isValidImageBase64(base64)) {
            result.valid = false;
            result.message = "无效的 Base64 图片格式";
            return result;
        }

        // 检查恶意内容
        if (containsMaliciousContent(base64)) {
            result.valid = false;
            result.message = "图片包含不安全内容";
            return result;
        }

        // 检查大小
        if (!validateSize(base64, (long) maxSizeMB)) {
            result.valid = false;
            result.message = "图片大小超过限制 (" + maxSizeMB + "MB)";
            return result;
        }

        // 检查类型
        if (!validateType(base64, allowedTypes)) {
            result.valid = false;
            result.message = "不支持的图片类型";
            return result;
        }

        result.valid = true;
        result.message = "验证通过";
        return result;
    }

    /**
     * 验证结果类
     */
    public static class ValidationResult {
        public boolean valid;
        public String message;

        public boolean isValid() {
            return valid;
        }

        public String getMessage() {
            return message;
        }
    }
}
