<template>
  <div class="app-container">
    <Header />
    <main class="main-content">
      <router-view />
    </main>
    <Footer />
  </div>
</template>

<script setup>
import { watch } from 'vue'
import { useRoute } from 'vue-router'
import Header from '@user/components/Header.vue'
import Footer from '@user/components/Footer.vue'

const route = useRoute()

// 路由切换时滚动到顶部
watch(
  () => route.path,
  () => {
    window.scrollTo(0, 0)
  }
)
</script>

<style>
@import '@user/assets/mall-style.css';

* {
  
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
  width: 100%;
  padding-top: 10px;
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
