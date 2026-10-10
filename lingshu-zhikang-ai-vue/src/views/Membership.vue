<template>
  <DashboardLayout title="会员中心" subtitle="查看当前套餐并升级 AI 医疗服务额度">
    <!-- 支付对话框 -->
    <PaymentDialog
      v-model="showPaymentDialog"
      :planType="selectedPlan?.planType"
      :planName="selectedPlan?.name"
      :paymentType="selectedPaymentType"
      :amount="paymentAmount"
      @success="handlePaymentSuccess"
    />

    <div class="membership-page">
      <PageHeader
        title="会员与定价"
        subtitle="保留现有套餐、支付周期选择和会员升级流程，统一为工作台页面风格。"
      >
        <template #actions>
          <button class="zq-text-button" type="button" @click="goBack">
            <ArrowLeftIcon />
            返回首页
          </button>
        </template>
      </PageHeader>

      <!-- 当前会员状态 -->
      <section class="current-plan-card zq-card" v-if="currentPlan">
        <h2 class="zq-section-title">当前套餐</h2>
        <div class="plan-badge" :class="currentPlan.planType">
          <span class="plan-name">{{ currentPlan.name }}</span>
          <span class="plan-expire" v-if="currentPlan.endDate">有效期至 {{ currentPlan.endDate }}</span>
        </div>
      </section>
      <section class="current-plan-card zq-card free" v-else>
        <h2 class="zq-section-title">当前套餐</h2>
        <div class="plan-badge FREE">
          <span class="plan-name">免费版</span>
          <span class="plan-desc">每日 5 次 AI 对话</span>
        </div>
      </section>

      <!-- 套餐与定价（文档 2.2.1） -->
      <section class="pricing-section">
        <div class="section-heading">
          <h2 class="zq-section-title">选择套餐</h2>
          <p class="zq-section-subtitle">根据问诊频率、病历分析和企业服务需求选择合适版本。</p>
        </div>
        <div class="pricing-grid">
          <article class="pricing-card zq-card" :class="{ recommended: plan.recommended, current: isCurrentPlan(plan.planType) }" v-for="plan in plans" :key="plan.planType">
            <div class="card-header">
              <span class="plan-tag" v-if="plan.recommended">推荐</span>
              <span class="plan-tag current-tag" v-if="isCurrentPlan(plan.planType)">当前套餐</span>
              <h3>{{ plan.name }}</h3>
              <div class="price">
                <span class="amount">{{ plan.priceDisplay }}</span>
                <span class="unit">{{ plan.unit }}</span>
              </div>
            </div>
            <ul class="features">
              <li v-for="(f, i) in plan.features" :key="i">
                <CheckCircleIcon />
                <span>{{ f }}</span>
              </li>
            </ul>
            <button class="btn-primary" :class="{ outline: isCurrentPlan(plan.planType) }" @click="onSelectPlan(plan)">
              {{ isCurrentPlan(plan.planType) ? '当前使用' : '立即开通' }}
            </button>
          </article>
        </div>
      </section>

      <!-- 收入结构说明（体现商业化） -->
      <section class="business-section zq-card">
        <div class="section-heading">
          <h2 class="zq-section-title">服务说明</h2>
          <p class="zq-section-subtitle">覆盖个人健康管理、智能诊断、企业健康服务和 API 接入。</p>
        </div>
        <div class="business-cards">
          <div class="business-card">
            <h4>C 端用户</h4>
            <p>会员订阅 99元/月、999元/年；智能诊断 9.9元/次，病历分析 19.9元/次</p>
          </div>
          <div class="business-card">
            <h4>B 端企业</h4>
            <p>企业健康管理 10-50万/年；API 按量计费；定制开发 50-200万/项目</p>
          </div>
        </div>
      </section>
      </div>
  </DashboardLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { ArrowLeftIcon, CheckCircleIcon } from '@heroicons/vue/24/outline'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import { memberAPI } from '@/services/api'
import PaymentDialog from '@/components/PaymentDialog.vue'

const router = useRouter()
const currentPlan = ref(null)
const isLoading = ref(false)
const showPaymentDialog = ref(false)
const selectedPlan = ref(null)
const selectedPaymentType = ref('MONTHLY')
const paymentAmount = ref(0)

