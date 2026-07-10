const router = require('express').Router();
const BusinessSettings = require('../models/BusinessSettings');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success } = require('../utils/helpers');

router.get('/', asyncHandler(async (req, res) => {
  let settings = await BusinessSettings.findOne();
  if (!settings) settings = await BusinessSettings.create({});
  success(res, { settings });
}));

router.put('/', protect, adminOnly, asyncHandler(async (req, res) => {
  const update = req.body;
  const settings = await BusinessSettings.findOneAndUpdate({}, update, { new: true, upsert: true });
  success(res, { settings }, 'Business profile updated');
}));

module.exports = router;
