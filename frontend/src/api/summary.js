import request from './request'

/** 首页总览 AI 结论 */
export function getDashboardSummary() {
  return request.get('/dashboard/overview/summary')
}

/** 区域房源分布 AI 结论 */
export function getRegionDistributionSummary() {
  return request.get('/listing/region-distribution/summary')
}

/** 房型与房东经营 AI 结论 */
export function getRoomHostSummary() {
  return request.get('/listing/room-host/summary')
}

/** 区域价格水平 AI 结论 */
export function getRegionLevelSummary() {
  return request.get('/price/region-level/summary')
}

/** 价格热度与波动 AI 结论 */
export function getHeatTrendSummary() {
  return request.get('/price/heat-trend/summary')
}

/** 评论分析 AI 结论 */
export function getReviewSummary() {
  return request.get('/review/summary')
}
