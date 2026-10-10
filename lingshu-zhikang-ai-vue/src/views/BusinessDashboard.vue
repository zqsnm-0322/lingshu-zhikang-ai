<template>
  <div class="business-dashboard">
    <!-- 顶部标题栏 -->
    <div class="dashboard-header">
      <div class="header-content">
        <h1>商业价值数据看板</h1>
        <p class="subtitle">灵枢智康 | AI多模态智慧健康管理平台 - 实时运营数据分析</p>
      </div>
      <div class="header-actions">
        <button @click="loadStatistics" class="refresh-btn" :disabled="loading">
          <i class="iconify" data-icon="mdi:refresh" v-if="!loading"></i>
          <i class="iconify rotating" data-icon="mdi:loading" v-else></i>
          <span v-if="!loading">刷新</span>
          <span v-else>加载中</span>
        </button>
        <span class="update-time">{{ lastUpdateTime }}</span>
      </div>
    </div>

    <!-- 核心指标卡片 -->
    <div class="metrics-grid">
      <div class="metric-card blue">
        <div class="metric-header">
          <i class="iconify metric-icon" data-icon="mdi:account-group"></i>
          <span class="metric-title">累计用户</span>
        </div>
        <div class="metric-value">{{ formatNumber(statistics.totalUsers) }}+</div>
        <div class="metric-footer">
          <span class="metric-label">付费转化率</span>
          <span class="metric-badge">{{ statistics.conversionRate }}</span>
        </div>
      </div>

      <div class="metric-card green">
        <div class="metric-header">
          <i class="iconify metric-icon" data-icon="mdi:message-text"></i>
          <span class="metric-title">日均AI对话</span>
        </div>
        <div class="metric-value">{{ formatNumber(statistics.dailyAiConversations) }}+</div>
        <div class="metric-footer">
          <span class="metric-label">用户满意度</span>
          <span class="metric-badge">{{ statistics.userSatisfaction }}</span>
        </div>
      </div>

      <div class="metric-card orange">
        <div class="metric-header">
          <i class="iconify metric-icon" data-icon="mdi:currency-cny"></i>
          <span class="metric-title">月均收入</span>
        </div>
        <div class="metric-value">¥{{ formatNumber(statistics.monthlyRevenue) }}</div>
        <div class="metric-footer">
          <span class="metric-label">年收入</span>
          <span class="metric-badge">¥{{ formatNumber(statistics.annualRevenue) }}</span>
        </div>
      </div>

      <div class="metric-card purple">
        <div class="metric-header">
          <i class="iconify metric-icon" data-icon="mdi:office-building"></i>
          <span class="metric-title">企业客户</span>
        </div>
        <div class="metric-value">{{ statistics.enterpriseClients }}+</div>
        <div class="metric-footer">
          <span class="metric-label">客户留存率</span>
          <span class="metric-badge">{{ statistics.customerRetentionRate }}</span>
        </div>
      </div>
    </div>

    <!-- AI技术性能指标 -->
    <div class="section-card">
      <div class="section-header">
        <h2><i class="iconify" data-icon="mdi:robot"></i> AI技术性能指标</h2>
      </div>
      <div class="tech-metrics">
        <div class="tech-item">
          <div class="tech-info">
            <span class="tech-label">诊断准确率</span>
            <span class="tech-value">95%+</span>
          </div>
          <div class="progress-bar">
            <div class="progress-fill blue-progress" style="width: 95%"></div>
          </div>
        </div>
        <div class="tech-item">
          <div class="tech-info">
            <span class="tech-label">服务可用性</span>
            <span class="tech-value">99.9%</span>
          </div>
          <div class="progress-bar">
            <div class="progress-fill green-progress" style="width: 99.9%"></div>
          </div>
        </div>
        <div class="tech-item">
          <div class="tech-info">
            <span class="tech-label">平均响应时间</span>
            <span class="tech-value highlight">{{ statistics.averageResponseTime }}</span>
          </div>
        </div>
        <div class="tech-item">
          <div class="tech-info">
            <span class="tech-label">并发处理能力</span>
            <span class="tech-value highlight">{{ statistics.concurrentCapacity }}</span>
          </div>
        </div>
      </div>
    </div>

    <!-- 收入来源分布 -->
    <div class="section-card">
      <div class="section-header">
        <h2><i class="iconify" data-icon="mdi:chart-pie"></i> 收入来源分布</h2>
      </div>
      <div class="revenue-chart">
        <div class="revenue-item" v-for="(item, index) in revenueData" :key="index">
          <div class="revenue-info">
            <span class="revenue-name">{{ item.name }}</span>
            <span class="revenue-percentage">{{ item.percentage }}</span>
          </div>
          <div class="revenue-bar-container">
            <div class="revenue-bar" :style="{ width: item.percentage, backgroundColor: item.color }"></div>
          </div>
        </div>
      </div>
    </div>

    <!-- 商业模式 -->
    <div class="section-card">
      <div class="section-header">
        <h2><i class="iconify" data-icon="mdi:chart-line"></i> 商业模式与盈利分析</h2>
      </div>
      <div class="business-model">
        <div class="model-card">
          <div class="model-header">
            <i class="iconify model-icon" data-icon="mdi:account-circle"></i>
            <h3>会员体系</h3>
          </div>
          <ul class="model-list">
            <li><strong>免费版：</strong>基础AI咨询，每日5次对话</li>
            <li><strong>标准版（¥99/月）：</strong>无限对话，病历分析，健康报告</li>
            <li><strong>专业版（¥299/月）：</strong>专家级诊断，影像分析，优先响应</li>
            <li><strong>企业版（定制）：</strong>API接入，数据看板，定制化服务</li>
          </ul>
        </div>

        <div class="model-card">
          <div class="model-header">
            <i class="iconify model-icon" data-icon="mdi:domain"></i>
            <h3>企业服务</h3>
          </div>
          <ul class="model-list">
            <li><strong>基础版：</strong>¥10万/年（100-300人）</li>
            <li><strong>标准版：</strong>¥20万/年（300-500人）</li>
            <li><strong>高级版：</strong>¥50万/年（500人以上）</li>
            <li><strong>定制开发：</strong>¥50-200万/项目</li>
          </ul>
        </div>

        <div class="model-card">
          <div class="model-header">
            <i class="iconify model-icon" data-icon="mdi:api"></i>
            <h3>API服务</h3>
          </div>
          <ul class="model-list">
            <li><strong>按量计费：</strong>¥0.1/次调用</li>
            <li><strong>包年服务：</strong>¥10万/年（无限调用）</li>
            <li><strong>技术支持：</strong>完整API文档和SDK</li>
            <li><strong>SLA保障：</strong>99.9%可用性承诺</li>
          </ul>
        </div>
      </div>
    </div>

    <!-- 竞争优势 -->
    <div class="section-card">
      <div class="section-header">
        <h2><i class="iconify" data-icon="mdi:target"></i> 核心竞争优势</h2>
      </div>
      <div class="advantages-grid">
        <div class="advantage-card">
          <div class="advantage-icon blue-bg">
            <i class="iconify" data-icon="mdi:rocket-launch"></i>
          </div>
          <h3>技术领先</h3>
          <p>多模态AI交互、RAG增强分析、医学知识图谱集成</p>
        </div>
        <div class="advantage-card">
          <div class="advantage-icon green-bg">
            <i class="iconify" data-icon="mdi:lightning-bolt"></i>
          </div>
          <h3>服务优质</h3>
          <p>7x24小时在线、平均响应1.2秒、用户满意度92%</p>
        </div>
        <div class="advantage-card">
          <div class="advantage-icon orange-bg">
            <i class="iconify" data-icon="mdi:chart-areaspline"></i>
          </div>
          <h3>数据壁垒</h3>
          <p>积累10万+用户健康数据，形成强大的数据护城河</p>
        </div>
        <div class="advantage-card">
          <div class="advantage-icon purple-bg">
            <i class="iconify" data-icon="mdi:sync"></i>
          </div>
          <h3>生态闭环</h3>
          <p>完整的健康管理闭环，从诊断到治疗到康复</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import axios from 'axios'

