<template>
  <div class="order-review" v-loading="loading">
    <!-- 返回 -->
    <div class="review-header">
      <el-button text @click="$router.push(`/user/orders/${orderId}`)">
        <el-icon><ArrowLeft /></el-icon> 返回订单详情
      </el-button>
      <h2>评价订单</h2>
    </div>

    <!-- 订单不存在 -->
    <div v-if="!loading && !order" class="empty-state">
      <el-empty description="订单数据加载失败">
        <el-button type="primary" @click="$router.push('/user/orders')">返回订单列表</el-button>
      </el-empty>
    </div>

    <template v-if="order">
      <!-- 订单概览 -->
      <section class="review-section order-summary">
        <div class="summary-title">订单信息</div>
        <div class="summary-items">
          <div v-for="item in order.items" :key="item.id" class="summary-item">
            <el-image :src="item.productImage" fit="cover" class="item-img" />
            <span class="item-name">{{ item.productName }}</span>
            <span class="item-specs" v-if="item.specs">({{ item.specs }})</span>
            <span class="item-qty">x{{ item.quantity }}</span>
          </div>
        </div>
      </section>

      <!-- 评价表单 -->
      <el-form ref="formRef" :model="form" :rules="formRules" label-position="top" class="review-form">
        <!-- 总体评分 -->
        <section class="review-section score-section">
          <div class="section-title required">总体评分</div>
          <div class="score-area">
            <div
              v-for="star in 5"
              :key="star"
              class="score-star"
              :class="{ active: star <= form.overallScore, hover: star <= hoveredScore }"
              @mouseenter="hoveredScore = star"
              @mouseleave="hoveredScore = 0"
              @click="form.overallScore = star"
            >
              <el-icon :size="28"><StarFilled /></el-icon>
            </div>
            <span class="score-text">{{ scoreLabels[form.overallScore - 1] || '请评分' }}</span>
          </div>
        </section>

        <!-- 分项评分 -->
        <section class="review-section">
          <div class="section-title">分项评分</div>
          <div class="sub-scores">
            <div class="sub-score-row" v-for="cat in categories" :key="cat.key">
              <span class="sub-label">{{ cat.label }}</span>
              <el-rate
                v-model="(form.scores as any)[cat.key]"
                :colors="rateColors"
                show-text
                :texts="['很差', '较差', '一般', '较好', '很好']"
                size="small"
              />
            </div>
          </div>
        </section>

        <!-- 逐项商品评价 -->
        <section class="review-section">
          <div class="section-title">商品评价</div>
          <div class="item-reviews">
            <div v-for="(item, index) in form.items" :key="item.itemId" class="item-review-card">
              <div class="item-review-header">
                <el-image :src="item.image" fit="cover" class="item-review-img" />
                <span class="item-review-name">{{ item.name }}</span>
              </div>
              <el-form-item :label="`商品${index + 1} 评分`" :prop="`items.${index}.score`" :rules="[{ required: true, message: '请评分' }]">
                <el-rate v-model="item.score" :colors="rateColors" />
              </el-form-item>
              <el-form-item :label="`商品${index + 1} 评价内容`">
                <el-input
                  v-model="item.content"
                  type="textarea"
                  :rows="2"
                  placeholder="分享您的使用体验..."
                  maxlength="500"
                  show-word-limit
                />
              </el-form-item>
              <el-form-item label="图片">
                <el-upload
                  v-model:file-list="item.imageList"
                  action="#"
                  list-type="picture-card"
                  :auto-upload="false"
                  :limit="5"
                  accept="image/*"
                >
                  <el-icon><Plus /></el-icon>
                </el-upload>
              </el-form-item>
            </div>
          </div>
        </section>

        <!-- 匿名选项 -->
        <section class="review-section">
          <div class="section-title">其他设置</div>
          <div class="option-row">
            <el-checkbox v-model="form.anonymous">匿名评价</el-checkbox>
            <span class="option-tip">勾选后其他用户将看不到您的昵称</span>
          </div>
        </section>

        <!-- 提交 -->
        <div class="submit-area">
          <el-button size="large" type="primary" round :loading="submitting" @click="handleSubmit" style="width: 200px;">
            提交评价
          </el-button>
        </div>
      </el-form>
    </template>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage, ElMessageBox, type FormInstance, type UploadFile } from 'element-plus'
import { ArrowLeft, StarFilled, Plus } from '@element-plus/icons-vue'
import * as orderApi from '@user/api/order'

const route = useRoute()
const router = useRouter()

const orderId = route.params.id as string
const loading = ref(false)
const submitting = ref(false)
const formRef = ref<FormInstance>()
const order = ref<any>(null)

const hoveredScore = ref(0)

const rateColors = ['#ff4757', '#ff6b35', '#f39c12', '#27ae60', '#00b894']
const scoreLabels = ['非常差', '比较差', '一般般', '比较满意', '非常满意']

const categories = [
  { key: 'quality', label: '商品质量' },
  { key: 'logistics', label: '物流速度' },
  { key: 'service', label: '服务态度' }
]

