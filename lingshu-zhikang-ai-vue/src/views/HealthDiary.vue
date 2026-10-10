<template>
  <div class="health-diary-container">
    <!-- 日记本翻页加载动画 -->
    
    <div class="top-nav">
      <button @click="$router.back()" class="back-btn">← 返回</button>
      <h1 class="page-title">健康日记</h1>
      <button @click="showAdd" class="add-btn">+ 新建日记</button>
    </div>

    <!-- 统计信息 -->
    <div class="stats-card">
      <div class="stat-item">
        <div class="stat-value">{{ stats.totalDays }}</div>
        <div class="stat-label">记录天数</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ stats.consecutiveDays }}</div>
        <div class="stat-label">连续天数</div>
      </div>
      <div class="stat-item">
        <div class="stat-value">{{ stats.avgMood }} {{ stats.avgMoodEmoji }}</div>
        <div class="stat-label">平均心情</div>
      </div>
    </div>

    <!-- 日记列表 -->
    <div class="diary-list">
      <div v-if="diaryList.length === 0" class="empty-state">
        还没有日记，点击右上角"新建日记"开始记录吧
      </div>
      <div
        v-for="diary in diaryList"
        :key="diary.id"
        class="diary-card"
      >
        <div class="diary-content" @click="editDiary(diary)">
          <div class="diary-header">
            <span class="diary-date">{{ diary.date }}</span>
            <span class="mood-emoji">{{ getMoodEmoji(diary.mood) }}</span>
          </div>
          <div class="diary-tags">
            <span v-for="tag in diary.tags" :key="tag" class="tag">{{ tag }}</span>
          </div>
          <div class="diary-preview">{{ diary.notes || '无备注' }}</div>
        </div>
        <button @click.stop="deleteDiary(diary.id)" class="delete-btn" title="删除">🗑️</button>
      </div>
    </div>

    <!-- 编辑弹窗 -->
    <div v-if="showEditDialog" @click="hideDialog" class="modal-mask">
      <div @click.stop class="modal-content">
        <div class="modal-header">
          <h3>{{ isEditing ? '编辑日记' : '新建日记' }}</h3>
          <button @click="hideDialog" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>日期</label>
            <input v-model="diaryForm.date" type="date" class="form-input" />
          </div>
          <div class="form-item">
            <label>心情 ({{ diaryForm.mood }})</label>
            <div class="mood-selector">
              <div
                v-for="level in moodLevels"
                :key="level.value"
                @click="diaryForm.mood = level.value"
                class="mood-item"
                :class="{ selected: diaryForm.mood === level.value }"
              >
                <span class="mood-emoji">{{ level.emoji }}</span>
                <span class="mood-text">{{ level.text }}</span>
              </div>
            </div>
          </div>
          <div class="form-item">
            <label>睡眠时长 ({{ diaryForm.sleep }}小时)</label>
            <input v-model="diaryForm.sleep" type="range" min="0" max="12" step="0.5" class="slider" />
          </div>
          <div class="form-item">
            <label>标签</label>
            <div class="tags-selector">
              <span
                v-for="tag in commonTags"
                :key="tag"
                @click="toggleTag(tag)"
                class="tag-item"
                :class="{ selected: diaryForm.tags.includes(tag) }"
              >
                {{ tag }}
              </span>
            </div>
          </div>
          <div class="form-item">
            <label>备注</label>
            <textarea v-model="diaryForm.notes" rows="4" class="form-textarea" placeholder="记录今天的感受..."></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="hideDialog" class="btn secondary">取消</button>
          <button @click="saveDiary" class="btn primary">保存</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import { recordFeatureUsage } from '../utils/featureStats'
import iconNotebook from '../views/public/笔记本.png'

const isLoading = ref(true)
const stats = ref({ totalDays: 0, consecutiveDays: 0, avgMood: 0, avgMoodEmoji: '😐' })
const diaryList = ref([])
const showEditDialog = ref(false)
const isEditing = ref(false)
const diaryForm = ref({ id: null, date: '', mood: 3, sleep: 7, tags: [], notes: '' })
const moodLevels = [
  { value: 1, emoji: '😔', text: '很差' },
  { value: 2, emoji: '😟', text: '较差' },
  { value: 3, emoji: '😐', text: '一般' },
  { value: 4, emoji: '😊', text: '较好' },
  { value: 5, emoji: '😄', text: '很好' }
]
const commonTags = ['感冒', '发烧', '头痛', '失眠', '焦虑', '疲劳', '运动', '休息']
const userId = Number(localStorage.getItem('userId') || 1)

