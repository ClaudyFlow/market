<template>
  <div class="app-container">
    <!-- 未登录时只显示路由内容（登录页） -->
    <template v-if="!isLoggedIn">
      <router-view />
    </template>
    <!-- 已登录时显示完整布局 -->
    <template v-else>
      <Header />
      <main class="main-content">
        <div class="page-content">
          <router-view />
        </div>
      </main>
      <Footer />
    </template>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import Header from '@admin/components/Header.vue'
import Footer from '@admin/components/Footer.vue'

const isLoggedIn = computed(() => {
  return !!localStorage.getItem('admin_token')
})
</script>

<style>
@import '@admin/assets/mall-style.css';

* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

body {
  font-family: 'Microsoft YaHei', 'PingFang SC', sans-serif;
  background: var(--mall-bg-dark);
  min-height: 100vh;
  color: #fff;
}

.app-container {
  min-height: 100vh;
  display: flex;
  flex-direction: column;
  background:
    radial-gradient(ellipse at top, #1a2a4a 0%, transparent 50%),
    radial-gradient(ellipse at bottom, #0d1a2a 0%, transparent 50%),
    var(--mall-bg-dark);
}

.main-content {
  flex: 1;
  display: flex;
  flex-direction: column;
  padding: 20px;
  max-width: 1400px;
  margin: 0 auto;
  width: 100%;
}

.page-content {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

/* 科幻风格滚动条 */
::-webkit-scrollbar {
  width: 8px;
  height: 8px;
}

::-webkit-scrollbar-track {
  background: var(--mall-bg-medium);
}

::-webkit-scrollbar-thumb {
  background: var(--mall-primary);
  border-radius: 4px;
}

::-webkit-scrollbar-thumb:hover {
  background: var(--mall-secondary);
}
</style>
