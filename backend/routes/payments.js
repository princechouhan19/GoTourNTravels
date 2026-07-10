const router = require('express').Router();
const Payment = require('../models/Payment');
const Booking = require('../models/Booking');
const paymentService = require('../services/paymentService');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success, AppError, paginate } = require('../utils/helpers');

router.get('/', protect, asyncHandler(async (req, res) => {
  const { page, limit, skip } = paginate(req.query);
  const q = req.user.role === 'admin' ? {} : { user: req.user._id };
  const [items, total] = await Promise.all([
    Payment.find(q).populate('booking', 'bookingNumber vehicle').sort('-createdAt').skip(skip).limit(limit),
    Payment.countDocuments(q)
  ]);
  success(res, { items, total, page, pages: Math.ceil(total / limit) });
}));

router.get('/:id', protect, asyncHandler(async (req, res) => {
  const payment = await Payment.findById(req.params.id).populate('booking');
  if (!payment) throw new AppError('Payment not found', 404);
  success(res, { payment });
}));

router.post('/create-order', protect, asyncHandler(async (req, res) => {
  const booking = await Booking.findById(req.body.bookingId);
  if (!booking) throw new AppError('Booking not found', 404);
  if (booking.user.toString() !== req.user._id.toString() && req.user.role !== 'admin') {
    throw new AppError('Not authorized', 403);
  }
  const { payment, order } = await paymentService.createPaymentForBooking(booking, req.body.type || 'booking');
  success(res, { payment, order }, 'Order created');
}));

router.post('/verify', protect, asyncHandler(async (req, res) => {
  const { bookingId, razorpayOrderId, razorpayPaymentId, razorpaySignature } = req.body;
  const { payment, booking } = await paymentService.verifyPayment(bookingId, {
    razorpayOrderId,
    razorpayPaymentId,
    razorpaySignature
  });
  success(res, { payment, booking }, 'Payment verified');
}));

router.post('/:id/refund', protect, adminOnly, asyncHandler(async (req, res) => {
  const payment = await paymentService.refundPayment(req.params.id, req.body.amount);
  success(res, { payment }, 'Refund processed');
}));

module.exports = router;
