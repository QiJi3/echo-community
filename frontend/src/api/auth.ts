import http from '../utils/api'

export interface CaptchaResponse {
  captchaId: string
  imageBase64: string  // data:image/png;base64,...
}

export interface AuthUser {
  id: number
  username: string
  email: string
  avatar: string | null
  role: string
}

export interface LoginData {
  username: string
  password: string
  captchaId: string
  captchaCode: string
  rememberMe?: boolean
}

export interface RegisterData {
  username: string
  password: string
  email: string
  avatar?: string
}

export const getCaptcha = (): Promise<CaptchaResponse> => http.get('/api/v1/auth/captcha')

export const login = (data: LoginData): Promise<AuthUser> => http.post('/api/v1/auth/login', data)

export const register = (data: RegisterData): Promise<AuthUser> => http.post('/api/v1/auth/register', data)

export const logout = (): Promise<boolean> => http.post('/api/v1/auth/logout')
