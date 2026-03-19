// 第1页开始

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
  header: [],
  footer: [],
)

// 封面标题：标题前间距15mm，标题后间距10mm，水平居中

#v(85mm)

#align(center)[#text(26pt, font: "SimHei")[数据库结构文档]]

// 第1页结束
#pagebreak()
// 第2页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])
#v(1fr)


// 第2页结束
#pagebreak()
// 第3页开始

// 设置罗马数字页码和页眉页脚
#set page(
  numbering: "I",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——数据库结构文档]]
    #place(top, dy: 30mm)[#line(length: 100%, stroke: 0.5pt)]
  ],
  footer: [
    #place(bottom, dy: -25mm)[#line(length: 100%, stroke: 0.5pt)]
    #place(bottom + left, dy: -20mm)[#text(9pt, font: "SimSun")[#datetime.today().display()]]
    #context {
      place(bottom + right, dy: -20mm)[#align(right)[#text(9pt, font: "SimSun")[#counter(page).display()]]]
    }
  ],
)
#counter(page).update(1)

// 目录标题：从页眉线向下距离15mm，水平居中
#v(15mm)

#align(top + center)[#text(16pt, font: "SimHei")[目录]]

#v(10mm)

#set outline(
  title: none,
  depth: 2,
  indent: 1em,
)

// 设置目录内容为五号宋体，行距为1.5倍
#show outline.entry: it => {
  set text(font: "SimSun", size: 10.5pt)
  set block(spacing: 1.5em)
  it
}

#outline()

// 第3页结束
#pagebreak()
// 第4页开始

// 设置页眉页脚
#set page(
  numbering: "I",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——数据库结构文档]]
    #place(top, dy: 30mm)[#line(length: 100%, stroke: 0.5pt)]
  ],
  footer: [
    #place(bottom, dy: -25mm)[#line(length: 100%, stroke: 0.5pt)]
    #place(bottom + left, dy: -20mm)[#text(9pt, font: "SimSun")[#datetime.today().display()]]
    #context {
      place(bottom + right, dy: -20mm)[#align(right)[#text(9pt, font: "SimSun")[#counter(page).display()]]]
    }
  ],
)

// 前言标题：标题前间距15mm，标题后间距10mm，三号黑体居中对齐

#v(15mm)

#align(center)[#text(16pt, font: "SimHei")[数据库设计概述]]

// 前言内容：五号宋体左对齐，首行缩进两个全角空格，行距为1.25倍
#set text(font: "SimSun", size: 10.5pt)
#set par(justify: true, first-line-indent: 2em)
#set block(spacing: 1.25em)

#h(2.0em)本文档详细描述了购物商城系统数据库的结构设计，包括表结构、关系图、索引策略、数据完整性约束、性能优化和安全设计等内容。\

#h(2.0em)数据库设计遵循规范化设计原则，支持完整的电商业务流程，保证数据一致性和完整性，优化查询性能，易于扩展和维护，支持高并发访问。\

// 第4页结束
#pagebreak()
// 第5页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])

// 第5页结束
#pagebreak()
// 第6页开始

// 切换到阿拉伯数字页码，设置页眉页脚
#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——数据库结构文档]]
    #place(top, dy: 30mm)[#line(length: 100%, stroke: 0.5pt)]
  ],
  footer: [
    #place(bottom, dy: -25mm)[#line(length: 100%, stroke: 0.5pt)]
    #place(bottom + left, dy: -20mm)[#text(9pt, font: "SimSun")[#datetime.today().display()]]
    #context {
      place(bottom + right, dy: -20mm)[#align(right)[#text(9pt, font: "SimSun")[#counter(page).display()]]]
    }
  ],
)
#counter(page).update(1)

// 正文大标题：标题前间距15mm，标题后间距10mm，三号黑体居中对齐
#v(15mm)
#align(center)[#text(16pt, font: "SimHei")[数据库结构文档]]
#v(10mm)

