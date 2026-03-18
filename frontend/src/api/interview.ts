import http from '../utils/api'

export interface Interview {
  id: number
  userId: number
  company: string
  position: string
  difficulty: number
  result: string
  title: string
  content: string
  likeCount: number
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface InterviewListResponse {
  interviews: Interview[]
  total: number
  page: number
  size: number
  company: string | null
  position: string | null
}

export const listInterviews = (params: {
  company?: string
  position?: string
  page?: number
  size?: number
}): Promise<InterviewListResponse> => http.get('/api/interviews', { params })

export const getInterview = (id: number): Promise<Interview> => http.get(`/api/interviews/${id}`)
