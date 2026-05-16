import { createRouter, createWebHistory, type RouteRecordRaw } from 'vue-router'

const routes: RouteRecordRaw[] = [
  {
    path: '/login',
    name: 'AdminLogin',
    component: () => import('@admin/views/login/Login.vue'),
    meta: { title: '管理员登录' }
  },
  {
    path: '/',
    name: 'Home',
    component: () => import('@admin/views/Dashboard.vue'),
    meta: { title: '平台概览', requiresAuth: true }
  },
  {
    path: '/user',
    name: 'User',
    component: () => import('@admin/views/user/UserList.vue'),
    meta: { title: '用户管理', requiresAuth: true }
  },
  {
    path: '/merchant',
    name: 'Merchant',
    component: () => import('@admin/views/merchant/MerchantList.vue'),
    meta: { title: '商家管理', requiresAuth: true }
  },
  {
    path: '/product',
    name: 'Product',
    component: () => import('@admin/views/product/ProductAudit.vue'),
    meta: { title: '商品审核', requiresAuth: true }
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@admin/views/order/OrderMonitor.vue'),
    meta: { title: '订单监控', requiresAuth: true }
  },
  {
    path: '/review',
    name: 'Review',
    component: () => import('@admin/views/content/ReviewAudit.vue'),
    meta: { title: '评价审核', requiresAuth: true }
  },
  {
    path: '/forum-audit',
    name: 'ForumAudit',
    component: () => import('@admin/views/content/ForumAudit.vue'),
    meta: { title: '论坛审核', requiresAuth: true }
  },
  {
    path: '/statistic',
    name: 'Statistic',
    component: () => import('@admin/views/system/Statistics.vue'),
    meta: { title: '数据统计', requiresAuth: true }
  },
  {
    path: '/chat',
    name: 'Chat',
    component: () => import('@admin/views/content/MessageCenter.vue'),
    meta: { title: '消息中心', requiresAuth: true }
  },
  {
    path: '/setting',
    name: 'Setting',
    component: () => import('@admin/views/system/Settings.vue'),
    meta: { title: '平台设置', requiresAuth: true }
  },
  {
    path: '/coupon',
    name: 'Coupon',
    component: () => import('@admin/views/marketing/Coupon.vue'),
    meta: { title: '优惠券管理', requiresAuth: true }
  },
  {
    path: '/category',
    name: 'Category',
    component: () => import('@admin/views/system/Category.vue'),
    meta: { title: '分类管理', requiresAuth: true }
  },
  {
    path: '/platform-activity',
    name: 'PlatformActivity',
    component: () => import('@admin/views/marketing/Activity.vue'),
    meta: { title: '平台活动', requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory('/admin'),
  routes
})

router.beforeEach((to, from, next) => {
  if (to.meta.title) {
    document.title = `${to.meta.title} - 市场平台管理端`
  }

  // 检查是否需要登录
  if (to.meta.requiresAuth) {
    const token = localStorage.getItem('admin_token')
    if (!token) {
      next('/login')
      return
    }
  }

  // 已登录时访问登录页，重定向到主页
  if (to.path === '/login') {
    const token = localStorage.getItem('admin_token')
    if (token) {
      next('/')
      return
    }
  }

  next()
})

export default router
