<template>
  <div class="doctor-simulator-wrapper">
    <!-- 医疗脉冲加载动画 (3秒) -->
    <transition name="fade-out">
      <div v-if="isPageLoading" class="page-loading-overlay">
        <div class="page-loading-content">
          <div class="medical-loader">
            <div class="stethoscope">🩺</div>
            <div class="pulse-line">
              <svg viewBox="0 0 200 60" class="pulse-svg">
                <path class="pulse-path" d="M0,30 L40,30 L45,10 L50,50 L55,30 L200,30" />
              </svg>
            </div>
          </div>
          <div class="page-loading-text">
            <h2 class="page-loading-title">👨‍⚕️ AI 医生咨询</h2>
            <p class="page-loading-subtitle">医疗系统正在启动...</p>
          </div>
        </div>
      </div>
    </transition>
    
    <div class="doctor-simulator" :class="{ 'dark': isDark }">
    <div class="simulator-container">
      <!-- 左侧边栏 -->
      <div class="sidebar">
        <div class="sidebar-header">
          <a href="#" class="logo-link" @click="handleLogoClick">
            <UserIcon class="logo" />
            <h1 class="title">AI医生</h1>
          </a>
        </div>

        <div class="doctor-info">
          <div class="avatar">
            <UserIcon class="avatar-icon" />
          </div>
          <div class="info">
            <h3 class="name">Dr. AI Assistant</h3>
            <p class="specialty">全科医生 · 智能诊疗</p>
            <div class="status online">
              <div class="status-dot"></div>
              <span>在线咨询</span>
            </div>
          </div>
        </div>

        <div class="medical-record" v-if="medicalRecord">
          <h4>当前病历</h4>
          <div class="record-summary">
            <p class="filename">{{ medicalRecord.filename }}</p>
            <p class="upload-time">{{ formatTime(medicalRecord.uploadTime) }}</p>
          </div>
        </div>

        <div class="quick-questions">
          <h4>常见咨询</h4>
          <div class="question-list">
            <button 
              v-for="question in quickQuestions" 
              :key="question"
              class="question-btn"
              @click="handleQuestionClick(question)"
              :disabled="isStreaming"
            >
              {{ question }}
            </button>
          </div>
        </div>
      </div>
      
      <!-- 主要聊天区域 -->
      <div class="chat-main">
        <div class="chat-header">
          <div class="doctor-avatar">
            <UserIcon class="icon" />
          </div>
          <div class="doctor-details">
            <h2>AI医生在线咨询</h2>
            <p>基于您的病历提供专业医疗建议</p>
          </div>
          <button class="back-btn" @click="goBack">
            <ArrowLeftIcon class="icon" />
            返回病历分析
          </button>
        </div>

        <div class="messages" ref="messagesRef">
          <div v-if="messages.length === 0" class="welcome-message">
            <div class="welcome-content">
              <UserIcon class="welcome-icon" />
              <h3>欢迎来到AI医生咨询</h3>
              <p>我是您的专属AI医生，已经详细阅读了您的病历。</p>
              <p>请告诉我您想了解什么，我会基于您的病历提供专业建议。</p>
            </div>
          </div>
          
          <ChatMessage
            v-for="(message, index) in messages"
            :key="index"
            :message="message"
            :is-stream="isStreaming && index === messages.length - 1"
            :show-avatar="true"
          />
        </div>
        
        <div class="input-area">
          <div class="input-wrapper">
            <textarea
              v-model="userInput"
              @keydown.enter.prevent="sendMessage()"
              placeholder="请描述您的症状或问题..."
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
          <div class="input-tips">
            <span class="tip">💡 提示：您可以询问症状、用药、饮食、复查等任何医疗相关问题</span>
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
  UserIcon,
  PaperAirplaneIcon,
  ArrowLeftIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import { chatAPI } from '../services/api'
import { recordFeatureUsage } from '../utils/featureStats'

const isDark = useDark()
const router = useRouter()
const route = useRoute()
const isPageLoading = ref(true)
const messagesRef = ref(null)
const inputRef = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const messages = ref([])

// 病历信息
const medicalRecord = ref(null)

