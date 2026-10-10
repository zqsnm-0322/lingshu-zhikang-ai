<template>
  <div class="top-statistics">
    <div 
      v-for="(stat, index) in stats" 
      :key="index"
      class="stat-card"
      :class="{ loading: loading }"
    >
      <div class="stat-icon-wrapper">
        <div class="stat-icon" :style="{ color: stat.color }">
          {{ stat.icon }}
        </div>
      </div>
      
      <div class="stat-content">
        <div class="stat-value">
          <span class="number">{{ stat.value }}</span>
          <span class="unit">{{ stat.unit }}</span>
        </div>
        <div class="stat-label">{{ stat.label }}</div>
      </div>
      
      <div class="stat-trend" :class="stat.trend">
        <span>{{ stat.trendValue }}</span>
        <span class="trend-icon">{{ getTrendIcon(stat.trend) }}</span>
      </div>
      
      <div class="card-glow" :style="{ background: `radial-gradient(circle, ${stat.color}20, transparent 70%)` }"></div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  stats: {
    type: Array,
    default: () => []
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const getTrendIcon = (trend) => {
  if (trend === 'up') return '↗'
  if (trend === 'down') return '↘'
  return '→'
}
</script>

<style scoped lang="scss">
.top-statistics {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 20px;
  
  .stat-card {
    position: relative;
    background: rgba(10, 14, 39, 0.6);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(0, 217, 255, 0.2);
    border-radius: 16px;
    padding: 24px;
    display: flex;
    align-items: center;
    gap: 16px;
    transition: all 0.3s ease;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      width: 4px;
      height: 100%;
      background: linear-gradient(180deg, #00D9FF, #00FF88);
      opacity: 0;
      transition: opacity 0.3s ease;
    }
    
    &:hover {
      border-color: rgba(0, 217, 255, 0.5);
      transform: translateY(-6px);
      box-shadow: 0 12px 40px rgba(0, 217, 255, 0.3);
      
      &::before {
        opacity: 1;
      }
      
      .card-glow {
        opacity: 1;
      }
      
      .stat-icon {
        transform: scale(1.1) rotate(5deg);
      }
    }
    
    &.loading {
      .stat-value .number {
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
    
    .stat-icon-wrapper {
      width: 64px;
      height: 64px;
      border-radius: 14px;
      background: rgba(0, 217, 255, 0.08);
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
      position: relative;
      
      &::before {
        content: '';
        position: absolute;
        inset: -2px;
        border-radius: 16px;
        background: linear-gradient(135deg, rgba(0, 217, 255, 0.2), rgba(0, 255, 136, 0.2));
        opacity: 0;
        transition: opacity 0.3s ease;
      }
      
      .stat-icon {
        font-size: 2.5rem;
        filter: drop-shadow(0 0 15px currentColor);
        transition: all 0.3s ease;
        position: relative;
        z-index: 1;
      }
    }
    
    &:hover .stat-icon-wrapper::before {
      opacity: 1;
    }
    
    .stat-content {
      flex: 1;
      min-width: 0;
      
      .stat-value {
        display: flex;
        align-items: baseline;
        gap: 6px;
        margin-bottom: 6px;
        
        .number {
          font-size: 2rem;
          font-weight: 800;
          color: #fff;
          text-shadow: 0 0 10px rgba(255, 255, 255, 0.3);
        }
        
        .unit {
          font-size: 1rem;
          color: rgba(255, 255, 255, 0.6);
          font-weight: 500;
        }
      }
      
      .stat-label {
        font-size: 0.95rem;
        color: rgba(255, 255, 255, 0.7);
        font-weight: 500;
      }
    }
    
    .stat-trend {
      display: flex;
      align-items: center;
      gap: 6px;
      font-size: 0.9rem;
      font-weight: 700;
      padding: 8px 14px;
      border-radius: 12px;
      white-space: nowrap;
      
      &.up {
        color: #00FF88;
        background: rgba(0, 255, 136, 0.15);
        box-shadow: 0 0 15px rgba(0, 255, 136, 0.2);
      }
      
      &.down {
        color: #FF6B6B;
        background: rgba(255, 107, 107, 0.15);
        box-shadow: 0 0 15px rgba(255, 107, 107, 0.2);
      }
      
      &.stable {
        color: #FEC957;
        background: rgba(254, 201, 87, 0.15);
        box-shadow: 0 0 15px rgba(254, 201, 87, 0.2);
      }
      
      .trend-icon {
        font-size: 1.1rem;
      }
    }
    
    .card-glow {
      position: absolute;
      top: 50%;
      right: -25%;
      width: 150px;
      height: 150px;
      border-radius: 50%;
      opacity: 0;
      transition: all 0.5s ease;
      pointer-events: none;
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
  .top-statistics {
    grid-template-columns: repeat(2, 1fr);
  }
}

@media (max-width: 768px) {
  .top-statistics {
    grid-template-columns: 1fr;
    gap: 16px;
    
    .stat-card {
      padding: 20px;
      
      .stat-icon-wrapper {
        width: 56px;
        height: 56px;
        
        .stat-icon {
          font-size: 2rem;
        }
      }
      
      .stat-content .stat-value .number {
        font-size: 1.6rem;
      }
    }
  }
}
</style>

