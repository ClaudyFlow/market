package com.market.annotation;

import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 外部 API 可用性检测器
 */
@Component
public class ExternalApiAvailabilityDetector implements ApiAvailabilityDetector {

    private String apiUrl = "http://localhost:8080";

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
