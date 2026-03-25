import {
  Grid,
  Monitor,
  ShoppingBag,
  House,
  Timer,
  MagicStick,
  Food,
  Notebook,
  Van,
  Star,
  Stamp,
  Trophy
} from '@element-plus/icons-vue'

export const categories = [
  {
    name: '全部商品',
    path: '/item',
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
    name: '美妆护肤',
    path: '/beauty',
    icon: MagicStick
  },
  {
    name: '食品生鲜',
    path: '/food',
    icon: Food
  },
  {
    name: '图书文具',
    path: '/books',
    icon: Notebook
  },
  {
    name: '母婴用品',
    path: '/baby',
    icon: Van
  },
  {
    name: '运动户外',
    path: '/sports',
    icon: Trophy
  },
  {
    name: '珠宝首饰',
    path: '/jewelry',
    icon: Stamp
  },
  {
    name: '限时特惠',
    path: '/sale',
    icon: Timer
  },
  {
    name: '新品上市',
    path: '/new',
    icon: Star
  },
  {
    name: '热销爆款',
    path: '/hot',
    icon: Trophy
  }
]

// 轮播图配置 - 太空科幻风格深色背景
export const banners = [
  {
    title: '双十一狂欢节',
    subtitle: '全场低至 5 折',
    gradient: 'linear-gradient(135deg, #0a0e27 0%, #1a1f3a 50%, #2a1a5a 100%)'
  },
  {
    title: '新品首发',
    subtitle: '春季新品上市',
    gradient: 'linear-gradient(135deg, #0d1a2a 0%, #1a3a5a 50%, #0a2a4a 100%)'
  },
  {
    title: '限时秒杀',
    subtitle: '爆款 1 折起',
    gradient: 'linear-gradient(135deg, #1a0a2a 0%, #3a1a5a 50%, #2a0a4a 100%)'
  },
  {
    title: '会员专享',
    subtitle: 'VIP 专属优惠',
    gradient: 'linear-gradient(135deg, #0a1a2a 0%, #1a3a4a 50%, #0a2a3a 100%)'
  },
  {
    title: '品质好物',
    subtitle: '精选品牌推荐',
    gradient: 'linear-gradient(135deg, #1a1a2a 0%, #3a3a5a 50%, #2a2a4a 100%)'
  }
]
