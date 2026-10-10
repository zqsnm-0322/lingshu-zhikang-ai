<template>
  <div class="pdf-analysis-page">
    <aside class="pdf-sidebar">
      <button class="back-button" type="button" @click="goHome">← 返回主页</button>

      <div class="sidebar-heading">
        <DocumentTextIcon />
        <div>
          <p>Medical PDF</p>
          <h1>病历分析</h1>
        </div>
      </div>

      <div class="history-toolbar">
        <span>历史记录</span>
        <button type="button" @click="startNewChat">
          <PlusIcon />
          新聊天
        </button>
      </div>

      <label class="search-box">
        <MagnifyingGlassIcon />
        <input v-model="historySearchKeyword" type="text" placeholder="搜索病历记录..." />
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
          <DocumentTextIcon class="history-icon" />
          <span class="history-text">
            <strong>{{ chat.title || 'PDF 对话' }}</strong>
            <small>{{ chat.preview || '暂无消息' }}</small>
          </span>
          <TrashIcon
            class="delete-icon"
            :class="{ deleting: isDeleting === chat.id }"
            @click.stop="deleteChat(chat.id)"
          />
        </button>

        <div v-if="filteredChatHistory.length === 0" class="empty-history">
          暂无病历分析记录
        </div>
      </div>
    </aside>

    <main class="pdf-main">
      <section v-if="!hasPdf" class="upload-welcome">
        <div class="welcome-copy">
          <p>智能病历分析</p>
          <h2>上传 PDF，获取专业解读和健康建议</h2>
          <span>支持拖拽上传；上传后会自动分析，也可以继续追问具体指标。</span>
        </div>

        <div
          class="drop-zone"
          :class="{ dragging: isDragging, uploading: isUploading }"
          @dragover.prevent="handleDragOver"
          @dragleave.prevent="handleDragLeave"
          @drop.prevent="handleDrop"
        >
          <input
            ref="fileInput"
            class="file-input"
            type="file"
            accept="application/pdf,.pdf"
            :disabled="isUploading"
            @change="handleFileUpload"
          />

          <div v-if="isUploading" class="upload-state">
            <div class="spinner"></div>
            <strong>正在上传文件...</strong>
            <span>{{ uploadingFileName }}</span>
          </div>

          <template v-else>
            <DocumentArrowUpIcon class="upload-icon" />
            <strong>将病历 PDF 拖到这里</strong>
            <span>或点击下方按钮选择文件</span>
            <button class="upload-button" type="button" @click="triggerFileInput">
              <ArrowUpTrayIcon />
              上传 PDF
            </button>
          </template>
        </div>
      </section>

      <section v-else class="analysis-workspace">
        <PDFViewer :file="pdfFile" :fileName="currentPdfName" />

        <div class="chat-panel">
          <header class="chat-header">
            <div>
              <p>当前病历</p>
              <h2>{{ currentPdfName }}</h2>
            </div>
            <button type="button" :disabled="isUploading || isStreaming" @click="triggerFileInput">
              <ArrowPathIcon />
              更换 PDF
            </button>
          </header>

          <div ref="messagesRef" class="messages">
            <div v-if="currentMessages.length === 0 && !isStreaming" class="empty-messages">
              <ChatBubbleLeftRightIcon />
              <strong>PDF 已就绪</strong>
              <span>输入问题，例如“请解读这份病历”或“哪些指标需要注意？”</span>
            </div>

            <ChatMessage
              v-for="(message, index) in currentMessages"
              :key="index"
              :message="message"
              :is-stream="isStreaming && index === currentMessages.length - 1"
            />
          </div>

          <div class="quick-prompts">
            <button
              v-for="prompt in quickPrompts"
              :key="prompt"
              type="button"
              :disabled="isStreaming"
              @click="sendQuickPrompt(prompt)"
            >
              {{ prompt }}
            </button>
          </div>

          <form class="composer" @submit.prevent="sendMessage">
            <textarea
              ref="inputRef"
              v-model="userInput"
              rows="1"
              placeholder="输入关于这份病历的问题..."
              :disabled="isStreaming"
              @input="adjustTextareaHeight"
              @keydown.enter.prevent="sendMessage"
            ></textarea>
            <button
              type="submit"
              :disabled="isStreaming || !userInput.trim() || !currentChatId"
            >
              <PaperAirplaneIcon />
            </button>
          </form>
        </div>
      </section>
    </main>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowPathIcon,
  ArrowUpTrayIcon,
  ChatBubbleLeftRightIcon,
  DocumentArrowUpIcon,
  DocumentTextIcon,
  MagnifyingGlassIcon,
  PaperAirplaneIcon,
  PlusIcon,
  TrashIcon
} from '@heroicons/vue/24/outline'
import ChatMessage from '../components/ChatMessage.vue'
import PDFViewer from '../components/PDFViewer.vue'
import { chatAPI } from '../services/api'
import { recordFeatureUsage } from '../utils/featureStats'

