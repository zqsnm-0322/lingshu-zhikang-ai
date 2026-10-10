<template>
  <div class="symptom-page">
    <header class="page-header">
      <button class="back-button" type="button" @click="goBack">← 返回</button>
      <div>
        <p>AI Symptom Checker</p>
        <h1>症状自检</h1>
      </div>
    </header>

    <main class="checker-layout">
      <section class="input-panel">
        <div class="panel-title">
          <span>1</span>
          <div>
            <h2>描述您的症状</h2>
            <p>可选择常见症状，也可以直接输入具体感受。</p>
          </div>
        </div>

        <label class="free-text">
          <span>症状描述</span>
          <textarea
            v-model="form.description"
            rows="5"
            placeholder="例如：昨晚开始发热、咳嗽，喉咙痛，体温 38.2℃..."
          ></textarea>
        </label>

        <div class="symptom-groups">
          <div
            v-for="category in symptomCategories"
            :key="category.name"
            class="symptom-category"
          >
            <div class="category-title">
              <span>{{ category.icon }}</span>
              <strong>{{ category.name }}</strong>
            </div>
            <div class="symptom-list">
              <button
                v-for="symptom in category.symptoms"
                :key="symptom"
                type="button"
                :class="{ selected: selectedSymptoms.includes(symptom) }"
                @click="toggleSymptom(symptom)"
              >
                {{ symptom }}
              </button>
            </div>
          </div>
        </div>
      </section>

      <aside class="detail-panel">
        <div class="panel-title compact">
          <span>2</span>
          <div>
            <h2>补充信息</h2>
            <p>信息越完整，建议越有参考价值。</p>
          </div>
        </div>

        <div class="field-grid">
          <label>
            <span>持续时间</span>
            <select v-model="form.duration">
              <option value="">请选择</option>
              <option v-for="option in durationOptions" :key="option" :value="option">
                {{ option }}
              </option>
            </select>
          </label>

          <label>
            <span>年龄</span>
            <input v-model="form.age" type="number" min="0" max="130" placeholder="请输入年龄" />
          </label>

          <label>
            <span>性别</span>
            <select v-model="form.gender">
              <option value="">请选择</option>
              <option value="男">男</option>
              <option value="女">女</option>
              <option value="不便透露">不便透露</option>
            </select>
          </label>

          <label>
            <span>体温</span>
            <input v-model="form.temperature" type="text" placeholder="如 38.2℃" />
          </label>
        </div>

        <label class="severity-card">
          <span>严重程度：{{ form.severity }} / 5</span>
          <input v-model.number="form.severity" type="range" min="1" max="5" />
          <div>
            <small>轻微</small>
            <small>严重</small>
          </div>
        </label>

        <div v-if="selectedSymptoms.length" class="selected-card">
          <div class="selected-header">
            <strong>已选症状</strong>
            <button type="button" @click="clearSymptoms">清空</button>
          </div>
          <div class="selected-tags">
            <button
              v-for="symptom in selectedSymptoms"
              :key="symptom"
              type="button"
              @click="toggleSymptom(symptom)"
            >
              {{ symptom }} ×
            </button>
          </div>
        </div>

        <button
          class="analyze-button"
          type="button"
          :disabled="!canAnalyze || isAnalyzing"
          @click="startAnalysis"
        >
          <span v-if="isAnalyzing" class="mini-spinner"></span>
          {{ isAnalyzing ? '正在分析...' : '开始 AI 分析' }}
        </button>

        <p class="safe-tip">提示：自检结果仅供参考，不能替代医生诊断；胸痛、呼吸困难、意识异常等情况请及时就医。</p>
      </aside>
    </main>

    <section class="result-section">
      <div class="result-card">
        <div class="result-header">
          <div>
            <p>Analysis Result</p>
            <h2>分析结果</h2>
          </div>
          <div class="result-actions">
            <button type="button" :disabled="!analysisText" @click="restart">重新检查</button>
            <button type="button" @click="consultDoctor">继续问诊</button>
          </div>
        </div>

        <div v-if="!analysisText && !isAnalyzing" class="empty-result">
          <span>🩺</span>
          <strong>填写症状后开始分析</strong>
          <p>AI 会根据您选择和输入的信息生成初步建议。</p>
        </div>

        <div v-else class="advice-content">
          {{ analysisText || 'AI 正在组织分析结果，请稍候...' }}
        </div>
      </div>

      <div class="history-card">
        <div class="history-header">
          <h2>最近记录</h2>
          <button type="button" :disabled="historyRecords.length === 0" @click="clearHistory">
            清空历史
          </button>
        </div>

        <button
          v-for="record in historyRecords"
          :key="record.id"
          class="history-item"
          type="button"
          @click="viewHistory(record)"
        >
          <strong>{{ record.symptoms }}</strong>
          <span>{{ record.time }}</span>
        </button>

        <div v-if="historyRecords.length === 0" class="empty-history">暂无历史记录</div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import { chatAPI } from '../services/api'
