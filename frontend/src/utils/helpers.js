/** Format number with commas */
export function formatNumber(num) {
  if (num === null || num === undefined) return '--'
  return Number(num).toLocaleString('en-US')
}

/** Format price with $ sign */
export function formatPrice(price) {
  if (price === null || price === undefined) return '--'
  return `$${Number(price).toLocaleString('en-US', { minimumFractionDigits: 2, maximumFractionDigits: 2 })}`
}

/** Format ratio as percentage */
export function formatRatio(ratio) {
  if (ratio === null || ratio === undefined) return '--'
  return `${(Number(ratio) * 100).toFixed(1)}%`
}

/** Format short number (e.g. 68.3K) */
export function formatShortNum(num) {
  if (num === null || num === undefined) return '--'
  if (num >= 1000) {
    return `${(num / 1000).toFixed(1)}K`
  }
  return String(num)
}

/** Get pipeline stage label */
export function getStageLabel(stage) {
  const map = {
    CREATED: '已创建',
    UPLOAD_RAW_TO_HDFS: 'HDFS 导入',
    CLEAN_DATA: '数据清洗',
    UPLOAD_CLEAN_TO_HDFS: '清洗数据上传',
    HIVE_DWD: 'Hive DWD',
    HIVE_ADS: 'Hive 分析',
    SQOOP_EXPORT: 'Sqoop 导出',
    FINISHED: '已完成',
    FAILED: '失败'
  }
  return map[stage] || stage
}

/** Get task status label */
export function getStatusLabel(status) {
  const map = {
    CREATED: '已创建',
    RUNNING: '执行中',
    FINISHED: '已完成',
    FAILED: '失败'
  }
  return map[status] || status
}
