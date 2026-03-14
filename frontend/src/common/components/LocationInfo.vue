<template>
  <div class="location-info" aria-label="位置信息">
    <el-icon class="location-icon"><Location /></el-icon>
    <span class="location-text">{{ location }}</span>
    <span class="location-status" :class="{ 'updating': isUpdating }"></span>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue'
import { Location } from '@element-plus/icons-vue'

const location = ref('获取中...')
const isUpdating = ref(false)

let timer = null

// 获取地理位置
const getLocation = async () => {
  isUpdating.value = true

  if (!navigator.geolocation) {
    location.value = '位置服务不可用'
    isUpdating.value = false
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
          parseAddress(data.address)
        } else {
          location.value = '位置获取成功'
        }
      } catch (error) {
        console.error('位置解析失败:', error)
        location.value = '位置获取成功'
      } finally {
        isUpdating.value = false
      }
    },
    (error) => {
      console.error('获取位置失败:', error)
      location.value = '位置权限未开启'
      isUpdating.value = false
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 60000
    }
  )
}

// 解析地址
const parseAddress = (address) => {
  const { state, city, county, district, town, suburb } = address

  const province = state || ''
  const cityInfo = city || county || district || ''
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

  isUpdating.value = false
}

onMounted(() => {
  getLocation()
  timer = setInterval(getLocation, 60000) // 每 60 秒更新一次
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.location-info {
  display: flex;
  align-items: center;
  gap: 5px;
  color: var(--mall-primary);
  font-size: 13px;
}

.location-icon {
  font-size: 14px;
}

.location-text {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.location-status {
  width: 6px;
  height: 6px;
  border-radius: 50%;
  background: rgba(0, 212, 255, 0.3);
  transition: all 0.3s;
}

.location-status.updating {
  background: #00ff88;
  box-shadow: 0 0 8px rgba(0, 255, 136, 0.6);
  animation: pulse 1s infinite;
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
    transform: scale(1);
  }
  50% {
    opacity: 0.5;
    transform: scale(1.2);
  }
}
</style>
