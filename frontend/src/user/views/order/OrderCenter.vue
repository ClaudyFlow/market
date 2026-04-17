<template>
  <div class="order-center">
    <div class="page-header">
      <h1><el-icon><ShoppingCart /></el-icon> 我的订单</h1>
      <div class="header-actions">
        <el-button text @click="goToShop">
          <el-icon><Shop /></el-icon> 去逛逛
        </el-button>
      </div>
    </div>

    <!-- 订单统计卡片 -->
    <div class="stats-cards" v-if="stats && (stats.unpaid || stats.unshipped || stats.unreceived || stats.reviewed)">
      <div class="stat-item" @click="activeStatus = 'PENDING'; loadOrders()" :class="{ active: activeStatus === 'PENDING' }">
        <span class="stat-value">{{ stats.unpaid }}</span>
        <span class="stat-label">待付款</span>
      </div>
      <div class="stat-item" @click="activeStatus = 'PAID'; loadOrders()" :class="{ active: activeStatus === 'PAID' }">
        <span class="stat-value">{{ stats.unshipped }}</span>
        <span class="stat-label">待发货</span>
      </div>
      <div class="stat-item" @click="activeStatus = 'SHIPPED'; loadOrders()" :class="{ active: activeStatus === 'SHIPPED' }">
        <span class="stat-value">{{ stats.unreceived }}</span>
        <span class="stat-label">待收货</span>
      </div>
      <div class="stat-item" @click="activeStatus = 'COMPLETED'; loadOrders()" :class="{ active: activeStatus === 'COMPLETED' }">
        <span class="stat-value">{{ stats.reviewed }}</span>
        <span class="stat-label">待评价</span>
      </div>
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
          <div class="header-left">
            <span class="order-no">订单号：{{ order.orderNo }}</span>
            <span v-if="order.shopName" class="shop-name" @click="goToShopDetail(order.shopId)">
              <el-icon><StoreIcon /></el-icon> {{ order.shopName }}
            </span>
          </div>
          <div class="header-right">
            <span class="order-time">{{ formatTime(order.createdAt) }}</span>
            <el-tag :type="getOrderTagType(order.status)" size="small" effect="dark" round>
              {{ getStatusText(order.status) }}
            </el-tag>
          </div>
        </div>

        <!-- 订单商品 - 点击跳转详情 -->
        <div class="order-items" @click="viewDetail(order.id)" style="cursor: pointer;">
          <div v-for="item in order.items" :key="item.id" class="order-item">
            <div class="item-image-wrapper">
              <el-image :src="item.productImage" fit="cover" class="item-image">
                <template #error>
                  <div class="image-error"><el-icon><PictureFilled /></el-icon></div>
                </template>
              </el-image>
              <span v-if="item.quantity > 1" class="quantity-badge">x{{ item.quantity }}</span>
            </div>
            <div class="item-info">
              <div class="item-name ellipsis-2">{{ item.productName }}</div>
              <div v-if="item.specs" class="item-specs">{{ item.specs }}</div>
              <div class="item-meta">
                <span class="item-price">¥{{ item.price?.toFixed(2) }}</span>
                <span v-if="item.reviewStatus === 'reviewed'" class="review-tag">
                  <el-icon><ChatDotRound /></el-icon> 已评
                </span>
              </div>
            </div>
            <div class="item-subtotal">
              <span class="subtotal-text">¥{{ item.subtotal?.toFixed(2) || (item.price * item.quantity).toFixed(2) }}</span>
            </div>
          </div>
        </div>

        <!-- 订单底部 -->
        <div class="order-footer">
          <div class="order-total">
            <span v-if="order.itemCount > 1" class="item-count">共 {{ order.itemCount }} 件商品</span>
            <span>实付：</span>
            <span class="total-amount">¥{{ order.payAmount?.toFixed(2) || order.totalAmount?.toFixed(2) }}</span>
            <span v-if="order.freightAmount > 0" class="freight-info">(含运费¥{{ order.freightAmount.toFixed(2) }})</span>
          </div>
          <div class="order-actions">
            <!-- 待付款 -->
            <template v-if="order.status === 'PENDING'">
              <el-button size="small" plain @click.stop="cancelOrder(order.id)">取消订单</el-button>
              <el-button size="small" type="warning" @click.stop="payOrder(order.id)">立即付款</el-button>
            </template>
            <!-- 已支付 / 待发货 / 待收货 -->
            <template v-else-if="['PAID', 'SHIPPED'].includes(order.status)">
              <el-button size="small" plain @click.stop="showLogistics(order)">查看物流</el-button>
              <el-button size="small" plain @click.stop="handleRefund(order)" v-if="order.status === 'PAID'">
                申请退款
              </el-button>
              <el-button size="small" type="primary" @click.stop="confirmReceive(order.id)" v-if="order.status === 'SHIPPED'">
                确认收货
              </el-button>
              <el-button size="small" plain @click.stop="handleShare(order)">
                <el-icon><Share /></el-icon> 分享
              </el-button>
            </template>
            <!-- 已完成 -->
            <template v-else-if="order.status === 'COMPLETED'">
              <el-button size="small" plain @click.stop="viewDetail(order.id)">订单详情</el-button>
              <el-button size="small" plain @click.stop="handleRepurchase(order)">
                <el-icon><RefreshRight /></el-icon> 再买一单
              </el-button>
              <el-button size="small" plain @click.stop="handleFavorite(order.items)">
                <el-icon><Star /></el-icon> 收藏
              </el-button>
              <el-button size="small" plain type="warning" @click.stop="handleRefund(order)">
                申请售后
              </el-button>
              <el-button size="small" type="primary" @click.stop="reviewOrder(order.id)">
                评价
              </el-button>
              <el-button size="small" plain @click.stop="handleShare(order)">
                <el-icon><Share /></el-icon> 分享
              </el-button>
            </template>
            <!-- 退款中 -->
            <template v-else-if="order.status === 'REFUNDING'">
              <el-button size="small" plain @click.stop="viewRefundDetail(order.id)">退款详情</el-button>
              <el-button size="small" plain @click.stop="viewDetail(order.id)">订单详情</el-button>
            </template>
            <!-- 已取消 / 已退款 -->
            <template v-else-if="['CANCELLED', 'REFUNDED', 'CLOSED'].includes(order.status)">
              <el-button size="small" plain @click.stop="handleRepurchase(order)">
                <el-icon><RefreshRight /></el-icon> 再买一单
              </el-button>
              <el-button size="small" plain type="danger" @click.stop="deleteOrder(order.id)">删除订单</el-button>
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
    <el-dialog v-model="showLogisticsDialog" title="物流信息" width="600px" destroy-on-close>
      <div v-if="currentLogistics" class="logistics-content">
        <div class="logistics-header">
          <div class="tracking-no"><el-icon><Van /></el-icon> 运单号：{{ currentLogistics.trackingNo || '-' }}</div>
          <div class="carrier"><el-icon><OfficeBuilding /></el-icon> 物流公司：{{ currentLogistics.company || currentLogistics.carrier }}</div>
          <div v-if="currentLogistics.estimatedDelivery" class="estimated-delivery">
            <el-icon><Clock /></el-icon> 预计送达：{{ currentLogistics.estimatedDelivery }}
          </div>
        </div>
        <el-timeline class="logistics-timeline">
          <el-timeline-item
            v-for="(trace, index) in currentLogistics.traces"
            :key="index"
            :timestamp="trace.time"
            :type="index === 0 ? 'primary' : undefined"
            :hollow="index !== 0"
            placement="top"
          >
            <div class="trace-content">
              <div class="trace-desc">{{ trace.description }}</div>
              <div v-if="trace.location" class="trace-location">{{ trace.location }}</div>
            </div>
          </el-timeline-item>
        </el-timeline>
        <el-empty v-if="!currentLogistics.traces?.length" description="暂无物流轨迹" :image-size="80" />
      </div>
      <el-empty v-else description="暂无物流信息" />
    </el-dialog>

    <!-- 退款/售后申请弹窗 -->
    <el-dialog
      v-model="showRefundDialog"
      :title="refundForm.orderStatus === 'COMPLETED' ? '申请售后服务' : '申请退款'"
      width="520px"
      destroy-on-close
      @close="resetRefundForm"
    >
      <el-form :model="refundForm" :rules="refundRules" ref="refundFormRef" label-width="90px" label-position="top">
        <el-form-item label="退款类型" prop="type">
          <el-radio-group v-model="refundForm.type">
            <el-radio value="only_refund">仅退款（未发货）</el-radio>
            <el-radio value="refund_return">退货退款</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="退款原因" prop="reason">
          <el-select v-model="refundForm.reason" placeholder="请选择退款原因" style="width: 100%;">
            <el-option label="不想要了/拍错了" value="不想要了" />
            <el-option label="商品缺货" value="商品缺货" />
            <el-option label="商品质量问题" value="商品质量问题" />
            <el-option label="商品描述不符" value="商品描述不符" />
            <el-option label="商品破损/污渍" value="商品破损" />
            <el-option label="发错货/少发" value="发错货" />
            <el-option label="未按时发货" value="未按时发货" />
            <el-option label="其他原因" value="其他" />
          </el-select>
        </el-form-item>
        <el-form-item label="退款金额" prop="amount">
          <el-input-number v-model="refundForm.amount" :min="0.01" :max="refundMaxAmount" :precision="2" style="width: 100%;" />
          <div class="form-tip">最大可退金额 ¥{{ refundMaxAmount.toFixed(2) }}</div>
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input
            v-model="refundForm.description"
            type="textarea"
            :rows="3"
            placeholder="请详细说明退款原因（选填）"
            maxlength="500"
            show-word-limit
          />
        </el-form-item>
        <el-form-item label="上传凭证">
          <el-upload
            v-model:file-list="refundForm.imageList"
            action="#"
            list-type="picture-card"
            :auto-upload="false"
            :limit="5"
            accept="image/*"
            :on-exceed="() => ElMessage.warning('最多上传5张图片')"
          >
            <el-icon><Plus /></el-icon>
          </el-upload>
          <div class="form-tip">支持 jpg/png 格式，最多5张，每张不超过5MB</div>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showRefundDialog = false">取消</el-button>
        <el-button type="danger" @click="submitRefund" :loading="submittingRefund">提交申请</el-button>
      </template>
    </el-dialog>

    <!-- 分享弹窗 -->
    <el-dialog v-model="showShareDialog" title="分享订单" width="440px" destroy-on-close>
      <div class="share-content">
        <div class="share-preview">
          <div class="share-order-card">
            <div class="share-header">
              <el-icon><ShoppingCart /></el-icon>
              <span>我的订单</span>
            </div>
            <div v-for="item in shareOrder.items?.slice(0, 3)" :key="item.id" class="share-item">
              <el-image :src="item.productImage" fit="cover" class="share-item-img" />
              <span class="share-item-name">{{ item.productName }}</span>
            </div>
            <div v-if="shareOrder.items?.length > 3" class="share-more">
              等 {{ shareOrder.items.length }} 件商品
            </div>
            <div class="share-footer">
              <span>¥{{ shareOrder.payAmount?.toFixed(2) || shareOrder.totalAmount?.toFixed(2) }}</span>
            </div>
          </div>
        </div>
        <div class="share-methods">
          <div class="share-method" v-for="method in shareMethods" :key="method.name" @click="doShare(method.type)">
            <div class="method-icon" :style="{ background: method.color }">
              <el-icon :size="24"><component :is="method.icon" /></el-icon>
            </div>
            <span>{{ method.name }}</span>
          </div>
        </div>
        <div class="share-link-area">
          <el-input v-model="shareLink" readonly placeholder="生成分享链接...">
            <template #append>
              <el-button @click="copyShareLink">复制链接</el-button>
            </template>
          </el-input>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import type { Component } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type UploadFile } from 'element-plus'
