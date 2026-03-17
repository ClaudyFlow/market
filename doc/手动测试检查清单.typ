// 第1页开始

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
  header: [],
  footer: [],
)

#v(85mm)

#align(center)[#text(26pt, font: "SimHei")[市场平台\
  手动测试检查清单]]

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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[市场平台——手动测试检查清单]]
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

// 空白页（无页眉页脚）
#set page(header: [], footer: [])
#v(1fr)

// 第4页结束
#pagebreak()
// 第5页开始

// 设置页眉页脚
#set page(
  numbering: "I",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[市场平台——手动测试检查清单]]
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

#align(center)[#text(16pt, font: "SimHei")[= 前言]]

#v(10mm)

// 前言内容：五号宋体左对齐，首行缩进两个全角空格，行距为1.25倍
#set text(font: "SimSun", size: 10.5pt)
#set par(leading: 1.25em)

#h(2.0em)本测试检查清单旨在为测试人员提供完整的市场平台手动测试指南。\
#h(2.0em)文档涵盖了测试环境、基础测试、功能测试和测试结果记录等内容。\
#h(2.0em)市场平台是一个基于Vue#h(0.25em)3的现代化组件化应用，采用Nginx作为Web服务器，支持完整的电商购物流程。\
#h(2.0em)通过本检查清单，测试人员将能够全面了解平台的测试要点和验收标准，确保系统功能正常、性能稳定。\
#h(2.0em)我们希望本检查清单能够帮助测试人员高效、全面地完成市场平台的测试工作。\
#h(2.0em)本检查清单适用于具备一定测试基础的人员使用，读者应熟悉Web应用测试、#h(0.25em)Vue组件测试、浏览器开发者工具等基础知识。\
#h(2.0em)在开始测试前，请仔细阅读本文档，确保理解每个测试项的测试方法和验收标准。\
#h(2.0em)如遇到本文档未覆盖的问题或有任何疑问，请及时联系技术支持团队。\
#h(2.0em)我们将持续更新和完善本检查清单，以帮助用户更好地测试市场平台。\

// 第5页结束
#pagebreak()
// 第6页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])

// 第6页结束
#pagebreak()
// 第7页开始

// 切换到阿拉伯数字页码，设置页眉页脚
#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[市场平台——手动测试检查清单]]
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
#align(center)[#text(16pt, font: "SimHei")[手动测试检查清单]]
#v(10mm)

// 正文内容：五号宋体左对齐，首行缩进两个全角空格，正文部分的标题是五号黑体，行距为1.25倍
// 设置标题编号与标题之间有一个全角空格
#set heading(numbering: (..nums) => {
  let num = numbering("1.1", ..nums)
  num + "\u{3000}"
})

// 设置标题字体为五号黑体
// 一级标题行距为三倍，二级及以下标题行距为两倍
#show heading.where(level: 1): set text(font: "SimHei", size: 10.5pt)
#show heading.where(level: 1): set block(spacing: 3em)
#show heading.where(level: 2): set text(font: "SimHei", size: 10.5pt)
#show heading.where(level: 2): set block(spacing: 2em)
#show heading.where(level: 3): set text(font: "SimHei", size: 10.5pt)
#show heading.where(level: 3): set block(spacing: 2em)

#set text(font: "SimSun", size: 10.5pt)
#set par(leading: 1.25em)

= 测试环境

#h(2.0em)市场平台需要特定的测试环境支持，确保测试结果的准确性和可重复性。\
#h(2.0em)测试环境包括服务器环境、框架版本和组件依赖等。\

== 服务器配置

#h(2.0em)市场平台使用Nginx作为Web服务器，提供静态文件服务。\
#h(2.0em)服务器配置如下：\

#h(2.0em)1. 服务器：Nginx：localhost:80；\
#h(2.0em)2. 框架：Vue#h(0.25em)3#h(0.25em)CDN版本；\
#h(2.0em)3. 组件：5个Vue组件：Navbar、ProductCard、CartItem、OrderCard、Modal；\
#h(2.0em)4. 数据存储：localStorage：购物车和用户信息。\

== 组件依赖

#h(2.0em)市场平台依赖以下外部库和资源：\

#h(2.0em)1. Vue.js#h(0.25em)3：前端核心框架；\
#h(2.0em)2. Font#h(0.25em)Awesome#h(0.25em)6：矢量图标库；\
#h(2.0em)3. Google#h(0.25em)Fonts：现代无衬线字体；\
#h(2.0em)4. 自定义CSS样式文件。\

= 基础测试

#h(2.0em)基础测试验证系统的基本功能和依赖是否正常工作。\
#h(2.0em)基础测试包括服务器访问测试、依赖加载测试和组件标签测试。\

== 服务器和访问测试

#h(2.0em)测试服务器是否正常启动，以及主页是否可以正常访问。\
#h(2.0em)测试项包括：\

