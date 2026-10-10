
<template>
  <div class="daily-food-wrapper">
    <!-- 页面加载动画 (3秒) -->
    
    <!-- 加载状态 -->
    <div class="loading" v-if="isLoading">
      <div class="loading-content">
        <div class="loading-spinner"></div>
        <div class="loading-text">正在分析病历，生成个性化食物推荐...</div>
      </div>
    </div>

    <!-- 无病历状态 -->
    <div class="no-medical-record" v-else-if="!medicalRecord" :class="{ 'dark': isDark }">
      <div class="no-record-content">
        <div class="no-record-icon">📋</div>
        <h3>未找到病历信息</h3>
        <p>请先在病历分析页面上传PDF文件，然后再使用食物推荐功能。</p>
        <button class="go-upload-btn" @click="goToUpload">
          <ArrowLeftIcon class="icon" />
          前往上传病历
        </button>
      </div>
    </div>

    <!-- 正常显示状态 -->
    <div class="daily-food-recommendation" :class="{ 'dark': isDark }" v-else>
    <div class="recommendation-container">
      <!-- 左侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <div class="logo-section">
            <div class="logo">🍎</div>
            <h1 class="title">每日食谱</h1>
          </div>
          <button class="back-btn" @click="goBack">
            <ArrowLeftIcon class="icon" />
            返回医生咨询
          </button>
        </div>

        <!-- 病历信息 -->
        <div class="medical-record" v-if="medicalRecord">
          <h4>当前病历</h4>
          <div class="record-summary">
            <p class="filename">{{ medicalRecord.filename }}</p>
            <p class="diagnosis">{{ medicalRecord.diagnosis || '单纯性阑尾炎' }}</p>
            <p class="upload-time">{{ formatTime(medicalRecord.uploadTime) }}</p>
          </div>
        </div>

        <!-- 推荐历史 -->
        <div class="history-header">
          <h2>推荐记录</h2>
          <button class="new-recommendation" @click="generateNewRecommendation">
            <PlusIcon class="icon" />
            新推荐
          </button>
        </div>

        <div class="history-list">
          <div 
            v-for="recommendation in recommendationHistory" 
            :key="recommendation.id"
            class="history-item"
            :class="{ 'active': currentRecommendationId === recommendation.id }"
          >
            <div class="history-item-content" @click="loadRecommendation(recommendation.id)">
              <ChatBubbleLeftRightIcon class="icon" />
              <span class="title">{{ recommendation.title }}</span>
            </div>
            <button 
              class="delete-btn"
              @click.stop="deleteRecommendation(recommendation.id)"
              :disabled="isDeleting === recommendation.id"
            >
              <TrashIcon class="icon" />
            </button>
          </div>
        </div>
      </div>
      
      <!-- 主要内容区域 -->
      <div class="main-content">
        <div class="content-header">
          <div class="nutritionist-info">
            <div class="avatar">🧑‍⚕️</div>
            <div class="info">
              <h3>AI营养师</h3>
              <p>基于您的病历提供个性化饮食建议</p>
            </div>
          </div>
        </div>

        <div class="messages" ref="messagesRef">
          <div v-if="currentMessages.length === 0" class="welcome-message">
            <div class="welcome-content">
              <div class="welcome-icon">🍎</div>
              <h3>欢迎使用每日食物推荐</h3>
              <p>我是您的专属AI营养师，已经详细分析了您的病历。</p>
              <p>我将为您提供个性化的饮食建议和每日食谱推荐。</p>
            </div>
          </div>
          
          <ChatMessage
            v-for="(message, index) in currentMessages"
            :key="index"
            :message="message"
            :is-stream="isStreaming && index === currentMessages.length - 1"
            :show-avatar="true"
          />
        </div>
        
        <div class="input-area">
          <div class="quick-actions">
            <button 
              v-for="action in quickActions" 
              :key="action"
              class="action-btn"
              @click="askQuestion(action)"
              :disabled="isStreaming"
            >
              {{ action }}
            </button>
          </div>
          
          <div class="input-wrapper">
            <textarea
              v-model="userInput"
              @keydown.enter.prevent="sendMessage()"
              placeholder="请告诉我您的饮食偏好或特殊需求..."
              rows="1"
              ref="inputRef"
              :disabled="isStreaming"
            ></textarea>
            <button 
              class="send-button" 
              @click="sendMessage()"
              :disabled="isStreaming || !userInput.trim()"
            >
              <PaperAirplaneIcon class="icon" />
            </button>
          </div>
        </div>
      </div>
    </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, nextTick, onUnmounted } from 'vue'
