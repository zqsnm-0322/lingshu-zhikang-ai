<template>
  <div class="symptom-distribution">
    <div class="card-header">
      <span class="header-icon"></span>
      <span class="header-title">症状分类统计</span>
    </div>
    
    <div ref="chartRef" class="chart-container"></div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const chartRef = ref(null)
let chartInstance = null
let resizeHandler = null

const initChart = () => {
  if (!chartRef.value) return
  
  // 如果已存在实例，先销毁
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  
  chartInstance = echarts.init(chartRef.value)
  updateChart()
}

const updateChart = () => {
  if (!chartInstance || props.loading) return
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'item',
      backgroundColor: 'rgba(0, 0, 0, 0.9)',
      borderColor: '#00D9FF',
      borderWidth: 1,
      textStyle: { 
        color: '#fff',
        fontSize: 14
      },
      formatter: '{b}: {c} ({d}%)'
    },
    legend: {
      top: '5%',
      left: 'center',
      textStyle: { 
        color: 'rgba(255, 255, 255, 0.8)',
        fontSize: 12
      },
      itemGap: 15,
      itemWidth: 12,
      itemHeight: 12
    },
    series: [
      {
        name: '症状分类',
        type: 'pie',
        radius: ['45%', '75%'],
        center: ['50%', '60%'],
        avoidLabelOverlap: false,
        itemStyle: {
          borderRadius: 8,
          borderColor: '#0a0e27',
          borderWidth: 3
        },
        label: {
          show: false
        },
        emphasis: {
          label: {
            show: true,
            fontSize: 16,
            fontWeight: 'bold',
            color: '#fff'
          },
          itemStyle: {
            shadowBlur: 20,
            shadowColor: 'rgba(0, 217, 255, 0.5)'
          }
        },
        labelLine: {
          show: false
        },
        data: props.data.map(item => ({
          value: item.value,
          name: item.name,
          itemStyle: { 
            color: item.color,
            shadowBlur: 10,
            shadowColor: item.color
          }
        }))
      }
    ]
  }
  
  chartInstance.setOption(option)
}

watch(() => props.data, () => {
  if (chartInstance) {
    updateChart()
  }
}, { deep: true })

watch(() => props.loading, (newVal) => {
  if (!newVal && chartInstance) {
    updateChart()
  }
})

onMounted(() => {
  nextTick(() => {
    initChart()
  })
  
  // 创建resize处理器
  resizeHandler = () => {
    if (chartInstance && !chartInstance.isDisposed()) {
      chartInstance.resize()
    }
  }
  
  window.addEventListener('resize', resizeHandler)
})

onUnmounted(() => {
  // 移除resize监听器
  if (resizeHandler) {
    window.removeEventListener('resize', resizeHandler)
    resizeHandler = null
  }
  
  // 销毁图表实例
  if (chartInstance && !chartInstance.isDisposed()) {
    chartInstance.dispose()
    chartInstance = null
  }
})
</script>

<style scoped lang="scss">
.symptom-distribution {
  background: rgba(10, 14, 39, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 217, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  transition: all 0.3s ease;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(0, 217, 255, 0.5), transparent);
  }
  
  &:hover {
    border-color: rgba(0, 217, 255, 0.4);
    box-shadow: 0 8px 40px rgba(0, 217, 255, 0.2);
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 20px;
    padding-bottom: 16px;
    border-bottom: 1px solid rgba(0, 217, 255, 0.2);
    
    .header-icon {
      font-size: 1.5rem;
      filter: drop-shadow(0 0 10px rgba(0, 217, 255, 0.6));
    }
    
    .header-title {
      font-size: 1.2rem;
      font-weight: 700;
      color: #00D9FF;
      letter-spacing: 1px;
    }
  }
  
  .chart-container {
    width: 100%;
    height: 420px;
  }
}

@media (max-width: 768px) {
  .symptom-distribution {
    .chart-container {
      height: 320px;
    }
  }
}
</style>