const statistics = ref({
  totalUsers: 100000,
  dailyAiConversations: 50000,
  monthlyRevenue: 500000,
  annualRevenue: 6000000,
  enterpriseClients: 50,
  conversionRate: '8.0%',
  userSatisfaction: '92%',
  customerRetentionRate: '75%',
  averageResponseTime: '1.2s',
  concurrentCapacity: '1000+ QPS'
})

const revenueData = ref([
  { name: 'C端用户付费', percentage: '40%', color: '#3b82f6' },
  { name: 'B端企业服务', percentage: '35%', color: '#10b981' },
  { name: '数据服务', percentage: '15%', color: '#f59e0b' },
  { name: '广告推广', percentage: '10%', color: '#8b5cf6' }
])

const loading = ref(false)
const lastUpdateTime = ref('')

const formatNumber = (num) => {
  return num.toLocaleString('zh-CN')
}

const loadStatistics = async () => {
  loading.value = true
  try {
    const response = await axios.get('http://localhost:8080/api/statistics/overview')
    if (response.data.success) {
      statistics.value = response.data.data
    }
    lastUpdateTime.value = new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } catch (error) {
    console.error('加载统计数据失败:', error)
    lastUpdateTime.value = new Date().toLocaleString('zh-CN', {
      year: 'numeric',
      month: '2-digit',
      day: '2-digit',
      hour: '2-digit',
      minute: '2-digit',
      second: '2-digit'
    })
  } finally {
    loading.value = false
  }
}

