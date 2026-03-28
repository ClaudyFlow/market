<template>
  <div class="order-center-page">
    <!-- 面包屑导航 -->
    <Breadcrumb />
    
    <div class="order-center">
      <h2 class="page-title">
        <el-icon><Document /></el-icon>
        订单中心 - 历史购买记录
      </h2>
      
      <!-- 订单筛选 -->
      <div class="order-filters">
        <el-radio-group v-model="filterStatus" size="large">
          <el-radio-button label="">全部</el-radio-button>
          <el-radio-button label="pending">待付款</el-radio-button>
          <el-radio-button label="paid">已付款</el-radio-button>
          <el-radio-button label="shipped">已发货</el-radio-button>
          <el-radio-button label="completed">已完成</el-radio-button>
          <el-radio-button label="cancelled">已取消</el-radio-button>
        </el-radio-group>
      </div>
      
      <!-- 订单列表 -->
      <div v-if="filteredOrders.length > 0" class="orders-list">
        <div class="order-item" v-for="order in filteredOrders" :key="order.id">
          <div class="order-header">
            <div class="order-info-left">
              <span class="order-id">订单号：{{ order.orderNo }}</span>
              <span class="order-date">{{ order.createdAt }}</span>
            </div>
            <el-tag :type="getStatusType(order.status)" size="large">
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
          
          <div class="order-details">
            <div class="order-item-detail" v-for="(item, index) in order.items" :key="index">
              <img :src="item.image" :alt="item.name" class="item-image" />
              <div class="item-info">
                <h4>{{ item.name }}</h4>
                <p>数量：{{ item.quantity }}</p>
                <p>单价：¥{{ item.price }}</p>
              </div>
              <div class="item-subtotal">
                ¥{{ (item.price * item.quantity).toFixed(2) }}
              </div>
            </div>
          </div>
          
          <div class="order-footer">
            <div class="order-total">
              <span>总计：</span>
              <span class="total-price">¥{{ order.totalAmount }}</span>
            </div>
            <div class="order-actions">
              <el-button 
                v-if="order.status === 'pending'" 
                type="primary" 
                size="small"
                @click="handlePay(order)"
              >
                立即付款
              </el-button>
              <el-button 
                v-if="order.status === 'shipped'" 
                type="success" 
                size="small"
                @click="handleConfirmReceive(order)"
              >
                确认收货
              </el-button>
              <el-button 
                v-if="order.status === 'completed'" 
                type="info" 
                size="small"
                @click="handleReview(order)"
              >
                评价
              </el-button>
              <el-button 
                v-if="['pending', 'paid'].includes(order.status)" 
                type="danger" 
                size="small"
                @click="handleCancel(order)"
              >
                取消订单
              </el-button>
              <el-button 
                size="small"
                @click="handleViewDetail(order)"
              >
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </div>
      
      <!-- 空状态 -->
      <el-empty v-else description="暂无订单记录" class="empty-state">
        <el-button type="primary" @click="goToShop">去购物</el-button>
      </el-empty>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Document } from '@element-plus/icons-vue'
import Breadcrumb from '@user/components/Breadcrumb.vue'

interface OrderItem {
  id: number
  name: string
  price: number
  quantity: number
  image: string
}

interface Order {
  id: number
  orderNo: string
  status: string
  createdAt: string
  totalAmount: number
  items: OrderItem[]
}

const router = useRouter()
const filterStatus = ref('')

// 订单数据
const orders = ref<Order[]>([])

// 模拟订单数据
const mockOrders: Order[] = [
  {
    id: 1,
    orderNo: 'ORD202603270001',
    status: 'completed',
    createdAt: '2026-03-27 14:30:00',
    totalAmount: 7999.00,
    items: [
      {
        id: 101,
        name: 'iPhone 15 Pro',
        price: 7999.00,
        quantity: 1,
        image: 'https://via.placeholder.com/100x100?text=iPhone'
      }
    ]
  },
  {
    id: 2,
    orderNo: 'ORD202603260002',
    status: 'shipped',
    createdAt: '2026-03-26 10:15:00',
    totalAmount: 12999.00,
    items: [
      {
        id: 102,
        name: 'MacBook Pro 14',
        price: 12999.00,
        quantity: 1,
        image: 'https://via.placeholder.com/100x100?text=MacBook'
      }
    ]
  },
  {
    id: 3,
    orderNo: 'ORD202603250003',
    status: 'pending',
    createdAt: '2026-03-25 16:45:00',
    totalAmount: 3999.00,
    items: [
      {
        id: 103,
        name: '海尔冰箱',
        price: 3999.00,
        quantity: 1,
        image: 'https://via.placeholder.com/100x100?text=冰箱'
      }
    ]
  },
  {
    id: 4,
    orderNo: 'ORD202603240004',
    status: 'paid',
    createdAt: '2026-03-24 09:20:00',
    totalAmount: 699.00,
    items: [
      {
        id: 104,
        name: 'Nike 运动鞋',
        price: 699.00,
        quantity: 1,
        image: 'https://via.placeholder.com/100x100?text=Nike'
      }
    ]
  },
  {
    id: 5,
    orderNo: 'ORD202603230005',
    status: 'cancelled',
    createdAt: '2026-03-23 20:00:00',
    totalAmount: 2899.00,
    items: [
      {
        id: 105,
        name: '格力空调 1.5 匹',
        price: 2899.00,
        quantity: 1,
        image: 'https://via.placeholder.com/100x100?text=空调'
      }
    ]
  }
]

