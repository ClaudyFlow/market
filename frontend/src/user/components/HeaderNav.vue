<template>
  <div class="main-nav">
    <div class="container">
      <router-link to="/" class="logo">
        <div class="logo-icon">
          <el-icon size="32"><ShoppingCart /></el-icon>
        </div>
        <div class="logo-text">
          <span class="logo-title">购物商城</span>
          <span class="logo-subtitle">
            <span>品质生活</span>
            <span>精选好物</span>
          </span>
        </div>
      </router-link>

      <nav class="nav-links">
        <router-link to="/" class="nav-item" active-class="active">
          <el-icon><HomeFilled /></el-icon>
          <span>首页</span>
        </router-link>
        <router-link to="/item" class="nav-item" active-class="active">
          <el-icon><Box /></el-icon>
          <span>全部商品</span>
        </router-link>
        <router-link to="/lottery" class="nav-item" active-class="active">
          <el-icon><Star /></el-icon>
          <span>幸运抽奖</span>
        </router-link>
        <router-link to="/digital" class="nav-item" active-class="active">
          <el-icon><Cellphone /></el-icon>
          <span>数码电器</span>
        </router-link>
        <router-link to="/fashion" class="nav-item" active-class="active">
          <el-icon><ShoppingBag /></el-icon>
          <span>服饰鞋包</span>
        </router-link>
        <router-link to="/home" class="nav-item" active-class="active">
          <el-icon><House /></el-icon>
          <span>家居家装</span>
        </router-link>
        <router-link to="/sale" class="nav-item" active-class="active">
          <el-icon><Timer /></el-icon>
          <span>限时特惠</span>
        </router-link>
      </nav>

      <div class="search-box">
        <el-input
          v-model="searchKeyword"
          placeholder="搜索商品..."
          size="small"
          round
          @keyup.enter="handleSearch"
        >
          <template #append>
            <el-button @click="handleSearch" round>
              <el-icon><Search /></el-icon>
            </el-button>
          </template>
        </el-input>
      </div>

      <div class="cart-icon" @click="router.push('/cart')">
        <el-icon size="22"><ShoppingCart /></el-icon>
        <span class="cart-count" v-if="购物车数量 > 0">{{ 购物车数量 }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@user/stores/cart'
import { ShoppingCart, HomeFilled, Box, Star, Cellphone, ShoppingBag, House, Timer, Search } from '@element-plus/icons-vue'

const router = useRouter()
const cartStore = useCartStore()

const searchKeyword = ref('')

// 购物车数量
const 购物车数量 = computed(() => cartStore.totalCount)

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/item', query: { keyword: searchKeyword.value } })
  }
}
</script>

<style scoped>
.main-nav {
  padding: 15px 0;
}

.main-nav > .container {
  display: flex;
  align-items: center;
  gap: 40px;
  max-width: 1400px;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  color: #fff;
}

.logo-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 20px rgba(0,212,255,0.5);
}

.logo-text {
  display: flex;
  flex-direction: column;
  align-items: flex-start;
  gap: 4px;
}

.logo-title {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 4px;
  white-space: nowrap;
}

.logo-subtitle {
  display: flex;
  flex-direction: column;
  gap: 2px;
  font-size: 11px;
  color: #888;
  letter-spacing: 2px;
  line-height: 1.3;
}

.nav-links {
  display: flex;
  gap: 10px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 15px;
  color: #fff;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  white-space: nowrap;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0,212,255,0.2), rgba(0,255,136,0.2));
  opacity: 0;
  transition: opacity 0.3s;
}

.nav-item:hover {
  color: var(--mall-primary);
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-item.active {
  color: var(--mall-primary);
  background: rgba(0,212,255,0.15);
  box-shadow: 0 0 15px rgba(0,212,255,0.3);
}

.nav-item .el-icon {
  font-size: 18px;
}

.search-box {
  width: 250px;
}

.search-box :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(0,212,255,0.3);
}

.search-box :deep(.el-input__inner) {
  color: #fff;
}

.search-box :deep(.el-input-group__append) {
  background: var(--mall-primary);
  color: #000;
  border: none;
}

.search-box :deep(.el-input-group__append:hover) {
  background: var(--mall-secondary);
}

.cart-icon {
  position: relative;
  width: 45px;
  height: 45px;
  background: linear-gradient(135deg, rgba(0,212,255,0.2), rgba(0,255,136,0.2));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--mall-primary);
  cursor: pointer;
  transition: all 0.3s;
}

.cart-icon:hover {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  color: #000;
  box-shadow: 0 0 20px rgba(0,212,255,0.5);
  transform: scale(1.1);
}

.cart-count {
  position: absolute;
  top: -5px;
  right: -5px;
  background: linear-gradient(135deg, #ff6600, #ff8800);
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: bold;
  border: 2px solid #1a2a4a;
}
</style>
