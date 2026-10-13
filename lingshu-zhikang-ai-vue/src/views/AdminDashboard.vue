<template>
  <div class="admin-dashboard">
    <header class="page-hero">
      <div>
        <p class="eyebrow"><i class="iconify" data-icon="mdi:shield-account-outline"></i> ADMIN CONSOLE</p>
        <h1>用户与会员管理</h1>
        <p class="hero-copy">集中查看用户增长、会员结构与服务使用情况</p>
      </div>
      <div class="hero-actions">
        <span class="system-state"><i></i> 服务运行正常</span>
        <button class="back-home" @click="router.push('/home')">
          <i class="iconify" data-icon="mdi:arrow-left"></i> 返回首页
        </button>
        <button class="hero-refresh" @click="refreshDashboard">
          <i class="iconify" data-icon="mdi:refresh"></i> 刷新数据
        </button>
      </div>
    </header>

    <!-- 顶部统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card blue-card">
        <div class="stat-icon blue">
          <i class="iconify" data-icon="mdi:account-group"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">总用户数</div>
          <div class="stat-value">{{ stats.totalUsers }}</div>
          <div class="stat-note">平台累计注册用户</div>
        </div>
        <span class="stat-index">01</span>
      </div>

      <div class="stat-card green-card">
        <div class="stat-icon green">
          <i class="iconify" data-icon="mdi:account-check"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">活跃用户</div>
          <div class="stat-value">{{ stats.activeUsers }}</div>
          <div class="stat-note">近期保持活跃</div>
        </div>
        <span class="stat-index">02</span>
      </div>

      <div class="stat-card orange-card">
        <div class="stat-icon orange">
          <i class="iconify" data-icon="mdi:account-plus"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">今日新增</div>
          <div class="stat-value">{{ stats.todayNewUsers }}</div>
          <div class="stat-note">今日新注册用户</div>
        </div>
        <span class="stat-index">03</span>
      </div>

      <div class="stat-card purple-card">
        <div class="stat-icon purple">
          <i class="iconify" data-icon="mdi:chart-line"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">付费转化率</div>
          <div class="stat-value">{{ stats.payingRate }}</div>
          <div class="stat-note">付费会员占比</div>
        </div>
        <span class="stat-index">04</span>
      </div>
    </div>

    <!-- 会员等级分布 -->
    <div class="section-card">
      <div class="section-header">
        <div class="section-heading">
          <span class="section-icon"><i class="iconify" data-icon="mdi:crown-outline"></i></span>
          <div><p>MEMBERSHIP OVERVIEW</p><h2>会员等级分布</h2></div>
        </div>
        <span class="section-summary">共 {{ stats.totalUsers }} 位用户</span>
      </div>
      <div class="member-stats">
        <div class="member-item">
          <div class="member-label"><i class="level-dot free"></i><span>免费版<small>FREE</small></span></div>
          <div class="member-bar">
            <div class="member-fill free" :style="{ width: getMemberPercent('freeUsers') }"></div>
          </div>
          <div class="member-count"><strong>{{ stats.freeUsers }}</strong><span>{{ getMemberPercent('freeUsers') }}</span></div>
        </div>
        <div class="member-item">
          <div class="member-label"><i class="level-dot standard"></i><span>标准版<small>STANDARD</small></span></div>
          <div class="member-bar">
            <div class="member-fill standard" :style="{ width: getMemberPercent('standardUsers') }"></div>
          </div>
          <div class="member-count"><strong>{{ stats.standardUsers }}</strong><span>{{ getMemberPercent('standardUsers') }}</span></div>
        </div>
        <div class="member-item">
          <div class="member-label"><i class="level-dot professional"></i><span>专业版<small>PROFESSIONAL</small></span></div>
          <div class="member-bar">
            <div class="member-fill professional" :style="{ width: getMemberPercent('professionalUsers') }"></div>
          </div>
          <div class="member-count"><strong>{{ stats.professionalUsers }}</strong><span>{{ getMemberPercent('professionalUsers') }}</span></div>
        </div>
        <div class="member-item">
          <div class="member-label"><i class="level-dot enterprise"></i><span>企业版<small>ENTERPRISE</small></span></div>
          <div class="member-bar">
            <div class="member-fill enterprise" :style="{ width: getMemberPercent('enterpriseUsers') }"></div>
          </div>
          <div class="member-count"><strong>{{ stats.enterpriseUsers }}</strong><span>{{ getMemberPercent('enterpriseUsers') }}</span></div>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="section-card">
      <div class="section-header users-header">
        <div class="section-heading">
          <span class="section-icon"><i class="iconify" data-icon="mdi:account-multiple-outline"></i></span>
          <div><p>USER DIRECTORY</p><h2>用户管理</h2></div>
        </div>
        <div class="header-actions">
          <label class="search-box"><i class="iconify" data-icon="mdi:magnify"></i><input v-model="searchKeyword" type="text" placeholder="搜索用户名或邮箱" @keyup.enter="searchUsers" /></label>
          <select v-model="filterMemberLevel" @change="loadUsers" class="filter-select">
            <option value="">全部等级</option>
            <option value="FREE">免费版</option>
            <option value="STANDARD">标准版</option>
            <option value="PROFESSIONAL">专业版</option>
            <option value="ENTERPRISE">企业版</option>
          </select>
          <button @click="searchUsers" class="btn-primary">
            <i class="iconify" data-icon="mdi:magnify"></i> 查询
          </button>
          <button v-if="selectedUsers.length > 0" @click="showBatchMemberModal" class="btn-warning">
            <i class="iconify" data-icon="mdi:account-multiple"></i> 批量设置 ({{ selectedUsers.length }})
          </button>
        </div>
      </div>

      <div class="table-container">
        <table class="user-table">
          <thead>
            <tr>
              <th>
                <input 
                  type="checkbox" 
                  @change="toggleSelectAll" 
                  :checked="selectedUsers.length === users.length && users.length > 0"
                />
              </th>
              <th>ID</th>
              <th>用户名</th>
              <th>邮箱</th>
              <th>会员等级</th>
              <th>每日配额</th>
              <th>今日使用</th>
              <th>累计使用</th>
              <th>注册时间</th>
              <th>最后登录</th>
              <th>状态</th>
              <th>操作</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.id">
              <td>
                <input 
                  type="checkbox" 
                  :value="user.id" 
                  v-model="selectedUsers"
                />
              </td>
              <td>{{ user.id }}</td>
              <td>
                <div class="user-info">
                  <i class="iconify" data-icon="mdi:account-circle"></i>
                  {{ user.username }}
                </div>
              </td>
              <td>{{ user.email || '-' }}</td>
              <td>
                <span :class="['badge', 'badge-' + user.memberLevel.toLowerCase()]">
                  {{ getMemberLevelName(user.memberLevel) }}
                </span>
              </td>
              <td>{{ user.dailyQuota }}</td>
              <td>{{ user.dailyUsed }}</td>
              <td>{{ user.totalUsed }}</td>
              <td>{{ formatDate(user.registerTime) }}</td>
              <td>{{ formatDate(user.lastLoginTime) }}</td>
              <td>
                <span :class="['status-badge', 'status-' + user.status.toLowerCase()]">
                  {{ getStatusName(user.status) }}
                </span>
              </td>
              <td>
                <button @click="viewUserDetail(user.id)" class="btn-small btn-info">
                  <i class="iconify" data-icon="mdi:eye"></i> 详情
                </button>
                <button @click="showMemberLevelModal(user)" class="btn-small btn-warning">
                  <i class="iconify" data-icon="mdi:crown"></i> 等级
                </button>
              </td>
            </tr>
            <tr v-if="users.length === 0">
              <td colspan="12" class="empty-row"><i class="iconify" data-icon="mdi:account-search-outline"></i><strong>暂无匹配用户</strong><span>调整搜索词或会员等级后重试</span></td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- 分页 -->
      <div class="pagination">
        <button @click="prevPage" :disabled="currentPage === 1" class="btn-page">
          <i class="iconify" data-icon="mdi:chevron-left"></i> 上一页
        </button>
        <span class="page-info">第 {{ currentPage }} / {{ totalPages }} 页，共 {{ totalUsers }} 条</span>
        <button @click="nextPage" :disabled="currentPage === totalPages" class="btn-page">
          下一页 <i class="iconify" data-icon="mdi:chevron-right"></i>
        </button>
      </div>
    </div>

    <!-- 设置会员等级弹窗 -->
    <div v-if="showMemberModal" class="modal-overlay" @click="closeMemberModal">
      <div class="modal-content member-modal" @click.stop>
        <div class="modal-header">
          <h3>
            <i class="iconify" data-icon="mdi:crown"></i> 
            {{ selectedMemberUser ? '设置会员等级' : `批量设置会员等级 (${selectedUsers.length}个用户)` }}
          </h3>
          <button @click="closeMemberModal" class="btn-close">
            <i class="iconify" data-icon="mdi:close"></i>
          </button>
        </div>
        <div class="modal-body">
          <!-- 单个用户信息 -->
          <div v-if="selectedMemberUser" class="user-info-card">
            <div class="user-avatar">
              <i class="iconify" data-icon="mdi:account-circle"></i>
            </div>
            <div class="user-details">
              <h4>{{ selectedMemberUser.username }}</h4>
              <p>{{ selectedMemberUser.email || '未设置邮箱' }}</p>
              <p>当前等级：<span :class="['badge', 'badge-' + selectedMemberUser.memberLevel.toLowerCase()]">
                {{ getMemberLevelName(selectedMemberUser.memberLevel) }}
              </span></p>
            </div>
          </div>

          <!-- 批量操作提示 -->
          <div v-else class="batch-info-card">
            <div class="batch-icon">
              <i class="iconify" data-icon="mdi:account-multiple"></i>
            </div>
            <div class="batch-details">
              <h4>批量设置会员等级</h4>
              <p>已选择 {{ selectedUsers.length }} 个用户</p>
              <p class="warning-text">注意：此操作将同时更新所有选中用户的会员等级和配额</p>
            </div>
          </div>

          <div class="member-level-options">
            <h4>选择{{ selectedMemberUser ? '新的' : '' }}会员等级：</h4>
            <div class="level-cards">
              <div 
                v-for="level in memberLevels" 
                :key="level.code"
                :class="['level-card', { 'selected': selectedLevel === level.code }]"
                @click="selectedLevel = level.code"
              >
                <div class="level-icon" :class="level.code.toLowerCase()">
                  <i class="iconify" :data-icon="level.icon"></i>
                </div>
                <div class="level-info">
                  <h5>{{ level.name }}</h5>
                  <p>每日配额：{{ level.quota }}次</p>
                  <div class="level-features">
                    <span v-for="feature in level.features" :key="feature" class="feature-tag">
                      {{ feature }}
                    </span>
                  </div>
                </div>
              </div>
            </div>
          </div>

          <div class="modal-actions">
            <button @click="closeMemberModal" class="btn-secondary">取消</button>
            <button 
              @click="selectedMemberUser ? updateMemberLevel() : batchUpdateMemberLevel()" 
              class="btn-primary" 
              :disabled="!selectedLevel || (selectedMemberUser && selectedLevel === selectedMemberUser.memberLevel)"
            >
              <i class="iconify" data-icon="mdi:check"></i> 
              {{ selectedMemberUser ? '确认更新' : `批量更新 (${selectedUsers.length}个)` }}
            </button>
          </div>
        </div>
      </div>
    </div>

    <!-- 用户详情弹窗 -->
    <div v-if="showDetailModal" class="modal-overlay" @click="closeDetailModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3><i class="iconify" data-icon="mdi:account-details"></i> 用户详情</h3>
          <button @click="closeDetailModal" class="btn-close">
            <i class="iconify" data-icon="mdi:close"></i>
          </button>
        </div>
        <div class="modal-body" v-if="selectedUser">
          <div class="detail-grid">
            <div class="detail-item">
              <label>用户ID:</label>
              <span>{{ selectedUser.id }}</span>
            </div>
            <div class="detail-item">
              <label>用户名:</label>
              <span>{{ selectedUser.username }}</span>
            </div>
            <div class="detail-item">
              <label>邮箱:</label>
              <span>{{ selectedUser.email || '-' }}</span>
            </div>
            <div class="detail-item">
              <label>手机:</label>
              <span>{{ selectedUser.phone || '-' }}</span>
            </div>
            <div class="detail-item">
              <label>会员等级:</label>
              <span :class="['badge', 'badge-' + selectedUser.memberLevel.toLowerCase()]">
                {{ getMemberLevelName(selectedUser.memberLevel) }}
              </span>
            </div>
            <div class="detail-item">
              <label>每日配额:</label>
              <span>{{ selectedUser.dailyQuota }}</span>
            </div>
            <div class="detail-item">
              <label>今日使用:</label>
              <span>{{ selectedUser.dailyUsed }}</span>
            </div>
            <div class="detail-item">
              <label>累计使用:</label>
              <span>{{ selectedUser.totalUsed }}</span>
            </div>
            <div class="detail-item">
              <label>注册时间:</label>
              <span>{{ formatDateTime(selectedUser.registerTime) }}</span>
            </div>
            <div class="detail-item">
              <label>最后登录:</label>
              <span>{{ formatDateTime(selectedUser.lastLoginTime) }}</span>
            </div>
            <div class="detail-item">
              <label>账号状态:</label>
              <span :class="['status-badge', 'status-' + selectedUser.status.toLowerCase()]">
                {{ getStatusName(selectedUser.status) }}
              </span>
            </div>
          </div>

          <div class="usage-logs" v-if="usageLogs.length > 0">
            <h4><i class="iconify" data-icon="mdi:chart-timeline"></i> 最近使用记录</h4>
            <table class="log-table">
              <thead>
                <tr>
                  <th>日期</th>
                  <th>功能类型</th>
                  <th>使用次数</th>
                  <th>使用时长</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="log in usageLogs" :key="log.id">
                  <td>{{ log.usageDate }}</td>
                  <td>{{ getFeatureTypeName(log.featureType) }}</td>
                  <td>{{ log.usageCount }}次</td>
                  <td>{{ formatDuration(log.durationSeconds) }}</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useRouter } from 'vue-router'
