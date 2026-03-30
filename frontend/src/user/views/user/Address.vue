<template>
  <div class="address-page">
    <div class="container">
      <!-- 头部区域 -->
      <div class="page-header">
        <h1 class="page-title">地址管理</h1>
        <el-button type="primary" class="add-btn" @click="handleAdd">
          <i class="fas fa-plus"></i> 新增收货地址
        </el-button>
      </div>

      <!-- 地址列表区域 -->
      <div class="address-list">
        <div 
          v-for="(item, index) in addressList" 
          :key="item.id" 
          class="address-card"
          :class="{ 'is-default': item.isDefault }"
        >
          <div class="card-header">
            <div class="user-info">
              <span class="name">{{ item.name }}</span>
              <span class="phone">{{ item.phone }}</span>
            </div>
            <el-tag v-if="item.isDefault" type="warning" effect="dark" class="default-tag">默认地址</el-tag>
          </div>

          <div class="card-body">
            <i class="fas fa-map-marker-alt location-icon"></i>
            <span class="full-address">{{ item.region }} {{ item.detail }}</span>
          </div>

          <div class="card-actions">
            <el-button link type="primary" @click="handleEdit(item)">编辑</el-button>
            <el-button link type="primary" @click="handleSetDefault(item)" v-if="!item.isDefault">设为默认</el-button>
            <el-button link type="danger" @click="handleDelete(item.id)">删除</el-button>
          </div>
        </div>

        <!-- 空状态提示 -->
        <el-empty v-if="addressList.length === 0" description="暂无收货地址，请点击右上角添加" />
      </div>
    </div>

    <!-- 新增/编辑 弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="isEdit ? '编辑地址' : '新增收货地址'"
      width="500px"
      class="address-dialog"
      :close-on-click-modal="false"
    >
      <el-form
        ref="addressFormRef"
        :model="form"
        :rules="rules"
        label-width="80px"
        class="address-form"
      >
        <el-form-item label="收货人" prop="name">
          <el-input v-model="form.name" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        
        <!-- 手动输入地区 -->
        <el-form-item label="所在地区" prop="region">
          <el-input 
            v-model="form.region" 
            placeholder="请输入省/市/区，例如：北京市朝阳区" 
          />
        </el-form-item>

        <el-form-item label="详细地址" prop="detail">
          <el-input
            v-model="form.detail"
            type="textarea"
            :rows="3"
            placeholder="请输入街道、门牌号等详细信息"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
      </el-form>

      <template #footer>
        <span class="dialog-footer">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" @click="submitForm">确认保存</el-button>
        </span>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
// Font Awesome 图标直接使用类名，无需导入
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'

// --- 1. 数据管理 ---
const addressList = ref([])

// --- 2. LocalStorage 辅助函数 ---
const STORAGE_KEY = 'shopping_address_data' // 定义存储键名

// 从本地存储加载数据
const loadFromLocal = () => {
  const saved = localStorage.getItem(STORAGE_KEY)
  if (saved) {
    try {
      addressList.value = JSON.parse(saved)
    } catch (e) {
      console.error('读取地址数据失败', e)
      addressList.value = []
    }
  }
}

// 保存数据到本地存储
const saveToLocal = () => {
  localStorage.setItem(STORAGE_KEY, JSON.stringify(addressList.value))
}

// --- 3. 页面生命周期 ---
// 页面加载时自动读取数据
onMounted(() => {
  loadFromLocal()
})

// --- 4. 状态与表单 ---
const dialogVisible = ref(false)
const isEdit = ref(false)
const addressFormRef = ref(null)

const form = reactive({
  id: null,
  name: '',
  phone: '',
  region: '',
  detail: '',
  isDefault: false
})

const rules = {
  name: [{ required: true, message: '请输入收货人姓名', trigger: 'blur' }],
  phone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号码', trigger: 'blur' }
  ],
  region: [{ required: true, message: '请输入所在地区', trigger: 'blur' }],
  detail: [{ required: true, message: '请输入详细地址', trigger: 'blur' }]
}

// --- 5. 操作方法 ---

