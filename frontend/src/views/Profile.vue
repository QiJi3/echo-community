<script setup lang="ts">
import { ref } from 'vue'
import { ElMessage } from 'element-plus'

const activeName = ref('posts')

interface Post {
  id: number
  title: string
  time: string
  views: number
  likes: number
  comments: number
}

const userPosts = ref<Post[]>([
  { id: 1, title: 'Spring Boot 3.x 升级踩坑记录', time: '2 小时前', views: 1560, likes: 128, comments: 42 },
  { id: 2, title: '一次 Redis 线上故障排查', time: '3 天前', views: 3200, likes: 256, comments: 89 },
  { id: 3, title: 'Docker 部署全栈项目的最佳实践', time: '1 周前', views: 5400, likes: 412, comments: 120 }
])
</script>

<template>
  <div class="main-container profile-container">
    <div class="content-area">
      <!-- Profile Header -->
      <div class="echo-panel profile-header">
        <div class="profile-info-box">
          <el-avatar :size="90" src="https://api.dicebear.com/7.x/bottts/svg?seed=Felix" class="profile-avatar" />
          <div class="profile-text-content">
            <h1 class="profile-name">TechExplorer</h1>
            <div class="profile-desc">
              <el-icon><Postcard /></el-icon> Java Backend Developer | Spring Boot · Redis 爱好者
            </div>
          </div>
          <div class="profile-actions">
            <el-button type="primary" plain @click="ElMessage.info('编辑资料功能开发中...')">编辑个人资料</el-button>
          </div>
        </div>
      </div>

      <!-- Profile Tabs -->
      <div class="echo-panel profile-content">
        <el-tabs v-model="activeName" class="profile-tabs custom-tabs">
          <el-tab-pane label="动态" name="activities" />
          <el-tab-pane label="文章" name="posts" />
          <el-tab-pane label="专栏" name="columns" />
          <el-tab-pane label="沸点" name="pins" />
          <el-tab-pane label="收藏" name="collections" />
          <el-tab-pane label="关注" name="following" />
          <el-tab-pane label="赞" name="likes" />
        </el-tabs>

        <div class="tab-pane-content" v-if="activeName === 'posts'">
          <div v-for="post in userPosts" :key="post.id" class="list-item post-item">
            <h3 class="post-title">{{ post.title }}</h3>
            <div class="post-meta">
              <span>{{ post.time }}</span>
              <span class="dot">·</span>
              <span>{{ post.views }} 阅读</span>
              <span class="dot">·</span>
              <span>{{ post.likes }} 点赞</span>
              <span class="dot">·</span>
              <span>{{ post.comments }} 评论</span>
            </div>
          </div>
        </div>
        
        <div class="tab-pane-content empty-state" v-else>
          <p>这里空空如也~</p>
        </div>
      </div>
    </div>

    <!-- Right Sidebar for Profile -->
    <aside class="sidebar-area">
      <div class="echo-panel achieve-card">
        <div class="card-title">个人成就</div>
        <div class="achieve-item">
          <el-icon class="achieve-icon"><Medal /></el-icon>
          <span>文章被阅读 <strong>10,160</strong></span>
        </div>
        <div class="achieve-item">
          <el-icon class="achieve-icon"><StarFilled /></el-icon>
          <span>文章被点赞 <strong>796</strong></span>
        </div>
      </div>
      
      <div class="echo-panel follow-card">
        <div class="follow-col">
          <div class="f-title">关注了</div>
          <div class="f-val">89</div>
        </div>
        <div class="follow-col center-col">
          <div class="f-title">关注者</div>
          <div class="f-val">256</div>
        </div>
      </div>
    </aside>
  </div>
</template>

<style scoped>
.profile-container {
  margin-top: 20px;
}

.profile-header {
  padding: 30px;
}

.profile-info-box {
  display: flex;
  align-items: center;
}

.profile-avatar {
  margin-right: 24px;
  border: 4px solid #fff;
  background: #f2f3f5;
}

html.dark .profile-avatar { border-color: #1e1e1e; background: #2b2b2b; }

.profile-text-content {
  flex: 1;
}

.profile-name {
  margin: 0 0 12px 0;
  font-size: 24px;
  font-weight: 600;
  color: #252933;
}

html.dark .profile-name { color: #c9cdd4; }

.profile-desc {
  font-size: 14px;
  color: #515767;
  display: flex;
  align-items: center;
  gap: 4px;
}

html.dark .profile-desc { color: #8a919f; }

.profile-content {
  margin-top: 12px;
  min-height: 400px;
}

.profile-tabs {
  padding: 0 20px;
  border-bottom: 1px solid #e4e6eb;
}

html.dark .profile-tabs { border-color: #333; }

:deep(.custom-tabs .el-tabs__nav-wrap::after) { height: 0; }
:deep(.custom-tabs .el-tabs__item) {
  height: 50px;
  line-height: 50px;
  font-size: 15px;
  color: #515767;
}

:deep(.custom-tabs .el-tabs__item.is-active) { font-weight: 500; color: #1e80ff; }

.post-item {
  cursor: pointer;
}

.post-title {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #252933;
}

html.dark .post-title { color: #c9cdd4; }

.post-meta {
  font-size: 13px;
  color: #8a919f;
}

.dot { margin: 0 6px; }

.empty-state {
  padding: 40px;
  text-align: center;
  color: #8a919f;
}

/* Sidebar */
.achieve-card { padding: 16px; }
.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #252933;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e6eb;
}
html.dark .card-title { border-color: #333; color: #c9cdd4; }

.achieve-item {
  display: flex;
  align-items: center;
  margin-bottom: 12px;
  font-size: 14px;
  color: #515767;
}

html.dark .achieve-item { color: #a3a6ad; }

.achieve-item:last-child { margin-bottom: 0; }

.achieve-icon {
  width: 24px;
  height: 24px;
  background: #eaf2ff;
  color: #1e80ff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 12px;
}

html.dark .achieve-icon { background: #1e3a5f; }

.follow-card {
  display: flex;
  padding: 16px 0;
}

.follow-col {
  flex: 1;
  text-align: center;
  cursor: pointer;
}

.center-col {
  border-left: 1px solid #e4e6eb;
}

html.dark .center-col { border-color: #333; }

.f-title {
  font-size: 14px;
  color: #515767;
  margin-bottom: 8px;
}

html.dark .f-title { color: #8a919f; }

.f-val {
  font-size: 16px;
  font-weight: 600;
  color: #252933;
}

html.dark .f-val { color: #c9cdd4; }

.follow-col:hover .f-val { color: #1e80ff; }
</style>
