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
              <div class="stat-value">{{ couponStats.active }}</div>
              <div class="stat-label">发放中</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ couponStats.usedUp }}</div>
              <div class="stat-label">已领完</div>
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
        <el-form-item label="优惠券状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="ACTIVE" value="ACTIVE" />
            <el-option label="INACTIVE" value="INACTIVE" />
            <el-option label="EXPIRED" value="EXPIRED" />
            <el-option label="USED_UP" value="USED_UP" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序方式">
          <el-select v-model="filterForm.sortBy" placeholder="选择排序" style="width: 120px">
            <el-option label="创建时间" value="createdAt" />
            <el-option label="使用量" value="usedCount" />
            <el-option label="剩余量" value="remainCount" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCouponList">刷新</el-button>
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
        <el-table-column prop="discountValue" label="优惠金额" width="100">
          <template #default="{ row }">
            <span class="value-text">{{ getFaceValue(row) }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="minPurchase" label="使用门槛" width="120">
          <template #default="{ row }">
            <span v-if="row.minPurchase">满¥{{ row.minPurchase }}</span>
            <span v-else>无门槛</span>
          </template>
        </el-table-column>
        <el-table-column prop="totalCount" label="发放数量" width="100" />
        <el-table-column prop="usedCount" label="已领取" width="100" />
        <el-table-column prop="remainCount" label="剩余" width="80" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusTag(row.status)" size="small">{{ row.status }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="validTo" label="有效期至" width="160">
          <template #default="{ row }">
            <span>{{ formatDate(row.validTo) }}</span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="editCoupon(row)">编辑</el-button>
            <el-button 
              v-if="row.status === 'ACTIVE'" 
              type="warning" text size="small" @click="toggleStatus(row, 'INACTIVE')"
            >
              下架
            </el-button>
            <el-button 
              v-if="row.status === 'INACTIVE'" 
              type="success" text size="small" @click="toggleStatus(row, 'ACTIVE')"
            >
              上架
            </el-button>
            <el-button type="danger" text size="small" @click="deleteCouponItem(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
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
    <el-dialog v-model="dialog.visible" :title="dialog.title" width="600px">
      <el-form :model="couponForm" label-width="100px">
        <el-form-item label="优惠券名称" required>
          <el-input v-model="couponForm.name" placeholder="请输入优惠券名称" />
        </el-form-item>
        <el-form-item label="优惠券类型" required>
          <el-radio-group v-model="couponForm.type">
            <el-radio label="FIXED">满减券</el-radio>
            <el-radio label="PERCENT">折扣券</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="优惠金额" required>
          <el-input-number 
            v-model="couponForm.discountValue" 
            :min="0" 
            :precision="2"
            style="width: 100%"
          />
          <span class="form-tip">{{ couponForm.type === 'PERCENT' ? '折扣比例 (1-100)' : '优惠金额 (元)' }}</span>
        </el-form-item>
        <el-form-item label="使用门槛">
          <el-input-number v-model="couponForm.minPurchase" :min="0" :precision="2" style="width: 100%" />
          <span class="form-tip">最低消费金额，0 表示无门槛</span>
        </el-form-item>
        <el-form-item label="最大优惠" v-if="couponForm.type === 'PERCENT'">
          <el-input-number v-model="couponForm.maxDiscount" :min="0" :precision="2" style="width: 100%" />
          <span class="form-tip">折扣券最高抵扣金额</span>
        </el-form-item>
        <el-form-item label="发放数量" required>
          <el-input-number v-model="couponForm.totalCount" :min="1" style="width: 100%" />
        </el-form-item>
        <el-form-item label="有效期" required>
          <el-date-picker
            v-model="couponForm.validPeriod"
            type="datetimerange"
            range-separator="至"
            start-placeholder="开始时间"
            end-placeholder="结束时间"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="使用范围">
          <el-select v-model="couponForm.scope" placeholder="选择使用范围" style="width: 100%">
            <el-option label="全场通用" value="ALL" />
            <el-option label="指定品类" value="CATEGORY" />
            <el-option label="指定商品" value="PRODUCT" />
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
import { 
  createCoupon, 
  updateCoupon, 
  deleteCoupon, 
  getCouponList, 
  getCouponStats,
  toggleCouponStatus,
  type Coupon 
} from '@merchant/api/coupon'

interface FilterForm {
  status: string
  sortBy: string
}

interface CouponStats {
  total: number
  active: number
  inactive: number
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
  currentCoupon: Coupon | null
}

interface CouponForm {
  name: string
  type: string
  discountValue: number
  minPurchase: number
  maxDiscount: number
  totalCount: number
  scope: string
  description: string
  validPeriod: [Date, Date] | null
}

const loading = ref(false)
const couponList = ref<Coupon[]>([])
const filterForm = reactive<FilterForm>({
  status: '',
  sortBy: 'createdAt'
})

const couponStats = ref<CouponStats>({
  total: 0,
  active: 0,
  inactive: 0,
  expired: 0,
  usedUp: 0
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
  type: 'FIXED',
  discountValue: 10,
  minPurchase: 100,
  maxDiscount: 0,
  totalCount: 1000,
  scope: 'ALL',
  description: '',
  validPeriod: null
})

// 加载优惠券列表
const loadCouponList = async () => {
  loading.value = true
  try {
    const res = await getCouponList({
      page: pagination.currentPage,
      size: pagination.pageSize,
      status: filterForm.status || undefined,
      sortBy: filterForm.sortBy
    })
    couponList.value = res.data.list
    pagination.total = res.data.total
  } catch (error: any) {
    ElMessage.error(error.message || '加载失败')
    couponList.value = []
  } finally {
    loading.value = false
  }
}

// 加载统计
const loadStats = async () => {
  try {
    const res = await getCouponStats()
    couponStats.value = res.data
  } catch (error) {
    console.error('加载统计失败', error)
  }
}

// 刷新
const searchCoupons = () => {
  pagination.currentPage = 1
  loadCouponList()
}

// 重置筛选
const resetFilter = () => {
  filterForm.status = ''
  filterForm.sortBy = 'createdAt'
  loadCouponList()
}

// 打开创建对话框
const openCreateDialog = () => {
  dialog.visible = true
  dialog.title = '创建优惠券'
  dialog.isEdit = false
  resetForm()
}

// 重置表单
const resetForm = () => {
  Object.assign(couponForm, {
    name: '',
    type: 'FIXED',
    discountValue: 10,
    minPurchase: 100,
    maxDiscount: 0,
    totalCount: 1000,
    scope: 'ALL',
    description: '',
    validPeriod: null
  })
}

// 编辑优惠券
const editCoupon = (coupon: Coupon) => {
  dialog.visible = true
  dialog.title = '编辑优惠券'
  dialog.isEdit = true
  dialog.currentCoupon = coupon
  
  Object.assign(couponForm, {
    name: coupon.name,
    type: coupon.type,
    discountValue: coupon.discountValue,
    minPurchase: coupon.minPurchase || 0,
    maxDiscount: coupon.maxDiscount || 0,
    totalCount: coupon.totalCount,
    scope: coupon.scope || 'ALL',
    description: coupon.description || '',
    validPeriod: coupon.validFrom && coupon.validTo ? [new Date(coupon.validFrom), new Date(coupon.validTo)] : null
  })
}

// 保存优惠券
const saveCoupon = async () => {
  if (!couponForm.name) {
    ElMessage.warning('请输入优惠券名称')
    return
  }
  
  if (!couponForm.validPeriod || couponForm.validPeriod.length !== 2) {
    ElMessage.warning('请选择有效期')
    return
  }

  try {
    const data: any = {
      name: couponForm.name,
      type: couponForm.type,
      discountValue: couponForm.discountValue,
      minPurchase: couponForm.minPurchase,
      totalCount: couponForm.totalCount,
      scope: couponForm.scope,
      description: couponForm.description,
      validFrom: couponForm.validPeriod[0].toISOString(),
      validTo: couponForm.validPeriod[1].toISOString()
    }

    if (couponForm.type === 'PERCENT' && couponForm.maxDiscount > 0) {
      data.maxDiscount = couponForm.maxDiscount
    }

    if (dialog.isEdit && dialog.currentCoupon) {
      await updateCoupon(dialog.currentCoupon.id, data)
      ElMessage.success('更新成功')
    } else {
      await createCoupon(data)
      ElMessage.success('创建成功')
    }

    dialog.visible = false
    loadCouponList()
    loadStats()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 删除优惠券
const deleteCouponItem = async (coupon: Coupon) => {
  try {
    await ElMessageBox.confirm(`确定要删除优惠券"${coupon.name}"吗？`, '提示', {
      type: 'warning'
    })
    
    await deleteCoupon(coupon.id)
    ElMessage.success('删除成功')
    loadCouponList()
    loadStats()
  } catch (error: any) {
    if (error !== 'cancel') {
      ElMessage.error(error.message || '删除失败')
    }
  }
}

// 上下架
const toggleStatus = async (coupon: Coupon, status: string) => {
  try {
    await toggleCouponStatus(coupon.id, status)
    ElMessage.success('操作成功')
    loadCouponList()
    loadStats()
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  }
}

// 工具函数
const getTypeTag = (type: string) => {
  const map: Record<string, string> = { FIXED: 'primary', PERCENT: 'success' }
  return map[type] || 'info'
}

const getTypeText = (type: string) => {
  const map: Record<string, string> = { FIXED: '满减券', PERCENT: '折扣券' }
  return map[type] || type
}

const getStatusTag = (status: string) => {
  const map: Record<string, string> = { ACTIVE: 'success', INACTIVE: 'info', EXPIRED: 'danger', USED_UP: 'warning' }
  return map[status] || 'info'
}

const getFaceValue = (row: Coupon) => {
  if (row.type === 'PERCENT') return `${row.discountValue}折`
  return `¥${row.discountValue}`
}

const formatDate = (dateStr?: string) => {
  if (!dateStr) return '-'
  const date = new Date(dateStr)
  return date.toLocaleDateString('zh-CN')
}

// 组件挂载时加载数据
onMounted(() => {
  loadCouponList()
  loadStats()
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

.glow-btn {
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
}

.glow-btn:hover {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

/* 统计卡片 */
.stats-cards {
  
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

.stat-card.primary { --glow-color: #00d4ff; }
.stat-card.success { --glow-color: #00ff88; }
.stat-card.warning { --glow-color: #ffaa00; }
.stat-card.danger { --glow-color: #ff4444; }

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
  background: linear-gradient(135deg, #ff4444, #cc0000);
  box-shadow: 0 0 15px rgba(255, 68, 68, 0.4);
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
  
}

/* 搜索栏 */
.search-bar {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.search-bar :deep(.el-form-item) {
  
  
}

.search-bar :deep(.el-form-item__label) {
  color: #ccc;
  font-weight: 500;
}

/* 表格区域 */
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

.value-text {
  color: #00ffff;
  font-weight: bold;
  font-size: 15px;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.8);
}

.pagination-bar {
  
  display: flex;
  justify-content: flex-end;
  padding-top: 20px;
  border-top: 1px solid rgba(0, 200, 255, 0.15);
}

.form-tip {
  display: block;
  font-size: 12px;
  color: #888;
  
}

/* 表格样式 */
.sci-table :deep(.el-table__header th) {
  background: linear-gradient(180deg,
    rgba(0, 100, 180, 0.4) 0%,
    rgba(0, 50, 100, 0.6) 100%);
  color: #00ffff;
  font-size: 13px;
  border-bottom: 2px solid rgba(0, 200, 255, 0.6);
  font-weight: 700;
  padding: 16px 0;
}

.sci-table :deep(.el-table__body td) {
  color: #88aacc !important;
  border-bottom: 1px solid rgba(0, 150, 200, 0.15);
  font-size: 13px;
  padding: 14px 0;
}

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

.sci-table :deep(.el-table__row:hover td) {
  color: #ffffff !important;
  background: transparent !important;
}
</style>
