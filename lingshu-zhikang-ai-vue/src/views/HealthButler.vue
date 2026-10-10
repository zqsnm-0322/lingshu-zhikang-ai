<template>
  <div class="health-butler">
    <!-- 头部 -->
    <div class="butler-header">
      <div class="header-content">
        <img src="https://api.iconify.design/mdi:robot.svg?color=%23ffffff&width=48&height=48" class="header-icon" alt="AI机器人" />
        <div class="header-text">
          <h1 class="title">AI健康管家</h1>
          <p class="subtitle">您的24/7贴心健康顾问</p>
        </div>
      </div>
    </div>

    <!-- Tab切换 -->
    <div class="butler-tabs">
      <button 
        v-for="tab in tabs" 
        :key="tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
        @click="activeTab = tab.key">
        <img :src="tab.icon" class="tab-icon" :alt="tab.label" />
        {{ tab.label }}
      </button>
    </div>

    <!-- 每日晨报 -->
    <div v-if="activeTab === 'report'" class="tab-content">
      <div v-if="dailyReport" class="daily-report">
        <div class="report-header">
          <div class="header-left">
            <img src="https://api.iconify.design/mdi:calendar-today.svg?color=%23333333&width=24&height=24" class="section-icon" alt="日历" />
            <h2>今日健康晨报</h2>
          </div>
          <span class="report-date">{{ dailyReport.reportDate || '2025-12-17' }}</span>
        </div>
        
        <div class="health-score-card">
          <div class="score-circle">
            <div class="score-value">{{ dailyReport.healthScore || 82 }}</div>
            <div class="score-label">健康评分</div>
          </div>
          <div class="health-level good">
            {{ dailyReport.healthLevel || '良好' }}
          </div>
        </div>

        <div class="report-sections">
          <div class="report-section">
            <div class="section-header">
              <img src="https://api.iconify.design/mdi:sleep.svg?color=%236366f1&width=24&height=24" class="section-icon" alt="睡眠" />
              <h3>睡眠质量</h3>
            </div>
            <div class="section-content">
              <span>质量评分: {{ dailyReport.sleepQuality || '7.5' }}/10</span>
              <span>睡眠时长: {{ dailyReport.sleepDuration || '7.4' }}小时</span>
            </div>
          </div>

          <div class="report-section">
            <div class="section-header">
              <img src="https://api.iconify.design/mdi:pill.svg?color=%23ef4444&width=24&height=24" class="section-icon" alt="用药" />
              <h3>今日用药</h3>
            </div>
            <div class="section-content">
              <span>需服用药物: {{ dailyReport.medicationCount || '0' }}项</span>
            </div>
          </div>

          <div class="report-section">
            <div class="section-header">
              <img src="https://api.iconify.design/mdi:food-apple.svg?color=%2322c55e&width=24&height=24" class="section-icon" alt="饮食" />
              <h3>饮食建议</h3>
            </div>
            <p class="suggestion-text">{{ dailyReport.dietSuggestions || '建议多摄入富含维生素的蔬菜水果，适量补充蛋白质，控制油脂摄入。' }}</p>
          </div>

          <div class="report-section">
            <div class="section-header">
              <img src="https://api.iconify.design/mdi:run.svg?color=%23f59e0b&width=24&height=24" class="section-icon" alt="运动" />
              <h3>运动建议</h3>
            </div>
            <p class="suggestion-text">{{ dailyReport.exerciseSuggestions || '建议进行30分钟中等强度有氧运动，如快走或游泳，配合适量力量训练。' }}</p>
          </div>

          <div class="report-section ai-suggestions">
            <div class="section-header">
              <img src="https://api.iconify.design/mdi:brain.svg?color=%238b5cf6&width=24&height=24" class="section-icon" alt="AI建议" />
              <h3>AI个性化建议</h3>
            </div>
            <p class="suggestion-text">{{ dailyReport.aiSuggestions || '根据您的健康数据分析，建议保持规律作息，适当增加户外活动时间，注意心理健康调节。' }}</p>
          </div>
        </div>

        <button class="refresh-btn" @click="generateReport">
          <img src="https://api.iconify.design/mdi:refresh.svg?color=%23ffffff&width=20&height=20" class="btn-icon" alt="刷新" />
          刷新晨报
        </button>
      </div>

      <div v-else class="no-data">
        <img src="https://api.iconify.design/mdi:file-document-outline.svg?color=%239ca3af&width=64&height=64" class="empty-icon" alt="暂无数据" />
        <p>暂无今日晨报</p>
        <button class="generate-btn" @click="generateReport">
          <img src="https://api.iconify.design/mdi:plus.svg?color=%23ffffff&width=20&height=20" class="btn-icon" alt="生成" />
          生成晨报
        </button>
      </div>
    </div>


    <!-- 健康计划 -->
    <div v-if="activeTab === 'plan'" class="tab-content">
      <div class="plans-header">
        <div class="header-left">
          <img src="https://api.iconify.design/mdi:clipboard-list.svg?color=%23333333&width=24&height=24" class="section-icon" alt="计划" />
          <h2>我的健康计划</h2>
        </div>
        <button @click="showPlanDialog = true" class="create-btn">
          <img src="https://api.iconify.design/mdi:plus.svg?color=%23ffffff&width=16&height=16" class="btn-icon" alt="创建" />
          创建计划
        </button>
      </div>

      <div v-if="healthPlans.length > 0" class="plans-list">
        <div v-for="plan in healthPlans" :key="plan.id" class="plan-card">
          <div class="plan-header">
            <h3>{{ plan.planName }}</h3>
            <div class="plan-actions">
              <span :class="['plan-status', plan.status]">{{ plan.status }}</span>
              <button @click="deletePlan(plan.id)" class="delete-btn" title="删除计划">
                <img src="https://api.iconify.design/mdi:delete.svg?color=%23ef4444&width=16&height=16" class="btn-icon" alt="删除" />
              </button>
            </div>
          </div>
          <div class="plan-info">
            <div class="info-item">
              <img src="https://api.iconify.design/mdi:tag.svg?color=%236b7280&width=16&height=16" class="info-icon" alt="类型" />
              <span>{{ plan.planType }}</span>
            </div>
            <div class="info-item">
              <img src="https://api.iconify.design/mdi:calendar-range.svg?color=%236b7280&width=16&height=16" class="info-icon" alt="日期" />
              <span>{{ plan.startDate }} ~ {{ plan.endDate }}</span>
            </div>
          </div>
          <div class="plan-progress">
            <div class="progress-bar">
              <div class="progress-fill" :style="{ width: plan.progress + '%' }"></div>
            </div>
            <span class="progress-text">{{ plan.progress }}%</span>
          </div>
          <p class="plan-goal">{{ plan.targetGoal }}</p>
        </div>
      </div>

      <div v-else class="no-plans">
        <img src="https://api.iconify.design/mdi:clipboard-outline.svg?color=%239ca3af&width=64&height=64" class="empty-icon" alt="暂无计划" />
        <p>暂无健康计划</p>
        <button @click="showPlanDialog = true" class="create-btn">
          <img src="https://api.iconify.design/mdi:plus.svg?color=%23ffffff&width=16&height=16" class="btn-icon" alt="创建" />
          创建第一个计划
        </button>
      </div>
    </div>

    <!-- 创建计划对话框 -->
    <div v-if="showPlanDialog" class="dialog-overlay" @click="showPlanDialog = false">
      <div class="dialog-content" @click.stop>
        <h3>创建健康计划</h3>
        <form @submit.prevent="createPlan">
          <div class="form-group">
            <label>计划名称</label>
            <input v-model="newPlan.planName" required />
          </div>
          <div class="form-group">
            <label>计划类型</label>
            <select v-model="newPlan.planType" required>
              <option value="饮食">饮食</option>
              <option value="运动">运动</option>
              <option value="睡眠">睡眠</option>
              <option value="用药">用药</option>
              <option value="综合">综合</option>
            </select>
          </div>
          <div class="form-group">
            <label>开始日期</label>
            <input type="date" v-model="newPlan.startDate" required />
          </div>
          <div class="form-group">
            <label>结束日期</label>
            <input type="date" v-model="newPlan.endDate" required />
          </div>
          <div class="form-group">
            <label>目标描述</label>
            <textarea v-model="newPlan.targetGoal" required></textarea>
          </div>
          <div class="dialog-actions">
            <button type="button" @click="showPlanDialog = false">取消</button>
            <button type="submit">创建</button>
          </div>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'HealthButler',
  data() {
    return {
      activeTab: 'report',
      tabs: [
        { 
          key: 'report', 
          label: '每日晨报',
          icon: 'https://api.iconify.design/mdi:newspaper.svg?color=%23ffffff&width=20&height=20'
        },
        { 
          key: 'plan', 
          label: '健康计划',
          icon: 'https://api.iconify.design/mdi:clipboard-list.svg?color=%23ffffff&width=20&height=20'
        }
      ],
      dailyReport: null,
      healthPlans: [],
      showPlanDialog: false,
      newPlan: {
        planName: '',
        planType: '',
        startDate: '',
        endDate: '',
        targetGoal: ''
      },
      userId: 1 // 从登录状态获取
    }
  },
  mounted() {
    this.loadLatestReport()
    this.loadHealthPlans()
  },
  methods: {
    async loadLatestReport() {
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
        const response = await axios.get(`${baseUrl}/api/health-butler/report/latest`, {
          params: { userId: this.userId }
        })
        if (response.data.code === 200) {
          this.dailyReport = response.data.data
        }
      } catch (error) {
        console.error('加载晨报失败:', error)
      }
    },
    
    async generateReport() {
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
        const response = await axios.post(`${baseUrl}/api/health-butler/report/generate`, null, {
          params: { userId: this.userId }
        })
        if (response.data.code === 200) {
          this.dailyReport = response.data.data
          alert('晨报生成成功！')
        }
      } catch (error) {
        console.error('生成晨报失败:', error)
        alert('生成晨报失败，请稍后重试')
      }
    },


    async loadHealthPlans() {
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
        const response = await axios.get(`${baseUrl}/api/health-butler/plan/list`, {
          params: { userId: this.userId }
        })
        if (response.data.code === 200) {
          this.healthPlans = response.data.data || []
        }
      } catch (error) {
        console.error('加载健康计划失败:', error)
      }
    },

    async createPlan() {
      try {
        const planData = {
          ...this.newPlan,
          userId: this.userId
        }

        const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
        const response = await axios.post(`${baseUrl}/api/health-butler/plan`, planData)
        if (response.data.code === 200) {
          alert('计划创建成功！')
          this.showPlanDialog = false
          this.newPlan = {
            planName: '',
            planType: '',
            startDate: '',
            endDate: '',
            targetGoal: ''
          }
          this.loadHealthPlans()
        }
      } catch (error) {
        console.error('创建计划失败:', error)
        alert('创建计划失败，请稍后重试')
      }
    },

    async deletePlan(planId) {
      if (!confirm('确定要删除这个健康计划吗？')) {
        return
      }
      
      try {
        const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
        const response = await axios.delete(`${baseUrl}/api/health-butler/plan/${planId}`)
        if (response.data.code === 200) {
          alert('计划删除成功！')
          this.loadHealthPlans()
        } else {
          alert('删除失败：' + response.data.msg)
        }
      } catch (error) {
        console.error('删除计划失败:', error)
        alert('删除计划失败，请稍后重试')
      }
    },

    getLevelClass(level) {
      const levelMap = {
        '优秀': 'excellent',
        '良好': 'good',
        '一般': 'normal',
        '较差': 'poor'
      }
      return levelMap[level] || 'normal'
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return `${date.getHours()}:${String(date.getMinutes()).padStart(2, '0')}`
    }
  }
}
</script>

