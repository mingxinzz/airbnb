<template>
  <div class="conclusion">
    <div class="conc-title">📋 {{ title }}</div>
    <div class="conc-text"><slot /></div>
  </div>
</template>

<script setup>
defineProps({
  title: { type: String, default: '分析结论' }
})
</script>

<style scoped>
.conclusion {
  flex-shrink: 0;
  background: var(--bg-elevated);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius);
  padding: 20px 26px;
}
.conc-title {
  font-size: 13px;
  color: var(--accent);
  font-weight: 600;
  text-transform: uppercase;
  letter-spacing: 0.06em;
  margin-bottom: 10px;
}
.conc-text {
  font-size: 15px;
  color: var(--text-secondary);
  line-height: 1.7;
}
.conc-text :deep(strong) {
  color: var(--text-primary);
}
</style>
