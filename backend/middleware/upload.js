const multer = require('multer');
const path = require('path');
const fs = require('fs');
const { AppError } = require('../utils/helpers');

const uploadsDir = path.join(__dirname, '..', 'public', 'uploads');
if (!fs.existsSync(uploadsDir)) fs.mkdirSync(uploadsDir, { recursive: true });

const storage = multer.diskStorage({
  destination: (req, file, cb) => cb(null, uploadsDir),
  filename: (req, file, cb) => {
    const ext = path.extname(file.originalname);
    const name = `${Date.now()}-${Math.round(Math.random() * 1e9)}${ext}`;
    cb(null, name);
  }
});

const fileFilter = (req, file, cb) => {
  // Android content providers may report a generic MIME type even for a valid PNG/JPEG.
  // Trust a known extension in that case, but never accept an unknown extension.
  const allowedExtensions = new Set(['.jpeg', '.jpg', '.png', '.webp', '.gif', '.pdf']);
  const allowedMimes = new Set([
    'image/jpeg', 'image/jpg', 'image/png', 'image/webp', 'image/gif',
    'application/pdf', 'application/octet-stream'
  ]);
  const okExt = allowedExtensions.has(path.extname(file.originalname).toLowerCase());
  const okMime = allowedMimes.has((file.mimetype || '').toLowerCase());
  if (okExt && okMime) cb(null, true);
  else cb(new AppError('Only images and PDF files are allowed', 400));
};

module.exports = multer({
  storage,
  fileFilter,
  limits: { fileSize: 8 * 1024 * 1024 } // 8 MB
});
