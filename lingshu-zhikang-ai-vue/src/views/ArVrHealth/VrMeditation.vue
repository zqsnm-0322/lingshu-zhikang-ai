<template>
  <div class="vr-meditation">
    <!-- 头部 -->
    <div class="header">
      <h1 class="title">
        <img src="https://api.iconify.design/mdi:meditation.svg?color=%232d3748" class="icon" alt="冥想" />
        VR冥想放松
      </h1>
      <router-link to="/arvr-health" class="btn-back">
        ← 返回
      </router-link>
    </div>

    <!-- 主要内容 -->
    <div class="content" v-if="!isMeditating">
      <!-- 场景分类 -->
      <div class="category-tabs">
        <button 
          v-for="category in categories" 
          :key="category.value"
          class="category-tab"
          :class="{ active: selectedCategory === category.value }"
          @click="selectedCategory = category.value"
        >
          <img :src="category.icon" class="tab-icon" :alt="category.label" />
          <span class="tab-label">{{ category.label }}</span>
        </button>
      </div>

      <!-- 场景列表 -->
      <div class="scenes-grid">
        <div 
          v-for="scene in filteredScenes" 
          :key="scene.id"
          class="scene-card"
          @click="selectScene(scene)"
        >
          <div class="scene-image">
            <img 
              :src="scene.thumbnailUrl" 
              :alt="scene.sceneName"
              class="scene-bg-image"
              @error="handleImageError"
              @load="handleImageLoad"
            />
            <div class="scene-overlay">
              <div class="scene-icon">{{ getSceneIcon(scene.sceneType) }}</div>
            </div>
            <div class="scene-duration">{{ scene.durationMinutes }}分钟</div>
            <div class="scene-badge" v-if="scene.difficultyLevel">
              {{ getDifficultyLabel(scene.difficultyLevel) }}
            </div>
          </div>
          <div class="scene-info">
            <h3 class="scene-name">{{ scene.sceneName }}</h3>
            <p class="scene-desc">{{ scene.description }}</p>
            <div class="scene-tags">
              <span v-for="(tag, index) in getSceneTags(scene.tags)" :key="index" class="tag">
                {{ tag }}
              </span>
            </div>
            <div class="scene-stats">
              <span class="stat"><img src="https://api.iconify.design/mdi:star.svg?color=%23fbbf24" class="stat-icon" alt="评分" /> {{ scene.ratingAvg || '5.0' }}</span>
              <span class="stat"><img src="https://api.iconify.design/mdi:account-group.svg?color=%236b7280" class="stat-icon" alt="使用次数" /> {{ scene.usageCount || 0 }}次使用</span>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 冥想进行中界面 -->
    <div class="meditation-session" v-else>
      <!-- 背景音乐播放器 -->
      <audio 
        ref="backgroundMusic" 
        :src="currentScene.backgroundMusicUrl" 
        loop 
        preload="auto"
        @loadstart="onMusicLoadStart"
        @canplay="onMusicCanPlay"
        @error="onMusicError"
      ></audio>
      
      <!-- VR场景容器 -->
      <div ref="vrContainer" class="vr-container">
        <div class="scene-preview">
          <div class="scene-title">{{ currentScene.sceneName }}</div>
          <div class="timer">{{ formattedTime }}</div>
          <div class="progress-ring">
            <svg width="200" height="200">
              <circle
                cx="100"
                cy="100"
                r="90"
                stroke="#e2e8f0"
                stroke-width="10"
                fill="none"
              />
              <circle
                cx="100"
                cy="100"
                r="90"
                stroke="#667eea"
                stroke-width="10"
                fill="none"
                :stroke-dasharray="circumference"
                :stroke-dashoffset="progressOffset"
                transform="rotate(-90 100 100)"
              />
            </svg>
          </div>
        </div>
      </div>

      <!-- 控制面板 -->
      <div class="control-panel">
        <button class="control-btn" @click="togglePause">
          <img :src="isPaused ? 'https://api.iconify.design/mdi:play.svg?color=%23ffffff' : 'https://api.iconify.design/mdi:pause.svg?color=%23ffffff'" class="control-icon" :alt="isPaused ? '继续' : '暂停'" />
          {{ isPaused ? '继续' : '暂停' }}
        </button>
        <button class="control-btn" @click="toggleMusic">
          <img :src="isMusicPlaying ? 'https://api.iconify.design/mdi:volume-high.svg?color=%23ffffff' : 'https://api.iconify.design/mdi:volume-off.svg?color=%23ffffff'" class="control-icon" :alt="isMusicPlaying ? '静音' : '播放音乐'" />
          {{ isMusicPlaying ? '静音' : '音乐' }}
        </button>
        <button class="control-btn" @click="adjustVolume">
          <img src="https://api.iconify.design/mdi:tune.svg?color=%23ffffff" class="control-icon" alt="音量调节" />
          音量 {{ Math.round(musicVolume * 100) }}%
        </button>
        <button class="control-btn" @click="endMeditation">
          <img src="https://api.iconify.design/mdi:stop.svg?color=%23ffffff" class="control-icon" alt="结束" />
          结束
        </button>
      </div>

      <!-- 音量调节滑块 -->
      <div class="volume-control" v-if="showVolumeControl">
        <div class="volume-slider-container">
          <span class="volume-label">音量调节</span>
          <input 
            type="range" 
            min="0" 
            max="1" 
            step="0.1" 
            v-model="musicVolume" 
            @input="updateVolume"
            class="volume-slider"
          />
          <span class="volume-value">{{ Math.round(musicVolume * 100) }}%</span>
        </div>
      </div>

      <!-- 引导提示 -->
      <div class="guidance-text" v-if="currentGuidance">
        {{ currentGuidance }}
      </div>
    </div>

    <!-- 冥想结束总结 -->
    <div class="meditation-summary" v-if="showSummary">
      <div class="summary-card">
        <h2 class="summary-title">🎉 冥想完成！</h2>
        <div class="summary-stats">
          <div class="summary-item">
            <div class="summary-icon">⏱️</div>
            <div class="summary-label">冥想时长</div>
            <div class="summary-value">{{ meditationDuration }}分钟</div>
          </div>
          <div class="summary-item">
            <div class="summary-icon">❤️</div>
            <div class="summary-label">平均心率</div>
            <div class="summary-value">72 bpm</div>
          </div>
          <div class="summary-item">
            <div class="summary-icon">😌</div>
            <div class="summary-label">放松程度</div>
            <div class="summary-value">95%</div>
          </div>
        </div>

        <!-- 操作按钮 -->
        <div class="summary-actions">
          <button class="btn-primary" @click="saveMeditationRecord">保存记录</button>
          <button class="btn-secondary" @click="closeSummary">关闭</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import axios from 'axios'

