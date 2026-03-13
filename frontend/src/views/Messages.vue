<script setup lang="ts">
import { ref } from 'vue'

const chatInput = ref('')

const chatMessages = ref([
  { id: 1, fromSelf: false, content: '你好！看了你关于 Spring Boot 3 升级的帖子，写得很棒！', time: '2 小时前' },
  { id: 2, fromSelf: true, content: '谢谢！你也在做升级吗？', time: '1 小时前' },
  { id: 3, fromSelf: false, content: '是的，javax 那个坑搞了我一天，看了你的帖子才恍然大悟', time: '50 分钟前' }
])

const sendMessage = () => {
  if (!chatInput.value.trim()) return
  chatMessages.value.push({
    id: Date.now(),
    fromSelf: true,
    content: chatInput.value,
    time: '刚刚'
  })
  chatInput.value = ''
}
</script>

<template>
  <div class="main-container msg-container">
    <div class="echo-panel msg-panel">
      <!-- Left sidebar -->
      <div class="msg-sidebar">
        <div class="sidebar-header">近期联络</div>
        <div class="contact-list">
          <div class="contact-item active">
            <el-avatar :size="40" src="https://api.dicebear.com/7.x/bottts/svg?seed=Ginger" />
            <div class="c-info">
              <div class="c-top">
                <span class="c-name">JavaDev2024</span>
                <span class="c-time">50分钟前</span>
              </div>
              <div class="c-msg">是的，javax 那个坑搞了我一天...</div>
            </div>
          </div>
          <div class="contact-item">
            <el-avatar :size="40" src="https://api.dicebear.com/7.x/bottts/svg?seed=Holly" />
            <div class="c-info">
              <div class="c-top">
                <span class="c-name">SpringFan</span>
                <span class="c-time">昨天</span>
              </div>
              <div class="c-msg">那篇文章写得真不错，点赞了</div>
            </div>
          </div>
        </div>
      </div>

      <!-- Right chat area -->
      <div class="msg-chat-area">
        <div class="chat-header">
          <h3>JavaDev2024</h3>
        </div>
        <div class="chat-history">
          <div v-for="msg in chatMessages" :key="msg.id" class="msg-bubble-wrap" :class="{ self: msg.fromSelf }">
            <div class="msg-bubble">{{ msg.content }}</div>
          </div>
        </div>
        <div class="chat-input-box">
          <el-input v-model="chatInput" placeholder="输入消息..." type="textarea" :rows="3" resize="none" />
          <div class="btn-row">
            <el-button type="primary" size="small" @click="sendMessage">发送 (Enter)</el-button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.msg-panel {
  display: flex;
  height: calc(100vh - 100px);
  width: 100%;
}

.msg-sidebar {
  width: 280px;
  border-right: 1px solid #e4e6eb;
  display: flex;
  flex-direction: column;
}

html.dark .msg-sidebar { border-color: #333; }

.sidebar-header {
  padding: 16px 20px;
  font-size: 15px;
  font-weight: 600;
  color: #252933;
  border-bottom: 1px solid #e4e6eb;
}

html.dark .sidebar-header { border-color: #333; color: #c9cdd4; }

.contact-list {
  flex: 1;
  overflow-y: auto;
}

.contact-item {
  display: flex;
  padding: 12px 20px;
  gap: 12px;
  cursor: pointer;
  align-items: center;
}

.contact-item:hover { background: #fcfcfc; }
html.dark .contact-item:hover { background: #252525; }

.contact-item.active { background: #f2f3f5; }
html.dark .contact-item.active { background: #2b2b2b; }

.c-info {
  flex: 1;
  min-width: 0;
}

.c-top {
  display: flex;
  justify-content: space-between;
  margin-bottom: 4px;
}

.c-name {
  font-size: 14px;
  font-weight: 500;
  color: #252933;
}

html.dark .c-name { color: #c9cdd4; }

.c-time {
  font-size: 12px;
  color: #8a919f;
}

.c-msg {
  font-size: 13px;
  color: #8a919f;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.msg-chat-area {
  flex: 1;
  display: flex;
  flex-direction: column;
}

.chat-header {
  padding: 16px 20px;
  border-bottom: 1px solid #e4e6eb;
}

html.dark .chat-header { border-color: #333; }

.chat-header h3 {
  margin: 0;
  font-size: 16px;
  color: #252933;
}

html.dark .chat-header h3 { color: #c9cdd4; }

.chat-history {
  flex: 1;
  padding: 20px;
  overflow-y: auto;
  background: #f4f5f5;
  display: flex;
  flex-direction: column;
  gap: 16px;
}

html.dark .chat-history { background: #121212; }

.msg-bubble-wrap {
  display: flex;
  justify-content: flex-start;
}

.msg-bubble-wrap.self {
  justify-content: flex-end;
}

.msg-bubble {
  max-width: 70%;
  padding: 10px 14px;
  background: #fff;
  border-radius: 4px;
  font-size: 14px;
  color: #252933;
  line-height: 1.5;
  box-shadow: 0 1px 2px rgba(0,0,0,0.05);
}

html.dark .msg-bubble { background: #1e1e1e; color: #a3a6ad; }

.msg-bubble-wrap.self .msg-bubble {
  background: #1e80ff;
  color: #fff;
}

.chat-input-box {
  padding: 16px;
  border-top: 1px solid #e4e6eb;
}

html.dark .chat-input-box { border-color: #333; }

:deep(.chat-input-box .el-textarea__inner) {
  border: none;
  box-shadow: none;
  background: transparent;
  font-family: inherit;
  font-size: 14px;
}

.btn-row {
  display: flex;
  justify-content: flex-end;
  margin-top: 8px;
}
</style>
