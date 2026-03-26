// 第 1 页开始
#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

// 封面标题：标题前间距 15mm，标题后间距 10mm，水平居中

#v(85mm)

#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  前端功能介绍]]

// 第 1 页结束
#pagebreak()
// 第 2 页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])
#v(1fr)


// 第 2 页结束
#pagebreak()
// 第 3 页开始

// 设置罗马数字页码和页眉页脚
#set page(
  numbering: "I",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——前端功能介绍]]\
    #place(top, dy: 30mm)[#line(length: 100%, stroke: 0.5pt)]
  ],
  footer: [
    #place(bottom, dy: -25mm)[#line(length: 100%, stroke: 0.5pt)]\
    #place(bottom + left, dy: -20mm)[#text(9pt, font: "SimSun")[#datetime.today().display()]]\
    #context {
      place(bottom + right, dy: -20mm)[#align(right)[#text(9pt, font: "SimSun")[#counter(page).display()]]]
    }
  ],
)
#counter(page).update(1)

// 目录标题：从页眉线向下距离 15mm，水平居中
#v(15mm)

#align(top + center)[#text(16pt, font: "SimHei")[目录]]

#v(10mm)

#set outline(
  title: none,
  depth: 2,
  indent: 1em,
)

// 设置目录内容为五号宋体，行距为 1.5 倍
#show outline.entry: it => {
  set text(font: "SimSun", size: 10.5pt)
  set block(spacing: 1.5em)
  it
}

#outline()

// 第 3 页结束
#pagebreak()
// 第 4 页开始

// 设置页眉页脚
#set page(
  numbering: "I",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——前端功能介绍]]\
    #place(top, dy: 30mm)[#line(length: 100%, stroke: 0.5pt)]
  ],
  footer: [
    #place(bottom, dy: -25mm)[#line(length: 100%, stroke: 0.5pt)]\
    #place(bottom + left, dy: -20mm)[#text(9pt, font: "SimSun")[#datetime.today().display()]]\
    #context {
      place(bottom + right, dy: -20mm)[#align(right)[#text(9pt, font: "SimSun")[#counter(page).display()]]]
    }
  ],
)

// 前言标题：标题前间距 15mm，标题后间距 10mm，三号黑体居中对齐

#v(15mm)

#align(center)[#text(16pt, font: "SimHei")[= 前言]]

#v(10mm)

// 前言内容：五号宋体左对齐，首行缩进两个全角空格，行距为 1.25 倍
#set text(font: "SimSun", size: 10.5pt)
#set par(leading: 1.25em)

#h(2.0em)本前端功能介绍旨在为开发人员和用户提供详细的购物商城系统前端应用功能说明。\
#h(2.0em)文档涵盖了技术栈、功能模块、组件架构、数据管理、用户体验特性等多个方面的内容。\
#h(2.0em)购物商城系统是一个基于 Vue#h(0.25em)3 的现代化在线购物商城前端应用，采用组件化架构，支持完整的电商购物流程。\
#h(2.0em)通过本指南，读者将能够了解前端应用的完整设计理念和实现细节，掌握必要的技术知识，并能够独立进行前端开发和维护工作。\
#h(2.0em)我们希望本指南能够帮助用户快速理解前端应用的功能特性和技术架构。\
#h(2.0em)本指南适用于具备一定前端开发基础的人员使用，读者应熟悉 HTML、CSS、JavaScript 等基础知识。\
#h(2.0em)在开始使用前，请仔细阅读本文档，确保理解每个功能模块的设计思想和使用方法。\
#h(2.0em)如遇到本文档未覆盖的问题或有任何疑问，请及时联系技术支持团队。\
#h(2.0em)我们将持续更新和完善本指南，以帮助用户更好地使用购物商城系统前端应用。\

// 第 4 页结束
#pagebreak()
// 第 5 页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])

// 第 5 页结束
#pagebreak()
// 第 6 页开始

// 切换到阿拉伯数字页码，设置页眉页脚
#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——前端功能介绍]]\
    #place(top, dy: 30mm)[#line(length: 100%, stroke: 0.5pt)]
  ],
  footer: [
    #place(bottom, dy: -25mm)[#line(length: 100%, stroke: 0.5pt)]\
    #place(bottom + left, dy: -20mm)[#text(9pt, font: "SimSun")[#datetime.today().display()]]\
    #context {
      place(bottom + right, dy: -20mm)[#align(right)[#text(9pt, font: "SimSun")[#counter(page).display()]]]
    }
  ],
)
#counter(page).update(1)

// 正文大标题：标题前间距 15mm，标题后间距 10mm，三号黑体居中对齐
#v(15mm)
#align(center)[#text(16pt, font: "SimHei")[前端功能介绍]]
#v(10mm)

// 正文内容：五号宋体左对齐，首行缩进两个全角空格，正文部分的标题是五号黑体，行距为 1.25 倍
// 设置标题编号与标题之间有一个全角空格
#set heading(numbering: (..nums) => {
  let num = numbering("1.1", ..nums)
  [#text(10.5pt, font: "SimHei")[#h(1em)#num#h(1em)]]
})

#set text(font: "SimSun", size: 10.5pt)
#set par(justify: true, first-line-indent: 2em)
#set block(spacing: 1.25em)

// 正文一级标题：三号黑体，段前段后间距 10mm
#show heading.where(level: 1): it => {
  set block(above: 10mm, below: 10mm)
  set text(font: "SimHei", size: 16pt)
  it
}

// 正文二级标题：四号黑体，段前段后间距 5mm
#show heading.where(level: 2): it => {
  set block(above: 5mm, below: 5mm)
  set text(font: "SimHei", size: 14pt)
  it
}

// 正文三级标题：小四号黑体，段前段后间距 3mm
#show heading.where(level: 3): it => {
  set block(above: 3mm, below: 3mm)
  set text(font: "SimHei", size: 12pt)
  it
}

= 项目概述

