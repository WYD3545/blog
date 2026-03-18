<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { api } from '@/lib/api'
import type { PostResponse } from '@/lib/types'
import { HttpError } from '@/lib/http'
import { useAuthStore } from '@/stores/auth'

const route = useRoute()
const router = useRouter()
const auth = useAuthStore()

const rawId = computed(() => route.params.id)
const isEdit = computed(() => typeof rawId.value === 'string' && rawId.value.length > 0)
const id = computed(() => (isEdit.value ? Number(rawId.value) : null))

const loading = ref(false)
const saving = ref(false)
const errorMsg = ref<string | null>(null)
const post = ref<PostResponse | null>(null)

const title = ref('')
const content = ref('')

async function loadForEdit() {
  if (!isEdit.value || id.value === null || Number.isNaN(id.value)) return
  loading.value = true
  errorMsg.value = null
  try {
    post.value = await api.posts.get(id.value)
    title.value = post.value.title
    content.value = post.value.content
  } catch (e) {
    if (e instanceof HttpError) errorMsg.value = e.message
    else errorMsg.value = '加载失败'
  } finally {
    loading.value = false
  }
}

async function submit() {
  errorMsg.value = null

  const t = title.value.trim()
  const c = content.value.trim()
  if (!t || !c) {
    errorMsg.value = '标题和内容不能为空'
    return
  }

  if (!auth.token) {
    await router.push({ name: 'login', query: { next: route.fullPath } })
    return
  }

  saving.value = true
  try {
    if (isEdit.value && id.value !== null && !Number.isNaN(id.value)) {
      const updated = await api.posts.update(id.value, { title: t, content: c }, auth.token)
      await router.replace(`/posts/${updated.id}`)
    } else {
      const created = await api.posts.create({ title: t, content: c }, auth.token)
      await router.replace(`/posts/${created.id}`)
    }
  } catch (e) {
    if (auth.isUnauthorizedError(e)) {
      await router.push({ name: 'login', query: { next: route.fullPath } })
      return
    }
    if (e instanceof HttpError) errorMsg.value = e.message
    else errorMsg.value = '保存失败'
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  void loadForEdit()
})
</script>

<template>
  <div class="wrap">
    <div class="header">
      <RouterLink class="btn ghost" to="/posts">← 返回列表</RouterLink>
      <div class="right">
        <span class="meta">{{ isEdit ? '编辑文章' : '新建文章' }}</span>
      </div>
    </div>

    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    <p v-else-if="loading" class="hint">加载中...</p>

    <form class="card" @submit.prevent="submit">
      <label class="field">
        <span class="label">标题</span>
        <input v-model="title" class="input" maxlength="120" required />
      </label>

      <label class="field">
        <span class="label">内容</span>
        <textarea v-model="content" class="textarea" rows="14" required />
      </label>

      <div class="actions">
        <button class="btn primary" type="submit" :disabled="saving || loading">
          {{ saving ? '保存中...' : '保存' }}
        </button>
        <RouterLink v-if="isEdit && id !== null" class="btn ghost" :to="`/posts/${id}`">取消</RouterLink>
        <RouterLink v-else class="btn ghost" to="/posts">取消</RouterLink>
      </div>
    </form>
  </div>
</template>

<style scoped>
.wrap {
  display: grid;
  gap: 14px;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
}

.meta {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
}

.card {
  display: grid;
  gap: 12px;
  padding: 14px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
}

.field {
  display: grid;
  gap: 6px;
}

.label {
  font-size: 13px;
  color: rgba(255, 255, 255, 0.7);
}

.input,
.textarea {
  width: 100%;
  padding: 10px 12px;
  border-radius: 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(0, 0, 0, 0.18);
  color: #fff;
  outline: none;
}

.input:focus,
.textarea:focus {
  border-color: rgba(99, 102, 241, 0.7);
}

.textarea {
  resize: vertical;
  min-height: 220px;
  font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', 'Courier New', monospace;
  white-space: pre-wrap;
  line-height: 1.5;
}

.actions {
  display: flex;
  gap: 10px;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.btn {
  border-radius: 12px;
  padding: 8px 12px;
  border: 1px solid rgba(255, 255, 255, 0.12);
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  cursor: pointer;
  text-decoration: none;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}

.ghost {
  background: rgba(255, 255, 255, 0.03);
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
  color: rgba(255, 255, 255, 0.7);
}
</style>

