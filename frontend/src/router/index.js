import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  { path: '/', redirect: '/activity/list' },
  { path: '/login', name: 'login', component: () => import('@/views/Login.vue') },
  { path: '/register', name: 'register', component: () => import('@/views/Register.vue') },
  { path: '/activity/list', name: 'activityList', component: () => import('@/views/ActivityList.vue') },
  { path: '/activity/detail/:id', name: 'activityDetail', component: () => import('@/views/ActivityDetail.vue') },
  { path: '/activity/publish', name: 'activityPublish', component: () => import('@/views/ActivityPublish.vue') },
  { path: '/activity/edit/:id', name: 'activityEdit', component: () => import('@/views/ActivityEdit.vue') },
  { path: '/my/activity', name: 'myActivity', component: () => import('@/views/MyActivity.vue') },
]

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes,
})

// 路由守卫：未登录只能访问登录、注册页
router.beforeEach((to) => {
  const token = localStorage.getItem('token')
  const publicPages = ['/login', '/register']
  if (!token && !publicPages.includes(to.path)) {
    return '/login'
  }
  if (token && publicPages.includes(to.path)) {
    return '/activity/list'
  }
})

export default router
