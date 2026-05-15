<template>
  <div class="page-container">
    <PageHeader title="商品管理" :icon="Goods">
      <template #actions>
        <SciButton type="primary" @click="打开新增窗口">
          <el-icon><Plus /></el-icon>
          新增商品
        </SciButton>
      </template>
    </PageHeader>

    <SearchPanel
      v-model="filterForm"
      :fields="searchFields"
      @search="搜索商品"
      @reset="重置筛选"
    />

    <DataPanel
      :data="商品列表"
      :loading="加载中"
      v-model:current-page="分页.当前页"
      v-model:page-size="分页.每页数量"
      :total="分页.总数"
      @size-change="加载商品列表"
      @current-change="加载商品列表"
    >
      <el-table-column prop="id" label="ID" width="80" />
      <el-table-column prop="image" label="商品图片" width="100">
        <template #default="{ row }">
          <SciImage
            :src="row.image || 'https://via.placeholder.com/60x60/1a2a4a/00d4ff?text=商品'"
            style="width: 60px; height: 60px"
            :preview-src-list="[row.image]"
          />
        </template>
      </el-table-column>
      <el-table-column prop="name" label="商品名称" min-width="200" />
      <el-table-column prop="category" label="分类" width="100">
        <template #default="{ row }">
          <SciTag type="info">{{ row.category }}</SciTag>
        </template>
      </el-table-column>
      <el-table-column label="价格" width="100">
        <template #default="{ row }">
          <SciPrice :amount="row.price" />
        </template>
      </el-table-column>
      <el-table-column prop="stock" label="库存" width="80" />
      <el-table-column prop="sales" label="销量" width="80" />
      <el-table-column prop="status" label="状态" width="80">
        <template #default="{ row }">
          <SciTag :type="获取状态类型(row.status)">{{ 获取状态文本(row.status) }}</SciTag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="260" fixed="right">
        <template #default="{ row }">
          <SciButton type="primary" size="small" text @click="编辑商品(row)">编辑</SciButton>
          <SciButton type="success" size="small" text @click="查看商品(row)">查看</SciButton>
          <SciButton
            :type="row.status === 'active' ? 'warning' : 'success'"
            size="small"
            text
            @click="切换状态(row)"
          >
            {{ row.status === 'active' ? '下架' : '上架' }}
          </SciButton>
          <SciButton type="danger" size="small" text @click="删除商品(row)">删除</SciButton>
        </template>
      </el-table-column>
    </DataPanel>

    <!-- 新增/编辑对话框 -->
    <el-dialog v-model="对话框.可见" :title="对话框.标题" width="600px">
      <el-form :model="对话框.表单" label-width="80px">
        <el-form-item label="商品名称">
          <SciInput v-model="对话框.表单.name" placeholder="请输入商品名称" />
        </el-form-item>
        <el-form-item label="商品分类">
          <SciSelect v-model="对话框.表单.category" placeholder="请选择分类">
            <el-option label="手机数码" value="digital" />
            <el-option label="电脑办公" value="office" />
            <el-option label="家用电器" value="appliance" />
            <el-option label="服装鞋包" value="fashion" />
            <el-option label="家居家装" value="home" />
            <el-option label="美妆护肤" value="beauty" />
          </SciSelect>
        </el-form-item>
        <el-form-item label="商品价格">
          <SciInput v-model="对话框.表单.price" type="number" placeholder="请输入价格" />
        </el-form-item>
        <el-form-item label="库存数量">
          <SciInput v-model="对话框.表单.stock" type="number" placeholder="请输入库存" />
        </el-form-item>
      </el-form>
      <template #footer>
        <SciButton @click="对话框.可见 = false">取消</SciButton>
        <SciButton type="primary" @click="保存商品">保存</SciButton>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Goods, Plus } from '@element-plus/icons-vue'
import { PageHeader, DataPanel, SearchPanel } from '@merchant/components'
import { SciButton, SciInput, SciSelect, SciTag, SciImage, SciPrice } from '@merchant/components/ui'