import axios from 'axios'

const router = useRouter()

const stats = ref({
  totalUsers: 0,
  activeUsers: 0,
  todayNewUsers: 0,
  freeUsers: 0,
  standardUsers: 0,
  professionalUsers: 0,
  enterpriseUsers: 0,
  payingRate: '0%'
})

const users = ref([])
const currentPage = ref(1)
const pageSize = ref(10)
const totalUsers = ref(0)
const searchKeyword = ref('')
const filterMemberLevel = ref('')

const showDetailModal = ref(false)
const selectedUser = ref(null)
const usageLogs = ref([])

const showMemberModal = ref(false)
const selectedMemberUser = ref(null)
const selectedLevel = ref('')

const selectedUsers = ref([])

const memberLevels = ref([
  {
    code: 'FREE',
    name: '免费版',
    quota: 5,
    icon: 'mdi:account',
    features: ['基础AI对话', '症状检查']
  },
  {
    code: 'STANDARD',
    name: '标准版',
    quota: 50,
    icon: 'mdi:star',
    features: ['AI对话', '智能诊断', '健康档案']
  },
  {
    code: 'PROFESSIONAL',
    name: '专业版',
    quota: 200,
    icon: 'mdi:diamond',
    features: ['全部功能', '影像分析', '优先支持']
  },
  {
    code: 'ENTERPRISE',
    name: '企业版',
    quota: 999999,
    icon: 'mdi:crown',
    features: ['无限使用', 'API接口', '定制服务']
  }
])

