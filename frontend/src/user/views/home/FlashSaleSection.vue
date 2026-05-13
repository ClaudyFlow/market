<template>
  <SectionContainer>
    <SectionHeader icon="fas fa-clock">限时特惠</SectionHeader>
    <div class="countdown-bar">
      <div class="cd-text">
        <span class="cd-label">距离结束还有</span>
      </div>
      <div class="cd-time">
        <span class="cd-item">{{ countdown.hours }}</span>
        <span class="cd-colon">:</span>
        <span class="cd-item">{{ countdown.minutes }}</span>
        <span class="cd-colon">:</span>
        <span class="cd-item">{{ countdown.seconds }}</span>
      </div>
    </div>
    <div class="sale-grid">
      <ProductCard v-for="item in flashItems" :key="item.id" :product="item" @click="goToDetail(item.id)"
        @add-to-cart="addToCart" />
    </div>
  </SectionContainer>
</template>

<script setup lang="ts">
import { ref, onMounted, onUnmounted, type Ref, computed } from "vue";
import { useRouter } from "vue-router";
// Font Awesome 图标直接使用类名，无需导入
import { useCartStore } from "@user/stores/cart";
import ProductCard from "@user/components/ProductCard.vue";
import SectionHeader from "@user/components/SectionHeader.vue";
import SectionContainer from "@user/components/SectionContainer.vue";
import { ElMessage } from "element-plus";
import { getFlashSales } from "@user/api/home";

interface Product {
  id: number
  name: string
  price: number
  originalPrice: number
  type?: string
  rating?: number
  sales?: string
  image?: string
  soldCount?: number
  remainCount?: number
  flashPrice?: number
  stock?: number
  progress?: number
}

const router = useRouter()
const cartStore = useCartStore()
const flashItems = ref<Product[]>([])
const countdown = ref({ hours: "00", minutes: "00", seconds: "00" })
let countdownTimer: ReturnType<typeof setInterval> | null = null
let countdownEndTime: Date | null = null

const loadFlashSales = async () => {
  try {
    const res = await getFlashSales()
    const data = Array.isArray(res) ? res : (res.data || [])
    if (data.length > 0) {
      const sale = data[0]
      flashItems.value = (sale.products || []).map((p: any) => ({
        id: p.id,
        name: p.name,
        price: p.flashPrice || p.price,
        originalPrice: p.originalPrice,
        image: p.image,
        soldCount: p.sales,
        remainCount: p.stock,
        stock: p.stock,
        progress: p.progress
      }))
      if (sale.endTime) {
        countdownEndTime = new Date(sale.endTime)
        startCountdown()
      }
    }
  } catch (error) {
    console.error('加载秒杀活动失败', error)
  }
}

const startCountdown = () => {
  if (countdownTimer) clearInterval(countdownTimer)
  countdownTimer = setInterval(updateCountdown, 1000)
}

const updateCountdown = () => {
  if (!countdownEndTime) return
  const now = new Date()
  const diff = countdownEndTime.getTime() - now.getTime()
  if (diff > 0) {
    const hours = Math.floor(diff / 1000 / 60 / 60)
    const minutes = Math.floor((diff / 1000 / 60) % 60)
    const seconds = Math.floor((diff / 1000) % 60)
    countdown.value = {
      hours: String(hours).padStart(2, "0"),
      minutes: String(minutes).padStart(2, "0"),
      seconds: String(seconds).padStart(2, "0")
    }
  } else {
    countdown.value = { hours: "00", minutes: "00", seconds: "00" }
    if (countdownTimer) clearInterval(countdownTimer)
  }
}

const goToDetail = (id: number) => {
  router.push(`/item/${id}`)
}

const addToCart = (item: Product) => {
  cartStore.addToCart({ ...item, quantity: 1 })
  ElMessage.success("已加入购物车")
}

onMounted(() => {
  loadFlashSales()
})

onUnmounted(() => {
  if (countdownTimer) clearInterval(countdownTimer)
})
</script>

<style scoped>
.countdown-bar {
  grid-column: 1 / -1;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  margin-bottom: 25px;
  margin-top: -10px;
}

.cd-text {
  display: flex;
  justify-content: center;
}

.cd-time {
  display: flex;
  align-items: center;
  gap: 8px;
}

.cd-label {
  color: #888;
  font-size: 14px;
}

.cd-item {
  background: linear-gradient(135deg, var(--mall-accent), #ff8800);
  color: #fff;
  padding: 8px 12px;
  border-radius: 6px;
  font-size: 20px;
  font-weight: bold;
  min-width: 45px;
  text-align: center;
}

.cd-colon {
  color: var(--mall-accent);
  font-size: 20px;
}

.sale-grid {
  grid-column: 1 / -1;
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 15px;
}
</style>
