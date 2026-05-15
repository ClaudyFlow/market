package com.market.util;

import java.util.Base64;
import java.util.regex.Pattern;

/**
 * Base64 工具类
 */
public class Base64Util {

    private static final Pattern BASE64_PATTERN = Pattern.compile(
        "^data:image/(png|jpg|jpeg|gif|webp);base64,[A-Za-z0-9+/]+=*$"
    );

    /**
     * 将字节数组编码为 Base64
     */
    public static String encode(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * 将 Base64 解码为字节数组
     */
    public static byte[] decode(String base64) {
        // 移除 data:image/xxx;base64, 前缀
        String pureBase64 = removePrefix(base64);
        return Base64.getDecoder().decode(pureBase64);
    }

    /**
     * 验证 Base64 图片格式
     */
    public static boolean isValidImageBase64(String base64) {
        if (base64 == null || base64.isEmpty()) {
            return false;
        }
        return BASE64_PATTERN.matcher(base64).matches();
    }

    /**
     * 获取图片 MIME 类型
     */
    public static String getMimeType(String base64) {
        if (!base64.startsWith("data:")) {
            return "image/jpeg";
        }
        int start = base64.indexOf("data:image/") + 11;
        int end = base64.indexOf(";base64");
        if (start < 0 || end < 0) {
            return "image/jpeg";
        }
        String type = base64.substring(start, end);
        return "image/" + type;
    }

    /**
     * 获取文件扩展名
     */
    public static String getExtension(String base64) {
        String mimeType = getMimeType(base64);
        if (mimeType.contains("png")) return "png";
        if (mimeType.contains("gif")) return "gif";
        if (mimeType.contains("webp")) return "webp";
        return "jpg";
    }

    /**
     * 移除 Base64 前缀
     */
    public static String removePrefix(String base64) {
        if (base64.contains(",")) {
            return base64.split(",", 2)[1];
        }
        return base64;
    }

    /**
     * 添加 Base64 前缀
     */
    public static String addPrefix(String base64, String mimeType) {
        if (base64.startsWith("data:")) {
            return base64;
        }
        return "data:" + mimeType + ";base64," + base64;
    }

    /**
     * 计算 Base64 字符串大小（字节）
     */
    public static long getSizeInBytes(String base64) {
        String pureBase64 = removePrefix(base64);
        // Base64 编码后大小约为原始的 4/3 倍
        return (long) (pureBase64.length() * 3.0 / 4.0);
    }

    /**
     * 检查大小是否超过限制
     */
    public static boolean exceedsMaxSize(String base64, long maxSizeMB) {
        long sizeBytes = getSizeInBytes(base64);
        long maxBytes = maxSizeMB * 1024 * 1024;
        return sizeBytes > maxBytes;
    }
}
