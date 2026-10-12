<template>
  <DashboardLayout title="今日健康中心" subtitle="集中查看风险、待办和智能医疗服务" @refresh="refreshPage">
    <div class="home-workspace">
      <PageHeader
        title="今天从这里开始"
        :subtitle="welcomeSubtitle"
        eyebrow="Health Workspace"
      >
        <template #actions>
          <button class="zq-text-button" type="button" @click="navigateTo('/health-report')">
            <DocumentArrowUpIcon />
            上传报告
          </button>
          <button class="zq-text-button zq-primary-button" type="button" @click="navigateTo('/ai-chat')">
            <SparklesIcon />
            开始咨询
          </button>
          <button class="zq-text-button" type="button" @click="showLogoutConfirm">
            <ArrowRightOnRectangleIcon />
            退出登录
          </button>
        </template>
      </PageHeader>

      <section class="zq-grid zq-grid-4">
        <StatCard label="健康评分" :value="healthStats.healthScore ?? '--'" :icon="HeartIcon" tone="green" />
        <StatCard label="累计咨询" :value="healthStats.totalConsultations" :icon="ChatBubbleLeftRightIcon" tone="blue" />
        <StatCard label="活跃用户" :value="healthStats.activeUsers" :icon="UserGroupIcon" tone="cyan" />
        <StatCard label="今日待办" :value="todayTodos.length" :icon="ClipboardDocumentCheckIcon" tone="amber" />
      </section>

      <section class="home-grid">
        <div class="main-column zq-stack">
          <section class="panel zq-card">
            <div class="panel-header">
              <div>
                <h2 class="zq-section-title">常用 AI 服务</h2>
                <p class="zq-section-subtitle">快速进入问诊、病历分析和症状自检。</p>
              </div>
            </div>

            <div class="zq-grid zq-grid-3">
              <FeatureCard
                v-for="feature in primaryFeatures"
                :key="feature.route"
                :title="feature.title"
                :description="feature.description"
                :icon="feature.icon"
                :tone="feature.tone"
                :badge="feature.badge"
                @select="navigateTo(feature.route)"
              />
            </div>
          </section>

          <section class="panel zq-card">
            <div class="panel-header">
              <div>
                <h2 class="zq-section-title">全部功能入口</h2>
                <p class="zq-section-subtitle">页面重构后仍保留完整功能矩阵。</p>
              </div>
            </div>

            <div class="feature-table">
              <button
                v-for="feature in allFeatures"
                :key="feature.route"
                class="feature-row"
                type="button"
                @click="navigateTo(feature.route)"
              >
                <span class="row-icon" :class="`tone-${feature.tone}`">
                  <component :is="feature.icon" />
                </span>
                <span>
                  <strong>{{ feature.title }}</strong>
                  <small>{{ feature.group }}</small>
                </span>
                <ChevronRightIcon />
              </button>
            </div>
          </section>
        </div>

        <aside class="side-column zq-stack">
          <section class="panel zq-card">
            <div class="panel-header">
              <div>
                <h2 class="zq-section-title">风险提醒</h2>
                <p class="zq-section-subtitle">重点关注异常指标和近期状态变化。</p>
              </div>
            </div>

            <div class="risk-list">
              <button
                v-for="risk in riskAlerts"
                :key="risk.title"
                class="risk-item"
                type="button"
                @click="navigateTo(risk.route)"
              >
                <span class="risk-level" :class="risk.className">{{ risk.level }}</span>
                <span>
                  <strong>{{ risk.title }}</strong>
                  <small>{{ risk.action }}</small>
                </span>
              </button>
            </div>
          </section>

          <section class="panel zq-card">
            <div class="panel-header">
              <div>
                <h2 class="zq-section-title">今日待办</h2>
                <p class="zq-section-subtitle">把记录、复查和用药放到可执行的位置。</p>
              </div>
            </div>

            <div class="todo-list">
              <button
                v-for="todo in todayTodos"
                :key="todo.title"
                class="todo-item"
                type="button"
                @click="navigateTo(todo.route)"
              >
                <span class="todo-time">{{ todo.time }}</span>
                <span>{{ todo.title }}</span>
              </button>
            </div>
          </section>

          <section class="panel zq-card">
            <div class="panel-header">
              <div>
                <h2 class="zq-section-title">最近问诊</h2>
                <p class="zq-section-subtitle">继续上次的医疗 AI 对话。</p>
              </div>
            </div>

            <div class="recent-list">
              <button
                v-for="chat in recentConsultations"
                :key="chat.title"
                class="recent-item"
                type="button"
                @click="navigateTo(chat.route)"
              >
                <ChatBubbleLeftRightIcon />
                <span>
                  <strong>{{ chat.title }}</strong>
                  <small>{{ chat.time }}</small>
                </span>
              </button>
            </div>
          </section>
        </aside>
      </section>
    </div>

    <LogoutModal
      v-if="showConfirm"
      @confirm="confirmLogout"
      @cancel="cancelLogout"
    />
  </DashboardLayout>
