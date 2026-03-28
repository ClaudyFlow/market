<template>
  <div class="page-container">
    <div class="container">
      <div class="page-header">
        <div style="display: flex; align-items: center;">
          <el-button class="back-btn" @click="goBack" circle>
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <h2 class="sub-title">
            <el-icon><Document /></el-icon>
            订单中心
          </h2>
        </div>
      </div>

      <div class="orders-section">
        <div class="orders-list" v-loading="loading">
          <div v-for="order in orderList" :key="order.id" class="order-card">
            <div class="order-header">
              <div class="order-info">
                <span class="order-id">订单号：{{ order.orderNo }}</span>
                <span class="order-date">{{ formatDateTime(order.createdAt) }}</span>
              </div>
              <el-tag :type="getStatusType(order.status)" effect="dark">
                {{ getStatusText(order.status) }}
              </el-tag>
            </div>

            <div class="order-items">
              <div
                v-for="(item, index) in order.items"
                :key="index"
                class="order-item"
              >
                <el-image
                  :src="item.productImage || 'https://via.placeholder.com/80x80?text=商品'"
                  class="item-image"
                  fit="cover"
                />
                <div class="item-info">
                  <h4 class="item-name">{{ item.productName }}</h4>
                  <p class="item-spec">{{ item.spec || '默认规格' }}</p>
                  <div class="item-meta">
                    <span class="item-price">¥{{ (item.price / 100).toFixed(2) }}</span>
                    <span class="item-quantity">x{{ item.quantity }}</span>
                  </div>
                </div>
              </div>
            </div>

            <div class="order-footer">
              <div class="order-total">
                <span>订单总额：</span>
                <span class="total-amount">¥{{ (order.totalAmount / 100).toFixed(2) }}</span>
              </div>
              <div class="order-actions">
                <el-button
                  v-if="order.status === 'PENDING_PAYMENT'"
                  type="primary"
                  size="small"
                  @click="payOrder(order.id)"
                >
                  立即支付
                </el-button>
                <el-button
                  v-if="order.status === 'PAID'"
                  type="warning"
                  size="small"
                  @click="cancelOrder(order.id)"
                >
                  取消订单
                </el-button>
                <el-button
                  v-if="order.status === 'SHIPPED'"
                  type="success"
                  size="small"
                  @click="confirmReceive(order.id)"
                >
                  确认收货
                </el-button>
                <el-button
                  v-if="order.status === 'COMPLETED'"
                  size="small"
                  @click="reviewOrder(order)"
                >
                  评价
                </el-button>
                <el-button size="small" @click="viewDetail(order.id)">
                  订单详情
                </el-button>
              </div>
            </div>
          </div>

          <el-empty v-if="orderList.length === 0" description="暂无订单" />
        </div>

        <div class="pagination-bar" v-if="orderList.length > 0">
          <el-pagination
            v-model:current-page="pagination.currentPage"
            v-model:page-size="pagination.pageSize"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="total, sizes, prev, pager, next, jumper"
            @size-change="loadOrders"
            @current-change="loadOrders"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Document, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/common/utils/api'

const router = useRouter()

const loading = ref(false)
const orderList = ref([])

const pagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const goBack = () => {
  router.push('/user/center')
}

const getStatusType = (status) => {
  const types = {
    PENDING_PAYMENT: 'warning',
    PAID: 'primary',
    SHIPPED: 'success',
    COMPLETED: 'info',
    CANCELLED: 'danger',
    REFUNDED: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    PENDING_PAYMENT: '待支付',
    PAID: '已支付',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDED: '已退款'
  }
  return texts[status] || status
}

const formatDateTime = (dateTime) => {
  if (!dateTime) return ''
  const date = new Date(dateTime)
  return date.toLocaleString('zh-CN', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit'
  })
}

const loadOrders = async () => {
  loading.value = true
  try {
    const res = await api.get('/api/order/list', {
      params: {
        page: pagination.currentPage - 1,
        size: pagination.pageSize
      }
    })
    orderList.value = res.data.content || []
    pagination.total = res.data.total || 0
  } catch (error) {
    console.error('加载订单列表失败', error)
    ElMessage.error('加载订单列表失败')
  } finally {
    loading.value = false
  }
}

const payOrder = async (orderId) => {
  try {
    ElMessageBox.confirm('确定要支付该订单吗？', '支付确认', {
      type: 'warning'
    })
    await api.post(`/api/order/${orderId}/pay`)
    ElMessage.success('支付成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('支付失败', error)
      ElMessage.error('支付失败')
    }
  }
}

const cancelOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '取消确认', {
      type: 'warning'
    })
    await api.post(`/api/order/${orderId}/cancel`)
    ElMessage.success('订单已取消')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('取消订单失败', error)
      ElMessage.error('取消订单失败')
    }
  }
}

const confirmReceive = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认已收到商品？', '确认收货', {
      type: 'warning'
    })
    await api.post(`/api/order/${orderId}/confirm`)
    ElMessage.success('确认收货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('确认收货失败', error)
      ElMessage.error('确认收货失败')
    }
  }
}

const reviewOrder = (order) => {
  router.push({
    path: '/user/review',
    query: {
      orderId: order.id,
      productId: order.items?.[0]?.productId
    }
  })
}

const viewDetail = (orderId) => {
  router.push(`/user/order/${orderId}`)
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.15) 0%, rgba(10, 14, 26, 0.8) 100%);
  padding: 40px 20px;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.sub-title {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-btn {
  margin-right: 20px;
  background: transparent;
  border: 1px solid var(--mall-primary);
  color: var(--mall-primary);
  cursor: pointer;
}

.back-btn:hover {
  background: rgba(0, 212, 255, 0.1);
}

.orders-section {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 16px;
  padding: 24px;
  backdrop-filter: blur(10px);
}

.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
}

.order-card:hover {
  border-color: var(--mall-primary);
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.2);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-bottom: 16px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.order-info {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-id {
  font-size: 14px;
  color: var(--mall-primary);
  font-weight: bold;
}

.order-date {
  font-size: 12px;
  color: #88aacc;
}

.order-items {
  padding: 16px 0;
}

.order-item {
  display: flex;
  gap: 16px;
  padding: 12px 0;
}

.item-image {
  width: 80px;
  height: 80px;
  border-radius: 8px;
  flex-shrink: 0;
}

.item-info {
  flex: 1;
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.item-name {
  font-size: 14px;
  color: #fff;
  margin: 0 0 6px 0;
}

.item-spec {
  font-size: 12px;
  color: #88aacc;
  margin: 0 0 8px 0;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.item-price {
  font-size: 16px;
  color: var(--mall-primary);
  font-weight: bold;
}

.item-quantity {
  font-size: 12px;
  color: #88aacc;
}

.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.order-total {
  font-size: 14px;
  color: #ccc;
}

.total-amount {
  font-size: 18px;
  color: #ff6b6b;
  font-weight: bold;
  margin-left: 8px;
}

.order-actions {
  display: flex;
  gap: 12px;
}

.pagination-bar {
  display: flex;
  justify-content: flex-end;
  margin-top: 24px;
}
</style>
