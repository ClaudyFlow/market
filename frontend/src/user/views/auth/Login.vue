<template>
  <div class="auth-page">
    <div class="auth-container">
      <div class="auth-card">
        <el-tabs v-model="activeTab" class="auth-tabs">
          <el-tab-pane label="登录" name="login"></el-tab-pane>
          <el-tab-pane label="注册" name="register"></el-tab-pane>
        </el-tabs>
        
        <!-- 登录表单 -->
        <el-form v-if="activeTab === 'login'" :model="loginForm" :rules="loginRules" ref="loginFormRef" class="auth-form">
          <el-form-item prop="name">
            <el-input
              v-model="loginForm.name"
              placeholder="用户名/邮箱"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="loginForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              size="large"
              show-password
              @keyup.enter="handleLogin"
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleLogin"
              class="submit-btn"
            >
              {{ loading ? '登录中...' : '登录' }}
            </el-button>
          </el-form-item>
        </el-form>
        
        <!-- 注册表单 -->
        <el-form v-else :model="registerForm" :rules="registerRules" ref="registerFormRef" class="auth-form">
          <el-form-item prop="name">
            <el-input
              v-model="registerForm.name"
              placeholder="用户名"
              prefix-icon="User"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="email">
            <el-input
              v-model="registerForm.email"
              placeholder="邮箱(可选)"
              prefix-icon="Message"
              size="large"
            />
          </el-form-item>
          
          <el-form-item prop="password">
            <el-input
              v-model="registerForm.password"
              type="password"
              placeholder="密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          
          <el-form-item prop="confirmPassword">
            <el-input
              v-model="registerForm.confirmPassword"
              type="password"
              placeholder="确认密码"
              prefix-icon="Lock"
              size="large"
              show-password
            />
          </el-form-item>
          
          <el-form-item>
            <el-button
              type="primary"
              size="large"
              :loading="loading"
              @click="handleRegister"
              class="submit-btn"
            >
              {{ loading ? '注册中...' : '注册' }}
            </el-button>
          </el-form-item>

          <el-form-item>
            <div class="agreement-tip">
              <el-checkbox v-model="agreeAgreement" />
              <span>登录/注册即表示同意</span>
              <router-link to="/agreement" target="_blank" class="link">《用户协议》</router-link>
              <span>和</span>
              <router-link to="/privacy" target="_blank" class="link">《隐私政策》</router-link>
            </div>
          </el-form-item>
        </el-form>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { login, register } from '@/user/api/auth'

const router = useRouter()
const route = useRoute()
const activeTab = ref('login')
const loginFormRef = ref()
const registerFormRef = ref()
const loading = ref(false)
const agreeAgreement = ref(true)

// 监听路由参数,如果是 /register 则自动切换到注册标签
if (route.query.tab === 'register' || route.path === '/register') {
  activeTab.value = 'register'
}

const loginForm = reactive({
  name: '',
  password: ''
})

const registerForm = reactive({
  name: '',
  email: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: string, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const loginRules = {
  name: [
    { required: true, message: '请输入用户名或邮箱', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ]
}

const registerRules = {
  name: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度 3-20 位', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码长度至少 6 位', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
}

const handleLogin = async () => {
  if (!loginFormRef.value) return

  await loginFormRef.value.validate(async (valid: boolean) => {
    if (!valid) return

    loading.value = true
    try {
      const res = await login({
        name: loginForm.name,
        password: loginForm.password
      })

      console.log('登录响应:', res)

      if (res.success) {
        localStorage.setItem('token', res.token)
        localStorage.setItem('user', JSON.stringify(res.user))
        ElMessage.success('登录成功')
        window.dispatchEvent(new Event('storage'))

        const redirect = route.query.redirect as string
        router.push(redirect || '/')
      } else {
        ElMessage.error(res.message || '登录失败')
      }
    } catch (error: any) {
      console.error('登录错误:', error)
      ElMessage.error(error.response?.data?.message || error.message || '登录失败,请检查网络')
    } finally {
      loading.value = false
    }
  })
}

const handleRegister = async () => {
  if (!registerFormRef.value) return

  await registerFormRef.value.validate(async (valid: boolean) => {
    if (!valid) {
      console.error('表单验证失败')
      return
    }

    loading.value = true
    try {
      // 前端验证密码一致性
      if (registerForm.password !== registerForm.confirmPassword) {
        ElMessage.error('两次输入的密码不一致')
        return
      }

      // 发送包含 confirmPassword 的字段到后端
      const payload = {
        name: registerForm.name,
        password: registerForm.password,
        email: registerForm.email,
        confirmPassword: registerForm.confirmPassword
      }
      console.log('注册请求数据:', payload)

      const res = await register(payload)
      console.log('注册响应:', res)

      if (res.success) {
        localStorage.setItem('token', res.token)
        localStorage.setItem('user', JSON.stringify(res.user))
        ElMessage.success('注册并登录成功')
        window.dispatchEvent(new Event('storage'))
        router.push('/')
      } else {
        ElMessage.error(res.message || '注册失败')
      }
    } catch (error: any) {
      console.error('注册错误详情:', error)
      console.error('错误响应:', error.response)
      const errorMsg = error.response?.data?.message || error.message || '注册失败,请检查网络'
      ElMessage.error(errorMsg)
    } finally {
      loading.value = false
    }
  })
}
</script>

<style scoped>
.auth-page {
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, rgba(0,212,255,0.1) 0%, rgba(10,14,26,0.9) 100%);
}

.auth-container {
  width: 100%;
  max-width: 420px;
  padding: 20px;
}

.auth-card {
  background: rgba(26,31,58,0.9);
  border: 1px solid rgba(0,212,255,0.2);
  border-radius: 16px;
  padding: 40px;
  box-shadow: 0 8px 32px rgba(0,0,0,0.3);
}

.auth-tabs {
  
}

.auth-tabs :deep(.el-tabs__header) {
  display: flex;
  justify-content: center;
  
}

.auth-tabs :deep(.el-tabs__item) {
  padding: 10px 30px;
  font-size: 16px;
  font-weight: 500;
  color: #888;
}

.auth-tabs :deep(.el-tabs__item.is-active) {
  color: var(--mall-primary);
}

.auth-tabs :deep(.el-tabs__active-bar) {
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
}

.auth-form {
  
}

.submit-btn {
  width: 100%;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  font-weight: 600;
  font-size: 16px;
  
}

.submit-btn:hover {
  box-shadow: 0 0 20px rgba(0,212,255,0.5);
}

.agreement-tip {
  font-size: 13px;
  color: #6b7280;
  text-align: center;
  width: 100%;
}

.agreement-tip .link {
  color: #00d4ff;
  text-decoration: none;
  margin: 0 4px;
}

.agreement-tip .link:hover {
  text-decoration: underline;
}
</style>
