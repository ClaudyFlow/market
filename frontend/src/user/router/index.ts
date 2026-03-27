import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@user/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/item',
    name: 'Item',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '全部商品' }
  },
  {
    path: '/digital',
    name: 'Digital',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '数码电器' }
  },
  {
    path: '/fashion',
    name: 'Fashion',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '服饰鞋包' }
  },
  {
    path: '/home',
    name: 'HomeGoods',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '家居家装' }
  },
  {
    path: '/beauty',
    name: 'Beauty',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '美妆护肤' }
  },
  {
    path: '/food',
    name: 'Food',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '食品生鲜' }
  },
  {
    path: '/books',
    name: 'Books',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '图书文具' }
  },
  {
    path: '/baby',
    name: 'Baby',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '母婴用品' }
  },
  {
    path: '/sports',
    name: 'Sports',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '运动户外' }
  },
  {
    path: '/jewelry',
    name: 'Jewelry',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '珠宝首饰' }
  },
  {
    path: '/sale',
    name: 'Sale',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '限时特惠' }
  },
  {
    path: '/new',
    name: 'New',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '新品上市' }
  },
  {
    path: '/hot',
    name: 'Hot',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '热销爆款' }
  },
  {
    path: '/item/:id',
    name: 'ItemDetail',
    component: () => import('@user/views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@user/views/Cart.vue'),
    meta: { title: '购物车' }
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@user/views/Order.vue'),
    meta: { title: '订单确认' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@user/views/auth/Login.vue'),
    meta: { title: '登录/注册' }
  },
  {
    path: '/register',
    name: 'Register',
    redirect: '/login?tab=register',
    meta: { title: '注册' }
  },
  
  {
    path: '/user',
    name: 'UserLayout', // 名字改为 UserLayout
    // 修改1:组件指向刚才新建的 UserLayout.vue
    component: () => import('@user/views/user/UserLayout.vue'), 
    meta: { title: '用户中心' },
    children: [
      {
        path: '', // 修改2:空路径，代表访问 /user 时默认显示 UserCenter
        name: 'UserCenter',
        component: () => import('@user/views/user/UserCenter.vue'),
        meta: { title: '个人中心' }
      },
      {
        path: 'order',
        name: 'UserOrder',
        component: () => import('@user/views/user/Orders.vue'),
        meta: { title: '我的订单' }
      },
      {
        path: 'address',
        name: 'UserAddress',
        component: () => import('@user/views/user/Address.vue'),
        meta: { title: '地址管理' }
      },
      {
        path: 'credit',
        name: 'UserCredit',
        component: () => import('@user/views/user/Credit.vue'),
        meta: { title: '我的积分' }
      },
      {
        path: 'favorite',
        name: 'UserFavorite',
        component: () => import('@user/views/user/Favorites.vue'),
        meta: { title: '我的收藏' }
      },
      {
        path: 'follow',
        name: 'UserFollow',
        component: () => import('@user/views/user/Follows.vue'),
        meta: { title: '我的关注' }
      }
    ]
  },

  {
    path: '/lottery',
    name: 'Lottery',
    component: () => import('@user/views/Lottery.vue'),
    meta: { title: '幸运抽奖' }
  },
  {
    path: '/vip',
    name: 'VipCenter',
    component: () => import('@user/views/VipCenter.vue'),
    meta: { title: 'VIP 会员中心' }
  },
  {
    path: '/forum',
    name: 'Forum',
    component: () => import('@user/views/Forum.vue'),
    meta: { title: '用户论坛' }
  },
  {
    path: '/forum/:id',
    name: 'ForumDetail',
    component: () => import('@user/views/Forum.vue'),
    meta: { title: '话题详情' }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@user/views/CustomerService.vue'),
    meta: { title: '在线客服' }
  },
  {
    path: '/faq',
    name: 'FAQ',
    component: () => import('@user/views/CustomerService.vue'),
    meta: { title: '常见问题' }
  },
  {
    path: '/service',
    name: 'CustomerService',
    component: () => import('@user/views/CustomerService.vue'),
    meta: { title: '客服中心' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 购物商城系统`
  }
  next()
})

export default router
