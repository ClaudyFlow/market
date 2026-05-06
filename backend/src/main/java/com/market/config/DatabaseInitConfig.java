package com.market.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * 数据库初始化配置（仅 dev profile）
 * 使用 JPA 自动创建表结构，此处仅插入初始数据
 */
@Profile("dev")
@Configuration
public class DatabaseInitConfig {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        // 等待 JPA 完成表创建（简单延时）
        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        jdbcTemplate = new JdbcTemplate(dataSource);
        insertInitialData();
    }

    private void insertInitialData() {
        // 插入管理员账户
        try {
            jdbcTemplate.update("""
                INSERT INTO "user" (
                    name, email, password_hash, is_merchant, 
                    credit, total_credit, consumed_credit, 
                    vip_level, growth_value, consecutive_checkin_days,
                    created_at
                ) VALUES (
                    'admin', 'admin@market.com', 
                    '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW', 
                    false, 
                    0, 0, 0, 
                    0, 0, 0,
                    CURRENT_TIMESTAMP
                )
                ON CONFLICT (name) DO NOTHING
            """);
        } catch (Exception e) {
            // 若已存在则忽略
        }

        // 插入测试商品（关联 admin 用户，假设 admin id 为 1）
        try {
            jdbcTemplate.update("""
                INSERT INTO product (
                    user_id, name, description, category, 
                    price, stock, available, 
                    created_at, updated_at, image_url
                )
                SELECT 1, '无线蓝牙耳机', '高品质无线蓝牙耳机，降噪效果好', 
                       '数码', 199.00, 100, true, 
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
                       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=耳机'
                WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '无线蓝牙耳机')
            """);
            jdbcTemplate.update("""
                INSERT INTO product (
                    user_id, name, description, category, 
                    price, stock, available, 
                    created_at, updated_at, image_url
                )
                SELECT 1, '智能手环', '运动健康监测，长续航', 
                       '数码', 149.00, 100, true, 
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
                       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=手环'
                WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '智能手环')
            """);
            jdbcTemplate.update("""
                INSERT INTO product (
                    user_id, name, description, category, 
                    price, stock, available, 
                    created_at, updated_at, image_url
                )
                SELECT 1, '机械键盘', 'Cherry 轴，RGB 背光', 
                       '数码', 329.00, 50, true, 
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, 
                       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=键盘'
                WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '机械键盘')
            """);
        } catch (Exception e) {
            // 忽略已存在的记录
        }

        // 插入店铺（admin）
        try {
            jdbcTemplate.update("""
                INSERT INTO shop (
                    owner_id, name, description, 
                    logo, banner, status, 
                    rating, followers, product_count, positive_rate,
                    created_at, updated_at
                )
                SELECT 1, 'admin的店铺', '管理员默认店铺',
                       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Shop',
                       'https://via.placeholder.com/800x200/1a2a4a/00d4ff?text=ShopBanner',
                       'ACTIVE', 
                       0.00, 0, 0, 0.00,
                       CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
                WHERE NOT EXISTS (SELECT 1 FROM shop WHERE owner_id = 1)
            """);
        } catch (Exception e) {
        }

        System.out.println("数据库初始数据插入完成");
    }
}
