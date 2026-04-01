<template>
  <div class="shop-page">
    <!-- 1. 店铺头部横幅 -->
    <header class="shop-banner">
      <div class="banner-content">
        <!-- 左侧：店铺信息区域 -->
        <div class="shop-info-group">
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
              <p class="shop-desc">{{ shopInfo.description }}</p>
            </div>
          </div>
        </div>

        <!-- 右侧：数据与操作区域 -->
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
          <div class="action-btns">
            <el-button
              :type="isFollowed ? 'success' : 'primary'"
              class="follow-btn"
              @click="toggleFollow"
            >
              <i class="fas fa-star"></i>
              {{ isFollowed ? '已关注' : '关注店铺' }}
            </el-button>
            <el-button class="contact-btn" @click="openChat">
              <i class="fas fa-comment-dots"></i> 联系客服
            </el-button>
          </div>
        </div>
      </div>
    </header>

    <!-- 2. 店铺导航栏 -->
    <nav class="shop-nav">
      <div class="nav-content">
        <router-link to="/" class="nav-item">
          <i class="fas fa-home"></i> 首页
        </router-link>
        <a href="javascript:;" class="nav-item active">
          <i class="fas fa-shopping-bag"></i> 全部宝贝
        </a>
        <a href="javascript:;" class="nav-item">
          <i class="fas fa-folder-open"></i> 店铺分类
        </a>
        <a href="javascript:;" class="nav-item">
          <i class="fas fa-comments"></i> 联系我们
        </a>
      </div>
    </nav>

    <!-- 【修复 1】移除 SectionContainer，使用自定义容器 -->
    <div class="shop-container">
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
              <div class="info-row">
                <span class="label">开店时长：</span>
                <span class="value">5 年</span>
              </div>
            </div>
          </div>

          <!-- 悬浮工具栏 -->
          <div class="float-tools">
            <div class="tool-item" @click="scrollToTop">
              <i class="fas fa-arrow-up"></i>
              <span>顶部</span>
            </div>
          </div>
        </aside>

        <!-- 4. 右侧主内容区 -->
        <main class="shop-content">
          <!-- 筛选栏 -->
          <div class="filter-bar">
            <div class="filter-item">
              <span class="filter-label">分类:</span>
              <el-radio-group v-model="selectedCategory" size="small">
                <el-radio-button label="">全部</el-radio-button>
                <el-radio-button label="digital">数码</el-radio-button>
                <el-radio-button label="appliance">家电</el-radio-button>
                <el-radio-button label="fashion">服饰</el-radio-button>
                <el-radio-button label="beauty">美妆</el-radio-button>
              </el-radio-group>
            </div>
            <div class="filter-item">
              <span class="filter-label">价格:</span>
              <el-radio-group v-model="priceRange" size="small">
                <el-radio-button label="">全部</el-radio-button>
                <el-radio-button label="0-1000">0-1000 元</el-radio-button>
                <el-radio-button label="1000-5000">1000-5000 元</el-radio-button>
                <el-radio-button label="5000+">5000 元以上</el-radio-button>
              </el-radio-group>
            </div>
            <div class="filter-item">
              <span class="filter-label">排序:</span>
              <el-radio-group v-model="sortBy" size="small">
                <el-radio-button label="default">综合</el-radio-button>
                <el-radio-button label="sales">销量</el-radio-button>
                <el-radio-button label="price-asc">价格↑</el-radio-button>
                <el-radio-button label="price-desc">价格↓</el-radio-button>
              </el-radio-group>
            </div>
          </div>

          <!-- 商品列表 - 调用 ProductCard 组件 -->
          <div class="product-grid">
            <div v-if="paginatedProducts.length === 0" class="empty-state">
              <el-empty description="没有找到相关商品" />
            </div>
            
            <ProductCard
              v-for="product in paginatedProducts" 
              :key="product.id" 
              :product="formatProduct(product)"
              @click="goToDetail"
              @add-to-cart="addToCart"
            />
          </div>

          <!-- 分页 -->
          <div class="pagination">
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
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, watch, onMounted } from "vue";
import { useRouter, useRoute } from "vue-router";
// 【修复 2】移除 SectionContainer 导入
// import SectionContainer from "@user/components/SectionContainer.vue";
import ProductCard from "@user/components/ProductCard.vue";
import { ElMessage } from 'element-plus';
import { useCartStore } from "@user/stores/cart";

