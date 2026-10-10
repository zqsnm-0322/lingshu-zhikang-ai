<template>
  <div class="human-organs-3d">
    <!-- 头部 -->
    <div class="header">
      <h1 class="title">
        <span class="icon">🫀</span>
        人体器官3D可视化
      </h1>
      <div class="header-actions">
        <button class="btn-secondary" @click="resetView">重置视角</button>
        <router-link to="/arvr-health" class="btn-back">
          ← 返回
        </router-link>
      </div>
    </div>

    <!-- 主要内容 -->
    <div class="main-content">
      <!-- 左侧：器官列表 -->
      <div class="sidebar">
        <h2 class="sidebar-title">器官系统</h2>
        
        <!-- 系统筛选 -->
        <div class="system-tabs">
          <button 
            v-for="system in bodySystems" 
            :key="system.value"
            class="system-tab"
            :class="{ active: selectedSystem === system.value }"
            @click="selectSystem(system.value)"
          >
            <span class="tab-icon">{{ system.icon }}</span>
            <span class="tab-label">{{ system.label }}</span>
          </button>
        </div>

        <!-- 器官列表 -->
        <div class="organ-list">
          <div 
            v-for="organ in filteredOrgans" 
            :key="organ.id"
            class="organ-item"
            :class="{ active: selectedOrgan?.id === organ.id }"
            @click="selectOrgan(organ)"
          >
            <div class="organ-name">{{ organ.organName }}</div>
            <div class="organ-name-en">{{ organ.organNameEn }}</div>
          </div>
        </div>
      </div>

      <!-- 中间：3D显示区域 -->
      <div class="viewer-container">
        <div ref="threeContainer" class="three-viewer"></div>
        
        <!-- 控制提示 -->
        <div class="control-hints">
          <div class="hint-item">🖱️ 拖动：旋转</div>
          <div class="hint-item">🔍 滚轮：缩放</div>
          <div class="hint-item">👆 右键：平移</div>
        </div>

        <!-- 加载提示 -->
        <div v-if="isLoading" class="loading-overlay">
          <div class="loader"></div>
          <p>加载3D模型中...</p>
        </div>
      </div>

      <!-- 右侧：器官详情 -->
      <div class="details-panel" v-if="selectedOrgan">
        <h2 class="panel-title">{{ selectedOrgan.organName }}</h2>
        <p class="panel-subtitle">{{ selectedOrgan.organNameEn }}</p>

        <!-- 功能描述 -->
        <div class="detail-section">
          <h3 class="section-title">📖 功能描述</h3>
          <p class="section-content">{{ selectedOrgan.functionDesc }}</p>
        </div>

        <!-- 常见疾病 -->
        <div class="detail-section" v-if="organDiseases.length > 0">
          <h3 class="section-title">⚠️ 常见疾病</h3>
          <div class="tag-list">
            <span v-for="(disease, index) in organDiseases" :key="index" class="tag disease-tag">
              {{ disease }}
            </span>
          </div>
        </div>

        <!-- 健康建议 -->
        <div class="detail-section" v-if="organTips.length > 0">
          <h3 class="section-title">💡 健康建议</h3>
          <ul class="tips-list">
            <li v-for="(tip, index) in organTips" :key="index">{{ tip }}</li>
          </ul>
        </div>

        <!-- 操作按钮 -->
        <div class="action-buttons">
          <button class="btn-action" @click="playAnimation">
            ▶️ 播放动画
          </button>
          <button class="btn-action" @click="viewInAR">
            🥽 AR查看
          </button>
        </div>
      </div>

      <!-- 占位符（未选择器官时） -->
      <div class="placeholder-panel" v-else>
        <div class="placeholder-icon">👈</div>
        <p class="placeholder-text">请从左侧选择一个器官查看详情</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted, watch } from 'vue'
import axios from 'axios'
import * as THREE from 'three'
import { OrbitControls } from 'three/examples/jsm/controls/OrbitControls'
import { GLTFLoader } from 'three/examples/jsm/loaders/GLTFLoader'

// 数据
const organs = ref([])
const selectedOrgan = ref(null)
const selectedSystem = ref('all')
const isLoading = ref(false)

// Three.js相关
const threeContainer = ref(null)
let scene, camera, renderer, controls, currentModel

