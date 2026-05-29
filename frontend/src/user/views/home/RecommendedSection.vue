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
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
// Font Awesome 图标直接使用类名，无需导入
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

// 推荐商品 - 从 API 加载
const recommendedItem = ref<Product[]>([])

const loadRecommended = async () => {
  try {
    const res = await fetch("/api/recommend/products?limit=8", {
      headers: { "Authorization": "Bearer " + localStorage.getItem("token") }
    })
    if (res.ok) {
      const data = await res.json()
      recommendedItem.value = (data.data || []).map((p: any) => ({
        id: p.id,
        name: p.name,
        price: p.price,
        originalPrice: p.originalPrice || p.price,
        description: p.description || "",
        rating: p.rating || 5.0,
        type: p.type || "digital",
        sales: p.sales || "0",
        salesPercent: p.salesPercent || 70,
        remaining: p.remaining || Math.floor(Math.random() * 1000),
        image: p.image || `https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=${encodeURIComponent(p.name)}`,
      }))
    }
  } catch {
    // 如果失败使用空数组，不影响页面渲染
  }
}

onMounted(() => {
  loadRecommended()
})

// 推荐商品
const recommendedItems = recommendedItem;

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
