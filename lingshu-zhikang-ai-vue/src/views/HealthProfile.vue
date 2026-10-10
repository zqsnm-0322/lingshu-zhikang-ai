<template>
  <div class="health-profile-page">
    <header class="topbar">
      <button class="nav-button" type="button" @click="goBack" aria-label="返回">
        <ArrowLeftIcon />
      </button>
      <div class="topbar-title">
        <p>Health Profile</p>
        <h1>健康档案</h1>
      </div>
      <button class="nav-button" type="button" @click="exportData" aria-label="导出">
        <ArrowDownTrayIcon />
      </button>
    </header>

    <main class="profile-layout">
      <section class="main-column">
        <div class="profile-card">
          <div class="profile-hero">
            <div class="avatar">
              <img v-if="userAvatar" :src="userAvatar" alt="用户头像" />
              <span v-else>{{ userInitial }}</span>
            </div>

            <div class="user-block">
              <p>欢迎回来</p>
              <h2>{{ username }}</h2>
              <span>
                <CalendarDaysIcon />
                加入于 {{ joinDate }}
              </span>
            </div>

            <div class="score-card">
              <div class="score-ring" :style="{ '--score': healthScore }">
                <strong>{{ healthScore }}</strong>
                <span>分</span>
              </div>
              <p>健康指数</p>
            </div>
          </div>

          <div class="metric-grid">
            <button
              v-for="metric in quickMetrics"
              :key="metric.label"
              class="metric-card"
              type="button"
              @click="navigateTo(metric.route)"
            >
              <component :is="metric.icon" />
              <span>
                <strong>{{ metric.value }}</strong>
                <small>{{ metric.label }}</small>
              </span>
            </button>
          </div>
        </div>

        <section class="section-card">
          <div class="section-title">
            <div>
              <p>Data Overview</p>
              <h2>数据统计</h2>
            </div>
          </div>

          <div class="stats-grid">
            <button
              v-for="item in statistics"
              :key="item.label"
              class="stat-card"
              type="button"
              @click="navigateTo(item.route)"
            >
              <span class="stat-icon" :class="item.tone">
                <component :is="item.icon" />
              </span>
              <span class="stat-info">
                <strong>{{ item.count }}</strong>
                <small>{{ item.label }}</small>
              </span>
              <ChevronRightIcon />
            </button>
          </div>
        </section>

        <section class="section-card">
          <div class="section-title">
            <div>
              <p>Quick Access</p>
              <h2>快捷功能</h2>
            </div>
          </div>

          <div class="action-grid">
            <button
              v-for="action in quickActions"
              :key="action.label"
              class="action-card"
              type="button"
              @click="navigateTo(action.route)"
            >
              <span :class="action.tone">
                <component :is="action.icon" />
              </span>
              <strong>{{ action.label }}</strong>
              <small>{{ action.desc }}</small>
            </button>
          </div>
        </section>
      </section>

      <aside class="side-column">
        <section class="activity-card">
          <div class="side-title">
            <div>
              <p>Timeline</p>
              <h2>最近活动</h2>
            </div>
            <button type="button" :disabled="activities.length === 0" @click="showAllActivities = !showAllActivities">
              {{ showAllActivities ? '收起' : '全部' }}
            </button>
          </div>

          <div v-if="visibleActivities.length" class="timeline">
            <button
              v-for="activity in visibleActivities"
              :key="activity.id"
              class="timeline-item"
              type="button"
              @click="navigateTo(activity.route)"
            >
              <span class="timeline-dot" :class="activity.type"></span>
              <span class="timeline-icon" :class="activity.type">
                <component :is="activity.icon" />
              </span>
              <span class="timeline-content">
                <strong>{{ activity.content }}</strong>
                <small>{{ formatDate(activity.date) }}</small>
              </span>
            </button>
          </div>

          <div v-else class="empty-state">
            <ClockIcon />
            <strong>暂无活动记录</strong>
            <span>开始使用健康功能后，这里会自动汇总。</span>
          </div>
        </section>

        <section class="recommend-card">
          <div class="side-title">
            <div>
              <p>AI Suggestions</p>
              <h2>智能推荐</h2>
            </div>
            <button type="button" @click="reloadData">
              <ArrowPathIcon />
            </button>
          </div>
          <SmartRecommendation :max-recommendations="3" />
        </section>
      </aside>
    </main>

    <div class="fab-wrap">
      <button class="fab-main" type="button" @click="showMenu = !showMenu">
        <PlusIcon :class="{ open: showMenu }" />
      </button>
      <transition-group name="fab" tag="div" class="fab-menu">
        <button
          v-if="showMenu"
          v-for="item in fabMenuItems"
          :key="item.label"
          type="button"
          @click="handleFabAction(item.route)"
        >
          <component :is="item.icon" />
          {{ item.label }}
        </button>
      </transition-group>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowDownTrayIcon,
  ArrowLeftIcon,
  ArrowPathIcon,
  BeakerIcon,
  BellAlertIcon,
  BookOpenIcon,
  CalendarDaysIcon,
  ChatBubbleLeftRightIcon,
  ChevronRightIcon,
  ClipboardDocumentListIcon,
  ClockIcon,
  DocumentTextIcon,
  HeartIcon,
  PlusIcon,
  SparklesIcon,
  UserCircleIcon
} from '@heroicons/vue/24/outline'
import { useHealthDataStore } from '@/stores/healthData'
import SmartRecommendation from '@/components/SmartRecommendation.vue'

