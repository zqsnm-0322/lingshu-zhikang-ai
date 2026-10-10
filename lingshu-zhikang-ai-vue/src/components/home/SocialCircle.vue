<template>
  <div class="social-circle-container" :class="{ 'hide-top': hideTop }">
    <div class="social-website-name">灵枢智康 | AI多模态智慧健康管理平台</div>
    <div class="social-circle">
      <div class="social-quadrant quadrant-1" @click="openLink('gitee')">
        <div class="social-icon gitee-icon">
          <svg viewBox="0 0 1024 1024" width="32" height="32">
            <path d="M512 1024C229.222 1024 0 794.778 0 512S229.222 0 512 0s512 229.222 512 512-229.222 512-512 512z m259.149-568.883h-290.74a25.293 25.293 0 0 0-25.292 25.293l-0.026 63.206c0 13.952 11.315 25.293 25.267 25.293h177.024c13.978 0 25.293 11.315 25.293 25.267v12.646a75.853 75.853 0 0 1-75.853 75.853h-240.23a25.293 25.293 0 0 1-25.267-25.293V417.203a75.853 75.853 0 0 1 75.827-75.853h353.946a25.293 25.293 0 0 0 25.267-25.292l0.077-63.207a25.293 25.293 0 0 0-25.268-25.293H417.152a189.62 189.62 0 0 0-189.62 189.645V771.15c0 13.977 11.316 25.293 25.294 25.293h372.94a170.65 170.65 0 0 0 170.65-170.65V480.384a25.293 25.293 0 0 0-25.267-25.267z" fill="#C71D23"/>
          </svg>
        </div>
      </div>
      
      <div class="social-quadrant quadrant-2" @click="openLink('github')">
        <div class="social-icon github-icon">
          <svg viewBox="0 0 1024 1024" width="32" height="32">
            <path d="M512 42.666667A464.64 464.64 0 0 0 42.666667 502.186667 460.373333 460.373333 0 0 0 363.52 938.666667c23.466667 4.266667 32-9.813333 32-22.186667v-78.08c-130.56 27.733333-158.293333-61.44-158.293333-61.44a122.026667 122.026667 0 0 0-52.053334-67.413333c-42.666667-28.16 3.413333-27.733333 3.413334-27.733334a98.56 98.56 0 0 1 71.68 47.36 101.12 101.12 0 0 0 136.533333 37.973334 99.413333 99.413333 0 0 1 29.866667-61.44c-104.106667-11.52-213.333333-50.773333-213.333334-226.986667a177.066667 177.066667 0 0 1 47.36-124.16 161.28 161.28 0 0 1 4.693334-121.173333s39.68-12.373333 128 46.933333a455.68 455.68 0 0 1 234.666666 0c89.6-59.306667 128-46.933333 128-46.933333a161.28 161.28 0 0 1 4.693334 121.173333A177.066667 177.066667 0 0 1 810.666667 477.866667c0 176.64-110.08 215.466667-213.333334 226.986666a106.666667 106.666667 0 0 1 32 85.333334v125.866666c0 14.933333 8.533333 26.88 32 22.186667A460.8 460.8 0 0 0 981.333333 502.186667 464.64 464.64 0 0 0 512 42.666667" fill="#231F20"/>
          </svg>
        </div>
      </div>
      
      <div class="social-quadrant quadrant-3" @click="openLink('qq')">
        <div class="social-icon qq-icon">
          <svg viewBox="0 0 1024 1024" width="32" height="32">
            <path d="M824.8 613.2c-16-51.4-34.4-94.6-62.7-165.3C766.5 262.2 689.3 112 511.5 112 331.7 112 256.5 265.2 261 447.9c-28.4 70.8-46.7 113.7-62.7 165.3-34 109.5-23 154.8-14.6 155.8 18 2.2 70.1-82.4 70.1-82.4 0 49 25.2 112.9 79.8 159-26.4 8.1-85.7 29.9-71.6 53.8 11.4 19.3 196.2 12.3 249.5 6.3 53.3 6 238.1 13 249.5-6.3 14.1-23.8-45.3-45.7-71.6-53.8 54.6-46.2 79.8-110.1 79.8-159 0 0 52.1 84.6 70.1 82.4 11.4-1.1 19.5-46.4-14.5-155.9z" fill="#12B7F5"/>
          </svg>
        </div>
      </div>
      
      <div class="social-quadrant quadrant-4" @click="openLink('wechat')">
        <div class="social-icon wechat-icon">
          <svg viewBox="0 0 1024 1024" width="32" height="32">
            <path d="M693.12 343.04c11.264 0 22.016 0.512 32.768 1.536C691.712 152.064 510.464 18.432 318.464 18.432 138.752 18.432 0 136.192 0 285.184c0 95.744 52.224 174.08 139.264 235.008L104.448 624.64l122.368-61.44c43.52 8.704 78.336 17.92 122.88 17.92 11.264 0 22.016-0.512 32.768-1.536-6.656-23.04-10.752-47.104-10.752-71.68C371.712 421.376 515.584 343.04 693.12 343.04z m-138.24-69.12c26.112 0 43.52-17.408 43.52-43.52s-17.408-43.52-43.52-43.52c-26.112 0-52.224 17.408-52.224 43.52s26.112 43.52 52.224 43.52z m-277.504 0c26.112 0 43.52-17.408 43.52-43.52s-17.408-43.52-43.52-43.52c-26.112 0-52.224 17.408-52.224 43.52s26.112 43.52 52.224 43.52z" fill="#00C800"/>
            <path d="M1024 508.928c0-122.368-122.368-226.816-261.12-226.816-148.48 0-261.632 104.448-261.632 226.816 0 122.88 113.152 226.816 261.632 226.816 34.816 0 69.632-8.704 104.448-17.408l95.744 52.224-26.112-87.04C984.064 648.192 1024 581.12 1024 508.928z m-348.16-43.52c-17.408 0-34.816-17.408-34.816-34.816 0-17.408 17.408-34.816 34.816-34.816 26.112 0 43.52 17.408 43.52 34.816 0 17.408-17.408 34.816-43.52 34.816z m173.568 0c-17.408 0-34.816-17.408-34.816-34.816 0-17.408 17.408-34.816 34.816-34.816 26.112 0 43.52 17.408 43.52 34.816 0 17.408-17.408 34.816-43.52 34.816z" fill="#00C800"/>
          </svg>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
