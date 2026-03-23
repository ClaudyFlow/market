<template>
  <div class="page-container">
    <!-- 页面标题栏 -->
    <header class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <el-icon><Goods /></el-icon>
          商品管理
        </h1>
      </div>
      <div class="header-right">
        <el-button type="primary" class="glow-btn" @click="打开新增窗口">
          <el-icon><Plus /></el-icon>
          新增商品
        </el-button>
      </div>
    </header>

    <!-- 搜索筛选栏 -->
    <section class="search-bar">
      <el-form :inline="true" :model="筛选表单">
        <el-form-item label="商品名称">
          <el-input
            v-model="筛选表单.商品名称"
            placeholder="请输入商品名称"
            clearable
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="商品分类">
          <el-select v-model="筛选表单.分类" placeholder="请选择分类" clearable style="width: 150px">
            <el-option label="手机数码" value="digital" />
            <el-option label="电脑办公" value="office" />
            <el-option label="家用电器" value="appliance" />
            <el-option label="服装鞋包" value="fashion" />
            <el-option label="家居家装" value="home" />
            <el-option label="美妆护肤" value="beauty" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品状态">
          <el-select v-model="筛选表单.状态" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="在售" value="active" />
            <el-option label="下架" value="inactive" />
            <el-option label="售罄" value="sold_out" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="搜索商品">搜索</el-button>
          <el-button @click="重置筛选">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <!-- 商品列表 -->
    <section class="table-section">
      <el-table :data="商品列表" class="sci-table" style="width: 100%" v-loading="加载中">
        <el-table-column prop="id" label="ID" width="80" />
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
        <el-table-column prop="name" label="商品名称" min-width="200" />
        <el-table-column prop="category" label="分类" width="100">
          <template #default="{ row }">
            <el-tag size="small" type="info">{{ row.category }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="price" label="价格" width="100">
          <template #default="{ row }">
            <span class="price-text">¥{{ row.price }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="stock" label="库存" width="80" />
        <el-table-column prop="sales" label="销量" width="80" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="获取状态类型 (row.status)" size="small">
              {{ 获取状态文本 (row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="编辑商品 (row)">编辑</el-button>
            <el-button type="success" text size="small" @click="查看商品 (row)">查看</el-button>
            <el-button
              :type="row.status === 'active' ? 'warning' : 'success'"
              text
              size="small"
              @click="切换状态 (row)"
            >
              {{ row.status === 'active' ? '下架' : '上架' }}
            </el-button>
            <el-button type="danger" text size="small" @click="删除商品 (row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="分页.当前页"
          v-model:page-size="分页.每页数量"
          :total="分页.总数"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="加载商品列表"
          @current-change="加载商品列表"
        />
      </div>
    </section>

    <!-- 新增/编辑商品对话框 -->
    <el-dialog
      v-model="对话框.可见"
      :title="对话框.标题"
      width="800px"
      :close-on-click-modal="false"
    >
      <el-form :model="商品表单" label-width="100px" class="product-form">
        <el-form-item label="商品名称" required>
          <el-input v-model="商品表单.名称" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类" required>
          <el-select v-model="商品表单.分类" placeholder="请选择分类" style="width: 100%">
            <el-option label="手机数码" value="digital" />
            <el-option label="电脑办公" value="office" />
            <el-option label="家用电器" value="appliance" />
            <el-option label="服装鞋包" value="fashion" />
            <el-option label="家居家装" value="home" />
            <el-option label="美妆护肤" value="beauty" />
          </el-select>
        </el-form-item>
        <el-form-item label="商品价格" required>
          <el-input-number v-model="商品表单.价格" :min="0" :precision="2" style="width: 100%" />
        </el-form-item>
        <el-form-item label="库存数量" required>
          <el-input-number v-model="商品表单.库存" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="商品图片" required>
          <el-upload
            class="image-uploader"
            action="/api/upload"
            :show-file-list="false"
            :on-success="处理图片上传成功"
          >
            <img v-if="商品表单.图片" :src="商品表单.图片" class="uploaded-image" />
            <el-icon v-else class="uploader-icon"><Plus /></el-icon>
          </el-upload>
        </el-form-item>
        <el-form-item label="商品描述">
          <el-input
            v-model="商品表单.描述"
            type="textarea"
            :rows="4"
            placeholder="请输入商品描述"
          />
        </el-form-item>
        <el-form-item label="商品状态">
          <el-radio-group v-model="商品表单.状态">
            <el-radio label="active">在售</el-radio>
            <el-radio label="inactive">下架</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="对话框.可见 = false">取消</el-button>
        <el-button type="primary" @click="保存商品">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Goods, Plus } from '@element-plus/icons-vue'

interface 商品项 {
  id: number
  name: string
  category: string
  price: number
  stock: number
  sales: number
  status: string
  image: string
  description?: string
}

interface 筛选表单类型 {
  商品名称:string
  分类:string
  状态:string
}

interface 商品表单类型 {
  名称:string
  分类:string
  价格:number
  库存:number
  图片:string
  描述:string
  状态:string
}

interface 分页类型 {
  当前页:number
  每页数量:number
  总数:number
}

interface 对话框类型 {
  可见:boolean
  标题:string
  编辑模式:boolean
  当前商品:商品项 | null
}

const 加载中 = ref(false)
const 商品列表 = ref<商品项[]>([])
const 筛选表单 = reactive<筛选表单类型>({
  商品名称:'',
  分类:'',
  状态:''
})

const 商品表单 = reactive<商品表单类型>({
  名称:'',
  分类:'',
  价格:0,
  库存:0,
  图片:'',
  描述:'',
  状态:'active'
})

const 分页 = reactive<分页类型>({
  当前页:1,
  每页数量:10,
  总数:0
})

const 对话框 = reactive<对话框类型>({
  可见:false,
  标题:'新增商品',
  编辑模式:false,
  当前商品:null
})

// 模拟商品数据
const 模拟商品数据:商品项 [] = [
  { id: 1, name: '无线蓝牙耳机', category: '手机数码', price: 199, stock: 500, sales: 1024, status: 'active', image: '' },
  { id: 2, name: '智能手环', category: '手机数码', price: 149, stock: 300, sales: 896, status: 'active', image: '' },
  { id: 3, name: '机械键盘', category: '电脑办公', price: 329, stock: 200, sales: 768, status: 'active', image: '' },
  { id: 4, name: '空气净化器', category: '家用电器', price: 999, stock: 100, sales: 512, status: 'active', image: '' },
  { id: 5, name: '运动跑鞋', category: '服装鞋包', price: 299, stock: 0, sales: 486, status: 'sold_out', image: '' },
  { id: 6, name: '护肤套装', category: '美妆护肤', price: 459, stock: 150, sales: 320, status: 'inactive', image: '' },
  { id: 7, name: '智能手表', category: '手机数码', price: 899, stock: 80, sales: 256, status: 'active', image: '' },
  { id: 8, name: '办公椅', category: '电脑办公', price: 599, stock: 50, sales: 189, status: 'active', image: '' }
]

const 加载商品列表 = () => {
  加载中.value = true
  // 模拟 API 调用
  setTimeout(() => {
    商品列表.value = 模拟商品数据
    分页.总数 = 模拟商品数据.length
    加载中.value = false
  }, 500)
}

const 搜索商品 = () => {
  ElMessage.success('搜索功能演示')
  加载商品列表 ()
}

const 重置筛选 = () => {
  筛选表单.商品名称 = ''
  筛选表单.分类 = ''
  筛选表单.状态 = ''
}

const 获取状态类型 = (状态:string) => {
  const 映射 = { active: 'success', inactive: 'info', sold_out: 'warning' }
  return 映射 [状态] || 'info'
}

const 获取状态文本 = (状态:string) => {
  const 映射 = { active: '在售', inactive: '下架', sold_out: '售罄' }
  return 映射 [状态] || 状态
}

const 打开新增窗口 = () => {
  对话框.可见 = true
  对话框.标题 = '新增商品'
  对话框.编辑模式 = false
  商品表单.名称 = ''
  商品表单.分类 = ''
  商品表单.价格 = 0
  商品表单.库存 = 0
  商品表单.图片 = ''
  商品表单.描述 = ''
  商品表单.状态 = 'active'
}

const 编辑商品 = (商品:商品项) => {
  对话框.可见 = true
  对话框.标题 = '编辑商品'
  对话框.编辑模式 = true
  对话框.当前商品 = 商品
  商品表单.名称 = 商品.name
  商品表单.分类 = 商品.category
  商品表单.价格 = 商品.price
  商品表单.库存 = 商品.stock
  商品表单.图片 = 商品.image
  商品表单.描述 = 商品.description || ''
  商品表单.状态 = 商品.status
}

const 查看商品 = (商品:商品项) => {
  ElMessage.info(`查看商品:${商品.name}`)
}

const 切换状态 = (商品:商品项) => {
  商品.status = 商品.status === 'active' ? 'inactive' : 'active'
  ElMessage.success(`已${商品.status === 'active' ? '上架' : '下架'}商品`)
}

const 删除商品 = (商品:商品项) => {
  ElMessageBox.confirm(`确定要删除商品"${商品.name}"吗?`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const 索引 = 商品列表.value.findIndex(item => item.id === 商品.id)
    if (索引 !== -1) {
      商品列表.value.splice(索引,1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

const 保存商品 = () => {
  if (!商品表单.名称) {
    ElMessage.warning('请输入商品名称')
    return
  }
  ElMessage.success(对话框.编辑模式 ? '保存成功' : '新增成功')
  对话框.可见 = false
  加载商品列表 ()
}

const 处理图片上传成功 = (响应:any) => {
  商品表单.图片 = 响应.url || 'https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=商品'
}

onMounted(() => {
  加载商品列表 ()
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
  margin-bottom: 25px;
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
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
  padding: 10px 20px;
  border-radius: 8px;
}

.glow-btn:hover {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

/* 搜索栏优化 */
.search-bar {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.search-bar :deep(.el-form-item) {
  margin-bottom: 0;
  margin-right: 15px;
}

.search-bar :deep(.el-form-item__label) {
  color: #ccc;
  font-weight: 500;
}

.search-bar :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s;
}

.search-bar :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.search-bar :deep(.el-input__inner) {
  color: #fff;
}

.search-bar :deep(.el-select .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-select__wrapper) {
  color: #fff;
}

.search-bar :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
  padding: 10px 20px;
}

.search-bar :deep(.el-button--primary:hover) {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

.search-bar :deep(.el-button) {
  border-radius: 8px;
}

/* 表格区域 */
.table-section {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 8px;
  cursor: pointer;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.3);
}

.product-image:hover {
  transform: scale(1.1);
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.3);
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

.product-form {
  max-height: 500px;
  overflow-y: auto;
}

.image-uploader {
  border: 1px dashed rgba(0, 212, 255, 0.3);
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(10, 14, 26, 0.4);
}

.image-uploader:hover {
  border-color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.05);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.1);
}

.uploaded-image {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.uploader-icon {
  font-size: 40px;
  color: var(--mall-primary);
}
</style>
