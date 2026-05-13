<template>
  <div class="review-form-overlay" @click="handleOverlayClick">
    <div class="review-form-modal" @click.stop>
      <div class="review-form-header">
        <h3>发表评价</h3>
        <button class="close-btn" @click="emit('close')">×</button>
      </div>

      <div class="review-form-body">
        <!-- 商品信息 -->
        <div class="product-info" v-if="product">
          <img :src="product.image" :alt="product.name" class="product-image" />
          <div class="product-details">
            <span class="product-name">{{ product.name }}</span>
            <span class="product-price">¥{{ product.price }}</span>
          </div>
        </div>

        <!-- 评分 -->
        <div class="form-group">
          <label>评分</label>
          <RatingStars v-model="rating" size="large" />
          <span class="rating-text">{{ ratingText }}</span>
        </div>

        <!-- 评价内容 -->
        <div class="form-group">
          <label>评价内容</label>
          <textarea
            v-model="content"
            placeholder="分享您的使用体验..."
            maxlength="1000"
            rows="5"
          ></textarea>
          <span class="char-count">{{ content.length }}/1000</span>
        </div>
      </div>

      <div class="review-form-footer">
        <button class="cancel-btn" @click="emit('close')">取消</button>
        <button class="submit-btn" @click="handleSubmit" :disabled="isSubmitting">
          {{ isSubmitting ? '提交中...' : '提交评价' }}
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { addReview, updateReview, checkReview } from '@/user/api/review'
import RatingStars from './RatingStars.vue'

interface Product {
  id: number
  name: string
  price: number | string
  image?: string
}

const props = defineProps<{
  productId: number
  product?: Product
}>()

const emit = defineEmits<{
  close: []
  success: []
}>()

const rating = ref(5)
const content = ref('')
const isSubmitting = ref(false)
const hasReviewed = ref(false)

const ratingText = computed(() => {
  const texts = ['非常差', '较差', '一般', '满意', '非常满意']
  return texts[rating.value - 1] || ''
})

const checkIfReviewed = async () => {
  try {
    const res = await checkReview(props.productId)
    if (res.data.data?.hasReviewed) {
      hasReviewed.value = true
      rating.value = res.data.data.rating || 5
      content.value = res.data.data.content || ''
    }
  } catch (error) {
    console.error('检查评价状态失败:', error)
  }
}

const handleSubmit = async () => {
  if (content.value.trim().length === 0) {
    alert('请输入评价内容')
    return
  }

  isSubmitting.value = true
  try {
    if (hasReviewed.value) {
      await updateReview(props.productId, rating.value, content.value)
    } else {
      await addReview(props.productId, rating.value, content.value)
    }
    emit('success')
    emit('close')
  } catch (error: any) {
    alert(error.response?.data || '提交失败,请重试')
  } finally {
    isSubmitting.value = false
  }
}

const handleOverlayClick = () => {
  emit('close')
}

onMounted(() => {
  checkIfReviewed()
})
</script>

<style scoped>
.review-form-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.7);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  animation: fadeIn 0.2s ease;
}

.review-form-modal {
  background: #1a1a2e;
  border-radius: 12px;
  width: 90%;
  max-width: 500px;
  max-height: 90vh;
  overflow-y: auto;
  animation: slideUp 0.3s ease;
}

.review-form-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.1);
}

.review-form-header h3 {
  
  color: #fff;
  font-size: 18px;
}

.close-btn {
  background: none;
  border: none;
  color: #888;
  font-size: 24px;
  cursor: pointer;
  padding: 0;
  width: 30px;
  height: 30px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: color 0.2s;
}

.close-btn:hover {
  color: #fff;
}

.review-form-body {
  padding: 20px;
}

.product-info {
  display: flex;
  gap: 12px;
  padding: 12px;
  background: rgba(0, 0, 0, 0.2);
  border-radius: 8px;
  
}

.product-image {
  width: 60px;
  height: 60px;
  border-radius: 6px;
  object-fit: cover;
}

.product-details {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.product-name {
  color: #fff;
  font-size: 14px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-price {
  color: var(--mall-accent);
  font-weight: 500;
}

.form-group {
  
}

.form-group label {
  display: block;
  color: #888;
  font-size: 14px;
  
}

.rating-text {
  
  color: var(--mall-accent);
  font-size: 14px;
}

textarea {
  width: 100%;
  padding: 12px;
  background: rgba(0, 0, 0, 0.3);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  color: #fff;
  font-size: 14px;
  resize: vertical;
  font-family: inherit;
  box-sizing: border-box;
}

textarea:focus {
  outline: none;
  border-color: var(--mall-accent);
}

.char-count {
  display: block;
  text-align: right;
  color: #666;
  font-size: 12px;
  
}

.review-form-footer {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.cancel-btn,
.submit-btn {
  padding: 10px 24px;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  transition: all 0.2s;
}

.cancel-btn {
  background: rgba(255, 255, 255, 0.1);
  border: none;
  color: #fff;
}

.cancel-btn:hover {
  background: rgba(255, 255, 255, 0.15);
}

.submit-btn {
  background: var(--mall-accent);
  border: none;
  color: #000;
  font-weight: 500;
}

.submit-btn:hover:not(:disabled) {
  opacity: 0.9;
}

.submit-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes slideUp {
  from {
    transform: translateY(20px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}
</style>