import {
  ShoppingCart, Shop, Shop as StoreIcon, PictureFilled, ChatDotRound,
  Share, RefreshRight, Star, Van, OfficeBuilding, Clock,
  Plus, Link, ChatRound, Promotion
} from '@element-plus/icons-vue'
import * as orderApi from '@user/api/order'
import * as favoriteApi from '@user/api/favorite'
import { useLocalCartStore } from '@user/stores/cart-local'
import type { Order, OrderStats, OrderLogistics, RefundInfo } from '@user/types/order'

const router = useRouter()
const localCart = useLocalCartStore()

const loading = ref(false)
const orders = ref<Order[]>([])
const total = ref(0)
const currentPage = ref(1)
const pageSize = ref(10)
const activeStatus = ref('all')
const stats = ref<OrderStats | null>(null)

// 物流弹窗
const showLogisticsDialog = ref(false)
const currentLogistics = ref<OrderLogistics | null>(null)

// 退款/售后弹窗
const showRefundDialog = ref(false)
const submittingRefund = ref(false)
const refundFormRef = ref<FormInstance>()
const refundMaxAmount = computed(() => currentRefundOrder.value?.payAmount || currentRefundOrder.value?.totalAmount || 0)
const currentRefundOrder = ref<Order | null>(null)

const refundForm = reactive({
  orderId: null as any,
  orderStatus: '' as string,
  type: 'only_refund' as 'only_refund' | 'refund_return',
  reason: '',
  amount: 0,
  description: '',
  imageList: [] as UploadFile[]
})