const totalPages = computed(() => Math.ceil(totalUsers.value / pageSize.value))

const loadStatistics = async () => {
  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const response = await axios.get(`${baseUrl}/api/admin/statistics`)
    if (response.data.success) {
      stats.value = response.data.data
    }
  } catch (error) {
    console.error('加载统计数据失败:', error)
  }
}

const loadUsers = async () => {
  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const response = await axios.get(`${baseUrl}/api/admin/users`, {
      params: {
        page: currentPage.value,
        pageSize: pageSize.value,
        keyword: searchKeyword.value,
        memberLevel: filterMemberLevel.value
      }
    })
    if (response.data.success) {
      users.value = response.data.data
      totalUsers.value = response.data.total
    }
  } catch (error) {
    console.error('加载用户列表失败:', error)
  }
}

const viewUserDetail = async (userId) => {
  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const response = await axios.get(`${baseUrl}/api/admin/users/${userId}`)
    if (response.data.success) {
      selectedUser.value = response.data.data
      usageLogs.value = response.data.usageLogs || []
      showDetailModal.value = true
    }
  } catch (error) {
    console.error('加载用户详情失败:', error)
  }
}

const closeDetailModal = () => {
  showDetailModal.value = false
  selectedUser.value = null
  usageLogs.value = []
}

