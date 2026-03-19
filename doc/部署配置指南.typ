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
  部署配置指南]]

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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——部署配置指南]]
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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——部署配置指南]]
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

#h(2.0em)本部署配置指南旨在为系统管理员和运维人员提供详细的购物商城系统部署和配置说明。\
#h(2.0em)文档涵盖了系统环境准备、软件安装、配置参数设置、服务启动与维护等关键环节。\
#h(2.0em)通过本指南，读者将能够了解购物商城系统的完整部署流程，掌握必要的配置技巧，并能够独立完成系统的安装和调试工作。\
#h(2.0em)我们希望本指南能够帮助用户快速搭建稳定、高效的购物商城系统环境。\
#h(2.0em)本指南适用于具备一定技术基础的人员使用，读者应熟悉操作系统配置、网络配置、数据库管理等基础知识。\
#h(2.0em)在开始部署前，请仔细阅读本文档，确保理解每个步骤的操作要求和注意事项。\
#h(2.0em)如遇到本文档未覆盖的问题或有任何疑问，请及时联系技术支持团队。\
#h(2.0em)我们将持续更新和完善本指南，以帮助用户更好地使用购物商城系统。\

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
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——部署配置指南]]
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
#align(center)[#text(16pt, font: "SimHei")[部署配置指南]]
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

= 范围

#h(2.0em)本指南适用于购物商城系统系统的部署和配置工作。\
#h(2.0em)主要面向系统管理员、运维工程师及相关技术人员，提供了从环境准备到系统上线的完整流程说明。\
#h(2.0em)本指南详细介绍了系统环境准备、软件安装、配置参数设置、服务启动与维护等关键环节，帮助用户快速搭建稳定、高效的购物商城系统环境。\

#h(2.0em)本指南适用于以下场景：\
#h(2.0em)——生产环境部署：适用于正式生产环境的部署和配置；\
#h(2.0em)——测试环境部署：适用于测试和开发环境的部署；\
#h(2.0em)——系统升级：适用于系统版本升级和迁移；\
#h(2.0em)——系统维护：适用于日常运维和故障处理。\

= 术语和定义

==

===
#h(2.0em)#text(font: "SimHei", weight: "bold", size: 10.5pt)[购物商城系统]\
#h(2.0em)指用于商品交易、用户管理、订单处理等功能的综合性电子商务系统。平台包括前端商城、后台管理、支付系统等模块。\

===
#h(2.0em)#text(font: "SimHei", weight: "bold", size: 10.5pt)[部署环境]\
#h(2.0em)指用于安装和运行购物商城系统系统所需的硬件、软件及网络环境。包括服务器硬件、操作系统、数据库、中间件等。\

===
#h(2.0em)#text(font: "SimHei", weight: "bold", size: 10.5pt)[容器化部署]\
#h(2.0em)指使用 Docker 等容器技术将应用及其依赖打包成容器镜像，实现应用的快速部署和迁移。\

===
#h(2.0em)#text(font: "SimHei", weight: "bold", size: 10.5pt)[负载均衡]\
#h(2.0em)指将网络流量分发到多个服务器，以提高系统的可用性和性能。\

= 系统环境准备

== 软件依赖

#h(2.0em)系统需要以下软件环境支持：\
#h(2.0em)——操作系统：Windows 10/11、Linux (Ubuntu 20.04+/CentOS 8+)、macOS；\
#h(2.0em)——Java 运行环境：JDK 21+；\
#h(2.0em)——数据库：PostgreSQL 14+ 或 SQLite 3.45+；\
#h(2.0em)——缓存服务：Redis 6+；\
#h(2.0em)——Web 服务器：Nginx 1.18+（生产环境）；\
#h(2.0em)——Node.js：18+（前端开发）；\
#h(2.0em)——Maven：3.6+（后端构建）。\

=== JDK 版本选择

#h(2.0em)项目使用 JDK 21，建议使用 LTS 版本以确保长期支持和稳定性。\

== 网络配置

#h(2.0em)系统需要开放以下端口：\
#h(2.0em)——80 端口：HTTP 访问（前端）；\
#h(2.0em)——443 端口：HTTPS 访问（生产环境建议）；\
#h(2.0em)——5432 端口：PostgreSQL 数据库访问；\
#h(2.0em)——6379 端口：Redis 缓存访问；\
#h(2.0em)——8080 端口：后端 API 服务。\

= 软件安装

#h(2.0em)本章节介绍了系统所需软件的安装步骤和注意事项。\
#h(2.0em)按照本章节的步骤操作，可以确保软件正确安装和配置。\

== Java 环境安装

