const mongoose = require('mongoose');

const advertisementSchema = new mongoose.Schema(
  {
    title: { type: String, required: true },
    subtitle: { type: String, default: '' },
    imageUrl: { type: String, required: true },
    actionUrl: { type: String, default: '' },
    actionLabel: { type: String, default: 'Book Now' },
    placement: {
      type: String,
      enum: ['home_banner', 'home_strip', 'search_top', 'app_popup'],
      default: 'home_banner'
    },
    targetVehicle: { type: mongoose.Schema.Types.ObjectId, ref: 'Vehicle', default: null },
    startDate: { type: Date, default: Date.now },
    endDate: { type: Date, required: true },
    isActive: { type: Boolean, default: true, index: true },
    impressions: { type: Number, default: 0 },
    clicks: { type: Number, default: 0 },
    order: { type: Number, default: 0 }
  },
  { timestamps: true }
);

module.exports = mongoose.model('Advertisement', advertisementSchema);
