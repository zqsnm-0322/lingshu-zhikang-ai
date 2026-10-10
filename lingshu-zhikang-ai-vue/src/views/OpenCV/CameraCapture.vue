<template>
  <div id="app">
    <el-container class="app-container">
      <el-header class="app-header">
        <div class="header-content">
          <h1>
            <el-icon><Camera /></el-icon>
            OpenCV AI问诊模拟器
          </h1>
          <nav class="header-nav">
            <router-link to="/game" class="nav-link">
              <el-icon><VideoCamera /></el-icon>
              摄像头
            </router-link>
            <router-link to="/" class="nav-link back-home">
              <el-icon><HomeFilled /></el-icon>
              返回首页
            </router-link>
          </nav>
        </div>
      </el-header>
      <el-main class="app-main">
        <div class="camera-capture">
    <el-row :gutter="20">
      <!-- 左侧：摄像头控制 -->
      <el-col :span="12">
        <el-card class="camera-panel">
          <template #header>
            <div class="card-header">
              <el-icon><VideoCamera /></el-icon>
              <span>摄像头</span>
            </div>
          </template>

          <!-- 摄像头视频流 -->
          <div class="camera-container" :class="{ 'camera-active': cameraActive, 'camera-starting': startingCamera }">
            <video
              ref="videoRef"
              :class="{ 'camera-video': true, 'mirrored': mirrorMode, 'video-active': cameraActive }"
              autoplay
              muted
              playsinline
            ></video>
            
            <!-- 摄像头未启动时的占位符 -->
            <div v-if="!cameraActive && !startingCamera" class="camera-placeholder">
              <div class="placeholder-content">
                <el-icon class="camera-icon"><VideoCamera /></el-icon>
                <p class="placeholder-text">点击下方按钮启动摄像头</p>
                <div class="placeholder-decoration">
                  <div class="decoration-circle"></div>
                  <div class="decoration-circle"></div>
                  <div class="decoration-circle"></div>
                </div>
              </div>
            </div>

            <!-- 启动中动画 -->
            <div v-if="startingCamera" class="camera-starting-animation">
              <div class="starting-content">
                <div class="loading-spinner"></div>
                <p class="starting-text">正在启动摄像头...</p>
                <div class="starting-waves">
                  <div class="wave"></div>
                  <div class="wave"></div>
                  <div class="wave"></div>
                </div>
              </div>
            </div>

            <!-- 摄像头边框装饰 -->
            <div v-if="cameraActive" class="camera-frame">
              <div class="frame-corner top-left"></div>
              <div class="frame-corner top-right"></div>
              <div class="frame-corner bottom-left"></div>
              <div class="frame-corner bottom-right"></div>
            </div>

            <!-- 对焦动画 -->
            <div v-if="showFocusAnimation" class="focus-animation" :style="focusStyle">
              <div class="focus-ring"></div>
            </div>

            <!-- 人像捕捉动画 -->
            <div v-if="faceDetected" class="face-detection" :style="faceBoxStyle">
              <div class="face-box">
                <div class="face-corner top-left"></div>
                <div class="face-corner top-right"></div>
                <div class="face-corner bottom-left"></div>
                <div class="face-corner bottom-right"></div>
                <div class="face-label">
                  <span class="face-text">人像识别</span>
                  <div class="face-confidence">{{ faceConfidence }}%</div>
                </div>
              </div>
            </div>

            <!-- 人像扫描动画 -->
            <div v-if="cameraActive && enableFaceDetection" class="face-scanner">
              <div class="scanner-line"></div>
            </div>

            <!-- 录制指示器 -->
            <div v-if="recording" class="recording-indicator">
              <div class="recording-dot"></div>
              <span class="recording-text">REC</span>
              <span class="recording-time">{{ recordingTime }}</span>
            </div>

            <!-- 拍照闪光效果 -->
            <div v-if="showFlash" class="flash-effect"></div>

            <!-- 网格线辅助 -->
            <div v-if="cameraActive && showGrid" class="camera-grid">
              <div class="grid-line vertical left"></div>
              <div class="grid-line vertical right"></div>
              <div class="grid-line horizontal top"></div>
              <div class="grid-line horizontal bottom"></div>
            </div>
          </div>

          <!-- 摄像头控制按钮 -->
          <div class="camera-controls">
            <el-button-group>
              <el-button 
                type="primary" 
                @click="startCamera"
                :disabled="cameraActive"
                :loading="startingCamera"
              >
                <el-icon><VideoCamera /></el-icon>
                启动摄像头
              </el-button>
              
              <el-button 
                type="danger" 
                @click="stopCamera"
                :disabled="!cameraActive"
              >
                <el-icon><VideoCameraFilled /></el-icon>
                关闭摄像头
              </el-button>
            </el-button-group>

            <el-row :gutter="10" style="margin-top: 10px;">
              <el-col :span="12">
                <el-button 
                  type="success" 
                  @click="capturePhoto"
                  :disabled="!cameraActive"
                  size="large"
                  style="width: 100%;"
                >
                  <el-icon><Camera /></el-icon>
                  拍照
                </el-button>
              </el-col>
              <el-col :span="12">
                <el-button 
                  :type="recording ? 'danger' : 'warning'"
                  @click="toggleRecording"
                  :disabled="!cameraActive"
                  size="large"
                  style="width: 100%;"
                >
                  <el-icon><VideoPlay v-if="!recording" /><VideoPause v-else /></el-icon>
                  {{ recording ? '停止录制' : '开始录制' }}
                </el-button>
              </el-col>
            </el-row>
          </div>

          <!-- 摄像头设置 -->
          <div v-if="cameraActive" class="camera-settings">
            <el-divider>摄像头设置</el-divider>
            
            <el-form label-width="80px">
              <el-form-item label="镜像模式">
                <el-switch v-model="mirrorMode" />
              </el-form-item>
              
              <el-form-item label="网格线">
                <el-switch v-model="showGrid" />
              </el-form-item>
              
              <el-form-item label="人像识别">
                <el-switch v-model="enableFaceDetection" />
              </el-form-item>
              
              <el-form-item label="摄像头" v-if="availableCameras.length > 1">
                <el-select v-model="selectedCamera" @change="switchCamera" style="width: 100%">
                  <el-option
                    v-for="(camera, index) in availableCameras"
                    :key="camera.deviceId"
                    :label="camera.label || `摄像头 ${index + 1}`"
                    :value="camera.deviceId"
                  />
                </el-select>
              </el-form-item>
            </el-form>
          </div>
        </el-card>
      </el-col>

      <!-- 右侧：拍照/录像结果和处理 -->
      <el-col :span="12">
        <el-card class="photo-panel">
          <template #header>
            <div class="card-header">
              <el-icon><Picture v-if="!recordedVideo" /><VideoPlay v-else /></el-icon>
              <span>{{ recordedVideo ? '录像结果' : '拍照结果' }}</span>
            </div>
          </template>

          <!-- 拍照/录像预览 -->
          <div v-if="!capturedPhoto && !recordedVideo" class="photo-placeholder">
            <el-empty description="还没有拍照或录像，点击左侧按钮开始" />
          </div>

          <!-- 录像结果 -->
          <div v-if="recordedVideo" class="video-result">
            <div class="video-preview">
              <video :src="recordedVideo" controls style="width: 100%; max-height: 300px; border-radius: 8px;"></video>
            </div>

            <!-- 录像操作按钮 -->
            <div class="video-actions">
              <el-button-group>
                <el-button type="primary" @click="downloadVideo">
                  <el-icon><Download /></el-icon>
                  下载视频
                </el-button>
                
                <el-button type="warning" @click="retakeVideo">
                  <el-icon><RefreshRight /></el-icon>
                  重新录制
                </el-button>
              </el-button-group>
            </div>

          </div>

          <div v-if="capturedPhoto && !recordedVideo" class="photo-result">
            <div class="photo-preview">
              <img :src="capturedPhoto" alt="拍照结果" />
            </div>

            <!-- 照片操作按钮 -->
            <div class="photo-actions">
              <el-button-group>
                <el-button type="primary" @click="downloadPhoto">
                  <el-icon><Download /></el-icon>
                  下载照片
                </el-button>
                
                <el-button type="warning" @click="retakePhoto">
                  <el-icon><RefreshRight /></el-icon>
                  重新拍照
                </el-button>
              </el-button-group>
            </div>

            <!-- 图像处理选项 -->
            <div class="processing-options">
              <el-divider>滤镜效果</el-divider>
              
              <div class="filter-buttons">
                <el-button @click="applyFilter('grayscale')" size="small">灰度</el-button>
                <el-button @click="applyFilter('sepia')" size="small">复古</el-button>
                <el-button @click="applyFilter('blur')" size="small">模糊</el-button>
                <el-button @click="applyFilter('brightness')" size="small">增亮</el-button>
                <el-button @click="applyFilter('contrast')" size="small">对比度</el-button>
                <el-button @click="resetFilters" size="small" type="danger">重置</el-button>
              </div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 隐藏的canvas用于拍照 -->
    <canvas ref="canvasRef" style="display: none;"></canvas>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  VideoCamera, 
  VideoCameraFilled, 
  Camera, 
  Picture, 
  Download, 
  RefreshRight,
  Upload,
  VideoPlay,
  VideoPause,
  Memo,
  HomeFilled
} from '@element-plus/icons-vue'
import { useRouter } from 'vue-router'

