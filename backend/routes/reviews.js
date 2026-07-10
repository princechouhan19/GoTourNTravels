const router = require('express').Router();
const Review = require('../models/Review');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success, AppError } = require('../utils/helpers');

router.get('/', asyncHandler(async (req, res) => {
  const q = { isApproved: true };
  if (req.query.vehicle) q.vehicle = req.query.vehicle;
  const items = await Review.find(q).populate('user', 'name avatar').populate('vehicle', 'name').sort('-createdAt').limit(100);
  success(res, { items });
}));

router.put('/:id', protect, adminOnly, asyncHandler(async (req, res) => {
  const r = await Review.findByIdAndUpdate(req.params.id, {
    isApproved: req.body.isApproved ?? true,
    isFeatured: req.body.isFeatured ?? false,
    adminReply: req.body.adminReply || ''
  }, { new: true });
  success(res, { review: r });
}));

router.delete('/:id', protect, adminOnly, asyncHandler(async (req, res) => {
  await Review.findByIdAndDelete(req.params.id);
  success(res, {}, 'Review deleted');
}));

module.exports = router;
