# 市场平台 - 项目文档

本目录包含市场平台项目的完整技术文档。

## 📚 文档列表

### Markdown 文档

| 文档名称 | 说明 |
|----------|------|
| [README.md](./README.md) | 文档导航和说明 |
| [MESSAGE_STATUS.md](./MESSAGE_STATUS.md) | 消息状态码系统完整文档 |
| [DATABASE.md](./DATABASE.md) | 数据库设计文档 |
| [DOCKER.md](./DOCKER.md) | Docker 部署指南 |
| [CONFIG.md](./CONFIG.md) | 配置说明文档 |
| [LOGGING.md](./LOGGING.md) | 日志系统文档 |
| [BACKEND_SUMMARY.md](./BACKEND_SUMMARY.md) | 后端功能总结 |

### Typst 文档

| 文档名称 | 说明 |
|----------|------|
| [项目简介.typ](./项目简介.typ) | 项目概述、系统架构、技术栈、功能特性和部署方式介绍 |
| [前端功能介绍.typ](./前端功能介绍.typ) | 前端应用的功能模块、组件架构、数据管理和用户体验特性详解 |
| [前端接口文档.typ](./前端接口文档.typ) | 前端组件接口规范、数据结构定义和 API 设计文档 |
| [部署配置指南.typ](./部署配置指南.typ) | 系统部署流程、配置参数、服务维护和故障处理指南 |
| [数据库结构文档.typ](./数据库结构文档.typ) | 数据库表结构设计、关系图和 SQL 脚本 |
| [手动测试检查清单.typ](./手动测试检查清单.typ) | 功能测试用例、兼容性测试和性能测试检查项 |

## 📖 快速开始

### 新成员阅读顺序

1. **[项目简介.typ](./项目简介.typ)** - 了解项目整体情况
2. **[前端功能介绍.typ](./前端功能介绍.typ)** - 熟悉前端功能模块
3. **[部署配置指南.typ](./部署配置指南.typ)** - 搭建开发环境
4. **[数据库结构文档.typ](./数据库结构文档.typ)** - 了解数据结构
5. **[手动测试检查清单.typ](./手动测试检查清单.typ)** - 学习测试流程

### 按角色阅读

#### 前端开发人员
- [前端功能介绍.typ](./前端功能介绍.typ)
- [前端接口文档.typ](./前端接口文档.typ)
- [MESSAGE_STATUS.md](./MESSAGE_STATUS.md) - 消息状态码系统

#### 后端开发人员
- [数据库结构文档.typ](./数据库结构文档.typ)
- [BACKEND_SUMMARY.md](./BACKEND_SUMMARY.md)
- [DATABASE.md](./DATABASE.md)

#### 测试人员
- [手动测试检查清单.typ](./手动测试检查清单.typ)
- [前端接口文档.typ](./前端接口文档.typ)
- [MESSAGE_STATUS.md](./MESSAGE_STATUS.md) - 消息状态测试

#### 运维人员
- [部署配置指南.typ](./部署配置指南.typ)
- [DOCKER.md](./DOCKER.md)
- [CONFIG.md](./CONFIG.md)
- [LOGGING.md](./LOGGING.md)

## 🛠️ 文档构建

### Typst 文档编译

**环境要求：**
- [Typst](https://typst.app/) 0.11.0 或更高版本
- 中文字体支持（SimHei、SimSun 等）

**编译命令：**
```bash
# 编译单个文档
typst compile 项目简介.typ
typst compile 前端功能介绍.typ
typst compile 部署配置指南.typ

# Watch 模式实时预览
typst watch 项目简介.typ
```

### Markdown 文档

Markdown 文档可直接在 GitHub 或 Markdown 编辑器中查看。

## 📝 文档规范

### 排版格式（Typst）

- 纸张：A4
- 页边距：内侧 25mm，外侧 20mm，顶部 35mm，底部 30mm
- 正文字体：五号宋体（10.5pt）
- 标题字体：五号黑体（10.5pt）
- 行距：1.25 倍

### 版本管理

- 文档应随代码变更同步更新
- 重大功能变更需要更新相关文档
- 版本号应在文档中标注
- 最后更新日期应保持一致

## 🔗 相关链接

- [Typst 官方文档](https://typst.app/docs/)
- [Typst 中文社区](https://github.com/typst-cn)
- [项目主仓库](https://github.com/market-platform)

## 📧 反馈与支持

如文档有任何问题或建议，请通过以下方式联系我们：

- 📧 Email: support@market.com
- 🐙 GitHub Issues: [提交问题](https://github.com/market-platform/issues)

---

**最后更新：** 2026 年 3 月 31 日  
**文档版本：** v1.1.0