import { useDark } from '@vueuse/core'
import { useRouter, useRoute } from 'vue-router'
import { 
  ArrowLeftIcon,
  PlusIcon,
  ChatBubbleLeftRightIcon,
  TrashIcon,
  PaperAirplaneIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'
import icon3 from '../views/public/3.png'

const isDark = useDark()
const router = useRouter()
const route = useRoute()
const isPageLoading = ref(true)
const isLoading = ref(true)
const messagesRef = ref(null)
const inputRef = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const currentRecommendationId = ref(null)
const currentMessages = ref([])
const recommendationHistory = ref([])
const isDeleting = ref(null)

// 病历信息
const medicalRecord = ref(null)

// 快速操作按钮
const quickActions = ref([
  '今日早餐推荐',
  '午餐食谱',
  '晚餐建议',
  '营养补充',
  '禁忌食物',
  '康复食谱'
])

// 格式化时间
const formatTime = (time) => {
  return new Date(time).toLocaleString('zh-CN')
}

// 滚动到底部
const scrollToBottom = async () => {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

// 自动调整输入框高度
const adjustTextareaHeight = () => {
  const textarea = inputRef.value
  if (textarea) {
    textarea.style.height = 'auto'
    textarea.style.height = textarea.scrollHeight + 'px'
  }
}

// 发送消息
const sendMessage = async () => {
  if (!userInput.value.trim() || isStreaming.value) return
  
  // 添加用户消息
  const userMessage = {
    role: 'user',
    content: userInput.value,
    timestamp: new Date(),
    avatar: 'user'
  }
  currentMessages.value.push(userMessage)
  
  // 清空输入框
  const input = userInput.value
  userInput.value = ''
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
  }
  
  await scrollToBottom()
  
  // 添加AI营养师回复容器
  const assistantMessageIndex = currentMessages.value.length
  currentMessages.value.push({
    role: 'assistant',
    content: '',
    timestamp: new Date(),
    isMarkdown: true,
    avatar: 'nutritionist'
  })
  
  try {
    isStreaming.value = true
    
    // 构建营养师咨询的特殊提示词
    const nutritionistPrompt = buildNutritionistPrompt(input)
    
    // 发送到PDF聊天接口（复用现有接口）
    const reader = await chatAPI.sendPdfMessage(nutritionistPrompt, medicalRecord.value?.chatId)
    const decoder = new TextDecoder()
    let result = ''
    
    // 处理流式响应
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value, { stream: true })
      result += chunk
      
      // 更新消息内容
      currentMessages.value[assistantMessageIndex] = {
        role: 'assistant',
        content: result,
        timestamp: new Date(),
        isMarkdown: true,
        avatar: 'nutritionist'
      }
      
      await nextTick()
      await scrollToBottom()
    }
    
    // 如果没有收到任何内容，提示用户
    if (!result || result.trim().length === 0) {
      currentMessages.value[assistantMessageIndex] = {
        role: 'assistant',
        content: '抱歉，未能获取到回复内容。请重新尝试或换个方式提问。',
        timestamp: new Date(),
        isMarkdown: true,
        avatar: 'nutritionist'
      }
    }
    
  } catch (error) {
    console.error('发送消息失败:', error)
    // 只有真正的错误才显示错误消息
    if (error.name !== 'AbortError') {
      currentMessages.value[assistantMessageIndex] = {
        role: 'assistant',
        content: '抱歉，营养推荐服务暂时不可用。请稍后重试或返回医生咨询页面。',
        timestamp: new Date(),
        isMarkdown: true,
        avatar: 'nutritionist'
      }
    }
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

// 构建营养师咨询提示词
const buildNutritionistPrompt = (userQuestion) => {
  return `作为一名专业的AI营养师，我已经详细阅读了患者的病历。现在患者向我咨询：

患者问题：${userQuestion}

请以专业营养师的身份，基于病历内容提供个性化的饮食建议。回答要求：

1. **专业性**：使用营养学专业知识，但用患者能理解的语言解释
2. **针对性**：结合患者的具体病情（单纯性阑尾炎）给出个性化饮食方案
3. **实用性**：提供具体的食物推荐、制作方法和注意事项
4. **安全性**：强调饮食禁忌和注意事项

回答格式：
## 🍎 营养师建议

[针对患者问题的专业营养建议]

## 📋 推荐食谱

### 早餐
[具体早餐推荐]

### 午餐  
[具体午餐推荐]

### 晚餐
[具体晚餐推荐]

## ⚠️ 饮食注意事项

[重要的饮食禁忌和注意事项]

## 💡 营养小贴士

[额外的营养建议和小贴士]

请记住：我是AI营养师助手，建议仅供参考，如有特殊情况请咨询医生。`
}

// 快速提问
const askQuestion = (question) => {
  userInput.value = question
  sendMessage()
}

// 生成新推荐
const generateNewRecommendation = async () => {
  // 从现有推荐中找到最大的编号
  const maxId = recommendationHistory.value.reduce((max, rec) => {
    const id = parseInt(rec.id)
    return isNaN(id) ? max : Math.max(max, id)
  }, 0)
  
  const newRecommendationId = (maxId + 1).toString()
  currentRecommendationId.value = newRecommendationId
  currentMessages.value = []
  
  // 添加新推荐到历史列表
  const newRecommendation = {
    id: newRecommendationId,
    title: `食谱推荐 ${newRecommendationId}`,
    date: new Date().toLocaleDateString('zh-CN')
  }
  recommendationHistory.value = [newRecommendation, ...recommendationHistory.value]
  
  // 发送初始推荐请求
  userInput.value = '请根据我的病历情况，为我制定今日的个性化饮食方案'
  await sendMessage()
}

// 加载特定推荐
const loadRecommendation = async (recommendationId) => {
  currentRecommendationId.value = recommendationId
  // 这里可以从后端加载具体的推荐内容
  // 暂时使用模拟数据
  currentMessages.value = []
}

// 删除推荐记录
const deleteRecommendation = async (recommendationId) => {
  if (!confirm('确定要删除这条推荐记录吗？此操作不可恢复。')) {
    return
  }
  
  isDeleting.value = recommendationId
  
  try {
    // 这里可以调用后端API删除
    recommendationHistory.value = recommendationHistory.value.filter(rec => rec.id !== recommendationId)
    
    if (currentRecommendationId.value === recommendationId) {
      if (recommendationHistory.value.length > 0) {
        await loadRecommendation(recommendationHistory.value[0].id)
      } else {
        await generateNewRecommendation()
      }
    }
  } catch (error) {
    console.error('删除失败:', error)
    alert('删除失败，请重试')
  } finally {
    isDeleting.value = null
  }
}

// 返回医生咨询页面
const goBack = () => {
  router.push({
    name: 'DoctorSimulator',
    query: {
      chatId: medicalRecord.value?.chatId,
      filename: encodeURIComponent(medicalRecord.value?.filename || '')
    }
  })
}

// 前往上传病历页面
const goToUpload = () => {
  router.push('/chat-pdf')
}

// 初始化
onMounted(async () => {
  setTimeout(() => {
    isPageLoading.value = false
  }, 3000)
  
  try {
    // 从路由参数或localStorage获取病历信息
    const chatId = route.query.chatId
    const filename = route.query.filename
    
    if (chatId && filename) {
      medicalRecord.value = {
        chatId,
        filename: decodeURIComponent(filename),
        uploadTime: new Date(),
        diagnosis: '单纯性阑尾炎'
      }
    } else {
      // 尝试从localStorage获取
      const storedRecord = localStorage.getItem('currentMedicalRecord')
      if (storedRecord) {
        medicalRecord.value = JSON.parse(storedRecord)
      }
    }
    
    // 如果没有病历信息，返回病历分析页面
    if (!medicalRecord.value) {
      router.push('/chat-pdf')
      return
    }
    
    // 初始化推荐历史
    recommendationHistory.value = [
      {
        id: '1',
        title: '食谱推荐 1',
        date: new Date().toLocaleDateString('zh-CN')
      }
    ]
    
    // 初始化第一个推荐
    currentRecommendationId.value = '1'
    
    // 添加欢迎消息和初始推荐
    currentMessages.value = [
      {
        role: 'assistant',
        content: `## 🍎 欢迎使用每日食物推荐

我是您的专属AI营养师，已经详细分析了您的病历：**${medicalRecord.value.filename}**

基于您的**${medicalRecord.value.diagnosis || '单纯性阑尾炎'}**诊断，我为您制定了以下个性化饮食方案：

## 📋 今日推荐食谱

### 🌅 早餐推荐
- **白粥** + **咸菜丝**：易消化，减轻肠胃负担
- **蒸蛋羹**：优质蛋白质，质地柔软
- **温开水**：保持充足水分

### 🍽️ 午餐推荐  
- **小米粥** + **蒸蛋**：营养丰富，易于吸收
- **清蒸鱼片**：优质蛋白，脂肪含量低
- **菠菜汤**：补充维生素，促进恢复

### 🌙 晚餐推荐
- **瘦肉粥**：补充蛋白质和能量
- **蒸南瓜**：富含维生素A，助消化
- **温牛奶**：促进睡眠，补充钙质

## ⚠️ 饮食注意事项

### 🚫 严格禁止
- **辛辣食物**：辣椒、胡椒、芥末等
- **油腻食物**：油炸食品、肥肉、奶油等
- **粗纤维食物**：芹菜、韭菜、玉米等
- **刺激性饮品**：咖啡、浓茶、酒精等

### ✅ 推荐食用
- **流质食物**：粥类、汤类、果汁等
- **软烂食物**：蒸蛋、豆腐、面条等
- **清淡蔬菜**：菠菜、冬瓜、丝瓜等
- **优质蛋白**：鱼肉、鸡蛋、瘦肉等

## 💡 营养小贴士

1. **少食多餐**：每日5-6次，每次少量
2. **细嚼慢咽**：充分咀嚼，减轻消化负担
3. **温度适宜**：避免过热或过冷食物
4. **充足水分**：每日1500-2000ml温开水

## 🔄 恢复进程

- **第1-3天**：流质饮食为主
- **第4-7天**：逐渐过渡到半流质
- **第8天后**：根据恢复情况调整

请记住：这些建议基于您的病历分析，如有不适请及时就医！`,
        timestamp: new Date(),
        isMarkdown: true,
        avatar: 'nutritionist'
      }
    ]
    
  } finally {
    isLoading.value = false
  }
  
  adjustTextareaHeight()
})

onUnmounted(() => {
  // 清理资源
})
</script>

<style scoped lang="scss">
.daily-food-wrapper {
  width: 100%;
  height: 100%;
}

@keyframes foodFloat {
  0%, 100% {
    transform: translateY(0) rotate(0deg) scale(1);
  }
  25% {
    transform: translateY(-30px) rotate(10deg) scale(1.1);
  }
  50% {
    transform: translateY(0) rotate(0deg) scale(1);
  }
  75% {
    transform: translateY(-15px) rotate(-10deg) scale(0.9);
  }
}

.fade-out-leave-active {
  transition: all 0.8s ease;
}

.fade-out-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.page-loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  
  .page-loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2.5rem;
  }
  
  .food-loader {
    display: flex;
    gap: 30px;
    
    .food-icon {
      font-size: 60px;
      animation: foodFloat 2s ease-in-out infinite;
      animation-delay: var(--delay);
      filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.3));
    }
  }
  
  .page-loading-text {
    text-align: center;
    color: white;
    
    .page-loading-title {
      font-size: 2rem;
      font-weight: 700;
      margin: 0 0 0.5rem 0;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    }
    
    .page-loading-subtitle {
      font-size: 1.125rem;
      margin: 0;
      opacity: 0.9;
    }
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(-20px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.8;
  }
}

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

