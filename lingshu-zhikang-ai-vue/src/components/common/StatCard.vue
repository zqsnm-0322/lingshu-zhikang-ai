<template>
  <section class="stat-card zq-card">
    <div class="stat-top">
      <span class="stat-icon" :class="`tone-${tone}`">
        <component :is="icon" />
      </span>
      <span v-if="trend" class="stat-trend" :class="{ down: trend.startsWith('-') }">{{ trend }}</span>
    </div>
    <div class="stat-value">{{ value }}</div>
    <div class="stat-label">{{ label }}</div>
  </section>
</template>

<script setup>
defineProps({
  label: {
    type: String,
    required: true
  },
  value: {
    type: [String, Number],
    required: true
  },
  icon: {
    type: [Object, Function],
    required: true
  },
  trend: {
    type: String,
    default: ''
  },
  tone: {
    type: String,
    default: 'blue'
  }
})
</script>

<style scoped lang="scss">
.stat-card {
  padding: 18px;
}

.stat-top {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.stat-icon {
  display: inline-flex;
  width: 40px;
  height: 40px;
  align-items: center;
  justify-content: center;
  border-radius: var(--zq-radius);
}

.stat-icon :deep(svg) {
  width: 22px;
  height: 22px;
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

.stat-trend {
  color: var(--zq-color-success);
  font-size: 13px;
  font-weight: 700;
}

.stat-trend.down {
  color: var(--zq-color-danger);
}

.stat-value {
  margin-top: 18px;
  color: var(--zq-text-primary);
  font-size: 30px;
  font-weight: 800;
  line-height: 1;
}

.stat-label {
  margin-top: 10px;
  color: var(--zq-text-secondary);
  font-size: 14px;
}
</style>
