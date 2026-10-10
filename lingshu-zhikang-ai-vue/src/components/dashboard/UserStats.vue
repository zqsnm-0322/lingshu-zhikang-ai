<template>
  <div class="user-stats">
    <div class="stats-header">
      <h3 class="stats-title">健康服务概览</h3>
      <div class="stats-period">较上周 {{ growthRate }}%</div>
    </div>
    
    <div class="stats-grid">
      <div class="stat-item">
        <div class="stat-number">{{ formatNumber(totalUsers) }}</div>
        <div class="stat-label">注册用户</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">{{ formatNumber(totalVisits) }}</div>
        <div class="stat-label">总咨询</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">{{ formatNumber(dailyActive) }}</div>
        <div class="stat-label">日活跃</div>
      </div>
      <div class="stat-item">
        <div class="stat-number">+{{ growthRate }}%</div>
        <div class="stat-label">增长率</div>
      </div>
    </div>
    
    <div class="stats-description">
      <p>灵枢智康为用户提供全方位的健康管理服务，包括症状自检、病历分析、用药提醒等功能</p>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  userStats: {
    type: Object,
    default: () => ({
      totalUsers: 32000,
      totalVisits: 128000,
      dailyActive: 1200,
      growthRate: 5
    })
  }
})

const totalUsers = computed(() => props.userStats.totalUsers)
const totalVisits = computed(() => props.userStats.totalVisits)
const dailyActive = computed(() => props.userStats.dailyActive)
const growthRate = computed(() => props.userStats.growthRate)

const formatNumber = (num) => {
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}
</script>

<style scoped lang="scss">
.user-stats {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
}

.stats-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.stats-title {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.stats-period {
  font-size: 12px;
  color: #10b981;
  font-weight: 500;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 20px;
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 24px;
  font-weight: 700;
  color: #1f2937;
  margin-bottom: 4px;
}

.stat-label {
  font-size: 12px;
  color: #6b7280;
}

.stats-description {
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
  
  p {
    font-size: 12px;
    color: #6b7280;
    line-height: 1.5;
    margin: 0;
  }
}
</style>
