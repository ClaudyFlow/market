// 数据库设计文档 - Typst 格式

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

#v(85mm)
#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  数据库设计文档]]
#pagebreak()

#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——数据库设计文档]]
    #place(top, dy: 30mm)[#line(length: 100%, stroke: 0.5pt)]
  ],
  footer: [
    #place(bottom, dy: -25mm)[#line(length: 100%, stroke: 0.5pt)]
    #place(bottom + left, dy: -20mm)[#text(9pt, font: "SimSun")[#datetime.today().display()]]
    #place(bottom + right, dy: -20mm)[#context text(9pt, font: "SimSun", counter(page).display())]
  ],
)

#set text(font: "SimSun", size: 10.5pt)
#set par(leading: 1.25em, first-line-indent: 2em)

= 1. 概述

== 1.1 数据库选型
#h(2.0em)——主数据库: PostgreSQL 14+\
#h(2.0em)——开发/测试: H2 内存数据库\
#h(2.0em)——缓存: Redis 7.x

== 1.2 设计原则
#h(2.0em)——遵循第三范式 (3NF)\
#h(2.0em)——合理使用索引优化查询\
#h(2.0em)——外键约束保证数据完整性

= 2. 核心数据表

== 2.1 用户表 (user)
#align(center)[#table(
  columns: (3fr, 3fr, 3fr, 6fr),
  stroke: 0.5pt,
  [字段], [类型], [约束], [说明],
  [id], [BIGINT], [PK, AUTO], [用户ID],
  [name], [VARCHAR(50)], [UNIQUE], [用户名],
  [email], [VARCHAR(100)], [UNIQUE], [邮箱],
  [password_hash], [VARCHAR(255)], [NOT NULL], [密码哈希],
  [phone], [VARCHAR(20)], [], [手机号],
  [avatar_url], [VARCHAR(255)], [], [头像URL],
  [role], [VARCHAR(20)], [DEFAULT 'USER'], [角色: USER/MERCHANT/ADMIN],
  [status], [VARCHAR(20)], [DEFAULT 'ACTIVE'], [状态],
  [is_merchant], [BOOLEAN], [DEFAULT FALSE], [是否商家],
  [shop_name], [VARCHAR(100)], [], [店铺名称],
  [vip_level], [INT], [DEFAULT 0], [VIP等级],
  [credit], [INT], [DEFAULT 0], [积分],
  [created_at], [TIMESTAMP], [NOT NULL], [创建时间],
  [updated_at], [TIMESTAMP], [], [更新时间],
)]

== 2.2 商品表 (product)
#align(center)[#table(
  columns: (3fr, 3fr, 3fr, 6fr),
  stroke: 0.5pt,
  [字段], [类型], [约束], [说明],
  [id], [BIGINT], [PK, AUTO], [商品ID],
  [name], [VARCHAR(200)], [NOT NULL], [商品名称],
  [description], [VARCHAR(1000)], [], [商品描述],
  [price], [DECIMAL(10,2)], [NOT NULL], [价格],
  [original_price], [DECIMAL(10,2)], [], [原价],
  [stock], [INT], [DEFAULT 0], [库存],
  [category], [VARCHAR(100)], [NOT NULL], [分类],
  [brand], [VARCHAR(100)], [], [品牌],
  [image_url], [VARCHAR(500)], [], [主图URL],
  [image_urls], [VARCHAR(1000)], [], [图片URLs],
  [status], [INT], [DEFAULT 1], [状态: 0下架 1上架],
  [audit_status], [INT], [DEFAULT 1], [审核: 0待审 1通过 2拒绝],
  [rating], [DOUBLE], [DEFAULT 0.0], [评分],
  [sales], [INT], [DEFAULT 0], [销量],
  [merchant_id], [BIGINT], [FK], [商家ID],
  [created_at], [TIMESTAMP], [NOT NULL], [创建时间],
  [updated_at], [TIMESTAMP], [], [更新时间],
)]

