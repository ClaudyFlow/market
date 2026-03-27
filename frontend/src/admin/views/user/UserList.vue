<template>
  <div class="page-container">
    <header class="page-header">
      <div class="header-left">
        <h1 class="page-title">
          <el-icon><User /></el-icon>
          用户管理
        </h1>
      </div>
      <div class="header-right">
        <el-button type="primary" @click="openAddDialog">
          <el-icon><Plus /></el-icon>
          新增用户
        </el-button>
      </div>
    </header>

    <section class="stats-cards">
      <el-row :gutter="15">
        <el-col :span="6">
          <div class="stat-card primary">
            <div class="stat-icon"><el-icon><User /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ userStats.total }}</div>
              <div class="stat-label">用户总数</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card success">
            <div class="stat-icon"><el-icon><CircleCheck /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ userStats.active }}</div>
              <div class="stat-label">活跃用户</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card warning">
            <div class="stat-icon"><el-icon><Clock /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ userStats.todayNew }}</div>
              <div class="stat-label">今日新增</div>
            </div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-card danger">
            <div class="stat-icon"><el-icon><CircleClose /></el-icon></div>
            <div class="stat-info">
              <div class="stat-value">{{ userStats.banned }}</div>
              <div class="stat-label">封禁用户</div>
            </div>
          </div>
        </el-col>
      </el-row>
    </section>

    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="用户 ID">
          <el-input v-model="filterForm.userId" placeholder="请输入用户 ID" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="用户名">
          <el-input v-model="filterForm.userName" placeholder="请输入用户名" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="手机号">
          <el-input v-model="filterForm.phone" placeholder="请输入手机号" clearable style="width: 150px" />
        </el-form-item>
        <el-form-item label="用户状态">
          <el-select v-model="filterForm.status" placeholder="请选择状态" clearable style="width: 120px">
            <el-option label="正常" value="active" />
            <el-option label="封禁" value="banned" />
            <el-option label="注销" value="deleted" />
          </el-select>
        </el-form-item>
        <el-form-item label="注册时间">
          <el-date-picker
            v-model="filterForm.dateRange"
            type="daterange"
            range-separator="至"
            start-placeholder="开始日期"
            end-placeholder="结束日期"
            style="width: 240px"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="searchUsers">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-section">
      <el-table :data="userList" class="sci-table" style="width: 100%" v-loading="loading">
        <el-table-column prop="id" label="用户 ID" width="100" />
        <el-table-column prop="avatar" label="头像" width="80">
          <template #default="{ row }">
            <el-avatar :size="40" :src="row.avatar || `https://via.placeholder.com/40x40/00d4ff/fff?text=${row.name?.[0] || 'U'}`" />
          </template>
        </el-table-column>
        <el-table-column prop="name" label="用户名" min-width="120" />
        <el-table-column prop="phone" label="手机号" width="130" />
        <el-table-column prop="email" label="邮箱" min-width="150" />
        <el-table-column prop="credit" label="积分" width="80">
          <template #default="{ row }">
            <span class="credit-text">{{ row.credit }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="registerTime" label="注册时间" width="160" />
        <el-table-column prop="status" label="状态" width="80">
          <template #default="{ row }">
            <el-tag :type="getStatusType(row.status)" size="small">
              {{ getStatusText(row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" text size="small" @click="viewUser(row)">详情</el-button>
            <el-button
              v-if="row.status === 'active'"
              type="warning"
              text
              size="small"
              @click="banUser(row)"
            >
              封禁
            </el-button>
            <el-button
              v-if="row.status === 'banned'"
              type="success"
              text
              size="small"
              @click="unbanUser(row)"
            >
              解封
            </el-button>
            <el-button type="danger" text size="small" @click="deleteUser(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="pagination-bar">
        <el-pagination
          v-model:current-page="pagination.currentPage"
          v-model:page-size="pagination.pageSize"
          :total="pagination.total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          @size-change="loadUserList"
          @current-change="loadUserList"
        />
      </div>
    </section>

    <el-dialog
      v-model="dialog.visible"
      :title="dialog.title"
      width="600px"
      :close-on-click-modal="false"
    >
      <el-form :model="userForm" label-width="80px">
        <el-form-item label="用户名" required>
          <el-input v-model="userForm.name" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="手机号" required>
          <el-input v-model="userForm.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="邮箱" required>
          <el-input v-model="userForm.email" placeholder="请输入邮箱" />
        </el-form-item>
        <el-form-item label="密码" required v-if="!dialog.isEdit">
          <el-input v-model="userForm.password" type="password" placeholder="请输入密码" show-password />
        </el-form-item>
        <el-form-item label="初始积分">
          <el-input-number v-model="userForm.credit" :min="0" style="width: 100%" />
        </el-form-item>
        <el-form-item label="用户状态">
          <el-radio-group v-model="userForm.status">
            <el-radio label="active">正常</el-radio>
            <el-radio label="banned">封禁</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialog.visible = false">取消</el-button>
        <el-button type="primary" @click="saveUser">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { User, Plus, CircleCheck, CircleClose, Clock } from '@element-plus/icons-vue'

interface UserItem {
  id: number
  name: string
  phone: string
  email: string
  credit: number
  registerTime: string
  status: string
  avatar?: string
}

interface FilterForm {
  userId: string
  userName: string
  phone: string
  status: string
  dateRange: [Date, Date] | null
}

interface UserStats {
  total: number
  active: number
  todayNew: number
  banned: number
}

interface UserForm {
  name: string
  phone: string
  email: string
  password: string
  credit: number
  status: string
}

interface Pagination {
  currentPage: number
  pageSize: number
  total: number
}

interface Dialog {
  visible: boolean
  title: string
  isEdit: boolean
  currentUser: UserItem | null
}

const loading = ref(false)
const userList = ref<UserItem[]>([])
const filterForm = reactive<FilterForm>({
  userId: '',
  userName: '',
  phone: '',
  status: '',
  dateRange: null
})

const userStats = ref<UserStats>({
  total: 12580,
  active: 8520,
  todayNew: 156,
  banned: 89
})

const userForm = reactive<UserForm>({
  name: '',
  phone: '',
  email: '',
  password: '',
  credit: 100,
  status: 'active'
})

const pagination = reactive<Pagination>({
  currentPage: 1,
  pageSize: 10,
  total: 0
})

const dialog = reactive<Dialog>({
  visible: false,
  title: '新增用户',
  isEdit: false,
  currentUser: null
})

const mockUserData: UserItem[] = [
  { id: 1001, name: '张三', phone: '138****1234', email: 'zhang***@qq.com', credit: 1250, registerTime: '2026-01-15', status: 'active', avatar: '' },
  { id: 1002, name: '李四', phone: '139****5678', email: 'li***@163.com', credit: 890, registerTime: '2026-01-20', status: 'active', avatar: '' },
  { id: 1003, name: '王五', phone: '137****9012', email: 'wang***@gmail.com', credit: 2100, registerTime: '2026-02-01', status: 'active', avatar: '' },
  { id: 1004, name: '赵六', phone: '136****3456', email: 'zhao***@qq.com', credit: 560, registerTime: '2026-02-10', status: 'banned', avatar: '' },
  { id: 1005, name: '钱七', phone: '135****7890', email: 'qian***@126.com', credit: 3200, registerTime: '2026-02-15', status: 'active', avatar: '' },
  { id: 1006, name: '孙八', phone: '134****2345', email: 'sun***@qq.com', credit: 780, registerTime: '2026-02-20', status: 'active', avatar: '' },
  { id: 1007, name: '周九', phone: '133****6789', email: 'zhou***@163.com', credit: 1500, registerTime: '2026-03-01', status: 'active', avatar: '' },
  { id: 1008, name: '吴十', phone: '132****0123', email: 'wu***@gmail.com', credit: 420, registerTime: '2026-03-05', status: 'deleted', avatar: '' }
]

const loadUserList = () => {
  loading.value = true
  setTimeout(() => {
    userList.value = mockUserData
    pagination.total = mockUserData.length
    loading.value = false
  }, 500)
}

const searchUsers = () => {
  ElMessage.success('搜索功能演示')
  loadUserList()
}

const resetFilter = () => {
  filterForm.userId = ''
  filterForm.userName = ''
  filterForm.phone = ''
  filterForm.status = ''
  filterForm.dateRange = null
}

const getStatusType = (status: string) => {
  const map: Record<string, string> = { active: 'success', banned: 'danger', deleted: 'info' }
  return map[status] || 'info'
}

const getStatusText = (status: string) => {
  const map: Record<string, string> = { active: '正常', banned: '封禁', deleted: '注销' }
  return map[status] || status
}

const openAddDialog = () => {
  dialog.visible = true
  dialog.title = '新增用户'
  dialog.isEdit = false
  userForm.name = ''
  userForm.phone = ''
  userForm.email = ''
  userForm.password = ''
  userForm.credit = 100
  userForm.status = 'active'
}

const viewUser = (user: UserItem) => {
  ElMessage.info(`查看用户详情：${user.name}`)
}

const banUser = (user: UserItem) => {
  ElMessageBox.confirm(`确定要封禁用户"${user.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    user.status = 'banned'
    ElMessage.success('封禁成功')
  }).catch(() => {})
}

const unbanUser = (user: UserItem) => {
  ElMessageBox.confirm(`确定要解封用户"${user.name}"吗？`, '提示', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    user.status = 'active'
    ElMessage.success('解封成功')
  }).catch(() => {})
}

const deleteUser = (user: UserItem) => {
  ElMessageBox.confirm(`确定要删除用户"${user.name}"吗？此操作不可恢复！`, '警告', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'error'
  }).then(() => {
    const index = userList.value.findIndex(item => item.id === user.id)
    if (index !== -1) {
      userList.value.splice(index, 1)
      ElMessage.success('删除成功')
    }
  }).catch(() => {})
}

const saveUser = () => {
  if (!userForm.name || !userForm.phone) {
    ElMessage.warning('请填写必填项')
    return
  }
  ElMessage.success(dialog.isEdit ? '保存成功' : '新增成功')
  dialog.visible = false
  loadUserList()
}

onMounted(() => {
  loadUserList()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.9), rgba(26, 31, 58, 0.7));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.1);
}

.page-title {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 22px;
  font-weight: bold;
  color: #fff;
}

.page-title .el-icon {
  color: var(--mall-primary);
  font-size: 26px;
  filter: drop-shadow(0 0 8px rgba(0, 212, 255, 0.5));
}

.stats-cards {
  
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  transition: all 0.3s;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  height: 2px;
  background: linear-gradient(90deg, transparent, var(--glow-color), transparent);
}

.stat-card:hover {
  transform: translateY(-3px);
  border-color: var(--glow-color);
  box-shadow: 0 8px 30px rgba(0, 212, 255, 0.15);
}

.stat-card.primary { --glow-color: #00d4ff; }
.stat-card.success { --glow-color: #00ff88; }
.stat-card.warning { --glow-color: #ffaa00; }
.stat-card.danger { --glow-color: #ff6666; }

.stat-icon {
  width: 50px;
  height: 50px;
  border-radius: 10px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: #fff;
  flex-shrink: 0;
}

.stat-card.primary .stat-icon {
  background: linear-gradient(135deg, #00d4ff, #00a8cc);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
}

.stat-card.success .stat-icon {
  background: linear-gradient(135deg, #00ff88, #00cc6a);
  box-shadow: 0 0 15px rgba(0, 255, 136, 0.4);
}

.stat-card.warning .stat-icon {
  background: linear-gradient(135deg, #ffaa00, #ff8800);
  box-shadow: 0 0 15px rgba(255, 170, 0, 0.4);
}

.stat-card.danger .stat-icon {
  background: linear-gradient(135deg, #ff6666, #ff4444);
  box-shadow: 0 0 15px rgba(255, 102, 102, 0.4);
}

.stat-info {
  flex: 1;
}

.stat-value {
  font-size: 24px;
  font-weight: bold;
  color: #fff;
}

.stat-label {
  font-size: 13px;
  color: #888;
  
}

/* 搜索栏优化 */
.search-bar {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.2);
  border-radius: 12px;
  padding: 20px;
  
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.search-bar :deep(.el-form-item) {
  
  
}

.search-bar :deep(.el-form-item__label) {
  color: #ccc;
  font-weight: 500;
}

.search-bar :deep(.el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 8px;
  padding: 8px 12px;
  transition: all 0.3s;
}

.search-bar :deep(.el-input__wrapper:hover) {
  border-color: rgba(0, 212, 255, 0.3);
}

.search-bar :deep(.el-input__wrapper.is-focus) {
  border-color: var(--mall-primary);
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.2);
}

.search-bar :deep(.el-input__inner) {
  color: #fff;
}

.search-bar :deep(.el-select .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-select__wrapper) {
  color: #fff;
}

.search-bar :deep(.el-date-editor .el-input__wrapper) {
  background: rgba(10, 14, 26, 0.6);
}

.search-bar :deep(.el-button--primary) {
  background: linear-gradient(135deg, #00d4ff, #00ff88);
  border: none;
  box-shadow: 0 0 15px rgba(0, 212, 255, 0.4);
  color: #000;
  font-weight: bold;
  padding: 10px 20px;
}

.search-bar :deep(.el-button--primary:hover) {
  box-shadow: 0 0 25px rgba(0, 212, 255, 0.6);
  transform: translateY(-2px);
}

.search-bar :deep(.el-button) {
  border-radius: 8px;
}

/* 表格区域 */
.table-section {
  background: linear-gradient(135deg, rgba(26, 31, 58, 0.8), rgba(26, 31, 58, 0.6));
  border: 1px solid rgba(0, 212, 255, 0.15);
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 4px 20px rgba(0, 212, 255, 0.08);
}

.credit-text {
  color: var(--mall-primary);
  font-weight: bold;
  font-size: 14px;
}

.pagination-bar {
  
  display: flex;
  justify-content: flex-end;
}

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
</style>
