-- Market Platform 数据库初始化脚本
-- 用于创建新增的表结构（支付、物流相关）

-- ==================== 支付相关表 ====================

-- 支付记录表
CREATE TABLE IF NOT EXISTS payment (
    id BIGSERIAL PRIMARY KEY,
    payment_no VARCHAR(64) NOT NULL UNIQUE,
    order_no VARCHAR(64) NOT NULL,
    user_id BIGINT NOT NULL,
    payment_method VARCHAR(20) NOT NULL, -- ALIPAY, WECHAT, BANK
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(20) NOT NULL, -- PENDING, SUCCESS, FAILED, REFUNDED
    transaction_id VARCHAR(64), -- 第三方支付流水号
    callback_data TEXT, -- 回调原始数据
    paid_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_payment_payment_no ON payment(payment_no);
CREATE INDEX idx_payment_order_no ON payment(order_no);
CREATE INDEX idx_payment_user_id ON payment(user_id);
CREATE INDEX idx_payment_status ON payment(status);

-- 退款记录表
CREATE TABLE IF NOT EXISTS payment_refund (
    id BIGSERIAL PRIMARY KEY,
    refund_no VARCHAR(64) NOT NULL UNIQUE,
    payment_no VARCHAR(64) NOT NULL,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    reason VARCHAR(500),
    images TEXT, -- JSON 数组
    status VARCHAR(20) NOT NULL, -- PENDING, APPROVED, REJECTED, SUCCESS
    merchant_remark VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    refunded_at TIMESTAMP
);

CREATE INDEX idx_payment_refund_refund_no ON payment_refund(refund_no);
CREATE INDEX idx_payment_refund_payment_no ON payment_refund(payment_no);
CREATE INDEX idx_payment_refund_order_id ON payment_refund(order_id);
CREATE INDEX idx_payment_refund_user_id ON payment_refund(user_id);
CREATE INDEX idx_payment_refund_status ON payment_refund(status);

-- ==================== 物流相关表 ====================

-- 物流信息表
CREATE TABLE IF NOT EXISTS logistics_info (
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    tracking_no VARCHAR(64) NOT NULL,
    company_code VARCHAR(20) NOT NULL, -- 快递公司编码
    company_name VARCHAR(100), -- 快递公司名称
    status VARCHAR(20) NOT NULL, -- PENDING, IN_TRANSIT, DELIVERED, EXCEPTION
    estimated_delivery TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP
);

CREATE INDEX idx_logistics_info_order_id ON logistics_info(order_id);
CREATE INDEX idx_logistics_info_tracking_no ON logistics_info(tracking_no);
CREATE INDEX idx_logistics_info_status ON logistics_info(status);

-- 物流轨迹表
CREATE TABLE IF NOT EXISTS logistics_track (
    id BIGSERIAL PRIMARY KEY,
    tracking_id BIGINT NOT NULL,
    tracking_no VARCHAR(64) NOT NULL,
    time TIMESTAMP NOT NULL,
    location VARCHAR(200),
    description VARCHAR(500) NOT NULL,
    status VARCHAR(20), -- 签收、运输中、异常等
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_logistics_track_tracking_id ON logistics_track(tracking_id);
CREATE INDEX idx_logistics_track_tracking_no ON logistics_track(tracking_no);
CREATE INDEX idx_logistics_track_time ON logistics_track(time);

-- ==================== 备注 ====================
-- 1. 外键约束在应用层通过 JPA 管理，此处不添加
-- 2. H2 数据库兼容此语法，PostgreSQL 可直接执行
-- 3. 使用 IF NOT EXISTS 确保重复执行不报错