// 数据
const scenes = ref([])
const selectedCategory = ref('all')
const currentScene = ref(null)
const isMeditating = ref(false)
const isPaused = ref(false)
const showSummary = ref(false)

// 音乐播放相关
const backgroundMusic = ref(null)
const isMusicPlaying = ref(false)
const musicVolume = ref(0.6)
const showVolumeControl = ref(false)

// 冥想计时
const meditationTime = ref(0) // 秒
const meditationDuration = ref(0) // 分钟
let meditationTimer = null

// 进度环
const circumference = 2 * Math.PI * 90
const progressOffset = computed(() => {
  const targetDuration = currentScene.value?.durationMinutes * 60 || 900
  const progress = meditationTime.value / targetDuration
  return circumference * (1 - progress)
})

// 引导文本
const currentGuidance = ref('')
const guidanceTexts = [
  '放松您的身体，闭上眼睛...',
  '深呼吸，感受空气进入您的肺部...',
  '让思绪像云朵一样飘过...',
  '专注于当下，感受内心的宁静...',
  '您正在做得很好，继续保持...'
]

// 分类
const categories = ref([
  { value: 'all', label: '全部', icon: 'https://api.iconify.design/mdi:star.svg?color=%23667eea' },
  { value: 'nature', label: '自然', icon: 'https://api.iconify.design/mdi:tree.svg?color=%2316a34a' },
  { value: 'beach', label: '海滩', icon: 'https://api.iconify.design/mdi:beach.svg?color=%2306b6d4' },
  { value: 'forest', label: '森林', icon: 'https://api.iconify.design/mdi:pine-tree.svg?color=%2316a34a' },
  { value: 'mountain', label: '山脉', icon: 'https://api.iconify.design/mdi:mountain.svg?color=%236b7280' },
  { value: 'space', label: '太空', icon: 'https://api.iconify.design/mdi:earth.svg?color=%233b82f6' },
  { value: 'garden', label: '花园', icon: 'https://api.iconify.design/mdi:flower.svg?color=%23ec4899' }
])

