-- 删除用户 "杜雨轩" 及其相关数据
-- 执行前请备份数据库

BEGIN;

-- 1. 删除用户收藏记录
DELETE FROM user_favorite WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 2. 删除用户地址
DELETE FROM user_address WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 3. 删除用户通知
DELETE FROM user_notification WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 4. 删除用户浏览历史
DELETE FROM user_browse_history WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 5. 删除用户关注（粉丝和关注）
DELETE FROM user_follow WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩') OR followed_user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 6. 删除用户优惠券
DELETE FROM user_coupon WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 7. 删除积分记录
DELETE FROM credit_history WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 8. 删除购物车项目
DELETE FROM cart_item WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 9. 删除聊天消息
DELETE FROM chat_message WHERE sender_id = (SELECT id FROM "user" WHERE name = '杜雨轩') OR receiver_id = (SELECT id FROM "user" WHERE name = '杜雨轩');

-- 10. 最后删除用户本身
DELETE FROM "user" WHERE name = '杜雨轩';

COMMIT;

-- 验证删除结果
-- SELECT * FROM "user" WHERE name = '杜雨轩'; -- 应返回空