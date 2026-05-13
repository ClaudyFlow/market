INSERT INTO "user" (name, email, password_hash, is_merchant, credit, total_credit, consumed_credit, vip_level, growth_value, consecutive_checkin_days, created_at) 
VALUES ('admin', 'admin@market.com', 
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW', 
        false, 0, 0, 0, 0, 0, 0, CURRENT_TIMESTAMP);