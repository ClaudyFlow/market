#!/bin/bash
# ========================================
# 前后端联调测试自动化脚本 (Linux/Mac)
# 功能: 启动后端服务 -> 运行测试 -> 生成报告
# ========================================

echo ""
echo "========================================"
echo "  前后端联调测试自动化脚本"
echo "========================================"
echo ""

# 设置变量
BACKEND_PORT=8080
TEST_PROFILE=integration
WAIT_TIME=30

echo "[1/5] 检查环境..."
echo ""

# 检查 Java
if ! command -v java &> /dev/null; then
    echo "❌ 错误: 未找到 Java"
    echo "请先安装 JDK 21 或更高版本"
    exit 1
fi

# 检查 Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ 错误: 未找到 Maven"
    echo "请先安装 Maven"
    exit 1
fi

# 检查 Node.js
if ! command -v node &> /dev/null; then
    echo "❌ 错误: 未找到 Node.js"
    echo "请先安装 Node.js"
    exit 1
fi

echo "✅ 环境检查通过"
echo ""

echo "[2/5] 清理之前的测试数据..."
echo ""

# 清理之前的构建
cd ../../backend
mvn clean -q
cd ../../script/test

echo "✅ 清理完成"
echo ""

echo "[3/5] 启动后端服务 (测试模式)..."
echo ""

# 启动后端（后台运行）
cd ../../backend
mvn spring-boot:run -Dspring-boot.run.profiles=$TEST_PROFILE -Dserver.port=$BACKEND_PORT &
BACKEND_PID=$!
cd ../../script/test

echo "⏳ 等待后端服务启动 ($WAIT_TIME 秒)..."
sleep $WAIT_TIME

echo ""
echo "✅ 后端服务已启动 (PID: $BACKEND_PID)"
echo ""

echo "[4/5] 执行联调测试..."
echo ""

# 运行前端联调测试脚本
node integration-test.js
TEST_EXIT_CODE=$?

echo ""
echo "[5/5] 测试完成，正在清理..."
echo ""

# 关闭后端服务
echo "⚠️  关闭后端服务..."
kill $BACKEND_PID 2>/dev/null
wait $BACKEND_PID 2>/dev/null

echo ""
echo "========================================"
if [ $TEST_EXIT_CODE -eq 0 ]; then
    echo "  ✅ 联调测试全部通过"
else
    echo "  ❌ 联调测试存在失败"
fi
echo "========================================"
echo ""
echo "📄 测试报告: integration-test-report.html"
echo ""

exit $TEST_EXIT_CODE
