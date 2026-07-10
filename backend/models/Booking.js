const mongoose = require('mongoose');

const bookingSchema = new mongoose.Schema(
  {
    bookingNumber: { type: String, required: true, unique: true, index: true },
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    vehicle: { type: mongoose.Schema.Types.ObjectId, ref: 'Vehicle', required: true },
    rentalType: { type: String, enum: ['hourly', 'daily', 'weekly'], required: true },
    startDate: { type: Date, required: true },
    endDate: { type: Date, required: true },
    durationHours: { type: Number, required: true },
    pickupLocation: {
      address: { type: String, default: 'Go Tour N Travels Office, Mount Abu' },
      lat: { type: Number, default: 24.5925 },
      lng: { type: Number, default: 72.7156 }
    },
    dropLocation: {
      address: String,
      lat: Number,
      lng: Number
    },
    withDriver: { type: Boolean, default: false },
    // pricing breakdown
    pricing: {
      baseAmount: { type: Number, required: true },
      securityDeposit: { type: Number, default: 0 },
      gstRate: { type: Number, default: 5 },
      gstAmount: { type: Number, default: 0 },
      extraKmCharge: { type: Number, default: 0 },
      lateFee: { type: Number, default: 0 },
      discount: { type: Number, default: 0 },
      total: { type: Number, required: true },
      refundableDeposit: { type: Number, default: 0 }
    },
    couponCode: { type: String, default: '' },
    status: {
      type: String,
      enum: [
        'pending',
        'confirmed',
        'active',
        'completed',
        'cancelled',
        'expired',
        'no-show'
      ],
      default: 'pending',
      index: true
    },
    paymentStatus: {
      type: String,
      enum: ['pending', 'partial', 'paid', 'refunded', 'failed'],
      default: 'pending'
    },
    payment: { type: mongoose.Schema.Types.ObjectId, ref: 'Payment' },
    razorpayOrderId: { type: String, default: '' },
    // active rental tracking
    tracking: {
      startedAt: { type: Date, default: null },
      endedAt: { type: Date, default: null },
      startOdometer: { type: Number, default: null },
      endOdometer: { type: Number, default: null },
      startFuelLevel: { type: Number, default: null },
      endFuelLevel: { type: Number, default: null },
      currentLat: { type: Number, default: null },
      currentLng: { type: Number, default: null },
      lastPingAt: { type: Date, default: null }
    },
    customerNotes: { type: String, default: '' },
    adminNotes: { type: String, default: '' },
    cancellationReason: { type: String, default: '' },
    rating: { type: Number, default: 0 },
    reviewText: { type: String, default: '' },
    invoiceUrl: { type: String, default: '' }
  },
  { timestamps: true }
);

bookingSchema.index({ user: 1, status: 1 });
bookingSchema.index({ vehicle: 1, startDate: 1, endDate: 1 });

module.exports = mongoose.model('Booking', bookingSchema);
