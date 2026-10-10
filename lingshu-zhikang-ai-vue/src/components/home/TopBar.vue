<template>
  <div class="top-user-bar" :class="{ 'hide-top': hideTop }">
    <button @click="$emit('logout')" class="logout-btn" type="button">
      <span class="logout-icon">🚪</span>
      <span>退出登录</span>
    </button>
  </div>
</template>

<script setup>
defineProps({
  hideTop: {
    type: Boolean,
    default: false
  }
})

defineEmits(['logout'])
</script>

<style scoped lang="scss">
.top-user-bar {
  position: fixed;
  top: 290px;
  right: 410px;
  z-index: 1100;
  display: flex;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.hide-top {
    opacity: 0;
    transform: translateY(-20px);
    pointer-events: none;
  }
  
  align-items: center;
  gap: 2rem;
  padding: 1rem 2rem;
  background: transparent;
  backdrop-filter: none;
  
  .logout-btn {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: rgba(255, 76, 76, 0.9);
    color: pink;
    border: none;
    border-radius: 25px;
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
    font-weight: 500;
    cursor: pointer;
    transition: all 0.3s ease;
    box-shadow: 0 2px 10px rgba(255, 76, 76, 0.3);
    z-index: 9999;
    
    .logout-icon {
      font-size: 1rem;
    }
    
    &:hover {
      background: rgba(255, 60, 60, 1);
      transform: translateY(-2px);
      box-shadow: 0 4px 15px rgba(255, 76, 76, 0.4);
    }
    
    &:active {
      transform: translateY(0);
      box-shadow: 0 2px 8px rgba(255, 76, 76, 0.3);
    }
  }
}

@media (max-width: 767.98px) {
  .top-user-bar {
    top: 10px !important;
    right: 10px !important;
    padding: 0.5rem !important;
    
    .logout-btn {
      padding: 0.4rem 0.8rem !important;
      font-size: 0.75rem !important;
      
      span:last-child {
        display: none !important;
      }
    }
  }
}
</style>