const refundRules = {
  type: [{ required: true, message: '请选择退款类型', trigger: 'change' }],
  reason: [{ required: true, message: '请选择退款原因', trigger: 'change' }],
  amount: [{ required: true, message: '请输入退款金额', trigger: 'blur' }]
}

// 分享弹窗
const showShareDialog = ref(false)
const shareOrder = ref<Order | null>(null)
const shareLink = ref('')

interface ShareMethod {
  name: string
  icon: Component
  color: string
  type: string
}

const shareMethods: ShareMethod[] = [
  { name: '微信', icon: ChatRound, color: '#07C160', type: 'wechat' },
  { name: '朋友圈', icon: Promotion, color: '#07C160', type: 'moments' },
  { name: 'QQ', icon: ChatRound, color: '#12B7F5', type: 'qq' },
  { name: '复制链接', icon: Link, color: '#666', type: 'link' }
]

// 加载订单列表
const loadOrders = async () => {
  loading.value = true
  try {
    const params: any = {
      current: currentPage.value,
      size: pageSize.value,
    }
    if (activeStatus.value !== 'all') {
      params.status = activeStatus.value
    }
    const result = await orderApi.getOrderList(params)
    orders.value = result.records || []
    total.value = result.total || 0
  } catch (error) {
    console.error('加载订单失败:', error)
    ElMessage.error('加载订单失败')
  } finally {
    loading.value = false
  }
}

