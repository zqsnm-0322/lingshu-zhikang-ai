<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 时钟闹钟动画 -->
        <div class="clock-container">
          <div class="clock-face">
            <img :src="icon" class="clock-icon" />
            <div class="clock-hand hour-hand"></div>
            <div class="clock-hand minute-hand"></div>
            <div class="clock-center"></div>
          </div>
          <div class="bell-rings">
            <span class="bell">🔔</span>
            <div class="ring ring-1"></div>
            <div class="ring ring-2"></div>
          </div>
        </div>
        
        <h2 class="loading-title">用药提醒</h2>
        <p class="loading-subtitle">按时服药健康管理...</p>
        <div class="reminder-time">正在加载提醒事项...</div>
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

.clock-container {
  position: relative;
  width: 250px;
  height: 250px;
  margin: 0 auto 2rem;
  
  .clock-face {
    position: relative;
    width: 180px;
    height: 180px;
    margin: 0 auto;
    background: white;
    border-radius: 50%;
    border: 8px solid rgba(255, 255, 255, 0.5);
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
    display: flex;
    align-items: center;
    justify-content: center;
    
    .clock-icon {
      width: 100px;
      height: 100px;
      position: absolute;
    }
    
    .clock-hand {
      position: absolute;
      background: #333;
      transform-origin: bottom center;
      border-radius: 10px;
      
      &.hour-hand {
        width: 6px;
        height: 50px;
        bottom: 50%;
        left: 50%;
        margin-left: -3px;
        animation: hourRotate 8s linear infinite;
      }
      
      &.minute-hand {
        width: 4px;
        height: 70px;
        bottom: 50%;
        left: 50%;
        margin-left: -2px;
        animation: minuteRotate 4s linear infinite;
      }
    }
    
    .clock-center {
      position: absolute;
      width: 14px;
      height: 14px;
      background: #333;
      border-radius: 50%;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      z-index: 10;
    }
  }
  
  .bell-rings {
    position: absolute;
    top: -20px;
    right: 20px;
    
    .bell {
      font-size: 50px;
      display: inline-block;
      animation: bellRing 1s ease-in-out infinite;
    }
    
    .ring {
      position: absolute;
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 60px;
      height: 60px;
      border: 3px solid rgba(255, 255, 255, 0.6);
      border-radius: 50%;
      animation: ringExpand 1.5s ease-out infinite;
      
      &.ring-2 {
        animation-delay: 0.75s;
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

.reminder-time {
  font-size: 0.95rem;
  opacity: 0.8;
  animation: textFade 1.5s ease-in-out infinite;
}

@keyframes hourRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes minuteRotate {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes bellRing {
  0%, 100% {
    transform: rotate(0deg);
  }
  10%, 30%, 50%, 70%, 90% {
    transform: rotate(-15deg);
  }
  20%, 40%, 60%, 80% {
    transform: rotate(15deg);
  }
}

@keyframes ringExpand {
  0% {
    opacity: 1;
    transform: translate(-50%, -50%) scale(0.8);
  }
  100% {
    opacity: 0;
    transform: translate(-50%, -50%) scale(1.5);
  }
}

@keyframes textFade {
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