const router = useRouter();
const route = useRoute();
const cartStore = useCartStore();

const isFollowed = ref(false);
const currentCategory = ref<number>(0);
const currentPage = ref(1);
const pageSize = ref(8);

const selectedCategory = ref('');
const priceRange = ref('');
const sortBy = ref<'default' | 'sales' | 'price-asc' | 'price-desc'>('default');

interface ShopInfo {
  name: string;
  description: string;
  rating: number;
  followers: number;
  productCount: number;
  category: string;
  location: string;
}

interface Product {
  id: number;
  name: string;
  price: number;
  originalPrice: number;
  type: 'digital' | 'appliance' | 'fashion' | 'beauty';
  rating: number;
  sales: string;
  salesNum: number;
  image: string;
  remainCount?: number;
  soldCount?: number;
  salesPercent?: number;
  remaining?: number;
  categoryId: number;
  [key: string]: unknown;
}

const shopInfo = ref<ShopInfo>({
  name: "",
  description: "专注品质生活，为您提供优质的数码家电产品",
  rating: 5.0,
  followers: 10000,
  productCount: 0,
  category: "",
  location: "广东 深圳",
});

const brandCategories: Record<string, { category: string; products: string[]; type: string }> = {
  "华为": { category: "数码电器", products: ["手机", "平板", "笔记本", "手表", "耳机"], type: "digital" },
  "小米": { category: "数码电器", products: ["手机", "智能家居", "电视", "笔记本", "耳机"], type: "digital" },
  "海尔": { category: "家用电器", products: ["冰箱", "洗衣机", "空调", "热水器", "厨电"], type: "appliance" },
  "格力": { category: "家用电器", products: ["空调", "空气净化器", "风扇", "取暖器"], type: "appliance" },
  "苹果": { category: "数码电器", products: ["iPhone", "MacBook", "iPad", "Apple Watch", "AirPods"], type: "digital" },
  "索尼": { category: "数码电器", products: ["相机", "耳机", "电视", "游戏机", "音响"], type: "digital" },
  "三星": { category: "数码电器", products: ["手机", "电视", "冰箱", "洗衣机", "平板"], type: "digital" },
  "耐克": { category: "服饰鞋包", products: ["运动鞋", "跑步鞋", "篮球鞋", "服装", "配件"], type: "fashion" },
  "阿迪达斯": { category: "服饰鞋包", products: ["运动鞋", "跑步鞋", "服装", "背包", "配件"], type: "fashion" },
  "兰蔻": { category: "美妆护肤", products: ["精华", "面霜", "口红", "香水", "眼霜"], type: "beauty" },
  "戴森": { category: "家用电器", products: ["吸尘器", "吹风机", "空气净化器", "卷发棒"], type: "appliance" },
  "西门子": { category: "家用电器", products: ["洗碗机", "洗衣机", "冰箱", "烤箱", "厨电"], type: "appliance" },
  "美的": { category: "家用电器", products: ["空调", "冰箱", "洗衣机", "微波炉", "电饭煲"], type: "appliance" },
  "任天堂": { category: "数码电器", products: ["Switch", "游戏卡带", "配件", "周边"], type: "digital" },
  "LV": { category: "服饰鞋包", products: ["钱包", "手提包", "皮带", "香水", "配饰"], type: "fashion" },
  "联想": { category: "数码电器", products: ["笔记本", "台式机", "平板", "显示器", "配件"], type: "digital" },
};

interface Category {
  id: number;
  name: string;
  count: number;
}

const categories = ref<Category[]>([
  { id: 0, name: "全部商品", count: 0 },
  { id: 1, name: "新品上市", count: 0 },
  { id: 2, name: "热销爆款", count: 0 },
  { id: 3, name: "优惠专区", count: 0 },
]);

const allProducts = ref<Product[]>([]);

