// 开发规范 - Typst 格式

#set page(
  paper: "a4",
  margin: (inside: 25mm, outside: 20mm, top: 35mm, bottom: 30mm),
)

#v(85mm)
#align(center)[#text(26pt, font: "SimHei")[购物商城系统\
  开发规范]]
#pagebreak()

#set page(
  numbering: "1",
  header: [
    #place(top + center, dy: 25mm)[#text(10.5pt, font: "SimHei")[购物商城系统——开发规范]]
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

= 1. 前端规范

== 1.1 命名规范
#h(2.0em)——文件名: PascalCase (组件) 或 camelCase (工具)\
#h(2.0em)——变量/函数: camelCase\
#h(2.0em)——常量: UPPER_SNAKE_CASE

== 1.2 组件规范
#h(2.0em)使用 Vue 3 Composition API (`<script setup lang="ts">`)\
#h(2.0em)——导入声明\
#h(2.0em)——类型定义\
#h(2.0em)——Props 定义\
#h(2.0em)——响应式数据\
#h(2.0em)——计算属性\
#h(2.0em)——方法

== 1.3 API 调用规范
#h(2.0em)——使用统一的 API 模块\
#h(2.0em)——禁止直接使用 axios\
#h(2.0em)——统一错误处理

= 2. 后端规范

== 2.1 命名规范
#h(2.0em)——类名: PascalCase\
#h(2.0em)——方法/变量: camelCase\
#h(2.0em)——常量: UPPER_SNAKE_CASE

== 2.2 Controller 规范
#h(2.0em)——使用 @`RestController 和 @`RequestMapping\
#h(2.0em)——方法添加 JavaDoc 注释\
#h(2.0em)——统一返回 Result<T> 格式

== 2.3 统一返回格式
#h(2.0em)Result<T> 包含: code, message, data\
#h(2.0em)——成功: Result.success(data)\
#h(2.0em)——失败: Result.error(message)

== 2.4 异常处理
#h(2.0em)——使用 @`ControllerAdvice 全局异常处理\
#h(2.0em)——自定义 BusinessException\
#h(2.0em)——记录错误日志

= 3. Git 规范

== 3.1 分支策略
#h(2.0em)——main: 生产分支\
#h(2.0em)——dev: 开发分支\
#h(2.0em)——feature/xxx: 功能分支\
#h(2.0em)——hotfix/xxx: 修复分支

== 3.2 提交信息格式
#h(2.0em)<type>(<scope>): <subject>\
#h(2.0em)——feat: 新功能\
#h(2.0em)——fix: 修复bug\
#h(2.0em)——docs: 文档更新\
#h(2.0em)——refactor: 重构\
#h(2.0em)——test: 测试

= 4. 注释规范

== 4.1 类注释
#h(2.0em)——类功能说明\
#h(2.0em)——@`author 开发团队\
#h(2.0em)——@`since 创建日期

== 4.2 方法注释
#h(2.0em)——方法功能说明\
#h(2.0em)——@`param 参数说明\
#h(2.0em)——@`return 返回值\
#h(2.0em)——@`throws 异常说明

#v(2cm)
#align(center)[#text(10pt, font: "SimSun")[文档版本：v1.0.0 | 最后更新：#datetime.today().display()]]
