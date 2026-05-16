<template>
  <div class="page-container">
    <header class="page-header">
      <h1 class="page-title">
        <el-icon><Tickets /></el-icon>
        平台活动参与设置
      </h1>
    </header>

    <section class="info-banner">
      <el-alert type="info" :closable="false">
        <template #title>
          平台活动说明：管理端创建折扣活动后，商家可在此设置参与方式。
          选择「折扣」可自定义折扣率，选择「立减」可设置固定金额优惠，或选择「退出」不参与该活动。
        </template>
      </el-alert>
    </section>

    <section class="table-section">
      <el-table :data="activityList" class="sci-table" v-loading="loading" row-key="activity.id">
        <el-table-column prop="activity.name" label="活动名称" min-width="150">
          <template #default="{ row }">
            <div class="activity-name">{{ row.activity.name }}</div>
            <div class="activity-desc text-muted">{{ row.activity.description }}</div>
          </template>
        </el-table-column>
        <el-table-column prop="activity.discountRate" label="平台折扣" width="100">
          <template #default="{ row }">
            <span class="platform-rate">{{ (row.activity.discountRate * 10).toFixed(1) }}折</span>
          </template>
        </el-table-column>
        <el-table-column label="时间" min-width="180">
          <template #default="{ row }">
            <div>{{ formatDate(row.activity.startTime) }} ~ {{ formatDate(row.activity.endTime) }}</div>
          </template>
        </el-table-column>
        <el-table-column label="我的设置" min-width="200">
          <template #default="{ row }">
            <div v-if="row.optedOut" class="optout-status">
              <el-tag type="danger" size="small">已退出</el-tag>
              <span v-if="row.remark" class="optout-remark text-muted">（原因：{{ row.remark }}）</span>
            </div>
            <div v-else-if="row.discountType" class="my-setting">
              <el-tag :type="row.discountType === 'DISCOUNT' ? 'success' : 'warning'" size="small">
                {{ row.discountType === 'DISCOUNT' ? '折扣' : '立减' }}
              </el-tag>
              <span v-if="row.discountType === 'DISCOUNT' && row.customDiscountRate" class="setting-value">
                {{ (row.customDiscountRate * 10).toFixed(1) }}折
              </span>
              <span v-if="row.discountType === 'MONEY' && row.customDiscountAmount" class="setting-value">
                减￥{{ row.customDiscountAmount }}
              </span>
            </div>
            <div v-else class="no-setting text-muted">使用平台折扣</div>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button type="primary" size="small" @click="openSettingDialog(row)">
              {{ row.optedOut || row.discountType ? '修改设置' : '设置参与' }}
            </el-button>
            <el-button v-if="!row.optedOut" type="danger" size="small" @click="quickOptOut(row)">
              退出
            </el-button>
          </template>
        </el-table-column>
      </el-table>

      <div v-if="activityList.length === 0 && !loading" class="empty-state">
        <el-empty description="暂无进行中的平台活动" />
      </div>
    </section>

    <!-- 设置参与方式弹窗 -->
    <el-dialog v-model="dialogVisible" title="活动参与设置" width="500px">
      <el-form :model="formData" label-width="120px">
        <el-form-item label="活动名称">
          <span class="fw-500">{{ currentActivity?.activity?.name }}</span>
        </el-form-item>
        <el-form-item label="平台折扣">
          <span class="platform-rate">{{ currentActivity ? (currentActivity.activity.discountRate * 10).toFixed(1) : '' }}折</span>
        </el-form-item>
        <el-divider />

        <el-form-item label="参与方式">
          <el-radio-group v-model="participationType" @change="onParticipationTypeChange">
            <el-radio label="platform">使用平台折扣</el-radio>
            <el-radio label="discount">自定义折扣</el-radio>
            <el-radio label="money">自定义立减</el-radio>
            <el-radio label="optout">退出活动</el-radio>
          </el-radio-group>
        </el-form-item>

        <el-form-item v-if="participationType === 'discount'" label="折扣率">
          <el-input-number v-model="formData.customDiscountRate" :min="0.01" :max="0.99" :precision="2" :step="0.01" />
          <span style="margin-left: 8px; color: #888">（如0.85表示85折）</span>
        </el-form-item>

        <el-form-item v-if="participationType === 'money'" label="立减金额">
          <el-input-number v-model="formData.customDiscountAmount" :min="0.01" :precision="2" :step="0.1" />
          <span style="margin-left: 8px; color: #888">元</span>
        </el-form-item>

        <el-form-item label="备注">
          <el-input v-model="formData.remark" type="textarea" :rows="2" placeholder="可选，如：商品库存不足" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="submitSetting">保存设置</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Tickets } from '@element-plus/icons-vue'
