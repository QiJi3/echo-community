import http from '../utils/api'

export interface NotificationItem {
  id: number
  userId: number
  type: string
  entityType: number
  entityId: number
  fromUserId: number
  status: number
  createdAt: string
}

export interface NotificationListResponse {
  notifications: NotificationItem[]
  total: number
  page: number
  size: number
}

export interface UnreadCountResponse {
  unreadCount: number
}

export interface ReadResponse {
  updatedCount: number
}

export const listNotifications = (params?: {
  page?: number
  size?: number
}): Promise<NotificationListResponse> =>
  http.get('/api/notification', { params: { action: 'list', ...params } })

export const getUnreadCount = (): Promise<UnreadCountResponse> =>
  http.get('/api/notification', { params: { action: 'count' } })

export const markRead = (ids?: number[]): Promise<ReadResponse> =>
  http.get('/api/notification', { params: { action: 'read', ids: ids?.join(',') } })
