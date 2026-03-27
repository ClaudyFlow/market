<template>
  <SectionContainer>
    <SectionHeader :icon="StarFilled">热门推荐</SectionHeader>
    <div class="recommend-grid">
      <ProductCard v-for="item in recommendedItems" :key="item.id" :product="item" @click="goToDetail(item.id)"
        @add-to-cart="addToCart" />
    </div>
  </SectionContainer>
</template>

<script setup lang="ts">
import { ref, computed } from "vue";
import { useRouter } from "vue-router";
import { StarFilled } from '@element-plus/icons-vue'
import { useCartStore } from "@user/stores/cart";
import ProductCard from "@user/components/ProductCard.vue";
import SectionHeader from "@user/components/SectionHeader.vue";
import SectionContainer from "@user/components/SectionContainer.vue";
import { ElMessage } from "element-plus";

interface Product {
  id: number
  name: string
  price: number
  originalPrice: number
  description?: string
  rating?: number
  type?: string
  sales?: string
  salesPercent?: number
  remaining?: number
  image?: string
}

const router = useRouter()
const cartStore = useCartStore()

// 推荐商品 - 15 个
const recommendedItem = ref<Product[]>([
  {
    id: 101,
    name: "iPhone 15 Pro",
    price: 7999,
    originalPrice: 9999,
    description: "256GB / 钛金属 / A17 Pro 芯片",
    rating: 4.9,
    type: "digital",
    sales: "10 万+",
    salesPercent: 85,
    remaining: 1500,
    image: "https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=iPhone",
  },
  {
    id: 102,
    name: "MacBook Pro 14",
    price: 12999,
    originalPrice: 15999,
    description: "M3 芯片 / 16GB / 512GB",
    rating: 4.9,
    type: "digital",
    sales: "5 万+",
    salesPercent: 65,
    remaining: 350,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff8800?text=MacBook",
  },
  {
    id: 103,
    name: "海尔冰箱",
    price: 3999,
    originalPrice: 5999,
    description: "500L / 变频 / 除菌净味",
    rating: 4.7,
    type: "appliance",
    sales: "8 万+",
    salesPercent: 75,
    remaining: 2500,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ff88?text=冰箱",
  },
  {
    id: 104,
    name: "格力空调 1.5 匹",
    price: 2899,
    originalPrice: 3999,
    description: "变频冷暖 / 一级能效",
    rating: 4.6,
    type: "appliance",
    sales: "15 万+",
    salesPercent: 90,
    remaining: 1000,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ffff?text=空调",
  },
  {
    id: 105,
    name: "Nike 运动鞋",
    price: 699,
    originalPrice: 999,
    description: "气垫减震 / 透气舒适",
    rating: 4.5,
    type: "fashion",
    sales: "12 万+",
    salesPercent: 70,
    remaining: 3000,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff3366?text=Nike",
  },
  {
    id: 106,
    name: "雅诗兰黛小棕瓶",
    price: 680,
    originalPrice: 880,
    description: "50ml / 修护精华",
    rating: 4.8,
    type: "beauty",
    sales: "25 万+",
    salesPercent: 92,
    remaining: 800,
    image: "https://via.placeholder.com/250x250/1a2a4a/a335ee?text=小棕瓶",
  },
  {
    id: 107,
    name: "索尼微单相机",
    price: 12999,
    originalPrice: 16999,
    description: "全画幅 / 3300 万像素",
    rating: 4.8,
    type: "digital",
    sales: "3 万+",
    salesPercent: 55,
    remaining: 450,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff6600?text=Sony",
  },
  {
    id: 108,
    name: "戴森吸尘器",
    price: 3990,
    originalPrice: 5990,
    description: "激光探测 / 深层清洁",
    rating: 4.7,
    type: "appliance",
    sales: "6 万+",
    salesPercent: 80,
    remaining: 2000,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff8800?text=Dyson",
  },
  {
    id: 109,
    name: "华为 MatePad Pro",
    price: 4999,
    originalPrice: 6499,
    description: "13.2 英寸 / 麒麟 9000S / 鸿蒙系统",
    rating: 4.8,
    type: "digital",
    sales: "4 万+",
    salesPercent: 72,
    remaining: 2800,
    image: "https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=MatePad",
  },
  {
    id: 110,
    name: "西门子洗碗机",
    price: 5999,
    originalPrice: 8999,
    description: "12 套 / 智能洗 / 烘干",
    rating: 4.7,
    type: "appliance",
    sales: "3 万+",
    salesPercent: 68,
    remaining: 3200,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ff88?text=洗碗机",
  },
  {
    id: 111,
    name: "LV 经典钱包",
    price: 3500,
    originalPrice: 4800,
    description: "真皮 / 多卡位 / 经典老花",
    rating: 4.6,
    type: "fashion",
    sales: "2 万+",
    salesPercent: 60,
    remaining: 400,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff8800?text=LV",
  },
  {
    id: 112,
    name: "兰蔻小黑瓶",
    price: 1080,
    originalPrice: 1580,
    description: "100ml / 肌底液 / 修护维稳",
    rating: 4.8,
    type: "beauty",
    sales: "20 万+",
    salesPercent: 88,
    remaining: 1200,
    image: "https://via.placeholder.com/250x250/1a2a4a/a335ee?text=兰蔻",
  },
  {
    id: 113,
    name: "任天堂 Switch OLED",
    price: 2099,
    originalPrice: 2599,
    description: "7 寸 OLED / 64GB / 续航提升",
    rating: 4.8,
    type: "digital",
    sales: "18 万+",
    salesPercent: 82,
    remaining: 1800,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff3366?text=Switch",
  },
  {
    id: 114,
    name: "美的微波炉",
    price: 699,
    originalPrice: 999,
    description: "智能菜单 / 易清洁 / 省电",
    rating: 4.5,
    type: "appliance",
    sales: "10 万+",
    salesPercent: 75,
    remaining: 2500,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ffff?text=美的",
  },
  {
    id: 115,
    name: "北面冲锋衣",
    price: 1299,
    originalPrice: 1899,
    description: "防风防水 / 透气 / 多口袋",
    rating: 4.7,
    type: "fashion",
    sales: "8 万+",
    salesPercent: 70,
    remaining: 3000,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff8800?text=北面",
  },
  {
    id: 116,
    name: "Bose QuietComfort 45",
    price: 2299,
    originalPrice: 2999,
    description: "主动降噪 / 24 小时续航",
    rating: 4.7,
    type: "digital",
    sales: "6 万+",
    salesPercent: 65,
    remaining: 3500,
    image: "https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=Bose",
  },
]);

// 推荐商品 (computed 属性)
const recommendedItems = computed(() => recommendedItem.value);

const goToDetail = (id: number) => {
  router.push(`/item/${id}`)
}

const addToCart = (item: Product) => {
  cartStore.addToCart({ ...item, quantity: 1 })
  ElMessage.success("已加入购物车")
}
</script>

<style scoped>
.recommend-grid {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}
</style>
