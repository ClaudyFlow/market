<template>
  <div class="footer-column">
    <h4>{{ title }}</h4>
    <a 
      v-for="(link, index) in links" 
      :key="index" 
      :href="link.href || '#'"
      :target="link.target || '_self'"
    >
      {{ link.text }}
    </a>
    <slot></slot>
  </div>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  links: {
    type: Array,
    default: () => []
  }
})
</script>

<style scoped>
.footer-column {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.footer-column h4 {
  font-size: 15px;
  color: var(--mall-text-primary);
  font-weight: bold;
}

.footer-column a {
  color: var(--mall-text-muted);
  text-decoration: none;
  font-size: 13px;
  transition: all 0.3s;
}

.footer-column a:hover {
  color: var(--mall-primary);
  padding-left: 5px;
}
</style>
