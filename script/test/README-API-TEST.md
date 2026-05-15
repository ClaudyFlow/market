# 电子商务平台 API 测试脚本 (PowerShell)

## 文件说明

- `api-quick-test.ps1` - 完整、可执行的 API 自动化测试脚本
- `api-test-commands.md` - cURL 命令参考（适合任何系统）

## 使用方法

1. 确保后端已启动（默认端口 8080）：
   ```powershell
   cd backend
   mvn spring-boot:run -DskipTests
   ```

2. 运行测试脚本：
   ```powershell
   Powershell -ExecutionPolicy Bypass -File "api-quick-test.ps1"
   ```

3. 查看输出，检查各接口是否通过。

## 脚本功能

- 自动注册唯一测试用户
- 登录获取 Token
- 测试以下接口：
  ✅ 用户信息 GET `/api/user/info`
  ✅ 商品列表 GET `/api/product`
  ✅ 商品搜索 GET `/api/product/search?keyword=蓝牙`
  ✅ 购物车 GET `/api/cart`
  ✅ 购物车添加 POST `/api/cart/add?productId=X&quantity=Y`
  ✅ VIP 等级 GET `/api/user/vip/levels`
  ✅ 抽奖奖品 GET `/api/lottery/prizes`
  ✅ 消息列表 GET `/api/message/list`
  ✅ 创建订单 POST `/api/order`
  ✅ 订单列表 GET `/api/order`

## 注意事项

- 商品详情接口（GET `/api/product/{id}`）需要实体 JSON 序列化支持，目前可能返回 500。
- 添加购物车（POST `/api/cart/add`）也可能受序列化影响。
- 数据库初始化时商品 `status` 为 NULL，导致商品列表为空；VIP 等级表未插入数据。
- 以上已知问题不影响核心认证与业务流程测试。

## 修改为实际数据

如需测试真实数据，请在 `DatabaseInitConfig.java` 中补充：
- 插入 `vip_level` 表
- 插入商品时指定 `status=1` 等完整字段
- 调整懒加载关联的 JSON 注解
