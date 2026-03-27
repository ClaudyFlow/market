<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Shop /></el-icon>
        店铺管理
      </h1>
    </header>

    <section class="shop-section">
      <el-row :gutter="20">
        <el-col :span="12">
          <div class="info-card">
            <div class="card-header">
              <el-icon><Shop /></el-icon>
              <span class="card-title">店铺基本信息</span>
            </div>
            <el-form :model="shopForm" label-width="100px" class="shop-form">
              <el-form-item label="店铺名称">
                <el-input v-model="shopForm.name" placeholder="请输入店铺名称" />
              </el-form-item>
              <el-form-item label="店铺 Logo">
                <div class="logo-uploader">
                  <el-upload
                    class="image-uploader"
                    action="/api/upload"
                    :show-file-list="false"
                    :on-success="handleLogoUpload"
                  >
                    <img v-if="shopForm.logo" :src="shopForm.logo" class="uploaded-logo" />
                    <el-icon v-else class="uploader-icon"><Plus /></el-icon>
                  </el-upload>
                  <p class="uploader-tip">建议尺寸：200x200 像素，支持 jpg、png 格式</p>
                </div>
              </el-form-item>
              <el-form-item label="店铺 Banner">
                <div class="banner-uploader">
                  <el-upload
                    class="image-uploader"
                    action="/api/upload"
                    :show-file-list="false"
                    :on-success="handleBannerUpload"
                  >
                    <img v-if="shopForm.banner" :src="shopForm.banner" class="uploaded-banner" />
                    <el-icon v-else class="uploader-icon"><Plus /></el-icon>
                  </el-upload>
                  <p class="uploader-tip">建议尺寸：1200x300 像素，支持 jpg、png 格式</p>
                </div>
              </el-form-item>
              <el-form-item label="店铺等级">
                <el-tag size="large" type="warning">{{ shopForm.level }}</el-tag>
              </el-form-item>
              <el-form-item label="营业状态">
                <el-switch
                  v-model="shopForm.isOpen"
                  active-text="营业中"
                  inactive-text="休息中"
                  inline-prompt
                />
              </el-form-item>
            </el-form>
          </div>
        </el-col>

        <el-col :span="12">
          <div class="info-card">
            <div class="card-header">
              <el-icon><Document /></el-icon>
              <span class="card-title">店铺描述</span>
            </div>
            <el-form :model="shopForm" label-width="100px">
              <el-form-item label="店铺简介">
                <el-input
                  v-model="shopForm.description"
                  type="textarea"
                  :rows="4"
                  placeholder="请输入店铺简介"
                />
              </el-form-item>
              <el-form-item label="主营类目">
                <el-select v-model="shopForm.categories" multiple placeholder="请选择主营类目" style="width: 100%">
                  <el-option label="手机数码" value="digital" />
                  <el-option label="电脑办公" value="office" />
                  <el-option label="家用电器" value="appliance" />
                  <el-option label="服装鞋包" value="fashion" />
                  <el-option label="家居家装" value="home" />
                  <el-option label="美妆护肤" value="beauty" />
                  <el-option label="食品生鲜" value="food" />
                </el-select>
              </el-form-item>
              <el-form-item label="店铺公告">
                <el-input
                  v-model="shopForm.announcement"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入店铺公告"
                />
              </el-form-item>
            </el-form>
          </div>
        </el-col>
      </el-row>

      <div class="info-card">
        <div class="card-header">
          <el-icon><Phone /></el-icon>
          <span class="card-title">联系方式</span>
        </div>
        <el-form :model="shopForm" label-width="100px" class="contact-form">
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="客服电话">
                <el-input v-model="shopForm.phone" placeholder="请输入客服电话" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="客服 QQ">
                <el-input v-model="shopForm.qq" placeholder="请输入客服 QQ" />
              </el-form-item>
            </el-col>
            <el-col :span="8">
              <el-form-item label="客服微信">
                <el-input v-model="shopForm.wechat" placeholder="请输入客服微信" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row :gutter="20">
            <el-col :span="8">
              <el-form-item label="邮箱地址">
                <el-input v-model="shopForm.email" placeholder="请输入邮箱地址" />
              </el-form-item>
            </el-col>
            <el-col :span="16">
              <el-form-item label="店铺地址">
                <el-input v-model="shopForm.address" placeholder="请输入店铺地址" />
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>

      <div class="info-card">
        <div class="card-header">
          <el-icon><DocumentChecked /></el-icon>
          <span class="card-title">资质信息</span>
        </div>
        <el-descriptions :column="3" border class="sci-descriptions">
          <el-descriptions-item label="营业执照">
            <el-image
              :src="shopForm.businessLicense || 'https://via.placeholder.com/150x100/1a2a4a/00d4ff?text=营业执照'"
              class="cert-image"
              fit="cover"
              :preview-src-list="[shopForm.businessLicense || 'https://via.placeholder.com/150x100/1a2a4a/00d4ff?text=营业执照']"
            />
          </el-descriptions-item>
          <el-descriptions-item label="许可证">
            <el-image
              :src="shopForm.license || 'https://via.placeholder.com/150x100/1a2a4a/00d4ff?text=许可证'"
              class="cert-image"
              fit="cover"
              :preview-src-list="[shopForm.license || 'https://via.placeholder.com/150x100/1a2a4a/00d4ff?text=许可证']"
            />
          </el-descriptions-item>
          <el-descriptions-item label="品牌授权">
            <el-image
              :src="shopForm.authorization || 'https://via.placeholder.com/150x100/1a2a4a/00d4ff?text=品牌授权'"
              class="cert-image"
              fit="cover"
              :preview-src-list="[shopForm.authorization || 'https://via.placeholder.com/150x100/1a2a4a/00d4ff?text=品牌授权']"
            />
          </el-descriptions-item>
        </el-descriptions>
        <div class="cert-upload-row">
          <el-button type="primary" size="small">上传营业执照</el-button>
          <el-button type="primary" size="small">上传许可证</el-button>
          <el-button type="primary" size="small">上传品牌授权</el-button>
        </div>
      </div>

      <div class="form-actions">
        <el-button type="primary" size="large" class="glow-btn" @click="saveShopInfo">
          <el-icon><Check /></el-icon>
          保存设置
        </el-button>
      </div>
    </section>

    <section class="stats-section">
      <div class="section-header">
        <el-icon><DataAnalysis /></el-icon>
        <h2>店铺数据</h2>
      </div>
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="data-card">
            <div class="data-label">总销售额</div>
            <div class="data-value">¥{{ shopStats.sales }}</div>
            <div class="data-trend positive">
              <el-icon><Top /></el-icon>
              <span>较上月 +12.5%</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-label">总订单数</div>
            <div class="data-value">{{ shopStats.orders }}</div>
            <div class="data-trend positive">
              <el-icon><Top /></el-icon>
              <span>较上月 +8.3%</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-label">访客数</div>
            <div class="data-value">{{ shopStats.visitors }}</div>
            <div class="data-trend negative">
              <el-icon><Bottom /></el-icon>
              <span>较上月 -2.1%</span>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="data-card">
            <div class="data-label">收藏数</div>
            <div class="data-value">{{ shopStats.favorites }}</div>
            <div class="data-trend positive">
              <el-icon><Top /></el-icon>
              <span>较上月 +15.7%</span>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { ElMessage } from 'element-plus'