#h(2.0em)1. #text(font: "SimHei", weight: "bold")[服务器启动]：验证Nginx服务器已启动并运行；\
#h(2.0em)2. #text(font: "SimHei", weight: "bold")[主页访问]：验证主页可访问：http://localhost/；\
#h(2.0em)3. #text(font: "SimHei", weight: "bold")[状态码]：验证返回状态码为#h(0.25em)200#h(0.25em)OK。\

== 依赖加载测试

#h(2.0em)测试所有外部依赖是否正确加载。\
#h(2.0em)测试项包括：\

#h(2.0em)1. #text(font: "SimHei", weight: "bold")[Vue.js#h(0.25em)3]：验证Vue#h(0.25em)3#h(2.0em)已正确加载CDN；\
#h(2.0em)2. #text(font: "SimHei", weight: "bold")[Font#h(0.25em)Awesome]：验证图标库已加载；\
#h(2.0em)3. #text(font: "SimHei", weight: "bold")[Google#h(0.25em)Fonts]：验证字体已加载；\
#h(2.0em)4. #text(font: "SimHei", weight: "bold")[CSS样式]：验证自定义CSS样式已加载。\

== 组件标签测试

#h(2.0em)测试所有Vue#h(2.0em)组件标签是否正确存在。\
#h(2.0em)测试项包括：\

#h(2.0em)1. #text(font: "SimHei", weight: "bold")[Navbar组件]：验证导航栏组件标签存在；\
#h(2.0em)2. #text(font: "SimHei", weight: "bold")[ProductCard组件]：验证商品卡片组件标签存在；\
#h(2.0em)3. #text(font: "SimHei", weight: "bold")[CartItem组件]：验证购物车商品组件标签存在；\
#h(2.0em)4. #text(font: "SimHei", weight: "bold")[OrderCard组件]：验证订单卡片组件标签存在；\
#h(2.0em)5. #text(font: "SimHei", weight: "bold")[Modal组件]：验证模态框组件标签存在。\

== 静态资源测试

#h(2.0em)测试所有静态资源文件是否可以正常访问。\
#h(2.0em)测试项包括：\

#h(2.0em)1. #text(font: "SimHei", weight: "bold")[styles.css]：验证样式文件可访问；\
#h(2.0em)2. #text(font: "SimHei", weight: "bold")[main.js]：验证主应用文件可访问；\
#h(2.0em)3. #text(font: "SimHei", weight: "bold")[组件文件]：验证所有#h(2.0em)Vue#h(2.0em)组件文件可访问。\

== 功能代码测试

#h(2.0em)测试核心功能代码是否正确实现。\
#h(2.0em)测试项包括：\

#h(2.0em)1. #text(font: "SimHei", weight: "bold")[localStorage功能]：验证本地存储功能代码存在；\
#h(2.0em)2. #text(font: "SimHei", weight: "bold")[异步组件]：验证异步组件定义正确；\
#h(2.0em)3. #text(font: "SimHei", weight: "bold")[响应式设计]：验证响应式设计CSS#h(2.0em)存在。\

= 功能测试

#h(2.0em)功能测试验证市场平台的各项功能是否正常工作。\
#h(2.0em)功能测试包括导航栏功能、商品浏览功能、购物车功能、订单功能和用户认证功能。\

== 导航栏功能

#h(2.0em)导航栏是应用的入口，提供全局导航和用户交互功能。\
#h(2.0em)测试项包括：\

#h(2.0em)1. 导航菜单切换：测试首页、商品、购物车、订单四个页面的切换功能；\
#h(2.0em)2. 搜索功能：测试搜索功能，输入搜索词后按回车，验证搜索结果；\
#h(2.0em)3. 购物车数量显示：验证导航栏中购物车数量的实时显示；\
#h(2.0em)4. 用户菜单：测试登录/注册入口、个人资料查看和退出登录功能。\

== 商品浏览功能

#h(2.0em)商品浏览功能是平台的核心功能之一，提供商品展示和搜索筛选能力。\
#h(2.0em)测试项包括：\

#h(2.0em)1. 商品卡片显示：验证商品卡片正确显示图片、名称、价格和评分信息；\
#h(2.0em)2. 鼠标悬停效果：验证鼠标悬停时图片缩放和加入购物车按钮显示；\
#h(2.0em)3. 添加到购物车：验证从商品页面添加商品到购物车的功能；\
#h(2.0em)4. 商品分类筛选：验证按电子产品、服装鞋帽、配饰、家用电器、运动户外五个分类进行筛选；\
#h(2.0em)5. 商品排序：验证按最新商品、价格从低到高、价格从高到低、评分最高进行排序。\

== 购物车功能

#h(2.0em)购物车功能管理用户的购物车数据，提供添加、调整和结算功能。\
#h(2.0em)测试项包括：\

#h(2.0em)1. 商品数量调整：验证使用加号和减号按钮调整商品数量的功能；\
#h(2.0em)2. 商品移除功能：验证从购物车中删除商品的功能；\
#h(2.0em)3. 购物车总价计算：验证商品总价、运费和订单总额的计算是否正确；\
#h(2.0em)4. 运费计算：验证满#h(0.25em)100#h(2.0em)元免运费的规则是否正确；\
#h(2.0em)5. 结算功能：验证结算功能是否正常，需要登录。\

