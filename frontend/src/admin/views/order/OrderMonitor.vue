<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Monitor /></el-icon>
        订单监控
      </h1>
    </header>

    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><ShoppingCart /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ orderStats.totalOrders }}</div>
              <div class="stat-label">总订单数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ orderStats.todayOrders }}</div>
              <div class="stat-label">今日订单</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ orderStats.abnormalOrders }}</div>
              <div class="stat-label">异常订单</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><Money /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">¥{{ orderStats.todaySales }}</div>
              <div class="stat-label">今日销售额</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="订单编号">
          <el-input v-model="filterForm.orderNo" placeholder="请输入订单编号" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="用户 ID">
          <el-input v-model="filterForm.userId" placeholder="请输入用户 ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="商家 ID">
          <el-input v-model="filterForm.merchantId" placeholder="请输入商家 ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="订单状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
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
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchOrders">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-section">
      <el-table :data="orderList" class="sci-table" style="width: 100%" v-loading="loading">
        <el-table-column prop="orderNo" label="订单编号" width="160" />
        <el-table-column prop="userId" label="用户 ID" width="90" />
        <el-table-column prop="shopName" label="店铺名称" width="130" />
        <el-table-column prop="productName" label="商品名称" min-width="150" />
        <el-table-column prop="amount" label="订单金额" width="100">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.amount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="paymentMethod" label="支付方式" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.paymentMethod }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="orderTime" label="下单时间" width="160" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <div class="action-buttons">
              <el-button type="primary" text size="small" @click="viewOrder(row)">详情</el-button>
              <el-button
                v-if="row.status === 'refunding'"
                type="warning"
                text
                size="small"
                @click="intervene(row)"
              >
                介入
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadOrderList"
          @current-change="loadOrderList"
        />
      </div>
    </section>

    <el-dialog
      v-model="detailDialog.visible"
      title="订单详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border class="sci-descriptions">
        <el-descriptions-item label="订单编号">{{ currentOrder?.orderNo }}</el-descriptions-item>
        <el-descriptions-item label="用户 ID">{{ currentOrder?.userId }}</el-descriptions-item>
        <el-descriptions-item label="店铺名称">{{ currentOrder?.shopName }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentOrder?.productName }}</el-descriptions-item>
        <el-descriptions-item label="订单金额">¥{{ currentOrder?.amount }}</el-descriptions-item>
        <el-descriptions-item label="支付方式">{{ currentOrder?.paymentMethod }}</el-descriptions-item>
        <el-descriptions-item label="下单时间">{{ currentOrder?.orderTime }}</el-descriptions-item>
        <el-descriptions-item label="订单状态">
          <el-tag :type="getStatusType(currentOrder?.status || '')" size="small">
            {{ getStatusText(currentOrder?.status || '') }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="收货地址" :span="2">{{ currentOrder?.address || '暂无地址' }}</el-descriptions-item>
        <el-descriptions-item label="订单备注" :span="2">{{ currentOrder?.remark || '无' }}</el-descriptions-item>
      </el-descriptions>
      <template #footer>
        <el-button @click="detailDialog.visible = false">关闭</el-button>
      </template>
    </el-dialog>

    <el-dialog
      v-model="interveneDialog.visible"
      title="介入处理"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="interveneForm" label-width="80px">
        <el-form-item label="处理方式">
          <el-radio-group v-model="interveneForm.method">
            <el-radio label="refund">同意退款</el-radio>
            <el-radio label="reject">拒绝退款</el-radio>
            <el-radio label="negotiate">协商处理</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="处理意见">
          <el-input
            v-model="interveneForm.comment"
            type="textarea"
            :rows="4"
            placeholder="请输入处理意见"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="interveneDialog.visible = false">取消</el-button>
        <el-button type="primary" @click="confirmIntervene">确认处理</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Monitor, ShoppingCart, CircleCheck, CircleClose, Clock, Money } from '@element-plus/icons-vue'

interface OrderItem {
  orderNo: string
  userId: number
  shopName: string
  productName: string
  amount: number
  paymentMethod: string
  orderTime: string
  status: string
  address?: string
  remark?: string
}

interface FilterForm {
  orderNo: string
  userId: string
  merchantId: string
  status: string
  dateRange: [Date, Date] | null
}

interface OrderStats {
  totalOrders: number
  todayOrders: number
  abnormalOrders: number
  todaySales: string
}

interface Pagination {
  currentPage: number
  pageSize: number
  total: number
}

interface Dialog {
  visible: boolean
}

interface InterveneForm {
  method: string
  comment: string
}

const loading = ref(false)
const orderList = ref<OrderItem[]>([])
const filterForm = reactive<FilterForm>({
  orderNo: '',
  userId: '',
  merchantId: '',
  status: '',
  dateRange: null
})

const orderStats = ref<OrderStats>({
  totalOrders: 89760,
  todayOrders: 8976,
  abnormalOrders: 156,
  todaySales: '125.8 万'
})

