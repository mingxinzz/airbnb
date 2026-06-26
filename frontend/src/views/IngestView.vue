<template>
  <div class="page">
    <div class="pg-hd">
      <div class="pg-title">数据接入与任务状态</div>
      <div class="pg-desc">
        上传原始数据文件，触发全链路自动化分析：HDFS 导入 → MapReduce 清洗 → Hive 分析 → Sqoop 导出 → MySQL 入库。
      </div>
    </div>
    <div class="pg-body">
      <!-- Upload Zone -->
      <div class="upload-zone" @click="triggerUpload" :class="{ dragging: isDragging }"
        @dragover.prevent="isDragging = true" @dragleave.prevent="isDragging = false" @drop.prevent="onDrop">
        <div class="up-ic">📁</div>
        <div class="up-txt">点击上传数据文件</div>
        <div class="up-hint">需同时提交 listings.csv · calendar.csv · reviews.csv</div>
        <input ref="fileInput" type="file" multiple accept=".csv" style="display:none" @change="onFileChange" />
      </div>

      <!-- File Status -->
      <div class="file-row">
        <div v-for="f in fileStatuses" :key="f.name" class="file-item">
          <span class="file-ic">{{ f.icon }}</span>
          <div style="flex:1;min-width:0">
            <div class="file-name">{{ f.name }}</div>
            <div class="file-meta" v-if="f.file">{{ f.file.size ? formatSize(f.file.size) : '' }}</div>
          </div>
          <span class="file-st" :class="{ ready: f.ready }">{{ f.ready ? '已就绪' : '待选择' }}</span>
        </div>
      </div>

      <!-- Buttons -->
      <div class="btn-row">
        <button class="btn btn-p" @click="startPipeline" :disabled="!canStart || isRunning">
          ▶ 启动全链路分析
        </button>
        <button class="btn btn-s" @click="refreshStatus">🔄 刷新任务状态</button>
      </div>

      <!-- Pipeline Stages -->
      <div class="panel" style="flex-shrink:0">
        <div class="panel-hd">
          <span class="panel-title">任务流水线 · #{{ currentTask?.taskId || '--' }}</span>
          <span class="panel-badge" :class="statusClass">{{ statusLabel }}</span>
        </div>
        <div style="padding:10px 16px">
          <div class="pipeline-row">
            <template v-for="(stage, idx) in pipelineStages" :key="stage.key">
              <div class="pnode" :class="stage.state">
                <span>{{ stage.icon }}</span>
                <span>{{ stage.label }}</span>
                <span class="pst" :class="stage.state">{{ stage.stateLabel }}</span>
              </div>
              <span v-if="idx < pipelineStages.length - 1" class="parrow">→</span>
            </template>
          </div>
        </div>
      </div>

      <!-- Log Panel -->
      <div class="panel" style="flex:1">
        <div class="panel-hd">
          <span class="panel-title">任务日志</span>
          <button class="log-clear-btn" @click="clearLogs" :disabled="!logs.length">清空</button>
        </div>
        <div style="padding:0 16px 12px;flex:1;display:flex;min-height:0">
          <div class="log-box" ref="logPanelRef">
            <div v-if="!logs.length" class="log-empty">等待任务启动...</div>
            <div v-for="(log, idx) in logs" :key="idx" class="log-l">
              <span class="log-t">{{ log.logTime?.slice(-8) || log.logTime }}</span>
              <span :class="log.level === 'INFO' ? 'log-i' : log.level === 'ERROR' ? 'log-e' : 'log-s'">
                [{{ log.level }}] {{ log.message }}
              </span>
            </div>
          </div>
        </div>
      </div>

      <!-- Conclusion -->
      <ConclusionBox title="流程状态说明">
        数据已上传并导入 HDFS，MapReduce 清洗执行中。完成后自动进入 Hive 分析，最终经 Sqoop 同步至 MySQL。全部完成后可进入各专题页面查看结果。
      </ConclusionBox>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onUnmounted, watch, nextTick } from 'vue'
import ConclusionBox from '../components/ConclusionBox.vue'
import { uploadAndStart, getTaskStatus, getTaskLogs } from '../api/pipeline'

