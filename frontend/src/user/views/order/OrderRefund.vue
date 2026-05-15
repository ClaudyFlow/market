<template>
  <div class="order-refund" v-loading="loading">
    <!-- 返回 -->
    <div class="refund-header">
      <el-button text @click="$router.push(`/user/orders/${orderId}`)">
        <el-icon><ArrowLeft /></el-icon> 返回订单详情
      </el-button>
      <h2>退款/售后详情</h2>
    </div>

    <div v-if="!loading && !order" class="empty-state">
      <el-empty description="订单数据加载失败">
        <el-button type="primary" @click="$router.push('/user/orders')">返回订单列表</el-button>
      </el-empty>
    </div>

    <template v-if="order && refundInfo">
      <!-- 退款进度 -->
      <section class="refund-section progress-section">
        <div class="section-title">退款进度</div>
        <el-steps :active="refundStep" align-center finish-status="success" process-color="#409eff">
          <el-step title="提交申请" :description="refundInfo.applyTime" />
          <el-step title="商家审核" :description="refundInfo.handleTime || (refundStep >= 1 ? '处理中' : '')" />
          <el-step title="退款完成" :description="refundStatus === 'success' ? refundInfo.handleTime : ''" />
        </el-steps>
        <div class="current-status">
          <el-tag :type="refundTagType(refundInfo.status)" effect="dark" size="large" round>
            {{ refundStatusText(refundInfo.status) }}
          </el-tag>
        </div>
      </section>

      <!-- 退款信息 -->
      <section class="refund-section info-section">
        <div class="section-title">退款详情</div>
        <div class="info-grid">
          <div class="info-item">
            <span class="label">退款金额</span>
            <span class="value amount">¥{{ refundInfo.amount.toFixed(2) }}</span>
          </div>
          <div class="info-item">
            <span class="label">退款原因</span>
            <span class="value">{{ refundInfo.reason }}</span>
          </div>
          <div class="info-item">
            <span class="label">申请时间</span>
            <span class="value">{{ formatTime(refundInfo.applyTime) }}</span>
          </div>
          <div v-if="refundInfo.handleTime" class="info-item">
            <span class="label">处理时间</span>
            <span class="value">{{ formatTime(refundInfo.handleTime) }}</span>
          </div>
          <div v-if="refundInfo.remark" class="info-item full-width">
            <span class="label">商家备注</span>
            <span class="value remark-text">{{ refundInfo.remark }}</span>
          </div>
          <div v-if="refundInfo.images?.length" class="info-item full-width">
            <span class="label">凭证图片</span>
            <div class="image-list">
              <el-image
                v-for="(img, idx) in refundInfo.images"
                :key="idx"
                :src="img"
                fit="cover"
                class="refund-image"
                :preview-src-list="refundInfo.images as string[]"
                :initial-index="idx"
              />
            </div>
          </div>
        </div>
      </section>

      <!-- 涉及商品 -->
      <section class="refund-section items-section">
        <div class="section-title">涉及商品</div>
        <div v-for="item in order.items" :key="item.id" class="item-row">
          <el-image :src="item.productImage" fit="cover" class="item-img" />
          <div class="item-info">
            <span class="item-name ellipsis-1">{{ item.productName }}</span>
            <span class="item-specs" v-if="item.specs">{{ item.specs }}</span>
          </div>
          <span class="item-qty">x{{ item.quantity }}</span>
          <span class="item-price">¥{{ item.price?.toFixed(2) }}</span>
        </div>
      </section>

      <!-- 操作按钮 -->
      <div class="action-area">
        <template v-if="refundInfo.status === 'pending'">
          <el-button type="danger" round @click="cancelRefund">取消退款</el-button>
        </template>
        <template v-if="refundInfo.status === 'rejected'">
          <el-button type="warning" round @click="reapplyRefund">重新申请</el-button>
        </template>
        <template v-if="['approved', 'success'].includes(refundInfo.status)">
          <el-button round @click="$router.push(`/user/orders/${orderId}`)">返回订单</el-button>
        </template>
        <el-button round @click="$router.push('/user/orders')">返回订单列表</el-button>
      </div>
    </template>

    <!-- 重新申请弹窗（复用 OrderCenter 的退款表单逻辑） -->
    <el-dialog v-model="showReapplyDialog" title="重新申请退款" width="520px" destroy-on-close>
      <el-form :model="reapplyForm" label-position="top">
        <el-form-item label="退款原因">
          <el-input v-model="reapplyForm.reason" placeholder="请说明退款原因" />
        </el-form-item>
        <el-form-item label="详细说明">
          <el-input v-model="reapplyForm.description" type="textarea" :rows="3" placeholder="补充说明（选填）" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="showReapplyDialog = false">取消</el-button>
        <el-button type="danger" @click="doReapply">提交</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeft } from '@element-plus/icons-vue'
import * as orderApi from '@user/api/order'

