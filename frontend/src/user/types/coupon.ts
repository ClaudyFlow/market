/**
 * 优惠券相关类型定义
 */

// 优惠券
export interface Coupon {
  id: number
  name: string
  amount: number
  condition: number
  status: 'unused' | 'used' | 'expired'
  validFrom: string
  validUntil: string
  description?: string
  scope: 'all' | 'shop' | 'product' | 'category'
  scopeId?: number
  scopeName?: string
}

// 优惠券模板
export interface CouponTemplate {
  id: number
  name: string
  type: 'discount' | 'reduction'
  amount?: number
  discount?: number
  condition: number
  totalStock: number
  receivedStock: number
  remainingStock: number
  validType: 'date_range' | 'fixed_days' | 'relative'
  validFrom?: string
  validUntil?: string
  validDays?: number
  description?: string
  scope: 'all' | 'shop' | 'product' | 'category'
  scopeId?: number
  scopeName?: string
  perLimit?: number
  image?: string
}

// 优惠券使用条件
export interface CouponCondition {
  minAmount: number
  maxDiscount?: number
  applicableShops?: number[]
  applicableProducts?: number[]
  applicableCategories?: number[]
  excludedProducts?: number[]
}

// 优惠券统计
export interface CouponStats {
  total: number
  unused: number
  used: number
  expired: number
  totalAmount: number
  availableAmount: number
}
