<template>
  <div class="review-form-with-images">
    <h3>发表评价</h3>
    
    <el-form :model="form" label-width="80px">
      <el-form-item label="评分">
        <el-rate v-model="form.rating" :colors="['#99A9BF', '#F7BA2A', '#FF9900']" />
      </el-form-item>
      
      <el-form-item label="内容">
        <el-input
          v-model="form.content"
          type="textarea"
          :rows="4"
          placeholder="分享你的使用体验..."
        />
      </el-form-item>
      
      <el-form-item label="图片">
        <div class="image-list">
          <ImageUploader
            v-for="(image, index) in form.images"
            :key="index"
            v-model="form.images[index]"
            :placeholder-text="'点击上传图片' + (index + 1)"
          />
          
          <!-- 添加图片按钮 -->
          <div 
            class="add-image-btn" 
            @click="addImage"
            v-if="form.images.length < maxImages"
          >
            <el-icon :size="30"><Plus /></el-icon>
            <span>添加图片</span>
          </div>
        </div>
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" @click="submitReview" :loading="submitting">
          提交评价
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { Plus } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'
import ImageUploader from '@user/components/ImageUploader.vue'

const maxImages = 5
const submitting = ref(false)

const form = reactive({
  rating: 5,
  content: '',
  images: ref<string[]>([])
})

// 添加图片
const addImage = () => {
  if (form.images.length < maxImages) {
    form.images.push('')
  }
}

// 提交评价
const submitReview = async () => {
  if (!form.content.trim()) {
    ElMessage.warning('请填写评价内容')
    return
  }

  submitting.value = true

  try {
    // 发送到后端
    const response = await fetch('http://localhost:8080/api/review', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({
        rating: form.rating,
        content: form.content,
        images: form.images.filter(img => img !== '') // 过滤空值
      })
    })

    const result = await response.json()
    
    if (result.code === 200) {
      ElMessage.success('评价提交成功')
      // 重置表单
      form.rating = 5
      form.content = ''
      form.images = []
    } else {
      ElMessage.error(result.message || '提交失败')
    }
  } catch (error) {
    ElMessage.error('提交失败，请重试')
  } finally {
    submitting.value = false
  }
}
</script>

<style scoped>
.review-form-with-images {
  padding: 20px;
  background: rgba(26, 31, 58, 0.5);
  border-radius: 12px;
  border: 1px solid rgba(0, 212, 255, 0.2);
}

.review-form-with-images h3 {
  color: #fff;
  margin-bottom: 20px;
}

.image-list {
  display: flex;
  flex-wrap: wrap;
  gap: 16px;
}

.add-image-btn {
  width: 200px;
  height: 200px;
  border: 2px dashed var(--mall-border-light);
  border-radius: 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  cursor: pointer;
  transition: all 0.3s;
  color: var(--mall-text-secondary);
  background: rgba(26, 31, 58, 0.5);
}

.add-image-btn:hover {
  border-color: var(--mall-primary);
  color: var(--mall-primary);
  background: rgba(0, 212, 255, 0.1);
}
</style>
