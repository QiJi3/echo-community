<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { Star, View, Clock } from '@element-plus/icons-vue'
import { listColumns } from '../../api/column'
import type { ColumnArticle } from '../../api/column'

const articles = ref<ColumnArticle[]>([])
const total = ref(0)
const currentPage = ref(1)
const loading = ref(false)
const activeColumn = ref('')

// Extract unique column names for filter
const columnNames = computed(() => {
  const names = new Set(articles.value.map(a => a.columnName))
  return Array.from(names)
})

const fetchArticles = async () => {
  loading.value = true
  try {
    const res = await listColumns({
      columnName: activeColumn.value || undefined,
      page: currentPage.value,
      size: 20
    })
    articles.value = res.articles || []
    total.value = res.total
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

const handleFilter = (name: string) => {
  activeColumn.value = activeColumn.value === name ? '' : name
  currentPage.value = 1
  fetchArticles()
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
  fetchArticles()
})
</script>

<template>
  <div class="main-container">
    <div class="content-area">
      <!-- Column Filter Chips -->
      <div class="echo-panel filter-panel">
        <h3 class="section-title">📚 技术专栏</h3>
        <div class="column-chips">
          <el-tag
            v-for="name in columnNames"
            :key="name"
            :type="activeColumn === name ? '' : 'info'"
            :effect="activeColumn === name ? 'dark' : 'plain'"
            class="column-chip"
            @click="handleFilter(name)"
          >{{ name }}</el-tag>
          <el-tag
            v-if="activeColumn"
            type="danger"
            effect="plain"
            class="column-chip"
            @click="handleFilter('')"
          >✕ 清除筛选</el-tag>
        </div>
      </div>

      <!-- Article Cards -->
      <div class="echo-panel article-list" v-loading="loading">
        <div
          v-for="article in articles"
          :key="article.id"
          class="list-item column-card"
        >
          <div class="column-badge">{{ article.columnName }}</div>
          <h3 class="column-title">{{ article.title }}</h3>
          <p class="column-desc">{{ truncate(article.content, 180) }}</p>
          <div class="column-footer">
            <span class="column-author">用户{{ article.userId }}</span>
            <span class="column-time">
              <el-icon><Clock /></el-icon> {{ formatTime(article.createdAt) }}
            </span>
            <span class="column-stat">
              <el-icon><View /></el-icon> {{ article.viewCount }}
            </span>
            <span class="column-stat">
              <el-icon><Star /></el-icon> {{ article.likeCount }}
            </span>
          </div>
        </div>
        <div v-if="!loading && articles.length === 0" class="empty-state">
          <p>暂无专栏文章</p>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="total > 20" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="20"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchArticles"
        />
      </div>
    </div>

    <!-- Sidebar -->
    <aside class="sidebar-area">
      <div class="echo-panel sidebar-card">
        <h4 class="sidebar-title">🔥 热门专栏</h4>
        <div
          v-for="name in columnNames"
          :key="name"
          class="hot-column-item"
          :class="{ active: activeColumn === name }"
          @click="handleFilter(name)"
        >
          <span class="hot-column-name">{{ name }}</span>
          <el-icon class="hot-column-arrow"><svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M9 18l6-6-6-6"/></svg></el-icon>
        </div>
      </div>
      <div class="echo-panel sidebar-card">
        <h4 class="sidebar-title">💡 关于专栏</h4>
        <p class="sidebar-desc">专栏汇集了站内精选技术系列文章，帮你系统化学习各类技术领域。</p>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.filter-panel { padding: 20px; }
.section-title { margin: 0 0 14px; font-size: 18px; font-weight: 600; color: var(--text-primary); }
.column-chips { display: flex; flex-wrap: wrap; gap: 8px; }
.column-chip { cursor: pointer; transition: all 0.2s; }
.column-chip:hover { transform: translateY(-1px); }

.article-list { min-height: 200px; }

.column-card { position: relative; }
.column-badge {
  display: inline-block; padding: 2px 10px; border-radius: 3px;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #fff; font-size: 12px; font-weight: 500; margin-bottom: 10px;
}
.column-title {
  margin: 0 0 8px; font-size: 17px; font-weight: 600;
  color: var(--text-primary); cursor: pointer;
}
.column-title:hover { color: var(--juejin-blue); }
.column-desc {
  margin: 0 0 12px; font-size: 14px;
  color: var(--text-secondary); line-height: 1.7;
}
.column-footer {
  display: flex; align-items: center; gap: 14px;
  font-size: 13px; color: var(--text-tertiary);
}
.column-footer .el-icon { margin-right: 2px; }
.column-author { font-weight: 500; }
.column-time, .column-stat {
  display: flex; align-items: center; gap: 3px;
}

.pagination-wrapper { padding: 16px; display: flex; justify-content: center; }

/* Sidebar */
.sidebar-card { padding: 16px; }
.sidebar-title { margin: 0 0 12px; font-size: 15px; font-weight: 600; color: var(--text-primary); }
.sidebar-desc { margin: 0; font-size: 13px; color: var(--text-secondary); line-height: 1.6; }

.hot-column-item {
  display: flex; justify-content: space-between; align-items: center;
  padding: 10px 8px; cursor: pointer; border-radius: 6px;
  transition: all 0.2s; font-size: 14px; color: var(--text-secondary);
}
.hot-column-item:hover { background: var(--bg-color-secondary); color: var(--juejin-blue); }
.hot-column-item.active { color: var(--juejin-blue); font-weight: 500; }
.hot-column-arrow { font-size: 14px; }

.empty-state { padding: 60px 20px; text-align: center; color: var(--text-tertiary); font-size: 15px; }
</style>
