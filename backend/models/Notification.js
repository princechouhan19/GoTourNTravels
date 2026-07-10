const mongoose = require('mongoose');

const notificationSchema = new mongoose.Schema(
  {
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true, index: true },
    title: { type: String, required: true },
    body: { type: String, required: true },
    type: {
      type: String,
      enum: ['booking', 'payment', 'sos', 'promo', 'system', 'review'],
      default: 'system'
    },
    data: { type: mongoose.Schema.Types.Mixed, default: {} },
    isRead: { type: Boolean, default: false, index: true },
    imageUrl: { type: String, default: '' },
    actionUrl: { type: String, default: '' }
  },
  { timestamps: true }
);

module.exports = mongoose.model('Notification', notificationSchema);
