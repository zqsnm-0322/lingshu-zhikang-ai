<template>
  <div class="health-warning">
    <div class="warning-header">
      <h1>⚠️ 智能预警系统</h1>
      <button @click="checkWarnings" class="check-btn">立即检查</button>
    </div>

    <!-- 预警统计 -->
    <div v-if="statistics" class="warning-stats">
      <div class="stat-card pending">
        <div class="stat-icon">🔔</div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.pendingCount }}</div>
          <div class="stat-label">待处理预警</div>
        </div>
      </div>
      <div class="stat-card total">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <div class="stat-value">{{ statistics.total }}</div>
          <div class="stat-label">近{{ statistics.period }}总预警</div>
        </div>
      </div>
    </div>

    <!-- 风险评估 -->
    <div v-if="riskAssessment" class="risk-assessment">
      <h2>🎯 健康风险评估</h2>
      <div class="risk-content">
        <div class="risk-score" :class="getRiskLevelClass(riskAssessment.riskLevel)">
          <div class="risk-value">{{ riskAssessment.riskScore }}</div>
          <div class="risk-level">{{ riskAssessment.riskLevel }}风险</div>
        </div>
        <div class="risk-details">
          <div class="risk-factors">
            <h3>风险因素</h3>
            <ul>
              <li v-for="(factor, index) in riskAssessment.riskFactors" :key="index">
                {{ factor }}
              </li>
            </ul>
          </div>
          <div class="risk-recommendations">
            <h3>建议措施</h3>
            <ul>
              <li v-for="(rec, index) in riskAssessment.recommendations" :key="index">
                {{ rec }}
              </li>
            </ul>
          </div>
        </div>
      </div>
    </div>

    <!-- 未处理预警 -->
    <div v-if="pendingWarnings.length > 0" class="pending-warnings">
      <h2>🚨 待处理预警</h2>
      <div class="warning-list">
        <div v-for="warning in pendingWarnings" :key="warning.id" 
             :class="['warning-card', warning.warningLevel, { urgent: warning.isUrgent }]">
          <div class="warning-header-content">
            <div class="warning-icon">{{ getWarningIcon(warning.warningLevel) }}</div>
            <div class="warning-info">
              <h3>{{ warning.warningTitle }}</h3>
              <div class="warning-meta">
                <span class="warning-type">{{ warning.warningType }}</span>
                <span class="warning-level-badge">{{ warning.warningLevel }}</span>
                <span v-if="warning.isUrgent" class="urgent-badge">紧急</span>
              </div>
            </div>
          </div>
          <div class="warning-content">
            <p class="warning-text">{{ warning.warningContent }}</p>
            <div class="warning-suggestions">
              <h4>建议措施：</h4>
              <p>{{ warning.suggestions }}</p>
            </div>
          </div>
          <div class="warning-actions">
            <button @click="handleWarning(warning.id, 'handled', '已处理')" class="handle-btn">
              已处理
            </button>
            <button @click="handleWarning(warning.id, 'viewed', '已查看')" class="view-btn">
              已查看
            </button>
            <button @click="handleWarning(warning.id, 'dismissed', '忽略')" class="dismiss-btn">
              忽略
            </button>
          </div>
          <div class="warning-time">{{ formatTime(warning.createdTime) }}</div>
        </div>
      </div>
    </div>

    <!-- 历史预警 -->
    <div class="history-warnings">
      <h2>📋 历史预警记录</h2>
      <div class="warning-list">
        <div v-for="warning in historyWarnings" :key="warning.id" 
             :class="['warning-card', 'history', warning.warningLevel]">
          <div class="warning-header-content">
            <div class="warning-icon">{{ getWarningIcon(warning.warningLevel) }}</div>
            <div class="warning-info">
              <h3>{{ warning.warningTitle }}</h3>
              <div class="warning-meta">
                <span class="warning-type">{{ warning.warningType }}</span>
                <span class="warning-level-badge">{{ warning.warningLevel }}</span>
                <span class="status-badge">{{ getStatusLabel(warning.status) }}</span>
              </div>
            </div>
          </div>
          <div class="warning-content">
            <p class="warning-text">{{ warning.warningContent }}</p>
          </div>
          <div class="warning-time">{{ formatTime(warning.createdTime) }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios'

export default {
  name: 'HealthWarning',
  data() {
    return {
      pendingWarnings: [],
      historyWarnings: [],
      statistics: null,
      riskAssessment: null,
      userId: 1
    }
  },
  mounted() {
    this.loadWarnings()
    this.loadStatistics()
    this.loadRiskAssessment()
  },
  methods: {
    async loadWarnings() {
      try {
        // 加载待处理预警
        const pendingRes = await axios.get('http://localhost:8080/api/health-warning/pending', {
          params: { userId: this.userId }
        })
        if (pendingRes.data.code === 200) {
          this.pendingWarnings = pendingRes.data.data || []
        }

        // 加载历史预警
        const historyRes = await axios.get('http://localhost:8080/api/health-warning/list', {
          params: { userId: this.userId, limit: 20 }
        })
        if (historyRes.data.code === 200) {
          this.historyWarnings = (historyRes.data.data || []).filter(w => w.status !== 'pending')
        }
      } catch (error) {
        console.error('加载预警失败:', error)
      }
    },

    async loadStatistics() {
      try {
        const response = await axios.get('http://localhost:8080/api/health-warning/statistics', {
          params: { userId: this.userId, days: 30 }
        })
        if (response.data.code === 200) {
          this.statistics = response.data.data
        }
      } catch (error) {
        console.error('加载统计数据失败:', error)
      }
    },

    async loadRiskAssessment() {
      try {
        const response = await axios.get('http://localhost:8080/api/health-warning/risk-assessment', {
          params: { userId: this.userId }
        })
        if (response.data.code === 200) {
          this.riskAssessment = response.data.data
        }
      } catch (error) {
        console.error('加载风险评估失败:', error)
      }
    },

    async checkWarnings() {
      try {
        const response = await axios.post('http://localhost:8080/api/health-warning/check', null, {
          params: { userId: this.userId }
        })
        if (response.data.code === 200) {
          const newWarnings = response.data.data || []
          if (newWarnings.length > 0) {
            alert(`检测到 ${newWarnings.length} 条新预警！`)
            this.loadWarnings()
          } else {
            alert('暂无新预警，您的健康状况良好！')
          }
        }
      } catch (error) {
        console.error('检查预警失败:', error)
        alert('检查预警失败，请稍后重试')
      }
    },

    async handleWarning(warningId, status, note) {
      try {
        const response = await axios.put(
          `http://localhost:8080/api/health-warning/${warningId}/status`,
          null,
          {
            params: { status, note }
          }
        )
        if (response.data.code === 200) {
          alert('操作成功！')
          this.loadWarnings()
          this.loadStatistics()
        }
      } catch (error) {
        console.error('处理预警失败:', error)
        alert('操作失败，请稍后重试')
      }
    },

    getWarningIcon(level) {
      const icons = {
        '红色': '🔴',
        '橙色': '🟠',
        '黄色': '🟡',
        '绿色': '🟢'
      }
      return icons[level] || '⚠️'
    },

    getRiskLevelClass(level) {
      const classMap = {
        '极高': 'critical',
        '高': 'high',
        '中': 'medium',
        '低': 'low'
      }
      return classMap[level] || 'medium'
    },

    getStatusLabel(status) {
      const labels = {
        'pending': '待处理',
        'viewed': '已查看',
        'handled': '已处理',
        'dismissed': '已忽略'
      }
      return labels[status] || status
    },

    formatTime(time) {
      if (!time) return ''
      const date = new Date(time)
      return date.toLocaleString('zh-CN')
    }
  }
}
</script>

<style>
.health-warning {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  min-height: 100vh;
  width: 100vw;
  background: #6a1b9a; /* 深紫色背景 */
  padding: 20px;
  margin: 0;
  box-sizing: border-box;
  overflow-y: auto;
}

.health-warning > * {
  max-width: 1200px;
  margin-left: auto;
  margin-right: auto;
}

.warning-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 30px;
}

