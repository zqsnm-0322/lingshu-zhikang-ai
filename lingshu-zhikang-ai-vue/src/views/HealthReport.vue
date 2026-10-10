<template>
  <DashboardLayout title="体检报告" subtitle="上传、预览并发起 AI 病历分析">
    <!-- 数据分析加载动画 -->

    <div class="health-report-container">
      <PageHeader
        title="体检报告储存库"
        subtitle="集中管理报告文件，支持预览、下载，并可跳转到病历分析进行 AI 解读。"
      >
        <template #actions>
          <button class="zq-text-button" type="button" @click="goHome">
            <ArrowLeftIcon />
            返回
          </button>
          <button class="zq-text-button zq-primary-button" type="button" @click="showUpload">
            <DocumentArrowUpIcon />
            上传报告
          </button>
        </template>
      </PageHeader>

      <!-- 统计信息 -->
      <section class="zq-grid zq-grid-2">
        <StatCard label="报告总数" :value="stats.totalReports" :icon="ClipboardDocumentListIcon" tone="blue" />
        <StatCard label="已储存" :value="stats.storedCount" :icon="ArchiveBoxIcon" tone="green" />
      </section>

      <!-- 报告列表 -->
      <section class="reports-list zq-card">
        <div class="section-heading">
          <h2 class="zq-section-title">报告列表</h2>
          <p class="zq-section-subtitle">按检查日期排序，保留原有预览、AI 分析和删除操作。</p>
        </div>

        <EmptyState
          v-if="reportList.length === 0"
          title="还没有上传报告"
          description="上传 PDF 或图片格式体检报告后，可以在这里预览并发起 AI 分析。"
          :icon="DocumentTextIcon"
        >
          <button class="zq-text-button zq-primary-button empty-action" type="button" @click="showUpload">
            上传报告
          </button>
        </EmptyState>

        <article
          v-for="report in reportList"
          :key="report.id"
          class="report-card"
        >
          <div class="report-header">
            <span class="report-icon">{{ getTypeIcon(report.type) }}</span>
            <div class="report-info">
              <h4>{{ report.title }}</h4>
              <p>{{ report.checkDate }} | {{ report.hospital || '未填写' }}</p>
            </div>
          </div>
          <div class="report-actions">
            <button @click="previewReport(report)" class="preview-btn" type="button">
              <DocumentMagnifyingGlassIcon />
              预览
            </button>
            <button
              v-if="!report.analyzed"
              @click="analyzeReport(report)"
              class="analyze-btn"
              type="button"
            >
              <SparklesIcon />
              AI分析
            </button>
            <button
              v-else
              @click="viewDetail(report)"
              class="view-btn"
              type="button"
            >
              <DocumentTextIcon />
              查看分析
            </button>
            <button @click="deleteReport(report.id)" class="delete-btn" type="button" title="删除报告">
              <TrashIcon />
            </button>
          </div>
        </article>
      </section>
    </div>

    <!-- 上传弹窗 -->
    <div v-if="showUploadDialog" @click="hideUploadDialog" class="modal-mask">
      <div @click.stop class="modal-content">
        <div class="modal-header">
          <h3>上传体检报告</h3>
          <button @click="hideUploadDialog" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-item">
            <label>报告标题</label>
            <input v-model="uploadForm.title" class="form-input" placeholder="如：2024年度体检" />
          </div>
          <div class="form-item">
            <label>报告类型</label>
            <select v-model="uploadForm.type" class="form-select">
              <option value="general">常规体检</option>
              <option value="blood">血液检查</option>
              <option value="imaging">影像检查</option>
              <option value="other">其他检查</option>
            </select>
          </div>
          <div class="form-item">
            <label>检查日期</label>
            <input v-model="uploadForm.checkDate" type="date" class="form-input" />
          </div>
          <div class="form-item">
            <label>医院</label>
            <input v-model="uploadForm.hospital" class="form-input" placeholder="如：某某医院" />
          </div>
          <div class="form-item">
            <label>备注</label>
            <textarea v-model="uploadForm.notes" rows="3" class="form-textarea" placeholder="其他说明..."></textarea>
          </div>
          <div class="form-item">
            <label>上传文件</label>
            <input type="file" @change="handleFileChange" accept="image/*,.pdf" class="file-input" />
            <p v-if="selectedFileName" class="file-name">已选择：{{ selectedFileName }}</p>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="hideUploadDialog" class="btn secondary">取消</button>
          <button @click="uploadReport" :disabled="isUploading" class="btn primary">
            {{ isUploading ? '上传中...' : '上传' }}
          </button>
        </div>
      </div>
    </div>

    <!-- 详情弹窗 -->
    <div v-if="showDetailDialog" @click="hideDetailDialog" class="modal-mask">
      <div @click.stop class="modal-content">
        <div class="modal-header">
          <h3>分析结果</h3>
          <button @click="hideDetailDialog" class="close-btn">✕</button>
        </div>
        <div class="modal-body">
          <div v-if="currentReport" class="analysis-result">
            <div class="result-section">
              <h4>报告信息</h4>
              <p><strong>标题：</strong>{{ currentReport.title }}</p>
              <p><strong>类型：</strong>{{ getTypeName(currentReport.type) }}</p>
              <p><strong>检查日期：</strong>{{ currentReport.checkDate }}</p>
              <p><strong>医院：</strong>{{ currentReport.hospital || '未填写' }}</p>
            </div>
            <div class="result-section">
              <h4>AI分析建议</h4>
              <div class="analysis-content">{{ currentReport.analysisResult || '暂无分析结果' }}</div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button @click="hideDetailDialog" class="btn primary">关闭</button>
        </div>
      </div>
    </div>

    <!-- PDF预览弹窗 -->
    <div v-if="showPreviewDialog" @click="hidePreviewDialog" class="modal-mask">
      <div @click.stop class="modal-content preview-modal">
        <div class="modal-header">
          <h3>报告预览</h3>
          <button @click="hidePreviewDialog" class="close-btn">✕</button>
        </div>
        <div class="modal-body preview-body">
          <div v-if="previewUrl" class="preview-container">
            <iframe 
              v-if="currentPreviewType === 'pdf'"
              :src="previewUrl" 
              class="pdf-preview"
              frameborder="0"
            ></iframe>
            <img 
              v-else
              :src="previewUrl" 
              class="image-preview"
              :class="{ 'zoomed': isImageZoomed }"
              alt="报告预览"
              @click="toggleImageZoom"
            />
          </div>
          <div v-else class="preview-loading">
            加载中...
          </div>
        </div>
        <div class="modal-footer">
          <button @click="downloadCurrent" class="btn secondary">下载</button>
          <button @click="hidePreviewDialog" class="btn primary">关闭</button>
        </div>
      </div>
    </div>
  </DashboardLayout>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import {
  ArchiveBoxIcon,
  ArrowLeftIcon,
  ClipboardDocumentListIcon,
  DocumentArrowUpIcon,
  DocumentMagnifyingGlassIcon,
  DocumentTextIcon,
  SparklesIcon,
  TrashIcon
} from '@heroicons/vue/24/outline'
import DashboardLayout from '@/layouts/DashboardLayout.vue'
import PageHeader from '@/components/common/PageHeader.vue'
import StatCard from '@/components/common/StatCard.vue'
import EmptyState from '@/components/common/EmptyState.vue'
import request from '@/utils/request'
import { recordFeatureUsage } from '../utils/featureStats'
import iconReport from '../views/public/报告共享.png'

