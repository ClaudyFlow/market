<template>
  <div class="order-detail" v-loading="loading">
    <!-- 返回按钮 -->
    <div class="detail-header">
      <el-button text @click="goBack">
        <el-icon><ArrowLeft /></el-icon> 返回订单列表
      </el-button>
      <h2>订单详情</h2>
    </div>

    <!-- 订单不存在 -->
    <div v-if="!loading && !order" class="empty-state">
      <el-empty description="订单不存在或已删除">
        <el-button type="primary" @click="goBack">返回订单列表</el-button>
      </el-empty>
    </div>

    <template v-if="order">
      <!-- 订单状态卡片 -->
      <div class="status-card" :class="`status-${(order.status || '').toLowerCase()}`">
        <div class="status-main">
          <div class="status-icon">
            <el-icon :size="36"><component :is="statusIcon" /></el-icon>
          </div>
          <div class="status-info">
            <h3>{{ getStatusText(order.status) }}</h3>
            <p v-if="statusTip">{{ statusTip }}</p>
          </div>
        </div>
        <div class="status-actions" v-if="showStatusActions">
          <el-button
            v-for="action in statusActions"
            :key="action.key"
            :type="action.type || 'primary'"
            size="default"
            @click="handleAction(action.key)"
          >{{ action.label }}</el-button>
        </div>
      </div>

      <!-- 收货地址 -->
      <section class="info-section">
        <div class="section-title">
          <el-icon><Location /></el-icon> 收货信息
          <el-button v-if="canEditAddress" text type="primary" size="small" @click="showEditAddress = true">修改</el-button>
        </div>
        <div class="address-card" v-if="order.shippingAddress">
          <span class="receiver-name">{{ order.shippingAddress.receiverName }}</span>
          <span class="receiver-phone">{{ maskPhone(order.shippingAddress.receiverPhone) }}</span>
          <p class="address-detail">
            {{ [order.shippingAddress.province, order.shippingAddress.city, order.shippingAddress.district, order.shippingAddress.detailAddress].filter(Boolean).join('') }}
          </p>
        </div>
        <div v-else class="no-address">暂无收货地址</div>
      </section>

      <!-- 商品清单 -->
      <section class="info-section">
        <div class="section-title">
          <el-icon><Goods /></el-icon> 商品清单
        </div>
        <div class="product-list">
          <div v-for="item in order.items" :key="item.id" class="product-row">
            <el-image :src="item.productImage" fit="cover" class="product-image">
              <template #error>
                <div class="image-error"><el-icon><PictureFilled /></el-icon></div>
              </template>
            </el-image>
            <div class="product-info">
              <div class="product-name ellipsis-2">{{ item.productName }}</div>
              <div v-if="item.specs" class="product-specs">{{ item.specs }}</div>
              <div class="product-meta">
                <span class="product-price">¥{{ item.price?.toFixed(2) }}</span>
                <span class="product-qty">x{{ item.quantity }}</span>
              </div>
            </div>
            <div class="product-total">
              <span>¥{{ item.subtotal?.toFixed(2) || (item.price * item.quantity).toFixed(2) }}</span>
              <el-tag v-if="item.reviewStatus === 'reviewed'" size="small" type="success" effect="light" round style="margin-top: 4px;">
                已评价
              </el-tag>
              <el-tag v-if="item.reviewStatus === 'not_reviewed' && order.status === 'COMPLETED'" size="small" type="warning" effect="light" round style="margin-top: 4px;">
                待评价
              </el-tag>
            </div>
          </div>
        </div>

        <!-- 费用明细 -->
        <div class="fee-detail">
          <div class="fee-row">
            <span>商品总额</span>
            <span>¥{{ (order.totalAmount + (order.discountAmount || 0)).toFixed(2) }}</span>
          </div>
          <div v-if="order.freightAmount > 0" class="fee-row">
            <span>运费</span>
            <span>+ ¥{{ order.freightAmount.toFixed(2) }}</span>
          </div>
          <div v-if="order.discountAmount > 0" class="fee-row discount">
            <span>优惠减免</span>
            <span>- ¥{{ order.discountAmount.toFixed(2) }}</span>
          </div>
          <div class="fee-row total">
            <span>实付金额</span>
            <span class="total-price">¥{{ order.payAmount?.toFixed(2) || order.totalAmount?.toFixed(2) }}</span>
          </div>
        </div>
      </section>

      <!-- 支付信息 -->
      <section class="info-section" v-if="order.paymentInfo">
        <div class="section-title">
          <el-icon><CreditCard /></el-icon> 支付信息
        </div>
        <div class="info-grid">
          <div class="grid-item">
            <span class="label">支付方式</span>
            <span class="value">{{ payTypeMap[order.paymentInfo.payType] || order.paymentInfo.payType }}</span>
          </div>
          <div class="grid-item">
            <span class="label">支付时间</span>
            <span class="value">{{ order.paymentInfo.payTime || '-' }}</span>
          </div>
          <div class="grid-item" v-if="order.paymentInfo.transactionId">
            <span class="label">交易单号</span>
            <span class="value mono">{{ order.paymentInfo.transactionId }}</span>
          </div>
        </div>
      </section>

      <!-- 物流信息 -->
      <section class="info-section" v-if="['PAID', 'SHIPPED', 'COMPLETED', 'REFUNDING', 'REFUNDED'].includes(order.status)">
        <div class="section-title">
          <el-icon><Van /></el-icon> 物流信息
          <el-button v-if="order.logistics?.traces?.length" text type="primary" size="small" @click="showLogisticsDetail = true">
            查看详情
          </el-button>
        </div>
        <div class="logistics-summary" v-if="order.logistics">
          <div class="logistics-info">
            <span>{{ order.logistics.company || order.logistics.carrier || '未知物流' }}</span>
            <span class="tracking-no">{{ order.logistics.trackingNo || '-' }}</span>
          </div>
          <p class="logistics-status">{{ order.logistics.statusDesc || '暂无物流信息' }}</p>
          <el-timeline v-if="order.logistics.traces?.length" class="logistics-mini">
            <el-timeline-item
              v-for="(trace, idx) in order.logistics.traces.slice(0, 2)"
              :key="idx"
              :timestamp="trace.time"
              :type="idx === 0 ? 'primary' : undefined"
              placement="top"
              size="small"
            >
              {{ trace.description }}
            </el-timeline-item>
          </el-timeline>
        </div>
        <div v-else class="no-logistics">暂无物流信息</div>
      </section>

      <!-- 退款信息 -->
      <section class="info-section" v-if="order.refundInfo && ['REFUNDING', 'REFUNDED'].includes(order.status)">
        <div class="section-title">
          <el-icon><Warning /></el-icon> 售后/退款信息
        </div>
        <div class="refund-info">
          <div class="refund-status-tag">
            <el-tag :type="refundTagType(order.refundInfo.status)" effect="dark" round>
              {{ refundStatusText(order.refundInfo.status) }}
            </el-tag>
          </div>
          <div class="refund-details">
            <div class="detail-item">
              <span class="label">退款金额</span>
              <span class="amount">¥{{ order.refundInfo.amount.toFixed(2) }}</span>
            </div>
            <div class="detail-item">
              <span class="label">退款原因</span>
              <span>{{ order.refundInfo.reason }}</span>
            </div>
            <div class="detail-item">
              <span class="label">申请时间</span>
              <span>{{ order.refundInfo.applyTime }}</span>
            </div>
            <div v-if="order.refundInfo.handleTime" class="detail-item">
              <span class="label">处理时间</span>
              <span>{{ order.refundInfo.handleTime }}</span>
            </div>
            <div v-if="order.refundInfo.remark" class="detail-item full-width">
              <span class="label">商家备注</span>
              <p>{{ order.refundInfo.remark }}</p>
            </div>
          </div>
        </div>
      </section>

      <!-- 订单信息 -->
      <section class="info-section">
        <div class="section-title">
          <el-icon><Document /></el-icon> 订单信息
        </div>
        <div class="info-grid">
          <div class="grid-item">
            <span class="label">订单编号</span>
            <span class="value mono">{{ order.orderNo }} <el-button text size="small" @click="copyOrderNo">复制</el-button></span>
          </div>
          <div class="grid-item">
            <span class="label">下单时间</span>
            <span class="value">{{ formatDateTime(order.createdAt || order.createTime) }}</span>
          </div>
          <div v-if="order.payTime" class="grid-item">
            <span class="label">支付时间</span>
            <span class="value">{{ formatDateTime(order.payTime) }}</span>
          </div>
          <div v-if="order.deliveryTime" class="grid-item">
            <span class="label">发货时间</span>
            <span class="value">{{ formatDateTime(order.deliveryTime) }}</span>
          </div>
          <div v-if="order.receiveTime" class="grid-item">
            <span class="label">收货时间</span>
            <span class="value">{{ formatDateTime(order.receiveTime) }}</span>
          </div>
          <div v-if="order.shopName" class="grid-item">
            <span class="label">所属店铺</span>
            <span class="value shop-link" @click="goToShop(order.shopId)">{{ order.shopName }}</span>
          </div>
          <div v-if="order.remark" class="grid-item full-width">
            <span class="label">订单备注</span>
            <span class="value remark-text">{{ order.remark }}</span>
          </div>
          <div v-if="order.cancelReason" class="grid-item full-width">
            <span class="label">取消原因</span>
            <span class="value cancel-reason">{{ order.cancelReason }}</span>
          </div>
        </div>
      </section>

      <!-- 底部操作栏 -->
      <div class="bottom-actions" v-if="bottomActions.length">
        <el-button
          v-for="action in bottomActions"
          :key="action.key"
          :type="action.type || ''"
          :plain="!!action.plain"
          size="large"
          round
          @click="handleAction(action.key)"
        >
          <el-icon v-if="action.icon"><component :is="action.icon" /></el-icon>
          {{ action.label }}
        </el-button>
      </div>
    </template>

    <!-- 物流详情弹窗 -->
    <el-dialog v-model="showLogisticsDetail" title="物流详情" width="600px" destroy-on-close>
      <div v-if="order?.logistics" class="logistics-dialog-content">
        <div class="logistics-header">
          <div><el-icon><OfficeBuilding /></el-icon> {{ order.logistics.company || order.logistics.carrier }}</div>
          <div>运单号：<strong>{{ order.logistics.trackingNo }}</strong></div>
          <div v-if="order.logistics.estimatedDelivery"><el-icon><Clock /></el-icon> 预计送达：{{ order.logistics.estimatedDelivery }}</div>
        </div>
        <el-timeline>
          <el-timeline-item
            v-for="(trace, index) in order.logistics.traces"
            :key="index"
            :timestamp="trace.time"
            :type="index === 0 ? 'primary' : undefined"
            :hollow="index !== 0"
            placement="top"
          >
            <div>
              <div>{{ trace.description }}</div>
              <div v-if="trace.location" style="font-size:12px;color:var(--mall-text-muted);margin-top:2px;">{{ trace.location }}</div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-if="!order.logistics.traces?.length" description="暂无物流轨迹" />
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeft, Clock, CreditCard, Document, Goods, Location,
  OfficeBuilding, PictureFilled, Van, Warning,
  ShoppingCart, Money, Check, CircleClose, Loading,
  RefreshRight, Star, Share, Edit, ChatDotRound
} from '@element-plus/icons-vue'
import * as orderApi from '@user/api/order'
import { useLocalCartStore } from '@user/stores/cart-local'
import type { Order, OrderDetail as OrderDetailType, OrderLogistics, RefundInfo } from '@user/types/order'

