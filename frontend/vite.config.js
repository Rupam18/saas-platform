import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': {
        target: 'http://localhost:8080', // Spring Boot Backend
        changeOrigin: true,
        rewrite: (path) => path.replace(/^\/api/, ''), // Backend endpoints are at root /login, /tasks etc, but let's assume we map /api/login to /login
      },
      // Or if backend is directly at /login, /tasks:
      '/login': 'http://localhost:8080',
      '/register': 'http://localhost:8080',
      '/tasks': 'http://localhost:8080',
      '/users': 'http://localhost:8080',
    }
  }
})
