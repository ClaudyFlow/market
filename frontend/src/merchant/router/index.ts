import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'MerchantLogin',
    component: () => import('@merchant/views/login/Login.vue'),
    meta: { title: '商家登录' }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@merchant/views/Dashboard.vue'),
    meta: { title: '首页看板', requiresAuth: true }
  },
  {
    path: '/product',
    name: 'Product',
    component: () => import('@merchant/views/product/ProductList.vue'),
    meta: { title: '商品管理', requiresAuth: true }
  },
  {
    path: '/product/list',
    name: 'ProductList',
    component: () => import('@merchant/views/product/ProductList.vue'),
    meta: { title: '商品列表', requiresAuth: true }
  },
  {
    path: '/product/add',
    name: 'ProductAdd',
    component: () => import('@merchant/views/product/ProductEdit.vue'),
    meta: { title: '添加商品', requiresAuth: true }
  },
  {
    path: '/product/edit/:id?',
    name: 'ProductEdit',
    component: () => import('@merchant/views/product/ProductEdit.vue'),
    meta: { title: '商品编辑', requiresAuth: true }
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@merchant/views/order/OrderList.vue'),
    meta: { title: '订单管理', requiresAuth: true }
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('@merchant/views/order/OrderList.vue'),
    meta: { title: '订单列表', requiresAuth: true }
  },
  {
    path: '/order/refund',
    name: 'OrderRefund',
    component: () => import('@merchant/views/order/OrderList.vue'),
    meta: { title: '退款处理', requiresAuth: true }
  },
  {
    path: '/shop',
    name: 'Shop',
    component: () => import('@merchant/views/shop/ShopInfo.vue'),
    meta: { title: '店铺管理', requiresAuth: true }
  },
  {
    path: '/shop/info',
    name: 'ShopInfo',
    component: () => import('@merchant/views/shop/ShopInfo.vue'),
    meta: { title: '店铺信息', requiresAuth: true }
  },
  {
    path: '/statistic',
    name: 'Statistic',
    component: () => import('@merchant/views/stats/Statistics.vue'),
    meta: { title: '数据统计', requiresAuth: true }
  },
  {
    path: '/stats',
    name: 'Stats',
    component: () => import('@merchant/views/stats/Statistics.vue'),
    meta: { title: '数据统计', requiresAuth: true }
  },
  {
    path: '/review',
    name: 'Review',
    component: () => import('@merchant/views/review/ReviewList.vue'),
    meta: { title: '评价管理', requiresAuth: true }
  },
  {
    path: '/review/list',
    name: 'ReviewList',
    component: () => import('@merchant/views/review/ReviewList.vue'),
    meta: { title: '评价列表', requiresAuth: true }
  },
  {
    path: '/activity',
    name: 'Activity',
    component: () => import('@merchant/views/activity/ActivityList.vue'),
    meta: { title: '我的活动', requiresAuth: true }
  },
  {
    path: '/activity/list',
    name: 'ActivityList',
    component: () => import('@merchant/views/activity/ActivityList.vue'),
    meta: { title: '活动列表', requiresAuth: true }
  },
  {
    path: '/platform-activity',
    name: 'PlatformActivity',
    component: () => import('@merchant/views/activity/PlatformActivitySettings.vue'),
    meta: { title: '平台活动', requiresAuth: true }
  },
  {
    path: '/coupon',
    name: 'Coupon',
    component: () => import('@merchant/views/coupon/CouponList.vue'),
    meta: { title: '优惠券', requiresAuth: true }
  },
  {
    path: '/coupon/list',
    name: 'CouponList',
    component: () => import('@merchant/views/coupon/CouponList.vue'),
    meta: { title: '优惠券列表', requiresAuth: true }
  },
  {
    path: '/coupon/add',
    name: 'CouponAdd',
    component: () => import('@merchant/views/coupon/CouponList.vue'),
    meta: { title: '添加优惠券', requiresAuth: true }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@merchant/views/customer/Chat.vue'),
    meta: { title: '消息中心', requiresAuth: true }
  },
  {
    path: '/customer/chat',
    name: 'CustomerChat',
    component: () => import('@merchant/views/customer/Chat.vue'),
    meta: { title: '客服聊天', requiresAuth: true }
  },
  {
    path: '/setting',
    name: 'Setting',
    component: () => import('@merchant/views/shop/ShopInfo.vue'),
    meta: { title: '店铺设置', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory('/'),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 市场平台商家端`
  }

  // 检查是否需要登录（已临时禁用）
  // if (to.meta.requiresAuth) {
  //   const token = localStorage.getItem('merchant_token')
  //   if (!token) {
  //     next('/login')
  //     return
  //   }
  // }

  // 已登录时访问登录页，重定向到主页
  if (to.path === '/login') {
    next('/')
    return
  }

  next()
})

export default router
