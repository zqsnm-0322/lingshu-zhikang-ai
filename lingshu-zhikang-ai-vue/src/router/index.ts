import { createRouter, createWebHistory } from 'vue-router'
import { ElMessage } from 'element-plus'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue')
  },
  {
    path: '/ai-chat',
    name: 'AIChat',
    component: () => import('../views/AIChat.vue')
  },
  {
    path: '/chat-pdf',
    name: 'ChatPDF',
    component: () => import('../views/ChatPDF.vue')
  },
  {
    path: '/medical-diagnosis',
    name: 'MedicalDiagnosis',
    component: () => import('../views/OpenCV/MedicalDiagnosis.vue')
  },
  {
    path: '/daily-food-recommendation',
    name: 'DailyFoodRecommendation',
    component: () => import('../views/DailyFoodRecommendation.vue')
  },
  {
    path: '/medicine-query',
    name: 'MedicineQuery',
    component: () => import('../views/MedicineQuery.vue')
  },
  {
    path: '/symptom-checker',
    name: 'SymptomChecker',
    component: () => import('../views/SymptomChecker.vue')
  },
  {
    path: '/chronic-disease',
    name: 'ChronicDisease',
    component: () => import('../views/ChronicDisease.vue')
  },
  {
    path: '/health-diary',
    name: 'HealthDiary',
    component: () => import('../views/HealthDiary.vue')
  },
  {
    path: '/medicine-reminder',
    name: 'MedicineReminder',
    component: () => import('../views/MedicineReminder.vue')
  },
  {
    path: '/health-report',
    name: 'HealthReport',
    component: () => import('../views/HealthReport.vue')
  },
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('../views/Profile.vue')
  },
  {
    path: '/membership',
    name: 'Membership',
    component: () => import('../views/Membership.vue')
  },
  {
    path: '/business-dashboard',
    name: 'BusinessDashboard',
    component: () => import('../views/BusinessDashboard.vue')
  },
  {
    path: '/admin',
    name: 'AdminDashboard',
    component: () => import('../views/AdminDashboard.vue'),
    meta: { requiresAdmin: true }
  },
  {
    path: '/health-butler',
    name: 'HealthButler',
    component: () => import('../views/HealthButler.vue')
  },
  {
    path: '/health-dashboard',
    name: 'HealthDashboard',
    component: () => import('../views/HealthDashboard.vue')
  },
  {
    path: '/health-warning',
    name: 'HealthWarning',
    component: () => import('../views/HealthWarning.vue')
  },

  {
    path: '/health-profile',
    name: 'HealthProfile',
    component: () => import('../views/HealthProfile.vue')
  },
  {
    path: '/example-feature',
    name: 'ExampleFeature',
    component: () => import('../views/ExampleFeaturePage.vue')
  },

  {
    path: '/home',
    redirect: '/' // 重定向到根路径
  },
  {
    // 捕获所有未匹配的路由，重定向到首页或登录页
    path: '/:pathMatch(.*)*',
    redirect: '/'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 添加路由导航守卫
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')
  const userRole = localStorage.getItem('userRole') // 获取用户角色
  
  // 如果路由不存在，重定向到首页
  if (!to.name && to.path !== '/') {
    next('/')
    return
  }
  
  // 检查是否需要管理员权限
  if (to.meta.requiresAdmin && userRole !== 'ADMIN') {
    ElMessage.warning('需要管理员权限才能访问此页面')
    next({ name: 'Home' })
    return
  }
  
  // 需要登录的路由
  const requiresAuth = to.meta.requiresAuth !== false && to.name !== 'Login'

  if (requiresAuth && !token) {
    // 未登录且要访问需要登录的页面，跳转到登录页
    next({ name: 'Login' })
  } else if (to.name === 'Login' && token) {
    // 已登录且要访问登录页，跳转到主页
    next({ name: 'Home' })
  } else {
    next()
  }
})

export default router
