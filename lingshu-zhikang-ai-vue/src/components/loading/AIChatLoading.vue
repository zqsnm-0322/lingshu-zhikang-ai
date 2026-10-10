<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 对话气泡动画 -->
        <div class="chat-bubbles">
          <div class="bubble bubble-left">
            <img :src="icon" class="bubble-icon" />
          </div>
          <div class="bubble bubble-right"></div>
          <div class="bubble bubble-left small"></div>
        </div>
        
        <h2 class="loading-title">AI 聊天</h2>
        <p class="loading-subtitle">多模态对话机器人正在启动...</p>
        <div class="loading-dots">
          <span class="dot"></span>
          <span class="dot"></span>
          <span class="dot"></span>
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
  animation: fadeIn 0.3s ease-out;
}

.loading-content {
  text-align: center;
  color: white;
}

.chat-bubbles {
  position: relative;
  width: 300px;
  height: 200px;
  margin: 0 auto 2rem;
  
  .bubble {
    position: absolute;
    background: rgba(255, 255, 255, 0.2);
    border-radius: 20px;
    backdrop-filter: blur(10px);
    animation: bubbleFloat 2s ease-in-out infinite;
    
    &.bubble-left {
      left: 0;
      top: 20px;
      width: 120px;
      height: 80px;
      display: flex;
      align-items: center;
      justify-content: center;
      animation-delay: 0s;
      
      .bubble-icon {
        width: 60px;
        height: 60px;
        filter: drop-shadow(0 4px 8px rgba(0, 0, 0, 0.3));
      }
      
      &.small {
        width: 80px;
        height: 60px;
        top: 120px;
        left: 20px;
        animation-delay: 0.8s;
      }
    }
    
    &.bubble-right {
      right: 0;
      top: 80px;
      width: 100px;
      height: 70px;
      animation-delay: 0.4s;
    }
  }
}

.loading-title {
  font-size: 2rem;
  font-weight: 700;
  margin-bottom: 0.5rem;
  animation: titlePulse 2s ease-in-out infinite;
}

.loading-subtitle {
  font-size: 1.1rem;
  opacity: 0.9;
  margin-bottom: 1.5rem;
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
    
    &:nth-child(1) { animation-delay: 0s; }
    &:nth-child(2) { animation-delay: 0.2s; }
    &:nth-child(3) { animation-delay: 0.4s; }
  }
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

@keyframes bubbleFloat {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

@keyframes titlePulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.05); }
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

.fade-out-enter-active,
.fade-out-leave-active {
  transition: opacity 0.5s ease;
}

.fade-out-enter-from,
.fade-out-leave-to {
  opacity: 0;
}
</style>
