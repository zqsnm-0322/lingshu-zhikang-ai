<template>
  <div v-if="recommendations.length > 0" class="smart-recommendation">
    <div class="recommendation-header">
      <div class="header-left">
        <img src="https://api.iconify.design/mdi:robot.svg?color=%23667eea" class="ai-icon" alt="AI" />
        <div>
          <h3>AI智能推荐</h3>
          <p class="subtitle">基于您的使用习惯为您推荐</p>
        </div>
      </div>
      <button @click="refreshRecommendations" class="refresh-btn" title="刷新推荐">
        <img src="https://api.iconify.design/mdi:refresh.svg?color=%23667eea" class="refresh-icon" :class="{ rotating: isRefreshing }" alt="刷新" />
      </button>
    </div>
    
    <div class="recommendations-list">
      <div 
        v-for="(rec, index) in recommendations" 
        :key="index"
        class="recommendation-item"
        :class="'priority-' + rec.priority"
        @click="handleClick(rec)"
      >
        <div class="priority-badge" :class="'badge-' + rec.priority">
          <img v-if="rec.priority === 'high'" src="https://api.iconify.design/mdi:lightning-bolt.svg?color=%23ffffff" alt="高优先级" />
          <img v-else-if="rec.priority === 'medium'" src="https://api.iconify.design/mdi:star.svg?color=%23ffffff" alt="中优先级" />
          <img v-else src="https://api.iconify.design/mdi:lightbulb-on.svg?color=%23ffffff" alt="低优先级" />
        </div>
        
        <div class="rec-content">
          <div class="rec-title">{{ getFeatureTitle(rec.feature) }}</div>
          <div class="rec-reason">{{ rec.reason }}</div>
        </div>
        
        <button class="action-btn">
          前往
          <span class="arrow">→</span>
        </button>
      </div>
    </div>
    
    <div v-if="showAllRecommendations" class="more-recommendations">
      <img src="https://api.iconify.design/mdi:lightbulb-on.svg?color=%23666666" class="hint-icon" alt="提示" />
      <p class="hint-text">提示：我们会根据您的使用情况持续优化推荐</p>
    </div>
  </div>
  
  <div v-else class="empty-recommendation">
    <img src="https://api.iconify.design/mdi:target.svg?color=%23667eea" class="empty-icon" alt="目标" />
    <p>继续使用系统，我们会为您提供个性化推荐</p>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useHealthDataStore } from '@/stores/healthData'

interface Props {
  maxRecommendations?: number
  showAllRecommendations?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  maxRecommendations: 5,
  showAllRecommendations: false
})

const router = useRouter()
const healthStore = useHealthDataStore()
const recommendations = ref<any[]>([])
const isRefreshing = ref(false)

// 功能名称映射
const featureNameMap: Record<string, string> = {
  'ai-chat': 'AI医生咨询',
  'medicine-query': '药品查询',
  'medicine-reminder': '用药提醒',
  'chat-pdf': '病历分析',
  'health-diary': '健康日记',
  'health-dashboard': '健康数据大屏',
  'symptom-checker': '症状自检',
  'chronic-disease': '慢性病管理',
  'health-report': '体检报告'
}

// 功能路由映射
const featureRouteMap: Record<string, string> = {
  'ai-chat': '/ai-chat',
  'medicine-query': '/medicine-query',
  'medicine-reminder': '/medicine-reminder',
  'chat-pdf': '/chat-pdf',
  'health-diary': '/health-diary',
  'health-dashboard': '/health-dashboard',
  'symptom-checker': '/symptom-checker',
  'chronic-disease': '/chronic-disease',
  'health-report': '/health-report',
  'game': '/game'
}

onMounted(() => {
  loadRecommendations()
})

const loadRecommendations = () => {
  const allRecs = healthStore.getSmartRecommendations()
  
  // 按优先级排序
  const sortedRecs = allRecs.sort((a, b) => {
    const priorityOrder = { high: 3, medium: 2, low: 1 }
    return priorityOrder[b.priority as keyof typeof priorityOrder] - 
           priorityOrder[a.priority as keyof typeof priorityOrder]
  })
  
  recommendations.value = props.showAllRecommendations 
    ? sortedRecs 
    : sortedRecs.slice(0, props.maxRecommendations)
}

const refreshRecommendations = () => {
  isRefreshing.value = true
  
  setTimeout(() => {
    loadRecommendations()
    isRefreshing.value = false
  }, 500)
}

const getFeatureTitle = (feature: string) => {
  return featureNameMap[feature] || feature
}

const handleClick = (rec: any) => {
  const route = featureRouteMap[rec.feature]
  if (route) {
    healthStore.trackAction('smart-recommendation', 'click', rec)
    router.push(route)
  }
}
</script>