== 2.3 订单表 (order)
#align(center)[#table(
  columns: (3fr, 3fr, 3fr, 6fr),
  stroke: 0.5pt,
  [字段], [类型], [约束], [说明],
  [id], [BIGINT], [PK, AUTO], [订单ID],
  [order_no], [VARCHAR(255)], [UNIQUE], [订单号],
  [user_id], [BIGINT], [FK], [用户ID],
  [merchant_id], [BIGINT], [FK], [商家ID],
  [total_amount], [DECIMAL(10,2)], [NOT NULL], [订单总额],
  [status], [VARCHAR(50)], [NOT NULL], [状态: PENDING/PAID/SHIPPED/COMPLETED/CANCELLED],
  [shipping_address], [VARCHAR(500)], [], [收货地址],
  [tracking_no], [VARCHAR(100)], [], [物流单号],
  [payment_method], [VARCHAR(50)], [], [支付方式],
  [paid_at], [TIMESTAMP], [], [支付时间],
  [created_at], [TIMESTAMP], [NOT NULL], [创建时间],
  [updated_at], [TIMESTAMP], [], [更新时间],
)]

== 2.4 购物车表 (cart_item)
#align(center)[#table(
  columns: (3fr, 3fr, 3fr, 6fr),
  stroke: 0.5pt,
  [字段], [类型], [约束], [说明],
  [id], [BIGINT], [PK, AUTO], [购物车项ID],
  [user_id], [BIGINT], [FK], [用户ID],
  [product_id], [BIGINT], [FK], [商品ID],
  [quantity], [INT], [NOT NULL], [数量],
  [created_at], [TIMESTAMP], [NOT NULL], [创建时间],
)]

== 2.5 优惠券表 (coupon)
#align(center)[#table(
  columns: (3fr, 3fr, 3fr, 6fr),
  stroke: 0.5pt,
  [字段], [类型], [约束], [说明],
  [id], [BIGINT], [PK, AUTO], [优惠券ID],
  [name], [VARCHAR(100)], [NOT NULL], [名称],
  [type], [VARCHAR(50)], [NOT NULL], [类型: FIXED/PERCENT],
  [discount_value], [DECIMAL(10,2)], [NOT NULL], [折扣值],
  [min_purchase], [DECIMAL(10,2)], [], [最低消费],
  [total_count], [INT], [NOT NULL], [发放总量],
  [remain_count], [INT], [NOT NULL], [剩余数量],
  [valid_from], [TIMESTAMP], [], [开始时间],
  [valid_to], [TIMESTAMP], [], [结束时间],
  [status], [VARCHAR(20)], [NOT NULL], [状态: ACTIVE/INACTIVE/EXPIRED],
  [merchant_id], [BIGINT], [FK], [商家ID],
  [created_at], [TIMESTAMP], [NOT NULL], [创建时间],
  [updated_at], [TIMESTAMP], [], [更新时间],
)]

= 3. 完整表列表（37个）

#align(center)[#table(
  columns: (2fr, 4fr, 4fr),
  stroke: 0.5pt,
  [序号], [表名], [说明],
  [1], [user], [用户表],
  [2], [product], [商品表],
  [3], [order], [订单表],
  [4], [order_item], [订单项表],
  [5], [cart_item], [购物车项表],
  [6], [shop], [店铺表],
  [7], [coupon], [优惠券表],
  [8], [user_coupon], [用户优惠券表],
  [9], [review], [评价表],
  [10], [product_review], [商品评价表],
  [11], [user_address], [用户地址表],
  [12], [user_browse_history], [浏览历史表],
  [13], [user_favorite], [用户收藏表],
  [14], [user_follow], [用户关注表],
  [15], [user_notification], [用户通知表],
  [16], [credit_history], [积分历史表],
  [17], [payment], [支付记录表],
  [18], [payment_refund], [退款记录表],
  [19], [product_image], [商品图片表],
  [20], [favorite], [收藏表],
  [21], [follow], [关注表],
  [22], [chat_message], [聊天消息表],
  [23], [forum_post], [论坛帖子表],
  [24], [forum_comment], [论坛评论表],
  [25], [logistics_info], [物流信息表],
  [26], [logistics_track], [物流轨迹表],
  [27], [lottery_prize], [抽奖奖品表],
  [28], [lottery_record], [抽奖记录表],
  [29], [message_receive], [消息接收表],
  [30], [system_message], [系统消息表],
  [31], [vip_level], [VIP等级表],
  [32], [vip_gift], [VIP礼包表],
  [33], [vip_gift_record], [VIP礼包领取记录表],
  [34], [vip_recharge_order], [VIP充值订单表],
  [35], [favorite], [商品收藏表],
  [36], [follow], [店铺关注表],
  [37], [user_browse_history], [用户浏览历史表],
)]

#v(2cm)
#align(center)[#text(10pt, font: "SimSun")[文档版本：v1.1.0 | 最后更新：#datetime.today().display()]]
