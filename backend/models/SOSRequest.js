const mongoose = require('mongoose');

const sosSchema = new mongoose.Schema(
  {
    sosNumber: { type: String, required: true, unique: true },
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    booking: { type: mongoose.Schema.Types.ObjectId, ref: 'Booking', default: null },
    location: {
      lat: { type: Number, required: true },
      lng: { type: Number, required: true },
      address: { type: String, default: '' }
    },
    type: {
      type: String,
      enum: ['accident', 'breakdown', 'medical', 'safety', 'other'],
      default: 'other'
    },
    description: { type: String, default: '' },
    contactPhone: { type: String, default: '' },
    status: {
      type: String,
      enum: ['open', 'acknowledged', 'resolved', 'cancelled'],
      default: 'open',
      index: true
    },
    handledBy: { type: mongoose.Schema.Types.ObjectId, ref: 'User', default: null },
    resolutionNotes: { type: String, default: '' },
    resolvedAt: { type: Date, default: null }
  },
  { timestamps: true }
);

module.exports = mongoose.model('SOSRequest', sosSchema);