</template>

<script setup>
import { computed, onMounted, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowRightOnRectangleIcon,
  BeakerIcon,
  ChartBarIcon,
  ChatBubbleLeftRightIcon,
  ChevronRightIcon,
  ClipboardDocumentCheckIcon,
  DocumentArrowUpIcon,
  DocumentTextIcon,
  HeartIcon,
  IdentificationIcon,
  SparklesIcon,
  UserGroupIcon
} from '@heroicons/vue/24/outline'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import FeatureCard from '@/components/common/FeatureCard.vue'
import StatCard from '@/components/common/StatCard.vue'
import LogoutModal from '@/components/home/LogoutModal.vue'
import { healthAPI } from '@/services/api'

const router = useRouter()
const showConfirm = ref(false)

const username = computed(() => localStorage.getItem('username') || '用户')
const welcomeSubtitle = computed(() => `${username.value}，这里保留了所有原有功能入口，并把今天最重要的健康任务放在首屏。`)

const healthStats = ref({
  healthScore: 0,
  totalConsultations: 0,
  activeUsers: 0,
  monthlyData: [],
  trendData: [],
  userStats: {
    totalUsers: 0,
    totalVisits: 0,
    dailyActive: 0,
    growthRate: 0
  }
})

const primaryFeatures = [
  {
    title: 'AI 聊天',
    description: '支持文本咨询和图片、音频、视频等多模态输入。',
    route: '/ai-chat',
    icon: ChatBubbleLeftRightIcon,
    tone: 'blue',
    badge: '常用'
  },
  {
    title: '病历分析',
    description: '上传 PDF 病历或报告，进行问答和自动分析。',
    route: '/chat-pdf',
    icon: DocumentTextIcon,
    tone: 'cyan'
  },
  {
    title: '症状自检',
    description: '按症状快速获得健康参考和下一步建议。',
    route: '/symptom-checker',
    icon: ClipboardDocumentCheckIcon,
    tone: 'green'
  },
  {
    title: '用药提醒',
    description: '管理药品计划，减少漏服和重复用药风险。',
    route: '/medicine-reminder',
    icon: BeakerIcon,
    tone: 'amber'
  },
  {
    title: '健康管家',
    description: '汇总健康档案、日记和报告形成连续管理。',
    route: '/health-butler',
    icon: SparklesIcon,
    tone: 'blue'
  }
]

const allFeatures = [
  ...primaryFeatures.map((item) => ({ ...item, group: '智能入口' })),
  { title: '医生模拟', group: '智能问诊', route: '/doctor-simulator', icon: UserGroupIcon, tone: 'blue' },
  { title: '健康档案', group: '健康管理', route: '/health-profile', icon: IdentificationIcon, tone: 'green' },
  { title: '健康日记', group: '健康管理', route: '/health-diary', icon: DocumentTextIcon, tone: 'green' },
  { title: '体检报告', group: '健康管理', route: '/health-report', icon: ClipboardDocumentCheckIcon, tone: 'green' },
  { title: '慢病管理', group: '健康管理', route: '/chronic-disease', icon: HeartIcon, tone: 'amber' },
  { title: '药品查询', group: '用药与营养', route: '/medicine-query', icon: BeakerIcon, tone: 'cyan' },
  { title: '饮食推荐', group: '用药与营养', route: '/daily-food-recommendation', icon: HeartIcon, tone: 'green' },
  { title: '健康数据大屏', group: '数据中心', route: '/health-dashboard', icon: ChartBarIcon, tone: 'blue' },
  { title: '商业看板', group: '数据中心', route: '/business-dashboard', icon: ChartBarIcon, tone: 'cyan' },
  { title: '管理后台', group: '数据中心', route: '/admin', icon: IdentificationIcon, tone: 'amber' },
  { title: '个人中心', group: '账户服务', route: '/profile', icon: IdentificationIcon, tone: 'blue' },
  { title: '会员中心', group: '账户服务', route: '/membership', icon: IdentificationIcon, tone: 'amber' }
]

const riskAlerts = [
  {
    level: '关注',
    title: '血压趋势需复测',
    action: '建议记录今日血压并查看健康数据大屏',
    route: '/health-dashboard',
    className: 'zq-risk-medium'
  },
  {
    level: '提醒',
    title: '体检报告待分析',
    action: '上传报告后可进入 AI 病历分析',
    route: '/health-report',
    className: 'zq-risk-low'
  },
  {
    level: '重要',
    title: '用药计划需要确认',
    action: '检查今日用药提醒是否完整',
    route: '/medicine-reminder',
    className: 'zq-risk-high'
  }
]

const todayTodos = ref([])

