import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'dashboard',
    component: () => import('../views/DashboardView.vue'),
    meta: { title: '首页总览', icon: '📊' }
  },
  {
    path: '/ingest',
    name: 'ingest',
    component: () => import('../views/IngestView.vue'),
    meta: { title: '数据接入与任务状态', icon: '📥' }
  },
  {
    path: '/distribution',
    name: 'distribution',
    component: () => import('../views/DistributionView.vue'),
    meta: { title: '区域房源分布', icon: '📍' }
  },
  {
    path: '/structure',
    name: 'structure',
    component: () => import('../views/StructureView.vue'),
    meta: { title: '房型与房东经营', icon: '🏠' }
  },
  {
    path: '/regional-price',
    name: 'regional-price',
    component: () => import('../views/RegionalPriceView.vue'),
    meta: { title: '区域价格水平', icon: '💰' }
  },
  {
    path: '/price-dynamics',
    name: 'price-dynamics',
    component: () => import('../views/PriceDynamicsView.vue'),
    meta: { title: '价格热度与波动', icon: '📈' }
  },
  {
    path: '/review',
    name: 'review',
    component: () => import('../views/ReviewView.vue'),
    meta: { title: '评论分析', icon: '💬' }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