import { recordFeatureUsage } from '../utils/featureStats'

const router = useRouter()
const selectedSymptoms = ref([])
const analysisText = ref('')
const isAnalyzing = ref(false)
const historyRecords = ref([])

const form = ref({
  description: '',
  duration: '',
  age: '',
  gender: '',
  temperature: '',
  severity: 3
})

const symptomCategories = [
  {
    name: '头部症状',
    icon: '🧠',
    symptoms: ['头痛', '头晕', '偏头痛', '记忆力下降', '视物模糊']
  },
  {
    name: '呼吸系统',
    icon: '🫁',
    symptoms: ['咳嗽', '咽喉痛', '呼吸困难', '流鼻涕', '鼻塞', '胸闷']
  },
  {
    name: '消化系统',
    icon: '🍽️',
    symptoms: ['恶心', '呕吐', '腹泻', '便秘', '胃痛', '腹胀', '食欲不振']
  },
  {
    name: '全身症状',
    icon: '🌡️',
    symptoms: ['发热', '乏力', '寒战', '出汗', '体重下降', '肌肉酸痛']
  },
  {
    name: '疼痛症状',
    icon: '⚡',
    symptoms: ['胸痛', '背痛', '关节痛', '腹痛', '腰痛', '牙痛']
  },
  {
    name: '皮肤症状',
    icon: '🧴',
    symptoms: ['皮疹', '瘙痒', '红肿', '皮肤干燥', '起水疱']
  }
]

const durationOptions = ['1天以内', '1-3天', '3-7天', '1-2周', '2周以上', '反复出现']

const canAnalyze = computed(() => {
  return selectedSymptoms.value.length > 0 || form.value.description.trim().length > 0
})

const goBack = () => {
  router.back()
}

const toggleSymptom = (symptom) => {
  if (selectedSymptoms.value.includes(symptom)) {
    selectedSymptoms.value = selectedSymptoms.value.filter((item) => item !== symptom)
  } else {
    selectedSymptoms.value.push(symptom)
  }
}

const clearSymptoms = () => {
  selectedSymptoms.value = []
}

const startAnalysis = async () => {
  if (!canAnalyze.value || isAnalyzing.value) return

  analysisText.value = ''
  isAnalyzing.value = true

  try {
    const chatId = await chatAPI.generateChatId('symptom')
    const reader = await chatAPI.sendMessage(buildPrompt(), chatId)
    analysisText.value = await readStream(reader)
    saveToHistory()
  } catch (error) {
    console.error('症状分析失败:', error)
    analysisText.value = '分析失败，请稍后重试；如症状明显加重，请及时线下就医。'
  } finally {
    isAnalyzing.value = false
  }
}

const buildPrompt = () => {
  const lines = [
    '请作为医疗健康助手，根据以下信息进行症状自检分析。',
    '要求：只提供初步参考建议，不做确诊；如果存在危险信号，请明确建议尽快就医或急诊。',
    '',
    `已选症状：${selectedSymptoms.value.length ? selectedSymptoms.value.join('、') : '未选择'}`,
    `自由描述：${form.value.description.trim() || '未填写'}`,
    `持续时间：${form.value.duration || '未填写'}`,
    `年龄：${form.value.age || '未填写'}`,
    `性别：${form.value.gender || '未填写'}`,
    `体温：${form.value.temperature || '未填写'}`,
    `严重程度：${form.value.severity}/5`,
    '',
    '请按以下结构回答：',
    '1. 紧急程度',
    '2. 可能原因',
    '3. 居家观察与处理建议',
    '4. 何时需要就医',
    '5. 建议咨询科室'
  ]
  return lines.join('\n')
}

const readStream = async (reader) => {
  const decoder = new TextDecoder()
  let result = ''

  while (true) {
    const { done, value } = await reader.read()
    if (done) break

    result += decoder.decode(value, { stream: true })
    analysisText.value = result
  }

  return result.trim()
}

const restart = () => {
  selectedSymptoms.value = []
  analysisText.value = ''
  form.value = {
    description: '',
    duration: '',
    age: '',
    gender: '',
    temperature: '',
    severity: 3
  }
}

const consultDoctor = () => {
  if (analysisText.value) {
    localStorage.setItem('symptomCheckerContext', buildPrompt())
  }
  router.push('/ai-chat')
}

