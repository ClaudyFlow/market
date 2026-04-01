<template>
  <SectionContainer>
    <SectionHeader icon="fas fa-shop">品牌精选</SectionHeader>

    <!-- 修改点：使用 router-link 包裹卡片，实现点击跳转 -->
    <router-link
      v-for="brand in brands"
      :key="brand.name"
      :to="{ name: 'Shop', query: { brand: brand.name } }"
      class="brand-card-link"
    >
      <article class="brand-card">
        <div class="brand-content">
          <div class="brand-logo">{{ brand.name }}</div>
          <p class="brand-desc">{{ brand.description }}</p>
        </div>
        <div class="brand-overlay">
          <el-button class="brand-btn">进入店铺</el-button>
        </div>
      </article>
    </router-link>
  </SectionContainer>
</template>

<script setup lang="ts">
import { ref } from "vue";
// Font Awesome 图标直接使用类名，无需导入
import SectionHeader from "@user/components/SectionHeader.vue";
import SectionContainer from "@user/components/SectionContainer.vue";

interface Brand {
  name: string
  description: string
}

const brands = ref<Brand[]>([
  { name: "华为", description: "构建万物互联的智能世界" },
  { name: "小米", description: "永远相信美好的事情即将发生" },
  { name: "海尔", description: "真诚到永远" },
  { name: "格力", description: "格力造，中国造" },
  { name: "苹果", description: "Think Different" },
  { name: "索尼", description: "感动人心" },
  { name: "三星", description: "数字世界的新纪元" },
  { name: "耐克", description: "Just Do It" },
  { name: "阿迪达斯", description: "Impossible Is Nothing" },
  { name: "兰蔻", description: "法式优雅，永恒之美" },
  { name: "戴森", description: "科技重新定义生活" },
  { name: "西门子", description: "德国工艺，品质生活" },
  { name: "美的", description: "原来生活可以更美的" },
  { name: "任天堂", description: "独乐不如众乐" },
  { name: "LV", description: "奢华与经典的象征" },
  { name: "联想", description: "智能，为每一个可能" },
]);
</script>

<style scoped>
/* 新增：链接样式，去除下划线并占满宽度 */
.brand-card-link {
  text-decoration: none;
  display: block;
}

/* 品牌卡片 */
article.brand-card {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
  transition: all 0.3s;
  position: relative;
  overflow: visible;
  min-height: 150px;
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  gap: 8px;
  height: 100%; /* 确保卡片填满链接容器 */
}

article.brand-card:hover {
  transform: translateY(-5px);
  border-color: var(--mall-primary);
  box-shadow: 0 5px 15px rgba(0, 212, 255, 0.2);
}

.brand-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  width: 100%;
}

.brand-overlay {
  display: flex;
  justify-content: center;
  align-items: center;
  opacity: 0;
  transition: all 0.3s;
  height: 0;
  overflow: hidden;
}

article.brand-card:hover .brand-overlay {
  opacity: 1;
  height: auto;
  padding-top: 10px;
}

.brand-logo {
  font-size: 22px;
  font-weight: bold;
  color: var(--mall-primary);
}

.brand-desc {
  font-size: 13px;
  color: #888;
  margin: 0; /* 重置默认 margin */
}

.brand-btn {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
  padding: 8px 25px;
  border-radius: 6px;
  transition: all 0.3s;
  cursor: pointer;
}

.brand-btn:hover {
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}
</style>
