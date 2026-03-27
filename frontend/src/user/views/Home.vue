<template>
  <main class="home-main">
    <section class="mall-home-section">
      <MallHomeSection />
    </section>

    <section class="flash-sale-section">
      <FlashSaleSection />
    </section>

    <section class="recommended-section">
      <RecommendedSection />
    </section>

    <section class="brand-section">
      <BrandSection />
    </section>
  </main>
</template>

<script setup lang="ts">
import BrandSection from "@user/views/home/BrandSection.vue";
import RecommendedSection from "@user/views/home/RecommendedSection.vue";
import FlashSaleSection from "@user/views/home/FlashSaleSection.vue";
import MallHomeSection from "@user/views/home/MallHomeSection.vue";
</script>

<style scoped>
@import "@user/assets/mall-style.css";

/* 各 section 背景 */
.mall-home-section,
.flash-sale-section,
.recommended-section,
.brand-section {
  background: linear-gradient(180deg,
      rgba(0, 212, 255, 0.15) 0%,
      rgba(10, 14, 26, 0.8) 100%);
}
</style>
