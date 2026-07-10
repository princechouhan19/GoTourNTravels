/**
 * Cloudinary config — gracefully no-ops when keys are placeholders.
 * The upload route falls back to local file URLs in that case.
 */
const { v2: cloudinary } = require('cloudinary');

const isConfigured = () => {
  return (
    process.env.CLOUDINARY_CLOUD_NAME &&
    process.env.CLOUDINARY_CLOUD_NAME !== 'placeholder' &&
    process.env.CLOUDINARY_API_KEY &&
    process.env.CLOUDINARY_API_KEY !== 'placeholder'
  );
};

if (isConfigured()) {
  cloudinary.config({
    cloud_name: process.env.CLOUDINARY_CLOUD_NAME,
    api_key: process.env.CLOUDINARY_API_KEY,
    api_secret: process.env.CLOUDINARY_API_SECRET
  });
}

module.exports = { cloudinary, isCloudinaryConfigured: isConfigured };
