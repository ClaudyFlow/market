<template>
  <div class="login-container">
    <div class="login-background">
      <div class="bg-circle circle-1"></div>
      <div class="bg-circle circle-2"></div>
      <div class="bg-circle circle-3"></div>
    </div>

    <div class="login-card">
      <div class="login-header">
        <div class="logo-wrapper">
          <div class="logo-icon">
            <el-icon><Monitor /></el-icon>
          </div>
          <h1 class="logo-title">市场平台管理端</h1>
          <p class="logo-subtitle">智慧运营 · 高效治理</p>
        </div>
      </div>

      <el-form
        ref="formRef"
        :model="loginForm"
        :rules="formRules"
        class="login-form"
        @keyup.enter="handleLogin"
      >
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="请输入用户名"
            size="large"
            clearable
          >
            <template #prefix>
              <el-icon><User /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            placeholder="请输入密码"
            size="large"
            show-password
          >
            <template #prefix>
              <el-icon><Lock /></el-icon>
            </template>
          </el-input>
        </el-form-item>

        <el-form-item>
          <div class="form-options">
            <el-checkbox v-model="loginForm.remember">记住密码</el-checkbox>
            <el-link type="primary" :underline="false">忘记密码？</el-link>
          </div>
        </el-form-item>

        <el-form-item>
          <el-button
            type="primary"
            size="large"
            class="login-btn"
            :loading="loading"
            @click="handleLogin"
          >
            {{ loading ? '登录中...' : '立即登录' }}
          </el-button>
        </el-form-item>
      </el-form>

      <div class="login-footer">
        <p class="footer-text">
          <el-icon><Service /></el-icon>
          技术支持：market-support@example.com
        </p>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { Monitor, User, Lock, Service } from '@element-plus/icons-vue'

interface LoginForm {
  username: string
  password: string
  remember: boolean
}

const router = useRouter()
const formRef = ref<FormInstance>()
const loading = ref(false)

const loginForm = reactive<LoginForm>({
  username: '',
  password: '',
  remember: false
})

const formRules: FormRules = {
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度在 3-20 个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度不能少于 6 位', trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!formRef.value) return

  await formRef.value.validate(async (valid) => {
    if (!valid) return

    loading.value = true

    try {
      // 模拟登录请求
      await new Promise(resolve => setTimeout(resolve, 1000))

      // 演示账号：admin / admin123
      if (loginForm.username === 'admin' && loginForm.password === 'admin123') {
        const token = 'admin_token_' + Date.now()
        localStorage.setItem('admin_token', token)
        localStorage.setItem('adminInfo', JSON.stringify({
          '姓名': '系统管理员',
          '角色': '超级管理员',
          '头像': ''
        }))

        ElMessage.success('登录成功')
        router.push('/admin/dashboard')
      } else {
        ElMessage.error('用户名或密码错误')
      }
    } catch (error) {
      ElMessage.error('登录失败，请稍后重试')
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: var(--mall-bg-dark);
  position: relative;
  overflow: hidden;
}

.login-background {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow: hidden;
}

.bg-circle {
  position: absolute;
  border-radius: 50%;
  background: radial-gradient(circle, rgba(0, 212, 255, 0.15) 0%, transparent 70%);
  animation: float 20s infinite ease-in-out;
}

.circle-1 {
  width: 600px;
  height: 600px;
  top: -200px;
  left: -200px;
}

.circle-2 {
  width: 400px;
  height: 400px;
  bottom: -100px;
  right: -100px;
  animation-delay: -5s;
}

.circle-3 {
  width: 300px;
  height: 300px;
  top: 50%;
  right: 10%;
  animation-delay: -10s;
}

@keyframes float {
  0%, 100% {
    transform: translate(0, 0) scale(1);
  }
  33% {
    transform: translate(30px, -30px) scale(1.1);
  }
  66% {
    transform: translate(-20px, 20px) scale(0.9);
  }
}

.login-card {
  width: 440px;
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 20px;
  padding: 40px;
  box-shadow: 0 0 60px rgba(0, 212, 255, 0.2);
  backdrop-filter: blur(10px);
  z-index: 1;
}

.login-header {
  text-align: center;
  margin-bottom: 40px;
}

.logo-wrapper {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
}

.logo-icon {
  width: 70px;
  height: 70px;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.5);
}

.logo-icon .el-icon {
  font-size: 36px;
  color: #fff;
}

.logo-title {
  font-size: 26px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  margin: 0;
}

.logo-subtitle {
  font-size: 13px;
  color: #888;
  letter-spacing: 2px;
  margin: 0;
}

.login-form {
  margin-top: 30px;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 24px;
}

.login-form :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 10px;
  padding: 12px 15px;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.4);
}

.login-form :deep(.el-input__wrapper.is-focus) {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.3);
}

.login-form :deep(.el-input__inner) {
  color: #fff;
  font-size: 15px;
}

.login-form :deep(.el-input__prefix) {
  color: var(--mall-primary);
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}

.login-btn {
  width: 100%;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border: none;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.4);
  font-size: 16px;
  font-weight: bold;
  padding: 14px;
  transition: all 0.3s;
}

.login-btn:hover {
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

.login-footer {
  margin-top: 30px;
  text-align: center;
}

.footer-text {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  font-size: 13px;
  color: #666;
}

.footer-text .el-icon {
  color: var(--mall-primary);
}
</style>
