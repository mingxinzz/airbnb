<template>
  <div class="page">
    <div class="pg-hd">
      <div class="pg-title">价格与评论热度 / 日历价格波动分析</div>
      <div class="pg-desc">探索房源价格与评论热度之间的关联关系，同时展示日历价格在时间维度上的波动趋势。</div>
    </div>
    <div class="pg-body">
      <div class="grid cols-2" style="flex:1">
        <div class="col">
          <ChartPanel title="价格与评论数量散点图" :option="scatterChartOption" />
        </div>
        <div class="col">
          <ChartPanel title="日历价格波动趋势（月均）" :option="trendChartOption" />
        </div>
      </div>
      <ConclusionBox title="分析结论">
        <strong>价格与评论：</strong>评论集中在 $50~$200 区间。高价房源（>$300）评论普遍较少，但非严格负相关。<br />
        <strong>日历波动：</strong>波士顿民宿价格呈显著季节性波动。夏季（6-9 月）上浮约 25%-40%，9 月达峰值；冬季（12-2 月）回落谷底。
      </ConclusionBox>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ChartPanel from '../components/ChartPanel.vue'
import ConclusionBox from '../components/ConclusionBox.vue'
import { getPriceReviewScatter, getPriceTrend } from '../api/price'
import { tooltipConfig, gridConfig, axisLabelStyle, axisLineStyle, splitLineStyle, CHART_COLORS } from '../utils/chart-theme'

const scatterData = ref([])
const trendData = ref([])

const scatterChartOption = computed(() => ({
  tooltip: {
    ...tooltipConfig(),
    formatter: (p) => `价格: $${p.value[0]}<br/>评论数: ${p.value[1]}`
  },
  grid: gridConfig({ bottom: '8%' }),
  xAxis: {
    type: 'value',
    name: '价格 ($)',
    nameTextStyle: { color: CHART_COLORS.textSecondary, fontSize: 15 },
    axisLabel: { ...axisLabelStyle(14), formatter: '${value}' },
    splitLine: splitLineStyle()
  },
  yAxis: {
    type: 'value',
    name: '评论数量',
    nameTextStyle: { color: CHART_COLORS.textSecondary, fontSize: 15 },
    axisLabel: axisLabelStyle(14),
    splitLine: splitLineStyle()
  },
  series: [{
    type: 'scatter',
    data: scatterData.value.map(d => [d.price, d.numberOfReviews]),
    symbolSize: (val) => Math.sqrt(val[1]) * 3 + 3,
    itemStyle: {
      color: 'rgba(255,90,95,0.45)',
      borderColor: 'rgba(255,255,255,0.15)',
      borderWidth: 1
    },
    emphasis: {
      itemStyle: { color: CHART_COLORS.amber, borderColor: CHART_COLORS.accent, borderWidth: 2 }
    }
  }]
}))

const trendChartOption = computed(() => ({
  tooltip: tooltipConfig(),
  grid: gridConfig({ bottom: '8%' }),
  xAxis: {
    type: 'category',
    data: trendData.value.map(d => {
      const month = d.statMonth || d.statDate || ''
      const parts = month.split('-')
      return parts[1] ? `${parts[1]}月` : month
    }),
    axisLabel: axisLabelStyle(14),
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
    type: 'line',
    data: trendData.value.map(d => d.avgPrice),
    smooth: true,
    symbol: 'circle',
    symbolSize: 8,
    lineStyle: { color: CHART_COLORS.accent, width: 2.5 },
    itemStyle: { color: CHART_COLORS.accent },
    areaStyle: {
      color: {
        type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(255,90,95,0.15)' },
          { offset: 1, color: 'rgba(255,90,95,0.01)' }
        ]
      }
    },
    markPoints: {
      data: [
        { type: 'max', name: '最高', symbolSize: 36, itemStyle: { color: CHART_COLORS.accent }, label: { color: '#fff', fontSize: 11 } },
        { type: 'min', name: '最低', symbolSize: 36, itemStyle: { color: CHART_COLORS.blue }, label: { color: '#fff', fontSize: 11 } }
      ]
    }
  }]
}))

onMounted(async () => {
  try {
    const [scatterRes, trendRes] = await Promise.all([
      getPriceReviewScatter(300),
      getPriceTrend()
    ])
    scatterData.value = scatterRes || []
    trendData.value = trendRes || []
  } catch (e) {
    console.error('Failed to load price dynamics data:', e)
  }
})
</script>

<style scoped>
.page { display: flex; flex: 1; flex-direction: column; padding: 0; animation: pgIn 0.3s ease; }
@keyframes pgIn { from { opacity: 0; transform: translateY(8px); } to { opacity: 1; transform: translateY(0); } }
.pg-hd { padding: 24px 32px 0; flex-shrink: 0; }
.pg-title { font-size: 30px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.01em; }
.pg-desc { font-size: 16px; color: var(--text-secondary); margin-top: 8px; line-height: 1.5; }
.pg-body { flex: 1; padding: 8px 32px 10px; display: flex; flex-direction: column; gap: 10px; overflow: hidden; min-height: 0; }

.grid { display: flex; gap: 12px; min-height: 0; }
.grid.cols-2 > .col { flex: 1; display: flex; flex-direction: column; gap: 12px; min-width: 0; }

@media (max-width: 900px) {
  .pg-hd { padding: 16px 16px 0; }
  .pg-body { padding: 10px 16px 12px; }
  .grid { flex-direction: column; }
}
</style>
