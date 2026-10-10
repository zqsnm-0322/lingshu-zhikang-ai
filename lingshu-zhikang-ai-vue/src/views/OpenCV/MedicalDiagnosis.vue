<template>
  <div id="app">
    <el-container class="app-container">
      <el-header class="app-header">
        <div class="header-content">
          <h1>
            <el-icon><Monitor /></el-icon>
            摄像头图像采集与辅助分析
          </h1>
          <nav class="header-nav">
            <router-link to="/" class="nav-link back-home">
              <el-icon><HomeFilled /></el-icon>
              返回首页
            </router-link>
          </nav>
        </div>
      </el-header>
      <el-main class="app-main">
        <div class="medical-diagnosis">
          <el-row :gutter="20">
            <!-- 左侧: 图像/视频上传和摄像头 -->
            <el-col :span="12">
              <el-card class="upload-panel">
                <template #header>
                  <div class="card-header">
                    <el-icon><Upload /></el-icon>
                    <span>摄像头图像采集</span>
                  </div>
                </template>

                <!-- 选项卡: 上传文件 vs 摄像头拍摄 -->
                <el-tabs v-model="activeInputTab" @tab-change="handleInputTabChange">
                  <!-- 文件上传 -->
                  <el-tab-pane label="上传文件" name="upload">
                    <el-upload
                      class="upload-demo"
                      drag
                      :auto-upload="false"
                      :on-change="handleFileChange"
                      :show-file-list="false"
                      accept="image/*,video/*"
                      multiple
                    >
                      <el-icon class="el-icon--upload"><UploadFilled /></el-icon>
                      <div class="el-upload__text">
                        将图片或视频拖到此处，或<em>点击上传</em>
                      </div>
                      <template #tip>
                        <div class="el-upload__tip">
                          支持 jpg/png/gif/mp4/avi 格式，用于体检报告分析
                        </div>
                      </template>
                    </el-upload>
                  </el-tab-pane>

                  <!-- 摄像头拍摄 -->
                  <el-tab-pane label="摄像头拍摄" name="camera">
                    <div class="camera-section">
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
                            <p class="placeholder-text">启动真实摄像头采集图像</p>
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
                          </div>
                        </div>

                        <!-- 摄像头边框装饰 -->
                        <div v-if="cameraActive" class="camera-frame">
                          <div class="frame-corner top-left"></div>
                          <div class="frame-corner top-right"></div>
                          <div class="frame-corner bottom-left"></div>
                          <div class="frame-corner bottom-right"></div>
                        </div>

                        <!-- 网格线辅助 -->
                        <div v-if="cameraActive && showGrid" class="camera-grid">
                          <div class="grid-line vertical left"></div>
                          <div class="grid-line vertical right"></div>
                          <div class="grid-line horizontal top"></div>
                          <div class="grid-line horizontal bottom"></div>
                        </div>

                      </div>

                      <!-- 摄像头控制 -->
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
                              @click="captureForDiagnosis"
                              :disabled="!cameraActive"
                              size="large"
                              style="width: 100%;"
                            >
                              <el-icon><Camera /></el-icon>
                              拍照分析
                            </el-button>
                          </el-col>
                          <el-col :span="12">
                            <el-button 
                              type="warning" 
                              @click="startRealTimeDiagnosis"
                              :disabled="!cameraActive"
                              size="large"
                              style="width: 100%;"
                            >
                              <el-icon><Monitor /></el-icon>
                              拍照并分析
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
                    </div>
                  </el-tab-pane>
                </el-tabs>

                <!-- 已上传的文件预览 -->
                <div v-if="uploadedFiles.length > 0" class="uploaded-files">
                  <h4>已上传文件</h4>
                  <div class="file-list">
                    <div 
                      v-for="(file, index) in uploadedFiles" 
                      :key="index"
                      class="file-item"
                      :class="{ 'active': selectedFileIndex === index }"
                      @click="selectFile(index)"
                    >
                      <div class="file-preview">
                        <img v-if="file.type === 'image'" :src="file.url" alt="预览" />
                        <video v-else :src="file.url" muted></video>
                      </div>
                      <div class="file-info">
                        <div class="file-name">{{ file.name }}</div>
                        <div class="file-type">{{ file.type === 'image' ? '图片' : '视频' }}</div>
                      </div>
                      <el-button 
                        type="danger" 
                        size="small" 
                        circle 
                        @click.stop="removeFile(index)"
                      >
                        <el-icon><Delete /></el-icon>
                      </el-button>
                    </div>
                  </div>
                </div>

                <!-- 诊断按钮 -->
                <div v-if="uploadedFiles.length > 0 || capturedImage" class="diagnosis-actions">
                  <el-button 
                    type="primary" 
                    size="large"
                    @click="startDiagnosis"
                    :loading="diagnosing"
                    style="width: 100%;"
                  >
                    <el-icon><Monitor /></el-icon>
                    开始 AI 智能分析
                  </el-button>
                </div>
              </el-card>
            </el-col>

            <!-- 右侧: 诊断结果 -->
            <el-col :span="12">
              <el-card class="result-panel">
                <template #header>
                  <div class="card-header">
                    <el-icon><Document /></el-icon>
                    <span>AI 图像观察结果（非医疗诊断）</span>
                  </div>
                </template>

                <!-- 诊断结果 -->
                <el-alert
                  v-if="analysisError && !diagnosing"
                  :title="analysisError"
                  description="摄像头画面和拍照是真实的；单张照片不能完成体检或判断疾病。如有不适，请使用正规医疗设备并咨询医生。"
                  type="error"
                  :closable="false"
                  show-icon
                  style="margin-bottom: 18px"
                />
                <div v-if="!diagnosisResult && !diagnosing" class="empty-result">
                  <el-empty description="请上传图片或使用摄像头拍照，进行非诊断性图像观察">
                    <template #image>
                      <div class="empty-icon">
                        <el-icon><Monitor /></el-icon>
                      </div>
                    </template>
                  </el-empty>
                </div>

                <!-- 诊断中 -->
                <div v-if="diagnosing" class="diagnosing-animation" v-loading="true" element-loading-text="AI正在分析体检报告...">
                  <div class="diagnosis-progress">
                    <div class="progress-item">
                      <el-icon><View /></el-icon>
                      <span>报告内容识别</span>
                      <el-progress :percentage="progressSteps.faceDetection" :show-text="false" />
                    </div>
                    <div class="progress-item">
                      <el-icon><Sunny /></el-icon>
                      <span>数据分析</span>
                      <el-progress :percentage="progressSteps.colorAnalysis" :show-text="false" />
                    </div>
                    <div class="progress-item">
                      <el-icon><Document /></el-icon>
                      <span>生成分析报告</span>
                      <el-progress :percentage="progressSteps.reportGeneration" :show-text="false" />
                    </div>
                  </div>
                </div>

                <!-- 诊断报告 -->
                <div v-if="diagnosisResult" class="diagnosis-report">
                  <!-- 总体诊断 -->
                  <div class="diagnosis-summary">
                    <div class="diagnosis-title">
                      <el-icon><Warning /></el-icon>
                      <h3>图像观察: {{ diagnosisResult.mainDiagnosis }}</h3>
                    </div>
                    <div class="confidence-score">
                      <span>模型置信度（不代表诊断准确率）:</span>
                      <el-progress 
                        :percentage="diagnosisResult.confidence" 
                        :color="getConfidenceColor(diagnosisResult.confidence)"
                        :stroke-width="8"
                      />
                    </div>
                  </div>

                  <!-- 症状分析 -->
                  <div class="symptoms-analysis">
                    <h4><el-icon><List /></el-icon>症状分析</h4>
                    <div class="symptom-categories">
                      <!-- 气虚症状 -->
                      <div class="symptom-category">
                        <div class="category-header">
                          <el-icon><Cloudy /></el-icon>
                          <span>气虚症状</span>
                        </div>
                        <div class="symptom-list">
                          <div 
                            v-for="symptom in diagnosisResult.qiDeficiencySymptoms" 
                            :key="symptom"
                            class="symptom-item"
                          >
                            <el-icon><Check /></el-icon>
                            <span>{{ symptom }}</span>
                          </div>
                        </div>
                      </div>

                      <!-- 血虚症状 -->
                      <div class="symptom-category">
                        <div class="category-header">
                          <el-icon><Sunny /></el-icon>
                          <span>血虚症状</span>
                        </div>
                        <div class="symptom-list">
                          <div 
                            v-for="symptom in diagnosisResult.bloodDeficiencySymptoms" 
                            :key="symptom"
                            class="symptom-item"
                          >
                            <el-icon><Check /></el-icon>
                            <span>{{ symptom }}</span>
                          </div>
                        </div>
                      </div>

                      <!-- 气血不足综合症状 -->
                      <div class="symptom-category">
                        <div class="category-header">
                          <el-icon><Warning /></el-icon>
                          <span>气血不足综合症状</span>
                        </div>
                        <div class="symptom-list">
                          <div 
                            v-for="symptom in diagnosisResult.combinedSymptoms" 
                            :key="symptom"
                            class="symptom-item"
                          >
                            <el-icon><Check /></el-icon>
                            <span>{{ symptom }}</span>
                          </div>
                        </div>
                      </div>
                    </div>
                  </div>

                  <!-- 操作按钮 -->
                  <div class="report-actions">
                    <el-dropdown @command="handleDownloadCommand">
                      <el-button type="primary">
                        <el-icon><Download /></el-icon>
                        下载分析报告
                        <el-icon class="el-icon--right"><ArrowDown /></el-icon>
                      </el-button>
                      <template #dropdown>
                        <el-dropdown-menu>
                          <el-dropdown-item command="pdf">
                            <el-icon><Document /></el-icon>
                            下载 PDF 报告
                          </el-dropdown-item>
                          <el-dropdown-item command="text">
                            <el-icon><DocumentCopy /></el-icon>
                            下载文本报告
                          </el-dropdown-item>
                        </el-dropdown-menu>
                      </template>
                    </el-dropdown>
                    
                    <el-button type="success" @click="shareReport">
                      <el-icon><Share /></el-icon>
                      分享报告
                    </el-button>
                    <el-button @click="resetDiagnosis">
                      <el-icon><RefreshRight /></el-icon>
                      重新分析
                    </el-button>
                  </div>
                </div>
              </el-card>
            </el-col>
          </el-row>

          <!-- 隐藏的canvas用于图像处理 -->
          <canvas ref="canvasRef" style="display: none;"></canvas>
        </div>
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { ref, reactive, onMounted, onUnmounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { 
  Monitor, Picture, VideoPlay, VideoCamera, Upload, UploadFilled, 
  Camera, VideoCameraFilled, Delete, Document, View, Sunny, 
  Warning, List, Cloudy, Check, MagicStick, ArrowRight, 
  Download, Share, RefreshRight, ArrowDown, DocumentCopy, Memo, HomeFilled
} from '@element-plus/icons-vue'
import axios from 'axios'

export default {
  name: 'MedicalDiagnosis',
  components: {
    Monitor, Picture, VideoPlay, VideoCamera, Upload, UploadFilled,
    Camera, VideoCameraFilled, Delete, Document, View, Sunny,
    Warning, List, Cloudy, Check, MagicStick, ArrowRight,
    Download, Share, RefreshRight, ArrowDown, DocumentCopy, Memo, HomeFilled
  },
  setup() {
    const activeInputTab = ref('upload')
    const uploadedFiles = ref([])
    const selectedFileIndex = ref(-1)
    const capturedImage = ref('')
    const diagnosing = ref(false)
    const diagnosisResult = ref(null)
    const analysisError = ref('')
    
    // 摄像头相关
    const videoRef = ref(null)
    const canvasRef = ref(null)
    const cameraActive = ref(false)
    const startingCamera = ref(false)
    const mirrorMode = ref(true)
    const faceDetected = ref(false)
    const showGrid = ref(false)
    const enableFaceDetection = ref(true)
    const availableCameras = ref([])
    const selectedCamera = ref('')
    let currentStream = null
    let faceDetectionTimer = null

    // 诊断进度
    const progressSteps = reactive({
      faceDetection: 0,
      colorAnalysis: 0,
      reportGeneration: 0
    })

    // 处理文件上传
    const handleFileChange = (file) => {
      const fileObj = file.raw || file
      const isImage = fileObj.type.startsWith('image/')
      const isVideo = fileObj.type.startsWith('video/')
      
      if (!isImage && !isVideo) {
        ElMessage.error('请上传图片或视频文件')
        return
      }

      const reader = new FileReader()
      reader.onload = (e) => {
        uploadedFiles.value.push({
          name: fileObj.name,
          type: isImage ? 'image' : 'video',
          url: e.target.result,
          file: fileObj
        })
        selectedFileIndex.value = uploadedFiles.value.length - 1
      }
      reader.readAsDataURL(fileObj)
    }

    // 选择文件
    const selectFile = (index) => {
      selectedFileIndex.value = index
    }

    // 删除文件
    const removeFile = (index) => {
      uploadedFiles.value.splice(index, 1)
      if (selectedFileIndex.value >= uploadedFiles.value.length) {
        selectedFileIndex.value = uploadedFiles.value.length - 1
      }
    }

    // 启动摄像头
    const startCamera = async () => {
      startingCamera.value = true
      
      try {
        if (!navigator.mediaDevices || !navigator.mediaDevices.getUserMedia) {
          throw new Error('您的浏览器不支持摄像头功能')
        }

        const constraints = {
          video: {
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
          ElMessage.success('摄像头启动成功')
        }
      } catch (error) {
        console.error('启动摄像头失败:', error)
        ElMessage.error('启动摄像头失败，请检查权限设置')
      } finally {
        startingCamera.value = false
      }
    }

    // 关闭摄像头
    const stopCamera = () => {
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

    // 切换摄像头
    const switchCamera = async () => {
      if (cameraActive.value) {
        stopCamera()
        await new Promise(resolve => setTimeout(resolve, 100))
        await startCamera()
      }
    }

    // 切换人像检测
    const toggleFaceDetection = () => {
      if (enableFaceDetection.value && cameraActive.value) {
        startFaceDetection()
      } else {
        stopFaceDetection()
      }
    }

    // 人脸检测
    const startFaceDetection = () => {
      faceDetectionTimer = setInterval(() => {
        // 模拟人脸检测
        faceDetected.value = Math.random() > 0.3
      }, 2000)
    }

    const stopFaceDetection = () => {
      if (faceDetectionTimer) {
        clearInterval(faceDetectionTimer)
        faceDetectionTimer = null
      }
      faceDetected.value = false
    }

    // 拍照用于诊断
    const captureForDiagnosis = () => {
      if (!videoRef.value || !canvasRef.value) return

      const video = videoRef.value
      const canvas = canvasRef.value
      const context = canvas.getContext('2d')

      canvas.width = video.videoWidth
      canvas.height = video.videoHeight

      if (mirrorMode.value) {
        context.scale(-1, 1)
        context.drawImage(video, -canvas.width, 0, canvas.width, canvas.height)
      } else {
        context.drawImage(video, 0, 0, canvas.width, canvas.height)
      }

      capturedImage.value = canvas.toDataURL('image/jpeg', 0.9)
      ElMessage.success('拍照成功，可以开始分析了')
    }

    // 开始实时诊断
    const startRealTimeDiagnosis = () => {
      if (!cameraActive.value) {
        ElMessage.error('请先启动摄像头')
        return
      }
      
      captureForDiagnosis()
      setTimeout(() => {
        startDiagnosis()
      }, 500)
    }

    // Base64转Blob工具函数
    const base64ToBlob = (base64) => {
      const parts = base64.split(';base64,')
      const contentType = parts[0].split(':')[1]
      const raw = window.atob(parts[1])
      const rawLength = raw.length
      const uInt8Array = new Uint8Array(rawLength)
      
      for (let i = 0; i < rawLength; ++i) {
        uInt8Array[i] = raw.charCodeAt(i)
      }
      
      return new Blob([uInt8Array], { type: contentType })
    }

    // 开始诊断（真实AI分析）
    const startDiagnosis = async () => {
      if (uploadedFiles.value.length === 0 && !capturedImage.value) {
        ElMessage.error('请先上传文件或拍摄照片')
        return
      }

      diagnosing.value = true
      diagnosisResult.value = null
      analysisError.value = ''

      // 重置进度
      progressSteps.faceDetection = 0
      progressSteps.colorAnalysis = 0
      progressSteps.reportGeneration = 0

      try {
        // 准备上传的文件
        const formData = new FormData()
        
        if (capturedImage.value) {
          // 将Base64转为Blob
          const blob = base64ToBlob(capturedImage.value)
          formData.append('file', blob, 'capture.jpg')
        } else if (uploadedFiles.value.length > 0) {
          formData.append('file', uploadedFiles.value[selectedFileIndex.value].file)
        }

        // 显示上传进度
        progressSteps.faceDetection = 20
        ElMessage.info('正在上传图像...')

        // 调用真实的AI诊断接口
        const response = await axios.post('/api/image-diagnosis/analyze', formData, {
          headers: {
            'Content-Type': 'multipart/form-data'
          },
          onUploadProgress: (progressEvent) => {
            // 更新上传进度 (0-30%)
            const percentCompleted = Math.round(
              (progressEvent.loaded * 30) / progressEvent.total
            )
            progressSteps.faceDetection = percentCompleted
          }
        })

        // 上传完成，开始AI分析
        progressSteps.faceDetection = 30
        ElMessage.info('AI正在分析图像...')

        // 模拟分析进度
        for (let i = 30; i <= 100; i += 10) {
          progressSteps.faceDetection = i
          await new Promise(resolve => setTimeout(resolve, 200))
        }

        // 数据分析阶段
        for (let i = 0; i <= 100; i += 20) {
          progressSteps.colorAnalysis = i
          await new Promise(resolve => setTimeout(resolve, 150))
        }

        // 报告生成阶段
        for (let i = 0; i <= 100; i += 25) {
          progressSteps.reportGeneration = i
          await new Promise(resolve => setTimeout(resolve, 100))
        }

        if (response.data.success) {
          // 显示AI诊断结果
          diagnosisResult.value = response.data.diagnosis
          
          console.log('AI诊断结果:', response.data.diagnosis)
          
          ElMessage.success('AI健康分析完成！')
        } else {
          throw new Error(response.data.message || '诊断失败')
        }
        
      } catch (error) {
        console.error('诊断失败:', error)
        const message = error.response?.data?.message || error.message || '图像分析服务暂不可用'
        analysisError.value = message
        ElMessage.error(message)
      } finally {
        diagnosing.value = false
      }
    }

    // 模拟AI分析过程
    const simulateAIAnalysis = async () => {
      // 第一阶段: 报告内容识别
      for (let i = 0; i <= 100; i += 10) {
        progressSteps.faceDetection = i
        await new Promise(resolve => setTimeout(resolve, 100))
      }

      // 第二阶段: 数据分析
      for (let i = 0; i <= 100; i += 15) {
        progressSteps.colorAnalysis = i
        await new Promise(resolve => setTimeout(resolve, 120))
      }

      // 第三阶段: 生成分析报告
      for (let i = 0; i <= 100; i += 20) {
        progressSteps.reportGeneration = i
        await new Promise(resolve => setTimeout(resolve, 80))
      }
    }

    // 获取置信度颜色
    const getConfidenceColor = (confidence) => {
      if (confidence >= 90) return '#67c23a'
      if (confidence >= 70) return '#e6a23c'
      return '#f56c6c'
    }

    // 下载诊断报告为 PDF
    const downloadReport = () => {
      if (!diagnosisResult.value) return

      // 创建一个新的窗口用于打印
      const printWindow = window.open('', '_blank')
      const reportContent = generatePDFContent()
      
      printWindow.document.write(reportContent)
      printWindow.document.close()
      
      // 等待内容加载完成后打印
      printWindow.onload = () => {
        printWindow.print()
        // 打印完成后关闭窗口
        setTimeout(() => {
          printWindow.close()
        }, 1000)
      }
      
      ElMessage.success('正在生成 PDF 分析报告...')
    }

    // 下载文本格式报告
    const downloadTextReport = () => {
      if (!diagnosisResult.value) return

      const reportContent = generateReportContent()
      const blob = new Blob([reportContent], { type: 'text/plain;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      
      const link = document.createElement('a')
      link.href = url
      link.download = `OpenCV体检分析_${new Date().toLocaleDateString()}.txt`
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      URL.revokeObjectURL(url)
      
      ElMessage.success('文本分析报告已下载')
    }

    // 生成报告内容（文本格式）
    const generateReportContent = () => {
      const result = diagnosisResult.value
      const date = new Date().toLocaleString()
      
      return `OpenCV 体检分析报告
================

分析时间: ${date}
分析结果: ${result.mainDiagnosis}
分析准确度: ${result.confidence}%

气虚症状:
${result.qiDeficiencySymptoms.map(s => `• ${s}`).join('\n')}

血虚症状:
${result.bloodDeficiencySymptoms.map(s => `• ${s}`).join('\n')}

气血不足综合症状:
${result.combinedSymptoms.map(s => `• ${s}`).join('\n')}

注意事项:
本分析结果仅供参考，具体治疗方案请咨询专业医师。`
    }

    // 生成 PDF 内容（HTML 格式）
    const generatePDFContent = () => {
      const result = diagnosisResult.value
      const date = new Date().toLocaleString()
      
      return `<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>OpenCV体检分析报告</title>
    <style>
        @media print {
            @page {
                margin: 20mm;
                size: A4;
            }
            body {
                -webkit-print-color-adjust: exact;
                color-adjust: exact;
            }
        }
        
        body {
            font-family: 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
            line-height: 1.6;
            color: #333;
            max-width: 800px;
            margin: 0 auto;
            padding: 20px;
            background: white;
        }
        
        .header {
            text-align: center;
            border-bottom: 3px solid #409eff;
            padding-bottom: 20px;
            margin-bottom: 30px;
        }
        
        .header h1 {
            color: #409eff;
            font-size: 28px;
            margin: 0;
            font-weight: bold;
        }
        
        .report-info {
            background: #f8f9fa;
            padding: 20px;
            border-radius: 8px;
            margin-bottom: 30px;
            border-left: 4px solid #409eff;
        }
        
        .diagnosis-main {
            background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
            padding: 20px;
            border-radius: 12px;
            margin-bottom: 30px;
            text-align: center;
        }
        
        .diagnosis-main h2 {
            color: #333;
            font-size: 24px;
            margin: 0 0 10px 0;
        }
        
        .confidence {
            font-size: 18px;
            color: #666;
        }
        
        .symptoms-section {
            margin-bottom: 25px;
        }
        
        .symptoms-section h3 {
            color: #409eff;
            font-size: 18px;
            margin-bottom: 15px;
            padding-bottom: 5px;
            border-bottom: 2px solid #409eff;
        }
        
        .symptom-category {
            background: #f8f9fa;
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 15px;
            border-left: 4px solid #67c23a;
        }
        
        .category-title {
            font-weight: bold;
            color: #333;
            margin-bottom: 10px;
            font-size: 16px;
        }
        
        .symptom-list {
            list-style: none;
            padding: 0;
            margin: 0;
        }
        
        .symptom-list li {
            padding: 5px 0;
            position: relative;
            padding-left: 20px;
        }
        
        .symptom-list li:before {
            content: "✓";
            color: #67c23a;
            font-weight: bold;
            position: absolute;
            left: 0;
        }
        
        .footer {
            margin-top: 40px;
            padding-top: 20px;
            border-top: 1px solid #e0e0e0;
            text-align: center;
            color: #666;
            font-size: 14px;
        }
        
        .footer .warning {
            background: #fff3cd;
            border: 1px solid #ffeaa7;
            border-radius: 6px;
            padding: 15px;
            margin-top: 20px;
            color: #856404;
        }
    </style>
</head>
<body>
    <div class="header">
        <h1>OpenCV 体检分析报告</h1>
    </div>
    
    <div class="report-info">
        <p><strong>分析时间:</strong>${date}</p>
        <p><strong>报告编号:</strong>HR-${Date.now()}</p>
    </div>
    
    <div class="diagnosis-main">
        <h2>分析结果: ${result.mainDiagnosis}</h2>
        <div class="confidence">分析准确度: ${result.confidence}%</div>
    </div>
    
    <div class="symptoms-section">
        <h3>症状分析</h3>
        
        <div class="symptom-category">
            <div class="category-title">气虚症状</div>
            <ul class="symptom-list">
                ${result.qiDeficiencySymptoms.map(symptom => `<li>${symptom}</li>`).join('')}
            </ul>
        </div>
        
        <div class="symptom-category">
            <div class="category-title">血虚症状</div>
            <ul class="symptom-list">
                ${result.bloodDeficiencySymptoms.map(symptom => `<li>${symptom}</li>`).join('')}
            </ul>
        </div>
        
        <div class="symptom-category">
            <div class="category-title">气血不足综合症状</div>
            <ul class="symptom-list">
                ${result.combinedSymptoms.map(symptom => `<li>${symptom}</li>`).join('')}
            </ul>
        </div>
    </div>
    
    <div class="footer">
        <div class="warning">
            <strong>注意事项:</strong><br>
            本分析结果仅供参考，具体治疗方案请咨询专业医师。
        </div>
        <p style="margin-top: 20px;">报告生成时间: ${new Date().toLocaleString()}</p>
    </div>
</body>
</html>`
    }

    // 分享报告
    const shareReport = () => {
      if (navigator.share) {
        navigator.share({
          title: 'OpenCV 体检分析',
          text: `分析结果: ${diagnosisResult.value.mainDiagnosis}`,
          url: window.location.href
        })
      } else {
        // 复制到剪贴板
        const reportText = generateReportContent()
        navigator.clipboard.writeText(reportText).then(() => {
          ElMessage.success('报告内容已复制到剪贴板')
        })
      }
    }

    // 重置诊断
    const resetDiagnosis = () => {
      diagnosisResult.value = null
      analysisError.value = ''
      uploadedFiles.value = []
      capturedImage.value = ''
      selectedFileIndex.value = -1
    }

    // 处理输入标签页切换
    const handleInputTabChange = (tabName) => {
      activeInputTab.value = tabName
      if (tabName === 'upload' && cameraActive.value) {
        stopCamera()
      }
    }

    // 处理下载命令
    const handleDownloadCommand = (command) => {
      if (command === 'pdf') {
        downloadReport()
      } else if (command === 'text') {
        downloadTextReport()
      }
    }

    // 组件卸载时清理资源
    onUnmounted(() => {
      stopCamera()
      if (faceDetectionTimer) {
        clearInterval(faceDetectionTimer)
      }
    })

    // 组件挂载时获取摄像头列表
    onMounted(() => {
      getCameraDevices()
    })

    return {
      activeInputTab,
      uploadedFiles,
      selectedFileIndex,
      capturedImage,
      diagnosing,
      diagnosisResult,
      analysisError,
      videoRef,
      canvasRef,
      cameraActive,
      startingCamera,
      mirrorMode,
      faceDetected,
      showGrid,
      enableFaceDetection,
      availableCameras,
      selectedCamera,
      progressSteps,
      handleFileChange,
      selectFile,
      removeFile,
      startCamera,
      stopCamera,
      switchCamera,
      toggleFaceDetection,
      captureForDiagnosis,
      startRealTimeDiagnosis,
      startDiagnosis,
      getConfidenceColor,
      downloadReport,
      downloadTextReport,
      shareReport,
      resetDiagnosis,
      handleInputTabChange,
      handleDownloadCommand
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

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '微软雅黑', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
  width: 100vw;
}

.app-container {
  min-height: 100vh;
  width: 100vw;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 25%, #43e97b 50%, #38f9d7 75%, #a8edea 100%);
  background-size: 400% 400%;
  animation: gradientShift 15s ease infinite;
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
  padding: 30px;
  overflow-y: auto;
  flex: 1;
  position: relative;
  z-index: 1;
}

.medical-diagnosis {
  max-width: 1400px;
  margin: 0 auto;
  
  .upload-panel, .result-panel {
    background: rgba(255, 255, 255, 0.95);
    backdrop-filter: blur(20px);
    border: 1px solid rgba(255, 255, 255, 0.3);
    border-radius: 20px;
    box-shadow: 0 20px 60px rgba(0, 0, 0, 0.15);
    transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
    
    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 25px 70px rgba(0, 0, 0, 0.2);
    }
    
    .card-header {
      display: flex;
      align-items: center;
      gap: 10px;
      font-weight: 600;
      font-size: 18px;
      color: #333;
      
      .el-icon {
        font-size: 24px;
        color: #4facfe;
      }
    }
  }

  .upload-demo {
    margin-bottom: 20px;
    
    :deep(.el-upload-dragger) {
      border: 3px dashed #d9d9d9;
      border-radius: 16px;
      background: linear-gradient(135deg, #fafafa 0%, #f0f0f0 100%);
      transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
      padding: 40px 20px;
      
      &:hover {
        border-color: #4facfe;
        background: linear-gradient(135deg, #f0f9ff 0%, #e0f2fe 100%);
        transform: scale(1.02);
        box-shadow: 0 10px 30px rgba(102, 126, 234, 0.2);
      }
      
      .el-icon--upload {
        font-size: 80px;
        color: #4facfe;
        margin-bottom: 20px;
      }
      
      .el-upload__text {
        font-size: 16px;
        color: #666;
        
        em {
          color: #4facfe;
          font-weight: 600;
        }
      }
    }
  }

  .camera-section {
    .camera-container {
      position: relative;
      width: 100%;
      height: 300px;
      background: linear-gradient(135deg, #1e3c72 0%, #2a5298 50%, #7e22ce 100%);
      border-radius: 16px;
      overflow: hidden;
      margin-bottom: 20px;
      transition: all 0.3s ease;
      box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);

      &.camera-starting {
        background: linear-gradient(135deg, #4facfe 0%, #43e97b 50%, #38f9d7 100%);
        animation: containerPulse 2s infinite;
      }

      &.camera-active {
        background: #000;
        box-shadow: 0 15px 50px rgba(0, 0, 0, 0.4);
        height: 350px;
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

        .placeholder-content {
          text-align: center;
          color: rgba(255, 255, 255, 0.9);
          
          .camera-icon {
            font-size: 48px;
            margin-bottom: 10px;
            animation: iconFloat 3s ease-in-out infinite;
          }
          
          .placeholder-text {
            font-size: 14px;
            margin-bottom: 15px;
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
        background: linear-gradient(135deg, rgba(102, 126, 234, 0.9) 0%, rgba(118, 75, 162, 0.9) 100%);

        .starting-content {
          text-align: center;
          color: white;
          
          .loading-spinner {
            width: 40px;
            height: 40px;
            border: 3px solid rgba(255, 255, 255, 0.3);
            border-top: 3px solid white;
            border-radius: 50%;
            animation: spin 1s linear infinite;
            margin: 0 auto 15px;
          }
          
          .starting-text {
            font-size: 14px;
            font-weight: 500;
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
          width: 15px;
          height: 15px;
          border: 2px solid rgba(0, 255, 136, 0.8);
          
          &.top-left {
            top: 8px;
            left: 8px;
            border-right: none;
            border-bottom: none;
          }
          
          &.top-right {
            top: 8px;
            right: 8px;
            border-left: none;
            border-bottom: none;
          }
          
          &.bottom-left {
            bottom: 8px;
            left: 8px;
            border-right: none;
            border-top: none;
          }
          
          &.bottom-right {
            bottom: 8px;
            right: 8px;
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

      .face-detection-overlay {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        width: 120px;
        height: 150px;
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
            width: 15px;
            height: 15px;
            border: 2px solid #00ff88;
            
            &.top-left {
              top: -2px;
              left: -2px;
              border-right: none;
              border-bottom: none;
            }
            
            &.top-right {
              top: -2px;
              right: -2px;
              border-left: none;
              border-bottom: none;
            }
            
            &.bottom-left {
              bottom: -2px;
              left: -2px;
              border-right: none;
              border-top: none;
            }
            
            &.bottom-right {
              bottom: -2px;
              right: -2px;
              border-left: none;
              border-top: none;
            }
          }
          
          .detection-label {
            position: absolute;
            top: -28px;
            left: 50%;
            transform: translateX(-50%);
            background: linear-gradient(135deg, rgba(0, 255, 136, 0.9) 0%, rgba(0, 200, 100, 0.9) 100%);
            color: white;
            padding: 3px 10px;
            border-radius: 12px;
            font-size: 11px;
            white-space: nowrap;
            box-shadow: 0 2px 10px rgba(0, 255, 136, 0.3);
            display: flex;
            align-items: center;
            gap: 6px;
            
            .confidence {
              background: rgba(255, 255, 255, 0.2);
              padding: 1px 5px;
              border-radius: 6px;
              font-size: 10px;
              font-weight: bold;
            }
          }
        }
      }
    }

    .camera-controls {
      text-align: center;
      margin-bottom: 15px;
    }

    .camera-settings {
      margin-top: 15px;
      padding-top: 15px;
      border-top: 1px solid #e0e0e0;
    }
  }

  @keyframes containerPulse {
    0%, 100% {
      transform: scale(1);
    }
    50% {
      transform: scale(1.01);
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

  @keyframes faceDetectionAppear {
    0% {
      opacity: 0;
      transform: translate(-50%, -50%) scale(0.8);
    }
    100% {
      opacity: 1;
      transform: translate(-50%, -50%) scale(1);
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

  .uploaded-files {
    margin-top: 20px;
    
    h4 {
      margin-bottom: 15px;
      color: #333;
    }
    
    .file-list {
      display: flex;
      flex-direction: column;
      gap: 10px;
      
      .file-item {
        display: flex;
        align-items: center;
        padding: 10px;
        border: 1px solid #e0e0e0;
        border-radius: 8px;
        cursor: pointer;
        transition: all 0.3s;
        
        &:hover {
          border-color: #409eff;
          background: #f0f9ff;
        }
        
        &.active {
          border-color: #409eff;
          background: #e6f7ff;
        }
        
        .file-preview {
          width: 60px;
          height: 40px;
          margin-right: 10px;
          border-radius: 4px;
          overflow: hidden;
          
          img, video {
            width: 100%;
            height: 100%;
            object-fit: cover;
          }
        }
        
        .file-info {
          flex: 1;
          
          .file-name {
            font-size: 14px;
            font-weight: 500;
            color: #333;
          }
          
          .file-type {
            font-size: 12px;
            color: #666;
          }
        }
      }
    }
  }

  .diagnosis-actions {
    margin-top: 20px;
  }

  .empty-result {
    height: 400px;
    display: flex;
    align-items: center;
    justify-content: center;
    
    .empty-icon {
      font-size: 64px;
      color: #ccc;
      margin-bottom: 20px;
    }
  }

  .diagnosing-animation {
    padding: 40px 20px;
    
    .diagnosis-progress {
      .progress-item {
        display: flex;
        align-items: center;
        margin-bottom: 20px;
        gap: 15px;
        
        .el-icon {
          font-size: 20px;
          color: #409eff;
        }
        
        span {
          width: 120px;
          font-size: 14px;
          color: #333;
        }
        
        .el-progress {
          flex: 1;
        }
      }
    }
  }

  .diagnosis-report {
    .diagnosis-summary {
      margin-bottom: 30px;
      padding: 25px;
      background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 50%, #fbc2eb 100%);
      border-radius: 16px;
      box-shadow: 0 10px 30px rgba(255, 154, 158, 0.3);
      
      .diagnosis-title {
        display: flex;
        align-items: center;
        gap: 12px;
        margin-bottom: 20px;
        
        h3 {
          color: #333;
          margin: 0;
          font-size: 20px;
          font-weight: 700;
        }
        
        .el-icon {
          color: #f56c6c;
          font-size: 28px;
          animation: bounce 2s infinite;
        }
      }
      
      .confidence-score {
        display: flex;
        align-items: center;
        gap: 15px;
        
        span {
          font-weight: 600;
          color: #333;
          font-size: 16px;
        }
        
        .el-progress {
          flex: 1;
        }
      }
    }

@keyframes bounce {
  0%, 100% {
    transform: translateY(0);
  }
  50% {
    transform: translateY(-5px);
  }
}

    .symptoms-analysis {
      margin-bottom: 30px;
      
      h4 {
        display: flex;
        align-items: center;
        gap: 10px;
        margin-bottom: 20px;
        color: #333;
        font-size: 18px;
        font-weight: 700;
        
        .el-icon {
          color: #4facfe;
          font-size: 22px;
        }
      }
      
      .symptom-categories {
        .symptom-category {
          margin-bottom: 20px;
          padding: 20px;
          background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
          border-radius: 12px;
          border-left: 5px solid #4facfe;
          box-shadow: 0 5px 15px rgba(0, 0, 0, 0.08);
          transition: all 0.3s;
          
          &:hover {
            transform: translateX(5px);
            box-shadow: 0 8px 25px rgba(0, 0, 0, 0.12);
          }
          
          &:nth-child(2) {
            border-left-color: #43e97b;
          }
          
          &:nth-child(3) {
            border-left-color: #4facfe;
          }
          
          .category-header {
            display: flex;
            align-items: center;
            gap: 10px;
            margin-bottom: 15px;
            font-weight: 700;
            font-size: 16px;
            color: #333;
            
            .el-icon {
              font-size: 20px;
              color: #4facfe;
            }
          }
          
          .symptom-list {
            .symptom-item {
              display: flex;
              align-items: center;
              gap: 10px;
              margin-bottom: 8px;
              font-size: 15px;
              color: #555;
              padding: 8px;
              border-radius: 8px;
              transition: all 0.2s;
              
              &:hover {
                background: rgba(102, 126, 234, 0.1);
                transform: translateX(3px);
              }
              
              .el-icon {
                color: #67c23a;
                font-size: 18px;
                flex-shrink: 0;
              }
            }
          }
        }
      }
    }

    .report-actions {
      text-align: center;
      padding-top: 25px;
      border-top: 2px solid #e0e0e0;
      
      .el-button {
        margin: 0 8px;
        padding: 12px 24px;
        font-size: 15px;
        font-weight: 600;
        border-radius: 12px;
        transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
        
        &:hover {
          transform: translateY(-3px);
          box-shadow: 0 10px 25px rgba(0, 0, 0, 0.15);
        }
        
        &.el-button--primary {
          background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
          border: none;
          
          &:hover {
            background: linear-gradient(135deg, #00f2fe 0%, #4facfe 100%);
          }
        }
        
        &.el-button--success {
          background: linear-gradient(135deg, #11998e 0%, #38ef7d 100%);
          border: none;
          
          &:hover {
            background: linear-gradient(135deg, #38ef7d 0%, #11998e 100%);
          }
        }
      }
    }
  }
}

@media (max-width: 768px) {
  .medical-diagnosis {
    :deep(.el-col) {
      margin-bottom: 20px;
    }
  }
}

/* 与健康管理页面一致的浅蓝视觉体系 */
.app-container {
  background: #f1f8fc;
  background-image:
    radial-gradient(circle at 8% 4%, rgba(45, 181, 235, 0.09), transparent 28%),
    linear-gradient(180deg, #f5fbff 0%, #edf7fc 100%);
  animation: none;
  overflow: auto;

  &::before {
    display: none;
  }
}

.app-header {
  width: min(1524px, calc(100% - 48px));
  height: 104px;
  margin: 32px auto 0;
  padding: 0;
  background: #fff;
  border: 1px solid #d4e8f5;
  border-radius: 24px;
  box-shadow: 0 14px 34px rgba(45, 104, 142, 0.08);
  backdrop-filter: none;

  .header-content {
    max-width: none;
    padding: 0 34px;
  }

  h1 {
    color: #1f2a3d;
    font-size: 28px;
    text-shadow: none;

    .el-icon {
      color: #28afe5;
      animation: none;
    }
  }

  .header-nav .nav-link,
  .header-nav .nav-link.back-home {
    color: #fff;
    background: #2bb4e8;
    border: 0;
    border-radius: 15px;
    padding: 14px 24px;
    font-weight: 700;
    box-shadow: 0 8px 20px rgba(43, 180, 232, 0.18);

    &:hover {
      color: #fff;
      background: #179fd7;
      transform: translateY(-1px);
      box-shadow: 0 10px 24px rgba(43, 180, 232, 0.24);
    }
  }
}

.app-main {
  padding: 28px 24px 48px;
  overflow: visible;
}

.medical-diagnosis {
  max-width: 1524px;

  .upload-panel,
  .result-panel {
    min-height: 650px;
    background: #fff;
    border: 1px solid #d4e8f5;
    border-radius: 22px;
    box-shadow: 0 12px 30px rgba(45, 104, 142, 0.07);
    backdrop-filter: none;

    &:hover {
      transform: none;
      box-shadow: 0 12px 30px rgba(45, 104, 142, 0.09);
    }

    :deep(.el-card__header) {
      padding: 24px 28px;
      border-bottom-color: #e3eef5;
    }

    :deep(.el-card__body) {
      padding: 26px;
    }

    .card-header {
      color: #1f2a3d;
      font-size: 20px;

      .el-icon {
        color: #2bb4e8;
      }
    }
  }

  .upload-demo :deep(.el-upload-dragger) {
    background: #f7fbfe;
    border: 2px dashed #9edcf5;
    box-shadow: none;

    &:hover {
      background: #eef9fe;
      border-color: #2bb4e8;
      transform: none;
      box-shadow: none;
    }
  }

  .camera-section .camera-container {
    background: #eaf6fc;
    border: 1px solid #c6e5f4;
    box-shadow: none;

    &.camera-starting {
      background: #eaf6fc;
      animation: none;
    }

    &.camera-active {
      box-shadow: 0 8px 24px rgba(24, 74, 104, 0.14);
    }

    .camera-placeholder {
      background: transparent;

      .placeholder-content {
        color: #4b819e;

        .placeholder-decoration .decoration-circle {
          background: #79ccec;
        }
      }
    }
  }

  .diagnosis-summary {
    background: #eef9fe;
    border: 1px solid #cceaf7;
    box-shadow: none;
  }

  .symptoms-analysis .symptom-categories .symptom-category {
    background: #f7fbfe;
    border-left-color: #2bb4e8;
    box-shadow: none;

    &:hover {
      transform: none;
      box-shadow: none;
    }
  }

  :deep(.el-button--primary),
  .report-actions .el-button.el-button--primary {
    background: #2bb4e8;
    border-color: #2bb4e8;
  }

  :deep(.el-tabs__active-bar) {
    background-color: #2bb4e8;
  }

  :deep(.el-tabs__item.is-active),
  :deep(.el-tabs__item:hover) {
    color: #169ed6;
  }
}

@media (max-width: 900px) {
  .app-header {
    width: calc(100% - 28px);
    height: auto;
    min-height: 92px;
    margin-top: 14px;

    .header-content {
      padding: 18px;
      gap: 16px;
    }

    h1 {
      font-size: 21px;
    }
  }

  .medical-diagnosis :deep(.el-col-12) {
    max-width: 100%;
    flex: 0 0 100%;
  }
}
</style>