import {
  Shop,
  Plus,
  Document,
  Phone,
  DocumentChecked,
  Check,
  DataAnalysis,
  Top,
  Bottom
} from '@element-plus/icons-vue'

interface ShopForm {
  name: string
  logo: string
  banner: string
  level: string
  isOpen: boolean
  description: string
  categories: string[]
  announcement: string
  phone: string
  qq: string
  wechat: string
  email: string
  address: string
  businessLicense: string
  license: string
  authorization: string
}

interface ShopStats {
  sales: string
  orders: number
  visitors: number
  favorites: number
}

const shopForm = reactive<ShopForm>({
  name: '品质优选店',
  logo: '',
  banner: '',
  level: '金牌商家',
  isOpen: true,
  description: '专注为消费者提供优质精选商品，所有商品均为正品保证。',
  categories: ['digital', 'appliance'],
  announcement: '新品上架，欢迎选购！全场满 199 包邮！',
  phone: '400-888-8888',
  qq: '800888888',
  wechat: 'shop_service',
  email: 'service@shop.com',
  address: '北京市朝阳区 xx 路 xx 号',
  businessLicense: '',
  license: '',
  authorization: ''
})

const shopStats = ref<ShopStats>({
  sales: '125,680',
  orders: 1256,
  visitors: 8520,
  favorites: 3580
})

