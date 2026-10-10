<template>
  <div class="health-score">
    <div class="card-header">
      <span class="header-title">健康综合评分</span>
    </div>
    
    <div class="score-display">
      <div class="score-ring">
        <svg viewBox="0 0 200 200" class="ring-svg">
          <defs>
            <linearGradient id="scoreGradient" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#00D9FF;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#00FF88;stop-opacity:1" />
            </linearGradient>
            <filter id="glow">
              <feGaussianBlur stdDeviation="4" result="coloredBlur"/>
              <feMerge>
                <feMergeNode in="coloredBlur"/>
                <feMergeNode in="SourceGraphic"/>
              </feMerge>
            </filter>
          </defs>
          
          <!-- 背景圆环 -->
          <circle 
            cx="100" cy="100" r="80" 
            fill="none" 
            stroke="rgba(255,255,255,0.05)" 
            stroke-width="16" 
          />
          
          <!-- 进度圆环 -->
          <circle 
            cx="100" cy="100" r="80" 
            fill="none" 
            stroke="url(#scoreGradient)"
            stroke-width="16" 
            :stroke-dasharray="scoreCircle"
            stroke-linecap="round"
            transform="rotate(-90 100 100)"
            class="score-circle"
            filter="url(#glow)"
          />
          
          <!-- 内部装饰点 -->
          <circle 
            v-for="i in 12" 
            :key="i"
            :cx="100 + Math.cos((i - 1) * 30 * Math.PI / 180) * 90"
            :cy="100 + Math.sin((i - 1) * 30 * Math.PI / 180) * 90"
            r="2"
            :fill="i <= Math.floor(score / 100 * 12) ? '#00D9FF' : 'rgba(255,255,255,0.1)'"
            class="dot"
          />
        </svg>
        
        <div class="score-content">
          <div class="score-number" :class="{ loading: loading }">
            {{ animatedScore }}
          </div>
          <div class="score-label">分</div>
        </div>
      </div>
      
      <div class="score-level" :style="{ color: scoreColor }">
        <div class="level-badge">
          <span class="badge-icon">{{ scoreBadge }}</span>
          <span class="badge-text">{{ scoreLevel }}</span>
        </div>
        <div class="level-desc">{{ scoreDescription }}</div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, watch } from 'vue'

const props = defineProps({
  score: {
    type: Number,
    default: 0
  },
  loading: {
    type: Boolean,
    default: false
  }
})

const animatedScore = ref(0)

// 动画更新分数
watch(() => props.score, (newScore) => {
  const duration = 1500
  const startScore = animatedScore.value
  const diff = newScore - startScore
  const startTime = Date.now()
  
  const animate = () => {
    const elapsed = Date.now() - startTime
    const progress = Math.min(elapsed / duration, 1)
    const easeOutQuad = 1 - (1 - progress) * (1 - progress)
    animatedScore.value = Math.floor(startScore + diff * easeOutQuad)
    
    if (progress < 1) {
      requestAnimationFrame(animate)
    }
  }
  
  animate()
}, { immediate: true })

const scoreCircle = computed(() => {
  const circumference = 2 * Math.PI * 80
  const percentage = animatedScore.value / 100
  return `${circumference * percentage} ${circumference}`
})

const scoreColor = computed(() => {
  const s = animatedScore.value
  if (s >= 90) return '#00FF88'
  if (s >= 75) return '#00D9FF'
  if (s >= 60) return '#FEC957'
  return '#FF6B6B'
})

const scoreLevel = computed(() => {
  const s = animatedScore.value
  if (s >= 90) return '优秀'
  if (s >= 75) return '良好'
  if (s >= 60) return '一般'
  return '需改善'
})

const scoreBadge = computed(() => {
  const s = animatedScore.value
  if (s >= 90) return ''
  if (s >= 75) return ''
  if (s >= 60) return ''
  return ''
})