// 备用音频源
const fallbackAudioSources = {
  forest: [
    'https://cdn.pixabay.com/audio/2022/05/27/audio_1808fbf07a.mp3',
    'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav',
    'https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav'
  ],
  beach: [
    'https://cdn.pixabay.com/audio/2022/03/10/audio_4621d6bc9c.mp3',
    'https://www2.cs.uic.edu/~i101/SoundFiles/BabyElephantWalk60.wav',
    'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav'
  ],
  garden: [
    'https://cdn.pixabay.com/audio/2022/01/18/audio_7cc0b5cecc.mp3',
    'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav',
    'https://www2.cs.uic.edu/~i101/SoundFiles/CantinaBand60.wav'
  ],
  space: [
    'https://cdn.pixabay.com/audio/2021/08/04/audio_bb630cc098.mp3',
    'https://www2.cs.uic.edu/~i101/SoundFiles/StarWars60.wav',
    'https://www.soundjay.com/misc/sounds/bell-ringing-05.wav'
  ]
}

// 当前音频源索引
const currentAudioIndex = ref(0)

// VR容器
const vrContainer = ref(null)

// 生成简单的冥想音调（作为最终备用方案）
const generateMeditationTone = () => {
  try {
    const audioContext = new (window.AudioContext || window.webkitAudioContext)()
    const oscillator = audioContext.createOscillator()
    const gainNode = audioContext.createGain()
    
    oscillator.connect(gainNode)
    gainNode.connect(audioContext.destination)
    
    // 设置为舒缓的频率
    oscillator.frequency.value = 432 // 432Hz 被认为是治疗频率
    oscillator.type = 'sine'
    
    // 设置音量
    gainNode.gain.setValueAtTime(0.1, audioContext.currentTime)
    
    oscillator.start()
    
    console.log('生成了简单的冥想音调')
    
    return { audioContext, oscillator, gainNode }
  } catch (error) {
    console.error('无法生成音调:', error)
    return null
  }
}

// 计算属性
const filteredScenes = computed(() => {
  if (selectedCategory.value === 'all') {
    return scenes.value
  }
  return scenes.value.filter(scene => scene.sceneType === selectedCategory.value)
})

const formattedTime = computed(() => {
  const minutes = Math.floor(meditationTime.value / 60)
  const seconds = meditationTime.value % 60
  return `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`
})

// 页面挂载
onMounted(async () => {
  await loadScenes()
  
  // 强制刷新页面数据
  console.log('VR冥想页面已加载 - 版本 2.0')
})

// 页面卸载
onUnmounted(() => {
  stopMeditationTimer()
})

