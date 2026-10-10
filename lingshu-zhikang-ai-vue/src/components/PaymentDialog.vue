<template>
  <el-dialog
    v-model="visible"
    title="确认支付"
    width="500px"
    :close-on-click-modal="false"
    @close="handleClose"
  >
    <div class="payment-dialog">
      <!-- 订单信息 -->
      <div class="order-info">
        <h3>订单信息</h3>
        <div class="info-row">
          <span class="label">套餐：</span>
          <span class="value">{{ planName }}</span>
        </div>
        <div class="info-row">
          <span class="label">支付方式：</span>
          <span class="value">{{ paymentTypeText }}</span>
        </div>
        <div class="info-row">
          <span class="label">支付金额：</span>
          <span class="value price">¥{{ amount }}</span>
        </div>
      </div>

      <!-- 支付方式选择 -->
      <div class="payment-method">
        <h3>选择支付方式</h3>
        <div class="method-options">
          <div 
            class="method-item"
            :class="{ active: selectedMethod === 'alipay' }"
            @click="selectedMethod = 'alipay'"
          >
            <div class="method-icon alipay-icon">
              <svg viewBox="0 0 1024 1024" width="48" height="48">
                <path d="M1024 701.9v162.8c0 88.4-71.7 160.1-160.1 160.1H160.1C71.7 1024.8 0 953.1 0 864.7V159.1C0 70.7 71.7-1 160.1-1h703.8c88.4 0 160.1 71.7 160.1 160.1v390.7c-53.7-24.5-154.4-65.2-267.5-84.7 29.4-38.7 52.7-82.2 68.6-128.9H624.7v-51.8h199.6v-31.3H624.7V322h-51.8v91.1H373.3v31.3h199.6v51.8H372.8c-17.8 78.4-56.1 149.2-112.7 207.1-17.8-13.4-49.2-40.2-64.6-54.1 47.4-47.4 79.2-106.6 93.1-172.5H185.9v-31.3h181.3v-51.8H185.9V322h181.3v-88.6c0-5.4 1.5-6.9 6.9-6.9h61.5v-91.1h255.1v91.1h61.5c5.4 0 6.9 1.5 6.9 6.9V322h199.6v-88.6c0-34.3-27.9-62.2-62.2-62.2H160.1c-34.3 0-62.2 27.9-62.2 62.2v705.6c0 34.3 27.9 62.2 62.2 62.2h703.8c34.3 0 62.2-27.9 62.2-62.2V750.8c32.8 10.9 63.1 23.4 97.9 38.7-53.7 120.3-173 204.4-312.7 204.4-188.5 0-341.3-152.8-341.3-341.3 0-31.3 4.5-61.5 12.4-90.6 20.3 11.9 52.7 32.3 70.1 44.7-5.9 14.9-10.4 30.8-13.4 46.7 120.3 30.8 283.9 98.5 415.2 189.5 21.8-48.9 34.3-102.6 34.3-159.2 0-9.9-0.5-19.8-1-29.7 97.9 18.3 186 54.1 234.4 76.9z" fill="#1296db"/>
              </svg>
            </div>
            <div class="method-name">支付宝</div>
          </div>
          <div 
            class="method-item"
            :class="{ active: selectedMethod === 'wechat' }"
            @click="selectedMethod = 'wechat'"
          >
            <div class="method-icon wechat-icon">
              <svg viewBox="0 0 1024 1024" width="48" height="48">
                <path d="M664.250054 368.541681c10.015098 0 19.892049 0.732687 29.67281 1.795902-26.647917-122.810047-159.358451-214.077703-310.826188-214.077703-169.353083 0-308.085774 114.232694-308.085774 259.274068 0 83.708494 46.165436 152.460344 123.281791 205.78483l-30.80868 91.730191 107.688651-53.455469c38.558178 7.53665 69.459978 15.308661 107.924012 15.308661 9.66308 0 19.230993-0.470721 28.752858-1.225921-6.025227-20.36584-9.521864-41.723264-9.521864-63.862493C402.328693 476.632491 517.908058 368.541681 664.250054 368.541681zM498.62897 285.87389c23.200398 0 38.557154 15.120372 38.557154 38.061874 0 22.846334-15.356756 38.156018-38.557154 38.156018-23.107277 0-46.260603-15.309684-46.260603-38.156018C452.368366 300.994262 475.522716 285.87389 498.62897 285.87389zM283.016307 362.090758c-23.107277 0-46.402843-15.309684-46.402843-38.156018 0-22.941502 23.295566-38.061874 46.402843-38.061874 23.081695 0 38.46301 15.120372 38.46301 38.061874C321.479317 346.782098 306.098002 362.090758 283.016307 362.090758zM945.448458 606.151333c0-121.888048-123.258255-221.236753-261.683954-221.236753-146.57838 0-262.015505 99.348706-262.015505 221.236753 0 122.06508 115.437126 221.200938 262.015505 221.200938 30.66644 0 61.617359-7.609305 92.423993-15.262612l84.513836 45.786813-23.178909-76.17082C899.379213 735.776599 945.448458 674.90216 945.448458 606.151333zM598.803483 567.994292c-15.332197 0-30.807656-15.096836-30.807656-30.501688 0-15.190981 15.47546-30.477129 30.807656-30.477129 23.295566 0 38.558178 15.286148 38.558178 30.477129C637.361661 552.897456 622.099049 567.994292 598.803483 567.994292zM768.25071 567.994292c-15.213493 0-30.594809-15.096836-30.594809-30.501688 0-15.190981 15.381315-30.477129 30.594809-30.477129 23.107277 0 38.558178 15.286148 38.558178 30.477129C806.808888 552.897456 791.357987 567.994292 768.25071 567.994292z" fill="#09bb07"/>
              </svg>
            </div>
            <div class="method-name">微信支付</div>
          </div>
        </div>
      </div>

      <!-- 二维码显示 -->
      <div class="qrcode-section" v-if="selectedMethod">
        <div class="qrcode-container">
          <div class="qrcode-placeholder">
            <div class="qrcode-icon">
              <svg viewBox="0 0 1024 1024" width="120" height="120">
                <path d="M128 128h256v256H128V128z m85.333333 85.333333v85.333334h85.333334V213.333333H213.333333zM128 640h256v256H128V640z m85.333333 85.333333v85.333334h85.333334v-85.333334H213.333333zM640 128h256v256H640V128z m85.333333 85.333333v85.333334h85.333334V213.333333h-85.333334zM128 469.333333h85.333333v85.333334H128v-85.333334z m170.666667 0h85.333333v85.333334h-85.333333v-85.333334z m170.666666 0h85.333334v170.666667h-85.333334V469.333333z m170.666667 0h85.333333v85.333334h-85.333333v-85.333334z m170.666667 0h85.333333v85.333334h-85.333333v-85.333334zM640 640h85.333333v85.333333h-85.333333V640z m0 170.666667h85.333333v85.333333h-85.333333v-85.333333z m170.666667-170.666667h85.333333v256h-85.333333V640z" fill="currentColor"/>
              </svg>
            </div>
            <p class="qrcode-tip">
              <span v-if="selectedMethod === 'alipay'">请使用支付宝扫码支付</span>
              <span v-else>请使用微信扫码支付</span>
            </p>
            <p class="qrcode-amount">¥{{ amount }}</p>
            <p class="qrcode-note">* 这是模拟支付，实际项目需接入真实支付接口</p>
          </div>
        </div>
      </div>

      <!-- 支付成功按钮 -->
      <div class="payment-actions">
        <el-button @click="handleClose">取消支付</el-button>
        <el-button 
          type="primary" 
          @click="handlePaymentSuccess"
          :loading="isProcessing"
          :disabled="!selectedMethod"
        >
          支付成功
        </el-button>
      </div>
    </div>
  </el-dialog>
