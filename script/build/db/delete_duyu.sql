BEGIN;

DELETE FROM cart_item WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');
DELETE FROM credit_history WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');
DELETE FROM user_address WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');
DELETE FROM user_browse_history WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');
DELETE FROM user_coupon WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');
DELETE FROM user_favorite WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');
DELETE FROM user_follow WHERE follower_id = (SELECT id FROM "user" WHERE name = '杜雨轩') OR following_id = (SELECT id FROM "user" WHERE name = '杜雨轩');
DELETE FROM user_notification WHERE user_id = (SELECT id FROM "user" WHERE name = '杜雨轩');
DELETE FROM "user" WHERE name = '杜雨轩';

COMMIT;