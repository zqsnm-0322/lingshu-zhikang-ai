<template>
  <header class="page-header">
    <div>
      <p v-if="eyebrow" class="page-eyebrow">{{ eyebrow }}</p>
      <h1>{{ title }}</h1>
      <p v-if="subtitle">{{ subtitle }}</p>
    </div>
    <div v-if="$slots.actions" class="page-actions">
      <slot name="actions" />
    </div>
  </header>
</template>

<script setup>
defineProps({
  title: {
    type: String,
    required: true
  },
  subtitle: {
    type: String,
    default: ''
  },
  eyebrow: {
    type: String,
    default: ''
  }
})
</script>

<style scoped lang="scss">
.page-header {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
  gap: 20px;
}

.page-eyebrow {
  margin: 0 0 6px;
  color: var(--zq-color-primary);
  font-size: 13px;
  font-weight: 700;
}

h1 {
  margin: 0;
  color: var(--zq-text-primary);
  font-size: 28px;
  font-weight: 700;
  line-height: 1.25;
}

p {
  margin: 8px 0 0;
  color: var(--zq-text-secondary);
  font-size: 14px;
  line-height: 1.6;
}

.page-actions {
  display: flex;
  flex-wrap: wrap;
  align-items: center;
  justify-content: flex-end;
  gap: 10px;
}

@media (max-width: 760px) {
  .page-header {
    flex-direction: column;
  }

  h1 {
    font-size: 24px;
  }

  .page-actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
