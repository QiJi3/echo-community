<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { listNotifications, markRead } from '../api/notification'
import type { NotificationItem } from '../api/notification'
import { ElMessage } from 'element-plus'

const activeTab = ref('all')
const notifications = ref<NotificationItem[]>([])
const total = ref(0)
const currentPage = ref(1)
const loading = ref(false)

const fetchNotifications = async () => {
  loading.value = true
  try {
    const res = await listNotifications({ page: currentPage.value, size: 20 })
    notifications.value = res.notifications || []
    total.value = res.total
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}


const displayedNotifications = computed(() => {
  if (activeTab.value === 'all') return notifications.value
  return notifications.value.filter(n => n.type === activeTab.value)
})

const handleMarkAllRead = async () => {
  const unreadIds = notifications.value.filter(n => n.status === 0).map(n => n.id)
  if (unreadIds.length === 0) {
    ElMessage.info('没有未读通知')
    return
  }
  try {
    await markRead(unreadIds)
    notifications.value.forEach(n => { n.status = 1 })
    ElMessage.success('已全部标为已读')
  } catch {
    // handled
  }
}

const getTypeLabel = (type: string) => {
  const map: Record<string, string> = {
    like: '点赞',
    follow: '关注',
    system: '系统'
  }
  return map[type] || type
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
  fetchNotifications()
})
</script>

<template>
  <div class="main-container" style="margin-top: 20px;">
    <div class="content-area">
      <div class="echo-panel notification-panel">
        <div class="notif-header">
          <el-tabs v-model="activeTab" class="notif-tabs custom-tabs">
            <el-tab-pane label="全部" name="all" />

            <el-tab-pane label="点赞" name="like" />
            <el-tab-pane label="关注" name="follow" />
            <el-tab-pane label="系统" name="system" />
          </el-tabs>
          <el-button type="primary" link @click="handleMarkAllRead" class="mark-read-btn">全部已读</el-button>
        </div>

        <div v-loading="loading">
          <div
            v-for="notif in displayedNotifications"
            :key="notif.id"
            class="list-item notif-item"
            :class="{ unread: notif.status === 0 }"
          >
            <div class="notif-left">
              <el-tag :type="notif.status === 0 ? 'primary' : 'info'" size="small">{{ getTypeLabel(notif.type) }}</el-tag>
              <span class="notif-text">用户{{ notif.fromUserId }} {{ getTypeLabel(notif.type) }}了你的内容</span>
            </div>
            <span class="notif-time">{{ formatTime(notif.createdAt) }}</span>
          </div>
        </div>

        <div v-if="!loading && displayedNotifications.length === 0" class="empty-notif">暂无通知</div>

        <div v-if="total > 20" class="pagination-wrapper">
          <el-pagination
            v-model:current-page="currentPage"
            :page-size="20"
            :total="total"
            layout="prev, pager, next"
            @current-change="fetchNotifications"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.notification-panel { min-height: 400px; }
.notif-header { display: flex; align-items: center; padding: 0 20px; }
.notif-tabs { flex: 1; }
:deep(.custom-tabs .el-tabs__nav-wrap::after) { height: 0; }
:deep(.custom-tabs .el-tabs__item) { height: 50px; line-height: 50px; font-size: 15px; color: var(--text-secondary); }
:deep(.custom-tabs .el-tabs__item.is-active) { font-weight: 500; color: var(--juejin-blue); }
.mark-read-btn { flex-shrink: 0; }

.notif-item { display: flex; justify-content: space-between; align-items: center; }
.notif-item.unread { background: rgba(30, 128, 255, 0.03); }
.notif-left { display: flex; align-items: center; gap: 10px; }
.notif-text { font-size: 14px; color: var(--text-primary); }
.notif-time { font-size: 13px; color: var(--text-tertiary); flex-shrink: 0; }
.empty-notif { padding: 60px; text-align: center; color: var(--text-tertiary); font-size: 15px; }
.pagination-wrapper { padding: 16px; display: flex; justify-content: center; }
</style>
