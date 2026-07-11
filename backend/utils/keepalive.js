const https = require('https');
const logger = require('./logger');

/**
 * Starts a self-pinging loop to keep the Render free instance active.
 * Pings the server's public URL at random intervals around 50 seconds.
 */
function startKeepAlive() {
  // Render automatically sets RENDER_EXTERNAL_URL for web services.
  // Fallback to the known deployed URL.
  const url = process.env.RENDER_EXTERNAL_URL || 'https://gotourntravels.onrender.com';

  if (process.env.NODE_ENV !== 'production' && !process.env.RENDER_EXTERNAL_URL) {
    logger.info('Keep-alive pinger skipped (not in production environment).');
    return;
  }

  logger.info(`Starting keep-alive pinger for: ${url}/health`);

  const ping = () => {
    // Generate a random delay between 45 and 55 seconds (around 50 seconds)
    const randomDelay = Math.floor(Math.random() * (55 - 45 + 1) + 45) * 1000;

    setTimeout(() => {
      https.get(`${url}/health`, (res) => {
        logger.info(`Keep-alive ping to ${url}/health returned status code: ${res.statusCode}`);
        // Schedule next ping
        ping();
      }).on('error', (err) => {
        logger.error(`Keep-alive ping failed: ${err.message}`);
        // Schedule next ping even if failed
        ping();
      });
    }, randomDelay);
  };

  // Start the first ping cycle
  ping();
}

module.exports = startKeepAlive;
