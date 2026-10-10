<template>
  <div class="arvr-health">
    <!-- 头部导航 -->
    <div class="header">
      <h1 class="title">
        <img src="https://api.iconify.design/mdi:virtual-reality.svg?color=%232d3748" class="title-icon" alt="VR" />
        AR/VR健康教育与康复训练
      </h1>
      <router-link to="/" class="back-home">
        <img src="https://api.iconify.design/mdi:home.svg?color=%23ffffff" class="icon" alt="首页" />
        返回首页
      </router-link>
    </div>

    <!-- 主要内容区域 -->
    <div class="content">
      <!-- 功能卡片区域 -->
      <div class="feature-cards">
        <div 
          v-for="feature in features" 
          :key="feature.id"
          class="feature-card"
          :style="{ background: feature.bgColor }"
          @click="navigateToFeature(feature.route)"
        >
          <img :src="feature.icon" class="card-icon" :alt="feature.title" />
          <h3 class="card-title">{{ feature.title }}</h3>
          <p class="card-desc">{{ feature.description }}</p>
          <div class="card-badge" v-if="feature.badge">{{ feature.badge }}</div>
        </div>
      </div>

      <!-- 统计数据 -->
      <div class="stats-section" v-if="userStats">
        <h2 class="section-title">您的训练统计</h2>
        <div class="stats-grid">
          <div class="stat-item">
            <div class="stat-value">{{ userStats.totalSessions }}</div>
            <div class="stat-label">训练次数</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ userStats.totalMinutes }}</div>
            <div class="stat-label">总时长（分钟）</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ userStats.averageScore }}</div>
            <div class="stat-label">平均得分</div>
          </div>
          <div class="stat-item">
            <div class="stat-value">{{ userStats.streak }}</div>
            <div class="stat-label">连续天数</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

// 功能列表
const features = ref([
  {
    id: 1,
    title: 'AR人体器官3D可视化',
    description: '探索人体内部结构，了解器官功能',
    icon: 'https://api.iconify.design/mdi:heart-pulse.svg?color=%23ffffff',
    route: '/arvr-health/organs',
    bgColor: '#667eea',
    badge: '热门'
  },
  {
    id: 4,
    title: 'VR冥想放松',
    description: '虚拟场景助您放松身心，减轻压力',
    icon: 'https://api.iconify.design/mdi:meditation.svg?color=%23ffffff',
    route: '/arvr-health/meditation',
    bgColor: '#43e97b'
  }
])

// 用户统计数据
const userStats = ref(null)

// 页面挂载时获取用户统计
onMounted(async () => {
  await loadUserStats()
})

// 加载用户统计数据
const loadUserStats = async () => {
  try {
    // 这里应该从后端API获取真实数据
    // const response = await axios.get('/api/arvr-health/user-stats/1')
    // userStats.value = response.data.data
    
    // 模拟数据
    userStats.value = {
      totalSessions: 15,
      totalMinutes: 350,
      averageScore: 85,
      streak: 7
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

// 导航到功能页面
const navigateToFeature = (route) => {
  router.push(route)
}
</script>

<style scoped>
.arvr-health {
  min-height: 100vh;
  background: #667eea;
  padding-bottom: 2rem;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 2rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
}

.title-icon {
  width: 32px;
  height: 32px;
}

.back-home {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1.5rem;
  background: #667eea;
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s;
  
  .icon {
    width: 20px;
    height: 20px;
  }
}

.back-home:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

.content {
  max-width: 1400px;
  margin: 2rem auto;
  padding: 0 2rem;
}

/* 功能卡片 */
.feature-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(350px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.feature-card {
  position: relative;
  padding: 2.5rem;
  border-radius: 20px;
  color: white;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
  overflow: hidden;
}

.feature-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.1);
  opacity: 0;
  transition: opacity 0.3s;
}

.feature-card:hover {
  transform: translateY(-8px) scale(1.02);
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.3);
}

.feature-card:hover::before {
  opacity: 1;
}

.card-icon {
  width: 56px;
  height: 56px;
  margin-bottom: 1rem;
}

.card-title {
  font-size: 1.5rem;
  font-weight: 600;
  margin-bottom: 0.75rem;
}

.card-desc {
  font-size: 1rem;
  opacity: 0.9;
  line-height: 1.5;
}

.card-badge {
  position: absolute;
  top: 20px;
  right: 20px;
  background: rgba(255, 255, 255, 0.9);
  color: #667eea;
  padding: 0.3rem 0.8rem;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 600;
}

/* 统计部分 */
.stats-section {
  background: white;
  border-radius: 20px;
  padding: 2.5rem;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 1.5rem;
  color: #2d3748;
  margin-bottom: 2rem;
  text-align: center;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 2rem;
}

.stat-item {
  text-align: center;
  padding: 1.5rem;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
  border-radius: 15px;
  transition: all 0.3s;
}

.stat-item:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 20px rgba(102, 126, 234, 0.2);
}

.stat-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.stat-label {
  font-size: 1rem;
  color: #718096;
}

/* 响应式 */
@media (max-width: 768px) {
  .header {
    flex-direction: column;
    gap: 1rem;
    padding: 1rem;
  }
  
  .title {
    font-size: 1.3rem;
  }
  
  .content {
    padding: 0 1rem;
  }
  
  .feature-cards {
    grid-template-columns: 1fr;
    gap: 1.5rem;
  }
  
  .feature-card {
    padding: 2rem;
  }
  
  .stats-grid {
    grid-template-columns: repeat(2, 1fr);
    gap: 1rem;
  }
  
  .stat-value {
    font-size: 2rem;
  }
}
</style>

