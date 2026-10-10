<template>
  <div class="time-turntable-fixed" :class="{ 'hide-top': hideTop }">
    <div class="current-time">{{ currentTime }}</div>
    <div class="current-date">{{ currentDate }}</div>
    <div class="turntable-container">
      <turntable :color1="'#00ffff'" :color2="'#41b883'" />
    </div>
  </div>
</template>

<script setup>
import turntable from '@/components/turntable.vue'

defineProps({
  hideTop: {
    type: Boolean,
    default: false
  },
  currentTime: {
    type: String,
    default: ''
  },
  currentDate: {
    type: String,
    default: ''
  }
})
</script>

<style scoped lang="scss">
.time-turntable-fixed {
  position: fixed;
  top: 20px;
  right: 80px;
  z-index: 1100;
  display: flex;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.hide-top {
    opacity: 0;
    transform: translateY(-20px);
    pointer-events: none;
  }
  
  flex-direction: column;
  align-items: center;
  color: #000;
  font-family: 'Courier New', monospace;
  text-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
  
  width: 350px;
  height: 275px;
  background: rgba(0, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border: 2px solid rgba(65, 184, 131, 0.3);
  border-radius: 20px;
  padding: 30px;
  box-shadow: 0 12px 40px rgba(0, 255, 255, 0.15), 0 4px 16px rgba(65, 184, 131, 0.1);
  transition: all 0.4s ease;
  justify-content: center;
  
  &:hover {
    background: rgba(0, 255, 255, 0.15);
    border-color: rgba(65, 184, 131, 0.5);
    box-shadow: 0 16px 50px rgba(0, 255, 255, 0.2), 0 6px 20px rgba(65, 184, 131, 0.15);
    transform: scale(1.02);
  }
  
  .current-time {
    font-size: 2.0rem;
    font-weight: bold;
    letter-spacing: 2px;
    margin-bottom: 0.2rem;
    text-shadow: 0 0 10px rgba(0, 0, 0, 0.3);
  }
  
  .current-date {
    font-size: 1rem;
    opacity: 0.9;
    letter-spacing: 1px;
    font-weight: 500;
    margin-bottom: 1rem;
  }
  
  .turntable-container {
    transform: scale(0.8);
    margin-top: -10px;
  }
}

@media (max-width: 767.98px) {
  .time-turntable-fixed {
    top: 200px !important;
    right: 10px !important;
    width: 200px !important;
    height: 180px !important;
    padding: 15px !important;
    
    .current-time {
      font-size: 1.5rem !important;
    }
    
    .current-date {
      font-size: 0.8rem !important;
    }
    
    .turntable-container {
      transform: scale(0.6) !important;
      margin-top: -15px !important;
    }
  }
}

@media (min-width: 768px) and (max-width: 1024px) {
  .time-turntable-fixed {
    width: 280px !important;
    height: 230px !important;
  }
}
</style>

