<template>
  <div class="login-page">
    <!-- 左侧装饰区域 -->
    <div class="login-left-view">
      <div class="logo">
        <img src="@/assets/images/login-medical-logo.png" alt="灵枢智康医疗标志" class="logo-icon">
        <h1 class="logo-title">灵枢智康 | AI多模态智慧健康管理平台</h1>
      </div>

      <div class="left-img">
        <svg viewBox="0 0 400 300" class="illustration-svg">
          <!-- 医疗主题插画 -->
          <defs>
            <linearGradient id="grad1" x1="0%" y1="0%" x2="100%" y2="100%">
              <stop offset="0%" style="stop-color:#4cd1c0;stop-opacity:1" />
              <stop offset="100%" style="stop-color:#5bc0eb;stop-opacity:1" />
            </linearGradient>
          </defs>
          <!-- 心电图线条 -->
          <polyline fill="none" stroke="url(#grad1)" stroke-width="3" 
            points="20,150 60,150 80,100 100,200 120,120 140,180 160,150 380,150"
            class="heartbeat-line"/>
          <!-- 医疗十字 -->
          <rect x="170" y="80" width="60" height="20" rx="4" fill="url(#grad1)" opacity="0.8"/>
          <rect x="190" y="60" width="20" height="60" rx="4" fill="url(#grad1)" opacity="0.8"/>
          <!-- 装饰圆点 -->
          <circle cx="50" cy="80" r="8" fill="#4cd1c0" opacity="0.6"/>
          <circle cx="350" cy="100" r="12" fill="#5bc0eb" opacity="0.5"/>
          <circle cx="320" cy="220" r="6" fill="#8b93f3" opacity="0.7"/>
        </svg>
      </div>

      <div class="text-wrap">
        <h1>专业智能问诊</h1>
        <p>贴心健康管理，开启智慧医疗新体验</p>
      </div>

      <!-- 几何装饰元素 -->
      <div class="geometric-decorations">
        <div class="geo-element circle-outline animate-fade-in-up"></div>
        <div class="geo-element square-rotated animate-fade-in-left"></div>
        <div class="geo-element circle-small animate-fade-in-up"></div>
        <div class="geo-element square-bottom-right animate-fade-in-right"></div>
        <div class="geo-element bg-bubble animate-scale-in"></div>
        <div class="geo-element circle-top-right animate-fade-in-down"></div>
        <div class="geo-element dot dot-top-left animate-bounce-in"></div>
        <div class="geo-element dot dot-top-right animate-bounce-in"></div>
        <div class="geo-element dot dot-center-right animate-bounce-in"></div>
        <div class="squares-group">
          <i class="geo-element square square-blue animate-fade-in-left-rotated"></i>
          <i class="geo-element square square-pink animate-fade-in-left-rotated"></i>
          <i class="geo-element square square-purple animate-fade-in-left"></i>
        </div>
      </div>
    </div>

    <!-- 右侧登录区域 -->
    <div class="login-right-view">
      <div class="auth-right-wrap">
        <div class="form" :class="{ 'register-mode': isRegisterMode }">
          <!-- 登录表单 -->
          <template v-if="!isRegisterMode">
            <h3 class="title">欢迎回来</h3>
            <p class="sub-title">请输入您的账号信息登录系统</p>
            
            <el-form 
              ref="loginFormRef"
              :model="loginForm"
              :rules="loginRules"
              @keyup.enter="handleLogin"
              class="login-form"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="loginForm.username"
                  placeholder="请输入用户名"
                  size="large"
                  :prefix-icon="User"
                />
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input
                  v-model="loginForm.password"
                  type="password"
                  placeholder="请输入密码"
                  size="large"
                  show-password
                  :prefix-icon="Lock"
                />
              </el-form-item>

              <!-- 滑动验证 -->
              <div class="drag-verify-wrap">
                <div 
                  class="drag-verify"
                  :class="{ 'is-success': isPassing, 'is-error': !isPassing && isClickPass }"
                >
                  <div class="drag-bar" :style="{ width: dragWidth + 'px' }"></div>
                  <div 
                    class="drag-handler"
                    :style="{ left: dragWidth + 'px' }"
                    @mousedown="startDrag"
                    @touchstart="startDrag"
                  >
                    <el-icon v-if="!isPassing"><Right /></el-icon>
                    <el-icon v-else class="success-icon"><Check /></el-icon>
                  </div>
                  <span class="drag-text">{{ isPassing ? '验证成功' : '请拖动滑块完成验证' }}</span>
                </div>
                <p v-if="!isPassing && isClickPass" class="verify-error">请完成滑动验证</p>
              </div>

              <div class="form-options">
                <el-checkbox v-model="rememberMe">记住密码</el-checkbox>
                <a href="#" class="forgot-link">忘记密码？</a>
              </div>

              <el-button 
                type="primary" 
                size="large" 
                class="submit-btn"
                :loading="isLoading"
                @click="handleLogin"
              >
                {{ isLoading ? '登录中...' : '登 录' }}
              </el-button>

              <div class="switch-mode">
                <span>还没有账号？</span>
                <a href="#" @click.prevent="switchMode(true)">立即注册</a>
              </div>
            </el-form>
          </template>

          <!-- 注册表单 -->
          <template v-else>
            <h3 class="title">创建账号</h3>
            <p class="sub-title">加入我们，开启智能健康管理之旅</p>
            
            <el-form 
              ref="registerFormRef"
              :model="registerForm"
              :rules="registerRules"
              @keyup.enter="handleRegister"
              class="login-form"
            >
              <el-form-item prop="username">
                <el-input
                  v-model="registerForm.username"
                  placeholder="请输入用户名（3-20个字符）"
                  size="large"
                  :prefix-icon="User"
                />
              </el-form-item>
              
              <el-form-item prop="password">
                <el-input
                  v-model="registerForm.password"
                  type="password"
                  placeholder="请输入密码（6-20个字符）"
                  size="large"
                  show-password
                  :prefix-icon="Lock"
                />
              </el-form-item>

              <el-form-item prop="confirmPassword">
                <el-input
                  v-model="registerForm.confirmPassword"
                  type="password"
                  placeholder="请确认密码"
                  size="large"
                  show-password
                  :prefix-icon="Lock"
                />
              </el-form-item>

              <div class="form-options">
                <el-checkbox v-model="agreeTerms">
                  我已阅读并同意 <a href="#" class="link">《医疗隐私协议》</a>
                </el-checkbox>
              </div>

              <el-button 
                type="primary" 
                size="large" 
                class="submit-btn"
                :loading="isLoading"
                @click="handleRegister"
              >
                {{ isLoading ? '注册中...' : '立即注册' }}
              </el-button>

              <div class="switch-mode">
                <span>已有账号？</span>
                <a href="#" @click.prevent="switchMode(false)">立即登录</a>
              </div>
            </el-form>
          </template>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, type FormInstance, type FormRules } from 'element-plus'
