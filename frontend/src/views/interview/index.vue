<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Star, View, Clock } from '@element-plus/icons-vue'
import { listInterviews } from '../../api/interview'
import type { Interview } from '../../api/interview'

const interviews = ref<Interview[]>([])
const total = ref(0)
const currentPage = ref(1)
const loading = ref(false)
const filterCompany = ref('')
const filterPosition = ref('')

// 从数据中提取唯一公司与岗位
const companies = computed(() => {
  const set = new Set(interviews.value.map(i => i.company))
  return Array.from(set)
})
const positions = computed(() => {
  const set = new Set(interviews.value.map(i => i.position))
  return Array.from(set)
})

const fetchInterviews = async () => {
  loading.value = true
  try {
    const res = await listInterviews({
      company: filterCompany.value || undefined,
      position: filterPosition.value || undefined,
      page: currentPage.value,
      size: 20
    })
    interviews.value = res.interviews || []
    total.value = res.total
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

const toggleCompany = (c: string) => {
  filterCompany.value = filterCompany.value === c ? '' : c
  currentPage.value = 1
  fetchInterviews()
}
const togglePosition = (p: string) => {
  filterPosition.value = filterPosition.value === p ? '' : p
  currentPage.value = 1
  fetchInterviews()
}

const difficultyStars = (n: number) => '★'.repeat(n) + '☆'.repeat(5 - n)

const resultTag = (result: string) => {
  if (result === 'offer') return { text: 'Offer ✅', type: 'success' as const }
  if (result === 'rejected') return { text: '挂了 ❌', type: 'danger' as const }
  return { text: '待定', type: 'info' as const }
}

const formatTime = (dateStr: string) => {
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const minutes = Math.floor(diff / 60000)
  if (minutes < 60) return `${minutes} 分钟前`
  const hours = Math.floor(minutes / 60)
  if (hours < 24) return `${hours} 小时前`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days} 天前`
  return date.toLocaleDateString()
}

const truncate = (str: string, len: number) => {
  if (!str) return ''
  return str.length > len ? str.substring(0, len) + '...' : str
}

onMounted(() => {
  fetchInterviews()
})
</script>

<template>
  <div class="main-container">
    <div class="content-area">
      <!-- Filter Panel -->
      <div class="echo-panel filter-panel">
        <h3 class="section-title">📝 面经广场</h3>
        <div class="filter-row">
          <span class="filter-label">公司：</span>
          <el-tag
            v-for="c in companies"
            :key="c"
            :type="filterCompany === c ? '' : 'info'"
            :effect="filterCompany === c ? 'dark' : 'plain'"
            class="filter-chip"
            @click="toggleCompany(c)"
          >{{ c }}</el-tag>
        </div>
        <div class="filter-row" style="margin-top: 10px;">
          <span class="filter-label">岗位：</span>
          <el-tag
            v-for="p in positions"
            :key="p"
            :type="filterPosition === p ? '' : 'info'"
            :effect="filterPosition === p ? 'dark' : 'plain'"
            class="filter-chip"
            @click="togglePosition(p)"
          >{{ p }}</el-tag>
        </div>
      </div>

      <!-- Interview Cards -->
      <div class="echo-panel interview-list" v-loading="loading">
        <div
          v-for="item in interviews"
          :key="item.id"
          class="list-item interview-card"
        >
          <div class="interview-header">
            <div class="interview-tags">
              <el-tag size="small" effect="dark" class="company-tag">{{ item.company }}</el-tag>
              <el-tag size="small" type="warning" effect="plain">{{ item.position }}</el-tag>
              <el-tag size="small" :type="resultTag(item.result).type" effect="plain">
                {{ resultTag(item.result).text }}
              </el-tag>
            </div>
            <span class="difficulty" :title="`难度 ${item.difficulty}/5`">
              {{ difficultyStars(item.difficulty) }}
            </span>
          </div>
          <h3 class="interview-title">{{ item.title }}</h3>
          <p class="interview-desc">{{ truncate(item.content, 200) }}</p>
          <div class="interview-footer">
            <span>用户{{ item.userId }}</span>
            <span><el-icon><Clock /></el-icon> {{ formatTime(item.createdAt) }}</span>
            <span><el-icon><View /></el-icon> {{ item.viewCount }}</span>
            <span><el-icon><Star /></el-icon> {{ item.likeCount }}</span>
          </div>
        </div>
        <div v-if="!loading && interviews.length === 0" class="empty-state">
          <p>暂无面经</p>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="total > 20" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="20"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchInterviews"
        />
      </div>
    </div>

    <!-- Sidebar -->
    <aside class="sidebar-area">
      <div class="echo-panel sidebar-card">
        <h4 class="sidebar-title">🏢 热门公司</h4>
        <div
          v-for="c in companies"
          :key="c"
          class="hot-item"
          :class="{ active: filterCompany === c }"
          @click="toggleCompany(c)"
        >{{ c }}</div>
      </div>
      <div class="echo-panel sidebar-card">
        <h4 class="sidebar-title">💡 面经须知</h4>
        <p class="sidebar-desc">本栏面经均为社区用户真实投稿，涵盖各大厂各轮次面试经历，助你提前备战。</p>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.filter-panel { padding: 20px; }
.section-title { margin: 0 0 14px; font-size: 18px; font-weight: 600; color: var(--text-primary); }
.filter-row { display: flex; align-items: center; flex-wrap: wrap; gap: 8px; }
.filter-label { font-size: 13px; color: var(--text-tertiary); min-width: 45px; }
.filter-chip { cursor: pointer; transition: all 0.2s; }
.filter-chip:hover { transform: translateY(-1px); }

.interview-list { min-height: 200px; }

.interview-card { }
.interview-header {
  display: flex; justify-content: space-between; align-items: center;
  margin-bottom: 10px;
}
.interview-tags { display: flex; gap: 6px; flex-wrap: wrap; }
.company-tag { background: linear-gradient(135deg, #1e80ff, #0052d9) !important; border: none !important; }
.difficulty { font-size: 14px; color: #faad14; letter-spacing: 1px; }
.interview-title {
  margin: 0 0 8px; font-size: 17px; font-weight: 600;
  color: var(--text-primary); cursor: pointer;
}
.interview-title:hover { color: var(--juejin-blue); }
.interview-desc {
  margin: 0 0 12px; font-size: 14px; color: var(--text-secondary); line-height: 1.7;
}
.interview-footer {
  display: flex; align-items: center; gap: 14px;
  font-size: 13px; color: var(--text-tertiary);
}
.interview-footer .el-icon { margin-right: 2px; }

.pagination-wrapper { padding: 16px; display: flex; justify-content: center; }

/* Sidebar */
.sidebar-card { padding: 16px; }
.sidebar-title { margin: 0 0 12px; font-size: 15px; font-weight: 600; color: var(--text-primary); }
.sidebar-desc { margin: 0; font-size: 13px; color: var(--text-secondary); line-height: 1.6; }
.hot-item {
  padding: 8px; cursor: pointer; border-radius: 6px;
  font-size: 14px; color: var(--text-secondary); transition: all 0.2s;
}
.hot-item:hover { background: var(--bg-color-secondary); color: var(--juejin-blue); }
.hot-item.active { color: var(--juejin-blue); font-weight: 500; }

.empty-state { padding: 60px 20px; text-align: center; color: var(--text-tertiary); font-size: 15px; }
</style>
