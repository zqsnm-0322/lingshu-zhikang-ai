<script setup lang="ts">
import { RouterView, useRouter } from 'vue-router'
import { useDark } from '@vueuse/core'
import { ref, onMounted } from 'vue'

const isDark = useDark()
const router = useRouter()
const resourceLoaded = ref(true)

router.beforeEach((to, from, next) => {
  if (from.path === '/chat-pdf') {
    window.dispatchEvent(new CustomEvent('cleanupChatPDF'))
  }
  next()
})

onMounted(() => {
  resourceLoaded.value = true
})
</script>

<template>
  <div class="app" :class="{ dark: isDark }">
    <router-view v-slot="{ Component, route }">
      <transition name="fade" mode="out-in">
        <component :is="Component" :key="route.fullPath" />
      </transition>
    </router-view>
  </div>
</template>

<style lang="scss">
* {
  box-sizing: border-box;
}

html,
body {
  width: 100%;
  min-height: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
}

body {
  min-height: 100vh;
  background: var(--zq-bg-page);
  color: var(--zq-text-primary);
  font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto, "Helvetica Neue", Arial,
    "Noto Sans SC", sans-serif;
}

button,
input,
textarea,
select {
  font: inherit;
}

button {
  letter-spacing: 0;
}

#app,
.app {
  min-height: 100vh;
  width: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.2s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style>
