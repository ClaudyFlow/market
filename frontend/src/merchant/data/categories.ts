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
  User
} from '@element-plus/icons-vue'

export const categories = [
  {
    name: '首页看板',
    path: '/',
    icon: HomeFilled
  },
  {
    name: '商品管理',
    path: '/products',
    icon: Goods
  },
  {
    name: '订单管理',
    path: '/orders',
    icon: List
  },
  {
    name: '店铺管理',
    path: '/shop',
    icon: Shop
  },
  {
    name: '数据统计',
    path: '/statistics',
    icon: DataAnalysis
  },
  {
    name: '评价管理',
    path: '/reviews',
    icon: ChatDotRound
  },
  {
    name: '优惠券',
    path: '/coupon',
    icon: Ticket
  },
  {
    name: '消息中心',
    path: '/chat',
    icon: ChatLineSquare
  },
  {
    name: '店铺设置',
    path: '/settings',
    icon: Setting
  }
]

export const banners = [
  {
    title: '商家入驻季',
    subtitle: '新商家专属优惠',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '流量扶持',
    subtitle: '平台流量倾斜',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '营销工具',
    subtitle: '智能营销助手',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  }
]

export const orderStatusOptions = [
  { label: '全部订单', value: '' },
  { label: '待付款', value: 'pending' },
  { label: '待发货', value: 'paid' },
  { label: '已发货', value: 'shipped' },
  { label: '已完成', value: 'completed' },
  { label: '已取消', value: 'cancelled' },
  { label: '退款中', value: 'refunding' }
]

export const productStatusOptions = [
  { label: '全部商品', value: '' },
  { label: '在售', value: 'active' },
  { label: '下架', value: 'inactive' },
  { label: '售罄', value: 'sold_out' }
]

export const quickActions = [
  { icon: ShoppingCart, label: '发布商品', path: '/products/add' },
  { icon: Box, label: '商品管理', path: '/products' },
  { icon: List, label: '订单管理', path: '/orders' },
  { icon: TrendCharts, label: '数据统计', path: '/statistics' },
  { icon: User, label: '客户管理', path: '/customers' },
  { icon: Ticket, label: '优惠券', path: '/coupon' }
]