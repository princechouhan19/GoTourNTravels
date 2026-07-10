const mongoose = require('mongoose');

const businessSettingsSchema = new mongoose.Schema(
  {
    name: { type: String, default: 'Go Tour N Travels' },
    tagline: { type: String, default: 'Your trusted travel partner in Mount Abu' },
    logo: { type: String, default: '' },
    coverImage: { type: String, default: '' },
    phone: { type: String, default: '+919000000000' },
    email: { type: String, default: 'hello@gotourntravels.com' },
    address: {
      line1: { type: String, default: 'Main Road, Mount Abu' },
      city: { type: String, default: 'Mount Abu' },
      state: { type: String, default: 'Rajasthan' },
      pincode: { type: String, default: '307501' },
      country: { type: String, default: 'India' },
      lat: { type: Number, default: 24.5925 },
      lng: { type: Number, default: 72.7156 }
    },
    gstNumber: { type: String, default: '' },
    panNumber: { type: String, default: '' },
    workingHours: {
      open: { type: String, default: '06:00' },
      close: { type: String, default: '22:00' },
      is24x7: { type: Boolean, default: false }
    },
    socialLinks: {
      facebook: { type: String, default: '' },
      instagram: { type: String, default: '' },
      whatsapp: { type: String, default: '' },
      website: { type: String, default: '' }
    },
    defaultGstRate: { type: Number, default: 5 },
    securityDepositPolicy: { type: String, default: 'Refundable after vehicle return inspection' },
    cancellationPolicy: {
      type: String,
      default: 'Free cancellation up to 2 hours before pickup. After that, 50% of base amount is non-refundable.'
    },
    emergencyContacts: [
      { name: String, phone: String, role: String }
    ],
    updatedAt: { type: Date, default: Date.now }
  },
  { timestamps: true }
);

module.exports = mongoose.model('BusinessSettings', businessSettingsSchema);
