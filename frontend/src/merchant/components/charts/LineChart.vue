<template>
  <div class="chart-wrapper" :style="{ height: height + 'px' }">
    <svg :width="width" :height="height" class="line-chart">
      <defs>
        <linearGradient :id="'gradient-' + uid" x1="0%" y1="0%" x2="0%" y2="100%">
          <stop offset="0%" stop-color="#00d4ff" stop-opacity="0.3" />
          <stop offset="100%" stop-color="#00d4ff" stop-opacity="0" />
        </linearGradient>
      </defs>

      <path
        :d="areaPath"
        :fill="`url(#gradient-${uid})`"
        class="area-path"
      />

      <path
        :d="linePath"
        fill="none"
        stroke="#00d4ff"
        stroke-width="2"
        class="line-path"
      />

      <circle
        v-for="(point, index) in points"
        :key="index"
        :cx="getX(index)"
        :cy="getY(point.value)"
        r="4"
        fill="#00d4ff"
        class="data-point"
      />

      <g v-if="labels.length > 0" class="x-labels">
        <text
          v-for="(label, index) in labels"
          :key="index"
          :x="getX(index)"
          :y="height - 5"
          text-anchor="middle"
          class="axis-label"
        >
          {{ label }}
        </text>
      </g>
    </svg>
  </div>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue'

interface DataPoint {
  label: string
  value: number
}

const props = withDefaults(defineProps<{
  data: DataPoint[]
  height?: number
  lineColor?: string
}>(), {
  height: 200,
  lineColor: '#00d4ff'
})

const uid = Math.random().toString(36).substr(2, 9)
const width = ref(600)

interface Point {
  x: number
  y: number
  value: number
  label: string
}

const points = computed<Point[]>(() => {
  if (props.data.length === 0) return []
  const w = width.value - 40
  const h = props.height - 40
  return props.data.map((d, i) => ({
    x: 20 + (i / (props.data.length - 1 || 1)) * w,
    y: h - (d.value / Math.max(...props.data.map(d => d.value), 1)) * h,
    value: d.value,
    label: d.label
  }))
})

const linePath = computed(() => {
  if (points.value.length === 0) return ''
  return points.value.map((p, i) => `${i === 0 ? 'M' : 'L'} ${p.x} ${p.y}`).join(' ')
})

const areaPath = computed(() => {
  if (points.value.length === 0) return ''
  const h = props.height - 20
  const lastX = points.value[points.value.length - 1].x
  const firstX = points.value[0].x
  return `${linePath.value} L ${lastX} ${h} L ${firstX} ${h} Z`
})

const labels = computed(() => props.data.map(d => d.label))

const getX = (index: number) => {
  if (props.data.length <= 1) return width.value / 2
  return 20 + (index / (props.data.length - 1)) * (width.value - 40)
}

const getY = (value: number) => {
  const maxVal = Math.max(...props.data.map(d => d.value), 1)
  return (props.height - 40) - (value / maxVal) * (props.height - 40)
}
</script>

<style scoped>
.chart-wrapper {
  width: 100%;
}

.line-chart {
  width: 100%;
}

.line-path {
  transition: all 0.3s ease;
}

.area-path {
  transition: all 0.3s ease;
}

.data-point {
  transition: all 0.3s ease;
}

.data-point:hover {
  r: 6;
  filter: drop-shadow(0 0 4px rgba(0, 212, 255, 0.8));
}

.axis-label {
  fill: #888;
  font-size: 11px;
}
</style>