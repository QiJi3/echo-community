<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { View, ChatDotSquare, Star, User } from '@element-plus/icons-vue'

const router = useRouter()
const activeTab = ref('recommended')

interface Post {
  id: number
  title: string
  content: string
  author: string
  time: string
  commentCount: number
  likeCount: number
  viewCount: number
  tags: string[]
}

const posts = ref<Post[]>([
  {
    id: 1,
    title: 'Spring Boot 3.x 升级踩坑记录与最佳实践',
    content: '最近把项目从 Spring Boot 2.7 升级到 3.x，遇到了不少兼容性问题，特别是 javax → jakarta 的包名迁移，以及 Spring Security 6.0 基于 Lambda DSL 的全新配置方式。本文详细总结了整个升级过程中的痛点与解决方案...',
    author: 'TechExplorer',
    time: '2 小时前',
    commentCount: 42,
    likeCount: 128,
    viewCount: 1560,
    tags: ['后端', 'Spring Boot']
  },
  {
    id: 2,
    title: 'Redis 分布式锁深入剖析：从 SETNX 到 Redisson',
    content: '在高并发场景下，分布式锁是保证数据一致性的关键手段。本文将对比最原始的 SETNX 方案、基于 LUA 脚本的优化方案，以及 Redisson 中看门狗 (Watchdog) 机制的底层原理与应用...',
    author: 'RedisGuru',
    time: '5 小时前',
    commentCount: 23,
    likeCount: 89,
    viewCount: 930,
    tags: ['架构设计', 'Redis']
  },
  {
    id: 3,
    title: 'Vue 3 + Vite + Element Plus 企业级中后台框架搭建指北',
    content: '从零开始讲解如何通过 Vite 搭建 Vue 3 项目，配置 TypeScript、Pinia 状态管理库以及 Vue Router。重点介绍了如何优雅地按需引入 Element Plus 组件与自定义自动导入...',
    author: '前端大牛',
    time: '1 天前',
    commentCount: 15,
    likeCount: 56,
    viewCount: 720,
    tags: ['前端', 'Vue.js']
  },
  {
    id: 4,
    title: '百万级并发量下的消息推送系统该如何架构？',
    content: '从传统的数据库轮询到 WebSocket 实时双向通信，再到引入 RabbitMQ/Kafka 消息队列进行异步削峰填谷，一文带你理解高性能消息通知系统的架构演进之路...',
    author: 'Architect007',
    time: '2 天前',
    commentCount: 31,
    likeCount: 95,
    viewCount: 1280,
    tags: ['系统架构', '高并发']
  }
])

const goPost = (id: number) => router.push(`/post/${id}`)
</script>

<template>
  <div class="main-container">
    <!-- Main Content Flow (Left) -->
    <div class="content-area">
      <div class="echo-panel home-header">
        <el-tabs v-model="activeTab" class="custom-tabs">
          <el-tab-pane label="推荐" name="recommended" />
          <el-tab-pane label="最新" name="latest" />
          <el-tab-pane label="热榜" name="hot" />
        </el-tabs>
      </div>

      <div class="echo-panel post-list-panel">
        <div
          v-for="post in posts"
          :key="post.id"
          class="list-item post-item"
          @click="goPost(post.id)"
        >
          <div class="post-item-main">
            <h2 class="title">{{ post.title }}</h2>
            <p class="abstract">{{ post.content }}</p>
            
            <div class="meta-row">
              <span class="meta-item writer">
                <el-icon class="mr-1"><User /></el-icon> {{ post.author }}
              </span>
              <span class="meta-item">{{ post.time }}</span>
              <span class="meta-item"><span class="dot">·</span> {{ post.tags[0] }}</span>
              <div class="actions">
                <span class="action-item"><el-icon><View /></el-icon>{{ post.viewCount }}</span>
                <span class="action-item"><el-icon><Star /></el-icon>{{ post.likeCount }}</span>
                <span class="action-item"><el-icon><ChatDotSquare /></el-icon>{{ post.commentCount }}</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sidebar (Right) -->
    <aside class="sidebar-area">
      <!-- Welcome Card -->
      <div class="echo-panel sidebar-card">
        <div class="welcome-box">
          <div class="title-row">
            <h4>早上好，工程师！</h4>
          </div>
          <p class="desc">点亮你在 Echo 的每一天 ☀️</p>
          <el-button type="primary" plain class="w-full sign-btn">去签到</el-button>
        </div>
      </div>

      <!-- Quick Links Card -->
      <div class="echo-panel sidebar-card links-card">
        <div class="link-item"><span class="badge new">新</span> 代码规范指南 V2.0</div>
        <div class="link-item">开源项目推荐栏目</div>
        <div class="link-item">2026 届秋招面经大合集</div>
      </div>
    </aside>
  </div>
</template>

<style scoped>
/* Home Content Adjustments */
.home-header {
  padding: 0 20px;
  margin-bottom: 2px;
}

:deep(.custom-tabs .el-tabs__header) {
  margin: 0;
}

:deep(.custom-tabs .el-tabs__nav-wrap::after) {
  height: 0;
}

:deep(.custom-tabs .el-tabs__item) {
  height: 48px;
  line-height: 48px;
  font-size: 15px;
  color: #515767;
}

:deep(.custom-tabs .el-tabs__item.is-active) {
  font-weight: 500;
  color: #1e80ff; /* Juejin blue */
}

/* Post List Item */
.post-list-panel {
  padding: 0;
}

.post-item {
  cursor: pointer;
  display: flex;
  gap: 16px;
}

.title {
  font-size: 16px;
  font-weight: 600;
  line-height: 24px;
  color: #252933;
  margin: 0 0 8px 0;
  display: -webkit-box;
  -webkit-line-clamp: 1;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.abstract {
  color: #8a919f;
  font-size: 14px;
  line-height: 22px;
  margin: 0 0 12px 0;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  text-overflow: ellipsis;
}

.meta-row {
  display: flex;
  align-items: center;
  font-size: 13px;
  color: #8a919f;
  gap: 12px;
}

.meta-item {
  display: flex;
  align-items: center;
}

.writer {
  color: #515767;
}

.mr-1 {
  margin-right: 4px;
}

.dot {
  font-weight: bold;
  margin-right: 4px;
}

.actions {
  margin-left: auto;
  display: flex;
  gap: 16px;
}

.action-item {
  display: flex;
  align-items: center;
  gap: 4px;
  transition: color 0.2s;
}

.post-item:hover .action-item:hover {
  color: #1e80ff;
}

html.dark .title {
  color: #e4e6eb;
}

html.dark .abstract, html.dark .meta-row {
  color: #8a919f;
}

/* Sidebar Styles */
.sidebar-card {
  padding: 16px;
}

.welcome-box h4 {
  margin: 0 0 4px 0;
  font-size: 16px;
  color: #252933;
}

.welcome-box .desc {
  margin: 0 0 16px 0;
  font-size: 12px;
  color: #8a919f;
}

.w-full {
  width: 100%;
}

.sign-btn {
  border-radius: 4px;
}

.links-card {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.link-item {
  font-size: 14px;
  color: #515767;
  cursor: pointer;
  display: flex;
  align-items: center;
}

.link-item:hover {
  color: #1e80ff;
}

.badge.new {
  font-size: 12px;
  background-color: #f56c6c;
  color: #fff;
  padding: 0 4px;
  border-radius: 2px;
  margin-right: 6px;
  line-height: 16px;
}
</style>