import { User, Lock, Right, Check } from '@element-plus/icons-vue'

const router = useRouter()

// 模式切换
const isRegisterMode = ref(false)
const isLoading = ref(false)
const rememberMe = ref(false)
const agreeTerms = ref(false)

// 滑动验证相关
const isPassing = ref(false)
const isClickPass = ref(false)
const dragWidth = ref(0)
const isDragging = ref(false)
const startX = ref(0)
const containerLeft = ref(0)
const maxDragWidth = ref(0) // 滑动条最大宽度，动态计算

// 登录表单
const loginFormRef = ref<FormInstance>()
const loginForm = reactive({
  username: '',
  password: ''
})

const loginRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, message: '用户名至少3个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, message: '密码至少6个字符', trigger: 'blur' }
  ]
})

// 注册表单
const registerFormRef = ref<FormInstance>()
const registerForm = reactive({
  username: '',
  password: '',
  confirmPassword: ''
})

const validateConfirmPassword = (rule: any, value: any, callback: any) => {
  if (value !== registerForm.password) {
    callback(new Error('两次输入的密码不一致'))
  } else {
    callback()
  }
}

const registerRules = reactive<FormRules>({
  username: [
    { required: true, message: '请输入用户名', trigger: 'blur' },
    { min: 3, max: 20, message: '用户名长度为3-20个字符', trigger: 'blur' }
  ],
  password: [
    { required: true, message: '请输入密码', trigger: 'blur' },
    { min: 6, max: 20, message: '密码长度为6-20个字符', trigger: 'blur' }
  ],
  confirmPassword: [
    { required: true, message: '请确认密码', trigger: 'blur' },
    { validator: validateConfirmPassword, trigger: 'blur' }
  ]
})

