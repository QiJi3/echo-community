import http from '../utils/api'

export interface Comment {
  id: number
  userId: number
  entityType: number
  entityId: number
  targetId: number | null
  content: string
  status: number
  createdAt: string
  updatedAt: string
}

export interface CommentListResponse {
  comments: Comment[]
  total: number
  page: number
  size: number
}

export const listComments = (params: {
  entityType: number
  entityId: number
  page?: number
  size?: number
}): Promise<CommentListResponse> => http.get('/api/comment/list', { params })

export const createComment = (data: {
  entityType: number
  entityId: number
  targetId?: number
  content: string
}): Promise<Comment> => http.post('/api/comment', data)