onMounted(() => {
  loadStatistics()
})
</script>

<style scoped>
.business-dashboard {
  min-height: 100vh;
  background: #f5f7fa;
  padding: 24px;
}

/* Iconify 图标样式 */
.iconify {
  display: inline-block;
  vertical-align: middle;
}

.rotating {
  animation: rotate 1s linear infinite;
}

@keyframes rotate {
  from { transform: rotate(0deg); }
  to { transform: rotate(360deg); }
}

/* 顶部标题栏 */
.dashboard-header {
  background: white;
  border-radius: 12px;
  padding: 24px 32px;
  margin-bottom: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.header-content h1 {
  font-size: 28px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 8px 0;
}

.subtitle {
  font-size: 14px;
  color: #6b7280;
  margin: 0;
}

.header-actions {
  display: flex;
  align-items: center;
  gap: 16px;
}

.refresh-btn {
  background: #3b82f6;
  color: white;
  border: none;
  padding: 10px 20px;
  border-radius: 8px;
  font-size: 14px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  display: flex;
  align-items: center;
  gap: 8px;
}

.refresh-btn .iconify {
  font-size: 18px;
}

.refresh-btn:hover:not(:disabled) {
  background: #2563eb;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.3);
}

.refresh-btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.update-time {
  font-size: 13px;
  color: #9ca3af;
}

/* 核心指标卡片 */
.metrics-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(280px, 1fr));
  gap: 20px;
  margin-bottom: 24px;
}

.metric-card {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.06);
  transition: all 0.3s;
  border-left: 4px solid;
}

.metric-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.12);
}

