<template>
  <DashboardLayout title="用药提醒" subtitle="管理今日用药计划和全部药品">
    <!-- 时钟提醒加载动画 -->

    <div class="medicine-reminder-container">
      <PageHeader
        title="用药提醒"
        subtitle="记录用药计划，标记今日完成状态，并集中查看全部药品。"
      >
        <template #actions>
          <button class="zq-text-button" type="button" @click="$router.back()">
            <ArrowLeftIcon />
            返回
          </button>
          <button class="zq-text-button zq-primary-button" type="button" @click="openAddDialog">
            <PlusIcon />
            添加
          </button>
        </template>
      </PageHeader>

      <!-- 统计卡片 -->
      <section class="zq-grid zq-grid-3">
        <StatCard label="今日提醒" :value="todayReminders" :icon="ClipboardDocumentCheckIcon" tone="blue" />
        <StatCard label="药品总数" :value="totalMedicines" :icon="BeakerIcon" tone="cyan" />
        <StatCard label="完成率" :value="`${completionRate}%`" :icon="CheckCircleIcon" tone="green" />
      </section>

      <!-- 今日用药列表 -->
      <section class="today-section zq-card">
        <div class="section-heading">
          <h3 class="zq-section-title">今日用药</h3>
          <p class="zq-section-subtitle">点击“完成”会写入今日用药记录。</p>
        </div>
        <EmptyState
          v-if="todayList.length === 0"
          title="今日无用药计划"
          description="添加药品后，符合日期范围的提醒会显示在这里。"
          :icon="BeakerIcon"
        />
        <div
          v-for="item in todayList"
          :key="item.id"
          class="medicine-item"
          :class="{ completed: item.status === 'completed', overdue: item.status === 'overdue' }"
        >
          <div class="item-info">
            <div class="item-name">{{ item.name }}</div>
            <div class="item-details">{{ item.dosage }} | {{ item.time }}</div>
          </div>
          <button
            v-if="item.status === 'pending' || item.status === 'overdue'"
            @click="completeMedicine(item.id)"
            class="complete-btn"
            type="button"
          >
            完成
          </button>
          <span v-else class="status-text">已完成</span>
        </div>
      </section>

      <!-- 全部药品列表 -->
      <section class="all-medicines-section zq-card">
        <div class="section-heading">
          <h3 class="zq-section-title">全部药品</h3>
          <p class="zq-section-subtitle">展示当前保存在本地的全部用药计划。</p>
        </div>
        <EmptyState
          v-if="medicineList.length === 0"
          title="暂无药品计划"
          description="点击右上角添加药品，创建你的第一条提醒。"
          :icon="ClipboardDocumentCheckIcon"
        />
        <article
          v-for="item in medicineList"
          :key="item.id"
          class="medicine-card"
        >
          <div class="card-header">
            <h4>{{ item.name }}</h4>
            <span><button @click="editMedicine(item)" class="edit-btn" type="button">编辑</button><button @click="deleteMedicine(item.id)" class="delete-btn" type="button" title="删除药品">
              <TrashIcon />
            </button></span>
          </div>
          <div class="card-body">
            <p>剂量：{{ item.dosage }}</p>
            <p>频率：{{ item.frequency }}</p>
            <p>时间：{{ item.reminderTimes.join(', ') }}</p>
            <p>周期：{{ item.startDate }} 至 {{ item.endDate }}</p>
            <p v-if="item.notes" class="notes">{{ item.notes }}</p>
          </div>
        </article>
      </section>
    </div>

    <!-- 添加药品弹窗 -->
    <div v-if="showAddDialog" @click="showAddDialog = false" class="modal-mask">
      <div @click.stop class="modal-content">
        <div class="modal-header">
          <h3>添加用药计划</h3>
          <button @click="showAddDialog = false" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>药品名称</label>
            <input v-model="formData.name" class="form-input" placeholder="如：阿莫西林胶囊" />
          </div>
          <div class="form-item">
            <label>剂量</label>
            <input v-model="formData.dosage" class="form-input" placeholder="如：2粒" />
          </div>
          <div class="form-item">
            <label>服用频率</label>
            <select v-model="formData.frequency" class="form-select">
              <option>每日1次</option>
              <option>每日2次</option>
              <option>每日3次</option>
              <option>按需服用</option>
            </select>
          </div>
          <div class="form-item">
            <label>提醒时间</label>
            <input v-model="formData.time" type="time" class="form-input" />
          </div>
          <div class="form-item">
            <label>开始日期</label>
            <input v-model="formData.startDate" type="date" class="form-input" />
          </div>
          <div class="form-item">
            <label>结束日期</label>
            <input v-model="formData.endDate" type="date" class="form-input" />
          </div>
          <div class="form-item">
            <label>备注</label>
            <textarea v-model="formData.notes" rows="2" class="form-textarea" placeholder="如：饭后服用"></textarea>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="showAddDialog = false" class="btn secondary">取消</button>
          <button @click="saveMedicine" class="btn primary">保存</button>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import axios from 'axios'
