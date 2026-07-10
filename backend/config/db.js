const mongoose = require('mongoose');
const logger = require('../utils/logger');

const connectDB = async () => {
  const uri = process.env.MONGO_URI || 'mongodb://127.0.0.1:27017/gotourntravels';
  try {
    mongoose.set('strictQuery', true);
    const conn = await mongoose.connect(uri);
    logger.info(`MongoDB connected: ${conn.connection.host}/${conn.connection.name}`);
  } catch (err) {
    logger.error('MongoDB connection error: ' + err.message);
    process.exit(1);
  }
};

module.exports = connectDB;