const fileInput = ref(null)
const isDragging = ref(false)
const currentTask = ref(null)
const logs = ref([])
const logPanelRef = ref(null)
const isRunning = ref(false)
let pollTimer = null

const fileStatuses = reactive([
  { name: 'listings.csv', icon: '📄', ready: false, file: null, key: 'listingsFile' },
  { name: 'calendar.csv', icon: '📅', ready: false, file: null, key: 'calendarFile' },
  { name: 'reviews.csv', icon: '💬', ready: false, file: null, key: 'reviewsFile' }
])

const pipelineStages = [
  { key: 'CREATED', label: '文件上传', icon: '📤', stage: 'CREATED' },
  { key: 'UPLOAD_RAW_TO_HDFS', label: 'HDFS 导入', icon: '💾', stage: 'UPLOAD_RAW_TO_HDFS' },
  { key: 'CLEAN_DATA', label: '数据清洗', icon: '⚙️', stage: 'CLEAN_DATA' },
  { key: 'UPLOAD_CLEAN_TO_HDFS', label: '清洗数据上传', icon: '📤', stage: 'UPLOAD_CLEAN_TO_HDFS' },
  { key: 'HIVE_DWD', label: 'Hive DWD', icon: '🗄️', stage: 'HIVE_DWD' },
  { key: 'HIVE_ADS', label: 'Hive 分析', icon: '🧮', stage: 'HIVE_ADS' },
  { key: 'SQOOP_EXPORT', label: 'Sqoop 导出', icon: '🔄', stage: 'SQOOP_EXPORT' },
  { key: 'FINISHED', label: '分析完成', icon: '✅', stage: 'FINISHED' }
]

const stageOrder = pipelineStages.map(s => s.stage)

function getStageState(stageKey) {
  if (!currentTask.value) return 'pending'
  const currentStage = currentTask.value.currentStage
  const currentIdx = stageOrder.indexOf(currentStage)
  const stageIdx = stageOrder.indexOf(stageKey)
  const status = currentTask.value.taskStatus

  if (status === 'FAILED' && stageKey === currentStage) return 'running'
  if (status === 'FINISHED') return 'done'
  if (status === 'FAILED' && stageIdx < currentIdx) return 'done'
  if (stageIdx < currentIdx) return 'done'
  if (stageIdx === currentIdx && status === 'RUNNING') return 'running'
  return 'pending'
}

const stageLabels = { done: '完成', running: '执行中', pending: '待执行' }

const computedStages = computed(() =>
  pipelineStages.map(s => {
    const state = getStageState(s.stage)
    return { ...s, state, stateLabel: stageLabels[state] }
  })
)

const canStart = computed(() => fileStatuses.every(f => f.ready) && !isRunning.value)

const statusLabel = computed(() => {
  if (!currentTask.value) return '待执行'
  const map = { CREATED: '已创建', RUNNING: '执行中', FINISHED: '已完成', FAILED: '失败' }
  return map[currentTask.value.taskStatus] || '待执行'
})

const statusClass = computed(() => {
  if (!currentTask.value) return ''
  const s = currentTask.value.taskStatus
  return s === 'RUNNING' ? 'running-badge' : s === 'FINISHED' ? 'done-badge' : ''
})

function formatSize(bytes) {
  if (!bytes) return ''
  if (bytes < 1024) return bytes + ' B'
  if (bytes < 1048576) return (bytes / 1024).toFixed(1) + ' KB'
  return (bytes / 1048576).toFixed(2) + ' MB'
}

function triggerUpload() {
  fileInput.value?.click()
}

function onFileChange(e) {
  const files = Array.from(e.target.files)
  processFiles(files)
}

function onDrop(e) {
  isDragging.value = false
  const files = Array.from(e.dataTransfer.files)
  processFiles(files)
}

function processFiles(files) {
  fileStatuses.forEach(status => {
    const match = files.find(f => f.name.toLowerCase() === status.name.toLowerCase())
    if (match) {
      status.file = match
      status.ready = true
    }
  })
}

function addLog(level, message) {
  const now = new Date()
  const time = now.toTimeString().slice(0, 8)
  logs.value.push({ logTime: time, level, message })
}

