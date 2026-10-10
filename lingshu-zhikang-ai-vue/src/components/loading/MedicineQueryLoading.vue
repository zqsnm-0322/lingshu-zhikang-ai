<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 药丸旋转动画 -->
        <div class="pill-container">
          <div class="pill pill-1">
            <img :src="icon" class="pill-icon" />
          </div>
          <div class="pill pill-2"></div>
          <div class="pill pill-3"></div>
        </div>
        
        <h2 class="loading-title"><img src="https://api.iconify.design/mdi:pill.svg?color=%23ffffff" class="title-icon" alt="药品" /> 药品查询</h2>
        <p class="loading-subtitle">正在初始化药品数据库...</p>
        <div class="loading-spinner"></div>
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

.pill-container {
  position: relative;
  width: 200px;
  height: 200px;
  margin: 0 auto 2rem;
  
  .pill {
    position: absolute;
    width: 80px;
    height: 40px;
    border-radius: 20px;
    background: rgba(255, 255, 255, 0.3);
    backdrop-filter: blur(10px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
    
    &.pill-1 {
      top: 50%;
      left: 50%;
      transform: translate(-50%, -50%);
      width: 100px;
      height: 100px;
      border-radius: 50%;
      background: white;
      display: flex;
      align-items: center;
      justify-content: center;
      z-index: 3;
      animation: pillRotate 3s linear infinite;
      
      .pill-icon {
        width: 70px;
        height: 70px;
      }
    }
    
    &.pill-2 {
      top: 20%;
      left: 20%;
      animation: pillFloat 2s ease-in-out infinite;
      animation-delay: 0.3s;
    }
    
    &.pill-3 {
      bottom: 20%;
      right: 20%;
      animation: pillFloat 2s ease-in-out infinite;
      animation-delay: 0.6s;
    }
  }
}

.loading-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  
  .title-icon {
    width: 32px;
    height: 32px;
  }
}

.loading-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 1.5rem;
}

.loading-spinner {
  width: 50px;
  height: 50px;
  margin: 0 auto;
  border: 4px solid rgba(255, 255, 255, 0.3);
  border-top-color: white;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes pillRotate {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}

@keyframes pillFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
  }
  50% {
    transform: translateY(-20px) rotate(180deg);
  }
}

@keyframes spin {
  to { transform: rotate(360deg); }
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
