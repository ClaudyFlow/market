/**
 * 用户状态管理
 */

import { defineStore } from 'pinia'
import { ref, computed } from 'vue'
import type { User, UserProfile, UserSettings, VipInfo, SigninInfo } from '@user/types'
import * as userApi from '@user/api/user'
import * as authApi from '@user/api/auth'

export const useUserStore = defineStore('user', () => {
  // 状态
  const user = ref<User | null>(null)
  const userProfile = ref<UserProfile | null>(null)
  const settings = ref<UserSettings | null>(null)
  const vipInfo = ref<VipInfo | null>(null)
  const signinInfo = ref<SigninInfo | null>(null)

  // 计算属性
  const isLoggedIn = computed(() => !!user.value)
  const userId = computed(() => user.value?.id)
  const username = computed(() => user.value?.username || '')
  const nickname = computed(() => user.value?.nickname || user.value?.username || '未命名')
  const avatar = computed(() => user.value?.avatar || '/images/avatar-default.png')
  const isVip = computed(() => vipInfo.value?.isVip || false)
  const vipLevel = computed(() => vipInfo.value?.level || 0)
  const points = computed(() => userProfile.value?.points || 0)
  const level = computed(() => userProfile.value?.level || 1)

  // 获取用户信息
  async function fetchUserInfo() {
    try {
      const data = await userApi.getCurrentUser()
      user.value = data
      return data
    } catch (error) {
      console.error('获取用户信息失败:', error)
      throw error
    }
  }

  // 获取用户详情
  async function fetchUserProfile(userId?: number) {
    try {
      const id = userId || user.value?.id
      if (!id) throw new Error('用户 ID 不存在')
      const data = await userApi.getUserDetail(id)
      userProfile.value = data
      return data
    } catch (error) {
      console.error('获取用户详情失败:', error)
      throw error
    }
  }

  // 更新用户信息
  async function updateUserInfo(data: Partial<User>) {
    try {
      const updated = await userApi.updateUserInfo(data)
      user.value = updated
      return updated
    } catch (error) {
      console.error('更新用户信息失败:', error)
      throw error
    }
  }

  // 更新头像
  async function updateAvatar(file: File) {
    try {
      const result = await userApi.updateAvatar(file)
      if (user.value) {
        user.value.avatar = result.avatar
      }
      return result.avatar
    } catch (error) {
      console.error('更新头像失败:', error)
      throw error
    }
  }

  // 获取用户设置
  async function fetchSettings() {
    try {
      const data = await userApi.getSettings()
      settings.value = data
      return data
    } catch (error) {
      console.error('获取用户设置失败:', error)
      throw error
    }
  }

  // 更新用户设置
  async function updateSettings(data: Partial<UserSettings>) {
    try {
      const updated = await userApi.updateSettings(data)
      settings.value = updated
      return updated
    } catch (error) {
      console.error('更新用户设置失败:', error)
      throw error
    }
  }

  // 获取 VIP 信息
  async function fetchVipInfo() {
    try {
      const data = await userApi.getLevel()
      vipInfo.value = {
        level: data.level,
        name: data.name,
        experience: data.experience,
        nextLevelExperience: data.nextLevel,
        progress: data.progress,
        isVip: data.level > 1,
        benefits: []
      } as VipInfo
      return vipInfo.value
    } catch (error) {
      console.error('获取 VIP 信息失败:', error)
      throw error
    }
  }

  // 获取签到信息
  async function fetchSigninInfo() {
    try {
      const data = await userApi.getSigninStatus()
      signinInfo.value = {
        signed: data.signed,
        consecutiveDays: data.consecutiveDays,
        totalSigninDays: 0,
        lastSigninTime: undefined
      } as SigninInfo
      return signinInfo.value
    } catch (error) {
      console.error('获取签到信息失败:', error)
      throw error
    }
  }

  // 签到
  async function doSignin() {
    try {
      const result = await userApi.dailySignin()
      if (signinInfo.value) {
        signinInfo.value.signed = true
        signinInfo.value.consecutiveDays = result.consecutiveDays
      }
      if (userProfile.value) {
        userProfile.value.points = (userProfile.value.points || 0) + result.points
      }
      return result
    } catch (error) {
      console.error('签到失败:', error)
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

  // 退出登录
  function logout() {
    user.value = null
    userProfile.value = null
    settings.value = null
    vipInfo.value = null
    signinInfo.value = null
    localStorage.removeItem('token')
    localStorage.removeItem('userInfo')
  }

  // 设置用户信息（登录后）
  function setUserInfo(userData: User) {
    user.value = userData
    localStorage.setItem('userInfo', JSON.stringify(userData))
  }

  // 从本地存储加载用户信息
  function loadFromStorage() {
    try {
      const stored = localStorage.getItem('userInfo')
      if (stored) {
        user.value = JSON.parse(stored)
      }
    } catch (error) {
      console.error('加载本地用户信息失败:', error)
    }
  }

  return {
    // 状态
    user,
    userProfile,
    settings,
    vipInfo,
    signinInfo,
    // 计算属性
    isLoggedIn,
    userId,
    username,
    nickname,
    avatar,
    isVip,
    vipLevel,
    points,
    level,
    // 方法
    fetchUserInfo,
    fetchUserProfile,
    updateUserInfo,
    updateAvatar,
    fetchSettings,
    updateSettings,
    fetchVipInfo,
    fetchSigninInfo,
    doSignin,
    changePassword,
    logout,
    setUserInfo,
    loadFromStorage
  }
})
