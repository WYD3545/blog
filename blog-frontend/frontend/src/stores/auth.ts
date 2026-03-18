import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { api } from '@/lib/api'
import { HttpError } from '@/lib/http'

const TOKEN_KEY = 'blog_token'
const UNAUTHORIZED_EVENT = 'auth:unauthorized'

export const useAuthStore = defineStore('auth', () => {
  const token = ref<string | null>(null)
  const username = ref<string | null>(null)

  const isAuthed = computed(() => Boolean(token.value))

  function loadFromStorage() {
    const t = localStorage.getItem(TOKEN_KEY)
    token.value = t && t.length > 0 ? t : null
    username.value = token.value ? parseUsernameFromJwt(token.value) : null
  }

  function saveToStorage(t: string | null) {
    if (t) localStorage.setItem(TOKEN_KEY, t)
    else localStorage.removeItem(TOKEN_KEY)
  }

  function logout() {
    token.value = null
    username.value = null
    saveToStorage(null)
  }

  async function login(payload: { username: string; password: string }) {
    const res = await api.auth.login(payload)
    token.value = res.token
    username.value = parseUsernameFromJwt(res.token) ?? payload.username
    saveToStorage(res.token)
  }

  async function register(payload: { username: string; password: string }) {
    return await api.auth.register(payload)
  }

  function init() {
    loadFromStorage()

    window.removeEventListener(UNAUTHORIZED_EVENT, onUnauthorized as EventListener)
    window.addEventListener(UNAUTHORIZED_EVENT, onUnauthorized as EventListener)
  }

  function onUnauthorized() {
    logout()
  }

  function notifyUnauthorized() {
    window.dispatchEvent(new Event(UNAUTHORIZED_EVENT))
  }

  function isUnauthorizedError(err: unknown) {
    return err instanceof HttpError && err.status === 401
  }

  return {
    token,
    username,
    isAuthed,
    init,
    login,
    register,
    logout,
    notifyUnauthorized,
    isUnauthorizedError,
  }
})

function parseUsernameFromJwt(jwt: string) {
  // JWT payload is base64url JSON; we only need "sub"
  const parts = jwt.split('.')
  if (parts.length < 2) return null
  try {
    const payload = JSON.parse(base64UrlDecode(parts[1])) as { sub?: string }
    return payload.sub ?? null
  } catch {
    return null
  }
}

function base64UrlDecode(input: string) {
  const base64 = input.replace(/-/g, '+').replace(/_/g, '/')
  const padLen = (4 - (base64.length % 4)) % 4
  const padded = base64 + '='.repeat(padLen)
  return decodeURIComponent(
    Array.from(atob(padded))
      .map((c) => `%${c.charCodeAt(0).toString(16).padStart(2, '0')}`)
      .join(''),
  )
}

