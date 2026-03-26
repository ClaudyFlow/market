// 第 1 页开始
#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

// 封面标题：标题前间距 15mm，标题后间距 10mm，水平居中

#v(85mm)

#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  前端接口文档]]

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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——前端接口文档]]\
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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——前端接口文档]]\
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

#h(2.0em)本前端接口文档旨在为开发人员提供详细的购物商城系统前端应用接口说明。\
#h(2.0em)文档涵盖了组件接口规范、数据接口规范、本地存储接口以及部署配置相关接口。\
#h(2.0em)购物商城系统是一个基于 Vue#h(0.25em)3 的现代化在线购物商城前端应用，采用组件化架构，使用 Nginx 作为 Web 服务器，支持完整的电商购物流程。\
#h(2.0em)通过本接口文档，开发人员将能够全面了解前端应用的组件设计、数据流转、状态管理和部署配置，便于进行功能扩展和维护优化。\
#h(2.0em)我们希望本接口文档能够帮助开发人员快速理解前端应用的架构设计和接口规范，提高开发效率和代码质量。\
#h(2.0em)本接口文档适用于具备一定前端开发基础的人员使用，读者应熟悉 Vue#h(0.25em)3 组件化开发、#h(0.25em)JavaScript 响应式编程、#h(0.25em)Nginx 配置等基础知识。\
#h(2.0em)在开始使用前，请仔细阅读本文档，确保理解每个接口的定义和使用方法。\
#h(2.0em)如遇到本文档未覆盖的问题或有任何疑问，请及时联系技术支持团队。\
#h(2.0em)我们将持续更新和完善本接口文档，以帮助用户更好地使用购物商城系统前端应用。\

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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——前端接口文档]]\
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
#align(center)[#text(16pt, font: "SimHei")[前端接口文档]]
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

= 概述

#h(2.0em)购物商城系统前端接口文档详细描述了前端应用的组件接口、数据结构和事件系统。\
#h(2.0em)购物商城系统采用纯前端开发模式，无需构建工具，直接在浏览器中运行，使用 Nginx 作为 Web 服务器提供静态文件服务。\
#h(2.0em)前端应用包含五个核心组件：Navbar（导航栏）、#h(0.25em)ProductCard（商品卡片）、#h(0.25em)CartItem（购物车商品）、#h(0.25em)OrderCard（订单卡片）、#h(0.25em)Modal（模态框）。\
#h(2.0em)应用使用 localStorage 进行数据持久化，包括购物车数据和用户登录状态。\
#h(2.0em)本文档面向开发人员，提供了完整的接口规范说明，包括组件 Props 和 Events 定义、数据结构规范、本地存储接口以及部署配置说明。\

== 系统架构

#h(2.0em)购物商城系统前端采用以下技术架构：\
#h(2.0em)——前端框架：Vue#h(0.25em)3#h(0.25em)CDN 版本，使用组合式 API 进行组件开发；\
#h(2.0em)——Web 服务器：Nginx#h(0.25em)1.18 及以上，提供静态文件服务和反向代理；\
#h(2.0em)——图标库：Font#h(0.25em)Awesome#h(0.25em)6，提供丰富的矢量图标；\
#h(2.0em)——字体：Google#h(0.25em)Fonts-Inter，现代无衬线字体；\
#h(2.0em)——数据存储：localStorage，用于购物车和用户数据持久化；\
#h(2.0em)——样式技术：CSS3#h(0.25em)+#h(0.25em)SCSS，支持嵌套规则、变量和混合宏。\

== 端口配置

#h(2.0em)购物商城系统前端使用以下端口配置：\
#h(2.0em)——80 端口：Http 访问，默认端口；\
#h(2.0em)——443 端口：Https 访问（可选，建议生产环境启用）；\
#h(2.0em)——访问地址：http://localhost 或 https://localhost。\

== 组件概览

#h(2.0em)前端应用包含五个核心组件，每个组件负责特定的功能：\
#h(2.0em)——Navbar 组件：提供全局导航、搜索功能和用户菜单；\
#h(2.0em)——ProductCard 组件：展示单个商品信息，支持添加到购物车；\
#h(2.0em)——CartItem 组件：展示购物车中的商品，支持数量调整和移除；\
#h(2.0em)——OrderCard 组件：展示订单信息，支持查看详情；\
#h(2.0em)——#h(0.25em)Modal 组件：通用的弹窗容器，用于登录和注册。\

= 组件接口规范

== Navbar 组件（导航栏）

#h(2.0em)Navbar 组件是应用的入口，提供全局导航和用户交互功能。\
#h(2.0em)该组件包括导航菜单切换、搜索功能、购物车数量显示和用户菜单等功能。\

