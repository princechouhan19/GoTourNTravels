const router = require('express').Router();
const User = require('../models/User');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success, AppError, paginate } = require('../utils/helpers');

router.get('/me', protect, asyncHandler(async (req, res) => {
  success(res, { user: req.user });
}));

router.put('/me', protect, asyncHandler(async (req, res) => {
  const allowed = ['name', 'email', 'phone', 'avatar', 'addresses', 'drivingLicense', 'aadhaar', 'preferences', 'fcmToken'];
  const update = {};
  allowed.forEach((k) => { if (req.body[k] !== undefined) update[k] = req.body[k]; });
  const user = await User.findByIdAndUpdate(req.user._id, update, { new: true });
  success(res, { user }, 'Profile updated');
}));

router.put('/me/password', protect, asyncHandler(async (req, res) => {
  const user = await User.findById(req.user._id).select('+password');
  const match = await user.matchPassword(req.body.currentPassword);
  if (!match) throw new AppError('Current password is incorrect', 400);
  user.password = req.body.newPassword;
  await user.save();
  success(res, {}, 'Password updated');
}));

router.put('/me/fcm-token', protect, asyncHandler(async (req, res) => {
  await User.findByIdAndUpdate(req.user._id, { fcmToken: req.body.token });
  success(res, {}, 'FCM token updated');
}));

// Admin: list all customers
router.get('/', protect, adminOnly, asyncHandler(async (req, res) => {
  const { page, limit, skip } = paginate(req.query);
  const q = { isDeleted: false, role: 'customer' };
  if (req.query.search) {
    q.$or = [
      { name: new RegExp(req.query.search, 'i') },
      { email: new RegExp(req.query.search, 'i') },
      { phone: new RegExp(req.query.search, 'i') }
    ];
  }
  const [items, total] = await Promise.all([
    User.find(q).sort('-createdAt').skip(skip).limit(limit),
    User.countDocuments(q)
  ]);
  success(res, { items, total, page, pages: Math.ceil(total / limit) });
}));

router.get('/:id', protect, adminOnly, asyncHandler(async (req, res) => {
  const user = await User.findById(req.params.id);
  if (!user) throw new AppError('User not found', 404);
  success(res, { user });
}));

router.put('/:id/block', protect, adminOnly, asyncHandler(async (req, res) => {
  const user = await User.findByIdAndUpdate(req.params.id, { isBlocked: !!req.body.block }, { new: true });
  success(res, { user }, 'User status updated');
}));

module.exports = router;
