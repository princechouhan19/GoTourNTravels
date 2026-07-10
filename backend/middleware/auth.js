const jwt = require('jsonwebtoken');
const User = require('../models/User');
const { AppError } = require('../utils/helpers');

const signToken = (userId) =>
  jwt.sign({ id: userId }, process.env.JWT_SECRET || 'dev-secret-change-me', {
    expiresIn: process.env.JWT_EXPIRES_IN || '7d'
  });

const verifyToken = (token) => jwt.verify(token, process.env.JWT_SECRET || 'dev-secret-change-me');

const protect = async (req, res, next) => {
  try {
    let token;
    if (req.headers.authorization?.startsWith('Bearer ')) {
      token = req.headers.authorization.split(' ')[1];
    }
    if (!token) throw new AppError('Authentication required', 401);

    const decoded = verifyToken(token);
    const user = await User.findById(decoded.id);
    if (!user) throw new AppError('User no longer exists', 401);
    if (user.isBlocked) throw new AppError('Account is blocked. Please contact support.', 403);

    req.user = user;
    next();
  } catch (err) {
    next(new AppError(err.message || 'Not authorized', 401));
  }
};

const adminOnly = (req, res, next) => {
  if (!req.user || req.user.role !== 'admin') {
    return next(new AppError('Admin access required', 403));
  }
  next();
};

const optionalAuth = async (req, res, next) => {
  try {
    let token;
    if (req.headers.authorization?.startsWith('Bearer ')) {
      token = req.headers.authorization.split(' ')[1];
    }
    if (token) {
      const decoded = verifyToken(token);
      req.user = await User.findById(decoded.id);
    }
  } catch (_) {
    // ignore
  }
  next();
};

module.exports = { signToken, verifyToken, protect, adminOnly, optionalAuth };
