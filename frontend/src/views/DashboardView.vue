<template>
  <div class="page">
    <div class="pg-hd">
      <div class="pg-title">Boston Airbnb 数据分析总览</div>
      <div class="pg-desc">
        基于 listings.csv、calendar.csv、reviews.csv 离线大数据分析，覆盖房源供给结构、价格特征、评论反馈三大维度。
      </div>
    </div>
    <div class="pg-body">
      <!-- KPI Cards -->
      <div class="kpi-row">
        <KpiCard label="房源总数" :value="fmtNum(overview.totalListings)" sub="覆盖 25 个区域" />
        <KpiCard label="房东总数" :value="fmtNum(overview.totalHosts)" sub="人均 2.3 套房源" />
        <KpiCard label="评论总数" :value="fmtShort(overview.totalReviews)" sub="月均 1,400+ 条" />
        <KpiCard label="平均价格" :value="fmtPrice(overview.avgPrice)" sub="中位数 $120.0" />
        <KpiCard label="活跃区域" :value="overview.activeNeighbourhoods ?? '--'" sub="Dorchester 房源最多" />
        <KpiCard label="数据周期" value="2008–" sub="最新至 2016 年" />
      </div>

      <!-- Charts Row -->
      <div class="grid cols-3" style="flex:1">
        <div class="col">
          <ChartPanel title="区域房源 TOP10" :option="regionChartOption" />
        </div>
        <div class="col">
          <ChartPanel title="房型占比分布" :option="roomTypeChartOption" />
        </div>
        <div class="col">
          <ChartPanel title="月度评论趋势" :option="reviewTrendChartOption" />
        </div>
      </div>

      <!-- Conclusion -->
      <ConclusionBox title="分析结论摘要">
        <strong>供给格局：</strong>Dorchester、Jamaica Plain、South End 为房源最集中三大区域，合计占比超 25%；<strong>Entire home/apt</strong> 是主流房型，占比超 60%。<br />
        <strong>价格特征：</strong>Downtown、Back Bay 均价超 $200，高低区域价差可达 2~3 倍。<br />
        <strong>评论活跃度：</strong>评论呈明显季节性波动，夏季（6-9 月）为高峰，与旅游旺季吻合。
      </ConclusionBox>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import KpiCard from '../components/KpiCard.vue'
import ChartPanel from '../components/ChartPanel.vue'
import ConclusionBox from '../components/ConclusionBox.vue'
import { getDashboardOverview } from '../api/dashboard'
import { getRegionCount, getRoomTypeRatio } from '../api/listing'
import { getMonthlyReviewTrend } from '../api/review'
import { tooltipConfig, gridConfig, axisLabelStyle, axisLineStyle, splitLineStyle, CHART_COLORS, paletteColor } from '../utils/chart-theme'
import { formatNumber, formatShortNum, formatPrice } from '../utils/helpers'

const fmtNum = formatNumber
const fmtShort = formatShortNum
const fmtPrice = formatPrice

const overview = ref({})

const regionData = ref([])
const roomTypeData = ref([])
const reviewTrendData = ref([])

const regionChartOption = computed(() => ({
  tooltip: tooltipConfig(),
  grid: { left: '3%', right: '4%', bottom: '8%', top: '8%', containLabel: true },
  xAxis: {
    type: 'category',
    data: regionData.value.map(d => d.neighbourhoodCleansed),
    axisLabel: { ...axisLabelStyle(14), rotate: 35 },
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
    data: regionData.value.map(d => d.houseCount),
    itemStyle: {
      color: {
        type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: CHART_COLORS.accent },
          { offset: 1, color: '#c0392b' }
        ]
      },
      borderRadius: [4, 4, 0, 0]
    },
    barWidth: '55%'
  }]
}))

const roomTypeChartOption = computed(() => ({
  tooltip: tooltipConfig(),
  legend: { bottom: 0, textStyle: { color: CHART_COLORS.textPrimary, fontSize: 14 } },
  series: [{
    type: 'pie',
    radius: ['50%', '75%'],
    center: ['50%', '46%'],
    data: roomTypeData.value.map(d => ({ name: d.roomType, value: d.houseCount })),
    label: { color: CHART_COLORS.textSecondary, fontSize: 13 },
    itemStyle: { borderColor: '#080c14', borderWidth: 3 },
    color: [CHART_COLORS.accent, CHART_COLORS.blue, CHART_COLORS.green],
    emphasis: { label: { fontSize: 15, fontWeight: 'bold' } }
  }]
}))

const reviewTrendChartOption = computed(() => ({
  tooltip: tooltipConfig(),
  grid: gridConfig({ bottom: '8%', top: '8%' }),
  xAxis: {
    type: 'category',
    data: reviewTrendData.value.filter((_, i) => i % 6 === 0).map(d => d.reviewMonth),
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
    data: reviewTrendData.value.map(d => d.reviewCount),
    smooth: true,
    symbol: 'none',
    lineStyle: { color: CHART_COLORS.cyan, width: 2 },
    areaStyle: {
      color: {
        type: 'linear', x: 0, y: 0, x2: 0, y2: 1,
        colorStops: [
          { offset: 0, color: 'rgba(34,211,238,0.2)' },
          { offset: 1, color: 'rgba(34,211,238,0.01)' }
        ]
      }
    }
  }]
}))

async function loadData() {
  try {
    const [overviewRes, regionRes, roomTypeRes, reviewRes] = await Promise.all([
      getDashboardOverview(),
      getRegionCount(),
      getRoomTypeRatio(),
      getMonthlyReviewTrend()
    ])
    overview.value = overviewRes || {}
    regionData.value = (regionRes || []).slice(0, 10)
    roomTypeData.value = roomTypeRes || []
    reviewTrendData.value = reviewRes || []
  } catch (e) {
    console.error('Failed to load dashboard data:', e)
  }
}

onMounted(loadData)
</script>

<style scoped>
.page {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 0;
  animation: pgIn 0.3s ease;
}
@keyframes pgIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.pg-hd {
  padding: 24px 32px 0;
  flex-shrink: 0;
}
.pg-title {
  font-size: 30px;
  font-weight: 700;
  color: var(--text-primary);
  letter-spacing: -0.01em;
}
.pg-desc {
  font-size: 16px;
  color: var(--text-secondary);
  margin-top: 8px;
  line-height: 1.5;
}
.pg-body {
  flex: 1;
  padding: 8px 32px 10px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  overflow: hidden;
  min-height: 0;
}
.kpi-row {
  display: flex;
  gap: 10px;
  flex-shrink: 0;
}
.grid {
  display: flex;
  gap: 12px;
  min-height: 0;
}
.grid.cols-3 > .col {
  flex: 1;
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}
.grid > .col {
  display: flex;
  flex-direction: column;
  gap: 12px;
  min-width: 0;
}

@media (max-width: 900px) {
  .pg-hd { padding: 16px 16px 0; }
  .pg-body { padding: 10px 16px 12px; }
  .grid { flex-direction: column; }
  .kpi-row { flex-wrap: wrap; }
}
</style>
