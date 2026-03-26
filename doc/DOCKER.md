# Docker 环境快速启动指南

## 目录结构

```
market/
├── docker-compose.yml      # Docker 编排配置
├── Dockerfile              # Java 应用镜像
├── frontend/nginx/
│   ├── Dockerfile          # Nginx 镜像
│   ├── conf/nginx.conf     # Nginx 配置
│   └── html/               # 前端静态文件
├── monitoring/             # Prometheus 配置
├── loki/                   # Loki 日志配置
├── promtail/               # Promtail 日志收集
└── scripts/
    └── init.sql            # 数据库初始化脚本
```

## 快速启动

### 1. 首次启动（构建镜像）

```bash
# 创建必要目录
mkdir -p data/postgres data/redis logs frontend/nginx/html

# 构建并启动所有服务
docker-compose up -d --build
```

### 2. 查看运行状态

```bash
# 查看容器状态
docker-compose ps

# 查看日志
docker-compose logs -f

# 查看特定服务日志
docker-compose logs -f app
docker-compose logs -f nginx
```

### 3. 访问服务

| 服务 | 地址 | 说明 |
|------|------|------|
| Nginx | http://localhost | 前端 + 反向代理 |
| Spring Boot | http://localhost:8080 | 后端 API |
| PostgreSQL | localhost:5432 | 数据库 |
| Redis | localhost:6379 | 缓存 |
| Prometheus | http://localhost:9090 | 监控 |
| Grafana | http://localhost:3000 | 可视化 (admin/admin123) |
| Loki | http://localhost:3100 | 日志聚合 |

### 4. 启动监控服务（可选）

```bash
# 启动完整监控套件
docker-compose --profile monitoring up -d
```

## 常用命令

```bash
# 停止所有服务
docker-compose down

# 停止并删除数据卷（⚠️ 会删除数据库数据）
docker-compose down -v

# 重启特定服务
docker-compose restart app
docker-compose restart nginx

# 进入容器
docker exec -it market-app bash
docker exec -it market-postgres bash
docker exec -it market-redis redis-cli

# 查看资源使用
docker stats
```

## 前端部署

```bash
# 1. 构建前端
cd frontend
npm run build

# 2. 复制构建文件到 nginx/html
cp -r dist/* nginx/html/

# 3. 重启 Nginx
docker-compose restart nginx
```

## 故障排查

### 容器启动失败
```bash
# 查看日志
docker-compose logs app

# 检查配置
docker-compose config
```

### 数据库连接问题
```bash
# 进入数据库容器
docker exec -it market-postgres psql -U admin -d market

# 检查数据库
\l
\dt
```

### 网络问题
```bash
# 查看网络
docker network ls

# 检查容器 IP
docker inspect market-app | grep IPAddress
```
