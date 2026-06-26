<template>
  <div class="page">
    <div class="pg-hd">
      <div class="pg-title">评论分析</div>
      <div class="pg-desc">分析评论活跃度的时间变化趋势，探索评论数量随时间的增长规律与季节性波动模式。</div>
    </div>
    <div class="pg-body">
      <!-- KPI Cards -->
      <div class="kpi-row">
        <KpiCard label="评论峰值月份" value="8 月" sub="月均 2,400+ 条" />
        <KpiCard label="评论低谷月份" value="1 月" sub="月均 600+ 条" />
        <KpiCard label="年增长率" value="35%" sub="2009→2016 年均" />
        <KpiCard label="总计评论" :value="fmtShort(totalReviews)" sub="2008-2016 累计" />
      </div>

      <!-- Chart -->
      <div class="panel" style="flex:1">
        <div class="panel-hd"><span class="panel-title">月度评论趋势（2009-2016）</span></div>
        <div class="chart-box" ref="chartRef"></div>
      </div>

      <ConclusionBox
        :panel-title="summaryData?.panelTitle"
        :summary-items="summaryData?.summaryItems || []"
        :loading="summaryLoading"
        :error="summaryError"
        :generated-at="summaryData?.generatedAt"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, nextTick, watch } from 'vue'
import * as echarts from 'echarts'
import KpiCard from '../components/KpiCard.vue'
import ConclusionBox from '../components/ConclusionBox.vue'
import { getMonthlyReviewTrend } from '../api/review'
import { getReviewSummary } from '../api/summary'
import { tooltipConfig, axisLabelStyle, axisLineStyle, splitLineStyle, CHART_COLORS } from '../utils/chart-theme'
import { formatShortNum } from '../utils/helpers'

const fmtShort = formatShortNum
const reviewData = ref([])

// AI 结论
const summaryLoading = ref(false)
const summaryError = ref(false)
const summaryData = ref(null)
const chartRef = ref(null)
let chartInstance = null

const totalReviews = computed(() => reviewData.value.reduce((sum, d) => sum + (d.reviewCount || 0), 0))

const chartOption = computed(() => ({
  tooltip: tooltipConfig(),
  grid: { left: '3%', right: '4%', bottom: '8%', top: '6%', containLabel: true },
  xAxis: {
    type: 'category',
    data: reviewData.value.filter((_, i) => i % 4 === 0).map(d => d.reviewMonth),
    axisLabel: axisLabelStyle(14),
    axisLine: axisLineStyle()
  },
  yAxis: {
    type: 'value',
    name: '评论数',
    nameTextStyle: { color: CHART_COLORS.textSecondary, fontSize: 15 },
    axisLabel: axisLabelStyle(14),
    splitLine: splitLineStyle()
  },
  series: [{
    type: 'line',
    data: reviewData.value.map(d => d.reviewCount),
    smooth: true,
    symbol: 'none',
    lineStyle: { color: CHART_COLORS.cyan, width: 2 },
    areaStyle: {
      color: {
        type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(34,211,238,0.25)' },
          { offset: 1, color: 'rgba(34,211,238,0.01)' }
        ]
      }
    }
  }]
}))

function initChart() {
  if (!chartRef.value || !chartRef.value.clientWidth) return
  if (chartInstance) chartInstance.dispose()
  chartInstance = echarts.init(chartRef.value, null, { devicePixelRatio: 2 })
}

function setChart() {
  if (!chartInstance) initChart()
  if (chartInstance && chartOption.value) {
    chartInstance.setOption(chartOption.value, true)
  }
}

function resizeChart() {
  chartInstance?.resize()
}

let resizeObserver = null

async function loadSummary() {
  summaryLoading.value = true
  summaryError.value = false
  try {
    summaryData.value = await getReviewSummary()
  } catch (e) {
    console.error('Failed to load summary:', e)
    summaryError.value = true
  } finally {
    summaryLoading.value = false
  }
}

onMounted(async () => {
  try {
    reviewData.value = await getMonthlyReviewTrend() || []
  } catch (e) {
    console.error('Failed to load review data:', e)
  }
  nextTick(() => { initChart(); setChart() })
  resizeObserver = new ResizeObserver(resizeChart)
  if (chartRef.value) resizeObserver.observe(chartRef.value)
  window.addEventListener('resize', resizeChart)
  loadSummary()
})

watch(chartOption, () => setChart(), { deep: true })

onUnmounted(() => {
  window.removeEventListener('resize', resizeChart)
  resizeObserver?.disconnect()
  chartInstance?.dispose()
})
</script>

<style scoped>
.page { display: flex; flex: 1; flex-direction: column; padding: 0; animation: pgIn 0.3s ease; }
@keyframes pgIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
.pg-hd { padding: 24px 32px 0; flex-shrink: 0; }
.pg-title { font-size: 30px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.01em; }
.pg-desc { font-size: 16px; color: var(--text-secondary); margin-top: 8px; line-height: 1.5; }
.pg-body { flex: 1; padding: 8px 32px 10px; display: flex; flex-direction: column; gap: 10px; overflow: hidden; min-height: 0; }

.kpi-row { display: flex; gap: 10px; flex-shrink: 0; }

.panel {
  background: var(--bg-card); border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg); display: flex; flex-direction: column; min-height: 0;
}
.panel-hd { padding: 14px 18px 0; display: flex; align-items: center; justify-content: space-between; flex-shrink: 0; }
.panel-title { font-size: 15px; font-weight: 600; color: var(--text-primary); }
.chart-box { flex: 1; min-height: 0; position: relative; }

@media (max-width: 900px) {
  .pg-hd { padding: 16px 16px 0; }
  .pg-body { padding: 10px 16px 12px; }
  .kpi-row { flex-wrap: wrap; }
}
</style>
