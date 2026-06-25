import request from './request'

/** Get region average price */
export function getRegionAvgPrice() {
  return request.post('/price/region-avg/list', {})
}

/** Get price vs review scatter data */
export function getPriceReviewScatter(limit = 500) {
  return request
    .post('/price/review-scatter/page', { pageNum: 1, pageSize: limit })
    .then((res) => res?.records ?? [])
}

/** Get calendar price trend */
export function getPriceTrend() {
  return request.post('/price/trend/list', {})
}
