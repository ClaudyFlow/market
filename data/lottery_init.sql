-- 初始化抽奖奖品数据
INSERT INTO lottery_prize (name, description, type, weight, image) VALUES
('50 积分', '奖励 50 积分', 1, 30, '/images/prize-50.png'),
('75 积分', '奖励 75 积分', 1, 20, '/images/prize-75.png'),
('100 积分', '奖励 100 积分', 1, 10, '/images/prize-100.png'),
('抽纸一大袋', '家用抽纸，柔软舒适', 2, 15, '/images/prize-tissue.png'),
('洗衣液一大袋', '深层洁净，持久留香', 2, 15, '/images/prize-detergent.png'),
('电饭锅一个', '智能电饭煲，多功能烹饪', 2, 5, '/images/prize-rice-cooker.png'),
('好电脑一台', '高性能游戏电脑', 2, 1, '/images/prize-computer.png'),
('人类下一颗类地行星命名权', '独一无二的命名权', 2, 1, '/images/prize-planet.png')
ON CONFLICT (id) DO NOTHING;
