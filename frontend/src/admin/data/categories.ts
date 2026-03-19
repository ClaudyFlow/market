import type { Component } from 'vue'
import {
  DataBoard,
  User,
  Shop,
  Goods,
  List,
  ChatDotRound,
  DataAnalysis,
  ChatLineSquare,
  Setting,
  Monitor,
  ShoppingCart,
  TrendCharts,
  Tools,
  DocumentChecked,
  Bell,
  Money,
  Star
} from '@element-plus/icons-vue'

export interface 分类项 {
  名称:string
  路径:string
  图标:Component
}

export interface 轮播图项 {
  标题:string
  副标题:string
  渐变:string
  链接:string
}

export interface 快捷操作项 {
  图标:Component
  名称:string
  路径:string
}

export interface 状态选项 {
  标签:string
  值:string
  颜色?: string
}

export const 分类列表:分类项 [] = [
  {
    名称:'平台概览',
    路径:'/admin/dashboard',
    图标:DataBoard
  },
  {
    名称:'用户管理',
    路径:'/admin/user',
    图标:User
  },
  {
    名称:'商家管理',
    路径:'/admin/merchant',
    图标:Shop
  },
  {
    名称:'商品审核',
    路径:'/admin/product',
    图标:Goods
  },
  {
    名称:'订单监控',
    路径:'/admin/order',
    图标:List
  },
  {
    名称:'评价审核',
    路径:'/admin/review',
    图标:ChatDotRound
  },
  {
    名称:'数据统计',
    路径:'/admin/statistic',
    图标:DataAnalysis
  },
  {
    名称:'消息中心',
    路径:'/admin/chat',
    图标:ChatLineSquare
  },
  {
    名称:'平台设置',
    路径:'/admin/setting',
    图标:Setting
  }
]

export const 轮播图列表:轮播图项 [] = [
  {
    标题:'平台运营报告',
    副标题:'数据驱动决策',
    渐变:'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    链接:'/admin/statistic'
  },
  {
    标题:'商家审核加速',
    副标题:'提升入驻效率',
    渐变:'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    链接:'/admin/merchant'
  },
  {
    标题:'系统升级通知',
    副标题:'功能持续优化',
    渐变:'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    链接:'/admin/setting'
  }
]

export const 审核状态选项:状态选项 [] = [
  { 标签:'全部商品', 值:'' },
  { 标签:'待审核', 值:'pending', 颜色:'#ffaa00' },
  { 标签:'已通过', 值:'approved', 颜色:'#00ff88' },
  { 标签:'已拒绝', 值:'rejected', 颜色:'#ff6666' }
]

export const 商家状态选项:状态选项 [] = [
  { 标签:'全部商家', 值:'' },
  { 标签:'待审核', 值:'pending', 颜色:'#ffaa00' },
  { 标签:'已通过', 值:'approved', 颜色:'#00ff88' },
  { 标签:'已拒绝', 值:'rejected', 颜色:'#ff6666' },
  { 标签:'已封禁', 值:'banned', 颜色:'#ff4444' }
]

export const 快捷操作列表:快捷操作项 [] = [
  { 图标:User, 名称:'用户管理', 路径:'/admin/user' },
  { 图标:Shop, 名称:'商家管理', 路径:'/admin/merchant' },
  { 图标:Goods, 名称:'商品审核', 路径:'/admin/product' },
  { 图标:List, 名称:'订单监控', 路径:'/admin/order' },
  { 图标:TrendCharts, 名称:'数据统计', 路径:'/admin/statistic' },
  { 图标:Monitor, 名称:'系统监控', 路径:'/admin/monitor' },
  { 图标:ShoppingCart, 名称:'营销管理', 路径:'/admin/marketing' },
  { 图标:Tools, 名称:'平台设置', 路径:'/admin/setting' }
]

// 导出兼容旧代码的变量
export const categories = 分类列表
export const banners = 轮播图列表
export const auditStatusOptions = 审核状态选项
export const merchantStatusOptions = 商家状态选项
export const quickActions = 快捷操作列表
