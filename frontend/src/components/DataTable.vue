<template>
  <div class="tbl-wrap">
    <table class="tbl">
      <thead>
        <tr>
          <th v-for="col in columns" :key="col.key" :class="col.align">
            {{ col.title }}
          </th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(row, idx) in data" :key="idx">
          <td v-for="col in columns" :key="col.key" :class="col.align">
            <slot :name="`cell-${col.key}`" :row="row" :index="idx" :value="row[col.key]">
              {{ row[col.key] }}
            </slot>
          </td>
        </tr>
      </tbody>
    </table>
    <div v-if="!data.length" class="tbl-empty">暂无数据</div>
  </div>
</template>

<script setup>
defineProps({
  columns: { type: Array, required: true },
  data: { type: Array, default: () => [] }
})
</script>

<style scoped>
.tbl-wrap {
  flex: 1;
  overflow-y: auto;
  min-height: 0;
}
.tbl {
  width: 100%;
  border-collapse: collapse;
  font-size: 14px;
}
.tbl th {
  text-align: left;
  color: var(--text-muted);
  font-weight: 500;
  padding: 8px 10px;
  border-bottom: 1px solid var(--border-subtle);
  font-size: 13px;
}
.tbl td {
  padding: 7px 10px;
  color: var(--text-secondary);
  border-bottom: 1px solid rgba(255, 255, 255, 0.04);
}
.tbl tr:hover td {
  background: rgba(255, 255, 255, 0.02);
}
.tbl .rnk {
  color: var(--accent);
  font-weight: 600;
  width: 20px;
}
.tbl .val {
  text-align: right;
  font-weight: 600;
  color: var(--text-primary);
}
.tbl-empty {
  text-align: center;
  padding: 40px;
  color: var(--text-muted);
  font-size: 14px;
}
.tbl-wrap::-webkit-scrollbar { width: 4px; }
.tbl-wrap::-webkit-scrollbar-thumb { background: var(--border-mid); border-radius: 2px; }
</style>
