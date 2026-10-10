<template>
  <div class="chart-container">
    <div class="chart-header">
      <h3 class="chart-title">{{ title }}</h3>
      <div class="chart-subtitle">
        <span class="growth-text">今日增长: {{ growthText }}</span>
      </div>
    </div>
    <div class="chart-content">
      <svg class="line-chart" :viewBox="`0 0 ${width} ${height}`">
        <!-- 网格线 -->
        <defs>
          <pattern id="grid" width="40" height="30" patternUnits="userSpaceOnUse">
            <path d="M 40 0 L 0 0 0 30" fill="none" stroke="#f3f4f6" stroke-width="1"/>
          </pattern>
        </defs>
        <rect width="100%" height="100%" fill="url(#grid)" />
        
        <!-- 折线 -->
        <path
          :d="linePath"
          fill="none"
          stroke="url(#lineGradient)"
          stroke-width="3"
          stroke-linecap="round"
          stroke-linejoin="round"
        />
        
        <!-- 填充区域 -->
        <path
          :d="areaPath"
          fill="url(#areaGradient)"
          opacity="0.3"
        />
        
        <!-- 数据点 -->
        <circle
          v-for="(point, index) in points"
          :key="index"
          :cx="point.x"
          :cy="point.y"
          r="4"
          fill="white"
          stroke="#3b82f6"
          stroke-width="2"
        />
        
        <!-- 渐变定义 -->
        <defs>
          <linearGradient id="lineGradient" x1="0%" y1="0%" x2="100%" y2="0%">
            <stop offset="0%" style="stop-color:#3b82f6"/>
            <stop offset="100%" style="stop-color:#1d4ed8"/>
          </linearGradient>
          <linearGradient id="areaGradient" x1="0%" y1="0%" x2="0%" y2="100%">
            <stop offset="0%" style="stop-color:#3b82f6;stop-opacity:0.3"/>
            <stop offset="100%" style="stop-color:#3b82f6;stop-opacity:0"/>
          </linearGradient>
        </defs>
      </svg>
      
      <!-- X轴标签 -->
      <div class="x-axis-labels">
        <span v-for="label in xLabels" :key="label" class="x-label">{{ label }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '访问量'
  },
  data: {
    type: Array,
    required: true
  },
  growth: {
    type: Number,
    default: 0
  }
})

const width = 400
const height = 200
const padding = 20

const maxValue = computed(() => Math.max(...props.data))
const minValue = computed(() => Math.min(...props.data))

const points = computed(() => {
  const stepX = (width - 2 * padding) / (props.data.length - 1)
  return props.data.map((value, index) => ({
    x: padding + index * stepX,
    y: height - padding - ((value - minValue.value) / (maxValue.value - minValue.value)) * (height - 2 * padding)
  }))
})

const linePath = computed(() => {
  if (points.value.length === 0) return ''
  const pathData = points.value.map((point, index) => 
    `${index === 0 ? 'M' : 'L'} ${point.x} ${point.y}`
  ).join(' ')
  return pathData
})

const areaPath = computed(() => {
  if (points.value.length === 0) return ''
  const pathData = [
    `M ${points.value[0].x} ${height - padding}`,
    ...points.value.map(point => `L ${point.x} ${point.y}`),
    `L ${points.value[points.value.length - 1].x} ${height - padding}`,
    'Z'
  ].join(' ')
  return pathData
})

const xLabels = computed(() => {
  return ['1月', '2月', '3月', '4月', '5月', '6月', '7月', '8月', '9月', '10月', '11月', '12月']
})

const growthText = computed(() => {
  const sign = props.growth > 0 ? '+' : ''
  return `${sign}${props.growth}%`
})
</script>

<style scoped lang="scss">
.chart-container {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  height: 300px;
  display: flex;
  flex-direction: column;
}

.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.chart-subtitle {
  font-size: 12px;
}

.growth-text {
  color: #10b981;
  font-weight: 500;
}

.chart-content {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.line-chart {
  flex: 1;
  width: 100%;
  height: 200px;
}

.x-axis-labels {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  padding: 0 20px;
}

.x-label {
  font-size: 12px;
  color: #6b7280;
}
</style>
