/**
 * 商品相关类型定义
 */

import type { ImageInfo, PriceRange } from './common'

// 商品信息
export interface Product {
  id: number
  name: string
  description?: string
  price: number
  originalPrice?: number
  discount?: number
  image?: string
  images?: ImageInfo[]
  stock: number
  sales: number
  status: 'onsale' | 'offsale' | 'pending'
  categoryId?: number
  categoryName?: string
  brandId?: number
  brandName?: string
  shopId?: number
  shopName?: string
  tags?: string[]
  createTime: string
  updateTime?: string
}

// 商品详情
export interface ProductDetail extends Product {
  detailHtml?: string
  detailText?: string
  specs?: ProductSpec[]
  skus?: ProductSku[]
  attributes?: ProductAttribute[]
  shipping?: ShippingInfo
  service?: ServiceInfo[]
  reviews?: ReviewSummary
}

// 商品规格
export interface ProductSpec {
  name: string
  value: string
  unit?: string
}

// 商品 SKU
export interface ProductSku {
  id: number
  productId: number
  specs: { specName: string; specValue: string }[]
  price: number
  originalPrice?: number
  stock: number
  image?: string
  skuCode?: string
}

// 商品属性
export interface ProductAttribute {
  id: number
  name: string
  value: string
  searchable: boolean
  filterable: boolean
}

// 商品图片
export interface ProductImage extends ImageInfo {
  type: 'main' | 'detail' | 'sku'
  sortOrder: number
}

// 物流信息
export interface ShippingInfo {
  freeShipping: boolean
  shippingFee: number
  shippingFrom: string
  deliveryTime: string
  shippingMethods: ShippingMethod[]
}

// 物流方式
export interface ShippingMethod {
  name: string
  price: number
  estimatedDays: string
}

// 服务信息
export interface ServiceInfo {
  type: 'return' | 'exchange' | 'warranty' | 'install'
  name: string
  description: string
  icon: string
}

// 评价汇总
export interface ReviewSummary {
  totalCount: number
  averageScore: number
  goodCount: number
  normalCount: number
  badCount: number
  withImageCount: number
  tags: { tag: string; count: number }[]
}

// 商品评价
export interface ProductComment {
  id: number
  productId: number
  userId: number
  userName: string
  userAvatar?: string
  score: number
  content?: string
  images?: ImageInfo[]
  specs?: string
  createTime: string
  reply?: {
    content: string
    replyTime: string
  }
  useful: number
}

// 商品分类
export interface ProductCategory {
  id: number
  name: string
  parentId?: number
  icon?: string
  level: number
  sortOrder: number
  children?: ProductCategory[]
}

// 商品品牌
export interface ProductBrand {
  id: number
  name: string
  logo?: string
  description?: string
  productCount?: number
}

// 库存信息
export interface StockInfo {
  productId: number
  skuId?: number
  stock: number
  availableStock: number
  reservedStock: number
}