#h(2.0em)1. 下载 JDK 安装包：从 Oracle 官网或 OpenJDK 官网下载 JDK 21 安装包\
#h(2.0em)2. 运行安装程序：双击安装包或运行安装向导，按照提示完成安装\
#h(2.0em)3. 配置环境变量：在系统环境变量中添加 JAVA_HOME、Path 等环境变量\
#h(2.0em)4. 验证安装结果：在命令行中使用 java -version 命令验证 JDK 是否正确安装\

#h(2.0em)安装完成后，建议配置 JVM 参数，根据服务器内存大小调整堆内存大小。\

== PostgreSQL 数据库安装

#h(2.0em)1. 下载 PostgreSQL 安装包：从 PostgreSQL 官网下载对应版本\
#h(2.0em)2. 安装数据库服务：运行安装程序，按照向导完成安装\
#h(2.0em)3. 初始化数据库：设置超级用户密码，配置监听地址\
#h(2.0em)4. 创建数据库和用户：\
#h(2.0em)   CREATE DATABASE market;\
#h(2.0em)   CREATE USER admin WITH PASSWORD '123456';\
#h(2.0em)   GRANT ALL PRIVILEGES ON DATABASE market TO admin;\

#h(2.0em)如使用 SQLite，无需单独安装，系统会自动创建数据库文件。\

== Redis 安装

#h(2.0em)Windows 安装：\
#h(2.0em)1. 使用 winget 安装：winget install Microsoft.OpenRedis\
#h(2.0em)2. 或使用 Docker：docker run -d -p 6379:6379 --name redis redis:latest\

#h(2.0em)Linux 安装：\
#h(2.0em)1. Ubuntu/Debian：sudo apt-get install redis-server\
#h(2.0em)2. CentOS/RHEL：sudo yum install redis\

#h(2.0em)macOS 安装：\
#h(2.0em)brew install redis\
#h(2.0em)brew services start redis\

#h(2.0em)验证安装：redis-cli ping，返回 PONG 表示成功。\

== Nginx 安装

#h(2.0em)1. 下载 Nginx 安装包：从 Nginx 官网下载最新稳定版安装包\
#h(2.0em)2. 安装 Nginx：运行安装程序，按照向导完成安装\
#h(2.0em)3. 配置 Nginx 服务：编辑 nginx.conf 配置文件，配置反向代理\
#h(2.0em)4. 启动 Nginx 服务：通过服务管理器或命令行启动 Nginx 服务\

#h(2.0em)建议配置 SSL 证书，启用 HTTPS 访问，提高数据传输安全性。\

= 系统配置

#h(2.0em)系统配置包括应用配置、数据库配置、安全配置等多个方面。\
#h(2.0em)正确的配置是系统稳定运行的基础。\

== 后端配置

#h(2.0em)后端配置文件位于 backend/src/main/resources/application.properties\

#h(2.0em)主要配置项包括：\
#h(2.0em)——服务器端口：server.port=8080\
#h(2.0em)——数据库配置：spring.datasource.url、username、password\
#h(2.0em)——Redis 配置：spring.data.redis.host、port\
#h(2.0em)——JWT 配置：jwt.secret、jwt.expiration\
#h(2.0em)——邮件配置：spring.mail.host、username、password\

#h(2.0em)示例配置：\
#h(2.0em)server.port=8080\
#h(2.0em)spring.datasource.url=jdbc:postgresql://localhost:5432/market\
#h(2.0em)spring.datasource.username=admin\
#h(2.0em)spring.datasource.password=123456\
#h(2.0em)spring.data.redis.host=localhost\
#h(2.0em)spring.data.redis.port=6379\

== 前端配置

#h(2.0em)前端配置文件位于 frontend/.env 或 frontend/vite.config.js\

#h(2.0em)主要配置项包括：\
#h(2.0em)——API 基础 URL：VITE_API_BASE_URL=http://localhost:8080\
#h(2.0em)——开发服务器端口：server.port=5173\

== Nginx 配置

#h(2.0em)Nginx 配置文件位于 nginx/conf/nginx.conf\

#h(2.0em)主要配置项包括：\
#h(2.0em)——监听端口：listen 80;\
#h(2.0em)——静态文件路径：root /path/to/frontend;\
#h(2.0em)——API 反向代理：location /api { proxy_pass http://localhost:8080; }\

= 部署步骤

#h(2.0em)本章节详细介绍系统的部署步骤。\
#h(2.0em)按照本章节的步骤操作，可以确保系统正确部署。\

== 本地开发环境部署

