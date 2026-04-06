<template>
  <section class="feedback-section">
    <h2><el-icon><EditPen /></el-icon> 意见反馈</h2>
    <div class="feedback-form">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="反馈类型">
          <el-select v-model="formData.type" placeholder="请选择类型" style="width: 100%">
            <el-option label="产品建议" value="suggestion" />
            <el-option label="功能问题" value="bug" />
            <el-option label="投诉建议" value="complaint" />
            <el-option label="其他" value="other" />
          </el-select>
        </el-form-item>
        <el-form-item label="联系方式">
          <el-input v-model="formData.contact" placeholder="手机/邮箱（选填）" />
        </el-form-item>
        <el-form-item label="反馈内容">
          <el-input
            v-model="formData.content"
            type="textarea"
            :rows="4"
            placeholder="请详细描述您的问题或建议"
            maxlength="1000"
            show-word-limit
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" :loading="loading">
            提交反馈
          </el-button>
        </el-form-item>
      </el-form>
    </div>
  </section>
</template>

<script setup lang="ts">
import { reactive, ref } from 'vue'
import { EditPen } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const emit = defineEmits<{
  submit: [data: any]
}>()

const formData = reactive({
  type: '',
  contact: '',
  content: ''
})

const loading = ref(false)

const handleSubmit = () => {
  if (!formData.type || !formData.content) {
    ElMessage.warning('请填写反馈类型和内容')
    return
  }
  loading.value = true
  emit('submit', { ...formData })
}

const reset = () => {
  formData.type = ''
  formData.contact = ''
  formData.content = ''
  loading.value = false
}

defineExpose({ reset })
</script>

<style scoped>
.feedback-section {
  padding: 24px;
  background: #fff;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.feedback-section h2 {
  display: flex;
  align-items: center;
  gap: 8px;
  margin: 0 0 20px;
  font-size: 18px;
  color: #1a1a1a;
}
</style>
