import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    // 开发环境下支持 /merchant 和 /admin 路径访问
    {
      name: 'html-rewrite',
      configureServer(server) {
        server.middlewares.use((req, res, next) => {
          if (req.url === '/merchant' || req.url === '/merchant/') {
            req.url = '/merchant.html'
          } else if (req.url === '/admin' || req.url === '/admin/') {
            req.url = '/admin.html'
          }
          next()
        })
      }
    }
  ],

  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      '@user': resolve(__dirname, 'src/user'),
      '@merchant': resolve(__dirname, 'src/merchant'),
      '@admin': resolve(__dirname, 'src/admin'),
      '@common': resolve(__dirname, 'src/common')
    }
  },

  server: {
    port: 5173,
    host: '0.0.0.0',
    proxy: {
      '/api': {
        target: 'http://localhost:8080',
        changeOrigin: true
      }
    }
  },

  preview: {
    port: 80,
    host: '0.0.0.0'
  },

  build: {
    // 直接输出到 nginx/html 目录
    outDir: resolve(__dirname, 'nginx/html'),
    emptyOutDir: true,

    rollupOptions: {
      input: {
        user: resolve(__dirname, 'index.html'),
        merchant: resolve(__dirname, 'merchant.html'),
        admin: resolve(__dirname, 'admin.html')
      },
      output: {
        manualChunks: {
          'vendor-vue': ['vue', 'vue-router', 'pinia'],
          'vendor-element': ['element-plus']
        }
      }
    }
  }
})
