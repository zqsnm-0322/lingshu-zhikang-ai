<template>
  <DashboardLayout title="个人中心" subtitle="账号资料、使用统计和基础设置">
    <div class="profile-container">
      <PageHeader
        title="个人中心"
        subtitle="管理头像、查看功能使用情况，并处理本地缓存与登录状态。"
      >
        <template #actions>
          <button class="zq-text-button" type="button" @click="$router.back()">
            <ArrowLeftIcon />
            返回
          </button>
          <button class="zq-text-button zq-primary-button" type="button" @click="triggerFileInput">
            <CameraIcon />
            上传头像
          </button>
        </template>
      </PageHeader>

      <section class="profile-hero zq-card">
        <button class="avatar" type="button" @click="triggerFileInput">
          <img v-if="userInfo.avatarUrl" :src="userInfo.avatarUrl" alt="头像" class="avatar-image" />
          <UserCircleIcon v-else />
          <span class="avatar-overlay">
            <CameraIcon />
            <span>上传头像</span>
          </span>
        </button>
        <input
          ref="fileInput"
          type="file"
          accept="image/*"
          @change="handleFileChange"
          class="file-input"
        />
        <div class="user-info">
          <p>当前账号</p>
          <h2>{{ userInfo.username }}</h2>
          <span>用户ID：{{ userInfo.id || '暂无' }}</span>
        </div>
      </section>

      <section class="zq-grid zq-grid-2">
        <StatCard label="使用天数" :value="usageStats.daysUsed" :icon="CalendarDaysIcon" tone="blue" />
        <StatCard label="总对话次数" :value="usageStats.totalChats" :icon="ChatBubbleLeftRightIcon" tone="cyan" />
      </section>

      <section class="panel zq-card">
        <div class="panel-header">
          <h3 class="zq-section-title">功能使用统计</h3>
          <p class="zq-section-subtitle">从本地统计中读取各功能的使用次数。</p>
        </div>
        <div class="feature-list">
          <div v-for="feature in featureStats" :key="feature.name" class="feature-item">
            <span class="feature-name">{{ feature.name }}</span>
            <span class="feature-count">{{ feature.count }}</span>
          </div>
        </div>
      </section>

      <section class="panel zq-card">
        <div class="panel-header">
          <h3 class="zq-section-title">设置</h3>
          <p class="zq-section-subtitle">处理缓存、查看版本信息或退出当前账号。</p>
        </div>
        <button @click="clearCache" class="menu-item" type="button">
          <TrashIcon />
          <span class="menu-text">清除缓存</span>
          <ChevronRightIcon />
        </button>
        <button @click="showAbout" class="menu-item" type="button">
          <InformationCircleIcon />
          <span class="menu-text">关于我们</span>
          <ChevronRightIcon />
        </button>
      </section>

      <button @click="logout" class="logout-btn" type="button">
        <ArrowRightOnRectangleIcon />
        退出登录
      </button>
    </div>
  </DashboardLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArrowLeftIcon,
  ArrowRightOnRectangleIcon,
  CalendarDaysIcon,
  CameraIcon,
  ChatBubbleLeftRightIcon,
  ChevronRightIcon,
  InformationCircleIcon,
  TrashIcon,
  UserCircleIcon
} from '@heroicons/vue/24/outline'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import StatCard from '@/components/common/StatCard.vue'
import { userAPI } from '@/services/api'

const router = useRouter()
const fileInput = ref(null)

const userInfo = ref({
  username: '',
  id: '',
  avatarUrl: ''
})

// 使用统计数据
const usageStats = ref({
  daysUsed: 0,
  totalChats: 0
})

// 功能使用统计
const featureStats = ref([
  { name: 'AI聊天', icon: '💬', count: 0 },
  { name: '病历分析', icon: '📄', count: 0 },
  { name: '食物推荐', icon: '🍽️', count: 0 },
  { name: '药品查询', icon: '💊', count: 0 },
  { name: '症状自检', icon: '🔬', count: 0 },
  { name: '慢性病管理', icon: '💓', count: 0 },
  { name: '健康日记', icon: '📔', count: 0 },
  { name: '用药提醒', icon: '⏰', count: 0 },
  { name: '体检报告储存', icon: '📋', count: 0 }
])

