const Payment = require('../models/Payment');
const Booking = require('../models/Booking');
const { AppError } = require('../utils/helpers');
const razorpay = require('../config/razorpay');
const { generateBookingNumber } = require('./bookingService');

const generatePaymentNumber = () => 'PAY' + Date.now() + Math.floor(Math.random() * 100);

const createPaymentForBooking = async (booking, type = 'booking') => {
  const amount = type === 'deposit' ? booking.pricing.securityDeposit : booking.pricing.total;
  const order = await razorpay.createOrder(amount * 100, 'INR', booking.bookingNumber);
  const payment = await Payment.create({
    paymentNumber: generatePaymentNumber(),
    booking: booking._id,
    user: booking.user,
    amount,
    currency: 'INR',
    type,
    method: order.mock ? 'mock' : 'razorpay',
    status: 'created',
    razorpay: { orderId: order.id },
    invoiceNumber: 'INV-' + booking.bookingNumber
  });
  booking.payment = payment._id;
  await booking.save();
  return { payment, order };
};

const verifyPayment = async (bookingId, { razorpayOrderId, razorpayPaymentId, razorpaySignature }) => {
  const ok = razorpay.verifySignature(razorpayOrderId, razorpayPaymentId, razorpaySignature);
  if (!ok) throw new AppError('Payment signature verification failed', 400);

  const payment = await Payment.findOneAndUpdate(
    { 'razorpay.orderId': razorpayOrderId },
    {
      status: 'captured',
      'razorpay.paymentId': razorpayPaymentId,
      'razorpay.signature': razorpaySignature,
      paidAt: new Date()
    },
    { new: true }
  );
  if (!payment) throw new AppError('Payment record not found', 404);

  const booking = await Booking.findById(bookingId);
  if (!booking) throw new AppError('Booking not found', 404);
  booking.paymentStatus = 'paid';
  if (booking.status === 'pending') booking.status = 'confirmed';
  await booking.save();

  return { payment, booking };
};

const refundPayment = async (paymentId, amount) => {
  const payment = await Payment.findById(paymentId);
  if (!payment) throw new AppError('Payment not found', 404);
  payment.status = 'refunded';
  payment.refund = { amount, id: 'rfd_' + Date.now(), status: 'processed' };
  await payment.save();
  return payment;
};

module.exports = { createPaymentForBooking, verifyPayment, refundPayment };