const filteredProducts = computed(() => {
  let result = [...allProducts.value];
  
  if (currentCategory.value !== 0) {
    result = result.filter(p => p.categoryId === currentCategory.value);
  }
  
  if (selectedCategory.value) {
    result = result.filter(p => p.type === selectedCategory.value);
  }
  
  if (priceRange.value) {
    if (priceRange.value === '0-1000') {
      result = result.filter(p => p.price <= 1000);
    } else if (priceRange.value === '1000-5000') {
      result = result.filter(p => p.price > 1000 && p.price <= 5000);
    } else if (priceRange.value === '5000+') {
      result = result.filter(p => p.price > 5000);
    }
  }
  
  if (sortBy.value === 'sales') {
    result.sort((a, b) => b.salesNum - a.salesNum);
  } else if (sortBy.value === 'price-asc') {
    result.sort((a, b) => a.price - b.price);
  } else if (sortBy.value === 'price-desc') {
    result.sort((a, b) => b.price - a.price);
  } else {
    result.sort((a, b) => b.id - a.id);
  }
  
  return result;
});

const paginatedProducts = computed(() => {
  const start = (currentPage.value - 1) * pageSize.value;
  const end = start + pageSize.value;
  return filteredProducts.value.slice(start, end);
});

watch([selectedCategory, priceRange, sortBy], () => {
  currentPage.value = 1;
});

onMounted(() => {
  window.scrollTo({ top: 0, behavior: 'instant' });
  const brandName = route.query.brand as string || "未知品牌";
  const brandInfo = brandCategories[brandName] || { category: "综合商品", products: ["商品"], type: "digital" };
  
  shopInfo.value.name = brandName;
  shopInfo.value.category = brandInfo.category;
  shopInfo.value.productCount = Math.floor(Math.random() * 200) + 50;
  shopInfo.value.followers = Math.floor(Math.random() * 100000) + 5000;
  
  categories.value[0].count = shopInfo.value.productCount;
  categories.value[1].count = Math.floor(shopInfo.value.productCount * 0.15);
  categories.value[2].count = Math.floor(shopInfo.value.productCount * 0.25);
  categories.value[3].count = Math.floor(shopInfo.value.productCount * 0.2);
  
  generateMockProducts(brandName, brandInfo.products, brandInfo.type);
});

const generateMockProducts = (brand: string, productTypes: string[], type: string) => {
  const list: Product[] = [];
  const salesOptions = ["1 万+", "3 万+", "5 万+", "8 万+", "10 万+", "15 万+"];
  
  for (let i = 1; i <= 32; i++) {
    const productType = productTypes[Math.floor(Math.random() * productTypes.length)];
    const price = parseFloat((Math.random() * 5000 + 200).toFixed(2));
    const discount = Math.floor(Math.random() * 3) + 7;
    const originalPrice = parseFloat((price / (discount / 10)).toFixed(2));
    const salesNum = Math.floor(Math.random() * 150000);
    const salesPercent = Math.floor(Math.random() * 40) + 50;
    const remaining = Math.floor(Math.random() * 5000) + 100;
    const soldCount = Math.floor(Math.random() * 10000) + 500;
    const rating = parseFloat((Math.random() * 1.5 + 3.5).toFixed(1));
    
    list.push({
      id: i,
      name: `${brand} ${productType} ${String.fromCharCode(65 + (i % 26))}`,
      price: price,
      originalPrice: originalPrice,
      type: type as 'digital' | 'appliance' | 'fashion' | 'beauty',
      rating: rating,
      sales: salesOptions[Math.floor(Math.random() * salesOptions.length)],
      salesNum,
      salesPercent,
      remaining,
      remainCount: remaining,
      soldCount,
      categoryId: Math.floor(Math.random() * 3) + 1,
      image: `https://via.placeholder.com/300x300/0a1628/00d4ff?text=${brand}`,
    });
  }
  allProducts.value = list;
};

const formatNumber = (num: number) => {
  if (num >= 10000) return (num / 10000).toFixed(1) + 'w';
  return num.toString();
};

const formatProduct = (product: Product) => ({
  ...product,
  remaining: product.remaining || Math.round((100 - (product.salesPercent || 70)) * 10),
  soldPercent: product.salesPercent || 70
});

const toggleFollow = () => {
  isFollowed.value = !isFollowed.value;
  ElMessage.success(isFollowed.value ? "关注成功" : "已取消关注");
};

const selectCategory = (id: number) => {
  currentCategory.value = id;
  currentPage.value = 1;
};

const goToDetail = (product: Product) => {
  router.push(`/item/${product.id}`);
};