// 身体系统列表
const bodySystems = ref([
  { value: 'all', label: '全部', icon: '🧬' },
  { value: 'circulatory', label: '循环系统', icon: '❤️' },
  { value: 'respiratory', label: '呼吸系统', icon: '🫁' },
  { value: 'digestive', label: '消化系统', icon: '🍔' },
  { value: 'nervous', label: '神经系统', icon: '🧠' },
  { value: 'skeletal', label: '骨骼系统', icon: '🦴' },
  { value: 'muscular', label: '肌肉系统', icon: '💪' }
])

// 计算属性
const filteredOrgans = computed(() => {
  if (selectedSystem.value === 'all') {
    return organs.value
  }
  return organs.value.filter(organ => organ.bodySystem === selectedSystem.value)
})

const organDiseases = computed(() => {
  if (!selectedOrgan.value?.commonDiseases) return []
  try {
    return JSON.parse(selectedOrgan.value.commonDiseases)
  } catch {
    return []
  }
})

const organTips = computed(() => {
  if (!selectedOrgan.value?.healthTips) return []
  try {
    return JSON.parse(selectedOrgan.value.healthTips)
  } catch {
    return []
  }
})

// 页面挂载
onMounted(async () => {
  await loadOrgans()
  initThreeJS()
})

// 页面卸载
onUnmounted(() => {
  if (renderer) {
    renderer.dispose()
  }
})

// 加载器官数据
const loadOrgans = async () => {
  try {
    const response = await axios.get('/api/arvr-health/organs')
    organs.value = response.data.data || []
  } catch (error) {
    console.error('加载器官数据失败:', error)
    // 使用模拟数据
    organs.value = [
      {
        id: 1,
        organName: '心脏',
        organNameEn: 'Heart',
        bodySystem: 'circulatory',
        modelUrl: '/assets/models/organs/heart.glb',
        functionDesc: '心脏是人体的动力泵，负责将血液输送到全身各处。',
        commonDiseases: '["冠心病", "心肌梗死", "心律失常"]',
        healthTips: '["保持适度运动", "控制血压", "戒烟限酒"]'
      },
      {
        id: 2,
        organName: '肺',
        organNameEn: 'Lungs',
        bodySystem: 'respiratory',
        modelUrl: '/assets/models/organs/lungs.glb',
        functionDesc: '肺是呼吸系统的主要器官，负责气体交换。',
        commonDiseases: '["肺炎", "哮喘", "肺癌"]',
        healthTips: '["戒烟", "避免空气污染", "适度运动"]'
      },
      {
        id: 3,
        organName: '大脑',
        organNameEn: 'Brain',
        bodySystem: 'nervous',
        modelUrl: '/assets/models/organs/brain.glb',
        functionDesc: '大脑控制思维、记忆、情感和身体活动。',
        commonDiseases: '["阿尔茨海默病", "中风", "偏头痛"]',
        healthTips: '["保持学习", "充足睡眠", "健康饮食"]'
      }
    ]
  }
}

// 初始化Three.js
const initThreeJS = () => {
  if (!threeContainer.value) return

  // 创建场景
  scene = new THREE.Scene()
  scene.background = new THREE.Color(0xf0f0f0)

  // 创建相机
  const width = threeContainer.value.clientWidth
  const height = threeContainer.value.clientHeight
  camera = new THREE.PerspectiveCamera(45, width / height, 0.1, 1000)
  camera.position.set(0, 0, 5)

  // 创建渲染器
  renderer = new THREE.WebGLRenderer({ antialias: true })
  renderer.setSize(width, height)
  renderer.setPixelRatio(window.devicePixelRatio)
  threeContainer.value.appendChild(renderer.domElement)

  // 添加光源
  const ambientLight = new THREE.AmbientLight(0xffffff, 0.6)
  scene.add(ambientLight)

  const directionalLight = new THREE.DirectionalLight(0xffffff, 0.8)
  directionalLight.position.set(1, 1, 1)
  scene.add(directionalLight)

  // 添加控制器
  controls = new OrbitControls(camera, renderer.domElement)
  controls.enableDamping = true
  controls.dampingFactor = 0.05

  // 添加网格辅助线
  const gridHelper = new THREE.GridHelper(10, 10)
  gridHelper.material.opacity = 0.2
  gridHelper.material.transparent = true
  scene.add(gridHelper)

  // 添加坐标轴辅助线
  const axesHelper = new THREE.AxesHelper(5)
  scene.add(axesHelper)

  // 渲染循环
  const animate = () => {
    requestAnimationFrame(animate)
    controls.update()
    renderer.render(scene, camera)
  }
  animate()

  // 窗口大小改变
  window.addEventListener('resize', onWindowResize)
}

