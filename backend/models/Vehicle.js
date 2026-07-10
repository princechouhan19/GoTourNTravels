const mongoose = require('mongoose');

const vehicleSchema = new mongoose.Schema(
  {
    name: { type: String, required: true, trim: true }, // e.g. "Honda Activa 6G"
    type: {
      type: String,
      enum: ['scooter', 'bike', 'car', 'suv', 'activa'],
      required: true
    },
    brand: { type: String, default: '' },
    model: { type: String, default: '' },
    year: { type: Number, default: new Date().getFullYear() },
    registrationNumber: { type: String, required: true, unique: true },
    color: { type: String, default: '' },
    fuelType: { type: String, enum: ['petrol', 'diesel', 'electric', 'cng'], default: 'petrol' },
    transmission: { type: String, enum: ['manual', 'automatic', 'cvta'], default: 'manual' },
    seatingCapacity: { type: Number, default: 2 },
    images: [{ type: String }],
    primaryImage: { type: String, default: '' },
    description: { type: String, default: '' },
    features: [{ type: String }], // ['helmet', 'top-box', 'ac', 'music']
    // pricing in INR
    hourlyRate: { type: Number, default: 0 },
    dailyRate: { type: Number, default: 0 },
    weeklyRate: { type: Number, default: 0 },
    securityDeposit: { type: Number, default: 0 },
    lateFeePerHour: { type: Number, default: 50 },
    extraKmChargePerKm: { type: Number, default: 5 },
    freeKmPerDay: { type: Number, default: 0 },
    // availability & status
    status: { type: String, enum: ['available', 'on-rent', 'maintenance', 'retired'], default: 'available' },
    isFeatured: { type: Boolean, default: false },
    isPublished: { type: Boolean, default: true },
    // withDriver for cars
    withDriver: { type: Boolean, default: false },
    driverName: { type: String, default: '' },
    driverPhone: { type: String, default: '' },
    // location
    location: {
      address: { type: String, default: 'Mount Abu, Rajasthan' },
      lat: { type: Number, default: 24.5925 },
      lng: { type: Number, default: 72.7156 }
    },
    rating: { type: Number, default: 0 },
    reviewsCount: { type: Number, default: 0 },
    totalBookings: { type: Number, default: 0 },
    odometer: { type: Number, default: 0 },
    tags: [{ type: String }]
  },
  { timestamps: true }
);

vehicleSchema.index({ type: 1, status: 1, isPublished: 1 });
vehicleSchema.index({ name: 'text', brand: 'text', model: 'text' });

module.exports = mongoose.model('Vehicle', vehicleSchema);
