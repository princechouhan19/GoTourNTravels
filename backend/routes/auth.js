const router = require('express').Router();
const Joi = require('joi');
const authService = require('../services/authService');
const { protect } = require('../middleware/auth');
const validate = require('../middleware/validate');
const { asyncHandler, success } = require('../utils/helpers');

const registerSchema = Joi.object({
  name: Joi.string().min(2).max(80).required(),
  email: Joi.string().email().required(),
  phone: Joi.string().pattern(/^[0-9+\-\s]{7,15}$/).required(),
  password: Joi.string().min(6).max(64).required()
});

const loginSchema = Joi.object({
  identifier: Joi.string().required(),
  password: Joi.string().required()
});

const otpSchema = Joi.object({
  code: Joi.string().length(6).pattern(/^[0-9]+$/).required()
});

const resetSchema = Joi.object({
  code: Joi.string().length(6).pattern(/^[0-9]+$/).required(),
  newPassword: Joi.string().min(6).max(64).required()
});

router.post('/register', validate(registerSchema), asyncHandler(async (req, res) => {
  const { user, otpDev, token } = await authService.register(req.body);
  success(res, { user, token, otpDev }, 'Registered. Verify OTP to activate account.', 201);
}));

router.post('/login', validate(loginSchema), asyncHandler(async (req, res) => {
  const { user, token } = await authService.login(req.body);
  success(res, { user, token }, 'Logged in');
}));

router.post('/admin/login', validate(loginSchema), asyncHandler(async (req, res) => {
  const { user, token } = await authService.adminLogin(req.body);
  success(res, { user, token }, 'Admin logged in');
}));

router.get('/me', protect, asyncHandler(async (req, res) => {
  success(res, { user: req.user }, 'Current user');
}));

router.post('/verify-otp', protect, validate(otpSchema), asyncHandler(async (req, res) => {
  const { user, token } = await authService.verifyOtp(req.user._id, req.body.code);
  success(res, { user, token }, 'Phone verified');
}));

router.post('/resend-otp', protect, asyncHandler(async (req, res) => {
  const { otpDev } = await authService.resendOtp(req.user._id);
  success(res, { otpDev }, 'OTP re-sent');
}));

router.post('/forgot-password', asyncHandler(async (req, res) => {
  const { otpDev, userId } = await authService.forgotPassword(req.body.identifier);
  success(res, { otpDev, userId }, 'OTP sent for password reset');
}));

router.post('/reset-password', validate(resetSchema), asyncHandler(async (req, res) => {
  const { token } = await authService.resetPassword(req.body.userId, req.body.code, req.body.newPassword);
  success(res, { token }, 'Password reset successful');
}));

module.exports = router;
