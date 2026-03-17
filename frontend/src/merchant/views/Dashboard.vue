<template>
  <!-- 第一部分：轮播图 + 商家信息 -->
  <section class="mall-home">
    <!-- 中间：轮播图 + 快捷操作 -->
    <div class="banner-section">
      <el-carousel height="420px" aria-label="促销轮播">
        <el-carousel-item v-for="(banner, index) in banners" :key="index">
          <article class="banner-item" :style="{ background: banner.gradient }">
            <div class="banner-info">
              <h2 class="banner-title">{{ banner.title }}</h2>
              <p class="banner-subtitle">{{ banner.subtitle }}</p>
              <el-button type="primary" size="large" class="glow-btn" @click="goToBanner(banner.link)">
                立即查看
              </el-button>
            </div>
          </article>
        </el-carousel-item>
      </el-carousel>

      <!-- 快捷操作 -->
      <nav class="quick-actions-bar" aria-label="快捷操作">
        <button
          v-for="action in quickActions"
          :key="action.name"
          class="action-item"
          @click="handleQuickAction(action)"
          :aria-label="action.name"
        >
          <el-icon><component :is="action.icon" /></el-icon>
          <span>{{ action.name }}</span>
        </button>
      </nav>
    </div>

    <!-- 右侧商家信息 -->
    <div class="user-panel" aria-label="商家信息">
      <article class="user-card">
        <figure class="user-avatar">
          <el-avatar :size="80" src="https://via.placeholder.com/80x80/00d4ff/fff?text=Shop">
            <el-icon size="40"><Shop /></el-icon>
          </el-avatar>
        </figure>
        <h3 class="user-name">店铺名称</h3>
        <div class="user-level">
          <span class="level-badge">金牌商家</span>
        </div>
        <div class="user-vip">
          <span class="vip-tag">营业中</span>
        </div>
        <div class="user-stats">
          <span class="stats-tag">
            <el-icon><ShoppingCart /></el-icon> {{ stats.orderCount }} 订单
          </span>
        </div>
        <div class="user-stats">
          <span class="stats-tag">
            <el-icon><Money /></el-icon> ¥{{ stats.revenue }} 销售额
          </span>
        </div>
      </article>

      <div class="announcement-wrapper">
        <AnnouncementPanel />
      </div>
    </div>
  </section>

  <!-- 第二部分：数据统计 -->
  <section class="stats-section" aria-label="数据统计">
    <div class="container">
      <header class="section-header">
        <div class="section-title">
          <el-icon><DataAnalysis /></el-icon>
          <h2>经营数据</h2>
        </div>
      </header>
      <el-row :gutter="15" class="stats-row">
        <el-col :span="6">
          <div class="stats-card primary">
            <div class="stats-icon">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.todayOrders }}</div>
              <div class="stats-label">今日订单</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stats-card success">
            <div class="stats-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">¥{{ stats.todayRevenue }}</div>
              <div class="stats-label">今日销售额</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stats-card warning">
            <div class="stats-icon">
              <el-icon><Goods /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.productCount }}</div>
              <div class="stats-label">商品总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stats-card danger">
            <div class="stats-icon">
              <el-icon><ChatDotRound /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ stats.pendingMessages }}</div>
              <div class="stats-label">待处理消息</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </section>

  <!-- 第三部分：订单和商品状态 -->
  <section class="content-section" aria-label="内容管理">
    <div class="container">
      <el-row :gutter="15">
        <el-col :span="12">
          <div class="section-card">
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span class="card-title">订单状态分布</span>
            </div>
            <div class="status-list">
              <div v-for="status in orderStatusOptions" :key="status.value" class="status-item">
                <span class="status-dot" :style="{ background: status.color }"></span>
                <span class="status-label">{{ status.label }}</span>
                <span class="status-count">{{ getRandomCount() }}单</span>
                <div class="status-bar">
                  <div class="status-bar-inner" :style="{ width: getRandomPercent() + '%', background: status.color }"></div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="section-card">
            <div class="card-header">
              <el-icon><DataAnalysis /></el-icon>
              <span class="card-title">商品状态分布</span>
            </div>
            <div class="status-list">
              <div v-for="status in productStatusOptions" :key="status.value" class="status-item">
                <span class="status-dot" :style="{ background: status.color }"></span>
                <span class="status-label">{{ status.label }}</span>
                <span class="status-count">{{ getRandomCount() }}个</span>
                <div class="status-bar">
                  <div class="status-bar-inner" :style="{ width: getRandomPercent() + '%', background: status.color }"></div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </section>

  <!-- 第四部分：销售排行 -->
  <section class="content-section" aria-label="销售排行">
    <div class="container">
      <div class="section-card">
        <div class="card-header">
          <el-icon><Ranking /></el-icon>
          <span class="card-title">商品销售排行</span>
        </div>
        <el-table :data="topProducts" class="sci-table" style="width: 100%">
          <el-table-column prop="rank" label="排名" width="80">
            <template #default="{ row }">
              <span class="rank-badge" :class="'rank-' + row.rank">{{ row.rank }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="name" label="商品名称" />
          <el-table-column prop="category" label="分类" width="120" />
          <el-table-column prop="sales" label="销量" width="100" />
          <el-table-column prop="revenue" label="销售额" width="120" />
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRouter } from "vue-router";
import { banners, orderStatusOptions, productStatusOptions, quickActions } from "@merchant/data/categories";
import AnnouncementPanel from "@merchant/components/AnnouncementPanel.vue";

