<template>
  <div class="chart-wrapper" :style="{ height: height + 'px' }">
    <svg :width="width" :height="height" class="pie-chart">
      <g :transform="`translate(${width / 2}, ${height / 2})`">
        <path
          v-for="(slice, index) in slices"
          :key="index"
          :d="getSlicePath(index)"
          :fill="slice.color"
          class="pie-slice"
          @mouseenter="hoveredIndex = index"
          @mouseleave="hoveredIndex = -1"
        />
        <circle
          v-if="hoveredIndex >= 0"
          :r="outerRadius - 5"
          :fill="'transparent'"
          :stroke="slices[hoveredIndex].color"
          stroke-width="3"
          class="hover-circle"
        />
        <text v-if="showLegend" :y="-(innerRadius - 10)" text-anchor="middle" class="center-value">
          {{ totalValue }}
        </text>
      </g>
      <g v-if="showLegend" :transform="`translate(${width - 100}, 20)`">
        <g v-for="(slice, index) in slices" :key="index" :transform="`translate(0, ${index * 22})`">
          <rect
            :width="14"
            :height="14"
            :fill="slice.color"
            rx="2"
          />
          <text
            x="20"
            y="12"
            class="legend-label"
          >
            {{ slice.label }}
          </text>
          <text
            :x="90"
            y="12"
            text-anchor="end"
            class="legend-value"
          >
            {{ slice.value }}
          </text>
        </g>
      </g>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface PieSlice {
  label: string
  value: number
  color?: string
}

const props = withDefaults(defineProps<{
  data: PieSlice[]
  height?: number
  showLegend?: boolean
}>(), {
  height: 280,
  showLegend: true
})

const uid = Math.random().toString(36).substr(2, 9)
const width = ref(400)
const hoveredIndex = ref(-1)

const defaultColors = ['#00d4ff', '#00ff88', '#ffd700', '#ff8800', '#ff4444', '#aa00ff', '#00ffff', '#ff00ff']

const slices = computed(() => {
  return props.data.map((item, index) => ({
    ...item,
    color: item.color || defaultColors[index % defaultColors.length]
  }))
})

const totalValue = computed(() => {
  return props.data.reduce((sum, item) => sum + item.value, 0)
})

const outerRadius = computed(() => Math.min(width.value, props.height) / 2 - 20)
const innerRadius = computed(() => outerRadius.value * 0.6)

const getSlicePath = (index: number) => {
  const slices2 = slices.value
  if (slices2.length === 0) return ''

  let startAngle = 0
  for (let i = 0; i < index; i++) {
    startAngle += (slices2[i].value / totalValue.value) * 360
  }
  const angle = (slices2[index].value / totalValue.value) * 360

  const startRad = (startAngle - 90) * Math.PI / 180
  const endRad = (startAngle + angle - 90) * Math.PI / 180

  const x1 = Math.cos(startRad) * outerRadius.value
  const y1 = Math.sin(startRad) * outerRadius.value
  const x2 = Math.cos(endRad) * outerRadius.value
  const y2 = Math.sin(endRad) * outerRadius.value

  const largeArc = angle > 180 ? 1 : 0

  if (angle === 360) {
    return `M ${-innerRadius.value} 0 A ${innerRadius.value} ${innerRadius.value} 0 1 1 ${innerRadius.value} 0 A ${innerRadius.value} ${innerRadius.value} 0 1 1 ${-innerRadius.value} 0 Z`
  }

  return [
    `M ${x1} ${y1}`,
    `A ${outerRadius.value} ${outerRadius.value} 0 ${largeArc} 1 ${x2} ${y2}`,
    `L ${x2 * innerRadius.value / outerRadius.value} ${y2 * innerRadius.value / outerRadius.value}`,
    `A ${innerRadius.value} ${innerRadius.value} 0 ${largeArc} 0 ${x1 * innerRadius.value / outerRadius.value} ${y1 * innerRadius.value / outerRadius.value}`,
    'Z'
  ].join(' ')
}
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
}

.pie-chart {
  width: 100%;
}

.pie-slice {
  transition: all 0.3s ease;
  cursor: pointer;
}

.pie-slice:hover {
  filter: brightness(1.2);
}

.hover-circle {
  pointer-events: none;
}

.center-value {
  fill: #fff;
  font-size: 24px;
  font-weight: bold;
}

.legend-label {
  fill: #888;
  font-size: 12px;
}

.legend-value {
  fill: #fff;
  font-size: 12px;
  font-weight: 500;
}
</style>