import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@merchant/views/Dashboard.vue'),
    meta: { title: '首页看板' }
  },
  {
    path: '/dashboard',
    name: 'DashboardPage',
    component: () => import('@merchant/views/Dashboard.vue'),
    meta: { title: '首页看板' }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('@merchant/views/product/ProductList.vue'),
    meta: { title: '商品管理' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@merchant/views/order/OrderList.vue'),
    meta: { title: '订单管理' }
  },
  {
    path: '/shop',
    name: 'Shop',
    component: () => import('@merchant/views/shop/ShopInfo.vue'),
    meta: { title: '店铺管理' }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@merchant/views/stats/Statistics.vue'),
    meta: { title: '数据统计' }
  },
  {
    path: '/reviews',
    name: 'Reviews',
    component: () => import('@merchant/views/Dashboard.vue'),
    meta: { title: '评价管理' }
  },
  {
    path: '/coupon',
    name: 'Coupon',
    component: () => import('@merchant/views/Dashboard.vue'),
    meta: { title: '优惠券' }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@merchant/views/customer/Chat.vue'),
    meta: { title: '消息中心' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@merchant/views/shop/ShopInfo.vue'),
    meta: { title: '店铺设置' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 市场平台商家端`
  }
  next()
})

export default router
