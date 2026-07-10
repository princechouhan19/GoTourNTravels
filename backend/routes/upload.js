const router = require('express').Router();
const upload = require('../middleware/upload');
const { isCloudinaryConfigured, cloudinary } = require('../config/cloudinary');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success } = require('../utils/helpers');
const fs = require('fs');
const path = require('path');

router.post('/', protect, upload.single('file'), asyncHandler(async (req, res) => {
  if (!req.file) throw new Error('No file uploaded');
  if (isCloudinaryConfigured()) {
    const result = await cloudinary.uploader.upload(req.file.path, {
      folder: 'gotourntravels',
      resource_type: 'auto'
    });
    fs.unlinkSync(req.file.path);
    return success(res, { url: result.secure_url, publicId: result.public_id }, 'Uploaded');
  }
  // Local fallback
  const url = `${req.protocol}://${req.get('host')}/static/uploads/${req.file.filename}`;
  success(res, { url, publicId: req.file.filename }, 'Uploaded (local)');
}));

router.post('/multiple', protect, upload.array('files', 8), asyncHandler(async (req, res) => {
  const urls = [];
  for (const file of req.files) {
    if (isCloudinaryConfigured()) {
      const result = await cloudinary.uploader.upload(file.path, { folder: 'gotourntravels' });
      urls.push({ url: result.secure_url, publicId: result.public_id });
      fs.unlinkSync(file.path);
    } else {
      urls.push({
        url: `${req.protocol}://${req.get('host')}/static/uploads/${file.filename}`,
        publicId: file.filename
      });
    }
  }
  success(res, { items: urls }, 'Uploaded');
}));

module.exports = router;
