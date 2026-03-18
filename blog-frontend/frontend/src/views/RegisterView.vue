<script setup lang="ts">
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { HttpError } from '@/lib/http'

const auth = useAuthStore()
const router = useRouter()

const username = ref('')
const password = ref('')
const loading = ref(false)
const errorMsg = ref<string | null>(null)
const successMsg = ref<string | null>(null)

async function submit() {
  errorMsg.value = null
  successMsg.value = null
  loading.value = true
  try {
    const msg = await auth.register({ username: username.value.trim(), password: password.value })
    successMsg.value = typeof msg === 'string' ? msg : '注册成功'
    await router.replace('/login')
  } catch (e) {
    if (e instanceof HttpError) errorMsg.value = e.message
    else errorMsg.value = '注册失败'
  } finally {
    loading.value = false
  }
}
</script>

<template>
  <div class="card">
    <h2>注册</h2>

    <form class="form" @submit.prevent="submit">
      <label class="field">
        <span class="label">用户名</span>
        <input v-model="username" class="input" autocomplete="username" required />
      </label>

      <label class="field">
        <span class="label">密码</span>
        <input v-model="password" class="input" type="password" autocomplete="new-password" required />
      </label>

      <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
      <p v-if="successMsg" class="success">{{ successMsg }}</p>

      <button class="btn primary" type="submit" :disabled="loading">
        {{ loading ? '提交中...' : '注册' }}
      </button>

      <p class="hint">
        已有账号？
        <RouterLink to="/login">去登录</RouterLink>
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

.success {
  margin: 0;
  color: #a7f3d0;
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

