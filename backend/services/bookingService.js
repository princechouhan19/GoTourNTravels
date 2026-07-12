const Vehicle = require('../models/Vehicle');
const Booking = require('../models/Booking');
const { AppError } = require('../utils/helpers');

const diffHours = (start, end) => Math.max(Math.ceil((end - start) / (1000 * 60 * 60)), 1);

const isVehicleAvailable = async (vehicleId, startDate, endDate, excludeBookingId = null) => {
  const q = {
    vehicle: vehicleId,
    status: { $in: ['confirmed', 'active'] },
    $or: [{ startDate: { $lt: endDate }, endDate: { $gt: startDate } }]
  };
  if (excludeBookingId) q._id = { $ne: excludeBookingId };
  const clash = await Booking.findOne(q);
  return !clash;
};

/**
 * Returns a fully-computed pricing breakdown for a rental request.
 */
const computePricing = (vehicle, rentalType, startDate, endDate, options = {}) => {
  const hours = diffHours(new Date(startDate), new Date(endDate));
  let baseAmount = 0;
  if (rentalType === 'hourly') {
    baseAmount = vehicle.hourlyRate * hours;
  } else if (rentalType === 'daily') {
    const days = Math.max(Math.ceil(hours / 24), 1);
    baseAmount = vehicle.dailyRate * days;
  } else if (rentalType === 'weekly') {
    const weeks = Math.max(Math.ceil(hours / (24 * 7)), 1);
    baseAmount = vehicle.weeklyRate * weeks;
  }

  const securityDeposit = vehicle.securityDeposit || 0;
  const gstRate = options.gstRate ?? 5;
  const gstAmount = Math.round((baseAmount * gstRate) / 100);
  const extraKmCharge = options.extraKm || 0;
  const lateFee = options.lateFee || 0;
  const discount = options.discount || 0;

  const total = Math.max(baseAmount + securityDeposit + gstAmount + extraKmCharge + lateFee - discount, 0);
  return {
    baseAmount,
    securityDeposit,
    gstRate,
    gstAmount,
    extraKmCharge,
    lateFee,
    discount,
    total,
    refundableDeposit: securityDeposit,
    durationHours: hours
  };
};

const generateBookingNumber = () => {
  const now = new Date();
  const y = now.getFullYear().toString().slice(-2);
  const m = String(now.getMonth() + 1).padStart(2, '0');
  const d = String(now.getDate()).padStart(2, '0');
  const rnd = Math.floor(1000 + Math.random() * 9000);
  return `GTT${y}${m}${d}${rnd}`;
};

const createBooking = async (user, payload) => {
  const vehicle = await Vehicle.findById(payload.vehicle);
  if (!vehicle) throw new AppError('Vehicle not found', 404);
  if (vehicle.status !== 'available' && vehicle.status !== 'on-rent') {
    throw new AppError('Vehicle is not currently available', 400);
  }
  const start = new Date(payload.startDate);
  const end = new Date(payload.endDate);
  if (start >= end) throw new AppError('End date must be after start date', 400);

  const ok = await isVehicleAvailable(vehicle._id, start, end);
  if (!ok) throw new AppError('Vehicle is already booked for these dates', 409);

  const pricing = computePricing(vehicle, payload.rentalType, start, end, {
    gstRate: 5,
    discount: payload.discount || 0
  });

  const booking = await Booking.create({
    bookingNumber: generateBookingNumber(),
    user: user._id,
    vehicle: vehicle._id,
    rentalType: payload.rentalType,
    startDate: start,
    endDate: end,
    durationHours: pricing.durationHours,
    pickupLocation: payload.pickupLocation || vehicle.location,
    dropLocation: payload.dropLocation,
    withDriver: payload.withDriver ?? vehicle.withDriver,
    verification: {
      idType: payload.idType || null,
      idImageUrl: payload.idImageUrl || '',
      status: 'pending_office_check'
    },
    advanceAmount: 200,
    pricing,
    couponCode: payload.couponCode || '',
    customerNotes: payload.customerNotes || '',
    status: 'pending'
  });

  return booking;
};

const activateBooking = async (bookingId) => {
  const booking = await Booking.findById(bookingId);
  if (!booking) throw new AppError('Booking not found', 404);
  booking.status = 'active';
  booking.tracking.startedAt = new Date();
  await booking.save();
  await Vehicle.findByIdAndUpdate(booking.vehicle, { status: 'on-rent' });
  return booking;
};

const completeBooking = async (bookingId, endOdometer, endFuelLevel) => {
  const booking = await Booking.findById(bookingId);
  if (!booking) throw new AppError('Booking not found', 404);
  booking.status = 'completed';
  booking.tracking.endedAt = new Date();
  booking.tracking.endOdometer = endOdometer;
  booking.tracking.endFuelLevel = endFuelLevel;
  await booking.save();
  await Vehicle.findByIdAndUpdate(booking.vehicle, {
    status: 'available',
    $inc: { totalBookings: 1 }
  });
  return booking;
};

const cancelBooking = async (bookingId, reason) => {
  const booking = await Booking.findById(bookingId);
  if (!booking) throw new AppError('Booking not found', 404);
  if (booking.status === 'completed') throw new AppError('Completed bookings cannot be cancelled', 400);
  booking.status = 'cancelled';
  booking.cancellationReason = reason || 'Cancelled by user';
  await booking.save();
  if (booking.vehicle) {
    await Vehicle.findByIdAndUpdate(booking.vehicle, { status: 'available' });
  }
  return booking;
};

module.exports = {
  isVehicleAvailable,
  computePricing,
  generateBookingNumber,
  createBooking,
  activateBooking,
  completeBooking,
  cancelBooking,
  diffHours
};