</template>

<script setup>
import { ref, computed } from 'vue'
import { ElMessage } from 'element-plus'

const props = defineProps({
  modelValue: {
    type: Boolean,
    default: false
  },
  planType: {
    type: String,
    required: true
  },
  planName: {
    type: String,
    required: true
  },
  paymentType: {
    type: String,
    required: true
  },
  amount: {
    type: Number,
    required: true
  }
})

const emit = defineEmits(['update:modelValue', 'success'])

const visible = computed({
  get: () => props.modelValue,
  set: (val) => emit('update:modelValue', val)
})

const selectedMethod = ref('')
const isProcessing = ref(false)

const paymentTypeText = computed(() => {
  return props.paymentType === 'MONTHLY' ? '月付' : '年付'
})

const handleClose = () => {
  visible.value = false
  selectedMethod.value = ''
}

const handlePaymentSuccess = async () => {
  if (!selectedMethod.value) {
    ElMessage.warning('请选择支付方式')
    return
  }

  isProcessing.value = true

  try {
    // 模拟支付处理延迟
    await new Promise(resolve => setTimeout(resolve, 1500))

    ElMessage.success('支付成功！')
    
    // 触发成功事件
    emit('success', {
      planType: props.planType,
      paymentType: props.paymentType,
      paymentMethod: selectedMethod.value,
      amount: props.amount
    })

    handleClose()
  } catch (error) {
    ElMessage.error('支付失败，请重试')
  } finally {
    isProcessing.value = false
  }
}
</script>

