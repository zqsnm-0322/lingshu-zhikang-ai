<template>
  <div class="medicine-query-container">
    <!-- 药丸旋转加载动画 -->
    
    <!-- 顶部导航 -->
    <div class="top-nav">
      <button @click="goBack" class="back-btn">
        <span>←</span> 返回
      </button>
      <h1 class="page-title">药品查询</h1>
    </div>

    <!-- 搜索框 -->
    <div class="search-section">
      <div class="search-bar">
        <img src="https://api.iconify.design/mdi:magnify.svg?color=%23999999" class="search-icon" alt="搜索" />
        <input
          v-model="searchKeyword"
          type="text"
          placeholder="搜索药品名称或成分"
          @keyup.enter="doSearch"
          class="search-input"
        />
        <button
          @click="doSearch"
          :disabled="isSearching"
          class="search-btn"
        >
          {{ isSearching ? '搜索中...' : '搜索' }}
        </button>
      </div>
    </div>

    <!-- 搜索结果 -->
    <div v-if="searchResults.length > 0" class="search-results">
      <div class="section-title">搜索结果 ({{ searchResults.length }})</div>
      <div
        v-for="item in searchResults"
        :key="item.id"
        @click="viewDetail(item)"
        class="medicine-item"
      >
        <div class="medicine-header">
          <div class="medicine-name">{{ item.name }}</div>
          <div class="medicine-price">¥{{ item.price }}</div>
        </div>
        <div class="medicine-info">
          <span class="info-tag">{{ item.category }}</span>
          <span class="info-text">{{ item.specification }}</span>
        </div>
        <div class="medicine-brief">{{ item.indications }}</div>
      </div>
    </div>

    <!-- 默认内容 -->
    <div v-if="searchResults.length === 0" class="default-content">
      <!-- 搜索历史 -->
      <div v-if="searchHistory.length > 0" class="history-section">
        <div class="section-header">
          <div class="section-title">搜索历史</div>
          <button @click="clearHistory" class="clear-btn">清空</button>
        </div>
        <div class="history-list">
          <span
            v-for="(item, index) in searchHistory"
            :key="index"
            @click="selectHistory(item)"
            class="history-tag"
          >
            {{ item }}
          </span>
        </div>
      </div>

      <!-- 热门药品 -->
      <div class="hot-section">
        <div class="section-title">热门药品</div>
        <div class="hot-list">
          <span
            v-for="(item, index) in hotMedicines"
            :key="index"
            @click="selectHotMedicine(item)"
            class="hot-tag"
          >
            {{ item }}
          </span>
        </div>
      </div>

      <!-- 药品分类 -->
      <div class="category-section">
        <div class="section-title">药品分类</div>
        <div class="category-list">
          <div
            v-for="cat in categories"
            :key="cat.id"
            class="category-card"
          >
            <img :src="cat.icon" class="category-icon" :alt="cat.name" />
            <div class="category-name">{{ cat.name }}</div>
          </div>
        </div>
      </div>

      <!-- 我的收藏 -->
      <div v-if="favorites.length > 0" class="favorites-section">
        <div class="section-title">我的收藏</div>
        <div
          v-for="item in favorites"
          :key="item.id"
          @click="viewDetail(item)"
          class="favorite-item"
        >
          <div class="favorite-content">
            <div class="favorite-name">{{ item.name }}</div>
            <div class="favorite-category">{{ item.category }}</div>
          </div>
          <button
            @click.stop="deleteFavorite(item.id)"
            class="delete-btn"
          >
            🗑️
          </button>
        </div>
      </div>
    </div>

    <!-- 药品详情弹窗 -->
    <div
      v-if="showDetailDialog"
      @click="hideDetailDialog"
      class="modal-mask"
    >
      <div @click.stop class="modal-content">
        <div class="modal-header">
          <div class="modal-title">药品详情</div>
          <button @click="hideDetailDialog" class="modal-close">✕</button>
        </div>

        <div v-if="currentMedicine" class="modal-body">
          <!-- 基本信息 -->
          <div class="detail-section">
            <div class="medicine-main-name">{{ currentMedicine.name }}</div>
            <div class="medicine-generic">通用名：{{ currentMedicine.genericName }}</div>
            <div class="medicine-price-tag">参考价格：¥{{ currentMedicine.price }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">规格</div>
            <div class="detail-value">{{ currentMedicine.specification }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">生产企业</div>
            <div class="detail-value">{{ currentMedicine.manufacturer }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">适应症</div>
            <div class="detail-value">{{ currentMedicine.indications }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">用法用量</div>
            <div class="detail-value">{{ currentMedicine.usage }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">禁忌</div>
            <div class="detail-value warning">{{ currentMedicine.contraindications }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">不良反应</div>
            <div class="detail-value">{{ currentMedicine.sideEffects }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">注意事项</div>
            <div class="detail-value">{{ currentMedicine.precautions }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">贮藏</div>
            <div class="detail-value">{{ currentMedicine.storage }}</div>
          </div>

          <div class="detail-section">
            <div class="detail-label">有效期</div>
            <div class="detail-value">{{ currentMedicine.validityPeriod }}</div>
          </div>
        </div>

        <div class="modal-footer">
          <button @click="toggleFavorite" class="footer-btn">
            <span class="footer-icon">{{ currentMedicine?.isFavorite ? '❤️' : '🤍' }}</span>
            <span>{{ currentMedicine?.isFavorite ? '已收藏' : '收藏' }}</span>
          </button>
          <button @click="consultDoctor" class="footer-btn primary">
            <span class="footer-icon">💬</span>
            <span>咨询医生</span>
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { recordFeatureUsage } from '../utils/featureStats'
import { ElMessage, ElMessageBox } from 'element-plus'
import iconMedicine from '../views/public/药丸.png'

const router = useRouter()
const isLoading = ref(true)

// 搜索关键词
const searchKeyword = ref('')

// 搜索历史
const searchHistory = ref([])

// 热门药品
const hotMedicines = ref([
  '阿莫西林', '布洛芬', '感冒灵', '维生素C',
  '头孢克肟', '对乙酰氨基酚', '阿司匹林', '甲硝唑'
])

// 搜索结果
const searchResults = ref([])

// 药品详情
const showDetailDialog = ref(false)
const currentMedicine = ref(null)

// 常见药品分类
const categories = ref([
  { id: 'fever', name: '感冒发烧', icon: 'https://api.iconify.design/mdi:thermometer.svg?color=%23ffffff', medicines: ['布洛芬', '对乙酰氨基酚', '感冒灵'] },
  { id: 'antibiotics', name: '抗生素', icon: 'https://api.iconify.design/mdi:pill.svg?color=%23ffffff', medicines: ['阿莫西林', '头孢克肟', '阿奇霉素'] },
  { id: 'digestion', name: '消化系统', icon: 'https://api.iconify.design/mdi:stomach.svg?color=%23ffffff', medicines: ['多潘立酮', '奥美拉唑', '蒙脱石散'] },
  { id: 'vitamin', name: '维生素类', icon: 'https://api.iconify.design/mdi:star.svg?color=%23ffffff', medicines: ['维生素C', '维生素B', '复合维生素'] },
  { id: 'external', name: '外用药', icon: 'https://api.iconify.design/mdi:bandage.svg?color=%23ffffff', medicines: ['碘伏', '红霉素软膏', '创可贴'] }
])

// 我的收藏
const favorites = ref([])

// 加载状态
const isSearching = ref(false)

// 模拟药品数据库
const medicineDatabase = ref([
  {
    id: 1,
    name: '阿莫西林胶囊',
    genericName: '阿莫西林',
    category: '抗生素',
    manufacturer: '某某制药厂',
    specification: '0.25g×24粒/盒',
    price: '15.80',
    indications: '用于敏感菌所致的各种感染，如呼吸道感染、泌尿道感染、皮肤软组织感染等。',
    usage: '口服。成人一次0.5g，每6~8小时1次，一日剂量不超过4g。',
    contraindications: '对青霉素类抗生素过敏者禁用。',
    sideEffects: '可能出现恶心、呕吐、腹泻及假膜性肠炎等胃肠道反应。',
    precautions: '使用前应详细询问患者的过敏史。肝肾功能不全者慎用。',
    storage: '遮光，密封保存。',
    validityPeriod: '24个月'
  },
  {
    id: 2,
    name: '布洛芬缓释胶囊',
    genericName: '布洛芬',
    category: '解热镇痛药',
    manufacturer: '某某制药厂',
    specification: '0.3g×20粒/盒',
    price: '12.50',
    indications: '用于缓解轻至中度疼痛如头痛、关节痛、偏头痛、牙痛、肌肉痛、神经痛、痛经，也用于普通感冒或流行性感冒引起的发热。',
    usage: '口服。成人一次0.3g，一日2次（早晚各一次）。',
    contraindications: '对本品及其他非甾体抗炎药过敏者禁用。活动性消化道溃疡或出血患者禁用。',
    sideEffects: '可见恶心、呕吐、胃烧灼感或轻度消化不良、胃肠道溃疡及出血、转氨酶升高、头痛、头晕、耳鸣、视力模糊、水肿等。',
    precautions: '有消化道疾病史者慎用。孕妇及哺乳期妇女慎用。',
    storage: '密封保存。',
    validityPeriod: '36个月'
  },
  {
    id: 3,
    name: '维生素C片',
    genericName: '维生素C',
    category: '维生素类药',
    manufacturer: '某某制药厂',
    specification: '0.1g×100片/瓶',
    price: '8.00',
    indications: '用于预防坏血病，也可用于各种急慢性传染疾病及紫癜等的辅助治疗。',
    usage: '口服。成人预防用量：一日50~100mg。治疗用量：一次100~200mg，一日3次。',
    contraindications: '对本品过敏者禁用。',
    sideEffects: '长期过量服用可引起腹泻、胃酸过多、胃液返流，有时可见泌尿系结石、深静脉血栓形成等。',
    precautions: '不宜长期过量服用，否则停药后可能出现坏血病症状。',
    storage: '遮光，密封保存。',
    validityPeriod: '24个月'
  }
])

// 返回上一页
const goBack = () => {
  router.back()
}

// 加载搜索历史
const loadSearchHistory = () => {
  const history = localStorage.getItem('medicineSearchHistory')
  if (history) {
    searchHistory.value = JSON.parse(history).slice(0, 10)
  }
}

// 加载收藏
const loadFavorites = () => {
  const favs = localStorage.getItem('medicineFavorites')
  if (favs) {
    favorites.value = JSON.parse(favs)
  }
}

// 执行搜索
const doSearch = () => {
  const keyword = searchKeyword.value.trim()

  if (!keyword) {
    ElMessage.warning('请输入药品名称')
    return
  }

  isSearching.value = true

  // 模拟搜索（实际应该调用后端API）
  setTimeout(() => {
    const results = medicineDatabase.value.filter(medicine =>
      medicine.name.includes(keyword) ||
      medicine.genericName.includes(keyword) ||
      medicine.category.includes(keyword)
    )

    searchResults.value = results
    isSearching.value = false

    // 保存搜索历史
    saveSearchHistory(keyword)

    if (results.length === 0) {
      ElMessage.info('未找到相关药品')
    }
  }, 500)
}

// 保存搜索历史
const saveSearchHistory = (keyword) => {
  let history = JSON.parse(localStorage.getItem('medicineSearchHistory') || '[]')

  // 去重
  history = history.filter(item => item !== keyword)

  // 添加到开头
  history.unshift(keyword)

  // 只保留最近20条
  if (history.length > 20) {
    history = history.slice(0, 20)
  }

  localStorage.setItem('medicineSearchHistory', JSON.stringify(history))
  searchHistory.value = history.slice(0, 10)
}

// 点击历史记录
const selectHistory = (keyword) => {
  searchKeyword.value = keyword
  doSearch()
}

// 清空搜索历史
const clearHistory = () => {
  ElMessageBox.confirm('确定要清空搜索历史吗？', '确认清空', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    localStorage.removeItem('medicineSearchHistory')
    searchHistory.value = []
    ElMessage.success('已清空')
  }).catch(() => {})
}

// 点击热门药品
const selectHotMedicine = (name) => {
  searchKeyword.value = name
  doSearch()
}

// 查看药品详情
const viewDetail = (medicine) => {
  // 检查是否已收藏
  const isFavorite = favorites.value.findIndex(f => f.id === medicine.id) >= 0
  currentMedicine.value = { ...medicine, isFavorite }
  showDetailDialog.value = true
}

// 隐藏详情弹窗
const hideDetailDialog = () => {
  showDetailDialog.value = false
}

// 收藏/取消收藏
const toggleFavorite = () => {
  const medicine = currentMedicine.value
  let favs = favorites.value

  const index = favs.findIndex(f => f.id === medicine.id)

  if (index >= 0) {
    // 取消收藏
    favs.splice(index, 1)
    medicine.isFavorite = false
    ElMessage.success('已取消收藏')
  } else {
    // 添加收藏
    favs.unshift(medicine)
    medicine.isFavorite = true
    ElMessage.success('已添加收藏')
  }

  localStorage.setItem('medicineFavorites', JSON.stringify(favs))
  favorites.value = favs
  currentMedicine.value = medicine
}

// 删除收藏
const deleteFavorite = (id) => {
  ElMessageBox.confirm('确定要删除该收藏吗？', '确认删除', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(() => {
    const favs = favorites.value.filter(f => f.id !== id)
    localStorage.setItem('medicineFavorites', JSON.stringify(favs))
    favorites.value = favs
    ElMessage.success('已删除')
  }).catch(() => {})
}

// 咨询医生
const consultDoctor = () => {
  router.push('/ai-chat')
}

// 页面加载时初始化
onMounted(() => {
  // 3秒后隐藏加载动画
  setTimeout(() => {
    isLoading.value = false
  }, 3000)
  
  loadSearchHistory()
  loadFavorites()
  // 记录功能使用次数
  recordFeatureUsage('药品查询')
})
</script>

<style scoped lang="scss">
@keyframes pillRotate {
  0%, 100% {
    transform: translateY(0) rotate(0deg);
    opacity: 1;
  }
  50% {
    transform: translateY(-30px) rotate(180deg);
    opacity: 0.6;
  }
}

@keyframes pillBounce {
  0%, 100% {
    transform: scale(1);
  }
  50% {
    transform: scale(1.1);
  }
}

.fade-out-enter-active {
  transition: opacity 0.5s ease;
}

.fade-out-leave-active {
  transition: all 0.8s ease;
}

.fade-out-enter-from {
  opacity: 0;
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
    gap: 2rem;
  }
  
  .pill-loader {
    position: relative;
    width: 200px;
    height: 100px;
    display: flex;
    justify-content: space-around;
    align-items: center;
    
    .pill {
      width: 50px;
      height: 80px;
      background: linear-gradient(135deg, #fff 0%, #f0f0f0 100%);
      border-radius: 25px;
      box-shadow: 0 5px 15px rgba(0, 0, 0, 0.2);
      position: relative;
      
      &::before {
        content: '';
        position: absolute;
        top: 50%;
        left: 0;
        right: 0;
        height: 40px;
        transform: translateY(-50%);
        border-radius: 25px;
      }
      
      &.pill-1 {
        animation: pillRotate 1.5s ease-in-out infinite;
        &::before {
          background: #ff6b6b;
        }
      }
      
      &.pill-2 {
        animation: pillRotate 1.5s ease-in-out 0.3s infinite;
        &::before {
          background: #4ecdc4;
        }
      }
      
      &.pill-3 {
        animation: pillRotate 1.5s ease-in-out 0.6s infinite;
        &::before {
          background: #ffe66d;
        }
      }
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
      margin: 0;
      opacity: 0.9;
    }
  }
}

.medicine-query-container {
  min-height: 100vh;
  width: 100%;
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  padding: 20px;
  box-sizing: border-box;
}

.top-nav {
  display: flex;
  align-items: center;
  margin-bottom: 20px;
  gap: 20px;

  .back-btn {
    background: rgba(255, 255, 255, 0.9);
    border: none;
    border-radius: 8px;
    padding: 10px 20px;
    cursor: pointer;
    font-size: 16px;
    transition: all 0.3s;

    &:hover {
      background: white;
      transform: translateX(-5px);
    }
  }

  .page-title {
    color: white;
    font-size: 24px;
    font-weight: 600;
  }
}

.search-section {
  margin-bottom: 20px;

  .search-bar {
    background: white;
    border-radius: 30px;
    padding: 10px 20px;
    display: flex;
    align-items: center;
    gap: 10px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

    .search-icon {
      width: 20px;
      height: 20px;
    }

    .search-input {
      flex: 1;
      border: none;
      outline: none;
      font-size: 16px;
      padding: 5px;
    }

    .search-btn {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: white;
      border: none;
      border-radius: 20px;
      padding: 10px 24px;
      cursor: pointer;
      font-size: 15px;
      font-weight: 600;
      transition: all 0.3s;

      &:hover:not(:disabled) {
        transform: scale(1.05);
        box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
      }

      &:disabled {
        opacity: 0.6;
        cursor: not-allowed;
      }
    }
  }
}

.search-results {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);

  .medicine-item {
    padding: 16px;
    border-bottom: 1px solid #eee;
    cursor: pointer;
    transition: all 0.3s;

    &:last-child {
      border-bottom: none;
    }

    &:hover {
      background: #f8f9fa;
      transform: translateX(5px);
    }

    .medicine-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: 8px;

      .medicine-name {
        font-size: 18px;
        font-weight: 600;
        color: #333;
      }

      .medicine-price {
        font-size: 18px;
        font-weight: 600;
        color: #f56c6c;
      }
    }

    .medicine-info {
      display: flex;
      gap: 10px;
      margin-bottom: 8px;

      .info-tag {
        background: #ecf5ff;
        color: #409eff;
        padding: 4px 12px;
        border-radius: 12px;
        font-size: 13px;
      }

      .info-text {
        color: #666;
        font-size: 14px;
      }
    }

    .medicine-brief {
      color: #666;
      font-size: 14px;
      line-height: 1.5;
    }
  }
}

.default-content {
  background: white;
  border-radius: 16px;
  padding: 20px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
}

.section-title {
  font-size: 18px;
  font-weight: 600;
  margin-bottom: 16px;
  color: #333;
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;

  .clear-btn {
    background: none;
    border: none;
    color: #909399;
    cursor: pointer;
    font-size: 14px;

    &:hover {
      color: #f56c6c;
    }
  }
}

.history-section,
.hot-section,
.category-section,
.favorites-section {
  margin-bottom: 24px;
}

.history-list,
.hot-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.history-tag,
.hot-tag {
  background: #f5f7fa;
  padding: 8px 16px;
  border-radius: 16px;
  cursor: pointer;
  font-size: 14px;
  color: #606266;
  transition: all 0.3s;

  &:hover {
    background: #ecf5ff;
    color: #409eff;
    transform: translateY(-2px);
  }
}

.category-list {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(140px, 1fr));
  gap: 16px;

  .category-card {
    background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);
    padding: 20px;
    border-radius: 12px;
    text-align: center;
    cursor: pointer;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 8px 16px rgba(67, 233, 123, 0.4);
    }

    .category-icon {
      width: 36px;
      height: 36px;
      margin-bottom: 8px;
    }

    .category-name {
      color: white;
      font-size: 15px;
      font-weight: 600;
    }
  }
}

.favorite-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #eee;
  cursor: pointer;
  transition: all 0.3s;

  &:last-child {
    border-bottom: none;
  }

  &:hover {
    background: #f8f9fa;
  }

  .favorite-content {
    flex: 1;

    .favorite-name {
      font-size: 16px;
      font-weight: 600;
      color: #333;
      margin-bottom: 4px;
    }

    .favorite-category {
      font-size: 13px;
      color: #909399;
    }
  }

  .delete-btn {
    background: none;
    border: none;
    font-size: 20px;
    cursor: pointer;
    padding: 8px;
    transition: all 0.3s;

    &:hover {
      transform: scale(1.2);
    }
  }
}

// 弹窗样式
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
  background: white;
  border-radius: 16px;
  max-width: 700px;
  width: 100%;
  max-height: 90vh;
  overflow: hidden;
  display: flex;
  flex-direction: column;
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 20px;
  border-bottom: 1px solid #eee;

  .modal-title {
    font-size: 20px;
    font-weight: 600;
  }

  .modal-close {
    background: none;
    border: none;
    font-size: 24px;
    cursor: pointer;
    color: #909399;
    padding: 0;
    width: 32px;
    height: 32px;
    display: flex;
    align-items: center;
    justify-content: center;
    border-radius: 50%;
    transition: all 0.3s;

    &:hover {
      background: #f5f7fa;
      color: #333;
    }
  }
}

.modal-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px;

  .detail-section {
    margin-bottom: 20px;

    .medicine-main-name {
      font-size: 22px;
      font-weight: 600;
      color: #333;
      margin-bottom: 8px;
    }

    .medicine-generic {
      font-size: 15px;
      color: #606266;
      margin-bottom: 8px;
    }

    .medicine-price-tag {
      font-size: 18px;
      color: #f56c6c;
      font-weight: 600;
    }

    .detail-label {
      font-size: 15px;
      font-weight: 600;
      color: #333;
      margin-bottom: 8px;
    }

    .detail-value {
      font-size: 14px;
      color: #606266;
      line-height: 1.6;

      &.warning {
        color: #f56c6c;
        font-weight: 500;
      }
    }
  }
}

