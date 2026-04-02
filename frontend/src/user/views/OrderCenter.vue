<template>
  <div class="order-center">
    <div class="page-header">
      <h1><el-icon><ShoppingCart /></el-icon> 我的订单</h1>
    </div>

    <!-- 订单状态筛选 -->
    <div class="filter-tabs">
      <el-tabs v-model="activeStatus" @tab-change="loadOrders">
        <el-tab-pane label="全部订单" name="all" />
        <el-tab-pane label="待付款" name="PENDING" />
        <el-tab-pane label="待发货" name="PAID" />
        <el-tab-pane label="待收货" name="SHIPPED" />
        <el-tab-pane label="待评价" name="COMPLETED" />
        <el-tab-pane label="售后/退款" name="REFUNDING" />
      </el-tabs>
    </div>

    <!-- 订单列表 -->
    <div class="order-list" v-loading="loading">
      <div v-if="orders.length === 0" class="empty-state">
        <el-empty description="暂无订单" />
        <el-button type="primary" @click="goToShop">去逛逛</el-button>
      </div>

      <div v-for="order in orders" :key="order.id" class="order-card">
        <!-- 订单头部 -->
        <div class="order-header">
          <span class="order-no">订单号：{{ order.orderNo }}</span>
          <span class="order-time">{{ formatDate(order.createdAt) }}</span>
          <span class="order-status" :class="getStatusClass(order.status)">
            {{ getStatusText(order.status) }}
          </span>
        </div>

        <!-- 订单商品 -->
        <div class="order-items">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <el-image :src="item.productImage" fit="cover" class="item-image" />
            <div class="item-info">
              <div class="item-name">{{ item.productName }}</div>
              <div v-if="item.specs" class="item-specs">{{ item.specs }}</div>
              <div class="item-meta">
                <span class="item-price">¥{{ item.price }}</span>
                <span class="item-quantity">x{{ item.quantity }}</span>
              </div>
            </div>
            <div class="item-subtotal">¥{{ item.subtotal }}</div>
          </div>
        </div>

        <!-- 订单底部 -->
        <div class="order-footer">
          <div class="order-total">
            <span>订单总额：</span>
            <span class="total-amount">¥{{ order.totalAmount }}</span>
          </div>
          <div class="order-actions">
            <template v-if="order.status === 'PENDING'">
              <el-button size="small" @click="cancelOrder(order.id)">取消订单</el-button>
              <el-button size="small" type="primary" @click="payOrder(order.id)">立即付款</el-button>
            </template>
            <template v-else-if="order.status === 'PAID' || order.status === 'SHIPPED'">
              <el-button size="small" @click="showLogistics(order)">查看物流</el-button>
              <el-button size="small" v-if="order.status === 'SHIPPED'" @click="confirmReceive(order.id)">
                确认收货
              </el-button>
            </template>
            <template v-else-if="order.status === 'COMPLETED'">
              <el-button size="small" @click="viewDetail(order.id)">订单详情</el-button>
              <el-button size="small" type="primary" @click="reviewOrder(order.id)">评价</el-button>
            </template>
            <template v-else-if="order.status === 'REFUNDING'">
              <el-button size="small" @click="viewRefundDetail(order.id)">退款详情</el-button>
            </template>
            <template v-else-if="order.status === 'CANCELLED' || order.status === 'REFUNDED'">
              <el-button size="small" @click="deleteOrder(order.id)">删除订单</el-button>
            </template>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="total > pageSize">
      <el-pagination
        v-model:current-page="currentPage"
        v-model:page-size="pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        @size-change="loadOrders"
        @current-change="loadOrders"
      />
    </div>

    <!-- 物流信息弹窗 -->
    <el-dialog v-model="showLogisticsDialog" title="物流信息" width="600px">
      <div v-if="currentLogistics" class="logistics-content">
        <div class="logistics-header">
          <div class="tracking-no">运单号：{{ currentLogistics.trackingNo }}</div>
          <div class="carrier">物流公司：{{ currentLogistics.carrier }}</div>
        </div>
        <el-timeline class="logistics-timeline">
          <el-timeline-item
            v-for="(trace, index) in currentLogistics.traces"
            :key="index"
            :timestamp="trace.time"
            placement="top"
          >
            <div class="trace-content">{{ trace.description }}</div>
          </el-timeline-item>
        </el-timeline>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ShoppingCart } from '@element-plus/icons-vue'
import * as orderApi from '@user/api/order'
import { formatDate } from '@user/util/format'

const router = useRouter()

const loading = ref(false)
const orders = ref([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const activeStatus = ref('all')

const showLogisticsDialog = ref(false)
const currentLogistics = ref(null)

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params = {
      current: currentPage.value,
      size: pageSize.value,
      status: activeStatus.value === 'all' ? undefined : activeStatus.value
    }
    const result = await orderApi.getOrderList(params)
    orders.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 取消订单
const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消订单吗？', '提示', { type: 'warning' })
    await orderApi.cancelOrder(orderId, '用户主动取消')
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('取消订单失败')
    }
  }
}