#h(2.0em)1. 安装依赖软件：JDK 21、Redis、PostgreSQL/SQLite、Node.js 18+\
#h(2.0em)2. 克隆项目代码：git clone <repository-url>\
#h(2.0em)3. 启动 Redis：redis-server 或使用 Docker\
#h(2.0em)4. 配置数据库：创建 PostgreSQL 数据库或使用 SQLite\
#h(2.0em)5. 配置后端：编辑 application.properties 文件\
#h(2.0em)6. 启动后端：运行 start_backend.bat 或 mvn spring-boot:run\
#h(2.0em)7. 安装前端依赖：cd frontend && npm install\
#h(2.0em)8. 启动前端：npm run dev\
#h(2.0em)9. 访问应用：前端 http://localhost:5173，后端 API http://localhost:8080\

== 生产环境部署

#h(2.0em)1. 准备服务器：Linux 服务器，至少 2GB 内存，2 核 CPU\
#h(2.0em)2. 安装软件：JDK 21、Redis、PostgreSQL、Nginx\
#h(2.0em)3. 配置数据库：创建数据库和用户，配置连接池\
#h(2.0em)4. 部署后端：打包 jar 文件，使用 systemd 或 Supervisor 管理进程\
#h(2.0em)5. 部署前端：构建静态文件，使用 Nginx 托管\
#h(2.0em)6. 配置 Nginx：配置反向代理和负载均衡\
#h(2.0em)7. 配置 SSL：申请证书，启用 HTTPS\
#h(2.0em)8. 启动服务：依次启动 Redis、PostgreSQL、后端、Nginx\
#h(2.0em)9. 验证部署：访问网站，测试各项功能\

== Docker 容器化部署

#h(2.0em)1. 安装 Docker 和 Docker Compose\
#h(2.0em)2. 配置 docker-compose.yml 文件\
#h(2.0em)3. 启动服务：docker-compose up -d\
#h(2.0em)4. 查看状态：docker-compose ps\
#h(2.0em)5. 访问应用：前端 http://localhost:80，后端 API http://localhost:8080\

#h(2.0em)docker-compose.yml 包含以下服务：\
#h(2.0em)——redis：Redis 缓存服务\
#h(2.0em)——postgres：PostgreSQL 数据库服务\
#h(2.0em)——backend：后端应用服务\
#h(2.0em)——frontend：前端应用服务\

= 系统维护

#h(2.0em)系统维护包括日常监控、日志管理、备份恢复、性能优化等工作。\
#h(2.0em)定期进行系统维护，可以确保系统稳定运行。\

== 日志管理

#h(2.0em)后端日志位于 logs/目录，使用 Spring Boot 默认日志配置。\
#h(2.0em)建议配置日志轮转，避免日志文件过大。\

== 数据库备份

#h(2.0em)PostgreSQL 备份命令：\
#h(2.0em)pg_dump -U admin market > backup.sql\
#h(2.0em)恢复命令：\
#h(2.0em)psql -U admin market < backup.sql\

#h(2.0em)建议配置定期自动备份策略。\

== 性能优化

#h(2.0em)性能优化建议：\
#h(2.0em)——启用 Redis 缓存热点数据；\
#h(2.0em)——配置数据库连接池；\
#h(2.0em)——启用 Nginx 静态资源缓存；\
#h(2.0em)——配置 JVM 参数优化；\
#h(2.0em)——使用 CDN 加速静态资源。\

= 常见问题

#h(2.0em)本章节列出部署过程中可能遇到的常见问题及解决方案。\

== 端口冲突

#h(2.0em)问题：端口被占用，服务无法启动\
#h(2.0em)解决：使用 netstat -ano 命令查看端口占用，关闭占用端口的进程或修改配置使用其他端口。\

== 数据库连接失败

#h(2.0em)问题：无法连接到数据库\
#h(2.0em)解决：检查数据库服务是否启动，检查数据库地址、端口、用户名、密码配置是否正确。\

== Redis 连接失败

#h(2.0em)问题：无法连接到 Redis\
#h(2.0em)解决：检查 Redis 服务是否启动，使用 redis-cli ping 测试连接，检查配置文件中的 Redis 地址和端口。\

= 联系我们

#h(2.0em)如遇到任何问题或有任何疑问，请及时联系技术支持团队。\
#h(2.0em)我们将竭诚为您提供帮助。\
#h(2.0em)1. GitHub Issues：技术问题讨论；\
#h(2.0em)2. 文档中心：详细的技术文档；\
#h(2.0em)3. 电子邮件：support\@market.com。\
#v(1cm)

#align(center)[#text(12pt, font: "SimHei")[感谢您对购物商城系统的关注和支持！]]\
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
#pagebreak()
// 第 8 页开始

// 空白页（无页眉页脚）
#set page(header: [], footer: [])

// 第 8 页结束