const router = useRouter()
const fileInput = ref(null)
const inputRef = ref(null)
const messagesRef = ref(null)

const userInput = ref('')
const currentChatId = ref(null)
const currentMessages = ref([])
const chatHistory = ref([])
const historySearchKeyword = ref('')
const pdfFile = ref(null)
const currentPdfName = ref('')
const uploadingFileName = ref('')
const isDragging = ref(false)
const isUploading = ref(false)
const isStreaming = ref(false)
const isDeleting = ref(null)

const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '').replace(/\/$/, '')

const quickPrompts = [
  '请解读这份病历',
  '有哪些异常指标？',
  '给出健康饮食建议',
  '需要复查哪些项目？'
]

const hasPdf = computed(() => Boolean(pdfFile.value && currentPdfName.value))

const filteredChatHistory = computed(() => {
  const keyword = historySearchKeyword.value.trim().toLowerCase()
  if (!keyword) return chatHistory.value
  return chatHistory.value.filter((chat) =>
    `${chat.title || ''}${chat.preview || ''}${chat.id || ''}`.toLowerCase().includes(keyword)
  )
})

const goHome = () => router.push('/')
const createPdfChatId = () => `pdf_${Date.now()}`
const apiUrl = (path) => `${API_BASE_URL}${path}`

const requestHeaders = () => {
  const userId = localStorage.getItem('userId')
  return userId ? { 'X-User-Id': userId } : {}
}

const startNewChat = () => {
  currentChatId.value = null
  currentMessages.value = []
  pdfFile.value = null
  currentPdfName.value = ''
  uploadingFileName.value = ''
  userInput.value = ''
  isDragging.value = false
  resetTextarea()
}

const loadChatHistory = async ({ loadFirst = false } = {}) => {
  chatHistory.value = await chatAPI.getChatHistory('pdf')
  if (loadFirst && chatHistory.value.length > 0) {
    await loadChat(chatHistory.value[0].id)
  }
}

const loadChat = async (chatId) => {
  if (!chatId || isStreaming.value || isUploading.value) return

  currentChatId.value = chatId
  currentMessages.value = []
  pdfFile.value = null
  currentPdfName.value = ''

  try {
    const messagesPromise = chatAPI.getChatMessages(chatId, 'pdf')
    await loadPdfFile(chatId)
    const messages = await messagesPromise

    currentMessages.value = messages.map((message) => ({
      ...message,
      isMarkdown: message.role === 'assistant'
    }))
    await scrollToBottom()
  } catch (error) {
    console.error('加载 PDF 对话失败:', error)
    currentMessages.value = [
      {
        role: 'assistant',
        content: '加载病历记录失败，请稍后重试。',
        timestamp: new Date(),
        isMarkdown: true
      }
    ]
  }
}

const loadPdfFile = async (chatId) => {
  const response = await fetch(apiUrl(`/ai/pdf/file/${chatId}`), {
    headers: requestHeaders()
  })
  if (!response.ok) throw new Error(`获取 PDF 失败: ${response.status}`)

  const filename = getFilenameFromResponse(response) || `病历_${chatId}.pdf`
  const blob = await response.blob()
  currentPdfName.value = filename
  pdfFile.value = new File([blob], filename, { type: 'application/pdf' })
  updateHistoryTitle(chatId, filename)
}

const getFilenameFromResponse = (response) => {
  const disposition = response.headers.get('content-disposition')
  if (!disposition) return ''

  const utf8Match = disposition.match(/filename\*=UTF-8''([^;]+)/i)
  if (utf8Match?.[1]) return decodeURIComponent(utf8Match[1])

  const normalMatch = disposition.match(/filename="?([^";]+)"?/i)
  if (normalMatch?.[1]) return decodeURIComponent(normalMatch[1])

  return ''
}

const updateHistoryTitle = (chatId, title) => {
  const item = chatHistory.value.find((chat) => chat.id === chatId)
  if (item) item.title = title
}

const handleDragOver = () => {
  isDragging.value = true
}

const handleDragLeave = () => {
  isDragging.value = false
}

const handleDrop = async (event) => {
  isDragging.value = false
  const file = event.dataTransfer?.files?.[0]
  if (file) await uploadPdf(file)
}

