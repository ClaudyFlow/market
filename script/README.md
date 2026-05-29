# Market 项目脚本目录说明

## 目录结构

```
market/
├── script/
│   ├── build/                    # 数据库和后端构建脚本
│   │   ├── db/                   # 数据库脚本
│   │   │   ├── init-market-db.sql     # 完整数据库初始化（含用户、数据库、表、初始数据）
│   │   │   ├── reset-market-db.sql    # 数据库重置脚本（清空并重新初始化）
│   │   │   ├── clean-market-db.sql    # 清空数据库（保留表结构）
│   │   │   ├── check-market.sql       # 检查数据库状态
│   │   │   ├── create-market-user.sql # 创建数据库用户
│   │   │   └── *.sql                  # 其他SQL检查脚本
│   │   ├── backend/               # 后端构建脚本
│   │   │   ├── build-backend.bat
│   │   │   ├── start-backend.bat
│   │   │   └── start-database.bat
│   │   ├── frontend/              # 前端构建脚本
│   │   │   ├── build-frontend.bat
│   │   │   └── start-frontend.bat
│   │   ├── init-db.bat            # 数据库初始化入口
│   │   ├── init-postgresql.sql    # PostgreSQL初始化SQL
│   │   ├── init-postgresql.ps1    # PostgreSQL初始化PowerShell脚本
│   │   └── *.bat / *.ps1          # 各种辅助脚本
│   │
│   ├── test/                     # 测试脚本
│   │   ├── integration/           # 集成测试
│   │   │   ├── integration-test.js
│   │   │   ├── integration-test-report.html
│   │   │   ├── run-integration-tests.bat
│   │   │   └── run-integration-tests.sh
│   │   ├── api-*.ps1              # API测试脚本
│   │   ├── test-*.ps1             # 功能测试脚本
│   │   ├── debug-token.ps1        # Token调试脚本
│   │   └── *.md                   # 测试相关文档
│   │
│   ├── jj-init.bat               # JJTree初始化
│   └── test.bat                  # 测试入口
│
├── backend/                     # 后端代码 (Spring Boot)
├── frontend/                    # 前端代码 (Vue/TypeScript)
├── doc/                         # 项目文档
├── depend/                      # 依赖文件
└── commit/                      # 提交记录
```

## 快速开始

### 1. 初始化数据库

```powershell
# 使用 init-db.bat
cd market
script\build\init-db.bat

# 或手动执行SQL
psql -U postgres -f script\build\db\init-market-db.sql
```

### 2. 启动后端

```powershell
cd backend
mvn spring-boot:run -DskipTests
```

### 3. 运行测试

```powershell
# API测试
powershell -ExecutionPolicy Bypass -File "script\test\api-quick-test.ps1"

# 集成测试
script\test\integration\run-integration-tests.bat
```

## 数据库配置

- 用户名: market
- 密码: market
- 数据库: market
- 地址: localhost:5432

## 测试账号

| 角色 | 用户名 | 密码 |
|------|--------|------|
| 管理员 | admin | admin123 |
| 商家 | merchant1 | 123456 |
| 用户 | user1 | 123456 |