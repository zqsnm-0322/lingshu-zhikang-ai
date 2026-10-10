<template>
  <button class="feature-card" type="button" @click="$emit('select')">
    <span class="feature-icon" :class="`tone-${tone}`">
      <component :is="icon" />
    </span>
    <span class="feature-content">
      <span class="feature-title">{{ title }}</span>
      <span class="feature-description">{{ description }}</span>
    </span>
    <span v-if="badge" class="feature-badge">{{ badge }}</span>
  </button>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  description: {
    type: String,
    default: ''
  },
  icon: {
    type: [Object, Function],
    required: true
  },
  badge: {
    type: String,
    default: ''
  },
  tone: {
    type: String,
    default: 'blue'
  }
})

defineEmits(['select'])
</script>

<style scoped lang="scss">
.feature-card {
  position: relative;
  display: flex;
  width: 100%;
  min-height: 116px;
  align-items: flex-start;
  gap: 14px;
  padding: 18px;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-panel);
  color: inherit;
  text-align: left;
  cursor: pointer;
  box-shadow: var(--zq-shadow-sm);
  transition: transform 0.2s ease, border-color 0.2s ease, box-shadow 0.2s ease;
}

.feature-card:hover {
  transform: translateY(-2px);
  border-color: rgba(37, 99, 235, 0.35);
  box-shadow: var(--zq-shadow-md);
}

.feature-icon {
  display: inline-flex;
  width: 42px;
  height: 42px;
  flex: 0 0 42px;
  align-items: center;
  justify-content: center;
  border-radius: var(--zq-radius);
}

.feature-icon :deep(svg) {
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

.tone-red {
  color: #dc2626;
  background: rgba(220, 38, 38, 0.1);
}

.feature-content {
  display: flex;
  min-width: 0;
  flex: 1;
  flex-direction: column;
  gap: 8px;
}

.feature-title {
  color: var(--zq-text-primary);
  font-size: 16px;
  font-weight: 700;
  line-height: 1.35;
}

.feature-description {
  color: var(--zq-text-secondary);
  font-size: 13px;
  line-height: 1.55;
}

.feature-badge {
  position: absolute;
  right: 14px;
  top: 14px;
  border-radius: 999px;
  background: var(--zq-bg-soft);
  color: var(--zq-color-primary);
  padding: 3px 8px;
  font-size: 12px;
  font-weight: 700;
}
</style>
