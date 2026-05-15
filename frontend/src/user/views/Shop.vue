<template>
  <div class="shop-page">
    <!-- 1. 店铺头部横幅 -->
    <header class="shop-banner">
      <div class="banner-content">
        <!-- 返回按钮 -->
        <div class="back-btn" @click="goBack">
          <el-icon><ArrowLeft /></el-icon> 返回
        </div>
        
        <div class="shop-logo-area">
          <div class="shop-avatar">
            {{ shopInfo.name.charAt(0) }}
          </div>
          <div class="shop-name-wrapper">
            <h1 class="shop-name">{{ shopInfo.name }} 官方旗舰店</h1>
            <div class="shop-tags">
              <span class="tag">官方</span>
              <span class="tag">正品</span>
              <span class="tag">极速发货</span>
            </div>
          </div>
        </div>
        <div class="shop-actions">
          <div class="shop-stats">
            <div class="stat-item">
              <span class="stat-num">{{ formatNumber(shopInfo.followers) }}</span>
              <span class="stat-label">粉丝</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ shopInfo.productCount }}</span>
              <span class="stat-label">宝贝</span>
            </div>
            <div class="stat-item">
              <span class="stat-num">{{ shopInfo.rating }}</span>
              <span class="stat-label">评分</span>
            </div>
          </div>
          <el-button 
            :type="isFollowed ? 'success' : 'primary'" 
            class="follow-btn"
            @click="toggleFollow"
          >
            <el-icon><Star /></el-icon>
            {{ isFollowed ? '已关注' : '关注店铺' }}
          </el-button>
        </div>
      </div>
    </header>

    <!-- 2. 店铺导航栏 -->
    <nav class="shop-nav">
      <div class="nav-content">
        <router-link to="/" class="nav-item">
          <el-icon><HomeFilled /></el-icon> 首页
        </router-link>
        <a href="javascript:;" class="nav-item active">
          <el-icon><ShoppingBag /></el-icon> 全部宝贝
        </a>
        <a href="javascript:;" class="nav-item">
          <el-icon><FolderOpened /></el-icon> 店铺分类
        </a>
        <a href="javascript:;" class="nav-item">
          <el-icon><ChatDotRound /></el-icon> 联系我们
        </a>
      </div>
    </nav>

    <SectionContainer>
      <div class="shop-main">
        <!-- 3. 左侧边栏 -->
        <aside class="shop-sidebar">
          <div class="sidebar-card">
            <h3 class="sidebar-title">店铺分类</h3>
            <ul class="category-list">
              <li 
                v-for="cat in categories" 
                :key="cat.id"
                :class="{ active: currentCategory === cat.id }"
                @click="selectCategory(cat.id)"
              >
                {{ cat.name }}
                <span class="count">({{ cat.count }})</span>
              </li>
            </ul>
          </div>

          <div class="sidebar-card">
            <h3 class="sidebar-title">联系客服</h3>
            <div class="service-area">
              <el-button class="service-btn" @click="openChat">
                <el-icon><ChatLineRound /></el-icon> 在线咨询
              </el-button>
              <p class="service-time">工作时间：9:00-22:00</p>
            </div>
          </div>

          <div class="sidebar-card">
            <h3 class="sidebar-title">店铺信息</h3>
            <div class="shop-info-detail">
              <div class="info-row">
                <span class="label">主营：</span>
                <span class="value">{{ shopInfo.category }}</span>
              </div>
              <div class="info-row">
                <span class="label">所在地：</span>
                <span class="value">{{ shopInfo.location }}</span>
              </div>
            </div>
          </div>

          <!-- 悬浮工具栏 -->
          <div class="float-tools">
            <div class="tool-item" @click="scrollToTop">
              <el-icon><Top /></el-icon>
              <span>顶部</span>
            </div>
          </div>
        </aside>

        <!-- 4. 右侧主内容区 -->
        <main class="shop-content">
          <!-- 搜索与排序 -->
          <div class="search-sort-bar">
            <div class="search-box">
              <el-input
                v-model="searchQuery"
                placeholder="搜索店内商品..."
                class="shop-search-input"
                clearable
                @keyup.enter="handleSearch"
              >
                <template #prefix>
                  <el-icon><Search /></el-icon>
                </template>
                <template #append>
                  <el-button @click="handleSearch">搜索</el-button>
                </template>
              </el-input>
            </div>
            <div class="sort-options">
              <span 
                v-for="sort in sortOptions" 
                :key="sort.value"
                :class="{ active: currentSort === sort.value }"
                @click="changeSort(sort.value)"
              >
                {{ sort.label }}
              </span>
            </div>
          </div>

          <!-- 商品列表 -->
          <div class="product-grid">
            <div v-if="filteredProducts.length === 0" class="empty-state">
              <el-empty description="没有找到相关商品" />
            </div>
            
            <!-- 商品卡片 - 与商品列表页相同的结构 -->
            <div 
              v-for="product in filteredProducts" 
              :key="product.id" 
              class="product-card"
              @click="goToDetail(product.id)"
            >
              <div class="product-image">
                <img :src="product.image" :alt="product.name" />
                <div class="product-overlay">
                  <el-button circle size="small">
                    <el-icon><View /></el-icon>
                  </el-button>
                </div>
              </div>
              <div class="product-info">
                <h3 class="product-title">{{ product.name }}</h3>
                <p class="product-desc">{{ product.description }}</p>
                <div class="product-meta">
                  <span class="rating">
                    <el-icon><StarFilled /></el-icon> {{ product.rating }}
                  </span>
                  <span class="sales">销量 {{ product.sales }}</span>
                </div>
                <div class="price-row">
                  <span class="current-price">¥{{ product.price }}</span>
                  <span class="original-price" v-if="product.originalPrice">¥{{ product.originalPrice }}</span>
                </div>
                <div class="stock-bar">
                  <div class="bar-bg">
                    <div class="bar-fill" :style="{ width: product.salesPercent + '%' }"></div>
                  </div>
                  <span class="stock-text">仅剩{{ product.remaining }}件</span>
                </div>
                <el-button class="add-cart-btn" @click.stop="addToCart(product)">
                  <el-icon><ShoppingCart /></el-icon> 加入购物车
                </el-button>
              </div>
            </div>
          </div>

          <!-- 分页 -->
          <div class="pagination-area">
            <el-pagination
              v-model:current-page="currentPage"
              v-model:page-size="pageSize"
              :total="filteredProducts.length"
              :page-sizes="[8, 16, 24, 32]"
              layout="total, sizes, prev, pager, next, jumper"
              @size-change="handleSizeChange"
              @current-change="handleCurrentChange"
            />
          </div>
        </main>
      </div>
    </SectionContainer>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