// 加载场景数据
const loadScenes = async () => {
  try {
    const response = await axios.get('/api/arvr-health/meditation/scenes')
    scenes.value = response.data.data || []
  } catch (error) {
    console.error('加载冥想场景失败:', error)
    // 使用模拟数据
    scenes.value = [
      {
        id: 1,
        sceneName: '宁静森林',
        sceneType: 'forest',
        thumbnailUrl: 'https://cdn.pixabay.com/photo/2015/12/01/20/28/forest-1072828_960_720.jpg',
        backgroundMusicUrl: 'https://cdn.pixabay.com/audio/2022/05/27/audio_1808fbf07a.mp3',
        description: '漫步在郁郁葱葱的森林中，感受大自然的宁静与和谐。鸟儿啁啾，溪水潺潺完全放松身心。',
        durationMinutes: 15,
        difficultyLevel: 'beginner',
        tags: '自然,放松,初学者友好',
        ratingAvg: 4.8,
        usageCount: 1250
      },
      {
        id: 2,
        sceneName: '海边日落',
        sceneType: 'beach',
        thumbnailUrl: 'https://cdn.pixabay.com/photo/2016/11/29/05/45/astronomy-1867616_960_720.jpg',
        backgroundMusicUrl: 'https://cdn.pixabay.com/audio/2022/03/10/audio_4621d6bc9c.mp3',
        description: '站在沙滩上，观看美丽的日落，海浪声帮助放松。',
        durationMinutes: 20,
        difficultyLevel: 'beginner',
        tags: '海洋,平静,睡眠',
        ratingAvg: 4.9,
        usageCount: 980
      },
      {
        id: 3,
        sceneName: '禅意庭院',
        sceneType: 'garden',
        thumbnailUrl: 'https://cdn.pixabay.com/photo/2017/02/08/17/24/fantasy-2049567_960_720.jpg',
        backgroundMusicUrl: 'https://cdn.pixabay.com/audio/2022/01/18/audio_7cc0b5cecc.mp3',
        description: '日式禅意庭院，枯山水、竹林、石灯笼营造宁静氛围。',
        durationMinutes: 25,
        difficultyLevel: 'intermediate',
        tags: '禅修,专注,清净',
        ratingAvg: 5.0,
        usageCount: 750
      },
      {
        id: 4,
        sceneName: '星空宇宙',
        sceneType: 'space',
        thumbnailUrl: 'https://cdn.pixabay.com/photo/2016/02/13/12/26/aurora-1197753_960_720.jpg',
        backgroundMusicUrl: 'https://cdn.pixabay.com/audio/2021/08/04/audio_bb630cc098.mp3',
        description: '漂浮在浩瀚的宇宙中，感受无限的宁静。',
        durationMinutes: 30,
        difficultyLevel: 'advanced',
        tags: '宇宙,哲学,高级',
        ratingAvg: 4.7,
        usageCount: 560
      }
    ]
  }
}

// 选择场景
const selectScene = (scene) => {
  currentScene.value = scene
  startMeditation()
}

// 开始冥想
const startMeditation = () => {
  isMeditating.value = true
  meditationTime.value = 0
  isPaused.value = false
  startMeditationTimer()
  startGuidance()
  playBackgroundMusic()
}

// 播放背景音乐
const playBackgroundMusic = async () => {
  if (!backgroundMusic.value || !currentScene.value.backgroundMusicUrl) {
    console.log('没有背景音乐文件')
    return
  }

  try {
    backgroundMusic.value.volume = musicVolume.value
    
    // 由于浏览器的自动播放策略，需要用户交互才能播放音频
    await backgroundMusic.value.play()
    isMusicPlaying.value = true
    console.log('背景音乐开始播放')
  } catch (error) {
    console.error('音乐播放失败:', error)
    
    // 如果自动播放失败，显示提示让用户手动点击播放
    if (error.name === 'NotAllowedError') {
      alert('请点击音乐按钮手动播放背景音乐')
    }
  }
}

// 停止背景音乐
const stopBackgroundMusic = () => {
  if (backgroundMusic.value) {
    backgroundMusic.value.pause()
    backgroundMusic.value.currentTime = 0
    isMusicPlaying.value = false
  }
}

// 切换音乐播放状态
const toggleMusic = async () => {
  if (!backgroundMusic.value) {
    console.log('音频元素未找到')
    return
  }
  
  try {
    if (isMusicPlaying.value) {
      backgroundMusic.value.pause()
      isMusicPlaying.value = false
      console.log('音乐已暂停')
    } else {
      await backgroundMusic.value.play()
      isMusicPlaying.value = true
      console.log('音乐开始播放')
    }
  } catch (error) {
    console.error('音乐播放控制失败:', error)
    alert('音乐播放失败，请检查音频文件是否存在')
  }
}

// 调整音量
const adjustVolume = () => {
  showVolumeControl.value = !showVolumeControl.value
  // 3秒后自动隐藏音量控制
  if (showVolumeControl.value) {
    setTimeout(() => {
      showVolumeControl.value = false
    }, 3000)
  }
}

// 更新音量
const updateVolume = () => {
  if (backgroundMusic.value) {
    backgroundMusic.value.volume = musicVolume.value
  }
}

// 音乐事件处理
const onMusicLoadStart = () => {
  console.log('开始加载背景音乐:', currentScene.value?.backgroundMusicUrl)
}

