import type { Component } from 'vue'
import {
  HomeFilled,
  Goods,
  List,
  Shop,
  DataAnalysis,
  ChatDotRound,
  Ticket,
  ChatLineSquare,
  Setting,
  ShoppingCart,
  Box,
  TrendCharts,
  User,
  Money,
  Star,
  Timer
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
    名称:'首页看板',
    路径:'/',
    图标:HomeFilled
  },
  {
    名称:'商品管理',
    路径:'/product',
    图标:Goods
  },
  {
    名称:'订单管理',
    路径:'/order',
    图标:List
  },
  {
    名称:'店铺管理',
    路径:'/shop',
    图标:Shop
  },
  {
    名称:'数据统计',
    路径:'/statistic',
    图标:DataAnalysis
  },
  {
    名称:'评价管理',
    路径:'/review',
    图标:ChatDotRound
  },
  {
    名称:'优惠券',
    路径:'/coupon',
    图标:Ticket
  },
  {
    名称:'消息中心',
    路径:'/chat',
    图标:ChatLineSquare
  },
  {
    名称:'店铺设置',
    路径:'/setting',
    图标:Setting
  }
]

export const 轮播图列表:轮播图项 [] = [
  {
    标题:'商家入驻季',
    副标题:'新商家专属优惠',
    渐变:'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
    链接:'/shop'
  },
  {
    标题:'流量扶持',
    副标题:'平台流量倾斜',
    渐变:'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)',
    链接:'/statistic'
  },
  {
    标题:'营销工具',
    副标题:'智能营销助手',
    渐变:'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)',
    链接:'/coupon'
  }
]

export const 订单状态选项:状态选项 [] = [
  { 标签:'全部订单', 值:'' },
  { 标签:'待付款', 值:'pending', 颜色:'#ffaa00' },
  { 标签:'待发货', 值:'paid', 颜色:'#00d4ff' },
  { 标签:'已发货', 值:'shipped', 颜色:'#00ff88' },
  { 标签:'已完成', 值:'completed', 颜色:'#00cc6a' },
  { 标签:'已取消', 值:'cancelled', 颜色:'#ff6666' },
  { 标签:'退款中', 值:'refunding', 颜色:'#ff8800' }
]

export const 商品状态选项:状态选项 [] = [
  { 标签:'全部商品', 值:'' },
  { 标签:'在售', 值:'active', 颜色:'#00ff88' },
  { 标签:'下架', 值:'inactive', 颜色:'#ff6666' },
  { 标签:'售罄', 值:'sold_out', 颜色:'#ffaa00' }
]

export const 快捷操作列表:快捷操作项 [] = [
  { 图标:ShoppingCart, 名称:'发布商品', 路径:'/product/edit' },
  { 图标:Goods, 名称:'商品管理', 路径:'/product' },
  { 图标:List, 名称:'订单管理', 路径:'/order' },
  { 图标:TrendCharts, 名称:'数据统计', 路径:'/statistic' },
  { 图标:User, 名称:'客户管理', 路径:'/customer' },
  { 图标:Ticket, 名称:'优惠券', 路径:'/coupon' }
]

// 导出兼容旧代码的变量
export const categories = 分类列表
export const banners = 轮播图列表
export const orderStatusOptions = 订单状态选项
export const productStatusOptions = 商品状态选项
export const quickActions = 快捷操作列表
