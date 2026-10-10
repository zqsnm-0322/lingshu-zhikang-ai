<template>
  <transition name="fade-out">
    <div v-if="show" class="loading-overlay">
      <div class="loading-content">
        <!-- 食物飘动动画 -->
        <div class="food-scene">
          <div class="main-dish">
            <img :src="icon" class="dish-icon" />
          </div>
        </div>
        <h2 class="loading-title">🍽️ 每日食品推荐</h2>
        <p class="loading-subtitle">24小时在线的食品推荐...</p>
        <p class="sub-text">正在为您准备个性化健康食谱...</p>
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
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 10000;
}

.loading-content {
  text-align: center;
  color: white;
}

.food-scene {
  position: relative;
  width: 350px;
  height: 300px;
  margin: 0 auto 2rem;
  
  .main-dish {
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
    width: 140px;
    height: 140px;
    background: white;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
    z-index: 10;
    animation: dishRotate 4s linear infinite;
    
    .dish-icon {
      width: 90px;
      height: 90px;
    }
  }
  
  .food-items {
    .food-item {
      position: absolute;
      font-size: 40px;
      animation: foodFloat 3s ease-in-out infinite;
      
      &.food-1 {
        top: 20px;
        left: 50px;
        animation-delay: 0s;
      }
      
      &.food-2 {
        top: 30px;
        right: 50px;
        animation-delay: 0.5s;
      }
      
      &.food-3 {
        top: 120px;
        left: 10px;
        animation-delay: 1s;
      }
      
      &.food-4 {
        top: 120px;
        right: 10px;
        animation-delay: 1.5s;
      }
      
      &.food-5 {
        bottom: 40px;
        left: 60px;
        animation-delay: 2s;
      }
      
      &.food-6 {
        bottom: 40px;
        right: 60px;
        animation-delay: 2.5s;
      }
    }
  }
  
  .chef-hat {
    position: absolute;
    top: -10px;
    left: 50%;
    transform: translateX(-50%);
    font-size: 50px;
    animation: hatBounce 1s ease-in-out infinite;
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

@keyframes dishRotate {
  0% {
    transform: translate(-50%, -50%) rotate(0deg);
  }
  100% {
    transform: translate(-50%, -50%) rotate(360deg);
  }
}

@keyframes foodFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg) scale(1);
  }
  25% {
    transform: translateY(-30px) rotate(10deg) scale(1.1);
  }
  50% {
    transform: translateY(0) rotate(0deg) scale(1);
  }
  75% {
    transform: translateY(-15px) rotate(-10deg) scale(0.9);
  }
}

@keyframes hatBounce {
  0%, 100% {
    transform: translateX(-50%) translateY(0);
  }
  50% {
    transform: translateX(-50%) translateY(-10px);
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
