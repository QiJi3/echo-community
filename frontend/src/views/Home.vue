<script setup lang="ts">
import { ref, computed, watch, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { Star, User } from '@element-plus/icons-vue'
import { listPosts } from '../api/post'
import type { Post } from '../api/post'
import { useUserStore } from '../stores/useUserStore'

// ── Hot Ranking ──
const hotPosts = ref<Post[]>([])
const fetchHotPosts = async () => {
  try {
    const res = await listPosts({ sort: 'hot', page: 1, size: 8 })
    hotPosts.value = res.posts || []
  } catch { /* silent */ }
}

const router = useRouter()
const route = useRoute()
const userStore = useUserStore()
const activeTab = ref('recommended')
const activeTag = ref((route.query.tag as string) || '')

watch(() => route.query.tag, (newTag) => {
  activeTag.value = (newTag as string) || ''
})

// ── Data from API ──
const posts = ref<Post[]>([])
const totalPosts = ref(0)
const currentPage = ref(1)
const loading = ref(false)

const fetchPosts = async () => {
  loading.value = true
  try {
    const sortMap: Record<string, string> = { recommended: 'time', latest: 'time', hot: 'hot' }
    const res = await listPosts({
      sort: sortMap[activeTab.value] || 'time',
      page: currentPage.value,
      size: 10
    })
    posts.value = res.posts || []
    totalPosts.value = res.total
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

// Filter by tag (client-side since backend has no tag field)
const displayedPosts = computed(() => {
  if (!activeTag.value) return posts.value
  const target = activeTag.value.toLowerCase().replace(/\s+/g, '')
  return posts.value.filter(p =>
    p.title.toLowerCase().replace(/\s+/g, '').includes(target) ||
    p.content.toLowerCase().replace(/\s+/g, '').includes(target)
  )
})

const hotTags = ref(['Java', 'Spring Boot', 'Vue.js', '系统架构', '高并发'])

watch(activeTab, () => {
  currentPage.value = 1
  fetchPosts()
})

onMounted(() => {
  fetchPosts()
  fetchHotPosts()
})

const goPost = (id: number) => router.push(`/post/${id}`)
const handleSign = () => router.push('/checkin')
const handleTagClick = (tag: string) => router.push({ path: '/', query: { tag } })
const clearTagFilter = () => router.push('/')

// Utils
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
</script>

<template>
  <div class="main-container">
    <!-- Main Content -->
    <div class="content-area">
      <div class="echo-panel tab-panel">
        <el-tabs v-model="activeTab" class="feed-tabs custom-tabs">
          <el-tab-pane label="推荐" name="recommended" />
          <el-tab-pane label="最新" name="latest" />
          <el-tab-pane label="热榜" name="hot" />
        </el-tabs>
      </div>

      <!-- Tag Filter Bar -->
      <div v-if="activeTag" class="echo-panel filter-bar">
        <span class="filter-label">当前筛选：</span>
        <el-tag type="primary" closable @close="clearTagFilter">{{ activeTag }}</el-tag>
        <span class="filter-count">共 {{ displayedPosts.length }} 篇</span>
      </div>

      <div class="echo-panel post-list-panel">
        <div v-loading="loading">
          <div
            v-for="post in displayedPosts"
            :key="post.id"
            class="list-item post-card"
            @click="goPost(post.id)"
          >
            <h3 class="post-title">{{ post.title }}</h3>
            <p class="post-desc">{{ truncate(post.content, 120) }}</p>
            <div class="post-footer">
              <span class="post-author">
                <el-icon><User /></el-icon> 用户{{ post.userId }}
              </span>
              <span>{{ formatTime(post.createdAt) }}</span>
              <span class="dot">·</span>
              <span><el-icon><Star /></el-icon> {{ post.likeCount }}</span>
            </div>
          </div>
        </div>
        <div v-if="!loading && displayedPosts.length === 0" class="empty-posts">
          <p v-if="activeTag">没有找到与「{{ activeTag }}」相关的文章</p>
          <p v-else>暂无文章</p>
          <el-button v-if="activeTag" type="primary" link @click="clearTagFilter">清除筛选</el-button>
        </div>
        <!-- Pagination -->
        <div v-if="totalPosts > 10" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="10"
            :total="totalPosts"
            layout="prev, pager, next"
            @current-change="fetchPosts"
          />
        </div>
      </div>
    </div>

    <!-- Sidebar -->
    <aside class="sidebar-area">
      <div class="echo-panel sign-card">
        <h3>早上好，{{ userStore.isLoggedIn ? userStore.userInfo?.username : '工程师' }}！</h3>
        <p>点亮你在 Echo 的每一天 🌟</p>
        <el-button round class="sign-btn" :class="{ 'is-signed': userStore.hasCheckedInToday }" @click="handleSign">
          {{ userStore.hasCheckedInToday ? '已签到' : '去签到' }}
        </el-button>
      </div>

      <div class="echo-panel notice-card">
        <div class="notice-item">
          <el-tag type="danger" size="small" effect="dark">新</el-tag>
          <span>代码规范指南 V2.0</span>
        </div>
        <div class="notice-item clickable" @click="router.push('/post/1')">开源项目推荐栏目</div>
        <div class="notice-item clickable" @click="router.push('/post/2')">2026 届秋招面经大合集</div>
      </div>

      <div class="echo-panel tags-card">
        <h4>热门标签</h4>
        <div class="tags-wrap">
          <el-tag
            v-for="tag in hotTags"
            :key="tag"
            class="hot-tag"
            effect="plain"
            @click="handleTagClick(tag)"
          >{{ tag }}</el-tag>
        </div>
      </div>

      <div class="echo-panel ranking-card">
        <h4>🔥 热点排行</h4>
        <div class="ranking-list">
          <div
            v-for="(hp, index) in hotPosts"
            :key="hp.id"
            class="ranking-item"
            @click="goPost(hp.id)"
          >
            <span class="ranking-index" :class="{ top: index < 3 }">{{ index + 1 }}</span>
            <div class="ranking-info">
              <span class="ranking-title">{{ hp.title }}</span>
              <span class="ranking-heat">{{ hp.likeCount + hp.commentCount }} 热度</span>
            </div>
          </div>
          <div v-if="hotPosts.length === 0" class="ranking-empty">暂无数据</div>
        </div>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.tab-panel { padding: 0 20px; }
:deep(.custom-tabs .el-tabs__header) {
  margin: 0;
}
:deep(.custom-tabs .el-tabs__nav-wrap::after) { height: 0; }
:deep(.custom-tabs .el-tabs__item) {
  height: 50px; 
  line-height: 50px; 
  font-size: 16px; 
  color: var(--text-secondary);
  padding: 0 24px; /* Default is often 0 20px, increasing to 24px */
}
/* Specifically add space between tabs by targeting items except the first */
:deep(.custom-tabs .el-tabs__item + .el-tabs__item) {
  margin-left: 20px; /* This injects the exact space you wanted between the words */
}
:deep(.custom-tabs .el-tabs__item.is-active) { font-weight: 500; color: var(--juejin-blue); }

.post-card { cursor: pointer; }
.post-title { margin: 0 0 8px; font-size: 16px; font-weight: 600; color: var(--text-primary); }
.post-desc { margin: 0 0 10px; font-size: 14px; color: var(--text-secondary); line-height: 1.6; }
.post-footer { display: flex; align-items: center; gap: 8px; font-size: 13px; color: var(--text-tertiary); }
.post-footer .el-icon { margin-right: 2px; }
.post-author { display: flex; align-items: center; gap: 4px; }
.dot { color: var(--text-tertiary); }

.pagination-wrapper { padding: 16px; display: flex; justify-content: center; }

/* Sidebar */
.sign-card { padding: 20px; text-align: center; }
.sign-card h3 { margin: 0 0 8px; font-size: 18px; color: var(--text-primary); }
.sign-card p { margin: 0 0 16px; font-size: 14px; color: var(--text-secondary); }
.sign-btn {
  width: 100%; border: 1px solid var(--juejin-blue);
  color: var(--juejin-blue); background: transparent;
}
.sign-btn:not(.is-signed):hover { background: var(--juejin-blue); color: #fff; }
.sign-btn.is-signed {
  border-color: var(--border-color);
  color: var(--text-tertiary);
  background: var(--bg-color-secondary);
  cursor: default;
}
.notice-card { padding: 16px; }
.notice-item {
  font-size: 14px; color: var(--text-secondary);
  padding: 6px 0; display: flex; align-items: center; gap: 8px;
}
.notice-item.clickable { cursor: pointer; }
.notice-item.clickable:hover { color: var(--juejin-blue); }
.tags-card { padding: 16px; }
.tags-card h4 { margin: 0 0 12px; font-size: 16px; color: var(--text-primary); }
.tags-wrap { display: flex; flex-wrap: wrap; gap: 8px; }
.hot-tag { cursor: pointer; }

/* Ranking */
.ranking-card { padding: 16px; }
.ranking-card h4 { margin: 0 0 12px; font-size: 16px; color: var(--text-primary); }
.ranking-list { display: flex; flex-direction: column; }
.ranking-item {
  display: flex; align-items: flex-start; gap: 10px;
  padding: 8px 4px; border-radius: 6px; cursor: pointer;
  transition: background 0.2s;
}
.ranking-item:hover { background: var(--bg-color-secondary); }
.ranking-index {
  flex-shrink: 0; width: 20px; height: 20px;
  display: flex; align-items: center; justify-content: center;
  font-size: 12px; font-weight: 700; border-radius: 4px;
  color: var(--text-tertiary); background: var(--bg-color-secondary);
  margin-top: 2px;
}
.ranking-index.top { color: #fff; background: linear-gradient(135deg, #ff6a00, #ee0979); }
.ranking-info { display: flex; flex-direction: column; gap: 2px; min-width: 0; }
.ranking-title {
  font-size: 13px; color: var(--text-primary); line-height: 1.4;
  overflow: hidden; text-overflow: ellipsis;
  display: -webkit-box; -webkit-line-clamp: 2; -webkit-box-orient: vertical;
}
.ranking-item:hover .ranking-title { color: var(--juejin-blue); }
.ranking-heat { font-size: 11px; color: var(--text-tertiary); }
.ranking-empty { padding: 20px 0; text-align: center; font-size: 13px; color: var(--text-tertiary); }

.filter-bar {
  display: flex; align-items: center; gap: 10px;
  padding: 12px 20px; margin-bottom: 8px;
}
.filter-label { font-size: 14px; color: var(--text-secondary); }
.filter-count { font-size: 13px; color: var(--text-tertiary); margin-left: auto; }
.empty-posts { padding: 60px 20px; text-align: center; color: var(--text-tertiary); }
.empty-posts p { margin-bottom: 12px; font-size: 15px; }
</style>