const handleFileUpload = async (event) => {
  const file = event.target.files?.[0]
  if (file) await uploadPdf(file)
  event.target.value = ''
}

const triggerFileInput = () => {
  fileInput.value?.click()
}

const uploadPdf = async (file) => {
  if (isUploading.value || isStreaming.value) return
  if (!isPdfFile(file)) {
    alert('请上传 PDF 文件')
    return
  }

  const uploadChatId = currentChatId.value || createPdfChatId()
  const formData = new FormData()
  formData.append('file', file)

  try {
    isUploading.value = true
    uploadingFileName.value = file.name

    const response = await fetch(apiUrl(`/ai/pdf/upload/${uploadChatId}`), {
      method: 'POST',
      headers: requestHeaders(),
      body: formData
    })
    if (!response.ok) throw new Error(`上传失败: ${response.status}`)

    const result = await response.json().catch(() => ({}))
    currentChatId.value = result.chatId || uploadChatId
    currentPdfName.value = file.name
    pdfFile.value = file
    currentMessages.value = []
    upsertHistory({
      id: currentChatId.value,
      title: file.name,
      preview: '已上传 PDF，等待分析',
      messageCount: 0,
      timestamp: Date.now()
    })

    await autoAnalyzeMedicalRecord()
    await loadChatHistory()
  } catch (error) {
    console.error('上传 PDF 失败:', error)
    alert('文件上传失败，请重试')
  } finally {
    isUploading.value = false
    uploadingFileName.value = ''
  }
}

const isPdfFile = (file) => {
  return file.type === 'application/pdf' || file.name.toLowerCase().endsWith('.pdf')
}

const upsertHistory = (chat) => {
  chatHistory.value = [chat, ...chatHistory.value.filter((item) => item.id !== chat.id)]
}

const autoAnalyzeMedicalRecord = async () => {
  if (!currentChatId.value) return

  const assistantIndex = currentMessages.value.length
  currentMessages.value.push({
    role: 'assistant',
    content: '正在分析病历内容，请稍候...',
    timestamp: new Date(),
    isMarkdown: true
  })
  await scrollToBottom()

  try {
    isStreaming.value = true
    await new Promise((resolve) => setTimeout(resolve, 800))
    const reader = await chatAPI.autoAnalyzeMedicalRecord(currentChatId.value)
    await readStreamIntoMessage(reader, assistantIndex)
  } catch (error) {
    console.error('自动分析病历失败:', error)
    currentMessages.value[assistantIndex] = {
      role: 'assistant',
      content: `自动分析失败：${error.message || '请稍后重试'}。\n\n你也可以直接输入“请分析这份病历”进行手动分析。`,
      timestamp: new Date(),
      isMarkdown: true
    }
  } finally {
    isStreaming.value = false
    await scrollToBottom()
  }
}

const sendQuickPrompt = (prompt) => {
  userInput.value = prompt
  nextTick(() => sendMessage())
}

const sendMessage = async () => {
  const prompt = userInput.value.trim()
  if (!prompt || !currentChatId.value || isStreaming.value) return

  currentMessages.value.push({
    role: 'user',
    content: prompt,
    timestamp: new Date()
  })
  userInput.value = ''
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
    const reader = await chatAPI.sendPdfMessage(prompt, currentChatId.value)
    await readStreamIntoMessage(reader, assistantIndex)
    await loadChatHistory()
  } catch (error) {
    console.error('发送 PDF 问答失败:', error)
    currentMessages.value[assistantIndex] = {
      role: 'assistant',
      content: '发送失败，请确认 PDF 已上传并稍后重试。',
      timestamp: new Date(),
      isMarkdown: true
    }
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

const deleteChat = async (chatId) => {
  if (!confirm('确定删除这条 PDF 对话记录吗？')) return

  try {
    isDeleting.value = chatId
    const success = await chatAPI.deleteChatHistory(chatId, 'pdf')
    if (!success) {
      alert('删除失败，请重试')
      return
    }

    chatHistory.value = chatHistory.value.filter((chat) => chat.id !== chatId)
    if (currentChatId.value === chatId) {
      startNewChat()
    }
  } catch (error) {
    console.error('删除 PDF 对话失败:', error)
    alert('删除失败，请重试')
  } finally {
    isDeleting.value = null
  }
}

const loadHealthReportPdf = async (reportInfo) => {
  if (!reportInfo?.fileId) {
    await loadChatHistory({ loadFirst: true })
    return
  }

  try {
    isUploading.value = true
    uploadingFileName.value = reportInfo.fileName || '体检报告.pdf'

    const response = await fetch(apiUrl(`/api/health-report/download/${reportInfo.fileId}`), {
      headers: requestHeaders()
    })
    if (!response.ok) throw new Error('获取体检报告失败')

    const blob = await response.blob()
    const filename = reportInfo.fileName || '体检报告.pdf'
    const file = new File([blob], filename, { type: 'application/pdf' })
    currentChatId.value = `health-report-${reportInfo.reportId || Date.now()}`
    await uploadPdf(file)
  } catch (error) {
    console.error('加载体检报告失败:', error)
    alert('加载体检报告失败，请重试')
    await loadChatHistory({ loadFirst: true })
  } finally {
    isUploading.value = false
    uploadingFileName.value = ''
  }
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
  const healthReportData = sessionStorage.getItem('healthReportToAnalyze')
  if (healthReportData) {
    sessionStorage.removeItem('healthReportToAnalyze')
    await loadHealthReportPdf(JSON.parse(healthReportData))
  } else {
    await loadChatHistory({ loadFirst: true })
  }

  recordFeatureUsage('ChatPDF')
})
</script>