const router = useRouter()
const goHome = () => router.push('/')
const isLoading = ref(true)
const stats = ref({ totalReports: 0, storedCount: 0 })
const reportList = ref([])
const showUploadDialog = ref(false)
const showDetailDialog = ref(false)
const showPreviewDialog = ref(false)
const currentReport = ref(null)
const isUploading = ref(false)
const selectedFile = ref(null)
const selectedFileName = ref('')
const previewUrl = ref('')
const currentPreviewType = ref('pdf')
const isImageZoomed = ref(false)

const uploadForm = ref({
  title: '',
  type: 'general',
  checkDate: '',
  hospital: '',
  notes: ''
})

const reportTypes = {
  general: { name: '常规体检', icon: '📋' },
  blood: { name: '血液检查', icon: '🩸' },
  imaging: { name: '影像检查', icon: '📷' },
  other: { name: '其他检查', icon: '📄' }
}

const showUpload = () => {
  uploadForm.value = {
    title: '',
    type: 'general',
    checkDate: new Date().toISOString().split('T')[0],
    hospital: '',
    notes: ''
  }
  selectedFile.value = null
  selectedFileName.value = ''
  showUploadDialog.value = true
}

const hideUploadDialog = () => {
  showUploadDialog.value = false
}

const hideDetailDialog = () => {
  showDetailDialog.value = false
}

