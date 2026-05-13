// 版本发布说明 - Typst 格式

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

#v(85mm)
#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  版本发布说明]]
#pagebreak()

#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——版本发布说明]]
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

= 1. 当前版本

== v1.0.0 (2026-04-06)

=== 新增功能
#h(2.0em)——用户认证系统（登录/注册/JWT）\
#h(2.0em)——商品浏览与搜索（10+分类）\
#h(2.0em)——购物车完整功能（选中/全选/库存检查）\
#h(2.0em)——订单完整流程（创建/支付/取消/评价）\
#h(2.0em)——优惠券系统（领取/使用/管理）\
#h(2.0em)——用户中心（个人信息/地址/收藏/关注）\
#h(2.0em)——积分系统（签到/查询/兑换）\
#h(2.0em)——VIP会员系统\
#h(2.0em)——论坛社区（发帖/评论/点赞）\
#h(2.0em)——在线客服（WebSocket实时聊天）\
#h(2.0em)——通知中心（系统/订单/活动通知）\
#h(2.0em)——幸运抽奖\
#h(2.0em)——购物评分反馈

=== 技术架构
#h(2.0em)——前端: Vue 3 + TypeScript + Element Plus + Vite\
#h(2.0em)——后端: Spring Boot 3.4.0 + JPA + Spring Security + Redis\
#h(2.0em)——数据库: PostgreSQL 14+ / H2 (开发)\
#h(2.0em)——三端分离: 用户端/商家端/管理端

=== 已知问题
#h(2.0em)——cart.ts 有6处路径不匹配（待修复）\
#h(2.0em)——部分 vendor chunk 超过 500KB（性能优化）\
#h(2.0em)——通知中心页面UI待完善

= 2. 版本规划

== v1.1.0 (规划中)
#h(2.0em)——修复 cart.ts 路径问题\
#h(2.0em)——完善通知中心UI\
#h(2.0em)——WebSocket实时通知推送\
#h(2.0em)——订单修改地址/备注联调测试\
#h(2.0em)——大chunk分割优化\
#h(2.0em)预计发布日期: 2026-04-10

== v1.2.0 (规划中)
#h(2.0em)——移动端App优化\
#h(2.0em)——社交分享功能\
#h(2.0em)——推荐算法优化\
#h(2.0em)——多语言支持

== v2.0.0 (远期规划)
#h(2.0em)——微服务架构改造\
#h(2.0em)——AI智能推荐\
#h(2.0em)——多商户支持\
#h(2.0em)——区块链溯源

= 3. 版本统计

#align(center)[#table(
  columns: (2fr, 3fr, 2fr, 2fr, 2fr, 2fr),
  stroke: 0.5pt,
  [版本], [发布日期], [新功能], [Bug修复], [优化项], [状态],
  [v1.0.0], [2026-04-06], [13], [0], [0], [已发布],
  [v1.1.0], [规划中], [5], [-], [-], [规划中],
  [v1.2.0], [规划中], [4], [-], [-], [规划中],
  [v2.0.0], [规划中], [4], [-], [-], [远期],
)]

= 4. 升级指南

== 从 v1.0.0 升级到 v1.1.0
#h(2.0em)1. 备份数据: pg_dump -U market market > backup_v1.0.0.sql\
#h(2.0em)2. 更新代码: git pull origin v1.1.0\
#h(2.0em)3. 更新依赖: mvnd clean install && npm install\
#h(2.0em)4. 数据库迁移（如有）\
#h(2.0em)5. 重启服务\
#h(2.0em)6. 验证: 运行测试用例、检查核心功能

= 5. 变更日志格式

#h(2.0em)每个版本的变更日志按以下格式记录：\
#h(2.0em)——[版本号] - [日期]\
#h(2.0em)——新增: [功能描述]\
#h(2.0em)——修复: [Bug描述]\
#h(2.0em)——优化: [优化描述]\
#h(2.0em)——变更: [变更描述]\
#h(2.0em)——废弃: [废弃描述]

#v(2cm)
#align(center)[#text(10pt, font: "SimSun")[文档版本：v1.0.0 | 最后更新：#datetime.today().display()]]
