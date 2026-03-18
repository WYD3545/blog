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

const id = computed(() => Number(route.params.id))

const loading = ref(false)
const deleting = ref(false)
const errorMsg = ref<string | null>(null)
const post = ref<PostResponse | null>(null)

async function fetchOne() {
  loading.value = true
  errorMsg.value = null
  try {
    post.value = await api.posts.get(id.value)
  } catch (e) {
    if (e instanceof HttpError) errorMsg.value = e.message
    else errorMsg.value = '加载失败'
  } finally {
    loading.value = false
  }
}

async function remove() {
  if (!auth.token) {
    await router.push({ name: 'login', query: { next: route.fullPath } })
    return
  }
  if (!confirm('确定要删除这篇文章吗？')) return

  deleting.value = true
  errorMsg.value = null
  try {
    await api.posts.remove(id.value, auth.token)
    await router.replace('/posts')
  } catch (e) {
    if (auth.isUnauthorizedError(e)) {
      await router.push({ name: 'login', query: { next: route.fullPath } })
      return
    }
    if (e instanceof HttpError) errorMsg.value = e.message
    else errorMsg.value = '删除失败'
  } finally {
    deleting.value = false
  }
}

onMounted(() => {
  void fetchOne()
})
</script>

<template>
  <div class="wrap">
    <div class="header">
      <RouterLink class="btn ghost" to="/posts">← 返回列表</RouterLink>
      <div class="actions">
        <RouterLink class="btn ghost" :to="`/posts/${id}/edit`">编辑</RouterLink>
        <button class="btn danger" type="button" :disabled="deleting" @click="remove">
          {{ deleting ? '删除中...' : '删除' }}
        </button>
      </div>
    </div>

    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    <p v-else-if="loading" class="hint">加载中...</p>

    <article v-else-if="post" class="card">
      <h2 class="title">{{ post.title }}</h2>
      <div class="meta">
        <span v-if="post.author">作者：{{ post.author }}</span>
        <span v-if="post.updatedAt">更新：{{ new Date(post.updatedAt).toLocaleString() }}</span>
        <span v-else-if="post.createdAt">创建：{{ new Date(post.createdAt).toLocaleString() }}</span>
      </div>
      <pre class="content">{{ post.content }}</pre>
    </article>
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

.actions {
  display: flex;
  gap: 10px;
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

.danger {
  background: rgba(239, 68, 68, 0.18);
  border-color: rgba(239, 68, 68, 0.35);
}

.error {
  margin: 0;
  color: #fecaca;
}

.hint {
  margin: 0;
  color: rgba(255, 255, 255, 0.7);
}

.card {
  padding: 14px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
}

.title {
  margin: 0 0 8px;
}

.meta {
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: rgba(255, 255, 255, 0.65);
  font-size: 13px;
  margin-bottom: 12px;
}

.content {
  margin: 0;
  white-space: pre-wrap;
  font-family: ui-sans-serif, system-ui, -apple-system, Segoe UI, Roboto, Helvetica, Arial, 'Apple Color Emoji',
    'Segoe UI Emoji';
  line-height: 1.6;
  color: rgba(255, 255, 255, 0.88);
}
</style>

