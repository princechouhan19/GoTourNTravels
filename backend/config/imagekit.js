const ImageKit = require('@imagekit/nodejs').default;

const isImageKitConfigured = () => {
  const key = process.env.IMAGEKIT_PRIVATE_KEY;
  return Boolean(key && key !== 'placeholder');
};

const imagekit = isImageKitConfigured()
  ? new ImageKit({ privateKey: process.env.IMAGEKIT_PRIVATE_KEY })
  : null;

module.exports = { imagekit, isImageKitConfigured };
