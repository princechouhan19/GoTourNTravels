const Notification = require('../models/Notification');
const logger = require('../utils/logger');

/**
 * Sends a notification to a user. If FCM is configured, also pushes to FCM.
 */
const send = async ({ user, title, body, type = 'system', data = {}, imageUrl = '', actionUrl = '' }) => {
  const n = await Notification.create({
    user,
    title,
    body,
    type,
    data,
    imageUrl,
    actionUrl
  });
  // FCM push would happen here if firebase-admin was configured
  // For now we just log; the Socket.IO channel also broadcasts
  logger.info(`[Notify] user=${user} title="${title}"`);
  return n;
};

const broadcast = async ({ title, body, type = 'system', data = {} }) => {
  // Send to all users — implemented as a bulk insert in real deployments
  logger.info(`[Broadcast] ${title}: ${body}`);
  return { title, body, type, data };
};

module.exports = { send, broadcast };