export default {
  name: 'CameraCapture',
  components: {
    VideoCamera,
    VideoCameraFilled,
    Camera,
    Picture,
    Download,
    RefreshRight,
    Upload,
    VideoPlay,
    VideoPause,
    Memo,
    HomeFilled
  },
  setup() {
    const router = useRouter()
    const videoRef = ref(null)
    const canvasRef = ref(null)
    
    const cameraActive = ref(false)
    const startingCamera = ref(false)
    const capturedPhoto = ref('')
    const recordedVideo = ref('')
    const recording = ref(false)
    const mirrorMode = ref(true)
    const availableCameras = ref([])
    const selectedCamera = ref('')
    const uploading = ref(false)
    const showFocusAnimation = ref(false)
    const showFlash = ref(false)
    const focusStyle = ref({})
    const recordingTime = ref('00:00')
    const showGrid = ref(false)
    const enableFaceDetection = ref(true)
    const faceDetected = ref(false)
    const faceBoxStyle = ref({})
    const faceConfidence = ref(95)
    
    let currentStream = null
    let mediaRecorder = null
    let recordedChunks = []
    let recordingTimer = null
    let recordingStartTime = 0
    let faceDetectionTimer = null

    // 获取可用摄像头列表
    const getCameraDevices = async () => {
      try {
        const devices = await navigator.mediaDevices.enumerateDevices()
        availableCameras.value = devices.filter(device => device.kind === 'videoinput')
        if (availableCameras.value.length > 0 && !selectedCamera.value) {
          selectedCamera.value = availableCameras.value[0].deviceId
        }
      } catch (error) {
        console.error('获取摄像头设备失败:', error)
        ElMessage.error('无法获取摄像头设备列表')
      }
    }

    // 启动摄像头
    const startCamera = async () => {
      startingCamera.value = true
      
      try {
        // 检查浏览器支持
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
          throw new Error('您的浏览器不支持摄像头功能')
        }

        const constraints = {
          video: {
            deviceId: selectedCamera.value ? { exact: selectedCamera.value } : undefined,
            width: { ideal: 1280 },
            height: { ideal: 720 },
            facingMode: 'user'
          },
          audio: false
        }

        currentStream = await navigator.mediaDevices.getUserMedia(constraints)
        
        if (videoRef.value) {
          videoRef.value.srcObject = currentStream
          cameraActive.value = true
          
          // 启动人像检测
          if (enableFaceDetection.value) {
            startFaceDetection()
          }
          
          ElMessage.success('摄像头启动成功')
        }
      } catch (error) {
        console.error('启动摄像头失败:', error)
        let errorMessage = '启动摄像头失败'
        
        if (error.name === 'NotAllowedError') {
          errorMessage = '请允许访问摄像头权限'
        } else if (error.name === 'NotFoundError') {
          errorMessage = '未找到摄像头设备'
        } else if (error.name === 'NotReadableError') {
          errorMessage = '摄像头被其他应用占用'
        }
        
        ElMessage.error(errorMessage)
      } finally {
        startingCamera.value = false
      }
    }

    // 关闭摄像头
    const stopCamera = () => {
      // 停止人像检测
      stopFaceDetection()
      
      if (currentStream) {
        currentStream.getTracks().forEach(track => track.stop())
        currentStream = null
      }
      
      if (videoRef.value) {
        videoRef.value.srcObject = null
      }
      
      cameraActive.value = false
      faceDetected.value = false
      ElMessage.info('摄像头已关闭')
    }

    // 切换摄像头
    const switchCamera = async () => {
      if (cameraActive.value) {
        stopCamera()
        await new Promise(resolve => setTimeout(resolve, 100))
        await startCamera()
      }
    }

    // 拍照
    const capturePhoto = () => {
      if (!videoRef.value || !canvasRef.value) return

      // 显示对焦动画
      showFocusAnimation.value = true
      
      // 随机位置显示对焦框
      const container = videoRef.value.parentElement
      const rect = container.getBoundingClientRect()
      const x = Math.random() * (rect.width - 100) + 50
      const y = Math.random() * (rect.height - 100) + 50
      
      focusStyle.value = {
        left: x + 'px',
        top: y + 'px'
      }

      // 对焦动画持续时间
      setTimeout(() => {
        showFocusAnimation.value = false
        
        // 显示闪光效果
        showFlash.value = true
        setTimeout(() => {
          showFlash.value = false
        }, 200)

        // 执行拍照
        const video = videoRef.value
        const canvas = canvasRef.value
        const context = canvas.getContext('2d')

        // 设置canvas尺寸
        canvas.width = video.videoWidth
        canvas.height = video.videoHeight

        // 如果是镜像模式，翻转画布
        if (mirrorMode.value) {
          context.scale(-1, 1)
          context.drawImage(video, -canvas.width, 0, canvas.width, canvas.height)
        } else {
          context.drawImage(video, 0, 0, canvas.width, canvas.height)
        }

        // 获取图像数据
        capturedPhoto.value = canvas.toDataURL('image/jpeg', 0.9)
        ElMessage.success('拍照成功！')
      }, 800)
    }

    // 下载照片
    const downloadPhoto = () => {
      if (!capturedPhoto.value) return

      const link = document.createElement('a')
      link.href = capturedPhoto.value
      link.download = `camera_photo_${new Date().getTime()}.jpg`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      ElMessage.success('照片下载成功')
    }

    // 重新拍照
    const retakePhoto = () => {
      capturedPhoto.value = ''
      recordedVideo.value = ''
    }

    // 开始/停止录制
    const toggleRecording = async () => {
      if (!recording.value) {
        await startRecording()
      } else {
        stopRecording()
      }
    }

    // 开始录制
    const startRecording = async () => {
      if (!currentStream) {
        ElMessage.error('请先启动摄像头')
        return
      }

      try {
        recordedChunks = []
        
        // 创建MediaRecorder
        const options = {
          mimeType: 'video/webm;codecs=vp9',
          videoBitsPerSecond: 2500000
        }
        
        // 检查浏览器支持的格式
        if (!MediaRecorder.isTypeSupported(options.mimeType)) {
          options.mimeType = 'video/webm'
          if (!MediaRecorder.isTypeSupported(options.mimeType)) {
            options.mimeType = 'video/mp4'
          }
        }

        mediaRecorder = new MediaRecorder(currentStream, options)
        
        mediaRecorder.ondataavailable = (event) => {
          if (event.data.size > 0) {
            recordedChunks.push(event.data)
          }
        }
        
        mediaRecorder.onstop = () => {
          const blob = new Blob(recordedChunks, { type: 'video/webm' })
          recordedVideo.value = URL.createObjectURL(blob)
          capturedPhoto.value = '' // 清除照片
          
          // 停止录制计时器
          if (recordingTimer) {
            clearInterval(recordingTimer)
            recordingTimer = null
          }
          
          ElMessage.success('录制完成！')
        }
        
        mediaRecorder.start()
        recording.value = true
        
        // 开始录制计时
        recordingStartTime = Date.now()
        recordingTimer = setInterval(updateRecordingTime, 1000)
        
        ElMessage.success('开始录制视频')
        
      } catch (error) {
        console.error('开始录制失败:', error)
        ElMessage.error('开始录制失败: ' + error.message)
      }
    }

    // 停止录制
    const stopRecording = () => {
      if (mediaRecorder && recording.value) {
        mediaRecorder.stop()
        recording.value = false
      }
    }

    // 更新录制时间
    const updateRecordingTime = () => {
      if (recording.value) {
        const elapsed = Math.floor((Date.now() - recordingStartTime) / 1000)
        const minutes = Math.floor(elapsed / 60).toString().padStart(2, '0')
        const seconds = (elapsed % 60).toString().padStart(2, '0')
        recordingTime.value = `${minutes}:${seconds}`
      }
    }

    // 重新录制
    const retakeVideo = () => {
      if (recordedVideo.value) {
        URL.revokeObjectURL(recordedVideo.value)
      }
      recordedVideo.value = ''
      capturedPhoto.value = ''
    }

    // 下载视频
    const downloadVideo = () => {
      if (!recordedVideo.value) return

      const link = document.createElement('a')
      link.href = recordedVideo.value
      link.download = `camera_video_${new Date().getTime()}.webm`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      ElMessage.success('视频下载成功')
    }


    // 应用滤镜效果
    const applyFilter = (filterType) => {
      if (!capturedPhoto.value || !canvasRef.value) return

      const canvas = canvasRef.value
      const context = canvas.getContext('2d')
      const img = new Image()
      
      img.onload = () => {
        canvas.width = img.width
        canvas.height = img.height
        
        // 应用不同的滤镜
        switch (filterType) {
          case 'grayscale':
            context.filter = 'grayscale(100%)'
            break
          case 'sepia':
            context.filter = 'sepia(100%)'
            break
          case 'blur':
            context.filter = 'blur(2px)'
            break
          case 'brightness':
            context.filter = 'brightness(150%)'
            break
          case 'contrast':
            context.filter = 'contrast(150%)'
            break
          default:
            context.filter = 'none'
        }
        
        context.drawImage(img, 0, 0)
        capturedPhoto.value = canvas.toDataURL('image/jpeg', 0.9)
        ElMessage.success(`${filterType}滤镜应用成功`)
      }
      
      img.src = capturedPhoto.value
    }

    // 重置滤镜
    const resetFilters = () => {
      // 这里需要保存原始照片，简化处理就重新拍照
      ElMessageBox.confirm('重置滤镜将清除当前照片，是否继续？', '确认', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        retakePhoto()
      })
    }


    // 人像检测相关函数
    const startFaceDetection = () => {
      if (!enableFaceDetection.value || !videoRef.value) return
      
      faceDetectionTimer = setInterval(() => {
        simulateFaceDetection()
      }, 2000) // 每2秒检测一次
    }

    const stopFaceDetection = () => {
      if (faceDetectionTimer) {
        clearInterval(faceDetectionTimer)
        faceDetectionTimer = null
      }
      faceDetected.value = false
    }

    const simulateFaceDetection = () => {
      if (!videoRef.value || !enableFaceDetection.value) return
      
      // 模拟人脸检测（实际项目中可以集成真实的人脸检测库）
      const shouldDetectFace = Math.random() > 0.3 // 70%概率检测到人脸
      
      if (shouldDetectFace) {
        const container = videoRef.value.parentElement
        const rect = container.getBoundingClientRect()
        
        // 模拟人脸位置（通常在画面中央偏上）
        const faceWidth = 120
        const faceHeight = 150
        const x = (rect.width - faceWidth) / 2 + (Math.random() - 0.5) * 100
        const y = rect.height * 0.3 + (Math.random() - 0.5) * 80
        
        faceBoxStyle.value = {
          left: Math.max(10, Math.min(x, rect.width - faceWidth - 10)) + 'px',
          top: Math.max(10, Math.min(y, rect.height - faceHeight - 10)) + 'px',
          width: faceWidth + 'px',
          height: faceHeight + 'px'
        }
        
        // 随机置信度
        faceConfidence.value = Math.floor(Math.random() * 15) + 85 // 85-99%
        
        faceDetected.value = true
        
        // 3秒后隐藏检测框
        setTimeout(() => {
          faceDetected.value = false
        }, 3000)
      }
    }

    // 监听人像检测开关
    const toggleFaceDetection = () => {
      if (enableFaceDetection.value && cameraActive.value) {
        startFaceDetection()
      } else {
        stopFaceDetection()
      }
    }

    // 组件挂载时获取摄像头列表
    onMounted(() => {
      getCameraDevices()
    })

    // 组件卸载时关闭摄像头和录制
    onUnmounted(() => {
      if (recording.value) {
        stopRecording()
      }
      if (recordingTimer) {
        clearInterval(recordingTimer)
      }
      if (faceDetectionTimer) {
        clearInterval(faceDetectionTimer)
      }
      stopCamera()
      if (recordedVideo.value) {
        URL.revokeObjectURL(recordedVideo.value)
      }
    })

    return {
      videoRef,
      canvasRef,
      cameraActive,
      startingCamera,
      capturedPhoto,
      recordedVideo,
      recording,
      mirrorMode,
      availableCameras,
      selectedCamera,
      uploading,
      showFocusAnimation,
      showFlash,
      focusStyle,
      recordingTime,
      showGrid,
      enableFaceDetection,
      faceDetected,
      faceBoxStyle,
      faceConfidence,
      startCamera,
      stopCamera,
      switchCamera,
      capturePhoto,
      downloadPhoto,
      retakePhoto,
      toggleRecording,
      retakeVideo,
      downloadVideo,
      applyFilter,
      resetFilters,
      toggleFaceDetection
    }
  }
}
</script>

