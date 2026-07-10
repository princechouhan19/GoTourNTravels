const router = require('express').Router();
const Notification = require('../models/Notification');
const { protect } = require('../middleware/auth');
const { asyncHandler, success, paginate } = require('../utils/helpers');

router.get('/', protect, asyncHandler(async (req, res) => {
  const { page, limit, skip } = paginate(req.query);
  const q = { user: req.user._id };
  if (req.query.unread === 'true') q.isRead = false;
  const [items, total, unreadCount] = await Promise.all([
    Notification.find(q).sort('-createdAt').skip(skip).limit(limit),
    Notification.countDocuments(q),
    Notification.countDocuments({ user: req.user._id, isRead: false })
  ]);
  success(res, { items, total, unreadCount, page, pages: Math.ceil(total / limit) });
}));

router.put('/:id/read', protect, asyncHandler(async (req, res) => {
  await Notification.findOneAndUpdate({ _id: req.params.id, user: req.user._id }, { isRead: true });
  success(res, {}, 'Marked as read');
}));

router.put('/read-all', protect, asyncHandler(async (req, res) => {
  await Notification.updateMany({ user: req.user._id, isRead: false }, { isRead: true });
  success(res, {}, 'All marked as read');
}));

router.delete('/:id', protect, asyncHandler(async (req, res) => {
  await Notification.findOneAndDelete({ _id: req.params.id, user: req.user._id });
  success(res, {}, 'Notification deleted');
}));

module.exports = router;
