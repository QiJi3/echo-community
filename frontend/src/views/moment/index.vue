<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { Star } from '@element-plus/icons-vue'
import { listMoments } from '../../api/moment'
import type { MomentItem } from '../../api/moment'

const moments = ref<MomentItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const loading = ref(false)
const activeTopic = ref('')

const topics = computed(() => {
  const set = new Set(moments.value.map(m => m.topic).filter(Boolean) as string[])
  return Array.from(set)
})

const fetchMoments = async () => {
  loading.value = true
  try {
    const res = await listMoments({
      topic: activeTopic.value || undefined,
      page: currentPage.value,
      size: 20
    })
    moments.value = res.moments || []
    total.value = res.total
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

const toggleTopic = (t: string) => {
  activeTopic.value = activeTopic.value === t ? '' : t
  currentPage.value = 1
  fetchMoments()
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

// Random avatar color based on userId
const avatarColor = (userId: number) => {
  const colors = ['#1e80ff', '#00b96b', '#ff6a00', '#7c3aed', '#e91e63', '#00bcd4']
  return colors[userId % colors.length]
}

onMounted(() => {
  fetchMoments()
})
</script>

<template>
  <div class="main-container">
    <div class="content-area">
      <!-- Topic Filter -->
      <div class="echo-panel topic-bar">
        <h3 class="section-title">🔥 沸点</h3>
        <div class="topic-chips">
          <el-tag
            :type="activeTopic === '' ? '' : 'info'"
            :effect="activeTopic === '' ? 'dark' : 'plain'"
            class="topic-chip"
            @click="toggleTopic('')"
          >全部</el-tag>
          <el-tag
            v-for="t in topics"
            :key="t"
            :type="activeTopic === t ? '' : 'info'"
            :effect="activeTopic === t ? 'dark' : 'plain'"
            class="topic-chip"
            @click="toggleTopic(t)"
          >{{ t }}</el-tag>
        </div>
      </div>

      <!-- Timeline -->
      <div class="echo-panel moment-list" v-loading="loading">
        <div
          v-for="item in moments"
          :key="item.id"
          class="moment-card"
        >
          <div class="moment-avatar" :style="{ backgroundColor: avatarColor(item.userId) }">
            {{ ('U' + item.userId) }}
          </div>
          <div class="moment-body">
            <div class="moment-header">
              <span class="moment-author">用户{{ item.userId }}</span>
              <span class="moment-time">{{ formatTime(item.createdAt) }}</span>
            </div>
            <div class="moment-content">{{ item.content }}</div>
            <div class="moment-bottom">
              <el-tag v-if="item.topic" size="small" type="info" effect="plain" class="moment-topic">
                # {{ item.topic }}
              </el-tag>
              <span class="moment-like">
                <el-icon><Star /></el-icon> {{ item.likeCount }}
              </span>
            </div>
          </div>
        </div>
        <div v-if="!loading && moments.length === 0" class="empty-state">
          <p>暂无沸点动态</p>
        </div>
      </div>

      <!-- Pagination -->
      <div v-if="total > 20" class="pagination-wrapper">
        <el-pagination
          v-model:current-page="currentPage"
          :page-size="20"
          :total="total"
          layout="prev, pager, next"
          @current-change="fetchMoments"
        />
      </div>
    </div>

    <!-- Sidebar -->
    <aside class="sidebar-area">
      <div class="echo-panel sidebar-card">
        <h4 class="sidebar-title">📌 热门话题</h4>
        <div
          v-for="t in topics"
          :key="t"
          class="hot-topic-item"
          :class="{ active: activeTopic === t }"
          @click="toggleTopic(t)"
        >
          <span class="topic-hash">#</span>
          <span>{{ t }}</span>
        </div>
        <div v-if="topics.length === 0" class="sidebar-empty">暂无话题</div>
      </div>
      <div class="echo-panel sidebar-card">
        <h4 class="sidebar-title">💡 沸点是什么</h4>
        <p class="sidebar-desc">沸点是轻量级的技术动态分享区，记录你的学习碎片、每日感悟和摸鱼日常。</p>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.topic-bar { padding: 20px; }
.section-title { margin: 0 0 14px; font-size: 18px; font-weight: 600; color: var(--text-primary); }
.topic-chips { display: flex; flex-wrap: wrap; gap: 8px; }
.topic-chip { cursor: pointer; transition: all 0.2s; }
.topic-chip:hover { transform: translateY(-1px); }

.moment-list { min-height: 200px; padding: 0 !important; }

.moment-card {
  display: flex; gap: 14px;
  padding: 20px 24px;
  border-bottom: 1px solid var(--border-color);
  transition: background 0.15s;
}
.moment-card:last-child { border-bottom: none; }
.moment-card:hover { background: var(--bg-color-secondary); }

.moment-avatar {
  width: 42px; height: 42px; border-radius: 50%;
  display: flex; align-items: center; justify-content: center;
  color: #fff; font-size: 12px; font-weight: 600;
  flex-shrink: 0;
}

.moment-body { flex: 1; min-width: 0; }
.moment-header {
  display: flex; align-items: center; gap: 10px;
  margin-bottom: 8px;
}
.moment-author { font-size: 14px; font-weight: 600; color: var(--text-primary); }
.moment-time { font-size: 12px; color: var(--text-tertiary); }

.moment-content {
  font-size: 15px; line-height: 1.7; color: var(--text-primary);
  white-space: pre-wrap; word-break: break-word;
  margin-bottom: 10px;
}

.moment-bottom {
  display: flex; align-items: center; gap: 12px;
}
.moment-topic { cursor: pointer; }
.moment-like {
  display: flex; align-items: center; gap: 4px;
  font-size: 13px; color: var(--text-tertiary);
  margin-left: auto;
}

.pagination-wrapper { padding: 16px; display: flex; justify-content: center; }

/* Sidebar */
.sidebar-card { padding: 16px; }
.sidebar-title { margin: 0 0 12px; font-size: 15px; font-weight: 600; color: var(--text-primary); }
.sidebar-desc { margin: 0; font-size: 13px; color: var(--text-secondary); line-height: 1.6; }
.sidebar-empty { font-size: 13px; color: var(--text-tertiary); }

.hot-topic-item {
  display: flex; align-items: center; gap: 4px;
  padding: 8px; cursor: pointer; border-radius: 6px;
  font-size: 14px; color: var(--text-secondary); transition: all 0.2s;
}
.hot-topic-item:hover { background: var(--bg-color-secondary); color: var(--juejin-blue); }
.hot-topic-item.active { color: var(--juejin-blue); font-weight: 500; }
.topic-hash { color: var(--juejin-blue); font-weight: 700; }

.empty-state { padding: 60px 20px; text-align: center; color: var(--text-tertiary); font-size: 15px; }
</style>
