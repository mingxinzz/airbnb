import request from './request'

/**
 * Get dashboard overview data.
 * Aggregates from existing analytics tables (no dedicated dashboard_overview endpoint).
 */
export async function getDashboardOverview() {
  const [regions, roomTypes, reviews, prices] = await Promise.all([
    request.post('/listing/region-count/list', {}),
    request.post('/listing/room-type-ratio/list', {}),
    request.post('/review/month-trend/list', {}),
    request.post('/price/region-avg/list', {})
  ])

  const totalListings = (roomTypes || []).reduce((sum, r) => sum + (r.houseCount || 0), 0)
  const totalReviews = (reviews || []).reduce((sum, r) => sum + (r.reviewCount || 0), 0)
  const activeNeighbourhoods = (regions || []).length

  const totalHouses = (prices || []).reduce((sum, p) => sum + (p.houseCount || 0), 0)
  const avgPrice =
    totalHouses > 0
      ? (prices || []).reduce(
          (sum, p) => sum + Number(p.avgPrice || 0) * (p.houseCount || 0),
          0
        ) / totalHouses
      : null

  return {
    totalListings: totalListings || null,
    totalHosts: null,
    totalReviews: totalReviews || null,
    avgPrice,
    activeNeighbourhoods: activeNeighbourhoods || null
  }
}
