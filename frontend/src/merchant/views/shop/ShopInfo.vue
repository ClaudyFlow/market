<template>
  <div class="page-container">
    <PageHeader title="店铺信息" :icon="Shop">
      <template #actions>
        <SciButton type="primary" @click="saveShopInfo">
          <el-icon><Check /></el-icon>
          保存修改
        </SciButton>
      </template>
    </PageHeader>

    <SciCard variant="gradient" class="shop-form-card">
      <el-form :model="shopForm" label-width="100px">
        <el-form-item label="店铺名称">
          <SciInput v-model="shopForm.name" placeholder="请输入店铺名称" />
        </el-form-item>
        <el-form-item label="店铺 logo">
          <SciInput v-model="shopForm.logo" placeholder="请输入 logo 地址" />
        </el-form-item>
        <el-form-item label="店铺 banner">
          <SciInput v-model="shopForm.banner" placeholder="请输入 banner 地址" />
        </el-form-item>
        <el-form-item label="店铺描述">
          <SciInput v-model="shopForm.description" type="textarea" :rows="4" placeholder="请输入店铺描述" />
        </el-form-item>
        <el-form-item label="联系电话">
          <SciInput v-model="shopForm.phone" placeholder="请输入联系电话" />
        </el-form-item>
        <el-form-item label="店铺地址">
          <SciInput v-model="shopForm.address" placeholder="请输入店铺地址" />
        </el-form-item>
        <el-form-item label="营业时间">
          <SciInput v-model="shopForm.businessHours" placeholder="请输入营业时间" />
        </el-form-item>
        <el-form-item label="店铺公告">
          <SciInput v-model="shopForm.announcement" type="textarea" :rows="3" placeholder="请输入店铺公告" />
        </el-form-item>
      </el-form>
    </SciCard>
  </div>
</template>

<script setup lang="ts">
import { reactive, onMounted } from 'vue'
import { ElMessage } from 'element-plus'
import { Shop, Check } from '@element-plus/icons-vue'
import { PageHeader } from '@merchant/components'
import { SciButton, SciInput, SciCard } from '@merchant/components/ui'

const shopForm = reactive({
  name: '',
  logo: '',
  banner: '',
  description: '',
  phone: '',
  address: '',
  businessHours: '',
  announcement: ''
})

const mockShopInfo = {
  name: '潮流数码旗舰店',
  logo: 'https://via.placeholder.com/100x100/1a2a4a/00d4ff?text=LOGO',
  banner: 'https://via.placeholder.com/800x200/1a2a4a/00d4ff?text=BANNER',
  description: '专注高品质数码产品',
  phone: '400-123-4567',
  address: '深圳市福田区',
  businessHours: '09:00-22:00',
  announcement: '新品上市，欢迎选购！'
}

const loadShopInfo = () => {
  Object.assign(shopForm, mockShopInfo)
}

const saveShopInfo = () => {
  ElMessage.success('保存成功')
}

onMounted(() => loadShopInfo())
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.shop-form-card {
  max-width: 800px;
}
</style>
