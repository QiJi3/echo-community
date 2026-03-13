<script setup lang="ts">
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { getPost } from '../api/post'
import { like } from '../api/social'
import { useUserStore } from '../stores/useUserStore'
import { ElMessage } from 'element-plus'
import { Star, StarFilled, User } from '@element-plus/icons-vue'
import type { Post } from '../api/post'

const route = useRoute()
const router = useRouter()
const userStore = useUserStore()

const post = ref<Post | null>(null)
const loading = ref(false)
const liked = ref(false)
const likeAnimating = ref(false)

const fetchPost = async () => {
  loading.value = true
  try {
    const id = Number(route.params.id)
    post.value = await getPost(id)
  } catch {
    // handled by interceptor
  } finally {
    loading.value = false
  }
}

const handleLike = async () => {
  if (!userStore.isLoggedIn) {
    ElMessage.warning('请先登录')
    router.push(`/login?redirect=${route.fullPath}`)
    return
  }
  try {
    likeAnimating.value = true
    const res = await like(1, post.value!.id)
    liked.value = res.liked
    if (post.value) post.value.likeCount = res.likeCount
    ElMessage.success(res.liked ? '已点赞 👍' : '已取消点赞')
    setTimeout(() => { likeAnimating.value = false }, 400)
  } catch {
    likeAnimating.value = false
  }
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

onMounted(() => {
  fetchPost()
})
</script>

<template>
  <div class="main-container" style="margin-top: 20px;">
    <div class="content-area" v-loading="loading">
      <div class="echo-panel article-panel" v-if="post">
        <h1 class="article-title">{{ post.title }}</h1>
        <div class="article-meta">
          <el-icon><User /></el-icon>
          <span>用户{{ post.userId }}</span>
          <span class="dot">·</span>
          <span>{{ formatTime(post.createdAt) }}</span>
        </div>
        <div class="article-content">{{ post.content }}</div>

        <!-- Like Action Bar -->
        <div class="article-actions">
          <button
            class="like-btn"
            :class="{ liked: liked, animating: likeAnimating }"
            @click="handleLike"
          >
            <el-icon class="like-icon">
              <StarFilled v-if="liked" />
              <Star v-else />
            </el-icon>
            <span class="like-text">{{ liked ? '已赞' : '点赞' }}</span>
            <span class="like-count">{{ post.likeCount }}</span>
          </button>
        </div>
      </div>


    </div>

    <aside class="sidebar-area">
      <div class="echo-panel sidebar-card">
        <el-button type="primary" plain style="width: 100%;" @click="router.push('/')">← 返回首页</el-button>
      </div>
      <div class="echo-panel sidebar-card" v-if="post">
        <h4 class="sidebar-title">文章信息</h4>
        <div class="stat-row">
          <span class="stat-label">👍 点赞</span>
          <span class="stat-value">{{ post.likeCount }}</span>
        </div>
        <div class="stat-row">
          <span class="stat-label">💬 评论</span>
          <span class="stat-value">{{ post.commentCount }}</span>
        </div>
        <div class="stat-row">
          <span class="stat-label">🕐 发布</span>
          <span class="stat-value">{{ formatTime(post.createdAt) }}</span>
        </div>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.article-panel { padding: 30px; }
.article-title { margin: 0 0 16px; font-size: 28px; font-weight: 700; color: var(--text-primary); }
.article-meta {
  font-size: 14px; color: var(--text-tertiary); margin-bottom: 24px;
  display: flex; align-items: center; gap: 6px;
}
.dot { color: var(--text-tertiary); }
.article-content {
  font-size: 16px; line-height: 1.8; color: var(--text-primary);
  white-space: pre-wrap; word-break: break-word;
}
.article-actions {
  margin-top: 32px; padding-top: 24px;
  border-top: 1px solid var(--border-color);
  display: flex; align-items: center; gap: 12px;
}

/* Like Button */
.like-btn {
  display: inline-flex; align-items: center; gap: 6px;
  padding: 10px 24px; border-radius: 20px;
  border: 1px solid var(--border-color);
  background: var(--bg-color); color: var(--text-secondary);
  font-size: 15px; cursor: pointer;
  transition: all 0.25s ease;
}
.like-btn:hover {
  border-color: var(--juejin-blue);
  color: var(--juejin-blue);
  background: #eaf2ff;
}
.like-btn.liked {
  border-color: #ff4d4f;
  color: #ff4d4f;
  background: #fff1f0;
}
.like-btn.liked:hover {
  background: #ffccc7;
}
.like-icon { font-size: 18px; transition: transform 0.3s ease; }
.like-btn.animating .like-icon {
  animation: bounce 0.4s ease;
}
@keyframes bounce {
  0% { transform: scale(1); }
  30% { transform: scale(1.4); }
  60% { transform: scale(0.9); }
  100% { transform: scale(1); }
}
.like-text { font-weight: 500; }
.like-count { color: var(--text-tertiary); font-size: 13px; }

/* Comment notice */
.comment-notice {
  padding: 24px; text-align: center;
  color: var(--text-tertiary); font-size: 14px;
}
.comment-notice p { margin: 0; }

/* Sidebar */
.sidebar-card { padding: 16px; }
.sidebar-title {
  margin: 0 0 12px; font-size: 15px;
  font-weight: 600; color: var(--text-primary);
}
.stat-row {
  display: flex; justify-content: space-between;
  padding: 8px 0;
  border-bottom: 1px solid var(--border-color);
  font-size: 14px;
}
.stat-row:last-child { border-bottom: none; }
.stat-label { color: var(--text-secondary); }
.stat-value { color: var(--text-primary); font-weight: 500; }
</style>
