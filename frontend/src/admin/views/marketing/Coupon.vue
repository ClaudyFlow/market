<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Ticket /></el-icon>
        优惠券管理
      </h1>
    </header>

    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><Tickets /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ couponStats.total }}</div>
              <div class="stat-label">优惠券总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ couponStats.active }}</div>
              <div class="stat-label">活动中</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ couponStats.expired }}</div>
              <div class="stat-label">已过期</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><SoldOut /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ couponStats.usedUp }}</div>
              <div class="stat-label">已用完</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="优惠券名称">
          <el-input v-model="filterForm.name" placeholder="请输入优惠券名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="活动中" value="active" />
            <el-option label="已过期" value="expired" />
            <el-option label="已用完" value="usedUp" />
            <el-option label="未激活" value="inactive" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchCoupons">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
          <el-button type="success" @click="openCreateDialog">
            <el-icon><Plus /></el-icon>
            创建优惠券
          </el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-section">
      <el-table :data="couponList" class="sci-table" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="150">
          <template #default="{ row }">
            <span class="coupon-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="row.type === 'PERCENT' ? 'success' : 'primary'" size="small">
              {{ row.type === 'PERCENT' ? '折扣券' : '满减券' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="discountValue" label="优惠内容" width="120">
          <template #default="{ row }">
            <span class="discount-text">
              {{ row.type === 'PERCENT' ? row.discountValue + '%' : '¥' + row.discountValue }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发放数量" width="100" />
        <el-table-column prop="remainCount" label="剩余数量" width="100">
          <template #default="{ row }">
            <span :class="row.remainCount < 10 ? 'low-stock' : ''">{{ row.remainCount }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="usedCount" label="已使用" width="100" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="validTo" label="有效期至" width="120" />
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="viewCoupon(row)">详情</el-button>
            <el-button type="warning" text size="small" @click="editCoupon(row)">编辑</el-button>
            <el-button type="danger" text size="small" @click="deleteCoupon(row)">删除</el-button>
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
          @size-change="loadCouponList"
          @current-change="loadCouponList"
        />
      </div>
    </section>

    <el-dialog v-model="dialog.visible" :title="dialog.title" width="600px">
      <el-form :model="couponForm" label-width="100px">
        <el-form-item label="优惠券名称" required>
          <el-input v-model="couponForm.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠类型" required>
          <el-radio-group v-model="couponForm.type">
            <el-radio label="PERCENT">折扣券</el-radio>
            <el-radio label="FIXED">满减券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优惠值" required>
          <el-input-number v-model="couponForm.discountValue" :min="1" :precision="couponForm.type === 'PERCENT' ? 0 : 2" />
          <span v-if="couponForm.type === 'PERCENT'">%</span>
          <span v-else>元</span>
        </el-form-item>
        <el-form-item label="最低消费">
          <el-input-number v-model="couponForm.minPurchase" :min="0" :precision="2" /> 元
        </el-form-item>
        <el-form-item label="发放数量">
          <el-input-number v-model="couponForm.totalCount" :min="1" />
        </el-form-item>
        <el-form-item label="有效期至" required>
          <el-date-picker
            v-model="couponForm.validTo"
            type="date"
            placeholder="选择日期"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="使用范围">
          <el-select v-model="couponForm.scope" style="width: 100%">
            <el-option label="全场通用" value="ALL" />
            <el-option label="指定分类" value="CATEGORY" />
            <el-option label="指定商品" value="PRODUCT" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="couponForm.status">
            <el-radio label="ACTIVE">激活</el-radio>
            <el-radio label="INACTIVE">未激活</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveCoupon">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { Ticket, Tickets, CircleCheck, Clock, Plus, SoldOut } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Coupon {
  id: number
  name: string
  type: string
  discountValue: number
  minPurchase?: number
  maxDiscount?: number
  totalCount: number
  usedCount: number
  remainCount: number
  status: string
  validTo: string
  scope: string
}

interface FilterForm {
  name: string
  status: string
}

interface CouponStats {
  total: number
  active: number
  expired: number
  usedUp: number
}

interface Pagination {
  currentPage: number
  pageSize: number
  total: number
}

interface Dialog {
  visible: boolean
  title: string
  isEdit: boolean
}

interface CouponForm {
  name: string
  type: string
  discountValue: number
  minPurchase: number
  totalCount: number
  validTo: string
  scope: string
  status: string
}

const loading = ref(false)
const couponList = ref<Coupon[]>([])
const filterForm = reactive<FilterForm>({
  name: '',
  status: ''
})

const couponStats = ref<CouponStats>({
  total: 156,
  active: 42,
  expired: 89,
  usedUp: 25
})

const pagination = reactive<Pagination>({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const dialog = reactive<Dialog>({
  visible: false,
  title: '创建优惠券',
  isEdit: false
})

const couponForm = reactive<CouponForm>({
  name: '',
  type: 'FIXED',
  discountValue: 10,
  minPurchase: 100,
  totalCount: 100,
  validTo: '',
  scope: 'ALL',
  status: 'ACTIVE'
})

const mockCouponData: Coupon[] = [
  { id: 3001, name: '新人专享券', type: 'FIXED', discountValue: 20, minPurchase: 100, totalCount: 1000, usedCount: 756, remainCount: 244, status: 'active', validTo: '2026-06-30', scope: 'ALL' },
  { id: 3002, name: '满减折扣券', type: 'PERCENT', discountValue: 15, minPurchase: 200, totalCount: 500, usedCount: 320, remainCount: 180, status: 'active', validTo: '2026-07-15', scope: 'CATEGORY' },
  { id: 3003, name: '限时特惠券', type: 'FIXED', discountValue: 50, minPurchase: 300, totalCount: 200, usedCount: 200, remainCount: 0, status: 'usedUp', validTo: '2026-05-20', scope: 'ALL' },
  { id: 3004, name: '生日专属券', type: 'PERCENT', discountValue: 10, minPurchase: 0, totalCount: 300, usedCount: 156, remainCount: 144, status: 'active', validTo: '2026-08-01', scope: 'ALL' },
  { id: 3005, name: '会员专享券', type: 'FIXED', discountValue: 30, minPurchase: 150, totalCount: 400, usedCount: 400, remainCount: 0, status: 'expired', validTo: '2026-04-30', scope: 'ALL' }
]

const loadCouponList = () => {
  loading.value = true
  setTimeout(() => {
    let data = mockCouponData
    if (filterForm.status) {
      data = data.filter(c => c.status === filterForm.status)
    }
    couponList.value = data
    pagination.total = data.length
    loading.value = false
  }, 500)
}

const searchCoupons = () => {
  pagination.currentPage = 1
  loadCouponList()
}

const resetFilter = () => {
  filterForm.name = ''
  filterForm.status = ''
  loadCouponList()
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { active: 'success', expired: 'warning', usedUp: 'info', inactive: '' }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { active: '活动中', expired: '已过期', usedUp: '已用完', inactive: '未激活' }
  return map[status] || status
}

const openCreateDialog = () => {
  dialog.visible = true
  dialog.title = '创建优惠券'
  dialog.isEdit = false
  couponForm.name = ''
  couponForm.type = 'FIXED'
  couponForm.discountValue = 10
  couponForm.minPurchase = 100
  couponForm.totalCount = 100
  couponForm.validTo = ''
  couponForm.scope = 'ALL'
  couponForm.status = 'ACTIVE'
}

const viewCoupon = (coupon: Coupon) => {
  ElMessage.info(`查看优惠券：${coupon.name}`)
}

const editCoupon = (coupon: Coupon) => {
  dialog.visible = true
  dialog.title = '编辑优惠券'
  dialog.isEdit = true
  couponForm.name = coupon.name
  couponForm.type = coupon.type
  couponForm.discountValue = coupon.discountValue
  couponForm.minPurchase = coupon.minPurchase || 0
  couponForm.totalCount = coupon.totalCount
  couponForm.validTo = coupon.validTo
  couponForm.scope = coupon.scope
  couponForm.status = coupon.status === 'active' ? 'ACTIVE' : 'INACTIVE'
}

const deleteCoupon = (coupon: Coupon) => {
  ElMessageBox.confirm(`确定要删除优惠券"${coupon.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = couponList.value.findIndex(c => c.id === coupon.id)
    if (index !== -1) {
      couponList.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

const saveCoupon = () => {
  if (!couponForm.name || !couponForm.validTo) {
    ElMessage.warning('请填写必填项')
    return
  }
  ElMessage.success(dialog.isEdit ? '更新成功' : '创建成功')
  dialog.visible = false
  loadCouponList()
}

onMounted(() => {
  loadCouponList()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.page-header {
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.9), rgba(26, 31, 58, 0.7));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
  margin-bottom: 20px;
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: bold;
  color: #fff;
  margin: 0;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 26px;
}

.stats-cards { margin-bottom: 20px; }

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
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

.stat-card:hover { transform: translateY(-3px); border-color: var(--glow-color); }
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
}

.stat-card.primary .stat-icon { background: linear-gradient(135deg, #00d4ff, #00a8cc); }
.stat-card.success .stat-icon { background: linear-gradient(135deg, #00ff88, #00cc6a); }
.stat-card.warning .stat-icon { background: linear-gradient(135deg, #ffaa00, #ff8800); }
.stat-card.danger .stat-icon { background: linear-gradient(135deg, #ff6666, #ff4444); }

.stat-info { flex: 1; }
.stat-value { font-size: 24px; font-weight: bold; color: #fff; }
.stat-label { font-size: 13px; color: #888; }

.search-bar {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.search-bar :deep(.el-form-item__label) { color: #ccc; }
.search-bar :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
}
.search-bar :deep(.el-input__inner) { color: #fff; }
.search-bar :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
}

.table-section {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.coupon-name { font-weight: 500; color: #fff; }
.discount-text { color: var(--mall-primary); font-weight: bold; }
.low-stock { color: #ff6666; }

.pagination-bar { display: flex; justify-content: flex-end; margin-top: 20px; }

.sci-table :deep(.el-table__header th) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--mall-primary);
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}
.sci-table :deep(.el-table__body td) {
  background: transparent;
  color: #aaa;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
}
.sci-table :deep(.el-table__row:hover) { background: rgba(0, 212, 255, 0.05); }
</style>