.daily-food-recommendation {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  background: linear-gradient(135deg, #ff9a56 0%, #ff6a88 50%, #ff5e62 100%);
  overflow: hidden;
  animation: fadeIn 0.5s ease-out;

  &::before {
    content: '';
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: 
      radial-gradient(circle at 20% 50%, rgba(255, 154, 86, 0.3), transparent 50%),
      radial-gradient(circle at 80% 80%, rgba(255, 106, 136, 0.2), transparent 50%);
    animation: pulse 8s ease-in-out infinite;
  }

  .recommendation-container {
    flex: 1;
    display: flex;
    max-width: 1800px;
    width: 100%;
    margin: 0 auto;
    padding: calc(64px + 1.5rem) 2rem 1.5rem 2rem;
    gap: 1.5rem;
    height: 100%;
    overflow: hidden;
    position: relative;
    z-index: 1;
  }

  .sidebar {
    width: 320px;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 1.25rem;
    display: flex;
    flex-direction: column;
    overflow: hidden;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12), 0 2px 8px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.5);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15), 0 5px 15px rgba(0, 0, 0, 0.1);
    }
    
    .sidebar-header {
      padding: 1.5rem;
      display: flex;
      flex-direction: column;
      gap: 1rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      background: linear-gradient(to right, rgba(255, 152, 0, 0.08), rgba(255, 94, 98, 0.08));

      .logo-section {
        display: flex;
        align-items: center;
        gap: 0.75rem;

        .logo {
          font-size: 2rem;
          animation: bounce 2s ease-in-out infinite;
        }

        .title {
          font-size: 1.5rem;
          font-weight: 700;
          background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
      }

      .back-btn {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.75rem 1rem;
        border: 1px solid rgba(255, 152, 0, 0.2);
        border-radius: 0.75rem;
        background: white;
        color: #ff9800;
        cursor: pointer;
        transition: all 0.3s ease;
        font-weight: 500;
        
        &:hover {
          border-color: #ff9800;
          background: rgba(255, 152, 0, 0.05);
          transform: translateX(-3px);
        }
        
        .icon {
          width: 1rem;
          height: 1rem;
          transition: transform 0.3s ease;
        }
        
        &:hover .icon {
          transform: translateX(-3px);
        }
      }
    }

    .medical-record {
      padding: 1.5rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.05);
      
      h4 {
        font-size: 0.875rem;
        font-weight: 600;
        color: #666;
        text-transform: uppercase;
        margin-bottom: 1rem;
      }
      
      .record-summary {
        .filename {
          font-weight: 500;
          color: #333;
          margin-bottom: 0.25rem;
          font-size: 0.875rem;
        }
        
        .diagnosis {
          color: #ff9800;
          font-size: 0.875rem;
          font-weight: 500;
          margin-bottom: 0.25rem;
        }
        
        .upload-time {
          color: #666;
          font-size: 0.75rem;
        }
      }
    }

    .history-header {
      flex-shrink: 0;
      padding: 1rem 1.5rem;
      display: flex;
      justify-content: space-between;
      align-items: center;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);

      h2 {
        font-size: 1rem;
        font-weight: 700;
        background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      .new-recommendation {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.625rem 1.125rem;
        border-radius: 0.75rem;
        background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
        color: white;
        border: none;
        cursor: pointer;
        transition: all 0.3s ease;
        font-size: 0.875rem;
        font-weight: 600;
        box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);

        &:hover {
          transform: translateY(-2px);
          box-shadow: 0 6px 20px rgba(255, 152, 0, 0.4);
        }
        
        &:active {
          transform: translateY(0);
        }

        .icon {
          width: 1rem;
          height: 1rem;
        }
      }
    }

    .history-list {
      flex: 1;
      overflow-y: auto;
      padding: 0 1.5rem 1.5rem;
      
      &::-webkit-scrollbar {
        width: 6px;
      }
      
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      
      &::-webkit-scrollbar-thumb {
        background: rgba(255, 152, 0, 0.3);
        border-radius: 3px;
        
        &:hover {
          background: rgba(255, 152, 0, 0.5);
        }
      }

      .history-item {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 0.875rem;
        border-radius: 0.75rem;
        transition: all 0.3s ease;
        margin-bottom: 0.5rem;
        border: 1px solid transparent;
        animation: slideIn 0.3s ease-out;

        &:hover {
          background: linear-gradient(135deg, rgba(255, 152, 0, 0.08), rgba(255, 94, 98, 0.08));
          border-color: rgba(255, 152, 0, 0.2);
          transform: translateX(4px);
        }

        &.active {
          background: linear-gradient(135deg, rgba(255, 152, 0, 0.15), rgba(255, 94, 98, 0.15));
          border-color: rgba(255, 152, 0, 0.3);
          box-shadow: 0 2px 8px rgba(255, 152, 0, 0.2);
        }

        .history-item-content {
          display: flex;
          align-items: center;
          gap: 0.75rem;
          flex: 1;
          cursor: pointer;

          .icon {
            width: 1.25rem;
            height: 1.25rem;
            color: #ff9800;
            transition: transform 0.3s ease;
          }
          
          &:hover .icon {
            transform: scale(1.1);
          }

          .title {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
            font-size: 0.875rem;
            font-weight: 500;
            color: #333;
          }
        }

        .delete-btn {
          display: flex;
          align-items: center;
          justify-content: center;
          width: 2rem;
          height: 2rem;
          border-radius: 0.5rem;
          border: none;
          background: transparent;
          color: #999;
          cursor: pointer;
          opacity: 0;
          transition: all 0.3s ease;

          &:hover {
            background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
            color: white;
            transform: scale(1.1);
          }

          &:disabled {
            opacity: 0.5;
            cursor: not-allowed;
          }

          .icon {
            width: 1.125rem;
            height: 1.125rem;
          }
        }

        &:hover .delete-btn {
          opacity: 1;
        }
      }
    }
  }

  .main-content {
    flex: 1;
    display: flex;
    flex-direction: column;
    background: rgba(255, 255, 255, 0.98);
    backdrop-filter: blur(20px);
    border-radius: 1.25rem;
    overflow: hidden;
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.12), 0 2px 8px rgba(0, 0, 0, 0.08);
    border: 1px solid rgba(255, 255, 255, 0.5);
    transition: all 0.3s ease;
    
    &:hover {
      box-shadow: 0 15px 50px rgba(0, 0, 0, 0.15), 0 5px 15px rgba(0, 0, 0, 0.1);
    }

    .content-header {
      padding: 1.5rem 2rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      background: linear-gradient(to right, rgba(255, 152, 0, 0.05), rgba(255, 94, 98, 0.05));
      
      .nutritionist-info {
        display: flex;
        align-items: center;
        gap: 1.25rem;
        
        .avatar {
          width: 3.5rem;
          height: 3.5rem;
          background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
          border-radius: 50%;
          display: flex;
          align-items: center;
          justify-content: center;
          font-size: 1.75rem;
          box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
          animation: bounce 2s ease-in-out infinite;
          transition: all 0.3s ease;
          
          &:hover {
            transform: scale(1.1) rotate(5deg);
            box-shadow: 0 8px 20px rgba(255, 152, 0, 0.4);
          }
        }
        
        .info {
          flex: 1;
          
          h3 {
            font-size: 1.5rem;
            font-weight: 700;
            background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
            margin-bottom: 0.375rem;
          }
          
          p {
            color: #777;
            font-size: 0.9375rem;
            font-weight: 500;
          }
        }
      }
    }
    
    .messages {
      flex: 1;
      overflow-y: auto;
      padding: 2rem;
      
      &::-webkit-scrollbar {
        width: 8px;
      }
      
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      
      &::-webkit-scrollbar-thumb {
        background: rgba(255, 152, 0, 0.3);
        border-radius: 4px;
        
        &:hover {
          background: rgba(255, 152, 0, 0.5);
        }
      }
      
      .welcome-message {
        display: flex;
        justify-content: center;
        padding: 2rem 0;
        
        .welcome-content {
          text-align: center;
          max-width: 500px;
          
          .welcome-icon {
            font-size: 4rem;
            margin-bottom: 1rem;
          }
          
          h3 {
            font-size: 1.5rem;
            font-weight: bold;
            color: #333;
            margin-bottom: 1rem;
          }
          
          p {
            color: #666;
            line-height: 1.6;
            margin-bottom: 0.5rem;
          }
        }
      }
    }
    
    .input-area {
      padding: 1.5rem 2rem 2rem;
      border-top: 1px solid rgba(0, 0, 0, 0.06);
      background: rgba(255, 255, 255, 0.98);
      
      .quick-actions {
        display: flex;
        flex-wrap: wrap;
        gap: 0.625rem;
        margin-bottom: 1rem;
        
        .action-btn {
          padding: 0.625rem 1.125rem;
          border: 1px solid rgba(255, 152, 0, 0.3);
          border-radius: 1.25rem;
          background: linear-gradient(135deg, rgba(255, 152, 0, 0.08), rgba(255, 94, 98, 0.08));
          color: #ff9800;
          cursor: pointer;
          transition: all 0.3s ease;
          font-size: 0.875rem;
          font-weight: 500;
          
          &:hover:not(:disabled) {
            background: linear-gradient(135deg, rgba(255, 152, 0, 0.15), rgba(255, 94, 98, 0.15));
            border-color: #ff9800;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(255, 152, 0, 0.2);
          }
          
          &:disabled {
            opacity: 0.5;
            cursor: not-allowed;
          }
        }
      }
      
      .input-wrapper {
        display: flex;
        gap: 1rem;
        align-items: flex-end;
        
        textarea {
          flex: 1;
          resize: none;
          border: 2px solid rgba(255, 152, 0, 0.2);
          background: white;
          border-radius: 1.25rem;
          padding: 1rem 1.25rem;
          color: #333;
          font-family: inherit;
          font-size: 1rem;
          line-height: 1.6;
          max-height: 150px;
          transition: all 0.3s ease;
          box-shadow: 0 2px 8px rgba(255, 152, 0, 0.08);
          
          &:focus {
            outline: none;
            border-color: rgba(255, 152, 0, 0.5);
            box-shadow: 0 4px 16px rgba(255, 152, 0, 0.15);
            transform: translateY(-2px);
          }

          &:disabled {
            background: #f5f5f5;
            cursor: not-allowed;
          }
          
          &::placeholder {
            color: #aaa;
          }
        }
        
        .send-button {
          background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
          color: white;
          border: none;
          border-radius: 0.875rem;
          width: 2.75rem;
          height: 2.75rem;
          display: flex;
          align-items: center;
          justify-content: center;
          cursor: pointer;
          transition: all 0.3s ease;
          box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
          
          &:hover:not(:disabled) {
            transform: translateY(-3px) scale(1.05);
            box-shadow: 0 8px 20px rgba(255, 152, 0, 0.4);
          }
          
          &:active:not(:disabled) {
            transform: translateY(-1px) scale(1.02);
          }
          
          &:disabled {
            background: linear-gradient(135deg, #ccc, #bbb);
            cursor: not-allowed;
            box-shadow: none;
          }
          
          .icon {
            width: 1.375rem;
            height: 1.375rem;
          }
        }
      }
    }
  }
}