defineProps({
  hideTop: {
    type: Boolean,
    default: false
  }
})

function openLink(platform) {
  const links = {
    gitee: 'https://gitee.com',
    github: 'https://github.com',
    qq: 'https://im.qq.com',
    wechat: 'https://weixin.qq.com'
  }
  
  if (links[platform]) {
    window.open(links[platform], '_blank')
  }
}
</script>

<style scoped lang="scss">
.social-circle-container {
  position: fixed;
  top: 30px;
  left: 220px;
  z-index: 1100;
  display: flex;
  flex-direction: column;
  align-items: center;
  transition: opacity 0.3s cubic-bezier(0.4, 0, 0.2, 1), transform 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  
  &.hide-top {
    opacity: 0;
    transform: translateY(-20px);
    pointer-events: none;
  }
}

.social-website-name {
  font-size: 24px;
  font-weight: bold;
  color: #00ffff;
  text-shadow: 0 0 10px rgba(0, 255, 255, 0.6), 0 2px 4px rgba(0, 0, 0, 0.3);
  margin-bottom: 15px;
  padding: 8px 16px;
  background: rgba(0, 255, 255, 0.1);
  backdrop-filter: blur(10px);
  -webkit-backdrop-filter: blur(10px);
  border: 1px solid rgba(65, 184, 131, 0.3);
  border-radius: 20px;
  box-shadow: 0 4px 16px rgba(0, 255, 255, 0.2);
  transition: all 0.3s ease;
  letter-spacing: 1px;
  
  &:hover {
    color: #41b883;
    background: rgba(65, 184, 131, 0.15);
    border-color: rgba(0, 255, 255, 0.5);
    box-shadow: 0 6px 20px rgba(65, 184, 131, 0.3);
    transform: translateY(-2px);
    text-shadow: 0 0 15px rgba(65, 184, 131, 0.8), 0 2px 4px rgba(0, 0, 0, 0.3);
  }
}

