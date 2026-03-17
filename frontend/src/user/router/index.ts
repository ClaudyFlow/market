import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@user/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/items',
    name: 'Items',
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
    path: '/sale',
    name: 'Sale',
    component: () => import('@user/views/ProductList.vue'),
    meta: { title: '限时特惠' }
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
    name: 'UserCenter',
    component: () => import('@user/views/user/UserCenter.vue'),
    meta: { title: '用户中心' },
    children: [
      {
        path: 'orders',
        name: 'UserOrders',
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
        path: 'favorites',
        name: 'UserFavorites',
        component: () => import('@user/views/user/Favorites.vue'),
        meta: { title: '我的收藏' }
      },
      {
        path: 'follows',
        name: 'UserFollows',
        component: () => import('@user/views/user/Follows.vue'),
        meta: { title: '我的关注' }
      }
    ]
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