// 窗口大小改变处理
const onWindowResize = () => {
  if (!threeContainer.value) return
  const width = threeContainer.value.clientWidth
  const height = threeContainer.value.clientHeight
  camera.aspect = width / height
  camera.updateProjectionMatrix()
  renderer.setSize(width, height)
}

// 选择系统
const selectSystem = (system) => {
  selectedSystem.value = system
  // 切换系统时，如果当前选中的器官不在新系统中，清空选择
  if (selectedOrgan.value) {
    const isOrganInNewSystem = filteredOrgans.value.some(
      organ => organ.id === selectedOrgan.value.id
    )
    if (!isOrganInNewSystem) {
      selectedOrgan.value = null
    }
  }
}

// 选择器官
const selectOrgan = async (organ) => {
  selectedOrgan.value = organ
  await load3DModel(organ.modelUrl)
}

// 加载3D模型
const load3DModel = async (modelUrl) => {
  if (!scene) return

  isLoading.value = true

  // 移除旧模型
  if (currentModel) {
    scene.remove(currentModel)
  }

  // 根据器官类型创建不同的形状
  try {
    const organName = selectedOrgan.value?.organName
    let geometry, material, mesh
    
    switch(organName) {
      case '心脏':
        // 心脏：使用球体 + 圆锥组合
        const heartGroup = new THREE.Group()
        
        // 主体（球体）
        const heartBody = new THREE.Mesh(
          new THREE.SphereGeometry(0.6, 32, 32),
          new THREE.MeshPhongMaterial({ color: 0xc41e3a, shininess: 30 })
        )
        heartGroup.add(heartBody)
        
        // 左心室（小球）
        const leftVentricle = new THREE.Mesh(
          new THREE.SphereGeometry(0.4, 32, 32),
          new THREE.MeshPhongMaterial({ color: 0xa01729 })
        )
        leftVentricle.position.set(-0.3, 0.3, 0)
        heartGroup.add(leftVentricle)
        
        // 右心室（小球）
        const rightVentricle = new THREE.Mesh(
          new THREE.SphereGeometry(0.4, 32, 32),
          new THREE.MeshPhongMaterial({ color: 0xa01729 })
        )
        rightVentricle.position.set(0.3, 0.3, 0)
        heartGroup.add(rightVentricle)
        
        currentModel = heartGroup
        break
        
      case '肺':
        // 肺：两个椭球体
        const lungsGroup = new THREE.Group()
        
        // 左肺
        const leftLung = new THREE.Mesh(
          new THREE.SphereGeometry(0.5, 32, 32),
          new THREE.MeshPhongMaterial({ color: 0xff9999, transparent: true, opacity: 0.8 })
        )
        leftLung.scale.set(0.8, 1.2, 0.6)
        leftLung.position.set(-0.6, 0, 0)
        lungsGroup.add(leftLung)
        
        // 右肺
        const rightLung = new THREE.Mesh(
          new THREE.SphereGeometry(0.5, 32, 32),
          new THREE.MeshPhongMaterial({ color: 0xff9999, transparent: true, opacity: 0.8 })
        )
        rightLung.scale.set(0.8, 1.2, 0.6)
        rightLung.position.set(0.6, 0, 0)
        lungsGroup.add(rightLung)
        
        currentModel = lungsGroup
        break
        
      case '肝脏':
        // 肝脏：不规则形状（使用拉伸的球体）
        geometry = new THREE.SphereGeometry(0.8, 32, 32)
        material = new THREE.MeshPhongMaterial({ 
          color: 0x8b4513,
          shininess: 50
        })
        mesh = new THREE.Mesh(geometry, material)
        mesh.scale.set(1.5, 0.6, 1)
        currentModel = mesh
        break
        
      case '胃':
        // 胃：椭圆柱体
        geometry = new THREE.CylinderGeometry(0.3, 0.4, 1, 32)
        material = new THREE.MeshPhongMaterial({ 
          color: 0xf5deb3,
          transparent: true,
          opacity: 0.9
        })
        mesh = new THREE.Mesh(geometry, material)
        currentModel = mesh
        break
        
      case '大脑':
        // 大脑：复杂的球体
        const brainGroup = new THREE.Group()
        
        // 主体
        const brainMain = new THREE.Mesh(
          new THREE.SphereGeometry(0.7, 32, 32),
          new THREE.MeshPhongMaterial({ 
            color: 0xffb6c1,
            shininess: 20
          })
        )
        brainMain.scale.set(1, 0.9, 1.1)
        brainGroup.add(brainMain)
        
        // 添加一些"褶皱"效果
        for (let i = 0; i < 8; i++) {
          const fold = new THREE.Mesh(
            new THREE.TorusGeometry(0.5, 0.05, 8, 16),
            new THREE.MeshPhongMaterial({ color: 0xff9999 })
          )
          fold.position.y = (i - 4) * 0.15
          fold.rotation.z = Math.random() * Math.PI * 2
          brainGroup.add(fold)
        }
        
        currentModel = brainGroup
        break
        
      case '肾脏':
        // 肾脏：豆形
        const kidneysGroup = new THREE.Group()
        
        // 左肾
        const leftKidney = new THREE.Mesh(
          new THREE.SphereGeometry(0.4, 32, 32),
          new THREE.MeshPhongMaterial({ color: 0x8b0000 })
        )
        leftKidney.scale.set(0.6, 1, 0.8)
        leftKidney.position.set(-0.5, 0, 0)
        kidneysGroup.add(leftKidney)
        
        // 右肾
        const rightKidney = new THREE.Mesh(
          new THREE.SphereGeometry(0.4, 32, 32),
          new THREE.MeshPhongMaterial({ color: 0x8b0000 })
        )
        rightKidney.scale.set(0.6, 1, 0.8)
        rightKidney.position.set(0.5, 0, 0)
        kidneysGroup.add(rightKidney)
        
        currentModel = kidneysGroup
        break
        
      case '骨骼':
        // 骨骼：简化的骨架
        const skeletonGroup = new THREE.Group()
        
        // 脊柱
        const spine = new THREE.Mesh(
          new THREE.CylinderGeometry(0.1, 0.1, 2, 16),
          new THREE.MeshPhongMaterial({ color: 0xf5f5dc })
        )
        skeletonGroup.add(spine)
        
        // 肋骨
        for (let i = 0; i < 6; i++) {
          const rib = new THREE.Mesh(
            new THREE.TorusGeometry(0.5, 0.05, 8, 16, Math.PI),
            new THREE.MeshPhongMaterial({ color: 0xfffacd })
          )
          rib.position.y = 0.7 - i * 0.25
          rib.rotation.x = Math.PI / 2
          skeletonGroup.add(rib)
        }
        
        currentModel = skeletonGroup
        break
        
      case '肌肉系统':
        // 肌肉：纤维状
        const muscleGroup = new THREE.Group()
        
        // 创建多个纤维束
        for (let i = 0; i < 12; i++) {
          const fiber = new THREE.Mesh(
            new THREE.CylinderGeometry(0.08, 0.08, 1.5, 8),
            new THREE.MeshPhongMaterial({ 
              color: 0xcd5c5c,
              shininess: 10
            })
          )
          const angle = (i / 12) * Math.PI * 2
          fiber.position.set(
            Math.cos(angle) * 0.4,
            0,
            Math.sin(angle) * 0.4
          )
          muscleGroup.add(fiber)
        }
        
        currentModel = muscleGroup
        break
        
      default:
        // 默认：简单球体
        geometry = new THREE.SphereGeometry(0.6, 32, 32)
        material = new THREE.MeshPhongMaterial({ 
          color: 0xff6b6b,
          transparent: true,
          opacity: 0.8
        })
        currentModel = new THREE.Mesh(geometry, material)
    }
    
    // 添加到场景并启动旋转动画
    scene.add(currentModel)
    animateModel()
    
    isLoading.value = false
  } catch (error) {
    console.error('加载模型失败:', error)
    isLoading.value = false
  }
}

