import type { ApiErrorBody } from './types'

export class HttpError extends Error {
  status: number
  body?: unknown

  constructor(status: number, message: string, body?: unknown) {
    super(message)
    this.name = 'HttpError'
    this.status = status
    this.body = body
  }
}

function getBaseUrl() {
  return (import.meta.env.VITE_API_BASE_URL as string | undefined) ?? 'http://localhost:8080'
}

async function readBodySafe(res: Response) {
  const contentType = res.headers.get('content-type') ?? ''
  if (contentType.includes('application/json')) {
    try {
      return (await res.json()) as unknown
    } catch {
      return undefined
    }
  }
  try {
    return await res.text()
  } catch {
    return undefined
  }
}

function toErrorMessage(status: number, body: unknown) {
  if (typeof body === 'string' && body.trim().length > 0) return body
  if (body && typeof body === 'object') {
    const b = body as ApiErrorBody
    if (b.error) return b.error
    if (b.message) return b.message
  }
  if (status === 401) return '未登录或登录已过期'
  if (status === 403) return '没有权限执行此操作'
  return '请求失败'
}

export type HttpOptions = {
  method?: 'GET' | 'POST' | 'PUT' | 'DELETE'
  headers?: Record<string, string>
  body?: unknown
  token?: string | null
}

export async function http<T>(path: string, opts: HttpOptions = {}): Promise<T> {
  const url = `${getBaseUrl()}${path.startsWith('/') ? path : `/${path}`}`

  const headers: Record<string, string> = {
    ...(opts.headers ?? {}),
  }

  if (opts.body !== undefined) headers['Content-Type'] = 'application/json'
  if (opts.token) headers.Authorization = `Bearer ${opts.token}`

  const res = await fetch(url, {
    method: opts.method ?? 'GET',
    headers,
    body: opts.body === undefined ? undefined : JSON.stringify(opts.body),
  })

  if (!res.ok) {
    const body = await readBodySafe(res)
    if (res.status === 401) {
      try {
        window.dispatchEvent(new Event('auth:unauthorized'))
      } catch {
        // ignore
      }
    }
    throw new HttpError(res.status, toErrorMessage(res.status, body), body)
  }

  if (res.status === 204) return undefined as T

  const contentType = res.headers.get('content-type') ?? ''
  if (contentType.includes('application/json')) {
    return (await res.json()) as T
  }

  return (await res.text()) as unknown as T
}

