<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 心跳监测动画 -->
        <div class="heart-monitor">
          <div class="heart-icon">💓</div>
          <div class="ecg-container">
            <svg viewBox="0 0 300 100" class="ecg-svg">
              <polyline 
                points="0,50 50,50 60,20 70,80 80,50 130,50 140,20 150,80 160,50 300,50" 
                class="ecg-line"
              />
            </svg>
          </div>
        </div>
        
        <h2 class="loading-title">💓 慢性病管理</h2>
        <p class="loading-subtitle">记录管理您的健康指标...</p>
        <div class="health-stats">
          <div class="stat">
            <span class="stat-label">心率</span>
            <span class="stat-value">72</span>
          </div>
          <div class="stat">
            <span class="stat-label">血压</span>
            <span class="stat-value">120</span>
          </div>
          <div class="stat">
            <span class="stat-label">血糖</span>
            <span class="stat-value">5.5</span>
          </div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { defineProps } from 'vue'

defineProps({
  show: { type: Boolean, default: true }
})
</script>

<style scoped lang="scss">
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.loading-content {
  text-align: center;
  color: white;
}

.heart-monitor {
  width: 350px;
  margin: 0 auto 2rem;
  
  .heart-icon {
    font-size: 80px;
    animation: heartBeat 1.5s ease-in-out infinite;
    margin-bottom: 1rem;
  }
  
  .ecg-container {
    width: 100%;
    height: 100px;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 10px;
    padding: 10px;
    backdrop-filter: blur(10px);
    
    .ecg-svg {
      width: 100%;
      height: 100%;
      
      .ecg-line {
        fill: none;
        stroke: white;
        stroke-width: 3;
        stroke-linecap: round;
        stroke-linejoin: round;
        animation: ecgMove 2s linear infinite;
      }
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
  margin-bottom: 1.5rem;
}

.health-stats {
  display: flex;
  justify-content: center;
  gap: 2rem;
  
  .stat {
    display: flex;
    flex-direction: column;
    align-items: center;
    background: rgba(255, 255, 255, 0.2);
    padding: 1rem 1.5rem;
    border-radius: 10px;
    backdrop-filter: blur(10px);
    
    .stat-label {
      font-size: 0.9rem;
      opacity: 0.9;
      margin-bottom: 0.5rem;
    }
    
    .stat-value {
      font-size: 1.5rem;
      font-weight: 700;
      animation: valueChange 2s ease-in-out infinite;
    }
  }
}

@keyframes heartBeat {
  0%, 100% {
    transform: scale(1);
  }
  10%, 30% {
    transform: scale(1.2);
  }
  20%, 40% {
    transform: scale(1);
  }
}

@keyframes ecgMove {
  0% {
    stroke-dasharray: 0 1000;
    stroke-dashoffset: 0;
  }
  100% {
    stroke-dasharray: 1000 0;
    stroke-dashoffset: -1000;
  }
}

@keyframes valueChange {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.6; }
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
