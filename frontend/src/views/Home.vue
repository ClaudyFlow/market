<template>
  <section class="mall-home">
    <!-- 左侧分类导航 -->
    <CategoryPanel />

    <!-- 中间：轮播图 + 快捷操作 -->
    <div class="banner-section">
      <el-carousel height="420px" aria-label="促销轮播">
        <el-carousel-item v-for="(banner, index) in banners" :key="index">
          <article class="banner-item" :style="{ background: banner.gradient }">
            <div class="banner-info">
              <h2 class="banner-title">{{ banner.title }}</h2>
              <p class="banner-subtitle">{{ banner.subtitle }}</p>
              <el-button type="primary" size="large" class="glow-btn">
                立即查看
              </el-button>
            </div>
          </article>
        </el-carousel-item>
      </el-carousel>

      <!-- 快捷操作 -->
      <nav class="quick-actions-bar" aria-label="快捷操作">
        <button class="action-item" @click="router.push('/items')" aria-label="浏览商品">
          <el-icon><Box /></el-icon>
          <span>商品</span>
        </button>
        <button class="action-item" @click="router.push('/order')" aria-label="查看订单">
          <el-icon><Document /></el-icon>
          <span>订单</span>
        </button>
        <button
          class="action-item"
          @click="router.push('/coupon')"
          aria-label="查看优惠券"
        >
          <el-icon><Ticket /></el-icon>
          <span>优惠券</span>
        </button>
        <button
          class="action-item"
          @click="router.push('/address')"
          aria-label="管理地址"
        >
          <el-icon><Location /></el-icon>
          <span>地址</span>
        </button>
      </nav>
    </div>

    <!-- 右侧用户信息 -->
    <div class="user-panel" aria-label="用户信息">
      <article class="user-card">
        <figure class="user-avatar">
          <el-avatar
            :size="80"
            src="https://via.placeholder.com/80x80/00d4ff/fff?text=VIP"
          >
            <el-icon size="40"><User /></el-icon>
          </el-avatar>
        </figure>
        <h3 class="user-name">尊敬的会员</h3>
        <div class="user-level">
          <span class="level-badge">LV 8</span>
        </div>
        <div class="user-vip">
          <span class="vip-tag">{{ userStore.vipLevelName }}</span>
        </div>
        <div class="user-points">
          <span class="points-tag">
            <el-icon><Trophy /></el-icon> {{ userStore.userPoints }} 积分
          </span>
        </div>
        <button
          class="check-in-btn"
          :class="{ 'checked-in': userStore.hasCheckedIn }"
          @click="handleCheckIn"
          :aria-label="userStore.hasCheckedIn ? '已签到' : '立即签到'"
        >
          <el-icon><Calendar /></el-icon>
          <span>{{ userStore.hasCheckedIn ? "已签到" : "立即签到" }}</span>
          <span class="check-in-reward" v-if="userStore.hasCheckedIn">+10 积分</span>
        </button>
      </article>

      <div class="announcement-wrapper">
        <AnnouncementPanel />
      </div>
    </div>
  </section>

  <!-- 限时特惠 -->
  <section class="flash-sale-section" aria-label="限时特惠">
    <div class="container">
      <header class="section-header">
        <div class="section-title">
          <el-icon><Timer /></el-icon>
          <h2>限时特惠</h2>
        </div>
      </header>
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
        <ProductCard
          v-for="item in flashItems"
          :key="item.id"
          :product="item"
          @click="goToDetail(item.id)"
          @add-to-cart="addToCart"
        />
      </div>
    </div>
  </section>

  <!-- 热门推荐 -->
  <section class="recommended-section" aria-label="热门推荐">
    <div class="container">
      <header class="section-header">
        <div class="section-title">
          <el-icon><StarFilled /></el-icon>
          <h2>热门推荐</h2>
        </div>
      </header>
      <div class="recommend-grid">
        <ProductCard
          v-for="item in recommendedItems"
          :key="item.id"
          :product="item"
          @click="goToDetail(item.id)"
          @add-to-cart="addToCart"
        />
      </div>
    </div>
  </section>

  <!-- 品牌精选 -->
  <section class="brand-section" aria-label="品牌精选">
    <div class="container">
      <header class="section-header">
        <div class="section-title">
          <el-icon><Shop /></el-icon>
          <h2>品牌精选</h2>
        </div>
      </header>
      <div class="brand-grid">
        <article class="brand-card" v-for="brand in brands" :key="brand.name">
          <div class="brand-content">
            <div class="brand-logo">{{ brand.name }}</div>
            <p class="brand-desc">{{ brand.description }}</p>
          </div>
          <div class="brand-overlay">
            <el-button class="brand-btn">进入店铺</el-button>
          </div>
        </article>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from "vue";
