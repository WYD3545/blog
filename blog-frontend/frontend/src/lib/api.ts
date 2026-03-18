import { http } from './http'
import type {
  CreatePostRequest,
  LoginRequest,
  LoginResponse,
  Page,
  PostResponse,
  RegisterRequest,
  UpdatePostRequest,
} from './types'

export const api = {
  auth: {
    login: (body: LoginRequest) => http<LoginResponse>('/api/auth/login', { method: 'POST', body }),
    register: (body: RegisterRequest) =>
      http<string>('/api/auth/register', { method: 'POST', body }),
  },
  posts: {
    list: (params: { page?: number; size?: number }) => {
      const sp = new URLSearchParams()
      if (params.page !== undefined) sp.set('page', String(params.page))
      if (params.size !== undefined) sp.set('size', String(params.size))
      const qs = sp.toString()
      return http<Page<PostResponse>>(`/api/posts${qs ? `?${qs}` : ''}`)
    },
    search: (params: { title: string; page?: number; size?: number }) => {
      const sp = new URLSearchParams()
      sp.set('title', params.title)
      if (params.page !== undefined) sp.set('page', String(params.page))
      if (params.size !== undefined) sp.set('size', String(params.size))
      return http<Page<PostResponse>>(`/api/posts/search?${sp.toString()}`)
    },
    get: (id: number) => http<PostResponse>(`/api/posts/${id}`),
    create: (body: CreatePostRequest, token: string) =>
      http<PostResponse>('/api/posts', { method: 'POST', body, token }),
    update: (id: number, body: UpdatePostRequest, token: string) =>
      http<PostResponse>(`/api/posts/${id}`, { method: 'PUT', body, token }),
    remove: (id: number, token: string) =>
      http<void>(`/api/posts/${id}`, { method: 'DELETE', token }),
  },
}