<style scoped lang="scss">
.smart-recommendation {
  background: linear-gradient(135deg, rgba(79, 172, 254, 0.08) 0%, rgba(67, 233, 123, 0.08) 100%);
  border-radius: 24px;
  padding: 2rem;
  border: 1px solid rgba(79, 172, 254, 0.15);
  box-shadow: 0 8px 32px rgba(79, 172, 254, 0.12);
  transition: all 0.4s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    height: 3px;
    background: linear-gradient(90deg, #4facfe, #00f2fe, #43e97b, #38f9d7);
    background-size: 200% 100%;
    animation: shimmer 3s linear infinite;
  }
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 12px 40px rgba(79, 172, 254, 0.18);
  }
}

@keyframes shimmer {
  0% {
    background-position: 0% 0%;
  }
  100% {
    background-position: 200% 0%;
  }
}

.recommendation-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 1.5rem;
  
  .header-left {
    display: flex;
    align-items: center;
    gap: 1.2rem;
    
    .ai-icon {
      width: 48px;
      height: 48px;
      animation: float 3s ease-in-out infinite;
      filter: drop-shadow(0 4px 12px rgba(102, 126, 234, 0.3));
      position: relative;
      
      &::after {
        content: '';
        position: absolute;
        top: -5px;
        right: -5px;
        width: 12px;
        height: 12px;
        background: linear-gradient(135deg, #43e97b, #38f9d7);
        border-radius: 50%;
        border: 2px solid white;
        animation: pulse 2s infinite;
      }
    }
    
    h3 {
      font-size: 1.5rem;
      font-weight: 800;
      margin: 0;
      background: linear-gradient(135deg, #4facfe, #43e97b);
      background-size: 200% auto;
      -webkit-background-clip: text;
      -webkit-text-fill-color: transparent;
      background-clip: text;
      animation: gradient-shift 3s ease infinite;
      letter-spacing: -0.5px;
    }
    
    .subtitle {
      font-size: 0.95rem;
      color: #888;
      margin: 0.3rem 0 0 0;
      font-weight: 500;
    }
  }
  
  @keyframes pulse {
    0%, 100% {
      transform: scale(1);
      opacity: 1;
    }
    50% {
      transform: scale(1.2);
      opacity: 0.8;
    }
  }
  
  @keyframes gradient-shift {
    0%, 100% {
      background-position: 0% 50%;
    }
    50% {
      background-position: 100% 50%;
    }
  }
  
  .refresh-btn {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border: 2px solid rgba(79, 172, 254, 0.2);
    border-radius: 10px;
    padding: 0.6rem;
    cursor: pointer;
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.08);
    display: flex;
    align-items: center;
    justify-content: center;
    width: 40px;
    height: 40px;
    
    &:hover {
      background: linear-gradient(135deg, #4facfe, #43e97b);
      border-color: transparent;
      transform: translateY(-2px) scale(1.05);
      box-shadow: 0 6px 24px rgba(79, 172, 254, 0.3);
      
      .refresh-icon {
        filter: brightness(0) invert(1);
      }
    }
    
    &:active {
      transform: translateY(0) scale(1);
    }
    
    .refresh-icon {
      width: 20px;
      height: 20px;
      display: inline-block;
      transition: transform 0.5s ease, filter 0.3s ease;
      
      &.rotating {
        animation: rotate 0.5s linear;
      }
    }
  }
}

.recommendations-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.recommendation-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 18px;
  padding: 1.2rem;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  border: 2px solid rgba(79, 172, 254, 0.1);
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.06);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    left: 0;
    top: 0;
    bottom: 0;
    width: 4px;
    background: linear-gradient(180deg, transparent, transparent);
    transition: all 0.3s ease;
  }
  
  &:hover {
    transform: translateX(8px) translateY(-2px);
    box-shadow: 0 8px 28px rgba(79, 172, 254, 0.2);
    border-color: rgba(79, 172, 254, 0.3);
    background: white;
    
    .action-btn {
      background: linear-gradient(135deg, #43e97b, #38f9d7);
      color: white;
      transform: translateX(4px) scale(1.05);
      box-shadow: 0 6px 20px rgba(67, 233, 123, 0.5);
      
      .arrow {
        transform: translateX(4px);
      }
    }
    
    .priority-badge {
      transform: scale(1.1) rotate(5deg);
      box-shadow: 0 6px 20px rgba(0, 0, 0, 0.2);
    }
  }
  
  &.priority-high::before {
    background: linear-gradient(180deg, #ff6b6b, #ee5a6f);
  }
  
  &.priority-medium::before {
    background: linear-gradient(180deg, #4facfe, #00f2fe);
  }
  
  &.priority-low::before {
    background: linear-gradient(180deg, #43e97b, #38f9d7);
  }
  
  &:hover::before {
    width: 5px;
  }
}

.priority-badge {
  width: 50px;
  height: 50px;
  border-radius: 14px;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  flex-shrink: 0;
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  position: relative;
  
  img {
    width: 24px;
    height: 24px;
  }
  
  &::before {
    content: '';
    position: absolute;
    top: -2px;
    left: -2px;
    right: -2px;
    bottom: -2px;
    border-radius: 16px;
    padding: 2px;
    background: linear-gradient(45deg, rgba(255, 255, 255, 0.5), transparent);
    -webkit-mask: 
      linear-gradient(#fff 0 0) content-box, 
      linear-gradient(#fff 0 0);
    mask: 
      linear-gradient(#fff 0 0) content-box, 
      linear-gradient(#fff 0 0);
    -webkit-mask-composite: xor;
    mask-composite: exclude;
  }
  
  &.badge-high {
    background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
    animation: glow-red 2s ease-in-out infinite;
  }
  
  &.badge-medium {
    background: linear-gradient(135deg, #4facfe, #00f2fe);
    animation: glow-blue 2s ease-in-out infinite;
  }
  
  &.badge-low {
    background: linear-gradient(135deg, #43e97b, #38f9d7);
    animation: glow-green 2s ease-in-out infinite;
  }
}

@keyframes glow-red {
  0%, 100% {
    box-shadow: 0 4px 16px rgba(255, 107, 107, 0.3);
  }
  50% {
    box-shadow: 0 4px 24px rgba(255, 107, 107, 0.5);
  }
}

@keyframes glow-blue {
  0%, 100% {
    box-shadow: 0 4px 16px rgba(79, 172, 254, 0.3);
  }
  50% {
    box-shadow: 0 4px 24px rgba(79, 172, 254, 0.5);
  }
}

@keyframes glow-green {
  0%, 100% {
    box-shadow: 0 4px 16px rgba(67, 233, 123, 0.3);
  }
  50% {
    box-shadow: 0 4px 24px rgba(67, 233, 123, 0.5);
  }
}

.rec-content {
  flex: 1;
  min-width: 0;
  
  .rec-title {
    font-size: 1.15rem;
    font-weight: 700;
    color: #1a1a1a;
    margin-bottom: 0.4rem;
    letter-spacing: -0.3px;
    line-height: 1.3;
  }
  
  .rec-reason {
    font-size: 0.95rem;
    color: #666;
    line-height: 1.5;
    font-weight: 500;
  }
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  border: none;
  border-radius: 12px;
  padding: 0.7rem 1.2rem;
  font-size: 0.95rem;
  font-weight: 700;
  color: white;
  cursor: pointer;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  white-space: nowrap;
  box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: -100%;
    width: 100%;
    height: 100%;
    background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.3), transparent);
    transition: left 0.5s ease;
  }
  
  &:hover::before {
    left: 100%;
  }
  
  .arrow {
    font-size: 1.2rem;
    transition: transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  }
}

.more-recommendations {
  margin-top: 1.5rem;
  padding-top: 1.5rem;
  border-top: 2px dashed rgba(102, 126, 234, 0.2);
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  
  .hint-icon {
    width: 18px;
    height: 18px;
  }
  
  .hint-text {
    text-align: center;
    color: #666;
    font-size: 0.9rem;
    margin: 0;
  }
}

.empty-recommendation {
  text-align: center;
  padding: 4rem 2rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(20px);
  border-radius: 28px;
  margin: 20px;
  border: 1px solid rgba(255, 255, 255, 0.6);
  box-shadow: 0 10px 40px rgba(102, 126, 234, 0.1);
  
  .empty-icon {
    width: 80px;
    height: 80px;
    margin: 0 auto 1.5rem;
    display: block;
    animation: bounce 2s infinite;
    filter: drop-shadow(0 4px 12px rgba(0, 0, 0, 0.1));
  }
  
  p {
    color: #666;
    font-size: 1.05rem;
    font-weight: 500;
    line-height: 1.6;
  }
}

@keyframes float {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-10px);
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-15px);
  }
}

// 移动端适配
@media (max-width: 767px) {
  .smart-recommendation {
    padding: 1.5rem;
    border-radius: 16px;
  }
  
  .recommendation-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 1rem;
    
    .header-left {
      .ai-icon {
        width: 32px;
        height: 32px;
      }
      
      h3 {
        font-size: 1.3rem;
      }
      
      .subtitle {
        font-size: 0.85rem;
      }
    }
    
    .refresh-btn {
      align-self: flex-end;
      width: 36px;
      height: 36px;
      padding: 0.5rem;
      
      .refresh-icon {
        width: 16px;
        height: 16px;
      }
    }
  }
  
  .recommendation-item {
    padding: 1rem;
    
    &:hover {
      transform: translateX(4px);
    }
  }
  
  .priority-badge {
    width: 40px;
    height: 40px;
    
    img {
      width: 20px;
      height: 20px;
    }
  }
  
  .rec-content {
    .rec-title {
      font-size: 1rem;
    }
    
    .rec-reason {
      font-size: 0.85rem;
    }
  }
  
  .action-btn {
    padding: 0.6rem 1rem;
    font-size: 0.9rem;
  }
  
  .empty-recommendation {
    padding: 2rem 1rem;
    
    .empty-icon {
      width: 48px;
      height: 48px;
    }
    
    p {
      font-size: 0.9rem;
    }
  }
}
</style>

