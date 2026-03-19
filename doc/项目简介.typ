// 第 1 页开始

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
  header: [],
  footer: [],
)

// 封面标题：标题前间距 15mm，标题后间距 10mm，水平居中

#v(85mm)

#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  项目简介]]

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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——项目简介]]
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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——项目简介]]
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

// 前言标题：标题前间距 15mm，标题后间距 10mm，三号黑体居中对齐

#v(15mm)

#align(center)[#text(16pt, font: "SimHei")[= 前言]]

#v(10mm)

// 前言内容：五号宋体左对齐，首行缩进两个全角空格，行距为 1.25 倍
#set text(font: "SimSun", size: 10.5pt)
#set par(leading: 1.25em)

#h(2.0em)本项目简介旨在为用户提供购物商城系统的全面介绍，包括项目背景、系统架构、功能特性、技术栈和部署方式等内容。\
#h(2.0em)文档涵盖了购物商城系统的完整设计理念、技术实现和应用场景，帮助读者全面了解项目的整体情况。\
#h(2.0em)购物商城系统是一个基于 Vue 3 和 Spring Boot 3 的现代化全栈电商解决方案，采用前后端分离架构，支持完整的电商购物流程。\
#h(2.0em)通过本文档，读者将能够了解购物商城系统的完整技术架构和功能特性，掌握必要的技术知识，并能够独立进行项目部署和开发工作。\
#h(2.0em)我们希望本文档能帮助用户快速理解购物商城系统的设计思想和实现细节。\
#h(2.0em)本文档适用于具备一定技术基础的人员使用，读者应熟悉 HTML、CSS、JavaScript、Java 等基础知识。\
#h(2.0em)在开始使用前，请仔细阅读本文档，确保理解每个功能模块的设计思想和使用方法。\
#h(2.0em)如遇到本文档未覆盖的问题或有任何疑问，请及时联系技术支持团队。\
#h(2.0em)我们将持续更新和完善本文档，以帮助用户更好地使用购物商城系统。\
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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——项目简介]]
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

// 正文大标题：标题前间距 15mm，标题后间距 10mm，三号黑体居中对齐
#v(15mm)
#align(center)[#text(16pt, font: "SimHei")[项目简介]]
#v(10mm)

// 正文内容：五号宋体左对齐，首行缩进两个全角空格，正文部分的标题是五号黑体，行距为 1.25 倍
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

= 项目概述


#h(2.0em)购物商城系统是一个现代化的全栈电商平台，旨在为用户提供便捷、安全、高效的在线购物体验。\
#h(2.0em)平台采用前后端分离架构，前端使用 Vue 3 框架，后端使用 Spring Boot 3.4.0 框架，支持完整的电商购物流程。\
#h(2.0em)本简介将详细介绍项目的整体架构、技术栈、功能特性和部署方式，帮助读者全面了解购物商城系统的设计和实现。\

= 系统架构

== 技术栈


#h(2.0em)购物商城系统采用现代化的技术栈，确保系统具有良好的性能和可扩展性。\
#h(2.0em)主要技术包括前端技术、后端技术和部署技术。\

=== 前端技术


#h(2.0em)前端应用采用 Vue 3 框架，使用组合式 API 进行组件开发。\
#h(2.0em)前端技术栈包括：\
#h(2.0em)——框架：Vue 3 组合式 API；\
#h(2.0em)——UI 组件：Element Plus；\
#h(2.0em)——状态管理：Pinia；\
#h(2.0em)——路由：Vue Router；\
#h(2.0em)——HTTP 客户端：Axios；\
#h(2.0em)——样式：CSS3 + SCSS；\
#h(2.0em)——构建：Vite。\

=== 后端技术


#h(2.0em)后端应用采用 Java 21 语言，基于 Spring Boot 3.4.0 框架构建。\
#h(2.0em)后端技术栈包括：\
#h(2.0em)——语言：Java 21；\
#h(2.0em)——框架：Spring Boot 3.4.0 + Spring Security + Spring Data JPA + Spring Data Redis；\
#h(2.0em)——数据库：PostgreSQL 14+ / SQLite 3.45+；\
#h(2.0em)——缓存：Redis 6+；\
#h(2.0em)——认证：JWT Token；\
#h(2.0em)——ORM：Hibernate；\
#h(2.0em)——API 风格：RESTful API；\
#h(2.0em)——构建工具：Maven。\

