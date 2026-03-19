<template>
  <!-- 第一部分:轮播图 + 平台信息 -->
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

    <!-- 右侧平台信息 -->
    <div class="user-panel" aria-label="平台信息">
      <article class="user-card">
        <figure class="user-avatar">
          <el-avatar :size="80" :src="平台信息.头像 || `https://via.placeholder.com/80x80/00d4ff/fff?text=平台`">
            <el-icon v-if="!平台信息.头像" size="40"><Setting /></el-icon>
          </el-avatar>
        </figure>
        <h3 class="user-name">{{ 平台信息.平台名称 }}</h3>
        <div class="user-level">
          <span class="level-badge">{{ 平台信息.运营状态 }}</span>
        </div>
        <div class="user-vip">
          <span class="vip-tag">正常运行</span>
        </div>
        <div class="user-stats">
          <span class="stats-tag">
            <el-icon><User /></el-icon> {{ 平台数据.用户总数 }} 用户
          </span>
        </div>
        <div class="user-stats">
          <span class="stats-tag">
            <el-icon><Shop /></el-icon> {{ 平台数据.商家总数 }} 商家
          </span>
        </div>
      </article>

      <div class="announcement-wrapper">
        <AnnouncementPanel />
      </div>
    </div>
  </section>

  <!-- 第二部分:平台数据 -->
  <section class="stats-section" aria-label="平台数据">
    <div class="container">
      <header class="section-header">
        <div class="section-title">
          <el-icon><DataAnalysis /></el-icon>
          <h2>平台概览</h2>
        </div>
      </header>
      <el-row :gutter="15" class="stats-row">
        <el-col :span="6">
          <div class="stats-card primary">
            <div class="stats-icon">
              <el-icon><User /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ 平台数据.用户总数 }}</div>
              <div class="stats-label">用户总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stats-card success">
            <div class="stats-icon">
              <el-icon><Shop /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ 平台数据.商家总数 }}</div>
              <div class="stats-label">商家总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stats-card warning">
            <div class="stats-icon">
              <el-icon><ShoppingCart /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">{{ 平台数据.今日订单 }}</div>
              <div class="stats-label">今日订单</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stats-card danger">
            <div class="stats-icon">
              <el-icon><Money /></el-icon>
            </div>
            <div class="stats-info">
              <div class="stats-value">¥{{ 平台数据.今日销售额 }}</div>
              <div class="stats-label">平台销售额</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </div>
  </section>

  <!-- 第三部分:审核状态 -->
  <section class="content-section" aria-label="审核管理">
    <div class="container">
      <el-row :gutter="15">
        <el-col :span="12">
          <div class="section-card">
            <div class="card-header">
              <el-icon><DocumentChecked /></el-icon>
              <span class="card-title">商品审核状态</span>
            </div>
            <div class="status-list">
              <div v-for="状态 in 审核状态选项" :key="状态.值" class="status-item" v-if="状态.值">
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
        <el-col :span="12">
          <div class="section-card">
            <div class="card-header">
              <el-icon><TrendCharts /></el-icon>
              <span class="card-title">商家状态分布</span>
            </div>
            <div class="status-list">
              <div v-for="状态 in 商家状态选项" :key="状态.值" class="status-item" v-if="状态.值">
                <span class="status-dot" :style="{ background: 状态.颜色 || '#00d4ff' }"></span>
                <span class="status-label">{{ 状态.标签 }}</span>
                <span class="status-count">{{ 获取随机数量 () }}家</span>
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

  <!-- 第四部分:待处理事项 -->
  <section class="content-section" aria-label="待处理事项">
    <div class="container">
      <div class="section-card">
        <div class="card-header">
          <el-icon><Bell /></el-icon>
          <span class="card-title">待处理事项</span>
        </div>
        <el-table :data="待处理事项列表" class="sci-table" style="width: 100%">
          <el-table-column prop="type" label="类型" width="140">
            <template #default="{ row }">
              <el-tag :type="获取事项类型 (row.type)" size="small" class="task-tag">{{ row.type }}</el-tag>
            </template>
          </el-table-column>
          <el-table-column prop="count" label="待处理数量" width="120">
            <template #default="{ row }">
              <span class="pending-count">{{ row.count }}</span>
            </template>
          </el-table-column>
          <el-table-column prop="desc" label="描述" />
          <el-table-column prop="action" label="操作" width="100" fixed="right">
            <template #default>
              <el-button type="primary" text size="small" class="action-link">处理</el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  Setting,
  User,
  Shop,
  ShoppingCart,
  Money,
  DataAnalysis,
  DocumentChecked,
  TrendCharts,
  Bell
} from '@element-plus/icons-vue'
import { 轮播图列表,快捷操作列表,审核状态选项,商家状态选项 } from '@admin/data/categories'
import AnnouncementPanel from '@admin/components/AnnouncementPanel.vue'
import type { 快捷操作项 } from '@admin/data/categories'

interface 平台信息类型 {
  平台名称:string
  运营状态:string
  头像:string
}

interface 平台数据类型 {
  用户总数:string
  商家总数:string
  今日订单:string
  今日销售额:string
}

interface 待处理事项项 {
  type: string
  count: number
  desc: string
}

const router = useRouter()

const 平台信息 = ref<平台信息类型>({
  平台名称:'购物商城平台',
  运营状态:'官方运营',
  头像:''
})

const 平台数据 = ref<平台数据类型>({
  用户总数:'12,580',
  商家总数:'356',
  今日订单:'8,976',
  今日销售额:'125.8 万'
})

const 待处理事项列表 = ref<待处理事项项[]>([
  { type: '商品审核', count: 23, desc: '新提交的商品待审核' },
  { type: '评价审核', count: 15, desc: '用户举报的评价待处理' },
  { type: '商家入驻', count: 8, desc: '待审核的商家入驻申请' },
  { type: '客诉处理', count: 5, desc: '待处理的客户投诉' },
  { type: '退款申请', count: 12, desc: '待处理的退款申请' }
])

const 处理快捷操作 = (操作:快捷操作项) => {
  if (操作.路径) {
    router.push(操作.路径)
  }
}

const 跳转链接 = (链接:string) => {
  router.push(链接)
}

const 获取随机数量 = () => Math.floor(Math.random() * 200) + 20
const 获取随机百分比 = () => Math.floor(Math.random() * 60) + 20

const 获取事项类型 = (类型:string) => {
  const 映射:Record<string, string> = {
    '商品审核': 'warning',
    '评价审核': 'info',
    '商家入驻': 'success',
    '客诉处理': 'danger',
    '退款申请': 'warning'
  }
  return 映射 [类型] || 'info'
}
</script>

<style scoped>
@import "@admin/assets/mall-style.css";

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

.task-tag :deep(.el-tag__content) {
  font-size: 12px;
}

.pending-count {
  color: var(--mall-accent);
  font-weight: bold;
  font-size: 16px;
}

.action-link {
  color: var(--mall-primary);
}

.action-link:hover {
  color: var(--mall-secondary);
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