import { 
  Star, StarFilled, HomeFilled, ShoppingBag, FolderOpened, 
  ChatDotRound, Search, ShoppingCart, ChatLineRound, Top, 
  View, ArrowLeft
} from '@element-plus/icons-vue';
import SectionContainer from "@user/components/SectionContainer.vue";
import { ElMessage } from 'element-plus';
import { useCartStore } from "@user/stores/cart";

const router = useRouter();
const route = useRoute();
const cartStore = useCartStore();

// 状态
const isFollowed = ref(false);
const searchQuery = ref("");
const currentCategory = ref<number>(0);
const currentSort = ref<string>("default");
const currentPage = ref(1);
const pageSize = ref(8);

const shopInfo = ref({
  name: "",
  description: "",
  rating: 5.0,
  followers: 10000,
  productCount: 0,
  category: "",
  location: "广东 深圳",
});

// 品牌与品类映射
const brandCategories: Record<string, { category: string; products: string[] }> = {
  "华为": { category: "数码电器", products: ["手机", "平板", "笔记本", "手表", "耳机"] },
  "小米": { category: "数码电器", products: ["手机", "智能家居", "电视", "笔记本", "耳机"] },
  "海尔": { category: "家用电器", products: ["冰箱", "洗衣机", "空调", "热水器", "厨电"] },
  "格力": { category: "家用电器", products: ["空调", "空气净化器", "风扇", "取暖器"] },
  "苹果": { category: "数码电器", products: ["iPhone", "MacBook", "iPad", "Apple Watch", "AirPods"] },
  "索尼": { category: "数码电器", products: ["相机", "耳机", "电视", "游戏机", "音响"] },
  "三星": { category: "数码电器", products: ["手机", "电视", "冰箱", "洗衣机", "平板"] },
  "耐克": { category: "服饰鞋包", products: ["运动鞋", "跑步鞋", "篮球鞋", "服装", "配件"] },
  "阿迪达斯": { category: "服饰鞋包", products: ["运动鞋", "跑步鞋", "服装", "背包", "配件"] },
  "兰蔻": { category: "美妆护肤", products: ["精华", "面霜", "口红", "香水", "眼霜"] },
  "戴森": { category: "家用电器", products: ["吸尘器", "吹风机", "空气净化器", "卷发棒"] },
  "西门子": { category: "家用电器", products: ["洗碗机", "洗衣机", "冰箱", "烤箱", "厨电"] },
  "美的": { category: "家用电器", products: ["空调", "冰箱", "洗衣机", "微波炉", "电饭煲"] },
  "任天堂": { category: "数码电器", products: ["Switch", "游戏卡带", "配件", "周边"] },
  "LV": { category: "服饰鞋包", products: ["钱包", "手提包", "皮带", "香水", "配饰"] },
  "联想": { category: "数码电器", products: ["笔记本", "台式机", "平板", "显示器", "配件"] },
};