// 加载订单统计
const loadStats = async () => {
  try {
    stats.value = await orderApi.getOrderStats()
  } catch (error) {
    // 静默处理，统计信息非关键功能
  }
}

// 取消订单
const cancelOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm('确定要取消该订单吗？取消后订单将无法恢复。', '确认取消', {
      type: 'warning',
      confirmButtonText: '确定取消',
      cancelButtonText: '再想想'
    })
    await orderApi.cancelOrder(orderId, '用户主动取消')
    ElMessage.success('订单已取消')
    loadOrders()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '取消订单失败')
    }
  }
}

// 支付订单
const payOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm('即将跳转到支付页面完成付款', '确认支付', {
      type: 'info',
      confirmButtonText: '去支付',
      cancelButtonText: '取消'
    })
    const result = await orderApi.payOrder(orderId, 'alipay')
    if (result?.payUrl) {
      window.open(result.payUrl, '_blank')
      ElMessage.success('正在跳转支付页面...')
    } else {
      ElMessage.info('支付接口暂未配置')
    }
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('支付请求失败')
    }
  }
}

// 确认收货
const confirmReceive = async (orderId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定已收到商品吗？确认后订单将变为已完成状态。',
      '确认收货',
      { type: 'warning', confirmButtonText: '确认收货' }
    )
    await orderApi.confirmReceive(orderId)
    ElMessage.success('确认收货成功，感谢您的购买！')
    loadOrders()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '操作失败')
    }
  }
}

// 查看订单详情
const viewDetail = (orderId: number | string) => {
  router.push({ path: `/user/orders/${orderId}` })
}

// 评价订单
const reviewOrder = (orderId: number | string) => {
  router.push({ path: `/user/orders/${orderId}/review` })
}

// 查看物流
const showLogistics = async (order: Order) => {
  try {
    const logistics = await orderApi.getOrderLogistics(order.id)
    currentLogistics.value = logistics
    showLogisticsDialog.value = true
  } catch (error) {
    ElMessage.warning('暂无物流信息')
  }
}

// 查看退款详情
const viewRefundDetail = (orderId: number | string) => {
  router.push({ path: `/user/orders/${orderId}/refund` })
}