<style lang="scss" scoped>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body {
  height: 100%;
  margin: 0;
  padding: 0;
  overflow-x: hidden;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
  width: 100vw;
  margin: 0;
  padding: 0;
}

.app-container {
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 25%, #43e97b 50%, #38f9d7 75%, #a8edea 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
  margin: 0;
  padding: 0;
  display: flex;
  flex-direction: column;
  position: relative;
  overflow: hidden;
  
  &::before {
    content: '';
    position: absolute;
    top: -50%;
    left: -50%;
    width: 200%;
    height: 200%;
    background: radial-gradient(circle, rgba(255,255,255,0.1) 0%, transparent 70%);
    animation: rotate 30s linear infinite;
  }
}

@keyframes gradientShift {
  0% {
    background-position: 0% 50%;
  }
  50% {
    background-position: 100% 50%;
  }
  100% {
    background-position: 0% 50%;
  }
}

@keyframes rotate {
  from {
    transform: rotate(0deg);
  }
  to {
    transform: rotate(360deg);
  }
}

.app-header {
  background: rgba(255, 255, 255, 0.15);
  backdrop-filter: blur(20px);
  border-bottom: 1px solid rgba(255, 255, 255, 0.3);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
  position: relative;
  z-index: 10;
  
  .header-content {
    display: flex;
    align-items: center;
    justify-content: space-between;
    height: 100%;
    max-width: 1400px;
    margin: 0 auto;
    padding: 0 30px;
  }
  
  h1 {
    color: white;
    font-size: 28px;
    font-weight: 700;
    display: flex;
    align-items: center;
    gap: 12px;
    margin: 0;
    text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    
    .el-icon {
      font-size: 32px;
      animation: pulse 2s ease-in-out infinite;
    }
  }
  
  .header-nav {
    display: flex;
    gap: 15px;
    
    .nav-link {
      display: flex;
      align-items: center;
      gap: 8px;
      color: rgba(255, 255, 255, 0.9);
      text-decoration: none;
      padding: 10px 20px;
      border-radius: 12px;
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      font-size: 15px;
      font-weight: 500;
      background: rgba(255, 255, 255, 0.1);
      border: 1px solid rgba(255, 255, 255, 0.2);
      
      &:hover {
        color: white;
        background: rgba(255, 255, 255, 0.25);
        transform: translateY(-2px);
        box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
      }
      
      &.router-link-active {
        color: white;
        background: rgba(255, 255, 255, 0.3);
        box-shadow: 0 4px 15px rgba(0, 0, 0, 0.2);
      }
      
      &.back-home {
        background: linear-gradient(135deg, rgba(255, 255, 255, 0.2), rgba(255, 255, 255, 0.1));
        border: 1px solid rgba(255, 255, 255, 0.3);
        
        &:hover {
          background: linear-gradient(135deg, rgba(255, 255, 255, 0.35), rgba(255, 255, 255, 0.25));
          transform: translateY(-3px);
          box-shadow: 0 10px 25px rgba(0, 0, 0, 0.2);
        }
      }
    }
  }
}

