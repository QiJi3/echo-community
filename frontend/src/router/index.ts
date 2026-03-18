import { createRouter, createWebHistory } from 'vue-router'
import { useUserStore } from '../stores/useUserStore'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    { path: '/', name: 'home', component: () => import('../views/Home.vue') },
    { path: '/post/:id', name: 'post-detail', component: () => import('../views/PostDetail.vue') },
    { path: '/login', name: 'login', component: () => import('../views/Login.vue'), meta: { guest: true } },
    { path: '/profile', name: 'profile', component: () => import('../views/Profile.vue'), meta: { auth: true } },
    { path: '/notifications', name: 'notifications', component: () => import('../views/Notifications.vue'), meta: { auth: true } },
    { path: '/messages', name: 'messages', component: () => import('../views/Messages.vue'), meta: { auth: true } },
    { path: '/checkin', name: 'checkin', component: () => import('../views/CheckIn.vue'), meta: { auth: true } },
    { path: '/column', name: 'column', component: () => import('../views/column/index.vue') },
    { path: '/interview', name: 'interview', component: () => import('../views/interview/index.vue') },
    { path: '/moment', name: 'moment', component: () => import('../views/moment/index.vue') }
  ]
})

router.beforeEach((to) => {
  const userStore = useUserStore()
  if (to.meta.auth && !userStore.isLoggedIn) {
    return { path: '/login', query: { redirect: to.fullPath } }
  }
  if (to.meta.guest && userStore.isLoggedIn) {
    return '/'
  }
})

export default router
