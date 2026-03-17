import { 
  Grid, 
  Monitor, 
  ShoppingBag, 
  House, 
  Timer 
} from '@element-plus/icons-vue'

export const categories = [
  {
    name: '全部商品',
    path: '/items',
    icon: Grid
  },
  {
    name: '数码电器',
    path: '/digital',
    icon: Monitor
  },
  {
    name: '服饰鞋包',
    path: '/fashion',
    icon: ShoppingBag
  },
  {
    name: '家居家装',
    path: '/home',
    icon: House
  },
  {
    name: '限时特惠',
    path: '/sale',
    icon: Timer
  }
]

export const banners = [
  {
    title: '双十一狂欢节',
    subtitle: '全场低至5折',
    gradient: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  },
  {
    title: '新品首发',
    subtitle: '春季新品上市',
    gradient: 'linear-gradient(135deg, #f093fb 0%, #f5576c 100%)'
  },
  {
    title: '限时秒杀',
    subtitle: '爆款1折起',
    gradient: 'linear-gradient(135deg, #4facfe 0%, #00f2fe 100%)'
  },
  {
    title: '会员专享',
    subtitle: 'VIP专属优惠',
    gradient: 'linear-gradient(135deg, #43e97b 0%, #38f9d7 100%)'
  },
  {
    title: '品质好物',
    subtitle: '精选品牌推荐',
    gradient: 'linear-gradient(135deg, #fa709a 0%, #fee140 100%)'
  }
]