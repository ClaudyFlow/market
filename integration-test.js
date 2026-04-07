/**
 * 前后端联调测试脚本
 * 使用 Node.js + Axios 模拟前端调用后端 API
 * 
 * 运行方式:
 *   node integration-test.js
 * 
 * 功能:
 *   1. 测试所有核心 API 端点
 *   2. 验证前后端数据格式兼容性
 *   3. 生成测试报告
 */

const axios = require('axios');
const fs = require('fs');
const path = require('path');

// 配置
const BASE_URL = process.env.BACKEND_URL || 'http://localhost:8080';
const REPORT_FILE = 'integration-test-report.html';

// 测试结果
const testResults = {
  total: 0,
  passed: 0,
  failed: 0,
  tests: [],
  startTime: null,
  endTime: null
};

// 颜色输出
const colors = {
  reset: '\x1b[0m',
  green: '\x1b[32m',
  red: '\x1b[31m',
  yellow: '\x1b[33m',
  blue: '\x1b[34m',
  cyan: '\x1b[36m'
};

function log(color, message) {
  console.log(`${color}${message}${colors.reset}`);
}

// 创建 axios 实例
const api = axios.create({
  baseURL: `${BASE_URL}/api`,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json'
  }
});

// 请求拦截器
api.interceptors.request.use(config => {
  if (global.authToken) {
    config.headers.Authorization = `Bearer ${global.authToken}`;
  }
  return config;
});

// 响应拦截器
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response) {
      return Promise.reject(error.response);
    }
    return Promise.reject(error);
  }
);

/**
 * 执行单个测试
 */
async function runTest(name, testFn) {
  testResults.total++;
  const startTime = Date.now();
  
  try {
    await testFn();
    const duration = Date.now() - startTime;
    testResults.passed++;
    testResults.tests.push({
      name,
      status: '✅ PASS',
      duration: `${duration}ms`,
      error: null
    });
    log(colors.green, `✅ [${duration}ms] ${name}`);
  } catch (error) {
    const duration = Date.now() - startTime;
    testResults.failed++;
    const errorMsg = error.data?.message || error.message || 'Unknown error';
    testResults.tests.push({
      name,
      status: '❌ FAIL',
      duration: `${duration}ms`,
      error: errorMsg
    });
    log(colors.red, `❌ [${duration}ms] ${name}`);
    log(colors.red, `   错误: ${errorMsg}`);
  }
}

/**
 * 生成 HTML 测试报告
 */
