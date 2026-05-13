<template>
  <div class="page-container">
    <PageHeader :title="isEdit ? '编辑商品' : '添加商品'" :icon="Goods">
      <template #actions>
        <SciButton @click="goBack">取消</SciButton>
        <SciButton type="primary" @click="submitForm" :loading="submitting">
          <el-icon><Check /></el-icon>
          {{ isEdit ? '保存修改' : '发布商品' }}
        </SciButton>
      </template>
    </PageHeader>

    <div class="edit-layout">
      <div class="edit-main">
        <SciCard title="基本信息">
          <el-form :model="form" label-width="100px" class="product-form">
            <el-form-item label="商品名称" required>
              <SciInput v-model="form.name" placeholder="请输入商品名称" />
            </el-form-item>
            <el-form-item label="商品分类" required>
              <SciSelect v-model="form.category" :options="categoryOptions" placeholder="请选择分类" />
            </el-form-item>
            <el-form-item label="商品品牌">
              <SciInput v-model="form.brand" placeholder="请输入品牌" />
            </el-form-item>
            <el-form-item label="商品描述">
              <el-input v-model="form.description" type="textarea" :rows="4" placeholder="请输入商品描述" />
            </el-form-item>
          </el-form>
        </SciCard>

        <SciCard title="价格库存">
          <el-form :model="form" label-width="100px" class="product-form">
            <el-form-item label="销售价格" required>
              <SciInput v-model="form.price" type="number" placeholder="0.00">
                <template #prepend>¥</template>
              </SciInput>
            </el-form-item>
            <el-form-item label="原价">
              <SciInput v-model="form.originalPrice" type="number" placeholder="0.00">
                <template #prepend>¥</template>
              </SciInput>
            </el-form-item>
            <el-form-item label="商品库存" required>
              <SciInput v-model="form.stock" type="number" placeholder="0" />
            </el-form-item>
            <el-form-item label="商品编码">
              <SciInput v-model="form.productNo" placeholder="系统自动生成" />
            </el-form-item>
          </el-form>
        </SciCard>

        <SciCard title="商品图片">
          <div class="image-upload-area">
            <div class="main-image">
              <span class="label">主图</span>
              <SciImage v-model="form.imageUrl" :limit="1" />
            </div>
            <div class="detail-images">
              <span class="label">详情图</span>
              <SciImage v-model="form.imageUrls" :limit="9" multiple />
            </div>
          </div>
        </SciCard>

        <SciCard title="商品详情">
          <el-form :model="form" label-width="100px" class="product-form">
            <el-form-item label="商品详情">
              <el-input v-model="form.detail" type="textarea" :rows="6" placeholder="请输入商品详情介绍" />
            </el-form-item>
          </el-form>
        </SciCard>
      </div>

      <div class="edit-sidebar">
        <SciCard title="其他设置">
          <el-form :model="form" label-width="80px" class="product-form">
            <el-form-item label="状态">
              <el-switch v-model="form.available" active-text="上架" inactive-text="下架" />
            </el-form-item>
            <el-form-item label="热卖">
              <el-switch v-model="form.hot" />
            </el-form-item>
            <el-form-item label="新品">
              <el-switch v-model="form.new" />
            </el-form-item>
            <el-form-item label="推荐">
              <el-switch v-model="form.recommend" />
            </el-form-item>
          </el-form>
        </SciCard>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { Check, Goods, Plus } from '@element-plus/icons-vue'
import { PageHeader, SciCard, SciButton, SciInput, SciSelect, SciImage } from '@merchant/components'
import { createProduct, updateProduct, getProductDetail } from '@merchant/api/product'

const route = useRoute()
const router = useRouter()

const submitting = ref(false)
const isEdit = computed(() => !!route.params.id)

const form = reactive({
  name: '',
  category: '',
  brand: '',
  description: '',
  price: 0,
  originalPrice: 0,
  stock: 0,
  productNo: '',
  imageUrl: '',
  imageUrls: [] as string[],
  detail: '',
  available: true,
  hot: false,
  new: false,
  recommend: false
})

const categoryOptions = [
  { label: '手机数码', value: '手机数码' },
  { label: '电脑办公', value: '电脑办公' },
  { label: '家用电器', value: '家用电器' },
  { label: '服装鞋包', value: '服装鞋包' },
  { label: '美妆护肤', value: '美妆护肤' },
  { label: '食品生鲜', value: '食品生鲜' }
]

const goBack = () => router.back()

const loadProduct = async () => {
  if (!isEdit.value) return
  try {
    const res = await getProductDetail(Number(route.params.id))
    const data = res.data || res
    Object.assign(form, {
      name: data.name || '',
      category: data.category || '',
      brand: data.brand || '',
      description: data.description || '',
      price: data.price || 0,
      originalPrice: data.originalPrice || 0,
      stock: data.stock || 0,
      productNo: data.productNo || '',
      imageUrl: data.imageUrl || data.image || '',
      imageUrls: data.imageUrls || [],
      detail: data.detail || '',
      available: data.available !== undefined ? data.available : true,
      hot: data.hot || false,
      new: data.new || false,
      recommend: data.recommend || false
    })
  } catch (error) {
    ElMessage.error('加载商品失败')
  }
}

const submitForm = async () => {
  if (!form.name || !form.price) {
    ElMessage.warning('请填写必要信息')
    return
  }
  submitting.value = true
  try {
    if (isEdit.value) {
      await updateProduct(Number(route.params.id), form)
      ElMessage.success('保存成功')
    } else {
      await createProduct(form)
      ElMessage.success('发布成功')
    }
    router.push('/merchant/product')
  } catch (error: any) {
    ElMessage.error(error.message || '操作失败')
  } finally {
    submitting.value = false
  }
}

onMounted(() => {
  loadProduct()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.edit-layout {
  display: grid;
  grid-template-columns: 1fr 320px;
  gap: 20px;
  margin-top: 20px;
}

.edit-main {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.product-form :deep(.el-form-item) {
  margin-bottom: 20px;
}

.image-upload-area {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.main-image .label,
.detail-images .label {
  display: block;
  margin-bottom: 10px;
  font-weight: 500;
  color: var(--mall-text-primary);
}
</style>