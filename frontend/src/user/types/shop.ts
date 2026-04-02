/**
 * 店铺相关类型定义
 */

import type { ImageInfo } from './common'

// 店铺信息
export interface Shop {
  id: number
  name: string
  logo?: string
  banner?: string
  description?: string
  rating: number
  followers: number
  productCount: number
  status: 'active' | 'inactive' | 'closed'
  certified: boolean
  tags?: string[]
  createTime: string
}

// 店铺详情
export interface ShopDetail extends Shop {
  slogan?: string
  positiveRate: number
  openYears: number
  announcement?: string
  businessLicense?: string
  location?: string
  serviceScores: ServiceScores
  coupons?: ShopCoupon[]
}

// 服务评分
export interface ServiceScores {
  description: number
  service: number
  logistics: number
}

// 店铺统计
export interface ShopStats {
  totalProducts: number
  totalSales: number
  totalOrders: number
  totalRevenue: number
  newProducts: number
  hotProducts: number
}

// 店铺商品
export interface ShopProduct {
  id: number
  name: string
  image: string
  price: number
  originalPrice?: number
  sales: number
  stock: number
  status: 'onsale' | 'offsale'
  tags?: string[]
  specs?: string[]
  discount?: number
}

// 店铺优惠券
export interface ShopCoupon {
  id: number
  name: string
  amount: number
  condition: number
  description: string
  validFrom: string
  validUntil: string
  received: boolean
  stock?: number
  remainingStock?: number
}

// 店铺分类
export interface ShopCategory {
  id: number
  name: string
  parentId?: number
  productCount: number
  icon?: string
}

// 店铺认证信息
export interface ShopCertification {
  type: 'enterprise' | 'individual' | 'brand'
  status: 'pending' | 'approved' | 'rejected'
  name: string
  licenseNumber: string
  legalPerson?: string
  verifiedTime?: string
}

// 店铺动态
export interface ShopActivity {
  id: number
  type: 'product' | 'promotion' | 'notice'
  title: string
  content: string
  image?: string
  createTime: string
}