const router = useRouter();

const stats = ref({
  orderCount: 1256,
  revenue: '9,876',
  todayOrders: 128,
  todayRevenue: '9,876',
  productCount: 256,
  pendingMessages: 12
});

const topProducts = ref([
  { rank: 1, name: '无线蓝牙耳机', category: '手机数码', sales: 1024, revenue: '¥20.4 万' },
  { rank: 2, name: '智能手环', category: '手机数码', sales: 896, revenue: '¥12.9 万' },
  { rank: 3, name: '机械键盘', category: '电脑办公', sales: 768, revenue: '¥25.2 万' },
  { rank: 4, name: '空气净化器', category: '家用电器', sales: 512, revenue: '¥51.2 万' },
  { rank: 5, name: '运动跑鞋', category: '服装鞋包', sales: 486, revenue: '¥14.5 万' }
]);

const handleQuickAction = (action) => {
  if (action.path) router.push(action.path);
};

const goToBanner = (link) => {
  router.push(link);
};

const getRandomCount = () => Math.floor(Math.random() * 100) + 10;
const getRandomPercent = () => Math.floor(Math.random() * 60) + 20;
</script>

<style scoped>
@import "@merchant/assets/mall-style.css";

/* 主容器 - 2 列布局（轮播图 + 右侧信息） */
section.mall-home {
  display: grid;
  grid-template-columns: 1fr 280px;
  gap: 10px;
  align-items: stretch;
}

/* 轮播图区域 */
.banner-section {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.el-carousel {
  border-radius: 12px;
  overflow: hidden;
  box-shadow: 0 0 30px rgba(0, 212, 255, 0.2);
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

/* 快捷操作栏 */
.quick-actions-bar {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 10px;
  padding: 15px;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
}

.action-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 6px;
  padding: 12px 8px;
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  color: #aaa;
  font-size: 13px;
}

.action-item:hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: var(--mall-primary);
  transform: translateY(-3px);
  box-shadow: 0 8px 25px rgba(0, 212, 255, 0.2);
  color: #fff;
}

.action-item .el-icon {
  font-size: 24px;
  color: var(--mall-primary);
}

/* 用户面板 */
.user-panel {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.user-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  text-align: center;
}

.user-avatar {
  margin-bottom: 15px;
}

.user-name {
  font-size: 16px;
  color: #fff;
  margin-bottom: 10px;
}

.level-badge {
  display: inline-block;
  padding: 3px 10px;
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  color: #000;
  border-radius: 12px;
  font-size: 12px;
  font-weight: bold;
}

.user-vip {
  margin: 10px 0;
}

.vip-tag {
  display: inline-block;
  padding: 3px 10px;
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  color: #000;
  border-radius: 6px;
  font-size: 12px;
  font-weight: bold;
}

.user-stats {
  margin: 8px 0;
}

.stats-tag {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  font-size: 13px;
  color: #aaa;
}

.stats-tag .el-icon {
  color: var(--mall-primary);
}

/* 公告区域 */
.announcement-wrapper {
  flex: 1;
  min-height: 150px;
}

/* 统计区域 */
.stats-section {
  padding: 20px 0;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05), transparent);
}

