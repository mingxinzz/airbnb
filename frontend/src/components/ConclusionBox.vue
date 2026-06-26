<template>
  <div class="conclusion">
    <div class="conc-title">📋 {{ displayTitle }}</div>
    <div class="conc-text">
      <!-- Loading -->
      <div v-if="loading" class="conc-status">⏳ 分析结论生成中...</div>
      <!-- Error -->
      <div v-else-if="error" class="conc-status conc-error">分析结论暂不可用</div>
      <!-- Empty -->
      <div v-else-if="!summaryItems.length && !$slots.default" class="conc-status">暂无分析结论</div>
      <!-- Dynamic items -->
      <p v-for="item in summaryItems" :key="item.label">
        <strong>{{ item.label }}：</strong>{{ item.content }}
      </p>
      <!-- Fallback slot for static content -->
      <slot v-if="!summaryItems.length" />
    </div>
    <div v-if="generatedAt && summaryItems.length" class="conc-meta">
      生成于 {{ generatedAt }}
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'

const props = defineProps({
  title: { type: String, default: '分析结论' },
  panelTitle: { type: String, default: '' },
  summaryItems: { type: Array, default: () => [] },
  loading: { type: Boolean, default: false },
  error: { type: Boolean, default: false },
  generatedAt: { type: String, default: '' }
})

const displayTitle = computed(() => props.panelTitle || props.title)
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
.conc-status {
  color: var(--text-muted);
  font-style: italic;
}
.conc-error {
  color: var(--accent);
}
.conc-meta {
  font-size: 11px;
  color: var(--text-muted);
  margin-top: 10px;
  text-align: right;
  opacity: 0.6;
}
</style>
