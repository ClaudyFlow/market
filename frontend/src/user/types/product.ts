/**
 * 商品相关类型定义
 */

/**
 * 商品接口
 */
export interface Product {
  id: string
  name: string
  description: string
  price: number
  originalPrice: number
  discount: number
  stock: number
  category: string
  categoryId?: number
  brand: string
  activities?: ActivityInfo[]
  colors: string[]
  versions: string[]
  promotions: string[]
  specifications: Record<string, string>
  images: string[]
  detailImages: string[]
  detailText: string
  services: string[]
  sales: number
  rating: number
  reviewCount: number
}

/**
 * 评价用户信息
 */
export interface ReviewUser {
  name: string
  avatar: string
}

/**
 * 评价接口
 */
export interface Review {
  id: string
  user: ReviewUser
  rating: number
  content: string
  images: string[]
  specs: string
  createdAt: string
}

/**
 * 评价标签
 */
export interface ReviewTag {
  label: string
  count: number
}

/**
 * 选中的规格
 */
export interface SelectedSpecs {
  color: string
  version: string
}

/**
 * 购物车项
 */
export interface CartItem {
  id: string
  productId: string
  productName: string
  productImage: string
  price: number
  quantity: number
  specs: SelectedSpecs
  selected: boolean
}

/**
 * 商品规格选项
 */
export interface SpecOption {
  label: string
  value: string
  image?: string
  disabled?: boolean
}

/**
 * 商品规格组
 */
export interface SpecGroup {
  key: string
  label: string
  options: SpecOption[]
}

/**
 * 商品促销信息
 */
export interface Promotion {
  type: 'discount' | 'gift' | 'shipping' | 'points'
  label: string
  description: string
}

/**
 * 商品服务承诺
 */
export interface ServicePromise {
  icon: string
  label: string
  description: string
}

/**
 * 商品评价统计
 */
export interface ReviewStats {
  totalCount: number
  averageRating: number
  ratingDistribution: Record<number, number>
  tags: ReviewTag[]
  withImagesCount: number
  withAdditionalCount: number
}

/**
 * 平台活动信息
 */
export interface ActivityInfo {
  id: number
  name: string
  discountRate: number
  endTime?: string
}