// 模型动画
const animateModel = () => {
  if (currentModel) {
    // 缓慢旋转展示
    const animate = () => {
      if (currentModel && scene.children.includes(currentModel)) {
        currentModel.rotation.y += 0.005
        requestAnimationFrame(animate)
      }
    }
    animate()
  }
}

// 重置视角
const resetView = () => {
  if (camera && controls) {
    camera.position.set(0, 0, 5)
    controls.reset()
  }
}

// 播放动画
const playAnimation = () => {
  alert('动画播放功能开发中...')
}

// AR查看
const viewInAR = () => {
  alert('AR查看功能需要支持WebXR的设备...')
}
</script>

<style scoped>
.human-organs-3d {
  min-height: 100vh;
  background: #f7fafc;
}

.header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1.5rem 2rem;
  background: white;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
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

.header-actions {
  display: flex;
  gap: 1rem;
}

.btn-secondary,
.btn-back {
  padding: 0.6rem 1.2rem;
  border-radius: 8px;
  font-weight: 500;
  transition: all 0.3s;
  border: none;
  cursor: pointer;
}

.btn-secondary {
  background: #edf2f7;
  color: #4a5568;
}

.btn-secondary:hover {
  background: #e2e8f0;
}

.btn-back {
  background: #667eea;
  color: white;
  text-decoration: none;
}

