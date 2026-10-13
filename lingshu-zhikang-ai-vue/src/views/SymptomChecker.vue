<template>
  <div class="symptom-page">
    <header class="checker-hero">
      <div class="hero-nav">
        <button class="back-button" type="button" @click="goBack"><span>←</span> 返回首页</button>
        <span class="hero-badge"><i></i> AI 健康评估</span>
      </div>
      <div class="hero-content">
        <div>
          <p class="hero-kicker">AI SYMPTOM CHECKER</p>
          <h1>症状自检</h1>
          <p class="hero-description">描述身体不适，AI 将结合症状与基本信息，为您提供初步健康建议。</p>
        </div>
        <div class="hero-notice"><span>!</span><p><strong>紧急情况请立即就医</strong>胸痛、呼吸困难、意识异常等症状不适合在线自检</p></div>
      </div>
      <div class="step-track">
        <div class="active"><b>01</b><span>选择并描述症状</span></div><i></i>
        <div><b>02</b><span>补充基本信息</span></div><i></i>
        <div><b>03</b><span>获取分析建议</span></div>
      </div>
    </header>

    <main class="checker-layout">
      <section class="input-panel">
        <div class="panel-title">
          <span><i class="iconify" data-icon="mdi:clipboard-text-outline"></i></span>
          <div>
            <h2>描述您的症状</h2>
            <p>选择常见症状，或用自己的语言描述不适感受</p>
          </div>
        </div>

        <label class="free-text">
          <span class="field-title">症状描述 <small>{{ form.description.length }} 字</small></span>
          <textarea
            v-model="form.description"
            rows="5"
            placeholder="例如：昨晚开始发热、咳嗽，喉咙痛，体温 38.2℃..."
          ></textarea>
        </label>

        <div class="symptom-groups">
          <div
            v-for="(category, index) in symptomCategories"
            :key="category.name"
            class="symptom-category"
            :style="{ '--category-index': index }"
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
          <span><i class="iconify" data-icon="mdi:account-heart-outline"></i></span>
          <div>
            <h2>补充信息</h2>
            <p>完善信息有助于提高分析参考价值</p>
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

        <div class="analysis-ready" :class="{ ready: canAnalyze }">
          <i class="iconify" :data-icon="canAnalyze ? 'mdi:check-circle' : 'mdi:information-outline'"></i>
          <span>{{ canAnalyze ? '已具备分析条件' : '请至少选择或描述一个症状' }}</span>
        </div>

        <button
          class="analyze-button"
          type="button"
          :disabled="!canAnalyze || isAnalyzing"
          @click="startAnalysis"
        >
          <span v-if="isAnalyzing" class="mini-spinner"></span>
          {{ isAnalyzing ? '正在分析...' : '生成 AI 健康建议' }} <span v-if="!isAnalyzing">→</span>
        </button>

        <p class="safe-tip">提示：自检结果仅供参考，不能替代医生诊断；胸痛、呼吸困难、意识异常等情况请及时就医。</p>
      </aside>
    </main>

    <section class="result-section">
      <div class="result-card">
        <div class="result-header">
          <div class="result-heading">
            <span class="result-icon"><i class="iconify" data-icon="mdi:heart-pulse"></i></span>
            <div>
              <p>AI HEALTH INSIGHT</p>
              <h2>健康分析建议</h2>
            </div>
          </div>
          <div v-if="analysisText" class="result-actions">
            <span class="result-status"><i></i> 分析完成</span>
            <button type="button" @click="restart">重新检查</button>
            <button type="button" @click="consultDoctor">继续问诊</button>
          </div>
        </div>

        <div v-if="!analysisText && !isAnalyzing" class="empty-result">
          <div class="empty-visual">
            <span class="pulse-ring"></span>
            <i class="iconify" data-icon="mdi:stethoscope"></i>
          </div>
          <div class="empty-copy">
            <span class="waiting-label">WAITING FOR INFORMATION</span>
            <strong>等待生成健康建议</strong>
            <p>完成上方症状填写并点击“生成 AI 健康建议”，分析结果将在这里呈现。</p>
          </div>
          <div class="result-preview">
            <div><i class="iconify" data-icon="mdi:alert-circle-outline"></i><span><b>紧急程度</b><small>识别危险信号</small></span></div>
            <div><i class="iconify" data-icon="mdi:clipboard-text-search-outline"></i><span><b>可能原因</b><small>梳理相关因素</small></span></div>
            <div><i class="iconify" data-icon="mdi:hospital-building"></i><span><b>就医建议</b><small>匹配建议科室</small></span></div>
          </div>
        </div>

        <div v-else-if="isAnalyzing" class="analyzing-result">
          <span class="analysis-loader"><i></i><i></i><i></i></span>
          <strong>AI 正在分析您的症状</strong>
          <p>正在综合症状、持续时间与基本信息，请稍候…</p>
        </div>

        <div v-else class="advice-content">
          <div class="advice-note"><i class="iconify" data-icon="mdi:information-outline"></i> 以下内容为 AI 生成的健康参考，不能替代专业医疗诊断。</div>
          <div class="advice-text">{{ analysisText }}</div>
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