const form = reactive({
  overallScore: 0,
  scores: {
    quality: 5,
    logistics: 5,
    service: 5,
  },
  items: [] as {
    itemId: number
    name: string
    image: string
    score: number
    content: string
    imageList: UploadFile[]
  }[],
  anonymous: false
})

const formRules = {
  overallScore: [{ required: true, message: '请选择总体评分', trigger: 'change' }],
}

// 加载订单
const loadOrder = async () => {
  loading.value = true
  try {
    const detail = await orderApi.getOrderDetail(orderId)
    order.value = detail
    // 初始化商品评价项
    form.items = (detail.items || []).map((item: any) => ({
      itemId: item.productId || item.id,
      name: item.productName,
      image: item.productImage,
      score: 0,
      content: '',
      imageList: []
    }))
  } catch (error) {
    console.error('加载订单失败:', error)
  } finally {
    loading.value = false
  }
}

// 提交评价
const handleSubmit = async () => {
  if (!formRef.value) return

  // 校验总体评分
  if (form.overallScore === 0) {
    ElMessage.warning('请选择总体评分')
    return
  }

  // 校验表单并提交
  await validateAndSubmit()
}

const validateAndSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
  } catch {
    // 部分校验失败也允许提交，只要总体评分有值即可
  }

  submitting.value = true
  try {
    // 构造提交数据
    const reviewItems = form.items.map(item => ({
      itemId: item.itemId,
      score: item.score || form.overallScore,
      content: item.content || undefined,
      images: item.imageList?.length ? item.imageList.filter(f => f.raw).map(f => URL.createObjectURL(f.raw)) : undefined
    }))

    await orderApi.submitReview(orderId, {
      score: form.overallScore,
      content: `【总体】${scoreLabels[form.overallScore - 1]}` +
        ` | 质量：${scoreLabels[form.scores.quality - 1] || '-'}` +
        ` | 物流：${scoreLabels[form.scores.logistics - 1] || '-'}` +
        ` | 服务：${scoreLabels[form.scores.service - 1] || '-'}`,
      images: [],
      items: reviewItems
    })

    ElMessage.success('评价提交成功！感谢您的反馈')
    router.push(`/user/orders/${orderId}`)
  } catch (error: any) {
    ElMessage.error(error?.message || '评价提交失败')
  } finally {
    submitting.value = false
  }
}

onMounted(loadOrder)
</script>

<style scoped>
.order-review {
  max-width: 860px;
  margin: 0 auto;
  padding: 20px;
}

.review-header {
  display: flex; align-items: center; gap: 16px; margin-bottom: 20px;
}
.review-header h2 {
  font-size: 22px; font-weight: bold; color: #fff; margin: 0;
}

.empty-state { text-align: center; padding: 80px 20px; background: var(--mall-bg-card); border-radius: 12px; }

.review-section {
  background: var(--mall-bg-card);
  border: 1px solid var(--mall-border);
  border-radius: 12px;
  padding: 24px;
  margin-bottom: 16px;
}

.section-title {
  font-size: 16px; font-weight: bold; color: #fff;
  margin-bottom: 16px;
}
.section-title.required::before {
  content: '*'; color: #ff4757; margin-right: 4px;
}

/* 订单摘要 */
.summary-items {
  display: flex; flex-wrap: wrap; gap: 10px;
}
.summary-item {
  display: flex; align-items: center; gap: 8px;
  background: rgba(255,255,255,0.04); padding: 8px 14px;
  border-radius: 20px; font-size: 13px;
}
.item-img { width: 36px; height: 36px; border-radius: 6px; }
.item-name { max-width: 140px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; color: #ddd; }
.item-specs, .item-qty { color: var(--mall-text-muted); }

/* 评分区域 */
.score-area {
  display: flex; align-items: center; gap: 12px;
}
.score-star {
  cursor: pointer; transition: transform 0.15s; color: rgba(255,255,255,0.15);
  display: inline-flex;
}
.score-star.active, .score-star.hover { color: #ffc107; transform: scale(1.15); }
.score-star:hover { transform: scale(1.25); }
.score-text { font-size: 16px; color: var(--mall-primary); font-weight: 500; min-width: 80px; }

/* 分项评分 */
.sub-scores { display: flex; flex-direction: column; gap: 14px; }
.sub-score-row {
  display: flex; align-items: center; justify-content: space-between;
  padding: 8px 0; border-bottom: 1px solid rgba(255,255,255,0.04);
}
.sub-label { font-size: 14px; color: var(--mall-text-secondary); min-width: 80px; }

/* 商品评价 */
.item-reviews { display: flex; flex-direction: column; gap: 16px; }
.item-review-card {
  background: rgba(255,255,255,0.03);
  border: 1px solid rgba(255,255,255,0.06);
  border-radius: 10px;
  padding: 18px;
}
.item-review-header {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 14px;
}
.item-review-img { width: 48px; height: 48px; border-radius: 8px; }
.item-review-name { font-size: 14px; font-weight: 500; color: #eee; }

.option-row { display: flex; align-items: center; gap: 10px; }
.option-tip { font-size: 12px; color: var(--mall-text-muted); }

.submit-area { text-align: center; padding: 30px 0 10px; }
</style>
