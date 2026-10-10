<template>
  <div class="resource-loader" v-if="isLoading">
    <div class="loader-content">
      <div class="loader-icon">
        <div class="sun-icon"></div>
      </div>
      <div class="loader-text">拼命加载中...</div>
      <div class="progress-bar">
        <div class="progress" :style="{ width: `${progress}%` }"></div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'

const props = defineProps({
  autoIncrement: {
    type: Boolean,
    default: true
  },
  duration: {
    type: Number,
    default: 3000
  }
})

const isLoading = ref(true)
const progress = ref(0)

onMounted(() => {
  if (props.autoIncrement) {
    const interval = setInterval(() => {
      if (progress.value < 90) {
        progress.value += Math.random() * 10
      }
    }, props.duration / 10)

    // 模拟资源加载完成
    setTimeout(() => {
      progress.value = 100
      setTimeout(() => {
        isLoading.value = false
      }, 300)
      clearInterval(interval)
    }, props.duration)
  }
})

// 提供方法给父组件调用
const setProgress = (value) => {
  progress.value = value
}

const complete = () => {
  progress.value = 100
  setTimeout(() => {
    isLoading.value = false
  }, 300)
}

defineExpose({
  setProgress,
  complete,
  isLoading
})
</script>

<style scoped>
.resource-loader {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: #f8d56b;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.loader-content {
  text-align: center;
}

.loader-icon {
  margin-bottom: 20px;
}

.sun-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: #fff;
  box-shadow: 0 0 20px rgba(255, 255, 255, 0.8);
  position: relative;
  margin: 0 auto;
  animation: pulse 2s infinite;
}

.sun-icon::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #f8d56b;
}

.sun-icon::after {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 10px;
  height: 10px;
  border-radius: 50%;
  background: #fff;
}

@keyframes pulse {
  0% { transform: scale(1); }
  50% { transform: scale(1.1); }
  100% { transform: scale(1); }
}

.loader-text {
  color: #333;
  font-size: 18px;
  margin-bottom: 15px;
  font-weight: bold;
}

.progress-bar {
  width: 200px;
  height: 6px;
  background-color: rgba(255, 255, 255, 0.3);
  border-radius: 3px;
  overflow: hidden;
  margin: 0 auto;
}

.progress {
  height: 100%;
  background-color: #fff;
  border-radius: 3px;
  transition: width 0.3s ease;
}
</style>