import { merchantActivityApi } from '@merchant/api/activity'

const loading = ref(false)
const activityList = ref<any[]>([])
const dialogVisible = ref(false)
const participationType = ref('platform')
const currentActivity = ref<any>(null)

const formData = ref({
  customDiscountRate: 0.90,
  customDiscountAmount: 0,
  remark: ''
})

const formatDate = (d: any) => d ? new Date(d).toLocaleString() : '-'

const loadActivities = async () => {
  loading.value = true
  try {
    const res: any = await merchantActivityApi.getMyActivities()
    activityList.value = res.data || []
  } catch (e) {
    ElMessage.error('加载活动失败')
  } finally {
    loading.value = false
  }
}

const onParticipationTypeChange = (val: string) => {
  if (val === 'optout') {
    // nothing special needed
  }
}

const openSettingDialog = (row: any) => {
  currentActivity.value = row
  if (row.optedOut) {
    participationType.value = 'optout'
  } else if (row.discountType === 'DISCOUNT') {
    participationType.value = 'discount'
    formData.value.customDiscountRate = row.customDiscountRate || 0.90
  } else if (row.discountType === 'MONEY') {
    participationType.value = 'money'
    formData.value.customDiscountAmount = row.customDiscountAmount || 0
  } else {
    participationType.value = 'platform'
  }
  formData.value.remark = row.remark || ''
  dialogVisible.value = true
}

const submitSetting = async () => {
  if (!currentActivity.value) return

  const activityId = currentActivity.value.activity.id
  const data: any = {
    optedOut: participationType.value === 'optout',
    remark: formData.value.remark
  }

  if (participationType.value === 'discount') {
    data.discountType = 'DISCOUNT'
    data.customDiscountRate = formData.value.customDiscountRate
  } else if (participationType.value === 'money') {
    data.discountType = 'MONEY'
    data.customDiscountAmount = formData.value.customDiscountAmount
  }

  try {
    await merchantActivityApi.setSetting(activityId, data)
    ElMessage.success('设置成功')
    dialogVisible.value = false
    loadActivities()
  } catch (e) {
    ElMessage.error('设置失败')
  }
}

const quickOptOut = async (row: any) => {
  try {
    await ElMessageBox.confirm('确定退出该活动吗？退出后将不享受平台活动的优惠。', '提示', { type: 'warning' })
    await merchantActivityApi.optOut(row.activity.id, { remark: '' })
    ElMessage.success('已退出活动')
    loadActivities()
  } catch (e: any) {
    if (e !== 'cancel') ElMessage.error('操作失败')
  }
}

onMounted(() => {
  loadActivities()
})
</script>

<style scoped>
.page-container { padding: 20px; }
.page-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.page-title { font-size: 20px; display: flex; align-items: center; gap: 8px; margin: 0; }
.info-banner { margin-bottom: 16px; }
.activity-name { font-weight: 500; }
.activity-desc { font-size: 12px; margin-top: 2px; }
.platform-rate { color: #f56c6c; font-weight: bold; }
.optout-status { display: flex; align-items: center; gap: 6px; }
.optout-remark { font-size: 12px; }
.my-setting { display: flex; align-items: center; gap: 6px; }
.setting-value { font-weight: 500; color: #409eff; }
.no-setting { font-size: 13px; }
.text-muted { color: #999; }
.fw-500 { font-weight: 500; }
.empty-state { padding: 40px 0; text-align: center; }
</style>
