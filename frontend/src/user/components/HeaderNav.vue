<template>
  <div class="main-nav">
    <div class="container">
      <router-link to="/" class="logo">
        <div class="logo-icon">
          <i class="fas fa-shopping-cart"></i>
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
          <i class="fas fa-home"></i>
          <span>首页</span>
        </router-link>
        <router-link to="/item" class="nav-item" active-class="active">
          <i class="fas fa-box"></i>
          <span>全部商品</span>
        </router-link>
        <router-link to="/sale" class="nav-item" active-class="active">
          <i class="fas fa-clock"></i>
          <span>限时特惠</span>
        </router-link>
        <router-link to="/lottery" class="nav-item" active-class="active">
          <i class="fas fa-star"></i>
          <span>幸运抽奖</span>
        </router-link>
      </nav>

      <!-- 使用新的搜索框组件（紧凑模式） -->
      <div class="search-cart-wrapper">
        <SearchBar 
          compact 
          :showHotTags="false" 
          :showHistory="false"
          placeholder="搜索商品..."
        />
        <!-- 购物车标签 -->
        <div class="cart-tab" @click="router.push('/cart')" role="button" tabindex="0">
          <div class="cart-tab-icon">
            <i class="fas fa-shopping-cart"></i>
            <span class="cart-tab-count" v-if="购物车数量 > 0">{{ 购物车数量 > 99 ? '99+' : 购物车数量 }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useLocalCartStore } from '@user/stores/cart-local'
import SearchBar from './SearchBar.vue'

const router = useRouter()
const cartStore = useLocalCartStore()

// 购物车数量
const 购物车数量 = computed(() => cartStore.totalCount)
</script>

<style scoped>
.main-nav {
  padding: 1% 0;
}

.main-nav > .container {
  display: flex;
  align-items: center;
  gap: 20px;
  width: 100%;
  padding: 0 2%;
  height: 70px;
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

.logo-icon i {
  color: #000;
  font-size: 28px;
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

.nav-item i {
  font-size: 18px;
}

/* 搜索购物车包装器 */
.search-cart-wrapper {
  display: flex;
  align-items: center;
  gap: 10px;
  width: 500px;
  flex-shrink: 0;
  margin-left: auto;
}

.search-cart-wrapper :deep(.search-bar-component) {
  padding: 0;
  background: transparent;
  border: none;
  flex: 1;
}

/* 购物车标签 */
.cart-tab {
  flex-shrink: 0;
  cursor: pointer;
  transition: all 0.3s;
}

.cart-tab:hover {
  transform: scale(1.05);
}

.cart-tab:focus {
  outline: 2px solid #00d4ff;
  outline-offset: 2px;
}

.cart-tab-icon {
  position: relative;
  width: 45px;
  height: 45px;
  background: linear-gradient(135deg, rgba(0,212,255,0.2), rgba(0,255,136,0.2));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--mall-primary);
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.cart-tab:hover .cart-tab-icon {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  color: #000;
  box-shadow: 0 0 20px rgba(0,212,255,0.5);
}

.cart-tab-icon i {
  font-size: 20px;
}

.cart-tab-count {
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
  min-width: 18px;
  text-align: center;
}

/* 购物车图标包装器（已移除） */
</style>
