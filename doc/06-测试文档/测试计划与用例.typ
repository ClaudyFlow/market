// 测试计划与用例 - Typst 格式

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

#v(85mm)
#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  测试计划与用例]]
#pagebreak()

#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——测试计划与用例]]
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

= 1. 测试概述

== 1.1 测试目标
#h(2.0em)——验证系统功能符合需求规格\
#h(2.0em)——发现并修复缺陷\
#h(2.0em)——确保系统稳定性和性能

== 1.2 测试范围
#h(2.0em)——前端：用户端、商家端、管理端\
#h(2.0em)——后端：API接口、业务逻辑\
#h(2.0em)——数据库：数据完整性、性能

= 2. 测试策略

== 2.1 单元测试
#h(2.0em)——前端: Vitest + Vue Test Utils\
#h(2.0em)——后端: JUnit 5 + Mockito\
#h(2.0em)——覆盖率目标: > 70%

== 2.2 集成测试
#h(2.0em)——前后端联调测试\
#h(2.0em)——数据库集成测试\
#h(2.0em)——Redis 缓存测试

== 2.3 端到端测试
#h(2.0em)——Cypress / Playwright\
#h(2.0em)——核心业务流程测试

== 2.4 性能测试
#h(2.0em)——JMeter 压力测试\
#h(2.0em)——并发用户测试

= 3. 测试用例

== 3.1 用户认证模块
#align(center)[#table(
  columns: (3fr, 4fr, 4fr, 2fr),
  stroke: 0.5pt,
  [用例ID], [测试场景], [预期结果], [优先级],
  [TC-AUTH-001], [正常登录], [登录成功，跳转首页], [P0],
  [TC-AUTH-002], [密码错误], [提示"密码错误"], [P0],
  [TC-AUTH-003], [用户不存在], [提示"用户不存在"], [P0],
  [TC-AUTH-004], [注册新用户], [注册成功，跳转登录], [P0],
  [TC-AUTH-005], [Token过期], [返回401，跳转登录], [P1],
)]

== 3.2 购物车模块
#align(center)[#table(
  columns: (3fr, 4fr, 4fr, 2fr),
  stroke: 0.5pt,
  [用例ID], [测试场景], [预期结果], [优先级],
  [TC-CART-001], [添加商品], [购物车数量+1], [P0],
  [TC-CART-002], [修改数量], [总价重新计算], [P0],
  [TC-CART-003], [删除商品], [商品从购物车移除], [P0],
  [TC-CART-004], [全选/取消全选], [所有商品选中], [P1],
  [TC-CART-005], [库存不足], [提示"库存不足"], [P0],
)]

== 3.3 订单模块
#align(center)[#table(
  columns: (3fr, 4fr, 4fr, 2fr),
  stroke: 0.5pt,
  [用例ID], [测试场景], [预期结果], [优先级],
  [TC-ORDER-001], [创建订单], [订单创建成功], [P0],
  [TC-ORDER-002], [订单支付], [支付成功，订单状态更新], [P0],
  [TC-ORDER-003], [取消订单], [订单状态变为已取消], [P0],
  [TC-ORDER-004], [订单评价], [评价成功], [P1],
  [TC-ORDER-005], [订单退款], [退款申请成功], [P1],
)]

= 4. 缺陷管理

== 4.1 缺陷等级
#align(center)[#table(
  columns: (3fr, 6fr, 4fr),
  stroke: 0.5pt,
  [等级], [说明], [响应时间],
  [P0-致命], [系统崩溃、数据丢失], [立即修复],
  [P1-严重], [核心功能不可用], [4小时内],
  [P2-一般], [部分功能异常], [24小时内],
  [P3-轻微], [UI/体验问题], [下次迭代],
)]

== 4.2 缺陷跟踪
#h(2.0em)使用 GitHub Issues 跟踪缺陷，标签: bug, priority/P0, priority/P1 等

= 5. 测试进度

#align(center)[#table(
  columns: (3fr, 2fr, 2fr, 2fr, 2fr, 2fr),
  stroke: 0.5pt,
  [模块], [总用例], [已执行], [通过], [失败], [进度],
  [用户认证], [5], [0], [0], [0], [0%],
  [商品模块], [4], [0], [0], [0], [0%],
  [购物车], [5], [0], [0], [0], [0%],
  [订单模块], [5], [0], [0], [0], [0%],
  [优惠券], [3], [0], [0], [0], [0%],
  [总计], [22], [0], [0], [0], [0%],
)]

#v(2cm)
#align(center)[#text(10pt, font: "SimSun")[文档版本：v1.0.0 | 最后更新：#datetime.today().display()]]
