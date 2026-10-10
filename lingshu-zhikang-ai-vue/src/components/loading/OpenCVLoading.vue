<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 相机图像处理动画 -->
        <div class="camera-scene">
          <div class="camera-frame">
            <img :src="icon" class="camera-icon" />
            <div class="scan-grid">
              <div class="grid-line h-line" v-for="i in 3" :key="`h-${i}`"></div>
              <div class="grid-line v-line" v-for="i in 3" :key="`v-${i}`"></div>
            </div>
            <div class="focus-box"></div>
          </div>
          <div class="processing-effect">
            <div class="pixel pixel-1"></div>
            <div class="pixel pixel-2"></div>
            <div class="pixel pixel-3"></div>
            <div class="pixel pixel-4"></div>
          </div>
        </div>
        
        <h2 class="loading-title">OpenCV AI问诊模拟器</h2>
        <p class="loading-subtitle">一个可以询问病情的在线聊天...</p>
        <div class="processing-status">
          <span class="status-dot"></span>
          <span class="status-text">正在初始化图像处理引擎...</span>
        </div>
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
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.loading-content {
  text-align: center;
  color: white;
}

.camera-scene {
  position: relative;
  width: 300px;
  height: 300px;
  margin: 0 auto 2rem;
  
  .camera-frame {
    position: relative;
    width: 250px;
    height: 250px;
    margin: 0 auto;
    background: rgba(255, 255, 255, 0.15);
    border: 4px solid rgba(255, 255, 255, 0.4);
    border-radius: 20px;
    display: flex;
    align-items: center;
    justify-content: center;
    backdrop-filter: blur(10px);
    overflow: hidden;
    
    .camera-icon {
      width: 120px;
      height: 120px;
      z-index: 10;
      animation: iconPulse 2s ease-in-out infinite;
    }
    
    .scan-grid {
      position: absolute;
      width: 100%;
      height: 100%;
      
      .grid-line {
        position: absolute;
        background: rgba(255, 255, 255, 0.2);
        
        &.h-line {
          width: 100%;
          height: 1px;
          left: 0;
          animation: gridScan 2s linear infinite;
          
          &:nth-child(1) { top: 33%; animation-delay: 0s; }
          &:nth-child(2) { top: 50%; animation-delay: 0.3s; }
          &:nth-child(3) { top: 66%; animation-delay: 0.6s; }
        }
        
        &.v-line {
          height: 100%;
          width: 1px;
          top: 0;
          animation: gridScan 2s linear infinite;
          
          &:nth-child(4) { left: 33%; animation-delay: 0.2s; }
          &:nth-child(5) { left: 50%; animation-delay: 0.5s; }
          &:nth-child(6) { left: 66%; animation-delay: 0.8s; }
        }
      }
    }
    
    .focus-box {
      position: absolute;
      width: 150px;
      height: 150px;
      border: 3px solid rgba(255, 255, 255, 0.6);
      border-radius: 10px;
      animation: focusZoom 2s ease-in-out infinite;
      
      &::before,
      &::after {
        content: '';
        position: absolute;
        width: 20px;
        height: 20px;
        border: 3px solid white;
      }
      
      &::before {
        top: -3px;
        left: -3px;
        border-right: none;
        border-bottom: none;
      }
      
      &::after {
        bottom: -3px;
        right: -3px;
        border-left: none;
        border-top: none;
      }
    }
  }
  
  .processing-effect {
    .pixel {
      position: absolute;
      width: 30px;
      height: 30px;
      background: rgba(255, 255, 255, 0.3);
      border-radius: 5px;
      animation: pixelFloat 3s ease-in-out infinite;
      
      &.pixel-1 {
        top: 20px;
        left: 20px;
        animation-delay: 0s;
      }
      
      &.pixel-2 {
        top: 30px;
        right: 20px;
        animation-delay: 0.5s;
      }
      
      &.pixel-3 {
        bottom: 30px;
        left: 30px;
        animation-delay: 1s;
      }
      
      &.pixel-4 {
        bottom: 20px;
        right: 30px;
        animation-delay: 1.5s;
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
  margin-bottom: 1rem;
}

.processing-status {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 0.5rem;
  
  .status-dot {
    width: 10px;
    height: 10px;
    background: white;
    border-radius: 50%;
    animation: dotPulse 1.5s ease-in-out infinite;
  }
  
  .status-text {
    font-size: 0.95rem;
    opacity: 0.9;
  }
}

@keyframes iconPulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

@keyframes gridScan {
  0%, 100% {
    opacity: 0.2;
  }
  50% {
    opacity: 0.8;
  }
}

@keyframes focusZoom {
  0%, 100% {
    transform: scale(1);
    opacity: 0.6;
  }
  50% {
    transform: scale(1.2);
    opacity: 1;
  }
}

@keyframes pixelFloat {
  0%, 100% {
    transform: translate(0, 0);
    opacity: 0.3;
  }
  50% {
    transform: translate(-10px, -10px);
    opacity: 0.8;
  }
}

@keyframes dotPulse {
  0%, 100% {
    transform: scale(1);
    opacity: 1;
  }
  50% {
    transform: scale(1.5);
    opacity: 0.5;
  }
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