<style scoped lang="scss">
.pdf-analysis-page {
  height: 100vh;
  padding: 28px;
  box-sizing: border-box;
  display: grid;
  grid-template-columns: 330px minmax(0, 1fr);
  gap: 24px;
  overflow: hidden;
  background: linear-gradient(135deg, #e8f7ff 0%, #f8fcff 48%, #eefbf8 100%);
}

.pdf-sidebar,
.pdf-main {
  min-height: 0;
  border: 1px solid #d7eefb;
  border-radius: 18px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 44px rgba(31, 130, 179, 0.13);
  overflow: hidden;
}

.pdf-sidebar {
  display: flex;
  flex-direction: column;
}

.back-button {
  height: 44px;
  margin: 22px 22px 0;
  border: 1px solid #bfe8fb;
  border-radius: 10px;
  background: #effaff;
  color: #18a8e6;
  font-weight: 700;
  cursor: pointer;
}

.sidebar-heading {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 30px 22px;
  background: linear-gradient(180deg, #eafbff 0%, #f9fdff 100%);

  > svg {
    width: 36px;
    height: 36px;
    color: #38bdf8;
  }

  p {
    margin: 0;
    color: #7aa8bb;
    font-size: 12px;
    font-weight: 800;
    text-transform: uppercase;
  }

  h1 {
    margin: 4px 0 0;
    color: #0ea5e9;
    font-size: 28px;
  }
}

.history-toolbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 20px 22px 12px;

  span {
    color: #0ea5e9;
    font-weight: 800;
  }

  button {
    height: 42px;
    padding: 0 14px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border: 0;
    border-radius: 10px;
    background: #38bdf8;
    color: #fff;
    font-weight: 800;
    cursor: pointer;

    svg {
      width: 18px;
      height: 18px;
    }
  }
}

.search-box {
  height: 48px;
  margin: 8px 22px 14px;
  padding: 0 14px;
  display: flex;
  align-items: center;
  gap: 10px;
  border: 1px solid #d5eefb;
  border-radius: 12px;
  background: #fff;

  svg {
    width: 20px;
    height: 20px;
    color: #38bdf8;
  }

  input {
    width: 100%;
    border: 0;
    outline: 0;
    color: #1f2937;
    font-size: 14px;
  }
}

.history-list {
  flex: 1;
  min-height: 0;
  padding: 2px 14px 22px;
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
  border: 1px solid #e0edf6;
  border-radius: 12px;
  background: #fff;
  color: #243047;
  text-align: left;
  cursor: pointer;

  &.active {
    border-color: #7dd3fc;
    background: #ecfbff;
    box-shadow: inset 3px 0 0 #38bdf8;
  }
}

.history-icon,
.delete-icon {
  width: 22px;
  height: 22px;
}

.history-icon {
  color: #38bdf8;
}

.delete-icon {
  color: #94a3b8;
  opacity: 0;
  transition: 0.2s;

  &.deleting {
    opacity: 0.5;
  }
}

.history-item:hover .delete-icon {
  opacity: 1;
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

.pdf-main {
  display: flex;
  min-width: 0;
}

.upload-welcome {
  flex: 1;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 34px;
  padding: 42px;
}

.welcome-copy {
  text-align: center;

  p {
    margin: 0 0 10px;
    color: #0ea5e9;
    font-weight: 800;
  }

  h2 {
    margin: 0;
    color: #202938;
    font-size: 42px;
    line-height: 1.2;
  }

  span {
    display: block;
    margin-top: 14px;
    color: #6b7280;
  }
}

.drop-zone {
  width: min(720px, 100%);
  min-height: 330px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 12px;
  border: 2px dashed #aee7fb;
  border-radius: 22px;
  background: #fbfeff;
  box-shadow: 0 22px 48px rgba(14, 165, 233, 0.1);
  transition: 0.2s;

  &.dragging,
  &.uploading {
    border-color: #38bdf8;
    background: #effbff;
    transform: translateY(-2px);
  }

  strong {
    color: #344054;
    font-size: 20px;
  }

  span {
    color: #7c8da6;
  }
}

.file-input {
  display: none;
}

.upload-icon {
  width: 82px;
  height: 82px;
  color: #38bdf8;
}

.upload-button {
  height: 52px;
  margin-top: 12px;
  padding: 0 28px;
  display: inline-flex;
  align-items: center;
  gap: 10px;
  border: 0;
  border-radius: 12px;
  background: #38bdf8;
  color: #fff;
  font-weight: 800;
  cursor: pointer;

  svg {
    width: 22px;
    height: 22px;
  }
}

.upload-state {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 12px;
}

.spinner {
  width: 46px;
  height: 46px;
  border: 4px solid #dff5ff;
  border-left-color: #38bdf8;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

.analysis-workspace {
  flex: 1;
  min-width: 0;
  min-height: 0;
  display: grid;
  grid-template-columns: minmax(0, 1fr) minmax(390px, 0.85fr);
  background: #fff;

  :deep(.pdf-view) {
    min-width: 0;
    min-height: 0;
  }
}

.chat-panel {
  min-width: 0;
  min-height: 0;
  display: flex;
  flex-direction: column;
  border-left: 1px solid #e5edf5;
  background: #fbfdff;
}

.chat-header {
  min-height: 76px;
  padding: 14px 20px;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 14px;
  border-bottom: 1px solid #e5edf5;
  background: #fff;

  p {
    margin: 0;
    color: #7c8da6;
    font-size: 12px;
    font-weight: 800;
  }

  h2 {
    max-width: 360px;
    margin: 4px 0 0;
    overflow: hidden;
    color: #1f2937;
    font-size: 16px;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  button {
    height: 38px;
    padding: 0 12px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border: 1px solid #cfefff;
    border-radius: 10px;
    background: #effaff;
    color: #0ea5e9;
    font-weight: 800;
    cursor: pointer;

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }

    svg {
      width: 18px;
      height: 18px;
    }
  }
}

.messages {
  flex: 1;
  min-height: 0;
  padding: 20px;
  overflow-y: auto;
}

.empty-messages {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  color: #7c8da6;
  text-align: center;

  svg {
    width: 46px;
    height: 46px;
    color: #38bdf8;
  }

  strong {
    color: #334155;
  }
}

.quick-prompts {
  padding: 0 20px 12px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;

  button {
    height: 32px;
    padding: 0 12px;
    border: 1px solid #d5eefb;
    border-radius: 999px;
    background: #fff;
    color: #0ea5e9;
    cursor: pointer;

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}

.composer {
  padding: 16px 20px 20px;
  display: grid;
  grid-template-columns: minmax(0, 1fr) 48px;
  gap: 12px;
  align-items: end;
  border-top: 1px solid #e5edf5;
  background: #fff;

  textarea {
    min-height: 48px;
    max-height: 132px;
    padding: 13px 14px;
    box-sizing: border-box;
    border: 1px solid #cfefff;
    border-radius: 12px;
    outline: 0;
    resize: none;
    color: #1f2937;
    background: #fbfeff;
    font: inherit;
    line-height: 1.5;

    &:focus {
      border-color: #38bdf8;
      box-shadow: 0 0 0 3px rgba(56, 189, 248, 0.14);
    }

    &:disabled {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }

  button {
    width: 48px;
    height: 48px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border: 0;
    border-radius: 12px;
    background: #38bdf8;
    color: #fff;
    cursor: pointer;

    &:disabled {
      background: #cbd5e1;
      cursor: not-allowed;
    }

    svg {
      width: 22px;
      height: 22px;
    }
  }
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1080px) {
  .analysis-workspace {
    grid-template-columns: 1fr;

    :deep(.pdf-view) {
      min-height: 42vh;
      border-right: 0;
      border-bottom: 1px solid #e5edf5;
    }
  }

  .chat-panel {
    min-height: 42vh;
    border-left: 0;
  }
}

@media (max-width: 860px) {
  .pdf-analysis-page {
    grid-template-columns: 1fr;
    padding: 0;
  }

  .pdf-sidebar {
    display: none;
  }

  .pdf-main {
    border: 0;
    border-radius: 0;
  }

  .welcome-copy h2 {
    font-size: 32px;
  }
}
</style>
