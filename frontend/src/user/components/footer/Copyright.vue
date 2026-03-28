<template>
  <div class="copyright">
    <p v-for="(line, index) in texts" :key="index">{{ line }}</p>
    <div class="legal-links" v-if="showLegalLinks">
      <router-link to="/agreement">用户协议</router-link>
      <span class="divider">|</span>
      <router-link to="/privacy">隐私政策</router-link>
    </div>
  </div>
</template>

<script setup>
defineProps({
  texts: {
    type: Array,
    default: () => [
      '© 2024-2026 购物商城系统 版权所有',
      '专业的综合网上购物商城 - 为您提供正品低价的优质商品'
    ]
  },
  showLegalLinks: {
    type: Boolean,
    default: true
  }
})
</script>

<style scoped>
.copyright {
  text-align: center;
  color: var(--mall-text-muted);
  font-size: 12px;
  line-height: 28px;
}

.copyright p {
  margin: 4px 0;
}

.legal-links {
  margin-top: 12px;
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.legal-links a {
  color: var(--mall-text-secondary);
  text-decoration: none;
  font-size: 12px;
  transition: all 0.3s;
}

.legal-links a:hover {
  color: var(--mall-primary);
  text-decoration: underline;
}

.legal-links .divider {
  color: var(--mall-border-light);
}
</style>
