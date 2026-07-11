require('dotenv').config();
const app = require('./app');
const http = require('http');
const { Server } = require('socket.io');
const connectDB = require('./config/db');
const { initSockets } = require('./sockets');
const logger = require('./utils/logger');

const PORT = process.env.PORT || 5000;

// Connect to MongoDB
connectDB();

const server = http.createServer(app);

// Socket.IO
const io = new Server(server, {
  cors: { origin: process.env.CLIENT_URL || '*', methods: ['GET', 'POST'] }
});
initSockets(io);
app.set('io', io);

const startKeepAlive = require('./utils/keepalive');

server.listen(PORT, () => {
  logger.info(`Go Tour N Travels API running on port ${PORT}`);
  logger.info(`Environment: ${process.env.NODE_ENV || 'development'}`);
  startKeepAlive();
});

// Graceful shutdown
process.on('unhandledRejection', (err) => {
  logger.error('Unhandled Rejection: ' + err.message);
  server.close(() => process.exit(1));
});
