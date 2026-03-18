<script setup lang="ts">
import { computed, onMounted, ref } from 'vue'
import { api } from '@/lib/api'
import type { Page, PostResponse } from '@/lib/types'
import { HttpError } from '@/lib/http'

const page = ref(0)
const size = ref(10)
const query = ref('')

const loading = ref(false)
const errorMsg = ref<string | null>(null)
const data = ref<Page<PostResponse> | null>(null)

const posts = computed(() => data.value?.content ?? [])
const totalPages = computed(() => data.value?.totalPages ?? 0)

async function fetchList() {
  loading.value = true
  errorMsg.value = null
  try {
    if (query.value.trim().length > 0) {
      data.value = await api.posts.search({
        title: query.value.trim(),
        page: page.value,
        size: size.value,
      })
    } else {
      data.value = await api.posts.list({ page: page.value, size: size.value })
    }
  } catch (e) {
    if (e instanceof HttpError) errorMsg.value = e.message
    else errorMsg.value = '加载失败'
  } finally {
    loading.value = false
  }
}

function goPage(p: number) {
  page.value = Math.max(0, p)
  void fetchList()
}

function submitSearch() {
  page.value = 0
  void fetchList()
}

onMounted(() => {
  void fetchList()
})
</script>

<template>
  <div class="wrap">
    <div class="toolbar">
      <form class="search" @submit.prevent="submitSearch">
        <input v-model="query" class="input" placeholder="按标题搜索（title）" />
        <button class="btn" type="submit" :disabled="loading">搜索</button>
        <button class="btn ghost" type="button" :disabled="loading" @click="(query = ''), submitSearch()">
          清空
        </button>
      </form>

      <div class="pager">
        <button class="btn ghost" type="button" :disabled="loading || page <= 0" @click="goPage(page - 1)">
          上一页
        </button>
        <span class="meta">第 {{ page + 1 }} 页 / 共 {{ Math.max(totalPages, 1) }} 页</span>
        <button
          class="btn ghost"
          type="button"
          :disabled="loading || (data?.last ?? true)"
          @click="goPage(page + 1)"
        >
          下一页
        </button>
      </div>
    </div>

    <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
    <p v-else-if="loading" class="hint">加载中...</p>
    <p v-else-if="posts.length === 0" class="hint">暂无文章</p>

    <div class="list" v-else>
      <article v-for="p in posts" :key="p.id" class="item">
        <div class="item-main">
          <RouterLink class="title" :to="`/posts/${p.id}`">{{ p.title }}</RouterLink>
          <div class="sub">
            <span v-if="p.author">作者：{{ p.author }}</span>
            <span v-if="p.updatedAt">更新：{{ new Date(p.updatedAt).toLocaleString() }}</span>
            <span v-else-if="p.createdAt">创建：{{ new Date(p.createdAt).toLocaleString() }}</span>
          </div>
          <p class="excerpt">{{ p.content }}</p>
        </div>

        <div class="item-actions">
          <RouterLink class="btn ghost" :to="`/posts/${p.id}`">查看</RouterLink>
          <RouterLink class="btn ghost" :to="`/posts/${p.id}/edit`">编辑</RouterLink>
        </div>
      </article>
    </div>
  </div>
</template>

<style scoped>
.wrap {
  display: grid;
  gap: 14px;
}

.toolbar {
  display: grid;
  gap: 10px;
  padding: 12px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
}

.search {
  display: flex;
  gap: 10px;
  align-items: center;
  flex-wrap: wrap;
}

.input {
  flex: 1;
  min-width: 220px;
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

.pager {
  display: flex;
  gap: 10px;
  align-items: center;
  justify-content: flex-end;
  flex-wrap: wrap;
}

.meta {
  color: rgba(255, 255, 255, 0.7);
  font-size: 14px;
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
  gap: 8px;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.ghost {
  background: rgba(255, 255, 255, 0.03);
}

.error {
  margin: 0;
  color: #fecaca;
}

.hint {
  margin: 0;
  color: rgba(255, 255, 255, 0.7);
}

.list {
  display: grid;
  gap: 10px;
}

.item {
  display: flex;
  gap: 14px;
  align-items: flex-start;
  justify-content: space-between;
  padding: 12px;
  background: rgba(255, 255, 255, 0.04);
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 16px;
}

.item-main {
  flex: 1;
  min-width: 0;
}

.title {
  color: #fff;
  text-decoration: none;
  font-weight: 700;
  font-size: 18px;
}

.sub {
  margin-top: 6px;
  display: flex;
  gap: 12px;
  flex-wrap: wrap;
  color: rgba(255, 255, 255, 0.65);
  font-size: 13px;
}

.excerpt {
  margin: 10px 0 0;
  color: rgba(255, 255, 255, 0.8);
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
  white-space: pre-wrap;
}

.item-actions {
  display: flex;
  flex-direction: column;
  gap: 8px;
}
</style>