.loading {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #ff9a56 0%, #ff6a88 50%, #ff5e62 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;

  .loading-content {
    text-align: center;
    animation: fadeIn 0.5s ease-out;
  }

  .loading-spinner {
    width: 60px;
    height: 60px;
    border: 4px solid rgba(255, 255, 255, 0.2);
    border-top: 4px solid white;
    border-radius: 50%;
    animation: spin 1s linear infinite;
    margin: 0 auto 1.5rem;
    box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
  }

  .loading-text {
    color: white;
    font-size: 1.125rem;
    font-weight: 600;
    text-shadow: 0 2px 8px rgba(0, 0, 0, 0.2);
  }
}

.no-medical-record {
  position: fixed;
  top: 64px;
  left: 0;
  right: 0;
  bottom: 0;
  background: var(--bg-color);
  display: flex;
  align-items: center;
  justify-content: center;

  .no-record-content {
    text-align: center;
    max-width: 500px;
    padding: 2rem;
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(10px);
    border-radius: 1rem;
    box-shadow: 0 4px 6px rgba(0, 0, 0, 0.05);

    .no-record-icon {
      font-size: 4rem;
      margin-bottom: 1.5rem;
      opacity: 0.6;
    }

    h3 {
      font-size: 1.5rem;
      font-weight: bold;
      color: #333;
      margin-bottom: 1rem;
    }

    p {
      color: #666;
      line-height: 1.6;
      margin-bottom: 2rem;
    }

    .go-upload-btn {
      display: flex;
      align-items: center;
      justify-content: center;
      gap: 0.5rem;
      padding: 1rem 2rem;
      background: #ff9800;
      color: white;
      border: none;
      border-radius: 0.75rem;
      cursor: pointer;
      transition: all 0.3s;
      font-size: 1rem;
      font-weight: 500;
      margin: 0 auto;

      &:hover {
        background: #f57c00;
        transform: translateY(-2px);
        box-shadow: 0 8px 16px rgba(255, 152, 0, 0.3);
      }

      .icon {
        width: 1.25rem;
        height: 1.25rem;
      }
    }
  }
}

