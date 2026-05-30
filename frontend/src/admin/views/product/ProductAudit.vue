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
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { DocumentChecked, Goods, CircleCheck, CircleClose, Clock } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'

interface Product {
  id: number
  name: string
  shopName: string
  category: string
  price: number
  stock: number
  image?: string
  description?: string
  submitTime: string
  status: string
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
  pending: number
  approved: number
  rejected: number
}

interface Pagination {
  currentPage: number
  pageSize: number
  total: number
}

interface DetailDialog {
  visible: boolean
}

const loading = ref(false)
const productList = ref<Product[]>([])
const filterForm = reactive<FilterForm>({
  productId: '',
  productName: '',
  shopName: '',
  status: ''
})

const auditStats = ref<AuditStats>({
  total: 2560,
  pending: 128,
  approved: 2305,
  rejected: 127
})

const pagination = reactive<Pagination>({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const detailDialog = reactive<DetailDialog>({
  visible: false
})

const currentProduct = ref<Product | null>(null)

const mockProductData: Product[] = [
  { id: 4001, name: 'iPhone 15 Pro Max', shopName: '潮流数码旗舰店', category: '数码电子', price: 9999, stock: 50, image: '', description: '苹果旗舰手机，A17 Pro芯片', submitTime: '2026-05-28 10:30', status: 'pending' },
  { id: 4002, name: '华为Mate60 Pro', shopName: '数码世界', category: '数码电子', price: 6999, stock: 100, image: '', description: '华为旗舰手机，麒麟芯片', submitTime: '2026-05-28 09:15', status: 'pending' },
  { id: 4003, name: '小米14 Ultra', shopName: '优品数码', category: '数码电子', price: 5999, stock: 80, image: '', description: '小米旗舰手机，骁龙8 Gen3', submitTime: '2026-05-27 16:45', status: 'approved' },
  { id: 4004, name: '索尼WH-1000XM5', shopName: '音频专营店', category: '数码电子', price: 2499, stock: 30, image: '', description: '索尼降噪耳机旗舰款', submitTime: '2026-05-27 14:20', status: 'approved' },
  { id: 4005, name: '耐克Air Jordan 1', shopName: '潮流运动馆', category: '服装鞋帽', price: 1499, stock: 200, image: '', description: '经典篮球鞋款', submitTime: '2026-05-26 11:30', status: 'rejected', rejectReason: '商品图片不清晰' },
  { id: 4006, name: '联想拯救者R9000P', shopName: '电脑专营', category: '数码电子', price: 8999, stock: 45, image: '', description: '游戏笔记本电脑', submitTime: '2026-05-26 09:00', status: 'approved' }
]

const loadProductList = () => {
  loading.value = true
  setTimeout(() => {
    let data = mockProductData
    if (filterForm.status) {
      data = data.filter(p => p.status === filterForm.status)
    }
    productList.value = data
    pagination.total = data.length
    loading.value = false
  }, 500)
}

const searchProducts = () => {
  pagination.currentPage = 1
  loadProductList()
}

const resetFilter = () => {
  filterForm.productId = ''
  filterForm.productName = ''
  filterForm.shopName = ''
  filterForm.status = ''
  loadProductList()
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { approved: 'success', pending: 'warning', rejected: 'danger' }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { approved: '已通过', pending: '待审核', rejected: '已拒绝' }
  return map[status] || status
}

const viewProduct = (product: Product) => {
  currentProduct.value = product
  detailDialog.visible = true
}

const approveProduct = (product: Product) => {
  ElMessageBox.confirm(`确定要通过商品"${product.name}"的审核吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'success'
  }).then(() => {
    product.status = 'approved'
    ElMessage.success('审核已通过')
  }).catch(() => {})
}

const rejectProduct = (product: Product) => {
  ElMessageBox.confirm(`确定要拒绝商品"${product.name}"的审核吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    product.status = 'rejected'
    product.rejectReason = '商品信息不符合规范'
    ElMessage.success('已拒绝')
  }).catch(() => {})
}

onMounted(() => {
  loadProductList()
})
</script>

<style scoped>
.page-container {
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.page-header {
  
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
  
}

.search-bar {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 15px 20px;
  
}

.search-bar :deep(.el-form-item) {
  
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
