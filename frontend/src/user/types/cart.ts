/**
 * 购物车相关类型定义
 */

import type { Product, ProductSku } from './product'

// 购物车
export interface Cart {
  id: number
  userId: number
  items: CartItem[]
  selectedItems: CartItem[]
  totalAmount: number
  totalCount: number
  updateTime: string
}

// 购物车项
export interface CartItem {
  id: number
  productId: number
  skuId?: number
  quantity: number
  selected: boolean
  product: CartProduct
  sku?: CartSku
  checked?: boolean
  invalid?: boolean
  reason?: string
}

// 购物车商品信息
export interface CartProduct {
  id: number
  name: string
  image: string
  price: number
  originalPrice?: number
  status: 'onsale' | 'offsale' | 'deleted'
  shopId?: number
  shopName?: string
}

// 购物车 SKU 信息
export interface CartSku {
  id: number
  specs: { specName: string; specValue: string }[]
  price: number
  stock: number
  image?: string
}

// 购物车失效商品
export interface InvalidItem {
  itemId: number
  reason: 'sold_out' | 'deleted' | 'price_change' | 'stock不足'
  productName: string
}

// 购物车推荐
export interface CartRecommendation {
  id: number
  name: string
  image: string
  price: number
  reason: 'frequently_bought_together' | 'similar' | 'complementary'
}