// 滑动验证方法
const startDrag = (e: MouseEvent | TouchEvent) => {
  if (isPassing.value) return
  isDragging.value = true
  const target = (e.target as HTMLElement).closest('.drag-verify') as HTMLElement
  if (target) {
    const rect = target.getBoundingClientRect()
    containerLeft.value = rect.left
    // 动态计算最大拖动宽度 = 容器宽度 - 滑块宽度(44px)
    maxDragWidth.value = rect.width - 44
  }
  startX.value = 'touches' in e ? e.touches[0].clientX : e.clientX
  document.addEventListener('mousemove', onDrag)
  document.addEventListener('mouseup', endDrag)
  document.addEventListener('touchmove', onDrag)
  document.addEventListener('touchend', endDrag)
}

const onDrag = (e: MouseEvent | TouchEvent) => {
  if (!isDragging.value) return
  const currentX = 'touches' in e ? e.touches[0].clientX : e.clientX
  // 计算相对于容器的位置，减去滑块自身宽度的一半以居中
  let newWidth = currentX - containerLeft.value - 22 // 22是滑块宽度的一半(44/2)
  if (newWidth < 0) newWidth = 0
  if (newWidth > maxDragWidth.value) newWidth = maxDragWidth.value
  dragWidth.value = newWidth
}

const endDrag = () => {
  isDragging.value = false
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', endDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', endDrag)
  
  if (dragWidth.value >= maxDragWidth.value - 10) {
    isPassing.value = true
    dragWidth.value = maxDragWidth.value
  } else {
    dragWidth.value = 0
  }
}

const resetDragVerify = () => {
  isPassing.value = false
  isClickPass.value = false
  dragWidth.value = 0
}

// 模式切换方法
const switchMode = (registerMode: boolean) => {
  isRegisterMode.value = registerMode
  loginForm.username = ''
  loginForm.password = ''
  registerForm.username = ''
  registerForm.password = ''
  registerForm.confirmPassword = ''
  resetDragVerify()
}

// 登录处理
const handleLogin = async () => {
  if (!loginFormRef.value) return
  
  try {
    const valid = await loginFormRef.value.validate()
    if (!valid) return
    
    // 验证滑动
    if (!isPassing.value) {
      isClickPass.value = true
      return
    }
    
    isLoading.value = true
    
    const response = await fetch('/api/user/login', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: loginForm.username,
        password: loginForm.password
      })
    })
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    
    const result = await response.json()
    
    if (result.code === '200') {
      ElMessage.success('登录成功')
      localStorage.setItem('username', loginForm.username)
      if (result.data && result.data.id != null) {
        localStorage.setItem('userId', String(result.data.id))
      }
      if (result.data && result.data.role) {
        localStorage.setItem('userRole', result.data.role)
      }
      if (result.data && result.data.token) {
        localStorage.setItem('token', result.data.token)
      } else {
        localStorage.setItem('token', 'default-token')
      }
      
      // 所有角色登录后统一进入主页面，管理员可从主页进入管理后台
      setTimeout(() => {
        router.push('/home')
      }, 1000)
    } else {
      throw new Error(result.msg || '登录失败')
    }
  } catch (e: any) {
    ElMessage.error(`登录失败: ${e.message || '未知错误'}`)
    resetDragVerify()
  } finally {
    isLoading.value = false
  }
}