// 正文内容：五号宋体左对齐，首行缩进两个全角空格，正文部分的标题是五号黑体，行距为1.25倍
// 设置标题编号与标题之间有一个全角空格
#set heading(numbering: (..nums) => {
  let num = numbering("1.1", ..nums)
  [#text(10.5pt, font: "SimHei")[#h(1em)#num#h(1em)]]
})

#set text(font: "SimSun", size: 10.5pt)
#set par(justify: true, first-line-indent: 2em)
#set block(spacing: 1.25em)

// 正文一级标题：三号黑体，段前段后间距10mm
#show heading.where(level: 1): it => {
  set block(above: 10mm, below: 10mm)
  set text(font: "SimHei", size: 16pt)
  it
}

// 正文二级标题：四号黑体，段前段后间距5mm
#show heading.where(level: 2): it => {
  set block(above: 5mm, below: 5mm)
  set text(font: "SimHei", size: 14pt)
  it
}

// 正文三级标题：小四号黑体，段前段后间距3mm
#show heading.where(level: 3): it => {
  set block(above: 3mm, below: 3mm)
  set text(font: "SimHei", size: 12pt)
  it
}

= 数据库设计概述

== 设计目标

#h(2.0em)支持完整的电商业务流程\
#h(2.0em)保证数据一致性和完整性\
#h(2.0em)优化查询性能\
#h(2.0em)易于扩展和维护\
#h(2.0em)支持高并发访问\

== 设计原则

#h(2.0em)#text(font: "SimHei")[规范化设计]：遵循第三范式，减少数据冗余\
#h(2.0em)#text(font: "SimHei")[性能优先]：合理使用索引，优化查询效率\
#h(2.0em)#text(font: "SimHei")[扩展性]：支持业务功能扩展\
#h(2.0em)#text(font: "SimHei")[安全性]：敏感数据加密存储\
#h(2.0em)#text(font: "SimHei")[可维护性]：清晰的表结构和关系\

== 技术选型

#h(2.0em)#text(font: "SimHei")[数据库系统]：#text(font: "Times New Roman")[SQLite 3.45+]\
#h(2.0em)#text(font: "SimHei")[文件位置]：#text(font: "Times New Roman")[data/market.db]\
#h(2.0em)#text(font: "SimHei")[ORM 框架]：#text(font: "Times New Roman")[Spring Data JPA]\
#h(2.0em)#text(font: "SimHei")[Hibernate 方言]：#text(font: "Times New Roman")[SQLite Dialect]\

= 数据库表结构