const handleLogoUpload = (response: any) => {
  shopForm.logo = response.url || 'https://via.placeholder.com/200x200/00d4ff/fff?text=Logo'
  ElMessage.success('Logo 上传成功')
}

const handleBannerUpload = (response: any) => {
  shopForm.banner = response.url || 'https://via.placeholder.com/1200x300/1a2a4a/00d4ff?text=Banner'
  ElMessage.success('Banner 上传成功')
}

const saveShopInfo = () => {
  if (!shopForm.name) {
    ElMessage.warning('请输入店铺名称')
    return
  }
  ElMessage.success('店铺信息保存成功')
}
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

.shop-section {
  display: flex;
  flex-direction: column;
  gap: 20px;
  
}

.info-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.card-header .el-icon {
  color: var(--mall-primary);
  font-size: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
}

.shop-form :deep(.el-form-item) {
  
}

.shop-form :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
}

.shop-form :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.shop-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.shop-form :deep(.el-input__inner) {
  color: #fff;
}

.shop-form :deep(.el-textarea__inner) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  color: #fff;
}

.shop-form :deep(.el-textarea__inner:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.shop-form :deep(.el-textarea__inner:focus) {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.shop-form :deep(.el-select .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.shop-form :deep(.el-select__wrapper) {
  color: #fff;
}

.logo-uploader,
.banner-uploader {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.image-uploader {
  border: 1px dashed rgba(0, 212, 255, 0.3);
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  cursor: pointer;
  transition: all 0.3s;
  background: rgba(10, 14, 26, 0.4);
  width: fit-content;
}

.image-uploader:hover {
  border-color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.05);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.1);
}

.uploaded-logo {
  width: 150px;
  height: 150px;
  object-fit: cover;
  border-radius: 12px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.uploaded-banner {
  width: 400px;
  height: 100px;
  object-fit: cover;
  border-radius: 8px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
}

.uploader-icon {
  font-size: 40px;
  color: var(--mall-primary);
}

.uploader-tip {
  font-size: 12px;
  color: #888;
}

.contact-form {
  max-width: 800px;
}

.cert-image {
  width: 150px;
  height: 100px;
  border-radius: 6px;
  cursor: pointer;
}

.cert-upload-row {
  
  display: flex;
  gap: 10px;
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

.form-actions {
  display: flex;
  justify-content: center;
  padding: 20px;
}

.glow-btn {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
  padding: 15px 40px;
}

.glow-btn:hover {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

.stats-section {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 10px;
  
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.section-header .el-icon {
  color: var(--mall-primary);
  font-size: 22px;
}

.section-header h2 {
  font-size: 18px;
  font-weight: bold;
  color: #fff;
}

.data-card {
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
}

.data-card:hover {
  border-color: var(--mall-primary);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.15);
}

.data-label {
  font-size: 13px;
  color: #888;
  
}

.data-value {
  font-size: 28px;
  font-weight: bold;
  color: var(--mall-primary);
  
}

.data-trend {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 5px;
  font-size: 12px;
}

.data-trend.positive {
  color: #00ff88;
}

.data-trend.negative {
  color: #ff6666;
}
</style>