// 筛选后的订单
const filteredOrders = computed(() => {
  if (!filterStatus.value) {
    return orders.value
  }
  return orders.value.filter(order => order.status === filterStatus.value)
})

// 获取状态类型
const getStatusType = (status: string) => {
  const types: Record<string, any> = {
    pending: 'warning',
    paid: 'info',
    shipped: 'success',
    completed: '',
    cancelled: 'danger'
  }
  return types[status] || 'info'
}

// 获取状态文本
const getStatusText = (status: string) => {
  const texts: Record<string, string> = {
    pending: '待付款',
    paid: '已付款',
    shipped: '已发货',
    completed: '已完成',
    cancelled: '已取消'
  }
  return texts[status] || status
}

// 加载订单
const loadOrders = async () => {
  try {
    // TODO: 调用后端 API
    // const res = await getUserOrders()
    // orders.value = res
    
    // 使用模拟数据
    orders.value = mockOrders
  } catch (error) {
    ElMessage.error('加载订单失败')
    orders.value = []
  }
}

// 立即付款
const handlePay = (order: Order) => {
  ElMessageBox.confirm('确认支付该订单吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('支付成功')
    order.status = 'paid'
  }).catch(() => {})
}

// 确认收货
const handleConfirmReceive = (order: Order) => {
  ElMessageBox.confirm('确认收到商品吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    ElMessage.success('确认收货成功')
    order.status = 'completed'
  }).catch(() => {})
}

// 评价
const handleReview = (order: Order) => {
  router.push(`/review?orderId=${order.id}`)
}

// 取消订单
const handleCancel = (order: Order) => {
  ElMessageBox.confirm('确认取消该订单吗？', '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    ElMessage.success('订单已取消')
    order.status = 'cancelled'
  }).catch(() => {})
}

// 查看详情
const handleViewDetail = (order: Order) => {
  router.push(`/order/${order.id}`)
}

// 去购物
const goToShop = () => {
  router.push('/item')
}

onMounted(() => {
  loadOrders()
})
</script>

<style scoped>
.order-center-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.15) 0%, rgba(10, 14, 26, 0.8) 100%);
  padding-bottom: 40px;
}

.order-center {
  max-width: 1000px;
  margin: 0 auto;
  padding: 30px 20px;
}

.page-title {
  font-size: 28px;
  color: #fff;
  margin-bottom: 30px;
  display: flex;
  align-items: center;
  gap: 10px;
  justify-content: center;
}

.page-title .el-icon {
  font-size: 32px;
  color: var(--mall-primary);
}

/* 订单筛选 */
.order-filters {
  margin-bottom: 30px;
  display: flex;
  justify-content: center;
}

/* 订单列表 */
.orders-list {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.order-item {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  transition: all 0.3s;
}

.order-item:hover {
  border-color: var(--mall-primary);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.2);
}

.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.order-info-left {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.order-id {
  font-weight: bold;
  color: var(--mall-primary);
  font-size: 16px;
}

.order-date {
  color: #888;
  font-size: 14px;
}

/* 订单详情 */
.order-details {
  margin-bottom: 20px;
}

.order-item-detail {
  display: flex;
  align-items: center;
  padding: 15px 0;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.order-item-detail:last-child {
  border-bottom: none;
}

.item-image {
  width: 80px;
  height: 80px;
  object-fit: cover;
  margin-right: 20px;
  border-radius: 8px;
  border: 1px solid rgba(0, 212, 255, 0.2);
}

.item-info {
  flex: 1;
}

.item-info h4 {
  margin: 0 0 8px 0;
  color: #fff;
  font-size: 16px;
}

.item-info p {
  margin: 4px 0;
  color: #888;
  font-size: 14px;
}

.item-subtotal {
  color: var(--mall-accent);
  font-weight: bold;
  font-size: 18px;
  min-width: 100px;
  text-align: right;
}

/* 订单底部 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 15px;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
}

.order-total {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 16px;
  color: #888;
}

.total-price {
  color: var(--mall-accent);
  font-weight: bold;
  font-size: 22px;
}

.order-actions {
  display: flex;
  gap: 10px;
}

/* 空状态 */
.empty-state {
  margin-top: 50px;
  padding: 50px 0;
}

.empty-state :deep(.el-empty__description) {
  color: #888;
  font-size: 16px;
}
</style>