== 订单功能

#h(2.0em)订单功能管理用户的订单信息，提供订单查看和状态跟踪功能。\
#h(2.0em)测试项包括：\

#h(2.0em)1. 订单列表显示：验证历史订单列表是否正确显示；\
#h(2.0em)2. 订单状态显示：验证待处理、处理中、已发货、已送达四种订单状态的显示是否正确；\
#h(2.0em)3. 订单详情查看：验证查看订单详细信息的功能；\
#h(2.0em)4. 订单总额计算：验证订单金额的计算是否正确。\

== 用户认证功能

#h(2.0em)用户认证功能提供用户登录、注册和个人信息管理功能。\
#h(2.0em)测试项包括：\

#h(2.0em)1. 登录模态框显示：验证登录模态框的显示和隐藏功能；\
#h(2.0em)2. 注册模态框显示：验证注册模态框的显示和隐藏功能；\
#h(2.0em)3. 登录功能：验证用户名/密码登录功能；\
#h(2.0em)4. 注册功能：验证新用户注册功能；\
#h(2.0em)5. 用户信息显示：验证用户个人信息和登录状态的显示。\

== 数据持久化功能

#h(2.0em)数据持久化功能确保用户数据在页面刷新后不会丢失。\
#h(2.0em)测试项包括：\

#h(2.0em)1. 购物车数据保存：验证购物车数据是否正确保存到localStorage；\
#h(2.0em)2. 用户登录状态保存：验证用户登录状态是否正确保存到localStorage；\
#h(2.0em)3. 页面刷新后数据恢复：验证页面刷新后购物车和用户数据是否正确恢复。\

== 响应式设计

#h(2.0em)响应式设计确保平台在不同设备上都能正常显示和使用。\
#h(2.0em)测试项包括：\

#h(2.0em)1. 桌面端布局：验证在桌面端布局是否正常；\
#h(2.0em)2. 平板端布局适配：验证在平板端布局是否正确适配；\
#h(2.0em)3. 手机端布局适配：验证在手机端布局是否正确适配；\
#h(2.0em)4. 导航栏表现：验证导航栏在不同屏幕尺寸下的表现是否正常。\

= 测试结果

#h(2.0em)本章节记录测试的执行情况和结果，便于问题追踪和改进。\

== 测试信息

#h(2.0em)*测试时间：*2026年3月7日\

#h(2.0em)*测试人员：*市场平台测试团队\

#h(2.0em)*总体状态：*✅ 通过\

== 详细结果

#h(2.0em)1. *基础架构*：✅ 所有组件和依赖已正确加载；\
#h(2.0em)2. *Vue组件化*：✅#h(0.25em)5个组件均已实现为异步组件；\
#h(2.0em)3. *数据流*：✅ 父子组件通信正常，使用props/emit；\
#h(2.0em)4. *状态管理*：✅#h(0.25em)Vue响应式系统工作正常；\
#h(2.0em)5. *数据持久化*：✅#h(0.25em)localStorage集成完成；\
#h(2.0em)6. *UI#h(0.25em)/UX*：✅ 响应式设计已实现；\
#h(2.0em)7. *错误处理*：⚠️#h(0.25em)仅有一个非关键警告：favicon.ico缺失。\

== 建议改进

#h(2.0em)1. 添加favicon.ico文件消除警告；\
#h(2.0em)2. 考虑添加Vue#h(0.25em)Router进行更复杂的路由管理；\
#h(2.0em)3. 添加Vuex/Pinia进行更复杂的状态管理；\
#h(2.0em)4. 添加单元测试和端到端测试。\

= 结论

#h(2.0em)市场平台手动测试已成功完成，所有核心功能均正常工作。\
#h(2.0em)平台采用组件化架构，具备良好的可维护性和扩展性，能够满足用户的购物需求。\
#h(2.0em)通过本次测试，验证了平台的稳定性和可靠性，为后续的功能迭代和优化奠定了基础。\

#v(1cm)

#align(center)[#text(12pt, font: "SimHei")[感谢您对市场平台测试工作的支持！]]\
#align(center)[让我们一起打造更好的电商体验。]\

#v(0.5cm)

#align(center)[#text(10pt, font: "SimSun")[📧 Email: support\@market.com | 🌐 Website: https://market.com]]\
#align(center)[#text(
  10pt,
  font: "SimSun",
)[🐙 GitHub: https://github.com/market-platform | 📚 Docs: https://docs.market.com]]\

#v(1cm)

#align(right)[#text(9pt, font: "SimSun")[最后更新：2026年3月7日]]\
#align(right)[#text(9pt, font: "SimSun")[文档版本：v1.0.0]]

#v(2.0em)

// 版本历史结束，添加水平居中的分割线
#align(center)[#line(length: 25%, stroke: 0.5pt)]

// 第7页结束
#pagebreak()
// 第7页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])

// 第8页结束
#pagebreak()
// 第9页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])

// 第9页结束
