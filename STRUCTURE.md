# 项目结构说明

## 目录组织

```
market/
├── backend/                    # 后端代码
│   ├── src/                   # 源代码
│   └── pom.xml                # Maven 构建配置（从根目录移入）
│
├── frontend/                   # 前端代码
│
├── depend/                     # Docker 依赖配置（新增）
│   ├── docker-compose.yml     # Docker 编排配置（从根目录移入）
│   └── Dockerfile             # Java 应用镜像（从根目录移入）
│
├── script/                     # 脚本目录
│   ├── compile/               # 编译脚本
│   └── test/                  # 测试脚本（新增联调测试文件）
│       ├── INTEGRATION_TEST_GUIDE.md    # 联调测试文档
│       ├── integration-test.js          # 联调测试脚本
│       ├── package.json                 # 测试依赖配置
│       ├── run-integration-tests.bat    # Windows 测试启动脚本
│       └── run-integration-tests.sh     # Linux/Mac 测试启动脚本
│
├── doc/                        # 项目文档
├── scripts/                    # 数据库初始化脚本
├── monitoring/                 # Prometheus 监控配置
├── loki/                       # Loki 日志配置
├── promtail/                   # Promtail 日志收集配置
│
├── pom.xml                     # 根目录 Maven 配置（保留，用于 IDE 识别）
└── package-lock.json           # Node.js 依赖锁定
```

## 主要变更

### 1. 联调测试文件迁移
- **从**: 根目录
- **到**: `script/test/`
- **包含**: 测试文档、测试脚本、运行脚本

### 2. pom.xml 调整
- **backend/pom.xml**: 项目主要构建配置，路径已更新为相对 src 目录
- **根目录 pom.xml**: 保留用于 IDE 项目识别

### 3. Docker 配置迁移
- **从**: 根目录 `docker-compose.yml` 和 `Dockerfile`
- **到**: `depend/` 目录
- **说明**: 所有 Docker 相关配置统一放在 depend 目录

## 快速开始

### 运行联调测试
```bash
cd script/test
run-integration-tests.bat   # Windows
# 或
./run-integration-tests.sh  # Linux/Mac
```

### 启动 Docker 环境
```bash
cd depend
docker-compose up -d
```

### 构建后端
```bash
cd backend
mvn clean package
```

## 注意事项

1. **Docker 构建**: 需要在 `depend/` 目录下执行，context 已配置为父目录
2. **联调测试**: 需要在 `script/test/` 目录下执行，脚本会自动定位后端路径
3. **Maven 构建**: 可以在 `backend/` 目录下直接使用 `mvn` 命令
