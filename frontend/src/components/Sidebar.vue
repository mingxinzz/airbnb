<template>
  <aside class="sidebar">
    <div class="sb-brand">
      <div class="sb-logo">A</div>
      <div>
        <div class="sb-title">Airbnb Analytics</div>
        <div class="sb-sub">Boston 数据分析平台</div>
      </div>
    </div>

    <nav class="sb-nav">
      <div class="nav-sec">导航</div>
      <router-link
        v-for="item in mainNav"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        active-class="active"
      >
        <span class="nav-ic">{{ item.meta.icon }}</span>
        {{ item.meta.title }}
      </router-link>

      <div class="nav-sec">专题分析</div>
      <router-link
        v-for="item in analysisNav"
        :key="item.path"
        :to="item.path"
        class="nav-item"
        active-class="active"
      >
        <span class="nav-ic">{{ item.meta.icon }}</span>
        {{ item.meta.title }}
      </router-link>
    </nav>

    <div class="sb-foot">
      <span class="sb-dot"></span>
      系统运行中 · v1.0
    </div>
  </aside>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()
const routes = router.getRoutes()

const mainNav = routes.filter(r => ['dashboard', 'ingest'].includes(r.name))
const analysisNav = routes.filter(r => !['dashboard', 'ingest'].includes(r.name))
</script>

<style scoped>
.sidebar {
  width: var(--sidebar-w);
  height: 100vh;
  background: var(--bg-surface);
  border-right: 1px solid var(--border-subtle);
  display: flex;
  flex-direction: column;
  flex-shrink: 0;
  z-index: 10;
}
.sb-brand {
  padding: 20px 16px 16px;
  border-bottom: 1px solid var(--border-subtle);
  display: flex;
  align-items: center;
  gap: 12px;
}
.sb-logo {
  width: 38px;
  height: 38px;
  background: var(--accent);
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  color: #fff;
  font-weight: 700;
  flex-shrink: 0;
}
.sb-title {
  font-size: 16px;
  font-weight: 600;
  color: var(--text-primary);
}
.sb-sub {
  font-size: 12px;
  color: var(--text-secondary);
}
.sb-nav {
  flex: 1;
  padding: 12px 10px;
  overflow-y: auto;
  display: flex;
  flex-direction: column;
  gap: 1px;
}
.nav-sec {
  font-size: 11px;
  text-transform: uppercase;
  letter-spacing: 0.08em;
  color: var(--text-muted);
  padding: 14px 10px 4px;
  font-weight: 600;
}
.nav-item {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 9px 12px;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.25s;
  color: var(--text-secondary);
  font-size: 14px;
  font-weight: 500;
  text-decoration: none;
  position: relative;
}
.nav-item:hover {
  background: var(--bg-elevated);
  color: var(--text-primary);
}
.nav-item.active {
  background: var(--accent-dim);
  color: var(--accent);
}
.nav-item.active::before {
  content: '';
  position: absolute;
  left: -10px;
  top: 50%;
  transform: translateY(-50%);
  width: 3px;
  height: 18px;
  background: var(--accent);
  border-radius: 0 3px 3px 0;
}
.nav-ic {
  font-size: 15px;
  width: 18px;
  text-align: center;
  flex-shrink: 0;
}
.sb-foot {
  padding: 12px 16px;
  border-top: 1px solid var(--border-subtle);
  font-size: 10px;
  color: var(--text-muted);
  display: flex;
  align-items: center;
  gap: 6px;
}
.sb-dot {
  width: 6px;
  height: 6px;
  background: var(--green);
  border-radius: 50%;
  box-shadow: 0 0 5px var(--green);
}

@media (max-width: 900px) {
  .sidebar { display: none; }
}
</style>
