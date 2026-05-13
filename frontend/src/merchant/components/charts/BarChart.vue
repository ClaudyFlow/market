<template>
  <div class="chart-wrapper" :style="{ height: height + 'px' }">
    <svg :width="width" :height="height" class="bar-chart">
      <g v-for="(bar, index) in bars" :key="index">
        <rect
          :x="getBarX(index)"
          :y="getBarY(bar.value)"
          :width="barWidth"
          :height="getBarHeight(bar.value)"
          :fill="bar.color || defaultColor"
          rx="4"
          class="bar-rect"
        />
        <text
          v-if="showLabel"
          :x="getBarX(index) + barWidth / 2"
          :y="height - 8"
          text-anchor="middle"
          class="bar-label"
        >
          {{ bar.label }}
        </text>
        <text
          v-if="showValue"
          :x="getBarX(index) + barWidth / 2"
          :y="getBarY(bar.value) - 8"
          text-anchor="middle"
          class="bar-value"
        >
          {{ formatValue(bar.value) }}
        </text>
      </g>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface BarItem {
  label: string
  value: number
  color?: string
}

const props = withDefaults(defineProps<{
  data: BarItem[]
  height?: number
  showLabel?: boolean
  showValue?: boolean
  maxValue?: number
  barColor?: string
}>(), {
  height: 200,
  showLabel: true,
  showValue: true,
  barColor: '#00d4ff'
})

const width = ref(600)
const barWidth = computed(() => Math.min(60, (width.value - 40) / props.data.length - 10))
const barGap = computed(() => ((width.value - 40) - barWidth.value * props.data.length) / (props.data.length + 1))
const defaultColor = props.barColor

const bars = computed(() => props.data)

const maxVal = computed(() => {
  if (props.maxValue) return props.maxValue
  return Math.max(...props.data.map(d => d.value), 1)
})

const chartHeight = computed(() => props.height - 30)

const getBarX = (index: number) => {
  return 20 + barGap.value + index * (barWidth.value + barGap.value)
}

const getBarHeight = (value: number) => {
  return (value / maxVal.value) * chartHeight.value
}

const getBarY = (value: number) => {
  return chartHeight.value - getBarHeight(value)
}

const formatValue = (value: number) => {
  if (value >= 10000) {
    return (value / 10000).toFixed(1) + 'w'
  }
  if (value >= 1000) {
    return (value / 1000).toFixed(1) + 'k'
  }
  return value.toString()
}
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
}

.bar-chart {
  width: 100%;
}

.bar-rect {
  transition: all 0.3s ease;
}

.bar-rect:hover {
  filter: brightness(1.2);
}

.bar-label {
  fill: #888;
  font-size: 12px;
}

.bar-value {
  fill: #fff;
  font-size: 12px;
  font-weight: 500;
}
</style>