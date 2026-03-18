import http from '../utils/api'

export interface MomentItem {
  id: number
  userId: number
  content: string
  topic: string | null
  likeCount: number
  createdAt: string
  updatedAt: string
}

export interface MomentListResponse {
  moments: MomentItem[]
  total: number
  page: number
  size: number
  topic: string | null
}

export const listMoments = (params: {
  topic?: string
  page?: number
  size?: number
}): Promise<MomentListResponse> => http.get('/api/moments', { params })

export const getMoment = (id: number): Promise<MomentItem> => http.get(`/api/moments/${id}`)
