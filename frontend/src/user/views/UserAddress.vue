<template>
  <div class="page-container">
    <div class="container">
      <div class="page-header">
        <div style="display: flex; align-items: center;">
          <el-button class="back-btn" @click="goBack" circle>
            <el-icon><ArrowLeft /></el-icon>
          </el-button>
          <h2 class="sub-title">
            <el-icon><Location /></el-icon>
            地址管理
          </h2>
        </div>
        <el-button type="primary" class="add-btn" @click="openDialog('add')">
          <el-icon><Plus /></el-icon> 新增收货地址
        </el-button>
      </div>

      <div class="address-list">
        <div
          v-for="item in addressList"
          :key="item.id"
          class="address-card"
          :class="{ 'is-default': item.isDefault }"
        >
          <div class="card-header">
            <div class="user-info">
              <span class="name">{{ item.receiverName }}</span>
              <span class="phone">{{ item.receiverPhone }}</span>
            </div>
            <el-tag v-if="item.isDefault" type="warning" effect="dark" class="default-tag">
              默认地址
            </el-tag>
          </div>
          <div class="card-body">
            <el-icon class="location-icon"><Location /></el-icon>
            <span>{{ item.province }}{{ item.city }}{{ item.district }}{{ item.detailAddress }}</span>
          </div>
          <div class="card-actions">
            <el-button link type="primary" @click="openDialog('edit', item)">编辑</el-button>
            <el-button link type="primary" @click="setDefault(item)" v-if="!item.isDefault">
              设为默认
            </el-button>
            <el-button link type="danger" @click="deleteAddress(item.id)">删除</el-button>
          </div>
        </div>

        <el-empty v-if="addressList.length === 0" description="暂无收货地址" />
      </div>
    </div>

    <!-- 新增/编辑地址弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
    >
      <el-form :model="form" label-width="80px" :rules="rules" ref="formRef">
        <el-form-item label="收货人" prop="receiverName">
          <el-input v-model="form.receiverName" placeholder="请输入收货人姓名" />
        </el-form-item>
        <el-form-item label="手机号" prop="receiverPhone">
          <el-input v-model="form.receiverPhone" placeholder="请输入手机号码" maxlength="11" />
        </el-form-item>
        <el-form-item label="所在地区" prop="region">
          <el-input v-model="form.region" placeholder="省/市/区" />
        </el-form-item>
        <el-form-item label="详细地址" prop="detailAddress">
          <el-input
            v-model="form.detailAddress"
            type="textarea"
            :rows="3"
            placeholder="街道门牌"
          />
        </el-form-item>
        <el-form-item label="设为默认">
          <el-switch v-model="form.isDefault" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="saveAddress">确认保存</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { Location, Plus, ArrowLeft } from '@element-plus/icons-vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import request from '@/common/api/request'

const router = useRouter()

const addressList = ref([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增地址')
const formRef = ref(null)

const form = reactive({
  id: null,
  receiverName: '',
  receiverPhone: '',
  region: '',
  detailAddress: '',
  isDefault: false
})

const rules = {
  receiverName: [
    { required: true, message: '请输入收货人姓名', trigger: 'blur' }
  ],
  receiverPhone: [
    { required: true, message: '请输入手机号码', trigger: 'blur' },
    { pattern: /^1[3-9]\d{9}$/, message: '请输入正确的手机号', trigger: 'blur' }
  ],
  region: [
    { required: true, message: '请输入所在地区', trigger: 'blur' }
  ],
  detailAddress: [
    { required: true, message: '请输入详细地址', trigger: 'blur' }
  ]
}

const goBack = () => {
  router.push('/user/center')
}

const loadAddresses = async () => {
  try {
    const res = await request.get('/address/list')
    addressList.value = res.data || res || []
  } catch (error) {
    console.error('加载地址列表失败', error)
  }
}

const openDialog = (type, item = null) => {
  if (type === 'edit' && item) {
    dialogTitle.value = '编辑地址'
    form.id = item.id
    form.receiverName = item.receiverName
    form.receiverPhone = item.receiverPhone
    form.region = `${item.province}${item.city}${item.district}`
    form.detailAddress = item.detailAddress
    form.isDefault = item.isDefault
  } else {
    dialogTitle.value = '新增收货地址'
    form.id = null
    form.receiverName = ''
    form.receiverPhone = ''
    form.region = ''
    form.detailAddress = ''
    form.isDefault = false
  }
  dialogVisible.value = true
}

const saveAddress = async () => {
  try {
    await formRef.value.validate()

    const regionMatch = form.region.match(/([\u4e00-\u9fa5]+[省市自治区])([\u4e00-\u9fa5]+[市州区])([\u4e00-\u9fa5]+[区市县])?/)
    const province = regionMatch?.[1] || form.region
    const city = regionMatch?.[2] || ''
    const district = regionMatch?.[3] || ''

    const data = {
      receiverName: form.receiverName,
      receiverPhone: form.receiverPhone,
      province,
      city,
      district,
      detailAddress: form.detailAddress,
      isDefault: form.isDefault
    }

    if (form.id) {
      await request.put(`/address/${form.id}`, data)
    } else {
      await request.post('/address', data)
    }

    ElMessage.success(form.id ? '修改成功' : '添加成功')
    dialogVisible.value = false
    loadAddresses()
  } catch (error) {
    if (error !== false) {
      console.error('保存地址失败', error)
      ElMessage.error('保存地址失败')
    }
  }
}

const deleteAddress = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除吗？', '提示', { type: 'warning' })
    await request.delete(`/address/${id}`)
    ElMessage.success('删除成功')
    loadAddresses()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除地址失败')
    }
  }
}

const setDefault = async (item) => {
  try {
    await request.post(`/address/${item.id}/default`)
    ElMessage.success('已设为默认地址')
    loadAddresses()
  } catch (error) {
    ElMessage.error('设置默认地址失败')
  }
}

onMounted(() => {
  loadAddresses()
})
</script>

<style scoped>
.page-container {
  min-height: 100vh;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.15) 0%, rgba(10, 14, 26, 0.8) 100%);
  padding: 40px 20px;
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

.sub-title {
  font-size: 28px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 12px;
}

.back-btn {
  margin-right: 20px;
  background: transparent;
  border: 1px solid var(--mall-primary);
  color: var(--mall-primary);
  cursor: pointer;
}

.back-btn:hover {
  background: rgba(0, 212, 255, 0.1);
}

.add-btn {
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  border: none;
  color: #fff;
  padding: 10px 24px;
  border-radius: 8px;
  cursor: pointer;
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

:deep(.el-dialog) {
  background: rgba(20, 24, 40, 0.95) !important;
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 212, 255, 0.2);
}

:deep(.el-dialog__title) {
  color: #fff;
}

:deep(.el-form-item__label) {
  color: #ccc;
}

:deep(.el-input__wrapper) {
  background: rgba(0, 0, 0, 0.3);
  border-color: rgba(255, 255, 255, 0.1);
  box-shadow: none !important;
}

:deep(.el-input__inner) {
  color: #fff;
}
</style>
