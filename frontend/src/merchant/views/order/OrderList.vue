<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <header class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <el-icon><List /></el-icon>
          订单管理
        </h1>
      </div>
      <div class="header-right">
        <el-button type="success" @click="导出订单">
          <el-icon><Download /></el-icon>
          导出订单
        </el-button>
      </div>
    </header>

    <!-- 统计卡片 -->
    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary" @click="筛选订单状态 ('')">
            <div class="stat-icon"><el-icon><List /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ 订单统计.全部 }}</div>
              <div class="stat-label">全部订单</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning" @click="筛选订单状态 ('pending')">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ 订单统计.待付款 }}</div>
              <div class="stat-label">待付款</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card info" @click="筛选订单状态 ('paid')">
            <div class="stat-icon"><el-icon><ShoppingCart /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ 订单统计.待发货 }}</div>
              <div class="stat-label">待发货</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success" @click="筛选订单状态 ('shipped')">
            <div class="stat-icon"><el-icon><Van /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ 订单统计.已发货 }}</div>
              <div class="stat-label">已发货</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 搜索筛选栏 -->
    <section class="search-bar">
      <el-form :inline="true" :model="筛选表单">
        <el-form-item label="订单编号">
          <el-input v-model="筛选表单.订单编号" placeholder="请输入订单编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="筛选表单.商品名称" placeholder="请输入商品名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="筛选表单.状态" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="待付款" value="pending" />
            <el-option label="待发货" value="paid" />
            <el-option label="已发货" value="shipped" />
            <el-option label="已完成" value="completed" />
            <el-option label="已取消" value="cancelled" />
            <el-option label="退款中" value="refunding" />
          </el-select>
        </el-form-item>
        <el-form-item label="下单时间">
          <el-date-picker
            v-model="筛选表单.日期范围"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="搜索订单">搜索</el-button>
          <el-button @click="重置筛选">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <!-- 订单列表 -->
    <section class="table-section">
      <el-table :data="订单列表" class="sci-table" style="width: 100%" v-loading="加载中">
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="productImage" label="商品图片" width="80">
          <template #default="{ row }">
            <el-image
              :src="row.productImage || 'https://via.placeholder.com/50x50/1a2a4a/00d4ff?text=商品'"
              class="product-image"
              fit="cover"
            />
          </template>
        </el-table-column>
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="customerName" label="客户" width="100" />
        <el-table-column prop="amount" label="订单金额" width="100">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" width="160" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="获取状态类型 (row.status)" size="small">
              {{ 获取状态文本 (row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="查看订单详情 (row)">详情</el-button>
            <el-button
              v-if="row.status === 'paid'"
              type="success"
              text
              size="small"
              @click="发货操作 (row)"
            >
              发货
            </el-button>
            <el-button
              v-if="row.status === 'shipped'"
              type="info"
              text
              size="small"
              @click="查看物流 (row)"
            >
              物流
            </el-button>
            <el-button
              v-if="row.status === 'refunding'"
              type="warning"
              text
              size="small"
              @click="处理退款 (row)"
            >
              处理退款
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="分页.当前页"
          v-model:page-size="分页.每页数量"
          :total="分页.总数"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="加载订单列表"
          @current-change="加载订单列表"
        />
      </div>
    </section>

    <!-- 发货对话框 -->
    <el-dialog v-model="发货对话框.可见" title="发货操作" width="500px">
      <el-form :model="发货表单" label-width="80px">
        <el-form-item label="物流公司">
          <el-select v-model="发货表单.物流公司" placeholder="请选择物流公司" style="width: 100%">
            <el-option label="顺丰速运" value="sf" />
            <el-option label="中通快递" value="zto" />
            <el-option label="圆通速递" value="yto" />
            <el-option label="申通快递" value="sto" />
            <el-option label="韵达快递" value="yunda" />
          </el-select>
        </el-form-item>
        <el-form-item label="物流单号">
          <el-input v-model="发货表单.物流单号" placeholder="请输入物流单号" />
        </el-form-item>
        <el-form-item label="备注">
          <el-input v-model="发货表单.备注" type="textarea" :rows="3" placeholder="选填备注" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="发货对话框.可见 = false">取消</el-button>
        <el-button type="primary" @click="确认发货">确认发货</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { List, Download, Clock, ShoppingCart, Van } from '@element-plus/icons-vue'

interface 订单项 {
  orderNo: string
  productImage: string
  productName: string
  customerName: string
  amount: number
  orderTime: string
  status: string
}

interface 筛选表单类型 {
  订单编号:string
  商品名称:string
  状态:string
  日期范围:[Date, Date] | null
}

interface 订单统计类型 {
  全部:number
  待付款:number
  待发货:number
  已发货:number
  已完成:number
}

interface 分页类型 {
  当前页:number
  每页数量:number
  总数:number
}

interface 发货对话框类型 {
  可见:boolean
  当前订单:订单项 | null
}

interface 发货表单类型 {
  物流公司:string
  物流单号:string
  备注:string
}

const 加载中 = ref(false)
const 订单列表 = ref<订单项[]>([])
const 筛选表单 = reactive<筛选表单类型>({
  订单编号:'',
  商品名称:'',
  状态:'',
  日期范围:null
})

const 订单统计 = ref<订单统计类型>({
  全部:1256,
  待付款:86,
  待发货:124,
  已发货:358,
  已完成:688
})

const 分页 = reactive<分页类型>({
  当前页:1,
  每页数量:10,
  总数:0
})

const 发货对话框 = reactive<发货对话框类型>({
  可见:false,
  当前订单:null
})

const 发货表单 = reactive<发货表单类型>({
  物流公司:'',
  物流单号:'',
  备注:''
})

// 模拟订单数据
const 模拟订单数据:订单项 [] = [
  { orderNo: 'DD202603180001', productImage: '', productName: '无线蓝牙耳机', customerName: '张先生', amount: 199, orderTime: '2026-03-18 10:30:00', status: 'paid' },
  { orderNo: 'DD202603180002', productImage: '', productName: '智能手环', customerName: '李女士', amount: 149, orderTime: '2026-03-18 09:15:00', status: 'pending' },
  { orderNo: 'DD202603170003', productImage: '', productName: '机械键盘', customerName: '王先生', amount: 329, orderTime: '2026-03-17 16:45:00', status: 'shipped' },
  { orderNo: 'DD202603170004', productImage: '', productName: '空气净化器', customerName: '赵女士', amount: 999, orderTime: '2026-03-17 14:20:00', status: 'completed' },
  { orderNo: 'DD202603170005', productImage: '', productName: '运动跑鞋', customerName: '刘先生', amount: 299, orderTime: '2026-03-17 11:00:00', status: 'refunding' },
  { orderNo: 'DD202603160006', productImage: '', productName: '护肤套装', customerName: '陈女士', amount: 459, orderTime: '2026-03-16 20:30:00', status: 'completed' },
  { orderNo: 'DD202603160007', productImage: '', productName: '智能手表', customerName: '杨先生', amount: 899, orderTime: '2026-03-16 15:10:00', status: 'shipped' },
  { orderNo: 'DD202603160008', productImage: '', productName: '办公椅', customerName: '周女士', amount: 599, orderTime: '2026-03-16 10:00:00', status: 'cancelled' }
]

const 加载订单列表 = () => {
  加载中.value = true
  setTimeout(() => {
    订单列表.value = 模拟订单数据
    分页.总数 = 模拟订单数据.length
    加载中.value = false
  }, 500)
}

const 筛选订单状态 = (状态:string) => {
  筛选表单.状态 = 状态
  加载订单列表 ()
}

const 搜索订单 = () => {
  ElMessage.success('搜索功能演示')
  加载订单列表 ()
}

const 重置筛选 = () => {
  筛选表单.订单编号 = ''
  筛选表单.商品名称 = ''
  筛选表单.状态 = ''
  筛选表单.日期范围 = null
}

const 获取状态类型 = (状态:string) => {
  const 映射 = {
    pending: 'warning',
    paid: 'primary',
    shipped: 'success',
    completed: 'info',
    cancelled: 'warning',
    refunding: 'danger'
  }
  return 映射 [状态] || 'info'
}

const 获取状态文本 = (状态:string) => {
  const 映射 = {
    pending: '待付款',
    paid: '待发货',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消',
    refunding: '退款中'
  }
  return 映射 [状态] || 状态
}

const 查看订单详情 = (订单:订单项) => {
  ElMessage.info(`查看订单详情:${订单.orderNo}`)
}

const 发货操作 = (订单:订单项) => {
  发货对话框.可见 = true
  发货对话框.当前订单 = 订单
  发货表单.物流公司 = ''
  发货表单.物流单号 = ''
  发货表单.备注 = ''
}

const 确认发货 = () => {
  if (!发货表单.物流公司 || !发货表单.物流单号) {
    ElMessage.warning('请选择物流公司并填写物流单号')
    return
  }
  ElMessage.success('发货成功')
  发货对话框.可见 = false
  加载订单列表 ()
}

const 查看物流 = (订单:订单项) => {
  ElMessage.info(`查看物流:${订单.orderNo}`)
}

const 处理退款 = (订单:订单项) => {
  ElMessage.info(`处理退款:${订单.orderNo}`)
}

const 导出订单 = () => {
  ElMessage.success('订单导出功能演示')
}

onMounted(() => {
  加载订单列表 ()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 25px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.9), rgba(26, 31, 58, 0.7));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: bold;
  color: #fff;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 26px;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.5));
}

.stats-cards {
  margin-bottom: 25px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--glow-color), transparent);
}

.stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--glow-color);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.15);
}

.stat-card.primary { --glow-color: #00d4ff; }
.stat-card.warning { --glow-color: #ffaa00; }
.stat-card.info { --glow-color: #00d4ff; }
.stat-card.success { --glow-color: #00ff88; }

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
  box-shadow: 0 0 15px rgba(255, 170, 0, 0.4);
}

.stat-card.info .stat-icon {
  background: linear-gradient(135deg, #00d4ff, #0088cc);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.4);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}

/* 搜索栏优化 */
.search-bar {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.search-bar :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 15px;
}

.search-bar :deep(.el-form-item__label) {
  color: #ccc;
  font-weight: 500;
}

.search-bar :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s;
}

.search-bar :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.search-bar :deep(.el-input__inner) {
  color: #fff;
}

.search-bar :deep(.el-select .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-select__wrapper) {
  color: #fff;
}

.search-bar :deep(.el-date-editor .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
  padding: 10px 20px;
}

.search-bar :deep(.el-button--primary:hover) {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

.search-bar :deep(.el-button) {
  border-radius: 8px;
}

/* 表格区域 - 深空科技风格 */
.table-section {
  background: linear-gradient(180deg,
    rgba(8, 12, 28, 0.98) 0%,
    rgba(12, 18, 40, 0.95) 50%,
    rgba(8, 12, 28, 0.98) 100%);
  border: 1px solid rgba(0, 200, 255, 0.3);
  border-radius: 4px;
  padding: 24px;
  position: relative;
  overflow: hidden;
  box-shadow:
    0 0 30px rgba(0, 150, 255, 0.15),
    0 0 60px rgba(0, 100, 255, 0.1),
    inset 0 0 100px rgba(0, 150, 255, 0.05);
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(0, 200, 255, 0.2);
  transition: all 0.3s;
}

.product-image:hover {
  border-color: #00ffff;
  box-shadow: 0 0 15px rgba(0, 200, 255, 0.3);
}

.price-text {
  color: #00ffff;
  font-weight: bold;
  font-size: 15px;
  text-shadow:
    0 0 10px rgba(0, 255, 255, 0.8),
    0 0 20px rgba(0, 255, 255, 0.4);
}

.pagination-bar {
  margin-top: 24px;
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 200, 255, 0.15);
}

/* 表格样式美化 - 深空科技主题 */
.sci-table {
  border-radius: 4px;
  overflow: hidden;
  background: linear-gradient(180deg,
    rgba(10, 20, 50, 0.8) 0%,
    rgba(5, 15, 40, 0.9) 100%);
  position: relative;
  border: 1px solid rgba(0, 180, 255, 0.2);
  box-shadow:
    inset 0 0 60px rgba(0, 150, 255, 0.08),
    0 0 30px rgba(0, 200, 255, 0.1);
}

/* 表头样式 - 科技蓝 */
.sci-table :deep(.el-table__header th) {
  background: linear-gradient(180deg,
    rgba(0, 100, 180, 0.4) 0%,
    rgba(0, 50, 100, 0.6) 100%);
  color: #00ffff;
  font-size: 13px;
  border-bottom: 2px solid rgba(0, 200, 255, 0.6);
  font-weight: 700;
  padding: 16px 0;
  letter-spacing: 1px;
  text-transform: uppercase;
  position: relative;
  z-index: 2;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.5);
}

/* 表格主体样式 */
.sci-table :deep(.el-table__body td) {
  color: #88aacc !important;
  border-bottom: 1px solid rgba(0, 150, 200, 0.15);
  font-size: 13px;
  padding: 14px 0;
  transition: all 0.3s ease;
  position: relative;
  z-index: 2;
}

/* 行悬停效果 - 能量辉光 */
.sci-table :deep(.el-table__row) {
  background: transparent !important;
  transition: all 0.3s ease;
  border-radius: 4px;
}

/* Hover 效果 - 渐变辉光 */
.sci-table :deep(.el-table__row:hover) {
  background: linear-gradient(90deg,
    rgba(0, 180, 220, 0.3) 0%,
    rgba(0, 220, 255, 0.25) 50%,
    rgba(0, 180, 220, 0.3) 100%) !important;
  box-shadow:
    0 0 10px rgba(0, 200, 255, 0.4),
    0 0 20px rgba(0, 200, 255, 0.2),
    0 0 30px rgba(0, 200, 255, 0.1);
}

.sci-table :deep(.el-table__row:hover .el-table__cell),
.sci-table :deep(.el-table__row:hover td),
.sci-table :deep(.el-table__row:hover .el-table_1),
.sci-table :deep(.el-table__row:hover .el-table_2) {
  background: transparent !important;
}

.sci-table :deep(.el-table__row:hover td) {
  color: #ffffff !important;
  background: transparent !important;
  border-top: 1px solid rgba(0, 200, 255, 0.4) !important;
  border-bottom: 1px solid rgba(0, 200, 255, 0.4) !important;
}

.sci-table :deep(.el-table__row:hover td:first-child) {
  border-left: 2px solid rgba(0, 200, 255, 0.6);
  padding-left: 12px;
}

.sci-table :deep(.el-table__row:hover td:last-child) {
  border-right: 2px solid rgba(0, 200, 255, 0.6);
  padding-right: 12px;
}

.sci-table :deep(.el-table__empty-text) {
  color: #555;
  font-size: 14px;
}

/* 操作按钮样式 - 科技风格 */
.sci-table :deep(.el-button) {
  border-radius: 4px;
  font-size: 12px;
  padding: 8px 16px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  text-shadow: 0 0 5px currentColor;
}

.sci-table :deep(.el-button--primary) {
  background: linear-gradient(180deg,
    rgba(0, 150, 200, 0.3) 0%,
    rgba(0, 100, 150, 0.4) 100%);
  border: 1px solid rgba(0, 200, 255, 0.6);
  color: #00ffff;
  box-shadow:
    0 0 15px rgba(0, 200, 255, 0.3),
    inset 0 0 20px rgba(0, 200, 255, 0.1);
}

.sci-table :deep(.el-button--primary:hover) {
  background: linear-gradient(180deg,
    rgba(0, 200, 255, 0.5) 0%,
    rgba(0, 150, 200, 0.6) 100%);
  border-color: #00ffff;
  box-shadow:
    0 0 25px rgba(0, 200, 255, 0.6),
    0 0 40px rgba(0, 200, 255, 0.3),
    inset 0 0 30px rgba(0, 200, 255, 0.2);
  transform: translateY(-2px);
}

.sci-table :deep(.el-button--success) {
  background: linear-gradient(180deg,
    rgba(0, 200, 100, 0.3) 0%,
    rgba(0, 150, 80, 0.4) 100%);
  border: 1px solid rgba(0, 255, 136, 0.6);
  color: #00ff88;
  box-shadow:
    0 0 15px rgba(0, 255, 136, 0.3),
    inset 0 0 20px rgba(0, 255, 136, 0.1);
}

.sci-table :deep(.el-button--success:hover) {
  background: linear-gradient(180deg,
    rgba(0, 255, 136, 0.5) 0%,
    rgba(0, 200, 100, 0.6) 100%);
  border-color: #00ffaa;
  box-shadow:
    0 0 25px rgba(0, 255, 136, 0.6),
    0 0 40px rgba(0, 255, 136, 0.3),
    inset 0 0 30px rgba(0, 255, 136, 0.2);
  transform: translateY(-2px);
}

.sci-table :deep(.el-button--info) {
  background: linear-gradient(180deg,
    rgba(0, 150, 200, 0.3) 0%,
    rgba(0, 100, 150, 0.4) 100%);
  border: 1px solid rgba(0, 200, 255, 0.6);
  color: #00ccff;
  box-shadow:
    0 0 15px rgba(0, 200, 255, 0.3),
    inset 0 0 20px rgba(0, 200, 255, 0.1);
}

.sci-table :deep(.el-button--info:hover) {
  background: linear-gradient(180deg,
    rgba(0, 200, 255, 0.5) 0%,
    rgba(0, 150, 200, 0.6) 100%);
  border-color: #00ffff;
  box-shadow:
    0 0 25px rgba(0, 200, 255, 0.6),
    0 0 40px rgba(0, 200, 255, 0.3),
    inset 0 0 30px rgba(0, 200, 255, 0.2);
  transform: translateY(-2px);
}

.sci-table :deep(.el-button--warning) {
  background: linear-gradient(180deg,
    rgba(255, 150, 0, 0.3) 0%,
    rgba(200, 100, 0, 0.4) 100%);
  border: 1px solid rgba(255, 170, 0, 0.6);
  color: #ffaa00;
  box-shadow:
    0 0 15px rgba(255, 170, 0, 0.3),
    inset 0 0 20px rgba(255, 170, 0, 0.1);
}

.sci-table :deep(.el-button--warning:hover) {
  background: linear-gradient(180deg,
    rgba(255, 170, 0, 0.5) 0%,
    rgba(255, 150, 0, 0.6) 100%);
  border-color: #ffcc00;
  box-shadow:
    0 0 25px rgba(255, 170, 0, 0.6),
    0 0 40px rgba(255, 170, 0, 0.3),
    inset 0 0 30px rgba(255, 170, 0, 0.2);
  transform: translateY(-2px);
}

/* 状态标签样式 - 科技风格 */
.sci-table :deep(.el-tag) {
  border-radius: 4px;
  padding: 6px 14px;
  font-size: 12px;
  font-weight: 600;
  transition: all 0.3s ease;
  border: 1px solid;
  text-shadow: 0 0 5px currentColor;
  position: relative;
  overflow: hidden;
}

/* 已发货 - 蓝色 */
.sci-table :deep(.el-tag--success) {
  background: linear-gradient(180deg,
    rgba(0, 150, 200, 0.2) 0%,
    rgba(0, 100, 150, 0.3) 100%);
  color: #00ccff;
  border-color: rgba(0, 200, 255, 0.6);
  box-shadow:
    0 0 10px rgba(0, 200, 255, 0.3),
    inset 0 0 15px rgba(0, 200, 255, 0.1);
}

.sci-table :deep(.el-tag--success:hover) {
  background: linear-gradient(180deg,
    rgba(0, 200, 255, 0.4) 0%,
    rgba(0, 150, 200, 0.5) 100%);
  border-color: #00ffff;
  box-shadow:
    0 0 20px rgba(0, 200, 255, 0.6),
    0 0 40px rgba(0, 200, 255, 0.3),
    inset 0 0 25px rgba(0, 200, 255, 0.2);
}

/* 已完成 - 绿色 */
.sci-table :deep(.el-tag--info) {
  background: linear-gradient(180deg,
    rgba(0, 200, 100, 0.2) 0%,
    rgba(0, 150, 80, 0.3) 100%);
  color: #00ff88;
  border-color: rgba(0, 255, 136, 0.6);
  box-shadow:
    0 0 10px rgba(0, 255, 136, 0.3),
    inset 0 0 15px rgba(0, 255, 136, 0.1);
}

.sci-table :deep(.el-tag--info:hover) {
  background: linear-gradient(180deg,
    rgba(0, 255, 136, 0.4) 0%,
    rgba(0, 200, 100, 0.5) 100%);
  border-color: #00ffaa;
  box-shadow:
    0 0 20px rgba(0, 255, 136, 0.6),
    0 0 40px rgba(0, 255, 136, 0.3),
    inset 0 0 25px rgba(0, 255, 136, 0.2);
}

/* 待付款 - 橙色 */
.sci-table :deep(.el-tag--warning) {
  background: linear-gradient(180deg,
    rgba(255, 170, 0, 0.2) 0%,
    rgba(255, 150, 0, 0.3) 100%);
  color: #ffaa00;
  border-color: rgba(255, 170, 0, 0.6);
  box-shadow:
    0 0 10px rgba(255, 170, 0, 0.3),
    inset 0 0 15px rgba(255, 170, 0, 0.1);
}

.sci-table :deep(.el-tag--warning:hover) {
  background: linear-gradient(180deg,
    rgba(255, 170, 0, 0.4) 0%,
    rgba(255, 150, 0, 0.5) 100%);
  border-color: #ffcc00;
  box-shadow:
    0 0 20px rgba(255, 170, 0, 0.6),
    0 0 40px rgba(255, 170, 0, 0.3),
    inset 0 0 25px rgba(255, 170, 0, 0.2);
}

/* 待发货 - 黄色 (支付方式) */
.sci-table :deep(.el-tag--primary) {
  background: linear-gradient(180deg,
    rgba(255, 200, 0, 0.2) 0%,
    rgba(255, 180, 0, 0.3) 100%);
  color: #ffcc00;
  border-color: rgba(255, 200, 0, 0.6);
  box-shadow:
    0 0 10px rgba(255, 200, 0, 0.3),
    inset 0 0 15px rgba(255, 200, 0, 0.1);
}

.sci-table :deep(.el-tag--primary:hover) {
  background: linear-gradient(180deg,
    rgba(255, 220, 0, 0.4) 0%,
    rgba(255, 200, 0, 0.5) 100%);
  border-color: #ffdd00;
  box-shadow:
    0 0 20px rgba(255, 200, 0, 0.6),
    0 0 40px rgba(255, 200, 0, 0.3),
    inset 0 0 25px rgba(255, 200, 0, 0.2);
}

/* 退款中 - 红色 */
.sci-table :deep(.el-tag--danger) {
  background: linear-gradient(180deg,
    rgba(255, 80, 80, 0.2) 0%,
    rgba(200, 50, 50, 0.3) 100%);
  color: #ff6666;
  border-color: rgba(255, 100, 100, 0.6);
  box-shadow:
    0 0 10px rgba(255, 100, 100, 0.3),
    inset 0 0 15px rgba(255, 100, 100, 0.1);
}

.sci-table :deep(.el-tag--danger:hover) {
  background: linear-gradient(180deg,
    rgba(255, 100, 100, 0.4) 0%,
    rgba(255, 80, 80, 0.5) 100%);
  border-color: #ff8888;
  box-shadow:
    0 0 20px rgba(255, 100, 100, 0.6),
    0 0 40px rgba(255, 100, 100, 0.3),
    inset 0 0 25px rgba(255, 100, 100, 0.2);
}

/* 已取消 - 灰色 (使用 el-tag 选择器覆盖) */
.sci-table :deep(.el-table__cell .el-tag:not(.el-tag--success):not(.el-tag--warning):not(.el-tag--info):not(.el-tag--danger):not(.el-tag--primary)) {
  background: linear-gradient(180deg,
    rgba(100, 100, 100, 0.2) 0%,
    rgba(80, 80, 80, 0.3) 100%);
  color: #aaaaaa;
  border-color: rgba(150, 150, 150, 0.6);
  box-shadow:
    0 0 10px rgba(150, 150, 150, 0.3),
    inset 0 0 15px rgba(150, 150, 150, 0.1);
}

.sci-table :deep(.el-table__cell .el-tag:not(.el-tag--success):not(.el-tag--warning):not(.el-tag--info):not(.el-tag--danger):not(.el-tag--primary):hover) {
  background: linear-gradient(180deg,
    rgba(150, 150, 150, 0.4) 0%,
    rgba(100, 100, 100, 0.5) 100%);
  border-color: #cccccc;
  box-shadow:
    0 0 20px rgba(150, 150, 150, 0.6),
    0 0 40px rgba(150, 150, 150, 0.3),
    inset 0 0 25px rgba(150, 150, 150, 0.2);
}

/* 分页器美化 - 科技风格 */
.pagination-bar :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-bar :deep(.el-pagination__total) {
  color: #6688aa;
  font-size: 13px;
}

.pagination-bar :deep(.el-pager li) {
  background: linear-gradient(180deg,
    rgba(0, 100, 150, 0.2) 0%,
    rgba(0, 50, 100, 0.3) 100%);
  border: 1px solid rgba(0, 180, 220, 0.3);
  border-radius: 4px;
  color: #6688aa;
  min-width: 36px;
  height: 36px;
  line-height: 36px;
  transition: all 0.3s ease;
  box-shadow: 0 0 10px rgba(0, 150, 200, 0.1);
}

.pagination-bar :deep(.el-pager li:hover) {
  background: linear-gradient(180deg,
    rgba(0, 180, 220, 0.4) 0%,
    rgba(0, 150, 200, 0.5) 100%);
  border-color: rgba(0, 200, 255, 0.6);
  color: #00ffff;
  box-shadow:
    0 0 15px rgba(0, 200, 255, 0.4),
    0 0 30px rgba(0, 200, 255, 0.2);
}

.pagination-bar :deep(.el-pager li.is-active) {
  background: linear-gradient(180deg,
    rgba(0, 200, 255, 0.5) 0%,
    rgba(0, 150, 200, 0.6) 100%);
  border-color: #00ffff;
  color: #ffffff;
  font-weight: bold;
  box-shadow:
    0 0 20px rgba(0, 200, 255, 0.6),
    0 0 40px rgba(0, 200, 255, 0.3);
}

.pagination-bar :deep(.btn-prev),
.pagination-bar :deep(.btn-next) {
  background: linear-gradient(180deg,
    rgba(0, 100, 150, 0.2) 0%,
    rgba(0, 50, 100, 0.3) 100%);
  border: 1px solid rgba(0, 180, 220, 0.3);
  border-radius: 4px;
  width: 36px;
  height: 36px;
  transition: all 0.3s ease;
  color: #6688aa;
  box-shadow: 0 0 10px rgba(0, 150, 200, 0.1);
}

.pagination-bar :deep(.btn-prev):not(:disabled):hover,
.pagination-bar :deep(.btn-next):not(:disabled):hover {
  background: linear-gradient(180deg,
    rgba(0, 180, 220, 0.4) 0%,
    rgba(0, 150, 200, 0.5) 100%);
  border-color: rgba(0, 200, 255, 0.6);
  color: #00ffff;
  box-shadow:
    0 0 15px rgba(0, 200, 255, 0.4),
    0 0 30px rgba(0, 200, 255, 0.2);
}

.pagination-bar :deep(.btn-prev:disabled),
.pagination-bar :deep(.btn-next:disabled) {
  opacity: 0.2;
  cursor: not-allowed;
}

.pagination-bar :deep(.el-pagination__sizes) {
  margin: 0 8px;
}

.pagination-bar :deep(.el-select .el-input__wrapper) {
  background: linear-gradient(180deg,
    rgba(0, 100, 150, 0.2) 0%,
    rgba(0, 50, 100, 0.3) 100%);
  border: 1px solid rgba(0, 180, 220, 0.3);
  border-radius: 4px;
  padding: 4px 12px;
  box-shadow: 0 0 10px rgba(0, 150, 200, 0.1);
}

.pagination-bar :deep(.el-select__placeholder) {
  color: #6688aa;
}

.pagination-bar :deep(.el-select__input) {
  color: #aaccff;
}
</style>