const onMusicCanPlay = () => {
  console.log('背景音乐可以播放')
}

const onMusicError = (error) => {
  console.error('背景音乐加载失败:', error)
  console.log('音频文件路径:', currentScene.value?.backgroundMusicUrl)
  
  // 尝试使用备用音频源
  tryFallbackAudio()
}

// 尝试备用音频源
const tryFallbackAudio = () => {
  if (!currentScene.value) return
  
  const sceneType = currentScene.value.sceneType
  const fallbackSources = fallbackAudioSources[sceneType]
  
  if (fallbackSources && currentAudioIndex.value < fallbackSources.length - 1) {
    currentAudioIndex.value++
    const newAudioUrl = fallbackSources[currentAudioIndex.value]
    
    console.log(`尝试备用音频源 ${currentAudioIndex.value + 1}:`, newAudioUrl)
    
    if (backgroundMusic.value) {
      backgroundMusic.value.src = newAudioUrl
      backgroundMusic.value.load()
    }
  } else {
    console.log('所有音频源都失败，将继续进行无音乐冥想')
    alert('背景音乐暂时不可用，将继续进行无音乐冥想')
  }
}

// 开始计时
const startMeditationTimer = () => {
  meditationTimer = setInterval(() => {
    if (!isPaused.value) {
      meditationTime.value++
      
      // 检查是否完成
      const targetDuration = currentScene.value.durationMinutes * 60
      if (meditationTime.value >= targetDuration) {
        completeMeditation()
      }
    }
  }, 1000)
}

// 停止计时
const stopMeditationTimer = () => {
  if (meditationTimer) {
    clearInterval(meditationTimer)
    meditationTimer = null
  }
}

// 切换暂停
const togglePause = () => {
  isPaused.value = !isPaused.value
  
  // 同时控制音乐播放
  if (backgroundMusic.value) {
    if (isPaused.value) {
      backgroundMusic.value.pause()
    } else if (isMusicPlaying.value) {
      backgroundMusic.value.play()
    }
  }
}

// 结束冥想
const endMeditation = () => {
  if (confirm('确定要结束冥想吗？')) {
    completeMeditation()
  }
}

// 完成冥想
const completeMeditation = () => {
  stopMeditationTimer()
  stopBackgroundMusic()
  meditationDuration.value = Math.round(meditationTime.value / 60)
  isMeditating.value = false
  showSummary.value = true
}

// 开始引导
const startGuidance = () => {
  let index = 0
  const showNextGuidance = () => {
    if (isMeditating.value && !isPaused.value) {
      currentGuidance.value = guidanceTexts[index]
      index = (index + 1) % guidanceTexts.length
      
      setTimeout(() => {
        currentGuidance.value = ''
        setTimeout(showNextGuidance, 10000) // 10秒后显示下一条
      }, 5000) // 显示5秒
    }
  }
  showNextGuidance()
}

// 保存冥想记录
const saveMeditationRecord = async () => {
  try {
    await axios.post('/api/arvr-health/meditation/record', {
      userId: 1,
      sceneId: currentScene.value.id,
      durationMinutes: meditationDuration.value
    })
    alert('记录已保存！')
    closeSummary()
  } catch (error) {
    console.error('保存记录失败:', error)
    alert('保存失败，请重试')
  }
}

// 关闭总结
const closeSummary = () => {
  showSummary.value = false
  currentScene.value = null
}

// 工具函数
const getDifficultyLabel = (level) => {
  const labels = {
    'beginner': '初级',
    'intermediate': '中级',
    'advanced': '高级'
  }
  return labels[level] || level
}

const getSceneTags = (tags) => {
  if (!tags) return []
  return tags.split(',').map(t => t.trim()).slice(0, 3)
}

// 图片加载处理
const handleImageError = (event) => {
  console.log('图片加载失败:', event.target.src)
  // 隐藏图片，显示背景色
  event.target.style.display = 'none'
  const sceneCard = event.target.closest('.scene-image')
  if (sceneCard) {
    sceneCard.classList.add('image-error')
  }
}