.dark {
  background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);
  
  .loading {
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);

    .loading-spinner {
      border-color: rgba(255, 152, 0, 0.2);
      border-top-color: #ff9800;
    }

    .loading-text {
      color: #ffb74d;
    }
  }

  .no-medical-record {
    background: linear-gradient(135deg, #1a1a2e 0%, #16213e 100%);

    .no-record-content {
      background: rgba(30, 30, 40, 0.95);
      border: 1px solid rgba(255, 183, 77, 0.2);
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);

      h3 {
        background: linear-gradient(135deg, #ffb74d 0%, #ff8a80 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      p {
        color: #ccc;
      }

      .go-upload-btn {
        background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
        box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);

        &:hover {
          box-shadow: 0 6px 20px rgba(255, 152, 0, 0.4);
        }
      }
    }
  }

  .sidebar {
    background: rgba(30, 30, 40, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);

    .sidebar-header {
      background: linear-gradient(to right, rgba(255, 152, 0, 0.15), rgba(255, 94, 98, 0.15));
      border-bottom-color: rgba(255, 255, 255, 0.08);
      
      .title {
        background: linear-gradient(135deg, #ffb74d 0%, #ff8a80 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }

      .back-btn {
        background: rgba(40, 40, 50, 0.8);
        border-color: rgba(255, 183, 77, 0.25);
        color: #ffb74d;
        
        &:hover {
          border-color: #ff9800;
          background: rgba(255, 152, 0, 0.15);
          color: #ffb74d;
        }
      }
    }

    .medical-record {
      border-bottom-color: rgba(255, 255, 255, 0.08);
      
      h4 {
        color: #999;
      }
      
      .record-summary {
        .filename {
          color: #e0e0e0;
        }
        
        .diagnosis {
          color: #ffb74d;
        }
        
        .upload-time {
          color: #999;
        }
      }
    }

    .history-header {
      border-bottom-color: rgba(255, 255, 255, 0.08);

      h2 {
        background: linear-gradient(135deg, #ffb74d 0%, #ff8a80 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .history-list {
      &::-webkit-scrollbar-thumb {
        background: rgba(255, 183, 77, 0.3);
        
        &:hover {
          background: rgba(255, 183, 77, 0.5);
        }
      }
      
      .history-item {
        &:hover {
          background: linear-gradient(135deg, rgba(255, 152, 0, 0.15), rgba(255, 94, 98, 0.15));
          border-color: rgba(255, 183, 77, 0.3);
        }

        &.active {
          background: linear-gradient(135deg, rgba(255, 152, 0, 0.25), rgba(255, 94, 98, 0.25));
          border-color: rgba(255, 183, 77, 0.4);
          box-shadow: 0 2px 8px rgba(255, 183, 77, 0.3);
        }

        .history-item-content {
          .icon {
            color: #ffb74d;
          }
          
          .title {
            color: #e0e0e0;
          }
        }

        .delete-btn {
          color: #888;
        }
      }
    }
  }
  
  .main-content {
    background: rgba(30, 30, 40, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
    
    .content-header {
      background: linear-gradient(to right, rgba(255, 152, 0, 0.08), rgba(255, 94, 98, 0.08));
      border-bottom-color: rgba(255, 255, 255, 0.08);
      
      .nutritionist-info {
        .avatar {
          box-shadow: 0 4px 12px rgba(255, 152, 0, 0.4);
        }
        
        .info {
          h3 {
            background: linear-gradient(135deg, #ffb74d 0%, #ff8a80 100%);
            -webkit-background-clip: text;
            -webkit-text-fill-color: transparent;
            background-clip: text;
          }
          
          p {
            color: #999;
          }
        }
      }
    }

    .messages {
      &::-webkit-scrollbar-thumb {
        background: rgba(255, 183, 77, 0.3);
        
        &:hover {
          background: rgba(255, 183, 77, 0.5);
        }
      }
      
      .welcome-message {
        .welcome-content {
          h3 {
            color: #e0e0e0;
          }
          
          p {
            color: #ccc;
          }
        }
      }
    }

    .input-area {
      background: rgba(25, 25, 35, 0.98);
      border-top-color: rgba(255, 255, 255, 0.08);
      
      .quick-actions {
        .action-btn {
          background: linear-gradient(135deg, rgba(255, 152, 0, 0.15), rgba(255, 94, 98, 0.15));
          border-color: rgba(255, 183, 77, 0.3);
          color: #ffb74d;
          
          &:hover:not(:disabled) {
            background: linear-gradient(135deg, rgba(255, 152, 0, 0.25), rgba(255, 94, 98, 0.25));
            border-color: #ff9800;
          }
        }
      }
      
      .input-wrapper {
        textarea {
          background: rgba(40, 40, 50, 0.8);
          border-color: rgba(255, 183, 77, 0.25);
          color: #e0e0e0;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
          
          &:focus {
            border-color: rgba(255, 183, 77, 0.5);
            box-shadow: 0 4px 16px rgba(255, 183, 77, 0.2);
          }

          &:disabled {
            background: rgba(30, 30, 30, 0.95);
          }
          
          &::placeholder {
            color: #666;
          }
        }
      }
    }
  }
}

@keyframes spin {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 768px) {
  .daily-food-recommendation {
    .recommendation-container {
      padding: 0;
    }

    .sidebar {
      display: none;
    }

    .main-content {
      border-radius: 0;
    }
  }
}
</style>
