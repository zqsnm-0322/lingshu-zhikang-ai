<template>
  <div class="rehabilitation">
    <div class="header">
      <h1 class="title">
        <span class="icon">🏃</span>
        VR康复训练
      </h1>
      <router-link to="/arvr-health" class="btn-back">
        ← 返回
      </router-link>
    </div>

    <div class="content">
      <div class="coming-soon">
        <div class="icon-large">🚧</div>
        <h2>功能开发中</h2>
        <p class="description">VR康复训练功能正在开发中，敬请期待！</p>
        
        <div class="feature-preview">
          <h3>即将上线的功能：</h3>
          <ul>
            <li>🏥 针对不同疾病的康复训练计划</li>
            <li>🎯 中风康复训练</li>
            <li>🦴 运动损伤康复</li>
            <li>🤸 骨折术后康复</li>
            <li>📊 实时动作追踪和纠正</li>
            <li>📈 康复进度统计和分析</li>
            <li>👨‍⚕️ 专业医生在线指导</li>
          </ul>
        </div>

        <div class="tip-box">
          <p>💡 提示：您可以先体验"康复游戏中心"，通过游戏化的方式进行康复训练！</p>
        </div>

        <div class="action-buttons">
          <button class="btn-primary" @click="goToGames">体验康复游戏</button>
          <button class="btn-secondary" @click="goBack">返回主页</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

const router = useRouter()

const goBack = () => {
  router.push('/arvr-health')
}

const goToGames = () => {
  router.push('/arvr-health/games')
}
</script>

<style scoped>
.rehabilitation {
  min-height: 100vh;
  height: 100vh;
  background: #4facfe !important;
  background-image: none !important;
  overflow-y: auto;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 2rem;
  background: rgba(255, 255, 255, 0.95);
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

.btn-back {
  padding: 0.6rem 1.2rem;
  background: #4facfe;
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #00f2fe;
  transform: translateY(-2px);
}

.content {
  display: flex;
  align-items: center;
  justify-content: center;
  min-height: calc(100vh - 90px);
  padding: 2rem;
  width: 100%;
  box-sizing: border-box;
}

.coming-soon {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border-radius: 20px;
  padding: 4rem 3rem;
  max-width: 700px;
  width: 100%;
  text-align: center;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.2);
}

.icon-large {
  font-size: 6rem;
  margin-bottom: 1.5rem;
  animation: bounce 2s infinite;
}

@keyframes bounce {
  0%, 100% { transform: translateY(0); }
  50% { transform: translateY(-20px); }
}

.coming-soon h2 {
  font-size: 2.5rem;
  color: #2d3748;
  margin-bottom: 1rem;
}

.description {
  font-size: 1.2rem;
  color: #718096;
  margin-bottom: 3rem;
}

.feature-preview {
  background: #f7fafc;
  border-radius: 15px;
  padding: 2rem;
  margin-bottom: 2rem;
  text-align: left;
}

.feature-preview h3 {
  color: #2d3748;
  margin-bottom: 1.5rem;
  text-align: center;
}

.feature-preview ul {
  list-style: none;
  padding: 0;
}

.feature-preview li {
  padding: 0.75rem 0;
  color: #4a5568;
  font-size: 1.1rem;
  border-bottom: 1px solid #e2e8f0;
}

.feature-preview li:last-child {
  border-bottom: none;
}

.tip-box {
  background: #e6f7ff;
  border-left: 4px solid #4facfe;
  padding: 1.5rem;
  border-radius: 8px;
  margin-bottom: 2rem;
}

.tip-box p {
  margin: 0;
  color: #2d3748;
  font-size: 1rem;
}

.action-buttons {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.btn-primary,
.btn-secondary {
  padding: 1rem 2rem;
  border: none;
  border-radius: 25px;
  font-size: 1.1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: #4facfe;
  color: white;
}

.btn-primary:hover {
  transform: translateY(-3px);
  box-shadow: 0 10px 30px rgba(79, 172, 254, 0.4);
}

.btn-secondary {
  background: #edf2f7;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #e2e8f0;
}
</style>