/* Symptom checker redesign */
.symptom-page {
  --brand: #0ea5e9;
  --brand-dark: #087fab;
  --mint: #14b8a6;
  --ink: #17243a;
  --muted: #718096;
  --line: #dcebf2;
  min-height: 100vh;
  padding: 0 0 56px;
  background:
    radial-gradient(circle at 8% 5%, rgba(56,189,248,.14), transparent 27%),
    radial-gradient(circle at 92% 12%, rgba(45,212,191,.1), transparent 25%),
    linear-gradient(180deg,#f6fcff 0,#edf8fc 45%,#f8fcfd 100%);
}

.checker-hero {
  position: relative;
  padding: 24px clamp(22px,5vw,82px) 30px;
  overflow: hidden;
  border-bottom: 1px solid rgba(171,218,236,.72);
  background: rgba(255,255,255,.78);
  box-shadow: 0 12px 34px rgba(45,110,145,.06);
  backdrop-filter: blur(16px);
}
.checker-hero::after { content: ''; position: absolute; top: -120px; right: 7%; width: 310px; height: 310px; border: 58px solid rgba(56,189,248,.045); border-radius: 50%; pointer-events: none; }
.hero-nav { position: relative; z-index: 1; display: flex; align-items: center; justify-content: space-between; }
.back-button { display: inline-flex; align-items: center; gap: 9px; height: 40px; padding: 0 14px; border: 1px solid #d3e8f1; border-radius: 10px; color: #496478; background: #fff; font-size: 13px; font-weight: 700; box-shadow: 0 5px 15px rgba(45,110,145,.06); }
.back-button span { color: var(--brand); font-size: 17px; }
.back-button:hover { border-color: #7dd3fc; color: var(--brand-dark); transform: translateX(-2px); }
.hero-badge { display: inline-flex; align-items: center; gap: 8px; padding: 7px 11px; border: 1px solid #d5edf5; border-radius: 99px; color: #4c7386; background: #f7fcfe; font-size: 11px; font-weight: 700; }
.hero-badge i { width: 7px; height: 7px; border-radius: 50%; background: #22c58b; box-shadow: 0 0 0 4px rgba(34,197,139,.1); }
.hero-content { position: relative; z-index: 1; display: flex; align-items: flex-end; justify-content: space-between; gap: 32px; max-width: 1500px; margin: 24px auto 22px; }
.hero-kicker { margin: 0 0 7px; color: var(--brand-dark); font-size: 11px; font-weight: 900; letter-spacing: .16em; }
.hero-content h1 { margin: 0; color: #122239; font-size: clamp(34px,4vw,52px); line-height: 1; letter-spacing: -.045em; }
.hero-description { max-width: 650px; margin: 13px 0 0; color: #657b8d; font-size: 14px; line-height: 1.65; }
.hero-notice { display: flex; align-items: center; gap: 12px; max-width: 390px; padding: 13px 15px; border: 1px solid #fde3c1; border-radius: 13px; background: #fffbf3; }
.hero-notice>span { display: grid; flex: 0 0 30px; width: 30px; height: 30px; place-items: center; border-radius: 9px; color: #fff; background: #f59e0b; font-weight: 900; }
.hero-notice p,.hero-notice strong { display: block; margin: 0; }
.hero-notice strong { margin-bottom: 3px; color: #915617; font-size: 12px; }
.hero-notice p { color: #9a7750; font-size: 10px; line-height: 1.45; }
.step-track { position: relative; z-index: 1; display: grid; grid-template-columns: auto 1fr auto 1fr auto; align-items: center; max-width: 920px; margin: auto; }
.step-track>div { display: flex; align-items: center; gap: 8px; color: #9aaab7; font-size: 11px; white-space: nowrap; }
.step-track b { display: grid; width: 27px; height: 27px; place-items: center; border: 1px solid #d9e8ee; border-radius: 50%; background: #f7fafb; font: 700 9px Inter,sans-serif; }
.step-track .active { color: #197fa8; font-weight: 700; }
.step-track .active b { border-color: var(--brand); color: #fff; background: linear-gradient(135deg,#38bdf8,#0ea5e9); }
.step-track>i { height: 1px; margin: 0 15px; background: linear-gradient(90deg,#9bdcf5,#dceaf0); }

.checker-layout,.result-section { width: min(1500px,calc(100% - 44px)); margin-right: auto; margin-left: auto; grid-template-columns: minmax(0,1fr) 390px; gap: 18px; }
.checker-layout { margin-top: 24px; }
.input-panel,.detail-panel,.result-card,.history-card { border-color: rgba(180,218,234,.78); border-radius: 18px; box-shadow: 0 12px 34px rgba(40,105,140,.075); }
.input-panel { padding: 25px; }
.detail-panel { position: sticky; top: 18px; padding: 23px; }
.panel-title { align-items: center; gap: 12px; margin-bottom: 20px; padding-bottom: 17px; border-bottom: 1px solid #e8f1f5; }
.panel-title>span { display: grid; flex: 0 0 38px; width: 38px; height: 38px; place-items: center; border-radius: 11px; background: linear-gradient(135deg,#38bdf8,#0ea5e9); font-size: 19px; box-shadow: 0 7px 15px rgba(14,165,233,.18); }
.panel-title h2,.panel-title.compact h2 { color: #1a2a40; font-size: 19px; }
.panel-title p { margin-top: 4px; color: #8392a2; font-size: 12px; }
.free-text { margin-bottom: 20px; }
.field-title { display: flex !important; align-items: center; justify-content: space-between; color: #33475b; font-size: 13px; }
.field-title small { color: #a0adba; font-size: 10px; font-weight: 500; }
.free-text textarea { min-height: 112px; padding: 15px 17px; border-color: #dce9ef; border-radius: 12px; background: #fbfdfe; font-size: 13px; }
.free-text textarea:focus { border-color: #5fc9f4; box-shadow: 0 0 0 4px rgba(56,189,248,.1); }
.symptom-groups { grid-template-columns: repeat(3,minmax(0,1fr)); gap: 11px; }
.symptom-category { position: relative; min-height: 132px; padding: 15px; overflow: hidden; border-color: #e5eef3; border-radius: 13px; background: linear-gradient(145deg,#fff,#fbfdfe); }
.symptom-category::after { content: ''; position: absolute; top: -24px; right: -24px; width: 70px; height: 70px; border-radius: 50%; background: hsla(calc(185 + var(--category-index)*18),75%,60%,.055); }
.category-title { position: relative; z-index: 1; gap: 8px; margin-bottom: 12px; color: #304458; font-size: 13px; }
.category-title span { font-size: 20px; }
.symptom-list { position: relative; z-index: 1; gap: 7px; }
.symptom-list button { min-height: 31px; padding: 0 11px; border-color: #dfe9ee; border-radius: 8px; color: #637487; background: #fff; font-size: 11px; }
.symptom-list button:hover,.symptom-list button.selected { border-color: #38bdf8; color: #087fab; background: #ebf9ff; }
.symptom-list button.selected { box-shadow: inset 0 0 0 1px #38bdf8; }

.field-grid { gap: 12px; }
.field-grid label>span,.severity-card>span { margin-bottom: 7px; color: #526579; font-size: 11px; }
.field-grid input,.field-grid select { height: 42px; border-color: #dce9ef; border-radius: 9px; background: #fbfdfe; font-size: 12px; }
.severity-card { margin-top: 14px; padding: 14px; border: 1px solid #e3edf2; border-radius: 11px; background: #f7fbfd; }
.severity-card input { accent-color: var(--brand); }
.severity-card div { font-size: 10px; }
.selected-card { margin-top: 14px; padding: 13px; border-color: #ccecf8; border-radius: 11px; background: #f0faff; }
.selected-header { margin-bottom: 9px; font-size: 12px; }
.selected-tags { gap: 6px; }
.selected-tags button { min-height: 29px; padding: 0 9px; border-radius: 7px; font-size: 10px; }
.analysis-ready { display: flex; align-items: center; gap: 7px; margin-top: 15px; color: #8a99a8; font-size: 11px; }
.analysis-ready.ready { color: #139672; }
.analyze-button { height: 48px; margin-top: 10px; border-radius: 11px; background: linear-gradient(135deg,#0ea5e9,#14b8a6); font-size: 14px; box-shadow: 0 9px 20px rgba(14,165,233,.2); }
.safe-tip { margin: 13px 0 0; padding-top: 13px; border-top: 1px solid #edf2f5; color: #8a99a8; font-size: 10px; }

.result-section { margin-top: 18px; }
.result-card,.history-card { padding: 22px; }
.result-header,.history-header { padding-bottom: 15px; border-bottom: 1px solid #e8f1f5; }
.advice-content { border: 1px solid #e5eff4; background: #f8fcfe; }
.history-item { border-color: #e5eef3; background: #fbfdfe; }
.history-item:hover { border-color: #9edcf3; transform: translateY(-1px); }

@media (max-width: 1180px) {
  .checker-layout,.result-section { grid-template-columns: 1fr; }
  .detail-panel { position: static; }
  .symptom-groups { grid-template-columns: repeat(2,minmax(0,1fr)); }
}
@media (max-width: 720px) {
  .symptom-page { padding: 0 0 32px; }
  .checker-hero { padding: 17px 16px 22px; }
  .hero-content { align-items: flex-start; flex-direction: column; margin: 22px 0 18px; }
  .hero-notice { max-width: none; }
  .step-track { grid-template-columns: 1fr 1fr 1fr; gap: 6px; }
  .step-track>i { display: none; }
  .step-track>div { align-items: flex-start; flex-direction: column; white-space: normal; }
  .checker-layout,.result-section { width: calc(100% - 24px); }
  .input-panel,.detail-panel,.result-card,.history-card { padding: 17px; border-radius: 14px; }
  .symptom-groups,.field-grid { grid-template-columns: 1fr; }
  .symptom-category { min-height: auto; }
  .result-actions { width: 100%; }
  .result-actions button { flex: 1; }
}

/* Analysis result workspace */
.result-card { min-height: 350px; }
.result-header { min-height: 51px; margin-bottom: 0; }
.result-heading { display: flex; align-items: center; gap: 12px; }
.result-icon { display: grid; width: 39px; height: 39px; place-items: center; border: 1px solid #cdebf6; border-radius: 11px; color: #078ebd; background: linear-gradient(145deg,#f3fbff,#e8f8fd); font-size: 20px; }
.result-heading p { margin: 0 0 3px; color: #4d95b2; font-size: 9px; letter-spacing: .14em; }
.result-heading h2 { margin: 0; color: #1a2b40; font-size: 18px; }
.result-actions { align-items: center; }
.result-status { display: inline-flex; align-items: center; gap: 7px; margin-right: 4px; color: #23866f; font-size: 10px; font-weight: 700; }
.result-status i { width: 7px; height: 7px; border-radius: 50%; background: #22c58b; box-shadow: 0 0 0 4px rgba(34,197,139,.1); }
.result-actions button { border-color: #d7e8ef; color: #537083; font-size: 11px; }
.result-actions button:last-child { border-color: #0ea5e9; background: linear-gradient(135deg,#38bdf8,#0ea5e9); box-shadow: 0 6px 14px rgba(14,165,233,.17); }

.empty-result { min-height: 240px; display: grid; grid-template-columns: auto minmax(220px,1fr) minmax(350px,1.35fr); align-items: center; gap: 22px; padding: 28px clamp(10px,3vw,40px); text-align: left; }
.empty-visual { position: relative; display: grid; width: 84px; height: 84px; place-items: center; border-radius: 24px; color: #0d9dca; background: linear-gradient(145deg,#edfbff,#e7f8f5); font-size: 37px; }
.pulse-ring { position: absolute; inset: -7px; border: 1px dashed rgba(14,165,233,.3); border-radius: 28px; animation: resultPulse 2.4s ease-in-out infinite; }
.empty-copy { min-width: 0; }
.empty-copy .waiting-label { display: block; margin-bottom: 7px; color: #60a3bd; font-size: 8px; font-weight: 900; letter-spacing: .14em; }
.empty-copy strong { display: block; color: #24384d; font-size: 18px; }
.empty-copy p { max-width: 450px; margin: 8px 0 0; color: #7d8d9d; font-size: 11px; line-height: 1.65; }
.result-preview { display: grid; grid-template-columns: repeat(3,minmax(0,1fr)); gap: 8px; }
.result-preview>div { display: flex; align-items: center; gap: 9px; min-height: 65px; padding: 10px; border: 1px solid #e3eef3; border-radius: 11px; background: linear-gradient(145deg,#fff,#f8fcfe); }
.result-preview .iconify { flex: 0 0 auto; color: #0ea5e9; font-size: 19px; }
.result-preview span,.result-preview b,.result-preview small { display: block; }
.result-preview b { color: #40566a; font-size: 11px; }
.result-preview small { margin-top: 3px; color: #9aa8b4; font-size: 8px; }

.analyzing-result { min-height: 240px; display: flex; align-items: center; justify-content: center; flex-direction: column; color: #7a8c9d; text-align: center; }
.analyzing-result strong { margin-top: 17px; color: #334b60; font-size: 16px; }
.analyzing-result p { margin: 7px 0 0; font-size: 11px; }
.analysis-loader { display: flex; gap: 6px; }
.analysis-loader i { width: 9px; height: 9px; border-radius: 50%; background: #38bdf8; animation: resultBounce 1s ease-in-out infinite; }
.analysis-loader i:nth-child(2) { animation-delay: .14s; }
.analysis-loader i:nth-child(3) { animation-delay: .28s; }

.advice-content { min-height: 210px; margin-top: 20px; padding: 0; overflow: hidden; border: 1px solid #deebf1; border-radius: 13px; background: #fbfdfe; white-space: normal; }
.advice-note { display: flex; align-items: center; gap: 7px; padding: 10px 14px; border-bottom: 1px solid #e4eef3; color: #708697; background: #f1f8fb; font-size: 10px; }
.advice-note .iconify { color: #0ea5e9; font-size: 15px; }
.advice-text { padding: 18px; color: #304458; font-size: 13px; line-height: 1.85; white-space: pre-wrap; }

@keyframes resultPulse { 50% { opacity: .35; transform: scale(1.04); } }
@keyframes resultBounce { 0%,100% { transform: translateY(0); opacity: .45; } 50% { transform: translateY(-7px); opacity: 1; } }

@media (max-width: 900px) {
  .empty-result { grid-template-columns: auto 1fr; }
  .result-preview { grid-column: 1 / 3; }
}
@media (max-width: 600px) {
  .result-header { align-items: flex-start; }
  .result-actions { flex-wrap: wrap; }
  .result-status { width: 100%; }
  .empty-result { grid-template-columns: 1fr; padding: 25px 2px; text-align: center; }
  .empty-visual { margin: auto; }
  .result-preview { grid-column: auto; grid-template-columns: 1fr; width: 100%; }
  .result-preview>div { min-height: 54px; text-align: left; }
}
</style>