const plans = [
  {
    planType: 'FREE',
    name: '免费版',
    priceDisplay: '0',
    unit: '元/月',
    features: ['每日 5 次 AI 对话', '基础健康咨询', '部分功能体验'],
    recommended: false
  },
  {
    planType: 'STANDARD',
    name: '标准版',
    priceDisplay: '99',
    unit: '元/月',
    features: ['无限 AI 对话', '病历分析', '健康报告', '优先响应'],
    recommended: true
  },
  {
    planType: 'PROFESSIONAL',
    name: '专业版',
    priceDisplay: '299',
    unit: '元/月',
    features: ['专家级诊断', '优先响应', '全部功能'],
    recommended: false
  },
  {
    planType: 'ENTERPRISE',
    name: '企业版',
    priceDisplay: '定制',
    unit: '',
    features: ['API 接入', '数据看板', '定制化服务', '专属对接'],
    recommended: false
  }
]

const goBack = () => router.push('/')

const onSelectPlan = async (plan) => {
  if (isCurrentPlan(plan.planType)) return
  
  // 显示支付类型选择对话框
  const paymentType = await showPaymentTypeDialog(plan)
  if (!paymentType) return
  
  // 设置选中的套餐和支付信息
  selectedPlan.value = plan
  selectedPaymentType.value = paymentType
  
  // 计算支付金额
  if (plan.planType === 'FREE') {
    paymentAmount.value = 0
  } else if (plan.priceDisplay === '定制') {
    paymentAmount.value = 999
  } else {
    const monthlyPrice = parseInt(plan.priceDisplay)
    paymentAmount.value = paymentType === 'YEARLY' 
      ? Math.floor(monthlyPrice * 12 * 0.8) 
      : monthlyPrice
  }
  
  // 显示支付对话框
  showPaymentDialog.value = true
}

const showPaymentTypeDialog = (plan) => {
  return new Promise((resolve) => {
    const monthlyPrice = plan.priceDisplay === '定制' ? 999 : plan.priceDisplay
    const yearlyPrice = Math.floor(monthlyPrice * 12 * 0.8)
    
    const message = `
      <div style="text-align: left; padding: 10px;">
        <h3 style="margin-bottom: 15px;">选择支付周期</h3>
        <div style="margin-bottom: 10px;">
          <strong>套餐：</strong>${plan.name}
        </div>
        <div style="margin-bottom: 15px;">
          <label style="display: block; margin-bottom: 10px; cursor: pointer;">
            <input type="radio" name="payment" value="MONTHLY" checked style="margin-right: 8px;">
            月付：¥${monthlyPrice}/月
          </label>
          <label style="display: block; cursor: pointer;">
            <input type="radio" name="payment" value="YEARLY" style="margin-right: 8px;">
            年付：¥${yearlyPrice}/年 <span style="color: #10b981; font-size: 12px;">(省¥${monthlyPrice * 12 - yearlyPrice})</span>
          </label>
        </div>
      </div>
    `
    
    ElMessageBox.confirm(message, '选择支付周期', {
      confirmButtonText: '下一步',
      cancelButtonText: '取消',
      dangerouslyUseHTMLString: true,
      beforeClose: (action, instance, done) => {
        if (action === 'confirm') {
          const selected = document.querySelector('input[name="payment"]:checked')
          resolve(selected ? selected.value : null)
        } else {
          resolve(null)
        }
        done()
      }
    }).catch(() => {
      resolve(null)
    })
  })
}

const handlePaymentSuccess = async (paymentInfo) => {
  try {
    isLoading.value = true
    const userId = localStorage.getItem('userId')
    
    const response = await fetch('/api/member/upgrade', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': userId
      },
      body: JSON.stringify({
        userId: userId,
        planType: paymentInfo.planType,
        paymentType: paymentInfo.paymentType
      })
    })
    
    const result = await response.json()
    
    if (result.code === '200') {
      ElMessage.success('会员升级成功！')
      // 重新加载会员信息
      await loadSubscription()
      // 刷新页面显示新的配额
      setTimeout(() => {
        window.location.reload()
      }, 1500)
    } else {
      ElMessage.error(result.msg || '升级失败')
    }
  } catch (e) {
    console.error('升级失败', e)
    ElMessage.error('升级失败，请稍后再试')
  } finally {
    isLoading.value = false
  }
}

