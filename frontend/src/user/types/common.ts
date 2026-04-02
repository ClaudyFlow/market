/**
 * 通用类型定义
 */

// 通用响应结构
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
  timestamp?: number
  traceId?: string
}

// 分页数据
export interface PageData<T> {
  records: T[]
  total: number
  size: number
  current: number
  pages: number
}

// 分页参数
export interface PageParams {
  current?: number
  size?: number
  [key: string]: any
}

// 性别枚举
export type Gender = 'male' | 'female' | 'unknown'

// 状态枚举
export type Status = 'active' | 'inactive' | 'deleted' | 'pending' | 'approved' | 'rejected'

// 时间范围
export interface DateRange {
  startDate: string
  endDate: string
}

// 坐标位置
export interface Location {
  latitude: number
  longitude: number
}

// 地址信息
export interface Address {
  province: string
  city: string
  district: string
  street?: string
  detail: string
  fullAddress?: string
}

// 图片信息
export interface ImageInfo {
  url: string
  thumbnail?: string
  width?: number
  height?: number
}

// 价格范围
export interface PriceRange {
  min: number
  max: number
}

// 排序选项
export interface SortOption {
  field: string
  order: 'asc' | 'desc'
}
