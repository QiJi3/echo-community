<script setup lang="ts">
import { ref, watch, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from '../stores/useUserStore'
import { listPosts } from '../api/post'
import { listFollowees } from '../api/social'
import type { Post } from '../api/post'

const router = useRouter()
const userStore = useUserStore()
const activeName = ref('posts')


watch(activeName, (tab) => {
  if (tab === 'following' && following.value.length === 0) fetchFollowing()
})

// ── My Posts ──
const myPosts = ref<Post[]>([])
const postTotal = ref(0)
const postLoading = ref(false)

const fetchMyPosts = async () => {
  if (!userStore.userInfo) return
  postLoading.value = true
  try {
    const res = await listPosts({ userId: userStore.userInfo.id, page: 1, size: 50 })
    myPosts.value = res.posts || []
    postTotal.value = res.total
  } catch {
    // handled
  } finally {
    postLoading.value = false
  }
}

// ── Following ──
const following = ref<any[]>([])
const fetchFollowing = async () => {
  if (!userStore.userInfo) return
  try {
    const res = await listFollowees({
      userId: userStore.userInfo.id,
      entityType: 3, // follow user
      page: 1,
      size: 50
    })
    following.value = res.followees || res.records || []
  } catch {
    // handled
  }
}

// ── Edit Profile Dialog ──
const editDialogVisible = ref(false)
const editForm = ref({ username: '', email: '' })

const openEditDialog = () => {
  editForm.value.username = userStore.userInfo?.username || ''
  editForm.value.email = userStore.userInfo?.email || ''
  editDialogVisible.value = true
}

const formatTime = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const now = new Date()
  const diff = now.getTime() - date.getTime()
  const hours = Math.floor(diff / 3600000)
  if (hours < 24) return `${hours} 小时前`
  const days = Math.floor(hours / 24)
  if (days < 30) return `${days} 天前`
  return date.toLocaleDateString()
}

onMounted(() => {
  fetchMyPosts()
})
</script>

<template>
  <div class="main-container" style="margin-top: 20px;">
    <div class="content-area">
      <!-- Profile Header -->
      <div class="echo-panel profile-header">
        <div class="profile-info-box">
          <el-avatar
            :size="90"
            :src="userStore.userInfo?.avatar || 'https://api.dicebear.com/7.x/bottts/svg?seed=Felix'"
            class="profile-avatar"
          />
          <div class="profile-text-content">
            <h1 class="profile-name">{{ userStore.userInfo?.username }}</h1>
            <div class="profile-desc">{{ userStore.userInfo?.email }}</div>
            <div class="profile-role">
              <el-tag size="small">{{ userStore.userInfo?.role }}</el-tag>
            </div>
          </div>
          <div class="profile-actions">
            <el-button type="primary" plain @click="openEditDialog">编辑个人资料</el-button>
          </div>
        </div>
      </div>

      <!-- Edit Dialog -->
      <el-dialog v-model="editDialogVisible" title="编辑个人资料" width="480px" :close-on-click-modal="false">
        <el-form :model="editForm" label-width="80px">
          <el-form-item label="用户名">
            <el-input v-model="editForm.username" disabled />
          </el-form-item>
          <el-form-item label="邮箱">
            <el-input v-model="editForm.email" disabled />
          </el-form-item>
          <el-form-item>
            <span style="font-size: 13px; color: var(--text-tertiary);">更多资料修改功能正在开发中</span>
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="editDialogVisible = false">关闭</el-button>
        </template>
      </el-dialog>

      <!-- Tabs -->
      <div class="echo-panel profile-content">
        <el-tabs v-model="activeName" class="profile-tabs custom-tabs">
          <el-tab-pane label="我的文章" name="posts" />
          <el-tab-pane label="关注" name="following" />
        </el-tabs>

        <!-- Posts Tab -->
        <div v-if="activeName === 'posts'" v-loading="postLoading">
          <div
            v-for="post in myPosts"
            :key="post.id"
            class="list-item post-item"
            @click="router.push(`/post/${post.id}`)"
          >
            <h3 class="post-title">{{ post.title }}</h3>
            <div class="post-meta">
              <span>{{ formatTime(post.createdAt) }}</span>
              <span class="dot">·</span>
              <span>{{ post.likeCount }} 点赞</span>
              <span class="dot">·</span>
              <span>{{ post.commentCount }} 评论</span>
            </div>
          </div>
          <div v-if="myPosts.length === 0 && !postLoading" class="empty-state">
            <p>还没有发布过文章</p>
          </div>
        </div>

        <!-- Following Tab -->
        <div v-else-if="activeName === 'following'">
          <div v-for="(user, idx) in following" :key="idx" class="list-item following-item">
            <el-avatar :size="40" :src="`https://api.dicebear.com/7.x/bottts/svg?seed=${user.id || idx}`" />
            <div class="following-info">
              <div class="following-name">{{ user.username || `用户${user.id}` }}</div>
            </div>
            <el-button size="small" plain>已关注</el-button>
          </div>
          <div v-if="following.length === 0" class="empty-state">
            <p>还没有关注任何人</p>
          </div>
        </div>
      </div>
    </div>

    <!-- Sidebar -->
    <aside class="sidebar-area">
      <div class="echo-panel" style="padding: 16px;">
        <div class="stat-card">
          <div class="stat-item">
            <div class="stat-val">{{ postTotal }}</div>
            <div class="stat-label">文章</div>
          </div>
        </div>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.profile-header { padding: 30px; }
.profile-info-box { display: flex; align-items: center; }
.profile-avatar { margin-right: 24px; }
.profile-text-content { flex: 1; }
.profile-name { margin: 0 0 8px; font-size: 24px; font-weight: 600; color: var(--text-primary); }
.profile-desc { font-size: 14px; color: var(--text-secondary); margin-bottom: 8px; }
.profile-role { margin-top: 4px; }
.profile-content { margin-top: 12px; min-height: 300px; }
.profile-tabs { padding: 0 20px; }
:deep(.custom-tabs .el-tabs__nav-wrap::after) { height: 0; }
:deep(.custom-tabs .el-tabs__item) { height: 50px; line-height: 50px; font-size: 15px; color: var(--text-secondary); }
:deep(.custom-tabs .el-tabs__item.is-active) { font-weight: 500; color: var(--juejin-blue); }

.post-item { cursor: pointer; }
.post-title { margin: 0 0 8px; font-size: 16px; font-weight: 600; color: var(--text-primary); }
.post-meta { font-size: 13px; color: var(--text-tertiary); }
.dot { margin: 0 6px; }

.following-item { display: flex; align-items: center; gap: 14px; }
.following-info { flex: 1; }
.following-name { font-size: 15px; font-weight: 500; color: var(--text-primary); }

.empty-state { padding: 40px; text-align: center; color: var(--text-tertiary); }

.stat-card { display: flex; justify-content: center; }
.stat-item { text-align: center; }
.stat-val { font-size: 24px; font-weight: 600; color: var(--juejin-blue); }
.stat-label { font-size: 14px; color: var(--text-tertiary); margin-top: 4px; }
</style>