// 快速问题
const quickQuestions = ref([
  '我的病情严重吗？',
  '需要注意什么？',
  '什么时候复查？',
  '饮食有什么禁忌？',
  '可以运动吗？',
  '药物副作用有哪些？',
  '每日食物推荐'
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

// 处理问题点击
const handleQuestionClick = (question) => {
  if (question === '每日食物推荐') {
    // 跳转到每日食物推荐页面
    router.push({
      name: 'DailyFoodRecommendation',
      query: {
        chatId: medicalRecord.value?.chatId,
        filename: encodeURIComponent(medicalRecord.value?.filename || '')
      }
    })
  } else {
    // 普通问题，发送消息
    askQuestion(question)
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
  messages.value.push(userMessage)
  
  // 清空输入框
  const input = userInput.value
  userInput.value = ''
  if (inputRef.value) {
    inputRef.value.style.height = 'auto'
  }
  
  await scrollToBottom()
  
  // 添加AI医生回复容器
  const assistantMessageIndex = messages.value.length
  messages.value.push({
    role: 'assistant',
    content: '',
    timestamp: new Date(),
    isMarkdown: true,
    avatar: 'doctor'
  })
  
  try {
    isStreaming.value = true
    
    // 构建医生咨询的特殊提示词
    const doctorPrompt = buildDoctorPrompt(input)
    
    // 发送到PDF聊天接口（复用现有接口）
    const reader = await chatAPI.sendPdfMessage(doctorPrompt, medicalRecord.value?.chatId)
    const decoder = new TextDecoder()
    let result = ''
    
    // 处理流式响应
    while (true) {
      const { done, value } = await reader.read()
      if (done) break
      
      const chunk = decoder.decode(value, { stream: true })
      result += chunk
      
      // 更新消息内容
      messages.value[assistantMessageIndex] = {
        role: 'assistant',
        content: result,
        timestamp: new Date(),
        isMarkdown: true,
        avatar: 'doctor'
      }
      
      await nextTick()
      await scrollToBottom()
    }
    
    // 如果没有收到任何内容，提示用户
    if (!result || result.trim().length === 0) {
      messages.value[assistantMessageIndex] = {
        role: 'assistant',
        content: '抱歉，未能获取到回复内容。请重新尝试或换个方式提问。',
        timestamp: new Date(),
        isMarkdown: true,
        avatar: 'doctor'
      }
    }
    
  } catch (error) {
    console.error('发送消息失败:', error)
    // 只有真正的错误才显示错误消息
    if (error.name !== 'AbortError') {
      messages.value[assistantMessageIndex] = {
        role: 'assistant',
        content: '抱歉，咨询服务暂时不可用。请稍后重试或返回病历分析页面。',
        timestamp: new Date(),
        isMarkdown: true,
        avatar: 'doctor'
      }
    }
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

// 构建医生咨询提示词
const buildDoctorPrompt = (userQuestion) => {
  return `作为一名专业的AI医生，我已经详细阅读了患者的病历。现在患者向我咨询：

患者问题：${userQuestion}

请以专业医生的身份，基于病历内容回答患者的问题。回答要求：

1. **专业性**：使用医学专业知识，但用患者能理解的语言解释
2. **针对性**：结合患者的具体病历情况给出个性化建议
3. **安全性**：强调遵医嘱的重要性，不能替代面诊
4. **关怀性**：体现医生的关怀和耐心

回答格式：
## 🩺 医生回复

[针对患者问题的专业回答]

## 💊 具体建议

[基于病历的具体建议]

## ⚠️ 重要提醒

[安全提醒和注意事项]

请记住：我是AI医生助手，建议仅供参考，如有紧急情况请及时就医。`
}

// 快速提问
const askQuestion = (question) => {
  userInput.value = question
  sendMessage()
}

// 返回病历分析页面
const goBack = () => {
  router.push('/chat-pdf')
}

// Logo点击处理
const handleLogoClick = (event) => {
  event.preventDefault()
  router.push('/')
}

// 初始化
onMounted(() => {
  setTimeout(() => {
    isPageLoading.value = false
  }, 3000)
  
  // 从路由参数或localStorage获取病历信息
  const chatId = route.query.chatId
  const filename = route.query.filename
  
  if (chatId && filename) {
    medicalRecord.value = {
      chatId,
      filename: decodeURIComponent(filename),
      uploadTime: new Date()
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
  
  // 添加欢迎消息
  messages.value.push({
    role: 'assistant',
    content: `## 👋 您好！我是您的AI医生

我已经仔细阅读了您的病历：**${medicalRecord.value.filename}**

作为您的专属医疗顾问，我可以为您提供：

- 🔍 **病情解读**：详细解释检查结果和诊断
- 💊 **用药指导**：药物使用方法和注意事项  
- 🍎 **生活建议**：饮食、运动、作息调整
- 📅 **复查提醒**：何时需要复查和注意事项
- ⚠️ **症状监测**：需要警惕的症状变化

请告诉我您想了解什么，我会基于您的病历给出专业建议。

💡 **特别提示**：点击"每日食物推荐"可获得基于您病历的个性化饮食方案！`,
    timestamp: new Date(),
    isMarkdown: true,
    avatar: 'doctor'
  })
  
  adjustTextareaHeight()
  
  // 记录医生模拟功能使用
  recordFeatureUsage('医生模拟')
})

onUnmounted(() => {
  // 清理资源
})
</script>

<style scoped lang="scss">
.doctor-simulator-wrapper {
  width: 100%;
  height: 100%;
}

@keyframes pulseDraw {
  0%, 100% {
    stroke-dashoffset: 400;
  }
  50% {
    stroke-dashoffset: 0;
  }
}

@keyframes stethoscopeFloat {
  0%, 100% {
    transform: translateY(0) rotate(-10deg);
  }
  50% {
    transform: translateY(-20px) rotate(10deg);
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
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
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
  
  .medical-loader {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 20px;
    
    .stethoscope {
      font-size: 80px;
      animation: stethoscopeFloat 2s ease-in-out infinite;
      filter: drop-shadow(0 10px 20px rgba(0, 0, 0, 0.3));
    }
    
    .pulse-line {
      width: 100%;
      
      .pulse-svg {
        width: 300px;
        height: 80px;
        
        .pulse-path {
          fill: none;
          stroke: white;
          stroke-width: 3;
          stroke-linecap: round;
          stroke-linejoin: round;
          stroke-dasharray: 400;
          stroke-dashoffset: 400;
          animation: pulseDraw 2s ease-in-out infinite;
          filter: drop-shadow(0 0 10px rgba(255, 255, 255, 0.8));
        }
      }
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

.doctor-simulator {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  background: linear-gradient(135deg, #10b981 0%, #059669 100%);
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
      radial-gradient(circle at 20% 50%, rgba(16, 185, 129, 0.3), transparent 50%),
      radial-gradient(circle at 80% 80%, rgba(5, 150, 105, 0.2), transparent 50%);
    animation: pulse 8s ease-in-out infinite;
  }

  .simulator-container {
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
      align-items: center;
      gap: 0.75rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      background: linear-gradient(to right, rgba(16, 185, 129, 0.08), rgba(5, 150, 105, 0.08));

      .logo-link {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        text-decoration: none;
        color: inherit;
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.05);
        }
      }

      .logo {
        width: 2rem;
        height: 2rem;
        color: #10b981;
        transition: transform 0.3s ease;
        
        &:hover {
          transform: rotate(10deg);
        }
      }

      .title {
        font-size: 1.5rem;
        font-weight: 700;
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .doctor-info {
      padding: 1.5rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      
      .avatar {
        width: 4.5rem;
        height: 4.5rem;
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        margin-bottom: 1rem;
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.1) rotate(5deg);
          box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4);
        }
        
        .avatar-icon {
          width: 2.25rem;
          height: 2.25rem;
          color: white;
        }
      }
      
      .info {
        .name {
          font-size: 1.25rem;
          font-weight: bold;
          color: #333;
          margin-bottom: 0.25rem;
        }
        
        .specialty {
          color: #666;
          font-size: 0.875rem;
          margin-bottom: 0.75rem;
        }
        
        .status {
          display: flex;
          align-items: center;
          gap: 0.5rem;
          font-size: 0.875rem;
          
          &.online {
            color: #10b981;
            
            .status-dot {
              width: 0.5rem;
              height: 0.5rem;
              background: #10b981;
              border-radius: 50%;
              animation: pulse 2s infinite;
            }
          }
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
        
        .upload-time {
          color: #666;
          font-size: 0.75rem;
        }
      }
    }

    .quick-questions {
      flex: 1;
      padding: 1.5rem;
      overflow-y: auto;
      
      &::-webkit-scrollbar {
        width: 6px;
      }
      
      &::-webkit-scrollbar-track {
        background: transparent;
      }
      
      &::-webkit-scrollbar-thumb {
        background: rgba(16, 185, 129, 0.3);
        border-radius: 3px;
        
        &:hover {
          background: rgba(16, 185, 129, 0.5);
        }
      }
      
      h4 {
        font-size: 0.875rem;
        font-weight: 700;
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
        text-transform: uppercase;
        margin-bottom: 1rem;
      }
      
      .question-list {
        display: flex;
        flex-direction: column;
        gap: 0.625rem;
        
        .question-btn {
          padding: 0.875rem 1.125rem;
          border: 1px solid rgba(16, 185, 129, 0.2);
          border-radius: 0.75rem;
          background: white;
          color: #333;
          text-align: left;
          cursor: pointer;
          transition: all 0.3s ease;
          font-size: 0.875rem;
          font-weight: 500;
          animation: slideIn 0.3s ease-out;
          
          &:hover:not(:disabled) {
            border-color: #10b981;
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.08), rgba(5, 150, 105, 0.08));
            transform: translateX(4px);
            box-shadow: 0 2px 8px rgba(16, 185, 129, 0.15);
          }
          
          &:disabled {
            opacity: 0.5;
            cursor: not-allowed;
          }

          // 特殊样式为"每日食物推荐"按钮
          &:last-child {
            background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
            color: white;
            border-color: #ff9800;
            font-weight: 600;
            box-shadow: 0 4px 12px rgba(255, 152, 0, 0.3);
            
            &:hover:not(:disabled) {
              transform: translateX(4px) translateY(-2px);
              box-shadow: 0 6px 20px rgba(255, 152, 0, 0.4);
            }
          }
        }
      }
    }
  }

  .chat-main {
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

    .chat-header {
      padding: 1.5rem 2rem;
      border-bottom: 1px solid rgba(0, 0, 0, 0.06);
      background: linear-gradient(to right, rgba(16, 185, 129, 0.05), rgba(5, 150, 105, 0.05));
      display: flex;
      align-items: center;
      gap: 1rem;
      
      .doctor-avatar {
        width: 3.5rem;
        height: 3.5rem;
        background: linear-gradient(135deg, #10b981 0%, #059669 100%);
        border-radius: 50%;
        display: flex;
        align-items: center;
        justify-content: center;
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.1) rotate(5deg);
          box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4);
        }
        
        .icon {
          width: 1.75rem;
          height: 1.75rem;
          color: white;
        }
      }
      
      .doctor-details {
        flex: 1;
        
        h2 {
          font-size: 1.5rem;
          font-weight: 700;
          background: linear-gradient(135deg, #10b981 0%, #059669 100%);
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
      
      .back-btn {
        display: flex;
        align-items: center;
        gap: 0.5rem;
        padding: 0.875rem 1.125rem;
        border: 1px solid rgba(16, 185, 129, 0.2);
        border-radius: 0.75rem;
        background: white;
        color: #10b981;
        cursor: pointer;
        transition: all 0.3s ease;
        font-weight: 500;
        
        &:hover {
          border-color: #10b981;
          background: rgba(16, 185, 129, 0.05);
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
        background: rgba(16, 185, 129, 0.3);
        border-radius: 4px;
        
        &:hover {
          background: rgba(16, 185, 129, 0.5);
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
            width: 4rem;
            height: 4rem;
            color: #10b981;
            margin: 0 auto 1rem;
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
      
      .input-wrapper {
        display: flex;
        gap: 1rem;
        align-items: flex-end;
        margin-bottom: 0.75rem;
        
        textarea {
          flex: 1;
          resize: none;
          border: 2px solid rgba(16, 185, 129, 0.2);
          background: white;
          border-radius: 1.25rem;
          padding: 1rem 1.25rem;
          color: #333;
          font-family: inherit;
          font-size: 1rem;
          line-height: 1.6;
          max-height: 150px;
          transition: all 0.3s ease;
          box-shadow: 0 2px 8px rgba(16, 185, 129, 0.08);
          
          &:focus {
            outline: none;
            border-color: rgba(16, 185, 129, 0.5);
            box-shadow: 0 4px 16px rgba(16, 185, 129, 0.15);
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
          background: linear-gradient(135deg, #10b981 0%, #059669 100%);
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
          box-shadow: 0 4px 12px rgba(16, 185, 129, 0.3);
          
          &:hover:not(:disabled) {
            transform: translateY(-3px) scale(1.05);
            box-shadow: 0 8px 20px rgba(16, 185, 129, 0.4);
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
      
      .input-tips {
        .tip {
          font-size: 0.75rem;
          color: #888;
          font-weight: 500;
        }
      }
    }
  }
}

.dark {
  background: linear-gradient(135deg, #1a1a2e 0%, #0f3d3e 100%);
  
  .sidebar {
    background: rgba(30, 30, 40, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);

    .sidebar-header {
      background: linear-gradient(to right, rgba(16, 185, 129, 0.15), rgba(5, 150, 105, 0.15));
      border-bottom-color: rgba(255, 255, 255, 0.08);
      
      .title {
        background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
    }

    .doctor-info {
      border-bottom-color: rgba(255, 255, 255, 0.08);
      
      .avatar {
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
      }
      
      .info {
        .name {
          color: #e0e0e0;
        }
        
        .specialty {
          color: #999;
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
        
        .upload-time {
          color: #999;
        }
      }
    }

    .quick-questions {
      &::-webkit-scrollbar-thumb {
        background: rgba(52, 211, 153, 0.3);
        
        &:hover {
          background: rgba(52, 211, 153, 0.5);
        }
      }
      
      h4 {
        background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
        -webkit-background-clip: text;
        -webkit-text-fill-color: transparent;
        background-clip: text;
      }
      
      .question-list {
        .question-btn {
          background: rgba(40, 40, 50, 0.8);
          border-color: rgba(52, 211, 153, 0.25);
          color: #e0e0e0;
          
          &:hover:not(:disabled) {
            border-color: #10b981;
            background: linear-gradient(135deg, rgba(16, 185, 129, 0.15), rgba(5, 150, 105, 0.15));
          }

          &:last-child {
            background: linear-gradient(135deg, #ff9800 0%, #ff5e62 100%);
            border-color: #ff9800;
            
            &:hover:not(:disabled) {
              box-shadow: 0 6px 20px rgba(255, 152, 0, 0.4);
            }
          }
        }
      }
    }
  }
  
  .chat-main {
    background: rgba(30, 30, 40, 0.95);
    border-color: rgba(255, 255, 255, 0.1);
    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.5);
    
    .chat-header {
      background: linear-gradient(to right, rgba(16, 185, 129, 0.08), rgba(5, 150, 105, 0.08));
      border-bottom-color: rgba(255, 255, 255, 0.08);
      
      .doctor-avatar {
        box-shadow: 0 4px 12px rgba(16, 185, 129, 0.4);
      }
      
      .doctor-details {
        h2 {
          background: linear-gradient(135deg, #34d399 0%, #10b981 100%);
          -webkit-background-clip: text;
          -webkit-text-fill-color: transparent;
          background-clip: text;
        }
        
        p {
          color: #999;
        }
      }
      
      .back-btn {
        background: rgba(40, 40, 50, 0.8);
        border-color: rgba(52, 211, 153, 0.25);
        color: #34d399;
        
        &:hover {
          border-color: #10b981;
          background: rgba(16, 185, 129, 0.15);
        }
      }
    }

    .messages {
      &::-webkit-scrollbar-thumb {
        background: rgba(52, 211, 153, 0.3);
        
        &:hover {
          background: rgba(52, 211, 153, 0.5);
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
      
      .input-wrapper {
        textarea {
          background: rgba(40, 40, 50, 0.8);
          border-color: rgba(52, 211, 153, 0.25);
          color: #e0e0e0;
          box-shadow: 0 2px 8px rgba(0, 0, 0, 0.3);
          
          &:focus {
            border-color: rgba(52, 211, 153, 0.5);
            box-shadow: 0 4px 16px rgba(52, 211, 153, 0.2);
          }

          &:disabled {
            background: rgba(30, 30, 30, 0.95);
          }
          
          &::placeholder {
            color: #666;
          }
        }
      }
      
      .input-tips {
        .tip {
          color: #999;
        }
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}

@media (max-width: 768px) {
  .doctor-simulator {
    .simulator-container {
      padding: 0;
    }

    .sidebar {
      display: none;
    }

    .chat-main {
      border-radius: 0;
    }
  }
}
</style>
