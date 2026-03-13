import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('@/views/Home.vue'),
    meta: { title: '首页' }
  },
  {
    path: '/items',
    name: 'Items',
    component: () => import('@/views/ProductList.vue'),
    meta: { title: '全部商品' }
  },
  {
    path: '/digital',
    name: 'Digital',
    component: () => import('@/views/ProductList.vue'),
    meta: { title: '数码电器' }
  },
  {
    path: '/fashion',
    name: 'Fashion',
    component: () => import('@/views/ProductList.vue'),
    meta: { title: '服饰鞋包' }
  },
  {
    path: '/home',
    name: 'HomeGoods',
    component: () => import('@/views/ProductList.vue'),
    meta: { title: '家居家装' }
  },
  {
    path: '/sale',
    name: 'Sale',
    component: () => import('@/views/ProductList.vue'),
    meta: { title: '限时特惠' }
  },
  {
    path: '/item/:id',
    name: 'ItemDetail',
    component: () => import('@/views/ProductDetail.vue'),
    meta: { title: '商品详情' }
  },
  {
    path: '/cart',
    name: 'Cart',
    component: () => import('@/views/Cart.vue'),
    meta: { title: '购物车' }
  },
  {
    path: '/order',
    name: 'Order',
    component: () => import('@/views/Order.vue'),
    meta: { title: '订单确认' }
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