// 计算使用天数
const calculateDaysUsed = () => {
  const registerDate = localStorage.getItem('registerDate')
  if (registerDate) {
    const start = new Date(registerDate)
    const now = new Date()
    const diffTime = Math.abs(now - start)
    const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24))
    return diffDays
  }
  return 1 // 如果没有注册日期，默认返回1
}

// 加载使用统计
const loadUsageStats = () => {
  // 计算使用天数
  usageStats.value.daysUsed = calculateDaysUsed()
  
  // 加载对话次数统计
  const stats = JSON.parse(localStorage.getItem('featureStats') || '{}')
  let totalChats = 0
  
  featureStats.value.forEach(feature => {
    const count = stats[feature.name] || 0
    feature.count = count
    totalChats += count
  })
  
  usageStats.value.totalChats = totalChats
}

// 记录功能使用次数（工具函数）
const recordFeatureUsage = (featureName) => {
  const stats = JSON.parse(localStorage.getItem('featureStats') || '{}')
  stats[featureName] = (stats[featureName] || 0) + 1
  localStorage.setItem('featureStats', JSON.stringify(stats))
}

const goTo = (path) => {
  router.push(path)
}

// 触发文件选择
const triggerFileInput = () => {
  fileInput.value?.click()
}

// 处理文件选择
const handleFileChange = async (event) => {
  const file = event.target.files[0]
  if (!file) return

  // 验证文件类型
  if (!file.type.startsWith('image/')) {
    ElMessage.error('请选择图片文件')
    return
  }

  // 验证文件大小（限制5MB）
  if (file.size > 5 * 1024 * 1024) {
    ElMessage.error('图片大小不能超过5MB')
    return
  }

  try {
    // 显示加载提示
    const loadingInstance = ElMessage({
      message: '正在上传头像...',
      type: 'info',
      duration: 0,
      showClose: false
    })
    
    // 调用上传API
    const avatarUrl = await userAPI.uploadAvatar(file)
    
    // 关闭加载提示
    loadingInstance.close()
    
    console.log('✅ 头像上传成功，URL:', avatarUrl)
    
    // 更新用户头像
    userInfo.value.avatarUrl = avatarUrl
    
    // 保存到localStorage
    localStorage.setItem('avatarUrl', avatarUrl)
    
    console.log('✅ 头像URL已保存到localStorage')
    console.log('📊 当前userInfo.avatarUrl:', userInfo.value.avatarUrl)
    
    // 触发自定义事件通知其他组件更新头像
    window.dispatchEvent(new CustomEvent('avatarUpdated', { detail: { avatarUrl } }))
    
    ElMessage.success('头像上传成功')
  } catch (error) {
    console.error('❌ 头像上传失败:', error)
    ElMessage.error(error.message || '头像上传失败，请重试')
  } finally {
    // 清空input，允许重复选择相同文件
    if (event.target) {
      event.target.value = ''
    }
  }
}

const clearCache = () => {
  ElMessageBox.confirm('确定要清除所有缓存数据吗？此操作不可恢复。', '确认清除', {
    type: 'warning'
  }).then(() => {
    // 清除所有本地存储（保留登录信息）
    const token = localStorage.getItem('token')
    const username = localStorage.getItem('username')
    
    localStorage.clear()
    
    // 恢复登录信息
    if (token) localStorage.setItem('token', token)
    if (username) localStorage.setItem('username', username)
    
    ElMessage.success('缓存已清除')
  }).catch(() => {})
}

const showAbout = () => {
  ElMessageBox.alert(
    '灵枢智康 | AI多模态智慧健康管理平台 v1.0\n\n提供专业的健康管理与病历分析服务\n\n© 2025 灵枢智康',
    '关于我们',
    {
      confirmButtonText: '知道了'
    }
  )
}

const logout = () => {
  ElMessageBox.confirm('确定要退出登录吗？', '确认退出', {
    type: 'warning'
  }).then(() => {
    // 清除登录信息
    localStorage.removeItem('token')
    localStorage.removeItem('username')
    localStorage.removeItem('userId')
    ElMessage.success('已退出登录')
    
    // 跳转到登录页
    router.replace({ name: 'Login' })
  }).catch(() => {})
}

