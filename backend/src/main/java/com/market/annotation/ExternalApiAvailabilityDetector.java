package com.market.annotation;

import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 外部 API 可用性检测器
 * 
 * 实现 ApiAvailabilityDetector 接口，用于检测外部 HTTP API 服务的可用性
 * 通过发送 HTTP GET 请求并检查响应状态码来判断外部服务是否可用
 * 
 * 使用场景：
 * - 检测第三方 API 服务是否正常运行
 * - 微服务架构中检测依赖服务的可用性
 * - API 网关健康检查
 * 
 * 检测逻辑：
 * 1. 向配置的 apiUrl 发送 HTTP GET 请求
 * 2. 设置连接超时和读取超时各 3 秒
 * 3. 检查响应状态码，200 表示可用，其他状态码表示异常
 * 4. 返回检测结果（成功/失败及原因）
 *
 * @author market-team
 * @since 1.0
 */
@Component
public class ExternalApiAvailabilityDetector implements ApiAvailabilityDetector {

    /**
     * 外部 API 地址
     * 默认指向本地 8080 端口，可通过 setApiUrl 方法修改
     */
    private String apiUrl = "http://localhost:8080";

    /**
     * 设置外部 API 地址
     * 
     * @param apiUrl 完整的 API URL 地址
     */
    public void setApiUrl(String apiUrl) {
        this.apiUrl = apiUrl;
    }

    @Override
    public DetectionResult detect() {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);

            int responseCode = connection.getResponseCode();
            connection.disconnect();

            if (responseCode == 200) {
                return DetectionResult.success("External API is available");
            } else {
                return DetectionResult.failure("External API returned status: " + responseCode);
            }
        } catch (Exception e) {
            return DetectionResult.failure("External API check failed: " + e.getMessage());
        }
    }
}
