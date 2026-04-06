// 需求规格说明书 - Typst 格式

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

#v(85mm)
#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  需求规格说明书]]
#pagebreak()

#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——需求规格说明书]]
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

= 1. 引言

#h(2.0em)本文档旨在详细描述购物商城系统的功能需求和非功能需求，为开发、测试和项目管理提供依据。

#h(2.0em)本系统是一个基于 Web 的电商平台，包含用户端、商家端和管理端三个应用端。

== 1.1 目标用户
#h(2.0em)——普通用户: 浏览商品、下单购物\
#h(2.0em)——商家: 管理商品、处理订单\
#h(2.0em)——管理员: 系统管理、内容审核

= 2. 功能需求

== 2.1 用户端功能

=== 2.1.1 用户认证
#h(2.0em)——账号密码登录\
#h(2.0em)——用户注册\
#h(2.0em)——JWT Token 认证\
#h(2.0em)——密码找回/重置

=== 2.1.2 首页模块
#h(2.0em)——轮播图展示\
#h(2.0em)——分类导航\
#h(2.0em)——楼层商品展示\
#h(2.0em)——推荐店铺\
#h(2.0em)——活动信息\
#h(2.0em)——秒杀活动\
#h(2.0em)——品牌专区\
#h(2.0em)——新人专享\
#h(2.0em)——猜你喜欢

=== 2.1.3 商品模块
#h(2.0em)——商品列表浏览（10+分类）\
#h(2.0em)——商品搜索与筛选\
#h(2.0em)——综合搜索（商品/帖子/用户）\
#h(2.0em)——商品详情查看\
#h(2.0em)——商品收藏\
#h(2.0em)——浏览历史\
#h(2.0em)——看了又看/买了又买推荐

=== 2.1.4 购物车
#h(2.0em)——添加/删除商品\
#h(2.0em)——修改数量\
#h(2.0em)——选中/全选商品\
#h(2.0em)——库存检查\
#h(2.0em)——价格计算

=== 2.1.5 订单系统
#h(2.0em)——创建订单、订单支付\
#h(2.0em)——订单列表/详情\
#h(2.0em)——取消订单/确认收货\
#h(2.0em)——订单评价、退款\
#h(2.0em)——物流跟踪\
#h(2.0em)——订单修改地址/备注

=== 2.1.6 用户中心
#h(2.0em)——个人信息管理\
#h(2.0em)——收货地址管理\
#h(2.0em)——积分查询与兑换\
#h(2.0em)——签到打卡\
#h(2.0em)——VIP 等级与权益\
#h(2.0em)——通知中心\
#h(2.0em)——客服聊天

=== 2.1.7 社区论坛
#h(2.0em)——帖子列表/详情\
#h(2.0em)——发布帖子/评论\
#h(2.0em)——点赞/热门/精华\
#h(2.0em)——帖子搜索

=== 2.1.8 营销功能
#h(2.0em)——优惠券领取与使用\
#h(2.0em)——抽奖活动\
#h(2.0em)——VIP 礼包领取

== 2.2 商家端功能
#h(2.0em)——商品管理（CRUD、上下架）\
#h(2.0em)——订单处理（发货、退款）\
#h(2.0em)——数据统计（销售/订单/商品）\
#h(2.0em)——客服管理\
#h(2.0em)——评价管理\
#h(2.0em)——优惠券管理

== 2.3 管理端功能
#h(2.0em)——用户管理（封禁/解封）\
#h(2.0em)——商家管理（审核/封禁）\
#h(2.0em)——商品审核\
#h(2.0em)——订单监控\
#h(2.0em)——优惠券管理\
#h(2.0em)——数据统计（平台/订单/商品/用户/论坛）\
#h(2.0em)——系统设置

= 3. 非功能需求

== 3.1 性能要求
#h(2.0em)——页面加载时间 < 3秒\
#h(2.0em)——API 响应时间 < 500ms\
#h(2.0em)——支持 1000+ 并发用户

== 3.2 安全性
#h(2.0em)——JWT Token 认证\
#h(2.0em)——密码 BCrypt 加密\
#h(2.0em)——SQL 注入防护、XSS 防护\
#h(2.0em)——CORS 配置

== 3.3 可用性
#h(2.0em)——系统可用性 > 99%\
#h(2.0em)——数据自动备份\
#h(2.0em)——错误日志记录

= 4. 数据需求

#h(2.0em)核心数据实体包括：用户(User)、商品(Product)、订单(Order)、购物车项(CartItem)、优惠券(Coupon)、评价(Review)、通知(Notification)、论坛帖子(ForumPost)、聊天消息(ChatMessage)、VIP等级(VipLevel)、积分历史(CreditHistory)、抽奖记录(LotteryRecord)等 37 个表。

#v(2cm)
#align(center)[#text(10pt, font: "SimSun")[文档版本：v1.1.0 | 最后更新：#datetime.today().display()]]
