import http from '../utils/api'

export interface Post {
  id: number
  userId: number
  title: string
  content: string
  type: number
  status: number
  commentCount: number
  likeCount: number
  score: number
  createdAt: string
  updatedAt: string
}

export interface PostListResponse {
  posts: Post[]
  total: number
  page: number
  size: number
  orderBy: string
  userId: number | null
}

export const listPosts = (params: {
  sort?: string
  page?: number
  size?: number
  userId?: number
}): Promise<PostListResponse> => http.get('/api/post/list', { params })

export const getPost = (id: number): Promise<Post> => http.get(`/api/post/${id}`)

export const createPost = (data: { title: string; content: string }): Promise<Post> =>
  http.post('/api/post', data)

export const deletePost = (id: number): Promise<boolean> => http.delete(`/api/post/${id}`)