// 删除订单
const deleteOrder = async (orderId: number) => {
  try {
    await ElMessageBox.confirm(
      '确定要删除该订单吗？删除后将无法查看订单记录。',
      '删除订单',
      { type: 'warning', confirmButtonText: '确定删除', confirmButtonClass: 'el-button--danger' }
    )
    await orderApi.deleteOrder(orderId)
    ElMessage.success('订单已删除')
    loadOrders()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error?.message || '删除失败')
    }
  }
}

// ========== 新增功能 ==========

// 再买一单（重新购买）
const handleRepurchase = async (order: Order) => {
  try {
    // 将订单商品加入本地购物车
    order.items?.forEach((item: any) => {
      for (let i = 0; i < item.quantity; i++) {
        localCart.addToCart({
          id: item.productId || item.id,
          name: item.productName,
          price: Number(item.price),
          originalPrice: Number(item.price),
          image: item.productImage,
          shopId: order.shopId,
          shopName: order.shopName,
          spec: item.specs || ''
        })
      }
    })
    ElMessage.success(`已将 ${order.itemCount || order.items?.length} 件商品加入购物车`)
    // 延迟跳转购物车
    setTimeout(() => router.push('/cart'), 500)
  } catch (error: any) {
    ElMessage.error(error?.message || '加入购物车失败')
  }
}

// 申请退款 / 售后
const handleRefund = (order: Order) => {
  currentRefundOrder.value = order
  refundForm.orderId = order.id
  refundForm.orderStatus = order.status
  refundForm.type = order.status === 'COMPLETED' ? 'refund_return' : 'only_refund'
  refundForm.amount = order.payAmount || order.totalAmount
  refundForm.reason = ''
  refundForm.description = ''
  refundForm.imageList = []
  showRefundDialog.value = true
}

// 提交退款申请
const submitRefund = async () => {
  if (!refundFormRef.value) return
  await refundFormRef.value.validate(async (valid) => {
    if (!valid) return
    submittingRefund.value = true
    try {
      const images = refundForm.imageList
        .filter(f => f.raw)
        .map(f => URL.createObjectURL(f.raw))
      await orderApi.refundOrder(refundForm.orderId, `${refundForm.reason}${refundForm.description ? ' - ' + refundForm.description : ''}`, images)
      ElMessage.success('退款申请提交成功，请耐心等待商家处理')
      showRefundDialog.value = false
      loadOrders()
      loadStats()
    } catch (error: any) {
      ElMessage.error(error?.message || '提交申请失败')
    } finally {
      submittingRefund.value = false
    }
  })
}

const resetRefundForm = () => {
  refundForm.type = 'only_refund'
  refundForm.reason = ''
  refundForm.amount = 0
  refundForm.description = ''
  refundForm.imageList = []
  currentRefundOrder.value = null
}

// 收藏订单中的商品
const handleFavorite = async (items: Order['items']) => {
  let successCount = 0
  for (const item of items) {
    try {
      await favoriteApi.addFavorite({
        type: 'product',
        itemId: item.productId || item.id,
        name: item.productName,
        image: item.productImage
      })
      successCount++
    } catch {
      // 单个失败不影响其他
    }
  }
  if (successCount > 0) {
    ElMessage.success(`已收藏 ${successCount} 件商品`)
  } else {
    ElMessage.warning('收藏失败，可能已经收藏过了')
  }
}

// 分享订单
const handleShare = (order: Order) => {
  shareOrder.value = order
  shareLink.value = `${window.location.origin}/user/orders/${order.id}`
  showShareDialog.value = true
}

const doShare = (type: string) => {
  if (type === 'link') {
    copyShareLink()
  } else {
    ElMessage.info(`${type === 'wechat' ? '微信' : type === 'moments' ? '朋友圈' : 'QQ'} 分享功能开发中...`)
  }
}

const copyShareLink = () => {
  navigator.clipboard.writeText(shareLink.value).then(() => {
    ElMessage.success('链接已复制到剪贴板')
  }).catch(() => {
    // fallback
    const input = document.createElement('input')
    input.value = shareLink.value
    document.body.appendChild(input)
    input.select()
    document.execCommand('copy')
    document.body.removeChild(input)
    ElMessage.success('链接已复制到剪贴板')
  })
}

