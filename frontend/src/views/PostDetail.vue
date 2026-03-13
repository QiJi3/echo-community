<script setup lang="ts">
import { ref } from 'vue'
import { CaretTop, ChatRound } from '@element-plus/icons-vue'
import { ElMessage } from 'element-plus'

const isFollowed = ref(false)
const handleFollow = () => {
  isFollowed.value = !isFollowed.value
  ElMessage.success(isFollowed.value ? '关注成功' : '已取消关注')
}
const handleRelatedClick = (title: string) => ElMessage.info(`即将跳转到「${title}」`)

const post = ref({
  id: 1,
  title: 'Spring Boot 3.x 升级踩坑记录与最佳实践',
  author: 'TechExplorer',
  authorAvatar: 'https://api.dicebear.com/7.x/bottts/svg?seed=Felix',
  authorDesc: 'Java Backend Developer | Spring Boot · Redis 爱好者',
  time: '2026-02-15 14:30',
  viewCount: 1560,
  content: `
最近把项目从 Spring Boot 2.7 升级到 3.x，遇到了不少兼容性问题，特别是 \`javax\` → \`jakarta\` 的包名迁移，以及 Spring Security 6.0 基于 Lambda DSL 的全新配置方式。

### 1. Javax to Jakarta
这是最大的坑。由于 Oracle 将 Java EE 移交给 Eclipse 基金会，所有的 \`javax.*\` 包名都改为了 \`jakarta.*\`。
解决方案：
- 替换所有的 import 语句
- 检查第三方依赖是否支持 Jakarta EE 9+

### 2. Spring Security 6.0
WebSecurityConfigurerAdapter 被彻底废弃了，现在必须使用基于 SecurityFilterChain 的 Bean 配置方式...
  `,
  tags: ['Spring Boot', 'Java']
})

const comments = ref([
  {
    id: 1,
    author: 'JavaDev2024',
    avatar: 'https://api.dicebear.com/7.x/bottts/svg?seed=Ginger',
    content: '感谢分享！刚好我们团队准备下个月升级，这篇避雷指南太及时了。',
    time: '2 小时前',
    likes: 12,
    replies: [
      { id: 11, author: 'TechExplorer', authorRole: '楼主', content: '不客气，升级前建议先在测试分支跑一遍全量单测，祝顺利！', time: '1 小时前', likes: 3 }
    ]
  },
  {
    id: 2,
    author: 'SpringFan',
    avatar: 'https://api.dicebear.com/7.x/bottts/svg?seed=Holly',
    content: 'Security 那块确实改动很大，之前习惯了 extends adapter，现在全变成 filter chain 里的 lambda，适应了好几天。',
    time: '3 小时前',
    likes: 8,
    replies: []
  }
])

const commentInput = ref('')
const submitComment = () => {
  if (!commentInput.value) return
  comments.value.unshift({
    id: Date.now(),
    author: 'Guest',
    avatar: 'https://api.dicebear.com/7.x/bottts/svg?seed=Guest',
    content: commentInput.value,
    time: '刚刚',
    likes: 0,
    replies: []
  })
  commentInput.value = ''
}
</script>