function generateReport() {
  const html = `
<!DOCTYPE html>
<html lang="zh-CN">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>前后端联调测试报告</title>
  <style>
    * { margin: 0; padding: 0; box-sizing: border-box; }
    body {
      font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', sans-serif;
      background: #f5f7fa;
      padding: 20px;
    }
    .container {
      max-width: 1200px;
      margin: 0 auto;
      background: white;
      border-radius: 8px;
      box-shadow: 0 2px 12px rgba(0,0,0,0.1);
      overflow: hidden;
    }
    .header {
      background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
      color: white;
      padding: 30px;
    }
    .header h1 {
      font-size: 28px;
      margin-bottom: 10px;
    }
    .header p {
      opacity: 0.9;
      font-size: 14px;
    }
    .stats {
      display: flex;
      gap: 20px;
      padding: 20px 30px;
      background: #f8f9fa;
      border-bottom: 1px solid #e9ecef;
    }
    .stat-item {
      flex: 1;
      text-align: center;
      padding: 15px;
      background: white;
      border-radius: 6px;
    }
    .stat-item .number {
      font-size: 32px;
      font-weight: bold;
      margin-bottom: 5px;
    }
    .stat-item .label {
      font-size: 12px;
      color: #6c757d;
    }
    .stat-item.total .number { color: #667eea; }
    .stat-item.passed .number { color: #28a745; }
    .stat-item.failed .number { color: #dc3545; }
    .stat-item.rate .number { color: #17a2b8; }
    .test-list {
      padding: 20px 30px;
    }
    .test-item {
      padding: 15px;
      border-left: 4px solid #e9ecef;
      margin-bottom: 10px;
      background: #f8f9fa;
      border-radius: 4px;
    }
    .test-item.pass {
      border-left-color: #28a745;
      background: #f0fff4;
    }
    .test-item.fail {
      border-left-color: #dc3545;
      background: #fff5f5;
    }
    .test-item .test-name {
      font-weight: 600;
      margin-bottom: 5px;
    }
    .test-item .test-meta {
      font-size: 12px;
      color: #6c757d;
    }
    .test-item .test-error {
      margin-top: 8px;
      padding: 8px;
      background: white;
      border-radius: 4px;
      font-size: 12px;
      color: #dc3545;
      font-family: monospace;
    }
    .footer {
      padding: 20px 30px;
      text-align: center;
      color: #6c757d;
      font-size: 12px;
      border-top: 1px solid #e9ecef;
    }
    .progress-bar {
      height: 8px;
      background: #e9ecef;
      border-radius: 4px;
      overflow: hidden;
      margin: 20px 30px 0;
    }
    .progress-fill {
      height: 100%;
      background: linear-gradient(90deg, #28a745 0%, #20c997 100%);
      transition: width 0.3s ease;
    }
  </style>
</head>
<body>
  <div class="container">
    <div class="header">
      <h1>🔗 前后端联调测试报告</h1>
      <p>生成时间: ${testResults.endTime}</p>
      <p>后端地址: ${BASE_URL}</p>
    </div>
    
    <div class="stats">
      <div class="stat-item total">
        <div class="number">${testResults.total}</div>
        <div class="label">总测试数</div>
      </div>
      <div class="stat-item passed">
        <div class="number">${testResults.passed}</div>
        <div class="label">通过</div>
      </div>
      <div class="stat-item failed">
        <div class="number">${testResults.failed}</div>
        <div class="label">失败</div>
      </div>
      <div class="stat-item rate">
        <div class="number">${testResults.total > 0 ? Math.round(testResults.passed * 100 / testResults.total) : 0}%</div>
        <div class="label">通过率</div>
      </div>
    </div>
    
    <div class="progress-bar">
      <div class="progress-fill" style="width: ${testResults.total > 0 ? (testResults.passed * 100 / testResults.total) : 0}%"></div>
    </div>
    
    <div class="test-list">
      <h3 style="margin-bottom: 15px;">测试详情</h3>
      ${testResults.tests.map(test => `
        <div class="test-item ${test.status.includes('PASS') ? 'pass' : 'fail'}">
          <div class="test-name">${test.status} ${test.name}</div>
          <div class="test-meta">
            <span>⏱️ ${test.duration}</span>
          </div>
          ${test.error ? `<div class="test-error">❌ ${test.error}</div>` : ''}
        </div>
      `).join('')}
    </div>
    
    <div class="footer">
      <p>Market Platform 前后端联调测试 | Generated by Integration Test Script</p>
    </div>
  </div>
</body>
</html>
  `;

  fs.writeFileSync(REPORT_FILE, html, 'utf8');
  log(colors.cyan, `\n📄 测试报告已生成: ${path.resolve(REPORT_FILE)}`);
}

/**
 * 主测试流程
 */