== 用户表

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)1-#h(1.0em)用户表(#text(font: "Times New Roman")[user])结构]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 3fr),
  stroke: 0.5pt,
  align: (left, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[字段名]]], [#align(center)[#text(9pt, font: "SimSun")[数据类型]]], [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#text(9pt, font: "Times New Roman")[id]], [#text(9pt, font: "Times New Roman")[INTEGER]], [#text(9pt, font: "SimSun")[用户ID，主键，自增]],
  [#text(9pt, font: "Times New Roman")[name]], [#text(9pt, font: "Times New Roman")[VARCHAR(50)]], [#text(9pt, font: "SimSun")[用户名，唯一]],
  [#text(9pt, font: "Times New Roman")[email]], [#text(9pt, font: "Times New Roman")[VARCHAR(100)]], [#text(9pt, font: "SimSun")[邮箱地址，唯一]],
  [#text(9pt, font: "Times New Roman")[password_hash]], [#text(9pt, font: "Times New Roman")[VARCHAR(255)]], [#text(9pt, font: "SimSun")[密码哈希值(#text(font: "Times New Roman")[BCrypt]加密)]],
  [#text(9pt, font: "Times New Roman")[avatar_url]], [#text(9pt, font: "Times New Roman")[VARCHAR(255)]], [#text(9pt, font: "SimSun")[头像URL]],
  [#text(9pt, font: "Times New Roman")[points]], [#text(9pt, font: "Times New Roman")[INTEGER]], [#text(9pt, font: "SimSun")[当前积分，默认0]],
  [#text(9pt, font: "Times New Roman")[total_points]], [#text(9pt, font: "Times New Roman")[INTEGER]], [#text(9pt, font: "SimSun")[累计积分，默认0]],
  [#text(9pt, font: "Times New Roman")[created_at]], [#text(9pt, font: "Times New Roman")[TIMESTAMP]], [#text(9pt, font: "SimSun")[创建时间]],
  [#text(9pt, font: "Times New Roman")[updated_at]], [#text(9pt, font: "Times New Roman")[TIMESTAMP]], [#text(9pt, font: "SimSun")[更新时间]],
)]

#h(2.0em)#text(font: "SimHei")[注：]#text(font: "SimSun")[存储用户基本信息和积分数据，支持#text(font: "Times New Roman")[Spring#h(0.25em)Security]认证。]\

#h(2.0em)#text(font: "SimHei")[索引策略]：\

#h(2.0em)主键索引：#text(font: "Times New Roman")[id]\
#h(2.0em)唯一索引：#text(font: "Times New Roman")[name], #text(font: "Times New Roman")[email]\

== 商品表

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)2-#h(1.0em)商品表(#text(font: "Times New Roman")[product])结构]]

#v(0.5em)

#align(center)[#text(10.5pt, font: "SimSun")[#h(0.5em)注：此表在当前版本中尚未实现，为规划中的功能。]]

== 购物车表

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)3-#h(1.0em)购物车表(#text(font: "Times New Roman")[cart_item])结构]]

#v(0.5em)

#align(center)[#text(10.5pt, font: "SimSun")[#h(0.5em)注：此表在当前版本中尚未实现，为规划中的功能。]]

== 订单表

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)4-#h(1.0em)订单表(#text(font: "Times New Roman")[order])结构]]

#v(0.5em)

#align(center)[#text(10.5pt, font: "SimSun")[#h(0.5em)注：此表在当前版本中尚未实现，为规划中的功能。]]

== 订单商品表

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)5-#h(1.0em)订单商品表(#text(font: "Times New Roman")[order_item])结构]]

#v(0.5em)

#align(center)[#text(10.5pt, font: "SimSun")[#h(0.5em)注：此表在当前版本中尚未实现，为规划中的功能。]]

== 积分历史表

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)6-#h(1.0em)积分历史表(#text(font: "Times New Roman")[points_history])结构]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 3fr),
  stroke: 0.5pt,
  align: (left, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[字段名]]], [#align(center)[#text(9pt, font: "SimSun")[数据类型]]], [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#text(9pt, font: "Times New Roman")[id]], [#text(9pt, font: "Times New Roman")[INTEGER]], [#text(9pt, font: "SimSun")[记录ID，主键，自增]],
  [#text(9pt, font: "Times New Roman")[user_id]], [#text(9pt, font: "Times New Roman")[INTEGER]], [#text(9pt, font: "SimSun")[用户ID]],
  [#text(9pt, font: "Times New Roman")[points_change]], [#text(9pt, font: "Times New Roman")[INTEGER]], [#text(9pt, font: "SimSun")[积分变化量（正数增加，负数减少）]],
  [#text(9pt, font: "Times New Roman")[balance_after]], [#text(9pt, font: "Times New Roman")[INTEGER]], [#text(9pt, font: "SimSun")[变化后的余额]],
  [#text(9pt, font: "Times New Roman")[reason]], [#text(9pt, font: "Times New Roman")[VARCHAR(100)]], [#text(9pt, font: "SimSun")[积分变化原因：购物获得、兑换商品、签到奖励等]],
  [#text(9pt, font: "Times New Roman")[related_order_id]], [#text(9pt, font: "Times New Roman")[VARCHAR(50)]], [#text(9pt, font: "SimSun")[关联的订单ID（可选）]],
  [#text(9pt, font: "Times New Roman")[created_at]], [#text(9pt, font: "Times New Roman")[TIMESTAMP]], [#text(9pt, font: "SimSun")[创建时间]],
)]

#h(2.0em)#text(font: "SimHei")[注：]#text(font: "SimSun")[记录用户的所有积分变化历史，用于积分统计和追溯。]\

#h(2.0em)#text(font: "SimHei")[索引策略]：\

#h(2.0em)主键索引：#text(font: "Times New Roman")[id]\
#h(2.0em)普通索引：#text(font: "Times New Roman")[user_id] (按时间倒序查询)\

= 数据关系图

== 表关系说明

#h(2.0em)当前版本已实现的表关系如下：\

#h(2.0em)#text(font: "SimHei")[用户与积分历史]：#text(font: "Times New Roman")[user.id] - #text(font: "Times New Roman")[points_history.user_id] (一对多)\

#h(2.0em)规划中的表关系：\

#h(2.0em)#text(font: "SimHei")[用户与购物车]：#text(font: "Times New Roman")[user.id] - #text(font: "Times New Roman")[cart_item.user_id] (一对多)  #text(font: "SimHei")[（规划中）]\
#h(2.0em)#text(font: "SimHei")[用户与订单]：#text(font: "Times New Roman")[user.id] - #text(font: "Times New Roman")[order.user_id] (一对多)  #text(font: "SimHei")[（规划中）]\
#h(2.0em)#text(font: "SimHei")[购物车与商品]：#text(font: "Times New Roman")[product.id] - #text(font: "Times New Roman")[cart_item.product_id] (一对多)  #text(font: "SimHei")[（规划中）]\
#h(2.0em)#text(font: "SimHei")[订单与订单商品]：#text(font: "Times New Roman")[order.id] - #text(font: "Times New Roman")[order_item.order_id] (一对多)  #text(font: "SimHei")[（规划中）]\
#h(2.0em)#text(font: "SimHei")[订单商品与商品]：#text(font: "Times New Roman")[product.id] - #text(font: "Times New Roman")[order_item.product_id] (一对多)  #text(font: "SimHei")[（规划中）]\

== ER图说明

#h(2.0em)当前版本已实现的核心实体关系如下：\
#h(2.0em)——用户(#text(font: "Times New Roman")[User])：1个用户可以有多个积分历史记录\
#h(2.0em)——积分历史(#text(font: "Times New Roman")[PointsHistory])：记录用户每次积分变化的详细信息\

#h(2.0em)规划中的实体关系：\
#h(2.0em)——商品(#text(font: "Times New Roman")[Product])：1个商品可以被多个购物车项和多个订单商品引用  #text(font: "SimHei")[（规划中）]\
#h(2.0em)——购物车(#text(font: "Times New Roman")[CartItem])：记录用户未结算的商品  #text(font: "SimHei")[（规划中）]\
#h(2.0em)——订单(#text(font: "Times New Roman")[Order])：记录用户已结算的订单信息  #text(font: "SimHei")[（规划中）]\
#h(2.0em)——订单商品(#text(font: "Times New Roman")[OrderItem])：记录订单中包含的商品明细  #text(font: "SimHei")[（规划中）]\

= 数据字典

== 订单状态枚举

#align(center)[#table(
  columns: (1fr, 2fr),
  stroke: 0.5pt,
  align: (center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[状态值]]], [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#text(9pt, font: "Times New Roman")[pending]], [#text(9pt, font: "SimSun")[待处理]],
  [#text(9pt, font: "Times New Roman")[processing]], [#text(9pt, font: "SimSun")[处理中]],
  [#text(9pt, font: "Times New Roman")[shipped]], [#text(9pt, font: "SimSun")[已发货]],
  [#text(9pt, font: "Times New Roman")[delivered]], [#text(9pt, font: "SimSun")[已送达]],
)]
== 商品状态枚举

#align(center)[#table(
  columns: (1fr, 2fr),
  stroke: 0.5pt,
  align: (center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[状态值]]], [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#text(9pt, font: "Times New Roman")[0]], [#text(9pt, font: "SimSun")[下架]],
  [#text(9pt, font: "Times New Roman")[1]], [#text(9pt, font: "SimSun")[上架]],
)]
== 标志位枚举

#align(center)[#table(
  columns: (1fr, 2fr),
  stroke: 0.5pt,
  align: (center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[值]]], [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#text(9pt, font: "Times New Roman")[0]], [#text(9pt, font: "SimSun")[否]],
  [#text(9pt, font: "Times New Roman")[1]], [#text(9pt, font: "SimSun")[是]],
)]

= 索引策略

== 索引类型说明

#h(2.0em)#text(font: "SimHei")[主键索引]：自动创建的唯一索引，用于快速定位记录\
#h(2.0em)#text(font: "SimHei")[唯一索引]：保证列值的唯一性，提高查询效率\
#h(2.0em)#text(font: "SimHei")[普通索引]：提高查询和排序性能\
#h(2.0em)#text(font: "SimHei")[复合索引]：多个列组合的索引，适用于多条件查询\
#h(2.0em)#text(font: "SimHei")[全文索引]：支持文本内容的全文搜索\

== 索引优化建议

#h(2.0em)在频繁查询的字段上创建索引\
#h(2.0em)在关联字段上创建索引\
#h(2.0em)避免在小表上创建过多索引\
#h(2.0em)定期分析和优化索引\
#h(2.0em)使用复合索引减少索引数量\

= 性能优化

== 查询优化

#h(2.0em)使用覆盖索引减少回表\
#h(2.0em)避免使用 #text(font: "Times New Roman")[SELECT \*]，只查询需要的字段\
#h(2.0em)合理使用分页查询\
#h(2.0em)使用 #text(font: "Times New Roman")[EXPLAIN] 分析查询执行计划\
#h(2.0em)优化子查询，改用连接查询\

== 表分区策略

#h(2.0em)按时间分区：订单表可按月分区\
#h(2.0em)按业务分区：购物车表可按用户ID哈希分区\
#h(2.0em)历史数据归档：定期将历史订单数据归档\

== 缓存策略

#h(2.0em)商品数据缓存：使用 #text(font: "Times New Roman")[Redis] 缓存热点商品数据\
#h(2.0em)购物车缓存：临时购物车数据存储在 #text(font: "Times New Roman")[Redis]\
#h(2.0em)订单缓存：最近订单数据缓存，提高查询速度\

= 安全设计

== 数据加密

#h(2.0em)用户密码：使用 #text(font: "Times New Roman")[BCrypt] 加密存储\
#h(2.0em)敏感信息：个人隐私信息加密存储\
#h(2.0em)传输加密：使用 #text(font: "Times New Roman")[HTTPS] 协议\

== 权限控制

#h(2.0em)最小权限原则：数据库用户只授予必要的权限\
#h(2.0em)角色分离：读写权限分离\
#h(2.0em)审计日志：记录敏感操作日志\

== SQL注入防护

#h(2.0em)使用参数化查询\
#h(2.0em)使用 ORM 框架避免直接拼接 SQL\
#h(2.0em)输入验证和过滤\
#h(2.0em)使用存储过程封装业务逻辑\

= 数据备份与恢复

== 备份策略

#h(2.0em)全量备份：每天凌晨进行全量备份\
#h(2.0em)增量备份：每小时进行增量备份\
#h(2.0em)日志备份：每15分钟进行日志备份\
#h(2.0em)备份保留：保留30天的备份文件\

== 恢复策略

#h(2.0em)点时间恢复：支持恢复到指定时间点\
#h(2.0em)灾难恢复：异地备份，快速切换\
#h(2.0em)恢复演练：定期进行恢复演练\

= 版本历史

== 版本 1.0.0

#h(2.0em)初始版本，建立基础数据库结构\
#h(2.0em)支持用户、商品、购物车、订单核心功能\

#v(2.0em)

// 版本历史结束，添加水平居中的分割线
#align(center)[#line(length: 25%, stroke: 0.5pt)]

// 第6页结束
#pagebreak()
// 第7页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])

// 第7页结束
