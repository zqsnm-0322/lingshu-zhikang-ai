<template>
  <div 
    class="user-info-fixed" 
    :class="{ 'hide-top': hideTop }"
    @click="$emit('click')"
    title="查看我的健康档案"
  >
    <div class="user-avatar">
      <img v-if="avatarUrl" :src="avatarUrl" alt="用户头像" class="avatar-img" />
      <span v-else class="user-icon">👤</span>
    </div>
    <span class="username">{{ username }}</span>
  </div>
</template>

<script setup>
defineProps({
  hideTop: {
    type: Boolean,
    default: false
  },
  username: {
    type: String,
    default: '灵枢用户'
  },
  avatarUrl: {
    type: String,
    default: ''
  }
})

defineEmits(['click'])
</script>

<style scoped lang="scss">
.user-info-fixed {
  position: fixed;
  top: 310px;
  right: 1120px;
  z-index: 1100;
  display: flex;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.hide-top {
    opacity: 0;
    transform: translateY(-20px);
    pointer-events: none;
  }
  
  align-items: center;
  gap: 0.5rem;
  color: #fff;
  font-weight: 500;
  font-size: 0.95rem;
  cursor: pointer;
  transition: all 0.3s ease;
  padding: 0.5rem 0.8rem;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  
  &:hover {
    background: rgba(255, 255, 255, 0.2);
    transform: translateY(-2px);
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
  }
  
  &:active {
    transform: translateY(0);
  }
  
  .user-avatar {
    width: 32px;
    height: 32px;
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    overflow: hidden;
    transition: all 0.3s ease;
    background: rgba(255, 255, 255, 0.2);
    
    .avatar-img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      border-radius: 50%;
    }
    
    .user-icon {
      font-size: 1.2rem;
      color: white;
    }
  }
  
  &:hover .user-avatar {
    transform: scale(1.1);
    box-shadow: 0 0 12px rgba(255, 255, 255, 0.3);
  }
  
  .username {
    text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
  }
}

@media (max-width: 767.98px) {
  .user-info-fixed {
    top: 200px !important;
    left: 10px !important;
    right: auto !important;
    
    .user-avatar {
      width: 28px !important;
      height: 28px !important;
    }
    
    .username {
      font-size: 0.85rem !important;
    }
  }
}
</style>

