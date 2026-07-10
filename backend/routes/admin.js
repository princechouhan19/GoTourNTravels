const router = require('express').Router();
const Booking = require('../models/Booking');
const Vehicle = require('../models/Vehicle');
const User = require('../models/User');
const Payment = require('../models/Payment');
const SOSRequest = require('../models/SOSRequest');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success } = require('../utils/helpers');

// Dashboard summary
router.get('/dashboard', protect, adminOnly, asyncHandler(async (req, res) => {
  const today = new Date();
  const startOfToday = new Date(today.getFullYear(), today.getMonth(), today.getDate());
  const startOfMonth = new Date(today.getFullYear(), today.getMonth(), 1);

  const [
    totalBookings,
    activeBookings,
    todayBookings,
    monthBookings,
    totalCustomers,
    totalVehicles,
    availableVehicles,
    openSos,
    revenueAgg,
    monthRevenueAgg
  ] = await Promise.all([
    Booking.countDocuments(),
    Booking.countDocuments({ status: 'active' }),
    Booking.countDocuments({ createdAt: { $gte: startOfToday } }),
    Booking.countDocuments({ createdAt: { $gte: startOfMonth } }),
    User.countDocuments({ role: 'customer' }),
    Vehicle.countDocuments(),
    Vehicle.countDocuments({ status: 'available' }),
    SOSRequest.countDocuments({ status: 'open' }),
    Payment.aggregate([{ $match: { status: 'captured' } }, { $group: { _id: null, total: { $sum: '$amount' } } }]),
    Payment.aggregate([
      { $match: { status: 'captured', paidAt: { $gte: startOfMonth } } },
      { $group: { _id: null, total: { $sum: '$amount' } } }
    ])
  ]);

  success(res, {
    totalBookings,
    activeBookings,
    todayBookings,
    monthBookings,
    totalCustomers,
    totalVehicles,
    availableVehicles,
    openSos,
    totalRevenue: revenueAgg[0]?.total || 0,
    monthRevenue: monthRevenueAgg[0]?.total || 0
  });
}));

// Bookings by day for last 14 days (chart data)
router.get('/analytics/bookings', protect, adminOnly, asyncHandler(async (req, res) => {
  const days = parseInt(req.query.days) || 14;
  const since = new Date(Date.now() - days * 24 * 60 * 60 * 1000);
  const items = await Booking.aggregate([
    { $match: { createdAt: { $gte: since } } },
    { $group: { _id: { $dateToString: { format: '%Y-%m-%d', date: '$createdAt' } }, count: { $sum: 1 }, revenue: { $sum: '$pricing.total' } } },
    { $sort: { _id: 1 } }
  ]);
  success(res, { items });
}));

// Revenue by vehicle type
router.get('/analytics/revenue-by-type', protect, adminOnly, asyncHandler(async (req, res) => {
  const items = await Booking.aggregate([
    { $match: { status: { $in: ['completed', 'active'] } } },
    { $lookup: { from: 'vehicles', localField: 'vehicle', foreignField: '_id', as: 'v' } },
    { $unwind: '$v' },
    { $group: { _id: '$v.type', revenue: { $sum: '$pricing.total' }, count: { $sum: 1 } } },
    { $sort: { revenue: -1 } }
  ]);
  success(res, { items });
}));

// Top vehicles
router.get('/analytics/top-vehicles', protect, adminOnly, asyncHandler(async (req, res) => {
  const items = await Vehicle.find().sort({ totalBookings: -1, rating: -1 }).limit(5).select('name type primaryImage totalBookings rating');
  success(res, { items });
}));

module.exports = router;