<style scoped>
.health-butler {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  width: 100vw;
  height: 100vh;
  min-height: 100vh;
  background: #ff7f7f; /* 浅红色背景 */
  padding: 20px;
  margin: 0;
  box-sizing: border-box;
  overflow-y: auto;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, sans-serif;
}

/* 头部样式 */
.butler-header {
  text-align: center;
  margin-bottom: 40px;
  padding: 30px 0;
}

.header-content {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 20px;
  max-width: 1200px;
  margin: 0 auto;
}

.header-icon {
  width: 48px;
  height: 48px;
}

.header-text {
  text-align: left;
}

.title {
  font-size: 2.5rem;
  color: #ffffff;
  margin: 0 0 8px 0;
  font-weight: 700;
}

.subtitle {
  color: #ffffff;
  font-size: 1.1rem;
  opacity: 0.9;
  margin: 0;
}

/* Tab切换样式 */
.butler-tabs {
  display: flex;
  justify-content: center;
  gap: 8px;
  margin-bottom: 40px;
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.tab-btn {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  cursor: pointer;
  font-size: 16px;
  font-weight: 500;
  border-radius: 25px;
  transition: all 0.3s ease;
}

.tab-btn:hover {
  background: rgba(255, 255, 255, 0.3);
}

.tab-btn.active {
  background: #ffffff;
  color: #d84315;
}

.tab-icon {
  width: 20px;
  height: 20px;
}

/* 内容区域 */
.tab-content {
  max-width: 1200px;
  margin: 0 auto;
  animation: fadeIn 0.3s ease;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(20px); }
  to { opacity: 1; transform: translateY(0); }
}

