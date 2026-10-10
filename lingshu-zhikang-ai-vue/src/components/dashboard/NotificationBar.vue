<template>
  <div class="notification-bar">
    <div class="notification-content">
      <div class="notification-icon">🔊</div>
      <div class="notification-text">
        <span class="highlight">选 Gitee 年度开源项目日报！</span>
        这是我们今年最重要的一次亮相，需要你的支持
      </div>
    </div>
    <div class="notification-tabs">
      <div class="tab active">
        <span class="tab-icon">💼</span>
        <span class="tab-text">工作台</span>
      </div>
      <div class="tab">
        <span class="tab-icon">📊</span>
        <span class="tab-text">分析页</span>
      </div>
    </div>
  </div>
</template>

<script setup>
// 组件逻辑
</script>

<style scoped lang="scss">
.notification-bar {
  background: white;
  border-bottom: 1px solid #e5e7eb;
  padding: 12px 24px;
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.notification-content {
  display: flex;
  align-items: center;
  gap: 12px;
}

.notification-icon {
  font-size: 16px;
  color: #3b82f6;
}

.notification-text {
  font-size: 14px;
  color: #6b7280;
  
  .highlight {
    color: #3b82f6;
    font-weight: 500;
  }
}

.notification-tabs {
  display: flex;
  align-items: center;
  gap: 8px;
}

.tab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 8px 16px;
  border-radius: 6px;
  background: #f8fafc;
  font-size: 14px;
  color: #6b7280;
  cursor: pointer;
  transition: all 0.2s ease;
  
  &:hover {
    background: #e2e8f0;
  }
  
  &.active {
    background: #eff6ff;
    color: #2563eb;
  }
}

.tab-icon {
  font-size: 14px;
}

.tab-text {
  font-size: 13px;
}

.tab-close {
  font-size: 16px;
  color: #9ca3af;
  margin-left: 4px;
  
  &:hover {
    color: #6b7280;
  }
}
</style>
