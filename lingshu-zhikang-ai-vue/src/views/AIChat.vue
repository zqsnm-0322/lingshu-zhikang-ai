<template>
  <div class="ai-chat-page">
    <aside class="chat-sidebar">
      <button class="back-button" type="button" @click="goHome">返回主页</button>

      <div class="sidebar-title-row">
        <div>
          <p class="eyebrow">AI Consultation</p>
          <h1>聊天记录</h1>
        </div>
        <button class="new-button" type="button" @click="startNewChat">
          <PlusIcon />
          新对话
        </button>
      </div>

      <label class="search-box">
        <MagnifyingGlassIcon />
        <input v-model="historySearchKeyword" type="text" placeholder="搜索对话..." />
      </label>

      <div class="history-list">
        <button
          v-for="chat in filteredChatHistory"
          :key="chat.id"
          class="history-item"
          :class="{ active: currentChatId === chat.id }"
          type="button"
          @click="loadChat(chat.id)"
        >
          <ChatBubbleLeftRightIcon />
          <span class="history-text">
            <strong>{{ chat.title || '新对话' }}</strong>
            <small>{{ chat.preview || '暂无消息' }}</small>
          </span>
          <TrashIcon class="delete-icon" @click.stop="deleteChat(chat.id)" />
        </button>

        <div v-if="filteredChatHistory.length === 0" class="empty-history">暂无聊天记录</div>
      </div>
    </aside>

    <main class="chat-main">
      <UserQuotaDisplay
        :memberLevel="userQuota.memberLevel"
        :dailyQuota="userQuota.dailyQuota"
        :dailyUsed="userQuota.dailyUsed"
        :totalUsed="userQuota.totalUsed"
        @upgrade="goToMembership"
      />

      <section class="message-panel" ref="messagesRef">
        <div v-if="currentMessages.length === 0 && !isStreaming" class="welcome-panel">
          <p class="welcome-kicker">灵枢智康 AI 助手</p>
          <h2>今天想咨询什么健康问题？</h2>
          <div class="prompt-grid">
            <button
              v-for="prompt in quickPrompts"
              :key="prompt"
              type="button"
              @click="sendQuickPrompt(prompt)"
            >
              {{ prompt }}
            </button>
          </div>
        </div>

        <ChatMessage
          v-for="(message, index) in currentMessages"
          :key="index"
          :message="message"
          :is-stream="isStreaming && index === currentMessages.length - 1"
        />
      </section>

      <section class="composer">
        <div v-if="selectedFiles.length" class="selected-files">
          <div v-for="(file, index) in selectedFiles" :key="file.name + index" class="selected-file">
            <DocumentIcon />
            <span>{{ file.name }}</span>
            <button type="button" @click="removeFile(index)">
              <XMarkIcon />
            </button>
          </div>
        </div>

        <div class="composer-row">
          <input
            ref="fileInput"
            class="hidden-file"
            type="file"
            accept="image/*,audio/*,video/*"
            multiple
            @change="handleFileUpload"
          />
          <button class="icon-button" type="button" :disabled="isStreaming" @click="triggerFileInput">
            <PaperClipIcon />
          </button>
          <textarea
            ref="inputRef"
            v-model="userInput"
            rows="1"
            :placeholder="selectedFiles.length ? '补充说明后发送...' : '输入消息，可上传图片、音频或视频...'"
            @input="adjustTextareaHeight"
            @keydown.enter.prevent="sendMessage"
          />
          <button
            class="send-button"
            type="button"
            :disabled="isStreaming || (!userInput.trim() && selectedFiles.length === 0)"
            @click="sendMessage"
          >
            <PaperAirplaneIcon />
          </button>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ChatBubbleLeftRightIcon,
  DocumentIcon,
  MagnifyingGlassIcon,
  PaperAirplaneIcon,
  PaperClipIcon,
  PlusIcon,
  TrashIcon,
  XMarkIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import UserQuotaDisplay from '../components/UserQuotaDisplay.vue'
