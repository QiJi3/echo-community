import { createRouter, createWebHistory } from 'vue-router'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: () => import('../views/Home.vue')
    },
    {
      path: '/post/:id',
      name: 'post-detail',
      component: () => import('../views/PostDetail.vue')
    },
    {
      path: '/profile/:id?',
      name: 'profile',
      component: () => import('../views/Profile.vue')
    },
    {
      path: '/messages',
      name: 'messages',
      component: () => import('../views/Messages.vue')
    },
    {
      path: '/notifications',
      name: 'notifications',
      component: () => import('../views/Notifications.vue')
    }
  ]
})

export default router
