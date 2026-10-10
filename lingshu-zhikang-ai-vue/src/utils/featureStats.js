/**
 * 功能使用统计工具
 */

// 记录功能使用次数
export const recordFeatureUsage = (featureName) => {
  try {
    const stats = JSON.parse(localStorage.getItem('featureStats') || '{}')
    stats[featureName] = (stats[featureName] || 0) + 1
    localStorage.setItem('featureStats', JSON.stringify(stats))
    console.log(`📊 记录功能使用: ${featureName}, 当前次数: ${stats[featureName]}`)
  } catch (error) {
    console.error('记录功能使用失败:', error)
  }
}

// 获取功能使用次数
export const getFeatureUsage = (featureName) => {
  try {
    const stats = JSON.parse(localStorage.getItem('featureStats') || '{}')
    return stats[featureName] || 0
  } catch (error) {
    console.error('获取功能使用次数失败:', error)
    return 0
  }
}

// 获取所有功能统计
export const getAllFeatureStats = () => {
  try {
    return JSON.parse(localStorage.getItem('featureStats') || '{}')
  } catch (error) {
    console.error('获取所有功能统计失败:', error)
    return {}
  }
}

// 清空统计数据
export const clearFeatureStats = () => {
  localStorage.removeItem('featureStats')
  console.log('📊 已清空功能使用统计')
}
