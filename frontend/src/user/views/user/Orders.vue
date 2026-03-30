<template>
  <div class="page-container">
    <!-- 页面标题 -->
    <header class="page-header">
      <h1 class="page-title">
        <i class="fas fa-list"></i>
        我的订单
      </h1>
    </header>

    <!-- 统计卡片 -->
    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary" @click="筛选订单状态 ('')">
            <div class="stat-icon"><i class="fas fa-list"></i></div>
            <div class="stat-info">
              <div class="stat-value">{{ 订单统计.全部 }}</div>
              <div class="stat-label">全部订单</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning" @click="筛选订单状态 ('pending')">
            <div class="stat-icon"><i class="fas fa-clock"></i></div>
            <div class="stat-info">
              <div class="stat-value">{{ 订单统计.待付款 }}</div>
              <div class="stat-label">待付款</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card info" @click="筛选订单状态 ('paid')">
            <div class="stat-icon"><i class="fas fa-shopping-cart"></i></div>
            <div class="stat-info">
              <div class="stat-value">{{ 订单统计.待发货 }}</div>
              <div class="stat-label">待发货</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success" @click="筛选订单状态 ('completed')">
            <div class="stat-icon"><i class="fas fa-check-circle"></i></div>
            <div class="stat-info">
              <div class="stat-value">{{ 订单统计.已完成 }}</div>
              <div class="stat-label">已完成</div>
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
              v-if="row.status === 'pending'"
              type="success"
              text
              size="small"
              @click="去支付 (row)"
            >
              去支付
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
              v-if="row.status === 'completed'"
              type="warning"
              text
              size="small"
              @click="评价订单 (row)"
            >
              评价
            </el-button>
            <el-button
              v-if="row.status === 'paid' || row.status === 'shipped'"
              type="danger"
              text
              size="small"
              @click="申请退款 (row)"
            >
              退款
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

    <!-- 订单详情对话框 -->
    <el-dialog v-model="详情对话框.可见" title="订单详情" width="700px">
      <el-descriptions :column="2" border class="sci-descriptions" v-if="当前订单">
        <el-descriptions-item label="订单编号">{{ 当前订单.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ 当前订单.orderTime }}</el-descriptions-item>
        <el-descriptions-item label="商品名称" :span="2">{{ 当前订单.productName }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ 当前订单.amount }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="获取状态类型 (当前订单.status)" size="small">
            {{ 获取状态文本 (当前订单.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ 当前订单.address || '暂无地址' }}</el-descriptions-item>
        <el-descriptions-item label="订单备注" :span="2">{{ 当前订单.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="详情对话框.可见 = false">关闭</el-button>
        <el-button type="primary" @click="详情对话框.可见 = false">确认</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
// Font Awesome 图标直接使用类名，无需导入
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'

interface 订单项 {
  orderNo: string
  productImage: string
  productName: string
  amount: number
  orderTime: string
  status: string
  address?: string
  remark?: string
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
  已完成:number
}

interface 分页类型 {
  当前页:number
  每页数量:number
  总数:number
}

interface 详情对话框类型 {
  可见:boolean
  当前订单:订单项 | null
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
  全部:156,
  待付款:12,
  待发货:8,
  已完成:126
})

const 分页 = reactive<分页类型>({
  当前页:1,
  每页数量:10,
  总数:0
})

const 详情对话框 = reactive<详情对话框类型>({
  可见:false,
  当前订单:null
})

const 当前订单 = ref<订单项 | null>(null)

// 模拟订单数据
const 模拟订单数据:订单项 [] = [
  { orderNo: 'DD202603180001', productImage: '', productName: '无线蓝牙耳机', amount: 199, orderTime: '2026-03-18 10:30:00', status: 'paid', address: '北京市朝阳区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603180002', productImage: '', productName: '智能手环', amount: 149, orderTime: '2026-03-18 09:15:00', status: 'pending', address: '上海市浦东新区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603170003', productImage: '', productName: '机械键盘', amount: 329, orderTime: '2026-03-17 16:45:00', status: 'shipped', address: '广州市天河区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603170004', productImage: '', productName: '空气净化器', amount: 999, orderTime: '2026-03-17 14:20:00', status: 'completed', address: '深圳市南山区 xx 路 xx 号', remark: '请尽快发货' },
  { orderNo: 'DD202603170005', productImage: '', productName: '运动跑鞋', amount: 299, orderTime: '2026-03-17 11:00:00', status: 'refunding', address: '杭州市西湖区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603160006', productImage: '', productName: '护肤套装', amount: 459, orderTime: '2026-03-16 20:30:00', status: 'completed', address: '成都市武侯区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603160007', productImage: '', productName: '智能手表', amount: 899, orderTime: '2026-03-16 15:10:00', status: 'shipped', address: '武汉市江汉区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603160008', productImage: '', productName: '办公椅', amount: 599, orderTime: '2026-03-16 10:00:00', status: 'cancelled', address: '南京市鼓楼区 xx 路 xx 号', remark: '用户主动取消' }
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
    paid: 'info',
    shipped: 'success',
    completed: '',
    cancelled: 'info',
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
  当前订单.value = 订单
  详情对话框.可见 = true
}

const 去支付 = (订单:订单项) => {
  ElMessage.info(`去支付：${订单.orderNo}`)
}

const 查看物流 = (订单:订单项) => {
  ElMessage.info(`查看物流：${订单.orderNo}`)
}

const 评价订单 = (订单:订单项) => {
  ElMessage.info(`评价订单：${订单.orderNo}`)
}

const 申请退款 = (订单:订单项) => {
  ElMessage.info(`申请退款：${订单.orderNo}`)
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

.page-title i {
  color: var(--mall-primary);
  font-size: 26px;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.5));
}

.stats-cards {
  
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
  
}

/* 搜索栏优化 */
.search-bar {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.search-bar :deep(.el-form-item) {
  
  
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

/* 表格区域 - 深色科幻风格 */
.table-section {
  background: linear-gradient(135deg, rgba(10, 14, 26, 0.95), rgba(10, 14, 26, 0.85));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 
    0 4px 30px rgba(0, 0, 0, 0.5),
    inset 0 0 80px rgba(0, 212, 255, 0.03);
  position: relative;
  overflow: hidden;
}

.table-section::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.5), transparent);
}