async function startPipeline() {
  if (!canStart.value) return

  const formData = new FormData()
  fileStatuses.forEach(s => {
    if (s.file) formData.append(s.key, s.file)
  })

  addLog('INFO', '任务已创建，正在上传文件...')
  isRunning.value = true

  try {
    const res = await uploadAndStart(formData)
    currentTask.value = res
    addLog('SUCCESS', `文件上传完成，taskId=${res.taskId}`)
    addLog('INFO', '全链路分析任务已启动')
    startPolling(res.taskId)
  } catch (e) {
    addLog('ERROR', `启动失败: ${e.message}`)
    isRunning.value = false
  }
}

function startPolling(taskId) {
  stopPolling()
  pollTimer = setInterval(async () => {
    try {
      const statusRes = await getTaskStatus(taskId)
      currentTask.value = statusRes
      if (statusRes.taskStatus === 'FINISHED') {
        addLog('SUCCESS', '全链路完成，结果已同步至 MySQL')
        stopPolling()
      } else if (statusRes.taskStatus === 'FAILED') {
        addLog('ERROR', `任务失败: ${statusRes.errorMessage || '未知错误'}`)
        stopPolling()
      }

      // Load logs
      try {
        const logRes = await getTaskLogs(taskId)
        if (Array.isArray(logRes)) {
          logs.value = logRes
        }
      } catch (e) { /* log endpoint may not be ready */ }
    } catch (e) {
      addLog('ERROR', `状态查询失败: ${e.message}`)
      stopPolling()
    }
  }, 3000)
}

function stopPolling() {
  if (pollTimer) {
    clearInterval(pollTimer)
    pollTimer = null
  }
  isRunning.value = false
}

function refreshStatus() {
  if (currentTask.value?.taskId) {
    getTaskStatus(currentTask.value.taskId).then(res => {
      currentTask.value = res
      addLog('INFO', '已刷新状态')
    }).catch(() => {
      addLog('INFO', '刷新状态失败')
    })
  } else {
    addLog('INFO', '尚未创建任务')
  }
}

function clearLogs() {
  logs.value = []
}

// 日志变化时自动滚动到底部
watch(
  () => logs.value.length,
  () => {
    nextTick(() => {
      if (logPanelRef.value) {
        logPanelRef.value.scrollTop = logPanelRef.value.scrollHeight
      }
    })
  }
)

onUnmounted(stopPolling)
</script>

<style scoped>
.page {
  display: flex;
  flex: 1;
  flex-direction: column;
  padding: 0;
  animation: pgIn 0.3s ease;
}
@keyframes pgIn {
  from { opacity: 0; transform: translateY(8px); }
  to { opacity: 1; transform: translateY(0); }
}
.pg-hd { padding: 24px 32px 0; flex-shrink: 0; }
.pg-title { font-size: 30px; font-weight: 700; color: var(--text-primary); letter-spacing: -0.01em; }
.pg-desc { font-size: 16px; color: var(--text-secondary); margin-top: 8px; line-height: 1.5; }
.pg-body {
  flex: 1; padding: 8px 32px 10px; display: flex; flex-direction: column; gap: 10px; overflow: hidden; min-height: 0;
}

.upload-zone {
  border: 2px dashed var(--border-mid);
  border-radius: var(--radius-lg);
  padding: 18px 30px;
  text-align: center;
  background: var(--bg-elevated);
  transition: all 0.25s;
  cursor: pointer;
  flex-shrink: 0;
}
.upload-zone:hover, .upload-zone.dragging {
  border-color: var(--accent);
  background: var(--accent-dim);
}
.up-ic { font-size: 28px; margin-bottom: 6px; opacity: 0.5; }
.up-txt { font-size: 13px; color: var(--text-secondary); }
.up-hint { font-size: 10px; color: var(--text-muted); margin-top: 2px; }

.file-row { display: flex; gap: 10px; flex-shrink: 0; }
.file-item {
  flex: 1; background: var(--bg-card); border: 1px solid var(--border-subtle);
  border-radius: var(--radius); padding: 10px 14px; display: flex; align-items: center; gap: 10px; font-size: 11px;
}
.file-ic { font-size: 18px; flex-shrink: 0; }
.file-name { font-size: 12px; font-weight: 600; color: var(--text-primary); }
.file-meta { font-size: 10px; color: var(--text-muted); }
.file-st {
  font-size: 10px; font-weight: 600; padding: 2px 8px; border-radius: 100px;
  color: var(--text-muted); background: rgba(255,255,255,0.04);
}
.file-st.ready { color: var(--green); background: rgba(52,211,153,0.1); }