.stats-row {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.stats-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  position: relative;
  overflow: hidden;
}

.stats-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--glow-color), transparent);
}

.stats-card:hover {
  transform: translateY(-3px);
  border-color: var(--glow-color);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.15);
}

.stats-card.primary { --glow-color: #00d4ff; }
.stats-card.success { --glow-color: #00ff88; }
.stats-card.warning { --glow-color: #ffaa00; }
.stats-card.danger { --glow-color: #ff6666; }

.stats-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 26px;
  color: #fff;
  flex-shrink: 0;
}

.stats-card.primary .stats-icon {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
  box-shadow: 0 0 20px rgba(0, 212, 255, 0.4);
}

.stats-card.success .stats-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  box-shadow: 0 0 20px rgba(0, 255, 136, 0.4);
}

.stats-card.warning .stats-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
  box-shadow: 0 0 20px rgba(255, 170, 0, 0.4);
}

.stats-card.danger .stats-icon {
  background: linear-gradient(135deg, #ff6666, #ff4444);
  box-shadow: 0 0 20px rgba(255, 102, 102, 0.4);
}

.stats-info {
  flex: 1;
}

.stats-value {
  font-size: 26px;
  font-weight: bold;
  color: #fff;
}

.stats-label {
  font-size: 13px;
  color: #888;
  margin-top: 4px;
}

/* 内容区域 */
.content-section {
  padding: 20px 0;
}

.container {
  max-width: 1400px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-card {
  background: rgba(26, 31, 58, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
}

.card-header {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 20px;
  padding-bottom: 15px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.card-header .el-icon {
  color: var(--mall-primary);
  font-size: 20px;
}

.card-title {
  font-size: 16px;
  font-weight: bold;
  color: #fff;
  background: linear-gradient(90deg, #00d4ff, #00ff88);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

/* 状态列表 */
.status-list {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.status-item {
  display: flex;
  align-items: center;
  gap: 12px;
}

.status-dot {
  width: 10px;
  height: 10px;
  border-radius: 50%;
  flex-shrink: 0;
  box-shadow: 0 0 10px currentColor;
}

.status-label {
  width: 80px;
  font-size: 14px;
  color: #aaa;
}

.status-count {
  width: 60px;
  font-size: 15px;
  font-weight: bold;
  color: #fff;
  text-align: right;
}

.status-bar {
  flex: 1;
  height: 8px;
  background: rgba(255, 255, 255, 0.05);
  border-radius: 4px;
  overflow: hidden;
  margin-left: 10px;
}

.status-bar-inner {
  height: 100%;
  border-radius: 4px;
  transition: width 0.5s ease;
  box-shadow: 0 0 10px currentColor;
}

/* 表格样式 */
.sci-table :deep(.el-table__header th) {
  background: rgba(0, 212, 255, 0.08);
  color: var(--mall-primary);
  font-size: 13px;
  border-bottom: 1px solid rgba(0, 212, 255, 0.15);
}

.sci-table :deep(.el-table__body td) {
  background: transparent;
  color: #aaa;
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  font-size: 13px;
}

.sci-table :deep(.el-table__row:hover) {
  background: rgba(0, 212, 255, 0.05);
}

.rank-badge {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 26px;
  height: 26px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: bold;
  color: #fff;
}

.rank-badge.rank-1 {
  background: linear-gradient(135deg, #ffd700, #ffaa00);
  box-shadow: 0 0 12px rgba(255, 215, 0, 0.5);
}

.rank-badge.rank-2 {
  background: linear-gradient(135deg, #c0c0c0, #999);
  box-shadow: 0 0 12px rgba(192, 192, 192, 0.5);
}

.rank-badge.rank-3 {
  background: linear-gradient(135deg, #cd7f32, #b87333);
  box-shadow: 0 0 12px rgba(205, 127, 50, 0.5);
}

.rank-badge.rank-4,
.rank-badge.rank-5 {
  background: rgba(255, 255, 255, 0.15);
}

/* 区块标题 */
.section-header {
  margin-bottom: 20px;
}

.section-title {
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-title .el-icon {
  color: var(--mall-primary);
  font-size: 22px;
}

.section-title h2 {
  font-size: 20px;
  font-weight: bold;
  color: #fff;
  background: linear-gradient(90deg, #00d4ff, #00ff88);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}
</style>
