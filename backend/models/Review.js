const mongoose = require('mongoose');

const reviewSchema = new mongoose.Schema(
  {
    user: { type: mongoose.Schema.Types.ObjectId, ref: 'User', required: true },
    vehicle: { type: mongoose.Schema.Types.ObjectId, ref: 'Vehicle' },
    booking: { type: mongoose.Schema.Types.ObjectId, ref: 'Booking', required: true },
    rating: { type: Number, required: true, min: 1, max: 5 },
    title: { type: String, default: '' },
    comment: { type: String, default: '' },
    isApproved: { type: Boolean, default: true },
    isFeatured: { type: Boolean, default: false },
    adminReply: { type: String, default: '' },
    tags: [{ type: String }]
  },
  { timestamps: true }
);

reviewSchema.index({ vehicle: 1, isApproved: 1 });
reviewSchema.index({ user: 1 });

module.exports = mongoose.model('Review', reviewSchema);