type ActivityItem = {
  id: string
  type: string
  content: string
  date: string
  route: string
  icon: unknown
}

const router = useRouter()
const healthStore = useHealthDataStore()
const showMenu = ref(false)
const showAllActivities = ref(false)
const username = ref('用户')
const userAvatar = ref('')
const joinDate = ref('-')

const featureRoutes: Record<string, string> = {
  'symptom-checker': '/symptom-checker',
  'medicine-reminder': '/medicine-reminder',
  'health-report': '/health-report',
  'health-diary': '/health-diary',
  'chronic-disease': '/chronic-disease',
  'ai-chat': '/ai-chat',
  'medicine-query': '/medicine-query',
  'chat-pdf': '/chat-pdf'
}

const featureLabels: Record<string, string> = {
  'symptom-checker': '症状自检',
  'medicine-reminder': '用药提醒',
  'health-report': '体检报告',
  'health-diary': '健康日记',
  'chronic-disease': '慢性病管理',
  'ai-chat': 'AI 聊天',
  'medicine-query': '药品查询',
  'chat-pdf': '病历分析'
}

const featureIcons: Record<string, unknown> = {
  symptom: ClipboardDocumentListIcon,
  medication: BellAlertIcon,
  report: DocumentTextIcon,
  diary: BookOpenIcon,
  chronic: HeartIcon,
  action: SparklesIcon
}

const featureStats = computed<Record<string, number>>(() => {
  try {
    return JSON.parse(localStorage.getItem('featureStats') || '{}')
  } catch {
    return {}
  }
})

const stats = computed(() => healthStore.healthStats)

const healthScore = computed(() => {
  const base = 70
  const diaryBonus = Math.min(stats.value.diaryDays * 2, 10)
  const reportBonus = Math.min(stats.value.totalReports * 3, 9)
  const medicationBonus = Math.min(stats.value.activeMedications * 2, 6)
  const symptomPenalty = Math.min(stats.value.totalSymptoms, 10)
  return Math.max(0, Math.min(100, base + diaryBonus + reportBonus + medicationBonus - symptomPenalty))
})

const userInitial = computed(() => (username.value || 'U').charAt(0).toUpperCase())

const quickMetrics = computed(() => [
  { label: '症状记录', value: stats.value.totalSymptoms, route: '/symptom-checker', icon: ClipboardDocumentListIcon },
  { label: '正在用药', value: stats.value.activeMedications, route: '/medicine-reminder', icon: BellAlertIcon },
  { label: '体检报告', value: stats.value.totalReports, route: '/health-report', icon: DocumentTextIcon },
  { label: '日记天数', value: stats.value.diaryDays, route: '/health-diary', icon: BookOpenIcon }
])

