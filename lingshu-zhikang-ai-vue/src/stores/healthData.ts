import { defineStore } from 'pinia'
import { ref, computed } from 'vue'

// 健康数据类型定义
export interface SymptomRecord {
  id: string
  date: string
  symptoms: string[]
  severity: string
  aiSuggestion?: string
}

export interface MedicationRecord {
  id: string
  name: string
  dosage: string
  frequency: string
  startDate: string
  endDate?: string
  reminders: string[]
}

export interface HealthReport {
  id: string
  date: string
  title: string
  type: string
  fileUrl?: string
  summary?: string
  keyIndicators?: Record<string, any>
}

export interface DiaryEntry {
  id: string
  date: string
  content: string
  mood?: string
  bloodPressure?: string
  bloodSugar?: string
  weight?: string
}

export interface ChronicDisease {
  id: string
  name: string
  diagnosisDate: string
  status: string
  medications: string[]
  lastCheckDate?: string
}

export interface UserAction {
  feature: string
  action: string
  timestamp: number
  data?: any
}

export const useHealthDataStore = defineStore('healthData', () => {
  // 状态定义
  const symptoms = ref<SymptomRecord[]>([])
  const medications = ref<MedicationRecord[]>([])
  const reports = ref<HealthReport[]>([])
  const diaryEntries = ref<DiaryEntry[]>([])
  const chronicDiseases = ref<ChronicDisease[]>([])
  const userActions = ref<UserAction[]>([])
  const lastVisitedFeature = ref<string>('')

  // 从localStorage加载数据
  const loadFromStorage = () => {
    try {
      const saved = localStorage.getItem('healthData')
      if (saved) {
        const data = JSON.parse(saved)
        symptoms.value = data.symptoms || []
        medications.value = data.medications || []
        reports.value = data.reports || []
        diaryEntries.value = data.diaryEntries || []
        chronicDiseases.value = data.chronicDiseases || []
        userActions.value = data.userActions || []
      }
    } catch (error) {
      console.error('加载健康数据失败:', error)
    }
  }

  // 保存到localStorage
  const saveToStorage = () => {
    try {
      const data = {
        symptoms: symptoms.value,
        medications: medications.value,
        reports: reports.value,
        diaryEntries: diaryEntries.value,
        chronicDiseases: chronicDiseases.value,
        userActions: userActions.value
      }
      localStorage.setItem('healthData', JSON.stringify(data))
    } catch (error) {
      console.error('保存健康数据失败:', error)
    }
  }

  // 症状记录操作
  const addSymptom = (symptom: Omit<SymptomRecord, 'id'>) => {
    const newSymptom = {
      ...symptom,
      id: Date.now().toString()
    }
    symptoms.value.unshift(newSymptom)
    saveToStorage()
    trackAction('symptom-checker', 'add', newSymptom)
    return newSymptom
  }

  // 用药记录操作
  const addMedication = (medication: Omit<MedicationRecord, 'id'>) => {
    const newMedication = {
      ...medication,
      id: Date.now().toString()
    }
    medications.value.unshift(newMedication)
    saveToStorage()
    trackAction('medicine-reminder', 'add', newMedication)
    return newMedication
  }

  // 体检报告操作
  const addReport = (report: Omit<HealthReport, 'id'>) => {
    const newReport = {
      ...report,
      id: Date.now().toString()
    }
    reports.value.unshift(newReport)
    saveToStorage()
    trackAction('health-report', 'add', newReport)
    return newReport
  }

  // 健康日记操作
  const addDiaryEntry = (entry: Omit<DiaryEntry, 'id'>) => {
    const newEntry = {
      ...entry,
      id: Date.now().toString()
    }
    diaryEntries.value.unshift(newEntry)
    saveToStorage()
    trackAction('health-diary', 'add', newEntry)
    return newEntry
  }

  // 慢性病管理操作
  const addChronicDisease = (disease: Omit<ChronicDisease, 'id'>) => {
    const newDisease = {
      ...disease,
      id: Date.now().toString()
    }
    chronicDiseases.value.unshift(newDisease)
    saveToStorage()
    trackAction('chronic-disease', 'add', newDisease)
    return newDisease
  }

  // 用户行为追踪
  const trackAction = (feature: string, action: string, data?: any) => {
    const userAction: UserAction = {
      feature,
      action,
      timestamp: Date.now(),
      data
    }
    userActions.value.push(userAction)
    lastVisitedFeature.value = feature
    
    // 只保留最近100条记录
    if (userActions.value.length > 100) {
      userActions.value = userActions.value.slice(-100)
    }
    saveToStorage()
  }

  // 计算属性：最近的活动
  const recentActivities = computed(() => {
    const activities = []
    
    // 最近的症状记录
    if (symptoms.value.length > 0) {
      activities.push({
        type: 'symptom',
        date: symptoms.value[0].date,
        content: `症状自检：${symptoms.value[0].symptoms.join('、')}`
      })
    }
    
    // 最近的用药记录
    if (medications.value.length > 0) {
      activities.push({
        type: 'medication',
        date: medications.value[0].startDate,
        content: `添加用药：${medications.value[0].name}`
      })
    }
    
    // 最近的体检报告
    if (reports.value.length > 0) {
      activities.push({
        type: 'report',
        date: reports.value[0].date,
        content: `上传报告：${reports.value[0].title}`
      })
    }
    
    // 最近的日记
    if (diaryEntries.value.length > 0) {
      activities.push({
        type: 'diary',
        date: diaryEntries.value[0].date,
        content: '记录健康日记'
      })
    }
    
    return activities.sort((a, b) => 
      new Date(b.date).getTime() - new Date(a.date).getTime()
    ).slice(0, 10)
  })

  // 计算属性：健康统计
  const healthStats = computed(() => ({
    totalSymptoms: symptoms.value.length,
    activeMedications: medications.value.filter(m => !m.endDate).length,
    totalReports: reports.value.length,
    diaryDays: diaryEntries.value.length,
    chronicDiseasesCount: chronicDiseases.value.length
  }))

  // 跨功能数据共享
  const shareData = (fromFeature: string, toFeature: string, data: any) => {
    trackAction(fromFeature, 'share_to_' + toFeature, data)
    return data
  }

  // 获取智能推荐
  const getSmartRecommendations = () => {
    const recommendations = []
    
    // 基于用户行为的推荐
    const recentFeatures = userActions.value
      .slice(-10)
      .map(a => a.feature)
    
    // 如果用户查看了症状，推荐AI咨询
    if (recentFeatures.includes('symptom-checker')) {
      recommendations.push({
        feature: 'ai-chat',
        reason: '基于您的症状记录，建议咨询AI医生',
        priority: 'high'
      })
    }
    
    // 如果有症状但没有用药记录，推荐药品查询
    if (symptoms.value.length > 0 && medications.value.length === 0) {
      recommendations.push({
        feature: 'medicine-query',
        reason: '您可以查询相关药品信息',
        priority: 'medium'
      })
    }
    
    // 如果有用药记录但没有设置提醒
    if (medications.value.length > 0) {
      recommendations.push({
        feature: 'medicine-reminder',
        reason: '设置用药提醒，按时服药',
        priority: 'high'
      })
    }
    
    // 如果有体检报告，推荐ChatPDF解析
    if (reports.value.length > 0) {
      recommendations.push({
        feature: 'chat-pdf',
        reason: '使用AI解析您的体检报告',
        priority: 'medium'
      })
    }
    
    // 如果超过3天没有记录日记
    const lastDiary = diaryEntries.value[0]
    if (!lastDiary || 
        (Date.now() - new Date(lastDiary.date).getTime()) > 3 * 24 * 60 * 60 * 1000) {
      recommendations.push({
        feature: 'health-diary',
        reason: '记录今日健康状况',
        priority: 'low'
      })
    }
    
    // 健康数据可视化
    if (diaryEntries.value.length > 7 || reports.value.length > 2) {
      recommendations.push({
        feature: 'health-dashboard',
        reason: '查看您的健康数据分析',
        priority: 'medium'
      })
    }
    
    return recommendations
  }

  // 获取下一步建议
  const getNextStepSuggestions = (currentFeature: string) => {
    const suggestionMap: Record<string, any[]> = {
      'symptom-checker': [
        { route: '/ai-chat', title: 'AI医生咨询', icon: '🤖', reason: '获取专业建议' },
        { route: '/medicine-query', title: '药品查询', icon: '💊', reason: '了解相关药物' },
        { route: '/health-diary', title: '健康日记', icon: '📔', reason: '记录症状变化' }
      ],
      'medicine-query': [
        { route: '/medicine-reminder', title: '用药提醒', icon: '⏰', reason: '设置服药提醒' },
        { route: '/ai-chat', title: 'AI咨询', icon: '🤖', reason: '咨询用药问题' },
        { route: '/health-diary', title: '记录用药', icon: '📔', reason: '记录用药情况' }
      ],
      'health-report': [
        { route: '/chat-pdf', title: 'ChatPDF', icon: '📄', reason: '智能解析报告' },
        { route: '/chronic-disease', title: '慢性病管理', icon: '💓', reason: '记录健康指标' },
        { route: '/health-dashboard', title: '数据大屏', icon: '📊', reason: '可视化数据' }
      ],
      'chat-pdf': [
        { route: '/chronic-disease', title: '慢性病管理', icon: '💓', reason: '记录指标数据' },
        { route: '/ai-chat', title: 'AI咨询', icon: '🤖', reason: '详细咨询' },
        { route: '/health-dashboard', title: '健康分析', icon: '📊', reason: '查看趋势' }
      ],
      'health-diary': [
        { route: '/health-dashboard', title: '数据分析', icon: '📊', reason: '查看健康趋势' },
        { route: '/medicine-reminder', title: '用药提醒', icon: '⏰', reason: '管理用药' },
        { route: '/chronic-disease', title: '慢性病管理', icon: '💓', reason: '更新健康数据' }
      ],
      'chronic-disease': [
        { route: '/medicine-reminder', title: '用药提醒', icon: '⏰', reason: '管理慢病用药' },
        { route: '/health-diary', title: '健康日记', icon: '📔', reason: '记录每日状况' },
        { route: '/health-dashboard', title: '数据可视化', icon: '📊', reason: '查看指标变化' }
      ],
      'ai-chat': [
        { route: '/medicine-query', title: '药品查询', icon: '💊', reason: '查询推荐药物' },
        { route: '/health-diary', title: '健康日记', icon: '📔', reason: '记录咨询建议' },
        { route: '/symptom-checker', title: '症状自检', icon: '🔍', reason: '进一步自检' }
      ]
    }
    
    return suggestionMap[currentFeature] || []
  }

  // 初始化时加载数据
  loadFromStorage()

  return {
    // 状态
    symptoms,
    medications,
    reports,
    diaryEntries,
    chronicDiseases,
    userActions,
    lastVisitedFeature,
    
    // 计算属性
    recentActivities,
    healthStats,
    
    // 方法
    addSymptom,
    addMedication,
    addReport,
    addDiaryEntry,
    addChronicDisease,
    trackAction,
    shareData,
    getSmartRecommendations,
    getNextStepSuggestions,
    loadFromStorage,
    saveToStorage
  }
})