const addToCart = (product: Product) => {
  if (product.remaining && product.remaining <= 0) {
    ElMessage.warning("商品已售罄");
    return;
  }
  cartStore.addToCart({ 
    id: product.id,
    name: product.name,
    price: product.price,
    originalPrice: product.originalPrice,
    image: product.image,
    description: `${product.type} / 官方正品`,
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
  background-color: #050a14;
  width: 100%;
}

/* ========== 店铺横幅 ========== */
.shop-banner {
  background: linear-gradient(135deg, rgba(16, 24, 45, 0.95) 0%, rgba(5, 10, 20, 0.9) 100%);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  padding: 30px 0;
  width: 100%;
}

.banner-content {
  width: 100%;
  padding: 0 60px;
  display: flex;
  justify-content: space-between;
  align-items: center;
  gap: 40px;
  max-width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

.shop-info-group {
  display: flex;
  align-items: center;
  flex: 1;
}

.shop-logo-area {
  display: flex;
  align-items: center;
  gap: 25px;
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
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.3);
  border: 2px solid rgba(255, 255, 255, 0.1);
  flex-shrink: 0;
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
  font-weight: 700;
}

.shop-tags {
  display: flex;
  gap: 8px;
}

.tag {
  background: rgba(255, 255, 255, 0.1);
  color: #ccc;
  padding: 2px 8px;
  border-radius: 4px;
  font-size: 12px;
  border: 1px solid rgba(255, 255, 255, 0.1);
}

.shop-desc {
  color: #666;
  font-size: 13px;
  margin: 0;
}

.shop-actions {
  display: flex;
  align-items: center;
  gap: 30px;
  flex-shrink: 0;
}

.shop-stats {
  display: flex;
  gap: 25px;
  border-right: 1px solid rgba(255, 255, 255, 0.1);
  padding-right: 25px;
}

.stat-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  min-width: 50px;
}

.stat-num {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 12px;
  color: #666;
  margin-top: 2px;
}

.action-btns {
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.follow-btn, .contact-btn {
  width: 110px;
  border-radius: 6px;
  font-size: 13px;
}

.follow-btn {
  background: rgba(0, 212, 255, 0.15);
  border: 1px solid rgba(0, 212, 255, 0.3);
  color: #00d4ff;
}

.follow-btn:hover {
  background: rgba(0, 212, 255, 0.25);
  color: #fff;
}

.contact-btn {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #aaa;
}

/* ========== 店铺导航 ========== */
.shop-nav {
  background: rgba(10, 15, 30, 0.8);
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  position: sticky;
  top: 60px;
  z-index: 90;
  backdrop-filter: blur(10px);
  width: 100%;
}

.nav-content {
  width: 100%;
  padding: 0 60px;
  display: flex;
  gap: 5px;
  max-width: 100%;
  margin: 0 auto;
  box-sizing: border-box;
}

.nav-item {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 15px 25px;
  color: #888;
  text-decoration: none;
  font-size: 14px;
  transition: all 0.3s;
  border-bottom: 2px solid transparent;
}

.nav-item:hover {
  color: #fff;
  background: rgba(255, 255, 255, 0.02);
}

.nav-item.active {
  color: #00d4ff;
  border-bottom-color: #00d4ff;
  background: rgba(0, 212, 255, 0.05);
}

/* ========== 【修复 3】自定义容器，替代 SectionContainer ========== */
.shop-container {
  width: 100%;
  padding: 20px 60px;
  box-sizing: border-box;
  max-width: 100%;
}

/* ========== 主内容区 ========== */
.shop-main {
  display: flex;
  gap: 20px;
  width: 100%;
  box-sizing: border-box;
  max-width: 100%;
  margin: 0;
}

/* 左侧边栏 */
.shop-sidebar {
  width: 200px;
  flex-shrink: 0;
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.sidebar-card {
  background: rgba(20, 30, 48, 0.6);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  padding: 20px;
}

.sidebar-title {
  font-size: 16px;
  color: #fff;
  margin: 0 0 20px 0;
  padding-bottom: 12px;
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  font-weight: 600;
}

.category-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.category-list li {
  padding: 12px 15px;
  color: #aaa;
  cursor: pointer;
  border-radius: 6px;
  transition: all 0.3s;
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 14px;
  margin-bottom: 5px;
}

.category-list li:hover {
  background: rgba(0, 212, 255, 0.1);
  color: #fff;
}

.category-list li.active {
  background: rgba(0, 212, 255, 0.2);
  color: #00d4ff;
  font-weight: 600;
}

.count {
  color: #666;
  font-size: 12px;
}

.shop-info-detail {
  font-size: 14px;
}

.info-row {
  display: flex;
  margin-bottom: 12px;
}

.info-row:last-child {
  margin-bottom: 0;
}

.label {
  color: #888;
  width: 70px;
}

.value {
  color: #fff;
}

.float-tools {
  position: fixed;
  right: 30px;
  bottom: 100px;
  background: rgba(20, 30, 48, 0.9);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 8px;
  overflow: hidden;
  z-index: 100;
  box-shadow: 0 4px 20px rgba(0,0,0,0.3);
}

.tool-item {
  padding: 15px 15px;
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

/* 右侧内容区 */
.shop-content {
  flex: 1;
  min-width: 0;
  width: 100%;
}

/* ========== 筛选栏 ========== */
.filter-bar {
  background: rgba(20, 30, 48, 0.4);
  backdrop-filter: blur(10px);
  padding: 25px 30px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.05);
  margin-bottom: 25px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.2);
  width: 100%;
  box-sizing: border-box;
}

.filter-item {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.filter-item:last-child {
  margin-bottom: 0;
}

.filter-label {
  width: 50px;
  color: #aaa;
  font-size: 14px;
  flex-shrink: 0;
  font-weight: 500;
}

:deep(.el-radio-group) {
  gap: 5px;
}

:deep(.el-radio-button__inner) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #aaa;
  box-shadow: none;
  border-radius: 4px;
  padding: 8px 15px;
  font-size: 13px;
  transition: all 0.3s;
}

:deep(.el-radio-button__inner:hover) {
  color: #fff;
  background: rgba(255, 255, 255, 0.1);
}

:deep(.el-radio-button__original-radio:checked + .el-radio-button__inner) {
  background: #00d4ff;
  border-color: #00d4ff;
  color: #000;
  box-shadow: 0 0 10px rgba(0, 212, 255, 0.3);
}

/* ========== 【修复 4】商品网格 - 移除响应式媒体查询干扰 ========== */
.product-grid {
  display: grid;
  /* 只使用 auto-fit，让列自动填充 */
  grid-template-columns: repeat(auto-fit, minmax(300px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
  width: 100%;
}

/* ========== 分页 ========== */
.pagination {
  display: flex;
  justify-content: center;
  padding: 30px 0;
  width: 100%;
}

.pagination :deep(.el-pagination) {
  --el-pagination-bg-color: transparent;
  --el-pagination-text-color: #888;
  --el-pagination-button-color: #888;
  --el-pagination-hover-color: #00d4ff;
  --el-pagination-color: #00d4ff;
}

.pagination :deep(.el-pagination .btn-prev),
.pagination :deep(.el-pagination .btn-next),
.pagination :deep(.el-pagination .number) {
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  color: #aaa;
}

.pagination :deep(.el-pagination .number.active) {
  background: #00d4ff;
  border-color: #00d4ff;
  color: #000;
}

/* 空状态 */
.empty-state {
  grid-column: 1 / -1;
  padding: 80px;
  background: rgba(255,255,255,0.02);
  border-radius: 12px;
}

/* ========== 响应式适配 ========== */
@media (max-width: 1400px) {
  .banner-content, .nav-content, .shop-container {
    padding-left: 40px;
    padding-right: 40px;
  }
}

@media (max-width: 1100px) {
  .shop-sidebar {
    width: 180px;
  }
}

@media (max-width: 768px) {
  .shop-main {
    flex-direction: column;
  }
  
  .shop-sidebar {
    width: 100%;
  }
  
  .shop-content {
    width: 100%;
  }
  
  .banner-content {
    flex-direction: column;
    align-items: flex-start;
    gap: 20px;
  }
  
  .shop-actions {
    width: 100%;
    justify-content: space-between;
  }
  
  .shop-nav {
    top: 0;
  }
  
  .banner-content, .nav-content, .shop-container {
    padding-left: 20px;
    padding-right: 20px;
  }
}
</style>
