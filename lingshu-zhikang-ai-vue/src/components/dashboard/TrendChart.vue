<template>
  <div class="trend-chart-container">
    <div class="chart-header">
      <div class="chart-tabs">
        <button 
          v-for="tab in tabs" 
          :key="tab.key"
          :class="['tab-btn', { active: activeTab === tab.key }]"
          @click="handleTabClick(tab.key)"
        >
          <span class="tab-icon">{{ tab.icon }}</span>
          <span class="tab-label">{{ tab.label }}</span>
        </button>
      </div>
      
      <div class="risk-legend">
        <span class="legend-item high">● 偏高</span>
        <span class="legend-item normal">● 正常</span>
        <span class="legend-item low">● 偏低</span>
      </div>
    </div>
    
    <div ref="chartRef" class="chart-canvas"></div>
  </div>
</template>

<script setup>
import { ref, watch, onMounted, onUnmounted, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  data: {
    type: Object,
    default: () => ({})
  },
  activeTab: {
    type: String,
    default: 'bloodPressure'
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const emit = defineEmits(['tab-change'])

const chartRef = ref(null)
let chartInstance = null
let resizeHandler = null

const tabs = [
  { key: 'bloodPressure', label: '血压趋势', icon: '' },
  { key: 'bloodSugar', label: '血糖趋势', icon: '' },
  { key: 'weight', label: '体重趋势', icon: '' },
  { key: 'heartRate', label: '心率趋势', icon: '' }
]

const handleTabClick = (key) => {
  emit('tab-change', key)
}

const initChart = () => {
  if (!chartRef.value) return
  
  // 如果已存在实例，先销毁
  if (chartInstance) {
    chartInstance.dispose()
    chartInstance = null
  }
  
  chartInstance = echarts.init(chartRef.value)
  
  // 如果有有效数据才渲染，否则显示loading或等待
  if (props.data?.dates?.length > 0 && !props.loading) {
    updateChart()
  } else if (props.loading) {
    chartInstance.showLoading({
      text: '加载中...',
      color: '#00FF88',
      textColor: '#fff',
      maskColor: 'rgba(0, 0, 0, 0.5)'
    })
  }
}

const updateChart = () => {
  console.log('TrendChart updateChart 被调用:', {
    hasInstance: !!chartInstance,
    loading: props.loading,
    hasDates: !!props.data?.dates,
    datesLength: props.data?.dates?.length,
    activeTab: props.activeTab,
    dataKeys: props.data ? Object.keys(props.data) : []
  })
  
  if (!chartInstance) {
    console.warn('图表实例不存在')
    return
  }
  
  if (props.loading) {
    console.log('数据加载中，跳过渲染')
    // 显示loading状态
    chartInstance.showLoading({
      text: '加载中...',
      color: '#00FF88',
      textColor: '#fff',
      maskColor: 'rgba(0, 0, 0, 0.5)'
    })
    return
  }
  
  // 检查数据是否存在且有效
  if (!props.data || !props.data.dates || props.data.dates.length === 0) {
    console.warn('没有有效的日期数据，跳过渲染')
    return
  }
  
  // 隐藏loading
  chartInstance.hideLoading()
  
  const isBloodPressure = props.activeTab === 'bloodPressure'
  console.log('🎨 开始渲染图表 🎨')
  console.log('props.activeTab =', props.activeTab)
  console.log('isBloodPressure =', isBloodPressure)
  console.log('数据详情:', { 
    dataPoints: props.data.dates.length,
    values1Length: props.data.values1?.length,
    values2Length: props.data.values2?.length,
    values1前3个: props.data.values1?.slice(0, 3),
    values2前3个: props.data.values2?.slice(0, 3)
  })
  console.log('将要设置的图例:', isBloodPressure ? ['收缩压', '舒张压'] : ['数值'])
  
  const option = {
    backgroundColor: 'transparent',
    tooltip: {
      trigger: 'axis',
      backgroundColor: 'rgba(0, 0, 0, 0.9)',
      borderColor: '#00D9FF',
      borderWidth: 1,
      textStyle: { 
        color: '#fff',
        fontSize: 14
      },
      axisPointer: {
        type: 'cross',
        crossStyle: {
          color: 'rgba(0, 217, 255, 0.5)'
        }
      }
    },
    legend: {
      data: isBloodPressure ? ['收缩压', '舒张压'] : ['数值'],
      textStyle: { 
        color: 'rgba(255, 255, 255, 0.8)',
        fontSize: 13
      },
      top: 10,
      right: 20
    },
    grid: {
      left: '3%',
      right: '4%',
      bottom: '5%',
      top: '15%',
      containLabel: true
    },
    xAxis: {
      type: 'category',
      boundaryGap: false,
      data: props.data.dates || [],
      axisLine: { 
        lineStyle: { 
          color: 'rgba(0, 217, 255, 0.3)',
          width: 2
        } 
      },
      axisLabel: { 
        color: 'rgba(255, 255, 255, 0.7)',
        interval: 4,
        fontSize: 12
      },
      splitLine: {
        show: true,
        lineStyle: {
          color: 'rgba(255, 255, 255, 0.05)',
          type: 'dashed'
        }
      }
    },
    yAxis: {
      type: 'value',
      axisLine: { 
        lineStyle: { 
          color: 'rgba(0, 217, 255, 0.3)',
          width: 2
        } 
      },
      splitLine: { 
        lineStyle: { 
          color: 'rgba(255, 255, 255, 0.05)',
          type: 'dashed'
        } 
      },
      axisLabel: { 
        color: 'rgba(255, 255, 255, 0.7)',
        fontSize: 12
      }
    },
    series: [
      {
        name: isBloodPressure ? '收缩压' : '数值',
        type: 'line',
        smooth: true,
        symbol: 'circle',
        symbolSize: 8,
        data: props.data.values1 || [],
        itemStyle: { 
          color: '#00D9FF',
          borderWidth: 2,
          borderColor: '#fff'
        },
        lineStyle: {
          width: 3,
          shadowBlur: 10,
          shadowColor: 'rgba(0, 217, 255, 0.5)'
        },
        areaStyle: {
          color: {
            type: 'linear',
            x: 0,
            y: 0,
            x2: 0,
            y2: 1,
            colorStops: [
              { offset: 0, color: 'rgba(0, 217, 255, 0.4)' },
              { offset: 1, color: 'rgba(0, 217, 255, 0.05)' }
            ]
          }
        }
      }
    ]
  }
  
  if (isBloodPressure && props.data.values2) {
    option.series.push({
      name: '舒张压',
      type: 'line',
      smooth: true,
      symbol: 'circle',
      symbolSize: 8,
      data: props.data.values2,
      itemStyle: { 
        color: '#00FF88',
        borderWidth: 2,
        borderColor: '#fff'
      },
      lineStyle: {
        width: 3,
        shadowBlur: 10,
        shadowColor: 'rgba(0, 255, 136, 0.5)'
      },
      areaStyle: {
        color: {
          type: 'linear',
          x: 0,
          y: 0,
          x2: 0,
          y2: 1,
          colorStops: [
            { offset: 0, color: 'rgba(0, 255, 136, 0.4)' },
            { offset: 1, color: 'rgba(0, 255, 136, 0.05)' }
          ]
        }
      }
    })
  }
  
  chartInstance.setOption(option)
}

// 监听数据变化
watch(
  () => props.data,
  (newData, oldData) => {
    console.log('========== TrendChart data 变化 ==========')
    console.log('触发时间:', new Date().toLocaleTimeString())
    console.log('🔍 newData:', {
      是否存在: !!newData,
      类型: typeof newData,
      keys: newData ? Object.keys(newData) : [],
      dates长度: newData?.dates?.length,
      values1长度: newData?.values1?.length,
      values2长度: newData?.values2?.length,
      前3个dates: newData?.dates?.slice(0, 3),
      前3个values1: newData?.values1?.slice(0, 3)
    })
    console.log('🔍 oldData:', {
      dates长度: oldData?.dates?.length,
      values1长度: oldData?.values1?.length
    })
    
    if (!chartInstance) {
      console.log('⚠️ 图表实例不存在')
      return
    }
    
    if (props.loading) {
      console.log('⏳ 正在loading，跳过')
      return
    }
    
    if (!newData?.dates?.length || !newData?.values1?.length) {
      console.log('⚠️ 数据无效，无法渲染')
      return
    }
    
    console.log('✅ 数据有效，更新图表')
    updateChart()
    console.log('========================================')
  },
  { deep: true, immediate: true }
)

// 监听 activeTab 变化
watch(
  () => props.activeTab,
  (newTab, oldTab) => {
    console.log('========== activeTab 变化:', oldTab, '→', newTab, '==========')
    
    if (!chartInstance || props.loading || !props.data?.dates?.length) {
      console.log('条件不满足，跳过渲染')
      return
    }
    
    console.log('✅ 更新图表')
    updateChart()
  }
)

// 监听 loading 变化
watch(
  () => props.loading,
  (newLoading, oldLoading) => {
    console.log('========== loading 变化:', oldLoading, '→', newLoading, '==========')
    
    if (!chartInstance) {
      console.log('⚠️ 图表实例不存在')
      return
    }
    
    if (newLoading) {
      console.log('🔄 显示加载动画')
      chartInstance.showLoading({
        text: '加载中...',
        color: '#00FF88',
        textColor: '#fff',
        maskColor: 'rgba(0, 0, 0, 0.5)'
      })
    } else {
      console.log('✅ 取消loading')
      chartInstance.hideLoading()
      
      // loading结束后，如果有有效数据就渲染
      if (props.data?.dates?.length && props.data?.values1?.length) {
        console.log('✅ 数据有效，开始渲染')
        updateChart()
      } else {
        console.log('⚠️ 数据无效，无法渲染')
      }
    }
  }
)

onMounted(() => {
  console.log('TrendChart 组件挂载', {
    初始数据: props.data,
    hasDates: !!props.data?.dates,
    datesLength: props.data?.dates?.length
  })
  
  nextTick(() => {
    console.log('🔧 开始初始化图表...')
    initChart()
    console.log('图表初始化完成，实例:', !!chartInstance)
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
.trend-chart-container {
  background: rgba(10, 14, 39, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 255, 136, 0.2);
  border-radius: 16px;
  padding: 24px;
  flex: 1;
  display: flex;
  flex-direction: column;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  transition: all 0.3s ease;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(0, 255, 136, 0.5), transparent);
  }
  
  &:hover {
    border-color: rgba(0, 255, 136, 0.4);
    box-shadow: 0 8px 40px rgba(0, 255, 136, 0.2);
  }
  
  .chart-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 24px;
    gap: 20px;
    flex-wrap: wrap;
    
    .chart-tabs {
      display: flex;
      gap: 12px;
      flex-wrap: wrap;
      
      .tab-btn {
        display: flex;
        align-items: center;
        gap: 8px;
        padding: 12px 24px;
        background: rgba(0, 0, 0, 0.3);
        border: 1px solid rgba(0, 255, 136, 0.2);
        border-radius: 24px;
        color: rgba(255, 255, 255, 0.7);
        font-size: 0.95rem;
        cursor: pointer;
        transition: all 0.3s ease;
        position: relative;
        overflow: hidden;
        
        &::before {
          content: '';
          position: absolute;
          top: 50%;
          left: 50%;
          width: 0;
          height: 0;
          border-radius: 50%;
          background: rgba(0, 255, 136, 0.2);
          transform: translate(-50%, -50%);
          transition: width 0.6s, height 0.6s;
        }
        
        &:hover {
          border-color: rgba(0, 255, 136, 0.5);
          color: #fff;
          background: rgba(0, 255, 136, 0.1);
          transform: translateY(-2px);
          
          &::before {
            width: 300px;
            height: 300px;
          }
        }
        
        &.active {
          background: rgba(0, 255, 136, 0.2);
          border-color: #00FF88;
          color: #00FF88;
          box-shadow: 0 0 20px rgba(0, 255, 136, 0.3);
          font-weight: 600;
          
          .tab-icon {
            filter: drop-shadow(0 0 8px #00FF88);
          }
        }
        
        .tab-icon {
          font-size: 1.2rem;
          transition: all 0.3s ease;
        }
        
        .tab-label {
          position: relative;
          z-index: 1;
        }
      }
    }
    
    .risk-legend {
      display: flex;
      gap: 20px;
      align-items: center;
      
      .legend-item {
        font-size: 0.9rem;
        font-weight: 600;
        display: flex;
        align-items: center;
        gap: 8px;
        
        &.high {
          color: #FF6B6B;
        }
        
        &.normal {
          color: #00FF88;
        }
        
        &.low {
          color: #FEC957;
        }
      }
    }
  }
  
  .chart-canvas {
    width: 100%;
    flex: 1;
    min-height: 450px;
  }
}

@media (max-width: 1200px) {
  .trend-chart-container {
    .chart-canvas {
      min-height: 350px;
    }
  }
}

@media (max-width: 768px) {
  .trend-chart-container {
    padding: 16px;
    
    .chart-header {
      flex-direction: column;
      align-items: flex-start;
      gap: 16px;
      
      .chart-tabs {
        width: 100%;
        
        .tab-btn {
          flex: 1;
          min-width: 0;
          padding: 10px 16px;
          font-size: 0.85rem;
          justify-content: center;
        }
      }
      
      .risk-legend {
        width: 100%;
        justify-content: space-around;
        
        .legend-item {
          font-size: 0.85rem;
        }
      }
    }
    
    .chart-canvas {
      min-height: 280px;
    }
  }
}
</style>