const route = useRoute()
const router = useRouter()
const localCart = useLocalCartStore()

const loading = ref(false)
const order = ref<OrderDetailType | null>(null)
const showLogisticsDetail = ref(false)
const showEditAddress = ref(false)

// 状态图标映射
const statusIconMap: Record<string, Component> = {
  pending_payment: Money,
  paid: Loading,
  shipped: Van,
  delivered: Check,
  completed: Check,
  cancelled: CircleClose,
  refunding: Warning,
  refunded: CircleClose,
  closed: CircleClose,
  PENDING: Money,
  PAID: Loading,
  SHIPPED: Van,
  COMPLETED: Check,
  CANCELLED: CircleClose,
  REFUNDING: Warning,
  REFUNDED: CircleClose,
}

const payTypeMap: Record<string, string> = {
  alipay: '支付宝',
  wechat: '微信支付',
  card: '银行卡',
  balance: '余额支付',
}

// 状态提示文案
const statusTips: Record<string, string> = {
  PENDING: '请尽快完成支付，订单将在超时后自动取消',
  PAID: '商家已接单，正在准备商品中',
  SHIPPED: '商品正在配送中，请保持电话畅通',
  COMPLETED: '感谢您的购买，期待再次光临！',
  CANCELLED: '订单已取消，如有需要可以重新下单',
  REFUNDING: '退款申请已提交，等待商家处理中',
  REFUNDED: '退款已完成，款项将原路退回',
}