.social-circle {
  width: 200px;
  height: 200px;
  border-radius: 50%;
  position: relative;
  overflow: hidden;
  background: rgba(0, 255, 255, 0.1);
  backdrop-filter: blur(15px);
  -webkit-backdrop-filter: blur(15px);
  border: 2px solid rgba(65, 184, 131, 0.3);
  box-shadow: 0 12px 40px rgba(0, 255, 255, 0.15), 0 4px 16px rgba(65, 184, 131, 0.1);
  transition: all 0.4s ease;
  
  &:hover {
    transform: scale(1.05);
    background: rgba(0, 255, 255, 0.15);
    border-color: rgba(65, 184, 131, 0.5);
    box-shadow: 0 16px 50px rgba(0, 255, 255, 0.2), 0 6px 20px rgba(65, 184, 131, 0.15);
  }
}

.social-quadrant {
  position: absolute;
  width: calc(50% + 2px);
  height: calc(50% + 2px);
  display: flex;
  align-items: center;
  justify-content: center;
  transition: all 0.3s ease;
  cursor: pointer;
  background: transparent;
  
  &:active {
    background: rgba(230, 230, 230, 0.9);
  }
}

.quadrant-1 {
  top: -2px;
  left: -2px;
  border-radius: 100% 0 0 0;
  
  &:hover {
    background: rgba(199, 29, 35, 0.15);
    z-index: 2;
  }
}

.quadrant-2 {
  top: -2px;
  right: -2px;
  border-radius: 0 100% 0 0;
  
  &:hover {
    background: rgba(36, 41, 46, 0.15);
    z-index: 2;
  }
}

.quadrant-3 {
  bottom: -2px;
  left: -2px;
  border-radius: 0 0 0 100%;
  
  &:hover {
    background: rgba(18, 183, 245, 0.15);
    z-index: 2;
  }
}

.quadrant-4 {
  bottom: -2px;
  right: -2px;
  border-radius: 0 0 100% 0;
  
  &:hover {
    background: rgba(7, 193, 96, 0.15);
    z-index: 2;
  }
}

.social-icon {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  transition: all 0.3s ease;
  
  svg {
    width: 28px;
    height: 28px;
    transition: all 0.3s ease;
  }
}

.gitee-icon svg path {
  fill: #C71D23;
}

.github-icon svg path {
  fill: #24292e;
}

.qq-icon svg path {
  fill: #12B7F5;
}

.wechat-icon svg path {
  fill: #07C160;
}

.social-quadrant:hover .social-icon {
  transform: scale(1.2);
}

.social-quadrant:hover .social-icon svg {
  filter: drop-shadow(0 2px 8px rgba(0, 0, 0, 0.2));
}

.social-quadrant:active .social-icon {
  transform: scale(1.1);
}

@media (max-width: 767.98px) {
  .social-circle-container {
    top: 60px !important;
    left: 50% !important;
    transform: translateX(-50%);
  }
  
  .social-website-name {
    font-size: 16px !important;
    padding: 6px 12px;
    margin-bottom: 10px;
  }
  
  .social-circle {
    width: 120px !important;
    height: 120px !important;
  }
  
  .social-icon {
    width: 24px !important;
    height: 24px !important;
    
    svg {
      width: 18px !important;
      height: 18px !important;
    }
  }
}
</style>