const saveToHistory = () => {
  const history = JSON.parse(localStorage.getItem('symptomCheckHistory') || '[]')
  const record = {
    id: Date.now(),
    symptoms: selectedSymptoms.value.join('、') || form.value.description.slice(0, 36),
    form: { ...form.value },
    selectedSymptoms: [...selectedSymptoms.value],
    analysisText: analysisText.value,
    time: new Date().toLocaleString('zh-CN')
  }

  history.unshift(record)
  localStorage.setItem('symptomCheckHistory', JSON.stringify(history.slice(0, 20)))
  loadHistory()
}

const loadHistory = () => {
  historyRecords.value = JSON.parse(localStorage.getItem('symptomCheckHistory') || '[]').slice(0, 6)
}

const viewHistory = (record) => {
  selectedSymptoms.value = record.selectedSymptoms || []
  form.value = {
    description: '',
    duration: '',
    age: '',
    gender: '',
    temperature: '',
    severity: 3,
    ...(record.form || {})
  }
  analysisText.value = record.analysisText || ''
}

const clearHistory = () => {
  localStorage.removeItem('symptomCheckHistory')
  historyRecords.value = []
}

onMounted(() => {
  loadHistory()
  recordFeatureUsage('症状自检')
})
</script>

<style scoped lang="scss">
.symptom-page {
  min-height: 100vh;
  padding: 28px;
  box-sizing: border-box;
  background: linear-gradient(135deg, #eef7ff 0%, #f8fbff 50%, #effaf7 100%);
  color: #172033;
}

.page-header {
  display: flex;
  align-items: center;
  gap: 22px;
  margin-bottom: 24px;

  p,
  h1 {
    margin: 0;
  }

  p {
    color: #6b8cae;
    font-size: 13px;
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
  }

  h1 {
    margin-top: 4px;
    font-size: 34px;
    font-weight: 900;
  }
}

.back-button {
  height: 48px;
  padding: 0 22px;
  border: 0;
  border-radius: 14px;
  background: #fff;
  color: #172033;
  font-size: 16px;
  font-weight: 800;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.08);
  cursor: pointer;
}

.checker-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 380px;
  gap: 22px;
  align-items: start;
}

.input-panel,
.detail-panel,
.result-card,
.history-card {
  border: 1px solid #dfeaf5;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 44px rgba(49, 91, 143, 0.1);
}

.input-panel,
.detail-panel {
  padding: 26px;
}

.panel-title {
  display: flex;
  align-items: flex-start;
  gap: 14px;
  margin-bottom: 22px;

  > span {
    width: 34px;
    height: 34px;
    display: inline-flex;
    align-items: center;
    justify-content: center;
    border-radius: 12px;
    background: #2563eb;
    color: #fff;
    font-weight: 900;
  }

  h2,
  p {
    margin: 0;
  }

  h2 {
    font-size: 22px;
  }

  p {
    margin-top: 6px;
    color: #708196;
  }

  &.compact h2 {
    font-size: 20px;
  }
}

.free-text {
  display: block;
  margin-bottom: 24px;

  span {
    display: block;
    margin-bottom: 10px;
    font-weight: 800;
  }

  textarea {
    width: 100%;
    min-height: 130px;
    padding: 16px;
    box-sizing: border-box;
    border: 1px solid #d4e2f2;
    border-radius: 16px;
    outline: 0;
    resize: vertical;
    color: #172033;
    background: #fbfdff;
    font: inherit;
    line-height: 1.6;

    &:focus {
      border-color: #2563eb;
      box-shadow: 0 0 0 4px rgba(37, 99, 235, 0.1);
    }
  }
}

.symptom-groups {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 18px;
}

.symptom-category {
  padding: 18px;
  border: 1px solid #edf2f8;
  border-radius: 18px;
  background: #fff;
}

.category-title {
  display: flex;
  align-items: center;
  gap: 10px;
  margin-bottom: 14px;

  span {
    font-size: 24px;
  }
}

.symptom-list,
.selected-tags {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;

  button {
    min-height: 38px;
    padding: 0 15px;
    border: 1px solid #dbe4ee;
    border-radius: 999px;
    background: #fff;
    color: #4b5f78;
    font: inherit;
    cursor: pointer;
    transition: 0.18s ease;

    &:hover,
    &.selected {
      border-color: #2563eb;
      background: #eff6ff;
      color: #1d4ed8;
    }

    &.selected {
      box-shadow: inset 0 0 0 1px #2563eb;
      font-weight: 800;
    }
  }
}

