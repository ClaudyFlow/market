-- 抽奖奖品表
CREATE TABLE lottery_prize (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    description VARCHAR(255),
    type INTEGER NOT NULL, -- 1=积分，2=实物
    weight INTEGER NOT NULL DEFAULT 1,
    image VARCHAR(255),
    available BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- 抽奖记录表
CREATE TABLE lottery_record (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES "user"(id) ON DELETE CASCADE,
    prize_id BIGINT NOT NULL REFERENCES lottery_prize(id) ON DELETE CASCADE,
    prize_name VARCHAR(100) NOT NULL,
    prize_type INTEGER NOT NULL,
    cost INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_lottery_record_user_id ON lottery_record(user_id);
CREATE INDEX idx_lottery_record_created_at ON lottery_record(created_at);

-- 插入奖品数据
-- 积分奖品（权重较高，容易抽中）
INSERT INTO lottery_prize (name, description, type, weight, image) VALUES
('50 积分', '奖励 50 积分', 1, 30, '/images/prize-50.png'),
('75 积分', '奖励 75 积分', 1, 20, '/images/prize-75.png'),
('100 积分', '奖励 100 积分', 1, 10, '/images/prize-100.png');

-- 实物奖品
INSERT INTO lottery_prize (name, description, type, weight, image) VALUES
('抽纸（一袋）', '家用抽纸，柔软舒适', 2, 15, '/images/prize-tissue.png'),
('洗衣液（一瓶）', '深层洁净，持久留香', 2, 15, '/images/prize-detergent.png'),
('电饭锅（一个）', '智能电饭煲，多功能烹饪', 2, 5, '/images/prize-rice-cooker.png'),
('电脑（一台）', '高性能游戏电脑', 2, 1, '/images/prize-computer.png'),
('人类下一颗类地行星命名权（遥遥无期）', '联合国并不承认', 2, 1, '/images/prize-planet.png');