// 获取订单标签类型
const getOrderTagType = (status: string): 'success' | 'warning' | 'danger' | 'info' | '' | undefined => {
  const map: Record<string, 'success' | 'warning' | 'danger' | 'info' | ''> = {
    PENDING: 'warning',
    PAID: '',       // 默认蓝色
    SHIPPED: 'success',
    COMPLETED: 'info',
    CANCELLED: 'info',
    REFUNDING: 'warning',
    REFUNDED: 'danger',
    CLOSED: 'info'
  }
  return map[status] || ''
}

// 获取状态文本
const getStatusText = (status: string) => {
  const textMap: Record<string, string> = {
    PENDING: '待付款',
    PAID: '待发货',
    SHIPPED: '待收货',
    COMPLETED: '已完成',
    CANCELLED: '已取消',
    REFUNDING: '退款中',
    REFUNDED: '已退款',
    CLOSED: '已关闭'
  }
  return textMap[status] || status
}

// 格式化时间
const formatTime = (date?: string) => {
  if (!date) return ''
  const d = new Date(date)
  const now = new Date()
  const diff = now.getTime() - d.getTime()
  if (diff < 60000) return '刚刚'
  if (diff < 3600000) return `${Math.floor(diff / 60000)}分钟前`
  if (diff < 86400000) return `${Math.floor(diff / 3600000)}小时前`
  if (diff < 2592000000) return `${Math.floor(diff / 86400000)}天前`
  return `${d.getMonth() + 1}-${d.getDate()} ${String(d.getHours()).padStart(2, '0')}:${String(d.getMinutes()).padStart(2, '0')}`
}

// 导航到店铺
const goToShop = () => router.push('/shops')
const goToShopDetail = (shopId?: number) => {
  if (shopId) router.push(`/shop/${shopId}`)
}

onMounted(() => {
  loadOrders()
  loadStats()
})
</script>

<style scoped>
.order-center {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.header-actions .el-button {
  color: var(--mall-text-secondary);
}

/* 订单统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 12px;
  margin-bottom: 20px;
}

.stat-item {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 10px;
  padding: 16px;
  text-align: center;
  cursor: pointer;
  transition: all 0.25s ease;
}

.stat-item:hover {
  border-color: var(--mall-primary);
  transform: translateY(-2px);
}

.stat-item.active {
  border-color: var(--mall-primary);
  background: rgba(64, 158, 255, 0.08);
}

.stat-value {
  display: block;
  font-size: 24px;
  font-weight: bold;
  color: var(--mall-primary);
  line-height: 1.2;
}

.stat-label {
  display: block;
  font-size: 13px;
  color: var(--mall-text-muted);
  margin-top: 4px;
}

/* 筛选标签 */
.filter-tabs {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 0 20px;
  margin-bottom: 20px;
}

.filter-tabs :deep(.el-tabs__header) {
  margin-bottom: 0;
}

.filter-tabs :deep(.el-tabs__item) {
  color: var(--mall-text-secondary);
  height: 50px;
  line-height: 50px;
  font-size: 14px;
}

.filter-tabs :deep(.el-tabs__item.is-active) {
  color: var(--mall-primary);
  font-weight: bold;
}

/* 订单列表 */
.order-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.empty-state {
  text-align: center;
  padding: 80px 20px;
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
}

/* 订单卡片 */
.order-card {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  overflow: hidden;
  transition: border-color 0.2s, box-shadow 0.2s;
}

.order-card:hover {
  border-color: rgba(255, 255, 255, 0.15);
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.15);
}

/* 订单头部 */
.order-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.04);
  border-bottom: 1px solid var(--mall-border);
}

.header-left,
.header-right {
  display: flex;
  align-items: center;
  gap: 14px;
}

.order-no {
  font-size: 13px;
  color: var(--mall-text-secondary);
}

.shop-name {
  font-size: 13px;
  color: var(--mall-primary);
  cursor: pointer;
  transition: opacity 0.2s;
  display: flex;
  align-items: center;
  gap: 3px;
}

.shop-name:hover {
  opacity: 0.8;
}

.order-time {
  font-size: 12px;
  color: var(--mall-text-muted);
}

/* 订单商品区域 */
.order-items {
  padding: 8px 20px;
}

.order-item {
  display: flex;
  gap: 14px;
  padding: 14px 0;
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
  align-items: flex-start;
}

.order-item:last-child {
  border-bottom: none;
}

.item-image-wrapper {
  position: relative;
  flex-shrink: 0;
}

.item-image {
  width: 88px;
  height: 88px;
  border-radius: 10px;
  background: rgba(255, 255, 255, 0.06);
}