const statusIcon = computed(() => statusIconMap[order.value?.status] || ShoppingCart)
const statusTip = computed(() => statusTips[order.value?.status] || '')

// 是否可编辑地址
const canEditAddress = computed(() =>
  ['PENDING', 'pending_payment'].includes(order.value?.status)
)

// 状态区域操作按钮
const statusActions = computed(() => {
  const s = order.value?.status
  if (!s) return []
  const actions: any[] = []
  if (s === 'PENDING' || s === 'pending_payment') {
    actions.push({ key: 'pay', label: '立即付款', type: 'warning' })
  }
  if (s === 'SHIPPED' || s === 'shipped') {
    actions.push({ key: 'confirm', label: '确认收货', type: 'success' })
  }
  return actions
})

const showStatusActions = computed(() => statusActions.value.length > 0)

// 底部操作按钮
const bottomActions = computed(() => {
  const s = order.value?.status
  if (!s) return []
  const actions: any[] = []

  // 所有状态通用
  actions.push({ key: 'share', label: '分享订单', icon: Share, plain: true })

  if (s === 'PENDING' || s === 'pending_payment') {
    actions.push({ key: 'cancel', label: '取消订单', plain: true })
  }

  if (['PAID', 'paid'].includes(s)) {
    actions.push({ key: 'refund', label: '申请退款', plain: true, type: 'warning' })
  }

  if (['SHIPPED', 'shipped'].includes(s)) {
    // 已有顶部确认按钮，底部不重复
  }

  if (['COMPLETED', 'completed'].includes(s)) {
    actions.push(
      { key: 'repurchase', label: '再买一单', icon: RefreshRight, plain: true },
      { key: 'favorite', label: '收藏商品', icon: Star, plain: true },
      { key: 'refund', label: '申请售后', plain: true, type: 'warning' },
      { key: 'review', label: '评价', type: 'primary' }
    )
  }

  if (['CANCELLED', 'cancelled', 'REFUNDED', 'refunded', 'CLOSED', 'closed'].includes(s)) {
    actions.push(
      { key: 'repurchase', label: '再买一单', icon: RefreshRight, plain: true },
      { key: 'delete', label: '删除订单', plain: true, type: 'danger' }
    )
  }

  if (['REFUNDING', 'refunding'].includes(s)) {
    actions.push({ key: 'refund_detail', label: '查看退款进度', plain: true })
  }

  return actions
})

