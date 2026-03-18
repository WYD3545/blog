<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { HttpError } from '@/lib/http'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref<string | null>(null)

const nextPath = computed(() => {
  const q = route.query.next
  return typeof q === 'string' && q.length > 0 ? q : '/posts'
})

async function submit() {
  errorMsg.value = null
  loading.value = true
  try {
    await auth.login({ username: username.value.trim(), password: password.value })
    await router.replace(nextPath.value)
  } catch (e) {
    if (e instanceof HttpError) errorMsg.value = e.message
    else errorMsg.value = '登录失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="card">
    <h2>登录</h2>

    <form class="form" @submit.prevent="submit">
      <label class="field">
        <span class="label">用户名</span>
        <input v-model="username" class="input" autocomplete="username" required />
      </label>

      <label class="field">
        <span class="label">密码</span>
        <input
          v-model="password"
          class="input"
          type="password"
          autocomplete="current-password"
          required
        />
      </label>

      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

      <button class="btn primary" type="submit" :disabled="loading">
        {{ loading ? '登录中...' : '登录' }}
      </button>

      <p class="hint">
        没有账号？
        <RouterLink to="/register">去注册</RouterLink>
      </p>
    </form>
  </div>
</template>

<style scoped>
.card {
  max-width: 420px;
  margin: 24px auto;
  padding: 18px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
}

h2 {
  margin: 0 0 12px;
}

.form {
  display: grid;
  gap: 12px;
}

.field {
  display: grid;
  gap: 6px;
}

.label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.input {
  width: 100%;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(0, 0, 0, 0.18);
  color: #fff;
  outline: none;
}

.input:focus {
  border-color: rgba(99, 102, 241, 0.7);
}

.btn {
  border-radius: 12px;
  padding: 10px 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  cursor: pointer;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.primary {
  background: rgba(99, 102, 241, 0.25);
  border-color: rgba(99, 102, 241, 0.45);
}

.error {
  margin: 0;
  color: #fecaca;
}

.hint {
  margin: 0;
  font-size: 14px;
  color: rgba(255, 255, 255, 0.7);
}

a {
  color: #c7d2fe;
}
</style>

