package com.market.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 数据库可用性检测器
 */
@Component
public class DatabaseAvailabilityDetector implements ApiAvailabilityDetector {

    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    @Autowired(required = false)
    private DataSource dataSource;

    @Override
    public DetectionResult detect() {
        try {
            if (jdbcTemplate != null) {
                jdbcTemplate.queryForObject("SELECT 1", Integer.class);
                return DetectionResult.success("Database connection OK");
            } else if (dataSource != null) {
                var connection = dataSource.getConnection();
                if (connection != null && !connection.isClosed()) {
                    connection.close();
                    return DetectionResult.success("DataSource connection OK");
                }
            }
            return DetectionResult.failure("No database connection available");
        } catch (Exception e) {
            return DetectionResult.failure("Database check failed: " + e.getMessage());
        }
    }
}
