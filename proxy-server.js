const http = require('http');
const { createProxyMiddleware } = require('http-proxy-middleware');
const express = require('express');

const app = express();

// CORS middleware
app.use((req, res, next) => {
  res.header('Access-Control-Allow-Origin', '*');
  res.header('Access-Control-Allow-Methods', 'GET,PUT,POST,DELETE,OPTIONS');
  res.header('Access-Control-Allow-Headers', 'Content-Type, Authorization, Content-Length, X-Requested-With');
  
  if (req.method === 'OPTIONS') {
    res.sendStatus(200);
  } else {
    next();
  }
});

// Simple test endpoint
app.get('/test', (req, res) => {
  res.json({ message: 'Proxy server is working!', timestamp: new Date().toISOString() });
});

// Proxy all API requests to localhost:8080
const proxy = createProxyMiddleware({
  target: 'http://localhost:8080',
  changeOrigin: true,
  logLevel: 'debug',
});

app.use('/api', proxy);

const PORT = 3000;
const server = app.listen(PORT, '0.0.0.0', () => {
  console.log(`🌐 Proxy server running on http://0.0.0.0:${PORT}`);
  console.log(`📱 Mobile devices can connect to: http://192.168.1.11:${PORT}`);
  console.log(`🔄 Proxying /api/* to http://localhost:8080`);
});

// Handle graceful shutdown
process.on('SIGINT', () => {
  console.log('\n🛑 Shutting down proxy server...');
  server.close(() => {
    console.log('✅ Proxy server stopped');
    process.exit(0);
  });
});