const handleAdd = () => {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

const handleEdit = (item) => {
  isEdit.value = true
  Object.assign(form, JSON.parse(JSON.stringify(item)))
  dialogVisible.value = true
}

const handleDelete = (id) => {
  ElMessageBox.confirm('确定要删除这个收货地址吗？', '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning',
  }).then(() => {
    addressList.value = addressList.value.filter(item => item.id !== id)
    saveToLocal() // 删除后保存
    ElMessage.success('删除成功')
  }).catch(() => {})
}

const handleSetDefault = (item) => {
  addressList.value.forEach(addr => addr.isDefault = false)
  item.isDefault = true
  saveToLocal() // 修改默认状态后保存
  ElMessage.success('已设为默认地址')
}

const submitForm = async () => {
  if (!addressFormRef.value) return
  
  await addressFormRef.value.validate((valid) => {
    if (valid) {
      if (isEdit.value) {
        const index = addressList.value.findIndex(item => item.id === form.id)
        if (index !== -1) {
          if (form.isDefault) {
            addressList.value.forEach(addr => addr.isDefault = false)
          }
          addressList.value[index] = { ...form }
          ElMessage.success('修改成功')
        }
      } else {
        const newId = addressList.value.length > 0 ? Math.max(...addressList.value.map(i => i.id)) + 1 : 1
        if (form.isDefault) {
          addressList.value.forEach(addr => addr.isDefault = false)
        }
        addressList.value.push({ ...form, id: newId })
        ElMessage.success('添加成功')
      }
      
      saveToLocal() // 提交成功后保存
      dialogVisible.value = false
    } else {
      return false
    }
  })
}

const resetForm = () => {
  form.id = null
  form.name = ''
  form.phone = ''
  form.region = ''
  form.detail = ''
  form.isDefault = false
  if (addressFormRef.value) {
    addressFormRef.value.clearValidate()
  }
}
</script>

<style scoped>
@import '@user/assets/mall-style.css';

.address-page {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.15) 0%, rgba(10, 14, 26, 0.8) 100%);
  padding: 40px 20px;
  color: var(--mall-text-primary);
}

.container {
  max-width: 1200px;
  margin: 0 auto;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.page-title {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.add-btn {
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  border: none;
  color: #fff;
  padding: 10px 24px;
  border-radius: 8px;
  font-weight: 500;
  box-shadow: 0 4px 15px rgba(0, 212, 255, 0.3);
  transition: all 0.3s;
}

.add-btn:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(0, 212, 255, 0.5);
}

.address-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 20px;
}

.address-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 24px;
  position: relative;
  transition: all 0.3s;
  backdrop-filter: blur(10px);
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.address-card:hover {
  border-color: var(--mall-primary);
  transform: translateY(-4px);
  box-shadow: 0 8px 30px rgba(0, 0, 0, 0.3);
}

.address-card.is-default {
  border-color: #ffd700;
  background: linear-gradient(135deg, rgba(255, 215, 0, 0.05), rgba(26, 31, 58, 0.6));
}

.card-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
}

.user-info {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.name {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.phone {
  font-size: 14px;
  color: #88aacc;
}

.default-tag {
  background: #ffd700;
  color: #000;
  border: none;
  font-weight: bold;
}

.card-body {
  display: flex;
  align-items: flex-start;
  gap: 8px;
  color: #ccc;
  font-size: 14px;
  line-height: 1.6;
  min-height: 40px;
}

.location-icon {
  margin-top: 2px;
  color: var(--mall-primary);
  flex-shrink: 0;
}

.card-actions {
  display: flex;
  gap: 12px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
  padding-top: 16px;
  margin-top: auto;
}

:deep(.address-dialog) {
  background: rgba(20, 24, 40, 0.95);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 212, 255, 0.2);
}

:deep(.el-dialog__title) {
  color: #fff;
}

:deep(.el-form-item__label) {
  color: #ccc;
}

:deep(.el-input__wrapper), :deep(.el-textarea__inner) {
  background: rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.1);
  color: #fff;
  box-shadow: none !important;
}

:deep(.el-input__wrapper:hover), :deep(.el-textarea__inner:hover) {
  border-color: var(--mall-primary);
}

:deep(.el-input__inner), :deep(.el-textarea__inner) {
  color: #fff;
}
</style>
