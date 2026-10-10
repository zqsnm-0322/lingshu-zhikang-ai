<template>
  <div class="data-overview">
    <div class="card-header">
      <span class="header-icon"></span>
      <span class="header-title">数据总览</span>
    </div>
    
    <div class="overview-grid">
      <div 
        v-for="(item, index) in data" 
        :key="index"
        class="overview-item"
        :class="{ loading: loading }"
      >
        <div class="item-icon" :style="{ background: item.color }">
          {{ item.icon }}
        </div>
        <div class="item-info">
          <div class="item-value">{{ item.value }}</div>
          <div class="item-label">{{ item.label }}</div>
          <div class="item-trend" :class="{ up: item.trendUp, down: !item.trendUp }">
            <span>{{ item.trend }}</span>
            <span class="trend-icon">{{ item.trendUp ? '▲' : '▼' }}</span>
          </div>
        </div>
        <div class="item-glow"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  data: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})
</script>

<style scoped lang="scss">
.data-overview {
  background: rgba(10, 14, 39, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 217, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 1px;
    background: linear-gradient(90deg, transparent, rgba(0, 217, 255, 0.5), transparent);
  }
  
  &:hover {
    border-color: rgba(0, 217, 255, 0.4);
    box-shadow: 0 8px 40px rgba(0, 217, 255, 0.2);
  }
  
  .card-header {
    display: flex;
    align-items: center;
    gap: 12px;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid rgba(0, 217, 255, 0.2);
    
    .header-icon {
      font-size: 1.5rem;
      filter: drop-shadow(0 0 10px rgba(0, 217, 255, 0.6));
    }
    
    .header-title {
      font-size: 1.2rem;
      font-weight: 700;
      color: #00D9FF;
      letter-spacing: 1px;
    }
  }
  
  .overview-grid {
    display: flex;
    flex-direction: column;
    gap: 12px;
    
    .overview-item {
      position: relative;
      display: flex;
      align-items: center;
      gap: 12px;
      padding: 16px;
      background: rgba(0, 0, 0, 0.3);
      border-radius: 12px;
      border: 1px solid rgba(255, 255, 255, 0.05);
      transition: all 0.3s ease;
      overflow: hidden;
      
      &::before {
        content: '';
        position: absolute;
        top: 0;
        left: 0;
        width: 3px;
        height: 100%;
        background: linear-gradient(180deg, rgba(0, 217, 255, 0.8), rgba(0, 255, 136, 0.8));
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      &:hover {
        background: rgba(0, 0, 0, 0.5);
        transform: translateY(-4px);
        border-color: rgba(0, 217, 255, 0.3);
        box-shadow: 0 8px 24px rgba(0, 217, 255, 0.2);
        
        &::before {
          opacity: 1;
        }
        
        .item-glow {
          opacity: 1;
        }
      }
      
      &.loading {
        .item-value {
          background: linear-gradient(90deg, 
            rgba(255, 255, 255, 0.1) 25%, 
            rgba(255, 255, 255, 0.2) 50%, 
            rgba(255, 255, 255, 0.1) 75%
          );
          background-size: 200% 100%;
          animation: loading 1.5s infinite;
          color: transparent;
        }
      }
      
      .item-icon {
        width: 48px;
        height: 48px;
        border-radius: 10px;
        display: flex;
        align-items: center;
        justify-content: center;
        font-size: 1.6rem;
        flex-shrink: 0;
        box-shadow: 0 4px 16px rgba(0, 0, 0, 0.3);
        position: relative;
        
        &::before {
          content: '';
          position: absolute;
          inset: -2px;
          border-radius: 14px;
          background: inherit;
          filter: blur(8px);
          opacity: 0.5;
          z-index: -1;
        }
      }
      
      .item-info {
        flex: 1;
        min-width: 0;
        
        .item-value {
          font-size: 1.5rem;
          font-weight: 800;
          color: #fff;
          text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
          margin-bottom: 2px;
        }
        
        .item-label {
          font-size: 0.85rem;
          color: rgba(255, 255, 255, 0.6);
          margin-bottom: 4px;
        }
        
        .item-trend {
          display: inline-flex;
          align-items: center;
          gap: 3px;
          font-size: 0.75rem;
          font-weight: 600;
          padding: 3px 8px;
          border-radius: 10px;
          
          &.up {
            color: #00FF88;
            background: rgba(0, 255, 136, 0.15);
          }
          
          &.down {
            color: #FF6B6B;
            background: rgba(255, 107, 107, 0.15);
          }
          
          .trend-icon {
            font-size: 0.75rem;
          }
        }
      }
      
      .item-glow {
        position: absolute;
        top: 50%;
        right: -50%;
        width: 100px;
        height: 100px;
        border-radius: 50%;
        background: radial-gradient(circle, rgba(0, 217, 255, 0.2), transparent 70%);
        opacity: 0;
        transition: all 0.5s ease;
        pointer-events: none;
      }
    }
  }
}

@keyframes loading {
  0% {
    background-position: 200% 0;
  }
  100% {
    background-position: -200% 0;
  }
}

@media (max-width: 1400px) {
  .data-overview {
    .overview-grid {
      .overview-item {
        padding: 14px;
        
        .item-icon {
          width: 44px;
          height: 44px;
          font-size: 1.4rem;
        }
        
        .item-info {
          .item-value {
            font-size: 1.3rem;
          }
          
          .item-label {
            font-size: 0.8rem;
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .data-overview {
    .overview-grid {
      gap: 10px;
    }
  }
}
</style>