const scoreDescription = computed(() => {
  const s = animatedScore.value
  if (s >= 90) return '您的健康状况非常好，请继续保持！'
  if (s >= 75) return '您的健康状况良好，建议持续关注。'
  if (s >= 60) return '您的健康状况一般，建议加强锻炼。'
  return '建议您及时调整生活习惯，必要时就医。'
})
</script>

<style scoped lang="scss">
.health-score {
  background: rgba(10, 14, 39, 0.6);
  backdrop-filter: blur(20px);
  border: 1px solid rgba(0, 217, 255, 0.2);
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.4);
  transition: all 0.3s ease;
  position: relative;
  
  * {
    &::before,
    &::after {
      font-family: inherit !important;
    }
  }
  
  &::before {
    content: '' !important;
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
    gap: 0;
    margin-bottom: 24px;
    padding-bottom: 16px;
    border-bottom: 1px solid rgba(0, 217, 255, 0.2);
    position: relative;
    overflow: hidden;
    
    &::before,
    &::after {
      display: none !important;
      content: none !important;
      opacity: 0 !important;
      width: 0 !important;
      height: 0 !important;
    }
    
    > * {
      &::before,
      &::after {
        display: none !important;
        content: none !important;
        opacity: 0 !important;
      }
    }
    
    .header-title {
      font-size: 1.2rem;
      font-weight: 700;
      color: #00D9FF;
      letter-spacing: 1px;
      position: relative;
      margin: 0;
      padding: 0;
      
      &::before,
      &::after {
        display: none !important;
        content: none !important;
        opacity: 0 !important;
        width: 0 !important;
        height: 0 !important;
      }
    }
    
    // 强制隐藏任何图标元素
    [class*="icon"],
    .icon,
    .header-icon {
      display: none !important;
      width: 0 !important;
      height: 0 !important;
      opacity: 0 !important;
    }
  }
  
  .score-display {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 24px;
    
    .score-ring {
      position: relative;
      width: 220px;
      height: 220px;
      
      .ring-svg {
        width: 100%;
        height: 100%;
      }
      
      .score-circle {
        transition: stroke-dasharray 1s cubic-bezier(0.4, 0, 0.2, 1);
      }
      
      .dot {
        transition: all 0.3s ease;
      }
      
      .score-content {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        text-align: center;
        
        .score-number {
          font-size: 4.5rem;
          font-weight: 900;
          color: #fff;
          text-shadow: 
            0 0 30px rgba(0, 217, 255, 0.8),
            0 0 60px rgba(0, 217, 255, 0.4);
          line-height: 1;
          
          &.loading {
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
        
        .score-label {
          font-size: 1.2rem;
          color: rgba(255, 255, 255, 0.8);
          margin-top: 8px;
          font-weight: 600;
        }
      }
    }
    
    .score-level {
      text-align: center;
      
      .level-badge {
        display: inline-flex;
        align-items: center;
        gap: 12px;
        padding: 12px 32px;
        background: rgba(0, 217, 255, 0.1);
        border: 2px solid currentColor;
        border-radius: 30px;
        margin-bottom: 16px;
        box-shadow: 0 0 20px currentColor;
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.05);
          box-shadow: 0 0 30px currentColor;
        }
        
        .badge-icon {
          font-size: 1.8rem;
          filter: drop-shadow(0 0 10px currentColor);
        }
        
        .badge-text {
          font-size: 1.5rem;
          font-weight: 800;
          text-shadow: 0 0 20px currentColor;
        }
      }
      
      .level-desc {
        font-size: 0.95rem;
        color: rgba(255, 255, 255, 0.7);
        line-height: 1.6;
        max-width: 300px;
        margin: 0 auto;
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

@media (max-width: 768px) {
  .health-score {
    .score-ring {
      width: 180px !important;
      height: 180px !important;
      
      .score-content .score-number {
        font-size: 3.5rem !important;
      }
    }
    
    .score-level {
      .level-badge {
        padding: 10px 24px;
        
        .badge-icon {
          font-size: 1.5rem;
        }
        
        .badge-text {
          font-size: 1.2rem;
        }
      }
    }
  }
}
</style>

