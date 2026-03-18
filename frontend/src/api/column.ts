import http from '../utils/api'

export interface ColumnArticle {
  id: number
  userId: number
  columnName: string
  title: string
  content: string
  coverImg: string | null
  status: number
  likeCount: number
  viewCount: number
  createdAt: string
  updatedAt: string
}

export interface ColumnArticleListResponse {
  articles: ColumnArticle[]
  total: number
  page: number
  size: number
  columnName: string | null
}

export const listColumns = (params: {
  columnName?: string
  page?: number
  size?: number
}): Promise<ColumnArticleListResponse> => http.get('/api/columns', { params })

export const getColumn = (id: number): Promise<ColumnArticle> => http.get(`/api/columns/${id}`)
