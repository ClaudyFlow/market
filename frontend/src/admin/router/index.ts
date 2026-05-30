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
    path: '/user/list',
    name: 'UserList',
    component: () => import('@admin/views/user/UserList.vue'),
    meta: { title: '用户列表', requiresAuth: true }
  },
  {
    path: '/user/credit',
    name: 'UserCredit',
    component: () => import('@admin/views/user/UserList.vue'),
    meta: { title: '用户积分', requiresAuth: true }
  },
  {
    path: '/merchant',
    name: 'Merchant',
    component: () => import('@admin/views/merchant/MerchantList.vue'),
    meta: { title: '商家管理', requiresAuth: true }
  },
  {
    path: '/merchant/list',
    name: 'MerchantList',
    component: () => import('@admin/views/merchant/MerchantList.vue'),
    meta: { title: '商家列表', requiresAuth: true }
  },
  {
    path: '/merchant/audit',
    name: 'MerchantAudit',
    component: () => import('@admin/views/merchant/MerchantList.vue'),
    meta: { title: '入驻审核', requiresAuth: true }
  },
  {
    path: '/product',
    name: 'Product',
    component: () => import('@admin/views/product/ProductAudit.vue'),
    meta: { title: '商品审核', requiresAuth: true }
  },
  {
    path: '/product/list',
    name: 'ProductList',
    component: () => import('@admin/views/product/ProductAudit.vue'),
    meta: { title: '商品列表', requiresAuth: true }
  },
  {
    path: '/product/audit',
    name: 'ProductAudit',
    component: () => import('@admin/views/product/ProductAudit.vue'),
    meta: { title: '商品审核', requiresAuth: true }
  },
  {
    path: '/product/category',
    name: 'ProductCategory',
    component: () => import('@admin/views/system/Category.vue'),
    meta: { title: '分类管理', requiresAuth: true }
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@admin/views/order/OrderMonitor.vue'),
    meta: { title: '订单监控', requiresAuth: true }
  },
  {
    path: '/order/list',
    name: 'OrderList',
    component: () => import('@admin/views/order/OrderMonitor.vue'),
    meta: { title: '订单列表', requiresAuth: true }
  },
  {
    path: '/order/refund',
    name: 'OrderRefund',
    component: () => import('@admin/views/order/OrderMonitor.vue'),
    meta: { title: '退款管理', requiresAuth: true }
  },
  {
    path: '/review',
    name: 'Review',
    component: () => import('@admin/views/content/ReviewAudit.vue'),
    meta: { title: '评价审核', requiresAuth: true }
  },
  {
    path: '/content/review',
    name: 'ContentReview',
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
    path: '/system/settings',
    name: 'SystemSettings',
    component: () => import('@admin/views/system/Settings.vue'),
    meta: { title: '系统配置', requiresAuth: true }
  },
  {
    path: '/system/log',
    name: 'SystemLog',
    component: () => import('@admin/views/system/Settings.vue'),
    meta: { title: '操作日志', requiresAuth: true }
  },
  {
    path: '/system/permission',
    name: 'SystemPermission',
    component: () => import('@admin/views/system/Settings.vue'),
    meta: { title: '权限管理', requiresAuth: true }
  },
  {
    path: '/coupon',
    name: 'Coupon',
    component: () => import('@admin/views/marketing/Coupon.vue'),
    meta: { title: '优惠券管理', requiresAuth: true }
  },
  {
    path: '/coupon/list',
    name: 'CouponList',
    component: () => import('@admin/views/marketing/Coupon.vue'),
    meta: { title: '优惠券列表', requiresAuth: true }
  },
  {
    path: '/coupon/template',
    name: 'CouponTemplate',
    component: () => import('@admin/views/marketing/Coupon.vue'),
    meta: { title: '模板管理', requiresAuth: true }
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
  },
  {
    path: '/marketing/activity',
    name: 'MarketingActivity',
    component: () => import('@admin/views/marketing/Activity.vue'),
    meta: { title: '活动管理', requiresAuth: true }
  },
  {
    path: '/marketing/lottery',
    name: 'MarketingLottery',
    component: () => import('@admin/views/marketing/Activity.vue'),
    meta: { title: '抽奖管理', requiresAuth: true }
  },
  {
    path: '/content/notice',
    name: 'ContentNotice',
    component: () => import('@admin/views/content/MessageCenter.vue'),
    meta: { title: '公告管理', requiresAuth: true }
  },
  {
    path: '/content/banner',
    name: 'ContentBanner',
    component: () => import('@admin/views/content/MessageCenter.vue'),
    meta: { title: '轮播图管理', requiresAuth: true }
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
