import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/merchant/login',
    name: 'Login',
    component: () => import('@merchant/views/login/Login.vue'),
    meta: { title: '商家登录' }
  },
  {
    path: '/merchant',
    name: 'Dashboard',
    component: () => import('@merchant/views/Dashboard.vue'),
    meta: { title: '首页看板', requiresAuth: true }
  },
  {
    path: '/merchant/dashboard',
    name: 'DashboardPage',
    component: () => import('@merchant/views/Dashboard.vue'),
    meta: { title: '首页看板', requiresAuth: true }
  },
  {
    path: '/merchant/product',
    name: 'Product',
    component: () => import('@merchant/views/product/ProductList.vue'),
    meta: { title: '商品管理', requiresAuth: true }
  },
  {
    path: '/merchant/product/edit',
    name: 'ProductEdit',
    component: () => import('@merchant/views/product/ProductEdit.vue'),
    meta: { title: '编辑商品', requiresAuth: true }
  },
  {
    path: '/merchant/order',
    name: 'Order',
    component: () => import('@merchant/views/order/OrderList.vue'),
    meta: { title: '订单管理', requiresAuth: true }
  },
  {
    path: '/merchant/shop',
    name: 'Shop',
    component: () => import('@merchant/views/shop/ShopInfo.vue'),
    meta: { title: '店铺管理', requiresAuth: true }
  },
  {
    path: '/merchant/statistic',
    name: 'Statistic',
    component: () => import('@merchant/views/stats/Statistics.vue'),
    meta: { title: '数据统计', requiresAuth: true }
  },
  {
    path: '/merchant/review',
    name: 'Review',
    component: () => import('@merchant/views/review/ReviewList.vue'),
    meta: { title: '评价管理', requiresAuth: true }
  },
  {
    path: '/merchant/coupon',
    name: 'Coupon',
    component: () => import('@merchant/views/coupon/CouponList.vue'),
    meta: { title: '优惠券', requiresAuth: true }
  },
  {
    path: '/merchant/chat',
    name: 'Chat',
    component: () => import('@merchant/views/customer/Chat.vue'),
    meta: { title: '消息中心', requiresAuth: true }
  },
  {
    path: '/merchant/setting',
    name: 'Setting',
    component: () => import('@merchant/views/shop/ShopInfo.vue'),
    meta: { title: '店铺设置', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory('/merchant'),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 市场平台商家端`
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('merchant_token')
    if (!token && !to.path.includes('/login')) {
      next('/merchant/login')
      return
    }
  }

  // 已登录时访问登录页，重定向到首页
  if (to.path === '/merchant/login') {
    const token = localStorage.getItem('merchant_token')
    if (token) {
      next('/merchant')
      return
    }
  }

  next()
})

export default router