// 加载订单详情
const loadDetail = async () => {
  loading.value = true
  try {
    orderId.value = route.params.id as string
    order.value = await orderApi.getOrderDetail(orderId.value)
  } catch (error: any) {
    console.error('加载订单详情失败:', error)
    ElMessage.error(error?.message || '加载订单详情失败')
  } finally {
    loading.value = false
  }
}

// 操作分发
const handleAction = async (key: string) => {
  switch (key) {
    case 'pay':
      await doPay()
      break
    case 'cancel':
      await doCancel()
      break
    case 'confirm':
      await doConfirm()
      break
    case 'repurchase':
      doRepurchase()
      break
    case 'favorite':
      doFavorite()
      break
    case 'refund':
      goRefund()
      break
    case 'review':
      goReview()
      break
    case 'share':
      doShare()
      break
    case 'delete':
      await doDelete()
      break
    case 'refund_detail':
      router.push({ path: `/user/orders/${orderId.value}/refund` })
      break
    default:
      ElMessage.info(`功能开发中：${key}`)
  }
}

// ========== 具体操作实现 ==========
const doPay = async () => {
  try {
    await ElMessageBox.confirm('即将跳转到支付页面', '确认支付', { type: 'info' })
    const result = await orderApi.payOrder(orderId.value, 'alipay')
    window.open(result.payUrl, '_blank')
  } catch (e) {
    /* 取消 */
  }
}

