import { defineConfig } from 'vite';

export default defineConfig({
  server: {
    historyApiFallback: true, // This redirects all 404s to index.html for the router
  },
});