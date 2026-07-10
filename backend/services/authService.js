const User = require('../models/User');
const { signToken } = require('../middleware/auth');
const { AppError } = require('../utils/helpers');
const otpService = require('./otpService');
const logger = require('../utils/logger');

const register = async ({ name, email, phone, password }) => {
  const exists = await User.findOne({ $or: [{ email }, { phone }] });
  if (exists) throw new AppError('User already exists with this email or phone', 409);

  const user = await User.create({ name, email, phone, password });
  // Generate OTP for verification
  const otp = await otpService.generate(user._id, phone);
  logger.info(`[DEV] OTP for ${phone}: ${otp}`);

  return { user, otpDev: otp, token: signToken(user._id) };
};

const login = async ({ identifier, password }) => {
  // identifier can be email or phone
  const user = await User.findOne({
    $or: [{ email: identifier?.toLowerCase() }, { phone: identifier }]
  }).select('+password');
  if (!user) throw new AppError('Invalid credentials', 401);
  if (user.isBlocked) throw new AppError('Account is blocked', 403);

  const match = await user.matchPassword(password);
  if (!match) throw new AppError('Invalid credentials', 401);

  user.lastLoginAt = new Date();
  await user.save();
  return { user, token: signToken(user._id) };
};

const adminLogin = async ({ email, password }) => {
  const user = await User.findOne({ email, role: 'admin' }).select('+password');
  if (!user) throw new AppError('Admin not found', 404);
  const match = await user.matchPassword(password);
  if (!match) throw new AppError('Invalid admin credentials', 401);
  user.lastLoginAt = new Date();
  await user.save();
  return { user, token: signToken(user._id) };
};

const verifyOtp = async (userId, code) => {
  const ok = await otpService.verify(userId, code);
  if (!ok) throw new AppError('Invalid or expired OTP', 400);
  const user = await User.findByIdAndUpdate(userId, { isVerified: true }, { new: true });
  return { user, token: signToken(user._id) };
};

const resendOtp = async (userId) => {
  const user = await User.findById(userId);
  if (!user) throw new AppError('User not found', 404);
  const otp = await otpService.generate(user._id, user.phone);
  logger.info(`[DEV] Re-sent OTP for ${user.phone}: ${otp}`);
  return { otpDev: otp };
};

const forgotPassword = async (identifier) => {
  const user = await User.findOne({ $or: [{ email: identifier }, { phone: identifier }] });
  if (!user) throw new AppError('No account found with these details', 404);
  const otp = await otpService.generate(user._id, user.phone);
  logger.info(`[DEV] Forgot-password OTP for ${user.phone}: ${otp}`);
  return { otpDev: otp, userId: user._id };
};

const resetPassword = async (userId, code, newPassword) => {
  const ok = await otpService.verify(userId, code);
  if (!ok) throw new AppError('Invalid or expired OTP', 400);
  const user = await User.findById(userId).select('+password');
  user.password = newPassword;
  await user.save();
  return { token: signToken(user._id) };
};

module.exports = {
  register,
  login,
  adminLogin,
  verifyOtp,
  resendOtp,
  forgotPassword,
  resetPassword
};
