// ECharts dark theme colors matching the prototype
export const CHART_COLORS = {
  accent: '#FF5A5F',
  blue: '#60a5fa',
  green: '#34d399',
  amber: '#fbbf24',
  purple: '#a78bfa',
  cyan: '#22d3ee',
  textMuted: '#8894a8',
  textSecondary: '#bcc6d4',
  textPrimary: '#f0f4fa',
  border: 'rgba(255, 255, 255, 0.08)'
}

const PALETTE = [
  CHART_COLORS.accent,
  CHART_COLORS.blue,
  CHART_COLORS.green,
  CHART_COLORS.amber,
  CHART_COLORS.purple,
  CHART_COLORS.cyan,
  '#f472b6',
  '#38bdf8',
  '#fb923c',
  '#a3e635'
]

/** Get common grid config */
export function gridConfig(overrides = {}) {
  return { left: '3%', right: '4%', bottom: '10%', top: '16%', containLabel: true, ...overrides }
}

/** Get common tooltip config */
export function tooltipConfig() {
  return {
    backgroundColor: 'rgba(15, 23, 36, 0.95)',
    borderColor: CHART_COLORS.border,
    textStyle: { color: CHART_COLORS.textPrimary, fontSize: 14 },
    extraCssText: 'border-radius:8px;box-shadow:0 4px 20px rgba(0,0,0,0.6)'
  }
}

/** Get axis text style */
export function axisLabelStyle(fontSize = 14) {
  return { color: CHART_COLORS.textSecondary, fontSize }
}

/** Get axis line style */
export function axisLineStyle() {
  return { lineStyle: { color: CHART_COLORS.border } }
}

/** Get split line style */
export function splitLineStyle() {
  return { lineStyle: { color: CHART_COLORS.border } }
}

/** Get gradient color for bar charts */
export function gradientBar(startColor, endColor = 'rgba(0,0,0,0.15)') {
  return {
    type: 'linear',
    x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: startColor },
      { offset: 1, color: endColor }
    ]
  }
}

/** Get area gradient for line charts */
export function areaGradient(color) {
  return {
    type: 'linear',
    x: 0, y: 0, x2: 0, y2: 1,
    colorStops: [
      { offset: 0, color: color.replace(')', ',0.2)').replace('rgb', 'rgba') },
      { offset: 1, color: color.replace(')', ',0.01)').replace('rgb', 'rgba') }
    ]
  }
}

/** Pick a color from palette */
export function paletteColor(index) {
  return PALETTE[index % PALETTE.length]
}

const COLORS = { ...CHART_COLORS, palette: PALETTE }
export default COLORS
