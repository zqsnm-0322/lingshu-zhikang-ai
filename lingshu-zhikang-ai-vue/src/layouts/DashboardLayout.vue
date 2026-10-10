<template>
  <div class="dashboard-layout" :class="{ 'sidebar-collapsed': !sidebarVisible }">
    <Sidebar :class="{ 'sidebar-hidden': !sidebarVisible }" />

    <div v-if="sidebarVisible" class="mobile-mask" @click="sidebarVisible = false"></div>

    <main class="dashboard-main">
      <header class="dashboard-topbar">
        <div class="topbar-left">
          <button class="zq-icon-button" type="button" title="切换导航" @click="toggleSidebar">
            <Bars3Icon />
          </button>
          <div class="topbar-title">
            <span>{{ title }}</span>
            <small v-if="subtitle">{{ subtitle }}</small>
          </div>
        </div>

        <div class="topbar-search">
          <MagnifyingGlassIcon />
          <input type="search" placeholder="搜索功能、报告或问诊记录" />
        </div>

        <div class="topbar-actions">
          <button class="zq-icon-button" type="button" title="刷新页面" @click="$emit('refresh')">
            <ArrowPathIcon />
          </button>
          <button class="user-chip" type="button" @click="goProfile">
            <UserCircleIcon />
            <span>{{ username }}</span>
          </button>
        </div>
      </header>

      <div class="dashboard-body">
        <slot />
      </div>
    </main>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import { useRouter } from 'vue-router'
import {
  ArrowPathIcon,
  Bars3Icon,
  MagnifyingGlassIcon,
  UserCircleIcon
} from '@heroicons/vue/24/outline'
import Sidebar from '@/components/dashboard/Sidebar.vue'

defineProps({
  title: {
    type: String,
    default: '工作台'
  },
  subtitle: {
    type: String,
    default: ''
  }
})

defineEmits(['refresh'])

const router = useRouter()
const sidebarVisible = ref(window.innerWidth > 900)
const username = computed(() => localStorage.getItem('username') || '用户')

const toggleSidebar = () => {
  sidebarVisible.value = !sidebarVisible.value
}

const goProfile = () => {
  router.push('/profile')
}

</script>

<style scoped lang="scss">
.dashboard-layout {
  min-height: 100vh;
  background: var(--zq-bg-page);
}

.dashboard-main {
  min-height: 100vh;
  margin-left: var(--zq-sidebar-width);
  transition: margin-left 0.25s ease;
}

.sidebar-collapsed .dashboard-main {
  margin-left: 0;
}

.dashboard-topbar {
  position: sticky;
  top: 0;
  z-index: 50;
  display: grid;
  min-height: var(--zq-header-height);
  grid-template-columns: minmax(220px, 1fr) minmax(260px, 420px) minmax(260px, 1fr);
  align-items: center;
  gap: 16px;
  border-bottom: 1px solid var(--zq-border);
  background: rgba(255, 255, 255, 0.88);
  padding: 14px 24px;
  backdrop-filter: blur(16px);
}

.dark .dashboard-topbar {
  background: rgba(17, 24, 39, 0.88);
}

.topbar-left,
.topbar-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.topbar-actions {
  justify-content: flex-end;
}

.topbar-title {
  display: flex;
  min-width: 0;
  flex-direction: column;
  gap: 4px;
}

.topbar-title span {
  color: var(--zq-text-primary);
  font-size: 18px;
  font-weight: 700;
}

.topbar-title small {
  color: var(--zq-text-secondary);
  font-size: 12px;
}

.topbar-search {
  display: flex;
  height: 42px;
  align-items: center;
  gap: 10px;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-panel);
  padding: 0 12px;
}

.topbar-search svg,
.zq-icon-button svg,
.user-chip svg {
  width: 20px;
  height: 20px;
}

.topbar-search svg {
  color: var(--zq-text-muted);
}

.topbar-search input {
  min-width: 0;
  flex: 1;
  border: 0;
  outline: 0;
  background: transparent;
  color: var(--zq-text-primary);
  font-size: 14px;
}

.user-chip {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  min-height: 40px;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-panel);
  color: var(--zq-text-primary);
  padding: 0 12px;
  cursor: pointer;
  font-size: 14px;
  font-weight: 600;
}

.dashboard-body {
  padding: 24px;
}

.mobile-mask {
  display: none;
}

@media (max-width: 1100px) {
  .dashboard-topbar {
    grid-template-columns: 1fr auto;
  }

  .topbar-search {
    display: none;
  }
}

@media (max-width: 900px) {
  .dashboard-main {
    margin-left: 0;
  }

  .dashboard-topbar {
    padding: 12px 16px;
  }

  .dashboard-body {
    padding: 16px;
  }

  .mobile-mask {
    position: fixed;
    inset: 0;
    z-index: 90;
    display: block;
    background: rgba(15, 23, 42, 0.36);
  }
}

@media (max-width: 560px) {
  .topbar-title small,
  .user-chip span {
    display: none;
  }

  .topbar-actions {
    gap: 8px;
  }
}
</style>
