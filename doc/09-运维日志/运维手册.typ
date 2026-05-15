// 运维手册 - Typst 格式

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

#v(85mm)
#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  运维手册]]
#pagebreak()

#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——运维手册]]
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

= 1. 系统监控

== 1.1 监控指标
#align(center)[#table(
  columns: (4fr, 4fr, 5fr),
  stroke: 0.5pt,
  [指标], [工具], [告警阈值],
  [CPU使用率], [Prometheus], [> 80%],
  [内存使用率], [Prometheus], [> 85%],
  [磁盘使用率], [Node Exporter], [> 90%],
  [数据库连接数], [pg_stat], [> 80%],
  [API响应时间], [Actuator], [> 1000ms],
  [错误率], [Loki + Promtail], [> 5%],
)]

== 1.2 Spring Boot Actuator
#h(2.0em)配置: management.endpoints.web.exposure.include=health,info,metrics\
#h(2.0em)访问: http://localhost:8080/actuator/health

= 2. 日志管理

== 2.1 日志位置
#align(center)[#table(
  columns: (4fr, 5fr, 5fr),
  stroke: 0.5pt,
  [组件], [日志路径], [说明],
  [后端], [logs/market.log], [应用日志],
  [Nginx], [/var/log/nginx/], [访问/错误日志],
  [PostgreSQL], [/var/log/postgresql/], [数据库日志],
  [Redis], [/var/log/redis/], [缓存日志],
)]

== 2.2 日志轮转
#h(2.0em)——配置 daily 轮转\
#h(2.0em)——保留 30 天\
#h(2.0em)——启用压缩

== 2.3 日志查询
#h(2.0em)——查看最近错误: tail -f logs/market.log | grep ERROR\
#h(2.0em)——搜索特定用户: grep "user_id=123" logs/market.log

= 3. 数据备份

== 3.1 数据库备份
#h(2.0em)——全量备份（每天凌晨2点）: pg_dump -U market market > backup.sql\
#h(2.0em)——保留30天备份\
#h(2.0em)——恢复: psql -U market market < backup.sql

== 3.2 Redis 备份
#h(2.0em)——配置自动保存: save 900 1, save 300 10, save 60 10000\
#h(2.0em)——手动保存: redis-cli BGSAVE

== 3.3 备份验证
#h(2.0em)——定期验证备份文件完整性\
#h(2.0em)——gunzip -t backup.sql.gz

= 4. 日常运维操作

== 4.1 服务启停
#h(2.0em)——后端: sudo systemctl restart market-backend\
#h(2.0em)——Nginx: sudo systemctl restart nginx\
#h(2.0em)——PostgreSQL: sudo systemctl restart postgresql\
#h(2.0em)——Redis: sudo systemctl restart redis

== 4.2 缓存清理
#h(2.0em)——清理Redis缓存: redis-cli FLUSHDB\
#h(2.0em)——清理特定缓存: redis-cli KEYS "product:\*" | xargs redis-cli DEL

== 4.3 数据库维护
#h(2.0em)——分析表统计信息: ANALYZE\
#h(2.0em)——清理死元组: VACUUM\
#h(2.0em)——重建索引: REINDEX DATABASE market

= 5. 故障处理

== 5.1 常见问题

=== 问题1: 服务无法启动
#h(2.0em)——检查端口占用: netstat -tulpn | grep :8080\
#h(2.0em)——检查日志: tail -f logs/market.log\
#h(2.0em)——检查数据库连接: pg_isready

=== 问题2: 数据库连接失败
#h(2.0em)——检查PostgreSQL状态\
#h(2.0em)——检查连接配置\
#h(2.0em)——测试连接: psql -U market -h localhost -d market -c "SELECT 1"

=== 问题3: 磁盘空间不足
#h(2.0em)——检查磁盘使用: df -h\
#h(2.0em)——清理日志\
#h(2.0em)——清理Docker: docker system prune -a

== 5.2 应急响应流程
#h(2.0em)1. 发现问题 → 监控告警 / 用户反馈\
#h(2.0em)2. 确认问题 → 查看日志、监控\
#h(2.0em)3. 隔离问题 → 确定影响范围\
#h(2.0em)4. 解决问题 → 修复 / 回滚 / 重启\
#h(2.0em)5. 验证修复 → 功能测试\
#h(2.0em)6. 记录总结 → 更新故障知识库

= 6. 性能优化

== 6.1 JVM 参数优化
#h(2.0em)java -Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200 -jar market.jar

== 6.2 数据库优化
#h(2.0em)——添加索引: CREATE INDEX idx_orders_user_id ON orders(user_id)\
#h(2.0em)——查询优化: EXPLAIN ANALYZE

== 6.3 Nginx 优化
#h(2.0em)——开启gzip压缩\
#h(2.0em)——静态资源缓存: expires 30d

#v(2cm)
#align(center)[#text(10pt, font: "SimSun")[文档版本：v1.0.0 | 最后更新：#datetime.today().display()]]