const showAdd = () => {
  const today = new Date().toISOString().split('T')[0]
  diaryForm.value = { id: null, date: today, mood: 3, sleep: 7, tags: [], notes: '' }
  isEditing.value = false
  showEditDialog.value = true
}

const hideDialog = () => {
  showEditDialog.value = false
}

const editDiary = (diary) => {
  diaryForm.value = { ...diary, tags: Array.isArray(diary.tags) ? diary.tags : (diary.tags || '').split(',').filter(Boolean) }
  isEditing.value = true
  showEditDialog.value = true
}

const toggleTag = (tag) => {
  const index = diaryForm.value.tags.indexOf(tag)
  if (index >= 0) {
    diaryForm.value.tags.splice(index, 1)
  } else {
    diaryForm.value.tags.push(tag)
  }
}

const saveDiary = async () => {
  if (!diaryForm.value.date) {
    ElMessage.warning('请选择日期')
    return
  }

  try {
    const payload = { ...diaryForm.value, userId, tags: diaryForm.value.tags.join(',') }
    const url = isEditing.value ? '/api/health-diary/update' : '/api/health-diary/add'
    const response = await axios[isEditing.value ? 'put' : 'post'](url, payload)
    if (response.data?.code !== 200) throw new Error(response.data?.message || '保存失败')
    ElMessage.success(isEditing.value ? '修改成功' : '添加成功')
    hideDialog()
    await loadDiaries()
  } catch (error) { ElMessage.error(error.message || '保存失败') }
}

const getMoodEmoji = (mood) => {
  return moodLevels.find(l => l.value === mood)?.emoji || '😐'
}

const deleteDiary = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除这条日记吗？此操作不可恢复。', '确认删除', {
      type: 'warning',
      confirmButtonText: '确定',
      cancelButtonText: '取消'
    })
    
    await axios.delete(`/api/health-diary/delete/${id}`, { params: { userId } })
    ElMessage.success('删除成功')
    await loadDiaries()
  } catch (error) {
    // 用户取消删除，不做任何操作
  }
}

const loadDiaries = async () => {
  const response = await axios.get('/api/health-diary/list', { params: { userId } })
  const diaries = (response.data?.data || []).map(item => ({ ...item, tags: (item.tags || '').split(',').filter(Boolean) }))
  diaries.sort((a, b) => new Date(b.date) - new Date(a.date))
  diaryList.value = diaries

  // 计算统计
  const totalDays = diaries.length
  let consecutiveDays = 0
  const today = new Date()
  today.setHours(0, 0, 0, 0)

  for (let i = 0; i < diaries.length; i++) {
    const diaryDate = new Date(diaries[i].date)
    diaryDate.setHours(0, 0, 0, 0)
    const daysDiff = Math.floor((today - diaryDate) / (1000 * 60 * 60 * 24))
    if (daysDiff === consecutiveDays) consecutiveDays++
    else break
  }

  const avgMood = totalDays > 0 ? (diaries.reduce((sum, d) => sum + d.mood, 0) / totalDays).toFixed(1) : 0
  const avgMoodEmoji = getMoodEmoji(Math.round(parseFloat(avgMood)))

  stats.value = { totalDays, consecutiveDays, avgMood, avgMoodEmoji }
}

onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 3000)
  
  loadDiaries()
  // 记录功能使用次数
  recordFeatureUsage('健康日记')
})
</script>

<style scoped lang="scss">
@keyframes pageFlip {
  0%, 100% {
    transform: rotateY(0deg);
  }
  50% {
    transform: rotateY(-180deg);
  }
}

@keyframes penWrite {
  0%, 100% {
    transform: translateX(0) rotate(0deg);
  }
  50% {
    transform: translateX(20px) rotate(-10deg);
  }
}

.fade-out-leave-active {
  transition: all 0.8s ease;
}

