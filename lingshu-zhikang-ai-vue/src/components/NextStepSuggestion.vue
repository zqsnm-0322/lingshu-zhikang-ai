<template>
  <div v-if="suggestions.length > 0" class="next-step-container" :class="{ 'compact': compact }">
    <div class="suggestion-header">
      <span class="header-icon">💡</span>
      <h3>{{ title }}</h3>
      <button v-if="closeable" @click="$emit('close')" class="close-btn">✕</button>
    </div>
    
    <div class="suggestions-grid">
      <div 
        v-for="(suggestion, index) in suggestions" 
        :key="index"
        class="suggestion-card"
        @click="handleNavigate(suggestion)"
      >
        <div class="card-icon">{{ suggestion.icon }}</div>
        <div class="card-content">
          <h4>{{ suggestion.title }}</h4>
          <p class="reason">{{ suggestion.reason }}</p>
        </div>
        <div class="arrow-icon">→</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useHealthDataStore } from '@/stores/healthData'

interface Props {
  currentFeature?: string
  title?: string
  compact?: boolean
  closeable?: boolean
}

const props = withDefaults(defineProps<Props>(), {
  currentFeature: '',
  title: '接下来您可能需要',
  compact: false,
  closeable: false
})

const emit = defineEmits(['close', 'navigate'])

const router = useRouter()
const healthStore = useHealthDataStore()
const suggestions = ref<any[]>([])

onMounted(() => {
  loadSuggestions()
})

const loadSuggestions = () => {
  if (props.currentFeature) {
    suggestions.value = healthStore.getNextStepSuggestions(props.currentFeature)
  } else {
    // 如果没有指定当前功能，显示智能推荐
    const smartRecs = healthStore.getSmartRecommendations()
    suggestions.value = smartRecs.map(rec => {
      const featureMap: Record<string, any> = {
        'ai-chat': { route: '/ai-chat', title: 'AI医生咨询', icon: '🤖' },
        'medicine-query': { route: '/medicine-query', title: '药品查询', icon: '💊' },
        'medicine-reminder': { route: '/medicine-reminder', title: '用药提醒', icon: '⏰' },
        'chat-pdf': { route: '/chat-pdf', title: '病历分析', icon: '📄' },
        'health-diary': { route: '/health-diary', title: '健康日记', icon: '📔' },
        'health-dashboard': { route: '/health-dashboard', title: '健康数据大屏', icon: '📊' },
        'symptom-checker': { route: '/symptom-checker', title: '症状自检', icon: '🔍' }
      }
      
      return {
        ...featureMap[rec.feature],
        reason: rec.reason
      }
    }).filter(s => s.route)
  }
}

const handleNavigate = (suggestion: any) => {
  // 追踪用户点击
  healthStore.trackAction(props.currentFeature || 'home', 'navigate_to_suggestion', suggestion)
  
  emit('navigate', suggestion)
  router.push(suggestion.route)
}
</script>

<style scoped lang="scss">
.next-step-container {
  background: linear-gradient(135deg, rgba(255, 255, 255, 0.95), rgba(248, 250, 252, 0.95));
  backdrop-filter: blur(20px);
  border-radius: 24px;
  padding: 2rem;
  margin: 2rem 0;
  box-shadow: 0 8px 32px rgba(138, 43, 226, 0.12), 0 2px 8px rgba(0, 0, 0, 0.08);
  border: 2px solid rgba(138, 43, 226, 0.2);
  transition: all 0.3s ease;
  
  &:hover {
    box-shadow: 0 12px 40px rgba(138, 43, 226, 0.18), 0 4px 12px rgba(0, 0, 0, 0.1);
    transform: translateY(-2px);
  }
  
  &.compact {
    padding: 1.5rem;
    margin: 1rem 0;
  }
}

.suggestion-header {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  margin-bottom: 1.5rem;
  
  .header-icon {
    font-size: 1.8rem;
    animation: pulse 2s infinite;
  }
  
  h3 {
    flex: 1;
    font-size: 1.5rem;
    font-weight: 700;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    background-clip: text;
    margin: 0;
  }
  
  .close-btn {
    background: transparent;
    border: none;
    font-size: 1.5rem;
    color: #999;
    cursor: pointer;
    padding: 0.25rem 0.5rem;
    border-radius: 8px;
    transition: all 0.2s ease;
    
    &:hover {
      background: rgba(0, 0, 0, 0.05);
      color: #666;
    }
  }
}

.suggestions-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 1rem;
}

.suggestion-card {
  display: flex;
  align-items: center;
  gap: 1rem;
  background: white;
  border-radius: 16px;
  padding: 1.2rem;
  cursor: pointer;
  transition: all 0.3s ease;
  border: 2px solid rgba(138, 43, 226, 0.1);
  
  &:hover {
    transform: translateY(-4px) translateX(4px);
    box-shadow: 0 8px 24px rgba(138, 43, 226, 0.15);
    border-color: rgba(138, 43, 226, 0.3);
    
    .arrow-icon {
      transform: translateX(4px);
      color: #667eea;
    }
    
    .card-icon {
      transform: scale(1.1) rotate(5deg);
    }
  }
  
  .card-icon {
    font-size: 2.5rem;
    transition: all 0.3s ease;
    flex-shrink: 0;
  }
  
  .card-content {
    flex: 1;
    min-width: 0;
    
    h4 {
      font-size: 1.1rem;
      font-weight: 700;
      color: #333;
      margin: 0 0 0.25rem 0;
    }
    
    .reason {
      font-size: 0.9rem;
      color: #666;
      margin: 0;
      line-height: 1.4;
    }
  }
  
  .arrow-icon {
    font-size: 1.5rem;
    color: #ccc;
    transition: all 0.3s ease;
    flex-shrink: 0;
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

// 移动端适配
@media (max-width: 767px) {
  .next-step-container {
    padding: 1.5rem;
    border-radius: 16px;
    
    &.compact {
      padding: 1rem;
    }
  }
  
  .suggestion-header {
    h3 {
      font-size: 1.2rem;
    }
    
    .header-icon {
      font-size: 1.5rem;
    }
  }
  
  .suggestions-grid {
    grid-template-columns: 1fr;
    gap: 0.75rem;
  }
  
  .suggestion-card {
    padding: 1rem;
    
    .card-icon {
      font-size: 2rem;
    }
    
    .card-content {
      h4 {
        font-size: 1rem;
      }
      
      .reason {
        font-size: 0.85rem;
      }
    }
  }
}
</style>

