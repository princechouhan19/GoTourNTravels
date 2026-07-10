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
  const allowed = /jpeg|jpg|png|webp|gif|pdf/;
  const okExt = allowed.test(path.extname(file.originalname).toLowerCase());
  const okMime = allowed.test(file.mimetype);
  if (okExt && okMime) cb(null, true);
  else cb(new AppError('Only images and PDF files are allowed', 400));
};

module.exports = multer({
  storage,
  fileFilter,
  limits: { fileSize: 8 * 1024 * 1024 } // 8 MB
});
