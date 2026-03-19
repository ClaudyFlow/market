<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Ticket /></el-icon>
        优惠券管理
      </h1>
      <div class="header-actions">
        <el-button type="primary" class="glow-btn" @click="openCreateDialog">
          <el-icon><Plus /></el-icon>
          创建优惠券
        </el-button>
      </div>
    </header>

    <!-- 统计卡片 -->
    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><Ticket /></el-icon></div>
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
              <div class="stat-value">{{ couponStats.used }}</div>
              <div class="stat-label">已使用</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ couponStats.unused }}</div>
              <div class="stat-label">未使用</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><CircleClose /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ couponStats.expired }}</div>
              <div class="stat-label">已过期</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <!-- 搜索筛选 -->
    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="优惠券名称">
          <el-input v-model="filterForm.name" placeholder="请输入优惠券名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="优惠券类型">
          <el-select v-model="filterForm.type" placeholder="请选择类型" clearable style="width: 120px">
            <el-option label="满减券" value="discount" />
            <el-option label="折扣券" value="percent" />
            <el-option label="运费券" value="shipping" />
          </el-select>
        </el-form-item>
        <el-form-item label="状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="未开始" value="pending" />
            <el-option label="发放中" value="active" />
            <el-option label="已过期" value="expired" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchCoupons">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <!-- 优惠券列表 -->
    <section class="table-section">
      <el-table :data="couponList" class="sci-table" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="优惠券名称" min-width="180" />
        <el-table-column prop="type" label="类型" width="100">
          <template #default="{ row }">
            <el-tag :type="getTypeTag(row.type)" size="small">{{ getTypeText(row.type) }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="value" label="面额" width="100">
          <template #default="{ row }">
            <span class="value-text">{{ getFaceValue(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="threshold" label="使用门槛" width="120">
          <template #default="{ row }">
            <span v-if="row.threshold > 0">满¥{{ row.threshold }}可用</span>
            <span v-else>无门槛</span>
          </template>
        </el-table-column>
        <el-table-column prop="validDays" label="有效期" width="120">
          <template #default="{ row }">
            <span>{{ row.validDays }}天</span>
          </template>
        </el-table-column>
        <el-table-column prop="total" label="发放数量" width="100" />
        <el-table-column prop="used" label="已领取" width="100" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="180" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="editCoupon(row)">编辑</el-button>
            <el-button
              v-if="row.status === 'active'"
              type="warning"
              text
              size="small"
              @click="toggleStatus(row)"
            >
              下架
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              text
              size="small"
              @click="toggleStatus(row)"
            >
              上架
            </el-button>
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

    <!-- 创建/编辑对话框 -->
    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="couponForm" label-width="100px">
        <el-form-item label="优惠券名称" required>
          <el-input v-model="couponForm.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠券类型" required>
          <el-select v-model="couponForm.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="满减券" value="discount" />
            <el-option label="折扣券" value="percent" />
            <el-option label="运费券" value="shipping" />
          </el-select>
        </el-form-item>
        <el-form-item label="面额/折扣" required v-if="couponForm.type !== 'shipping'">
          <el-input
            v-model="couponForm.value"
            :placeholder="couponForm.type === 'discount' ? '请输入减免金额' : '请输入折扣率 (如 8 表示 8 折)'"
            type="number"
          >
            <template #append v-if="couponForm.type === 'discount'">元</template>
            <template #append v-else>折</template>
          </el-input>
        </el-form-item>
        <el-form-item label="使用门槛">
          <el-input-number v-model="couponForm.threshold" :min="0" :precision="2" style="width: 100%">
            <template #append>元</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="发放数量" required>
          <el-input-number v-model="couponForm.total" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期" required>
          <el-input-number v-model="couponForm.validDays" :min="1" :max="365" style="width: 100%">
            <template #append>天</template>
          </el-input-number>
        </el-form-item>
        <el-form-item label="适用商品">
          <el-select v-model="couponForm.applicableProducts" placeholder="选择适用商品" multiple style="width: 100%">
            <el-option label="全场通用" value="all" />
            <el-option label="手机数码" value="digital" />
            <el-option label="电脑办公" value="office" />
            <el-option label="家用电器" value="appliance" />
          </el-select>
        </el-form-item>
        <el-form-item label="优惠券描述">
          <el-input
            v-model="couponForm.description"
            type="textarea"
            :rows="3"
            placeholder="请输入优惠券描述"
          />
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
import { ElMessage, ElMessageBox } from 'element-plus'
import { Ticket, Plus, CircleCheck, CircleClose, Clock } from '@element-plus/icons-vue'

interface CouponItem {
  id: number
  name: string
  type: string
  value: number
  threshold: number
  validDays: number
  total: number
  used: number
  status: string
  description?: string
  applicableProducts?: string[]
}

interface FilterForm {
  name: string
  type: string
  status: string
}

interface CouponStats {
  total: number
  used: number
  unused: number
  expired: number
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
  currentCoupon: CouponItem | null
}

interface CouponForm {
  name: string
  type: string
  value: number
  threshold: number
  total: number
  validDays: number
  description: string
  applicableProducts: string[]
}

const loading = ref(false)
const couponList = ref<CouponItem[]>([])
const filterForm = reactive<FilterForm>({
  name: '',
  type: '',
  status: ''
})

const couponStats = ref<CouponStats>({
  total: 58,
  used: 1256,
  unused: 3420,
  expired: 12
})

const pagination = reactive<Pagination>({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const dialog = reactive<Dialog>({
  visible: false,
  title: '创建优惠券',
  isEdit: false,
  currentCoupon: null
})

const couponForm = reactive<CouponForm>({
  name: '',
  type: 'discount',
  value: 10,
  threshold: 100,
  total: 1000,
  validDays: 7,
  description: '',
  applicableProducts: ['all']
})

const mockCouponData: CouponItem[] = [
  { id: 1, name: '新人专享券', type: 'discount', value: 20, threshold: 100, validDays: 7, total: 1000, used: 856, status: 'active', description: '新用户专享优惠券' },
  { id: 2, name: '满 200 减 30', type: 'discount', value: 30, threshold: 200, validDays: 15, total: 500, used: 230, status: 'active', description: '全品类通用' },
  { id: 3, name: '8 折折扣券', type: 'percent', value: 8, threshold: 50, validDays: 10, total: 200, used: 89, status: 'active', description: '数码产品专用' },
  { id: 4, name: '包邮券', type: 'shipping', value: 0, threshold: 0, validDays: 30, total: 300, used: 81, status: 'active', description: '全场包邮' },
  { id: 5, name: '满 500 减 80', type: 'discount', value: 80, threshold: 500, validDays: 7, total: 100, used: 0, status: 'pending', description: '大额优惠券' },
  { id: 6, name: '618 大促券', type: 'discount', value: 50, threshold: 300, validDays: 3, total: 1000, used: 0, status: 'pending', description: '618 活动专用' },
  { id: 7, name: '春节特惠券', type: 'discount', value: 100, threshold: 800, validDays: 15, total: 200, used: 200, status: 'expired', description: '春节活动已结束' }
]

const getFaceValue = (row: CouponItem) => {
  if (row.type === 'shipping') return '包邮'
  if (row.type === 'percent') return `${row.value}折`
  return `¥${row.value}`
}

const getTypeTag = (type: string) => {
  const map: Record<string, string> = { discount: 'primary', percent: 'success', shipping: 'warning' }
  return map[type] || 'info'
}

const getTypeText = (type: string) => {
  const map: Record<string, string> = { discount: '满减券', percent: '折扣券', shipping: '运费券' }
  return map[type] || type
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', active: 'success', expired: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '未开始', active: '发放中', expired: '已过期' }
  return map[status] || status
}

const loadCouponList = () => {
  loading.value = true
  setTimeout(() => {
    couponList.value = mockCouponData
    pagination.total = mockCouponData.length
    loading.value = false
  }, 500)
}

const searchCoupons = () => {
  ElMessage.success('搜索功能演示')
  loadCouponList()
}

const resetFilter = () => {
  filterForm.name = ''
  filterForm.type = ''
  filterForm.status = ''
}

const openCreateDialog = () => {
  dialog.visible = true
  dialog.title = '创建优惠券'
  dialog.isEdit = false
  Object.assign(couponForm, {
    name: '',
    type: 'discount',
    value: 10,
    threshold: 100,
    total: 1000,
    validDays: 7,
    description: '',
    applicableProducts: ['all']
  })
}

const editCoupon = (coupon: CouponItem) => {
  dialog.visible = true
  dialog.title = '编辑优惠券'
  dialog.isEdit = true
  dialog.currentCoupon = coupon
  Object.assign(couponForm, { ...coupon })
}

const toggleStatus = (coupon: CouponItem) => {
  coupon.status = coupon.status === 'active' ? 'pending' : 'active'
  ElMessage.success(`已${coupon.status === 'active' ? '上架' : '下架'}`)
}

const deleteCoupon = (coupon: CouponItem) => {
  ElMessageBox.confirm(`确定要删除优惠券"${coupon.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const index = couponList.value.findIndex(item => item.id === coupon.id)
    if (index !== -1) {
      couponList.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

const saveCoupon = () => {
  if (!couponForm.name) {
    ElMessage.warning('请输入优惠券名称')
    return
  }
  ElMessage.success(dialog.isEdit ? '保存成功' : '创建成功')
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
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
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

.glow-btn {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
}

.glow-btn:hover {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
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

.value-text {
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
</style>