const statistics = computed(() => [
  { label: '症状自检', count: stats.value.totalSymptoms || featureStats.value['症状自检'] || 0, route: '/symptom-checker', icon: ClipboardDocumentListIcon, tone: 'red' },
  { label: '用药提醒', count: stats.value.activeMedications || featureStats.value['用药提醒'] || 0, route: '/medicine-reminder', icon: BellAlertIcon, tone: 'green' },
  { label: '慢性病管理', count: stats.value.chronicDiseasesCount || featureStats.value['慢性病管理'] || 0, route: '/chronic-disease', icon: HeartIcon, tone: 'cyan' },
  { label: '健康日记', count: stats.value.diaryDays || featureStats.value['健康日记'] || 0, route: '/health-diary', icon: BookOpenIcon, tone: 'orange' },
  { label: '体检报告', count: stats.value.totalReports || featureStats.value['体检报告'] || 0, route: '/health-report', icon: DocumentTextIcon, tone: 'blue' }
])

const quickActions = [
  { label: 'AI 聊天', desc: '继续咨询健康问题', route: '/ai-chat', icon: ChatBubbleLeftRightIcon, tone: 'blue' },
  { label: '药品查询', desc: '查询药品说明与提醒', route: '/medicine-query', icon: BeakerIcon, tone: 'green' },
  { label: '健康日记', desc: '记录今日身体状态', route: '/health-diary', icon: BookOpenIcon, tone: 'orange' },
  { label: '病历分析', desc: '上传 PDF 智能解读', route: '/chat-pdf', icon: DocumentTextIcon, tone: 'cyan' }
]

const fabMenuItems = [
  { label: '新增日记', route: '/health-diary', icon: BookOpenIcon },
  { label: '添加用药', route: '/medicine-reminder', icon: BellAlertIcon },
  { label: '记录慢病', route: '/chronic-disease', icon: HeartIcon }
]

const activities = computed<ActivityItem[]>(() => {
  const storeActivities = healthStore.recentActivities.map((activity, index) => ({
    id: `store-${index}-${activity.date}`,
    type: activity.type,
    content: activity.content,
    date: activity.date,
    route: activityRoute(activity.type),
    icon: featureIcons[activity.type] || SparklesIcon
  }))

  const actions = healthStore.userActions
    .slice(-20)
    .reverse()
    .map((action, index) => ({
      id: `action-${index}-${action.timestamp}`,
      type: 'action',
      content: `${featureLabels[action.feature] || action.feature}：${action.action}`,
      date: new Date(action.timestamp).toISOString(),
      route: featureRoutes[action.feature] || '/health-profile',
      icon: SparklesIcon
    }))

  return [...storeActivities, ...actions]
    .sort((left, right) => new Date(right.date).getTime() - new Date(left.date).getTime())
    .slice(0, 20)
})

const visibleActivities = computed(() => {
  return showAllActivities.value ? activities.value : activities.value.slice(0, 5)
})

const activityRoute = (type: string) => {
  const map: Record<string, string> = {
    symptom: '/symptom-checker',
    medication: '/medicine-reminder',
    report: '/health-report',
    diary: '/health-diary',
    chronic: '/chronic-disease'
  }
  return map[type] || '/health-profile'
}

const loadUserData = () => {
  username.value = localStorage.getItem('username') || localStorage.getItem('userName') || '用户'
  userAvatar.value = localStorage.getItem('avatarUrl') || ''
  joinDate.value = localStorage.getItem('registerDate')?.split('T')[0] || '暂无记录'
}

const reloadData = () => {
  healthStore.loadFromStorage()
  loadUserData()
}

const navigateTo = (route: string) => {
  router.push(route)
}

const goBack = () => {
  router.push('/')
}

const exportData = () => {
  const data = {
    username: username.value,
    exportDate: new Date().toISOString(),
    healthScore: healthScore.value,
    statistics: statistics.value.map(({ label, count, route }) => ({ label, count, route })),
    activities: activities.value.map(({ type, content, date, route }) => ({ type, content, date, route })),
    healthData: {
      symptoms: healthStore.symptoms,
      medications: healthStore.medications,
      reports: healthStore.reports,
      diaryEntries: healthStore.diaryEntries,
      chronicDiseases: healthStore.chronicDiseases
    }
  }

  const blob = new Blob([JSON.stringify(data, null, 2)], { type: 'application/json' })
  const url = URL.createObjectURL(blob)
  const link = document.createElement('a')
  link.href = url
  link.download = `健康档案_${username.value}_${new Date().toISOString().slice(0, 10)}.json`
  link.click()
  URL.revokeObjectURL(url)
}