const route = useRoute()
const router = useRouter()

const orderId = route.params.id as string
const loading = ref(false)
const order = ref<any>(null)
const refundInfo = ref<any>(null)

// 重新申请
const showReapplyDialog = ref(false)
const reapplyForm = reactive({
  reason: '',
  description: ''
})

// 退款步骤映射
const refundStep = computed(() => {
  const map: Record<string, number> = {
    pending: 0,
    approved: 1,
    rejected: 1,
    success: 2
  }
  return map[refundInfo.value?.status] ?? 0
})

const refundStatus = computed(() => refundInfo.value?.status)

// 加载数据
const loadData = async () => {
  loading.value = true
  try {
    const [detail, refund] = await Promise.all([
      orderApi.getOrderDetail(orderId),
      orderApi.getRefundDetail(orderId).catch(() => null)
    ])
    order.value = detail
    // 订单中可能已有 refundInfo，优先用接口数据
    refundInfo.value = refund || detail.refundInfo || null
  } catch (error: any) {
    ElMessage.error(error?.message || '加载数据失败')
  } finally {
    loading.value = false
  }
}

const refundTagType = (s: string): any => ({
  pending: 'warning', approved: '', rejected: 'danger', success: 'success'
}[s] || 'info')

const refundStatusText = (s: string) => ({
  pending: '等待商家处理',
  approved: '商家已同意，退款中',
  rejected: '商家已拒绝',
  success: '退款成功'
}[s] || s)

const formatTime = (t?: string) => {
  if (!t) return '-'
  return new Date(t).toLocaleString('zh-CN')
}

// 取消退款
const cancelRefund = async () => {
  try {
    await ElMessageBox.confirm('确定要取消该退款申请吗？', '确认取消', { type: 'warning' })
    ElMessage.info('取消退款功能需要后端支持')
  } catch {}
}

// 重新申请
const reapplyRefund = () => {
  reapplyForm.reason = refundInfo.value?.reason || ''
  reapplyForm.description = ''
  showReapplyDialog.value = true
}

const doReapply = async () => {
  if (!reapplyForm.reason.trim()) {
    ElMessage.warning('请输入退款原因')
    return
  }
  try {
    await orderApi.refundOrder(
      orderId,
      `${reapplyForm.reason}${reapplyForm.description ? ' - ' + reapplyForm.description : ''}`
    )
    ElMessage.success('重新申请已提交')
    showReapplyDialog.value = false
    loadData()
  } catch (error: any) {
    ElMessage.error(error?.message || '申请失败')
  }
}

onMounted(loadData)
</script>

<style scoped>
.order-refund {
  max-width: 860px;
  margin: 0 auto;
  padding: 20px;
}

.refund-header {
  display: flex; align-items: center; gap: 16px; margin-bottom: 20px;
}
.refund-header h2 {
  font-size: 22px; font-weight: bold; color: #fff; margin: 0;
}

.empty-state { text-align: center; padding: 80px 20px; background: var(--mall-bg-card); border-radius: 12px; }

.refund-section {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px; font-weight: bold; color: #fff;
  margin-bottom: 20px;
}

/* 进度 */
.progress-section .section-title { margin-bottom: 24px; }
.current-status {
  display: flex; justify-content: center; margin-top: 20px;
}

/* 信息网格 */
.info-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 14px;
}
.info-item { display: flex; flex-direction: column; gap: 4px; }
.info-item.full-width { grid-column: 1 / -1; }
.info-item .label { font-size: 12px; color: var(--mall-text-muted); }
.info-item .value { font-size: 14px; color: #ddd; }
.info-item .amount { font-size: 22px; font-weight: bold; color: #ff4757; }
.remark-text {
  background: rgba(255,255,255,0.04); padding: 10px 14px; border-radius: 8px;
  line-height: 1.6;
}

.image-list {
  display: flex; gap: 8px; flex-wrap: wrap; margin-top: 6px;
}
.refund-image {
  width: 80px; height: 80px; border-radius: 8px; cursor: pointer;
}

/* 商品列表 */
.item-row {
  display: flex; align-items: center; gap: 12px;
  padding: 12px 0; border-bottom: 1px solid rgba(255,255,255,0.04);
}
.item-row:last-child { border-bottom: none; }
.item-img { width: 64px; height: 64px; border-radius: 8px; flex-shrink: 0; }
.item-info { flex: 1; min-width: 0; display: flex; flex-direction: column; gap: 2px; }
.item-name { font-size: 14px; font-weight: 500; color: #eee; }
.ellipsis-1 { overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
.item-specs { font-size: 12px; color: var(--mall-text-muted); }
.item-qty { color: var(--mall-text-muted); font-size: 13px; min-width: 40px; }
.item-price { font-size: 15px; font-weight: bold; color: #ff4757; }

/* 操作区 */
.action-area {
  display: flex; justify-content: center; gap: 16px; padding: 30px 0 20px;
}
</style>