=== 部署技术


#h(2.0em)部署采用 Nginx 作为 Web 服务器，支持容器化部署。\
#h(2.0em)部署技术栈包括：\
#h(2.0em)——Web 服务器：Nginx 1.18+；\
#h(2.0em)——容器化：Docker + Docker Compose；\
#h(2.0em)——操作系统：支持 Windows、Linux、macOS。\

= 功能特性


#h(2.0em)购物商城系统提供完整的电商功能，满足用户从商品浏览到订单完成的全流程需求。\
#h(2.0em)主要功能模块包括商品管理、购物车管理、订单管理、用户管理、积分系统、优惠券系统等。\

== 商品管理


#h(2.0em)商品管理模块提供商品浏览、搜索和筛选功能。\
#h(2.0em)用户可以方便地查找和浏览商品。\

=== 商品浏览


#h(2.0em)商品采用网格布局展示，响应式设计适配不同屏幕尺寸。\
#h(2.0em)商品卡片包含以下信息：\
#h(2.0em)——商品图片；\
#h(2.0em)——商品名称；\
#h(2.0em)——商品描述；\
#h(2.0em)——价格信息；\
#h(2.0em)——用户评分。\

=== 商品搜索


#h(2.0em)支持全局搜索功能，支持商品名称、描述、分类的模糊搜索。\
#h(2.0em)实时显示搜索结果，提供快速的商品查找体验。\

== 购物车管理


#h(2.0em)购物车管理模块提供购物车管理和订单创建功能。\
#h(2.0em)购物车数据保存在数据库中，支持多端同步。\

=== 购物车功能


#h(2.0em)购物车管理功能包括：\
#h(2.0em)——添加商品：从商品页面添加到购物车；\
#h(2.0em)——数量调整：增加/减少商品数量；\
#h(2.0em)——移除商品：从购物车中删除商品；\
#h(2.0em)——实时计算：商品总价、运费、订单总额。\

== 订单管理


#h(2.0em)订单管理模块提供订单查看和管理功能。\
#h(2.0em)用户可以查看历史订单和订单状态。\

=== 订单查看


#h(2.0em)订单列表展示，支持订单详情查看。\
#h(2.0em)订单状态跟踪包括：\
#h(2.0em)——待处理；\
#h(2.0em)——处理中；\
#h(2.0em)——已发货；\
#h(2.0em)——已送达。\

== 用户管理


#h(2.0em)用户管理模块提供用户认证和用户信息管理功能。\
#h(2.0em)支持用户登录、注册和个人资料查看。\

=== 用户认证


#h(2.0em)用户认证功能包括：\
#h(2.0em)——登录功能：用户名/密码登录；\
#h(2.0em)——注册功能：新用户注册；\
#h(2.0em)——退出登录：安全退出。\

== 积分系统


#h(2.0em)积分系统模块提供积分管理和签到打卡功能。\
#h(2.0em)用户可以通过签到、购物等方式获取积分，积分可用于兑换商品或优惠券。\

=== 积分功能


#h(2.0em)积分系统功能包括：\
#h(2.0em)——积分查询：查看当前积分余额；\
#h(2.0em)——积分历史：查看积分增减记录；\
#h(2.0em)——签到打卡：每日签到获取积分；\
#h(2.0em)——积分兑换：使用积分兑换商品或优惠券。\

== 优惠券系统


#h(2.0em)优惠券系统模块提供优惠券领取和使用功能。\
#h(2.0em)用户可以领取优惠券并在下单时使用。\

== 抽奖功能


#h(2.0em)抽奖功能模块提供幸运抽奖活动。\
#h(2.0em)用户可以参与抽奖获取积分、优惠券等奖励。\

= 部署方式


#h(2.0em)购物商城系统支持多种部署方式，包括传统部署、容器化部署和云平台部署。\
#h(2.0em)用户可以根据实际需求选择合适的部署方式。\

== 传统部署


