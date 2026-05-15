<template>
  <div class="stat-cards">
    <el-row :gutter="15">
      <el-col v-for="card in cards" :key="card.label" :span="card.span || 6">
        <SciStatCard
          :value="card.value"
          :label="card.label"
          :type="card.type"
          @click="$emit('click', card)"
        >
          <template #icon>
            <el-icon>
              <component :is="card.icon" />
            </el-icon>
          </template>
        </SciStatCard>
      </el-col>
    </el-row>
  </div>
</template>

<script setup lang="ts">
import { SciStatCard } from '../ui'

interface StatCard {
  value: string | number
  label: string
  type: 'primary' | 'success' | 'warning' | 'info'
  icon: any
  span?: number
}

interface Props {
  cards: StatCard[]
}

defineProps<Props>()

defineEmits<{
  'click': [card: StatCard]
}>()
</script>

<style scoped>
.stat-cards {
  margin-bottom: 20px;
}
</style>
