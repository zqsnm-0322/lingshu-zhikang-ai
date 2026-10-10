<template>
  <aside class="sidebar">
    <div class="sidebar-header">
      <button class="brand" type="button" @click="navigateTo('/')">
        <span class="brand-mark">
          <img src="/lingshu-favicon.png" alt="灵枢智康图标">
        </span>
        <span>
          <strong>灵枢智康</strong>
          <small>Medical AI Workspace</small>
        </span>
      </button>
    </div>

    <nav class="sidebar-nav">
      <section v-for="group in menuGroups" :key="group.title" class="nav-group">
        <button class="group-title" type="button" @click="toggleExpand(group.title)">
          <component :is="group.icon" />
          <span>{{ group.title }}</span>
          <ChevronDownIcon class="group-arrow" :class="{ expanded: expandedItems.has(group.title) }" />
        </button>

        <div v-show="expandedItems.has(group.title)" class="group-items">
          <button
            v-for="item in group.items"
            :key="item.route"
            class="nav-item"
            :class="{ active: isActive(item.route) }"
            type="button"
            @click="navigateTo(item.route)"
          >
            <component :is="item.icon" />
            <span>{{ item.text }}</span>
            <small v-if="item.badge">{{ item.badge }}</small>
          </button>
        </div>
      </section>
    </nav>
  </aside>
</template>

<script setup>
import { ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import {
  BeakerIcon,
  ChartBarIcon,
  ChatBubbleLeftRightIcon,
  ChevronDownIcon,
  ClipboardDocumentCheckIcon,
  DocumentTextIcon,
  HeartIcon,
  HomeIcon,
  IdentificationIcon,
  SparklesIcon,
  UserCircleIcon
} from '@heroicons/vue/24/outline'

const router = useRouter()
const route = useRoute()
const expandedItems = ref(new Set(['工作台', '智能问诊', '健康管理', '用药与营养']))

const menuGroups = [
  {
    title: '工作台',
    icon: HomeIcon,
    items: [
      { text: '今日健康中心', route: '/', icon: HomeIcon }
    ]
  },
  {
    title: '智能问诊',
    icon: SparklesIcon,
    items: [
      { text: 'AI 聊天', route: '/ai-chat', icon: ChatBubbleLeftRightIcon },
      { text: '症状自检', route: '/symptom-checker', icon: ClipboardDocumentCheckIcon },
      { text: '病历分析', route: '/chat-pdf', icon: DocumentTextIcon },
      { text: '医生模拟', route: '/doctor-simulator', icon: UserCircleIcon }
    ]
  },
  {
    title: '健康管理',
    icon: HeartIcon,
    items: [
      { text: '健康日记', route: '/health-diary', icon: DocumentTextIcon },
      { text: '体检报告', route: '/health-report', icon: ClipboardDocumentCheckIcon },
      { text: '慢病管理', route: '/chronic-disease', icon: HeartIcon }
    ]
  },
  {
    title: '用药与营养',
    icon: BeakerIcon,
    items: [
      { text: '药品查询', route: '/medicine-query', icon: BeakerIcon },
      { text: '用药提醒', route: '/medicine-reminder', icon: ClipboardDocumentCheckIcon },
      { text: '饮食推荐', route: '/daily-food-recommendation', icon: HeartIcon }
    ]
  },
  {
    title: '数据中心',
    icon: ChartBarIcon,
    items: [
      { text: '健康数据大屏', route: '/health-dashboard', icon: ChartBarIcon },
      { text: '管理后台', route: '/admin', icon: IdentificationIcon, badge: 'Admin' }
    ]
  },

  {
    title: '账户服务',
    icon: UserCircleIcon,
    items: [
      { text: '个人中心', route: '/profile', icon: UserCircleIcon },
      { text: '会员中心', route: '/membership', icon: IdentificationIcon }
    ]
  }
]

const navigateTo = (targetRoute) => {
  router.push(targetRoute)
}

const toggleExpand = (title) => {
  const next = new Set(expandedItems.value)
  if (next.has(title)) {
    next.delete(title)
  } else {
    next.add(title)
  }
  expandedItems.value = next
}

const isActive = (targetRoute) => {
  if (targetRoute === '/') return route.path === '/' || route.path === '/home'
  return route.path === targetRoute
}

const expandCurrentGroup = () => {
  const currentGroup = menuGroups.find((group) => group.items.some((item) => isActive(item.route)))
  if (!currentGroup) return
  const next = new Set(expandedItems.value)
  next.add(currentGroup.title)
  expandedItems.value = next
}

watch(() => route.path, expandCurrentGroup, { immediate: true })
</script>

<style scoped lang="scss">
.sidebar {
  position: fixed;
  left: 0;
  top: 0;
  z-index: 100;
  display: flex;
  width: var(--zq-sidebar-width);
  height: 100vh;
  flex-direction: column;
  border-right: 1px solid var(--zq-border);
  background: var(--zq-bg-panel);
  transition: transform 0.25s ease;
}

.sidebar.sidebar-hidden {
  transform: translateX(-100%);
}

.sidebar-header {
  padding: 18px;
  border-bottom: 1px solid var(--zq-border);
}

.brand {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 12px;
  border: 0;
  background: transparent;
  color: var(--zq-text-primary);
  padding: 0;
  text-align: left;
  cursor: pointer;
}

.brand-mark {
  display: inline-flex;
  width: 52px;
  height: 52px;
  align-items: center;
  justify-content: center;
  flex: 0 0 52px;
  overflow: hidden;
  border-radius: 12px;
}

.brand-mark img {
  width: 100%;
  height: 100%;
  object-fit: contain;
}

.brand strong,
.brand small {
  display: block;
}

.brand strong {
  font-size: 17px;
  line-height: 1.35;
}

.brand small {
  margin-top: 2px;
  color: var(--zq-text-muted);
  font-size: 11px;
}

.sidebar-nav {
  flex: 1;
  overflow-y: auto;
  padding: 12px;
}

.nav-group {
  margin-bottom: 8px;
}

.group-title,
.nav-item {
  display: flex;
  width: 100%;
  align-items: center;
  border: 0;
  border-radius: var(--zq-radius);
  background: transparent;
  color: var(--zq-text-secondary);
  cursor: pointer;
  text-align: left;
  transition: background-color 0.2s ease, color 0.2s ease;
}

.group-title {
  gap: 10px;
  min-height: 38px;
  padding: 0 10px;
  font-size: 13px;
  font-weight: 700;
}

.group-title svg,
.nav-item svg {
  width: 18px;
  height: 18px;
  flex: 0 0 18px;
}

.group-arrow {
  margin-left: auto;
  transition: transform 0.2s ease;
}

.group-arrow.expanded {
  transform: rotate(180deg);
}

.group-items {
  display: flex;
  flex-direction: column;
  gap: 3px;
  margin-top: 4px;
}

.nav-item {
  gap: 10px;
  min-height: 38px;
  padding: 0 10px 0 30px;
  font-size: 14px;
  font-weight: 600;
}

.group-title:hover,
.nav-item:hover,
.nav-item.active {
  background: var(--zq-bg-soft);
  color: var(--zq-color-primary);
}

.nav-item small {
  margin-left: auto;
  border-radius: 999px;
  background: rgba(245, 158, 11, 0.12);
  color: #b45309;
  padding: 2px 6px;
  font-size: 11px;
}

@media (max-width: 900px) {
  .sidebar {
    box-shadow: var(--zq-shadow-md);
  }
}
</style>