const isCurrentPlan = (planType) => {
  if (!currentPlan.value) {
    return planType === 'FREE'
  }
  return currentPlan.value.planType === planType
}

const loadSubscription = async () => {
  try {
    const data = await memberAPI.getSubscription()
    if (data && data.planType && data.planType !== 'FREE') {
      currentPlan.value = {
        planType: data.planType,
        name: plans.find(p => p.planType === data.planType)?.name || data.planType,
        endDate: data.endDate
      }
    }
  } catch (e) {
    console.error('获取会员信息失败', e)
  }
}

onMounted(() => {
  loadSubscription()
})
</script>

<style scoped lang="scss">
.membership-page {
  display: flex;
  flex-direction: column;
  gap: 22px;
}

.membership-page :deep(.zq-text-button svg) {
  width: 18px;
  height: 18px;
}

.current-plan-card {
  padding: 20px;
}

.plan-badge {
  display: inline-flex;
  flex-direction: column;
  gap: 4px;
  padding: 12px 20px;
  border-radius: 10px;
  background: #eff6ff;
  color: #1e40af;
  font-weight: 600;
  .plan-desc, .plan-expire { font-size: 12px; font-weight: 400; color: #64748b; }
}

.section-heading {
  margin-bottom: 16px;
}

.pricing-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(240px, 1fr));
  gap: 20px;
}

.pricing-card {
  padding: 24px;
  position: relative;
  transition: all 0.2s;
  &.recommended {
    border-color: #1e40af;
    box-shadow: 0 4px 12px rgba(30, 64, 175, 0.15);
  }
  &.current {
    border-color: #10b981;
    background: linear-gradient(135deg, rgba(16, 185, 129, 0.05) 0%, rgba(16, 185, 129, 0.02) 100%);
    box-shadow: 0 4px 12px rgba(16, 185, 129, 0.2);
  }
}

.plan-tag {
  position: absolute;
  top: -10px;
  right: 16px;
  background: #1e40af;
  color: #fff;
  font-size: 12px;
  padding: 4px 10px;
  border-radius: 20px;
  &.current-tag {
    background: #10b981;
    right: auto;
    left: 16px;
  }
}

.card-header h3 {
  font-size: 18px;
  margin-bottom: 8px;
  color: var(--zq-text-primary);
}

.price {
  margin-bottom: 16px;
  .amount { font-size: 28px; font-weight: 700; color: #1e40af; }
  .unit { font-size: 14px; color: #64748b; margin-left: 4px; }
}

.features {
  list-style: none;
  padding: 0;
  margin: 0 0 20px 0;
  font-size: 14px;
  color: var(--zq-text-secondary);
  li {
    display: flex;
    align-items: center;
    gap: 8px;
    padding: 6px 0;
    border-bottom: 1px solid var(--zq-border);
    &:last-child { border-bottom: none; }
  }
  svg {
    width: 16px;
    height: 16px;
    color: var(--zq-color-success);
  }
}

.btn-primary {
  width: 100%;
  padding: 10px 16px;
  border-radius: 8px;
  border: none;
  background: var(--zq-color-primary);
  color: #fff;
  font-weight: 600;
  cursor: pointer;
  &.outline {
    background: #f1f5f9;
    color: #64748b;
    cursor: default;
  }
  &:not(.outline):hover {
    opacity: 0.95;
  }
}

.business-section {
  padding: 20px;
}

.business-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(280px, 1fr));
  gap: 16px;
}

.business-card {
  background: var(--zq-bg-muted);
  border-radius: var(--zq-radius);
  padding: 20px;
  border: 1px solid var(--zq-border);
  h4 { font-size: 16px; color: #1e40af; margin-bottom: 8px; }
  p { font-size: 14px; color: var(--zq-text-secondary); line-height: 1.5; margin: 0; }
}

@media (max-width: 760px) {
  .pricing-card {
    padding: 20px;
  }
}
</style>