const categories = ref([
  { id: 0, name: "全部商品", count: 0 },
  { id: 1, name: "新品上市", count: 0 },
  { id: 2, name: "热销爆款", count: 0 },
  { id: 3, name: "优惠专区", count: 0 },
]);

const sortOptions = ref([
  { label: "默认", value: "default" },
  { label: "销量", value: "sales" },
  { label: "价格", value: "price" },
  { label: "新品", value: "new" },
]);

const allProducts = ref<any[]>([]);

const filteredProducts = computed(() => {
  let result = allProducts.value;
  
  if (currentCategory.value !== 0) {
    result = result.filter(p => p.categoryId === currentCategory.value);
  }
  
  if (searchQuery.value) {
    result = result.filter(p => 
      p.name.toLowerCase().includes(searchQuery.value.toLowerCase())
    );
  }
  
  if (currentSort.value === "sales") {
    result = [...result].sort((a, b) => b.salesNum - a.salesNum);
  } else if (currentSort.value === "price") {
    result = [...result].sort((a, b) => parseFloat(a.price) - parseFloat(b.price));
  } else if (currentSort.value === "new") {
    result = [...result].sort((a, b) => b.id - a.id);
  }
  
  return result;
});

onMounted(() => {
  window.scrollTo({ top: 0, behavior: 'instant' });
  
  const brandName = route.query.brand as string || "未知品牌";
  
  const brandInfo = brandCategories[brandName] || { category: "综合商品", products: ["商品"] };
  
  shopInfo.value.name = brandName;
  shopInfo.value.category = brandInfo.category;
  shopInfo.value.productCount = Math.floor(Math.random() * 200) + 50;
  shopInfo.value.followers = Math.floor(Math.random() * 100000) + 5000;
  
  categories.value[0].count = shopInfo.value.productCount;
  categories.value[1].count = Math.floor(shopInfo.value.productCount * 0.15);
  categories.value[2].count = Math.floor(shopInfo.value.productCount * 0.25);
  categories.value[3].count = Math.floor(shopInfo.value.productCount * 0.2);
  
  generateMockProducts(brandName, brandInfo.products);
});

