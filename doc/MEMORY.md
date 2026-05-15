# 项目长期记忆

## 工作背景
用户在 Windows (win32) 环境下使用 PowerShell Core 工作，主导一个名为 market 的前端电商项目（Vue 3 + TypeScript + Pinia）。

## 个人偏好
- 用户明确禁止在 Rust 项目结构中使用 mod.rs 模式，工具调用参数优先使用绝对路径而非相对路径
- 测试场景中需要看到实际的输入输出数据，而非仅关注 pass/fail 结果
- 遇到复杂方案时用户倾向于取消长时间运行的操作并重新定义问题，简化学方向

## 当前关注
market 前端项目涉及 Vue 3 电商平台开发，包含用户端和管理端。技术栈为 Vue 3 + TypeScript + Vite + Pinia + Element Plus。用户关注功能完善、UI/UX优化以及代码质量。

## 近期动态
- 完善了购物车功能：本地购物车 store (cart-local.ts)，支持 localStorage 持久化
- 完善了商店页面：ShopDetail.vue 商店详情页，三标签页布局（首页/商品/分类）
- 修改商品标签颜色从蓝色改为黑底白字
- 实现了客服聊天功能：WebSocket 网络库、聊天 Store、ChatWidget 组件
- Header 购物车按钮添加了商品数量标签

## 技术栈
- **框架**: Vue 3 (Composition API) + TypeScript
- **构建工具**: Vite
- **状态管理**: Pinia
- **UI 组件库**: Element Plus
- **路由**: Vue Router 4
- **样式**: SCSS
- **网络请求**: Axios
- **实时通信**: WebSocket (自建网络库)
