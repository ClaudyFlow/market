<template>
  <nav class="breadcrumb" aria-label="面包屑导航">
    <div class="container">
      <el-breadcrumb separator="/">
        <el-breadcrumb-item :to="{ path: '/' }">
          <el-icon><HomeFilled /></el-icon>
          首页
        </el-breadcrumb-item>
        <el-breadcrumb-item
          v-for="(item, index) in breadcrumbItems"
          :key="index"
          :to="item.to"
        >
          {{ item.name }}
        </el-breadcrumb-item>
      </el-breadcrumb>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import { HomeFilled } from '@element-plus/icons-vue'

const route = useRoute()

// 路由名称映射
const routeNameMap: Record<string, string> = {
  '/item': '商品列表',
  '/cart': '购物车',
  '/user': '用户中心',
  '/order': '订单中心',
  '/favorite': '收藏夹',
  '/address': '地址管理',
  '/lottery': '幸运抽奖',
  '/digital': '数码电器',
  '/fashion': '服饰鞋包',
  '/home': '家居家装',
  '/sale': '限时特惠',
  '/vip': 'VIP 中心',
  '/service': '客服中心',
  '/forum': '用户论坛',
  '/coupon': '优惠券中心',
  '/agreement': '用户协议',
  '/privacy': '隐私政策'
}

// 自动生成面包屑
const breadcrumbItems = computed(() => {
  const paths = route.path.split('/').filter(Boolean)
  const items = []

  let currentPath = ''
  for (let i = 0; i < paths.length; i++) {
    currentPath += '/' + paths[i]
    const isLast = i === paths.length - 1

    // 特殊处理商品详情页面
    if (currentPath.startsWith('/item/') && currentPath !== '/item') {
      const productId = currentPath.split('/item/')[1]
      items.push({
        name: `商品${productId}`,
        to: undefined
      })
      break
    }

    items.push({
      name: routeNameMap[currentPath] || paths[i],
      to: isLast ? undefined : currentPath
    })
  }

  return items
})
</script>

<style scoped>
.breadcrumb {
  background: linear-gradient(90deg,
    rgba(0, 212, 255, 0.1) 0%,
    rgba(0, 255, 136, 0.05) 50%,
    rgba(0, 212, 255, 0.1) 100%);
  border-top: 1px solid rgba(0, 212, 255, 0.2);
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
  padding: 0;
  position: relative;
  overflow: hidden;
  height: 40px;
  display: flex;
  align-items: center;
}

.breadcrumb::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,
    transparent 0%,
    rgba(0, 212, 255, 0.1) 50%,
    transparent 100%);
  animation: shimmer 3s infinite;
}

@keyframes shimmer {
  0% { left: -100%; }
  100% { left: 100%; }
}

.breadcrumb .container {
  width: 100%;
  padding: 0 2%;
  position: relative;
  z-index: 1;
  display: flex;
  align-items: center;
}

.breadcrumb :deep(.el-breadcrumb__inner) {
  color: #888;
  font-size: 14px;
  transition: all 0.3s;
}

.breadcrumb :deep(.el-breadcrumb__inner a) {
  color: #888;
  transition: all 0.3s;
  position: relative;
}

.breadcrumb :deep(.el-breadcrumb__inner a)::after {
  content: '';
  position: absolute;
  bottom: -2px;
  left: 0;
  width: 0;
  height: 1px;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  transition: width 0.3s;
}

.breadcrumb :deep(.el-breadcrumb__inner a:hover) {
  color: var(--mall-primary);
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.breadcrumb :deep(.el-breadcrumb__inner a:hover)::after {
  width: 100%;
}

.breadcrumb :deep(.el-breadcrumb__separator) {
  color: rgba(0, 212, 255, 0.4);
  margin: 0 8px;
}

.breadcrumb :deep(.el-breadcrumb__item:last-child .el-breadcrumb__inner) {
  color: var(--mall-primary);
  font-weight: 600;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

.breadcrumb :deep(.el-icon) {
  margin-right: 6px;
  vertical-align: middle;
  color: var(--mall-primary);
}

.breadcrumb :deep(.el-breadcrumb__item:first-child .el-icon) {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { 
    transform: scale(1);
    filter: drop-shadow(0 0 5px rgba(0, 212, 255, 0.3));
  }
  50% { 
    transform: scale(1.1);
    filter: drop-shadow(0 0 10px rgba(0, 212, 255, 0.5));
  }
}
</style>
