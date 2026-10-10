<template>
  <div class="rehab-games">
    <!-- 头部 -->
    <div class="header">
      <h1 class="title">
        <span class="icon">🎮</span>
        康复游戏中心
      </h1>
      <router-link to="/arvr-health" class="btn-back">
        ← 返回
      </router-link>
    </div>

    <!-- 主要内容 -->
    <div class="content">
      <!-- 筛选器 -->
      <div class="filters">
        <div class="filter-group">
          <label>游戏类型：</label>
          <select v-model="filterType" class="filter-select">
            <option value="all">全部</option>
            <option value="puzzle">益智</option>
            <option value="action">动作</option>
            <option value="rhythm">节奏</option>
            <option value="balance">平衡</option>
            <option value="coordination">协调</option>
            <option value="memory">记忆</option>
          </select>
        </div>
        
        <div class="filter-group">
          <label>难度：</label>
          <select v-model="filterDifficulty" class="filter-select">
            <option value="all">全部</option>
            <option value="easy">简单</option>
            <option value="medium">中等</option>
            <option value="hard">困难</option>
          </select>
        </div>

        <div class="filter-group">
          <label>目标部位：</label>
          <input 
            v-model="filterBodyPart" 
            class="filter-input" 
            placeholder="如：上肢、下肢、手部..."
          >
        </div>
      </div>

      <!-- 游戏列表 -->
      <div class="games-grid">
        <div 
          v-for="game in filteredGames" 
          :key="game.id"
          class="game-card"
        >
          <div class="game-thumbnail">
            <img 
              :src="game.thumbnailUrl" 
              :alt="game.gameName"
              @error="handleImageError"
              @load="handleImageLoad"
              style="width: 100%; height: 100%; object-fit: cover; display: block;"
            >
            <div class="game-badges">
              <span class="badge vr-badge" v-if="game.isVrSupported">🥽 VR</span>
              <span class="badge ar-badge" v-if="game.isArSupported">📱 AR</span>
            </div>
            <div class="game-difficulty" :class="`difficulty-${game.difficultyLevel}`">
              {{ getDifficultyLabel(game.difficultyLevel) }}
            </div>
          </div>

          <div class="game-info">
            <h3 class="game-name">{{ game.gameName }}</h3>
            <p class="game-desc">{{ game.description }}</p>

            <div class="game-meta">
              <span class="meta-item">
                <span class="meta-icon">🎯</span>
                {{ game.targetBodyPart }}
              </span>
              <span class="meta-item">
                <span class="meta-icon">⏱️</span>
                {{ game.estimatedDuration }}分钟
              </span>
            </div>

            <div class="game-tags">
              <span v-for="(tag, index) in getGameTags(game.tags)" :key="index" class="tag">
                {{ tag }}
              </span>
            </div>

            <div class="game-stats">
              <span class="stat">⭐ {{ game.ratingAvg || '5.0' }}</span>
              <span class="stat">🎮 {{ game.playCount || 0 }}次游玩</span>
            </div>

            <div class="game-benefits" v-if="game.therapeuticBenefits">
              <strong>治疗益处：</strong>
              <p>{{ game.therapeuticBenefits }}</p>
            </div>

            <button class="btn-play" @click="playGame(game)">
              🎮 开始游戏
            </button>
          </div>
        </div>
      </div>

      <!-- 空状态 -->
      <div v-if="filteredGames.length === 0" class="empty-state">
        <div class="empty-icon">🎮</div>
        <p class="empty-text">没有找到符合条件的游戏</p>
      </div>
    </div>

    <!-- 游戏进行中界面 -->
    <div class="game-session" v-if="isPlaying">
      <div class="game-container" ref="gameContainer">
        <!-- 这里放置实际的游戏画面 -->
        <div class="game-screen">
          <h2>{{ currentGame.gameName }}</h2>
          <div class="game-score">
            <div class="score-item">
              <span class="score-label">得分</span>
              <span class="score-value">{{ gameScore }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">时间</span>
              <span class="score-value">{{ formattedGameTime }}</span>
            </div>
            <div class="score-item">
              <span class="score-label">准确率</span>
              <span class="score-value">{{ gameAccuracy }}%</span>
            </div>
          </div>
          <div class="game-instructions">
            {{ currentGame.instructions || '按照屏幕提示完成训练动作' }}
          </div>
        </div>
      </div>

      <div class="game-controls">
        <button class="control-btn" @click="pauseGame">
          {{ isGamePaused ? '继续' : '暂停' }}
        </button>
        <button class="control-btn" @click="endGame">
          结束游戏
        </button>
      </div>
    </div>

    <!-- 游戏结束总结 -->
    <div class="game-summary" v-if="showGameSummary">
      <div class="summary-card">
        <h2 class="summary-title">🎉 游戏完成！</h2>

        <div class="summary-content">
          <div class="final-score">
            <div class="score-circle">
              <div class="score-number">{{ gameScore }}</div>
              <div class="score-text">总分</div>
            </div>
          </div>

          <div class="summary-stats">
            <div class="stat-item">
              <span class="stat-label">游戏时长</span>
              <span class="stat-value">{{ Math.floor(gameTime / 60) }}分{{ gameTime % 60 }}秒</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">准确率</span>
              <span class="stat-value">{{ gameAccuracy }}%</span>
            </div>
            <div class="stat-item">
              <span class="stat-label">动作质量</span>
              <span class="stat-value">{{ gameQuality }}/100</span>
            </div>
          </div>

          <div class="achievements" v-if="achievements.length > 0">
            <h3>🏆 解锁成就</h3>
            <div class="achievement-list">
              <div v-for="(achievement, index) in achievements" :key="index" class="achievement-item">
                <span class="achievement-icon">{{ achievement.icon }}</span>
                <span class="achievement-name">{{ achievement.name }}</span>
              </div>
            </div>
          </div>

          <div class="improvement-tip">
            <h3>💡 改善建议</h3>
            <p>{{ improvementTip }}</p>
          </div>
        </div>

        <div class="summary-actions">
          <button class="btn-primary" @click="saveGameRecord">保存记录</button>
          <button class="btn-secondary" @click="playAgain">再玩一次</button>
          <button class="btn-secondary" @click="closeSummary">返回</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import axios from 'axios'

// 数据
const games = ref([])
const filterType = ref('all')
const filterDifficulty = ref('all')
const filterBodyPart = ref('')

// 游戏状态
const isPlaying = ref(false)
const isGamePaused = ref(false)
const showGameSummary = ref(false)
const currentGame = ref(null)
const gameContainer = ref(null)

// 游戏数据
const gameScore = ref(0)
const gameTime = ref(0)
const gameAccuracy = ref(0)
const gameQuality = ref(0)
const achievements = ref([])
const improvementTip = ref('')
let gameTimer = null

// 计算属性
const filteredGames = computed(() => {
  return games.value.filter(game => {
    const typeMatch = filterType.value === 'all' || game.gameType === filterType.value
    const difficultyMatch = filterDifficulty.value === 'all' || game.difficultyLevel === filterDifficulty.value
    const bodyPartMatch = !filterBodyPart.value || 
      (game.targetBodyPart && game.targetBodyPart.includes(filterBodyPart.value))
    return typeMatch && difficultyMatch && bodyPartMatch
  })
})

const formattedGameTime = computed(() => {
  const minutes = Math.floor(gameTime.value / 60)
  const seconds = gameTime.value % 60
  return `${minutes}:${seconds.toString().padStart(2, '0')}`
})

// 页面挂载
onMounted(async () => {
  await loadGames()
})

// 页面卸载
onUnmounted(() => {
  stopGameTimer()
})

// 加载游戏数据
const loadGames = async () => {
  try {
    const response = await axios.get('/api/arvr-health/games')
    games.value = response.data.data || []
  } catch (error) {
    console.error('加载游戏数据失败:', error)
    // 模拟数据
    games.value = [
      {
        id: 1,
        gameName: '水果忍者康复版',
        gameType: 'action',
        targetBodyPart: '上肢,手臂',
        difficultyLevel: 'easy',
        thumbnailUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjIyMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjNENBRjUwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyNCIgZmlsbD0iI0ZGRiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuawtOaenOW/jeWjq+W6t+WkjeeJiDwvdGV4dD48L3N2Zz4=',
        description: '通过切水果锻炼手臂力量和协调性',
        therapeuticBenefits: '提高手眼协调，增强肩关节活动范围',
        estimatedDuration: 10,
        isVrSupported: true,
        isArSupported: false,
        tags: '有趣,上肢训练,协调性',
        ratingAvg: 4.8,
        playCount: 5200
      },
      {
        id: 2,
        gameName: '平衡大师',
        gameType: 'balance',
        targetBodyPart: '全身,核心',
        difficultyLevel: 'medium',
        thumbnailUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjIyMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjMjE5NkYzIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyNCIgZmlsbD0iI0ZGRiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuW5s+ihoeWkp+W4 iTwvdGV4dD48L3N2Zz4=',
        description: '通过平衡挑战提高身体稳定性',
        therapeuticBenefits: '改善平衡感，增强核心肌群',
        estimatedDuration: 15,
        isVrSupported: true,
        isArSupported: false,
        tags: '平衡,核心,防跌倒',
        ratingAvg: 4.7,
        playCount: 3800
      },
      {
        id: 3,
        gameName: '记忆宫殿',
        gameType: 'memory',
        targetBodyPart: '认知,大脑',
        difficultyLevel: 'medium',
        thumbnailUrl: 'data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iNDAwIiBoZWlnaHQ9IjIyMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48cmVjdCB3aWR0aD0iMTAwJSIgaGVpZ2h0PSIxMDAlIiBmaWxsPSIjRkY5ODAwIi8+PHRleHQgeD0iNTAlIiB5PSI1MCUiIGZvbnQtZmFtaWx5PSJBcmlhbCIgZm9udC1zaXplPSIyNCIgZmlsbD0iI0ZGRiIgdGV4dC1hbmNob3I9Im1pZGRsZSIgZHk9Ii4zZW0iPuiusOW/huWuq+auvzwvdGV4dD48L3N2Zz4=',
        description: '通过记忆训练提高认知能力',
        therapeuticBenefits: '增强记忆力，延缓认知衰退',
        estimatedDuration: 20,
        isVrSupported: false,
        isArSupported: false,
        tags: '认知,记忆,老年友好',
        ratingAvg: 4.6,
        playCount: 2900
      }
    ]
  }
}

// 开始游戏
const playGame = (game) => {
  currentGame.value = game
  isPlaying.value = true
  isGamePaused.value = false
  gameScore.value = 0
  gameTime.value = 0
  gameAccuracy.value = 100
  startGameTimer()
  
  // 模拟游戏进行
  setTimeout(() => {
    gameScore.value = Math.floor(Math.random() * 500) + 500
    gameAccuracy.value = Math.floor(Math.random() * 20) + 80
  }, 1000)
}

// 开始计时
const startGameTimer = () => {
  gameTimer = setInterval(() => {
    if (!isGamePaused.value) {
      gameTime.value++
    }
  }, 1000)
}

// 停止计时
const stopGameTimer = () => {
  if (gameTimer) {
    clearInterval(gameTimer)
    gameTimer = null
  }
}

// 暂停游戏
const pauseGame = () => {
  isGamePaused.value = !isGamePaused.value
}

// 结束游戏
const endGame = () => {
  if (confirm('确定要结束游戏吗？')) {
    completeGame()
  }
}

// 完成游戏
const completeGame = () => {
  stopGameTimer()
  gameQuality.value = Math.floor(Math.random() * 20) + 80
  
  // 生成成就
  achievements.value = []
  if (gameScore.value > 800) {
    achievements.value.push({ icon: '🏆', name: '高分达人' })
  }
  if (gameAccuracy.value >= 90) {
    achievements.value.push({ icon: '🎯', name: '神枪手' })
  }
  
  // 生成改善建议
  if (gameAccuracy.value < 80) {
    improvementTip.value = '建议放慢动作速度，注重动作质量而非数量'
  } else if (gameQuality.value < 80) {
    improvementTip.value = '动作完成得很好！可以适当增加难度挑战自己'
  } else {
    improvementTip.value = '表现优秀！继续保持，建议尝试更高难度'
  }
  
  isPlaying.value = false
  showGameSummary.value = true
}

// 保存游戏记录
const saveGameRecord = async () => {
  try {
    await axios.post('/api/arvr-health/games/record', {
      userId: 1,
      gameId: currentGame.value.id,
      score: gameScore.value,
      durationMinutes: Math.floor(gameTime.value / 60),
      accuracyRate: gameAccuracy.value,
      movementQuality: gameQuality.value
    })
    alert('记录已保存！')
    closeSummary()
  } catch (error) {
    console.error('保存记录失败:', error)
    alert('保存失败，请重试')
  }
}

// 再玩一次
const playAgain = () => {
  showGameSummary.value = false
  playGame(currentGame.value)
}

// 关闭总结
const closeSummary = () => {
  showGameSummary.value = false
  currentGame.value = null
}

// 工具函数
const getDifficultyLabel = (level) => {
  const labels = {
    'easy': '简单',
    'medium': '中等',
    'hard': '困难'
  }
  return labels[level] || level
}

const getGameTags = (tags) => {
  if (!tags) return []
  return tags.split(',').map(t => t.trim()).slice(0, 3)
}

// 图片加载处理
const handleImageError = (event) => {
  console.log('游戏图片加载失败:', event.target.src)
  // 不隐藏图片，让浏览器显示默认的破损图片图标
}

const handleImageLoad = (event) => {
  console.log('游戏图片加载成功:', event.target.src)
}
</script>

<style scoped>
.rehab-games {
  min-height: 100vh;
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
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
  background: #30cfd0;
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #330867;
}

.content {
  max-width: 1400px;
  margin: 2rem auto;
  padding: 0 2rem;
}

/* 筛选器 */
.filters {
  display: flex;
  gap: 2rem;
  margin-bottom: 2rem;
  background: white;
  padding: 1.5rem;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  flex-wrap: wrap;
}

.filter-group {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.filter-group label {
  font-weight: 600;
  color: #2d3748;
}

.filter-select,
.filter-input {
  padding: 0.5rem 1rem;
  border: 2px solid #e2e8f0;
  border-radius: 8px;
  font-size: 0.95rem;
  transition: all 0.3s;
}

.filter-select:focus,
.filter-input:focus {
  outline: none;
  border-color: #30cfd0;
}

/* 游戏网格 */
.games-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(380px, 1fr));
  gap: 2rem;
}

