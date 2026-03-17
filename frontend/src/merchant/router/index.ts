import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
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
    path: '/product',
    name: 'Product',
    component: () => import('@merchant/views/product/ProductList.vue'),
    meta: { title: '商品管理' }
  },
  {
    path: '/order',
    name: 'Order',
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
    path: '/statistic',
    name: 'Statistic',
    component: () => import('@merchant/views/stats/Statistics.vue'),
    meta: { title: '数据统计' }
  },
  {
    path: '/review',
    name: 'Review',
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
    path: '/setting',
    name: 'Setting',
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