import { chatAPI } from '../services/api'

const router = useRouter()
const messagesRef = ref(null)
const inputRef = ref(null)
const fileInput = ref(null)
const userInput = ref('')
const isStreaming = ref(false)
const currentChatId = ref(null)
const currentMessages = ref([])
const chatHistory = ref([])
const selectedFiles = ref([])
const historySearchKeyword = ref('')

const userQuota = ref({
  memberLevel: 'FREE',
  dailyQuota: 5,
  dailyUsed: 0,
  totalUsed: 0
})

const quickPrompts = [
  '最近头疼、乏力，可能是什么原因？',
  '感冒了吃什么药比较合适？',
  '如何改善睡眠质量？',
  '体检报告里的指标怎么解读？'
]

const filteredChatHistory = computed(() => {
  const keyword = historySearchKeyword.value.trim().toLowerCase()
  if (!keyword) return chatHistory.value
  return chatHistory.value.filter((chat) =>
    `${chat.title || ''}${chat.preview || ''}`.toLowerCase().includes(keyword)
  )
})

const goHome = () => router.push('/home')
const goToMembership = () => router.push('/membership')
const createChatId = () => `chat_${Date.now()}`

const startNewChat = () => {
  currentChatId.value = createChatId()
  currentMessages.value = []
  selectedFiles.value = []
  userInput.value = ''
  resetTextarea()
}

const loadUserQuota = async () => {
  try {
    const userId = localStorage.getItem('userId')
    if (!userId) return

    const response = await fetch(`/api/user/${userId}`, {
      headers: { 'X-User-Id': userId }
    })
    const result = await response.json()
    if (result.code === '200' && result.data) {
      userQuota.value = {
        memberLevel: result.data.memberLevel || 'FREE',
        dailyQuota: result.data.dailyQuota || 5,
        dailyUsed: result.data.dailyUsed || 0,
        totalUsed: result.data.totalUsed || 0
      }
    }
  } catch (error) {
    console.error('加载用户配额失败:', error)
  }
}

const loadChatHistory = async () => {
  chatHistory.value = await chatAPI.getChatHistory('chat')
  if (!currentChatId.value) {
    currentChatId.value = chatHistory.value[0]?.id || createChatId()
  }
}

const loadChat = async (chatId) => {
  currentChatId.value = chatId
  currentMessages.value = await chatAPI.getChatMessages(chatId, 'chat')
  await scrollToBottom()
}

const deleteChat = async (chatId) => {
  if (!confirm('确定删除这条聊天记录吗？')) return
  const success = await chatAPI.deleteChatHistory(chatId, 'chat')
  if (!success) {
    alert('删除失败，请重试')
    return
  }

  chatHistory.value = chatHistory.value.filter((chat) => chat.id !== chatId)
  if (currentChatId.value === chatId) {
    startNewChat()
  }
}

const sendQuickPrompt = (text) => {
  userInput.value = text
  nextTick(() => sendMessage())
}

