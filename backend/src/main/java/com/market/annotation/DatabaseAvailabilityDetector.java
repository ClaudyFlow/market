package com.market.annotation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

/**
 * 数据库可用性检测器
 * 
 * 实现 ApiAvailabilityDetector 接口，用于检测数据库连接的可用性
 * 通过执行简单的 SQL 查询（SELECT 1）或检查 DataSource 连接状态来判断数据库是否可用
 * 
 * 使用场景：
 * - API 健康检查时检测数据库依赖
 * - 系统监控和告警
 * - 数据库故障自动发现和恢复检测
 * 
 * 检测逻辑：
 * 1. 优先使用 JdbcTemplate 执行 SELECT 1 查询
 * 2. 若 JdbcTemplate 不可用，则检查 DataSource 连接状态
 * 3. 返回检测结果（成功/失败及原因）
 *
 * @author market-team
 * @since 1.0
 */
@Component
public class DatabaseAvailabilityDetector implements ApiAvailabilityDetector {

    /**
     * JDBC 模板，用于执行数据库查询
     * 标记为 required = false，允许在数据库未配置时正常启动
     */
    @Autowired(required = false)
    private JdbcTemplate jdbcTemplate;

    /**
     * 数据源，用于获取数据库连接
     * 标记为 required = false，作为 JdbcTemplate 的备选方案
     */
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