.field-grid {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 14px;
}

.field-grid label,
.severity-card {
  span {
    display: block;
    margin-bottom: 8px;
    color: #344054;
    font-weight: 800;
  }
}

.field-grid input,
.field-grid select {
  width: 100%;
  height: 44px;
  padding: 0 12px;
  box-sizing: border-box;
  border: 1px solid #d4e2f2;
  border-radius: 12px;
  outline: 0;
  background: #fbfdff;
  color: #172033;
  font: inherit;

  &:focus {
    border-color: #2563eb;
  }
}

.severity-card,
.selected-card,
.safe-tip {
  margin-top: 18px;
}

.severity-card {
  display: block;
  padding: 16px;
  border-radius: 16px;
  background: #f7fbff;

  input {
    width: 100%;
  }

  div {
    display: flex;
    justify-content: space-between;
    color: #708196;
  }
}

.selected-card {
  padding: 16px;
  border: 1px solid #dbeafe;
  border-radius: 16px;
  background: #eff6ff;
}

.selected-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 12px;

  button {
    border: 0;
    background: transparent;
    color: #2563eb;
    font-weight: 800;
    cursor: pointer;
  }
}

.selected-tags button {
  border-color: #bfdbfe;
  color: #1d4ed8;
}

.analyze-button {
  width: 100%;
  height: 52px;
  margin-top: 20px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border: 0;
  border-radius: 14px;
  background: linear-gradient(135deg, #2563eb 0%, #22c55e 100%);
  color: #fff;
  font-size: 16px;
  font-weight: 900;
  cursor: pointer;

  &:disabled {
    opacity: 0.55;
    cursor: not-allowed;
  }
}

.mini-spinner {
  width: 18px;
  height: 18px;
  border: 3px solid rgba(255, 255, 255, 0.35);
  border-top-color: #fff;
  border-radius: 50%;
  animation: spin 0.8s linear infinite;
}

.safe-tip {
  color: #708196;
  font-size: 13px;
  line-height: 1.6;
}

.result-section {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 380px;
  gap: 22px;
  margin-top: 22px;
}

.result-card,
.history-card {
  padding: 24px;
}

.result-header,
.history-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 18px;

  p,
  h2 {
    margin: 0;
  }

  p {
    color: #6b8cae;
    font-size: 12px;
    font-weight: 800;
    text-transform: uppercase;
  }

  h2 {
    margin-top: 4px;
    font-size: 22px;
  }
}

.result-actions {
  display: flex;
  gap: 10px;

  button {
    height: 38px;
    padding: 0 14px;
    border: 1px solid #d4e2f2;
    border-radius: 10px;
    background: #fff;
    color: #2563eb;
    font-weight: 800;
    cursor: pointer;

    &:last-child {
      border-color: #2563eb;
      background: #2563eb;
      color: #fff;
    }

    &:disabled {
      opacity: 0.5;
      cursor: not-allowed;
    }
  }
}

.empty-result {
  min-height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #708196;
  text-align: center;

  span {
    font-size: 42px;
  }

  strong {
    color: #344054;
  }

  p {
    margin: 0;
  }
}

.advice-content {
  min-height: 180px;
  padding: 18px;
  border-radius: 16px;
  background: #f8fbff;
  color: #26364d;
  line-height: 1.8;
  white-space: pre-wrap;
}

.history-header {
  h2 {
    margin: 0;
  }

  button {
    border: 0;
    background: transparent;
    color: #2563eb;
    font-weight: 800;
    cursor: pointer;

    &:disabled {
      color: #94a3b8;
      cursor: not-allowed;
    }
  }
}

.history-item {
  width: 100%;
  margin-bottom: 10px;
  padding: 14px;
  display: block;
  border: 1px solid #edf2f8;
  border-radius: 14px;
  background: #fff;
  text-align: left;
  cursor: pointer;

  strong,
  span {
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  strong {
    color: #344054;
  }

  span {
    margin-top: 6px;
    color: #708196;
    font-size: 13px;
  }
}

.empty-history {
  padding: 42px 0;
  color: #94a3b8;
  text-align: center;
}

@keyframes spin {
  to {
    transform: rotate(360deg);
  }
}

@media (max-width: 1080px) {
  .checker-layout,
  .result-section {
    grid-template-columns: 1fr;
  }

  .symptom-groups {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 720px) {
  .symptom-page {
    padding: 18px;
  }

  .page-header {
    align-items: flex-start;
    flex-direction: column;
  }

  .field-grid {
    grid-template-columns: 1fr;
  }

  .result-header {
    align-items: flex-start;
    flex-direction: column;
  }
}
</style>
