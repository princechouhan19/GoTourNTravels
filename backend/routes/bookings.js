const router = require('express').Router();
const Booking = require('../models/Booking');
const Vehicle = require('../models/Vehicle');
const bookingService = require('../services/bookingService');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success, AppError, paginate } = require('../utils/helpers');

router.get('/', protect, asyncHandler(async (req, res) => {
  const { page, limit, skip } = paginate(req.query);
  const q = req.user.role === 'admin' ? {} : { user: req.user._id };
  if (req.query.status) q.status = req.query.status;
  const [items, total] = await Promise.all([
    Booking.find(q)
      .populate('vehicle', 'name type primaryImage registrationNumber')
      .populate('user', 'name phone email')
      .sort('-createdAt')
      .skip(skip)
      .limit(limit),
    Booking.countDocuments(q)
  ]);
  success(res, { items, total, page, pages: Math.ceil(total / limit) });
}));

router.get('/active', protect, asyncHandler(async (req, res) => {
  const q = req.user.role === 'admin' ? { status: 'active' } : { user: req.user._id, status: 'active' };
  const items = await Booking.find(q)
    .populate('vehicle')
    .populate('user', 'name phone')
    .sort('-createdAt');
  success(res, { items });
}));

router.get('/:id', protect, asyncHandler(async (req, res) => {
  const booking = await Booking.findById(req.params.id)
    .populate('vehicle')
    .populate('user', 'name phone email avatar')
    .populate('payment');
  if (!booking) throw new AppError('Booking not found', 404);
  if (req.user.role !== 'admin' && booking.user._id.toString() !== req.user._id.toString()) {
    throw new AppError('Not authorized to view this booking', 403);
  }
  success(res, { booking });
}));

router.post('/', protect, asyncHandler(async (req, res) => {
  const booking = await bookingService.createBooking(req.user, req.body);
  await booking.populate('vehicle');
  success(res, { booking }, 'Booking created', 201);
}));

router.post('/:id/activate', protect, adminOnly, asyncHandler(async (req, res) => {
  const b = await bookingService.activateBooking(req.params.id);
  success(res, { booking: b }, 'Booking activated — rental started');
}));

router.post('/:id/complete', protect, adminOnly, asyncHandler(async (req, res) => {
  const b = await bookingService.completeBooking(req.params.id, req.body.endOdometer, req.body.endFuelLevel);
  success(res, { booking: b }, 'Booking completed');
}));

router.post('/:id/cancel', protect, asyncHandler(async (req, res) => {
  const booking = await Booking.findById(req.params.id);
  if (!booking) throw new AppError('Booking not found', 404);
  if (req.user.role !== 'admin' && booking.user.toString() !== req.user._id.toString()) {
    throw new AppError('Not authorized', 403);
  }
  const b = await bookingService.cancelBooking(req.params.id, req.body.reason);
  success(res, { booking: b }, 'Booking cancelled');
}));

router.post('/:id/location', protect, asyncHandler(async (req, res) => {
  const booking = await Booking.findById(req.params.id);
  if (!booking) throw new AppError('Booking not found', 404);
  if (booking.status !== 'active') throw new AppError('Booking is not active', 400);
  booking.tracking.currentLat = req.body.lat;
  booking.tracking.currentLng = req.body.lng;
  booking.tracking.lastPingAt = new Date();
  await booking.save();
  // Emit via socket.io if available
  const io = req.app.get('io');
  if (io) io.to(`booking-${booking._id}`).emit('tracking-update', { lat: req.body.lat, lng: req.body.lng, ts: new Date() });
  success(res, {}, 'Location updated');
}));

router.put('/:id/review', protect, asyncHandler(async (req, res) => {
  const booking = await Booking.findById(req.params.id);
  if (!booking) throw new AppError('Booking not found', 404);
  if (booking.user.toString() !== req.user._id.toString()) throw new AppError('Not authorized', 403);
  if (booking.status !== 'completed') throw new AppError('Only completed bookings can be reviewed', 400);
  booking.rating = req.body.rating;
  booking.reviewText = req.body.comment || '';
  await booking.save();
  // Create review record + recompute vehicle rating
  const Review = require('../models/Review');
  await Review.create({
    user: req.user._id,
    vehicle: booking.vehicle,
    booking: booking._id,
    rating: req.body.rating,
    comment: req.body.comment || '',
    title: req.body.title || ''
  });
  const agg = await Review.aggregate([
    { $match: { vehicle: booking.vehicle, isApproved: true } },
    { $group: { _id: null, avg: { $avg: '$rating' }, count: { $sum: 1 } } }
  ]);
  if (agg[0]) {
    await Vehicle.findByIdAndUpdate(booking.vehicle, {
      rating: Math.round(agg[0].avg * 10) / 10,
      reviewsCount: agg[0].count
    });
  }
  success(res, { booking }, 'Review submitted');
}));

module.exports = router;