const showMemberLevelModal = (user) => {
  selectedMemberUser.value = user
  selectedLevel.value = user.memberLevel
  showMemberModal.value = true
}

const closeMemberModal = () => {
  showMemberModal.value = false
  selectedMemberUser.value = null
  selectedLevel.value = ''
}

const updateMemberLevel = async () => {
  if (!selectedMemberUser.value || !selectedLevel.value) return
  
  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const response = await axios.put(
      `${baseUrl}/api/admin/users/${selectedMemberUser.value.id}/member-level`,
      null,
      {
        params: { memberLevel: selectedLevel.value }
      }
    )
    
    if (response.data.success) {
      // 更新本地数据
      const userIndex = users.value.findIndex(u => u.id === selectedMemberUser.value.id)
      if (userIndex !== -1) {
        users.value[userIndex].memberLevel = selectedLevel.value
        users.value[userIndex].dailyQuota = memberLevels.value.find(l => l.code === selectedLevel.value)?.quota || 5
      }
      
      // 重新加载统计数据
      loadStatistics()
      
      alert('用户等级更新成功！')
      closeMemberModal()
    } else {
      alert('更新失败：' + response.data.message)
    }
  } catch (error) {
    console.error('更新用户等级失败:', error)
    alert('更新失败，请稍后重试')
  }
}

const toggleSelectAll = (event) => {
  if (event.target.checked) {
    selectedUsers.value = users.value.map(user => user.id)
  } else {
    selectedUsers.value = []
  }
}

const showBatchMemberModal = () => {
  if (selectedUsers.value.length === 0) return
  selectedLevel.value = ''
  showMemberModal.value = true
}

const batchUpdateMemberLevel = async () => {
  if (!selectedLevel.value || selectedUsers.value.length === 0) return
  
  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const promises = selectedUsers.value.map(userId => 
      axios.put(
        `${baseUrl}/api/admin/users/${userId}/member-level`,
        null,
        { params: { memberLevel: selectedLevel.value } }
      )
    )
    
    const results = await Promise.all(promises)
    const successCount = results.filter(r => r.data.success).length
    
    if (successCount > 0) {
      // 重新加载数据
      loadUsers()
      loadStatistics()
      
      alert(`成功更新 ${successCount} 个用户的等级！`)
      selectedUsers.value = []
      closeMemberModal()
    } else {
      alert('批量更新失败，请稍后重试')
    }
  } catch (error) {
    console.error('批量更新用户等级失败:', error)
    alert('批量更新失败，请稍后重试')
  }
}

const prevPage = () => {
  if (currentPage.value > 1) {
    currentPage.value--
    loadUsers()
  }
}

const nextPage = () => {
  if (currentPage.value < totalPages.value) {
    currentPage.value++
    loadUsers()
  }
}

const searchUsers = () => {
  currentPage.value = 1
  selectedUsers.value = []
  loadUsers()
}

const refreshDashboard = () => {
  selectedUsers.value = []
  Promise.all([loadStatistics(), loadUsers()])
}

const getMemberPercent = (key) => {
  const total = stats.value.totalUsers
  if (total === 0) return '0%'
  return ((stats.value[key] / total) * 100).toFixed(1) + '%'
}

const getMemberLevelName = (level) => {
  const names = {
    'FREE': '免费版',
    'STANDARD': '标准版',
    'PROFESSIONAL': '专业版',
    'ENTERPRISE': '企业版'
  }
  return names[level] || level
}