.metric-card.blue { border-left-color: #3b82f6; }
.metric-card.green { border-left-color: #10b981; }
.metric-card.orange { border-left-color: #f59e0b; }
.metric-card.purple { border-left-color: #8b5cf6; }

.metric-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
}

.metric-icon {
  font-size: 32px;
}

.metric-card.blue .metric-icon { color: #3b82f6; }
.metric-card.green .metric-icon { color: #10b981; }
.metric-card.orange .metric-icon { color: #f59e0b; }
.metric-card.purple .metric-icon { color: #8b5cf6; }

.metric-title {
  font-size: 14px;
  color: #6b7280;
  font-weight: 500;
}

.metric-value {
  font-size: 36px;
  font-weight: 700;
  color: #1f2937;
  margin: 12px 0;
  line-height: 1;
}

.metric-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding-top: 12px;
  border-top: 1px solid #f3f4f6;
}

.metric-label {
  font-size: 13px;
  color: #9ca3af;
}

.metric-badge {
  font-size: 14px;
  font-weight: 600;
  color: #3b82f6;
  background: #eff6ff;
  padding: 4px 12px;
  border-radius: 12px;
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

/* AI技术指标 */
.tech-metrics {
  display: grid;
  gap: 20px;
}

.tech-item {
  padding: 16px 0;
}

.tech-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
}

.tech-label {
  font-size: 15px;
  font-weight: 500;
  color: #4b5563;
}

.tech-value {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.tech-value.highlight {
  color: #3b82f6;
  font-size: 18px;
}

.progress-bar {
  height: 12px;
  background: #f3f4f6;
  border-radius: 6px;
  overflow: hidden;
}

.progress-fill {
  height: 100%;
  border-radius: 6px;
  transition: width 1s ease;
}

.blue-progress {
  background: linear-gradient(90deg, #3b82f6, #60a5fa);
}

.green-progress {
  background: linear-gradient(90deg, #10b981, #34d399);
}

/* 收入来源分布 */
.revenue-chart {
  display: grid;
  gap: 16px;
}

.revenue-item {
  padding: 12px 0;
}

.revenue-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}

.revenue-name {
  font-size: 15px;
  font-weight: 500;
  color: #4b5563;
}

.revenue-percentage {
  font-size: 16px;
  font-weight: 600;
  color: #1f2937;
}

.revenue-bar-container {
  height: 32px;
  background: #f3f4f6;
  border-radius: 8px;
  overflow: hidden;
}

.revenue-bar {
  height: 100%;
  border-radius: 8px;
  transition: width 1s ease;
}

/* 商业模式 */
.business-model {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
}

.model-card {
  background: #f9fafb;
  border-radius: 10px;
  padding: 24px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s;
}

.model-card:hover {
  border-color: #3b82f6;
  box-shadow: 0 4px 12px rgba(59, 130, 246, 0.1);
}

.model-header {
  display: flex;
  align-items: center;
  gap: 12px;
  margin-bottom: 16px;
  padding-bottom: 12px;
  border-bottom: 2px solid #e5e7eb;
}

.model-icon {
  font-size: 28px;
  color: #3b82f6;
}

.model-header h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0;
}

.model-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.model-list li {
  padding: 10px 0;
  font-size: 14px;
  color: #4b5563;
  line-height: 1.6;
  border-bottom: 1px solid #e5e7eb;
}

.model-list li:last-child {
  border-bottom: none;
}

.model-list strong {
  color: #1f2937;
  font-weight: 600;
}

/* 竞争优势 */
.advantages-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(260px, 1fr));
  gap: 20px;
}

.advantage-card {
  text-align: center;
  padding: 28px 20px;
  background: #f9fafb;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  transition: all 0.3s;
}

.advantage-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.08);
  border-color: transparent;
}

.advantage-icon {
  width: 64px;
  height: 64px;
  margin: 0 auto 16px;
  border-radius: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.advantage-icon .iconify {
  font-size: 36px;
  color: white;
}

.blue-bg { background: linear-gradient(135deg, #3b82f6, #60a5fa); }
.green-bg { background: linear-gradient(135deg, #10b981, #34d399); }
.orange-bg { background: linear-gradient(135deg, #f59e0b, #fbbf24); }
.purple-bg { background: linear-gradient(135deg, #8b5cf6, #a78bfa); }

.advantage-card h3 {
  font-size: 18px;
  font-weight: 600;
  color: #1f2937;
  margin: 0 0 12px 0;
}

.advantage-card p {
  font-size: 14px;
  color: #6b7280;
  line-height: 1.6;
  margin: 0;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .business-dashboard {
    padding: 16px;
  }

  .dashboard-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 16px;
  }

  .header-content h1 {
    font-size: 22px;
  }

  .metrics-grid {
    grid-template-columns: 1fr;
  }

  .metric-value {
    font-size: 28px;
  }

  .business-model,
  .advantages-grid {
    grid-template-columns: 1fr;
  }
}
</style>