const handleImageLoad = (event) => {
  console.log('图片加载成功:', event.target.src)
  // 确保图片正常显示
  event.target.style.display = 'block'
  const sceneCard = event.target.closest('.scene-image')
  if (sceneCard) {
    sceneCard.classList.remove('image-error')
  }
}

// 获取场景图标
const getSceneIcon = (sceneType) => {
  const icons = {
    'forest': '🌲',
    'beach': '🏖️',
    'garden': '🏯',
    'space': '🌌',
    'mountain': '⛰️',
    'nature': '🌿'
  }
  return icons[sceneType] || '🧘'
}
</script>

<style scoped>
.vr-meditation {
  min-height: 100vh;
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 2rem;
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
}

.title {
  font-size: 1.8rem;
  font-weight: 600;
  color: #2d3748;
  margin: 0;
  display: flex;
  align-items: center;
  gap: 0.75rem;
  
  .icon {
    width: 32px;
    height: 32px;
  }
}

.btn-back {
  padding: 0.6rem 1.2rem;
  background: #43e97b;
  color: white;
  text-decoration: none;
  border-radius: 25px;
  font-weight: 500;
  transition: all 0.3s;
}

.btn-back:hover {
  background: #38f9d7;
  transform: translateY(-2px);
}

.content {
  max-width: 1400px;
  margin: 2rem auto;
  padding: 0 2rem;
}

/* 分类标签 */
.category-tabs {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  flex-wrap: wrap;
  background: white;
  padding: 1rem;
  border-radius: 15px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.category-tab {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: #f7fafc;
  border: 2px solid transparent;
  border-radius: 25px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 1rem;
}

.category-tab:hover {
  background: #edf2f7;
}

.category-tab.active {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.tab-icon {
  width: 24px;
  height: 24px;
}

.category-stat {
  display: flex;
  align-items: center;
  gap: 0.25rem;
  font-size: 0.85rem;
  color: #718096;
  
  .stat-icon {
    width: 16px;
    height: 16px;
  }
}

/* 场景网格 */
.scenes-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 2rem;
}

.scene-card {
  background: white;
  border-radius: 15px;
  overflow: hidden;
  cursor: pointer;
  transition: all 0.3s;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.scene-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 30px rgba(0, 0, 0, 0.2);
}

.scene-image {
  position: relative;
  height: 200px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}

.scene-bg-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.3s ease;
  position: absolute;
  top: 0;
  left: 0;
  z-index: 1;
}

.scene-card:hover .scene-bg-image {
  transform: scale(1.05);
}

/* 图片加载错误时的回退样式 */
.scene-image.image-error {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.scene-image.image-error.forest {
  background: linear-gradient(135deg, #61892f 0%, #86c232 100%);
}

.scene-image.image-error.beach {
  background: linear-gradient(135deg, #feb47b 0%, #ff7e5f 100%);
}

.scene-image.image-error.garden {
  background: linear-gradient(135deg, #2ebf91 0%, #8360c3 100%);
}

.scene-image.image-error.space {
  background: linear-gradient(135deg, #302b63 0%, #24243e 100%);
}

.scene-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  opacity: 1;
  transition: opacity 0.3s ease;
  z-index: 2;
}

.scene-card:hover .scene-overlay {
  opacity: 0.8;
}

.scene-icon {
  font-size: 3rem;
  color: white;
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.8));
  animation: float 3s ease-in-out infinite;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.7);
}

@keyframes float {
  0%, 100% { transform: translateY(0px); }
  50% { transform: translateY(-10px); }
}

.scene-duration {
  position: absolute;
  top: 15px;
  left: 15px;
  background: rgba(0, 0, 0, 0.7);
  color: white;
  padding: 0.4rem 0.8rem;
  border-radius: 15px;
  font-size: 0.85rem;
  z-index: 3;
}

.scene-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background: #ffd700;
  color: #2d3748;
  padding: 0.4rem 0.8rem;
  border-radius: 15px;
  font-size: 0.85rem;
  font-weight: 600;
}

.scene-info {
  padding: 1.5rem;
}

