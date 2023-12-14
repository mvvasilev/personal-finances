import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'
import { fileURLToPath, URL } from 'node:url'

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: [
      { find: '@', replacement: fileURLToPath(new URL('./src', import.meta.url)) }
    ]
  },
  optimizeDeps: {
    include: ['@mui/material/Tooltip']
  },
  server: {
    host: '127.0.0.1',
    port: 5173,
  }
})