/* 每日报告样式 */
.daily-report {
  background: #ffffff;
  border-radius: 16px;
  padding: 32px;
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
}

.report-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 32px;
}

.header-left {
  display: flex;
  align-items: center;
  gap: 12px;
}

.section-icon {
  width: 24px;
  height: 24px;
}

.report-header h2 {
  font-size: 1.5rem;
  color: #333333;
  margin: 0;
  font-weight: 600;
}

.report-date {
  color: #6b7280;
  font-size: 0.9rem;
}

/* 健康评分卡片 */
.health-score-card {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 40px;
  padding: 40px;
  background: #6366f1; /* 纯色紫色背景 */
  border-radius: 16px;
  color: white;
  margin-bottom: 32px;
}

.score-circle {
  text-align: center;
}

.score-value {
  font-size: 3.5rem;
  font-weight: 700;
  margin-bottom: 8px;
}

.score-label {
  font-size: 1rem;
  opacity: 0.9;
}

.health-level {
  font-size: 1.5rem;
  font-weight: 600;
  padding: 12px 24px;
  border-radius: 25px;
  background: rgba(255, 255, 255, 0.2);
}

.health-level.good {
  background: #22c55e; /* 纯色绿色 */
}

/* 报告区块 */
.report-sections {
  display: grid;
  gap: 20px;
}