const 加载中 = ref(false)
const 商品列表 = ref<any[]>([])

const filterForm = reactive({
  商品名称: '',
  分类: '',
  状态: ''
})

const searchFields = [
  { prop: '商品名称', label: '商品名称', type: 'input' as const, placeholder: '请输入商品名称' },
  {
    prop: '分类',
    label: '商品分类',
    type: 'select' as const,
    placeholder: '请选择分类',
    options: [
      { label: '手机数码', value: 'digital' },
      { label: '电脑办公', value: 'office' },
      { label: '家用电器', value: 'appliance' },
      { label: '服装鞋包', value: 'fashion' },
      { label: '家居家装', value: 'home' },
      { label: '美妆护肤', value: 'beauty' }
    ]
  },
  {
    prop: '状态',
    label: '商品状态',
    type: 'select' as const,
    placeholder: '请选择状态',
    options: [
      { label: '在售', value: 'active' },
      { label: '下架', value: 'inactive' },
      { label: '售罄', value: 'sold_out' }
    ]
  }
]

const 分页 = reactive({ 当前页: 1, 每页数量: 10, 总数: 0 })

const 对话框 = reactive({
  可见: false,
  标题: '新增商品',
  表单: { name: '', category: '', price: '', stock: '' }
})

const 模拟商品数据 = [
  { id: 1, image: '', name: '无线蓝牙耳机', category: '手机数码', price: 199, stock: 150, sales: 520, status: 'active' },
  { id: 2, image: '', name: '机械键盘', category: '电脑办公', price: 329, stock: 80, sales: 380, status: 'active' },
  { id: 3, image: '', name: '智能手环', category: '手机数码', price: 149, stock: 0, sales: 650, status: 'sold_out' },
  { id: 4, image: '', name: '空气净化器', category: '家用电器', price: 999, stock: 50, sales: 120, status: 'inactive' }
]

const 加载商品列表 = () => {
  加载中.value = true
  setTimeout(() => {
    商品列表.value = 模拟商品数据
    分页.总数 = 模拟商品数据.length
    加载中.value = false
  }, 500)
}

const 搜索商品 = () => {
  ElMessage.success('搜索功能演示')
  加载商品列表()
}

const 重置筛选 = () => {
  filterForm.商品名称 = ''
  filterForm.分类 = ''
  filterForm.状态 = ''
}

const 获取状态类型 = (状态: string) => {
  const 映射: Record<string, any> = { active: 'success', inactive: 'warning', sold_out: 'info' }
  return 映射[状态] || 'info'
}

const 获取状态文本 = (状态: string) => {
  const 映射: Record<string, string> = { active: '在售', inactive: '下架', sold_out: '售罄' }
  return 映射[状态] || 状态
}

const 打开新增窗口 = () => {
  对话框.标题 = '新增商品'
  对话框.表单 = { name: '', category: '', price: '', stock: '' }
  对话框.可见 = true
}

const 编辑商品 = (商品: any) => {
  对话框.标题 = '编辑商品'
  对话框.表单 = { ...商品 }
  对话框.可见 = true
}

const 查看商品 = (商品: any) => {
  ElMessage.info(`查看商品: ${商品.name}`)
}

const 切换状态 = async (商品: any) => {
  const 新状态 = 商品.status === 'active' ? 'inactive' : 'active'
  商品.status = 新状态
  ElMessage.success(`已${新状态 === 'active' ? '上架' : '下架'}`)
}

const 删除商品 = async (商品: any) => {
  await ElMessageBox.confirm(`确定删除商品"${商品.name}"吗？`, '提示', { type: 'warning' })
  商品列表.value = 商品列表.value.filter(item => item.id !== 商品.id)
  ElMessage.success('删除成功')
}

const 保存商品 = () => {
  ElMessage.success('保存成功')
  对话框.可见 = false
  加载商品列表()
}

onMounted(() => {
  加载商品列表()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}
</style>