.fade-out-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  
  .loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2.5rem;
  }
  
  .diary-loader {
    position: relative;
    
    .book {
      width: 180px;
      height: 240px;
      position: relative;
      transform-style: preserve-3d;
      perspective: 1000px;
      
      .page {
        position: absolute;
        width: 90px;
        height: 100%;
        background: white;
        border: 2px solid #ddd;
        box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
        
        &::before {
          content: '';
          position: absolute;
          top: 20px;
          left: 10px;
          right: 10px;
          height: 3px;
          background: rgba(255, 154, 158, 0.3);
        }
        
        &::after {
          content: '';
          position: absolute;
          top: 30px;
          left: 10px;
          right: 10px;
          bottom: 20px;
          background: repeating-linear-gradient(
            transparent,
            transparent 18px,
            rgba(255, 154, 158, 0.15) 18px,
            rgba(255, 154, 158, 0.15) 20px
          );
        }
        
        &.left-page {
          left: 0;
          border-radius: 10px 0 0 10px;
        }
        
        &.right-page {
          right: 0;
          border-radius: 0 10px 10px 0;
          animation: pageFlip 3s ease-in-out infinite;
          transform-origin: left center;
        }
      }
    }
    
    .pen {
      font-size: 50px;
      position: absolute;
      bottom: -60px;
      right: -20px;
      animation: penWrite 2s ease-in-out infinite;
      filter: drop-shadow(0 5px 10px rgba(0, 0, 0, 0.2));
    }
  }
  
  .loading-text {
    text-align: center;
    color: white;
    
    .loading-title {
      font-size: 2rem;
      font-weight: 700;
      margin: 0 0 0.5rem 0;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    }
    
    .loading-subtitle {
      font-size: 1.125rem;
      margin: 0;
      opacity: 0.9;
    }
  }
}

.health-diary-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  background-attachment: fixed;
  background-size: cover;
  padding: 20px;
  box-sizing: border-box;
}

.top-nav {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 20px;

  .page-title {
    color: white;
    font-size: 24px;
    font-weight: 600;
  }

  .back-btn,
  .add-btn {
    background: rgba(255, 255, 255, 0.9);
    border: none;
    border-radius: 8px;
    padding: 10px 20px;
    cursor: pointer;
    font-size: 16px;
    transition: all 0.3s;

    &:hover {
      background: white;
    }
  }
}

.stats-card {
  background: white;
  border-radius: 16px;
  padding: 20px;
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 20px;
  margin-bottom: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .stat-item {
    text-align: center;

    .stat-value {
      font-size: 28px;
      font-weight: 600;
      color: #333;
      margin-bottom: 4px;
    }

    .stat-label {
      font-size: 14px;
      color: #909399;
    }
  }
}

