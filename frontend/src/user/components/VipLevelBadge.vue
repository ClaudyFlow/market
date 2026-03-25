<template>
  <div class="vip-level-badge">
    <img :src="icon" alt="" class="level-icon" v-if="icon" />
    <span class="level-name" :style="{ color: textColor }">{{ levelName }}</span>
  </div>
</template>

<script setup lang="ts">
defineProps<{
  levelName?: string
  icon?: string
  textColor?: string
}>()
</script>

<style scoped>
.vip-level-badge {
  position: relative;
  overflow: hidden;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  background: linear-gradient(90deg, #ffdf00, #ffcc00);
  padding: 8px 20px;
  border-radius: 9999px;
  border: 1px solid rgba(255, 223, 0, 0.9);
  box-shadow: 0 0 25px rgba(255, 223, 0, 0.8);
  width: 100%;
}

.level-icon {
  width: 36px;
  height: 36px;
  filter: drop-shadow(0 0 20px rgba(255, 249, 196, 0.9));
}

.level-name {
  font-size: 14px;
  font-weight: bold;
  color: #fff9c4;
}

.vip-level-badge::before {
  content: '';
  position: absolute;
  top: -50%;
  left: -100%;
  width: 50%;
  height: 200%;
  background: linear-gradient(
    90deg,
    transparent,
    rgba(255, 249, 196, 0.6),
    rgba(255, 223, 0, 0.7),
    rgba(255, 249, 196, 0.6),
    transparent
  );
  transform: skewX(-45deg);
  animation: shine-slide 5s linear infinite;
  pointer-events: none;
  z-index: 1;
}

@keyframes shine-slide {
  0% {
    left: -100%;
  }
  100% {
    left: 600%;
  }
}
</style>