.btn-back:hover {
  background: #5568d3;
}

/* 主要内容 */
.main-content {
  display: grid;
  grid-template-columns: 300px 1fr 350px;
  height: calc(100vh - 90px);
  gap: 1rem;
  padding: 1rem;
}

/* 左侧边栏 */
.sidebar {
  background: white;
  border-radius: 15px;
  padding: 1.5rem;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.sidebar-title {
  font-size: 1.2rem;
  color: #2d3748;
  margin-bottom: 1rem;
}

.system-tabs {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  margin-bottom: 1.5rem;
}

.system-tab {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1rem;
  background: #f7fafc;
  border: 2px solid transparent;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
  font-size: 0.9rem;
}

.system-tab:hover {
  background: #edf2f7;
}

.system-tab.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-color: #667eea;
}

.tab-icon {
  font-size: 1.2rem;
}

.organ-list {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.organ-item {
  padding: 1rem;
  background: #f7fafc;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s;
}

.organ-item:hover {
  background: #edf2f7;
  transform: translateX(5px);
}

.organ-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.organ-name {
  font-weight: 600;
  margin-bottom: 0.25rem;
}

.organ-name-en {
  font-size: 0.85rem;
  opacity: 0.7;
}

/* 3D查看器 */
.viewer-container {
  position: relative;
  background: white;
  border-radius: 15px;
  overflow: hidden;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.three-viewer {
  width: 100%;
  height: 100%;
}

.control-hints {
  position: absolute;
  bottom: 20px;
  left: 20px;
  display: flex;
  gap: 1rem;
  background: rgba(0, 0, 0, 0.7);
  padding: 0.75rem 1rem;
  border-radius: 10px;
  color: white;
  font-size: 0.85rem;
}

.loading-overlay {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(255, 255, 255, 0.9);
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  gap: 1rem;
}

.loader {
  width: 50px;
  height: 50px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 右侧详情面板 */
.details-panel,
.placeholder-panel {
  background: white;
  border-radius: 15px;
  padding: 2rem;
  overflow-y: auto;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
}

.panel-title {
  font-size: 1.8rem;
  color: #2d3748;
  margin-bottom: 0.5rem;
}

.panel-subtitle {
  color: #718096;
  margin-bottom: 2rem;
}

.detail-section {
  margin-bottom: 2rem;
}

.section-title {
  font-size: 1.1rem;
  color: #2d3748;
  margin-bottom: 1rem;
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.section-content {
  color: #4a5568;
  line-height: 1.6;
}

.tag-list {
  display: flex;
  flex-wrap: wrap;
  gap: 0.5rem;
}

.tag {
  padding: 0.4rem 0.8rem;
  border-radius: 15px;
  font-size: 0.85rem;
}

.disease-tag {
  background: #fee;
  color: #c53030;
}

.tips-list {
  list-style-position: inside;
  color: #4a5568;
  line-height: 1.8;
}

.action-buttons {
  display: grid;
  gap: 1rem;
  margin-top: 2rem;
}

.btn-action {
  padding: 0.75rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 10px;
  font-size: 1rem;
  cursor: pointer;
  transition: all 0.3s;
}

.btn-action:hover {
  transform: translateY(-2px);
  box-shadow: 0 8px 20px rgba(102, 126, 234, 0.4);
}

/* 占位符 */
.placeholder-panel {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
}

.placeholder-icon {
  font-size: 5rem;
  margin-bottom: 1rem;
  animation: float 3s ease-in-out infinite;
}

@keyframes float {
  0%, 100% { transform: translateX(0); }
  50% { transform: translateX(-10px); }
}

.placeholder-text {
  color: #a0aec0;
  font-size: 1.1rem;
}

/* 响应式 */
@media (max-width: 1200px) {
  .main-content {
    grid-template-columns: 1fr;
    grid-template-rows: auto 1fr auto;
    height: auto;
  }
  
  .sidebar {
    max-height: 300px;
  }
  
  .viewer-container {
    height: 500px;
  }
}
</style>

