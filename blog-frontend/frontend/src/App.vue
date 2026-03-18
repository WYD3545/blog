<script setup lang="ts">
import { useAuthStore } from '@/stores/auth'

const auth = useAuthStore()
</script>

<template>
  <div class="app">
    <header class="topbar">
      <div class="brand">
        <RouterLink to="/posts" class="brand-link">Blog System</RouterLink>
      </div>

      <nav class="nav">
        <RouterLink to="/posts" class="nav-link">文章</RouterLink>
        <RouterLink to="/posts/new" class="nav-link">新建</RouterLink>
      </nav>

      <div class="auth">
        <template v-if="auth.isAuthed">
          <span class="username">{{ auth.username ?? '已登录' }}</span>
          <button class="btn" type="button" @click="auth.logout()">退出</button>
        </template>
        <template v-else>
          <RouterLink class="nav-link" to="/login">登录</RouterLink>
          <RouterLink class="nav-link" to="/register">注册</RouterLink>
        </template>
      </div>
    </header>

    <main class="content">
      <RouterView />
    </main>
  </div>
</template>

<style scoped>
.app {
  min-height: 100vh;
  background: #0b1220;
  color: #e5e7eb;
}

.topbar {
  position: sticky;
  top: 0;
  z-index: 10;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 18px;
  background: rgba(16, 24, 39, 0.9);
  border-bottom: 1px solid rgba(255, 255, 255, 0.08);
  backdrop-filter: blur(10px);
}

.brand-link {
  color: #fff;
  font-weight: 700;
  letter-spacing: 0.2px;
  text-decoration: none;
}

.nav {
  display: flex;
  gap: 10px;
  flex: 1;
  justify-content: center;
}

.nav-link {
  color: #c7d2fe;
  text-decoration: none;
  padding: 6px 10px;
  border-radius: 10px;
}

.nav-link.router-link-active {
  background: rgba(99, 102, 241, 0.18);
  color: #fff;
}

.auth {
  display: flex;
  align-items: center;
  gap: 10px;
}

.username {
  color: #a7f3d0;
  font-size: 14px;
}

.btn {
  appearance: none;
  border: 1px solid rgba(255, 255, 255, 0.14);
  background: rgba(255, 255, 255, 0.06);
  color: #fff;
  padding: 6px 10px;
  border-radius: 10px;
  cursor: pointer;
}

.btn:hover {
  background: rgba(255, 255, 255, 0.1);
}

.content {
  max-width: 960px;
  margin: 0 auto;
  padding: 18px;
}
</style>