const hidePreviewDialog = () => {
  showPreviewDialog.value = false
  previewUrl.value = ''
  isImageZoomed.value = false
}

// 切换图片缩放
const toggleImageZoom = () => {
  isImageZoomed.value = !isImageZoomed.value
}

const handleFileChange = (e) => {
  const file = e.target.files[0]
  if (file) {
    selectedFile.value = file
    selectedFileName.value = file.name
  }
}

const getTypeIcon = (type) => {
  return reportTypes[type]?.icon || '📄'
}

const getTypeName = (type) => {
  return reportTypes[type]?.name || '其他检查'
}

const uploadReport = async () => {
  if (!uploadForm.value.title) {
    ElMessage.warning('请输入报告标题')
    return
  }

  if (!selectedFile.value) {
    ElMessage.warning('请选择报告文件')
    return
  }

  isUploading.value = true

  try {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    
    // 创建FormData
    const formData = new FormData()
    formData.append('file', selectedFile.value)
    formData.append('userId', localStorage.getItem('userId') || '1')
    formData.append('title', uploadForm.value.title)

    // 上传到MinIO
    const uploadRes = await request.post(`${baseUrl}/api/health-report/upload`, formData, {
      headers: {
        'Content-Type': 'multipart/form-data'
      }
    })

    if (uploadRes.code === 200) {
      const { fileId, previewUrl: filePreviewUrl } = uploadRes.data
      
      ElMessage.success('上传成功')
      await loadReports()
      showUploadDialog.value = false
    } else {
      ElMessage.error(uploadRes.msg || '上传失败')
    }
  } catch (error) {
    console.error('上传报告失败:', error)
    ElMessage.error('上传失败，请重试')
  } finally {
    isUploading.value = false
  }
}

const analyzeReport = (report) => {
  // 跳转到ChatPDF页面
  router.push({
    path: '/chat-pdf',
    query: {
      reportId: report.id,
      reportTitle: report.title,
      fileId: report.fileId,
      autoAnalyze: 'true'
    }
  })
}

const viewDetail = (report) => {
  currentReport.value = report
  showDetailDialog.value = true
}

// 预览报告
const previewReport = async (report) => {
  try {
    currentReport.value = report
    
    if (report.previewUrl) {
      // 如果已有预览链接，直接使用
      previewUrl.value = report.previewUrl
    } else if (report.fileId) {
      // 如果有fileId，获取新的预览链接
      const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
      const res = await request.get(`${baseUrl}/api/health-report/preview/${report.fileId}`)
      if (res.code === 200) {
        previewUrl.value = res.data
      } else {
        ElMessage.error('获取预览链接失败')
        return
      }
    } else {
      ElMessage.warning('该报告暂不支持预览')
      return
    }
    
    // 判断文件类型
    currentPreviewType.value = report.fileType && report.fileType.startsWith('image/') ? 'image' : 'pdf'
    showPreviewDialog.value = true
  } catch (error) {
    console.error('预览报告失败:', error)
    ElMessage.error('预览失败')
  }
}

// 下载当前预览的报告
const downloadCurrent = () => {
  if (currentReport.value && currentReport.value.fileId) {
    const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
    const downloadUrl = `${baseUrl}/api/health-report/download/${currentReport.value.fileId}`
    window.open(downloadUrl, '_blank')
  } else if (previewUrl.value) {
    window.open(previewUrl.value, '_blank')
  }
}

const deleteReport = async (id) => {
  try {
    await ElMessageBox.confirm('确定要删除该报告吗？', '确认删除', {
      type: 'warning'
    })
    
    const report = reportList.value.find(r => r.id === id)
    
    // 如果有fileId，从MinIO删除
    if (report && report.fileId) {
      const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
      await request.delete(`${baseUrl}/api/health-report/delete/${report.fileId}`, { params: { userId: localStorage.getItem('userId') || '1' } })
    }
    
    ElMessage.success('删除成功')
    await loadReports()
  } catch (error) {
    if (error !== 'cancel') {
      console.error('删除报告失败:', error)
      ElMessage.error('删除失败')
    }
  }
}

