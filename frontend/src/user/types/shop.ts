/**
 * ShopHome 相关类型定义
 */

export interface ShopInfo {
  id: number
  name: string
  banner: string
  logo: string
  rating: number
  followers: number
  productCount: number
  positiveRate: number
  openYears: number
  certified: boolean
  tags: string[]
  announcement: string
  coupons: Coupon[]
}

export interface Coupon {
  id: number
  name: string
  amount: number
  condition: number
  description: string
  validUntil: string
  received: boolean
}

export interface Category {
  id: number
  name: string
  icon: string
  count: number
}

export interface Product {
  id: number
  name: string
  description: string
  price: number
  originalPrice?: number
  sales: number
  stock: number
  image: string
}

export interface SortOption {
  label: string
  value: string
}