const recentConsultations = [
  { title: '头痛与睡眠咨询', time: '最近一次 AI 对话', route: '/ai-chat' },
  { title: '体检指标解读', time: '继续病历分析', route: '/chat-pdf' }
]

const navigateTo = (targetRoute) => {
  router.push(targetRoute)
}

const refreshPage = () => {
  window.location.reload()
}

const showLogoutConfirm = () => {
  showConfirm.value = true
}

const cancelLogout = () => {
  showConfirm.value = false
}

const confirmLogout = () => {
  showConfirm.value = false
  localStorage.removeItem('token')
  localStorage.removeItem('username')
  localStorage.removeItem('userId')
  localStorage.removeItem('userRole')
  router.replace({ name: 'Login' })
}

const loadHealthStats = async () => {
  try {
    const data = await healthAPI.getHealthStats()
    if (data) {
      healthStats.value = data
    }
  } catch (error) {
    console.error('加载健康统计数据失败:', error)
  }
}

const loadTodayTodos = async () => {
  try {
    const data = await healthAPI.getTodayReminders()
    todayTodos.value = (data?.todayList || []).map((item) => ({
      time: item.time,
      title: `${item.name}${item.statusText ? `（${item.statusText}）` : ''}`,
      route: '/medicine-reminder'
    }))
  } catch (error) {
    console.error('加载今日待办失败:', error)
    todayTodos.value = []
  }
}

onMounted(async () => {
  const token = localStorage.getItem('token')
  if (!token) {
    router.push('/login')
    return
  }

  await Promise.all([loadHealthStats(), loadTodayTodos()])
})
</script>

<style scoped lang="scss">
.home-workspace {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.home-workspace :deep(.zq-text-button svg) {
  width: 18px;
  height: 18px;
}

.home-grid {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 360px;
  gap: 20px;
  align-items: start;
}

.panel {
  padding: 20px;
}

.panel-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 16px;
  margin-bottom: 16px;
}

.feature-table {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.feature-row,
.risk-item,
.todo-item,
.recent-item {
  display: flex;
  width: 100%;
  align-items: center;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-panel);
  color: var(--zq-text-primary);
  cursor: pointer;
  text-align: left;
  transition: border-color 0.2s ease, background-color 0.2s ease;
}

.feature-row {
  gap: 12px;
  min-height: 72px;
  padding: 12px;
}

.feature-row:hover,
.risk-item:hover,
.todo-item:hover,
.recent-item:hover {
  border-color: rgba(37, 99, 235, 0.35);
  background: var(--zq-bg-soft);
}

.feature-row strong,
.risk-item strong,
.recent-item strong {
  display: block;
  font-size: 14px;
  line-height: 1.4;
}

.feature-row small,
.risk-item small,
.recent-item small {
  display: block;
  margin-top: 4px;
  color: var(--zq-text-secondary);
  font-size: 12px;
}

.feature-row > svg {
  width: 18px;
  height: 18px;
  margin-left: auto;
  color: var(--zq-text-muted);
}

.row-icon {
  display: inline-flex;
  width: 38px;
  height: 38px;
  flex: 0 0 38px;
  align-items: center;
  justify-content: center;
  border-radius: var(--zq-radius);
}

.row-icon svg {
  width: 20px;
  height: 20px;
}

.tone-blue {
  color: #2563eb;
  background: rgba(37, 99, 235, 0.1);
}

.tone-green {
  color: #16a34a;
  background: rgba(22, 163, 74, 0.1);
}

.tone-cyan {
  color: #0891b2;
  background: rgba(8, 145, 178, 0.1);
}

.tone-amber {
  color: #d97706;
  background: rgba(245, 158, 11, 0.12);
}

.tone-red {
  color: #dc2626;
  background: rgba(220, 38, 38, 0.1);
}

.risk-list,
.todo-list,
.recent-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.risk-item {
  align-items: flex-start;
  gap: 10px;
  padding: 12px;
}

.risk-level {
  flex: 0 0 auto;
  border-radius: 999px;
  padding: 4px 8px;
  font-size: 12px;
  font-weight: 700;
}

.todo-item {
  gap: 12px;
  min-height: 52px;
  padding: 10px 12px;
  color: var(--zq-text-secondary);
  font-size: 14px;
  font-weight: 600;
}

.todo-time {
  flex: 0 0 auto;
  color: var(--zq-color-primary);
  font-weight: 800;
}

.recent-item {
  gap: 10px;
  min-height: 62px;
  padding: 12px;
}

.recent-item > svg {
  width: 20px;
  height: 20px;
  color: var(--zq-color-primary);
}

@media (max-width: 1180px) {
  .home-grid {
    grid-template-columns: 1fr;
  }

  .side-column {
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 16px;
  }
}

@media (max-width: 900px) {
  .side-column,
  .feature-table {
    grid-template-columns: 1fr;
  }
}

@media (max-width: 760px) {
  .panel {
    padding: 16px;
  }
}
</style>
