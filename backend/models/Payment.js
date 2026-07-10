const mongoose = require('mongoose');

const paymentSchema = new mongoose.Schema(
  {
    paymentNumber: { type: String, required: true, unique: true },
    booking: { type: mongoose.Schema.Types.ObjectId, ref: 'Booking', required: true },
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    amount: { type: Number, required: true },
    currency: { type: String, default: 'INR' },
    type: { type: String, enum: ['booking', 'deposit', 'extra', 'refund', 'late-fee'], default: 'booking' },
    method: { type: String, enum: ['razorpay', 'cash', 'upi', 'card', 'wallet', 'mock'], default: 'razorpay' },
    status: {
      type: String,
      enum: ['created', 'authorized', 'captured', 'failed', 'refunded'],
      default: 'created',
      index: true
    },
    razorpay: {
      orderId: { type: String, default: '' },
      paymentId: { type: String, default: '' },
      signature: { type: String, default: '' },
      method: { type: String, default: '' }
    },
    refund: {
      amount: { type: Number, default: 0 },
      id: { type: String, default: '' },
      status: { type: String, default: '' }
    },
    invoiceNumber: { type: String, default: '' },
    invoiceUrl: { type: String, default: '' },
    notes: { type: String, default: '' },
    paidAt: { type: Date, default: null }
  },
  { timestamps: true }
);

module.exports = mongoose.model('Payment', paymentSchema);