.game-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: all 0.3s;
}

.game-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.2);
}

.game-thumbnail {
  position: relative;
  height: 220px;
  background: #f3f4f6;
  overflow: hidden;
}

.game-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  display: block;
  transition: transform 0.3s ease;
  background: white;
}

.game-card:hover .game-thumbnail img {
  transform: scale(1.05);
}

.game-thumbnail img[src=""], 
.game-thumbnail img:not([src]) {
  display: none;
}

.game-badges {
  position: absolute;
  top: 15px;
  left: 15px;
  display: flex;
  gap: 0.5rem;
  z-index: 10;
}

.badge {
  padding: 0.3rem 0.6rem;
  border-radius: 12px;
  font-size: 0.75rem;
  font-weight: 600;
}

.vr-badge {
  background: #667eea;
  color: white;
}

.ar-badge {
  background: #f093fb;
  color: white;
}

.game-difficulty {
  position: absolute;
  top: 15px;
  right: 15px;
  padding: 0.4rem 0.8rem;
  border-radius: 12px;
  font-size: 0.85rem;
  font-weight: 600;
  z-index: 10;
}

.difficulty-easy {
  background: #68d391;
  color: white;
}

.difficulty-medium {
  background: #f6ad55;
  color: white;
}

.difficulty-hard {
  background: #fc8181;
  color: white;
}

