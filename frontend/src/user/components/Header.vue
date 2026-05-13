<template>
  <header class="header">
    <HeaderInfo />
    <HeaderNav />
    <Breadcrumb />
  </header>
</template>

<script setup>
import HeaderInfo from './HeaderInfo.vue'
import HeaderNav from './HeaderNav.vue'
import Breadcrumb from './Breadcrumb.vue'
</script>

<style scoped>
.header {
  background: linear-gradient(180deg, #1a2a4a 0%, #0d1a2a 100%);
  border-bottom: 1px solid rgba(0,212,255,0.3);
  position: sticky;
  top: 0;
  z-index: 1000;
}
</style>
