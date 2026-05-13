/**
 * 收藏相关类型定义
 */

// 收藏信息
export interface Favorite {
  id: number
  type: 'product' | 'shop' | 'article'
  itemId: number
  itemName: string
  itemImage?: string
  itemDescription?: string
  itemPrice?: number
  groupId?: number
  groupName?: string
  createTime: string
  extra?: Record<string, any>
}

// 收藏分组
export interface FavoriteGroup {
  id: number
  name: string
  description?: string
  itemCount: number
  createTime: string
  updateTime?: string
}

// 收藏统计
export interface FavoriteStats {
  total: number
  productCount: number
  shopCount: number
  articleCount: number
  groupCount: number
}

// 收藏商品快照
export interface FavoriteProductSnapshot {
  id: number
  name: string
  image: string
  price: number
  originalPrice?: number
  status: 'onsale' | 'offsale' | 'deleted'
  priceChanged: boolean
  stockChanged: boolean
}
