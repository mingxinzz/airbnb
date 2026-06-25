import request from './request'

/** Get region house count rankings */
export function getRegionCount() {
  return request.post('/listing/region-count/list', {})
}

/** Get room type ratio */
export function getRoomTypeRatio() {
  return request.post('/listing/room-type-ratio/list', {})
}

/** Get host top N */
export function getHostTopN(limit = 15) {
  return request
    .post('/listing/host-topn/page', { pageNum: 1, pageSize: limit })
    .then((res) => res?.records ?? [])
}