onMounted(async () => {
  // 获取用户信息
  const token = localStorage.getItem('token')
  const username = localStorage.getItem('username')
  const avatarUrl = localStorage.getItem('avatarUrl')
  
  if (!token) {
    router.replace({ name: 'Login' })
    return
  }
  
  // 生成用户ID显示：如果token是default-token，则使用用户名生成ID
  let userId = ''
  if (token === 'default-token' || !token) {
    // 使用用户名生成一个简单的ID
    userId = username ? `USER_${username.substring(0, 3).toUpperCase()}` : 'GUEST'
  } else {
    // 从真实token中截取
    userId = token.substring(0, 10)
  }
  
  userInfo.value = {
    username: username || '灵枢用户',
    id: userId,
    avatarUrl: avatarUrl || ''
  }
  
  // 设置注册日期（如果是第一次登录）
  if (!localStorage.getItem('registerDate')) {
    localStorage.setItem('registerDate', new Date().toISOString())
  }
  
  // 加载使用统计数据
  loadUsageStats()
  
  // 尝试从服务器获取头像（如果本地没有）
  if (!avatarUrl) {
    try {
      const serverAvatarUrl = await userAPI.getAvatarUrl()
      if (serverAvatarUrl) {
        userInfo.value.avatarUrl = serverAvatarUrl
        localStorage.setItem('avatarUrl', serverAvatarUrl)
      }
    } catch (error) {
      console.log('获取头像失败:', error)
    }
  }
})
</script>

<style scoped lang="scss">
.profile-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.profile-container :deep(.zq-text-button svg) {
  width: 18px;
  height: 18px;
}

.profile-hero {
  display: flex;
  align-items: center;
  gap: 18px;
  padding: 22px;
}

.avatar {
  position: relative;
  display: inline-flex;
  width: 88px;
  height: 88px;
  flex: 0 0 88px;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  border: 0;
  border-radius: 50%;
  background: var(--zq-bg-soft);
  color: var(--zq-color-primary);
  cursor: pointer;
}

.avatar > svg {
  width: 54px;
  height: 54px;
}

.avatar-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.avatar-overlay {
  position: absolute;
  inset: 0;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 4px;
  background: rgba(15, 23, 42, 0.68);
  color: #ffffff;
  opacity: 0;
  transition: opacity 0.2s ease;
  font-size: 12px;
  font-weight: 700;
}

.avatar-overlay svg {
  width: 20px;
  height: 20px;
}

.avatar:hover .avatar-overlay {
  opacity: 1;
}

.file-input {
  display: none;
}

.user-info p,
.user-info h2,
.user-info span {
  margin: 0;
}

.user-info p {
  color: var(--zq-color-primary);
  font-size: 13px;
  font-weight: 700;
}

.user-info h2 {
  margin-top: 6px;
  color: var(--zq-text-primary);
  font-size: 26px;
}

.user-info span {
  display: block;
  margin-top: 8px;
  color: var(--zq-text-secondary);
  font-size: 14px;
}

.panel {
  padding: 20px;
}

.panel-header {
  margin-bottom: 16px;
}

.feature-list {
  display: grid;
  grid-template-columns: repeat(2, minmax(0, 1fr));
  gap: 10px;
}

.feature-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 12px;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-muted);
  padding: 12px;
}

.feature-name {
  color: var(--zq-text-primary);
  font-size: 14px;
  font-weight: 600;
}

.feature-count {
  border-radius: 999px;
  background: var(--zq-bg-soft);
  color: var(--zq-color-primary);
  padding: 4px 10px;
  font-size: 13px;
  font-weight: 800;
}

.menu-item {
  display: flex;
  width: 100%;
  align-items: center;
  gap: 12px;
  min-height: 52px;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-panel);
  color: var(--zq-text-primary);
  padding: 0 14px;
  cursor: pointer;
  text-align: left;
}

.menu-item + .menu-item {
  margin-top: 10px;
}

.menu-item:hover {
  background: var(--zq-bg-soft);
  color: var(--zq-color-primary);
}

.menu-item svg {
  width: 20px;
  height: 20px;
}

.menu-text {
  flex: 1;
  font-size: 14px;
  font-weight: 700;
}

.logout-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  min-height: 44px;
  border: 1px solid rgba(220, 38, 38, 0.24);
  border-radius: var(--zq-radius);
  background: rgba(220, 38, 38, 0.08);
  color: var(--zq-color-danger);
  cursor: pointer;
  font-size: 14px;
  font-weight: 800;
}

.logout-btn svg {
  width: 18px;
  height: 18px;
}

@media (max-width: 760px) {
  .profile-hero {
    align-items: flex-start;
  }

  .feature-list {
    grid-template-columns: 1fr;
  }
}
</style>