import {
  ArrowLeftIcon,
  BeakerIcon,
  CheckCircleIcon,
  ClipboardDocumentCheckIcon,
  PlusIcon,
  TrashIcon
} from '@heroicons/vue/24/outline'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import StatCard from '@/components/common/StatCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import { recordFeatureUsage } from '../utils/featureStats'
import iconClock from '../views/public/时钟.png'

const isLoading = ref(true)
const todayReminders = ref(0)
const totalMedicines = ref(0)
const completionRate = ref(0)
const todayList = ref([])
const medicineList = ref([])
const showAddDialog = ref(false)
const editingId = ref(null)
const userId = Number(localStorage.getItem('userId') || 1)
const formData = ref({
  name: '',
  dosage: '',
  frequency: '每日1次',
  time: '08:00',
  startDate: '',
  endDate: '',
  notes: ''
})

const openAddDialog = () => {
  editingId.value = null
  const today = new Date().toISOString().split('T')[0]
  formData.value = { name: '', dosage: '', frequency: '每日1次', time: '08:00', startDate: today, endDate: today, notes: '' }
  showAddDialog.value = true
}

const saveMedicine = async () => {
  if (!formData.value.name || !formData.value.dosage) {
    ElMessage.warning('请填写药品名称和剂量')
    return
  }

  try {
    const payload = { ...formData.value, id: editingId.value, userId, reminderTimes: JSON.stringify([formData.value.time]), isActive: true }
    const response = await axios[editingId.value ? 'put' : 'post'](editingId.value ? '/api/medicine/update' : '/api/medicine/add', payload)
    if (response.data?.code !== 200) throw new Error(response.data?.message || '保存失败')
    ElMessage.success(editingId.value ? '修改成功' : '添加成功')
    showAddDialog.value = false
    editingId.value = null
    await loadData()
  } catch (error) { ElMessage.error(error.message || '保存失败') }
}

const completeMedicine = async (id) => {
  const today = new Date().toISOString().split('T')[0]
  const item = todayList.value.find(row => row.id === id)
  await axios.post('/api/medicine/record', { userId, reminderId: item.medicineId, recordDate: today, reminderTime: item.time, status: 'completed' })
  ElMessage.success('已标记为完成')
  await loadData()
}

const deleteMedicine = (id) => {
  ElMessageBox.confirm('确定要删除该药品吗？', '确认删除', {
    type: 'warning'
  }).then(() => {
    axios.delete(`/api/medicine/delete/${id}`).then(async () => { ElMessage.success('删除成功'); await loadData() })
  }).catch(() => {})
}

const loadData = async () => {
  try {
    const [listResponse, todayResponse] = await Promise.all([
      axios.get('/api/medicine/list', { params: { userId } }),
      axios.get('/api/medicine/today', { params: { userId } })
    ])
    medicineList.value = (listResponse.data?.data || []).map(item => ({ ...item, reminderTimes: JSON.parse(item.reminderTimes || '[]') }))
    const todayData = todayResponse.data?.data || {}
    todayList.value = todayData.todayList || []
    todayReminders.value = todayData.todayReminders || 0
    totalMedicines.value = todayData.totalMedicines || 0
    completionRate.value = todayData.completionRate || 0
  } catch (error) { ElMessage.error('用药数据加载失败') }

  // 设置默认日期
  const now = new Date()
  formData.value.startDate = now.toISOString().split('T')[0]
  const endDate = new Date(now.getTime() + 7 * 24 * 60 * 60 * 1000)
  formData.value.endDate = endDate.toISOString().split('T')[0]
}

const editMedicine = (item) => {
  editingId.value = item.id
  formData.value = { ...item, time: item.reminderTimes?.[0] || '08:00' }
  showAddDialog.value = true
}

onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 3000)
  
  loadData()
  // 记录功能使用次数
  recordFeatureUsage('用药提醒')
})
</script>

