package com.market.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 数据库初始化配置
 * 在应用启动时自动创建表结构和初始数据
 */
@Profile("dev") // 只在 dev  profile 下启用
@Configuration
public class DatabaseInitConfig {

    @Autowired
    private DataSource dataSource;

    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    public void init() {
        jdbcTemplate = new JdbcTemplate(dataSource);
        createTables();
        createIndexes();
        insertInitialData();
    }

    /**
     * 创建表结构
     */
    private void createTables() {
        // 用户表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS "user" (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(50) NOT NULL UNIQUE,
                email VARCHAR(100) UNIQUE,
                phone VARCHAR(20),
                password_hash VARCHAR(255) NOT NULL,
                avatar_url VARCHAR(255),
                credit INTEGER NOT NULL DEFAULT 0,
                total_credit INTEGER NOT NULL DEFAULT 0,
                consumed_credit INTEGER NOT NULL DEFAULT 0,
                vip_level INTEGER NOT NULL DEFAULT 0,
                vip_expire_time TIMESTAMP,
                growth_value INTEGER NOT NULL DEFAULT 0,
                consecutive_checkin_days INTEGER NOT NULL DEFAULT 0,
                last_checkin_time TIMESTAMP,
                is_merchant BOOLEAN NOT NULL DEFAULT FALSE,
                shop_name VARCHAR(100),
                shop_description VARCHAR(500),
                merchant_status VARCHAR(20) DEFAULT 'INACTIVE',
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 商品表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS product (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT REFERENCES "user"(id),
                name VARCHAR(200) NOT NULL,
                description TEXT,
                image VARCHAR(500),
                images VARCHAR(500),
                category VARCHAR(50),
                price DECIMAL(10, 2) NOT NULL,
                stock INTEGER NOT NULL DEFAULT 0,
                sales INTEGER NOT NULL DEFAULT 0,
                status VARCHAR(20) NOT NULL DEFAULT 'ONSALE',
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 订单表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS "order" (
                id BIGSERIAL PRIMARY KEY,
                order_no VARCHAR(50) NOT NULL UNIQUE,
                user_id BIGINT NOT NULL REFERENCES "user"(id),
                merchant_id BIGINT REFERENCES "user"(id),
                total_amount DECIMAL(10, 2) NOT NULL,
                status VARCHAR(50) NOT NULL DEFAULT 'PENDING',
                shipping_address VARCHAR(500),
                payment_method VARCHAR(50),
                tracking_no VARCHAR(100),
                carrier VARCHAR(50),
                refund_reason VARCHAR(500),
                refund_images TEXT,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                paid_at TIMESTAMP,
                shipped_at TIMESTAMP,
                completed_at TIMESTAMP,
                cancelled_at TIMESTAMP
            )
        """);

        // 订单项表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS order_item (
                id BIGSERIAL PRIMARY KEY,
                order_id BIGINT NOT NULL REFERENCES "order"(id) ON DELETE CASCADE,
                product_id BIGINT NOT NULL REFERENCES product(id),
                quantity INTEGER NOT NULL,
                price DECIMAL(10, 2) NOT NULL
            )
        """);

        // 购物车项表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS cart_item (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                product_id BIGINT NOT NULL REFERENCES product(id),
                quantity INTEGER NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(user_id, product_id)
            )
        """);

        // 收藏夹表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS favorite (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                product_id BIGINT NOT NULL REFERENCES product(id),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(user_id, product_id)
            )
        """);

        // 关注表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS follow (
                id BIGSERIAL PRIMARY KEY,
                follower_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                followee_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                UNIQUE(follower_id, followee_id)
            )
        """);

        // 评价表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS review (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id),
                product_id BIGINT NOT NULL REFERENCES product(id),
                order_id BIGINT REFERENCES "order"(id),
                score INTEGER NOT NULL CHECK (score >= 1 AND score <= 5),
                content TEXT,
                images VARCHAR(500),
                reply TEXT,
                reply_time TIMESTAMP,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 优惠券表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS coupon (
                id BIGSERIAL PRIMARY KEY,
                merchant_id BIGINT REFERENCES "user"(id),
                name VARCHAR(100) NOT NULL,
                type VARCHAR(20) NOT NULL,
                value DECIMAL(10, 2) NOT NULL,
                threshold DECIMAL(10, 2),
                total INTEGER NOT NULL,
                used INTEGER NOT NULL DEFAULT 0,
                start_time TIMESTAMP NOT NULL,
                end_time TIMESTAMP NOT NULL,
                status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 用户优惠券表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS user_coupon (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id),
                coupon_id BIGINT NOT NULL REFERENCES coupon(id),
                status VARCHAR(20) NOT NULL DEFAULT 'UNUSED',
                obtained_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                used_at TIMESTAMP
            )
        """);

        // 积分历史表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS credit_history (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id),
                amount INTEGER NOT NULL,
                type VARCHAR(20) NOT NULL,
                description VARCHAR(500),
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 抽奖奖品表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS lottery_prize (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                type VARCHAR(20) NOT NULL,
                value DECIMAL(10, 2),
                cost INTEGER NOT NULL DEFAULT 10,
                probability DECIMAL(5, 4) NOT NULL,
                total INTEGER NOT NULL,
                remaining INTEGER NOT NULL,
                image VARCHAR(500),
                status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'
            )
        """);

        // 抽奖记录表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS lottery_record (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id),
                prize_id BIGINT REFERENCES lottery_prize(id),
                prize_name VARCHAR(100),
                prize_type VARCHAR(20),
                cost INTEGER NOT NULL,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 聊天消息表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS chat_message (
                id BIGSERIAL PRIMARY KEY,
                sender_id BIGINT NOT NULL REFERENCES "user"(id),
                receiver_id BIGINT NOT NULL REFERENCES "user"(id),
                content TEXT NOT NULL,
                is_read BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 地址表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS user_address (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
                name VARCHAR(50) NOT NULL,
                phone VARCHAR(20) NOT NULL,
                province VARCHAR(50),
                city VARCHAR(50),
                district VARCHAR(50),
                detail VARCHAR(200) NOT NULL,
                is_default BOOLEAN NOT NULL DEFAULT FALSE,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // 公告表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS announcement (
                id BIGSERIAL PRIMARY KEY,
                title VARCHAR(200) NOT NULL,
                content TEXT NOT NULL,
                type VARCHAR(20) NOT NULL,
                priority INTEGER NOT NULL DEFAULT 0,
                is_published BOOLEAN NOT NULL DEFAULT FALSE,
                published_at TIMESTAMP,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // VIP 等级表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS vip_level (
                id BIGSERIAL PRIMARY KEY,
                level INTEGER NOT NULL UNIQUE,
                name VARCHAR(50) NOT NULL,
                icon VARCHAR(500),
                growth_value_required INTEGER NOT NULL DEFAULT 0,
                discount_rate DECIMAL(3, 2) NOT NULL DEFAULT 1.00,
                daily_credit INTEGER NOT NULL DEFAULT 0,
                monthly_credit INTEGER NOT NULL DEFAULT 0,
                free_shipping_count INTEGER NOT NULL DEFAULT 0,
                refund_priority BOOLEAN NOT NULL DEFAULT FALSE,
                exclusive_service BOOLEAN NOT NULL DEFAULT FALSE,
                description VARCHAR(500),
                privileges TEXT,
                background_color VARCHAR(20),
                text_color VARCHAR(20)
            )
        """);

        // VIP 礼包表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS vip_gift (
                id BIGSERIAL PRIMARY KEY,
                name VARCHAR(100) NOT NULL,
                type VARCHAR(20) NOT NULL,
                vip_level_required INTEGER NOT NULL DEFAULT 0,
                credit_reward INTEGER NOT NULL DEFAULT 0,
                coupon_ids VARCHAR(500),
                product_ids VARCHAR(500),
                claim_type VARCHAR(20) NOT NULL,
                claim_interval_hours INTEGER NOT NULL DEFAULT 24,
                description VARCHAR(500),
                image VARCHAR(500),
                status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);

        // VIP 礼包领取记录表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS vip_gift_record (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id),
                gift_id BIGINT NOT NULL REFERENCES vip_gift(id),
                claimed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                reward_type VARCHAR(20),
                reward_value VARCHAR(500)
            )
        """);

        // VIP 充值订单表
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS vip_recharge_order (
                id BIGSERIAL PRIMARY KEY,
                user_id BIGINT NOT NULL REFERENCES "user"(id),
                order_no VARCHAR(50) NOT NULL UNIQUE,
                amount DECIMAL(10, 2) NOT NULL,
                growth_value INTEGER NOT NULL,
                status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
                payment_method VARCHAR(50),
                paid_at TIMESTAMP,
                created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
            )
        """);
    }

    /**
     * 创建索引
     */
    private void createIndexes() {
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_order_user ON \"order\"(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_order_merchant ON \"order\"(merchant_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_order_status ON \"order\"(status)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_order_created_at ON \"order\"(created_at DESC)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_order_item_order ON order_item(order_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_order_item_product ON order_item(product_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_product_user ON product(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_product_category ON product(category)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_product_status ON product(status)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_cart_item_user ON cart_item(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_favorite_user ON favorite(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_follow_follower ON follow(follower_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_follow_followee ON follow(followee_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_review_product ON review(product_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_review_user ON review(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_chat_message_sender ON chat_message(sender_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_chat_message_receiver ON chat_message(receiver_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_address_user ON user_address(user_id)");
        
        // VIP 相关索引
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vip_level ON vip_level(level)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vip_gift_type ON vip_gift(type)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vip_gift_record_user ON vip_gift_record(user_id)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vip_recharge_user ON vip_recharge_order(user_id)");
    }

    /**
     * 插入初始数据
     */
    private void insertInitialData() {
        // 插入管理员账户（密码：admin123）
        try {
            jdbcTemplate.update("""
                INSERT INTO "user" (name, email, password_hash, is_merchant, merchant_status) 
                VALUES ('admin', 'admin@market.com', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW', true, 'ACTIVE')
                ON CONFLICT (name) DO NOTHING
            """);
        } catch (Exception e) {
            // 忽略已存在的记录
        }

        // 插入测试商品
        try {
            jdbcTemplate.update("""
                INSERT INTO product (user_id, name, description, image, category, price, stock, sales, status)
                SELECT 1, '无线蓝牙耳机', '高品质无线蓝牙耳机，降噪效果好', 
                       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=耳机', 
                       '数码', 199.00, 100, 0, 'ONSALE'
                WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '无线蓝牙耳机')
            """);

            jdbcTemplate.update("""
                INSERT INTO product (user_id, name, description, image, category, price, stock, sales, status)
                SELECT 1, '智能手环', '运动健康监测，长续航', 
                       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=手环', 
                       '数码', 149.00, 100, 0, 'ONSALE'
                WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '智能手环')
            """);

            jdbcTemplate.update("""
                INSERT INTO product (user_id, name, description, image, category, price, stock, sales, status)
                SELECT 1, '机械键盘', 'Cherry 轴，RGB 背光', 
                       'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=键盘', 
                       '数码', 329.00, 50, 0, 'ONSALE'
                WHERE NOT EXISTS (SELECT 1 FROM product WHERE name = '机械键盘')
            """);
        } catch (Exception e) {
            // 忽略已存在的记录
        }

        // 插入抽奖奖品
        try {
            jdbcTemplate.update("""
                INSERT INTO lottery_prize (name, type, value, cost, probability, total, remaining, image, status)
                VALUES 
                    ('一等奖：iPhone 15', 'physical', 7999.00, 100, 0.001, 1, 1, 
                     'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=iPhone', 'ACTIVE'),
                    ('二等奖：AirPods Pro', 'physical', 1999.00, 50, 0.01, 5, 5, 
                     'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=AirPods', 'ACTIVE'),
                    ('三等奖：100 元优惠券', 'coupon', 100.00, 20, 0.1, 100, 100, 
                     'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=优惠券', 'ACTIVE'),
                    ('四等奖：50 积分', 'credit', 50.00, 10, 0.3, 500, 500, 
                     'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=积分', 'ACTIVE'),
                    ('谢谢参与', 'none', 0.00, 10, 0.589, 1000, 1000, 
                     'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=谢谢', 'ACTIVE')
                ON CONFLICT DO NOTHING
            """);
        } catch (Exception e) {
            // 忽略已存在的记录
        }

        // 插入 VIP 等级数据
        try {
            jdbcTemplate.execute("""
                INSERT INTO vip_level (level, name, growth_value_required, discount_rate, daily_credit, 
                    monthly_credit, free_shipping_count, refund_priority, exclusive_service, description, 
                    background_color, text_color)
                VALUES 
                    (0, '普通会员', 0, 1.00, 0, 0, 0, false, false, 
                     '基础会员，享受普通服务', '#888888', '#ffffff'),
                    (1, '白银会员', 1000, 0.98, 10, 100, 1, false, false, 
                     '白银等级，享受 98 折优惠', '#cccccc', '#ffffff'),
                    (2, '黄金会员', 5000, 0.95, 20, 300, 3, false, false, 
                     '黄金等级，享受 95 折优惠', '#ffd700', '#000000'),
                    (3, '铂金会员', 20000, 0.90, 50, 500, 5, true, false, 
                     '铂金等级，享受 90 折优惠', '#00d4ff', '#000000'),
                    (4, '钻石会员', 100000, 0.85, 100, 1000, 10, true, true, 
                     '钻石等级，享受 85 折优惠', '#9932cc', '#ffffff'),
                    (5, '至尊会员', 500000, 0.80, 200, 2000, 999, true, true, 
                     '至尊等级，享受 80 折优惠', '#ff4444', '#ffffff')
                ON CONFLICT (level) DO NOTHING
            """);
        } catch (Exception e) {
            // 忽略已存在的记录
        }

        // 插入 VIP 礼包数据
        try {
            jdbcTemplate.execute("""
                INSERT INTO vip_gift (name, type, vip_level_required, credit_reward, claim_type, claim_interval_hours, description, image, status)
                VALUES 
                    ('每日签到礼包', 'DAILY', 0, 10, 'DAILY', 24, '每日签到领取积分奖励', 
                     'https://via.placeholder.com/200x200/00d4ff/ffffff?text=每日', 'ACTIVE'),
                    ('白银每日礼包', 'DAILY', 1, 20, 'DAILY', 24, '白银会员每日专属礼包', 
                     'https://via.placeholder.com/200x200/cccccc/ffffff?text=白银每日', 'ACTIVE'),
                    ('黄金每日礼包', 'DAILY', 2, 50, 'DAILY', 24, '黄金会员每日专属礼包', 
                     'https://via.placeholder.com/200x200/ffd700/000000?text=黄金每日', 'ACTIVE'),
                    ('铂金每日礼包', 'DAILY', 3, 100, 'DAILY', 24, '铂金会员每日专属礼包', 
                     'https://via.placeholder.com/200x200/00d4ff/ffffff?text=铂金每日', 'ACTIVE'),
                    ('钻石每日礼包', 'DAILY', 4, 200, 'DAILY', 24, '钻石会员每日专属礼包', 
                     'https://via.placeholder.com/200x200/9932cc/ffffff?text=钻石每日', 'ACTIVE'),
                    ('至尊每日礼包', 'DAILY', 5, 500, 'DAILY', 24, '至尊会员每日专属礼包', 
                     'https://via.placeholder.com/200x200/ff4444/ffffff?text=至尊每日', 'ACTIVE'),
                    ('白银每月礼包', 'MONTHLY', 1, 200, 'MONTHLY', 720, '白银会员每月专属礼包', 
                     'https://via.placeholder.com/200x200/cccccc/ffffff?text=白银每月', 'ACTIVE'),
                    ('黄金每月礼包', 'MONTHLY', 2, 500, 'MONTHLY', 720, '黄金会员每月专属礼包', 
                     'https://via.placeholder.com/200x200/ffd700/000000?text=黄金每月', 'ACTIVE'),
                    ('铂金每月礼包', 'MONTHLY', 3, 1000, 'MONTHLY', 720, '铂金会员每月专属礼包', 
                     'https://via.placeholder.com/200x200/00d4ff/ffffff?text=铂金每月', 'ACTIVE'),
                    ('钻石每月礼包', 'MONTHLY', 4, 2000, 'MONTHLY', 720, '钻石会员每月专属礼包', 
                     'https://via.placeholder.com/200x200/9932cc/ffffff?text=钻石每月', 'ACTIVE'),
                    ('至尊每月礼包', 'MONTHLY', 5, 5000, 'MONTHLY', 720, '至尊会员每月专属礼包', 
                     'https://via.placeholder.com/200x200/ff4444/ffffff?text=至尊每月', 'ACTIVE')
                ON CONFLICT DO NOTHING
            """);
        } catch (Exception e) {
            // 忽略已存在的记录
        }
    }
}
