INSERT INTO "user" (name, email, password_hash, is_merchant, merchant_status, role, status) 
VALUES ('admin', 'admin@market.com', 
        '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iKTm8IjtVzXmhqZz.KcXJnZbZfUW', 
        true, 'ACTIVE', 'ADMIN', 'ACTIVE')
ON CONFLICT (name) DO NOTHING;