const loadReports = async () => {
  const baseUrl = import.meta.env.VITE_API_BASE_URL || 'http://localhost:8080'
  const response = await request.get(`${baseUrl}/api/health-report/list`, { params: { userId: localStorage.getItem('userId') || '1' } })
  const reports = (response.data || []).map(item => ({
    ...item,
    checkDate: item.createdTime?.slice(0, 10),
    uploadDate: item.createdTime,
    fileType: item.contentType,
    type: 'other',
    analyzed: false
  }))
  reportList.value = reports

  stats.value = {
    totalReports: reports.length,
    storedCount: reports.length  // 已储存等于所有已上传的报告
  }
}

onMounted(() => {
  setTimeout(() => {
    isLoading.value = false
  }, 3000)
  
  loadReports()
  // 记录功能使用次数
  recordFeatureUsage('体检报告储存库')
})
</script>

<style scoped lang="scss">
@keyframes barGrow {
  0%, 100% {
    height: 30%;
  }
  50% {
    height: 100%;
  }
}

@keyframes progressLoad {
  0% {
    width: 0%;
  }
  100% {
    width: 100%;
  }
}

.fade-out-leave-active {
  transition: all 0.8s ease;
}

.fade-out-leave-to {
  opacity: 0;
  transform: scale(0.95);
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
  
  .loading-content {
    display: flex;
    flex-direction: column;
    align-items: center;
    gap: 2.5rem;
  }
  
  .chart-loader {
    display: flex;
    align-items: flex-end;
    justify-content: center;
    gap: 12px;
    height: 150px;
    
    .bar {
      width: 30px;
      height: 30%;
      background: linear-gradient(to top, white, rgba(255, 255, 255, 0.6));
      border-radius: 8px 8px 0 0;
      animation: barGrow 1.5s ease-in-out infinite;
      animation-delay: var(--delay);
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
    }
  }
  
  .loading-text {
    text-align: center;
    color: white;
    
    .loading-title {
      font-size: 2rem;
      font-weight: 700;
      margin: 0 0 0.5rem 0;
      text-shadow: 0 2px 10px rgba(0, 0, 0, 0.2);
    }
    
    .loading-subtitle {
      font-size: 1.125rem;
      margin: 0 0 1.5rem 0;
      opacity: 0.9;
    }
    
    .loading-progress {
      width: 300px;
      height: 6px;
      background: rgba(255, 255, 255, 0.2);
      border-radius: 3px;
      overflow: hidden;
      
      .progress-fill {
        width: 0;
        height: 100%;
        background: white;
        border-radius: 3px;
        animation: progressLoad 3s ease-out forwards;
      }
    }
  }
}

.health-report-container {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.health-report-container :deep(.zq-text-button svg) {
  width: 18px;
  height: 18px;
}

.reports-list {
  padding: 20px;
}

.section-heading {
  margin-bottom: 16px;
}

.empty-action {
  margin-top: 18px;
}

.report-card {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-panel);
  padding: 16px;
}

.report-card + .report-card {
  margin-top: 12px;
}

.report-header {
  display: flex;
  min-width: 0;
  flex: 1;
  align-items: center;
  gap: 12px;
}

.report-icon {
  display: inline-flex;
  width: 44px;
  height: 44px;
  flex: 0 0 44px;
  align-items: center;
  justify-content: center;
  border-radius: var(--zq-radius);
  background: var(--zq-bg-soft);
  font-size: 24px;
}

.report-info {
  min-width: 0;
}

.report-info h4 {
  margin: 0;
  overflow: hidden;
  color: var(--zq-text-primary);
  font-size: 16px;
  font-weight: 700;
  text-overflow: ellipsis;
  white-space: nowrap;
}

.report-info p {
  margin: 6px 0 0;
  color: var(--zq-text-secondary);
  font-size: 13px;
}

.report-actions {
  display: flex;
  flex-wrap: wrap;
  justify-content: flex-end;
  gap: 8px;
}

.preview-btn,
.analyze-btn,
.view-btn,
.delete-btn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  gap: 6px;
  min-height: 36px;
  border: 1px solid var(--zq-border);
  border-radius: var(--zq-radius);
  background: var(--zq-bg-muted);
  color: var(--zq-text-primary);
  cursor: pointer;
  padding: 0 12px;
  font-size: 13px;
  font-weight: 700;
  transition: background-color 0.2s ease, color 0.2s ease, border-color 0.2s ease;
}

