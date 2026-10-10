<template>
  <section class="empty-state">
    <component :is="icon" v-if="icon" class="empty-icon" />
    <h3>{{ title }}</h3>
    <p v-if="description">{{ description }}</p>
    <slot />
  </section>
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
    default: null
  }
})
</script>

<style scoped lang="scss">
.empty-state {
  display: flex;
  min-height: 220px;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  border: 1px dashed var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-panel);
  padding: 32px;
  text-align: center;
}

.empty-icon {
  width: 42px;
  height: 42px;
  color: var(--zq-text-muted);
}

h3 {
  margin: 14px 0 0;
  color: var(--zq-text-primary);
  font-size: 18px;
}

p {
  margin: 8px 0 0;
  color: var(--zq-text-secondary);
  font-size: 14px;
  line-height: 1.6;
}
</style>
