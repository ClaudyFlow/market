<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Collection /></el-icon>
        商品分类管理
      </h1>
      <el-button type="primary" @click="showCreateDialog">
        <el-icon><Plus /></el-icon>
        新增分类
      </el-button>
    </header>

    <section class="search-bar">
      <el-form :inline="true" :model="filterForm">
        <el-form-item label="分类名称">
          <el-input v-model="filterForm.name" placeholder="请输入分类名称" clearable style="width: 180px" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadCategories">搜索</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </section>

    <section class="table-section">
      <el-table :data="categoryList" class="sci-table" v-loading="loading" row-key="id" default-expand-all>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="name" label="分类名称" min-width="150">
          <template #default="{ row }">
            <span class="category-name">{{ row.name }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路径" min-width="120" />
        <el-table-column prop="icon" label="图标" width="120">
          <template #default="{ row }">
            <span v-if="row.icon">{{ row.icon }}</span>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sortOrder" label="排序" width="80" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'ACTIVE' ? 'success' : 'danger'" size="small">
              {{ row.status === 'ACTIVE' ? '启用' : '禁用' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="editCategory(row)">编辑</el-button>
            <el-button type="danger" size="small" @click="deleteCategory(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </section>

    <!-- Create/Edit Dialog -->
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px">
      <el-form :model="formData" label-width="100px">
        <el-form-item label="分类名称" required>
          <el-input v-model="formData.name" placeholder="如：数码产品、日用品" />
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="formData.path" placeholder="如：/digital" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="formData.icon" placeholder="Font Awesome 类名，如：fas fa-desktop" />
        </el-form-item>
        <el-form-item label="父分类">
          <el-select v-model="formData.parentId" placeholder="顶级分类（无父级）" clearable style="width: 100%">
            <el-option label="顶级分类" :value="null" />
            <el-option v-for="cat in flatCategories" :key="cat.id" :label="cat.name" :value="cat.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="排序值">
          <el-input-number v-model="formData.sortOrder" :min="0" :max="999" />
        </el-form-item>
        <el-form-item label="状态">
          <el-radio-group v-model="formData.status">
            <el-radio label="ACTIVE">启用</el-radio>
            <el-radio label="DISABLED">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitForm">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Collection, Plus } from '@element-plus/icons-vue'
import { categoryApi } from '@admin/api/category'

const loading = ref(false)
const categoryList = ref<any[]>([])
const dialogVisible = ref(false)
const dialogTitle = ref('新增分类')
const isEdit = ref(false)

const filterForm = ref({ name: '' })

const formData = ref({
  id: null as number | null,
  name: '',
  path: '',
  icon: '',
  parentId: null as number | null,
  sortOrder: 0,
  status: 'ACTIVE'
})

const flatCategories = computed(() => {
  const flat: any[] = []
  const flatten = (cats: any[], depth = 0) => {
    for (const cat of cats) {
      flat.push(cat)
    }
  }
  flatten(categoryList.value)
  return flat
})

const loadCategories = async () => {
  loading.value = true
  try {
    const res: any = await categoryApi.getAll()
    categoryList.value = res.data || []
  } catch (e) {
    ElMessage.error('加载分类失败')
  } finally {
    loading.value = false
  }
}

const resetFilter = () => {
  filterForm.value.name = ''
  loadCategories()
}

const showCreateDialog = () => {
  isEdit.value = false
  dialogTitle.value = '新增分类'
  formData.value = { id: null, name: '', path: '', icon: '', parentId: null, sortOrder: 0, status: 'ACTIVE' }
  dialogVisible.value = true
}

const editCategory = (row: any) => {
  isEdit.value = true
  dialogTitle.value = '编辑分类'
  formData.value = { ...row }
  dialogVisible.value = true
}

const submitForm = async () => {
  if (!formData.value.name) {
    ElMessage.warning('请填写分类名称')
    return
  }
  try {
    if (isEdit.value) {
      await categoryApi.update(formData.value.id!, formData.value)
      ElMessage.success('更新成功')
    } else {
      await categoryApi.create(formData.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadCategories()
  } catch (e) {
    ElMessage.error('操作失败')
  }
}

const deleteCategory = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定删除该分类吗？', '提示', { type: 'warning' })
    await categoryApi.delete(row.id)
    ElMessage.success('删除成功')
    loadCategories()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(() => {
  loadCategories()
})
</script>

<style scoped>
.page-container { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 20px; }
.page-title { font-size: 20px; display: flex; align-items: center; gap: 8px; margin: 0; }
.category-name { font-weight: 500; }
.text-muted { color: #999; }
.science-table { margin-top: 15px; }
</style>
