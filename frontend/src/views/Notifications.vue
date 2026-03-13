<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const markAllRead = () => {
  notifications.value.forEach(n => n.read = true)
  ElMessage.success('已全部标记为已读')
}

const activeTab = ref('all')

const notifications = ref([
  { id: 1, type: 'comment', user: '前端小妹', action: '评论了你的文章', target: '《Vue 3 性能优化指南》', time: '10分钟前', read: false },
  { id: 2, type: 'like', user: 'Node大牛', action: '赞了你的文章', target: '《百万级并发架构演进》', time: '1小时前', read: true },
  { id: 3, type: 'follow', user: 'Go开发者', action: '关注了你', target: '', time: '昨天', read: true }
])
</script>

<template>
  <div class="main-container">
    <div class="content-area">
      <div class="echo-panel notif-panel">
        <div class="notif-header">
          <h2>消息通知</h2>
          <el-button type="primary" link @click="markAllRead">全部已读</el-button>
        </div>
        <el-tabs v-model="activeTab" class="custom-tabs notif-tabs">
          <el-tab-pane label="全部" name="all" />
          <el-tab-pane label="点赞和收藏" name="likes" />
          <el-tab-pane label="新增粉丝" name="follows" />
          <el-tab-pane label="系统通知" name="system" />
        </el-tabs>

        <div class="notif-list">
          <div v-for="item in notifications" :key="item.id" class="list-item notif-item" :class="{ unread: !item.read }">
            <el-avatar :size="40" src="https://api.dicebear.com/7.x/bottts/svg?seed=Felix" />
            <div class="notif-content">
              <div class="notif-text">
                <span class="n-user">{{ item.user }}</span>
                <span class="n-action">{{ item.action }}</span>
                <span v-if="item.target" class="n-target">{{ item.target }}</span>
              </div>
              <div class="notif-time">{{ item.time }}</div>
            </div>
            <div class="unread-dot" v-if="!item.read"></div>
          </div>
        </div>
      </div>
    </div>
    
    <aside class="sidebar-area">
      <div class="echo-panel sidebar-card">
        <p style="color: #8a919f; font-size: 14px; margin: 0;">消息设置可以在“个人设置”中修改，请注意查收系统重要通知。</p>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.notif-panel {
  min-height: 500px;
}

.notif-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 20px;
  border-bottom: 1px solid #e4e6eb;
}

html.dark .notif-header { border-color: #333; }

.notif-header h2 {
  margin: 0;
  font-size: 18px;
  color: #252933;
}

html.dark .notif-header h2 { color: #c9cdd4; }

.notif-tabs {
  padding: 0 20px;
}

:deep(.custom-tabs .el-tabs__nav-wrap::after) { height: 0; }
:deep(.custom-tabs .el-tabs__item) {
  height: 48px;
  line-height: 48px;
  font-size: 15px;
  color: #515767;
}
:deep(.custom-tabs .el-tabs__item.is-active) { font-weight: 500; color: #1e80ff; }

.notif-item {
  display: flex;
  align-items: center;
  gap: 16px;
  position: relative;
}

.notif-content {
  flex: 1;
}

.notif-text {
  font-size: 15px;
  color: #515767;
  margin-bottom: 4px;
}

html.dark .notif-text { color: #a3a6ad; }

.n-user {
  font-weight: 500;
  color: #252933;
  margin-right: 8px;
}

html.dark .n-user { color: #c9cdd4; }

.n-target {
  color: #1e80ff;
  margin-left: 8px;
  cursor: pointer;
}

.notif-time {
  font-size: 13px;
  color: #8a919f;
}

.unread-dot {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background-color: #f56c6c;
}
</style>
