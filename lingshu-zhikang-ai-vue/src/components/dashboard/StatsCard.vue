<template>
  <div class="stats-card">
    <div class="stats-content">
      <div class="stats-header">
        <h3 class="stats-title">{{ title }}</h3>
        <div class="stats-icon">
          <img :src="icon" :alt="title" class="icon-img" />
        </div>
      </div>
      <div class="stats-number">{{ formattedValue }}</div>
      <div class="stats-change" :class="changeType">
        <span class="change-text">较上周 {{ changeText }}</span>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: {
    type: String,
    required: true
  },
  value: {
    type: Number,
    required: true
  },
  change: {
    type: Number,
    default: 0
  },
  icon: {
    type: String,
    required: true
  }
})

const formattedValue = computed(() => {
  if (props.value >= 1000) {
    return (props.value / 1000).toFixed(1) + 'k'
  }
  return props.value.toString()
})

const changeType = computed(() => {
  if (props.change > 0) return 'positive'
  if (props.change < 0) return 'negative'
  return 'neutral'
})

const changeText = computed(() => {
  const absChange = Math.abs(props.change)
  const sign = props.change > 0 ? '+' : props.change < 0 ? '-' : ''
  return `${sign}${absChange}%`
})
</script>

<style scoped lang="scss">
.stats-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
  
  &:hover {
    transform: translateY(-2px);
    box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  }
}

.stats-content {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.stats-title {
  font-size: 14px;
  color: #666;
  margin: 0;
  font-weight: 500;
}

.stats-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #f0f9ff;
  padding: 8px;
}

.icon-img {
  width: 24px;
  height: 24px;
  object-fit: contain;
  filter: brightness(0) saturate(100%) invert(47%) sepia(69%) saturate(959%) hue-rotate(187deg) brightness(98%) contrast(89%);
}

.stats-number {
  font-size: 32px;
  font-weight: 700;
  color: #1f2937;
  line-height: 1;
}

.stats-change {
  font-size: 12px;
  font-weight: 500;
  
  &.positive {
    color: #10b981;
  }
  
  &.negative {
    color: #ef4444;
  }
  
  &.neutral {
    color: #6b7280;
  }
}

.change-text {
  display: flex;
  align-items: center;
  gap: 4px;
}
</style>