import { useRouter } from "vue-router";
import { useCartStore } from "@/stores/cart";
import { useUserStore } from "@/stores/user";
import { banners } from "@/data/categories";
import ProductCard from "@/components/ProductCard.vue";
import CategoryPanel from "@/components/CategoryPanel.vue";
import AnnouncementPanel from "@/components/AnnouncementPanel.vue";

const router = useRouter();
const cartStore = useCartStore();
const userStore = useUserStore();
const remainingPercent = ref(42);

// 签到处理
const handleCheckIn = async () => {
  await userStore.doCheckIn();
};

// 倒计时
const countdown = ref({ hours: "04", minutes: "30", seconds: "00" });

// 限时特惠商品 - 15 个（使用 soldCount 和 remainCount 格式）
const flashItems = ref([
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
]);

// 推荐商品 - 15 个
const recommendedItems = ref([
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
]);

// 品牌数据 - 15 个
const brands = ref([
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
]);

let timer = null;
const updateCountdown = () => {
  const now = new Date();
  const target = new Date();
  target.setHours(23, 59, 59);
  const diff = target - now;
  if (diff > 0) {
    const hours = Math.floor(diff / 1000 / 60 / 60);
    const minutes = Math.floor((diff / 1000 / 60) % 60);
    const seconds = Math.floor((diff / 1000) % 60);
    countdown.value = {
      hours: String(hours).padStart(2, "0"),
      minutes: String(minutes).padStart(2, "0"),
      seconds: String(seconds).padStart(2, "0"),
    };
  }
};

const goToDetail = (id) => {
  router.push(`/item/${id}`);
};

const addToCart = (item) => {
  cartStore.addToCart({ ...item, quantity: 1 });
  ElMessage.success("已加入购物车");
};

onMounted(() => {
  updateCountdown();
  timer = setInterval(updateCountdown, 1000);
});

onUnmounted(() => {
  if (timer) clearInterval(timer);
});
</script>

<style scoped>
@import "@/assets/mall-style.css";

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

/* 中间横幅区域 */
section.banner-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

/* 主容器 */
section.mall-home {
  display: grid;
  grid-template-columns: 200px 1fr 280px;
  gap: 10px;
  padding: 15px 20px;
  max-width: 1400px;
  margin: 0 auto;
  align-items: stretch;
}

/* 轮播图 */
.el-carousel {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.2);
  height: 380px;
}