#h(2.0em)传统部署方式适合对容器化不熟悉的用户，部署过程相对简单。\
#h(2.0em)部署步骤包括：\
#h(2.0em)1. 部署前端：使用 Nginx 托管静态文件；\
#h(2.0em)2. 部署后端：运行 start_backend.bat 启动脚本或使用 java -jar 命令；\
#h(2.0em)3. 数据库：PostgreSQL 需预先安装，SQLite 数据库自动创建在 data/ 目录。\

== 容器化部署


#h(2.0em)容器化部署方式适合需要快速部署和环境一致性的场景。\
#h(2.0em)使用 Docker 和 Docker Compose 可以实现一键部署。\

=== Docker 部署


#h(2.0em)使用 Docker 快速部署的步骤：\
#h(2.0em)1. 克隆项目：git clone <repository-url>；\
#h(2.0em)2. 启动服务：docker-compose up -d；\
#h(2.0em)3. 访问应用：前端访问 http://localhost:80，后端 API 访问 http://localhost:8080。\

== 云平台部署


#h(2.0em)云平台部署方式适合需要高可用和弹性扩展的生产环境。\
#h(2.0em)支持的云平台包括：\
#h(2.0em)1. AWS：Amazon Web Services；\
#h(2.0em)2. 阿里云：Alibaba Cloud；\
#h(2.0em)3. 腾讯云：Tencent Cloud。\

= 项目结构


#h(2.0em)购物商城系统采用模块化的项目结构，便于开发和维护。\
#h(2.0em)项目结构包括前端代码、后端代码和文档。\

== 目录结构


#h(2.0em)项目根目录结构如下：\
#h(2.0em)market/ 项目根目录\
#h(2.0em)├── backend/ Java 后端 Spring Boot 项目\
#h(2.0em)├── frontend/ 前端 Vue.js 项目\
#h(2.0em)├── doc/ 项目文档（包含 .typ 源文件和 .pdf 输出）\
#h(2.0em)├── data/ 数据文件（SQLite 数据库、Redis 数据）\
#h(2.0em)├── logs/ 日志文件\
#h(2.0em)├── script/ 辅助脚本\
#h(2.0em)├── pom.xml Maven 配置文件\
#h(2.0em)└── start_backend.bat 后端启动脚本\

=== 文档说明


#h(2.0em)doc/ 目录包含完整的技术文档：\
#h(2.0em)1. 项目简介：项目概述、架构、功能、技术栈；\
#h(2.0em)2. 前端功能介绍：详细的功能模块说明和使用指南；\
#h(2.0em)3. 前端接口文档：组件接口规范、数据结构、API 设计；\
#h(2.0em)4. 部署配置指南：完整的部署步骤、配置优化、运维管理；\
#h(2.0em)5. 数据库结构文档：数据库表结构、关系图、SQL 脚本。\

= 版本信息


#h(2.0em)购物商城系统持续迭代更新，定期发布新版本。\
#h(2.0em)版本历史记录了各版本的更新内容。\

== 版本历史


#h(2.0em)*v1.0.0* 2026-03-09 初始版本：基础电商功能、Vue 3 前端、Spring Boot 3.4.0 后端 已发布\
#h(2.0em)*v1.1.0* 规划中 支付集成、物流跟踪、商品评价 开发中\
#h(2.0em)*v1.2.0* 规划中 移动端 App 完善、社交分享、优惠券系统 规划中\
#h(2.0em)*v2.0.0* 规划中 微服务架构、AI 推荐、多商户支持 规划中\

== 当前版本


#h(2.0em)当前版本为 v1.0.0，提供以下核心功能：\
#h(2.0em)1. 用户认证系统：注册、登录、JWT Token 认证；\
#h(2.0em)2. 邮箱验证系统：邮箱格式验证、MX 记录验证、验证码发送；\
#h(2.0em)3. 积分管理系统：积分增减、历史记录、统计功能；\
#h(2.0em)4. 优惠券系统：优惠券领取、使用、管理；\
#h(2.0em)5. 抽奖功能：幸运抽奖活动；\
#h(2.0em)6. 商品管理：商品 CRUD、搜索、分类；\
#h(2.0em)7. 购物车：添加、删除、修改数量；\
#h(2.0em)8. 订单管理：订单创建、查询、状态跟踪；\
#h(2.0em)9. 数据持久化：PostgreSQL/SQLite 数据库、Spring Data JPA 操作；\
#h(2.0em)10. 缓存支持：Redis 缓存、验证码存储、热点数据加速。\

