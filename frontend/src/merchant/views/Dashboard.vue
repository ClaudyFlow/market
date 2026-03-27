<template>
  <!-- 第一部分:轮播图 + 商家信息 -->
  <section class="mall-home">
    <!-- 中间:轮播图 + 快捷操作 -->
    <div class="banner-section">
      <el-carousel height="420px" aria-label="促销轮播">
        <el-carousel-item v-for="(轮播,index) in 轮播图列表" :key="index">
          <article class="banner-item" :style="{ background: 轮播.渐变 }">
            <div class="banner-info">
              <h2 class="banner-title">{{ 轮播.标题 }}</h2>
              <p class="banner-subtitle">{{ 轮播.副标题 }}</p>
              <el-button type="primary" size="large" class="glow-btn" @click="跳转链接 (轮播.链接)">
                立即查看
              </el-button>
            </div>
          </article>
        </el-carousel-item>
      </el-carousel>

      <!-- 快捷操作 -->
      <nav class="quick-actions-bar" aria-label="快捷操作">
        <button
          v-for="操作 in 快捷操作列表"
          :key="操作.名称"
          class="action-item"
          @click="处理快捷操作 (操作)"
          :aria-label="操作.名称"
        >
          <el-icon><component :is="操作.图标" /></el-icon>
          <span>{{ 操作.名称 }}</span>
        </button>
      </nav>
    </div>

    <!-- 右侧商家信息 -->
    <div class="user-panel" aria-label="商家信息">
      <article class="user-card">
        <figure class="user-avatar">
          <el-avatar :size="80" :src="商家信息.头像 || `https://via.placeholder.com/80x80/00d4ff/fff?text=${商家信息.店铺名称?.[0] || '商'}`">
            <el-icon v-if="!商家信息.头像" size="40"><Shop /></el-icon>
          </el-avatar>
        </figure>
        <h3 class="user-name">{{ 商家信息.店铺名称 }}</h3>
        <div class="user-level">
          <span class="level-badge">{{ 商家信息.店铺等级 }}</span>
        </div>
        <div class="user-vip">
          <span class="vip-tag">{{ 商家信息.营业状态 }}</span>
        </div>
        <div class="user-stats">
          <span class="stats-tag">
            <el-icon><ShoppingCart /></el-icon> {{ 统计数据.订单总数 }} 订单
          </span>
        </div>
        <div class="user-stats">
          <span class="stats-tag">
            <el-icon><Money /></el-icon> ¥{{ 统计数据.销售额 }} 销售额
          </span>
        </div>
      </article>

      <div class="announcement-wrapper">
        <AnnouncementPanel />
      </div>
    </div>
  </section>

  <!-- 第二部分:数据统计 -->
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
              <div class="stats-value">{{ 统计数据.今日订单 }}</div>
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
              <div class="stats-value">¥{{ 统计数据.今日销售额 }}</div>
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
              <div class="stats-value">{{ 统计数据.商品总数 }}</div>
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
              <div class="stats-value">{{ 统计数据.待处理消息 }}</div>
              <div class="stats-label">待处理消息</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </section>

  <!-- 第三部分:订单和商品状态 -->
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
              <div v-for="状态 in 订单状态选项" :key="状态.值" class="status-item" v-if="状态.值">
                <span class="status-dot" :style="{ background: 状态.颜色 || '#00d4ff' }"></span>
                <span class="status-label">{{ 状态.标签 }}</span>
                <span class="status-count">{{ 获取随机数量 () }}单</span>
                <div class="status-bar">
                  <div class="status-bar-inner" :style="{ width: 获取随机百分比 () + '%', background: 状态.颜色 || '#00d4ff' }"></div>
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
              <div v-for="状态 in 商品状态选项" :key="状态.值" class="status-item" v-if="状态.值">
                <span class="status-dot" :style="{ background: 状态.颜色 || '#00d4ff' }"></span>
                <span class="status-label">{{ 状态.标签 }}</span>
                <span class="status-count">{{ 获取随机数量 () }}个</span>
                <div class="status-bar">
                  <div class="status-bar-inner" :style="{ width: 获取随机百分比 () + '%', background: 状态.颜色 || '#00d4ff' }"></div>
                </div>
              </div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </section>

  <!-- 第四部分:销售排行 -->
  <section class="content-section" aria-label="销售排行">
    <div class="container">
      <div class="section-card">
        <div class="card-header">
          <el-icon><Odometer /></el-icon>
          <span class="card-title">商品销售排行</span>
        </div>
        <el-table :data="销售排行列表" class="sci-table" style="width: 100%">
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

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import {
  Shop,
  ShoppingCart,
  Money,
  Goods,
  ChatDotRound,
  DataAnalysis,
  TrendCharts,
  Odometer
} from '@element-plus/icons-vue'
import { 轮播图列表,快捷操作列表,订单状态选项,商品状态选项 } from '@merchant/data/categories'
import AnnouncementPanel from '@merchant/components/AnnouncementPanel.vue'
import type { 快捷操作项 } from '@merchant/data/categories'

interface 商家信息类型 {
  店铺名称:string
  店铺等级:string
  营业状态:string
  头像:string
}

interface 统计数据类型 {
  订单总数:number
  销售额:string
  今日订单:number
  今日销售额:string
  商品总数:number
  待处理消息:number
}

interface 销售排行项 {
  rank: number
  name: string
  category: string
  sales: number
  revenue: string
}

const router = useRouter()

const 商家信息 = ref<商家信息类型>({
  店铺名称:'品质优选店',
  店铺等级:'金牌商家',
  营业状态:'营业中',
  头像:''
})

const 统计数据 = ref<统计数据类型>({
  订单总数:1256,
  销售额:'9,876',
  今日订单:128,
  今日销售额:'9,876',
  商品总数:256,
  待处理消息:12
})

const 销售排行列表 = ref<销售排行项[]>([
  { rank: 1, name: '无线蓝牙耳机', category: '手机数码', sales: 1024, revenue: '¥20.4 万' },
  { rank: 2, name: '智能手环', category: '手机数码', sales: 896, revenue: '¥12.9 万' },
  { rank: 3, name: '机械键盘', category: '电脑办公', sales: 768, revenue: '¥25.2 万' },
  { rank: 4, name: '空气净化器', category: '家用电器', sales: 512, revenue: '¥51.2 万' },
  { rank: 5, name: '运动跑鞋', category: '服装鞋包', sales: 486, revenue: '¥14.5 万' }
])

const 处理快捷操作 = (操作:快捷操作项) => {
  if (操作.路径) {
    router.push(操作.路径)
  }
}

const 跳转链接 = (链接:string) => {
  router.push(链接)
}

const 获取随机数量 = () => Math.floor(Math.random() * 100) + 10
const 获取随机百分比 = () => Math.floor(Math.random() * 60) + 20

// 组件挂载时加载数据
onMounted(() => {
  // 模拟加载商家信息
  const 存储信息 = localStorage.getItem('merchantInfo')
  if (存储信息) {
    商家信息.value = JSON.parse(存储信息)
  }
})
</script>

<style scoped>
@import "@merchant/assets/mall-style.css";

/* 主容器 - 2 列布局(轮播图 + 右侧信息) */
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
  
  background: linear-gradient(90deg, var(--mall-primary), var(--mall-secondary));
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
}

.banner-subtitle {
  font-size: 20px;
  
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
  
}

.user-name {
  font-size: 16px;
  color: #fff;
  
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
  
}

/* 内容区域 */
.content-section {
  padding: 20px 0;
}

.container {
  max-width: 1400px;
  
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

/* 区块标题
.section-header {
  
} */

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