.report-section {
  padding: 24px;
  background: #f8fafc;
  border-radius: 12px;
  border-left: 4px solid #e5e7eb;
}

.section-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.section-header h3 {
  font-size: 1.1rem;
  color: #374151;
  margin: 0;
  font-weight: 600;
}

.section-content {
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}

.section-content span {
  color: #6b7280;
  font-size: 0.95rem;
}

.suggestion-text {
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
}

.ai-suggestions {
  background: #f3f4f6;
  border-left-color: #8b5cf6;
}

/* 按钮样式 */
.refresh-btn, .generate-btn, .create-btn {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  padding: 12px 24px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.refresh-btn {
  width: 100%;
  margin-top: 24px;
  background: #3b82f6;
  color: white;
}

.refresh-btn:hover {
  background: #2563eb;
}

.generate-btn {
  background: #3b82f6;
  color: white;
  margin-top: 20px;
}

.generate-btn:hover {
  background: #2563eb;
}

.create-btn {
  background: #10b981;
  color: white;
}

.create-btn:hover {
  background: #059669;
}

.btn-icon {
  width: 16px;
  height: 16px;
}

/* 计划相关样式 */
.plans-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 24px;
  background: white;
  padding: 24px;
  border-radius: 16px;
}

.plans-header h2 {
  font-size: 1.5rem;
  color: #333333;
  margin: 0;
  font-weight: 600;
}