.diary-list {
  .empty-state {
    background: white;
    border-radius: 16px;
    padding: 60px 20px;
    text-align: center;
    color: #909399;
  }

  .diary-card {
    background: white;
    border-radius: 12px;
    padding: 16px;
    margin-bottom: 12px;
    transition: all 0.3s;
    display: flex;
    align-items: flex-start;
    gap: 12px;
    position: relative;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

      .delete-btn {
        opacity: 1;
      }
    }

    .diary-content {
      flex: 1;
      cursor: pointer;
    }

    .diary-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      .diary-date {
        font-size: 16px;
        font-weight: 600;
        color: #333;
      }

      .mood-emoji {
        font-size: 24px;
      }
    }

    .diary-tags {
      display: flex;
      gap: 8px;
      margin-bottom: 8px;

      .tag {
        padding: 4px 12px;
        background: #ecf5ff;
        color: #409eff;
        border-radius: 12px;
        font-size: 12px;
      }
    }

    .diary-preview {
      font-size: 14px;
      color: #606266;
      line-height: 1.5;
    }

    .delete-btn {
      background: transparent;
      border: none;
      font-size: 20px;
      cursor: pointer;
      padding: 4px 8px;
      border-radius: 6px;
      transition: all 0.3s;
      opacity: 0;
      color: #909399;
      flex-shrink: 0;

      &:hover {
        background: linear-gradient(135deg, #ff6b6b, #ee5a6f);
        color: white;
        transform: scale(1.1);
      }

      &:active {
        transform: scale(1.05);
      }
    }
  }
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: white;
  border-radius: 16px;
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    font-size: 18px;
    font-weight: 600;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #909399;

    &:hover {
      color: #333;
    }
  }
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;

  .form-item {
    margin-bottom: 20px;

    label {
      display: block;
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 8px;
      color: #333;
    }

    .form-input,
    .form-textarea {
      width: 100%;
      padding: 10px 14px;
      border: 1px solid #dcdfe6;
      border-radius: 8px;
      font-size: 14px;

      &:focus {
        outline: none;
        border-color: #4facfe;
      }
    }

    .slider {
      width: 100%;
    }

    .mood-selector,
    .tags-selector {
      display: flex;
      flex-wrap: wrap;
      gap: 10px;
    }

    .mood-item {
      flex: 1;
      min-width: 80px;
      padding: 12px 8px;
      border: 2px solid #e5e7eb;
      border-radius: 8px;
      text-align: center;
      cursor: pointer;
      transition: all 0.3s;

      &:hover {
        border-color: #4facfe;
      }

      &.selected {
        border-color: #4facfe;
        background: #e3f2fd;
      }

      .mood-emoji {
        display: block;
        font-size: 28px;
        margin-bottom: 4px;
      }

      .mood-text {
        font-size: 12px;
        color: #606266;
      }
    }

    .tag-item {
      padding: 8px 16px;
      border: 1px solid #e5e7eb;
      border-radius: 16px;
      cursor: pointer;
      font-size: 14px;
      transition: all 0.3s;

      &:hover {
        border-color: #4facfe;
        color: #4facfe;
      }

      &.selected {
        background: #4facfe;
        color: white;
        border-color: #4facfe;
      }
    }
  }
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 12px;

  .btn {
    flex: 1;
    padding: 12px;
    border: none;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;

    &.primary {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: white;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
      }
    }

    &.secondary {
      background: #f5f7fa;
      color: #606266;

      &:hover {
        background: #e4e7ed;
      }
    }
  }
}
/* 与“病历分析”页面统一的医疗蓝视觉 */
:global(body) {
  background: #eef9fe;
}

.health-diary-container {
  padding: 30px;
  background: linear-gradient(135deg, #e8f7ff 0%, #f8fcff 48%, #eefbf8 100%);
  color: #243047;
}

.top-nav,
.stats-card,
.diary-list .empty-state,
.diary-list .diary-card {
  border: 1px solid #d7eefb;
  background: rgba(255, 255, 255, 0.96);
  box-shadow: 0 12px 30px rgba(45, 126, 164, 0.08);
}

.top-nav {
  max-width: 1520px;
  min-height: 96px;
  margin: 0 auto 24px;
  padding: 20px 28px;
  border-radius: 24px;

  .page-title { color: #202938; font-size: 28px; }
  .back-btn { border: 1px solid #bfe8fb; color: #0ea5e9; background: #effaff; }
  .add-btn { color: #fff; background: #38bdf8; box-shadow: 0 8px 20px rgba(56, 189, 248, .22); }
  .back-btn:hover { background: #e6f8ff; }
  .add-btn:hover { background: #20afea; }
}

.stats-card {
  max-width: 1520px;
  margin: 0 auto 24px;
  padding: 28px;
  border-radius: 24px;
  box-shadow: 0 12px 30px rgba(45, 126, 164, 0.08);

  .stat-item + .stat-item { border-left: 1px solid #e0f1f9; }
  .stat-item .stat-value { color: #0ea5e9; }
  .stat-item .stat-label { color: #7aa8bb; }
}

.diary-list {
  max-width: 1520px;
  margin: 0 auto;

  .empty-state { min-height: 240px; border-radius: 24px; color: #7c8da6; }
  .diary-card { border-radius: 18px; padding: 20px; }
  .diary-card:hover { border-color: #aee7fb; box-shadow: 0 16px 34px rgba(45, 126, 164, .12); }
  .diary-card .diary-tags .tag { color: #0ea5e9; background: #effaff; }
}

.modal-content { border: 1px solid #d7eefb; border-radius: 22px; }
.modal-footer .btn.primary { background: #38bdf8; }

@media (max-width: 720px) {
  .health-diary-container { padding: 16px; }
  .top-nav { padding: 16px; }
  .top-nav .page-title { font-size: 22px; }
  .stats-card { padding: 20px 10px; }
}
</style>
