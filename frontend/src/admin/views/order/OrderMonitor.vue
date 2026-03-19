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
        <el-table-column label="操作" width="150" fixed="right">
          <template #default="{ row }">
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
    paid: 'info',
    shipped: 'success',
    completed: '',
    cancelled: 'info',
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
}

.page-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 24px;
}

.stats-cards {
  margin-bottom: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
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
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
}

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #ff6666, #ff4444);
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

.search-bar {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 15px 20px;
  margin-bottom: 20px;
}

.search-bar :deep(.el-form-item) {
  margin-bottom: 0;
}

.search-bar :deep(.el-form-item__label) {
  color: #aaa;
}

.table-section {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.price-text {
  color: var(--mall-primary);
  font-weight: bold;
  font-size: 15px;
}

.pagination-bar {
  margin-top: 20px;
  display: flex;
  justify-content: flex-end;
}

.sci-table :deep(.el-table__header th) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--mall-primary);
  font-size: 13px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.sci-table :deep(.el-table__body td) {
  background: transparent;
  color: #aaa;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 13px;
}

.sci-table :deep(.el-table__row:hover) {
  background: rgba(0, 212, 255, 0.05);
}

.sci-descriptions {
  --el-descriptions-bg-color: transparent;
}

.sci-descriptions :deep(.el-descriptions__label) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--mall-primary);
}

.sci-descriptions :deep(.el-descriptions__content) {
  color: #aaa;
}
</style>
