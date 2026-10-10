<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 功能图标动画 -->
        <div class="icon-wrapper">
          <div class="pulse-ring"></div>
          <div class="pulse-ring delay-1"></div>
          <div class="pulse-ring delay-2"></div>
          <div class="feature-icon" :style="{ background: bgColor }">
            <img v-if="icon" :src="icon" :alt="title" class="icon-img" />
            <span v-else class="icon-emoji">{{ emoji }}</span>
          </div>
        </div>
        
        <!-- 加载文字 -->
        <div class="loading-text">
          <h2 class="loading-title">{{ title }}</h2>
          <p class="loading-subtitle">{{ subtitle || '正在加载中...' }}</p>
          <div class="loading-dots">
            <span class="dot"></span>
            <span class="dot"></span>
            <span class="dot"></span>
          </div>
        </div>
        
        <!-- 进度条 -->
        <div class="progress-container">
          <div class="progress-bar" :style="{ background: bgColor }"></div>
        </div>
      </div>
    </div>
  </transition>
</template>

<script setup>
import { defineProps } from 'vue'

const props = defineProps({
  show: {
    type: Boolean,
    default: true
  },
  icon: {
    type: String,
    default: ''
  },
  emoji: {
    type: String,
    default: '🏥'
  },
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: '正在加载中...'
  },
  bgColor: {
    type: String,
    default: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)'
  }
})
</script>

<style scoped lang="scss">
.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, 
    rgba(102, 126, 234, 0.95) 0%, 
    rgba(118, 75, 162, 0.95) 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  backdrop-filter: blur(10px);
  animation: fadeIn 0.3s ease-out;
}

.loading-content {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 2rem;
  padding: 2rem;
  max-width: 500px;
}

// 图标动画容器
.icon-wrapper {
  position: relative;
  width: 180px;
  height: 180px;
  display: flex;
  align-items: center;
  justify-content: center;
  
  .pulse-ring {
    position: absolute;
    width: 180px;
    height: 180px;
    border: 3px solid rgba(255, 255, 255, 0.6);
    border-radius: 50%;
    animation: pulseRing 2s ease-out infinite;
    
    &.delay-1 {
      animation-delay: 0.5s;
    }
    
    &.delay-2 {
      animation-delay: 1s;
    }
  }
  
  .feature-icon {
    position: relative;
    width: 120px;
    height: 120px;
    border-radius: 30px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: white;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
    animation: iconFloat 3s ease-in-out infinite;
    z-index: 1;
    
    .icon-img {
      width: 70px;
      height: 70px;
      object-fit: contain;
      filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.1));
    }
    
    .icon-emoji {
      font-size: 60px;
      line-height: 1;
    }
  }
}

// 加载文字
.loading-text {
  text-align: center;
  color: white;
  
  .loading-title {
    font-size: 2rem;
    font-weight: 700;
    margin-bottom: 0.5rem;
    text-shadow: 0 4px 12px rgba(0, 0, 0, 0.3);
    animation: titlePulse 2s ease-in-out infinite;
  }
  
  .loading-subtitle {
    font-size: 1.1rem;
    opacity: 0.95;
    margin-bottom: 1rem;
    font-weight: 400;
  }
  
  .loading-dots {
    display: flex;
    gap: 8px;
    justify-content: center;
    
    .dot {
      width: 10px;
      height: 10px;
      background: white;
      border-radius: 50%;
      animation: dotBounce 1.4s ease-in-out infinite;
      
      &:nth-child(1) {
        animation-delay: 0s;
      }
      
      &:nth-child(2) {
        animation-delay: 0.2s;
      }
      
      &:nth-child(3) {
        animation-delay: 0.4s;
      }
    }
  }
}

// 进度条
.progress-container {
  width: 300px;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  overflow: hidden;
  
  .progress-bar {
    height: 100%;
    background: white;
    border-radius: 2px;
    animation: progressMove 2s ease-in-out infinite;
  }
}

// 动画定义
@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

@keyframes pulseRing {
  0% {
    transform: scale(0.8);
    opacity: 1;
  }
  100% {
    transform: scale(1.5);
    opacity: 0;
  }
}

@keyframes iconFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(5deg);
  }
}

@keyframes titlePulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.05);
  }
}

@keyframes dotBounce {
  0%, 80%, 100% {
    transform: scale(0.8);
    opacity: 0.5;
  }
  40% {
    transform: scale(1.2);
    opacity: 1;
  }
}

@keyframes progressMove {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(400%);
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

// 响应式
@media (max-width: 768px) {
  .loading-content {
    padding: 1.5rem;
  }
  
  .icon-wrapper {
    width: 150px;
    height: 150px;
    
    .pulse-ring {
      width: 150px;
      height: 150px;
    }
    
    .feature-icon {
      width: 100px;
      height: 100px;
      border-radius: 25px;
      
      .icon-img {
        width: 60px;
        height: 60px;
      }
      
      .icon-emoji {
        font-size: 50px;
      }
    }
  }
  
  .loading-text {
    .loading-title {
      font-size: 1.5rem;
    }
    
    .loading-subtitle {
      font-size: 1rem;
    }
  }
  
  .progress-container {
    width: 250px;
  }
}
</style>