<template>
  <div class="main-container">
    <div class="content-area">
      <!-- Post Content -->
      <article class="echo-panel article-panel">
        <h1 class="article-title">{{ post.title }}</h1>
        <div class="article-meta">
          <el-avatar :size="40" :src="post.authorAvatar" class="mr-2" />
          <div class="meta-info">
            <span class="author-name">{{ post.author }}</span>
            <div class="sub-meta">
              <span>{{ post.time }}</span>
              <span class="dot">·</span>
              <span>阅读 {{ post.viewCount }}</span>
            </div>
          </div>
        </div>
        
        <div class="article-content" v-html="post.content.replace(/\n/g, '<br/>')"></div>
        
        <div class="article-tags">
          <span v-for="tag in post.tags" :key="tag" class="tag-label"># {{ tag }}</span>
        </div>
      </article>

      <!-- Comments Section -->
      <div class="echo-panel comment-panel">
        <div class="comment-header">
          <h3>评论 {{ comments.length }}</h3>
        </div>
        
        <div class="comment-box">
          <el-avatar :size="40" src="https://api.dicebear.com/7.x/bottts/svg?seed=Guest" />
          <div class="input-wrapper">
            <el-input 
              v-model="commentInput" 
              type="textarea" 
              placeholder="输入评论（Enter换行，Ctrl+Enter发送）" 
              :rows="3"
            />
            <div class="submit-row">
              <el-button type="primary" @click="submitComment" :disabled="!commentInput">发表评论</el-button>
            </div>
          </div>
        </div>

        <div class="comment-list">
          <div v-for="c in comments" :key="c.id" class="comment-item">
            <el-avatar :size="40" :src="c.avatar" />
            <div class="comment-content-wrap">
              <div class="comment-author">{{ c.author }}</div>
              <div class="comment-text">{{ c.content }}</div>
              <div class="comment-actions">
                <span>{{ c.time }}</span>
                <span class="action-btn"><el-icon><CaretTop /></el-icon>{{ c.likes || '点赞' }}</span>
                <span class="action-btn"><el-icon><ChatRound /></el-icon>回复</span>
              </div>
              
              <!-- Nested Replies -->
              <div class="replies-box" v-if="c.replies.length > 0">
                <div v-for="r in c.replies" :key="r.id" class="reply-item">
                  <span class="reply-author">
                    {{ r.author }}<span v-if="r.authorRole" class="author-badge">楼主</span>: 
                  </span>
                  <span class="reply-text">{{ r.content }}</span>
                  <div class="reply-actions">
                    <span>{{ r.time }}</span>
                    <span class="action-btn"><el-icon><CaretTop /></el-icon>{{ r.likes || '赞' }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Sidebar -->
    <aside class="sidebar-area">
      <!-- Author Intro -->
      <div class="echo-panel author-card">
        <div class="author-info">
          <el-avatar :size="48" :src="post.authorAvatar" />
          <div class="info-right">
            <div class="author-name-side">{{ post.author }}</div>
            <div class="author-level">Lv4</div>
          </div>
        </div>
        <div class="author-desc">{{ post.authorDesc }}</div>
        <div class="author-stats">
          <div class="stat-col">
            <div class="stat-val">42</div>
            <div class="stat-lbl">文章</div>
          </div>
          <div class="stat-col">
            <div class="stat-val">1280</div>
            <div class="stat-lbl">获赞</div>
          </div>
          <div class="stat-col">
            <div class="stat-val">256</div>
            <div class="stat-lbl">粉丝</div>
          </div>
        </div>
        <el-button :type="isFollowed ? 'default' : 'primary'" class="w-full" @click="handleFollow">{{ isFollowed ? '已关注' : '关注' }}</el-button>
      </div>

      <!-- Related Posts -->
      <div class="echo-panel related-card">
        <div class="card-title">相关文章</div>
        <div class="related-list">
          <a class="r-item" @click.prevent="handleRelatedClick('Spring Boot 3.0 正式发布')">Spring Boot 3.0 正式发布！有哪些新特性？</a>
          <a class="r-item" @click.prevent="handleRelatedClick('Java 21 虚拟线程体验报告')">Java 21 虚拟线程体验报告</a>
          <a class="r-item" @click.prevent="handleRelatedClick('MyBatis Plus 踩坑合集')">MyBatis Plus 踩坑合集</a>
        </div>
      </div>
    </aside>
  </div>
</template>

<style scoped>
/* Article Panel */
.article-panel {
  padding: 32px;
}

.article-title {
  margin: 0 0 24px 0;
  font-size: 32px;
  font-weight: 700;
  line-height: 1.31;
  color: #252933;
}

html.dark .article-title {
  color: #c9cdd4;
}

.article-meta {
  display: flex;
  align-items: center;
  margin-bottom: 24px;
}

.mr-2 { margin-right: 12px; }

.author-name {
  font-size: 16px;
  font-weight: 500;
  color: #515767;
}

.sub-meta {
  font-size: 14px;
  color: #8a919f;
  margin-top: 4px;
  display: flex;
  align-items: center;
}

.dot { margin: 0 6px; }

.article-content {
  font-size: 16px;
  line-height: 1.75;
  color: #252933;
  word-break: break-word;
}

html.dark .article-content {
  color: #a3a6ad;
}

.article-tags {
  margin-top: 32px;
  display: flex;
  gap: 12px;
}

.tag-label {
  background: #f2f3f5;
  color: #515767;
  padding: 4px 12px;
  border-radius: 4px;
  font-size: 14px;
}

html.dark .tag-label {
  background: #2b2b2b;
  color: #8a919f;
}

/* Comment Panel */
.comment-panel {
  padding: 32px;
}

.comment-header h3 {
  margin: 0 0 24px 0;
  font-size: 18px;
  color: #252933;
}

html.dark .comment-header h3 {
  color: #c9cdd4;
}

.comment-box {
  display: flex;
  gap: 16px;
  margin-bottom: 32px;
}

.input-wrapper {
  flex: 1;
}

:deep(.input-wrapper .el-textarea__inner) {
  background-color: #f2f3f5;
  border: 1px solid transparent;
  box-shadow: none;
  font-family: inherit;
}

:deep(.input-wrapper .el-textarea__inner:focus) {
  background-color: #fff;
  border-color: #1e80ff;
}

html.dark :deep(.input-wrapper .el-textarea__inner) { background-color: #2b2b2b; }
html.dark :deep(.input-wrapper .el-textarea__inner:focus) { background-color: #1e1e1e; }

.submit-row {
  margin-top: 12px;
  display: flex;
  justify-content: flex-end;
}

.comment-item {
  display: flex;
  gap: 16px;
  margin-bottom: 24px;
}

.comment-content-wrap {
  flex: 1;
  border-bottom: 1px solid #e4e6eb;
  padding-bottom: 16px;
}

html.dark .comment-content-wrap { border-color: #333; }
.comment-item:last-child .comment-content-wrap { border-bottom: none; padding-bottom: 0; }

.comment-author {
  font-size: 15px;
  font-weight: 500;
  color: #252933;
  margin-bottom: 6px;
}

html.dark .comment-author { color: #c9cdd4; }

.comment-text {
  font-size: 15px;
  color: #515767;
  line-height: 1.6;
  margin-bottom: 8px;
}

html.dark .comment-text { color: #a3a6ad; }

.comment-actions {
  display: flex;
  align-items: center;
  gap: 16px;
  font-size: 14px;
  color: #8a919f;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 4px;
  cursor: pointer;
  transition: color 0.2s;
}

.action-btn:hover { color: #1e80ff; }

/* Replies */
.replies-box {
  margin-top: 12px;
  background: #f2f3f5;
  border-radius: 4px;
  padding: 12px 16px;
}

html.dark .replies-box { background: #2b2b2b; }

.reply-item {
  margin-bottom: 12px;
  font-size: 14px;
}

.reply-item:last-child { margin-bottom: 0; }

.reply-author {
  color: #252933;
  font-weight: 500;
}

html.dark .reply-author { color: #c9cdd4; }

.author-badge {
  background: #eaf2ff;
  color: #1e80ff;
  font-size: 12px;
  padding: 0 4px;
  border-radius: 2px;
  margin-left: 4px;
}

.reply-text {
  color: #515767;
  line-height: 1.6;
}

html.dark .reply-text { color: #a3a6ad; }

.reply-actions {
  display: flex;
  align-items: center;
  gap: 12px;
  font-size: 12px;
  color: #8a919f;
  margin-top: 4px;
}

/* Sidebar Info */
.author-card {
  padding: 20px;
}

.author-info {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 12px;
}

.author-name-side {
  font-size: 16px;
  font-weight: 600;
  color: #252933;
}

html.dark .author-name-side { color: #c9cdd4; }

.author-level {
  font-size: 12px;
  color: #1e80ff;
}

.author-desc {
  font-size: 14px;
  color: #515767;
  line-height: 1.5;
  margin-bottom: 16px;
  padding-bottom: 16px;
  border-bottom: 1px solid #e4e6eb;
}

html.dark .author-desc { border-color: #333; color: #a3a6ad; }

.author-stats {
  display: flex;
  justify-content: space-between;
  margin-bottom: 16px;
  text-align: center;
}

.stat-col {
  flex: 1;
}

.stat-val {
  font-size: 16px;
  font-weight: 600;
  color: #252933;
}

html.dark .stat-val { color: #c9cdd4; }

.stat-lbl {
  font-size: 12px;
  color: #8a919f;
  margin-top: 4px;
}

.w-full {
  width: 100%;
}

.related-card {
  padding: 16px;
}

.card-title {
  font-size: 16px;
  font-weight: 600;
  color: #252933;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 1px solid #e4e6eb;
}

html.dark .card-title { border-color: #333; color: #c9cdd4; }

.related-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.r-item {
  font-size: 14px;
  color: #515767;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

html.dark .r-item { color: #a3a6ad; }

.r-item:hover { color: #1e80ff; }
</style>
