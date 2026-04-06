<template>
  <div class="page-container">
    <PageHeader title="商家数据面板" />

    <StatCards :cards="statCards" />

    <el-row :gutter="20">
      <el-col :span="12">
        <SciCard variant="gradient">
          <template #header><h3>快捷操作</h3></template>
          <div class="quick-actions">
            <SciButton type="primary" block @click="$router.push('/merchant/product')">商品管理</SciButton>
            <SciButton type="success" block @click="$router.push('/merchant/order')">订单管理</SciButton>
            <SciButton type="warning" block @click="$router.push('/merchant/coupon')">优惠券管理</SciButton>
            <SciButton type="info" block @click="$router.push('/merchant/review')">评价管理</SciButton>
          </div>
        </SciCard>
      </el-col>
      <el-col :span="12">
        <SciCard variant="gradient">
          <template #header><h3>店铺信息</h3></template>
          <div class="shop-info">
            <p>店铺名称：{{ shopInfo.name }}</p>
            <p>联系电话：{{ shopInfo.phone }}</p>
            <p>店铺地址：{{ shopInfo.address }}</p>
          </div>
        </SciCard>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { computed, reactive } from 'vue'
import { ShoppingCart, Ticket, ChatDotRound, DataAnalysis } from '@element-plus/icons-vue'
import { PageHeader, StatCards } from '@merchant/components'
import { SciButton, SciCard } from '@merchant/components/ui'

const stats = { products: 125, orders: 38, coupons: 5, reviews: 12 }

const statCards = computed(() => [
  { value: stats.products, label: '在售商品', type: 'primary' as const, icon: DataAnalysis },
  { value: stats.orders, label: '待处理订单', type: 'warning' as const, icon: ShoppingCart },
  { value: stats.coupons, label: '生效优惠券', type: 'success' as const, icon: Ticket },
  { value: stats.reviews, label: '待回复评价', type: 'info' as const, icon: ChatDotRound }
])

const shopInfo = reactive({
  name: '潮流数码旗舰店',
  phone: '400-123-4567',
  address: '深圳市福田区'
})
</script>

<style scoped>
.page-container {
  padding: 20px;
  background: linear-gradient(180deg, rgba(0, 212, 255, 0.05) 0%, transparent 100%);
  min-height: 100vh;
}

.quick-actions {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.shop-info p {
  margin: 8px 0;
  color: #88aacc;
}
</style>
