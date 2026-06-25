<template>
  <div class="page">
    <div class="pg-hd">
      <div class="pg-title">区域房源分布分析</div>
      <div class="pg-desc">统计 Boston 各行政区的房源数量与排名，识别高供给区域与房源聚集格局。</div>
    </div>
    <div class="pg-body">
      <div class="grid cols-2r1" style="flex:1">
        <div class="col">
          <ChartPanel title="各区域房源数量分布" :option="barChartOption" />
        </div>
        <div class="col">
          <div class="panel" style="flex:1">
            <div class="panel-hd"><span class="panel-title">区域房源数量排行</span></div>
            <DataTable :columns="tableColumns" :data="tableData">
              <template #cell-rankingNo="{ index }">
                <span class="rnk">{{ index + 1 }}</span>
              </template>
              <template #cell-houseCount="{ value }">
                <span class="val">{{ fmtNum(value) }}</span>
              </template>
            </DataTable>
          </div>
        </div>
      </div>
      <ConclusionBox title="分析结论">
        <strong>高供给区域：</strong>Dorchester（358 套）、Jamaica Plain（287 套）、South End（241 套）最集中，合计占比约 24.7%。<br />
        <strong>中等供给：</strong>Back Bay、Fenway、Roxbury、East Boston 在 150~200 套之间。<br />
        <strong>低供给区域：</strong>Longwood（42 套）、Downtown/ Mattapan（52 套）房源较少，供给呈"东北-西南"带状聚集格局。
      </ConclusionBox>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import ChartPanel from '../components/ChartPanel.vue'
import DataTable from '../components/DataTable.vue'
import ConclusionBox from '../components/ConclusionBox.vue'
import { getRegionCount } from '../api/listing'
import { tooltipConfig, axisLabelStyle, axisLineStyle, splitLineStyle, CHART_COLORS, paletteColor } from '../utils/chart-theme'
import { formatNumber } from '../utils/helpers'

const fmtNum = formatNumber
const regionData = ref([])

const sortedData = computed(() => [...regionData.value].sort((a, b) => b.houseCount - a.houseCount))

const tableColumns = [
  { key: 'rankingNo', title: '#', align: 'rnk' },
  { key: 'neighbourhoodCleansed', title: '区域' },
  { key: 'houseCount', title: '房源数', align: 'val' }
]

const tableData = computed(() => sortedData.value)

const barChartOption = computed(() => ({
  tooltip: tooltipConfig(),
  grid: { left: '3%', right: '6%', bottom: '8%', top: '8%', containLabel: true },
  xAxis: {
    type: 'category',
    data: regionData.value.map(d => d.neighbourhoodCleansed),
    axisLabel: { ...axisLabelStyle(14), rotate: 45 },
    axisLine: axisLineStyle()
  },
  yAxis: {
    type: 'value',
    name: '房源数量',
    nameTextStyle: { color: CHART_COLORS.textSecondary, fontSize: 15 },
    axisLabel: axisLabelStyle(14),
    splitLine: splitLineStyle()
  },
  series: [{
    type: 'bar',
    data: regionData.value.map((d, i) => ({
      value: d.houseCount,
      itemStyle: {
        color: {
          type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
          colorStops: [
            { offset: 0, color: paletteColor(i) },
            { offset: 1, color: 'rgba(0,0,0,0.15)' }
          ]
        },
        borderRadius: [4, 4, 0, 0]
      }
    })),
    barWidth: '70%'
  }]
}))

onMounted(async () => {
  try {
    regionData.value = await getRegionCount() || []
  } catch (e) {
    console.error('Failed to load region data:', e)
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
.grid.cols-2r1 > .col:first-child { flex: 2; }
.grid.cols-2r1 > .col:last-child { flex: 1; }
.grid > .col { display: flex; flex-direction: column; gap: 12px; min-width: 0; }

.panel {
  background: var(--bg-card); border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg); display: flex; flex-direction: column; min-height: 0;
}
.panel-hd { padding: 14px 18px 0; display: flex; align-items: center; justify-content: space-between; flex-shrink: 0; }
.panel-title { font-size: 15px; font-weight: 600; color: var(--text-primary); }

.rnk { color: var(--accent); font-weight: 600; }
.val { text-align: right; font-weight: 600; color: var(--text-primary); }

@media (max-width: 900px) {
  .pg-hd { padding: 16px 16px 0; }
  .pg-body { padding: 10px 16px 12px; }
  .grid { flex-direction: column; }
}
</style>
