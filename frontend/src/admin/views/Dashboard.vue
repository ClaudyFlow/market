<template>
  <div class="admin-dashboard">
    <!-- 顶部导航 -->
    <header class="admin-header">
      <h1>购物系统管理员后台</h1>
      <div class="user-info">
        <span>管理员：{{ adminName }}</span>
        <el-button size="small" @click="logout">退出</el-button>
      </div>
    </header>

    <!-- 导航菜单 -->
    <nav class="admin-nav">
      <el-tabs v-model="activeTab" @tab-change="handleTabChange">
        <el-tab-pane label="数据概览" name="dashboard"></el-tab-pane>
        <el-tab-pane label="商品管理" name="products"></el-tab-pane>
        <el-tab-pane label="订单管理" name="orders"></el-tab-pane>
        <el-tab-pane label="用户管理" name="users"></el-tab-pane>
        <el-tab-pane label="商家管理" name="merchants"></el-tab-pane>
        <el-tab-pane label="公告管理" name="notices"></el-tab-pane>
      </el-tabs>
    </nav>

    <!-- 主内容区 -->
    <div class="admin-container">
      <!-- 数据概览 -->
      <section v-show="activeTab === 'dashboard'" class="dashboard-section">
        <h2>数据概览</h2>
        <div class="stats-cards">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalUsers || 0 }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><Shop /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalProducts || 0 }}</div>
              <div class="stat-label">商品总数</div>
            </div>
          </div>
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Document /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.totalOrders || 0 }}</div>
              <div class="stat-label">订单总数</div>
            </div>
          </div>
          <div class="stat-card info">
            <div class="stat-icon"><i class="fas fa-chart-line"></i></div>
            <div class="stat-info">
              <div class="stat-value">¥{{ stats.todaySales || 0 }}</div>
              <div class="stat-label">今日销售</div>
            </div>
          </div>
        </div>

        <div class="charts-section">
          <div class="chart-card">
            <h3>销售趋势</h3>
            <div ref="salesChart" class="chart"></div>
          </div>
          <div class="chart-card">
            <h3>用户增长</h3>
            <div ref="userChart" class="chart"></div>
          </div>
        </div>
      </section>

      <!-- 商品管理 -->
      <section v-show="activeTab === 'products'" class="products-section">
        <div class="section-header">
          <h2>商品管理</h2>
          <el-button type="primary" @click="openProductDialog">
            <i class="fas fa-plus"></i> 添加商品
          </el-button>
        </div>
        <el-table :data="products" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="商品名称" min-width="150" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">¥{{ (row.price / 100).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="80" />
          <el-table-column prop="sales" label="销量" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ON_SALE' ? 'success' : 'info'">
                {{ row.status === 'ON_SALE' ? '在售' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="editProduct(row)">编辑</el-button>
              <el-button size="small" type="danger" @click="deleteProduct(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <!-- 订单管理 -->
      <section v-show="activeTab === 'orders'" class="orders-section">
        <div class="section-header">
          <h2>订单管理</h2>
          <el-input
            v-model="orderSearch"
            placeholder="搜索订单号"
            style="width: 200px"
            clearable
            @clear="loadOrders"
            @keyup.enter="loadOrders"
          />
        </div>
        <el-table :data="orders" style="width: 100%" v-loading="loading">
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="userName" label="用户" width="100" />
          <el-table-column prop="totalAmount" label="金额" width="100">
            <template #default="{ row }">¥{{ (row.totalAmount / 100).toFixed(2) }}</template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="100">
            <template #default="{ row }">
              <el-tag :type="getStatusType(row.status)">
                {{ getStatusText(row.status) }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="createdAt" label="下单时间" width="160" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewOrder(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <!-- 用户管理 -->
      <section v-show="activeTab === 'users'" class="users-section">
        <div class="section-header">
          <h2>用户管理</h2>
          <el-input
            v-model="userSearch"
            placeholder="搜索用户名"
            style="width: 200px"
            clearable
            @clear="loadUsers"
            @keyup.enter="loadUsers"
          />
        </div>
        <el-table :data="users" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="用户名" min-width="120" />
          <el-table-column prop="email" label="邮箱" min-width="150" />
          <el-table-column prop="vipLevel" label="VIP" width="80">
            <template #default="{ row }">
              <el-tag :type="row.vipLevel > 0 ? 'warning' : 'info'">
                {{ row.vipLevel > 0 ? 'VIP' + row.vipLevel : '普通' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'">
                {{ row.status === 'ACTIVE' ? '正常' : '封禁' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button
                size="small"
                :type="row.status === 'ACTIVE' ? 'warning' : 'success'"
                @click="toggleUserStatus(row)"
              >
                {{ row.status === 'ACTIVE' ? '封禁' : '解封' }}
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <!-- 商家管理 -->
      <section v-show="activeTab === 'merchants'" class="merchants-section">
        <div class="section-header">
          <h2>商家管理</h2>
        </div>
        <el-table :data="merchants" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="name" label="商家名称" min-width="120" />
          <el-table-column prop="shopName" label="店铺名称" min-width="150" />
          <el-table-column prop="merchantStatus" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.merchantStatus === 'ACTIVE' ? 'success' : 'warning'">
                {{ row.merchantStatus === 'ACTIVE' ? '正常' : '审核中' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewMerchant(row)">查看</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <!-- 公告管理 -->
      <section v-show="activeTab === 'notices'" class="notices-section">
        <div class="section-header">
          <h2>公告管理</h2>
          <el-button type="primary" @click="openNoticeDialog">
            <i class="fas fa-plus"></i> 发布公告
          </el-button>
        </div>
        <el-table :data="notices" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="ID" width="80" />
          <el-table-column prop="title" label="标题" min-width="200" />
          <el-table-column prop="type" label="类型" width="100">
            <template #default="{ row }">
              <el-tag :type="getTypeTag(row.type)">{{ row.type }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="priority" label="优先级" width="80" />
          <el-table-column prop="sendTime" label="发送时间" width="160" />
          <el-table-column label="操作" width="150" fixed="right">
            <template #default="{ row }">
              <el-button size="small" @click="viewNotice(row)">查看</el-button>
              <el-button size="small" type="danger" @click="deleteNotice(row.id)">删除</el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>
    </div>

    <!-- 添加商品对话框 -->
    <el-dialog v-model="productDialogVisible" title="添加商品" width="500px">
      <el-form :model="productForm" label-width="80px">
        <el-form-item label="商品名称">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="价格">
          <el-input-number v-model="productForm.price" :min="0" placeholder="单位：分" />
        </el-form-item>
        <el-form-item label="库存">
          <el-input-number v-model="productForm.stock" :min="0" />
        </el-form-item>
        <el-form-item label="分类">
          <el-select v-model="productForm.category" placeholder="请选择分类">
            <el-option label="数码电子" value="数码电子" />
            <el-option label="服装鞋帽" value="服装鞋帽" />
            <el-option label="家居生活" value="家居生活" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>

    <!-- 发布公告对话框 -->
    <el-dialog v-model="noticeDialogVisible" title="发布公告" width="500px">
      <el-form :model="noticeForm" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="noticeForm.title" placeholder="请输入公告标题" />
        </el-form-item>
        <el-form-item label="内容">
          <el-input v-model="noticeForm.content" type="textarea" :rows="5" placeholder="请输入公告内容" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="noticeForm.type" placeholder="请选择类型">
            <el-option label="系统通知" value="SYSTEM" />
            <el-option label="活动通知" value="ACTIVITY" />
            <el-option label="订单通知" value="ORDER" />
          </el-select>
        </el-form-item>
        <el-form-item label="优先级">
          <el-input-number v-model="noticeForm.priority" :min="1" :max="5" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="noticeDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveNotice">发布</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
// Font Awesome 图标直接使用类名，无需导入
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/common/api/request'

const activeTab = ref('dashboard')
const adminName = ref('管理员')
const loading = ref(false)
const stats = ref({})

// 商品数据
const products = ref([])
const productDialogVisible = ref(false)
const productForm = reactive({
  name: '',
  price: 0,
  stock: 0,
  category: ''
})

// 订单数据
const orders = ref([])
const orderSearch = ref('')

// 用户数据
const users = ref([])
const userSearch = ref('')

// 商家数据
const merchants = ref([])

// 公告数据
const notices = ref([])
const noticeDialogVisible = ref(false)
const noticeForm = reactive({
  title: '',
  content: '',
  type: 'SYSTEM',
  priority: 3
})

// 加载统计数据
const loadStats = async () => {
  try {
    const res = await request.get('/statistics/platform')
    stats.value = res.data || res
  } catch (error) {
    console.error('加载统计数据失败', error)
  }
}

// 加载商品
const loadProducts = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/products')
    products.value = res.data || res || []
  } catch (error) {
    console.error('加载商品失败', error)
  } finally {
    loading.value = false
  }
}

// 加载订单
const loadOrders = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/orders')
    orders.value = res.data || res || []
  } catch (error) {
    console.error('加载订单失败', error)
  } finally {
    loading.value = false
  }
}

// 加载用户
const loadUsers = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/users')
    users.value = res.data || res || []
  } catch (error) {
    console.error('加载用户失败', error)
  } finally {
    loading.value = false
  }
}

// 加载商家
const loadMerchants = async () => {
  loading.value = true
  try {
    const res = await request.get('/admin/merchants')
    merchants.value = res.data || res || []
  } catch (error) {
    console.error('加载商家失败', error)
  } finally {
    loading.value = false
  }
}

// 加载公告
const loadNotices = async () => {
  loading.value = true
  try {
    const res = await request.get('/message/list', {
      params: { page: 1, size: 50 }
    })
    notices.value = (res.data || res || {}).list || []
  } catch (error) {
    console.error('加载公告失败', error)
  } finally {
    loading.value = false
  }
}

// 标签类型
const getStatusType = (status) => {
  const types = {
    PENDING_PAYMENT: 'warning',
    PAID: 'primary',
    SHIPPED: 'success',
    COMPLETED: 'info',
    CANCELLED: 'danger'
  }
  return types[status] || 'info'
}

const getStatusText = (status) => {
  const texts = {
    PENDING_PAYMENT: '待支付',
    PAID: '已支付',
    SHIPPED: '已发货',
    COMPLETED: '已完成',
    CANCELLED: '已取消'
  }
  return texts[status] || status
}

const getTypeTag = (type) => {
  const tags = {
    SYSTEM: 'info',
    ACTIVITY: 'success',
    ORDER: 'primary'
  }
  return tags[type] || 'info'
}

// 商品操作
const openProductDialog = () => {
  productForm.name = ''
  productForm.price = 0
  productForm.stock = 0
  productForm.category = ''
  productDialogVisible.value = true
}

const saveProduct = async () => {
  try {
    await request.post('/admin/products', productForm)
    ElMessage.success('添加成功')
    productDialogVisible.value = false
    loadProducts()
  } catch (error) {
    ElMessage.error('添加失败')
  }
}

const editProduct = (row) => {
  // 编辑商品逻辑
  console.log('编辑商品', row)
}

const deleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm('确定删除该商品吗？', '提示', { type: 'warning' })
    await request.delete(`/admin/products/${id}`)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 用户操作
const toggleUserStatus = async (row) => {
  try {
    const action = row.status === 'ACTIVE' ? 'ban' : 'unban'
    await request.post(`/admin/users/${id}/${action}`)
    ElMessage.success('操作成功')
    loadUsers()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

// 公告操作
const openNoticeDialog = () => {
  noticeForm.title = ''
  noticeForm.content = ''
  noticeForm.type = 'SYSTEM'
  noticeForm.priority = 3
  noticeDialogVisible.value = true
}

const saveNotice = async () => {
  try {
    await request.post('/message/send', {
      title: noticeForm.title,
      content: noticeForm.content,
      type: noticeForm.type,
      priority: noticeForm.priority
    })
    ElMessage.success('发布成功')
    noticeDialogVisible.value = false
    loadNotices()
  } catch (error) {
    ElMessage.error('发布失败')
  }
}

const viewNotice = (row) => {
  console.log('查看公告', row)
}

// Tab 切换
const handleTabChange = (tab) => {
  if (tab === 'products') loadProducts()
  else if (tab === 'orders') loadOrders()
  else if (tab === 'users') loadUsers()
  else if (tab === 'merchants') loadMerchants()
  else if (tab === 'notices') loadNotices()
  else if (tab === 'dashboard') loadStats()
}

const logout = () => {
  localStorage.removeItem('token')
  window.location.href = '/login'
}

onMounted(() => {
  loadStats()
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background-color: #f4f4f4;
}

.admin-header {
  background: #35424a;
  color: #ffffff;
  padding: 15px 30px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.admin-header h1 {
  margin: 0;
  font-size: 20px;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 15px;
}

.admin-nav {
  background: white;
  padding: 0 30px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.admin-container {
  max-width: 1400px;
  margin: 20px auto;
  padding: 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
}

section {
  animation: fadeIn 0.3s ease-in-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(10px); }
  to { opacity: 1; transform: translateY(0); }
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.section-header h2 {
  color: #35424a;
  margin: 0;
}

/* 统计卡片 */
.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: white;
  border: 1px solid #e0e0e0;
  border-radius: 12px;
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.1);
}

.stat-card.primary { border-left: 4px solid #00d4ff; }
.stat-card.success { border-left: 4px solid #00ff88; }
.stat-card.warning { border-left: 4px solid #ffaa00; }
.stat-card.info { border-left: 4px solid #00a0ff; }

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  flex-shrink: 0;
}

.stat-card.primary .stat-icon { background: linear-gradient(135deg, #00d4ff, #00a8cc); }
.stat-card.success .stat-icon { background: linear-gradient(135deg, #00ff88, #00cc6a); }
.stat-card.warning .stat-icon { background: linear-gradient(135deg, #ffaa00, #ff8800); }
.stat-card.info .stat-icon { background: linear-gradient(135deg, #00a0ff, #0088cc); }

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #333;
}

.stat-label {
  font-size: 13px;
  color: #888;
}

/* 图表区域 */
.charts-section {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 20px;
}

.chart-card {
  background: #f9fafb;
  border-radius: 8px;
  padding: 20px;
}

.chart-card h3 {
  margin: 0 0 15px 0;
  color: #35424a;
}

.chart {
  height: 300px;
}
</style>
