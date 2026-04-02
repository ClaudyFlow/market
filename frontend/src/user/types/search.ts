/**
 * 搜索相关类型定义
 */

// 搜索结果
export interface SearchResult {
  id: number
  type: 'product' | 'shop' | 'article' | 'user'
  name: string
  image?: string
  description?: string
  price?: number
  originalPrice?: number
  sales?: number
  rating?: number
  tags?: string[]
  highlight?: Record<string, string[]>
  extra?: Record<string, any>
}

// 搜索建议
export interface SearchSuggestion {
  keyword: string
  type: 'history' | 'hot' | 'recommend'
  icon?: string
  extra?: Record<string, any>
}

// 搜索历史
export interface SearchHistory {
  id: number
  keyword: string
  searchTime: string
  resultCount?: number
}

// 搜索筛选条件
export interface SearchFilters {
  categories: SearchCategory[]
  brands: SearchBrand[]
  priceRanges: PriceRangeFilter[]
  attributes: SearchAttribute[]
  sorts: SortOption[]
}

// 搜索分类
export interface SearchCategory {
  id: number
  name: string
  parentId?: number
  count: number
  selected: boolean
}

// 搜索品牌
export interface SearchBrand {
  id: number
  name: string
  logo?: string
  count: number
  selected: boolean
}

// 价格范围筛选
export interface PriceRangeFilter {
  min: number
  max: number
  label: string
  count: number
  selected: boolean
}

// 搜索属性
export interface SearchAttribute {
  id: number
  name: string
  values: AttributeValue[]
}

// 属性值
export interface AttributeValue {
  id: number
  value: string
  count: number
  selected: boolean
}

// 排序选项
export interface SortOption {
  value: string
  label: string
  selected: boolean
}

// 搜索参数
export interface SearchParams {
  keyword: string
  type?: 'all' | 'product' | 'shop' | 'article'
  categoryId?: number
  brandId?: number
  priceMin?: number
  priceMax?: number
  attributes?: number[]
  sort?: string
  page?: number
  size?: number
}