.btn-row { display: flex; gap: 8px; flex-shrink: 0; }
.btn {
  display: inline-flex; align-items: center; gap: 6px; padding: 8px 20px;
  border-radius: 8px; font-size: 12px; font-weight: 600; cursor: pointer; border: none; transition: all 0.25s;
}
.btn:disabled { opacity: 0.5; cursor: not-allowed; }
.btn-p { background: var(--accent); color: #fff; }
.btn-p:hover:not(:disabled) { box-shadow: 0 0 16px rgba(255,90,95,0.3); }
.btn-s { background: var(--bg-elevated); color: var(--text-secondary); border: 1px solid var(--border-mid); }
.btn-s:hover { background: var(--bg-card); color: var(--text-primary); }

.panel {
  background: var(--bg-card); border: 1px solid var(--border-subtle);
  border-radius: var(--radius-lg); display: flex; flex-direction: column; min-height: 0;
}
.panel-hd { padding: 14px 18px 0; display: flex; align-items: center; justify-content: space-between; flex-shrink: 0; }
.panel-title { font-size: 15px; font-weight: 600; color: var(--text-primary); }
.panel-badge {
  font-size: 10px; color: var(--accent); background: var(--accent-dim);
  padding: 2px 8px; border-radius: 100px; font-weight: 600;
}
.panel-badge.running-badge { color: var(--accent); background: var(--accent-dim); }
.panel-badge.done-badge { color: var(--green); background: rgba(52,211,153,0.1); }

.pipeline-row { display: flex; align-items: center; gap: 0; flex-shrink: 0; overflow-x: auto; }
.pnode {
  display: flex; align-items: center; gap: 7px; background: var(--bg-card);
  border: 1px solid var(--border-subtle); border-radius: var(--radius);
  padding: 8px 12px; white-space: nowrap; font-size: 11px; transition: all 0.25s;
}
.pnode.done { border-color: var(--green); }
.pnode.running { border-color: var(--accent); box-shadow: 0 0 10px rgba(255,90,95,0.15); }
.parrow { color: var(--text-muted); margin: 0 3px; padding-top: 4px; }
.pst { font-size: 9px; font-weight: 600; padding: 1px 6px; border-radius: 100px; }
.pst.done { color: var(--green); background: rgba(52,211,153,0.1); }
.pst.running { color: var(--accent); background: var(--accent-dim); }
.pst.pending { color: var(--text-muted); background: rgba(255,255,255,0.04); }

.log-box {
  flex: 1; min-height: 0; overflow-y: auto; background: #050a14;
  border: 1px solid var(--border-subtle); border-radius: var(--radius);
  padding: 10px 14px; font-family: 'SF Mono','Fira Code','Consolas',monospace;
  font-size: 10.5px; line-height: 1.7;
}
.log-empty { color: var(--text-muted); text-align: center; padding: 20px; }
.log-l { display: flex; gap: 10px; }
.log-t { color: var(--text-muted); flex-shrink: 0; }
.log-i { color: var(--blue); }
.log-s { color: var(--green); }
.log-e { color: var(--accent); }
.log-box::-webkit-scrollbar { width: 4px; }
.log-box::-webkit-scrollbar-thumb { background: var(--border-mid); border-radius: 2px; }

.log-clear-btn {
  font-size: 11px; font-weight: 500; color: var(--text-muted);
  background: transparent; border: 1px solid var(--border-subtle);
  border-radius: var(--radius-sm); padding: 3px 10px; cursor: pointer;
  transition: all 0.15s;
}
.log-clear-btn:hover:not(:disabled) {
  border-color: var(--accent); color: var(--accent);
}
.log-clear-btn:disabled { opacity: 0.35; cursor: not-allowed; }

@media (max-width: 900px) {
  .pg-hd { padding: 16px 16px 0; }
  .pg-body { padding: 10px 16px 12px; }
}
</style>
