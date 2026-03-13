import http from '../utils/api'

export interface LikeResponse {
  entityType: number
  entityId: number
  likeCount: number
  liked: boolean
}

export interface FollowActionResponse {
  entityType: number
  entityId: number
  followed: boolean
  followerCount: number
  followeeCount: number
}

export const like = (entityType: number, entityId: number): Promise<LikeResponse> =>
  http.post('/api/like', { entityType, entityId })

export const follow = (entityType: number, entityId: number, doFollow = true): Promise<FollowActionResponse> =>
  http.post('/api/follow', { entityType, entityId, follow: doFollow })

export const listFollowees = (params: {
  userId: number
  entityType: number
  page?: number
  size?: number
}): Promise<any> => http.get('/api/followees', { params })

export const listFollowers = (params: {
  entityType: number
  entityId: number
  page?: number
  size?: number
}): Promise<any> => http.get('/api/followers', { params })
