<template>
  <div class="example-feature-page">
    <!-- 顶部导航栏 -->
    <div class="page-header">
      <button @click="goBack" class="back-button">
        <span class="back-icon">←</span>
        返回
      </button>
      <h1 class="page-title">功能页面示例</h1>
      <div class="spacer"></div>
    </div>
    
    <!-- 说明卡片 -->
    <div class="info-card">
      <div class="info-icon">💡</div>
      <div class="info-content">
        <h3>这是一个功能串联示例页面</h3>
        <p>展示如何使用下一步建议组件和健康数据管理</p>
      </div>
    </div>
    
    <!-- 主要功能区域 -->
    <div class="main-content">
      <div class="content-card">
        <h2>模拟功能操作</h2>
        <p class="description">点击下面的按钮来模拟不同的功能操作，然后查看下方的智能推荐变化</p>
        
        <div class="action-buttons">
          <button @click="simulateSymptomCheck" class="action-btn symptom">
            <span class="btn-icon">🔍</span>
            <span>记录症状</span>
          </button>
          
          <button @click="simulateMedicationAdd" class="action-btn medication">
            <span class="btn-icon">💊</span>
            <span>添加用药</span>
          </button>
          
          <button @click="simulateReportUpload" class="action-btn report">
            <span class="btn-icon">📄</span>
            <span>上传报告</span>
          </button>
          
          <button @click="simulateDiaryEntry" class="action-btn diary">
            <span class="btn-icon">📔</span>
            <span>写日记</span>
          </button>
        </div>
        
        <!-- 操作结果显示 -->
        <div v-if="lastAction" class="action-result">
          <span class="result-icon">✅</span>
          <span>{{ lastAction }}</span>
        </div>
      </div>
      
      <!-- 数据统计卡片 -->
      <div class="stats-card">
        <h3>当前健康数据统计</h3>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ healthStats.totalSymptoms }}</div>
            <div class="stat-label">症状记录</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ healthStats.activeMedications }}</div>
            <div class="stat-label">进行中用药</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ healthStats.totalReports }}</div>
            <div class="stat-label">体检报告</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ healthStats.diaryDays }}</div>
            <div class="stat-label">日记天数</div>
          </div>
        </div>
        
        <button @click="viewHealthProfile" class="view-profile-btn">
          查看完整健康档案 →
        </button>
      </div>
    </div>
    
    <!-- 智能推荐组件 -->
    <SmartRecommendation :max-recommendations="3" />
    
    <!-- 下一步建议组件 -->
    <NextStepSuggestion 
      :current-feature="currentFeature"
      title="基于当前操作的建议"
      :closeable="true"
      @close="handleCloseSuggestion"
    />
    
    <!-- 最近活动 -->
    <div class="recent-activities">
      <h3>最近活动记录</h3>
      <div v-if="recentActivities.length > 0" class="activities-list">
        <div v-for="(activity, index) in recentActivities.slice(0, 5)" :key="index" class="activity-item">
          <div class="activity-icon">
            <span v-if="activity.type === 'symptom'">🔍</span>
            <span v-else-if="activity.type === 'medication'">💊</span>
            <span v-else-if="activity.type === 'report'">📄</span>
            <span v-else-if="activity.type === 'diary'">📔</span>
            <span v-else>📌</span>
          </div>
          <div class="activity-details">
            <div class="activity-text">{{ activity.content }}</div>
            <div class="activity-date">{{ activity.date }}</div>
          </div>
        </div>
      </div>
      <div v-else class="no-activities">
        <span class="empty-icon">📭</span>
        <p>暂无活动记录，开始使用上方功能吧！</p>
      </div>
    </div>
    
    <!-- 功能说明 -->
    <div class="feature-explanation">
      <h3>🎯 功能说明</h3>
      <div class="explanation-content">
        <div class="explanation-item">
          <strong>1. 数据持久化：</strong>
          <p>所有操作数据会自动保存到localStorage，刷新页面不会丢失</p>
        </div>
        <div class="explanation-item">
          <strong>2. 智能推荐：</strong>
          <p>系统根据您的操作历史，智能推荐相关功能，提升使用效率</p>
        </div>
        <div class="explanation-item">
          <strong>3. 功能串联：</strong>
          <p>各个功能之间数据互通，形成完整的健康管理闭环</p>
        </div>
        <div class="explanation-item">
          <strong>4. 下一步建议：</strong>
          <p>每次操作后，系统会推荐最相关的后续操作</p>
        </div>
      </div>
    </div>
    
    <!-- 代码示例 -->
    <div class="code-example">
      <h3>💻 代码示例</h3>
      <div class="code-block">
        <pre><code>// 在您的功能页面中使用

import { useHealthDataStore } from '@/stores/healthData'
import NextStepSuggestion from '@/components/NextStepSuggestion.vue'
import SmartRecommendation from '@/components/SmartRecommendation.vue'

const healthStore = useHealthDataStore()

