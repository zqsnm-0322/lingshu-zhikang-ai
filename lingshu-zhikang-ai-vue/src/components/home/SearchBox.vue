<template>
  <div class="search-container" :class="{ 'hide-top': hideTop }">
    <div class="search-box">
      <div class="search-engine-selector" @click="toggleDropdown">
        <span>{{ engine }}</span>
        <span class="dropdown-arrow" :class="{ active: showDropdown }">▼</span>
      </div>
      
      <div v-show="showDropdown" class="engine-dropdown">
        <div 
          v-for="eng in engines" 
          :key="eng.name"
          @click.stop="selectEngine(eng)"
          class="engine-option"
          :class="{ active: eng.name === engine }"
        >
          {{ eng.name }}
        </div>
      </div>
      
      <div class="search-input-container">
        <input 
          :value="query"
          @input="$emit('update:query', $event.target.value)"
          @keyup.enter="$emit('search')"
          type="text" 
          class="search-input"
          :placeholder="currentPlaceholder"
        />
        <button @click="$emit('search')" class="search-btn">
          <span class="search-icon">🔍</span>
        </button>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'

const props = defineProps({
  hideTop: {
    type: Boolean,
    default: false
  },
  query: {
    type: String,
    default: ''
  },
  engine: {
    type: String,
    default: '百度'
  },
  engines: {
    type: Array,
    default: () => []
  }
})

const emit = defineEmits(['update:query', 'update:engine', 'search'])

const showDropdown = ref(false)

const currentPlaceholder = computed(() => {
  const eng = props.engines.find(e => e.name === props.engine)
  return eng ? eng.placeholder : '请输入搜索关键词'
})

const toggleDropdown = () => {
  showDropdown.value = !showDropdown.value
}

const selectEngine = (eng) => {
  emit('update:engine', eng.name)
  showDropdown.value = false
}

const handleClickOutside = (event) => {
  const searchContainer = event.target.closest('.search-container')
  if (!searchContainer) {
    showDropdown.value = false
  }
}

onMounted(() => {
  document.addEventListener('click', handleClickOutside)
})

onUnmounted(() => {
  document.removeEventListener('click', handleClickOutside)
})
</script>

<style scoped lang="scss">
.search-container {
  position: fixed;
  top: 300px;
  right: 570px;
  z-index: 1100;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.hide-top {
    opacity: 0;
    transform: translateY(-20px);
    pointer-events: none;
  }
  
  .search-box {
    display: flex;
    background: linear-gradient(135deg, rgba(255, 255, 255, 0.98), rgba(248, 250, 252, 0.95));
    border-radius: 30px;
    overflow: visible;
    box-shadow: 0 8px 32px rgba(138, 43, 226, 0.15), 0 2px 8px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(20px);
    border: 2px solid rgba(138, 43, 226, 0.4);
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    
    &:hover {
      box-shadow: 0 12px 40px rgba(138, 43, 226, 0.25), 0 4px 16px rgba(0, 0, 0, 0.15);
      border-color: rgba(138, 43, 226, 0.6);
      transform: translateY(-2px);
      background: linear-gradient(135deg, rgba(255, 255, 255, 1), rgba(248, 250, 252, 0.98));
    }
  }
  
  .search-engine-selector {
    position: relative;
    padding: 14px 18px;
    background: linear-gradient(135deg, rgba(138, 43, 226, 0.12), rgba(147, 112, 219, 0.08));
    color: #8a2be2;
    font-weight: 700;
    cursor: pointer;
    display: flex;
    align-items: center;
    gap: 10px;
    min-width: 85px;
    border-radius: 26px 0 0 26px;
    transition: all 0.4s ease;
    font-size: 0.95rem;
    
    &:hover {
      background: linear-gradient(135deg, rgba(138, 43, 226, 0.2), rgba(147, 112, 219, 0.15));
      transform: scale(1.02);
    }
    
    .dropdown-arrow {
      font-size: 0.8rem;
      transition: transform 0.3s ease;
      
      &.active {
        transform: rotate(180deg);
      }
    }
  }
  
  .engine-dropdown {
    position: absolute;
    top: 100%;
    left: 0;
    width: 200px;
    background: white;
    border-radius: 8px;
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
    z-index: 1002;
    margin-top: 4px;
    overflow: hidden;
    animation: dropdownSlide 0.3s ease-out;
    border: 1px solid rgba(138, 43, 226, 0.3);
  }
  
  .engine-option {
    padding: 12px 16px;
    cursor: pointer;
    transition: all 0.2s ease;
    font-size: 0.9rem;
    
    &:hover {
      background: rgba(138, 43, 226, 0.1);
    }
    
    &.active {
      background: rgba(138, 43, 226, 0.2);
      color: #8a2be2;
      font-weight: 600;
    }
  }
  
  .search-input-container {
    display: flex;
    flex: 1;
    align-items: center;
  }
  
  .search-input {
    flex: 1;
    padding: 14px 20px;
    border: none;
    outline: none;
    background: transparent;
    font-size: 13px;
    color: #2d3748;
    min-width: 375px;
    font-weight: 500;
    
    &::placeholder {
      color: #a0aec0;
      font-style: italic;
      font-weight: 400;
    }
    
    &:focus {
      color: #1a202c;
    }
  }
  
  .search-btn {
    padding: 14px 20px;
    background: linear-gradient(135deg, #8a2be2, #9370db, #da70d6);
    border: none;
    cursor: pointer;
    transition: all 0.4s cubic-bezier(0.25, 0.46, 0.45, 0.94);
    border-radius: 0 26px 26px 0;
    position: relative;
    overflow: hidden;
    
    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
      transition: left 0.6s;
    }
    
    .search-icon {
      font-size: 1.2rem;
      color: white;
      filter: drop-shadow(0 1px 2px rgba(0, 0, 0, 0.1));
    }
    
    &:hover {
      background: linear-gradient(135deg, #7b1fa2, #8a2be2, #c961db);
      transform: scale(1.05) translateX(2px);
      box-shadow: 0 4px 20px rgba(138, 43, 226, 0.4);
      
      &::before {
        left: 100%;
      }
    }
    
    &:active {
      transform: scale(0.98) translateX(1px);
    }
  }
}

@keyframes dropdownSlide {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 767.98px) {
  .search-container {
    top: 250px !important;
    left: 50% !important;
    right: auto !important;
    transform: translateX(-50%);
    width: 90% !important;
    max-width: 400px;
    
    .search-box {
      width: 100%;
    }
    
    .search-input {
      min-width: auto !important;
      width: 100%;
      font-size: 14px !important;
      padding: 12px 15px !important;
    }
    
    .search-engine-selector {
      padding: 12px 14px !important;
      min-width: 70px !important;
      font-size: 0.85rem !important;
    }
    
    .search-btn {
      padding: 12px 16px !important;
    }
  }
}

@media (min-width: 768px) and (max-width: 1024px) {
  .search-input {
    min-width: 300px !important;
  }
}
</style>

