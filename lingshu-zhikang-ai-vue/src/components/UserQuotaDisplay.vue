<template>
  <div class="quota-display">
    <div class="quota-card">
      <div class="quota-header">
        <div class="member-badge" :class="'badge-' + memberLevel.toLowerCase()">
          <i class="iconify" data-icon="mdi:crown"></i>
          {{ memberLevelName }}
        </div>
        <button @click="$emit('upgrade')" class="btn-upgrade-small">
          <i class="iconify" data-icon="mdi:arrow-up-circle"></i>
          升级
        </button>
      </div>
      
      <div class="quota-content">
        <div class="quota-label">今日剩余配额</div>
        <div class="quota-value">
          <span class="remaining">{{ remainingQuota }}</span>
          <span class="total">/ {{ dailyQuota }}</span>
        </div>
        
        <div class="quota-bar">
          <div 
            class="quota-fill" 
            :style="{ width: usagePercent + '%' }"
            :class="{ 'quota-warning': usagePercent > 80 }"
          ></div>
        </div>
        
        <div class="quota-stats">
          <div class="stat-item">
            <span class="stat-label">今日已用</span>
            <span class="stat-value">{{ dailyUsed }}</span>
          </div>
          <div class="stat-item">
            <span class="stat-label">累计使用</span>
            <span class="stat-value">{{ totalUsed }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  memberLevel: {
    type: String,
    default: 'FREE'
  },
  dailyQuota: {
    type: Number,
    default: 5
  },
  dailyUsed: {
    type: Number,
    default: 0
  },
  totalUsed: {
    type: Number,
    default: 0
  }
})

defineEmits(['upgrade'])

const memberLevelName = computed(() => {
  const names = {
    'FREE': '免费版',
    'STANDARD': '标准版',
    'PROFESSIONAL': '专业版',
    'ENTERPRISE': '企业版'
  }
  return names[props.memberLevel] || props.memberLevel
})

const remainingQuota = computed(() => {
  return Math.max(0, props.dailyQuota - props.dailyUsed)
})

const usagePercent = computed(() => {
  if (props.dailyQuota === 0) return 0
  return Math.min(100, (props.dailyUsed / props.dailyQuota) * 100)
})
</script>

<style scoped>
.quota-display {
  margin-bottom: 20px;
}

.quota-card {
  background: white;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.quota-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.member-badge {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 600;
}

.member-badge .iconify {
  font-size: 18px;
}

.badge-free {
  background: #f3f4f6;
  color: #6b7280;
}

.badge-standard {
  background: linear-gradient(135deg, #dbeafe, #bfdbfe);
  color: #1e40af;
}

.badge-professional {
  background: linear-gradient(135deg, #ede9fe, #ddd6fe);
  color: #6b21a8;
}

.badge-enterprise {
  background: linear-gradient(135deg, #fef3c7, #fde68a);
  color: #92400e;
}

.btn-upgrade-small {
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border: none;
  padding: 6px 14px;
  border-radius: 8px;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
}

.btn-upgrade-small:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.quota-content {
  text-align: center;
}

.quota-label {
  font-size: 13px;
  color: #6b7280;
  margin-bottom: 8px;
}

.quota-value {
  font-size: 32px;
  font-weight: 700;
  margin-bottom: 12px;
}

.quota-value .remaining {
  color: #3b82f6;
}

.quota-value .total {
  color: #9ca3af;
  font-size: 20px;
}

.quota-bar {
  height: 8px;
  background: #f3f4f6;
  border-radius: 4px;
  overflow: hidden;
  margin-bottom: 16px;
}

.quota-fill {
  height: 100%;
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
  border-radius: 4px;
  transition: width 0.5s ease;
}

.quota-fill.quota-warning {
  background: linear-gradient(90deg, #f59e0b, #fbbf24);
}

.quota-stats {
  display: flex;
  justify-content: space-around;
  padding-top: 16px;
  border-top: 1px solid #f3f4f6;
}

.stat-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.stat-label {
  font-size: 12px;
  color: #9ca3af;
}

.stat-value {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}
</style>
