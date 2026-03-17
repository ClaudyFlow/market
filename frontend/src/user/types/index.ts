// 用户相关类型定义

export interface UserInfo {
  id: number
  username: string
  email?: string
  phone?: string
  avatar?: string
  createdAt?: string
}

export interface VipInfo {
  level: number
  expireTime: string | null
  benefits: string[]
}

export interface UserCredit {
  credit: number
  hasCheckedIn: boolean
}