const getStatusName = (status) => {
  const names = {
    'ACTIVE': '正常',
    'DISABLED': '禁用',
    'DELETED': '已删除'
  }
  return names[status] || status
}

const getFeatureTypeName = (type) => {
  const names = {
    'AI_CHAT': 'AI对话',
    'DIAGNOSIS': '智能诊断',
    'IMAGE_ANALYSIS': '影像分析',
    'SYMPTOM_CHECK': '症状检查'
  }
  return names[type] || type
}

const formatDate = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleDateString('zh-CN')
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return '-'
  return new Date(dateStr).toLocaleString('zh-CN')
}

const formatDuration = (seconds) => {
  if (!seconds) return '-'
  const minutes = Math.floor(seconds / 60)
  const secs = seconds % 60
  return `${minutes}分${secs}秒`
}

onMounted(() => {
  loadStatistics()
  loadUsers()
})
</script>

<style scoped>
.admin-dashboard {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

/* 统计卡片 */
.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.stat-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  display: flex;
  align-items: center;
  gap: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 28px;
  color: white;
}

.stat-icon.blue { background: linear-gradient(135deg, #3b82f6, #60a5fa); }
.stat-icon.green { background: linear-gradient(135deg, #10b981, #34d399); }
.stat-icon.orange { background: linear-gradient(135deg, #f59e0b, #fbbf24); }
.stat-icon.purple { background: linear-gradient(135deg, #8b5cf6, #a78bfa); }

.stat-content {
  flex: 1;
}

.stat-label {
  font-size: 14px;
  color: #6b7280;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #1f2937;
}

/* 区块卡片 */
.section-card {
  background: white;
  border-radius: 12px;
  padding: 28px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  padding-bottom: 16px;
  border-bottom: 2px solid #f3f4f6;
}

.section-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.section-header h2 .iconify {
  font-size: 24px;
  color: #3b82f6;
}

.header-actions {
  display: flex;
  gap: 12px;
}

.search-input {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  width: 200px;
}

.filter-select {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  border-radius: 8px;
  font-size: 14px;
  background: white;
  cursor: pointer;
}

.btn-primary {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 8px 16px;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-primary:hover {
  background: #2563eb;
}

/* 会员统计 */
.member-stats {
  display: grid;
  gap: 16px;
}

.member-item {
  display: grid;
  grid-template-columns: 100px 1fr 80px;
  align-items: center;
  gap: 16px;
}

.member-label {
  font-size: 15px;
  font-weight: 500;
  color: #4b5563;
}

.member-bar {
  height: 32px;
  background: #f3f4f6;
  border-radius: 8px;
  overflow: hidden;
}

.member-fill {
  height: 100%;
  transition: width 1s ease;
}

.member-fill.free { background: linear-gradient(90deg, #9ca3af, #d1d5db); }
.member-fill.standard { background: linear-gradient(90deg, #3b82f6, #60a5fa); }
.member-fill.professional { background: linear-gradient(90deg, #8b5cf6, #a78bfa); }
.member-fill.enterprise { background: linear-gradient(90deg, #f59e0b, #fbbf24); }

.member-count {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  text-align: right;
}

/* 表格 */
.table-container {
  overflow-x: auto;
}

.user-table {
  width: 100%;
  border-collapse: collapse;
}

.user-table thead {
  background: #f9fafb;
}

.user-table th {
  padding: 12px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  border-bottom: 2px solid #e5e7eb;
}

.user-table td {
  padding: 12px;
  font-size: 14px;
  color: #4b5563;
  border-bottom: 1px solid #f3f4f6;
}

.user-info {
  display: flex;
  align-items: center;
  gap: 8px;
}

.user-info .iconify {
  font-size: 20px;
  color: #6b7280;
}

.badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.badge-free {
  background: #f3f4f6;
  color: #6b7280;
}

.badge-standard {
  background: #dbeafe;
  color: #1e40af;
}

.badge-professional {
  background: #ede9fe;
  color: #6b21a8;
}

.badge-enterprise {
  background: #fef3c7;
  color: #92400e;
}

.status-badge {
  display: inline-block;
  padding: 4px 12px;
  border-radius: 12px;
  font-size: 12px;
  font-weight: 600;
}

.status-active {
  background: #d1fae5;
  color: #065f46;
}

.status-disabled {
  background: #fee2e2;
  color: #991b1b;
}

.btn-small {
  padding: 6px 12px;
  border: none;
  border-radius: 6px;
  font-size: 13px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 4px;
  transition: all 0.3s;
}

.btn-info {
  background: #3b82f6;
  color: white;
}

.btn-info:hover {
  background: #2563eb;
}

.btn-warning {
  background: #f59e0b;
  color: white;
}

.btn-warning:hover {
  background: #d97706;
}

.btn-secondary {
  background: #6b7280;
  color: white;
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  font-size: 14px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-secondary:hover {
  background: #4b5563;
}

/* 分页 */
.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 16px;
  margin-top: 24px;
}

.btn-page {
  padding: 8px 16px;
  border: 1px solid #e5e7eb;
  background: white;
  border-radius: 8px;
  font-size: 14px;
  cursor: pointer;
  display: flex;
  align-items: center;
  gap: 6px;
  transition: all 0.3s;
}

.btn-page:hover:not(:disabled) {
  background: #f9fafb;
  border-color: #3b82f6;
}

.btn-page:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  font-size: 14px;
  color: #6b7280;
}

/* 弹窗 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal-content {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 800px;
  max-height: 90vh;
  overflow-y: auto;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px 24px;
  border-bottom: 1px solid #e5e7eb;
}

.modal-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 10px;
}

.btn-close {
  background: none;
  border: none;
  font-size: 24px;
  color: #6b7280;
  cursor: pointer;
  padding: 4px;
  display: flex;
  align-items: center;
}

.btn-close:hover {
  color: #1f2937;
}

.modal-body {
  padding: 24px;
}

.detail-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 16px;
  margin-bottom: 24px;
}

.detail-item {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.detail-item label {
  font-size: 13px;
  color: #6b7280;
  font-weight: 500;
}

.detail-item span {
  font-size: 14px;
  color: #1f2937;
}

.usage-logs {
  margin-top: 24px;
  padding-top: 24px;
  border-top: 1px solid #e5e7eb;
}

.usage-logs h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
  display: flex;
  align-items: center;
  gap: 8px;
}

.log-table {
  width: 100%;
  border-collapse: collapse;
}

.log-table thead {
  background: #f9fafb;
}

.log-table th {
  padding: 10px;
  text-align: left;
  font-size: 13px;
  font-weight: 600;
  color: #6b7280;
  border-bottom: 2px solid #e5e7eb;
}

.log-table td {
  padding: 10px;
  font-size: 14px;
  color: #4b5563;
  border-bottom: 1px solid #f3f4f6;
}

/* 会员等级设置弹窗 */
.member-modal {
  max-width: 800px;
  width: 95%;
}

.user-info-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #f9fafb;
  border-radius: 12px;
  margin-bottom: 24px;
}

.user-avatar {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #3b82f6, #60a5fa);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
}

.user-details h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
}

.user-details p {
  margin: 4px 0;
  font-size: 14px;
  color: #6b7280;
}

.batch-info-card {
  display: flex;
  align-items: center;
  gap: 16px;
  padding: 20px;
  background: #fef3c7;
  border: 1px solid #f59e0b;
  border-radius: 12px;
  margin-bottom: 24px;
}

.batch-icon {
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background: linear-gradient(135deg, #f59e0b, #fbbf24);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 32px;
  color: white;
}

.batch-details h4 {
  margin: 0 0 8px 0;
  font-size: 18px;
  font-weight: 600;
  color: #92400e;
}

.batch-details p {
  margin: 4px 0;
  font-size: 14px;
  color: #92400e;
}

.warning-text {
  font-weight: 500;
  color: #d97706 !important;
}

.member-level-options h4 {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 16px 0;
}

.level-cards {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(180px, 1fr));
  gap: 16px;
  margin-bottom: 24px;
}

.level-card {
  border: 2px solid #e5e7eb;
  border-radius: 12px;
  padding: 16px;
  cursor: pointer;
  transition: all 0.3s;
  background: white;
}

.level-card:hover {
  border-color: #3b82f6;
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.15);
}

.level-card.selected {
  border-color: #3b82f6;
  background: #eff6ff;
}

.level-icon {
  width: 48px;
  height: 48px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 24px;
  color: white;
  margin-bottom: 12px;
}

.level-icon.free { background: linear-gradient(135deg, #9ca3af, #d1d5db); }
.level-icon.standard { background: linear-gradient(135deg, #3b82f6, #60a5fa); }
.level-icon.professional { background: linear-gradient(135deg, #8b5cf6, #a78bfa); }
.level-icon.enterprise { background: linear-gradient(135deg, #f59e0b, #fbbf24); }

.level-info h5 {
  margin: 0 0 8px 0;
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.level-info p {
  margin: 0 0 12px 0;
  font-size: 14px;
  color: #6b7280;
}

.level-features {
  display: flex;
  flex-wrap: wrap;
  gap: 6px;
}

.feature-tag {
  background: #f3f4f6;
  color: #4b5563;
  padding: 4px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 500;
}

.modal-actions {
  display: flex;
  justify-content: flex-end;
  gap: 12px;
  padding-top: 16px;
  border-top: 1px solid #e5e7eb;
}

.btn-primary:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

/* 响应式 */
@media (max-width: 768px) {
  .admin-dashboard {
    padding: 16px;
  }

  .stats-grid {
    grid-template-columns: 1fr;
  }

  .header-actions {
    flex-direction: column;
    width: 100%;
  }

  .search-input {
    width: 100%;
  }

  .detail-grid {
    grid-template-columns: 1fr;
  }

  .level-cards {
    grid-template-columns: 1fr;
  }

  .member-modal {
    width: 95%;
    margin: 20px;
  }
}

/* 2026 admin console redesign */
.admin-dashboard {
  --primary: #0ea5e9;
  --primary-dark: #0284c7;
  --ink: #172033;
  --muted: #748095;
  --line: #dceaf2;
  min-height: 100vh;
  padding: 28px clamp(18px, 3vw, 48px) 48px;
  color: var(--ink);
  background:
    radial-gradient(circle at 8% 0, rgba(56, 189, 248, .16), transparent 26%),
    radial-gradient(circle at 92% 14%, rgba(45, 212, 191, .1), transparent 25%),
    linear-gradient(180deg, #f6fcff 0, #edf8fd 44%, #f7fbfd 100%);
}

.page-hero {
  position: relative;
  display: flex;
  align-items: flex-end;
  justify-content: space-between;
  gap: 24px;
  max-width: 1800px;
  margin: 0 auto 24px;
  padding: 8px 2px 18px;
}
.page-hero::after { content: ''; position: absolute; right: 0; bottom: 0; left: 0; height: 1px; background: linear-gradient(90deg, var(--primary), rgba(14,165,233,.05)); }
.eyebrow { display: flex; align-items: center; gap: 7px; margin: 0 0 8px; color: var(--primary-dark); font-size: 11px; font-weight: 800; letter-spacing: .16em; }
.eyebrow .iconify { font-size: 16px; }
.page-hero h1 { margin: 0; color: #14233a; font-size: clamp(28px, 2.2vw, 38px); line-height: 1.15; letter-spacing: -.035em; }
.hero-copy { margin: 9px 0 0; color: var(--muted); font-size: 14px; }
.hero-actions { display: flex; align-items: center; gap: 14px; padding-bottom: 3px; }
.system-state { display: inline-flex; align-items: center; gap: 8px; color: #527081; font-size: 13px; white-space: nowrap; }
.system-state i { width: 8px; height: 8px; border-radius: 50%; background: #22c58b; box-shadow: 0 0 0 5px rgba(34,197,139,.1); }
.hero-refresh, .back-home { display: inline-flex; align-items: center; gap: 7px; height: 42px; padding: 0 17px; border: 1px solid #c8e7f4; border-radius: 10px; color: #087eae; background: rgba(255,255,255,.88); box-shadow: 0 6px 18px rgba(45,126,164,.07); cursor: pointer; }
.back-home { color: #526d80; }
.hero-refresh:hover, .back-home:hover { border-color: #7dd3fc; background: #fff; transform: translateY(-1px); }

.stats-grid { max-width: 1800px; margin: 0 auto 20px; grid-template-columns: repeat(4, minmax(0, 1fr)); gap: 14px; }
.stat-card { position: relative; min-height: 132px; padding: 22px; gap: 17px; overflow: hidden; border: 1px solid rgba(174,216,234,.7); border-radius: 16px; box-shadow: 0 10px 30px rgba(40,112,147,.075); }
.stat-card::after { content: ''; position: absolute; right: -34px; bottom: -62px; width: 140px; height: 140px; border: 22px solid rgba(56,189,248,.055); border-radius: 50%; }
.stat-card:hover { transform: translateY(-3px); box-shadow: 0 16px 34px rgba(40,112,147,.12); }
.stat-icon { z-index: 1; width: 58px; height: 58px; border-radius: 17px; font-size: 27px; box-shadow: inset 0 1px rgba(255,255,255,.35), 0 8px 18px rgba(30,110,160,.16); }
.stat-content { z-index: 1; }
.stat-label { margin: 0 0 4px; color: #607186; font-size: 13px; }
.stat-value { color: #14233a; font-size: 31px; line-height: 1.15; letter-spacing: -.02em; }
.stat-note { margin-top: 6px; color: #9aa7b5; font-size: 11px; }
.stat-index { position: absolute; top: 15px; right: 17px; color: #c8dbe4; font: 700 11px Consolas, monospace; letter-spacing: .1em; }
.stat-icon.blue { background: linear-gradient(135deg,#38bdf8,#3b82f6); }
.stat-icon.green { background: linear-gradient(135deg,#2dd4bf,#10b981); }
.stat-icon.orange { background: linear-gradient(135deg,#fbbf24,#f59e0b); }
.stat-icon.purple { background: linear-gradient(135deg,#a78bfa,#7c3aed); }

.section-card { max-width: 1800px; margin: 0 auto 20px; padding: 24px; border: 1px solid rgba(174,216,234,.68); border-radius: 16px; box-shadow: 0 10px 30px rgba(40,112,147,.065); }
.section-header { min-height: 52px; margin-bottom: 20px; padding-bottom: 18px; border-bottom: 1px solid #e7f0f4; }
.section-heading { display: flex; align-items: center; gap: 12px; }
.section-icon { display: grid; width: 40px; height: 40px; place-items: center; border: 1px solid #cdebf7; border-radius: 11px; color: var(--primary-dark); background: #eefaff; font-size: 21px; }
.section-heading p { margin: 0 0 3px; color: #54a2c2; font-size: 9px; font-weight: 800; letter-spacing: .14em; }
.section-heading h2 { margin: 0; color: #17283d; font-size: 19px; }
.section-summary { padding: 6px 11px; border-radius: 20px; color: #648091; background: #f0f7fa; font-size: 12px; }

.member-stats { grid-template-columns: repeat(4, minmax(0,1fr)); gap: 13px; }
.member-item { display: grid; grid-template-columns: 1fr auto; gap: 14px; padding: 17px; border: 1px solid #e4eef3; border-radius: 13px; background: linear-gradient(145deg,#fff,#f9fcfe); }
.member-label { display: flex; align-items: center; gap: 10px; color: #33475b; font-size: 14px; font-weight: 700; }
.member-label span { display: flex; flex-direction: column; gap: 3px; }
.member-label small { color: #a0adba; font-size: 8px; font-weight: 700; letter-spacing: .12em; }
.level-dot { width: 11px; height: 11px; border-radius: 4px; box-shadow: 0 0 0 5px rgba(148,163,184,.11); }
.level-dot.free { background: #94a3b8; }
.level-dot.standard { background: #3b82f6; box-shadow: 0 0 0 5px rgba(59,130,246,.1); }
.level-dot.professional { background: #8b5cf6; box-shadow: 0 0 0 5px rgba(139,92,246,.1); }
.level-dot.enterprise { background: #f59e0b; box-shadow: 0 0 0 5px rgba(245,158,11,.1); }
.member-bar { grid-column: 1 / 3; grid-row: 2; height: 6px; border-radius: 99px; background: #edf2f5; }
.member-fill { border-radius: inherit; }
.member-count { display: flex; align-items: baseline; gap: 6px; text-align: right; }
.member-count strong { color: #18283c; font-size: 23px; }
.member-count span { color: #93a2af; font-size: 10px; }

.users-header { align-items: center; }
.header-actions { flex-wrap: wrap; justify-content: flex-end; }
.search-box { display: flex; align-items: center; width: 240px; height: 42px; padding: 0 12px; border: 1px solid #d7e7ef; border-radius: 10px; color: #91a4b3; background: #fbfdfe; transition: .2s; }
.search-box:focus-within { border-color: #7dd3fc; box-shadow: 0 0 0 3px rgba(56,189,248,.1); }
.search-box input { width: 100%; min-width: 0; padding: 0 0 0 8px; border: 0; outline: 0; color: #2a3c50; background: transparent; font-size: 13px; }
.filter-select, .btn-primary, .btn-warning { min-height: 42px; border-radius: 10px; }
.filter-select { padding: 0 34px 0 13px; border-color: #d7e7ef; color: #42576a; }
.btn-primary { padding: 0 17px; background: linear-gradient(135deg,#38bdf8,#0ea5e9); box-shadow: 0 6px 15px rgba(14,165,233,.2); }
.btn-warning { border: 0; padding: 0 14px; }

.table-container { border: 1px solid #e0ebf1; border-radius: 12px; background: #fff; }
.user-table { min-width: 1180px; }
.user-table thead { background: #f3f9fc; }
.user-table th { height: 50px; padding: 0 13px; border-bottom: 1px solid #dce9ef; color: #688093; font-size: 12px; white-space: nowrap; }
.user-table td { height: 58px; padding: 0 13px; border-bottom-color: #edf3f6; color: #42556a; font-size: 13px; white-space: nowrap; }
.user-table tbody tr { transition: background .2s; }
.user-table tbody tr:hover { background: #f8fcfe; }
.user-table tbody tr:last-child td { border-bottom: 0; }
.user-info { color: #22354a; font-weight: 600; }
.user-info .iconify { color: #8ba2b5; font-size: 23px; }
.badge, .status-badge { padding: 5px 10px; border-radius: 7px; }
.btn-small { padding: 7px 10px; border-radius: 7px; }
.empty-row { height: 210px !important; text-align: center; color: #9aabb8 !important; }
.empty-row .iconify, .empty-row strong, .empty-row span { display: block; margin: auto; }
.empty-row .iconify { margin-bottom: 8px; font-size: 38px; }
.empty-row strong { color: #667d8d; font-size: 14px; }
.empty-row span { margin-top: 5px; font-size: 11px; }
.pagination { justify-content: flex-end; margin-top: 18px; }
.btn-page { border-color: #d9e7ee; border-radius: 9px; color: #547083; }

.modal-overlay { padding: 20px; background: rgba(14,31,50,.48); backdrop-filter: blur(5px); }
.modal-content { border: 1px solid rgba(255,255,255,.6); border-radius: 18px; box-shadow: 0 24px 70px rgba(17,48,70,.24); }
.modal-header { background: linear-gradient(180deg,#fff,#f8fcfe); }

@media (max-width: 1200px) {
  .stats-grid { grid-template-columns: repeat(2, minmax(0,1fr)); }
  .member-stats { grid-template-columns: repeat(2, minmax(0,1fr)); }
  .users-header { align-items: flex-start; flex-direction: column; }
  .header-actions { width: 100%; justify-content: flex-start; }
}

@media (max-width: 700px) {
  .admin-dashboard { padding: 18px 12px 36px; }
  .page-hero { align-items: flex-start; flex-direction: column; }
  .hero-actions { width: 100%; justify-content: space-between; }
  .stats-grid, .member-stats { grid-template-columns: 1fr; }
  .stat-card { min-height: 116px; }
  .section-card { padding: 17px; }
  .section-header { align-items: flex-start; }
  .header-actions { flex-direction: row; }
  .search-box { width: 100%; }
  .filter-select { flex: 1; }
  .pagination { justify-content: center; gap: 8px; }
  .page-info { font-size: 11px; }
}
</style>