// 支付订单
const payOrder = (orderId) => {
  ElMessageBox.confirm('确定要支付订单吗？', '提示', { type: 'info' })
    .then(async () => {
      try {
        const result = await orderApi.payOrder(orderId, 'alipay')
        window.open(result.payUrl, '_blank')
        ElMessage.success('正在跳转支付页面')
      } catch (error) {
        ElMessage.error('支付失败')
      }
    })
    .catch(() => {})
}

// 确认收货
const confirmReceive = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '提示', { type: 'warning' })
    await orderApi.confirmReceive(orderId)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('确认收货失败')
    }
  }
}

// 查看订单详情
const viewDetail = (orderId) => {
  router.push(`/order/${orderId}`)
}

// 评价订单
const reviewOrder = (orderId) => {
  router.push(`/order/${orderId}/review`)
}

// 查看物流
const showLogistics = async (order) => {
  try {
    const logistics = await orderApi.getOrderLogistics(order.id)
    currentLogistics.value = logistics
    showLogisticsDialog.value = true
  } catch (error) {
    ElMessage.error('获取物流信息失败')
  }
}

// 查看退款详情
const viewRefundDetail = (orderId) => {
  router.push(`/order/${orderId}/refund`)
}

// 删除订单
const deleteOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要删除订单吗？', '提示', { type: 'warning' })
    await orderApi.deleteOrder(orderId)
    ElMessage.success('订单已删除')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除订单失败')
    }
  }
}

// 获取状态样式类
const getStatusClass = (status) => {
  const classMap = {
    PENDING: 'status-pending',
    PAID: 'status-paid',
    SHIPPED: 'status-shipped',
    COMPLETED: 'status-completed',
    CANCELLED: 'status-cancelled',
    REFUNDING: 'status-refunding',
    REFUNDED: 'status-refunded'
  }
  return classMap[status] || ''
}

// 获取状态文本
const getStatusText = (status) => {
  const textMap = {
    PENDING: '待付款',
    PAID: '待发货',
    SHIPPED: '待收货',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDING: '退款中',
    REFUNDED: '已退款'
  }
  return textMap[status] || status
}

// 去店铺
const goToShop = () => {
  router.push('/shops')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-center {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h1 {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
  display: flex;
  align-items: center;
  gap: 10px;
}

.filter-tabs {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 0 20px;
  margin-bottom: 20px;
}

.order-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
}

.order-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  overflow: hidden;
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-bottom: 1px solid var(--mall-border);
}

.order-no {
  font-size: 14px;
  color: var(--mall-text-secondary);
}

.order-time {
  font-size: 13px;
  color: var(--mall-text-muted);
}

.order-status {
  font-size: 14px;
  font-weight: bold;
  padding: 4px 12px;
  border-radius: 4px;
}

.status-pending {
  background: rgba(255, 153, 0, 0.2);
  color: #ff9900;
}

.status-paid {
  background: rgba(0, 212, 255, 0.2);
  color: #00d4ff;
}

.status-shipped {
  background: rgba(0, 255, 136, 0.2);
  color: #00ff88;
}

.status-completed {
  background: rgba(157, 78, 221, 0.2);
  color: #9d4edd;
}

.status-cancelled,
.status-refunded {
  background: rgba(255, 68, 68, 0.2);
  color: #ff4444;
}

.status-refunding {
  background: rgba(255, 187, 0, 0.2);
  color: #ffbb00;
}

.order-items {
  padding: 20px;
}

.order-item {
  display: flex;
  gap: 15px;
  padding: 15px 0;
  border-bottom: 1px solid var(--mall-border);
}

.order-item:last-child {
  border-bottom: none;
}

.item-image {
  width: 100px;
  height: 100px;
  border-radius: 8px;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.item-name {
  font-size: 14px;
  font-weight: bold;
  color: #fff;
}

.item-specs {
  font-size: 12px;
  color: var(--mall-text-muted);
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 16px;
  font-weight: bold;
  color: var(--mall-primary);
}

.item-quantity {
  font-size: 13px;
  color: var(--mall-text-muted);
}

.item-subtotal {
  font-size: 16px;
  font-weight: bold;
  color: var(--mall-primary);
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background: rgba(255, 255, 255, 0.05);
  border-top: 1px solid var(--mall-border);
}

.order-total {
  font-size: 14px;
  color: var(--mall-text-secondary);
}

.total-amount {
  font-size: 18px;
  font-weight: bold;
  color: var(--mall-primary);
  margin-left: 10px;
}

.order-actions {
  display: flex;
  gap: 10px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--mall-border);
}

.logistics-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--mall-border);
}

.tracking-no,
.carrier {
  font-size: 14px;
  color: var(--mall-text-secondary);
  margin-bottom: 8px;
}

.trace-content {
  font-size: 14px;
  color: var(--mall-text-secondary);
}
</style>
