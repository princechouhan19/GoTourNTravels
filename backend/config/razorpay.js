const Razorpay = require('razorpay');

let instance = null;

const isConfigured = () =>
  process.env.RAZORPAY_KEY_ID &&
  process.env.RAZORPAY_KEY_ID !== 'rzp_test_placeholder';

if (isConfigured()) {
  instance = new Razorpay({
    key_id: process.env.RAZORPAY_KEY_ID,
    key_secret: process.env.RAZORPAY_KEY_SECRET
  });
}

/**
 * In mock mode, returns a fake order id so the client flow works end-to-end.
 */
const createOrder = async (amount, currency = 'INR', receipt = '') => {
  if (instance) {
    return instance.orders.create({ amount, currency, receipt, payment_capture: 1 });
  }
  return {
    id: 'order_mock_' + Date.now(),
    amount,
    currency,
    receipt,
    status: 'created',
    entity: 'order',
    mock: true
  };
};

const verifySignature = (orderId, paymentId, signature) => {
  if (!isConfigured()) return true; // mock mode accepts all
  const crypto = require('crypto');
  const expected = crypto
    .createHmac('sha256', process.env.RAZORPAY_KEY_SECRET)
    .update(orderId + '|' + paymentId)
    .digest('hex');
  return expected === signature;
};

module.exports = { createOrder, verifySignature, isConfigured };