const sendMessage = async () => {
  if (isStreaming.value) return
  const content = userInput.value.trim()
  if (!content && selectedFiles.value.length === 0) return

  const chatId = currentChatId.value || createChatId()
  currentChatId.value = chatId
  currentMessages.value.push({
    role: 'user',
    content: content || describeSelectedFiles(),
    timestamp: new Date()
  })

  const formData = new FormData()
  if (content) formData.append('prompt', content)
  selectedFiles.value.forEach((file) => formData.append('files', file))

  userInput.value = ''
  selectedFiles.value = []
  resetTextarea()
  await scrollToBottom()

  const assistantIndex = currentMessages.value.length
  currentMessages.value.push({
    role: 'assistant',
    content: '',
    timestamp: new Date(),
    isMarkdown: true
  })

  try {
    isStreaming.value = true
    const reader = await chatAPI.sendMessage(formData, chatId)
    await readStreamIntoMessage(reader, assistantIndex)
    await loadChatHistory()
    await loadUserQuota()
  } catch (error) {
    console.error('发送消息失败:', error)
    currentMessages.value[assistantIndex].content = '发送失败，请稍后重试。'
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

const readStreamIntoMessage = async (reader, messageIndex) => {
  const decoder = new TextDecoder()
  let content = ''
  while (true) {
    const { done, value } = await reader.read()
    if (done) break
    content += decoder.decode(value, { stream: true })
    currentMessages.value[messageIndex] = {
      ...currentMessages.value[messageIndex],
      content
    }
    await nextTick()
    await scrollToBottom()
  }
}

const triggerFileInput = () => fileInput.value?.click()

const handleFileUpload = (event) => {
  selectedFiles.value = Array.from(event.target.files || [])
  event.target.value = ''
}

const removeFile = (index) => {
  selectedFiles.value.splice(index, 1)
}

const describeSelectedFiles = () => {
  const count = selectedFiles.value.length
  return `已上传 ${count} 个文件，请进行分析。`
}

const adjustTextareaHeight = () => {
  const textarea = inputRef.value
  if (!textarea) return
  textarea.style.height = 'auto'
  textarea.style.height = `${Math.min(textarea.scrollHeight, 132)}px`
}

const resetTextarea = () => {
  if (inputRef.value) inputRef.value.style.height = 'auto'
}

const scrollToBottom = async () => {
  await nextTick()
  const panel = messagesRef.value
  if (panel) panel.scrollTop = panel.scrollHeight
}

onMounted(async () => {
  await Promise.all([loadUserQuota(), loadChatHistory()])
})
</script>

<style scoped lang="scss">
.ai-chat-page {
  height: 100vh;
  padding: 28px;
  box-sizing: border-box;
  display: grid;
  grid-template-columns: 330px minmax(0, 1fr);
  gap: 24px;
  background: linear-gradient(135deg, #eaf3ff 0%, #f7fbff 52%, #effaf7 100%);
  overflow: hidden;
}

.chat-sidebar,
.chat-main {
  min-height: 0;
  background: rgba(255, 255, 255, 0.94);
  border: 1px solid #dce8f6;
  border-radius: 16px;
  box-shadow: 0 18px 44px rgba(49, 91, 143, 0.12);
  overflow: hidden;
}

.chat-sidebar {
  display: flex;
  flex-direction: column;
}

.back-button {
  margin: 22px 22px 0;
  height: 44px;
  border: 1px solid #cfe0f5;
  border-radius: 10px;
  background: #f6faff;
  color: #2563eb;
  font-weight: 700;
  cursor: pointer;
}

.sidebar-title-row {
  display: flex;
  justify-content: space-between;
  align-items: flex-end;
  gap: 14px;
  padding: 28px 22px 22px;
  background: linear-gradient(180deg, #eff7ff 0%, #ffffff 100%);

  h1 {
    margin: 4px 0 0;
    color: #1d4ed8;
    font-size: 26px;
  }
}

.eyebrow {
  margin: 0;
  color: #7c8da6;
  font-size: 12px;
  font-weight: 700;
  text-transform: uppercase;
}

.new-button {
  height: 44px;
  padding: 0 14px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  border: 0;
  border-radius: 10px;
  background: #2563eb;
  color: #fff;
  font-weight: 700;
  cursor: pointer;

  svg {
    width: 18px;
    height: 18px;
  }
}

.search-box {
  margin: 18px 22px;
  height: 48px;
  display: flex;
  align-items: center;
  gap: 10px;
  padding: 0 14px;
  border: 1px solid #d8e5f6;
  border-radius: 12px;
  background: #fff;

  svg {
    width: 20px;
    height: 20px;
    color: #3b82f6;
  }

  input {
    width: 100%;
    border: 0;
    outline: 0;
    font-size: 14px;
    color: #1f2937;
  }
}

.history-list {
  flex: 1;
  min-height: 0;
  padding: 4px 14px 22px;
  overflow-y: auto;
}

.history-item {
  width: 100%;
  min-height: 72px;
  margin: 10px 0;
  padding: 14px;
  display: grid;
  grid-template-columns: 22px minmax(0, 1fr) 22px;
  align-items: center;
  gap: 12px;
  border: 1px solid #e3ebf5;
  border-radius: 12px;
  background: #fff;
  color: #253047;
  text-align: left;
  cursor: pointer;

  &.active {
    border-color: #7fb4ff;
    background: #eef6ff;
    box-shadow: inset 3px 0 0 #2563eb;
  }

  svg {
    width: 22px;
    height: 22px;
    color: #2563eb;
  }

  .delete-icon {
    opacity: 0;
    color: #94a3b8;
  }

  &:hover .delete-icon {
    opacity: 1;
  }
}

.history-text {
  min-width: 0;

  strong,
  small {
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  small {
    margin-top: 4px;
    color: #7c8da6;
    font-size: 12px;
  }
}

.empty-history {
  padding: 48px 0;
  color: #94a3b8;
  text-align: center;
}

.chat-main {
  display: flex;
  flex-direction: column;
}

.message-panel {
  flex: 1 1 auto;
  min-height: 0;
  padding: 24px 32px;
  overflow-y: auto;
}

.welcome-panel {
  max-width: 760px;
  margin: 70px auto 0;
  padding: 28px;
  border: 1px solid #dbe8f7;
  border-radius: 14px;
  background: linear-gradient(135deg, #f8fbff, #ffffff);

  h2 {
    margin: 4px 0 22px;
    font-size: 24px;
    color: #1f2937;
  }
}

.welcome-kicker {
  margin: 0;
  color: #2563eb;
  font-weight: 700;
}

.prompt-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 12px;

  button {
    min-height: 46px;
    padding: 10px 14px;
    border: 1px solid #cfe0f5;
    border-radius: 10px;
    background: #fff;
    color: #1d4ed8;
    font-weight: 700;
    cursor: pointer;
  }
}

.composer {
  flex: 0 0 auto;
  padding: 18px 32px 28px;
  border-top: 1px solid #e8eef7;
  background: #fff;
}

.selected-files {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  margin-bottom: 12px;
}

.selected-file {
  max-width: 280px;
  height: 32px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  padding: 0 8px;
  border: 1px solid #d8e5f6;
  border-radius: 8px;
  color: #334155;
  background: #f8fbff;

  span {
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  svg {
    width: 16px;
    height: 16px;
  }

  button {
    display: inline-flex;
    border: 0;
    background: transparent;
    color: #64748b;
    cursor: pointer;
  }
}

.composer-row {
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr) 48px;
  gap: 12px;
  align-items: end;
  padding: 12px;
  border: 1px solid #cfe0f5;
  border-radius: 14px;
  background: #fbfdff;
  box-shadow: 0 8px 22px rgba(37, 99, 235, 0.08);
}

.hidden-file {
  display: none;
}

.icon-button,
.send-button {
  width: 48px;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 0;
  border-radius: 10px;
  cursor: pointer;

  svg {
    width: 22px;
    height: 22px;
  }

  &:disabled {
    opacity: 0.45;
    cursor: not-allowed;
  }
}

.icon-button {
  background: #eef5ff;
  color: #2563eb;
}

.send-button {
  background: #2563eb;
  color: #fff;
}

textarea {
  min-height: 48px;
  max-height: 132px;
  padding: 13px 4px;
  box-sizing: border-box;
  border: 0;
  outline: 0;
  resize: none;
  color: #1f2937;
  background: transparent;
  font: inherit;
  line-height: 1.5;
}

@media (max-width: 900px) {
  .ai-chat-page {
    grid-template-columns: 1fr;
    padding: 0;
  }

  .chat-sidebar {
    display: none;
  }

  .chat-main {
    border: 0;
    border-radius: 0;
  }

  .prompt-grid {
    grid-template-columns: 1fr;
  }
}
</style>
