<template>
  <section class="user-forum" aria-label="用户论坛">
    <header class="forum-header">
      <i class="fas fa-comment"></i>
      <h3>用户论坛</h3>
      <el-button text size="small" @click="viewAll">查看全部</el-button>
    </header>
    <div class="forum-content">
      <div class="topic-list">
        <button v-for="topic in topics" :key="topic.id" class="topic-item" @click="viewTopic(topic.id)" type="button">
          <div class="topic-main">
            <div class="topic-title">{{ topic.title }}</div>
            <div class="topic-meta">
              <span class="topic-author">{{ topic.author }}</span>
              <span class="topic-time">{{ topic.time }}</span>
              <span class="topic-stats-inline">
                <i class="fas fa-comment-alt"></i>
                <span>{{ topic.replies }}</span>
                <i class="fas fa-eye"></i>
                <span>{{ topic.views }}</span>
              </span>
            </div>
          </div>
        </button>
      </div>
    </div>
  </section>
</template>

<script setup lang="ts">
// Font Awesome 图标直接使用类名，无需导入
import { useRouter } from 'vue-router'

const router = useRouter()

const topics = [
  { id: 1, title: '新手购物指南，教你如何优惠券叠加使用', author: '购物达人', time: '2 小时前', replies: 23, views: 156 },
  { id: 2, title: '晒单分享：刚收到的机械键盘，手感很棒！', author: '键盘侠', time: '3 小时前', replies: 45, views: 289 },
  { id: 3, title: '建议增加更多支付方式', author: '用户 123', time: '5 小时前', replies: 12, views: 78 },
  { id: 4, title: 'VIP 会员值得开通吗？使用体验分享', author: '省钱小能手', time: '1 天前', replies: 67, views: 423 }
]

const viewAll = () => {
  router.push('/forum')
}

const viewTopic = (id: number) => {
  router.push(`/forum/${id}`)
}
</script>

<style scoped>
.user-forum {
  width: 100%;
  height: 100%;
  min-height: 0;
  background: rgba(26, 31, 58, 0.8);
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 10px;
  display: flex;
  flex-direction: column;
  gap: 8px;
  box-sizing: border-box;
}

.forum-header {
  display: flex;
  align-items: center;
  gap: 6px;
  color: var(--mall-primary);
}

.forum-header h3 {
  font-size: 14px;
  
  flex: 1;
}

.forum-header i {
  font-size: 16px;
}

.forum-header .el-button {
  font-size: 11px;
  padding: 4px 8px;
}

.forum-content {
  flex: 1;
  overflow: hidden;
}

.topic-list {
  display: flex;
  flex-direction: column;
  gap: 6px;
  max-height: calc(100% - 30px);
  overflow-y: auto;
  scrollbar-width: thin;
  scrollbar-color: rgba(0, 212, 255, 0.3) transparent;
  padding-right: 3px;
  padding: 0;
  
  list-style: none;
}

.topic-list::-webkit-scrollbar {
  width: 6px;
}

.topic-list::-webkit-scrollbar-track {
  background: transparent;
}

.topic-list::-webkit-scrollbar-thumb {
  background: rgba(0, 212, 255, 0.3);
  border-radius: 3px;
}

.topic-list::-webkit-scrollbar-thumb:hover {
  background: rgba(0, 212, 255, 0.5);
}

.topic-item {
  background: rgba(0, 212, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.1);
  border-radius: 6px;
  padding: 8px;
  display: flex;
  flex-direction: column;
  gap: 4px;
  transition: all 0.3s;
  cursor: pointer;
  text-align: left;
  font: inherit;
  color: inherit;
  width: 100%;
}

.topic-item:hover {
  background: rgba(0, 212, 255, 0.15);
  border-color: rgba(0, 212, 255, 0.3);
}

.topic-main {
  width: 100%;
}

.topic-title {
  font-size: 12px;
  color: #fff;
  
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.topic-meta {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 10px;
  color: #888;
}

.topic-author {
  color: #aaa;
}

.topic-time {
  color: #666;
}

.topic-stats-inline {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  
}

.topic-stats-inline i {
  font-size: 13px;
}

.topic-stats-inline span {
  font-size: 11px;
  color: #888;
}
</style>
