<template>
  <div class="product-list">
    <div class="container">
      <!-- 筛选栏 -->
      <div class="filter-bar">
        <div class="filter-item">
          <span class="filter-label">分类：</span>
          <el-radio-group v-model="selectedCategory" size="small">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button v-for="cat in categories" :key="cat" :label="cat">{{ cat }}</el-radio-button>
          </el-radio-group>
        </div>
        <div class="filter-item">
          <span class="filter-label">价格：</span>
          <el-radio-group v-model="priceRange" size="small">
            <el-radio-button label="">全部</el-radio-button>
            <el-radio-button label="0-1000">0-1000 元</el-radio-button>
            <el-radio-button label="1000-5000">1000-5000 元</el-radio-button>
            <el-radio-button label="5000+">5000 元以上</el-radio-button>
          </el-radio-group>
        </div>
        <div class="filter-item">
          <span class="filter-label">排序：</span>
          <el-radio-group v-model="sortBy" size="small">
            <el-radio-button label="default">综合</el-radio-button>
            <el-radio-button label="sales">销量</el-radio-button>
            <el-radio-button label="price-asc">价格↑</el-radio-button>
            <el-radio-button label="price-desc">价格↓</el-radio-button>
          </el-radio-group>
        </div>
      </div>

      <!-- 商品列表 -->
      <div class="products-grid">
        <ProductCard
          v-for="product in filteredProducts"
          :key="product.id"
          :product="formatProduct(product)"
          @click="goToDetail(product.id)"
          @add-to-cart="addToCart"
        />
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <el-pagination
          v-model:current-page="currentPage"
          v-model:page-size="pageSize"
          :total="totalProducts"
          :page-sizes="[8, 16, 24, 32]"
          layout="total, sizes, prev, pager, next, jumper"
        />
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { ElMessage } from 'element-plus'
import ProductCard from '@/components/ProductCard.vue'

const router = useRouter()
const route = useRoute()
const cartStore = useCartStore()

const categories = ['手机数码', '电脑办公', '家用电器', '服装鞋包', '美妆护肤', '图书文娱', '食品生鲜', '母婴玩具']

const selectedCategory = ref(route.query.category || '')
const priceRange = ref('')
const sortBy = ref('default')
const currentPage = ref(1)
const pageSize = ref(8)

