import react from '@vitejs/plugin-react'
import tailwindcss from '@tailwindcss/vite'
import { defineConfig } from 'vite'
import type { ProxyOptions } from 'vite'

const proxy: Record<string, ProxyOptions> = {
  '/api': {
    target: 'http://127.0.0.1:8080',
    changeOrigin: true,
    ws: true,
    rewrite: (path: string) => path.replace(/^\/api/, ''),
    configure(proxy) {
      proxy.on('proxyReq', (proxyRequest, request) => {
        // A same-origin browser request becomes a server-to-server request.
        // Do not forward Vite's origin as if it were a direct API origin.
        const origin = request.headers.origin
        if (origin === `http://${request.headers.host}` || origin === `https://${request.headers.host}`) {
          proxyRequest.removeHeader('origin')
        }
      })
    },
  },
}

export default defineConfig({
  plugins: [react(), tailwindcss()],
  server: { host: '0.0.0.0', port: 5173, strictPort: true, proxy },
  preview: { host: '0.0.0.0', proxy },
  resolve: {
    alias: {
      '@': '/src',
    },
  },
})
