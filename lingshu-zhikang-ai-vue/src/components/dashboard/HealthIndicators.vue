<template>
  <div class="health-indicators">
    <div class="card-header">
      <span class="header-icon"></span>
      <span class="header-title">各项健康指标</span>
    </div>
    
    <div class="indicators-list">
      <div 
        v-for="(indicator, index) in indicators" 
        :key="index"
        class="indicator-item"
        :class="{ loading: loading }"
      >
        <div class="indicator-header">
          <div class="indicator-info">
            <span class="indicator-name">{{ indicator.name }}</span>
            <span class="indicator-status" :class="indicator.status">
              {{ getStatusText(indicator.status) }}
            </span>
          </div>
          <span class="indicator-value">{{ indicator.value }}%</span>
        </div>
        
        <div class="indicator-bar">
          <div 
            class="indicator-fill" 
            :style="{ 
              width: indicator.value + '%',
              background: indicator.gradient
            }"
          >
            <div class="fill-shine"></div>
          </div>
          <div class="bar-markers">
            <span v-for="i in 4" :key="i" class="marker"></span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  indicators: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const getStatusText = (status) => {
  const statusMap = {
    good: '优秀',
    normal: '正常',
    warning: '注意',
    danger: '警告'
  }
  return statusMap[status] || '正常'
}
</script>

<style scoped lang="scss">
.health-indicators {
  background: rgba(10, 14, 39, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 217, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  transition: all 0.3s ease;
  flex: 1;
  
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
  
  .indicators-list {
    display: flex;
    flex-direction: column;
    gap: 28px;
    
    .indicator-item {
      position: relative;
      transition: all 0.3s ease;
      
      &:hover {
        transform: translateX(4px);
        
        .indicator-fill {
          box-shadow: 0 0 25px currentColor;
        }
      }
      
      &.loading {
        .indicator-value {
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
      
      .indicator-header {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 14px;
        
        .indicator-info {
          display: flex;
          align-items: center;
          gap: 12px;
          
          .indicator-name {
            font-size: 1.1rem;
            color: #fff;
            font-weight: 600;
            letter-spacing: 0.5px;
          }
          
          .indicator-status {
            font-size: 0.75rem;
            font-weight: 700;
            padding: 4px 12px;
            border-radius: 12px;
            text-transform: uppercase;
            letter-spacing: 0.5px;
            
            &.good {
              color: #00FF88;
              background: rgba(0, 255, 136, 0.15);
            }
            
            &.normal {
              color: #00D9FF;
              background: rgba(0, 217, 255, 0.15);
            }
            
            &.warning {
              color: #FEC957;
              background: rgba(254, 201, 87, 0.15);
            }
            
            &.danger {
              color: #FF6B6B;
              background: rgba(255, 107, 107, 0.15);
            }
          }
        }
        
        .indicator-value {
          font-size: 1.4rem;
          font-weight: 800;
          color: #00D9FF;
          text-shadow: 0 0 10px rgba(0, 217, 255, 0.6);
        }
      }
      
      .indicator-bar {
        position: relative;
        height: 18px;
        background: rgba(0, 0, 0, 0.4);
        border-radius: 10px;
        overflow: hidden;
        box-shadow: inset 0 2px 8px rgba(0, 0, 0, 0.6);
        
        .indicator-fill {
          height: 100%;
          border-radius: 10px;
          transition: all 1s cubic-bezier(0.4, 0, 0.2, 1);
          box-shadow: 0 0 15px currentColor;
          position: relative;
          
          .fill-shine {
            position: absolute;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background: linear-gradient(90deg, 
              transparent 0%, 
              rgba(255, 255, 255, 0.3) 50%, 
              transparent 100%
            );
            animation: shine 2s infinite;
          }
        }
        
        .bar-markers {
          position: absolute;
          top: 0;
          left: 0;
          right: 0;
          bottom: 0;
          display: flex;
          justify-content: space-around;
          align-items: center;
          pointer-events: none;
          
          .marker {
            width: 1px;
            height: 100%;
            background: rgba(255, 255, 255, 0.1);
          }
        }
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

@keyframes shine {
  0% {
    transform: translateX(-100%);
  }
  100% {
    transform: translateX(200%);
  }
}

@media (max-width: 768px) {
  .health-indicators {
    .indicators-list {
      gap: 24px;
      
      .indicator-item {
        .indicator-header {
          .indicator-info {
            flex-direction: column;
            align-items: flex-start;
            gap: 6px;
            
            .indicator-name {
              font-size: 1rem;
            }
          }
          
          .indicator-value {
            font-size: 1.2rem;
          }
        }
        
        .indicator-bar {
          height: 16px;
        }
      }
    }
  }
}
</style>

