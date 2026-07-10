const router = require('express').Router();
const SOSRequest = require('../models/SOSRequest');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success, AppError } = require('../utils/helpers');
const notificationService = require('../services/notificationService');

router.get('/', protect, adminOnly, asyncHandler(async (req, res) => {
  const items = await SOSRequest.find().populate('user', 'name phone').populate('booking', 'bookingNumber').sort('-createdAt');
  success(res, { items });
}));

router.get('/me', protect, asyncHandler(async (req, res) => {
  const items = await SOSRequest.find({ user: req.user._id }).sort('-createdAt');
  success(res, { items });
}));

router.post('/', protect, asyncHandler(async (req, res) => {
  const sos = await SOSRequest.create({
    sosNumber: 'SOS' + Date.now(),
    user: req.user._id,
    booking: req.body.bookingId || null,
    location: req.body.location,
    type: req.body.type || 'other',
    description: req.body.description || '',
    contactPhone: req.body.contactPhone || req.user.phone
  });
  // Notify all admins (in real impl: find admins and send FCM)
  await notificationService.broadcast({
    title: 'SOS Alert',
    body: `${req.user.name} raised an SOS (${sos.type})`,
    type: 'sos',
    data: { sosId: sos._id.toString() }
  });
  success(res, { sos }, 'SOS raised. Our team has been alerted.', 201);
}));

router.put('/:id/acknowledge', protect, adminOnly, asyncHandler(async (req, res) => {
  const sos = await SOSRequest.findByIdAndUpdate(req.params.id, {
    status: 'acknowledged',
    handledBy: req.user._id
  }, { new: true });
  success(res, { sos }, 'SOS acknowledged');
}));

router.put('/:id/resolve', protect, adminOnly, asyncHandler(async (req, res) => {
  const sos = await SOSRequest.findByIdAndUpdate(req.params.id, {
    status: 'resolved',
    resolutionNotes: req.body.notes || '',
    resolvedAt: new Date()
  }, { new: true });
  success(res, { sos }, 'SOS resolved');
}));

module.exports = router;
