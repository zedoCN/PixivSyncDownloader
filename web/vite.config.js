import {defineConfig} from 'vite';
import {createProxyMiddleware} from 'http-proxy-middleware';

export default defineConfig({
    server: {
        proxy: {
            '/api': {
                target: 'http://localhost:8080',
                changeOrigin: true,
                pathRewrite: {
                    '^/api': '',
                },
            },
        },
        port: 7860
    },
});
