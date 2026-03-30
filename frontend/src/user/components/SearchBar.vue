<template>
  <div class="search-bar-component" :class="{ 'compact': compact }">
    <div class="search-container">
      <!-- 搜索图标 -->
      <div class="search-icon-wrapper">
        <i class="fas fa-search"></i>
      </div>
      
      <!-- 搜索输入框 -->
      <input
        v-model="searchKeyword"
        type="text"
        class="search-input"
        :placeholder="placeholder"
        @keyup.enter="handleSearch"
        @focus="handleFocus"
        @blur="handleBlur"
      />
      
      <!-- 清除按钮 -->
      <button
        v-if="searchKeyword"
        class="clear-btn"
        @click="clearSearch"
        type="button"
        aria-label="清除搜索"
      >
        <i class="fas fa-times-circle"></i>
      </button>
      
      <!-- 搜索按钮 -->
      <button class="search-btn" @click="handleSearch" type="button">
        <span class="btn-text">搜索</span>
      </button>
    </div>
    
    <!-- 热门搜索标签 -->
    <div class="hot-search-tags" v-if="showHotTags && !compact">
      <div class="tags-header">
        <i class="fas fa-fire"></i>
        <span>热门搜索</span>
      </div>
      <div class="tag-list">
        <button
          v-for="(tag, index) in hotTags"
          :key="index"
          class="tag-item"
          :class="{ 'hot': tag.isHot }"
          @click="selectTag(tag.text)"
          type="button"
        >
          <span>{{ tag.text }}</span>
        </button>
      </div>
    </div>
    
    <!-- 搜索历史 -->
    <div class="search-history" v-if="showHistory && !compact && searchHistory.length > 0">
      <div class="history-header">
        <div class="history-title">
          <i class="fas fa-history"></i>
          <span>搜索历史</span>
        </div>
        <button class="clear-history-btn" @click="clearHistory" type="button">
          <i class="fas fa-trash-alt"></i>
          <span>清除历史</span>
        </button>
      </div>
      <div class="history-list">
        <button
          v-for="(item, index) in searchHistory"
          :key="index"
          class="history-item"
          @click="selectTag(item)"
          type="button"
        >
          <i class="fas fa-clock"></i>
          <span>{{ item }}</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, defineProps, defineEmits } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

// 定义 props
interface Props {
  placeholder?: string
  showHotTags?: boolean
  showHistory?: boolean
  compact?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  placeholder: '搜索商品、品牌、分类...',
  showHotTags: true,
  showHistory: true,
  compact: false
})

const emit = defineEmits(['search', 'focus', 'blur'])

// 搜索关键词
const searchKeyword = ref<string>('')

// 是否聚焦
const isFocused = ref<boolean>(false)

// 处理聚焦
const handleFocus = (): void => {
  isFocused.value = true
  emit('focus')
}

// 处理失焦
const handleBlur = (): void => {
  isFocused.value = false
  emit('blur')
}

// 处理搜索
const handleSearch = (): void => {
  const keyword = searchKeyword.value.trim()
  
  if (!keyword) {
    ElMessage.warning('请输入搜索关键词')
    return
  }
  
  // 添加到搜索历史
  addToHistory(keyword)
  
  // 触发搜索事件
  emit('search', keyword)
  
  // 跳转到搜索结果页
  router.push({
    path: '/search',
    query: { q: keyword }
  })
  
  ElMessage.success(`搜索：${keyword}`)
}

// 选择标签
const selectTag = (text: string): void => {
  searchKeyword.value = text
  handleSearch()
}

// 清除搜索
const clearSearch = (): void => {
  searchKeyword.value = ''
}

// 添加到搜索历史
const addToHistory = (keyword: string): void => {
  // 移除重复项
  const index = searchHistory.value.indexOf(keyword)
  if (index > -1) {
    searchHistory.value.splice(index, 1)
  }
  
  // 添加到开头
  searchHistory.value.unshift(keyword)
  
  // 限制历史数量
  if (searchHistory.value.length > 10) {
    searchHistory.value.pop()
  }
  
  // 保存到 localStorage
  saveHistory()
}

// 清除历史
const clearHistory = (): void => {
  searchHistory.value = []
  localStorage.removeItem('searchHistory')
  ElMessage.success('搜索历史已清除')
}

// 保存历史到 localStorage
const saveHistory = (): void => {
  try {
    localStorage.setItem('searchHistory', JSON.stringify(searchHistory.value))
  } catch (error) {
    console.error('保存搜索历史失败:', error)
  }
}

// 从 localStorage 加载历史
const loadHistory = (): void => {
  try {
    const saved = localStorage.getItem('searchHistory')
    if (saved) {
      searchHistory.value = JSON.parse(saved)
    }
  } catch (error) {
    console.error('加载搜索历史失败:', error)
  }
}

// 搜索历史
const searchHistory = ref<string[]>([])

// 热门搜索标签
const hotTags = ref([
  { text: 'iPhone 15', isHot: true },
  { text: '华为 Mate60', isHot: true },
  { text: '笔记本电脑', isHot: false },
  { text: '运动鞋', isHot: false },
  { text: '机械键盘', isHot: false },
  { text: '无线耳机', isHot: true },
  { text: '智能手表', isHot: false },
  { text: '平板电脑', isHot: false }
])

