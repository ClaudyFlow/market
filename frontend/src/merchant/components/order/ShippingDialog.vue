<template>
  <el-dialog v-model="visible" title="发货操作" width="500px">
    <el-form :model="form" label-width="80px">
      <el-form-item label="物流公司">
        <el-select v-model="form.company" placeholder="请选择物流公司" style="width: 100%">
          <el-option
            v-for="item in logisticsCompanies"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="物流单号">
        <el-input v-model="form.trackingNo" placeholder="请输入物流单号" />
      </el-form-item>
      <el-form-item label="备注">
        <el-input v-model="form.remark" type="textarea" :rows="3" placeholder="选填备注" />
      </el-form-item>
    </el-form>
    <template #footer>
      <el-button @click="visible = false">取消</el-button>
      <el-button type="primary" @click="$emit('confirm')">确认发货</el-button>
    </template>
  </el-dialog>
</template>

<script setup lang="ts">
import { LOGISTICS_COMPANIES } from '../../composables/useOrderList'
import type { ShippingForm } from '../../types/order'

const visible = defineModel<boolean>({ default: false })
const form = defineModel<ShippingForm>('form', { default: () => ({ company: '', trackingNo: '', remark: '' }) })

defineEmits<{
  confirm: []
}>()

const logisticsCompanies = LOGISTICS_COMPANIES
</script>