.table-section::after {
  content: '';
  position: absolute;
  bottom: 0;
  left: 0;
  right: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, rgba(0, 212, 255, 0.3), transparent);
}

.product-image {
  width: 50px;
  height: 50px;
  border-radius: 6px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(0, 212, 255, 0.2);
  transition: all 0.3s;
}

.product-image:hover {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.3);
}

.price-text {
  color: #00d4ff;
  font-weight: bold;
  font-size: 15px;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.4);
}

.pagination-bar {
  
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.03);
}

/* 表格样式美化 - 深色主题 */
.sci-table {
  border-radius: 12px;
  overflow: hidden;
  background: rgba(10, 14, 26, 0.6);
  box-shadow: 
    inset 0 0 40px rgba(0, 212, 255, 0.02),
    0 0 20px rgba(0, 212, 255, 0.05);
  position: relative;
}

/* 表格内部辉光层 */
.sci-table::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: radial-gradient(ellipse at 50% 0%, rgba(0, 212, 255, 0.08) 0%, transparent 60%);
  pointer-events: none;
  z-index: 1;
}

/* 表头样式 - 深色 */
.sci-table :deep(.el-table__header th) {
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.12), rgba(10, 14, 26, 0.9));
  color: #00d4ff;
  font-size: 13px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.25);
  font-weight: 600;
  padding: 16px 0;
  letter-spacing: 0.5px;
  position: relative;
  z-index: 2;
}

.sci-table :deep(.el-table__header th)::after {
  content: '';
  position: absolute;
  bottom: -1px;
  left: 50%;
  width: 0;
  height: 1px;
  background: linear-gradient(90deg, transparent, #00d4ff, transparent);
  transition: all 0.3s;
}

.sci-table :deep(.el-table__header th:hover)::after {
  width: 100%;
  left: 0;
}

/* 表格主体样式 - 深色 */
.sci-table :deep(.el-table__body td) {
  background: transparent;
  color: #999 !important;
  border-bottom: 1px solid rgba(255, 255, 255, 0.03);
  font-size: 13px;
  padding: 16px 0;
  transition: all 0.3s ease;
}

/* 行悬停效果 - 深色 */
.sci-table :deep(.el-table__row) {
  background: transparent;
  transition: all 0.3s ease;
}

.sci-table :deep(.el-table__row:hover td) {
  background: rgba(0, 180, 220, 0.12);
  color: #b0d0e0 !important;
  box-shadow: 
    inset 0 0 20px rgba(0, 180, 220, 0.1),
    inset 0 1px 0 rgba(0, 180, 220, 0.3),
    inset 0 -1px 0 rgba(0, 180, 220, 0.1);
}

.sci-table :deep(.el-table__empty-text) {
  color: #555;
  font-size: 14px;
}

/* 操作按钮样式 - 深色 */
.sci-table :deep(.el-button) {
  border-radius: 6px;
  font-size: 12px;
  padding: 6px 12px;
  transition: all 0.3s;
}

.sci-table :deep(.el-button--primary) {
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #00d4ff;
}

.sci-table :deep(.el-button--primary:hover) {
  background: rgba(0, 212, 255, 0.2);
  border-color: #00d4ff;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.3);
  transform: translateY(-1px);
}

.sci-table :deep(.el-button--success) {
  background: rgba(0, 255, 136, 0.1);
  border: 1px solid rgba(0, 255, 136, 0.3);
  color: #00ff88;
}

.sci-table :deep(.el-button--success:hover) {
  background: rgba(0, 255, 136, 0.2);
  border-color: #00ff88;
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.3);
  transform: translateY(-1px);
}

.sci-table :deep(.el-button--info) {
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #00d4ff;
}