.image-error {
  width: 100%;
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--mall-text-muted);
  font-size: 24px;
  background: rgba(255, 255, 255, 0.06);
  border-radius: 10px;
}

.quantity-badge {
  position: absolute;
  bottom: -4px;
  right: -6px;
  background: rgba(255, 68, 68, 0.9);
  color: #fff;
  font-size: 10px;
  padding: 1px 5px;
  border-radius: 8px;
  font-weight: bold;
}

.item-info {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  height: 88px;
}

.item-name {
  font-size: 14px;
  font-weight: 500;
  color: #eee;
  line-height: 1.4;
}

.ellipsis-2 {
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.item-specs {
  font-size: 12px;
  color: var(--mall-text-muted);
  margin-top: 2px;
}

.item-meta {
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 8px;
}

.item-price {
  font-size: 15px;
  font-weight: bold;
  color: var(--mall-primary);
}

.review-tag {
  font-size: 11px;
  color: var(--mall-text-muted);
  display: flex;
  align-items: center;
  gap: 2px;
}

.item-subtotal {
  flex-shrink: 0;
  display: flex;
  align-items: center;
  min-width: 80px;
  justify-content: flex-end;
}

.subtotal-text {
  font-size: 15px;
  font-weight: 600;
  color: #eee;
}

/* 订单底部操作栏 */
.order-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 14px 20px;
  background: rgba(255, 255, 255, 0.03);
  border-top: 1px solid var(--mall-border);
}

.order-total {
  font-size: 13px;
  color: var(--mall-text-secondary);
  display: flex;
  align-items: baseline;
  gap: 4px;
  flex-wrap: wrap;
}

.item-count {
  color: var(--mall-text-muted);
  margin-right: 8px;
}

.total-amount {
  font-size: 18px;
  font-weight: bold;
  color: #ff4757;
}

.freight-info {
  font-size: 11px;
  color: var(--mall-text-muted);
}

.order-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.order-actions :deep(.el-button) {
  border-radius: 16px;
  font-size: 13px;
}

/* 分页 */
.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid var(--mall-border);
}

/* 物流弹窗 */
.logistics-content {
  min-height: 200px;
}

.logistics-header {
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid var(--mall-border);
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.tracking-no,
.carrier,
.estimated-delivery {
  font-size: 14px;
  color: var(--mall-text-secondary);
  display: flex;
  align-items: center;
  gap: 6px;
}

.trace-content {
  font-size: 14px;
  color: var(--mall-text-secondary);
}

.trace-desc {
  font-weight: 500;
  color: #ddd;
}

.trace-location {
  font-size: 12px;
  color: var(--mall-text-muted);
  margin-top: 2px;
}

.logistics-timeline {
  padding-left: 10px;
}

/* 退款弹窗 */
.form-tip {
  font-size: 12px;
  color: var(--mall-text-muted);
  margin-top: 4px;
}

/* 分享弹窗 */
.share-content {
  text-align: center;
}

.share-preview {
  margin-bottom: 24px;
}

.share-order-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 12px;
  padding: 20px;
  color: #fff;
  text-align: left;
  max-width: 280px;
  margin: 0 auto;
}

.share-header {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 16px;
  font-weight: bold;
  margin-bottom: 14px;
  opacity: 0.95;
}

.share-item {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 8px;
}

.share-item-img {
  width: 48px;
  height: 48px;
  border-radius: 6px;
  object-fit: cover;
}

.share-item-name {
  font-size: 13px;
  opacity: 0.9;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.share-more {
  font-size: 12px;
  opacity: 0.7;
  margin-bottom: 8px;
}

.share-footer {
  border-top: 1px solid rgba(255, 255, 255, 0.2);
  padding-top: 10px;
  text-align: right;
  font-size: 20px;
  font-weight: bold;
}

.share-methods {
  display: flex;
  justify-content: center;
  gap: 28px;
  margin-bottom: 20px;
}

.share-method {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 8px;
  cursor: pointer;
  transition: transform 0.2s;
}

.share-method:hover {
  transform: scale(1.08);
}

.method-icon {
  width: 52px;
  height: 52px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #fff;
}

.share-method span {
  font-size: 12px;
  color: var(--mall-text-secondary);
}

.share-link-area {
  padding: 0 20px;
}
</style>
