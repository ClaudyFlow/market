<template>
  <div class="merchant-page">
    <header class="header">
      <h1>商家管理后台</h1>
    </header>

    <nav class="nav-tabs">
      <a
        href="#product-management"
        :class="{ active: activeTab === 'product' }"
        @click.prevent="activeTab = 'product'"
      >
        商品管理
      </a>
      <a
        href="#order-management"
        :class="{ active: activeTab === 'order' }"
        @click.prevent="activeTab = 'order'"
      >
        订单管理
      </a>
      <a
        href="#shop-settings"
        :class="{ active: activeTab === 'settings' }"
        @click.prevent="activeTab = 'settings'"
      >
        店铺设置
      </a>
    </nav>

    <div class="container">
      <!-- 商品管理 -->
      <section id="product-management" v-show="activeTab === 'product'">
        <div class="section-header">
          <h2>
            <el-icon><Shop /></el-icon>
            商品管理
          </h2>
          <el-button type="primary" @click="openProductDialog('add')">
            <el-icon><Plus /></el-icon> 添加商品
          </el-button>
        </div>

        <el-table :data="productList" style="width: 100%" v-loading="loading">
          <el-table-column prop="id" label="商品 ID" width="80" />
          <el-table-column prop="image" label="商品图片" width="100">
            <template #default="{ row }">
              <el-image
                :src="row.image || 'https://via.placeholder.com/60x60?text=商品'"
                class="product-thumb"
                fit="cover"
              />
            </template>
          </el-table-column>
          <el-table-column prop="name" label="商品名称" min-width="150" />
          <el-table-column prop="price" label="价格" width="100">
            <template #default="{ row }">
              <span class="price">¥{{ (row.price / 100).toFixed(2) }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="stock" label="库存" width="80" />
          <el-table-column prop="status" label="状态" width="80">
            <template #default="{ row }">
              <el-tag :type="row.status === 'ON_SALE' ? 'success' : 'info'">
                {{ row.status === 'ON_SALE' ? '在售' : '下架' }}
              </el-tag>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200" fixed="right">
            <template #default="{ row }">
              <el-button type="primary" size="small" @click="openProductDialog('edit', row)">
                编辑
              </el-button>
              <el-button
                :type="row.status === 'ON_SALE' ? 'warning' : 'success'"
                size="small"
                @click="toggleProductStatus(row)"
              >
                {{ row.status === 'ON_SALE' ? '下架' : '上架' }}
              </el-button>
              <el-button type="danger" size="small" @click="deleteProduct(row.id)">
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>

        <div class="pagination-bar" v-if="productList.length > 0">
          <el-pagination
            v-model:current-page="productPagination.currentPage"
            v-model:page-size="productPagination.pageSize"
            :total="productPagination.total"
            layout="total, prev, pager, next"
            @current-change="loadProducts"
          />
        </div>
      </section>

      <!-- 订单管理 -->
      <section id="order-management" v-show="activeTab === 'order'">
        <div class="section-header">
          <h2>
            <el-icon><Document /></el-icon>
            订单管理
          </h2>
        </div>

        <el-table :data="orderList" style="width: 100%" v-loading="orderLoading">
          <el-table-column prop="orderNo" label="订单号" width="180" />
          <el-table-column prop="customerName" label="客户姓名" width="100" />
          <el-table-column prop="totalAmount" label="订单金额" width="100">
            <template #default="{ row }">
              <span class="price">¥{{ (row.totalAmount / 100).toFixed(2) }}</span>
            </template>
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
              <el-button type="primary" size="small" @click="viewOrder(row.id)">
                查看
              </el-button>
              <el-button
                v-if="row.status === 'PAID'"
                type="success"
                size="small"
                @click="shipOrder(row.id)"
              >
                发货
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </section>

      <!-- 店铺设置 -->
      <section id="shop-settings" v-show="activeTab === 'settings'">
        <div class="section-header">
          <h2>
            <el-icon><Setting /></el-icon>
            账户设置
          </h2>
        </div>

        <el-form :model="shopForm" label-width="120px" class="shop-form">
          <el-form-item label="商家名称">
            <el-input v-model="shopForm.shopName" placeholder="请输入商家名称" />
          </el-form-item>
          <el-form-item label="店铺 Logo">
            <el-input v-model="shopForm.shopLogo" placeholder="请输入 Logo URL" />
          </el-form-item>
          <el-form-item label="店铺简介">
            <el-input
              v-model="shopForm.description"
              type="textarea"
              :rows="4"
              placeholder="请输入店铺简介"
            />
          </el-form-item>
          <el-form-item label="联系电话">
            <el-input v-model="shopForm.phone" placeholder="请输入联系电话" />
          </el-form-item>
          <el-form-item label="联系邮箱">
            <el-input v-model="shopForm.email" placeholder="请输入联系邮箱" />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="saveShopSettings">保存设置</el-button>
          </el-form-item>
        </el-form>
      </section>
    </div>

    <!-- 添加/编辑商品对话框 -->
    <el-dialog
      v-model="productDialogVisible"
      :title="isEditMode ? '编辑商品' : '添加商品'"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="productForm" label-width="100px" :rules="productRules" ref="productFormRef">
        <el-form-item label="商品名称" prop="name">
          <el-input v-model="productForm.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品价格" prop="price">
          <el-input-number v-model="productForm.price" :min="0" :precision="2" placeholder="单位：分" />
        </el-form-item>
        <el-form-item label="库存数量" prop="stock">
          <el-input-number v-model="productForm.stock" :min="0" placeholder="库存数量" />
        </el-form-item>
        <el-form-item label="商品分类" prop="category">
          <el-select v-model="productForm.category" placeholder="请选择分类">
            <el-option label="数码电子" value="数码电子" />
            <el-option label="服装鞋帽" value="服装鞋帽" />
            <el-option label="家居生活" value="家居生活" />
            <el-option label="美妆护肤" value="美妆护肤" />
            <el-option label="食品饮料" value="食品饮料" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品图片" prop="image">
          <el-input v-model="productForm.image" placeholder="请输入图片 URL" />
        </el-form-item>
        <el-form-item label="商品描述" prop="description">
          <el-input
            v-model="productForm.description"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
          />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="productDialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveProduct">保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { Shop, Document, Setting, Plus } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/common/api/request'

const activeTab = ref('product')

// 商品管理
const loading = ref(false)
const productList = ref([])
const productDialogVisible = ref(false)
const isEditMode = ref(false)
const productFormRef = ref(null)

const productPagination = reactive({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const productForm = reactive({
  id: null,
  name: '',
  price: 0,
  stock: 0,
  category: '',
  image: '',
  description: ''
})

const productRules = {
  name: [{ required: true, message: '请输入商品名称', trigger: 'blur' }],
  price: [{ required: true, message: '请输入商品价格', trigger: 'blur' }],
  stock: [{ required: true, message: '请输入库存数量', trigger: 'blur' }],
  category: [{ required: true, message: '请选择商品分类', trigger: 'change' }]
}

const loadProducts = async () => {
  loading.value = true
  try {
    const res = await request.get('/merchant/products', {
      params: {
        page: productPagination.currentPage - 1,
        size: productPagination.pageSize
      }
    })
    productList.value = res.data?.content || res.content || []
    productPagination.total = res.data?.total || res.total || 0
  } catch (error) {
    console.error('加载商品列表失败', error)
    ElMessage.error('加载商品列表失败')
  } finally {
    loading.value = false
  }
}

const openProductDialog = (type, row = null) => {
  isEditMode.value = type === 'edit'
  if (row) {
    productForm.id = row.id
    productForm.name = row.name
    productForm.price = row.price
    productForm.stock = row.stock
    productForm.category = row.category
    productForm.image = row.image
    productForm.description = row.description
  } else {
    productForm.id = null
    productForm.name = ''
    productForm.price = 0
    productForm.stock = 0
    productForm.category = ''
    productForm.image = ''
    productForm.description = ''
  }
  productDialogVisible.value = true
}

const saveProduct = async () => {
  try {
    await productFormRef.value.validate()
    if (isEditMode.value) {
      await request.put(`/merchant/products/${productForm.id}`, productForm)
      ElMessage.success('修改成功')
    } else {
      await request.post('/merchant/products', productForm)
      ElMessage.success('添加成功')
    }
    productDialogVisible.value = false
    loadProducts()
  } catch (error) {
    if (error !== false) {
      console.error('保存商品失败', error)
      ElMessage.error('保存商品失败')
    }
  }
}

const toggleProductStatus = async (row) => {
  try {
    const newStatus = row.status === 'ON_SALE' ? 'OFF_SALE' : 'ON_SALE'
    await request.put(`/merchant/products/${row.id}/status`, { status: newStatus })
    ElMessage.success('操作成功')
    loadProducts()
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

const deleteProduct = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该商品吗？', '提示', { type: 'warning' })
    await request.delete(`/merchant/products/${id}`)
    ElMessage.success('删除成功')
    loadProducts()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

// 订单管理
const orderLoading = ref(false)
const orderList = ref([])

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

const loadOrders = async () => {
  orderLoading.value = true
  try {
    const res = await request.get('/merchant/orders')
    orderList.value = res.data || res || []
  } catch (error) {
    console.error('加载订单列表失败', error)
  } finally {
    orderLoading.value = false
  }
}

const viewOrder = (orderId) => {
  // 查看订单详情
  console.log('查看订单', orderId)
}

const shipOrder = async (orderId) => {
  try {
    await ElMessageBox.confirm('确认发货？', '提示', { type: 'warning' })
    await request.post(`/merchant/orders/${orderId}/ship`)
    ElMessage.success('发货成功')
    loadOrders()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('发货失败')
    }
  }
}

// 店铺设置
const shopForm = reactive({
  shopName: '',
  shopLogo: '',
  description: '',
  phone: '',
  email: ''
})

const loadShopSettings = async () => {
  try {
    const res = await request.get('/merchant/shop/info')
    Object.assign(shopForm, res.data || res)
  } catch (error) {
    console.error('加载店铺信息失败', error)
  }
}

const saveShopSettings = async () => {
  try {
    await request.put('/merchant/shop/info', shopForm)
    ElMessage.success('保存成功')
  } catch (error) {
    ElMessage.error('保存失败')
  }
}

onMounted(() => {
  loadProducts()
  loadOrders()
  loadShopSettings()
})
</script>

<style scoped>
.merchant-page {
  min-height: 100vh;
  background-color: #f5f7fa;
}

.header {
  background: linear-gradient(135deg, #4CAF50 0%, #45a049 100%);
  color: white;
  padding: 20px;
  text-align: center;
}

.header h1 {
  margin: 0;
  font-size: 24px;
}

.nav-tabs {
  background: white;
  padding: 0 20px;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
  display: flex;
  gap: 0;
}

.nav-tabs a {
  padding: 15px 30px;
  text-decoration: none;
  color: #666;
  font-weight: bold;
  border-bottom: 3px solid transparent;
  transition: all 0.3s;
}

.nav-tabs a:hover,
.nav-tabs a.active {
  color: #4CAF50;
  border-bottom-color: #4CAF50;
  background: rgba(76, 175, 80, 0.05);
}

.container {
  max-width: 1200px;
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
  color: #4CAF50;
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0;
}

.product-thumb {
  width: 60px;
  height: 60px;
  border-radius: 4px;
}

.price {
  color: #f56c6c;
  font-weight: bold;
}

.pagination-bar {
  display: flex;
  justify-content: center;
  margin-top: 20px;
}

.shop-form {
  max-width: 600px;
}
</style>
