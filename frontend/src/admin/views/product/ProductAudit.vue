<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><DocumentChecked /></el-icon>
        商品审核
      </h1>
    </header>

    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><Goods /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ auditStats.total }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ auditStats.approved }}</div>
              <div class="stat-label">已通过</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ auditStats.pending }}</div>
              <div class="stat-label">待审核</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><CircleClose /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ auditStats.rejected }}</div>
              <div class="stat-label">已拒绝</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="商品 ID">
          <el-input v-model="filterForm.productId" placeholder="请输入商品 ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="商品名称">
          <el-input v-model="filterForm.productName" placeholder="请输入商品名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item label="店铺名称">
          <el-input v-model="filterForm.shopName" placeholder="请输入店铺名称" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="审核状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="待审核" value="pending" />
            <el-option label="已通过" value="approved" />
            <el-option label="已拒绝" value="rejected" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchProducts">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-section">
      <el-table :data="productList" class="sci-table" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="商品 ID" width="100" />
        <el-table-column prop="image" label="商品图片" width="100">
          <template #default="{ row }">
            <el-image
              :src="row.image || 'https://via.placeholder.com/60x60/1a2a4a/00d4ff?text=商品'"
              class="product-image"
              fit="cover"
              :preview-src-list="[row.image]"
            />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="商品名称" min-width="180" />
        <el-table-column prop="shopName" label="店铺名称" width="130" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="90">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="submitTime" label="提交时间" width="150" />
        <el-table-column prop="status" label="状态" width="90">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="viewProduct(row)">详情</el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="success"
              text
              size="small"
              @click="approveProduct(row)"
            >
              通过
            </el-button>
            <el-button
              v-if="row.status === 'pending'"
              type="danger"
              text
              size="small"
              @click="rejectProduct(row)"
            >
              拒绝
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
          @size-change="loadProductList"
          @current-change="loadProductList"
        />
      </div>
    </section>

    <el-dialog
      v-model="detailDialog.visible"
      title="商品详情"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-descriptions :column="2" border class="sci-descriptions">
        <el-descriptions-item label="商品 ID">{{ currentProduct?.id }}</el-descriptions-item>
        <el-descriptions-item label="商品名称">{{ currentProduct?.name }}</el-descriptions-item>
        <el-descriptions-item label="店铺名称">{{ currentProduct?.shopName }}</el-descriptions-item>
        <el-descriptions-item label="商品分类">{{ currentProduct?.category }}</el-descriptions-item>
        <el-descriptions-item label="商品价格">¥{{ currentProduct?.price }}</el-descriptions-item>
        <el-descriptions-item label="库存数量">{{ currentProduct?.stock }}</el-descriptions-item>
        <el-descriptions-item label="提交时间">{{ currentProduct?.submitTime }}</el-descriptions-item>
        <el-descriptions-item label="审核状态">
          <el-tag :type="getStatusType(currentProduct?.status || '')" size="small">
            {{ getStatusText(currentProduct?.status || '') }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="商品图片" :span="2">
          <el-image
            :src="currentProduct?.image || 'https://via.placeholder.com/300x200/1a2a4a/00d4ff?text=商品'"
            class="detail-image"
            fit="cover"
          />
        </el-descriptions-item>
        <el-descriptions-item label="商品描述" :span="2">{{ currentProduct?.description || '暂无描述' }}</el-descriptions-item>
        <el-descriptions-item label="拒绝原因" :span="2" v-if="currentProduct?.status === 'rejected'">
          {{ currentProduct?.rejectReason || '无' }}
        </el-descriptions-item>
      </el-descriptions>

      <el-form :model="auditForm" label-width="80px" style="margin-top: 20px" v-if="currentProduct?.status === 'pending'">
        <el-form-item label="审核意见">
          <el-input
            v-model="auditForm.comment"
            type="textarea"
            :rows="3"
            placeholder="请输入审核意见（拒绝时必填）"
          />
        </el-form-item>
      </el-form>

      <template #footer v-if="currentProduct?.status === 'pending'">
        <el-button @click="detailDialog.visible = false">取消</el-button>
        <el-button type="danger" @click="handleAudit('rejected')">拒绝</el-button>
        <el-button type="primary" @click="handleAudit('approved')">通过</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { DocumentChecked, Goods, CircleCheck, CircleClose, Clock } from '@element-plus/icons-vue'

interface ProductItem {
  id: number
  name: string
  shopName: string
  category: string
  price: number
  stock: number
  submitTime: string
  status: string
  image?: string
  description?: string
  rejectReason?: string
}

interface FilterForm {
  productId: string
  productName: string
  shopName: string
  status: string
}

interface AuditStats {
  total: number
  approved: number
  pending: number
  rejected: number
}

interface Pagination {
  currentPage: number
  pageSize: number
  total: number
}

interface Dialog {
  visible: boolean
}

interface AuditForm {
  comment: string
}

const loading = ref(false)
const productList = ref<ProductItem[]>([])
const filterForm = reactive<FilterForm>({
  productId: '',
  productName: '',
  shopName: '',
  status: ''
})

const auditStats = ref<AuditStats>({
  total: 1256,
  approved: 986,
  pending: 156,
  rejected: 114
})

const pagination = reactive<Pagination>({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const detailDialog = reactive<Dialog>({
  visible: false
})

const auditForm = reactive<AuditForm>({
  comment: ''
})

const currentProduct = ref<ProductItem | null>(null)

const mockProductData: ProductItem[] = [
  { id: 3001, name: '无线蓝牙耳机 Pro', shopName: '品质优选店', category: '手机数码', price: 299, stock: 500, submitTime: '2026-03-18 10:30', status: 'pending', image: '', description: '高品质无线蓝牙耳机，降噪设计' },
  { id: 3002, name: '智能手环 5', shopName: '数码港湾', category: '手机数码', price: 199, stock: 300, submitTime: '2026-03-18 09:15', status: 'pending', image: '', description: '运动健康监测，长续航' },
  { id: 3003, name: '机械键盘 RGB', shopName: '电脑配件店', category: '电脑办公', price: 459, stock: 200, submitTime: '2026-03-17 16:45', status: 'approved', image: '', description: 'Cherry 轴体，RGB 背光' },
  { id: 3004, name: '空气净化器 Max', shopName: '电器城', category: '家用电器', price: 1299, stock: 100, submitTime: '2026-03-17 14:20', status: 'approved', image: '', description: '大空间净化，静音设计' },
  { id: 3005, name: '假冒品牌手表', shopName: '可疑店铺', category: '服装鞋包', price: 99, stock: 1000, submitTime: '2026-03-17 11:00', status: 'rejected', image: '', description: '品牌手表', rejectReason: '涉嫌假冒品牌，拒绝上架' },
  { id: 3006, name: '护肤套装', shopName: '美妆小屋', category: '美妆护肤', price: 599, stock: 150, submitTime: '2026-03-16 20:30', status: 'pending', image: '', description: '进口护肤套装' },
  { id: 3007, name: '智能手表', shopName: '品质优选店', category: '手机数码', price: 999, stock: 80, submitTime: '2026-03-16 15:10', status: 'approved', image: '', description: '多功能智能手表' },
  { id: 3008, name: '办公椅 人体工学', shopName: '家居生活馆', category: '电脑办公', price: 799, stock: 50, submitTime: '2026-03-16 10:00', status: 'pending', image: '', description: '人体工学设计，舒适久坐' }
]

const loadProductList = () => {
  loading.value = true
  setTimeout(() => {
    productList.value = mockProductData
    pagination.total = mockProductData.length
    loading.value = false
  }, 500)
}

const searchProducts = () => {
  ElMessage.success('搜索功能演示')
  loadProductList()
}

const resetFilter = () => {
  filterForm.productId = ''
  filterForm.productName = ''
  filterForm.shopName = ''
  filterForm.status = ''
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { pending: 'warning', approved: 'success', rejected: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { pending: '待审核', approved: '已通过', rejected: '已拒绝' }
  return map[status] || status
}

const viewProduct = (product: ProductItem) => {
  currentProduct.value = product
  detailDialog.visible = true
  auditForm.comment = ''
}

const approveProduct = (product: ProductItem) => {
  ElMessageBox.confirm(`确定要通过商品"${product.name}"的审核吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    product.status = 'approved'
    ElMessage.success('审核通过')
  }).catch(() => {})
}

const rejectProduct = (product: ProductItem) => {
  ElMessageBox.prompt('请输入拒绝原因', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    inputPattern: /.+/,
    inputErrorMessage: '请输入拒绝原因'
  }).then(({ value }) => {
    product.status = 'rejected'
    product.rejectReason = value
    ElMessage.success('已拒绝')
  }).catch(() => {})
}

const handleAudit = (result: string) => {
  if (currentProduct.value) {
    if (result === 'rejected' && !auditForm.comment) {
      ElMessage.warning('请填写拒绝原因')
      return
    }
    currentProduct.value.status = result
    if (result === 'rejected') {
      currentProduct.value.rejectReason = auditForm.comment
    }
    ElMessage.success(result === 'approved' ? '审核通过' : '已拒绝')
    detailDialog.visible = false
    loadProductList()
  }
}

onMounted(() => {
  loadProductList()
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

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  cursor: pointer;
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

.detail-image {
  width: 300px;
  height: 200px;
  border-radius: 8px;
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