const generateMockProducts = (brand: string, productTypes: string[]) => {
  const list = [];
  const salesOptions = ["1 万+", "3 万+", "5 万+", "8 万+", "10 万+", "15 万+"];
  
  for (let i = 1; i <= 32; i++) {
    const productType = productTypes[Math.floor(Math.random() * productTypes.length)];
    const price = parseFloat((Math.random() * 5000 + 200).toFixed(2));
    const discount = Math.floor(Math.random() * 3) + 7;
    const originalPrice = discount < 10 ? (price / (discount / 10)).toFixed(2) : null;
    const salesNum = Math.floor(Math.random() * 150000);
    const salesPercent = Math.floor(Math.random() * 40) + 50;
    const remaining = Math.floor(Math.random() * 5000) + 100;
    
    list.push({
      id: i,
      name: `${brand} ${productType} ${String.fromCharCode(65 + (i % 26))}`,
      description: `${productType} / 官方正品 / 全国联保`,
      price: price.toFixed(2),
      originalPrice,
      discount: discount < 10 ? discount : null,
      rating: (Math.random() * 1.5 + 3.5).toFixed(1),
      sales: salesOptions[Math.floor(Math.random() * salesOptions.length)],
      salesNum,
      salesPercent,
      remaining,
      categoryId: Math.floor(Math.random() * 3) + 1,
      image: `https://via.placeholder.com/250x250/1a2a4a/00d4ff?text=${brand}`,
    });
  }
  allProducts.value = list;
};

const formatNumber = (num: number) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w';
  return num.toString();
};

const toggleFollow = () => {
  isFollowed.value = !isFollowed.value;
  ElMessage.success(isFollowed.value ? "关注成功" : "已取消关注");
};

const selectCategory = (id: number) => {
  currentCategory.value = id;
  currentPage.value = 1;
};

const changeSort = (value: string) => {
  currentSort.value = currentSort.value === value ? "default" : value;
  currentPage.value = 1;
};

const handleSearch = () => {
  currentPage.value = 1;
  ElMessage.info(`搜索：${searchQuery.value}`);
};

// 返回上一页
const goBack = () => {
  router.back();
};

const goToDetail = (id: number) => {
  router.push(`/item/${id}`);
};

const addToCart = (product: any) => {
  cartStore.addToCart({ 
    id: product.id,
    name: product.name,
    price: parseFloat(product.price),
    originalPrice: product.originalPrice ? parseFloat(product.originalPrice) : 0,
    image: product.image,
    description: product.description,
    quantity: 1 
  });
  ElMessage.success("已加入购物车");
};

const openChat = () => {
  router.push('/chat');
};

const scrollToTop = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};

const handleSizeChange = () => {
  currentPage.value = 1;
};

const handleCurrentChange = () => {
  window.scrollTo({ top: 0, behavior: 'smooth' });
};
</script>

<style scoped>
.shop-page {
  min-height: 100vh;
  background-color: #0f1220;
}

/* ========== 店铺横幅 ========== */
.shop-banner {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.95) 0%, rgba(15, 18, 32, 0.9) 100%);
  border-bottom: 1px solid rgba(0, 212, 255, 0.2);
  padding: 30px 0;
}

.banner-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 20px;
}

/* 返回按钮 */
.back-btn {
  display: flex;
  align-items: center;
  gap: 5px;
  color: #aaa;
  cursor: pointer;
  padding: 8px 15px;
  border-radius: 6px;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.2);
  transition: all 0.3s;
  font-size: 13px;
}

.back-btn:hover {
  background: rgba(0, 212, 255, 0.2);
  border-color: var(--mall-primary);
  color: #fff;
}

.shop-logo-area {
  display: flex;
  align-items: center;
  gap: 20px;
}

.shop-avatar {
  width: 100px;
  height: 100px;
  border-radius: 12px;
  background: linear-gradient(135deg, #00d4ff, #0055ff);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 40px;
  font-weight: bold;
  color: #fff;
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.4);
  border: 2px solid rgba(255, 255, 255, 0.1);
}

