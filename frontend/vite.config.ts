import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'

export default defineConfig({
  plugins: [
    vue(),
    {
      name: 'html-rewrite',
      configureServer(server) {
        server.middlewares.use((req, res, next) => {
          // 商家端路由重写
          if (req.url === '/merchant' || req.url === '/merchant/' || req.url.startsWith('/merchant/')) {
            req.url = '/merchant.html'
          }
          // 管理端路由重写
          else if (req.url === '/admin' || req.url === '/admin/' || req.url.startsWith('/admin/')) {
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
    outDir: resolve(__dirname, 'nginx/html'),
    emptyOutDir: false,
    minify: 'esbuild',
    esbuildOptions: {
      drop: ['console', 'debugger'],
      treeShaking: true,
      target: 'es2020'
    },
    rollupOptions: {
      input: {
        user: resolve(__dirname, 'index.html'),
        merchant: resolve(__dirname, 'merchant.html'),
        admin: resolve(__dirname, 'admin.html')
      },
      output: {
        manualChunks: {
          'vendor-vue': ['vue', 'vue-router', 'pinia'],
          'vendor-element': ['element-plus'],
          'vendor-icons': ['@element-plus/icons-vue'],
          'vendor-utils': ['axios']
        },
        entryFileNames: 'assets/[name]-[hash].js',
        chunkFileNames: 'assets/[name]-[hash].js',
        assetFileNames: 'assets/[name]-[hash][extname]'
      }
    },
    cssCodeSplit: true,
    assetsInlineLimit: 4096,
    reportCompressedSize: true,
    chunkSizeWarningLimit: 500
  },

  optimizeDeps: {
    include: ['vue', 'vue-router', 'pinia', 'element-plus', '@element-plus/icons-vue', 'axios'],
    exclude: ['lodash-es'],
    esbuildOptions: {
      minify: true,
      treeShaking: true,
      target: 'es2020'
    }
  }
})