// 注册处理
const handleRegister = async () => {
  if (!registerFormRef.value) return
  
  try {
    const valid = await registerFormRef.value.validate()
    if (!valid) return
    
    if (!agreeTerms.value) {
      ElMessage.error('请同意用户协议')
      return
    }
    
    isLoading.value = true
    
    const response = await fetch('/api/user/register', {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({
        username: registerForm.username,
        password: registerForm.password
      })
    })
    
    if (!response.ok) {
      throw new Error(`HTTP ${response.status}: ${response.statusText}`)
    }
    
    const result = await response.json()
    
    if (result.code === '200') {
      ElMessage.success('注册成功，请登录')
      const registeredUsername = registerForm.username
      setTimeout(() => {
        switchMode(false)
        setTimeout(() => {
          loginForm.username = registeredUsername
        }, 100)
      }, 1500)
    } else {
      throw new Error(result.msg || '注册失败')
    }
  } catch (e: any) {
    ElMessage.error(`注册失败: ${e.message || '未知错误'}`)
  } finally {
    isLoading.value = false
  }
}

onUnmounted(() => {
  document.removeEventListener('mousemove', onDrag)
  document.removeEventListener('mouseup', endDrag)
  document.removeEventListener('touchmove', onDrag)
  document.removeEventListener('touchend', endDrag)
})
</script>

<style scoped>
/* 登录页面主容器 */
.login-page {
  display: flex;
  width: 100vw;
  height: 100vh;
  overflow: hidden;
}

/* 左侧装饰区域 */
.login-left-view {
  position: relative;
  box-sizing: border-box;
  width: 60%;
  height: 100%;
  padding: 20px;
  overflow: hidden;
  background: linear-gradient(135deg, 
    rgba(76, 209, 192, 0.15) 0%, 
    rgba(91, 192, 235, 0.12) 50%, 
    rgba(139, 147, 243, 0.1) 100%);
}

.logo {
  position: relative;
  z-index: 100;
  display: flex;
  align-items: center;
}