.plans-list {
  display: grid;
  gap: 20px;
}

.plan-card {
  background: white;
  padding: 24px;
  border-radius: 16px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

.plan-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.plan-actions {
  display: flex;
  align-items: center;
  gap: 12px;
}

.delete-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 4px;
  border-radius: 4px;
  transition: all 0.3s ease;
  display: flex;
  align-items: center;
  justify-content: center;
}

.delete-btn:hover {
  background: #fee2e2;
}

.plan-header h3 {
  font-size: 1.2rem;
  color: #374151;
  margin: 0;
  font-weight: 600;
}

.plan-status {
  padding: 6px 12px;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 500;
  color: white;
}

.plan-status.进行中 {
  background: #3b82f6;
}

.plan-status.已完成 {
  background: #10b981;
}

.plan-info {
  display: flex;
  gap: 24px;
  margin-bottom: 16px;
}

.info-item {
  display: flex;
  align-items: center;
  gap: 6px;
  color: #6b7280;
  font-size: 0.9rem;
}

.info-icon {
  width: 16px;
  height: 16px;
}

.plan-progress {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.progress-bar {
  flex: 1;
  height: 8px;
  background: #e5e7eb;
  border-radius: 4px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  background: #10b981;
  transition: width 0.3s ease;
}

.progress-text {
  font-size: 0.9rem;
  color: #6b7280;
  font-weight: 500;
}

.plan-goal {
  color: #4b5563;
  line-height: 1.6;
  margin: 0;
}

/* 空状态 */
.no-data, .no-plans {
  text-align: center;
  padding: 60px 40px;
  background: white;
  border-radius: 16px;
}

.empty-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
}

.no-data p, .no-plans p {
  color: #6b7280;
  font-size: 1.1rem;
  margin: 0 0 20px 0;
}

/* 对话框样式 */
.dialog-overlay {
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

.dialog-content {
  background: white;
  padding: 32px;
  border-radius: 16px;
  width: 500px;
  max-width: 90vw;
  max-height: 90vh;
  overflow-y: auto;
}

.dialog-content h3 {
  font-size: 1.3rem;
  color: #374151;
  margin: 0 0 24px 0;
  font-weight: 600;
}

.form-group {
  margin-bottom: 20px;
}

.form-group label {
  display: block;
  margin-bottom: 8px;
  color: #374151;
  font-weight: 500;
  font-size: 0.9rem;
}

.form-group input,
.form-group select,
.form-group textarea {
  width: 100%;
  padding: 12px;
  border: 2px solid #e5e7eb;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-group input:focus,
.form-group select:focus,
.form-group textarea:focus {
  outline: none;
  border-color: #3b82f6;
}

.form-group textarea {
  min-height: 100px;
  resize: vertical;
}

.dialog-actions {
  display: flex;
  gap: 12px;
  justify-content: flex-end;
  margin-top: 24px;
}

.dialog-actions button {
  padding: 12px 20px;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 0.95rem;
  font-weight: 500;
  transition: all 0.3s ease;
}

.dialog-actions button[type="button"] {
  background: #f3f4f6;
  color: #374151;
}

.dialog-actions button[type="button"]:hover {
  background: #e5e7eb;
}

.dialog-actions button[type="submit"] {
  background: #10b981;
  color: white;
}

.dialog-actions button[type="submit"]:hover {
  background: #059669;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .health-butler {
    padding: 16px;
  }
  
  .header-content {
    flex-direction: column;
    gap: 16px;
  }
  
  .header-text {
    text-align: center;
  }
  
  .title {
    font-size: 2rem;
  }
  
  .butler-tabs {
    flex-direction: column;
    align-items: center;
  }
  
  .health-score-card {
    flex-direction: column;
    gap: 20px;
    padding: 24px;
  }
  
  .report-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .plans-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }
  
  .section-content {
    flex-direction: column;
    gap: 12px;
  }
  
  .plan-info {
    flex-direction: column;
    gap: 8px;
  }
}
</style>

