<template>
  <div class="kpi-card">
    <div class="kpi-label">{{ label }}</div>
    <div class="kpi-value">{{ value }}</div>
    <div v-if="sub" class="kpi-sub">{{ sub }}</div>
  </div>
</template>

<script setup>
defineProps({
  label: { type: String, required: true },
  value: { type: [String, Number], required: true },
  sub: { type: String, default: '' }
})
</script>

<style scoped>
.kpi-card {
  flex: 1;
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius);
  padding: 16px 18px;
  transition: all 0.25s;
  min-width: 0;
}
.kpi-card:hover {
  transform: translateY(-1px);
  border-color: var(--border-mid);
}
.kpi-label {
  font-size: 13px;
  color: var(--text-muted);
  font-weight: 500;
  text-transform: uppercase;
  letter-spacing: 0.05em;
  margin-bottom: 6px;
}
.kpi-value {
  font-size: 40px;
  font-weight: 700;
  color: var(--text-primary);
  line-height: 1;
  letter-spacing: -0.02em;
}
.kpi-sub {
  font-size: 13px;
  color: var(--text-secondary);
  margin-top: 4px;
}
</style>
