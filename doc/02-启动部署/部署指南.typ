// 部署指南 - Typst 格式

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

#v(85mm)
#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  部署指南]]
#pagebreak()

#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——部署指南]]
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

= 1. 环境要求

== 1.1 软件依赖
#align(center)[#table(
  columns: (4fr, 3fr, 5fr),
  stroke: 0.5pt,
  [软件], [版本], [用途],
  [JDK], [21 LTS], [后端运行环境],
  [Node.js], [18+], [前端开发],
  [PostgreSQL], [14+], [主数据库],
  [Redis], [6+], [缓存服务],
  [Nginx], [1.18+], [反向代理],
  [Docker], [最新], [容器化部署],
)]

== 1.2 硬件要求
#align(center)[#table(
  columns: (3fr, 3fr, 3fr, 3fr),
  stroke: 0.5pt,
  [环境], [CPU], [内存], [磁盘],
  [开发], [2核], [4GB], [20GB],
  [测试], [4核], [8GB], [50GB],
  [生产], [8核+], [16GB+], [100GB+],
)]

= 2. 本地开发部署

== 2.1 后端部署
#h(2.0em)1. 克隆项目: git clone <repository-url>\
#h(2.0em)2. 配置数据库: PostgreSQL 和 Redis\
#h(2.0em)3. 编辑 application.properties 配置\
#h(2.0em)4. 启动后端: mvnd spring-boot:run\
#h(2.0em)访问: http://localhost:8080

== 2.2 前端部署
#h(2.0em)1. 安装依赖: cd frontend && npm install\
#h(2.0em)2. 启动开发服务器: npm run dev\
#h(2.0em)访问: http://localhost:5173

= 3. Docker 部署

== 3.1 使用 docker-compose
#h(2.0em)——启动所有服务: docker-compose up -d\
#h(2.0em)——查看状态: docker-compose ps\
#h(2.0em)——查看日志: docker-compose logs -f

== 3.2 服务组成
#h(2.0em)——postgres: PostgreSQL 数据库\
#h(2.0em)——redis: Redis 缓存\
#h(2.0em)——backend: 后端应用\
#h(2.0em)——frontend: 前端应用

= 4. 生产环境部署

== 4.1 后端部署
#h(2.0em)1. 打包应用: mvnd clean package -DskipTests\
#h(2.0em)2. 运行 JAR: java -jar market.jar --spring.profiles.active=prod\
#h(2.0em)3. 配置 systemd 服务管理

== 4.2 前端部署
#h(2.0em)1. 构建生产版本: npm run build\
#h(2.0em)2. 配置 Nginx 反向代理\
#h(2.0em)3. 配置 SSL 证书 (Let's Encrypt)

== 4.3 Nginx 配置要点
#h(2.0em)——前端静态文件: root /var/www/market/frontend/dist\
#h(2.0em)——后端API代理: proxy_pass http://localhost:8080\
#h(2.0em)——WebSocket代理: 配置 Upgrade 和 Connection 头

= 5. 部署验证

== 5.1 健康检查
#h(2.0em)——后端: curl http://localhost:8080/actuator/health\
#h(2.0em)——前端: curl http://localhost:80

== 5.2 功能测试清单
#h(2.0em)——[ ] 用户注册/登录\
#h(2.0em)——[ ] 商品浏览\
#h(2.0em)——[ ] 购物车操作\
#h(2.0em)——[ ] 订单创建\
#h(2.0em)——[ ] 支付流程

#v(2cm)
#align(center)[#text(10pt, font: "SimSun")[文档版本：v1.0.0 | 最后更新：#datetime.today().display()]]