async function runAllTests() {
  log(colors.cyan, '\n' + '='.repeat(60));
  log(colors.cyan, '🚀 开始执行前后端联调测试');
  log(colors.cyan, `📡 后端地址: ${BASE_URL}`);
  log(colors.cyan, '='.repeat(60) + '\n');

  testResults.startTime = new Date().toISOString();

  // 1. 健康检查
  await runTest('健康检查 - 应用启动验证', async () => {
    const response = await axios.get(`${BASE_URL}/actuator/health`);
    if (response.data.status !== 'UP') {
      throw new Error('应用状态异常');
    }
  });

  // 2. 用户注册
  await runTest('用户注册 - 正常流程', async () => {
    const response = await api.post('/auth/register', {
      username: 'integration_test_user',
      password: 'Test123456!',
      email: 'integration@test.com',
      phone: '13900139000',
      nickname: '联调测试用户'
    });
    if (response.data.code !== 200) {
      throw new Error(response.data.message);
    }
  });

  // 3. 用户登录
  await runTest('用户登录 - 获取 Token', async () => {
    const response = await api.post('/auth/login', {
      username: 'testuser',
      password: 'testpassword'
    });
    if (response.data.code !== 200 || !response.data.data.token) {
      throw new Error('登录失败');
    }
    global.authToken = response.data.data.token;
  });

  // 4. 获取当前用户信息
  await runTest('获取当前用户信息', async () => {
    const response = await api.get('/auth/me');
    if (response.data.code !== 200) {
      throw new Error('获取用户信息失败');
    }
    if (response.data.data.password) {
      throw new Error('密码字段不应返回');
    }
  });

  // 5. 获取商品列表
  await runTest('获取商品列表 - 分页查询', async () => {
    const response = await api.get('/product', {
      params: { page: 0, size: 10 }
    });
    if (response.data.code !== 200) {
      throw new Error('获取商品列表失败');
    }
  });

  // 6. 获取商品详情
  await runTest('获取商品详情', async () => {
    const response = await api.get('/product/1');
    if (response.data.code !== 200) {
      throw new Error('获取商品详情失败');
    }
  });

  // 7. 搜索商品
  await runTest('搜索商品', async () => {
    const response = await api.get('/product/search', {
      params: { keyword: 'iPhone', page: 0, size: 10 }
    });
    if (response.data.code !== 200) {
      throw new Error('搜索商品失败');
    }
  });

  // 8. 获取分类列表
  await runTest('获取商品分类', async () => {
    const response = await api.get('/product/categories');
    if (response.data.code !== 200) {
      throw new Error('获取分类失败');
    }
  });

  // 9. 获取推荐商品
  await runTest('获取推荐商品', async () => {
    const response = await api.get('/product/recommended', {
      params: { page: 0, size: 5 }
    });
    if (response.data.code !== 200) {
      throw new Error('获取推荐商品失败');
    }
  });

  // 10. 收藏商品
  await runTest('收藏商品', async () => {
    const response = await api.post('/product/1/favorite');
    if (response.data.code !== 200) {
      throw new Error('收藏商品失败');
    }
  });

  // 11. 检查收藏状态
  await runTest('检查收藏状态', async () => {
    const response = await api.get('/product/1/favorite');
    if (response.data.code !== 200) {
      throw new Error('检查收藏状态失败');
    }
  });

  // 12. 添加浏览记录
  await runTest('添加浏览记录', async () => {
    const response = await api.post('/product/1/browse');
    if (response.data.code !== 200) {
      throw new Error('添加浏览记录失败');
    }
  });

  // 13. 获取购物车
  await runTest('获取购物车', async () => {
    const response = await api.get('/cart');
    if (response.data.code !== 200) {
      throw new Error('获取购物车失败');
    }
  });

  // 14. 创建订单
  await runTest('创建订单', async () => {
    const response = await api.post('/order', {
      productId: 1,
      quantity: 1,
      addressId: 1,
      remark: '联调测试订单'
    });
    if (response.data.code !== 200) {
      throw new Error('创建订单失败');
    }
    global.testOrderId = response.data.data.id;
  });

  // 15. 获取订单列表
  await runTest('获取订单列表', async () => {
    const response = await api.get('/order', {
      params: { page: 0, size: 10 }
    });
    if (response.data.code !== 200) {
      throw new Error('获取订单列表失败');
    }
  });

  // 16. 获取订单详情
  await runTest('获取订单详情', async () => {
    if (!global.testOrderId) {
      throw new Error('订单 ID 不存在');
    }
    const response = await api.get(`/order/${global.testOrderId}`);
    if (response.data.code !== 200) {
      throw new Error('获取订单详情失败');
    }
  });

  // 17. 支付订单
  await runTest('支付订单', async () => {
    if (!global.testOrderId) {
      throw new Error('订单 ID 不存在');
    }
    const response = await api.post(`/order/${global.testOrderId}/pay`, null, {
      params: { payMethod: 'ALIPAY' }
    });
    if (response.data.code !== 200) {
      throw new Error('支付订单失败');
    }
  });

  // 18. 查询支付状态
  await runTest('查询支付状态', async () => {
    if (!global.testOrderId) {
      throw new Error('订单 ID 不存在');
    }
    const response = await api.get(`/order/${global.testOrderId}/pay-status`);
    if (response.data.code !== 200) {
      throw new Error('查询支付状态失败');
    }
  });

  // 19. 取消订单
  await runTest('取消订单', async () => {
    if (!global.testOrderId) {
      throw new Error('订单 ID 不存在');
    }
    const response = await api.put(`/order/${global.testOrderId}/cancel`);
    if (response.data.code !== 200) {
      throw new Error('取消订单失败');
    }
  });

  // 20. 获取用户通知
  await runTest('获取用户通知', async () => {
    const response = await api.get('/notification', {
      params: { page: 0, size: 10 }
    });
    if (response.data.code !== 200) {
      throw new Error('获取通知失败');
    }
  });

  // 21. 获取浏览历史
  await runTest('获取浏览历史', async () => {
    const response = await api.get('/browse-history', {
      params: { page: 0, size: 10 }
    });
    if (response.data.code !== 200) {
      throw new Error('获取浏览历史失败');
    }
  });

  // 22. 未授权访问
  await runTest('未授权访问 - 应返回 401', async () => {
    delete global.authToken;
    try {
      await api.get('/auth/me');
      throw new Error('应该返回 401');
    } catch (error) {
      if (error.status === 401) {
        // 预期行为
        return;
      }
      throw error;
    }
  });

  // 23. 错误处理 - 不存在的商品
  await runTest('错误处理 - 不存在的商品', async () => {
    try {
      await api.get('/product/999999');
      // 可能返回 404 或空数据，都是可接受的
    } catch (error) {
      if (error.status !== 404) {
        throw new Error('应该返回 404');
      }
    }
  });

  // 24. 数据格式验证 - 商品价格格式
  await runTest('数据格式验证 - 商品价格格式', async () => {
    const response = await api.get('/product/1');
    const price = response.data.data.price;
    if (typeof price !== 'number' || price <= 0) {
      throw new Error('商品价格格式不正确');
    }
  });

  // 25. 分页格式验证
  await runTest('分页格式验证 - 响应结构', async () => {
    const response = await api.get('/product', {
      params: { page: 0, size: 10 }
    });
    const data = response.data.data;
    if (!data.content || typeof data.totalElements !== 'number') {
      throw new Error('分页响应格式不正确');
    }
  });

  testResults.endTime = new Date().toISOString();

  // 生成报告
  generateReport();

  // 总结
  log(colors.cyan, '\n' + '='.repeat(60));
  log(colors.cyan, '📊 测试执行完成');
  log(colors.green, `✅ 通过: ${testResults.passed}`);
  log(colors.red, `❌ 失败: ${testResults.failed}`);
  log(colors.blue, `📝 总计: ${testResults.total}`);
  const passRate = testResults.total > 0 ? Math.round(testResults.passed * 100 / testResults.total) : 0;
  log(colors.yellow, `🎯 通过率: ${passRate}%`);
  log(colors.cyan, '='.repeat(60) + '\n');

  // 退出码
  process.exit(testResults.failed > 0 ? 1 : 0);
}

// 错误处理
process.on('unhandledRejection', (error) => {
  log(colors.red, `❌ 未处理的错误: ${error.message}`);
  process.exit(1);
});

// 运行测试
runAllTests().catch(error => {
  log(colors.red, `❌ 测试执行失败: ${error.message}`);
  process.exit(1);
});
