<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import { listConversations, listMessages, sendMessage } from '../api/message'
import type { Conversation, Message } from '../api/message'
import { useUserStore } from '../stores/useUserStore'

const userStore = useUserStore()
const conversations = ref<Conversation[]>([])
const activeConversationId = ref('')
const messages = ref<Message[]>([])
const newMessage = ref('')
const loading = ref(false)

const currentConversation = computed(() =>
  conversations.value.find(c => c.conversationId === activeConversationId.value)
)

const fetchConversations = async () => {
  loading.value = true
  try {
    const res = await listConversations({ page: 1, size: 50 })
    conversations.value = res.conversations || []
    if (conversations.value.length > 0 && !activeConversationId.value) {
      activeConversationId.value = conversations.value[0].conversationId
      fetchMessages()
    }
  } catch {
    // handled
  } finally {
    loading.value = false
  }
}

const fetchMessages = async () => {
  if (!activeConversationId.value) return
  try {
    const res = await listMessages({
      conversationId: activeConversationId.value,
      page: 1,
      size: 100
    })
    messages.value = res.messages || []
  } catch {
    // handled
  }
}

const selectConversation = (id: string) => {
  activeConversationId.value = id
  fetchMessages()
}

const handleSend = async () => {
  if (!newMessage.value.trim()) return
  if (!currentConversation.value) return
  try {
    await sendMessage({
      toId: currentConversation.value.targetUserId,
      content: newMessage.value.trim()
    })
    newMessage.value = ''
    fetchMessages()
  } catch {
    // handled
  }
}

const formatTime = (dateStr: string) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  return `${date.getMonth() + 1}/${date.getDate()} ${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
}

onMounted(() => {
  fetchConversations()
})
</script>

<template>
  <div class="main-container" style="margin-top: 20px;">
    <div class="content-area">
      <div class="echo-panel messages-panel" v-loading="loading">
        <div class="messages-layout">
          <!-- Contact List -->
          <div class="contact-list">
            <h3 class="contact-title">私信列表</h3>
            <div
              v-for="conv in conversations"
              :key="conv.conversationId"
              class="contact-item"
              :class="{ active: activeConversationId === conv.conversationId }"
              @click="selectConversation(conv.conversationId)"
            >
              <el-avatar :size="40" :src="`https://api.dicebear.com/7.x/bottts/svg?seed=${conv.targetUserId}`" />
              <div class="contact-info">
                <div class="contact-name">{{ conv.targetUsername || `用户${conv.targetUserId}` }}</div>
                <div class="contact-last">{{ conv.lastContent }}</div>
              </div>
              <el-badge v-if="conv.unreadCount" :value="conv.unreadCount" class="contact-badge" />
            </div>
            <div v-if="conversations.length === 0" class="empty-contacts">暂无私信</div>
          </div>

          <!-- Chat Area -->
          <div class="chat-area">
            <template v-if="currentConversation">
              <div class="chat-header">
                <h3>{{ currentConversation.targetUsername || `用户${currentConversation.targetUserId}` }}</h3>
              </div>
              <div class="chat-messages">
                <div
                  v-for="msg in messages"
                  :key="msg.id"
                  class="chat-msg"
                  :class="{ mine: msg.fromId === userStore.userInfo?.id }"
                >
                  <div class="msg-bubble">{{ msg.content }}</div>
                  <div class="msg-time">{{ formatTime(msg.createdAt) }}</div>
                </div>
                <div v-if="messages.length === 0" class="empty-messages">暂无消息记录</div>
              </div>
              <div class="chat-input-area">
                <el-input
                  v-model="newMessage"
                  type="textarea"
                  :rows="2"
                  placeholder="输入消息..."
                  @keyup.ctrl.enter="handleSend"
                />
                <el-button type="primary" @click="handleSend" :disabled="!newMessage.trim()">发送</el-button>
              </div>
            </template>
            <div v-else class="empty-chat">
              <p>选择一个对话开始聊天</p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<style scoped>
.messages-panel { min-height: 600px; }
.messages-layout { display: flex; height: 600px; }

.contact-list {
  width: 280px; border-right: 1px solid var(--border-color);
  overflow-y: auto; flex-shrink: 0;
}
.contact-title { padding: 16px; margin: 0; font-size: 16px; color: var(--text-primary); border-bottom: 1px solid var(--border-color); }
.contact-item {
  display: flex; align-items: center; gap: 12px;
  padding: 14px 16px; cursor: pointer; transition: background 0.15s;
}
.contact-item:hover { background: rgba(30, 128, 255, 0.04); }
.contact-item.active { background: rgba(30, 128, 255, 0.08); }
.contact-info { flex: 1; min-width: 0; }
.contact-name { font-size: 15px; font-weight: 500; color: var(--text-primary); }
.contact-last { font-size: 13px; color: var(--text-tertiary); white-space: nowrap; overflow: hidden; text-overflow: ellipsis; margin-top: 4px; }
.empty-contacts { padding: 40px; text-align: center; color: var(--text-tertiary); }

.chat-area { flex: 1; display: flex; flex-direction: column; min-width: 0; }
.chat-header { padding: 16px; border-bottom: 1px solid var(--border-color); }
.chat-header h3 { margin: 0; font-size: 16px; color: var(--text-primary); }
.chat-messages { flex: 1; overflow-y: auto; padding: 16px; }
.chat-msg { margin-bottom: 16px; }
.chat-msg.mine { text-align: right; }
.msg-bubble {
  display: inline-block; max-width: 70%; padding: 10px 14px;
  background: #f0f2f5; border-radius: 8px; font-size: 14px; color: var(--text-primary);
  text-align: left; word-break: break-word;
}
html.dark .msg-bubble { background: #2b2b2b; }
.chat-msg.mine .msg-bubble { background: #1e80ff; color: #fff; }
.msg-time { font-size: 12px; color: var(--text-tertiary); margin-top: 4px; }
.chat-input-area {
  padding: 12px 16px; border-top: 1px solid var(--border-color);
  display: flex; gap: 10px; align-items: flex-end;
}
.chat-input-area .el-input { flex: 1; }
.empty-chat { display: flex; justify-content: center; align-items: center; flex: 1; color: var(--text-tertiary); }
.empty-messages { text-align: center; padding: 40px; color: var(--text-tertiary); }
</style>
