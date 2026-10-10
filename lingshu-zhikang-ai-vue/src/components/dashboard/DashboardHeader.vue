<template>
  <div class="dashboard-header">
    <div class="header-left">
      <div class="logo-icon"></div>
      <div class="title-group">
        <h1 class="main-title">
          <span class="title-text">灵枢智康 AI多模态智慧健康数据大屏</span>
          <span class="title-glow"></span>
        </h1>
        <p class="subtitle">Health Data Visualization Platform</p>
      </div>
    </div>
    
    <div class="header-right">
      <div class="time-display">
        <div class="current-date">{{ currentDate }}</div>
        <div class="current-time">{{ currentTime }}</div>
      </div>
      <button @click="$emit('back')" class="back-button">
        <span class="btn-icon">◀</span>
        <span>返回首页</span>
      </button>
    </div>
  </div>
</template>

<script setup>
defineProps({
  currentTime: {
    type: String,
    default: ''
  },
  currentDate: {
    type: String,
    default: ''
  }
})

defineEmits(['back'])
</script>

<style scoped lang="scss">
.dashboard-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 40px;
  background: rgba(10, 14, 39, 0.8);
  backdrop-filter: blur(20px);
  border-bottom: 2px solid rgba(0, 217, 255, 0.3);
  position: relative;
  z-index: 100;
  box-shadow: 0 4px 30px rgba(0, 217, 255, 0.1);
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 2px;
    background: linear-gradient(90deg, 
      transparent, 
      rgba(0, 217, 255, 0.8) 20%, 
      rgba(0, 255, 136, 0.8) 80%, 
      transparent
    );
  }
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 24px;
    
    .logo-icon {
      font-size: 3.5rem;
      filter: drop-shadow(0 0 20px rgba(0, 217, 255, 0.8));
      animation: pulse 3s ease-in-out infinite;
    }
    
    .title-group {
      .main-title {
        position: relative;
        font-size: 2.2rem;
        font-weight: 900;
        color: #fff;
        margin: 0;
        letter-spacing: 3px;
        
        .title-text {
          position: relative;
          z-index: 1;
          background: linear-gradient(135deg, #00D9FF, #00FF88, #00D9FF);
          background-size: 200% 200%;
          -webkit-background-clip: text;
          background-clip: text;
          color: transparent;
          animation: gradientFlow 3s ease-in-out infinite;
        }
        
        .title-glow {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          filter: blur(20px);
          opacity: 0.6;
          z-index: 0;
        }
      }
      
      .subtitle {
        font-size: 1rem;
        color: rgba(255, 255, 255, 0.6);
        margin: 8px 0 0 0;
        letter-spacing: 2px;
        font-weight: 300;
      }
    }
  }
  
  .header-right {
    display: flex;
    align-items: center;
    gap: 32px;
    
    .time-display {
      text-align: right;
      padding: 12px 20px;
      background: rgba(0, 217, 255, 0.05);
      border-radius: 12px;
      border: 1px solid rgba(0, 217, 255, 0.2);
      
      .current-date {
        font-size: 0.95rem;
        color: rgba(255, 255, 255, 0.7);
        margin-bottom: 6px;
        font-weight: 500;
      }
      
      .current-time {
        font-size: 2rem;
        font-weight: 700;
        color: #00D9FF;
        font-family: 'Courier New', monospace;
        text-shadow: 0 0 15px rgba(0, 217, 255, 0.8);
        letter-spacing: 2px;
      }
    }
    
    .back-button {
      display: flex;
      align-items: center;
      gap: 8px;
      padding: 12px 28px;
      background: linear-gradient(135deg, rgba(0, 217, 255, 0.15), rgba(0, 255, 136, 0.15));
      border: 2px solid rgba(0, 217, 255, 0.5);
      border-radius: 30px;
      color: #00D9FF;
      font-size: 1.05rem;
      font-weight: 600;
      cursor: pointer;
      transition: all 0.3s ease;
      box-shadow: 0 0 20px rgba(0, 217, 255, 0.2);
      position: relative;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 50%;
        left: 50%;
        width: 0;
        height: 0;
        border-radius: 50%;
        background: rgba(0, 217, 255, 0.3);
        transform: translate(-50%, -50%);
        transition: width 0.6s, height 0.6s;
      }
      
      &:hover {
        background: linear-gradient(135deg, rgba(0, 217, 255, 0.25), rgba(0, 255, 136, 0.25));
        border-color: #00D9FF;
        box-shadow: 0 0 30px rgba(0, 217, 255, 0.5);
        transform: translateY(-2px);
        
        &::before {
          width: 300px;
          height: 300px;
        }
        
        .btn-icon {
          transform: translateX(-4px);
        }
      }
      
      &:active {
        transform: translateY(0);
      }
      
      .btn-icon {
        transition: transform 0.3s ease;
        font-size: 1.2rem;
      }
      
      span {
        position: relative;
        z-index: 1;
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
    filter: drop-shadow(0 0 20px rgba(0, 217, 255, 0.8));
  }
  50% {
    transform: scale(1.05);
    filter: drop-shadow(0 0 30px rgba(0, 217, 255, 1));
  }
}

@keyframes gradientFlow {
  0%, 100% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
}

// 响应式
@media (max-width: 1200px) {
  .dashboard-header {
    .header-left .title-group .main-title {
      font-size: 1.8rem;
    }
  }
}

@media (max-width: 768px) {
  .dashboard-header {
    flex-direction: column;
    gap: 20px;
    padding: 16px 20px;
    
    .header-left {
      flex-direction: column;
      align-items: center;
      text-align: center;
      gap: 16px;
      
      .logo-icon {
        font-size: 2.5rem;
      }
      
      .title-group .main-title {
        font-size: 1.4rem;
      }
      
      .title-group .subtitle {
        font-size: 0.85rem;
      }
    }
    
    .header-right {
      width: 100%;
      justify-content: space-between;
      gap: 16px;
      
      .time-display {
        .current-time {
          font-size: 1.5rem;
        }
        
        .current-date {
          font-size: 0.85rem;
        }
      }
      
      .back-button {
        padding: 10px 20px;
        font-size: 0.95rem;
      }
    }
  }
}
</style>