@keyframes pulse {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.app-main {
  padding: 20px;
  overflow-y: auto;
  flex: 1;
  min-height: 0;
}

.camera-capture {
  .camera-panel, .photo-panel {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 20px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 12px 48px rgba(0, 0, 0, 0.15);
    }
    
    :deep(.el-card__header) {
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.1), rgba(67, 233, 123, 0.1));
      border-bottom: 1px solid rgba(79, 172, 254, 0.2);
      padding: 20px;
    }
    
    :deep(.el-card__body) {
      padding: 25px;
    }
    
    .card-header {
      display: flex;
      align-items: center;
      gap: 12px;
      font-weight: 700;
      font-size: 18px;
      color: #4facfe;
      
      .el-icon {
        font-size: 24px;
        animation: iconPulse 2s ease-in-out infinite;
      }
    }
  }

  .camera-container {
    position: relative;
    width: 100%;
    height: 300px;
    background: linear-gradient(135deg, #1e3c72 0%, #2a5298 100%);
    border-radius: 12px;
    overflow: hidden;
    margin-bottom: 20px;
    transition: all 0.3s ease;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);

    &.camera-starting {
      background: linear-gradient(135deg, #4facfe 0%, #43e97b 100%);
      animation: containerPulse 2s infinite;
    }

    &.camera-active {
      background: #000;
      box-shadow: 0 8px 32px rgba(0, 0, 0, 0.3);
      
      &::before {
        content: '';
        position: absolute;
        top: -2px;
        left: -2px;
        right: -2px;
        bottom: -2px;
        background: linear-gradient(45deg, #00ff88, #00aaff, #ff00aa, #ffaa00);
        border-radius: 14px;
        z-index: -1;
        animation: borderGlow 3s linear infinite;
      }
    }

    .camera-video {
      width: 100%;
      height: 100%;
      object-fit: cover;
      opacity: 0;
      transition: opacity 0.5s ease;
      
      &.video-active {
        opacity: 1;
      }
      
      &.mirrored {
        transform: scaleX(-1);
      }
    }

    .camera-placeholder {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      justify-content: center;
      align-items: center;
      background: linear-gradient(135deg, rgba(255,255,255,0.1) 0%, rgba(255,255,255,0.05) 100%);
      backdrop-filter: blur(10px);

      .placeholder-content {
        text-align: center;
        color: rgba(255, 255, 255, 0.9);
        
        .camera-icon {
          font-size: 64px;
          margin-bottom: 20px;
          color: rgba(255, 255, 255, 0.8);
          animation: iconFloat 3s ease-in-out infinite;
        }
        
        .placeholder-text {
          font-size: 16px;
          margin-bottom: 20px;
          font-weight: 500;
        }
        
        .placeholder-decoration {
          display: flex;
          justify-content: center;
          gap: 8px;
          
          .decoration-circle {
            width: 8px;
            height: 8px;
            border-radius: 50%;
            background: rgba(255, 255, 255, 0.6);
            animation: decorationPulse 1.5s ease-in-out infinite;
            
            &:nth-child(2) {
              animation-delay: 0.3s;
            }
            
            &:nth-child(3) {
              animation-delay: 0.6s;
            }
          }
        }
      }
    }

    .camera-starting-animation {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      display: flex;
      justify-content: center;
      align-items: center;
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.9) 0%, rgba(67, 233, 123, 0.9) 100%);
      backdrop-filter: blur(10px);

      .starting-content {
        text-align: center;
        color: white;
        
        .loading-spinner {
          width: 50px;
          height: 50px;
          border: 4px solid rgba(255, 255, 255, 0.3);
          border-top: 4px solid white;
          border-radius: 50%;
          animation: spin 1s linear infinite;
          margin: 0 auto 20px;
        }
        
        .starting-text {
          font-size: 16px;
          font-weight: 500;
          margin-bottom: 20px;
        }
        
        .starting-waves {
          display: flex;
          justify-content: center;
          gap: 4px;
          
          .wave {
            width: 4px;
            height: 20px;
            background: white;
            border-radius: 2px;
            animation: waveAnimation 1.2s ease-in-out infinite;
            
            &:nth-child(2) {
              animation-delay: 0.2s;
            }
            
            &:nth-child(3) {
              animation-delay: 0.4s;
            }
          }
        }
      }
    }

    .camera-frame {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
      z-index: 5;
      
      .frame-corner {
        position: absolute;
        width: 20px;
        height: 20px;
        border: 2px solid rgba(0, 255, 136, 0.8);
        
        &.top-left {
          top: 10px;
          left: 10px;
          border-right: none;
          border-bottom: none;
        }
        
        &.top-right {
          top: 10px;
          right: 10px;
          border-left: none;
          border-bottom: none;
        }
        
        &.bottom-left {
          bottom: 10px;
          left: 10px;
          border-right: none;
          border-top: none;
        }
        
        &.bottom-right {
          bottom: 10px;
          right: 10px;
          border-left: none;
          border-top: none;
        }
      }
    }

    .camera-grid {
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      pointer-events: none;
      z-index: 3;
      
      .grid-line {
        position: absolute;
        background: rgba(255, 255, 255, 0.3);
        
        &.vertical {
          width: 1px;
          height: 100%;
          
          &.left {
            left: 33.33%;
          }
          
          &.right {
            right: 33.33%;
          }
        }
        
        &.horizontal {
          height: 1px;
          width: 100%;
          
          &.top {
            top: 33.33%;
          }
          
          &.bottom {
            bottom: 33.33%;
          }
        }
      }
    }

  @keyframes containerPulse {
    0%, 100% {
      transform: scale(1);
    }
    50% {
      transform: scale(1.02);
    }
  }

  @keyframes borderGlow {
    0% {
      background-position: 0% 50%;
    }
    50% {
      background-position: 100% 50%;
    }
    100% {
      background-position: 0% 50%;
    }
  }

  @keyframes iconFloat {
    0%, 100% {
      transform: translateY(0);
    }
    50% {
      transform: translateY(-10px);
    }
  }

  @keyframes decorationPulse {
    0%, 100% {
      opacity: 0.6;
      transform: scale(1);
    }
    50% {
      opacity: 1;
      transform: scale(1.2);
    }
  }

  @keyframes spin {
    0% {
      transform: rotate(0deg);
    }
    100% {
      transform: rotate(360deg);
    }
  }

  @keyframes waveAnimation {
    0%, 100% {
      transform: scaleY(1);
    }
    50% {
      transform: scaleY(0.5);
    }
  }

  .camera-controls {
    text-align: center;
    margin-bottom: 20px;
    
    .el-button-group {
      display: flex;
      gap: 10px;
      
      .el-button {
        flex: 1;
        border-radius: 12px;
        font-weight: 600;
        padding: 12px 24px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        border: none;
        
        &.el-button--primary {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          box-shadow: 0 4px 15px rgba(67, 233, 123, 0.4);
          
          &:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(67, 233, 123, 0.6);
          }
          
          &:disabled {
            background: linear-gradient(135deg, #ccc 0%, #999 100%);
            opacity: 0.6;
          }
        }
        
        &.el-button--danger {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          box-shadow: 0 4px 15px rgba(67, 233, 123, 0.4);
          
          &:hover:not(:disabled) {
            transform: translateY(-2px);
            box-shadow: 0 6px 20px rgba(67, 233, 123, 0.6);
          }
          
          &:disabled {
            background: linear-gradient(135deg, #ccc 0%, #999 100%);
            opacity: 0.6;
          }
        }
        
        &.el-button--success {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
          
          &:hover:not(:disabled) {
            transform: translateY(-2px) scale(1.05);
            box-shadow: 0 6px 20px rgba(79, 172, 254, 0.6);
          }
        }
        
        &.el-button--warning {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          box-shadow: 0 4px 15px rgba(67, 233, 123, 0.4);
          color: white;
          
          &:hover:not(:disabled) {
            transform: translateY(-2px) scale(1.05);
            box-shadow: 0 6px 20px rgba(67, 233, 123, 0.6);
          }
        }
      }
    }
    
    .el-row {
      .el-button {
        border-radius: 12px;
        font-weight: 600;
        padding: 14px 24px;
        font-size: 16px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        border: none;
        
        &.el-button--success {
          background: linear-gradient(135deg, #00f2fe 0%, #4facfe 100%);
          box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
          
          &:hover:not(:disabled) {
            transform: translateY(-2px) scale(1.05);
            box-shadow: 0 6px 20px rgba(79, 172, 254, 0.6);
          }
        }
        
        &.el-button--warning {
          background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
          box-shadow: 0 4px 15px rgba(253, 203, 110, 0.4);
          color: #333;
          
          &:hover:not(:disabled) {
            transform: translateY(-2px) scale(1.05);
            box-shadow: 0 6px 20px rgba(253, 203, 110, 0.6);
          }
        }
        
        &.el-button--danger {
          background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
          box-shadow: 0 4px 15px rgba(67, 233, 123, 0.4);
          
          &:hover:not(:disabled) {
            transform: translateY(-2px) scale(1.05);
            box-shadow: 0 6px 20px rgba(67, 233, 123, 0.6);
          }
        }
      }
    }
  }

  .camera-settings {
    margin-top: 20px;
    padding: 20px;
    background: linear-gradient(135deg, rgba(79, 172, 254, 0.05), rgba(67, 233, 123, 0.05));
    border-radius: 15px;
    border: 1px solid rgba(79, 172, 254, 0.1);
    
    .el-divider {
      margin: 0 0 20px 0;
      
      :deep(.el-divider__text) {
        background: transparent;
        color: #4facfe;
        font-weight: 600;
      }
    }
    
    .el-form {
      :deep(.el-form-item) {
        margin-bottom: 18px;
        
        .el-form-item__label {
          color: #555;
          font-weight: 600;
        }
        
        .el-switch {
          &.is-checked .el-switch__core {
            background: linear-gradient(135deg, #4facfe 0%, #43e97b 100%);
          }
        }
        
        .el-select {
          .el-input__wrapper {
            border-radius: 10px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            transition: all 0.3s ease;
            
            &:hover {
              box-shadow: 0 4px 12px rgba(79, 172, 254, 0.3);
            }
          }
        }
      }
    }
  }

  .photo-placeholder {
    height: 300px;
    display: flex;
    align-items: center;
    justify-content: center;
    background: linear-gradient(135deg, rgba(79, 172, 254, 0.05), rgba(67, 233, 123, 0.05));
    border-radius: 15px;
    border: 2px dashed rgba(79, 172, 254, 0.3);
    
    :deep(.el-empty) {
      .el-empty__image {
        svg {
          fill: #4facfe;
        }
      }
      
      .el-empty__description {
        color: #4facfe;
        font-weight: 500;
      }
    }
  }

  .photo-result {
    animation: resultAppear 0.5s ease-out;
    
    .photo-preview {
      text-align: center;
      margin-bottom: 20px;
      padding: 15px;
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.05), rgba(67, 233, 123, 0.05));
      border-radius: 15px;

      img {
        max-width: 100%;
        max-height: 300px;
        object-fit: contain;
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        transition: all 0.3s ease;
        
        &:hover {
          transform: scale(1.02);
          box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
        }
      }
    }

    .photo-actions {
      text-align: center;
      margin-bottom: 20px;
      
      .el-button-group {
        display: flex;
        gap: 10px;
        
        .el-button {
          flex: 1;
          border-radius: 12px;
          font-weight: 600;
          padding: 12px 24px;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          border: none;
          
          &.el-button--primary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
            }
          }
          
          &.el-button--warning {
            background: linear-gradient(135deg, #ffeaa7 0%, #fdcb6e 100%);
            box-shadow: 0 4px 15px rgba(253, 203, 110, 0.4);
            color: #333;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(253, 203, 110, 0.6);
            }
          }
        }
      }
    }

    .processing-options {
      padding: 20px;
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.05), rgba(67, 233, 123, 0.05));
      border-radius: 15px;
      border: 1px solid rgba(79, 172, 254, 0.1);
      
      .el-divider {
        margin: 0 0 20px 0;
        
        :deep(.el-divider__text) {
          background: transparent;
          color: #4facfe;
          font-weight: 600;
        }
      }
      
      .filter-buttons {
        display: flex;
        flex-wrap: wrap;
        gap: 10px;
        
        .el-button {
          flex: 1;
          min-width: 80px;
          border-radius: 10px;
          font-weight: 600;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          background: linear-gradient(135deg, rgba(79, 172, 254, 0.1), rgba(67, 233, 123, 0.1));
          border: 1px solid rgba(79, 172, 254, 0.2);
          color: #4facfe;
          
          &:hover {
            transform: translateY(-2px);
            background: linear-gradient(135deg, #4facfe 0%, #43e97b 100%);
            color: white;
            box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
          }
          
          &.el-button--danger {
            background: linear-gradient(135deg, rgba(67, 233, 123, 0.1), rgba(56, 249, 215, 0.1));
            border: 1px solid rgba(67, 233, 123, 0.2);
            color: #43e97b;
            
            &:hover {
              background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
              color: white;
              box-shadow: 0 4px 12px rgba(67, 233, 123, 0.4);
            }
          }
        }
      }
    }
  }

  .video-result {
    animation: resultAppear 0.5s ease-out;
    
    .video-preview {
      text-align: center;
      margin-bottom: 20px;
      padding: 15px;
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.05), rgba(67, 233, 123, 0.05));
      border-radius: 15px;

      video {
        border-radius: 12px;
        box-shadow: 0 8px 24px rgba(0, 0, 0, 0.15);
        transition: all 0.3s ease;
        
        &:hover {
          box-shadow: 0 12px 32px rgba(0, 0, 0, 0.2);
        }
      }
    }

    .video-actions {
      text-align: center;
      margin-bottom: 20px;
      
      .el-button-group {
        display: flex;
        gap: 10px;
        
        .el-button {
          flex: 1;
          border-radius: 12px;
          font-weight: 600;
          padding: 12px 24px;
          transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
          border: none;
          
          &.el-button--primary {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            box-shadow: 0 4px 15px rgba(79, 172, 254, 0.4);
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(79, 172, 254, 0.6);
            }
          }
          
          &.el-button--warning {
            background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
            box-shadow: 0 4px 15px rgba(67, 233, 123, 0.4);
            color: white;
            
            &:hover {
              transform: translateY(-2px);
              box-shadow: 0 6px 20px rgba(67, 233, 123, 0.6);
            }
          }
        }
      }
    }

    .processing-options {
      margin-top: 20px;
      padding: 20px;
      background: linear-gradient(135deg, rgba(79, 172, 254, 0.05), rgba(67, 233, 123, 0.05));
      border-radius: 15px;
      border: 1px solid rgba(79, 172, 254, 0.1);
    }
  }

  // 对焦动画
  .focus-animation {
    position: absolute;
    width: 80px;
    height: 80px;
    pointer-events: none;
    z-index: 10;
    
    .focus-ring {
      width: 100%;
      height: 100%;
      border: 2px solid #00ff00;
      border-radius: 4px;
      animation: focusAnimation 0.8s ease-out;
      box-shadow: 0 0 10px rgba(0, 255, 0, 0.5);
    }
  }

  @keyframes focusAnimation {
    0% {
      transform: scale(1.5);
      opacity: 0;
    }
    50% {
      transform: scale(1.1);
      opacity: 1;
    }
    100% {
      transform: scale(1);
      opacity: 0.8;
    }
  }

  // 录制指示器
  .recording-indicator {
    position: absolute;
    top: 15px;
    left: 15px;
    display: flex;
    align-items: center;
    gap: 8px;
    background: rgba(0, 0, 0, 0.7);
    padding: 8px 12px;
    border-radius: 20px;
    color: white;
    font-size: 14px;
    font-weight: 600;
    z-index: 10;
    
    .recording-dot {
      width: 12px;
      height: 12px;
      background: #ff4444;
      border-radius: 50%;
      animation: recordingPulse 1s infinite;
    }
    
    .recording-text {
      color: #ff4444;
      font-weight: bold;
    }
    
    .recording-time {
      color: white;
      font-family: 'Courier New', monospace;
    }
  }

  @keyframes recordingPulse {
    0%, 100% {
      opacity: 1;
      transform: scale(1);
    }
    50% {
      opacity: 0.3;
      transform: scale(0.8);
    }
  }

  // 闪光效果
  .flash-effect {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    background: rgba(255, 255, 255, 0.8);
    z-index: 15;
    animation: flashEffect 0.2s ease-out;
    pointer-events: none;
  }

  @keyframes flashEffect {
    0% {
      opacity: 0;
    }
    50% {
      opacity: 1;
    }
    100% {
      opacity: 0;
    }
  }

  // 人像检测动画
  .face-detection {
    position: absolute;
    pointer-events: none;
    z-index: 12;
    animation: faceDetectionAppear 0.5s ease-out;
    
    .face-box {
      position: relative;
      width: 100%;
      height: 100%;
      border: 2px solid #00ff88;
      border-radius: 8px;
      background: rgba(0, 255, 136, 0.1);
      box-shadow: 
        0 0 20px rgba(0, 255, 136, 0.5),
        inset 0 0 20px rgba(0, 255, 136, 0.1);
      animation: faceBoxPulse 2s ease-in-out infinite;
      
      .face-corner {
        position: absolute;
        width: 20px;
        height: 20px;
        border: 3px solid #00ff88;
        
        &.top-left {
          top: -3px;
          left: -3px;
          border-right: none;
          border-bottom: none;
          animation: cornerGlow 1.5s ease-in-out infinite;
        }
        
        &.top-right {
          top: -3px;
          right: -3px;
          border-left: none;
          border-bottom: none;
          animation: cornerGlow 1.5s ease-in-out infinite 0.2s;
        }
        
        &.bottom-left {
          bottom: -3px;
          left: -3px;
          border-right: none;
          border-top: none;
          animation: cornerGlow 1.5s ease-in-out infinite 0.4s;
        }
        
        &.bottom-right {
          bottom: -3px;
          right: -3px;
          border-left: none;
          border-top: none;
          animation: cornerGlow 1.5s ease-in-out infinite 0.6s;
        }
      }
      
      .face-label {
        position: absolute;
        top: -35px;
        left: 50%;
        transform: translateX(-50%);
        background: linear-gradient(135deg, rgba(0, 255, 136, 0.9) 0%, rgba(0, 200, 100, 0.9) 100%);
        color: white;
        padding: 4px 12px;
        border-radius: 15px;
        font-size: 12px;
        font-weight: 600;
        white-space: nowrap;
        box-shadow: 0 2px 10px rgba(0, 255, 136, 0.3);
        animation: labelFloat 2s ease-in-out infinite;
        
        .face-text {
          margin-right: 8px;
        }
        
        .face-confidence {
          display: inline-block;
          background: rgba(255, 255, 255, 0.2);
          padding: 1px 6px;
          border-radius: 8px;
          font-size: 10px;
          font-weight: bold;
        }
      }
    }
  }

  .face-scanner {
    position: absolute;
    top: 0;
    left: 0;
    right: 0;
    bottom: 0;
    pointer-events: none;
    z-index: 8;
    overflow: hidden;
    
    .scanner-line {
      position: absolute;
      top: -2px;
      left: 0;
      right: 0;
      height: 2px;
      background: linear-gradient(90deg, 
        transparent 0%, 
        rgba(0, 255, 136, 0.3) 20%, 
        rgba(0, 255, 136, 0.8) 50%, 
        rgba(0, 255, 136, 0.3) 80%, 
        transparent 100%);
      box-shadow: 0 0 10px rgba(0, 255, 136, 0.6);
      animation: scannerMove 3s linear infinite;
      
      &::before {
        content: '';
        position: absolute;
        top: -1px;
        left: 0;
        right: 0;
        height: 4px;
        background: linear-gradient(90deg, 
          transparent 0%, 
          rgba(0, 255, 136, 0.1) 20%, 
          rgba(0, 255, 136, 0.3) 50%, 
          rgba(0, 255, 136, 0.1) 80%, 
          transparent 100%);
        filter: blur(2px);
      }
    }
  }

  @keyframes faceDetectionAppear {
    0% {
      opacity: 0;
      transform: scale(0.8);
    }
    100% {
      opacity: 1;
      transform: scale(1);
    }
  }

  @keyframes faceBoxPulse {
    0%, 100% {
      border-color: #00ff88;
      box-shadow: 
        0 0 20px rgba(0, 255, 136, 0.5),
        inset 0 0 20px rgba(0, 255, 136, 0.1);
    }
    50% {
      border-color: #00cc66;
      box-shadow: 
        0 0 30px rgba(0, 255, 136, 0.8),
        inset 0 0 30px rgba(0, 255, 136, 0.2);
    }
  }

  @keyframes cornerGlow {
    0%, 100% {
      border-color: #00ff88;
      box-shadow: 0 0 5px rgba(0, 255, 136, 0.5);
    }
    50% {
      border-color: #ffffff;
      box-shadow: 0 0 15px rgba(0, 255, 136, 1);
    }
  }

  @keyframes labelFloat {
    0%, 100% {
      transform: translateX(-50%) translateY(0);
    }
    50% {
      transform: translateX(-50%) translateY(-3px);
    }
  }

  @keyframes scannerMove {
    0% {
      top: -2px;
    }
    100% {
      top: 100%;
    }
  }
}

  @keyframes iconPulse {
    0%, 100% {
      transform: scale(1);
    }
    50% {
      transform: scale(1.1);
    }
  }
  
  @keyframes resultAppear {
    0% {
      opacity: 0;
      transform: translateY(20px);
    }
    100% {
      opacity: 1;
      transform: translateY(0);
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .camera-capture {
    :deep(.el-col) {
      margin-bottom: 20px;
    }
    
    .recording-indicator {
      top: 10px;
      left: 10px;
      padding: 6px 10px;
      font-size: 12px;
      
      .recording-dot {
        width: 10px;
        height: 10px;
      }
    }
    
    .focus-animation {
      width: 60px;
      height: 60px;
    }
  }
}
</style>

<style lang="scss" scoped>
/* Product-style camera workspace */
.app-container { min-height: 100vh; background: #f4f8fc; }
.app-header { height: 76px !important; background: linear-gradient(110deg,#0f6fd8,#1cbfa7) !important; border: 0; }
.header-content { max-width: 1440px; margin: 0 auto; height: 100%; padding: 0 28px; display:flex; align-items:center; justify-content:space-between; }
.header-content h1 { color:#fff; font-size:26px; font-weight:700; letter-spacing:0; }
.header-nav { display:flex; gap:10px; }
.nav-link { display:inline-flex; align-items:center; gap:7px; padding:10px 18px; border:1px solid rgba(255,255,255,.3); border-radius:10px; color:#fff; background:rgba(255,255,255,.12); text-decoration:none; }
.nav-link:hover,.nav-link.router-link-active { background:rgba(255,255,255,.25); }
.app-main { max-width:1440px; width:100%; margin:0 auto; padding:24px 28px 40px; }
.camera-capture :deep(.el-card) { border:1px solid #dfe8f2; border-radius:12px; box-shadow:0 8px 24px rgba(28,69,110,.07); overflow:hidden; }
.camera-capture :deep(.el-card__header) { padding:18px 22px; background:#f7fbff; border-bottom:1px solid #e3edf6; }
.card-header { color:#1769e0; font-size:18px; font-weight:650; }
.camera-container { border-radius:8px; background:#10253d; box-shadow:inset 0 0 0 1px rgba(255,255,255,.08); }
.photo-placeholder { min-height:380px; background:#fbfdff; border:1px dashed #c8d8e8; border-radius:8px; }
.placeholder-content { color:#91a2b5; }
.camera-controls { padding-top:18px; }
.camera-controls :deep(.el-button) { border-radius:8px; font-weight:600; }
.camera-settings :deep(.el-divider__text) { color:#71849a; font-size:13px; }
@media (max-width: 768px) { .header-content{padding:0 16px}.header-content h1{font-size:19px}.header-nav .nav-link{padding:9px}.nav-link:not(.back-home){font-size:0}.app-main{padding:16px 12px}.camera-capture :deep(.el-col){margin-bottom:16px} }
</style>
