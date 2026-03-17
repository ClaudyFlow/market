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
  Tools
} from '@element-plus/icons-vue'

export const categories = [
  {
    name: '平台概览',
    path: '/',
    icon: DataBoard
  },
  {
    name: '用户管理',
    path: '/users',
    icon: User
  },
  {
    name: '商家管理',
    path: '/merchants',
    icon: Shop
  },
  {
    name: '商品审核',
    path: '/products',
    icon: Goods
  },
  {
    name: '订单监控',
    path: '/orders',
    icon: List
  },
  {
    name: '评价审核',
    path: '/reviews',
    icon: ChatDotRound
  },
  {
    name: '数据统计',
    path: '/statistics',
    icon: DataAnalysis
  },
  {
    name: '消息中心',
    path: '/chat',
    icon: ChatLineSquare
  },
  {
    name: '平台设置',
    path: '/settings',
    icon: Setting
  }
]

export const banners = [
  {
    title: '平台运营报告',
    subtitle: '数据驱动决策',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '商家审核加速',
    subtitle: '提升入驻效率',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '系统升级通知',
    subtitle: '功能持续优化',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  }
]

export const auditStatusOptions = [
  { label: '全部商品', value: '' },
  { label: '待审核', value: 'pending' },
  { label: '已通过', value: 'approved' },
  { label: '已拒绝', value: 'rejected' }
]

export const merchantStatusOptions = [
  { label: '全部商家', value: '' },
  { label: '待审核', value: 'pending' },
  { label: '已通过', value: 'approved' },
  { label: '已拒绝', value: 'rejected' },
  { label: '已封禁', value: 'banned' }
]

export const quickActions = [
  { icon: User, label: '用户管理', path: '/users' },
  { icon: Shop, label: '商家管理', path: '/merchants' },
  { icon: Goods, label: '商品审核', path: '/products' },
  { icon: List, label: '订单监控', path: '/orders' },
  { icon: TrendCharts, label: '数据统计', path: '/statistics' },
  { icon: Monitor, label: '系统监控', path: '/monitor' },
  { icon: ShoppingCart, label: '营销管理', path: '/marketing' },
  { icon: Tools, label: '平台设置', path: '/settings' }
]