const handleFabAction = (route: string) => {
  showMenu.value = false
  router.push(route)
}

const formatDate = (dateString: string) => {
  const date = new Date(dateString)
  if (Number.isNaN(date.getTime())) return '时间未知'

  const diff = Date.now() - date.getTime()
  if (diff < 60_000) return '刚刚'
  if (diff < 3_600_000) return `${Math.floor(diff / 60_000)}分钟前`
  if (diff < 86_400_000) return `${Math.floor(diff / 3_600_000)}小时前`
  return date.toLocaleDateString('zh-CN')
}

onMounted(() => {
  reloadData()
  recordFeatureUsage('健康档案')
})
</script>

<style scoped lang="scss">
.health-profile-page {
  min-height: 100vh;
  padding: 28px;
  box-sizing: border-box;
  background:
    radial-gradient(circle at top left, rgba(56, 189, 248, 0.18), transparent 34%),
    radial-gradient(circle at bottom right, rgba(45, 212, 191, 0.16), transparent 34%),
    #f4f8fc;
  color: #172033;
}

.topbar {
  height: 82px;
  margin-bottom: 26px;
  padding: 0 24px;
  display: grid;
  grid-template-columns: 56px minmax(0, 1fr) 56px;
  align-items: center;
  gap: 18px;
  border: 1px solid #e0ebf5;
  border-radius: 20px;
  background: rgba(255, 255, 255, 0.92);
  box-shadow: 0 16px 42px rgba(49, 91, 143, 0.1);
}

.topbar-title {
  text-align: center;

  p,
  h1 {
    margin: 0;
  }

  p {
    color: #7a8da6;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
  }

  h1 {
    margin-top: 4px;
    font-size: 28px;
    font-weight: 900;
  }
}

.nav-button {
  width: 56px;
  height: 48px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 0;
  border-radius: 14px;
  background: #fff;
  color: #2563eb;
  box-shadow: 0 10px 24px rgba(37, 99, 235, 0.1);
  cursor: pointer;

  svg {
    width: 22px;
    height: 22px;
  }
}

.profile-layout {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 390px;
  gap: 24px;
  align-items: start;
}

.main-column,
.side-column {
  display: flex;
  flex-direction: column;
  gap: 24px;
}

.profile-card,
.section-card,
.activity-card,
.recommend-card {
  border: 1px solid #dfeaf5;
  border-radius: 22px;
  background: rgba(255, 255, 255, 0.94);
  box-shadow: 0 18px 44px rgba(49, 91, 143, 0.1);
}

.profile-card {
  padding: 26px;
}

.profile-hero {
  display: grid;
  grid-template-columns: 120px minmax(0, 1fr) 140px;
  gap: 22px;
  align-items: center;
  padding-bottom: 24px;
  border-bottom: 1px solid #e6eef7;
}

