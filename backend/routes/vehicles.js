const router = require('express').Router();
const Vehicle = require('../models/Vehicle');
const Review = require('../models/Review');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success, AppError, paginate } = require('../utils/helpers');

// List / search
router.get('/', asyncHandler(async (req, res) => {
  const { page, limit, skip } = paginate(req.query);
  const q = { isPublished: true };
  if (req.query.type) q.type = req.query.type;
  if (req.query.fuelType) q.fuelType = req.query.fuelType;
  if (req.query.withDriver === 'true') q.withDriver = true;
  if (req.query.search) {
    q.$or = [
      { name: new RegExp(req.query.search, 'i') },
      { brand: new RegExp(req.query.search, 'i') },
      { model: new RegExp(req.query.search, 'i') }
    ];
  }
  const [items, total] = await Promise.all([
    Vehicle.find(q).sort({ isFeatured: -1, createdAt: -1 }).skip(skip).limit(limit),
    Vehicle.countDocuments(q)
  ]);
  success(res, { items, total, page, pages: Math.ceil(total / limit) });
}));

router.get('/featured', asyncHandler(async (req, res) => {
  const items = await Vehicle.find({ isPublished: true, isFeatured: true }).limit(10);
  success(res, { items });
}));

router.get('/by-type/:type', asyncHandler(async (req, res) => {
  const items = await Vehicle.find({ type: req.params.type, isPublished: true });
  success(res, { items });
}));

router.get('/:id', asyncHandler(async (req, res) => {
  const vehicle = await Vehicle.findById(req.params.id);
  if (!vehicle) throw new AppError('Vehicle not found', 404);
  const reviews = await Review.find({ vehicle: vehicle._id, isApproved: true })
    .populate('user', 'name avatar')
    .sort('-createdAt')
    .limit(20);
  success(res, { vehicle, reviews });
}));

// Admin: create / update / delete
router.post('/', protect, adminOnly, asyncHandler(async (req, res) => {
  const v = await Vehicle.create(req.body);
  success(res, { vehicle: v }, 'Vehicle created', 201);
}));

router.put('/:id', protect, adminOnly, asyncHandler(async (req, res) => {
  const v = await Vehicle.findByIdAndUpdate(req.params.id, req.body, { new: true });
  if (!v) throw new AppError('Vehicle not found', 404);
  success(res, { vehicle: v }, 'Vehicle updated');
}));

router.delete('/:id', protect, adminOnly, asyncHandler(async (req, res) => {
  await Vehicle.findByIdAndDelete(req.params.id);
  success(res, {}, 'Vehicle deleted');
}));

router.put('/:id/status', protect, adminOnly, asyncHandler(async (req, res) => {
  const v = await Vehicle.findByIdAndUpdate(req.params.id, { status: req.body.status }, { new: true });
  success(res, { vehicle: v }, 'Status updated');
}));

module.exports = router;
