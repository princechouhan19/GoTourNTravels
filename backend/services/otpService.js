/**
 * In-memory OTP store (dev-only). In production swap with Redis or Twilio Verify.
 * Format: { userId: { code, expiresAt, attempts } }
 */
const store = new Map();
const TTL_MS = (parseInt(process.env.OTP_TTL_MINUTES) || 10) * 60 * 1000;
const LEN = parseInt(process.env.OTP_LENGTH) || 6;

const generate = async (userId, phone) => {
  const code = String(Math.floor(Math.random() * Math.pow(10, LEN))).padStart(LEN, '0');
  store.set(userId, { code, expiresAt: Date.now() + TTL_MS, attempts: 0 });
  // TODO: integrate real SMS provider here (e.g. Twilio / MSG91 / TextLocal)
  return code;
};

const verify = async (userId, code) => {
  const entry = store.get(userId);
  if (!entry) return false;
  if (Date.now() > entry.expiresAt) {
    store.delete(userId);
    return false;
  }
  entry.attempts += 1;
  if (entry.attempts > 5) {
    store.delete(userId);
    return false;
  }
  if (entry.code !== code) return false;
  store.delete(userId);
  return true;
};

module.exports = { generate, verify };