// 组件挂载时加载历史
onMounted(() => {
  if (!props.compact) {
    loadHistory()
  }
})
</script>

<style scoped>
.search-bar-component {
  width: 100%;
  background: rgba(26, 31, 58, 0.8);
  border-radius: 12px;
  padding: 20px;
  border: 1px solid rgba(0, 212, 255, 0.2);
  position: relative;
}

/* 紧凑模式（用于 Header） */
.search-bar-component.compact {
  background: transparent;
  border: none;
  padding: 0;
}

.search-bar-component.compact .search-container {
  background: rgba(255, 255, 255, 0.1);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 20px;
  padding: 4px 6px;
  transition: all 0.3s;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
}

.search-bar-component.compact .search-container:focus-within {
  background: rgba(255, 255, 255, 0.15);
  border-color: #00d4ff;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  transform: translateY(-1px);
}

.search-bar-component.compact .search-icon-wrapper {
  width: 32px;
  height: 32px;
  font-size: 16px;
}

.search-bar-component.compact .search-input {
  height: 32px;
  font-size: 13px;
}

.search-bar-component.compact .search-btn {
  height: 32px;
  padding: 0 14px;
}

.search-bar-component.compact .btn-text {
  font-size: 13px;
}

/* 搜索容器 */
.search-container {
  display: flex;
  align-items: center;
  gap: 0;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(0, 212, 255, 0.3);
  border-radius: 25px;
  padding: 5px 8px;
  transition: all 0.3s;
}

.search-container:focus-within {
  border-color: #00d4ff;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.3);
  background: rgba(255, 255, 255, 0.08);
}

/* 搜索图标 */
.search-icon-wrapper {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 36px;
  height: 36px;
  color: #00d4ff;
  font-size: 18px;
}

/* 搜索输入框 */
.search-input {
  flex: 1;
  height: 36px;
  border: none;
  outline: none;
  background: transparent;
  color: #fff;
  font-size: 14px;
  padding: 0 12px;
}

.search-input::placeholder {
  color: #888;
}

/* 清除按钮 */
.clear-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 28px;
  height: 28px;
  border: none;
  background: transparent;
  color: #888;
  cursor: pointer;
  border-radius: 50%;
  transition: all 0.3s;
}

.clear-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  color: #fff;
}

/* 搜索按钮 */
.search-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  height: 36px;
  padding: 0 20px;
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  border-radius: 20px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-btn:hover {
  background: linear-gradient(135deg, #00ff88, #00d4ff);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  transform: translateY(-1px);
}

.btn-text {
  font-size: 14px;
  font-weight: 600;
  color: #000;
}

/* 热门搜索标签 */
.hot-search-tags {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.tags-header {
  display: flex;
  align-items: center;
  gap: 8px;
  margin-bottom: 12px;
}

.tags-header i {
  color: #ff6b6b;
  font-size: 16px;
  animation: fire 1s ease-in-out infinite;
}

.tags-header span {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

@keyframes fire {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.2); }
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.tag-item {
  display: inline-flex;
  align-items: center;
  padding: 6px 14px;
  background: rgba(255, 255, 255, 0.05);
  border: 1px solid rgba(255, 255, 255, 0.1);
  border-radius: 16px;
  cursor: pointer;
  transition: all 0.3s;
}

.tag-item span {
  font-size: 13px;
  color: #ccc;
}

.tag-item:hover {
  background: rgba(0, 212, 255, 0.15);
  border-color: #00d4ff;
  transform: translateY(-2px);
}

.tag-item:hover span {
  color: #fff;
}

.tag-item.hot {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.2), rgba(255, 150, 150, 0.1));
  border-color: rgba(255, 107, 107, 0.4);
}

.tag-item.hot span {
  color: #ff6b6b;
  font-weight: 600;
}

.tag-item.hot:hover {
  background: linear-gradient(135deg, rgba(255, 107, 107, 0.3), rgba(255, 150, 150, 0.2));
  border-color: #ff6b6b;
}

/* 搜索历史 */
.search-history {
  margin-top: 16px;
  padding-top: 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 12px;
}

.history-title {
  display: flex;
  align-items: center;
  gap: 8px;
}

.history-title i {
  color: #00d4ff;
  font-size: 16px;
}

.history-title span {
  font-size: 14px;
  font-weight: 600;
  color: #fff;
}

.clear-history-btn {
  display: flex;
  align-items: center;
  gap: 6px;
  padding: 4px 10px;
  background: transparent;
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 12px;
  color: #888;
  font-size: 12px;
  cursor: pointer;
  transition: all 0.3s;
}

.clear-history-btn:hover {
  background: rgba(255, 255, 255, 0.1);
  border-color: #ff6b6b;
  color: #ff6b6b;
}

.history-list {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.history-item {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 12px;
  background: rgba(255, 255, 255, 0.03);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 14px;
  cursor: pointer;
  transition: all 0.3s;
}

.history-item i {
  font-size: 12px;
  color: #888;
}

.history-item span {
  font-size: 13px;
  color: #aaa;
}

.history-item:hover {
  background: rgba(0, 212, 255, 0.1);
  border-color: rgba(0, 212, 255, 0.3);
}

.history-item:hover i,
.history-item:hover span {
  color: #00d4ff;
}
</style>
