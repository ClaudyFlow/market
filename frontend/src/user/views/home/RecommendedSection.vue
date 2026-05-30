<template>
  <SectionContainer>
    <SectionHeader icon="fas fa-star">热门推荐</SectionHeader>
    <div class="recommend-grid">
      <ProductCard v-for="item in recommendedItems" :key="item.id" :product="item" @click="goToDetail(item.id)"
        @add-to-cart="addToCart" />
    </div>
  </SectionContainer>
</template>

<script setup lang="ts">
import { ref } from "vue";
import { useRouter } from "vue-router";

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

// 推荐商品 - 限时特惠商品列表
const recommendedItem = ref<Product[]>([
  {
    id: 1,
    name: "无线蓝牙耳机",
    price: 198,
    originalPrice: 398,
    rating: 4.8,
    type: "digital",
    sales: "10 万+",
    salesPercent: 78,
    remaining: 2200,
    image: "https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=%E8%93%9D%E7%89%87%E8%80%B3%E6%9C%BA",
  },
  {
    id: 2,
    name: "智能手环",
    price: 145,
    originalPrice: 299,
    rating: 4.7,
    type: "digital",
    sales: "8 万+",
    salesPercent: 100,
    remaining: 0,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ff88?text=%E6%99%BA%E8%83%BD%E6%89%8B%E7%8E%AF",
  },
  {
    id: 3,
    name: "充电宝 20000mAh",
    price: 128,
    originalPrice: 258,
    rating: 4.6,
    type: "digital",
    sales: "5 万+",
    salesPercent: 65,
    remaining: 3500,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff6600?text=%E5%85%85%E7%94%B5%E5%AE%9D",
  },
  {
    id: 4,
    name: "机械键盘",
    price: 328,
    originalPrice: 598,
    rating: 4.8,
    type: "digital",
    sales: "6 万+",
    salesPercent: 88,
    remaining: 1200,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff8800?text=%E6%9C%BA%E6%A2%B0%E9%94%AE%E7%9B%98",
  },
  {
    id: 5,
    name: "海尔冰箱",
    price: 3999,
    originalPrice: 5999,
    rating: 4.7,
    type: "appliance",
    sales: "3 万+",
    salesPercent: 55,
    remaining: 4500,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ffff?text=%E6%B5%B7%E5%B0%94%E5%86%B0%E7%AE%B1",
  },
  {
    id: 6,
    name: "格力空调 1.5 匹",
    price: 2899,
    originalPrice: 3999,
    rating: 4.6,
    type: "appliance",
    sales: "4 万+",
    salesPercent: 40,
    remaining: 6000,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff8800?text=%E6%A0%BC%E5%8A%9B%E7%A9%BA%E8%B0%83",
  },
  {
    id: 7,
    name: "Nike 运动鞋",
    price: 699,
    originalPrice: 999,
    rating: 4.5,
    type: "fashion",
    sales: "7 万+",
    salesPercent: 25,
    remaining: 7500,
    image: "https://via.placeholder.com/250x250/1a2a4a/ffdd00?text=Nike%E8%BF%90%E5%8A%A8%E9%9E%8B",
  },
  {
    id: 8,
    name: "雅诗兰黛小棕瓶",
    price: 680,
    originalPrice: 880,
    rating: 4.8,
    type: "beauty",
    sales: "9 万+",
    salesPercent: 10,
    remaining: 9000,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ff88?text=%E9%9B%85%E8%AF%97%E5%85%B0%E9%BB%9B%E5%B0%8F%E6%A3%95%E7%93%B6",
  },
  {
    id: 9,
    name: "华为 Mate60 Pro",
    price: 6999,
    originalPrice: 7999,
    rating: 4.9,
    type: "digital",
    sales: "12 万+",
    salesPercent: 70,
    remaining: 3000,
    image: "https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=%E5%8D%8E%E4%B8%BA%20Mate60%20Pro",
  },
  {
    id: 10,
    name: "小米电视 75 英寸",
    price: 3999,
    originalPrice: 5499,
    rating: 4.7,
    type: "appliance",
    sales: "8 万+",
    salesPercent: 60,
    remaining: 4000,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ff88?text=%E5%B0%8F%E7%B1%B3%E7%94%B5%E8%A7%86",
  },
  {
    id: 11,
    name: "阿迪达斯跑鞋",
    price: 599,
    originalPrice: 899,
    rating: 4.6,
    type: "fashion",
    sales: "6 万+",
    salesPercent: 45,
    remaining: 5500,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff3366?text=%E9%98%BF%E8%BF%AA%E8%BE%BE%E6%96%AF%E8%B7%91%E9%9E%8B",
  },
  {
    id: 12,
    name: "兰蔻粉水",
    price: 420,
    originalPrice: 580,
    rating: 4.8,
    type: "beauty",
    sales: "15 万+",
    salesPercent: 80,
    remaining: 2000,
    image: "https://via.placeholder.com/250x250/1a2a4a/a335ee?text=%E5%85%B0%E8%92%82%E7%B2%89%E6%B0%B4",
  },
  {
    id: 13,
    name: "iPad Air 5",
    price: 4799,
    originalPrice: 5999,
    rating: 4.9,
    type: "digital",
    sales: "5 万+",
    salesPercent: 65,
    remaining: 3500,
    image: "https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=iPad%20Air%205",
  },
  {
    id: 14,
    name: "西门子洗衣机",
    price: 4599,
    originalPrice: 6999,
    rating: 4.7,
    type: "appliance",
    sales: "4 万+",
    salesPercent: 50,
    remaining: 5000,
    image: "https://via.placeholder.com/250x250/1a2a4a/00ffff?text=%E8%A5%BF%E9%97%A8%E5%AD%90%E6%B4%97%E8%A1%A3%E6%9C%BA",
  },
  {
    id: 15,
    name: "优衣库羽绒服",
    price: 599,
    originalPrice: 999,
    rating: 4.6,
    type: "fashion",
    sales: "10 万+",
    salesPercent: 75,
    remaining: 2500,
    image: "https://via.placeholder.com/250x250/1a2a4a/ff8800?text=%E4%BC%98%E8%A1%A3%E5%BA%93%E7%BE%BD%E7%BB%92%E6%9C%8D",
  },
  {
    id: 16,
    name: "罗技 MX Master 3S",
    price: 699,
    originalPrice: 899,
    rating: 4.7,
    type: "digital",
    sales: "5 万+",
    salesPercent: 55,
    remaining: 4500,
    image: "https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=%E7%BD%97%E6%8A%80%20MX%20Master%203S",
  },
])

const recommendedItems = recommendedItem

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
