# 编译脚本说明

> **负责人**: complie
> **用途**: 前后端编译、构建、服务管理
> **最后更新**: 2026-04-06

---

## 📁 脚本清单

### 编译脚本 (根目录)

| 脚本 | 用途 | 说明 |
|------|------|------|
| `compile-all.bat` | 完整编译 (前后端) | Batch 版本 |
| `compile-all.nu` | 完整编译 (前后端) | Nushell 版本 (推荐) |

### 子目录

| 目录 | 说明 |
|------|------|
| `backend/` | 后端编译相关 |
| `frontend/` | 前端编译相关 |

---

## 🔧 环境要求

### 必需工具

| 工具 | 版本 | 来源 | 说明 |
|------|------|------|------|
| Java | 21.0.10 | winget (Oracle.JDK.21) | 后端运行环境 |
| mvnd | 1.0.5 | winget | Maven Daemon |
| Node.js | 24.14.1 | winget (OpenJS.NodeJS) | 前端运行环境 |
| npm | 11.11.0 | 随 Node.js 安装 | 包管理器 |
| Nginx | 1.29.7 | **预置在仓库** | 前端服务器 |
| PostgreSQL | 18.3-2 | winget (PostgreSQL.PostgreSQL) | 数据库 |
| Redis | 3.0.504 | winget (Redis.Redis) | 缓存服务 |

### 工具官网

| 工具 | 官网 |
|------|------|
| Java | https://www.oracle.com/java/technologies/downloads/ |
| Maven Daemon | https://github.com/mvndaemon/mvnd |
| Node.js | https://nodejs.org/ |
| Nginx | https://nginx.org/ |
| PostgreSQL | https://www.postgresql.org/ |
| Redis | https://redis.io/ |

### Winget 包列表

```bash
# 安装所有必需的工具
winget install Oracle.JDK.21
winget install OpenJS.NodeJS
winget install PostgreSQL.PostgreSQL
winget install Redis.Redis
```

> **注意**: Nginx 已预置在仓库的 `frontend/nginx/` 目录中，无需单独安装。

---

## 🚀 使用方法

### 快速编译 (一键完成)

**BAT 版本**:
```bat
script\compile\compile-all.bat
```

**Nushell 版本** (推荐):
```bash
nu script\compile\compile-all.nu
```

### 编译流程

1. ✅ 检查 Java 环境
2. ✅ 检查 Maven Daemon (mvnd)
3. ✅ 检查 Node.js (如缺失则自动 winget 安装)
4. ✅ 检查 Nginx (预置在仓库)
5. ✅ 检查 PostgreSQL 服务
6. ✅ 检查 Redis 服务
7. ✅ 编译前端 (Vite + Vue 3)
8. ✅ 编译后端 (Spring Boot + mvnd)
9. ✅ 重启 Nginx

---

## 📊 技术栈版本

### 前端

| 技术 | 版本 | 说明 |
|------|------|------|
| Vue | 3.5.30 | 前端框架 |
| Vite | 5.4.21 | 构建工具 |
| Node.js | 24.14.1 | 运行环境 |
| npm | 11.11.0 | 包管理器 |
| Nginx | 1.29.7 | 生产服务器 |

### 后端

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21.0.10 LTS | 运行环境 |
| Spring Boot | 3.4.0 | 后端框架 |
| mvnd | 1.0.5 | 构建工具 |
| PostgreSQL | 18.3-2 | 数据库 |
| Redis | 3.0.504 | 缓存 |

---

## 🌐 访问地址

| 服务 | 地址 | 说明 |
|------|------|------|
| 前端生产环境 | http://localhost:80 | Nginx |
| 前端开发服务器 | http://localhost:5173 | Vite Dev Server |
| 后端 API | http://localhost:8080/api/ | Spring Boot |
| 数据库 | localhost:5432 | PostgreSQL |
| 缓存 | localhost:6379 | Redis |

---

## ⚠️ 注意事项

1. **Nginx 已预置**: 位于 `frontend/nginx/` 目录，无需下载
2. **Node.js 自动安装**: 如未检测到，脚本会自动使用 winget 安装
3. **编译前检查**: 脚本会自动检查所有依赖并提示缺失项
4. **Nginx 自动重启**: 前端编译完成后会自动重启 Nginx

---

*负责人: complie | 创建时间: 2026-04-06 | 最后更新: 2026-04-06*
