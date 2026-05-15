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
}

const router = useRouter()
const cartStore = useCartStore()

// 倒计时
const countdown: Ref<{ hours: string; minutes: string; seconds: string }> = ref({ hours: "04", minutes: "30", seconds: "00" })

// 限时特惠商品 - 15 个
const flashItem = ref<Product[]>([
  {
    id: 1,
    name: "无线蓝牙耳机",
    price: 198,
    originalPrice: 398,
    soldCount: 7800,
    remainCount: 2200,
    type: "digital",
    rating: 4.8,
    sales: "10 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=耳机",
  },
  {
    id: 2,
    name: "智能手环",
    price: 145,
    originalPrice: 299,
    soldCount: 10000,
    remainCount: 0,
    type: "digital",
    rating: 4.7,
    sales: "8 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00ff88?text=手环",
  },
  {
    id: 3,
    name: "充电宝 20000mAh",
    price: 128,
    originalPrice: 258,
    soldCount: 6500,
    remainCount: 3500,
    type: "digital",
    rating: 4.6,
    sales: "5 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/ff6600?text=充电宝",
  },
  {
    id: 4,
    name: "机械键盘",
    price: 328,
    originalPrice: 598,
    soldCount: 8800,
    remainCount: 1200,
    type: "digital",
    rating: 4.8,
    sales: "6 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/ff8800?text=键盘",
  },
  {
    id: 5,
    name: "海尔冰箱",
    price: 3999,
    originalPrice: 5999,
    soldCount: 5500,
    remainCount: 4500,
    type: "appliance",
    rating: 4.7,
    sales: "3 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00ffff?text=冰箱",
  },
  {
    id: 6,
    name: "格力空调 1.5 匹",
    price: 2899,
    originalPrice: 3999,
    soldCount: 4000,
    remainCount: 6000,
    type: "appliance",
    rating: 4.6,
    sales: "4 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/ff8800?text=空调",
  },
  {
    id: 7,
    name: "Nike 运动鞋",
    price: 699,
    originalPrice: 999,
    soldCount: 2500,
    remainCount: 7500,
    type: "fashion",
    rating: 4.5,
    sales: "7 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/ffdd00?text=Nike",
  },
  {
    id: 8,
    name: "雅诗兰黛小棕瓶",
    price: 680,
    originalPrice: 880,
    soldCount: 1000,
    remainCount: 9000,
    type: "beauty",
    rating: 4.8,
    sales: "9 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00ff88?text=小棕瓶",
  },
  {
    id: 9,
    name: "华为 Mate60 Pro",
    price: 6999,
    originalPrice: 7999,
    soldCount: 7000,
    remainCount: 3000,
    type: "digital",
    rating: 4.9,
    sales: "12 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=Mate60",
  },
  {
    id: 10,
    name: "小米电视 75 英寸",
    price: 3999,
    originalPrice: 5499,
    soldCount: 6000,
    remainCount: 4000,
    type: "appliance",
    rating: 4.7,
    sales: "8 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00ff88?text=小米电视",
  },
  {
    id: 11,
    name: "阿迪达斯跑鞋",
    price: 599,
    originalPrice: 899,
    soldCount: 4500,
    remainCount: 5500,
    type: "fashion",
    rating: 4.6,
    sales: "6 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/ff3366?text=阿迪达斯",
  },
  {
    id: 12,
    name: "兰蔻粉水",
    price: 420,
    originalPrice: 580,
    soldCount: 8000,
    remainCount: 2000,
    type: "beauty",
    rating: 4.8,
    sales: "15 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/a335ee?text=兰蔻",
  },
  {
    id: 13,
    name: "iPad Air 5",
    price: 4799,
    originalPrice: 5999,
    soldCount: 6500,
    remainCount: 3500,
    type: "digital",
    rating: 4.9,
    sales: "5 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=iPad",
  },
  {
    id: 14,
    name: "西门子洗衣机",
    price: 4599,
    originalPrice: 6999,
    soldCount: 5000,
    remainCount: 5000,
    type: "appliance",
    rating: 4.7,
    sales: "4 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00ffff?text=西门子",
  },
  {
    id: 15,
    name: "优衣库羽绒服",
    price: 599,
    originalPrice: 999,
    soldCount: 7500,
    remainCount: 2500,
    type: "fashion",
    rating: 4.6,
    sales: "10 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/ff8800?text=优衣库",
  },
  {
    id: 16,
    name: "罗技 MX Master 3S",
    price: 699,
    originalPrice: 899,
    soldCount: 5500,
    remainCount: 4500,
    type: "digital",
    rating: 4.7,
    sales: "5 万+",
    image: "https://via.placeholder.com/200x200/1a2a4a/00d4ff?text=罗技",
  },
]);

// 限时特惠商品 (computed 属性)
const flashItems = computed(() => flashItem.value);

let timer: ReturnType<typeof setInterval> | null = null

const updateCountdown = () => {
  const now = new Date()
  const target = new Date()
  target.setHours(23, 59, 59)
  const diff = target.getTime() - now.getTime()
  if (diff > 0) {
    const hours = Math.floor(diff / 1000 / 60 / 60)
    const minutes = Math.floor((diff / 1000 / 60) % 60)
    const seconds = Math.floor((diff / 1000) % 60)
    countdown.value = {
      hours: String(hours).padStart(2, "0"),
      minutes: String(minutes).padStart(2, "0"),
      seconds: String(seconds).padStart(2, "0")
    }
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
  updateCountdown()
  timer = setInterval(updateCountdown, 1000)
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
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
