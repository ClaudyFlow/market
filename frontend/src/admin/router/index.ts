import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/',
    name: 'Dashboard',
    component: () => import('@admin/views/Dashboard.vue'),
    meta: { title: '平台概览' }
  },
  {
    path: '/dashboard',
    name: 'DashboardPage',
    component: () => import('@admin/views/Dashboard.vue'),
    meta: { title: '平台概览' }
  },
  {
    path: '/users',
    name: 'Users',
    component: () => import('@admin/views/user/UserList.vue'),
    meta: { title: '用户管理' }
  },
  {
    path: '/merchants',
    name: 'Merchants',
    component: () => import('@admin/views/merchant/MerchantList.vue'),
    meta: { title: '商家管理' }
  },
  {
    path: '/products',
    name: 'Products',
    component: () => import('@admin/views/product/ProductAudit.vue'),
    meta: { title: '商品审核' }
  },
  {
    path: '/orders',
    name: 'Orders',
    component: () => import('@admin/views/order/OrderMonitor.vue'),
    meta: { title: '订单监控' }
  },
  {
    path: '/reviews',
    name: 'Reviews',
    component: () => import('@admin/views/content/ReviewAudit.vue'),
    meta: { title: '评价审核' }
  },
  {
    path: '/statistics',
    name: 'Statistics',
    component: () => import('@admin/views/Dashboard.vue'),
    meta: { title: '数据统计' }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@admin/views/Dashboard.vue'),
    meta: { title: '消息中心' }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: () => import('@admin/views/system/Settings.vue'),
    meta: { title: '平台设置' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 市场平台管理端`
  }
  next()
})

export default router