.game-info {
  padding: 1.5rem;
}

.game-name {
  font-size: 1.3rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.game-desc {
  color: #718096;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.game-meta {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
  color: #4a5568;
  font-size: 0.9rem;
}

.meta-item {
  display: flex;
  align-items: center;
  gap: 0.3rem;
}

.game-tags {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tag {
  padding: 0.3rem 0.8rem;
  background: #edf2f7;
  color: #4a5568;
  border-radius: 12px;
  font-size: 0.85rem;
}

.game-stats {
  display: flex;
  gap: 1.5rem;
  margin-bottom: 1rem;
  color: #a0aec0;
  font-size: 0.9rem;
}

.game-benefits {
  background: #f7fafc;
  padding: 1rem;
  border-radius: 10px;
  margin-bottom: 1rem;
  font-size: 0.9rem;
  line-height: 1.5;
}

.game-benefits strong {
  color: #30cfd0;
}

.btn-play {
  width: 100%;
  padding: 0.75rem;
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-play:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(48, 207, 208, 0.4);
}

/* 游戏会话 */
.game-session {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.95);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.game-container {
  width: 90%;
  max-width: 1200px;
  height: 70vh;
  background: white;
  border-radius: 20px;
  overflow: hidden;
  margin-bottom: 2rem;
}

.game-screen {
  height: 100%;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  background: linear-gradient(135deg, #f7fafc 0%, #edf2f7 100%);
}

.game-score {
  display: flex;
  gap: 3rem;
  margin: 2rem 0;
}

.score-item {
  display: flex;
  flex-direction: column;
  align-items: center;
}

.score-label {
  color: #718096;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.score-value {
  font-size: 2.5rem;
  font-weight: 700;
  color: #30cfd0;
}

.game-instructions {
  text-align: center;
  color: #4a5568;
  font-size: 1.1rem;
  max-width: 600px;
  line-height: 1.6;
}

.game-controls {
  display: flex;
  gap: 1rem;
}

.control-btn {
  padding: 1rem 2rem;
  background: white;
  color: #2d3748;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.control-btn:hover {
  transform: scale(1.05);
}

/* 游戏总结 */
.game-summary {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
}

.summary-card {
  background: white;
  border-radius: 20px;
  padding: 3rem;
  max-width: 700px;
  width: 90%;
  max-height: 90vh;
  overflow-y: auto;
}

.summary-title {
  text-align: center;
  font-size: 2rem;
  color: #2d3748;
  margin-bottom: 2rem;
}

.final-score {
  display: flex;
  justify-content: center;
  margin-bottom: 2rem;
}

.score-circle {
  width: 150px;
  height: 150px;
  border-radius: 50%;
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  color: white;
}

.score-number {
  font-size: 3rem;
  font-weight: 700;
}

.score-text {
  font-size: 1rem;
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.stat-item {
  text-align: center;
  padding: 1rem;
  background: #f7fafc;
  border-radius: 10px;
}

.stat-label {
  display: block;
  color: #718096;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.stat-value {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
  color: #30cfd0;
}

.achievements {
  margin-bottom: 2rem;
}

.achievement-list {
  display: flex;
  flex-wrap: wrap;
  gap: 1rem;
}

.achievement-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: #ffd700;
  border-radius: 20px;
  font-weight: 600;
}

.improvement-tip {
  background: #f7fafc;
  padding: 1.5rem;
  border-radius: 10px;
  margin-bottom: 2rem;
}

.improvement-tip h3 {
  margin-bottom: 0.5rem;
  color: #2d3748;
}

.improvement-tip p {
  color: #4a5568;
  line-height: 1.6;
}

.summary-actions {
  display: flex;
  gap: 1rem;
}

.btn-primary,
.btn-secondary {
  flex: 1;
  padding: 1rem;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #30cfd0 0%, #330867 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(48, 207, 208, 0.4);
}

.btn-secondary {
  background: #edf2f7;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

/* 空状态 */
.empty-state {
  text-align: center;
  padding: 4rem 2rem;
}

.empty-icon {
  font-size: 5rem;
  margin-bottom: 1rem;
  opacity: 0.5;
}

.empty-text {
  color: rgba(255, 255, 255, 0.8);
  font-size: 1.2rem;
}

/* 响应式 */
@media (max-width: 768px) {
  .games-grid {
    grid-template-columns: 1fr;
  }
  
  .filters {
    flex-direction: column;
  }
  
  .summary-stats {
    grid-template-columns: 1fr;
  }
  
  .summary-actions {
    flex-direction: column;
  }
}
</style>