<style scoped lang="scss">
.payment-dialog {
  padding: 10px;

  h3 {
    font-size: 16px;
    font-weight: 600;
    color: #1f2937;
    margin-bottom: 16px;
    padding-bottom: 8px;
    border-bottom: 2px solid #e5e7eb;
  }

  .order-info {
    margin-bottom: 24px;
    background: #f9fafb;
    padding: 16px;
    border-radius: 8px;

    .info-row {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 12px;

      &:last-child {
        margin-bottom: 0;
      }

      .label {
        font-size: 14px;
        color: #6b7280;
      }

      .value {
        font-size: 14px;
        color: #1f2937;
        font-weight: 500;

        &.price {
          font-size: 20px;
          color: #ef4444;
          font-weight: 700;
        }
      }
    }
  }

  .payment-method {
    margin-bottom: 24px;

    .method-options {
      display: flex;
      gap: 16px;

      .method-item {
        flex: 1;
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 20px;
        border: 2px solid #e5e7eb;
        border-radius: 12px;
        cursor: pointer;
        transition: all 0.3s ease;

        &:hover {
          border-color: #3b82f6;
          background: #eff6ff;
        }

        &.active {
          border-color: #3b82f6;
          background: linear-gradient(135deg, #eff6ff, #dbeafe);
          box-shadow: 0 4px 12px rgba(59, 130, 246, 0.2);
        }

        .method-icon {
          margin-bottom: 12px;

          &.alipay-icon svg {
            filter: drop-shadow(0 2px 4px rgba(18, 150, 219, 0.3));
          }

          &.wechat-icon svg {
            filter: drop-shadow(0 2px 4px rgba(9, 187, 7, 0.3));
          }
        }

        .method-name {
          font-size: 14px;
          font-weight: 600;
          color: #1f2937;
        }
      }
    }
  }

  .qrcode-section {
    margin-bottom: 24px;

    .qrcode-container {
      display: flex;
      justify-content: center;
      padding: 20px;
      background: #f9fafb;
      border-radius: 12px;

      .qrcode-placeholder {
        text-align: center;
        padding: 30px;
        background: white;
        border-radius: 12px;
        border: 2px dashed #d1d5db;

        .qrcode-icon {
          color: #9ca3af;
          margin-bottom: 16px;
          animation: pulse 2s ease-in-out infinite;
        }

        .qrcode-tip {
          font-size: 14px;
          color: #6b7280;
          margin-bottom: 8px;
        }

        .qrcode-amount {
          font-size: 24px;
          font-weight: 700;
          color: #ef4444;
          margin-bottom: 12px;
        }

        .qrcode-note {
          font-size: 12px;
          color: #9ca3af;
          margin: 0;
        }
      }
    }
  }

  .payment-actions {
    display: flex;
    justify-content: flex-end;
    gap: 12px;
    padding-top: 16px;
    border-top: 1px solid #e5e7eb;
  }
}

@keyframes pulse {
  0%, 100% {
    opacity: 1;
  }
  50% {
    opacity: 0.5;
  }
}
</style>
