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