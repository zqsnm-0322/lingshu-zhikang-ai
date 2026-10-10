<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 文档翻页动画 -->
        <div class="document-stack">
          <div class="page page-1">
            <img :src="icon" class="page-icon" />
          </div>
          <div class="page page-2"></div>
          <div class="page page-3"></div>
        </div>
        
        <h2 class="loading-title">ChatPDF</h2>
        <p class="loading-subtitle">解析你的病例，正在加载文档解析引擎...</p>
        <div class="progress-bar">
          <div class="progress-fill"></div>
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
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.loading-content {
  text-align: center;
  color: white;
}

.document-stack {
  position: relative;
  width: 200px;
  height: 250px;
  margin: 0 auto 2rem;
  
  .page {
    position: absolute;
    width: 180px;
    height: 230px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
    
    &.page-1 {
      z-index: 3;
      animation: pageFlip 3s ease-in-out infinite;
      display: flex;
      align-items: center;
      justify-content: center;
      
      .page-icon {
        width: 100px;
        height: 100px;
      }
    }
    
    &.page-2 {
      z-index: 2;
      transform: translateY(10px) translateX(5px);
      opacity: 0.8;
    }
    
    &.page-3 {
      z-index: 1;
      transform: translateY(20px) translateX(10px);
      opacity: 0.6;
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

.progress-bar {
  width: 300px;
  height: 4px;
  background: rgba(255, 255, 255, 0.3);
  border-radius: 2px;
  overflow: hidden;
  margin: 0 auto;
  
  .progress-fill {
    height: 100%;
    background: white;
    animation: progressMove 2s ease-in-out infinite;
  }
}

@keyframes pageFlip {
  0%, 100% {
    transform: rotateY(0deg);
  }
  50% {
    transform: rotateY(-180deg);
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
</style>