// 保存数据
const saveData = () => {
  healthStore.addSymptom({
    date: new Date().toISOString(),
    symptoms: ['头痛', '发烧'],
    severity: '中度'
  })
  
  // 追踪行为
  healthStore.trackAction('symptom-checker', 'submit')
}

// 在模板中使用组件
&lt;NextStepSuggestion current-feature="symptom-checker" /&gt;
&lt;SmartRecommendation :max-recommendations="3" /&gt;</code></pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useHealthDataStore } from '@/stores/healthData'
import NextStepSuggestion from '@/components/NextStepSuggestion.vue'
import SmartRecommendation from '@/components/SmartRecommendation.vue'

const router = useRouter()
const healthStore = useHealthDataStore()

const currentFeature = ref('example')
const lastAction = ref('')

// 计算属性
const healthStats = computed(() => healthStore.healthStats)
const recentActivities = computed(() => healthStore.recentActivities)

// 模拟症状检查
const simulateSymptomCheck = () => {
  healthStore.addSymptom({
    date: new Date().toISOString(),
    symptoms: ['头痛', '疲劳'],
    severity: '轻度',
    aiSuggestion: '建议多休息，如症状持续请就医'
  })
  
  healthStore.trackAction('example', 'symptom_check')
  currentFeature.value = 'symptom-checker'
  lastAction.value = '已记录症状：头痛、疲劳'
  
  setTimeout(() => {
    lastAction.value = ''
  }, 3000)
}

// 模拟添加用药
const simulateMedicationAdd = () => {
  healthStore.addMedication({
    name: '维生素C',
    dosage: '500mg',
    frequency: '每日1次',
    startDate: new Date().toISOString(),
    reminders: ['09:00']
  })
  
  healthStore.trackAction('example', 'medication_add')
  currentFeature.value = 'medicine-query'
  lastAction.value = '已添加用药：维生素C'
  
  setTimeout(() => {
    lastAction.value = ''
  }, 3000)
}

// 模拟上传报告
const simulateReportUpload = () => {
  healthStore.addReport({
    date: new Date().toISOString(),
    title: '血常规检查报告',
    type: '常规检查',
    summary: '各项指标正常'
  })
  
  healthStore.trackAction('example', 'report_upload')
  currentFeature.value = 'health-report'
  lastAction.value = '已上传体检报告：血常规检查'
  
  setTimeout(() => {
    lastAction.value = ''
  }, 3000)
}

// 模拟写日记
const simulateDiaryEntry = () => {
  healthStore.addDiaryEntry({
    date: new Date().toISOString(),
    content: '今天感觉良好，精神状态不错，按时服药',
    mood: '😊',
    bloodPressure: '120/80',
    bloodSugar: '5.5'
  })
  
  healthStore.trackAction('example', 'diary_entry')
  currentFeature.value = 'health-diary'
  lastAction.value = '已记录今日健康日记'
  
  setTimeout(() => {
    lastAction.value = ''
  }, 3000)
}

// 查看健康档案
const viewHealthProfile = () => {
  healthStore.trackAction('example', 'view_profile')
  router.push('/health-profile')
}

// 返回
const goBack = () => {
  router.back()
}

// 关闭建议
const handleCloseSuggestion = () => {
  console.log('用户关闭了建议')
}
</script>

<style scoped lang="scss">
.example-feature-page {
  min-height: 100vh;
  background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 40%, #ff9a9e 70%, #fecfef 100%);
  padding: 2rem;
  padding-bottom: 4rem;
}

.page-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 2rem;
  
  .back-button {
    display: flex;
    align-items: center;
    gap: 0.5rem;
    background: white;
    border: 2px solid rgba(102, 126, 234, 0.3);
    border-radius: 12px;
    padding: 0.75rem 1.25rem;
    font-size: 1rem;
    font-weight: 600;
    color: #667eea;
    cursor: pointer;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(102, 126, 234, 0.1);
      transform: translateX(-4px);
    }
    
    .back-icon {
      font-size: 1.2rem;
    }
  }
  
  .page-title {
    font-size: 2rem;
    font-weight: 800;
    background: linear-gradient(135deg, #667eea, #764ba2);
    -webkit-background-clip: text;
    -webkit-text-fill-color: transparent;
    margin: 0;
  }
  
  .spacer {
    width: 120px;
  }
}

.info-card {
  display: flex;
  align-items: center;
  gap: 1.5rem;
  background: linear-gradient(135deg, rgba(67, 233, 123, 0.95), rgba(56, 249, 215, 0.95));
  border-radius: 20px;
  padding: 1.5rem 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(67, 233, 123, 0.2);
  
  .info-icon {
    font-size: 3rem;
    animation: pulse 2s infinite;
  }
  
  .info-content {
    h3 {
      color: white;
      font-size: 1.3rem;
      margin: 0 0 0.5rem 0;
      font-weight: 700;
    }
    
    p {
      color: rgba(255, 255, 255, 0.95);
      margin: 0;
      font-size: 1rem;
    }
  }
}

.main-content {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 2rem;
  margin-bottom: 2rem;
}

