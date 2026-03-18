export type ApiErrorBody = {
  error?: string
  message?: string
}

export type LoginRequest = {
  username: string
  password: string
}

export type LoginResponse = {
  token: string
}

export type RegisterRequest = {
  username: string
  password: string
}

export type PostResponse = {
  id: number
  title: string
  content: string
  author?: string
  createdAt?: string
  updatedAt?: string
}

export type CreatePostRequest = {
  title: string
  content: string
}

export type UpdatePostRequest = {
  title: string
  content: string
}

export type Page<T> = {
  content: T[]
  totalElements: number
  totalPages: number
  size: number
  number: number
  first: boolean
  last: boolean
  numberOfElements: number
  empty: boolean
}