.sci-table :deep(.el-button--info:hover) {
  background: rgba(0, 212, 255, 0.2);
  border-color: #00d4ff;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.3);
  transform: translateY(-1px);
}

.sci-table :deep(.el-button--warning) {
  background: rgba(255, 170, 0, 0.1);
  border: 1px solid rgba(255, 170, 0, 0.3);
  color: #ffaa00;
}

.sci-table :deep(.el-button--warning:hover) {
  background: rgba(255, 170, 0, 0.2);
  border-color: #ffaa00;
  box-shadow: 0 0 15px rgba(255, 170, 0, 0.3);
  transform: translateY(-1px);
}

.sci-table :deep(.el-button--danger) {
  background: rgba(255, 102, 102, 0.1);
  border: 1px solid rgba(255, 102, 102, 0.3);
  color: #ff6666;
}

.sci-table :deep(.el-button--danger:hover) {
  background: rgba(255, 102, 102, 0.2);
  border-color: #ff6666;
  box-shadow: 0 0 15px rgba(255, 102, 102, 0.3);
  transform: translateY(-1px);
}

/* 状态标签样式 - 深色 */
.sci-table :deep(.el-tag) {
  border-radius: 6px;
  padding: 5px 12px;
  font-size: 12px;
  font-weight: 500;
  transition: all 0.3s;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.sci-table :deep(.el-tag--success) {
  background: rgba(0, 255, 136, 0.1);
  color: #00ff88;
  border-color: rgba(0, 255, 136, 0.2);
}

.sci-table :deep(.el-tag--success:hover) {
  background: rgba(0, 255, 136, 0.15);
  box-shadow: 0 0 10px rgba(0, 255, 136, 0.2);
}

.sci-table :deep(.el-tag--warning) {
  background: rgba(255, 170, 0, 0.1);
  color: #ffaa00;
  border-color: rgba(255, 170, 0, 0.2);
}

.sci-table :deep(.el-tag--warning:hover) {
  background: rgba(255, 170, 0, 0.15);
  box-shadow: 0 0 10px rgba(255, 170, 0, 0.2);
}

.sci-table :deep(.el-tag--info) {
  background: rgba(0, 212, 255, 0.1);
  color: #00d4ff;
  border-color: rgba(0, 212, 255, 0.2);
}

.sci-table :deep(.el-tag--info:hover) {
  background: rgba(0, 212, 255, 0.15);
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.2);
}

.sci-table :deep(.el-tag--danger) {
  background: rgba(255, 102, 102, 0.1);
  color: #ff6666;
  border-color: rgba(255, 102, 102, 0.2);
}

.sci-table :deep(.el-tag--danger:hover) {
  background: rgba(255, 102, 102, 0.15);
  box-shadow: 0 0 10px rgba(255, 102, 102, 0.2);
}

/* 对话框样式美化 - 深色 */
.sci-descriptions :deep(.el-descriptions__header) {
  
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.sci-descriptions :deep(.el-descriptions__label) {
  background: rgba(10, 14, 26, 0.8);
  color: #00d4ff;
  border: 1px solid rgba(0, 212, 255, 0.15);
}

.sci-descriptions :deep(.el-descriptions__content) {
  background: rgba(10, 14, 26, 0.6);
  color: #ccc;
  border: 1px solid rgba(255, 255, 255, 0.05);
}

/* 分页器美化 - 深色 */
.pagination-bar :deep(.el-pagination) {
  display: flex;
  align-items: center;
  gap: 8px;
}

.pagination-bar :deep(.el-pagination__total) {
  color: #666;
  font-size: 13px;
}

.pagination-bar :deep(.el-pager li) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  color: #666;
  min-width: 32px;
  height: 32px;
  line-height: 32px;
  transition: all 0.3s;
}

.pagination-bar :deep(.el-pager li:hover) {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.2);
  color: #00d4ff;
}

.pagination-bar :deep(.el-pager li.is-active) {
  background: rgba(0, 212, 255, 0.15);
  border-color: rgba(0, 212, 255, 0.3);
  color: #00d4ff;
  font-weight: bold;
}

.pagination-bar :deep(.btn-prev),
.pagination-bar :deep(.btn-next) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  width: 32px;
  height: 32px;
  transition: all 0.3s;
  color: #666;
}

.pagination-bar :deep(.btn-prev):not(:disabled):hover,
.pagination-bar :deep(.btn-next):not(:disabled):hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.2);
  color: #00d4ff;
}

.pagination-bar :deep(.btn-prev:disabled),
.pagination-bar :deep(.btn-next:disabled) {
  opacity: 0.2;
  cursor: not-allowed;
}

.pagination-bar :deep(.el-pagination__sizes) {
  
}

.pagination-bar :deep(.el-select .el-input__wrapper) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 6px;
  padding: 4px 12px;
}

.pagination-bar :deep(.el-select__placeholder) {
  color: #666;
}

.pagination-bar :deep(.el-select__input) {
  color: #ccc;
}
</style>