.content-card, .stats-card {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  
  h2, h3 {
    margin: 0 0 1rem 0;
    color: #333;
  }
  
  .description {
    color: #666;
    margin-bottom: 1.5rem;
    line-height: 1.6;
  }
}

.action-buttons {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
  
  .action-btn {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 0.75rem;
    background: white;
    border: 2px solid #e0e0e0;
    border-radius: 16px;
    padding: 1.5rem;
    cursor: pointer;
    transition: all 0.3s ease;
    
    .btn-icon {
      font-size: 2.5rem;
    }
    
    span:last-child {
      font-weight: 600;
      font-size: 1rem;
    }
    
    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
    }
    
    &.symptom {
      &:hover {
        border-color: #ff6b6b;
        background: rgba(255, 107, 107, 0.05);
      }
    }
    
    &.medication {
      &:hover {
        border-color: #4ecdc4;
        background: rgba(78, 205, 196, 0.05);
      }
    }
    
    &.report {
      &:hover {
        border-color: #feca57;
        background: rgba(254, 202, 87, 0.05);
      }
    }
    
    &.diary {
      &:hover {
        border-color: #48dbfb;
        background: rgba(72, 219, 251, 0.05);
      }
    }
  }
}

.action-result {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  background: linear-gradient(135deg, rgba(67, 233, 123, 0.15), rgba(56, 249, 215, 0.15));
  border-left: 4px solid #43e97b;
  padding: 1rem;
  border-radius: 8px;
  animation: slideIn 0.3s ease-out;
  
  .result-icon {
    font-size: 1.5rem;
  }
  
  span:last-child {
    font-weight: 600;
    color: #333;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
  
  .stat-item {
    background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
    border-radius: 12px;
    padding: 1.25rem;
    text-align: center;
    
    .stat-value {
      font-size: 2.5rem;
      font-weight: 800;
      color: #667eea;
      margin-bottom: 0.5rem;
    }
    
    .stat-label {
      font-size: 0.9rem;
      color: #666;
      font-weight: 600;
    }
  }
}

.view-profile-btn {
  width: 100%;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: white;
  border: none;
  border-radius: 12px;
  padding: 1rem;
  font-size: 1rem;
  font-weight: 700;
  cursor: pointer;
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 8px 24px rgba(102, 126, 234, 0.3);
  }
}

.recent-activities {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  
  h3 {
    margin: 0 0 1.5rem 0;
    color: #333;
  }
  
  .activities-list {
    display: flex;
    flex-direction: column;
    gap: 1rem;
  }
  
  .activity-item {
    display: flex;
    align-items: center;
    gap: 1rem;
    background: rgba(102, 126, 234, 0.05);
    border-radius: 12px;
    padding: 1rem;
    transition: all 0.3s ease;
    
    &:hover {
      background: rgba(102, 126, 234, 0.1);
      transform: translateX(4px);
    }
    
    .activity-icon {
      width: 48px;
      height: 48px;
      background: linear-gradient(135deg, #667eea, #764ba2);
      border-radius: 12px;
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1.5rem;
      flex-shrink: 0;
    }
    
    .activity-details {
      flex: 1;
      
      .activity-text {
        font-weight: 600;
        color: #333;
        margin-bottom: 0.25rem;
      }
      
      .activity-date {
        font-size: 0.85rem;
        color: #999;
      }
    }
  }
  
  .no-activities {
    text-align: center;
    padding: 3rem 2rem;
    
    .empty-icon {
      font-size: 4rem;
      display: block;
      margin-bottom: 1rem;
      opacity: 0.5;
    }
    
    p {
      color: #999;
      margin: 0;
    }
  }
}

.feature-explanation {
  background: white;
  border-radius: 20px;
  padding: 2rem;
  margin-bottom: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  
  h3 {
    margin: 0 0 1.5rem 0;
    color: #333;
  }
  
  .explanation-content {
    display: flex;
    flex-direction: column;
    gap: 1.5rem;
  }
  
  .explanation-item {
    strong {
      color: #667eea;
      font-size: 1.1rem;
      display: block;
      margin-bottom: 0.5rem;
    }
    
    p {
      color: #666;
      margin: 0;
      line-height: 1.6;
    }
  }
}

.code-example {
  background: #2d3748;
  border-radius: 20px;
  padding: 2rem;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  
  h3 {
    color: white;
    margin: 0 0 1.5rem 0;
  }
  
  .code-block {
    background: #1a202c;
    border-radius: 12px;
    padding: 1.5rem;
    overflow-x: auto;
    
    pre {
      margin: 0;
      
      code {
        color: #e2e8f0;
        font-family: 'Courier New', monospace;
        font-size: 0.9rem;
        line-height: 1.8;
      }
    }
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

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

// 移动端适配
@media (max-width: 1024px) {
  .main-content {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 767px) {
  .example-feature-page {
    padding: 1rem;
  }
  
  .page-header {
    .page-title {
      font-size: 1.5rem;
    }
    
    .spacer {
      display: none;
    }
  }
  
  .action-buttons {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
}
</style>