.logo-icon {
  width: 50px;
  height: 50px;
  border-radius: 12px;
  object-fit: cover;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.logo-title {
  margin-left: 12px;
  font-size: 20px;
  font-weight: 500;
  color: #2c3e50;
}

.left-img {
  position: absolute;
  inset: 0 0 15%;
  z-index: 10;
  width: 50%;
  margin: auto;
  animation: slideInLeft 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

.illustration-svg {
  width: 100%;
  height: 100%;
}

.heartbeat-line {
  stroke-dasharray: 500;
  stroke-dashoffset: 500;
  animation: drawLine 3s ease-in-out infinite;
}

@keyframes drawLine {
  0% { stroke-dashoffset: 500; }
  50% { stroke-dashoffset: 0; }
  100% { stroke-dashoffset: -500; }
}

.text-wrap {
  position: absolute;
  bottom: 80px;
  width: 100%;
  text-align: center;
  animation: slideInLeft 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

.text-wrap h1 {
  font-size: 28px;
  font-weight: 500;
  color: #2c3e50;
  margin: 0;
}

.text-wrap p {
  margin-top: 12px;
  font-size: 15px;
  color: #7f8c8d;
}

/* 几何装饰元素 */
.geometric-decorations {
  position: absolute;
  inset: 0;
  pointer-events: none;
}

.geo-element {
  position: absolute;
  opacity: 0;
  animation-fill-mode: forwards;
  animation-duration: 0.8s;
  animation-timing-function: cubic-bezier(0.25, 0.46, 0.45, 0.94);
}

.circle-outline {
  top: 10%;
  left: 25%;
  width: 42px;
  height: 42px;
  border: 2px solid rgba(76, 209, 192, 0.3);
  border-radius: 50%;
}

.square-rotated {
  top: 50%;
  left: 16%;
  width: 60px;
  height: 60px;
  background-color: rgba(76, 209, 192, 0.1);
  transform: rotate(-25deg);
}

.circle-small {
  bottom: 26%;
  left: 30%;
  width: 18px;
  height: 18px;
  background-color: rgba(76, 209, 192, 0.3);
  border-radius: 50%;
}

.circle-top-right {
  top: 3%;
  right: 3%;
  z-index: 100;
  width: 50px;
  height: 50px;
  background: rgba(76, 209, 192, 0.2);
  border-radius: 50%;
}

.square-bottom-right {
  right: 10%;
  bottom: 10%;
  width: 50px;
  height: 50px;
  background-color: rgba(91, 192, 235, 0.2);
  transform: rotate(45deg);
}

.bg-bubble {
  top: -120px;
  right: -120px;
  width: 360px;
  height: 360px;
  background-color: rgba(76, 209, 192, 0.08);
  border-radius: 50%;
}

.dot {
  width: 14px;
  height: 14px;
  background-color: rgba(76, 209, 192, 0.4);
  border-radius: 50%;
}

.dot-top-left {
  top: 140px;
  left: 100px;
}

.dot-top-right {
  top: 140px;
  right: 120px;
}

.dot-center-right {
  top: 46%;
  right: 22%;
  background-color: rgba(91, 192, 235, 0.3);
}

.squares-group {
  position: absolute;
  bottom: 18px;
  left: 20px;
  width: 140px;
  height: 140px;
}

.square {
  position: absolute;
  display: block;
  border-radius: 8px;
  box-shadow: 0 8px 24px rgba(64, 87, 167, 0.12);
}

.square-blue {
  top: 12px;
  left: 30px;
  z-index: 2;
  width: 50px;
  height: 50px;
  background-color: rgba(76, 209, 192, 0.3);
  transform: rotate(-10deg);
}

.square-pink {
  top: 30px;
  left: 48px;
  z-index: 1;
  width: 70px;
  height: 70px;
  background-color: rgba(91, 192, 235, 0.2);
  transform: rotate(10deg);
}

.square-purple {
  top: 66px;
  left: 86px;
  z-index: 3;
  width: 32px;
  height: 32px;
  background-color: rgba(139, 147, 243, 0.4);
}

/* 动画类 */
.animate-fade-in-up {
  animation-name: fadeInUp;
}

.animate-fade-in-down {
  animation-name: fadeInDown;
}

.animate-fade-in-left {
  animation-name: fadeInLeft;
}

.animate-fade-in-right {
  animation-name: fadeInRight;
}

.animate-scale-in {
  animation-name: scaleIn;
  animation-duration: 1.2s;
}

.animate-bounce-in {
  animation-name: bounceIn;
  animation-duration: 0.6s;
}

.animate-fade-in-left-rotated {
  animation-name: fadeInLeftRotated;
}

@keyframes fadeInUp {
  from { opacity: 0; transform: translateY(30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInDown {
  from { opacity: 0; transform: translateY(-30px); }
  to { opacity: 1; transform: translateY(0); }
}

@keyframes fadeInLeft {
  from { opacity: 0; transform: translateX(-30px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes fadeInRight {
  from { opacity: 0; transform: translateX(30px); }
  to { opacity: 1; transform: translateX(0); }
}

@keyframes fadeInLeftRotated {
  from { opacity: 0; transform: translateX(-30px) rotate(-10deg); }
  to { opacity: 1; transform: translateX(0) rotate(-10deg); }
}

@keyframes scaleIn {
  from { opacity: 0; transform: scale(0.8); }
  to { opacity: 1; transform: scale(1); }
}

@keyframes bounceIn {
  0% { opacity: 0; transform: scale(0.3); }
  50% { opacity: 1; transform: scale(1.05); }
  70% { transform: scale(0.9); }
  100% { opacity: 1; transform: scale(1); }
}

@keyframes slideInLeft {
  from { opacity: 0; transform: translateX(-30px); }
  to { opacity: 1; transform: translateX(0); }
}

/* 右侧登录区域 */
.login-right-view {
  position: relative;
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #fff;
}

.auth-right-wrap {
  width: 400px;
  max-width: 90%;
  animation: slideInRight 0.6s cubic-bezier(0.25, 0.46, 0.45, 0.94) forwards;
}

@keyframes slideInRight {
  from { opacity: 0; transform: translateX(30px); }
  to { opacity: 1; transform: translateX(0); }
}

.form {
  padding: 40px 0;
}

.title {
  font-size: 32px;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 10px 0;
}

.sub-title {
  font-size: 14px;
  color: #7f8c8d;
  margin: 0 0 30px 0;
}

.login-form {
  margin-top: 25px;
}

.login-form :deep(.el-input__wrapper) {
  height: 44px;
  border-radius: 8px;
  box-shadow: 0 0 0 1px #dcdfe6 inset;
  transition: all 0.3s;
}

.login-form :deep(.el-input__wrapper:hover) {
  box-shadow: 0 0 0 1px #4cd1c0 inset;
}

.login-form :deep(.el-input__wrapper.is-focus) {
  box-shadow: 0 0 0 1px #4cd1c0 inset;
}

.login-form :deep(.el-form-item) {
  margin-bottom: 22px;
}

/* 滑动验证 */
.drag-verify-wrap {
  margin: 20px 0;
}

.drag-verify {
  position: relative;
  height: 44px;
  background: #f5f7fa;
  border-radius: 8px;
  overflow: hidden;
  user-select: none;
  border: 1px solid transparent;
  transition: all 0.3s;
}

.drag-verify.is-error {
  border-color: #f56c6c;
}

.drag-verify.is-success {
  background: linear-gradient(90deg, #4cd1c0, #5bc0eb);
}

.drag-verify.is-success .drag-bar {
  display: none;
}

.drag-verify.is-success .drag-handler {
  display: none;
}

.drag-bar {
  position: absolute;
  left: 0;
  top: 0;
  height: 100%;
  background: linear-gradient(90deg, #4cd1c0, #5bc0eb);
  border-radius: 8px 0 0 8px;
  transition: none;
}

.drag-handler {
  position: absolute;
  top: 0;
  width: 44px;
  height: 44px;
  background: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: grab;
  z-index: 10;
  transition: none;
}

.drag-handler:active {
  cursor: grabbing;
}

.drag-handler .el-icon {
  font-size: 18px;
  color: #4cd1c0;
}

.drag-handler .success-icon {
  color: #67c23a;
}

.drag-text {
  position: absolute;
  width: 100%;
  text-align: center;
  line-height: 44px;
  font-size: 14px;
  color: #909399;
  pointer-events: none;
}

.is-success .drag-text {
  color: #fff;
  font-weight: 500;
}

.verify-error {
  font-size: 12px;
  color: #f56c6c;
  margin-top: 8px;
}

/* 表单选项 */
.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin: 20px 0;
}

.form-options :deep(.el-checkbox__label) {
  color: #606266;
  font-size: 14px;
}

.forgot-link,
.link {
  color: #4cd1c0;
  text-decoration: none;
  font-size: 14px;
  transition: color 0.3s;
}

.forgot-link:hover,
.link:hover {
  color: #3ab8a8;
}

/* 提交按钮 */
.submit-btn {
  width: 100%;
  height: 48px;
  font-size: 16px;
  font-weight: 500;
  border-radius: 8px;
  background: linear-gradient(135deg, #4cd1c0, #5bc0eb);
  border: none;
  transition: all 0.3s;
}

.submit-btn:hover {
  background: linear-gradient(135deg, #3ab8a8, #4fb3dd);
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(76, 209, 192, 0.4);
}

/* 切换模式 */
.switch-mode {
  text-align: center;
  margin-top: 24px;
  font-size: 14px;
  color: #909399;
}

.switch-mode a {
  color: #4cd1c0;
  text-decoration: none;
  font-weight: 500;
  margin-left: 4px;
}

.switch-mode a:hover {
  color: #3ab8a8;
}

/* 响应式适配 */
@media (max-width: 1180px) {
  .login-left-view {
    display: none;
  }
  
  .login-right-view {
    width: 100%;
  }
}

@media (max-width: 480px) {
  .auth-right-wrap {
    padding: 0 20px;
  }
  
  .title {
    font-size: 26px;
  }
  
  .form {
    padding: 30px 0;
  }
}
</style>
