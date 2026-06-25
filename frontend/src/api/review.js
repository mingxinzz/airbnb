import request from './request'

/** Get monthly review trend */
export function getMonthlyReviewTrend() {
  return request.post('/review/month-trend/list', {})
}