// 模拟商品数据 - 每个商品添加库存和销量数据
const allProducts = ref([
  { id: 1, name: 'Apple iPhone 15 Pro Max', price: 9999, originalPrice: 11999, description: '256GB / 钛金属 / A17 Pro 芯片', rating: 4.9, type: 'digital', sales: '10 万+', salesPercent: 85, remaining: 1500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=iPhone15' },
  { id: 2, name: '华为 Mate 60 Pro', price: 6999, originalPrice: 7999, description: '512GB / 卫星通话 / 昆仑玻璃', rating: 4.8, type: 'digital', sales: '8 万+', salesPercent: 75, remaining: 2500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Mate60' },
  { id: 3, name: 'MacBook Pro 16 寸', price: 18999, originalPrice: 21999, description: 'M3 Max / 32GB / 1TB SSD', rating: 4.9, type: 'digital', sales: '5 万+', salesPercent: 65, remaining: 350, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=MBP16' },
  { id: 4, name: 'ThinkPad X1 Carbon', price: 12999, originalPrice: 15999, description: '14 寸 / i7 / 16GB / 512GB', rating: 4.7, type: 'digital', sales: '3 万+', salesPercent: 55, remaining: 450, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=ThinkPad' },
  { id: 5, name: '索尼 A7M4 相机', price: 16999, originalPrice: 19999, description: '全画幅 / 3300 万像素 / 4K60P', rating: 4.8, type: 'digital', sales: '2 万+', salesPercent: 50, remaining: 200, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=SonyA7' },
  { id: 6, name: '海尔冰箱 十字对开', price: 3999, originalPrice: 5999, description: '500L / 变频 / 除菌净味', rating: 4.7, type: 'appliance', sales: '15 万+', salesPercent: 90, remaining: 1000, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Haier' },
  { id: 7, name: '格力空调 1.5 匹', price: 2899, originalPrice: 3999, description: '变频冷暖 / 一级能效 / 智能控制', rating: 4.6, type: 'appliance', sales: '20 万+', salesPercent: 95, remaining: 500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Gree' },
  { id: 8, name: '戴森 V15 吸尘器', price: 4990, originalPrice: 5990, description: '激光探测 / 深层清洁 / 60 分钟续航', rating: 4.8, type: 'appliance', sales: '8 万+', salesPercent: 80, remaining: 800, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Dyson' },
  { id: 9, name: 'Nike Air Max 运动鞋', price: 899, originalPrice: 1299, description: '气垫减震 / 透气舒适 / 多色可选', rating: 4.5, type: 'fashion', sales: '12 万+', salesPercent: 70, remaining: 3000, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Nike' },
  { id: 10, name: '雅诗兰黛小棕瓶', price: 680, originalPrice: 880, description: '50ml / 修护精华 / 抗衰老', rating: 4.7, type: 'beauty', sales: '25 万+', salesPercent: 92, remaining: 800, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Estee' },
  { id: 11, name: '任天堂 Switch OLED', price: 2099, originalPrice: 2599, description: '7 寸 OLED / 64GB / 续航提升', rating: 4.8, type: 'digital', sales: '18 万+', salesPercent: 88, remaining: 1200, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Switch' },
  { id: 12, name: 'iPad Pro 12.9 寸', price: 8999, originalPrice: 10999, description: 'M2 芯片 / 256GB / Liquid 视网膜', rating: 4.9, type: 'digital', sales: '6 万+', salesPercent: 68, remaining: 320, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=iPadPro' },
  { id: 13, name: '小米电视 75 英寸', price: 3999, originalPrice: 5499, description: '4K 超清 / 120Hz / 远场语音', rating: 4.6, type: 'appliance', sales: '10 万+', salesPercent: 72, remaining: 2800, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=MiTV' },
  { id: 14, name: 'LV 经典钱包', price: 3500, originalPrice: 4800, description: '真皮 / 多卡位 / 经典老花', rating: 4.7, type: 'fashion', sales: '3 万+', salesPercent: 60, remaining: 400, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=LV' },
  { id: 15, name: '兰蔻小黑瓶', price: 1080, originalPrice: 1580, description: '100ml / 肌底液 / 修护维稳', rating: 4.8, type: 'beauty', sales: '30 万+', salesPercent: 96, remaining: 400, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Lancome' },
  { id: 16, name: 'Kindle Paperwhite', price: 1299, originalPrice: 1699, description: '6.8 寸 / 32GB / 防水设计', rating: 4.6, type: 'digital', sales: '8 万+', salesPercent: 65, remaining: 3500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Kindle' },
  { id: 17, name: '华为 MatePad Pro', price: 4999, originalPrice: 6499, description: '13.2 英寸 / 麒麟 9000S / 鸿蒙系统', rating: 4.8, type: 'digital', sales: '4 万+', salesPercent: 72, remaining: 2800, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=MatePad' },
  { id: 18, name: '西门子洗碗机', price: 5999, originalPrice: 8999, description: '12 套 / 智能洗 / 烘干', rating: 4.7, type: 'appliance', sales: '3 万+', salesPercent: 68, remaining: 3200, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=洗碗机' },
  { id: 19, name: '阿迪达斯跑鞋', price: 599, originalPrice: 899, description: '透气减震 / 轻便舒适', rating: 4.5, type: 'fashion', sales: '6 万+', salesPercent: 45, remaining: 5500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=阿迪达斯' },
  { id: 20, name: '兰蔻粉水', price: 420, originalPrice: 580, description: '400ml / 保湿补水', rating: 4.8, type: 'beauty', sales: '15 万+', salesPercent: 80, remaining: 2000, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=兰蔻' },
  { id: 21, name: 'AirPods Pro 2', price: 1899, originalPrice: 2399, description: '主动降噪 / 空间音频', rating: 4.9, type: 'digital', sales: '20 万+', salesPercent: 90, remaining: 1000, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=AirPods' },
  { id: 22, name: '美的微波炉', price: 699, originalPrice: 999, description: '智能菜单 / 易清洁', rating: 4.5, type: 'appliance', sales: '10 万+', salesPercent: 75, remaining: 2500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=美的' },
  { id: 23, name: '优衣库羽绒服', price: 599, originalPrice: 999, description: '轻薄保暖 / 90% 绒', rating: 4.6, type: 'fashion', sales: '10 万+', salesPercent: 75, remaining: 2500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=优衣库' },
  { id: 24, name: 'SK-II 神仙水', price: 1580, originalPrice: 2080, description: '230ml / 护肤精华', rating: 4.9, type: 'beauty', sales: '18 万+', salesPercent: 85, remaining: 1500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=SK-II' },
  { id: 25, name: '索尼 WH-1000XM5', price: 2499, originalPrice: 3299, description: '降噪耳机 / 30 小时续航', rating: 4.8, type: 'digital', sales: '5 万+', salesPercent: 60, remaining: 4000, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Sony' },
  { id: 26, name: '西门子洗衣机', price: 4599, originalPrice: 6999, description: '10kg / 智能洗 / 静音', rating: 4.7, type: 'appliance', sales: '4 万+', salesPercent: 50, remaining: 5000, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=西门子' },
  { id: 27, name: '北面冲锋衣', price: 1299, originalPrice: 1899, description: '防风防水 / 透气', rating: 4.7, type: 'fashion', sales: '8 万+', salesPercent: 70, remaining: 3000, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=北面' },
  { id: 28, name: '资生堂红腰子', price: 880, originalPrice: 1280, description: '50ml / 精华液', rating: 4.8, type: 'beauty', sales: '12 万+', salesPercent: 78, remaining: 2200, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=资生堂' },
  { id: 29, name: '华为 Watch GT4', price: 1688, originalPrice: 2188, description: '智能手表 / 健康监测', rating: 4.7, type: 'digital', sales: '7 万+', salesPercent: 65, remaining: 3500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=华为' },
  { id: 30, name: '戴森吹风机', price: 3990, originalPrice: 4990, description: '负离子 / 速干', rating: 4.9, type: 'appliance', sales: '9 万+', salesPercent: 82, remaining: 1800, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=戴森' },
  { id: 31, name: 'Coach 手提包', price: 2599, originalPrice: 3999, description: '真皮 / 时尚百搭', rating: 4.6, type: 'fashion', sales: '2 万+', salesPercent: 55, remaining: 4500, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=Coach' },
  { id: 32, name: '欧莱雅套装', price: 699, originalPrice: 999, description: '水乳套装 / 补水保湿', rating: 4.5, type: 'beauty', sales: '20 万+', salesPercent: 88, remaining: 1200, image: 'https://via.placeholder.com/250x250/f5f5f5/333?text=欧莱雅' },
])

const typeText = (type) => {
  const map = { digital: '数码', appliance: '家电', fashion: '服饰', beauty: '美妆' }
  return map[type] || '商品'
}

const getProgressColor = (percent) => {
  if (percent === 0) return '#999999'
  if (percent >= 80) return '#00ff88'
  if (percent >= 60) return '#00d4ff'
  if (percent >= 40) return '#ffdd00'
  if (percent >= 20) return '#ff8800'
  return '#ff3366'
}

const filteredProducts = computed(() => {
  let result = [...allProducts.value]

  // 分类筛选
  if (selectedCategory.value) {
    result = result.filter(p => p.category === selectedCategory.value)
  }

  // 价格筛选
  if (priceRange.value) {
    if (priceRange.value === '0-1000') {
      result = result.filter(p => p.price <= 1000)
    } else if (priceRange.value === '1000-5000') {
      result = result.filter(p => p.price > 1000 && p.price <= 5000)
    } else if (priceRange.value === '5000+') {
      result = result.filter(p => p.price > 5000)
    }
  }

  // 排序
  if (sortBy.value === 'price-asc') {
    result.sort((a, b) => a.price - b.price)
  } else if (sortBy.value === 'price-desc') {
    result.sort((a, b) => b.price - a.price)
  }

  return result
})

const totalProducts = computed(() => filteredProducts.value.length)

const goToDetail = (id) => {
  router.push(`/product/${id}`)
}

const addToCart = (product) => {
  cartStore.addToCart(product)
  ElMessage.success('已加入购物车')
}

// 格式化商品数据以适配 ProductCard 组件
const formatProduct = (product) => ({
  ...product,
  remaining: product.remaining || Math.round((100 - (product.salesPercent || 70)) * 10),
  soldPercent: product.salesPercent || 70
})

watch([selectedCategory, priceRange, sortBy], () => {
  currentPage.value = 1
})
</script>

<style scoped>
@import '@/assets/mall-style.css';

.product-list {
  min-height: 100vh;
  padding: 20px 0;
  background: linear-gradient(180deg, rgba(0,212,255,0.15) 0%, rgba(10,14,26,0.8) 100%);
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 筛选栏 */
.filter-bar {
  background: linear-gradient(90deg, rgba(0,212,255,0.1), rgba(0,255,136,0.05));
  padding: 20px 30px;
  border-radius: 8px;
  margin-bottom: 20px;
  border: 1px solid rgba(0,212,255,0.15);
}

.filter-item {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
}

.filter-item:last-child {
  margin-bottom: 0;
}

.filter-label {
  width: 60px;
  color: #ccc;
  font-size: 14px;
}

/* 商品网格 */
.products-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

/* 分页 */
.pagination {
  margin-top: 30px;
  display: flex;
  justify-content: center;
}
</style>
