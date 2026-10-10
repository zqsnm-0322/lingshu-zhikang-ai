<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 扫描检测动画 -->
        <div class="scan-container">
          <div class="body-outline">
            <img :src="icon" class="body-icon" />
            <div class="scan-line"></div>
          </div>
          <div class="scan-points">
            <div class="point point-1"></div>
            <div class="point point-2"></div>
            <div class="point point-3"></div>
            <div class="point point-4"></div>
          </div>
        </div>
        
        <h2 class="loading-title">症状自检</h2>
        <p class="loading-subtitle">AI智能分析症状提供建议...</p>
        <div class="analyzing-text">正在初始化智能诊断引擎...</div>
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
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.loading-content {
  text-align: center;
  color: white;
}

.scan-container {
  position: relative;
  width: 250px;
  height: 250px;
  margin: 0 auto 2rem;
  
  .body-outline {
    position: relative;
    width: 150px;
    height: 200px;
    margin: 0 auto;
    background: rgba(255, 255, 255, 0.1);
    border: 3px solid rgba(255, 255, 255, 0.3);
    border-radius: 50px;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    
    .body-icon {
      width: 80px;
      height: 80px;
      z-index: 2;
    }
    
    .scan-line {
      position: absolute;
      width: 100%;
      height: 3px;
      background: linear-gradient(90deg, transparent, #00ffff, transparent);
      box-shadow: 0 0 20px #00ffff;
      animation: scanMove 2s ease-in-out infinite;
    }
  }
  
  .scan-points {
    .point {
      position: absolute;
      width: 12px;
      height: 12px;
      background: #00ffff;
      border-radius: 50%;
      box-shadow: 0 0 15px #00ffff;
      animation: pointPulse 2s ease-in-out infinite;
      
      &.point-1 {
        top: 30px;
        left: 50px;
        animation-delay: 0s;
      }
      
      &.point-2 {
        top: 80px;
        right: 50px;
        animation-delay: 0.3s;
      }
      
      &.point-3 {
        bottom: 80px;
        left: 60px;
        animation-delay: 0.6s;
      }
      
      &.point-4 {
        bottom: 40px;
        right: 60px;
        animation-delay: 0.9s;
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
  margin-bottom: 0.5rem;
}

.analyzing-text {
  font-size: 0.95rem;
  opacity: 0.8;
  animation: textBlink 1.5s ease-in-out infinite;
}

@keyframes scanMove {
  0% {
    top: 0;
    opacity: 0;
  }
  50% {
    opacity: 1;
  }
  100% {
    top: 100%;
    opacity: 0;
  }
}

@keyframes pointPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.5);
    opacity: 1;
  }
}

@keyframes textBlink {
  0%, 100% { opacity: 0.8; }
  50% { opacity: 1; }
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