= 快速开始


#h(2.0em)本章节介绍如何快速开始使用购物商城系统。\
#h(2.0em)包括环境要求和快速部署步骤。\

== 环境要求


#h(2.0em)*开发环境：*\
#h(2.0em)1. JDK 21+ Java 开发；\
#h(2.0em)2. PostgreSQL 14+ / SQLite 3.45+（数据库）；\
#h(2.0em)3. Redis 6+（缓存）；\
#h(2.0em)4. Maven 3.6+（构建工具）；\
#h(2.0em)5. Git 2.20.0+（版本控制）；\
#h(2.0em)6. Node.js 18+（前端开发）；\
#h(2.0em)7. VS Code 或 IntelliJ IDEA（开发工具）。\
#h(2.0em)*生产环境：*\
#h(2.0em)1. Linux 服务器 Ubuntu 20.04/CentOS 8；\
#h(2.0em)2. Nginx 1.18+Web 服务器；\
#h(2.0em)3. Docker 20.10+(容器化部署)；\
#h(2.0em)4. 至少 2GB 内存，2 核 CPU。\

== 快速部署


#h(2.0em)*本地快速部署：*\
#h(2.0em)1. 启动 Redis：redis-server 或使用 Docker；\
#h(2.0em)2. 启动后端：运行 start_backend.bat 脚本；\
#h(2.0em)3. 启动前端：cd frontend && npm install && npm run dev；\
#h(2.0em)4. 访问应用：前端访问 http://localhost:5173，后端 API 访问 http://localhost:8080。\

= 常见问题


#h(2.0em)本章节列出使用过程中可能遇到的常见问题及解决方案。\
#h(2.0em)遇到问题时，可以先参考本章内容，如无法解决，请联系技术支持。\

== 功能问题


#h(2.0em)1. 购物车数据丢失：购物车数据保存在数据库中，如出现数据丢失请检查数据库连接。\
#h(2.0em)2. 登录状态失效：登录状态使用 JWT Token，Token 过期后需要重新登录。\
#h(2.0em)3. 商品图片无法显示：检查网络连接，确保图片资源可以正常访问。如仍无法显示，请联系技术支持。\

== 兼容性问题


#h(2.0em)1. IE 浏览器不支持：应用不支持 IE 浏览器。建议用户使用 Chrome、Firefox 或 Edge 等现代浏览器。\
#h(2.0em)2. 移动端显示异常：检查浏览器版本，确保使用支持的浏览器版本。如仍无法正常显示，请联系技术支持。\

== 性能问题


#h(2.0em)1. 页面加载缓慢：检查网络连接，确保网络通畅。清除浏览器缓存，重新加载页面。\
#h(2.0em)2. 应用卡顿：检查浏览器版本，确保使用支持的浏览器版本。关闭其他占用资源的标签页和程序。\

= 联系我们


#h(2.0em)如遇到任何问题或有任何疑问，请及时联系技术支持团队。\
#h(2.0em)我们将竭诚为您提供帮助。\
#h(2.0em)1. GitHub Issues：技术问题讨论；\
#h(2.0em)2. 文档中心：详细的技术文档；\
#h(2.0em)3. 电子邮件：support\@market.com。\
#v(1cm)

#align(center)[#text(12pt, font: "SimHei")[感谢你对购物商城系统的关注和支持！]]\
#align(center)[让我们一起打造更好的电商体验。]\

#v(0.5cm)

#align(center)[#text(10pt, font: "SimSun")[📧 Email: contact\@market.com | 🌐 Website: https://market.com]]\
#align(center)[#text(
  10pt,
  font: "SimSun",
)[🐙 GitHub: https://github.com/market-platform | 📚 Docs: https://docs.market.com]]\

#v(1cm)

#align(right)[#text(9pt, font: "SimSun")[最后更新：2026 年 3 月 19 日]]\
#align(right)[#text(9pt, font: "SimSun")[文档版本：v1.1.0]]

#v(2.0em)

// 版本历史结束，添加水平居中的分割线
#align(center)[#line(length: 25%, stroke: 0.5pt)]

// 第 6 页结束
#pagebreak()
// 第 7 页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])

// 第 7 页结束