.modal-footer {
  display: flex;
  gap: 12px;
  padding: 20px;
  border-top: 1px solid #eee;

  .footer-btn {
    flex: 1;
    padding: 12px;
    border: 1px solid #dcdfe6;
    border-radius: 8px;
    background: white;
    cursor: pointer;
    display: flex;
    align-items: center;
    justify-content: center;
    gap: 8px;
    font-size: 15px;
    transition: all 0.3s;

    &:hover {
      transform: translateY(-2px);
      box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    }

    &.primary {
      background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
      color: white;
      border: none;

      &:hover {
        box-shadow: 0 4px 12px rgba(79, 172, 254, 0.4);
      }
    }

    .footer-icon {
      font-size: 18px;
    }
  }
}
/* 与“病历分析”页面统一的医疗蓝视觉 */
:global(body) { background: #eef9fe; }

.medicine-query-container {
  padding: 30px;
  background: linear-gradient(135deg, #e8f7ff 0%, #f8fcff 48%, #eefbf8 100%);
  color: #243047;
}

.top-nav,
.search-section .search-bar,
.default-content,
.search-results {
  border: 1px solid #d7eefb;
  background: rgba(255, 255, 255, .96);
  box-shadow: 0 12px 30px rgba(45, 126, 164, .08);
}

.top-nav {
  max-width: 1520px;
  min-height: 96px;
  margin: 0 auto 24px;
  padding: 20px 28px;
  border-radius: 24px;

  .page-title { color: #202938; font-size: 28px; }
  .back-btn { border: 1px solid #bfe8fb; color: #0ea5e9; background: #effaff; }
  .back-btn:hover { background: #e6f8ff; transform: translateX(-3px); }
}

.search-section,
.default-content,
.search-results { max-width: 1520px; margin-left: auto; margin-right: auto; }

.search-section .search-bar {
  padding: 12px 16px 12px 22px;
  border-radius: 20px;

  .search-input { color: #243047; }
  .search-btn { border-radius: 14px; background: #38bdf8; box-shadow: 0 8px 20px rgba(56,189,248,.2); }
  .search-btn:hover:not(:disabled) { background: #20afea; }
}

.default-content,
.search-results { padding: 28px; border-radius: 24px; }
.section-title { color: #202938; }
.history-tag,
.hot-tag { border: 1px solid #e0edf6; color: #557085; background: #f8fcff; }
.history-tag:hover,
.hot-tag:hover { border-color: #aee7fb; color: #0ea5e9; background: #effaff; }

.category-list .category-card {
  border: 1px solid #cfefff;
  color: #0ea5e9;
  background: linear-gradient(180deg, #effaff 0%, #e8f8ff 100%);
  box-shadow: none;
}
.category-list .category-card:hover { border-color: #7dd3fc; box-shadow: 0 14px 28px rgba(56,189,248,.16); }
.category-list .category-card .category-name { color: #0ea5e9; }
.category-list .category-card .category-icon { filter: brightness(0) saturate(100%) invert(58%) sepia(93%) saturate(1729%) hue-rotate(165deg) brightness(99%); }

.modal-content { border: 1px solid #d7eefb; border-radius: 22px; }

@media (max-width: 720px) {
  .medicine-query-container { padding: 16px; }
  .top-nav { padding: 16px; }
  .top-nav .page-title { font-size: 22px; }
  .default-content, .search-results { padding: 20px; }
}
</style>