#h(2.0em)购物商城系统是一个基于 Vue#h(0.25em)3 的现代化在线购物商城前端应用。应用采用组件化架构，支持完整的电商购物流程，包括商品浏览、购物车管理、订单查看和用户系统。\
#h(2.0em)前端应用使用 TypeScript 开发，采用 Vite 构建工具，提供高效的开发体验和优化的生产构建。\
#h(2.0em)系统包含三个应用端：用户端、管理后台和商家后台，提供完整的电商生态解决方案。\
#h(2.0em)本前端功能介绍将详细介绍系统的各项功能特性，帮助读者全面了解前端应用的设计和实现。\

= 技术栈

#h(2.0em)前端应用采用了现代化的技术栈，确保应用具有良好的性能和用户体验。\

== 核心技术栈

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)1#h(1.0em)核心技术栈]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[分类]]],
  [#align(center)[#text(9pt, font: "SimSun")[技术]]],
  [#align(center)[#text(9pt, font: "SimSun")[版本]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],

  [#text(9pt, font: "SimHei")[框架]],
  [#text(9pt, font: "Times New Roman")[Vue]],
  [#text(9pt, font: "Times New Roman")[3.4+]],
  [#text(9pt, font: "SimSun")[组合式 API，响应式框架]],

  [#text(9pt, font: "SimHei")[语言]],
  [#text(9pt, font: "Times New Roman")[TypeScript]],
  [#text(9pt, font: "Times New Roman")[5.9+]],
  [#text(9pt, font: "SimSun")[类型安全的 JavaScript 超集]],

  [#text(9pt, font: "SimHei")[构建]],
  [#text(9pt, font: "Times New Roman")[Vite]],
  [#text(9pt, font: "Times New Roman")[5.0+]],
  [#text(9pt, font: "SimSun")[下一代前端构建工具]],

  [#text(9pt, font: "SimHei")[状态管理]],
  [#text(9pt, font: "Times New Roman")[Pinia]],
  [#text(9pt, font: "Times New Roman")[2.1+]],
  [#text(9pt, font: "SimSun")[Vue 3 官方状态管理库]],

  [#text(9pt, font: "SimHei")[路由]],
  [#text(9pt, font: "Times New Roman")[Vue Router]],
  [#text(9pt, font: "Times New Roman")[4.2+]],
  [#text(9pt, font: "SimSun")[官方路由管理器]],

  [#text(9pt, font: "SimHei")[UI 库]],
  [#text(9pt, font: "Times New Roman")[Element Plus]],
  [#text(9pt, font: "Times New Roman")[2.4+]],
  [#text(9pt, font: "SimSun")[Vue 3 UI 组件库]],

  [#text(9pt, font: "SimHei")[图标]],
  [#text(9pt, font: "Times New Roman")[element-plus/icons-vue]],
  [#text(9pt, font: "Times New Roman")[2.3+]],
  [#text(9pt, font: "SimSun")[200+ 个矢量图标]],

  [#text(9pt, font: "SimHei")[HTTP]],
  [#text(9pt, font: "Times New Roman")[Axios]],
  [#text(9pt, font: "Times New Roman")[1.6+]],
  [#text(9pt, font: "SimSun")[HTTP 客户端]],

  [#text(9pt, font: "SimHei")[图表]],
  [#text(9pt, font: "Times New Roman")[ECharts]],
  [#text(9pt, font: "Times New Roman")[5.5+]],
  [#text(9pt, font: "SimSun")[数据可视化库]],
)]

== 与原档差异

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)2#h(1.0em)原文档与实际实现差异]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 2fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[项目]]],
  [#align(center)[#text(9pt, font: "SimSun")[原文档描述]]],
  [#align(center)[#text(9pt, font: "SimSun")[实际实现]]],

  [#text(9pt, font: "Times New Roman")[构建方式]],
  [#text(9pt, font: "SimSun")[无构建工具，CDN 引用]],
  [#text(9pt, font: "SimSun")[Vite 构建，npm 包管理]],

  [#text(9pt, font: "Times New Roman")[开发语言]],
  [#text(9pt, font: "SimSun")[JavaScript]],
  [#text(9pt, font: "SimSun")[TypeScript]],

  [#text(9pt, font: "Times New Roman")[状态管理]],
  [#text(9pt, font: "SimSun")[localStorage]],
  [#text(9pt, font: "SimSun")[Pinia Store]],

  [#text(9pt, font: "Times New Roman")[UI 框架]],
  [#text(9pt, font: "SimSun")[无]],
  [#text(9pt, font: "SimSun")[Element Plus]],

  [#text(9pt, font: "Times New Roman")[路由系统]],
  [#text(9pt, font: "SimSun")[无]],
  [#text(9pt, font: "SimSun")[Vue Router]],

  [#text(9pt, font: "Times New Roman")[HTTP 请求]],
  [#text(9pt, font: "SimSun")[无]],
  [#text(9pt, font: "SimSun")[Axios]],

  [#text(9pt, font: "Times New Roman")[组件数量]],
  [#text(9pt, font: "SimSun")[5 个]],
  [#text(9pt, font: "SimSun")[75+ 个]],

  [#text(9pt, font: "Times New Roman")[应用端]],
  [#text(9pt, font: "SimSun")[单端]],
  [#text(9pt, font: "SimSun")[三端（user/admin/merchant）]],
)]

= 功能模块

#h(2.0em)前端应用包含多个功能模块，每个模块负责特定的业务功能。\
#h(2.0em)系统包含三个应用端：用户端、管理后台和商家后台。\

== 用户端功能

=== 商品模块

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)3#h(1.0em)商品模块功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[商品列表]],
  [#text(9pt, font: "SimSun")[网格布局展示，支持分页、排序、筛选]],
  [#text(9pt, font: "Times New Roman")[ProductList.vue]],

  [#text(9pt, font: "SimHei")[商品搜索]],
  [#text(9pt, font: "SimSun")[支持名称、描述、分类的模糊搜索]],
  [#text(9pt, font: "Times New Roman")[ProductList.vue]],

  [#text(9pt, font: "SimHei")[商品详情]],
  [#text(9pt, font: "SimSun")[展示商品详细信息、图片、评价]],
  [#text(9pt, font: "Times New Roman")[ProductDetail.vue]],

  [#text(9pt, font: "SimHei")[商品分类]],
  [#text(9pt, font: "SimSun")[多级分类展示，点击筛选]],
  [#text(9pt, font: "Times New Roman")[CategoryPanel.vue]],

  [#text(9pt, font: "SimHei")[商品轮播]],
  [#text(9pt, font: "SimSun")[首页轮播图展示]],
  [#text(9pt, font: "Times New Roman")[BannerCarousel.vue]],
)]

=== 购物车模块

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)4#h(1.0em)购物车模块功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[购物车管理]],
  [#text(9pt, font: "SimSun")[添加商品、数量调整、移除商品]],
  [#text(9pt, font: "Times New Roman")[Cart.vue]],

  [#text(9pt, font: "SimHei")[实时计算]],
  [#text(9pt, font: "SimSun")[商品总价、运费、订单总额]],
  [#text(9pt, font: "Times New Roman")[cart.ts (Store)]],

  [#text(9pt, font: "SimHei")[数据持久化]],
  [#text(9pt, font: "SimSun")[Pinia Store + localStorage]],
  [#text(9pt, font: "Times New Roman")[cart.ts]],

  [#text(9pt, font: "SimHei")[快速添加]],
  [#text(9pt, font: "SimSun")[商品卡片直接添加到购物车]],
  [#text(9pt, font: "Times New Roman")[ProductCard.vue]],
)]

=== 订单模块

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)5#h(1.0em)订单模块功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[订单创建]],
  [#text(9pt, font: "SimSun")[从购物车生成订单]],
  [#text(9pt, font: "Times New Roman")[Order.vue]],

  [#text(9pt, font: "SimHei")[订单列表]],
  [#text(9pt, font: "SimSun")[展示历史订单，支持状态筛选]],
  [#text(9pt, font: "Times New Roman")[Orders.vue]],

  [#text(9pt, font: "SimHei")[订单详情]],
  [#text(9pt, font: "SimSun")[查看订单详细信息]],
  [#text(9pt, font: "Times New Roman")[OrderDetail.vue]],

  [#text(9pt, font: "SimHei")[订单状态]],
  [#text(9pt, font: "SimSun")[待处理、处理中、已发货、已送达]],
  [#text(9pt, font: "Times New Roman")[OrderCard.vue]],

  [#text(9pt, font: "SimHei")[订单操作]],
  [#text(9pt, font: "SimSun")[取消订单、确认收货]],
  [#text(9pt, font: "Times New Roman")[Orders.vue]],
)]

=== 用户中心模块

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)6#h(1.0em)用户中心模块功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[个人资料]],
  [#text(9pt, font: "SimSun")[查看和编辑个人信息]],
  [#text(9pt, font: "Times New Roman")[UserCenter.vue]],

  [#text(9pt, font: "SimHei")[收货地址]],
  [#text(9pt, font: "SimSun")[地址管理、默认地址设置]],
  [#text(9pt, font: "Times New Roman")[Address.vue]],

  [#text(9pt, font: "SimHei")[我的收藏]],
  [#text(9pt, font: "SimSun")[商品收藏管理]],
  [#text(9pt, font: "Times New Roman")[Favorites.vue]],

  [#text(9pt, font: "SimHei")[我的关注]],
  [#text(9pt, font: "SimSun")[店铺关注管理]],
  [#text(9pt, font: "Times New Roman")[Follows.vue]],

  [#text(9pt, font: "SimHei")[积分中心]],
  [#text(9pt, font: "SimSun")[积分查询、签到打卡]],
  [#text(9pt, font: "Times New Roman")[Credit.vue]],

  [#text(9pt, font: "SimHei")[会员中心]],
  [#text(9pt, font: "SimSun")[VIP 等级、权益展示]],
  [#text(9pt, font: "Times New Roman")[VipCenter.vue]],
)]

=== 认证模块

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)7#h(1.0em)认证模块功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[用户登录]],
  [#text(9pt, font: "SimSun")[用户名/密码登录]],
  [#text(9pt, font: "Times New Roman")[Login.vue]],

  [#text(9pt, font: "SimHei")[用户注册]],
  [#text(9pt, font: "SimSun")[新用户注册，支持邮箱验证]],
  [#text(9pt, font: "Times New Roman")[Register.vue]],

  [#text(9pt, font: "SimHei")[退出登录]],
  [#text(9pt, font: "SimSun")[安全退出，清除 token]],
  [#text(9pt, font: "Times New Roman")[Header.vue]],

  [#text(9pt, font: "SimHei")[权限守卫]],
  [#text(9pt, font: "SimSun")[未登录跳转登录页]],
  [#text(9pt, font: "Times New Roman")[router/index.ts]],
)]

=== 积分系统

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)8#h(1.0em)积分系统功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[API]]],

  [#text(9pt, font: "SimHei")[积分查询]],
  [#text(9pt, font: "SimSun")[查看当前积分余额]],
  [#text(9pt, font: "Times New Roman")[GET /api/credit]],

  [#text(9pt, font: "SimHei")[签到打卡]],
  [#text(9pt, font: "SimSun")[每日签到获取积分]],
  [#text(9pt, font: "Times New Roman")[POST /api/credit/checkin]],

  [#text(9pt, font: "SimHei")[积分历史]],
  [#text(9pt, font: "SimSun")[查看积分变动记录]],
  [#text(9pt, font: "Times New Roman")[GET /api/credit/history]],

  [#text(9pt, font: "SimHei")[积分兑换]],
  [#text(9pt, font: "SimSun")[使用积分兑换商品]],
  [#text(9pt, font: "Times New Roman")[POST /api/credit/redeem]],
)]

=== VIP 系统

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)9#h(1.0em)VIP 系统功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[API]]],

  [#text(9pt, font: "SimHei")[VIP 等级]],
  [#text(9pt, font: "SimSun")[展示当前 VIP 等级和进度]],
  [#text(9pt, font: "Times New Roman")[GET /api/vip/info]],

  [#text(9pt, font: "SimHei")[VIP 权益]],
  [#text(9pt, font: "SimSun")[展示会员专属权益]],
  [#text(9pt, font: "Times New Roman")[GET /api/vip/benefits]],

  [#text(9pt, font: "SimHei")[VIP 升级]],
  [#text(9pt, font: "SimSun")[满足条件自动升级]],
  [#text(9pt, font: "SimSun")[系统自动处理]],

  [#text(9pt, font: "SimHei")[成长值]],
  [#text(9pt, font: "SimSun")[展示成长值和升级进度]],
  [#text(9pt, font: "Times New Roman")[GET /api/vip/info]],
)]

=== 优惠券系统

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)10#h(1.0em)优惠券系统功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[API]]],

  [#text(9pt, font: "SimHei")[领取优惠券]],
  [#text(9pt, font: "SimSun")[领取可用优惠券]],
  [#text(9pt, font: "Times New Roman")[POST /api/coupon/:id/claim]],

  [#text(9pt, font: "SimHei")[我的优惠券]],
  [#text(9pt, font: "SimSun")[查看已领取优惠券]],
  [#text(9pt, font: "Times New Roman")[GET /api/coupon/my]],

  [#text(9pt, font: "SimHei")[优惠券使用]],
  [#text(9pt, font: "SimSun")[下单时使用优惠券]],
  [#text(9pt, font: "Times New Roman")[POST /api/coupon/:id/use]],

  [#text(9pt, font: "SimHei")[优惠券状态]],
  [#text(9pt, font: "SimSun")[未使用、已使用、已过期]],
  [#text(9pt, font: "Times New Roman")[GET /api/coupon/my?status=]],
)]

=== 收藏与关注

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)11#h(1.0em)收藏与关注功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[API]]],

  [#text(9pt, font: "SimHei")[商品收藏]],
  [#text(9pt, font: "SimSun")[收藏喜欢的商品]],
  [#text(9pt, font: "Times New Roman")[POST /api/favorite/:productId]],

  [#text(9pt, font: "SimHei")[收藏列表]],
  [#text(9pt, font: "SimSun")[查看已收藏商品]],
  [#text(9pt, font: "Times New Roman")[GET /api/favorite]],

  [#text(9pt, font: "SimHei")[店铺关注]],
  [#text(9pt, font: "SimSun")[关注喜欢的店铺]],
  [#text(9pt, font: "Times New Roman")[POST /api/follow/:shopId]],

  [#text(9pt, font: "SimHei")[关注列表]],
  [#text(9pt, font: "SimSun")[查看已关注店铺]],
  [#text(9pt, font: "Times New Roman")[GET /api/follow]],
)]

=== 评价系统

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)12#h(1.0em)评价系统功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[API]]],

  [#text(9pt, font: "SimHei")[商品评价]],
  [#text(9pt, font: "SimSun")[查看商品评价列表]],
  [#text(9pt, font: "Times New Roman")[GET /api/review/product/:id]],

  [#text(9pt, font: "SimHei")[发布评价]],
  [#text(9pt, font: "SimSun")[对已购商品进行评价]],
  [#text(9pt, font: "Times New Roman")[POST /api/review]],

  [#text(9pt, font: "SimHei")[评价图片]],
  [#text(9pt, font: "SimSun")[支持上传评价图片]],
  [#text(9pt, font: "Times New Roman")[POST /api/review]],

  [#text(9pt, font: "SimHei")[评分展示]],
  [#text(9pt, font: "SimSun")[平均分和评分分布]],
  [#text(9pt, font: "Times New Roman")[ProductDetail.vue]],
)]

=== 抽奖活动

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)13#h(1.0em)抽奖活动功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[抽奖转盘]],
  [#text(9pt, font: "SimSun")[幸运转盘抽奖界面]],
  [#text(9pt, font: "Times New Roman")[Lottery.vue]],

  [#text(9pt, font: "SimHei")[抽奖记录]],
  [#text(9pt, font: "SimSun")[查看历史抽奖结果]],
  [#text(9pt, font: "Times New Roman")[Lottery.vue]],

  [#text(9pt, font: "SimHei")[奖品展示]],
  [#text(9pt, font: "SimSun")[展示可抽取的奖品]],
  [#text(9pt, font: "Times New Roman")[Lottery.vue]],

  [#text(9pt, font: "SimHei")[积分消耗]],
  [#text(9pt, font: "SimSun")[抽奖消耗积分]],
  [#text(9pt, font: "Times New Roman")[POST /api/lottery/draw]],
)]

=== 消息通知

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)14#h(1.0em)消息通知功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[组件]]],

  [#text(9pt, font: "SimHei")[系统公告]],
  [#text(9pt, font: "SimSun")[展示系统公告]],
  [#text(9pt, font: "Times New Roman")[AnnouncementPanel.vue]],

  [#text(9pt, font: "SimHei")[用户通知]],
  [#text(9pt, font: "SimSun")[用户专属通知]],
  [#text(9pt, font: "Times New Roman")[UserNoticePanel.vue]],

  [#text(9pt, font: "SimHei")[消息中心]],
  [#text(9pt, font: "SimSun")[集中查看各类消息]],
  [#text(9pt, font: "Times New Roman")[MessageCenter.vue]],
)]

== 管理后台功能

=== 用户管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)15#h(1.0em)用户管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[用户列表]],
  [#text(9pt, font: "SimSun")[查看所有用户，支持搜索筛选]],
  [#text(9pt, font: "Times New Roman")[UserList.vue]],

  [#text(9pt, font: "SimHei")[用户详情]],
  [#text(9pt, font: "SimSun")[查看用户详细信息]],
  [#text(9pt, font: "Times New Roman")[UserDetail.vue]],

  [#text(9pt, font: "SimHei")[用户状态]],
  [#text(9pt, font: "SimSun")[启用/禁用用户账户]],
  [#text(9pt, font: "Times New Roman")[UserList.vue]],

  [#text(9pt, font: "SimHei")[用户统计]],
  [#text(9pt, font: "SimSun")[用户数量、活跃度统计]],
  [#text(9pt, font: "Times New Roman")[Statistics.vue]],
)]

=== 商品管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)16#h(1.0em)商品管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[商品审核]],
  [#text(9pt, font: "SimSun")[审核商家提交的商品]],
  [#text(9pt, font: "Times New Roman")[ProductAudit.vue]],

  [#text(9pt, font: "SimHei")[商品列表]],
  [#text(9pt, font: "SimSun")[查看所有商品]],
  [#text(9pt, font: "Times New Roman")[ProductList.vue]],

  [#text(9pt, font: "SimHei")[商品上下架]],
  [#text(9pt, font: "SimSun")[控制商品销售状态]],
  [#text(9pt, font: "Times New Roman")[ProductAudit.vue]],

  [#text(9pt, font: "SimHei")[分类管理]],
  [#text(9pt, font: "SimSun")[管理商品分类]],
  [#text(9pt, font: "Times New Roman")[CategoryManage.vue]],
)]

=== 订单管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)17#h(1.0em)订单管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[订单监控]],
  [#text(9pt, font: "SimSun")[查看所有订单状态]],
  [#text(9pt, font: "Times New Roman")[OrderMonitor.vue]],

  [#text(9pt, font: "SimHei")[订单处理]],
  [#text(9pt, font: "SimSun")[处理异常订单]],
  [#text(9pt, font: "Times New Roman")[OrderMonitor.vue]],

  [#text(9pt, font: "SimHei")[订单统计]],
  [#text(9pt, font: "SimSun")[订单数量、金额统计]],
  [#text(9pt, font: "Times New Roman")[Statistics.vue]],
)]

=== 商家管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)18#h(1.0em)商家管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[商家列表]],
  [#text(9pt, font: "SimSun")[查看所有入驻商家]],
  [#text(9pt, font: "Times New Roman")[MerchantList.vue]],

  [#text(9pt, font: "SimHei")[商家审核]],
  [#text(9pt, font: "SimSun")[审核商家入驻申请]],
  [#text(9pt, font: "Times New Roman")[MerchantList.vue]],

  [#text(9pt, font: "SimHei")[商家状态]],
  [#text(9pt, font: "SimSun")[启用/禁用商家账户]],
  [#text(9pt, font: "Times New Roman")[MerchantList.vue]],
)]

=== 评价管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)19#h(1.0em)评价管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[评价审核]],
  [#text(9pt, font: "SimSun")[审核用户评价]],
  [#text(9pt, font: "Times New Roman")[ReviewAudit.vue]],

  [#text(9pt, font: "SimHei")[评价管理]],
  [#text(9pt, font: "SimSun")[删除违规评价]],
  [#text(9pt, font: "Times New Roman")[ReviewAudit.vue]],
)]

=== 优惠券管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)20#h(1.0em)优惠券管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[优惠券列表]],
  [#text(9pt, font: "SimSun")[查看所有优惠券]],
  [#text(9pt, font: "Times New Roman")[Coupon.vue]],

  [#text(9pt, font: "SimHei")[创建优惠券]],
  [#text(9pt, font: "SimSun")[创建新的优惠券]],
  [#text(9pt, font: "Times New Roman")[Coupon.vue]],

  [#text(9pt, font: "SimHei")[优惠券统计]],
  [#text(9pt, font: "SimSun")[领取和使用统计]],
  [#text(9pt, font: "Times New Roman")[Coupon.vue]],
)]

=== 系统设置

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)21#h(1.0em)系统设置功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[基础设置]],
  [#text(9pt, font: "SimSun")[网站名称、Logo 等]],
  [#text(9pt, font: "Times New Roman")[Settings.vue]],

  [#text(9pt, font: "SimHei")[积分设置]],
  [#text(9pt, font: "SimSun")[签到积分、订单返还比例]],
  [#text(9pt, font: "Times New Roman")[Settings.vue]],

  [#text(9pt, font: "SimHei")[运费设置]],
  [#text(9pt, font: "SimSun")[包邮门槛、运费模板]],
  [#text(9pt, font: "Times New Roman")[Settings.vue]],
)]

=== 数据统计

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)22#h(1.0em)数据统计功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[销售统计]],
  [#text(9pt, font: "SimSun")[销售额、订单量图表]],
  [#text(9pt, font: "Times New Roman")[Statistics.vue]],

  [#text(9pt, font: "SimHei")[用户分析]],
  [#text(9pt, font: "SimSun")[用户增长、活跃度]],
  [#text(9pt, font: "Times New Roman")[Statistics.vue]],

  [#text(9pt, font: "SimHei")[商品分析]],
  [#text(9pt, font: "SimSun")[热销商品、库存预警]],
  [#text(9pt, font: "Times New Roman")[Statistics.vue]],
)]

== 商家后台功能

=== 商品管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)23#h(1.0em)商家商品管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[商品列表]],
  [#text(9pt, font: "SimSun")[查看和管理店铺商品]],
  [#text(9pt, font: "Times New Roman")[ProductList.vue]],

  [#text(9pt, font: "SimHei")[创建商品]],
  [#text(9pt, font: "SimSun")[发布新商品]],
  [#text(9pt, font: "Times New Roman")[ProductEdit.vue]],

  [#text(9pt, font: "SimHei")[编辑商品]],
  [#text(9pt, font: "SimSun")[修改商品信息]],
  [#text(9pt, font: "Times New Roman")[ProductEdit.vue]],

  [#text(9pt, font: "SimHei")[商品上下架]],
  [#text(9pt, font: "SimSun")[控制商品销售状态]],
  [#text(9pt, font: "Times New Roman")[ProductList.vue]],
)]

=== 订单管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)24#h(1.0em)商家订单管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[订单列表]],
  [#text(9pt, font: "SimSun")[查看店铺订单]],
  [#text(9pt, font: "Times New Roman")[OrderList.vue]],

  [#text(9pt, font: "SimHei")[订单详情]],
  [#text(9pt, font: "SimSun")[查看订单详细信息]],
  [#text(9pt, font: "Times New Roman")[OrderDetail.vue]],

  [#text(9pt, font: "SimHei")[订单发货]],
  [#text(9pt, font: "SimSun")[填写物流信息发货]],
  [#text(9pt, font: "Times New Roman")[OrderDetail.vue]],

  [#text(9pt, font: "SimHei")[退款处理]],
  [#text(9pt, font: "SimSun")[处理用户退款申请]],
  [#text(9pt, font: "Times New Roman")[OrderDetail.vue]],
)]

=== 评价管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)25#h(1.0em)商家评价管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[评价列表]],
  [#text(9pt, font: "SimSun")[查看店铺商品评价]],
  [#text(9pt, font: "Times New Roman")[ReviewList.vue]],

  [#text(9pt, font: "SimHei")[评价回复]],
  [#text(9pt, font: "SimSun")[回复用户评价]],
  [#text(9pt, font: "Times New Roman")[ReviewList.vue]],
)]

=== 店铺管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)26#h(1.0em)店铺管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[店铺信息]],
  [#text(9pt, font: "SimSun")[编辑店铺基本信息]],
  [#text(9pt, font: "Times New Roman")[ShopInfo.vue]],

  [#text(9pt, font: "SimHei")[店铺公告]],
  [#text(9pt, font: "SimSun")[发布店铺公告]],
  [#text(9pt, font: "Times New Roman")[ShopInfo.vue]],
)]

=== 优惠券管理

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)27#h(1.0em)商家优惠券管理功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[优惠券列表]],
  [#text(9pt, font: "SimSun")[查看店铺优惠券]],
  [#text(9pt, font: "Times New Roman")[CouponList.vue]],

  [#text(9pt, font: "SimHei")[创建优惠券]],
  [#text(9pt, font: "SimSun")[创建店铺优惠券]],
  [#text(9pt, font: "Times New Roman")[CouponList.vue]],
)]

=== 客服聊天

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)28#h(1.0em)客服聊天功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[聊天窗口]],
  [#text(9pt, font: "SimSun")[与用户实时聊天]],
  [#text(9pt, font: "Times New Roman")[Chat.vue]],

  [#text(9pt, font: "SimHei")[消息记录]],
  [#text(9pt, font: "SimSun")[查看历史聊天记录]],
  [#text(9pt, font: "Times New Roman")[Chat.vue]],

  [#text(9pt, font: "SimHei")[未读消息]],
  [#text(9pt, font: "SimSun")[显示未读消息数量]],
  [#text(9pt, font: "Times New Roman")[Chat.vue]],
)]

=== 数据统计

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)29#h(1.0em)商家数据统计功能]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[功能]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],
  [#align(center)[#text(9pt, font: "SimSun")[页面/组件]]],

  [#text(9pt, font: "SimHei")[销售统计]],
  [#text(9pt, font: "SimSun")[店铺销售数据图表]],
  [#text(9pt, font: "Times New Roman")[Statistics.vue]],

  [#text(9pt, font: "SimHei")[商品分析]],
  [#text(9pt, font: "SimSun")[商品销量排行]],
  [#text(9pt, font: "Times New Roman")[Statistics.vue]],

  [#text(9pt, font: "SimHei")[订单分析]],
  [#text(9pt, font: "SimSun")[订单状态分布]],
  [#text(9pt, font: "Times New Roman")[Statistics.vue]],
)]

= 组件架构

#h(2.0em)前端应用采用组件化架构，将应用拆分为多个可复用的组件。\
#h(2.0em)系统包含 75+ 个组件，分布在三个应用端和公共模块中。\

== 组件统计

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)30#h(1.0em)组件统计]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[应用端]]],
  [#align(center)[#text(9pt, font: "SimSun")[组件数量]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],

  [#text(9pt, font: "SimHei")[用户端]],
  [#text(9pt, font: "Times New Roman")[35+]],
  [#text(9pt, font: "SimSun")[商品、购物车、订单、用户中心等]],

  [#text(9pt, font: "SimHei")[管理后台]],
  [#text(9pt, font: "Times New Roman")[20+]],
  [#text(9pt, font: "SimSun")[用户管理、商品审核、数据统计等]],

  [#text(9pt, font: "SimHei")[商家后台]],
  [#text(9pt, font: "Times New Roman")[20+]],
  [#text(9pt, font: "SimSun")[商品管理、订单处理、客服聊天等]],

  [#text(9pt, font: "SimHei")[公共组件]],
  [#text(9pt, font: "Times New Roman")[10+]],
  [#text(9pt, font: "SimSun")[Loading、状态指示、时间显示等]],

  [#text(9pt, font: "SimHei", weight: "bold")[总计]],
  [#text(9pt, font: "Times New Roman", weight: "bold")[75+]],
  [#text(9pt, font: "SimSun")[]],
)]

== 用户端核心组件

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)31#h(1.0em)用户端核心组件]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 2fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[组件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[文件路径]]],
  [#align(center)[#text(9pt, font: "SimSun")[功能说明]]],

  [#text(9pt, font: "SimHei")[Header]],
  [#text(9pt, font: "Times New Roman")[user/components/Header.vue]],
  [#text(9pt, font: "SimSun")[顶部导航栏]],

  [#text(9pt, font: "SimHei")[Footer]],
  [#text(9pt, font: "Times New Roman")[user/components/Footer.vue]],
  [#text(9pt, font: "SimSun")[页脚信息]],

  [#text(9pt, font: "SimHei")[ProductCard]],
  [#text(9pt, font: "Times New Roman")[user/components/ProductCard.vue]],
  [#text(9pt, font: "SimSun")[商品卡片]],

  [#text(9pt, font: "SimHei")[BannerCarousel]],
  [#text(9pt, font: "Times New Roman")[user/components/BannerCarousel.vue]],
  [#text(9pt, font: "SimSun")[轮播图]],

  [#text(9pt, font: "SimHei")[CategoryPanel]],
  [#text(9pt, font: "Times New Roman")[user/components/CategoryPanel.vue]],
  [#text(9pt, font: "SimSun")[分类面板]],

  [#text(9pt, font: "SimHei")[FavoriteButton]],
  [#text(9pt, font: "Times New Roman")[user/components/FavoriteButton.vue]],
  [#text(9pt, font: "SimSun")[收藏按钮]],

  [#text(9pt, font: "SimHei")[FollowButton]],
  [#text(9pt, font: "Times New Roman")[user/components/FollowButton.vue]],
  [#text(9pt, font: "SimSun")[关注按钮]],

  [#text(9pt, font: "SimHei")[RatingStars]],
  [#text(9pt, font: "Times New Roman")[user/components/RatingStars.vue]],
  [#text(9pt, font: "SimSun")[评分星星]],

  [#text(9pt, font: "SimHei")[ReviewForm]],
  [#text(9pt, font: "Times New Roman")[user/components/ReviewForm.vue]],
  [#text(9pt, font: "SimSun")[评价表单]],

  [#text(9pt, font: "SimHei")[ReviewPanel]],
  [#text(9pt, font: "Times New Roman")[user/components/ReviewPanel.vue]],
  [#text(9pt, font: "SimSun")[评价列表]],

  [#text(9pt, font: "SimHei")[UserAvatar]],
  [#text(9pt, font: "Times New Roman")[user/components/UserAvatar.vue]],
  [#text(9pt, font: "SimSun")[用户头像]],

  [#text(9pt, font: "SimHei")[UserCredit]],
  [#text(9pt, font: "Times New Roman")[user/components/UserCredit.vue]],
  [#text(9pt, font: "SimSun")[用户积分]],

  [#text(9pt, font: "SimHei")[UserLevel]],
  [#text(9pt, font: "Times New Roman")[user/components/UserLevel.vue]],
  [#text(9pt, font: "SimSun")[用户等级]],

  [#text(9pt, font: "SimHei")[VipLevelBadge]],
  [#text(9pt, font: "Times New Roman")[user/components/VipLevelBadge.vue]],
  [#text(9pt, font: "SimSun")[VIP 徽章]],

  [#text(9pt, font: "SimHei")[CustomerService]],
  [#text(9pt, font: "Times New Roman")[user/components/CustomerService.vue]],
  [#text(9pt, font: "SimSun")[客服浮窗]],
)]

== 管理后台核心组件

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)32#h(1.0em)管理后台核心组件]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 2fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[组件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[文件路径]]],
  [#align(center)[#text(9pt, font: "SimSun")[功能说明]]],

  [#text(9pt, font: "SimHei")[Header]],
  [#text(9pt, font: "Times New Roman")[admin/components/Header.vue]],
  [#text(9pt, font: "SimSun")[顶部导航栏]],

  [#text(9pt, font: "SimHei")[Footer]],
  [#text(9pt, font: "Times New Roman")[admin/components/Footer.vue]],
  [#text(9pt, font: "SimSun")[页脚信息]],

  [#text(9pt, font: "SimHei")[CategoryPanel]],
  [#text(9pt, font: "Times New Roman")[admin/components/CategoryPanel.vue]],
  [#text(9pt, font: "SimSun")[分类面板]],
)]

== 商家后台核心组件

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)33#h(1.0em)商家后台核心组件]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 2fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[组件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[文件路径]]],
  [#align(center)[#text(9pt, font: "SimSun")[功能说明]]],

  [#text(9pt, font: "SimHei")[Header]],
  [#text(9pt, font: "Times New Roman")[merchant/components/Header.vue]],
  [#text(9pt, font: "SimSun")[顶部导航栏]],

  [#text(9pt, font: "SimHei")[Footer]],
  [#text(9pt, font: "Times New Roman")[merchant/components/Footer.vue]],
  [#text(9pt, font: "SimSun")[页脚信息]],

  [#text(9pt, font: "SimHei")[CategoryPanel]],
  [#text(9pt, font: "Times New Roman")[merchant/components/CategoryPanel.vue]],
  [#text(9pt, font: "SimSun")[分类面板]],
)]

== 公共组件

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)34#h(1.0em)公共组件]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 2fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[组件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[文件路径]]],
  [#align(center)[#text(9pt, font: "SimSun")[功能说明]]],

  [#text(9pt, font: "SimHei")[Loading]],
  [#text(9pt, font: "Times New Roman")[common/components/Loading.vue]],
  [#text(9pt, font: "SimSun")[加载动画]],

  [#text(9pt, font: "SimHei")[LoadingDot]],
  [#text(9pt, font: "Times New Roman")[common/components/LoadingDot.vue]],
  [#text(9pt, font: "SimSun")[加载点]],

  [#text(9pt, font: "SimHei")[StatusDot]],
  [#text(9pt, font: "Times New Roman")[common/components/StatusDot.vue]],
  [#text(9pt, font: "SimSun")[状态指示点]],

  [#text(9pt, font: "SimHei")[TimeInfo]],
  [#text(9pt, font: "Times New Roman")[common/components/TimeInfo.vue]],
  [#text(9pt, font: "SimSun")[时间信息]],

  [#text(9pt, font: "SimHei")[LocationInfo]],
  [#text(9pt, font: "Times New Roman")[common/components/LocationInfo.vue]],
  [#text(9pt, font: "SimSun")[位置信息]],

  [#text(9pt, font: "SimHei")[TopInfoBar]],
  [#text(9pt, font: "Times New Roman")[common/components/TopInfoBar.vue]],
  [#text(9pt, font: "SimSun")[顶部信息栏]],
)]

== 组件通信方式

```typescript
// 1. Props 向下传递
<ChildComponent :product="productData" @@add-to-cart="handleAddToCart" />

// 2. Events 向上传递
emit('add-to-cart', product)

// 3. Pinia Store 全局状态
const cartStore = useCartStore()
cartStore.addToCart(product)

// 4. 路由参数
route.params.id
route.query.keyword
```

= 数据管理

== Pinia Store

=== 购物车 Store

```typescript
// user/stores/cart.ts
export const useCartStore = defineStore('cart', () => {
  const cartItems = ref<CartItem[]>([])
  const totalCount = computed(() => ...)
  const totalPrice = computed(() => ...)

  // 方法：addToCart, removeFromCart, updateQuantity, clearCart
  return { cartItems, totalCount, totalPrice, ... }
})
```

=== 用户 Store

```typescript
// user/stores/user.ts
export const useUserStore = defineStore('user', () => {
  const userInfo = ref<UserInfo | null>(null)
  const token = ref('')
  const isLoggedIn = computed(() => !!token.value)

  // 方法：login, register, logout, fetchUserInfo
  return { userInfo, token, isLoggedIn, ... }
})
```

== 数据持久化

```typescript
// token 持久化
localStorage.setItem('token', token)

// 用户信息持久化
localStorage.setItem('user', JSON.stringify(userInfo))

// 页面加载时恢复
const savedToken = localStorage.getItem('token')
if (savedToken) {
  token.value = savedToken
  fetchUserInfo()
}
```

== API 请求封装

```typescript
// common/api/request.ts
const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器：自动添加 token
request.interceptors.request.use(config => {
  const token = localStorage.getItem('token')
  if (token) {
    config.headers.Authorization = 'Bearer ' + token
  }
  return config
})

// 响应拦截器：统一错误处理
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // 未授权，跳转登录
    }
    return Promise.reject(error)
  }
)
```

= 路由系统

== 路由守卫

```typescript
// 权限守卫
router.beforeEach((to, from, next) => {
  if (to.meta.requiresAuth && !isLoggedIn()) {
    next('/login')
  } else {
    next()
  }
})
```

== 路由配置

```typescript
// 用户端路由
const routes = [
  { path: '/', component: Home },
  { path: '/products', component: ProductList },
  { path: '/product/:id', component: ProductDetail },
  { path: '/cart', component: Cart },
  { path: '/order', component: Order },
  { path: '/login', component: Login },
  { path: '/user', component: UserCenter, meta: { requiresAuth: true } },
  // ...
]
```

= UI/UX 特性

== 响应式设计

#h(2.0em)应用支持桌面、平板、手机多设备。\
#h(2.0em)使用 Element Plus 响应式栅格系统。\
#h(2.0em)自适应布局和字体大小。\

== 视觉效果

#h(2.0em)渐变背景和阴影效果。\
#h(2.0em)卡片悬停动画。\
#h(2.0em)按钮交互反馈。\
#h(2.0em)平滑过渡动画。\

== 加载状态

#h(2.0em)Loading 组件全局加载提示。\
#h(2.0em)骨架屏占位。\
#h(2.0em)按钮 loading 状态。\

== 交互反馈

#h(2.0em)Element Plus Message 消息提示。\
#h(2.0em)Modal 确认对话框。\
#h(2.0em)表单验证实时反馈。\

= 性能优化

== 构建优化

#h(2.0em)Vite 快速构建。\
#h(2.0em)代码分割和懒加载。\
#h(2.0em)静态资源压缩。\

== 运行时优化

#h(2.0em)组件按需加载。\
#h(2.0em)图片懒加载。\
#h(2.0em)虚拟滚动长列表。\

== 缓存策略

#h(2.0em)API 响应缓存。\
#h(2.0em)静态资源 CDN。\
#h(2.0em)localStorage 数据缓存。\

= 浏览器兼容性

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)35#h(1.0em)浏览器兼容性]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr),
  stroke: 0.5pt,
  align: (left, center),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[浏览器]]],
  [#align(center)[#text(9pt, font: "SimSun")[最低版本]]],

  [#text(9pt, font: "Times New Roman")[Chrome]],
  [#text(9pt, font: "Times New Roman")[90+]],

  [#text(9pt, font: "Times New Roman")[Firefox]],
  [#text(9pt, font: "Times New Roman")[88+]],

  [#text(9pt, font: "Times New Roman")[Safari]],
  [#text(9pt, font: "Times New Roman")[14+]],

  [#text(9pt, font: "Times New Roman")[Edge]],
  [#text(9pt, font: "Times New Roman")[90+]],

  [#text(9pt, font: "Times New Roman")[IE]],
  [#text(9pt, font: "SimSun")[不支持]],
)]

= 版本历史

== 版本#h(0.25em)1.0.0

#h(2.0em)系统初始版本，实现基本的电商功能。\
#h(2.0em)包括商品浏览、购物车、订单和用户系统。\

== 版本#h(0.25em)1.1.0

#h(2.0em)优化系统性能，修复已知问题。\
#h(2.0em)改进用户界面，提升用户体验。\

== 版本#h(0.25em)2.0.0

#h(2.0em)重构系统架构，新增会员功能和优惠活动功能。\
#h(2.0em)优化组件结构，提高代码可维护性。\

#v(2.0em)

// 版本历史结束，添加水平居中的分割线
#align(center)[#line(length: 25%, stroke: 0.5pt)]

#v(2.0em)

#align(center)[#text(9pt, font: "SimSun")[文档版本：v1.1.0    更新日期：2026-03-26]]
