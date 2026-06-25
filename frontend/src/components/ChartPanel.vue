<template>
  <div class="panel" :style="{ flex: flex }">
    <div v-if="showTitle" class="panel-hd">
      <span class="panel-title">{{ title }}</span>
      <span v-if="badge" class="panel-badge">{{ badge }}</span>
    </div>
    <div class="chart-box" ref="chartRef"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, watch, nextTick } from 'vue'
import * as echarts from 'echarts'

const props = defineProps({
  title: { type: String, default: '' },
  badge: { type: String, default: '' },
  flex: { type: [String, Number], default: 1 },
  option: { type: Object, default: null },
  watchData: { type: Boolean, default: true },
  showTitle: { type: Boolean, default: true }
})

const chartRef = ref(null)
let chartInstance = null

function initChart() {
  if (!chartRef.value || !chartRef.value.clientWidth) return
  if (chartInstance) {
    chartInstance.dispose()
  }
  chartInstance = echarts.init(chartRef.value, null, { devicePixelRatio: 2 })
  if (props.option) {
    chartInstance.setOption(props.option, true)
  }
}

function resizeChart() {
  chartInstance?.resize()
}

let resizeObserver = null

onMounted(() => {
  nextTick(() => {
    initChart()
  })
  resizeObserver = new ResizeObserver(() => {
    resizeChart()
  })
  if (chartRef.value) {
    resizeObserver.observe(chartRef.value)
  }
  window.addEventListener('resize', resizeChart)
})

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
  resizeObserver?.disconnect()
  chartInstance?.dispose()
})

watch(
  () => props.option,
  (newOpt) => {
    if (newOpt && chartInstance) {
      chartInstance.setOption(newOpt, true)
    } else if (newOpt) {
      nextTick(() => initChart())
    }
  },
  { deep: true }
)

defineExpose({ getChart: () => chartInstance, resize: resizeChart })
</script>

<style scoped>
.panel {
  background: var(--bg-card);
  border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg);
  display: flex;
  flex-direction: column;
  min-height: 0;
}
.panel-hd {
  padding: 14px 18px 0;
  display: flex;
  align-items: center;
  justify-content: space-between;
  flex-shrink: 0;
}
.panel-title {
  font-size: 15px;
  font-weight: 600;
  color: var(--text-primary);
}
.panel-badge {
  font-size: 10px;
  color: var(--accent);
  background: var(--accent-dim);
  padding: 2px 8px;
  border-radius: 100px;
  font-weight: 600;
}
.chart-box {
  flex: 1;
  min-height: 0;
  position: relative;
}
</style>