.banner-item {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.banner-info {
  text-align: center;
  color: #fff;
}

.banner-title {
  font-size: 42px;
  margin-bottom: 15px;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.banner-subtitle {
  font-size: 20px;
  margin-bottom: 25px;
  color: #aaa;
}

.glow-btn {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
  color: #000;
  font-weight: bold;
}

.glow-btn:hover {
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.8);
}

/* 用户面板 */
aside.user-panel {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.announcement-wrapper {
  margin-top: 20px;
}

/* 用户卡片 */
article.user-card {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

figure.user-avatar :deep(.el-avatar) {
  border: 3px solid var(--mall-primary);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.user-name {
  font-size: 16px;
  margin-bottom: 8px;
}

.user-level {
  display: flex;
  justify-content: center;
  gap: 8px;
  align-items: center;
  margin-bottom: 8px;
}

.level-badge {
  background: linear-gradient(90deg, #ff6600, #ff8800);
  padding: 2px 10px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.user-vip {
  margin-bottom: 8px;
}

.vip-tag {
  background: linear-gradient(90deg, #ffd700, #ffaa00);
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: #000;
}

.user-points {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 5px;
}

.user-points .points-tag {
  display: inline-flex;
  align-items: center;
  gap: 4px;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  padding: 2px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
  color: #000;
}

.check-in-btn {
  margin-top: 15px;
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(0, 255, 136, 0.2));
  border: 1px solid rgba(0, 212, 255, 0.4);
  border-radius: 8px;
  padding: 10px 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  cursor: pointer;
  transition: all 0.3s;
  color: var(--mall-primary);
  font-size: 14px;
  font-weight: bold;
  width: 100%;
}

.check-in-btn:hover {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  color: #000;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
  transform: translateY(-2px);
}

.check-in-btn.checked-in {
  background: #cccccc;
  border-color: #cccccc;
  color: #666666;
  cursor: default;
  pointer-events: none;
}

.check-in-btn.checked-in:hover {
  background: #cccccc;
  color: #666666;
  box-shadow: none;
  transform: none;
}

.check-in-btn .el-icon {
  font-size: 18px;
}

.check-in-reward {
  background: linear-gradient(90deg, #ff6600, #ff8800);
  color: #fff;
  padding: 2px 8px;
  border-radius: 10px;
  font-size: 11px;
  font-weight: bold;
  margin-left: 5px;
}

/* 快捷操作栏 */
nav.quick-actions-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 8px;
  padding: 8px;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.08), rgba(0, 255, 136, 0.05));
  border-radius: 8px;
  margin-top: 15px;
}

button.action-item {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 6px;
  padding: 12px 8px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 6px;
  cursor: pointer;
  transition: all 0.3s;
}

button.action-item:hover {
  background: linear-gradient(135deg, rgba(0, 212, 255, 0.2), rgba(0, 255, 136, 0.2));
  border-color: var(--mall-primary);
  transform: translateY(-2px);
  box-shadow: 0 5px 15px rgba(0, 212, 255, 0.3);
}

button.action-item .el-icon {
  font-size: 28px;
  color: var(--mall-primary);
}

button.action-item span {
  font-size: 14px;
  color: #ccc;
}

/* 限时特惠 */
section.flash-sale-section {
  background: linear-gradient(
    180deg,
    rgba(0, 212, 255, 0.15) 0%,
    rgba(10, 14, 26, 0.8) 100%
  );
  padding: 40px 0;
  border-top: 1px solid rgba(0, 212, 255, 0.2);
}

.section-header {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 15px;
  margin-bottom: 25px;
  background: linear-gradient(90deg, rgba(0, 212, 255, 0.1), rgba(0, 255, 136, 0.05));
  padding: 15px 30px;
  border-radius: 8px;
  border: 1px solid rgba(0, 212, 255, 0.15);
}

.section-title {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-title h2 {
  font-size: 24px;
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.section-title .el-icon {
  font-size: 28px;
  color: var(--mall-accent);
}

.countdown-bar {
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
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

/* 限时特惠卡片 */
article.sale-card {
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
}

article.sale-card:hover {
  transform: translateY(-8px);
  border-color: var(--mall-primary);
  box-shadow: 0 10px 40px rgba(0, 212, 255, 0.2);
}

.sale-image {
  position: relative;
  padding: 20px;
  background: rgba(0, 0, 0, 0.3);
}

.sale-image img {
  width: 100%;
  display: block;
}

.sale-tag {
  display: inline-block;
  background: linear-gradient(135deg, var(--mall-accent), #ff8800);
  padding: 4px 12px;
  border-radius: 20px;
  margin-bottom: 10px;
}

.sale-tag .discount {
  color: #fff;
  font-size: 14px;
  font-weight: bold;
}

.sale-info {
  display: block;
  padding: 15px;
}

.sale-content {
  width: 100%;
}

.progress-container {
  margin-bottom: 8px;
}

.progress-container :deep(.el-progress__text) {
  text-align: right;
  font-size: 12px !important;
  color: var(--mall-accent);
  font-weight: bold;
}

.sale-action {
  margin-top: 10px;
}

.full-width-progress {
  width: 100%;
}

.sale-action {
  margin-top: 10px;
}

.sale-action .el-button {
  width: 100%;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

.sale-action .el-button:hover {
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.6);
}

.sale-action .sold-out-btn {
  width: 100%;
  background: #cccccc;
  border: none;
  color: #000000;
  cursor: default;
  box-shadow: none;
  pointer-events: none;
}

.sale-action .sold-out-btn:hover {
  background: #cccccc;
  box-shadow: none;
  transform: none;
}

.item-name {
  font-size: 14px;
  color: #fff;
  margin-bottom: 8px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
}

.price-row {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 10px;
}

.current-price {
  color: var(--mall-accent);
  font-size: 20px;
  font-weight: bold;
}

.original-price {
  color: #666;
  font-size: 13px;
  text-decoration: line-through;
}

.sold-info {
  font-size: 12px;
  color: #888;
}

/* 热门推荐 */
section.recommended-section {
  background: linear-gradient(
    180deg,
    rgba(0, 212, 255, 0.15) 0%,
    rgba(10, 14, 26, 0.8) 100%
  );
  padding: 40px 0;
  border-top: 1px solid rgba(0, 212, 255, 0.2);
}

.recommend-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
}

/* 品牌精选 */
section.brand-section {
  background: linear-gradient(
    180deg,
    rgba(0, 212, 255, 0.15) 0%,
    rgba(10, 14, 26, 0.8) 100%
  );
  padding: 40px 0;
  border-top: 1px solid rgba(0, 212, 255, 0.2);
}

.brand-grid {
  display: grid;
  grid-template-columns: repeat(5, 1fr);
  gap: 15px;
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
}

article.brand-card:hover {
  transform: translateY(-5px);
  border-color: var(--mall-primary);
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
  margin-top: 5px;
}

article.brand-card:hover .brand-overlay {
  opacity: 1;
  height: auto;
}

.brand-logo {
  font-size: 22px;
  font-weight: bold;
  color: var(--mall-primary);
  margin-bottom: 8px;
}

.brand-desc {
  font-size: 13px;
  color: #888;
  margin-bottom: 0;
}

.brand-btn {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
  padding: 8px 25px;
  border-radius: 6px;
  transition: all 0.3s;
}

.brand-btn:hover {
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}
</style>
