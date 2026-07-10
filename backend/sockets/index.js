const { Server } = require('socket.io');
const logger = require('../utils/logger');

const initSockets = (io) => {
  // Auth middleware — expects token in handshake auth
  io.use((socket, next) => {
    const token = socket.handshake.auth?.token;
    // In production verify JWT here. For dev we accept any token.
    if (!token) {
      socket.userId = 'anonymous';
    } else {
      socket.userId = token; // dev only — replace with real JWT decode
    }
    next();
  });

  io.on('connection', (socket) => {
    logger.info(`[Socket] connected: ${socket.id} user=${socket.userId}`);

    socket.on('join-booking', (bookingId) => {
      socket.join(`booking-${bookingId}`);
      logger.info(`[Socket] joined room booking-${bookingId}`);
    });

    socket.on('leave-booking', (bookingId) => {
      socket.leave(`booking-${bookingId}`);
    });

    socket.on('tracking-update', ({ bookingId, lat, lng }) => {
      socket.to(`booking-${bookingId}`).emit('tracking-update', { lat, lng, ts: new Date() });
    });

    socket.on('disconnect', () => {
      logger.info(`[Socket] disconnected: ${socket.id}`);
    });
  });
};

module.exports = { initSockets };
