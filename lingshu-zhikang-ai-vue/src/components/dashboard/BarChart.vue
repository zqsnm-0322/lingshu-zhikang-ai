<template>
  <div class="chart-container">
    <div class="chart-header">
      <h3 class="chart-title">{{ title }}</h3>
    </div>
    <div class="chart-content">
      <div class="chart-bars">
        <div 
          v-for="(item, index) in data" 
          :key="index"
          class="bar-item"
        >
          <div class="bar-wrapper">
            <div 
              class="bar" 
              :style="{ height: `${(item.value / maxValue) * 100}%` }"
            ></div>
          </div>
          <div class="bar-label">{{ item.label }}</div>
        </div>
      </div>
      <div class="chart-y-axis">
        <div v-for="tick in yAxisTicks" :key="tick" class="y-tick">
          {{ tick }}
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    default: '数据统计'
  },
  data: {
    type: Array,
    required: true
  }
})

const maxValue = computed(() => {
  return Math.max(...props.data.map(item => item.value))
})

const yAxisTicks = computed(() => {
  const max = maxValue.value
  const step = Math.ceil(max / 5)
  return Array.from({ length: 6 }, (_, i) => i * step).reverse()
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
  margin-bottom: 20px;
}

.chart-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.chart-content {
  flex: 1;
  display: flex;
  position: relative;
}

.chart-y-axis {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
  width: 40px;
}

.y-tick {
  font-size: 12px;
  color: #6b7280;
  text-align: right;
  padding-right: 8px;
}

.chart-bars {
  flex: 1;
  display: flex;
  align-items: end;
  gap: 12px;
  margin-left: 50px;
  height: 200px;
  padding-bottom: 30px;
}

.bar-item {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  height: 100%;
}

.bar-wrapper {
  flex: 1;
  width: 100%;
  display: flex;
  align-items: end;
  justify-content: center;
}

.bar {
  width: 80%;
  background: linear-gradient(180deg, #3b82f6 0%, #1d4ed8 100%);
  border-radius: 4px 4px 0 0;
  min-height: 4px;
  transition: all 0.3s ease;
  
  &:hover {
    background: linear-gradient(180deg, #2563eb 0%, #1e40af 100%);
  }
}

.bar-label {
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
  text-align: center;
}
</style>
