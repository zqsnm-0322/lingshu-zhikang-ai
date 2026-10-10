import { fileURLToPath, URL } from 'node:url'

import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
// import vueDevTools from 'vite-plugin-vue-devtools' // 禁用Vue DevTools
import AutoImport from 'unplugin-auto-import/vite'
import Components from 'unplugin-vue-components/vite'
import { ElementPlusResolver } from 'unplugin-vue-components/resolvers'

// https://vite.dev/config/
export default defineConfig({
  plugins: [
    vue(),
    // vueDevTools(), // 禁用Vue DevTools提示
    // Element Plus 自动导入配置
    AutoImport({
      resolvers: [ElementPlusResolver({
        importStyle: false // 禁用自动样式导入
      })],
      imports: [
        'vue',
        'vue-router',
        'pinia'
      ],
      dts: true, // 自动生成类型定义文件
      eslintrc: {
        enabled: true
      }
    }),
    Components({
      resolvers: [ElementPlusResolver({
        importStyle: false // 禁用自动样式导入
      })],
      dts: true // 自动生成组件类型定义
    }),
  ],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url))
    },
  },
  server: {
    host: '0.0.0.0',
    port: 3000,
    cors: true,
    allowedHosts: ['nominally-arriving-swine.ngrok-free.app', 'keen-ladybug-great.ngrok-free.app'],
    headers: {
      'Cross-Origin-Opener-Policy': 'same-origin',
      'Cross-Origin-Embedder-Policy': 'unsafe-none'  // 改为unsafe-none以允许MinIO图片加载
    },
    // 添加HMR配置来解决模块热重载问题
    hmr: {
      overlay: false
    },
    proxy: {
      '/api': {
        target: 'http://localhost:8080',  // 改为8081端口
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      },
      '^/ai/': {
        target: 'http://localhost:8080',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path
      },
      // MinIO图片代理
      '/minio': {
        target: 'http://localhost:9000',
        changeOrigin: true,
        secure: false,
        rewrite: (path) => path.replace(/^\/minio/, '')
      }
    }
  },
  optimizeDeps: {
    exclude: ['@pdftron/webviewer'],
    // 强制预构建某些模块
    include: ['vue', 'vue-router', 'element-plus']
  },
  // 添加构建配置来解决动态导入问题
  build: {
    rollupOptions: {
      output: {
        manualChunks: {
          vendor: ['vue', 'vue-router', 'element-plus']
        }
      }
    }
  }
})
