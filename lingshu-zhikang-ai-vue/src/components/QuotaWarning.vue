<template>
  <div v-if="showWarning" class="quota-warning-overlay" @click="close">
    <div class="quota-warning-modal" @click.stop>
      <div class="warning-icon">
        <i class="iconify" data-icon="mdi:alert-circle"></i>
      </div>
      <h3>今日使用次数已达上限</h3>
      <div class="quota-info">
        <p>您当前是 <strong>{{ memberLevelName }}</strong></p>
        <p>每日配额: <strong>{{ dailyQuota }}</strong> 次</p>
        <p>已使用: <strong>{{ dailyUsed }}</strong> 次</p>
      </div>
      <div class="upgrade-tip">
        <p>升级会员即可获得更多使用次数</p>
      </div>
      <div class="action-buttons">
        <button @click="goToMembership" class="btn-upgrade">
          <i class="iconify" data-icon="mdi:crown"></i>
          立即升级
        </button>
        <button @click="close" class="btn-cancel">
          明天再试
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'

const props = defineProps({
  showWarning: {
    type: Boolean,
    default: false
  },
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
  }
})

const emit = defineEmits(['close'])
const router = useRouter()

const memberLevelName = computed(() => {
  const names = {
    'FREE': '免费版',
    'STANDARD': '标准版',
    'PROFESSIONAL': '专业版',
    'ENTERPRISE': '企业版'
  }
  return names[props.memberLevel] || props.memberLevel
})

const close = () => {
  emit('close')
}

const goToMembership = () => {
  router.push('/membership')
  close()
}
</script>

<style scoped>
.quota-warning-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  animation: fadeIn 0.3s;
}

@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.quota-warning-modal {
  background: white;
  border-radius: 16px;
  padding: 32px;
  max-width: 400px;
  width: 90%;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
  animation: slideUp 0.3s;
}

@keyframes slideUp {
  from {
    transform: translateY(50px);
    opacity: 0;
  }
  to {
    transform: translateY(0);
    opacity: 1;
  }
}

.warning-icon {
  width: 80px;
  height: 80px;
  margin: 0 auto 20px;
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 48px;
  color: white;
}

.quota-warning-modal h3 {
  font-size: 22px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 20px 0;
}

.quota-info {
  background: #f9fafb;
  border-radius: 12px;
  padding: 20px;
  margin-bottom: 20px;
}

.quota-info p {
  margin: 8px 0;
  font-size: 15px;
  color: #4b5563;
}

.quota-info strong {
  color: #1f2937;
  font-weight: 600;
}

.upgrade-tip {
  margin-bottom: 24px;
}

.upgrade-tip p {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.action-buttons {
  display: flex;
  gap: 12px;
}

.btn-upgrade {
  flex: 1;
  background: linear-gradient(135deg, #3b82f6, #2563eb);
  color: white;
  border: none;
  padding: 14px 24px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: all 0.3s;
}

.btn-upgrade:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(59, 130, 246, 0.4);
}

.btn-cancel {
  flex: 1;
  background: white;
  color: #6b7280;
  border: 2px solid #e5e7eb;
  padding: 14px 24px;
  border-radius: 10px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-cancel:hover {
  background: #f9fafb;
  border-color: #d1d5db;
}
</style>