const pagination = reactive<Pagination>({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const detailDialog = reactive<Dialog>({
  visible: false
})

const interveneDialog = reactive<Dialog>({
  visible: false
})

const interveneForm = reactive<InterveneForm>({
  method: '',
  comment: ''
})

const currentOrder = ref<OrderItem | null>(null)

const mockOrderData: OrderItem[] = [
  { orderNo: 'DD202603180001', userId: 1001, shopName: '品质优选店', productName: '无线蓝牙耳机', amount: 199, paymentMethod: '微信支付', orderTime: '2026-03-18 10:30:00', status: 'completed', address: '北京市朝阳区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603180002', userId: 1002, shopName: '时尚衣橱', productName: '春季新款连衣裙', amount: 299, paymentMethod: '支付宝', orderTime: '2026-03-18 09:15:00', status: 'shipped', address: '上海市浦东新区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603180003', userId: 1003, shopName: '数码港湾', productName: '机械键盘', amount: 459, paymentMethod: '微信支付', orderTime: '2026-03-18 08:45:00', status: 'paid', address: '广州市天河区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603170004', userId: 1004, shopName: '家居生活馆', productName: '空气净化器', amount: 1299, paymentMethod: '银行卡', orderTime: '2026-03-17 16:20:00', status: 'refunding', address: '深圳市南山区 xx 路 xx 号', remark: '商品有质量问题' },
  { orderNo: 'DD202603170005', userId: 1005, shopName: '美妆小屋', productName: '护肤套装', amount: 599, paymentMethod: '支付宝', orderTime: '2026-03-17 14:00:00', status: 'completed', address: '杭州市西湖区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603170006', userId: 1006, shopName: '食品专营店', productName: '进口零食大礼包', amount: 168, paymentMethod: '微信支付', orderTime: '2026-03-17 11:30:00', status: 'cancelled', address: '成都市武侯区 xx 路 xx 号', remark: '用户主动取消' },
  { orderNo: 'DD202603170007', userId: 1007, shopName: '电器城', productName: '智能电视 55 寸', amount: 2999, paymentMethod: '分期付款', orderTime: '2026-03-17 10:00:00', status: 'shipped', address: '武汉市江汉区 xx 路 xx 号', remark: '' },
  { orderNo: 'DD202603160008', userId: 1008, shopName: '图书文具店', productName: '儿童百科全书', amount: 89, paymentMethod: '支付宝', orderTime: '2026-03-16 20:00:00', status: 'completed', address: '南京市鼓楼区 xx 路 xx 号', remark: '' }
]

const loadOrderList = () => {
  loading.value = true
  setTimeout(() => {
    orderList.value = mockOrderData
    pagination.total = mockOrderData.length
    loading.value = false
  }, 500)
}

const searchOrders = () => {
  ElMessage.success('搜索功能演示')
  loadOrderList()
}

const resetFilter = () => {
  filterForm.orderNo = ''
  filterForm.userId = ''
  filterForm.merchantId = ''
  filterForm.status = ''
  filterForm.dateRange = null
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = {
    pending: 'warning',
    paid: 'primary',
    shipped: 'success',
    completed: 'info',
    cancelled: 'warning',
    refunding: 'danger'
  }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    pending: '待付款',
    paid: '待发货',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消',
    refunding: '退款中'
  }
  return map[status] || status
}

const viewOrder = (order: OrderItem) => {
  currentOrder.value = order
  detailDialog.visible = true
}

const intervene = (order: OrderItem) => {
  currentOrder.value = order
  interveneDialog.visible = true
  interveneForm.method = ''
  interveneForm.comment = ''
}

const confirmIntervene = () => {
  if (!interveneForm.method || !interveneForm.comment) {
    ElMessage.warning('请选择处理方式并填写处理意见')
    return
  }
  ElMessage.success('处理成功')
  interveneDialog.visible = false
  loadOrderList()
}

onMounted(() => {
  loadOrderList()
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
.stat-card.success { --glow-color: #00ff88; }
.stat-card.warning { --glow-color: #ffaa00; }
.stat-card.danger { --glow-color: #ff6666; }

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

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.4);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
  box-shadow: 0 0 15px rgba(255, 170, 0, 0.4);
}

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #ff6666, #ff4444);
  box-shadow: 0 0 15px rgba(255, 102, 102, 0.4);
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
  border-radius: 8px;
}

.search-bar :deep(.el-button--primary:hover) {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
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

/* 顶部光效装饰线 */
.table-section .header-glow {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, 
    transparent 0%, 
    rgba(0, 200, 255, 0.3) 20%,
    rgba(0, 255, 200, 0.5) 50%,
    rgba(0, 200, 255, 0.3) 80%,
    transparent 100%);
  box-shadow: 0 0 20px rgba(0, 200, 255, 0.8);
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

/* 对话框样式美化 - 科技风格 */
.sci-descriptions :deep(.el-descriptions__header) {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 2px solid rgba(0, 200, 255, 0.3);
}

.sci-descriptions :deep(.el-descriptions__label) {
  background: linear-gradient(180deg, 
    rgba(0, 100, 150, 0.3) 0%, 
    rgba(0, 50, 100, 0.4) 100%);
  color: #00ffff;
  border: 1px solid rgba(0, 200, 255, 0.4);
  text-shadow: 0 0 5px rgba(0, 255, 255, 0.3);
}

.sci-descriptions :deep(.el-descriptions__content) {
  background: rgba(5, 15, 40, 0.6);
  color: #aaccff;
  border: 1px solid rgba(0, 150, 200, 0.2);
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

/* 操作按钮样式 */
.action-buttons {
  display: flex;
  flex-direction: column;
  gap: 6px;
  align-items: flex-start;
  width: 100%;
}

.action-buttons .el-button {
  white-space: nowrap;
  margin: 0;
  display: inline-flex;
}
</style>
