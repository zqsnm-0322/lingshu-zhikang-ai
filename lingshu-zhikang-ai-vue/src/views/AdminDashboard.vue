<template>
  <div class="admin-dashboard">
    <!-- 顶部统计卡片 -->
    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon blue">
          <i class="iconify" data-icon="mdi:account-group"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">总用户数</div>
          <div class="stat-value">{{ stats.totalUsers }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon green">
          <i class="iconify" data-icon="mdi:account-check"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">活跃用户</div>
          <div class="stat-value">{{ stats.activeUsers }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon orange">
          <i class="iconify" data-icon="mdi:account-plus"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">今日新增</div>
          <div class="stat-value">{{ stats.todayNewUsers }}</div>
        </div>
      </div>

      <div class="stat-card">
        <div class="stat-icon purple">
          <i class="iconify" data-icon="mdi:chart-line"></i>
        </div>
        <div class="stat-content">
          <div class="stat-label">付费转化率</div>
          <div class="stat-value">{{ stats.payingRate }}</div>
        </div>
      </div>
    </div>

    <!-- 会员等级分布 -->
    <div class="section-card">
      <div class="section-header">
        <h2><i class="iconify" data-icon="mdi:crown"></i> 会员等级分布</h2>
      </div>
      <div class="member-stats">
        <div class="member-item">
          <div class="member-label">免费版</div>
          <div class="member-bar">
            <div class="member-fill free" :style="{ width: getMemberPercent('freeUsers') }"></div>
          </div>
          <div class="member-count">{{ stats.freeUsers }}人</div>
        </div>
        <div class="member-item">
          <div class="member-label">标准版</div>
          <div class="member-bar">
            <div class="member-fill standard" :style="{ width: getMemberPercent('standardUsers') }"></div>
          </div>
          <div class="member-count">{{ stats.standardUsers }}人</div>
        </div>
        <div class="member-item">
          <div class="member-label">专业版</div>
          <div class="member-bar">
            <div class="member-fill professional" :style="{ width: getMemberPercent('professionalUsers') }"></div>
          </div>
          <div class="member-count">{{ stats.professionalUsers }}人</div>
        </div>
        <div class="member-item">
          <div class="member-label">企业版</div>
          <div class="member-bar">
            <div class="member-fill enterprise" :style="{ width: getMemberPercent('enterpriseUsers') }"></div>
          </div>
          <div class="member-count">{{ stats.enterpriseUsers }}人</div>
        </div>
      </div>
    </div>

    <!-- 用户列表 -->
    <div class="section-card">
      <div class="section-header">
        <h2><i class="iconify" data-icon="mdi:account-multiple"></i> 用户管理</h2>
        <div class="header-actions">
          <input 
            v-model="searchKeyword" 
            type="text" 
            placeholder="搜索用户名或邮箱" 
            class="search-input"
            @keyup.enter="loadUsers"
          />
          <select v-model="filterMemberLevel" @change="loadUsers" class="filter-select">
            <option value="">全部等级</option>
            <option value="FREE">免费版</option>
            <option value="STANDARD">标准版</option>
            <option value="PROFESSIONAL">专业版</option>
            <option value="ENTERPRISE">企业版</option>
          </select>
          <button @click="loadUsers" class="btn-primary">
            <i class="iconify" data-icon="mdi:refresh"></i> 刷新
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
import axios from 'axios'

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
</style>
