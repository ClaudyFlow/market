<template>
  <header class="header">
    <!-- 顶部信息栏 -->
    <div class="top-info">
      <div class="container">
        <div class="server-info">
          <div class="location-info">
            <el-icon><Location /></el-icon>
            <span>{{ location }}</span>
          </div>
          <span class="divider">|</span>
          <div class="online-info">
            <el-icon><Connection /></el-icon>
            <span class="online-count">在线人数 {{ onlineCount.toLocaleString() }}</span>
          </div>
        </div>
        <div class="time-center">
          <div class="time-standard">{{ standardTime }}</div>
          <div class="time-local">{{ localTime }}</div>
        </div>
        <div class="user-info">
          <span class="username">
            <el-avatar :size="24" src="https://via.placeholder.com/24x24/00d4ff/fff?text=U">
              <el-icon><User /></el-icon>
            </el-avatar>
            尊敬的会员
          </span>
          <span class="points">
            <el-icon><Trophy /></el-icon> 积分：{{ userPoints }}
          </span>
        </div>
      </div>
    </div>

    <!-- 主导航 -->
    <div class="main-nav">
      <div class="container">
        <router-link to="/" class="logo">
          <div class="logo-icon">
            <el-icon size="32"><ShoppingCart /></el-icon>
          </div>
          <div class="logo-text">
            <span class="logo-title">购物商城</span>
            <span class="logo-subtitle">
              <span>品质生活</span>
              <span>精选好物</span>
            </span>
          </div>
        </router-link>

        <nav class="nav-links">
          <router-link to="/" class="nav-item" active-class="active">
            <el-icon><HomeFilled /></el-icon>
            <span>首页</span>
          </router-link>
          <router-link to="/items" class="nav-item" active-class="active">
            <el-icon><Box /></el-icon>
            <span>全部商品</span>
          </router-link>
          <router-link to="/digital" class="nav-item" active-class="active">
            <el-icon><Cellphone /></el-icon>
            <span>数码电器</span>
          </router-link>
          <router-link to="/fashion" class="nav-item" active-class="active">
            <el-icon><ShoppingBag /></el-icon>
            <span>服饰鞋包</span>
          </router-link>
          <router-link to="/home" class="nav-item" active-class="active">
            <el-icon><House /></el-icon>
            <span>家居家装</span>
          </router-link>
          <router-link to="/sale" class="nav-item" active-class="active">
            <el-icon><Timer /></el-icon>
            <span>限时特惠</span>
          </router-link>
        </nav>

        <div class="search-box">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索商品..."
            size="small"
            round
            @keyup.enter="handleSearch"
          >
            <template #append>
              <el-button @click="handleSearch" round>
                <el-icon><Search /></el-icon>
              </el-button>
            </template>
          </el-input>
        </div>

        <div class="cart-icon" @click="router.push('/cart')">
          <el-icon size="22"><ShoppingCart /></el-icon>
          <span class="cart-count" v-if="cartCount > 0">{{ cartCount }}</span>
        </div>
      </div>
    </div>
  </header>
</template>

<script setup>
import { ref, onMounted, onUnmounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useCartStore } from '@/stores/cart'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const cartStore = useCartStore()
const userStore = useUserStore()

const searchKeyword = ref('')
const location = ref('获取中...')
const onlineCount = ref(128456)
const standardTime = ref('')
const localTime = ref('')

// 购物车数量
const cartCount = computed(() => cartStore.totalCount)
// 用户积分
const userPoints = computed(() => userStore.userPoints)

let onlineTimer = null
let timeTimer = null
let locationTimer = null