.shop-name-wrapper {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.shop-name {
  font-size: 28px;
  color: #fff;
  margin: 0;
  text-shadow: 0 0 10px rgba(0, 212, 255, 0.5);
}

.shop-tags {
  display: flex;
  gap: 8px;
}

.tag {
  background: rgba(0, 0, 0, 0.6);
  color: #fff;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 12px;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.shop-actions {
  display: flex;
  align-items: center;
  gap: 30px;
}

.shop-stats {
  display: flex;
  gap: 25px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.stat-num {
  font-size: 20px;
  font-weight: bold;
  color: #00d4ff;
}

.stat-label {
  font-size: 12px;
  color: #888;
  margin-top: 4px;
}

.follow-btn {
  background: transparent;
  border: 1px solid var(--mall-primary);
  color: var(--mall-primary);
  padding: 10px 30px;
  font-size: 14px;
}

.follow-btn:hover {
  background: var(--mall-primary);
  color: #000;
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.5);
}

.follow-btn.is-success {
  border-color: #67c23a;
  color: #67c23a;
}

.follow-btn.is-success:hover {
  background: #67c23a;
  color: #fff;
}

/* ========== 店铺导航 ========== */
.shop-nav {
  background: rgba(26, 31, 58, 0.8);
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.nav-content {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
  display: flex;
  gap: 5px;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 15px 25px;
  color: #aaa;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
}

.nav-item:hover {
  color: #fff;
  background: rgba(0, 212, 255, 0.1);
}

.nav-item.active {
  color: #00d4ff;
  border-bottom-color: #00d4ff;
  background: rgba(0, 212, 255, 0.1);
}

/* ========== 主内容区 ========== */
.shop-main {
  display: flex;
  gap: 20px;
  padding: 20px 0;
  max-width: 1400px;
  margin: 0 auto;
}

/* ========== 左侧边栏 ========== */
.shop-sidebar {
  width: 180px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.sidebar-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 8px;
  padding: 15px;
}

.sidebar-title {
  font-size: 14px;
  color: #fff;
  margin: 0 0 15px 0;
  padding-bottom: 10px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.1);
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-list li {
  padding: 10px 12px;
  color: #aaa;
  cursor: pointer;
  border-radius: 4px;
  transition: all 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
}

.category-list li:hover {
  background: rgba(0, 212, 255, 0.1);
  color: #fff;
}

.category-list li.active {
  background: rgba(0, 212, 255, 0.2);
  color: #00d4ff;
}

.count {
  color: #666;
  font-size: 12px;
}

.service-btn {
  width: 100%;
  background: rgba(0, 212, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #00d4ff;
  padding: 10px;
}

.service-btn:hover {
  background: var(--mall-primary);
  color: #000;
}

.service-time {
  font-size: 12px;
  color: #666;
  margin: 10px 0 0 0;
  text-align: center;
}

.shop-info-detail {
  font-size: 13px;
}

.info-row {
  display: flex;
  margin-bottom: 10px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #888;
  width: 60px;
}

.value {
  color: #fff;
}

.float-tools {
  position: fixed;
  right: 20px;
  bottom: 100px;
  background: rgba(26, 31, 58, 0.9);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 8px;
  overflow: hidden;
  z-index: 100;
}

.tool-item {
  padding: 15px 12px;
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 5px;
  color: #aaa;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 12px;
}

.tool-item:hover {
  background: rgba(0, 212, 255, 0.1);
  color: #00d4ff;
}

/* ========== 右侧内容区 ========== */
.shop-content {
  flex: 1;
  min-width: 0;
}

/* 搜索排序栏 */
.search-sort-bar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  gap: 20px;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 8px;
  padding: 15px 20px;
}

.search-box {
  flex: 1;
  max-width: 500px;
}

.shop-search-input {
  --el-input-bg-color: rgba(15, 18, 32, 0.5);
  --el-input-border-color: rgba(0, 212, 255, 0.3);
  --el-input-text-color: #fff;
}

.shop-search-input :deep(.el-input__wrapper) {
  box-shadow: none;
  border-radius: 20px;
}

.shop-search-input :deep(.el-button) {
  background: var(--mall-primary);
  color: #000;
  border: none;
}

.sort-options {
  display: flex;
  gap: 5px;
}

.sort-options span {
  padding: 8px 20px;
  background: rgba(15, 18, 32, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 4px;
  color: #aaa;
  cursor: pointer;
  font-size: 13px;
  transition: all 0.3s;
}

.sort-options span:hover {
  border-color: rgba(0, 212, 255, 0.3);
  color: #fff;
}

.sort-options span.active {
  background: rgba(0, 212, 255, 0.15);
  border-color: #00d4ff;
  color: #00d4ff;
}

/* ========== 商品网格 - 核心修改区域 ========== */
.product-grid {
  display: grid;
  /* 1. 将原来的 4 列改为 3 列，增加单卡宽度 */
  grid-template-columns: repeat(3, 1fr);
  /* 2. 略微增加卡片间距 */
  gap: 25px;
  margin-bottom: 30px;
}

.product-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 12px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
}

.product-card:hover {
  transform: translateY(-5px);
  border-color: var(--mall-primary);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.2);
}

.product-image {
  /* 3. 增加图片高度 */
  height: 320px;
  background: rgba(15, 18, 32, 0.5);
  position: relative;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.product-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.product-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 0;
  transition: all 0.3s;
}

.product-card:hover .product-overlay {
  opacity: 1;
}

.product-info {
  /* 4. 增加卡片内边距 */
  padding: 18px;
}

.product-title {
  /* 5. 放大标题字体 */
  font-size: 16px;
  color: #fff;
  margin: 0 0 10px 0;
  height: 44px;
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  line-height: 1.5;
}

.product-desc {
  /* 6. 放大描述字体 */
  font-size: 13px;
  color: #888;
  margin: 0 0 12px 0;
  height: 18px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.product-meta {
  display: flex;
  align-items: center;
  gap: 12px;
  /* 7. 放大元数据字体 */
  font-size: 13px;
  color: #aaa;
  margin-bottom: 12px;
}

.rating {
  color: #ffa41c;
  display: flex;
  align-items: center;
  gap: 4px;
}

.price-row {
  display: flex;
  align-items: baseline;
  gap: 12px;
  margin-bottom: 12px;
}

.current-price {
  color: #ff4d4f;
  /* 8. 放大价格字体 */
  font-size: 22px;
  font-weight: bold;
}

.original-price {
  color: #666;
  font-size: 14px;
  text-decoration: line-through;
}

.stock-bar {
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 11px;
  color: #888;
  margin-bottom: 15px;
}

.bar-bg {
  flex: 1;
  height: 6px;
  background: rgba(255, 255, 255, 0.1);
  border-radius: 3px;
  overflow: hidden;
}

.bar-fill {
  height: 100%;
  background: linear-gradient(90deg, #00d4ff, #00ff88);
  border-radius: 3px;
}

.add-cart-btn {
  width: 100%;
  background: linear-gradient(90deg, #00d4ff, #00ff88);
  border: none;
  color: #000;
  font-weight: bold;
  /* 9. 增加按钮高度 */
  padding: 12px;
  border-radius: 6px;
  /* 10. 放大按钮字体 */
  font-size: 15px;
  transition: all 0.3s;
}

.add-cart-btn:hover {
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

/* 分页 */
.pagination-area {
  display: flex;
  justify-content: center;
  padding: 30px 0;
  border-top: 1px solid rgba(0, 212, 255, 0.1);
}

.pagination-area :deep(.el-pagination) {
  --el-pagination-bg-color: rgba(26, 31, 58, 0.6);
  --el-pagination-text-color: #aaa;
  --el-pagination-button-color: #aaa;
  --el-pagination-hover-color: #00d4ff;
  --el-pagination-color: #00d4ff;
}

.pagination-area :deep(.el-pagination .btn-prev),
.pagination-area :deep(.el-pagination .btn-next),
.pagination-area :deep(.el-pagination .number) {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.1);
}

.pagination-area :deep(.el-pagination .number.active) {
  background: var(--mall-primary);
  border-color: var(--mall-primary);
  color: #000;
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  padding: 80px;
  --el-empty-fill-color-0: rgba(255,255,255,0.1);
}
</style>