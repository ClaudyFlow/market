<template>
  <div class="位置信息">
    <i class="位置图标 fas fa-map-marker-alt"></i>
    <span class="位置文本">{{ locationText }}</span>
    <StatusDot :status="status" />
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from "vue"
// Font Awesome 图标直接使用类名，无需导入
import StatusDot from "./StatusDot.vue"

type LocationStatus = 'loading' | 'stuck' | 'success' | 'timeout' | 'error'

interface Address {
  country?: string;
  state?: string;
  province?: string;
  city?: string;
  district?: string;
  county?: string;
  town?: string;
  village?: string;
  suburb?: string;
  [key: string]: string | undefined;
}

interface ReverseGeoResponse {
  address?: Address;
}

const locationText = ref<string>("获取中...")
const status = ref<LocationStatus>('loading')

// ================================
// 测试功能:测试其他国家/地区的地址解析
// 在控制台输出测试结果
// ================================
const 测试其他国家地址 = () => {
  console.log("\n=== 🌍 开始测试其他国家/地区地址解析 ===\n");

  // 测试数据:不同国家的地址格式
  const 测试地址列表 = [
    {
      国家: "美国",
      数据: {
        country: "United States",
        state: "California",
        county: "San Francisco County",
        city: "San Francisco",
        suburb: "Mission District",
      },
    },
    {
      国家: "日本",
      数据: {
        country: "日本",
        state: "東京都",
        city: "渋谷区",
        suburb: "原宿",
      },
    },
    {
      国家: "英国",
      数据: {
        country: "United Kingdom",
        state: "England",
        county: "Greater London",
        city: "London",
        suburb: "Westminster",
      },
    },
    {
      国家: "澳大利亚",
      数据: {
        country: "Australia",
        state: "New South Wales",
        city: "Sydney",
        suburb: "Bondi Beach",
      },
    },
    {
      国家: "加拿大",
      数据: {
        country: "Canada",
        province: "Ontario",
        city: "Toronto",
        suburb: "Downtown Toronto",
      },
    },
    {
      国家: "德国",
      数据: {
        country: "Deutschland",
        state: "Bayern",
        city: "München",
        suburb: "Altstadt-Lehel",
      },
    },
    {
      国家: "法国",
      数据: {
        country: "France",
        state: "Île-de-France",
        city: "Paris",
        suburb: "Le Marais",
      },
    },
    {
      国家: "韩国",
      数据: {
        country: "대한민국",
        state: "서울특별시",
        city: "강남구",
        suburb: "역삼동",
      },
    },
  ];

  测试地址列表.forEach((测试项, 索引) => {
    const 结果 = getCountryProvinceCityDistrict(测试项.数据 as Address);
    console.log(`【测试${索引 + 1}】${测试项.国家}`);
    console.log(`  原始数据:`, 测试项.数据);
    console.log(`  解析结果:${结果}`);
    console.log("");
  });

  console.log("=== ✅ 测试完成 ===\n");
};

// 开发环境下自动运行测试
const 运行测试 = () => {
  if (typeof window !== "undefined" && (window as any).__RUN_LOCATION_TEST__) {
    console.log("🔧 检测到测试模式,正在运行位置解析测试...");
    测试其他国家地址();
  }
};

const getCountryProvinceCityDistrict = (address: Address | null): string => {
  if (!address || typeof address !== "object") return "无法获取地址信息";

  const parts: string[] = [];
  if (address.country) parts.push(address.country);
  if (address.state) parts.push(address.state);
  else if (address.province) parts.push(address.province);
  if (address.city) parts.push(address.city);
  if (address.district) parts.push(address.district);
  else if (address.county) parts.push(address.county);
  else if (address.suburb) parts.push(address.suburb);

  return parts.join("");
};

const getLocation = async (): Promise<void> => {
  // 2 秒后如果还没获取到,显示卡顿状态(黄点)
  const stuckTimer = setTimeout(() => {
    if (status.value === 'loading') {
      status.value = 'stuck'
      locationText.value = '定位中...'
    }
  }, 2000)

  if (!navigator.geolocation) {
    locationText.value = '位置服务不可用'
    status.value = 'error'
    clearTimeout(stuckTimer)
    return
  }

  navigator.geolocation.getCurrentPosition(
    async (position) => {
      clearTimeout(stuckTimer)
      const { latitude: lat, longitude: lng } = position.coords

      try {
        const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lng}&zoom=18&addressdetails=1&accept-language=zh-CN`

        const response = await fetch(url, {
          headers: {
            'User-Agent': 'VueLocationComponent/1.0',
          },
        })

        const data: ReverseGeoResponse = await response.json()
        const simpleString = getCountryProvinceCityDistrict(data.address || null)

        if (simpleString && simpleString !== '无法获取地址信息') {
          locationText.value = simpleString
          console.log('✅ 国家省市区信息:', locationText.value)
        } else {
          locationText.value = '位置获取成功'
        }

        status.value = 'success'
      } catch (error) {
        locationText.value = '位置获取成功'
        status.value = 'success'
      }
    },
    (error) => {
      clearTimeout(stuckTimer)
      let errorMsg = '位置权限未开启'
      if (error.code === error.TIMEOUT) {
        errorMsg = '定位超时'
        status.value = 'timeout'
      } else if (error.code === error.PERMISSION_DENIED) {
        errorMsg = '用户拒绝授权'
        status.value = 'error'
      } else {
        status.value = 'error'
      }
      locationText.value = errorMsg
    },
    {
      enableHighAccuracy: true,
      timeout: 10000,
      maximumAge: 60000,
    }
  )
}

onMounted(() => {
  getLocation();
  运行测试();
});

// 暴露测试方法到全局,方便在控制台手动调用
if (typeof window !== "undefined") {
  (window as any).测试位置信息 = () => {
    测试其他国家地址();
  };
  console.log("💡 提示:在控制台输入 '测试位置信息 ()' 可以手动运行测试");
}
</script>

<style scoped>
.位置信息 {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--mall-primary, #00d4ff);
  font-size: 13px;
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue",
    Arial, sans-serif;
}
.位置图标 {
  font-size: 14px;
}
.位置文本 {
  max-width: 200px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>