.warning-header h1 {
  font-size: 32px;
  color: #ffffff;
}

.check-btn {
  padding: 12px 24px;
  background: #e74c3c;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-size: 16px;
}

.warning-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 20px;
  margin-bottom: 30px;
}

.stat-card {
  display: flex;
  align-items: center;
  gap: 20px;
  padding: 30px;
  border-radius: 12px;
  color: white;
}

.stat-card.pending {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-card.total {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.stat-icon {
  font-size: 48px;
}

.stat-value {
  font-size: 36px;
  font-weight: bold;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
}

.risk-assessment {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.risk-assessment h2 {
  margin-bottom: 20px;
  color: #2c3e50;
}

.risk-content {
  display: flex;
  gap: 30px;
  align-items: flex-start;
}

.risk-score {
  text-align: center;
  padding: 30px;
  border-radius: 12px;
  min-width: 200px;
}

.risk-score.low {
  background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
  color: white;
}

.risk-score.medium {
  background: linear-gradient(135deg, #FFE000 0%, #799F0C 100%);
  color: white;
}

.risk-score.high {
  background: linear-gradient(135deg, #FF8008 0%, #FFC837 100%);
  color: white;
}

.risk-score.critical {
  background: linear-gradient(135deg, #eb3349 0%, #f45c43 100%);
  color: white;
}

.risk-value {
  font-size: 48px;
  font-weight: bold;
}

.risk-level {
  font-size: 18px;
  margin-top: 10px;
}

.risk-details {
  flex: 1;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 30px;
}

.risk-factors h3,
.risk-recommendations h3 {
  margin-bottom: 10px;
  color: #2c3e50;
}

.risk-factors ul,
.risk-recommendations ul {
  list-style: none;
  padding: 0;
}

.risk-factors li,
.risk-recommendations li {
  padding: 8px 0;
  color: #555;
  border-bottom: 1px solid #ecf0f1;
}

.pending-warnings,
.history-warnings {
  background: white;
  border-radius: 12px;
  padding: 30px;
  margin-bottom: 30px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.pending-warnings h2,
.history-warnings h2 {
  margin-bottom: 20px;
  color: #2c3e50;
}

.warning-list {
  display: grid;
  gap: 20px;
}

.warning-card {
  border-left: 5px solid;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  transition: all 0.3s;
}

.warning-card:hover {
  box-shadow: 0 4px 12px rgba(0,0,0,0.15);
}

.warning-card.红色 {
  border-left-color: #e74c3c;
  background: #fee;
}

.warning-card.橙色 {
  border-left-color: #e67e22;
  background: #fef3e6;
}

.warning-card.黄色 {
  border-left-color: #f39c12;
  background: #fef9e6;
}

.warning-card.绿色 {
  border-left-color: #27ae60;
  background: #eef8f0;
}

.warning-card.urgent {
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.02); }
}

.warning-header-content {
  display: flex;
  align-items: center;
  gap: 15px;
  margin-bottom: 15px;
}

.warning-icon {
  font-size: 32px;
}

.warning-info h3 {
  margin-bottom: 8px;
  color: #2c3e50;
}

.warning-meta {
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.warning-type,
.warning-level-badge,
.urgent-badge,
.status-badge {
  padding: 3px 10px;
  border-radius: 12px;
  font-size: 12px;
  color: white;
}

.warning-type {
  background: #95a5a6;
}

.warning-level-badge {
  background: #3498db;
}

.urgent-badge {
  background: #e74c3c;
  animation: blink 1s infinite;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-badge {
  background: #27ae60;
}

.warning-content {
  margin-bottom: 15px;
}

.warning-text {
  color: #2c3e50;
  line-height: 1.6;
  margin-bottom: 10px;
}

.warning-suggestions {
  background: white;
  padding: 15px;
  border-radius: 6px;
}

.warning-suggestions h4 {
  color: #2c3e50;
  margin-bottom: 8px;
}

.warning-suggestions p {
  color: #555;
  line-height: 1.6;
}

.warning-actions {
  display: flex;
  gap: 10px;
  margin-bottom: 10px;
}

.warning-actions button {
  padding: 8px 16px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-size: 14px;
}

.handle-btn {
  background: #27ae60;
  color: white;
}

.view-btn {
  background: #3498db;
  color: white;
}

.dismiss-btn {
  background: #95a5a6;
  color: white;
}

.warning-time {
  font-size: 12px;
  color: #95a5a6;
  text-align: right;
}

.warning-card.history {
  opacity: 0.7;
}
</style>

