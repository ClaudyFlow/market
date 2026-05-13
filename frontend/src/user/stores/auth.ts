/**
 * 认证状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User } from '@user/types/user'
import * as authApi from '@user/api/auth'
import { useUserStore } from './user'

export const useAuthStore = defineStore('auth', () => {
  // 状态
  const token = ref<string | null>(localStorage.getItem('token'))
  const refreshToken = ref<string | null>(localStorage.getItem('refreshToken'))
  const isAuthenticated = computed(() => !!token.value)

  // 登录
  async function login(credentials: {
    username?: string
    phone?: string
    email?: string
    password: string
    captcha?: string
    captchaKey?: string
  }) {
    try {
      const result = await authApi.login(credentials)
      setToken(result.accessToken, result.refreshToken)
      
      // 获取用户信息
      const userStore = useUserStore()
      await userStore.fetchUserInfo()
      
      return result
    } catch (error) {
      console.error('登录失败:', error)
      throw error
    }
  }

  // 手机号登录
  async function loginByPhone(phone: string, code: string) {
    try {
      const result = await authApi.loginByPhone(phone, code)
      setToken(result.accessToken, result.refreshToken)
      
      const userStore = useUserStore()
      await userStore.fetchUserInfo()
      
      return result
    } catch (error) {
      console.error('手机号登录失败:', error)
      throw error
    }
  }

  // 第三方登录
  async function loginByThirdParty(provider: 'wechat' | 'qq' | 'weibo', code: string) {
    try {
      const result = await authApi.loginByThirdParty(provider, code)
      setToken(result.accessToken, result.refreshToken)
      
      const userStore = useUserStore()
      await userStore.fetchUserInfo()
      
      return result
    } catch (error) {
      console.error('第三方登录失败:', error)
      throw error
    }
  }

  // 注册
  async function register(data: {
    username: string
    password: string
    confirmPassword: string
    phone?: string
    phoneCode?: string
    email?: string
    emailCode?: string
    inviteCode?: string
  }) {
    try {
      const result = await authApi.register(data)
      setToken(result.accessToken, result.refreshToken)
      
      const userStore = useUserStore()
      await userStore.fetchUserInfo()
      
      return result
    } catch (error) {
      console.error('注册失败:', error)
      throw error
    }
  }

  // 退出登录
  async function logout() {
    try {
      await authApi.logout()
    } catch (error) {
      console.error('退出登录失败:', error)
    } finally {
      clearToken()
      const userStore = useUserStore()
      userStore.logout()
    }
  }

  // 发送验证码
  async function sendCaptcha(target: string, type: 'phone' | 'email', scene?: 'register' | 'login' | 'reset') {
    try {
      await authApi.sendCaptcha(target, type, scene)
    } catch (error) {
      console.error('发送验证码失败:', error)
      throw error
    }
  }

  // 重置密码
  async function resetPassword(data: {
    phone?: string
    email?: string
    code: string
    newPassword: string
    confirmPassword: string
  }) {
    try {
      await authApi.resetPassword(data)
    } catch (error) {
      console.error('重置密码失败:', error)
      throw error
    }
  }

  // 修改密码
  async function changePassword(oldPassword: string, newPassword: string) {
    try {
      await authApi.changePassword(oldPassword, newPassword)
    } catch (error) {
      console.error('修改密码失败:', error)
      throw error
    }
  }

  // 刷新 token
  async function refresh() {
    if (!refreshToken.value) {
      throw new Error('refreshToken 不存在')
    }
    
    try {
      const result = await authApi.refreshToken(refreshToken.value)
      setToken(result.accessToken, result.refreshToken)
      return result
    } catch (error) {
      console.error('刷新 token 失败:', error)
      clearToken()
      throw error
    }
  }

  // 验证 token
  async function validateToken() {
    try {
      const result = await authApi.validateToken()
      return result.valid
    } catch (error) {
      console.error('验证 token 失败:', error)
      return false
    }
  }

  // 设置 token
  function setToken(newToken: string, newRefreshToken?: string) {
    token.value = newToken
    localStorage.setItem('token', newToken)
    
    if (newRefreshToken) {
      refreshToken.value = newRefreshToken
      localStorage.setItem('refreshToken', newRefreshToken)
    }
  }

  // 清除 token
  function clearToken() {
    token.value = null
    refreshToken.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('refreshToken')
  }

  // 获取 token
  function getToken(): string | null {
    return token.value
  }

  // 检查是否已登录
  function checkLogin(): boolean {
    return !!token.value
  }

  // 需要登录时的处理
  function requireLogin(): boolean {
    if (!token.value) {
      // 可以跳转到登录页或显示登录弹窗
      window.location.href = '/login'
      return false
    }
    return true
  }

  return {
    // 状态
    token,
    refreshToken,
    isAuthenticated,
    // 方法
    login,
    loginByPhone,
    loginByThirdParty,
    register,
    logout,
    sendCaptcha,
    resetPassword,
    changePassword,
    refresh,
    validateToken,
    setToken,
    clearToken,
    getToken,
    checkLogin,
    requireLogin
  }
})