const doCancel = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？', '确认取消', { type: 'warning' })
    await orderApi.cancelOrder(orderId.value, '用户主动取消')
    ElMessage.success('订单已取消')
    loadDetail()
  } catch (e) { /* 取消 */ }
}

const doConfirm = async () => {
  try {
    await ElMessageBox.confirm('确定已收到商品吗？', '确认收货', { type: 'warning' })
    await orderApi.confirmReceive(orderId.value)
    ElMessage.success('确认收货成功')
    loadDetail()
  } catch (e) { /* 取消 */ }
}

const doRepurchase = () => {
  order.value.items?.forEach((item: any) => {
    for (let i = 0; i < item.quantity; i++) {
      localCart.addToCart({
        id: item.productId || item.id,
        name: item.productName,
        price: Number(item.price),
        originalPrice: Number(item.price),
        image: item.productImage,
        shopId: order.value.shopId,
        shopName: order.value.shopName,
        spec: item.specs || ''
      })
    }
  })
  ElMessage.success('已加入购物车')
  setTimeout(() => router.push('/cart'), 500)
}

const doFavorite = async () => {
  let count = 0
  for (const item of order.value.items || []) {
    try {
      await import('@user/api/favorite').then(m =>
        m.addFavorite({ type: 'product', itemId: item.productId || item.id, name: item.productName, image: item.productImage })
      )
      count++
    } catch { /* ignore */ }
  }
  ElMessage.success(count > 0 ? `已收藏 ${count} 件商品` : '可能已经收藏过了')
}

const goRefund = () => {
  router.push({ path: `/user/orders/${orderId.value}/refund` })
}

const goReview = () => {
  router.push({ path: `/user/orders/${orderId.value}/review` })
}

const doShare = () => {
  const url = `${window.location.origin}${route.fullPath}`
  navigator.clipboard.writeText(url).then(() => {
    ElMessage.success('订单链接已复制到剪贴板')
  }).catch(() => {
    ElMessage.warning('自动复制失败，请手动复制页面链接分享')
  })
}

const doDelete = async () => {
  try {
    await ElMessageBox.confirm('删除后将无法恢复此订单记录', '删除订单', { type: 'warning' })
    await orderApi.deleteOrder(orderId.value)
    ElMessage.success('订单已删除')
    router.push('/user/orders')
  } catch (e) { /* 取消 */ }
}

// 辅助方法
const getStatusText = (status: string) => {
  const map: Record<string, string> = {
    PENDING: '待付款', pending_payment: '待付款',
    PAID: '待发货', paid: '待发货',
    SHIPPED: '待收货', shipped: '待收货',
    delivered: '已送达',
    COMPLETED: '已完成', completed: '已完成',
    CANCELLED: '已取消', cancelled: '已取消',
    REFUNDING: '退款中', refunding: '退款中',
    REFUNDED: '已退款', refunded: '已退款',
    CLOSED: '已关闭', closed: '已关闭',
  }
  return map[status] || status
}

const refundTagType = (s: string): 'success' | 'warning' | 'danger' | 'info' | undefined => {
  const map: Record<string, any> = { pending: 'warning', approved: 'success', rejected: 'danger', success: 'success' }
  return map[s]
}

const refundStatusText = (s: string) => {
  const map: Record<string, string> = { pending: '待处理', approved: '已同意', rejected: '已拒绝', success: '退款成功' }
  return map[s] || s
}

const maskPhone = (phone?: string) => {
  if (!phone) return '-'
  return phone.replace(/(\d{3})\d{4}(\d{4})/, '$1****$2')
}

const formatDateTime = (date?: string) => {
  if (!date) return '-'
  return new Date(date).toLocaleString('zh-CN', {
    year: 'numeric', month: '2-digit', day: '2-digit',
    hour: '2-digit', minute: '2-digit', second: '2-digit'
  })
}

const copyOrderNo = () => {
  navigator.clipboard.writeText(order.value.orderNo).then(() => ElMessage.success('订单号已复制'))
}

