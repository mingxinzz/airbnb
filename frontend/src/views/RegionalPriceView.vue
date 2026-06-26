<template>
  <div class="page">
    <div class="pg-hd">
      <div class="pg-title">区域价格水平分析</div>
      <div class="pg-desc">按区域统计平均价格、最高/最低价格及样本数量，揭示区域间价格层级差异和溢价能力。</div>
    </div>
    <div class="pg-body">
      <!-- KPI Cards -->
      <div class="kpi-row">
        <KpiCard label="最高均价" :value="fmtPrice(maxAvg?.avgPrice)" :sub="`${maxAvg?.neighbourhoodCleansed || '--'} · ${maxAvg?.houseCount || 0} 套`" />
        <KpiCard label="最低均价" :value="fmtPrice(minAvg?.avgPrice)" :sub="`${minAvg?.neighbourhoodCleansed || '--'} · ${minAvg?.houseCount || 0} 套`" />
        <KpiCard label="整体均价" :value="fmtPrice(overallAvg)" sub="中位数 $120.0" />
        <KpiCard label="价格极差" :value="priceRange + '×'" :sub="`最高为最低的 ${priceRange} 倍`" />
      </div>

      <div class="grid cols-2" style="flex:1">
        <div class="col">
          <ChartPanel title="区域平均价格柱状图" :option="barChartOption" />
        </div>
        <div class="col">
          <ChartPanel title="区域价格排序对比" :option="lineChartOption" />
        </div>
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
import { ref, computed, onMounted } from 'vue'
import KpiCard from '../components/KpiCard.vue'
import ChartPanel from '../components/ChartPanel.vue'
import ConclusionBox from '../components/ConclusionBox.vue'
import { getRegionAvgPrice } from '../api/price'
import { getRegionLevelSummary } from '../api/summary'
import { tooltipConfig, axisLabelStyle, axisLineStyle, splitLineStyle, CHART_COLORS } from '../utils/chart-theme'
import { formatPrice } from '../utils/helpers'

const fmtPrice = formatPrice
const priceData = ref([])

// AI 结论
const summaryLoading = ref(false)
const summaryError = ref(false)
const summaryData = ref(null)

const sortedByAvg = computed(() => [...priceData.value].sort((a, b) => b.avgPrice - a.avgPrice))

const maxAvg = computed(() => sortedByAvg.value[0] || null)
const minAvg = computed(() => sortedByAvg.value[sortedByAvg.value.length - 1] || null)

const overallAvg = computed(() => {
  if (!priceData.value.length) return 0
  const sum = priceData.value.reduce((a, b) => a + (b.avgPrice || 0), 0)
  return sum / priceData.value.length
})

const priceRange = computed(() => {
  if (!maxAvg.value || !minAvg.value || !minAvg.value.avgPrice) return '--'
  return (maxAvg.value.avgPrice / minAvg.value.avgPrice).toFixed(1)
})

const barChartOption = computed(() => ({
  tooltip: {
    ...tooltipConfig(),
    formatter: (p) => `${p.name}<br/>均价: <b>$${p.value}</b>`
  },
  grid: { left: '3%', right: '6%', bottom: '8%', top: '8%', containLabel: true },
  xAxis: {
    type: 'category',
    data: sortedByAvg.value.map(d => d.neighbourhoodCleansed),
    axisLabel: { ...axisLabelStyle(14), rotate: 40 },
    axisLine: axisLineStyle()
  },
  yAxis: {
    type: 'value',
    name: '均价 ($)',
    nameTextStyle: { color: CHART_COLORS.textSecondary, fontSize: 15 },
    axisLabel: { ...axisLabelStyle(14), formatter: '${value}' },
    splitLine: splitLineStyle()
  },
  series: [{
    type: 'bar',
    data: sortedByAvg.value.map(d => ({
      value: d.avgPrice,
      itemStyle: {
        color: d.avgPrice > 180 ? CHART_COLORS.accent : d.avgPrice > 120 ? CHART_COLORS.blue : CHART_COLORS.green,
        borderRadius: [4, 4, 0, 0]
      }
    })),
    barWidth: '60%',
    markLine: {
      silent: true,
      data: [{ type: 'average', name: '整体均价', label: { color: CHART_COLORS.textMuted, fontSize: 11 } }],
      lineStyle: { color: CHART_COLORS.amber, type: 'dashed' }
    }
  }]
}))

const lineChartOption = computed(() => {
  const data = sortedByAvg.value.slice(0, 12)
  return {
    tooltip: tooltipConfig(),
    legend: {
      data: ['均价', '最高价', '最低价'],
      bottom: 0,
      textStyle: { color: CHART_COLORS.textPrimary, fontSize: 14 }
    },
    grid: { left: '3%', right: '4%', bottom: '12%', top: '8%', containLabel: true },
    xAxis: {
      type: 'category',
      data: data.map(d => d.neighbourhoodCleansed),
      axisLabel: { ...axisLabelStyle(14), rotate: 35 },
      axisLine: axisLineStyle()
    },
    yAxis: {
      type: 'value',
      name: '价格 ($)',
      nameTextStyle: { color: CHART_COLORS.textSecondary, fontSize: 15 },
      axisLabel: { ...axisLabelStyle(14), formatter: '${value}' },
      splitLine: splitLineStyle()
    },
    series: [
      {
        name: '均价', type: 'line', data: data.map(d => d.avgPrice),
        smooth: true, lineStyle: { color: CHART_COLORS.accent, width: 3 },
        symbol: 'circle', symbolSize: 8, itemStyle: { color: CHART_COLORS.accent }
      },
      {
        name: '最高价', type: 'line', data: data.map(d => d.maxPrice),
        smooth: true, lineStyle: { color: CHART_COLORS.purple, width: 1.5, type: 'dashed' },
        symbol: 'diamond', symbolSize: 6
      },
      {
        name: '最低价', type: 'line', data: data.map(d => d.minPrice),
        smooth: true, lineStyle: { color: CHART_COLORS.green, width: 1.5, type: 'dashed' },
        symbol: 'triangle', symbolSize: 6
      }
    ]
  }
})

async function loadSummary() {
  summaryLoading.value = true
  summaryError.value = false
  try {
    summaryData.value = await getRegionLevelSummary()
  } catch (e) {
    console.error('Failed to load summary:', e)
    summaryError.value = true
  } finally {
    summaryLoading.value = false
  }
}

onMounted(async () => {
  try {
    priceData.value = await getRegionAvgPrice() || []
  } catch (e) {
    console.error('Failed to load price data:', e)
  }
  loadSummary()
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
.grid { display: flex; gap: 12px; min-height: 0; }
.grid.cols-2 > .col { flex: 1; display: flex; flex-direction: column; gap: 12px; min-width: 0; }

@media (max-width: 900px) {
  .pg-hd { padding: 16px 16px 0; }
  .pg-body { padding: 10px 16px 12px; }
  .grid { flex-direction: column; }
  .kpi-row { flex-wrap: wrap; }
}
</style>
