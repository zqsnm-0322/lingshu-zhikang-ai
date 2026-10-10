<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 数据图表动画 -->
        <div class="chart-container">
          <div class="report-icon-wrapper">
            <img :src="icon" class="report-icon" />
          </div>
          <div class="chart-bars">
            <div class="bar bar-1"></div>
            <div class="bar bar-2"></div>
            <div class="bar bar-3"></div>
            <div class="bar bar-4"></div>
            <div class="bar bar-5"></div>
          </div>
          <div class="data-stream">
            <div class="data-line" v-for="i in 3" :key="i"></div>
          </div>
        </div>
        
        <h2 class="loading-title">体检报告储存库</h2>
        <p class="loading-subtitle">查看和管理体检报告...</p>
        <div class="analyzing">正在分析健康数据...</div>
        <div class="progress-percentage">68%</div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  show: { type: Boolean, default: true },
  icon: { type: String, required: true }
})
</script>

<style scoped lang="scss">
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: #667eea;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.loading-content {
  text-align: center;
  color: white;
}

.chart-container {
  position: relative;
  width: 320px;
  height: 250px;
  margin: 0 auto 2rem;
  background: rgba(255, 255, 255, 0.15);
  border-radius: 20px;
  padding: 20px;
  backdrop-filter: blur(10px);
  
  .report-icon-wrapper {
    position: absolute;
    top: -40px;
    left: 50%;
    transform: translateX(-50%);
    width: 80px;
    height: 80px;
    background: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    
    .report-icon {
      width: 50px;
      height: 50px;
    }
  }
  
  .chart-bars {
    display: flex;
    align-items: flex-end;
    justify-content: space-around;
    height: 120px;
    margin-top: 50px;
    
    .bar {
      width: 40px;
      background: linear-gradient(180deg, rgba(255, 255, 255, 0.8), rgba(255, 255, 255, 0.4));
      border-radius: 5px 5px 0 0;
      animation: barGrow 2s ease-in-out infinite;
      
      &.bar-1 {
        height: 40%;
        animation-delay: 0s;
      }
      
      &.bar-2 {
        height: 70%;
        animation-delay: 0.2s;
      }
      
      &.bar-3 {
        height: 50%;
        animation-delay: 0.4s;
      }
      
      &.bar-4 {
        height: 85%;
        animation-delay: 0.6s;
      }
      
      &.bar-5 {
        height: 60%;
        animation-delay: 0.8s;
      }
    }
  }
  
  .data-stream {
    position: absolute;
    bottom: 20px;
    left: 20px;
    right: 20px;
    
    .data-line {
      height: 2px;
      background: rgba(255, 255, 255, 0.3);
      margin-bottom: 8px;
      border-radius: 2px;
      animation: dataFlow 2s ease-in-out infinite;
      
      &:nth-child(1) { animation-delay: 0s; }
      &:nth-child(2) { animation-delay: 0.3s; }
      &:nth-child(3) { animation-delay: 0.6s; }
    }
  }
}

.loading-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
}

.loading-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 0.5rem;
}

.analyzing {
  font-size: 0.95rem;
  opacity: 0.8;
  margin-bottom: 1rem;
}

.progress-percentage {
  font-size: 2rem;
  font-weight: 700;
  animation: percentageChange 3s ease-in-out infinite;
}

@keyframes barGrow {
  0%, 100% {
    transform: scaleY(0.3);
    opacity: 0.5;
  }
  50% {
    transform: scaleY(1);
    opacity: 1;
  }
}

@keyframes dataFlow {
  0% {
    width: 0;
    opacity: 0;
  }
  50%, 100% {
    width: 100%;
    opacity: 1;
  }
}

@keyframes percentageChange {
  0% { content: "0%"; }
  100% { content: "100%"; }
}

.fade-out-enter-active,
.fade-out-leave-active {
  transition: opacity 0.5s ease;
}

.fade-out-enter-from,
.fade-out-leave-to {
  opacity: 0;
}
</style>
