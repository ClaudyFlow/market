/**
 * 用户相关类型定义
 */

import type { Gender, Status } from './common'

// 用户信息
export interface User {
  id: number
  username: string
  nickname?: string
  avatar?: string
  phone?: string
  email?: string
  gender?: Gender
  birthday?: string
  status: Status
  createTime: string
  updateTime?: string
}

// 用户详情
export interface UserProfile extends User {
  bio?: string
  location?: string
  level?: number
  experience?: number
  points?: number
  totalOrders?: number
  totalSpent?: number
  lastLoginTime?: string
  lastLoginIp?: string
}

// 用户设置
export interface UserSettings {
  notifyEmail: boolean
  notifySms: boolean
  notifyPush: boolean
  notifyActivity: boolean
  notifyOrder: boolean
  privacyShowPhone: boolean
  privacyShowEmail: boolean
}

// 认证令牌
export interface AuthToken {
  accessToken: string
  refreshToken: string
  expiresIn: number
  tokenType: 'Bearer'
  userId: number
}

// 登录参数
export interface LoginParams {
  username?: string
  phone?: string
  email?: string
  password: string
  captcha?: string
  captchaKey?: string
  rememberMe?: boolean
}

// 注册参数
export interface RegisterParams {
  username: string
  password: string
  confirmPassword: string
  phone?: string
  phoneCode?: string
  email?: string
  emailCode?: string
  inviteCode?: string
}

// 重置密码参数
export interface ResetPasswordParams {
  phone?: string
  email?: string
  code: string
  newPassword: string
  confirmPassword: string
}

// VIP 信息
export interface VipInfo {
  level: number
  name: string
  experience: number
  nextLevelExperience: number
  progress: number
  expireTime: string
  isVip: boolean
  benefits: VipBenefit[]
}

// VIP 特权
export interface VipBenefit {
  id: number
  name: string
  description: string
  icon: string
  enabled: boolean
}

// 用户等级
export interface UserLevel {
  level: number
  name: string
  icon: string
  minExperience: number
  maxExperience: number
  benefits: string[]
}

// 签到信息
export interface SigninInfo {
  signed: boolean
  consecutiveDays: number
  totalSigninDays: number
  lastSigninTime?: string
  todayReward?: {
    points: number
    experience: number
  }
}

// 地址信息
export interface UserAddress {
  id: number
  name: string
  phone: string
  province: string
  city: string
  district: string
  detail: string
  isDefault: boolean
  fullAddress?: string
}
