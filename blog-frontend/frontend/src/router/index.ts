import { createRouter, createWebHistory } from 'vue-router'
import { useAuthStore } from '@/stores/auth'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', redirect: '/posts' },
    {
      path: '/login',
      name: 'login',
      component: () => import('@/views/LoginView.vue'),
    },
    {
      path: '/register',
      name: 'register',
      component: () => import('@/views/RegisterView.vue'),
    },
    {
      path: '/posts',
      name: 'posts',
      component: () => import('@/views/PostsListView.vue'),
    },
    {
      path: '/posts/new',
      name: 'post-new',
      meta: { requireAuth: true },
      component: () => import('@/views/PostEditorView.vue'),
    },
    {
      path: '/posts/:id',
      name: 'post-detail',
      component: () => import('@/views/PostDetailView.vue'),
      props: true,
    },
    {
      path: '/posts/:id/edit',
      name: 'post-edit',
      meta: { requireAuth: true },
      component: () => import('@/views/PostEditorView.vue'),
      props: true,
    },
  ],
})

router.beforeEach((to) => {
  const requireAuth = Boolean(to.meta.requireAuth)
  if (!requireAuth) return true

  const auth = useAuthStore()
  if (auth.isAuthed) return true

  return { name: 'login', query: { next: to.fullPath } }
})

export default router
