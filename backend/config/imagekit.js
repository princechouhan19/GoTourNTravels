const ImageKit = require('@imagekit/nodejs').default;

const isImageKitConfigured = () => {
  const key = process.env.IMAGEKIT_PRIVATE_KEY;
  const publicKey = process.env.IMAGEKIT_PUBLIC_KEY;
  const urlEndpoint = process.env.IMAGEKIT_URL_ENDPOINT;
  return Boolean(
    key && key !== 'placeholder' &&
    publicKey && publicKey !== 'placeholder' &&
    urlEndpoint && urlEndpoint !== 'placeholder'
  );
};

const imagekit = isImageKitConfigured()
  ? new ImageKit({
      publicKey: process.env.IMAGEKIT_PUBLIC_KEY,
      privateKey: process.env.IMAGEKIT_PRIVATE_KEY,
      urlEndpoint: process.env.IMAGEKIT_URL_ENDPOINT
    })
  : null;

module.exports = { imagekit, isImageKitConfigured };
