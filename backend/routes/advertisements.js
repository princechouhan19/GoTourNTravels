const router = require('express').Router();
const Advertisement = require('../models/Advertisement');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success, AppError, paginate } = require('../utils/helpers');

// Public: get active ads for a placement
router.get('/', asyncHandler(async (req, res) => {
  const now = new Date();
  const q = { isActive: true, startDate: { $lte: now }, endDate: { $gte: now } };
  if (req.query.placement) q.placement = req.query.placement;
  const items = await Advertisement.find(q).populate('targetVehicle', 'name primaryImage').sort('order');
  success(res, { items });
}));

// Admin
router.get('/all', protect, adminOnly, asyncHandler(async (req, res) => {
  const { page, limit, skip } = paginate(req.query);
  const [items, total] = await Promise.all([
    Advertisement.find().sort('-createdAt').skip(skip).limit(limit),
    Advertisement.countDocuments()
  ]);
  success(res, { items, total, page, pages: Math.ceil(total / limit) });
}));

router.post('/', protect, adminOnly, asyncHandler(async (req, res) => {
  const ad = await Advertisement.create(req.body);
  success(res, { ad }, 'Advertisement created', 201);
}));

router.put('/:id', protect, adminOnly, asyncHandler(async (req, res) => {
  const ad = await Advertisement.findByIdAndUpdate(req.params.id, req.body, { new: true });
  success(res, { ad });
}));

router.delete('/:id', protect, adminOnly, asyncHandler(async (req, res) => {
  await Advertisement.findByIdAndDelete(req.params.id);
  success(res, {}, 'Advertisement deleted');
}));

router.post('/:id/click', asyncHandler(async (req, res) => {
  await Advertisement.findByIdAndUpdate(req.params.id, { $inc: { clicks: 1 } });
  success(res, {});
}));

module.exports = router;
