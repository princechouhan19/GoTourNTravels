const router = require('express').Router();
const upload = require('../middleware/upload');
const { isImageKitConfigured, imagekit } = require('../config/imagekit');
const { protect, adminOnly } = require('../middleware/auth');
const { asyncHandler, success } = require('../utils/helpers');
const fs = require('fs');
const path = require('path');

router.post('/', protect, upload.single('file'), asyncHandler(async (req, res) => {
  if (!req.file) throw new Error('No file uploaded');
  if (isImageKitConfigured()) {
    const result = await imagekit.files.upload({
      file: fs.createReadStream(req.file.path),
      fileName: req.file.filename,
      folder: '/gotourntravels',
      useUniqueFileName: true
    });
    fs.unlinkSync(req.file.path);
    return success(res, { url: result.url, publicId: result.fileId }, 'Uploaded');
  }
  // Local fallback
  const url = `${req.protocol}://${req.get('host')}/static/uploads/${req.file.filename}`;
  success(res, { url, publicId: req.file.filename }, 'Uploaded (local)');
}));

router.post('/multiple', protect, upload.array('files', 8), asyncHandler(async (req, res) => {
  const urls = [];
  for (const file of req.files) {
    if (isImageKitConfigured()) {
      const result = await imagekit.files.upload({
        file: fs.createReadStream(file.path),
        fileName: file.filename,
        folder: '/gotourntravels',
        useUniqueFileName: true
      });
      urls.push({ url: result.url, publicId: result.fileId });
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