// 更新时间
const updateTime = () => {
  const now = new Date()
  const year = now.getFullYear()
  const month = String(now.getMonth() + 1).padStart(2, '0')
  const day = String(now.getDate()).padStart(2, '0')
  const hours = String(now.getHours()).padStart(2, '0')
  const minutes = String(now.getMinutes()).padStart(2, '0')
  const seconds = String(now.getSeconds()).padStart(2, '0')

  // UTC0 时间
  const utcYear = now.getUTCFullYear()
  const utcMonth = String(now.getUTCMonth() + 1).padStart(2, '0')
  const utcDay = String(now.getUTCDate()).padStart(2, '0')
  const utcHours = String(now.getUTCHours()).padStart(2, '0')
  const utcMinutes = String(now.getUTCMinutes()).padStart(2, '0')
  const utcSeconds = String(now.getUTCSeconds()).padStart(2, '0')

  standardTime.value = `UTC+00:00 ${utcYear}-${utcMonth}-${utcDay} ${utcHours}:${utcMinutes}:${utcSeconds}`

  // 当地时间

  const offset = -now.getTimezoneOffset() / 60
  const offsetSign = offset >= 0 ? '+' : '-'
  const offsetHours = String(Math.abs(Math.floor(offset))).padStart(2, '0')
  const offsetMinutes = String(Math.abs(Math.round((offset % 1) * 60))).padStart(2, '0')

  localTime.value = `UTC${offsetSign}${offsetHours}:${offsetMinutes} ${year}-${month}-${day} ${hours}:${minutes}:${seconds}`
}

// 获取真实位置（使用 Geolocation API + 反向地理编码）
const getLocation = () => {
  if (!navigator.geolocation) {
    location.value = '位置服务不可用'
    return
  }
  
  navigator.geolocation.getCurrentPosition(
    async (position) => {
      const lat = position.coords.latitude
      const lng = position.coords.longitude
      
      try {
        // 使用 OpenStreetMap Nominatim API 进行反向地理编码
        const response = await fetch(
          `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=10&accept-language=zh-CN`
        )
        const data = await response.json()

        if (data && data.address) {
          const { state, city, county, district, town, suburb, road } = data.address
          // 组合省市区格式
          const province = state || ''
          // 直辖市情况下，city 可能为空，使用 county 或 district 代替
          const cityInfo = city || county || district || ''
          // 区/县信息
          const districtInfo = (district && district !== cityInfo) ? district : (town || suburb || '')
          
          if (province && cityInfo && districtInfo && districtInfo !== province) {
            location.value = `${province}${cityInfo}${districtInfo}`
          } else if (province && cityInfo) {
            location.value = `${province}${cityInfo}`
          } else if (province) {
            location.value = province
          } else {
            location.value = '位置获取成功'
          }
        }
      } catch (error) {
        console.error('位置解析失败:', error)
        location.value = '位置获取成功'
      }
    },
    (error) => {
      console.error('获取位置失败:', error)
      location.value = '位置权限未开启'
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 60000
    }
  )
}

// 模拟在线人数更新
const updateOnlineCount = () => {
  const change = Math.floor((Math.random() - 0.5) * 100)
  onlineCount.value = Math.max(10000, onlineCount.value + change)
}

onMounted(() => {
  updateTime()
  getLocation()
  updateOnlineCount()
  timeTimer = setInterval(updateTime, 1000)
  locationTimer = setInterval(getLocation, 60000)  // 每 60 秒更新一次位置
  onlineTimer = setInterval(updateOnlineCount, 2000)
})

onUnmounted(() => {
  if (timeTimer) clearInterval(timeTimer)
  if (locationTimer) clearInterval(locationTimer)
  if (onlineTimer) clearInterval(onlineTimer)
})

const handleSearch = () => {
  if (searchKeyword.value.trim()) {
    router.push({ path: '/items', query: { keyword: searchKeyword.value } })
  }
}
</script>