.avatar {
  width: 112px;
  height: 112px;
  padding: 8px;
  box-sizing: border-box;
  display: flex;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background: conic-gradient(from 180deg, #38bdf8, #34d399, #bfdbfe, #38bdf8);

  img,
  span {
    width: 100%;
    height: 100%;
    border: 5px solid #fff;
    border-radius: 50%;
  }

  img {
    object-fit: cover;
  }

  span {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, #38bdf8, #34d399);
    color: #fff;
    font-size: 42px;
    font-weight: 900;
  }
}

.user-block {
  min-width: 0;

  p,
  h2,
  span {
    margin: 0;
  }

  p {
    color: #7a8da6;
    font-weight: 800;
  }

  h2 {
    margin-top: 8px;
    color: #202938;
    font-size: 34px;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  span {
    margin-top: 12px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    color: #667085;

    svg {
      width: 18px;
      height: 18px;
    }
  }
}

.score-card {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;

  p {
    margin: 0;
    color: #667085;
    font-weight: 800;
  }
}

.score-ring {
  width: 92px;
  height: 92px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border-radius: 50%;
  background:
    radial-gradient(circle at center, #fff 58%, transparent 60%),
    conic-gradient(#34d399 calc(var(--score) * 1%), #e6eef7 0);

  strong {
    color: #21c690;
    font-size: 28px;
    line-height: 1;
  }

  span {
    margin-top: 3px;
    color: #667085;
    font-size: 12px;
    font-weight: 800;
  }
}

.metric-grid {
  margin-top: 24px;
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.metric-card {
  min-height: 86px;
  padding: 16px;
  display: flex;
  align-items: center;
  gap: 14px;
  border: 1px solid #ddf1ef;
  border-radius: 18px;
  background: linear-gradient(135deg, #f8fffd, #f6fbff);
  text-align: left;
  cursor: pointer;

  > svg {
    width: 46px;
    height: 46px;
    padding: 12px;
    box-sizing: border-box;
    border-radius: 14px;
    background: linear-gradient(135deg, #38bdf8, #34d399);
    color: #fff;
    box-shadow: 0 10px 22px rgba(52, 211, 153, 0.22);
  }

  strong,
  small {
    display: block;
  }

  strong {
    color: #2563eb;
    font-size: 24px;
  }

  small {
    margin-top: 3px;
    color: #667085;
    font-size: 13px;
  }
}

.section-card,
.activity-card,
.recommend-card {
  padding: 24px;
}

.section-title,
.side-title {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 18px;

  p,
  h2 {
    margin: 0;
  }

  p {
    color: #7a8da6;
    font-size: 12px;
    font-weight: 800;
    letter-spacing: 0.08em;
    text-transform: uppercase;
  }

  h2 {
    margin-top: 4px;
    font-size: 22px;
  }
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  gap: 16px;
}

.stat-card {
  min-height: 96px;
  padding: 18px;
  display: grid;
  grid-template-columns: 52px minmax(0, 1fr) 22px;
  align-items: center;
  gap: 14px;
  border: 1px solid #e5edf5;
  border-radius: 18px;
  background: #fff;
  text-align: left;
  cursor: pointer;

  > svg {
    width: 20px;
    height: 20px;
    color: #c1cad6;
  }
}

.stat-icon,
.action-card > span,
.timeline-icon {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  color: #fff;

  &.red { background: linear-gradient(135deg, #fb7185, #ef4444); }
  &.green { background: linear-gradient(135deg, #34d399, #22c55e); }
  &.cyan { background: linear-gradient(135deg, #22d3ee, #06b6d4); }
  &.orange { background: linear-gradient(135deg, #fbbf24, #fb923c); }
  &.blue { background: linear-gradient(135deg, #60a5fa, #2563eb); }
  &.symptom { background: linear-gradient(135deg, #fb7185, #ef4444); }
  &.medication { background: linear-gradient(135deg, #34d399, #22c55e); }
  &.report { background: linear-gradient(135deg, #22d3ee, #06b6d4); }
  &.diary { background: linear-gradient(135deg, #fbbf24, #fb923c); }
  &.chronic { background: linear-gradient(135deg, #f472b6, #ec4899); }
  &.action { background: linear-gradient(135deg, #818cf8, #6366f1); }
}

.stat-icon {
  width: 52px;
  height: 52px;
  border-radius: 15px;
  box-shadow: 0 10px 20px rgba(15, 23, 42, 0.1);

  svg {
    width: 26px;
    height: 26px;
  }
}

.stat-info {
  min-width: 0;

  strong,
  small {
    display: block;
  }

  strong {
    color: #202938;
    font-size: 25px;
  }

  small {
    margin-top: 4px;
    color: #667085;
    font-weight: 800;
  }
}

.action-grid {
  display: grid;
  grid-template-columns: repeat(4, minmax(0, 1fr));
  gap: 16px;
}

.action-card {
  min-height: 154px;
  padding: 20px 14px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 10px;
  border: 1px solid #e5edf5;
  border-radius: 18px;
  background: #fff;
  cursor: pointer;

  > span {
    width: 58px;
    height: 58px;
    border-radius: 17px;

    svg {
      width: 30px;
      height: 30px;
    }
  }

  strong {
    color: #202938;
    font-size: 16px;
  }

  small {
    color: #7a8da6;
  }
}

.side-title button {
  min-width: 44px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 0;
  border-radius: 10px;
  background: linear-gradient(135deg, #38bdf8, #34d399);
  color: #fff;
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

.timeline {
  position: relative;

  &::before {
    content: '';
    position: absolute;
    left: 10px;
    top: 8px;
    bottom: 8px;
    width: 2px;
    background: linear-gradient(#bfdbfe, #bbf7d0);
  }
}

.timeline-item {
  position: relative;
  width: 100%;
  min-height: 78px;
  margin-bottom: 12px;
  padding: 12px 12px 12px 42px;
  display: grid;
  grid-template-columns: 48px minmax(0, 1fr);
  align-items: center;
  gap: 12px;
  border: 1px solid #e5edf5;
  border-radius: 16px;
  background: #fbfffe;
  text-align: left;
  cursor: pointer;
}

.timeline-dot {
  position: absolute;
  left: 4px;
  top: 32px;
  width: 14px;
  height: 14px;
  border: 3px solid #fff;
  border-radius: 50%;
  background: #60a5fa;
  box-shadow: 0 0 0 2px #dbeafe;

  &.symptom { background: #ef4444; }
  &.medication { background: #22c55e; }
  &.report { background: #06b6d4; }
  &.diary { background: #fb923c; }
  &.chronic { background: #ec4899; }
}

.timeline-icon {
  width: 48px;
  height: 48px;
  border-radius: 14px;

  svg {
    width: 24px;
    height: 24px;
  }
}

.timeline-content {
  min-width: 0;

  strong,
  small {
    display: block;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
  }

  strong {
    color: #202938;
  }

  small {
    margin-top: 6px;
    color: #7a8da6;
  }
}

.empty-state {
  min-height: 180px;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 8px;
  color: #7a8da6;
  text-align: center;

  svg {
    width: 44px;
    height: 44px;
    color: #38bdf8;
  }

  strong {
    color: #202938;
  }
}

.recommend-card :deep(.smart-recommendation) {
  box-shadow: none;
}

.fab-wrap {
  position: fixed;
  right: 30px;
  bottom: 30px;
  z-index: 20;
}

.fab-main {
  width: 58px;
  height: 58px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  border: 0;
  border-radius: 50%;
  background: linear-gradient(135deg, #38bdf8, #34d399);
  color: #fff;
  box-shadow: 0 16px 32px rgba(14, 165, 233, 0.24);
  cursor: pointer;

  svg {
    width: 28px;
    height: 28px;
    transition: transform 0.2s ease;

    &.open {
      transform: rotate(45deg);
    }
  }
}

.fab-menu {
  position: absolute;
  right: 0;
  bottom: 72px;
  display: flex;
  flex-direction: column;
  gap: 10px;

  button {
    height: 44px;
    padding: 0 16px;
    display: inline-flex;
    align-items: center;
    gap: 8px;
    border: 1px solid #dfeaf5;
    border-radius: 999px;
    background: #fff;
    color: #2563eb;
    font-weight: 800;
    white-space: nowrap;
    cursor: pointer;
    box-shadow: 0 10px 24px rgba(37, 99, 235, 0.12);

    svg {
      width: 18px;
      height: 18px;
    }
  }
}

.fab-enter-active,
.fab-leave-active {
  transition: 0.18s ease;
}

.fab-enter-from,
.fab-leave-to {
  opacity: 0;
  transform: translateY(8px);
}

button {
  transition: transform 0.18s ease, box-shadow 0.18s ease, border-color 0.18s ease;
}

button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 14px 28px rgba(37, 99, 235, 0.12);
}

@media (max-width: 1180px) {
  .profile-layout {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 820px) {
  .health-profile-page {
    padding: 16px;
  }

  .profile-hero,
  .metric-grid,
  .stats-grid,
  .action-grid {
    grid-template-columns: 1fr;
  }

  .profile-hero {
    justify-items: center;
    text-align: center;
  }

  .user-block span {
    justify-content: center;
  }
}
</style>