.preview-btn svg,
.analyze-btn svg,
.view-btn svg,
.delete-btn svg {
  width: 16px;
  height: 16px;
}

.preview-btn,
.view-btn {
  color: var(--zq-color-primary);
}

.analyze-btn {
  color: var(--zq-color-success);
}

.delete-btn {
  width: 36px;
  padding: 0;
  color: var(--zq-color-danger);
}

.preview-btn:hover,
.analyze-btn:hover,
.view-btn:hover,
.delete-btn:hover {
  border-color: rgba(37, 99, 235, 0.35);
  background: var(--zq-bg-soft);
}

.modal-mask {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
  padding: 20px;
}

.modal-content {
  background: var(--zq-bg-panel);
  border-radius: var(--zq-radius);
  max-width: 600px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  padding: 20px;
  border-bottom: 1px solid #eee;
  display: flex;
  justify-content: space-between;
  align-items: center;

  h3 {
    font-size: 18px;
    font-weight: 600;
  }

  .close-btn {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #909399;

    &:hover {
      color: #333;
    }
  }
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;

  .form-item {
    margin-bottom: 16px;

    label {
      display: block;
      font-size: 14px;
      font-weight: 600;
      margin-bottom: 8px;
      color: #333;
    }

    .form-input,
    .form-select,
    .form-textarea {
      width: 100%;
      padding: 10px 14px;
      border: 1px solid #dcdfe6;
      border-radius: var(--zq-radius);
      font-size: 14px;

      &:focus {
        outline: none;
        border-color: #4facfe;
      }
    }

    .file-input {
      width: 100%;
      padding: 10px;
      border: 1px solid #dcdfe6;
      border-radius: 8px;
      font-size: 14px;
    }

    .file-name {
      margin-top: 8px;
      font-size: 13px;
      color: #606266;
    }
  }

  .analysis-result {
    .result-section {
      margin-bottom: 20px;

      h4 {
        font-size: 16px;
        font-weight: 600;
        margin-bottom: 12px;
        color: #333;
      }

      p {
        font-size: 14px;
        color: #606266;
        margin-bottom: 8px;
      }

      .analysis-content {
        padding: 16px;
        background: #f8f9fa;
        border-radius: 8px;
        font-size: 14px;
        line-height: 1.6;
        color: #606266;
        white-space: pre-wrap;
      }
    }
  }
}

.modal-footer {
  padding: 20px;
  border-top: 1px solid #eee;
  display: flex;
  gap: 12px;

  .btn {
    flex: 1;
    padding: 12px;
    border: none;
    border-radius: 8px;
    font-size: 15px;
    font-weight: 600;
    cursor: pointer;
    transition: all 0.3s;

    &.primary {
      background: var(--zq-color-primary);
      color: white;

      &:hover:not(:disabled) {
        background: var(--zq-color-primary-hover);
      }

      &:disabled {
        opacity: 0.5;
        cursor: not-allowed;
      }
    }

    &.secondary {
      background: #f5f7fa;
      color: #606266;

      &:hover {
        background: #e4e7ed;
      }
    }
  }
}

// 预览模态框样式
.preview-modal {
  max-width: 90vw;
  max-height: 90vh;
  width: auto;
  height: auto;

  .preview-body {
    padding: 20px;
    max-height: 75vh;
    overflow: auto;
  }

  .preview-container {
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .pdf-preview {
    width: 100%;
    height: 70vh;
    border: none;
  }

  .image-preview {
    width: 300px;
    height: 300px;
    object-fit: contain;
    cursor: zoom-in;
    border: 2px solid #e0e0e0;
    border-radius: 8px;
    transition: all 0.3s ease;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);

    &:hover {
      box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
      transform: scale(1.02);
    }

    &.zoomed {
      width: auto;
      height: auto;
      max-width: 90vw;
      max-height: 75vh;
      cursor: zoom-out;
      transform: scale(1);
    }
  }

  .preview-loading {
    padding: 40px;
    text-align: center;
    color: #909399;
    font-size: 16px;
  }
}

@media (max-width: 760px) {
  .report-card {
    align-items: flex-start;
    flex-direction: column;
  }

  .report-actions {
    width: 100%;
    justify-content: flex-start;
  }
}
</style>