<style scoped>
.header {
  background: linear-gradient(180deg, #1a2a4a 0%, #0d1a2a 100%);
  border-bottom: 1px solid rgba(0,212,255,0.3);
  position: sticky;
  top: 0;
  z-index: 1000;
}

/* 顶部信息栏 */
.top-info {
  background: rgba(10,14,26,1);
  padding: 8px 0;
  font-size: 13px;
}

.top-info > .container {
  display: flex;
  justify-content: space-between;
  align-items: center;
  position: relative;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.server-info {
  display: flex;
  align-items: center;
  gap: 15px;
  color: var(--mall-primary);
  flex: 1;
}

.time-center {
  position: absolute;
  left: 50%;
  top: 50%;
  transform: translate(-50%, -50%);
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 4px;
  white-space: nowrap;
}

.time-standard {
  color: #00d4ff;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.time-local {
  color: #00ff88;
  font-family: 'Courier New', monospace;
  font-size: 13px;
}

.location-info, .online-info {
  display: flex;
  align-items: center;
  gap: 5px;
}

.divider {
  color: rgba(255,255,255,0.3);
}

.online-count {
  color: var(--mall-secondary);
}

.user-info {
  display: flex;
  align-items: center;
  gap: 20px;
}

.username {
  display: flex;
  align-items: center;
  gap: 8px;
  color: #fff;
}

.points {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--mall-accent);
  font-weight: bold;
}

/* 主导航 */
.main-nav {
  padding: 15px 0;
}

.main-nav > .container {
  display: flex;
  align-items: center;
  gap: 40px;
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.logo {
  display: flex;
  align-items: center;
  gap: 12px;
  text-decoration: none;
  color: #fff;
}

.logo-icon {
  width: 50px;
  height: 50px;
  background: linear-gradient(135deg, var(--mall-primary), var(--mall-secondary));
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 0 20px rgba(0,212,255,0.5);
}

.logo-text {
  display: flex;
  flex-direction: column;
  justify-content: center;
}

.logo-title {
  font-size: 24px;
  font-weight: bold;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  letter-spacing: 2px;
  text-align: left;
}

.logo-subtitle {
  display: flex;
  flex-direction: column;
  font-size: 12px;
  color: #888;
  letter-spacing: 2px;
  line-height: 1.4;
}

.nav-links {
  display: flex;
  gap: 10px;
  flex: 1;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 10px 15px;
  color: #fff;
  text-decoration: none;
  border-radius: 8px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
  white-space: nowrap;
}

.nav-item::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, rgba(0,212,255,0.2), rgba(0,255,136,0.2));
  opacity: 0;
  transition: opacity 0.3s;
}

.nav-item:hover {
  color: var(--mall-primary);
}

.nav-item:hover::before {
  opacity: 1;
}

.nav-item.active {
  color: var(--mall-primary);
  background: rgba(0,212,255,0.15);
  box-shadow: 0 0 15px rgba(0,212,255,0.3);
}

.nav-item .el-icon {
  font-size: 18px;
}

.search-box {
  width: 250px;
}

.search-box :deep(.el-input__wrapper) {
  background: rgba(255,255,255,0.1);
  border: 1px solid rgba(0,212,255,0.3);
}

.search-box :deep(.el-input__inner) {
  color: #fff;
}

.search-box :deep(.el-input-group__append) {
  background: var(--mall-primary);
  color: #000;
  border: none;
}

.search-box :deep(.el-input-group__append:hover) {
  background: var(--mall-secondary);
}

.cart-icon {
  position: relative;
  width: 45px;
  height: 45px;
  background: linear-gradient(135deg, rgba(0,212,255,0.2), rgba(0,255,136,0.2));
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: var(--mall-primary);
  cursor: pointer;
  transition: all 0.3s;
  margin-left: 15px;
}

.cart-icon:hover {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  color: #000;
  box-shadow: 0 0 20px rgba(0,212,255,0.5);
  transform: scale(1.1);
}

.cart-count {
  position: absolute;
  top: -5px;
  right: -5px;
  background: linear-gradient(135deg, #ff6600, #ff8800);
  color: #fff;
  font-size: 11px;
  padding: 2px 6px;
  border-radius: 10px;
  font-weight: bold;
  border: 2px solid #1a2a4a;
}
</style>
