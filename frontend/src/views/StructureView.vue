<template>
  <div class="page">
    <div class="pg-hd">
      <div class="pg-title">房型结构与房东经营分析</div>
      <div class="pg-desc">分析平台上不同房型的占比结构，识别多房源经营者，判断供给主体类型与经营集中度。</div>
    </div>
    <div class="pg-body">
      <div class="grid" style="flex:1">
        <div class="col" style="flex:5">
          <ChartPanel title="房型占比分布" :option="pieChartOption" />
          <ChartPanel title="房型数量对比" :option="barChartOption" />
        </div>
        <div class="col" style="flex:4">
          <ChartPanel title="房东房源数量 Top 15" :option="hostChartOption" />
        </div>
      </div>
      <ConclusionBox title="分析结论">
        <strong>房型结构：</strong>Entire home/apt 占绝对主导（62.4%），Private room 占 35.5%，Shared room 仅 2.1%。波士顿以整租房源为主，适合家庭和商务出行。<br />
        <strong>房东格局：</strong>Top 15 合计持有 520 套房源，排名第一持有 105 套，存在明显职业化房东群体。约 70% 房东仅持有 1 套，呈典型长尾分布。
      </ConclusionBox>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ChartPanel from '../components/ChartPanel.vue'
import ConclusionBox from '../components/ConclusionBox.vue'
import { getRoomTypeRatio, getHostTopN } from '../api/listing'
import { tooltipConfig, gridConfig, axisLabelStyle, axisLineStyle, splitLineStyle, CHART_COLORS } from '../utils/chart-theme'

const roomTypeData = ref([])
const hostData = ref([])

const pieChartOption = computed(() => ({
  tooltip: tooltipConfig(),
  legend: { bottom: 0, textStyle: { color: CHART_COLORS.textPrimary, fontSize: 14 } },
  series: [{
    type: 'pie',
    radius: ['45%', '72%'],
    center: ['50%', '46%'],
    data: roomTypeData.value.map(d => ({ name: d.roomType, value: d.houseCount })),
    label: { show: true, formatter: '{b}\n{d}%', color: CHART_COLORS.textSecondary, fontSize: 13 },
    itemStyle: { borderColor: '#080c14', borderWidth: 4, borderRadius: 4 },
    color: [CHART_COLORS.accent, CHART_COLORS.blue, CHART_COLORS.green],
    emphasis: { scaleSize: 8, label: { fontSize: 15, fontWeight: 'bold' } }
  }]
}))

const barChartOption = computed(() => ({
  tooltip: tooltipConfig(),
  grid: gridConfig(),
  xAxis: {
    type: 'category',
    data: roomTypeData.value.map(d => d.roomType),
    axisLabel: axisLabelStyle(14),
    axisLine: axisLineStyle()
  },
  yAxis: {
    type: 'value',
    name: '房源数',
    nameTextStyle: { color: CHART_COLORS.textSecondary, fontSize: 15 },
    axisLabel: axisLabelStyle(14),
    splitLine: splitLineStyle()
  },
  series: [{
    type: 'bar',
    data: roomTypeData.value.map((d, i) => ({
      value: d.houseCount,
      itemStyle: {
        color: [CHART_COLORS.accent, CHART_COLORS.blue, CHART_COLORS.green][i],
        borderRadius: [6, 6, 0, 0]
      }
    })),
    barWidth: '45%'
  }]
}))

const hostChartOption = computed(() => {
  const sorted = [...hostData.value].reverse()
  return {
    tooltip: tooltipConfig(),
    grid: { left: '14%', right: '6%', bottom: '6%', top: '6%', containLabel: true },
    xAxis: {
      type: 'value',
      name: '房源数',
      nameTextStyle: { color: CHART_COLORS.textSecondary, fontSize: 15 },
      axisLabel: axisLabelStyle(14),
      splitLine: splitLineStyle()
    },
    yAxis: {
      type: 'category',
      data: sorted.map(d => d.hostName),
      axisLabel: axisLabelStyle(14),
      axisLine: axisLineStyle()
    },
    series: [{
      type: 'bar',
      data: sorted.map(d => ({
        value: d.houseCount,
        itemStyle: {
          color: {
            type: 'linear', x: 0, y: 0, x2: 1, y2: 0,
            colorStops: [
              { offset: 0, color: CHART_COLORS.accent },
              { offset: 1, color: CHART_COLORS.amber }
            ]
          },
          borderRadius: [0, 6, 6, 0]
        }
      })),
      barWidth: '60%'
    }]
  }
})

onMounted(async () => {
  try {
    const [roomRes, hostRes] = await Promise.all([
      getRoomTypeRatio(),
      getHostTopN(15)
    ])
    roomTypeData.value = roomRes || []
    hostData.value = hostRes || []
  } catch (e) {
    console.error('Failed to load structure data:', e)
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
.grid > .col { display: flex; flex-direction: column; gap: 12px; min-width: 0; }

@media (max-width: 900px) {
  .pg-hd { padding: 16px 16px 0; }
  .pg-body { padding: 10px 16px 12px; }
  .grid { flex-direction: column; }
}
</style>