<style scoped lang="scss">
@keyframes clockRotateHour {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes clockRotateMinute {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@keyframes bellRing {
  0%, 100% { transform: rotate(0deg); }
  10%, 30%, 50%, 70%, 90% { transform: rotate(-10deg); }
  20%, 40%, 60%, 80% { transform: rotate(10deg); }
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
  background: #667eea;
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
  
  .clock-loader {
    position: relative;
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 1.5rem;
    
    .clock-face {
      width: 150px;
      height: 150px;
      border-radius: 50%;
      background: white;
      border: 8px solid rgba(255, 255, 255, 0.9);
      position: relative;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.2),
                  inset 0 0 20px rgba(0, 0, 0, 0.1);
      
      .clock-hand {
        position: absolute;
        bottom: 50%;
        left: 50%;
        transform-origin: bottom center;
        background: #333;
        border-radius: 10px;
        
        &.hour-hand {
          width: 6px;
          height: 40px;
          margin-left: -3px;
          animation: clockRotateHour 8s linear infinite;
        }
        
        &.minute-hand {
          width: 4px;
          height: 55px;
          margin-left: -2px;
          animation: clockRotateMinute 4s linear infinite;
        }
      }
      
      .clock-center {
        position: absolute;
        top: 50%;
        left: 50%;
        width: 12px;
        height: 12px;
        border-radius: 50%;
        background: #ff6b6b;
        transform: translate(-50%, -50%);
      }
    }
    
    .bell-icon {
      font-size: 60px;
      animation: bellRing 2s ease-in-out infinite;
      filter: drop-shadow(0 0 20px rgba(255, 200, 0, 0.6));
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

.medicine-reminder-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.medicine-reminder-container :deep(.zq-text-button svg) {
  width: 18px;
  height: 18px;
}

.today-section,
.all-medicines-section {
  padding: 20px;
}

.section-heading {
  margin-bottom: 16px;
}

.medicine-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  margin-bottom: 12px;
  background: var(--zq-bg-panel);

  &.completed {
    background: rgba(22, 163, 74, 0.08);
    border-color: rgba(22, 163, 74, 0.3);
  }

  &.overdue {
    background: rgba(220, 38, 38, 0.08);
    border-color: rgba(220, 38, 38, 0.3);
  }

  .item-info {
    flex: 1;

    .item-name {
      font-size: 16px;
      font-weight: 600;
      color: var(--zq-text-primary);
      margin-bottom: 4px;
    }

    .item-details {
      font-size: 13px;
      color: var(--zq-text-secondary);
    }
  }

  .complete-btn {
    padding: 8px 20px;
    background: var(--zq-color-success);
    color: white;
    border: none;
    border-radius: var(--zq-radius);
    cursor: pointer;
    font-size: 14px;
    transition: all 0.3s;

    &:hover {
      background: #15803d;
    }
  }

  .status-text {
    color: var(--zq-color-success);
    font-size: 14px;
    font-weight: 600;
  }
}

.medicine-card {
  background: var(--zq-bg-muted);
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  padding: 16px;
  margin-bottom: 12px;

  .card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    margin-bottom: 12px;

    h4 {
      font-size: 16px;
      font-weight: 600;
      color: var(--zq-text-primary);
    }

    .delete-btn {
      display: inline-flex;
      width: 36px;
      height: 36px;
      align-items: center;
      justify-content: center;
      background: rgba(220, 38, 38, 0.08);
      border: 1px solid rgba(220, 38, 38, 0.2);
      border-radius: var(--zq-radius);
      color: var(--zq-color-danger);
      cursor: pointer;

      &:hover {
        background: rgba(220, 38, 38, 0.14);
      }
    }
  }

  .card-body {
    p {
      font-size: 14px;
      color: var(--zq-text-secondary);
      margin-bottom: 4px;
    }

    .notes {
      color: #909399;
      font-style: italic;
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
  background: var(--zq-bg-panel);
  border-radius: var(--zq-radius);
  max-width: 500px;
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
    margin-bottom: 16px;

    label {
      display: block;
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 8px;
      color: #333;
    }

    .form-input,
    .form-select,
    .form-textarea {
      width: 100%;
      padding: 10px 14px;
      border: 1px solid #dcdfe6;
      border-radius: var(--zq-radius);
      font-size: 14px;

      &:focus {
        outline: none;
        border-color: var(--zq-color-primary);
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
      background: var(--zq-color-primary);
      color: white;

      &:hover {
        background: var(--zq-color-primary-hover);
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

@media (max-width: 760px) {
  .medicine-item {
    align-items: flex-start;
    flex-direction: column;
  }

  .complete-btn {
    width: 100%;
  }
}
</style>
