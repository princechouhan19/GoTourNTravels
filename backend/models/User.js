const mongoose = require('mongoose');
const bcrypt = require('bcryptjs');

const userSchema = new mongoose.Schema(
  {
    name: { type: String, required: true, trim: true },
    email: { type: String, required: true, unique: true, lowercase: true, trim: true },
    phone: { type: String, required: true, unique: true, trim: true },
    phoneCountryCode: { type: String, default: '+91' },
    password: { type: String, required: true, select: false, minlength: 6 },
    role: { type: String, enum: ['customer', 'admin'], default: 'customer' },
    avatar: { type: String, default: '' },
    addresses: [
      {
        label: String,
        line1: String,
        city: String,
        state: String,
        pincode: String,
        lat: Number,
        lng: Number
      }
    ],
    drivingLicense: {
      number: { type: String, default: '' },
      imageUrl: { type: String, default: '' },
      verified: { type: Boolean, default: false }
    },
    aadhaar: { number: { type: String, default: '' }, verified: { type: Boolean, default: false } },
    isVerified: { type: Boolean, default: false },
    isBlocked: { type: Boolean, default: false },
    isDeleted: { type: Boolean, default: false },
    fcmToken: { type: String, default: '' },
    lastLoginAt: { type: Date, default: null },
    preferences: {
      darkMode: { type: Boolean, default: false },
      notifications: { type: Boolean, default: true }
    }
  },
  { timestamps: true }
);

userSchema.pre('save', async function (next) {
  if (!this.isModified('password')) return next();
  this.password = await bcrypt.hash(this.password, 10);
  next();
});

userSchema.methods.matchPassword = function (entered) {
  return bcrypt.compare(entered, this.password);
};

userSchema.methods.toJSON = function () {
  const obj = this.toObject();
  delete obj.password;
  delete obj.__v;
  return obj;
};

module.exports = mongoose.model('User', userSchema);