const goBack = () => router.push('/user/orders')
const goToShop = (shopId?: number) => {
  if (shopId) router.push(`/shop/${shopId}`)
}

const orderId = ref('')

onMounted(loadDetail)
</script>

<style scoped>
.order-detail {
  max-width: 960px;
  margin: 0 auto;
  padding: 20px;
}

.detail-header {
  display: flex;
  align-items: center;
  gap: 16px;
  margin-bottom: 20px;
}

.detail-header h2 {
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  margin: 0;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: var(--mall-bg-card);
  border-radius: 12px;
}

/* 状态卡片 */
.status-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 16px;
  padding: 28px 32px;
  margin-bottom: 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  overflow: hidden;
}

.status-card::before {
  content: '';
  position: absolute;
  top: 0; left: 0; right: 0;
  height: 4px;
}

.status-pending::before { background: linear-gradient(90deg, #ff9900, #ff6600); }
.status-paid::before { background: linear-gradient(90deg, #409eff, #00d4ff); }
.status-shipped::before { background: linear-gradient(90deg, #67c23a, #00ff88); }
.status-completed::before { background: linear-gradient(90deg, #9d4edd, #c77dff); }
.status-cancelled::before,
.status-refunded::before,
.status-closed::before { background: linear-gradient(90deg, #f56c6c, #ff4444); }
.status-refunding::before { background: linear-gradient(90deg, #e6a23c, #ffbb00); }

.status-main {
  display: flex;
  align-items: center;
  gap: 18px;
  z-index: 1;
}

.status-icon {
  width: 72px;
  height: 72px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
  flex-shrink: 0;
}

.status-pending .status-icon { background: rgba(255, 153, 0, 0.15); color: #ff9900; }
.status-paid .status-icon { background: rgba(64, 158, 255, 0.15); color: #409eff; }
.status-shipped .status-icon { background: rgba(103, 194, 58, 0.15); color: #67c23a; }
.status-completed .status-icon { background: rgba(157, 78, 221, 0.15); color: #9d4edd; }
.status-cancelled .status-icon,
.status-refunded .status-icon,
.status-closed .status-icon { background: rgba(245, 108, 108, 0.15); color: #f56c6c; }
.status-refunding .status-icon { background: rgba(230, 162, 60, 0.15); color: #e6a23c; }

.status-info h3 {
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  margin: 0 0 6px 0;
}

.status-info p {
  font-size: 13px;
  color: var(--mall-text-secondary);
  margin: 0;
}

.status-actions {
  z-index: 1;
}

/* 信息区块 */
.info-section {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  margin-bottom: 18px;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.06);
}

/* 地址 */
.address-card {
  background: rgba(64, 158, 255, 0.06);
  border: 1px solid rgba(64, 158, 255, 0.15);
  border-radius: 10px;
  padding: 18px 20px;
}

.receiver-name {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  margin-right: 14px;
}

.receiver-phone {
  font-size: 15px;
  color: var(--mall-text-secondary);
}

.address-detail {
  margin: 8px 0 0;
  font-size: 14px;
  color: var(--mall-text-secondary);
  line-height: 1.5;
}

.no-address, .no-logistics {
  color: var(--mall-text-muted);
  font-size: 13px;
}

/* 商品列表 */
.product-list {
  margin-bottom: 20px;
}

.product-row {
  display: flex;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}

.product-row:last-child {
  border-bottom: none;
}

.product-image {
  width: 88px;
  height: 88px;
  border-radius: 10px;
  flex-shrink: 0;
  background: rgba(255, 255, 255, 0.06);
}

.image-error {
  width: 100%; height: 100%;
  display: flex; align-items: center; justify-content: center;
  color: var(--mall-text-muted); font-size: 24px;
  border-radius: 10px;
}

.product-info {
  flex: 1; min-width: 0;
  display: flex; flex-direction: column; justify-content: space-between;
  height: 88px;
}

.product-name {
  font-size: 14px; font-weight: 500; color: #eee; line-height: 1.4;
}
.ellipsis-2 { display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical; overflow: hidden; }
.product-specs { font-size: 12px; color: var(--mall-text-muted); margin-top: 2px; }
.product-meta { display: flex; justify-content: space-between; align-items: center; gap: 8px; }
.product-price { font-size: 15px; font-weight: bold; color: var(--mall-primary); }
.product-qty { font-size: 13px; color: var(--mall-text-muted); }
.product-total {
  flex-shrink: 0; text-align: right;
  display: flex; flex-direction: column; align-items: flex-end; justify-content: center;
  font-size: 15px; font-weight: 600; color: #eee;
}

/* 费用明细 */
.fee-detail {
  background: rgba(255, 255, 255, 0.03);
  border-radius: 10px;
  padding: 16px 20px;
}

.fee-row {
  display: flex; justify-content: space-between; align-items: center;
  padding: 5px 0; font-size: 14px; color: var(--mall-text-secondary);
}
.fee-row.discount span:last-child { color: #ff4757; }
.fee-row.total {
  border-top: 1px dashed rgba(255, 255, 255, 0.08);
  margin-top: 8px; padding-top: 10px;
  font-size: 15px; color: #fff;
}
.total-price {
  font-size: 20px; font-weight: bold; color: #ff4757;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(260px, 1fr));
  gap: 12px;
}
.grid-item {
  display: flex; flex-direction: column; gap: 4px;
}
.grid-item.full-width { grid-column: 1 / -1; }
.grid-item .label {
  font-size: 12px; color: var(--mall-text-muted);
}
.grid-item .value {
  font-size: 14px; color: #eee;
}
.mono { font-family: 'SF Mono', Consolas, monospace; letter-spacing: 0.5px; }
.shop-link {
  color: var(--mall-primary); cursor: pointer;
  transition: opacity 0.2s;
}
.shop-link:hover { opacity: 0.8; }
.remark-text, .cancel-reason {
  background: rgba(255, 255, 255, 0.04); padding: 8px 12px; border-radius: 6px;
  font-size: 13px;
}

/* 物流摘要 */
.logistics-summary { }
.logistics-info {
  display: flex; gap: 16px; align-items: center; font-size: 14px; color: var(--mall-text-secondary);
  margin-bottom: 8px;
}
.tracking-no { font-family: 'SF Mono', Consolas, monospace; }
.logistics-status { font-size: 14px; color: #eee; margin: 0; }
.logistics-mini { padding-left: 4px; margin-top: 8px; }

/* 退款信息 */
.refund-info { }
.refund-status-tag { margin-bottom: 16px; }
.refund-details { }
.refund-details .detail-item {
  display: flex; gap: 12px; padding: 8px 0; font-size: 14px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}
.refund-details .detail-item:last-child { border-bottom: none; }
.refund-details .detail-item .label {
  color: var(--mall-text-muted); white-space: nowrap; min-width: 70px;
}
.refund-details .amount { color: #ff4757; font-weight: bold; font-size: 16px; }

/* 底部操作栏 */
.bottom-actions {
  position: sticky;
  bottom: 20px;
  left: 50%;
  transform: translateX(-50%);
  display: flex;
  justify-content: center;
  gap: 12px;
  padding: 18px 28px;
  background: rgba(30, 30, 35, 0.92);
  backdrop-filter: blur(12px);
  border: 1px solid var(--mall-border);
  border-radius: 60px;
  max-width: fit-content;
  margin: 30px auto 40px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  z-index: 10;
}

/* 物流弹窗 */
.logistics-dialog-content .logistics-header {
  display: flex; flex-direction: column; gap: 6px;
  margin-bottom: 20px; padding-bottom: 14px;
  border-bottom: 1px solid var(--mall-border);
  font-size: 14px; color: var(--mall-text-secondary);
}
.logistics-dialog-content .logistics-header strong {
  color: #eee;
}

@media (max-width: 768px) {
  .order-center, .order-detail { padding: 12px; }
  .stats-cards { grid-template-columns: repeat(2, 1fr); }
  .status-card { flex-direction: column; gap: 20px; text-align: center; }
  .status-main { justify-content: center; }
  .info-grid { grid-template-columns: 1fr; }
  .bottom-actions { width: calc(100% - 40px); flex-wrap: wrap; }
  .bottom-actions .el-button { flex: 1 1 auto; }
}
</style>
