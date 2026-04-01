package com.market.annotation;

/**
 * API 可用性检测器接口
 * 
 * 用于自定义 API 可用性检测逻辑
 * 实现此接口可以定义特定的健康检查规则
 */
public interface ApiAvailabilityDetector {

    /**
     * 执行可用性检测
     * 
     * @return 检测结果
     */
    DetectionResult detect();

    /**
     * 检测结果类
     */
    class DetectionResult {
        
        /**
         * 是否可用
         */
        private final boolean available;
        
        /**
         * 消息
         */
        private final String message;
        
        /**
         * 详细信息
         */
        private final Object details;

        public DetectionResult(boolean available) {
            this(available, null, null);
        }

        public DetectionResult(boolean available, String message) {
            this(available, message, null);
        }

        public DetectionResult(boolean available, String message, Object details) {
            this.available = available;
            this.message = message;
            this.details = details;
        }

        public static DetectionResult success() {
            return new DetectionResult(true, "OK");
        }

        public static DetectionResult success(String message) {
            return new DetectionResult(true, message);
        }

        public static DetectionResult failure() {
            return new DetectionResult(false, "Service unavailable");
        }

        public static DetectionResult failure(String message) {
            return new DetectionResult(false, message);
        }

        public boolean isAvailable() {
            return available;
        }

        public String getMessage() {
            return message;
        }

        public Object getDetails() {
            return details;
        }
    }
}
