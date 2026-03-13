import http from '../utils/api'

export interface Conversation {
  conversationId: string
  targetUserId: number
  targetUsername: string
  lastContent: string
  unreadCount: number
  lastMessageTime: string
}

export interface ConversationListResponse {
  conversations: Conversation[]
  total: number
  page: number
  size: number
}

export interface Message {
  id: number
  fromId: number
  toId: number
  conversationId: string
  content: string
  status: number
  createdAt: string
}

export interface MessagePageResponse {
  messages: Message[]
  total: number
  page: number
  size: number
}

export const listConversations = (params?: {
  page?: number
  size?: number
}): Promise<ConversationListResponse> => http.get('/api/message', { params })

export const listMessages = (params: {
  conversationId: string
  page?: number
  size?: number
}): Promise<MessagePageResponse> => http.get('/api/message', { params })

export const sendMessage = (data: {
  toId: number
  content: string
}): Promise<Message> => http.post('/api/message', data)