=== Props（属性）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)1#h(1.0em)Navbar 组件 Props]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[属性名]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[必填]]],
  [#align(center)[#text(9pt, font: "SimSun")[默认值]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[activeTab]],
  [#text(9pt, font: "Times New Roman")[String]],
  [#text(9pt, font: "SimSun")[是]],
  [#text(9pt, font: "Times New Roman")['home']],
  [#text(9pt, font: "SimSun")[当前激活的标签页]],

  [#text(9pt, font: "Times New Roman")[cartCount]],
  [#text(9pt, font: "Times New Roman")[Number]],
  [#text(9pt, font: "SimSun")[是]],
  [#text(9pt, font: "Times New Roman")[0]],
  [#text(9pt, font: "SimSun")[购物车商品数量]],

  [#text(9pt, font: "Times New Roman")[isLoggedIn]],
  [#text(9pt, font: "Times New Roman")[Boolean]],
  [#text(9pt, font: "SimSun")[是]],
  [#text(9pt, font: "Times New Roman")[false]],
  [#text(9pt, font: "SimSun")[用户登录状态]],

  [#text(9pt, font: "Times New Roman")[user]],
  [#text(9pt, font: "Times New Roman")[Object]],
  [#text(9pt, font: "SimSun")[是]],
  [#text(9pt, font: "Times New Roman")[{name: ''}]],
  [#text(9pt, font: "SimSun")[用户信息对象]],
)]

=== Events（事件）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)2#h(1.0em)Navbar 组件 Events]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[事件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[参数]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[tab-change]],
  [#text(9pt, font: "Times New Roman")[tab]],
  [#text(9pt, font: "Times New Roman")[String]],
  [#text(9pt, font: "SimSun")[标签页切换事件]],

  [#text(9pt, font: "Times New Roman")[search]],
  [#text(9pt, font: "Times New Roman")[query]],
  [#text(9pt, font: "Times New Roman")[String]],
  [#text(9pt, font: "SimSun")[搜索事件]],

  [#text(9pt, font: "Times New Roman")[login]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "SimSun")[登录按钮点击事件]],

  [#text(9pt, font: "Times New Roman")[register]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "SimSun")[注册按钮点击事件]],

  [#text(9pt, font: "Times New Roman")[view-profile]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "SimSun")[查看个人资料事件]],

  [#text(9pt, font: "Times New Roman")[logout]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "SimSun")[退出登录事件]],
)]

=== 内部状态

```javascript
data() {
  return {
    searchQuery: '',    // 搜索框内容
    showUserMenu: false // 用户菜单显示状态
  };
}
```

== ProductCard 组件（商品卡片）

#h(2.0em)ProductCard 组件用于显示单个商品信息，支持悬停效果和添加到购物车功能。\
#h(2.0em)商品卡片包含商品图片、名称、价格和评分信息。\

=== Props（属性）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)3#h(1.0em)ProductCard 组件 Props]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[属性名]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[必填]]],
  [#align(center)[#text(9pt, font: "SimSun")[默认值]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[product]],
  [#text(9pt, font: "Times New Roman")[Object]],
  [#text(9pt, font: "SimSun")[是]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "SimSun")[商品数据对象]],

  [#text(9pt, font: "Times New Roman")[showCategory]],
  [#text(9pt, font: "Times New Roman")[Boolean]],
  [#text(9pt, font: "SimSun")[否]],
  [#text(9pt, font: "Times New Roman")[true]],
  [#text(9pt, font: "SimSun")[是否显示商品分类]],
)]

=== Events（事件）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)4#h(1.0em)ProductCard 组件 Events]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[事件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[参数]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[add-to-cart]],
  [#text(9pt, font: "Times New Roman")[product]],
  [#text(9pt, font: "Times New Roman")[Object]],
  [#text(9pt, font: "SimSun")[添加到购物车事件]],
)]

=== 商品对象结构

```javascript
{
  id: Number,           // 商品 ID
  name: String,         // 商品名称
  description: String,  // 商品描述
  price: Number,        // 商品价格
  category: String,     // 商品分类
  rating: Number,       // 商品评分 (0-5)
  image: String         // 商品图片 URL
}
```

== CartItem 组件（购物车商品）

#h(2.0em)CartItem 组件用于显示购物车中的单个商品，支持数量调整和移除功能。\
#h(2.0em)购物车商品组件实时计算商品总价。\

=== Props（属性）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)5#h(1.0em)CartItem 组件 Props]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[属性名]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[必填]]],
  [#align(center)[#text(9pt, font: "SimSun")[默认值]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[item]],
  [#text(9pt, font: "Times New Roman")[Object]],
  [#text(9pt, font: "SimSun")[是]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "SimSun")[购物车商品对象]],
)]

=== Events（事件）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)6#h(1.0em)CartItem 组件 Events]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[事件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[参数]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[update-quantity]],
  [#text(9pt, font: "Times New Roman")[item]],
  [#text(9pt, font: "Times New Roman")[Object]],
  [#text(9pt, font: "SimSun")[更新商品数量]],

  [#text(9pt, font: "Times New Roman")[change]],
  [#text(9pt, font: "Times New Roman")[Number]],
  [#text(9pt, font: "SimSun")[数量变化值]],

  [#text(9pt, font: "Times New Roman")[remove-item]],
  [#text(9pt, font: "Times New Roman")[item]],
  [#text(9pt, font: "Times New Roman")[Object]],
  [#text(9pt, font: "SimSun")[移除商品]],
)]

=== 购物车商品对象结构

```javascript
{
  id: Number,           // 购物车项 ID
  product: Object,      // 商品对象
  quantity: Number       // 商品数量
}
```

== OrderCard 组件（订单卡片）

#h(2.0em)OrderCard 组件用于显示单个订单信息，支持订单状态显示和详情查看功能。\
#h(2.0em)订单状态包括：待处理、处理中、已发货、已送达。\

=== Props（属性）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)7#h(1.0em)OrderCard 组件 Props]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[属性名]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[必填]]],
  [#align(center)[#text(9pt, font: "SimSun")[默认值]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[order]],
  [#text(9pt, font: "Times New Roman")[Object]],
  [#text(9pt, font: "SimSun")[是]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "SimSun")[订单数据对象]],
)]

=== Events（事件）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)8#h(1.0em)OrderCard 组件 Events]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[事件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[参数]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[view-details]],
  [#text(9pt, font: "Times New Roman")[order]],
  [#text(9pt, font: "Times New Roman")[Object]],
  [#text(9pt, font: "SimSun")[查看订单详情]],
)]

=== 订单对象结构

```javascript
{
  id: String,           // 订单号 (格式：ORD YYYYMMDD001)
  date: String,         // 订单日期 (YYYY-MM-DD)
  status: String,       // 订单状态
  items: Array,         // 订单商品列表
  total: Number          // 订单总额
}
```

=== 订单状态映射

```javascript
const orderStatusMap = {
  pending: '待处理',
  processing: '处理中',
  shipped: '已发货',
  delivered: '已送达'
};
```

== Modal 组件（模态框）

#h(2.0em)Modal 组件是通用的弹窗容器，支持遮罩层、关闭功能和标题显示。\
#h(2.0em)模态框用于登录和注册功能。\

=== Props（属性）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)9#h(1.0em)Modal 组件 Props]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[属性名]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[必填]]],
  [#align(center)[#text(9pt, font: "SimSun")[默认值]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[show]],
  [#text(9pt, font: "Times New Roman")[Boolean]],
  [#text(9pt, font: "SimSun")[是]],
  [#text(9pt, font: "Times New Roman")[false]],
  [#text(9pt, font: "SimSun")[是否显示模态框]],

  [#text(9pt, font: "Times New Roman")[title]],
  [#text(9pt, font: "Times New Roman")[String]],
  [#text(9pt, font: "SimSun")[否]],
  [#text(9pt, font: "Times New Roman")['模态框']],
  [#text(9pt, font: "SimSun")[模态框标题]],
)]

=== Events（事件）

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)10#h(1.0em)Modal 组件 Events]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[事件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[参数]]],
  [#align(center)[#text(9pt, font: "SimSun")[类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],

  [#text(9pt, font: "Times New Roman")[close]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "Times New Roman")[—]],
  [#text(9pt, font: "SimSun")[关闭模态框事件]],
)]

= 数据接口规范

== 商品数据接口

#h(2.0em)商品数据接口提供商品列表、商品分类、商品筛选和商品搜索功能。\

=== 数据结构

```javascript
// 商品列表
const products = [
  {
    id: 1,
    name: '智能手机 Pro',
    description: '高性能智能手机，6.7 英寸屏幕，256GB 存储',
    price: 5999,
    category: '电子产品',
    rating: 4.8,
    image: 'https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&h=300&fit=crop'
  },
  // ...更多商品
];

// 商品分类
const categories = [
  '电子产品',
  '服装鞋帽',
  '配饰',
  '家用电器',
  '运动户外'
];
```

=== 筛选接口

```javascript
// 按分类筛选
const filteredByCategory = products.filter(p => p.category === selectedCategory);

// 按价格排序
const sortedByPrice = products.sort((a, b) => a.price - b.price);  // 升序
const sortedByPriceDesc = products.sort((a, b) => b.price - a.price);  // 降序

// 按评分排序
const sortedByRating = products.sort((a, b) => b.rating - a.rating);

// 搜索商品
const searchResults = products.filter(p =>
  p.name.toLowerCase().includes(query) ||
  p.description.toLowerCase().includes(query) ||
  p.category.toLowerCase().includes(query)
);
```

== 购物车数据接口

#h(2.0em)购物车数据接口提供购物车商品管理、数量调整和总价计算功能。\

=== 数据结构

```javascript
// 购物车项
const cartItem = {
  id: 1698912345678,  // 时间戳 ID
  product: {...},     // 商品对象
  quantity: 1         // 商品数量
};
```

=== 购物车操作

```javascript
// 购物车操作
const cartOperations = {
  // 添加商品到购物车
  addToCart(product) {
    const existing = cartItems.find(item => item.product.id === product.id);
    if (existing) {
      existing.quantity++;
    } else {
      cartItems.push({
        id: Date.now(),
        product: {...product},
        quantity: 1
      });
    }
  },

  // 更新商品数量
  updateQuantity(item, change) {
    item.quantity += change;
    if (item.quantity < 1) {
      this.removeFromCart(item);
    }
  },

  // 移除商品
  removeFromCart(item) {
    const index = cartItems.findIndex(cartItem => cartItem.id === item.id);
    if (index !== -1) {
      cartItems.splice(index, 1);
    }
  },

  // 清空购物车
  clearCart() {
    cartItems = [];
  }
};
```

=== 计算属性

```javascript
// 购物车商品总数
const cartCount = cartItems.reduce((total, item) => total + item.quantity, 0);

// 购物车总价
const cartTotal = cartItems.reduce((total, item) =>
  total + (item.product.price * item.quantity), 0);

// 运费计算 (满 100 免运费)
const shippingFee = cartTotal >= 100 ? 0 : 15;

// 订单总额
const orderTotal = cartTotal + shippingFee;
```

== 订单数据接口

#h(2.0em)订单数据接口提供订单创建、订单查看和订单状态跟踪功能。\

=== 数据结构

```javascript
// 订单对象
const order = {
  id: 'ORD20260303001',  // 订单号
  date: '2026-03-03',     // 下单日期
  status: 'processing',  // 订单状态
  items: [              // 订单商品
    {
      product: {...},    // 商品对象
      quantity: 2        // 商品数量
    }
  ],
  total: 8999            // 订单总额
};
```

=== 订单操作

```javascript
// 创建订单
function createOrder(cartItems) {
  const orderId = `ORD${new Date().getFullYear()}${String(new Date().getMonth() + 1).padStart(2, '0')}${String(new Date().getDate()).padStart(2, '0')}${String(orders.length + 1).padStart(3, '0')}`;

  return {
    id: orderId,
    date: new Date().toISOString().split('T')[0],
    status: 'processing',
    items: [...cartItems],
    total: cartTotal + shippingFee
  };
}

// 获取订单状态文本
function getOrderStatusText(status) {
  return orderStatusMap[status] || status;
}
```

== 用户数据接口

#h(2.0em)用户数据接口提供用户登录、注册和个人信息管理功能。\

=== 数据结构

```javascript
// 用户对象
const user = {
  name: '张三',
  email: 'zhangsan@example.com'
};

// 登录数据
const loginData = {
  username: '',
  password: ''
};

// 注册数据
const registerData = {
  username: '',
  email: '',
  password: '',
  confirmPassword: ''
};
```

=== 用户操作

```javascript
// 登录验证
function validateLogin(loginData) {
  if (!loginData.username || !loginData.password) {
    return {valid: false, message: '请输入用户名和密码'};
  }
  return {valid: true};
}

// 注册验证
function validateRegister(registerData) {
  if (!registerData.username || !registerData.email || !registerData.password) {
    return {valid: false, message: '请填写所有必填字段'};
  }

  if (registerData.password !== registerData.confirmPassword) {
    return {valid: false, message: '两次输入的密码不一致'};
  }

  return {valid: true};
}
```

= 本地存储接口

== localStorage 键值规范

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)11#h(1.0em)localStorage 键值规范]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 2fr, 1fr),
  stroke: 0.5pt,
  align: (left, center, left, center),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[键名]]],
  [#align(center)[#text(9pt, font: "SimSun")[数据类型]]],
  [#align(center)[#text(9pt, font: "SimSun")[描述]]],
  [#align(center)[#text(9pt, font: "SimSun")[有效期]]],

  [#text(9pt, font: "Times New Roman")[market_cart]],
  [#text(9pt, font: "Times New Roman")[JSON 字符串]],
  [#text(9pt, font: "SimSun")[购物车数据]],
  [#text(9pt, font: "SimSun")[永久]],

  [#text(9pt, font: "Times New Roman")[market_user]],
  [#text(9pt, font: "Times New Roman")[JSON 字符串]],
  [#text(9pt, font: "SimSun")[用户登录信息]],
  [#text(9pt, font: "SimSun")[永久]],
)]

== 数据持久化接口

```javascript
// 保存购物车数据
function saveCartData(cartItems) {
  try {
    localStorage.setItem('market_cart', JSON.stringify(cartItems));
    return {success: true};
  } catch (error) {
    console.error('保存购物车数据失败:', error);
    return {success: false, error};
  }
}

// 加载购物车数据
function loadCartData() {
  try {
    const saved = localStorage.getItem('market_cart');
    return saved ? JSON.parse(saved) : [];
  } catch (error) {
    console.error('加载购物车数据失败:', error);
    return [];
  }
}

// 保存用户数据
function saveUserData(user, isLoggedIn) {
  try {
    if (isLoggedIn) {
      localStorage.setItem('market_user', JSON.stringify(user));
    } else {
      localStorage.removeItem('market_user');
    }
    return {success: true};
  } catch (error) {
    console.error('保存用户数据失败:', error);
    return {success: false, error};
  }
}

// 加载用户数据
function loadUserData() {
  try {
    const saved = localStorage.getItem('market_user');
    if (saved) {
      const userData = JSON.parse(saved);
      return {
        isLoggedIn: true,
        user: userData
      };
    }
    return {
      isLoggedIn: false,
      user: {name: '', email: ''}
    };
  } catch (error) {
    console.error('加载用户数据失败:', error);
    return {
      isLoggedIn: false,
      user: {name: '', email: ''}
    };
  }
}
```

== 数据同步机制

```javascript
// 页面加载时恢复数据
onMounted(() => {
  const cartData = loadCartData();
  const userData = loadUserData();

  cartItems.value = cartData;
  isLoggedIn.value = userData.isLoggedIn;
  user.value = userData.user || {name: '', email: ''};
});

// 监听数据变化自动保存
watch(cartItems, (newCartItems) => {
  saveCartData(newCartItems);
}, {deep: true});

watch([isLoggedIn, user], () => {
  saveUserData(user.value, isLoggedIn.value);
}, {deep: true});
```

= 部署配置接口

== 服务器配置

购物商城系统前端使用 Nginx 作为 Web 服务器，提供静态文件服务。\
Nginx 配置文件位于 frontend/nginx/conf/nginx.conf。

=== Nginx 配置

```nginx
// 基础配置
server {
  listen       80;
  server_name  localhost;

  # 静态文件服务
  location / {
    root   html;
    index  index.html index.htm;
  }

  # 错误页面
  error_page   500 502 503 504  /50x.html;
  location = /50x.html {
    root   html;
  }
}
```

=== 文件结构

#h(2.0em)前端应用的文件结构如下：\
#h(2.0em)#h(0.5em)frontend/nginx/html\
#h(2.0em)├──#h(0.5em)index.html#h(1.0em)主页面\
#h(2.0em)├──#h(0.5em)main.js#h(1.0em)Vue 主应用\
#h(2.0em)├──#h(0.5em)styles.css#h(1.0em)样式文件\
#h(2.0em)├──#h(0.5em)components/#h(1.0em)Vue 组件目录\
#h(2.0em)└──#h(0.5em)favicon.ico#h(1.0em)网站图标\

== 端口配置接口

#h(2.0em)系统需要开放以下端口：\
#h(2.0em)——80 端口：Http 访问；\
#h(2.0em)——443 端口：Https 访问（可选）；\
#h(2.0em)——3306 端口：数据库访问（如果连接后端）；\
#h(2.0em)——6379 端口：#h(0.25em)Redis 访问（如果使用缓存）。\

=== Https 配置

#h(2.0em)建议配置 SSL 证书，启用 Https 访问，提高数据传输安全性。\
#h(2.0em)建议使用 Let's#h(0.25em)Encrypt 免费证书或购买企业级证书。\

== 访问地址

#h(2.0em)购物商城系统前端的访问地址配置如下：\
#h(2.0em)——本地开发：http://localhost；\
#h(2.0em)——生产环境：https://market.com（示例）；\
#h(2.0em)——测试环境：#h(0.25em)https://test.market.com（示例）。\

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

// 第 6 页结束
#pagebreak()
// 第 7 页开始

// 切换到阿拉伯数字页码，设置页眉页脚
#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——前端接口文档]]\
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
#counter(page).update(7)

// 正文大标题：标题前间距 15mm，标题后间距 10mm，三号黑体居中对齐
#v(15mm)
#align(center)[#text(16pt, font: "SimHei")[补充接口规范]]
#v(10mm)

= 技术栈修正

#h(2.0em)本节补充实际项目中使用的技术栈和版本信息。\

== 核心技术栈

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)12#h(1.0em)核心技术栈]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[技术]]],
  [#align(center)[#text(9pt, font: "SimSun")[版本]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],

  [#text(9pt, font: "Times New Roman")[Vue]],
  [#text(9pt, font: "Times New Roman")[3.4+]],
  [#text(9pt, font: "SimSun")[组合式 API，响应式框架]],

  [#text(9pt, font: "Times New Roman")[TypeScript]],
  [#text(9pt, font: "Times New Roman")[5.9+]],
  [#text(9pt, font: "SimSun")[类型安全的 JavaScript 超集]],

  [#text(9pt, font: "Times New Roman")[Vite]],
  [#text(9pt, font: "Times New Roman")[5.0+]],
  [#text(9pt, font: "SimSun")[下一代前端构建工具]],

  [#text(9pt, font: "Times New Roman")[Pinia]],
  [#text(9pt, font: "Times New Roman")[2.1+]],
  [#text(9pt, font: "SimSun")[Vue 3 官方状态管理库]],

  [#text(9pt, font: "Times New Roman")[Vue Router]],
  [#text(9pt, font: "Times New Roman")[4.2+]],
  [#text(9pt, font: "SimSun")[官方路由管理器]],

  [#text(9pt, font: "Times New Roman")[Element Plus]],
  [#text(9pt, font: "Times New Roman")[2.4+]],
  [#text(9pt, font: "SimSun")[Vue 3 UI 组件库]],

  [#text(9pt, font: "Times New Roman")[Axios]],
  [#text(9pt, font: "Times New Roman")[1.6+]],
  [#text(9pt, font: "SimSun")[HTTP 客户端]],

  [#text(9pt, font: "Times New Roman")[ECharts]],
  [#text(9pt, font: "Times New Roman")[5.5+]],
  [#text(9pt, font: "SimSun")[数据可视化图表库]],
)]

== 项目结构

```
frontend/
├── src/
│   ├── user/           # 用户端应用
│   │   ├── api/        # API 接口
│   │   ├── components/ # 组件
│   │   ├── data/       # 静态数据
│   │   ├── router/     # 路由配置
│   │   ├── stores/     # Pinia 状态管理
│   │   ├── types/      # TypeScript 类型定义
│   │   ├── util/       # 工具函数
│   │   ├── views/      # 页面视图
│   │   ├── App.vue     # 根组件
│   │   └── main.ts     # 入口文件
│   ├── admin/          # 管理后台
│   │   ├── api/
│   │   ├── components/
│   │   ├── data/
│   │   ├── router/
│   │   ├── views/
│   │   ├── App.vue
│   │   └── main.ts
│   ├── merchant/       # 商家后台
│   │   ├── api/
│   │   ├── components/
│   │   ├── data/
│   │   ├── router/
│   │   ├── views/
│   │   ├── App.vue
│   │   └── main.ts
│   └── common/         # 公共模块
│       ├── api/        # 公共 API
│       ├── components/ # 公共组件
│       ├── stores/     # 公共状态
│       ├── util/       # 公共工具
│       └── assets/     # 静态资源
├── index.html          # 用户端入口
├── admin.html          # 管理后台入口
├── merchant.html       # 商家后台入口
├── package.json
├── tsconfig.json
└── vite.config.ts
```

== 端口配置

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)13#h(1.0em)端口配置]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 1fr, 2fr),
  stroke: 0.5pt,
  align: (left, center, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[环境]]],
  [#align(center)[#text(9pt, font: "SimSun")[端口]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],

  [#text(9pt, font: "Times New Roman")[开发环境]],
  [#text(9pt, font: "Times New Roman")[5173]],
  [#text(9pt, font: "SimSun")[Vite dev server]],

  [#text(9pt, font: "Times New Roman")[生产环境]],
  [#text(9pt, font: "Times New Roman")[80]],
  [#text(9pt, font: "SimSun")[Nginx 静态文件服务]],

  [#text(9pt, font: "Times New Roman")[后端 API]],
  [#text(9pt, font: "Times New Roman")[8080]],
  [#text(9pt, font: "SimSun")[Spring Boot 服务]],
)]

= 状态管理接口

== 购物车 Store

```typescript
interface CartItem {
  id: number
  name: string
  price: number
  quantity: number
  image?: string
  [key: string]: unknown
}

// Store 接口
export const useCartStore = defineStore('cart', () => {
  const cartItems: Ref<CartItem[]> = ref([])
  const totalCount = computed(() => ...)  // 商品总数
  const totalPrice = computed(() => ...)  // 商品总价

  function addToCart(product: CartItem): void
  function removeFromCart(productId: number): void
  function updateQuantity(productId: number, quantity: number): void
  function clearCart(): void

  return { cartItems, totalCount, totalPrice, addToCart, removeFromCart, updateQuantity, clearCart }
})
```

== 用户 Store

```typescript
interface UserInfo {
  id: number
  name: string
  email: string
  avatarUrl?: string
  points?: number
  vipLevel?: number
}

// Store 接口
export const useUserStore = defineStore('user', () => {
  const userInfo: Ref<UserInfo | null> = ref(null)
  const token: Ref<string> = ref('')
  const isLoggedIn = computed(() => !!token.value)

  function login(credentials: LoginRequest): Promise<void>
  function register(data: RegisterRequest): Promise<void>
  function logout(): void
  function fetchUserInfo(): Promise<void>

  return { userInfo, token, isLoggedIn, login, register, logout, fetchUserInfo }
})
```

= API 接口规范

== 认证接口

```typescript
// 登录
POST /api/auth/login
Request: { name: string, password: string }
Response: { token: string, user: UserInfo }

// 注册
POST /api/auth/register
Request: {
  name: string,
  email: string,
  password: string,
  confirmPassword: string,
  verificationCode?: string
}
Response: { success: boolean, message?: string }

// 获取当前用户信息
GET /api/auth/me
Response: UserInfo

// 退出登录
POST /api/auth/logout
```

== 商品接口

```typescript
// 获取商品列表
GET /api/product
Query: {
  page?: number,
  size?: number,
  keyword?: string,
  categoryId?: number,
  sortBy?: string,
  order?: 'asc' | 'desc'
}
Response: {
  content: Product[],
  totalElements: number,
  totalPages: number,
  size: number,
  number: number
}

// 获取商品详情
GET /api/product/:id
Response: Product

// 创建商品
POST /api/product
Request: ProductCreateRequest

// 更新商品
PUT /api/product/:id
Request: ProductUpdateRequest

// 删除商品
DELETE /api/product/:id
```

== 购物车接口

```typescript
// 获取购物车
GET /api/cart
Response: CartItem[]

// 添加商品到购物车
POST /api/cart/items
Request: { productId: number, quantity: number }
Response: CartItem

// 更新购物车商品数量
PUT /api/cart/items/:id
Request: { quantity: number }
Response: CartItem

// 删除购物车商品
DELETE /api/cart/items/:id
```

== 订单接口

```typescript
// 获取订单列表
GET /api/order
Query: {
  page?: number,
  size?: number,
  status?: string
}
Response: {
  content: Order[],
  totalElements: number,
  ...
}

// 获取订单详情
GET /api/order/:id
Response: OrderDetail

// 创建订单
POST /api/order
Request: {
  items: Array<{ productId: number, quantity: number }>,
  addressId: number,
  remark?: string
}
Response: Order

// 取消订单
PUT /api/order/:id/cancel
Request: { reason: string }

// 确认收货
PUT /api/order/:id/confirm
```

== 积分接口

```typescript
// 获取积分余额
GET /api/credit
Response: { credit: number }

// 获取积分历史
GET /api/credit/history
Query: { page?: number, size?: number }
Response: { content: PointsHistory[], ... }

// 签到
POST /api/credit/checkin
Response: { points: number, totalPoints: number }
```

== 优惠券接口

```typescript
// 获取可领取优惠券列表
GET /api/coupon/available
Response: Coupon[]

// 领取优惠券
POST /api/coupon/:id/claim
Response: UserCoupon

// 获取我的优惠券
GET /api/coupon/my
Query: { status?: 'UNUSED' | 'USED' | 'EXPIRED' }
Response: UserCoupon[]

// 使用优惠券
POST /api/coupon/:id/use
Request: { orderId: number }
```

== 收藏接口

```typescript
// 获取收藏列表
GET /api/favorite
Response: Product[]

// 添加收藏
POST /api/favorite/:productId
Response: Favorite

// 取消收藏
DELETE /api/favorite/:productId
```

== 关注接口

```typescript
// 获取关注列表
GET /api/follow
Response: Shop[]

// 添加关注
POST /api/follow/:shopId
Response: Follow

// 取消关注
DELETE /api/follow/:shopId
```

== 评价接口

```typescript
// 获取商品评价
GET /api/review/product/:productId
Query: { page?: number, size?: number, rating?: number }
Response: { content: Review[], ... }

// 创建评价
POST /api/review
Request: {
  productId: number,
  orderId: number,
  rating: number,
  content?: string,
  images?: string[]
}

// 回复评价
POST /api/review/:id/reply
Request: { content: string }
```

== VIP 接口

```typescript
// 获取 VIP 信息
GET /api/vip/info
Response: {
  vipLevel: number,
  growthValue: number,
  expireTime: string,
  benefits: VipBenefit[]
}

// VIP 升级
POST /api/vip/upgrade
```

== 抽奖接口

```typescript
// 抽奖
POST /api/lottery/draw
Response: {
  prizeId: number,
  prizeName: string,
  prizeType: number,
  cost: number,
  remainingCredit: number
}

// 获取抽奖记录
GET /api/lottery/records
Response: LotteryRecord[]

// 获取奖品列表
GET /api/lottery/prizes
Response: LotteryPrize[]
```

== 聊天接口

```typescript
// 获取聊天记录
GET /api/chat/conversation/:otherUserId
Query: { page?: number, size?: number }
Response: ChatMessage[]

// 获取未读消息
GET /api/chat/unread
Response: ChatMessage[]

// 获取未读消息数量
GET /api/chat/unread/count
Response: number

// 标记消息为已读
POST /api/chat/mark-read/:senderId

// 发送消息 (HTTP 备用接口)
POST /api/chat/send
Request: {
  receiverId: number,
  content: string,
  type: 'TEXT' | 'IMAGE' | 'FILE'
}
```

= 数据结构定义

== 商品对象

```typescript
interface Product {
  id: number
  name: string
  description?: string
  price: number
  originalPrice?: number
  stock: number
  imageUrl?: string
  images?: string[]
  categoryId?: number
  categoryName: string
  brand?: string
  salesCount: number
  available: boolean
  status: 'PENDING' | 'APPROVED' | 'REJECTED'
  ratingAvg: number
  ratingCount: number
  createdAt: string
  updatedAt: string
}
```

== 订单对象

```typescript
interface Order {
  id: number
  orderNo: string
  userId: number
  totalAmount: number
  discountAmount?: number
  shippingFee?: number
  actualAmount: number
  status: 'PENDING' | 'PAID' | 'SHIPPED' | 'COMPLETED' | 'CANCELLED'
  paymentMethod?: string
  paymentTime?: string
  shippingAddress?: string
  receiverName?: string
  receiverPhone?: string
  remark?: string
  cancelReason?: string
  completedAt?: string
  createdAt: string
  updatedAt: string
}
```

== 用户对象

```typescript
interface User {
  id: number
  name: string
  email?: string
  avatarUrl?: string
  points: number
  totalPoints: number
  consumedPoints: number
  vipLevel: number
  vipExpireTime?: string
  growthValue: number
  consecutiveCheckinDays: number
  lastCheckinTime?: string
  createdAt: string
}
```

= 组件接口

== ProductCard 组件

```vue
<!-- props -->
product: Product  // 商品数据对象

<!-- events -->
@click: (product: Product) => void
@add-to-cart: (product: Product) => void
```

== Header 组件

```vue
<!-- props -->
isLoggedIn: boolean
user: UserInfo | null
cartCount: number

<!-- events -->
@search: (query: string) => void
@login: () => void
@logout: () => void
@view-profile: () => void
```

== 公共组件列表

#align(center)[#text(10.5pt, font: "SimHei")[表#h(0.25em)14#h(1.0em)公共组件列表]]

#v(0.5em)

#align(center)[#table(
  columns: (1fr, 2fr, 2fr),
  stroke: 0.5pt,
  align: (left, left, left),
  fill: (x, y) => rgb("ffffff"),
  [#align(center)[#text(9pt, font: "SimSun")[组件名]]],
  [#align(center)[#text(9pt, font: "SimSun")[路径]]],
  [#align(center)[#text(9pt, font: "SimSun")[说明]]],

  [#text(9pt, font: "Times New Roman")[Loading]],
  [#text(9pt, font: "Times New Roman")[common/components/Loading.vue]],
  [#text(9pt, font: "SimSun")[加载动画组件]],

  [#text(9pt, font: "Times New Roman")[StatusDot]],
  [#text(9pt, font: "Times New Roman")[common/components/StatusDot.vue]],
  [#text(9pt, font: "SimSun")[状态指示点]],

  [#text(9pt, font: "Times New Roman")[TimeInfo]],
  [#text(9pt, font: "Times New Roman")[common/components/TimeInfo.vue]],
  [#text(9pt, font: "SimSun")[时间信息显示]],

  [#text(9pt, font: "Times New Roman")[LocationInfo]],
  [#text(9pt, font: "Times New Roman")[common/components/LocationInfo.vue]],
  [#text(9pt, font: "SimSun")[位置信息显示]],

  [#text(9pt, font: "Times New Roman")[TopInfoBar]],
  [#text(9pt, font: "Times New Roman")[common/components/TopInfoBar.vue]],
  [#text(9pt, font: "SimSun")[顶部信息栏]],
)]

= 请求封装

== Axios 实例配置

```typescript
// common/api/request.ts
import axios from 'axios'

const request = axios.create({
  baseURL: '/api',
  timeout: 15000
})

// 请求拦截器
request.interceptors.request.use(
  config => {
    const token = localStorage.getItem('token')
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  error => Promise.reject(error)
)

// 响应拦截器
request.interceptors.response.use(
  response => response.data,
  error => {
    if (error.response?.status === 401) {
      // 未授权，跳转登录
    }
    return Promise.reject(error)
  }
)

export default request
```

= 路由配置

== 用户端路由

```typescript
const routes = [
  { path: '/', component: Home, name: 'home' },
  { path: '/products', component: ProductList, name: 'products' },
  { path: '/product/:id', component: ProductDetail, name: 'product-detail' },
  { path: '/cart', component: Cart, name: 'cart' },
  { path: '/order', component: Order, name: 'order' },
  { path: '/login', component: Login, name: 'login' },
  { path: '/user', component: UserCenter, name: 'user-center', meta: { requiresAuth: true } },
  { path: '/user/favorites', component: Favorites, name: 'favorites' },
  { path: '/user/credit', component: Credit, name: 'credit' },
  { path: '/vip', component: VipCenter, name: 'vip' }
]
```

== 管理后台路由

```typescript
const routes = [
  { path: '/', component: Dashboard, name: 'admin-dashboard' },
  { path: '/users', component: UserList, name: 'admin-users' },
  { path: '/products/audit', component: ProductAudit, name: 'admin-product-audit' },
  { path: '/orders', component: OrderMonitor, name: 'admin-orders' },
  { path: '/merchants', component: MerchantList, name: 'admin-merchants' },
  { path: '/reviews', component: ReviewAudit, name: 'admin-reviews' },
  { path: '/coupons', component: Coupon, name: 'admin-coupons' },
  { path: '/settings', component: Settings, name: 'admin-settings' },
  { path: '/statistics', component: Statistics, name: 'admin-statistics' }
]
```

== 商家后台路由

```typescript
const routes = [
  { path: '/', component: Dashboard, name: 'merchant-dashboard' },
  { path: '/products', component: ProductList, name: 'merchant-products' },
  { path: '/products/edit/:id', component: ProductEdit, name: 'merchant-product-edit' },
  { path: '/orders', component: OrderList, name: 'merchant-orders' },
  { path: '/orders/:id', component: OrderDetail, name: 'merchant-order-detail' },
  { path: '/reviews', component: ReviewList, name: 'merchant-reviews' },
  { path: '/shop', component: ShopInfo, name: 'merchant-shop' },
  { path: '/coupons', component: CouponList, name: 'merchant-coupons' },
  { path: '/chat', component: Chat, name: 'merchant-chat' },
  { path: '/statistics', component: Statistics, name: 'merchant-statistics' }
]
```

= 环境变量配置

== Vite 配置

```typescript
// vite.config.ts
import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@user': resolve(__dirname, 'src/user'),
      '@admin': resolve(__dirname, 'src/admin'),
      '@merchant': resolve(__dirname, 'src/merchant'),
      '@common': resolve(__dirname, 'src/common')
    }
  },
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  }
})
```

#v(2.0em)

// 版本历史结束，添加水平居中的分割线
#align(center)[#line(length: 25%, stroke: 0.5pt)]

#v(2.0em)

#align(center)[#text(9pt, font: "SimSun")[文档版本：v1.1.0    更新日期：2026-03-26]]

// 第 7 页结束