.scene-name {
  font-size: 1.3rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.scene-desc {
  color: #718096;
  margin-bottom: 1rem;
  line-height: 1.5;
}

.scene-tags {
  display: flex;
  gap: 0.5rem;
  margin-bottom: 1rem;
}

.tag {
  padding: 0.3rem 0.8rem;
  background: #edf2f7;
  color: #4a5568;
  border-radius: 15px;
  font-size: 0.85rem;
}

.scene-stats {
  display: flex;
  gap: 1.5rem;
  color: #a0aec0;
  font-size: 0.9rem;
}

/* 冥想会话 */
.meditation-session {
  position: relative;
  height: calc(100vh - 90px);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 2rem;
}

.vr-container {
  width: 100%;
  max-width: 1000px;
  height: 600px;
  background: rgba(255, 255, 255, 0.1);
  backdrop-filter: blur(20px);
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 2rem;
}

.scene-preview {
  text-align: center;
  color: white;
}

.scene-title {
  font-size: 2rem;
  font-weight: 600;
  margin-bottom: 2rem;
}

.timer {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 2rem;
}

.progress-ring {
  position: relative;
}

.control-panel {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.control-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 1rem 2rem;
  background: rgba(255, 255, 255, 0.2);
  color: white;
  border: none;
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s;
  
  .control-icon {
    width: 20px;
    height: 20px;
  }
}

.control-btn:hover {
  transform: scale(1.05);
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
}

.guidance-text {
  position: fixed;
  bottom: 100px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(0, 0, 0, 0.8);
  color: white;
  padding: 1rem 2rem;
  border-radius: 25px;
  font-size: 1.2rem;
  animation: fadeInOut 5s;
}

@keyframes fadeInOut {
  0%, 100% { opacity: 0; }
  10%, 90% { opacity: 1; }
}

/* 冥想总结 */
.meditation-summary {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.8);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.summary-card {
  background: white;
  border-radius: 20px;
  padding: 3rem;
  max-width: 600px;
  width: 90%;
}

.summary-title {
  text-align: center;
  font-size: 2rem;
  color: #2d3748;
  margin-bottom: 2rem;
}

.summary-stats {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 2rem;
  margin-bottom: 2rem;
}

.summary-item {
  text-align: center;
}

.summary-icon {
  font-size: 2rem;
  margin-bottom: 0.5rem;
}

.summary-label {
  color: #718096;
  font-size: 0.9rem;
  margin-bottom: 0.5rem;
}

.summary-value {
  font-size: 1.5rem;
  font-weight: 700;
  color: #43e97b;
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
  border-radius: 25px;
  font-size: 1rem;
  font-weight: 600;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-primary {
  background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
  color: white;
}

.btn-primary:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(67, 233, 123, 0.4);
}

.btn-secondary {
  background: #edf2f7;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

/* 音量控制 */
.volume-control {
  position: fixed;
  bottom: 200px;
  left: 50%;
  transform: translateX(-50%);
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(10px);
  border-radius: 15px;
  padding: 1rem 2rem;
  box-shadow: 0 8px 20px rgba(0, 0, 0, 0.2);
  z-index: 100;
}

.volume-slider-container {
  display: flex;
  align-items: center;
  gap: 1rem;
  color: #2d3748;
}

.volume-label {
  font-size: 0.9rem;
  font-weight: 600;
  min-width: 60px;
}

.volume-slider {
  width: 150px;
  height: 6px;
  border-radius: 3px;
  background: #e2e8f0;
  outline: none;
  -webkit-appearance: none;
}

.volume-slider::-webkit-slider-thumb {
  -webkit-appearance: none;
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #43e97b;
  cursor: pointer;
  box-shadow: 0 2px 6px rgba(67, 233, 123, 0.4);
}

.volume-slider::-moz-range-thumb {
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: #43e97b;
  cursor: pointer;
  border: none;
  box-shadow: 0 2px 6px rgba(67, 233, 123, 0.4);
}

.volume-value {
  font-size: 0.9rem;
  font-weight: 600;
  min-width: 40px;
  color: #43e97b;
}
@media (max-width: 768px) {
  .scenes-grid {
    grid-template-columns: 1fr;
  }
  
  .vr-container {
    height: 400px;
  }
  
  .summary-stats {
    grid-template-columns: 1fr;
  }
  
  .mood-options {
    flex-wrap: wrap;
  }
}
</style>

