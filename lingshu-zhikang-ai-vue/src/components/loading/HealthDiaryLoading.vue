<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 日记本书写动画 -->
        <div class="diary-book">
          <div class="book-cover">
            <img :src="icon" class="book-icon" />
          </div>
          <div class="pen-writing">
            <span class="pen">✍️</span>
          </div>
          <div class="writing-lines">
            <div class="line line-1"></div>
            <div class="line line-2"></div>
            <div class="line line-3"></div>
          </div>
        </div>
        
        <h2 class="loading-title">健康日记</h2>
        <p class="loading-subtitle">记录每日健康状况...</p>
        <p class="sub-text">正在打开您的健康日记...</p>
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
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.loading-content {
  text-align: center;
  color: white;
}

.diary-book {
  position: relative;
  width: 250px;
  height: 300px;
  margin: 0 auto 2rem;
  
  .book-cover {
    width: 200px;
    height: 250px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
    margin: 0 auto;
    display: flex;
    align-items: center;
    justify-content: center;
    animation: bookOpen 3s ease-in-out infinite;
    
    .book-icon {
      width: 120px;
      height: 120px;
    }
  }
  
  .pen-writing {
    position: absolute;
    right: 30px;
    top: 80px;
    
    .pen {
      font-size: 40px;
      display: inline-block;
      animation: penMove 2s ease-in-out infinite;
    }
  }
  
  .writing-lines {
    position: absolute;
    left: 50%;
    top: 60%;
    transform: translateX(-50%);
    width: 150px;
    
    .line {
      height: 2px;
      background: rgba(255, 255, 255, 0.6);
      margin-bottom: 15px;
      border-radius: 2px;
      animation: lineWrite 3s ease-in-out infinite;
      
      &.line-1 {
        animation-delay: 0s;
      }
      
      &.line-2 {
        animation-delay: 0.5s;
      }
      
      &.line-3 {
        animation-delay: 1s;
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

.sub-text {
  font-size: 0.95rem;
  opacity: 0.8;
}

@keyframes bookOpen {
  0%, 100% {
    transform: perspective(1000px) rotateY(0deg);
  }
  50% {
    transform: perspective(1000px) rotateY(-15deg);
  }
}

@keyframes penMove {
  0%, 100% {
    transform: translate(0, 0) rotate(-10deg);
  }
  50% {
    transform: translate(-30px, 20px) rotate(-45deg);
  }
}

@keyframes lineWrite {
  0% {
    width: 0;
    opacity: 0;
  }
  50%, 100% {
    width: 100%;
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
