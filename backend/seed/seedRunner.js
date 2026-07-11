/**
 * Seed runner — populates DB with vehicles, admin user, business settings,
 * and a few sample bookings. Idempotent: safe to re-run.
 */
require('dotenv').config();
const mongoose = require('mongoose');
const connectDB = require('../config/db');
const User = require('../models/User');
const Vehicle = require('../models/Vehicle');
const Booking = require('../models/Booking');
const BusinessSettings = require('../models/BusinessSettings');
const Advertisement = require('../models/Advertisement');
const bookingService = require('../services/bookingService');
const logger = require('../utils/logger');

const vehicles = [
  {
    name: 'Honda Activa 6G',
    primaryImage: 'https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1200&q=85',
    images: ['https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1200&q=85'],
    type: 'activa',
    brand: 'Honda',
    model: 'Activa 6G',
    year: 2023,
    registrationNumber: 'RJ01-A-2023',
    color: 'Matte Axis Grey',
    fuelType: 'petrol',
    transmission: 'automatic',
    seatingCapacity: 2,
    features: ['helmet', 'top-box', 'usb-charging'],
    hourlyRate: 80,
    dailyRate: 500,
    weeklyRate: 2800,
    securityDeposit: 1000,
    isFeatured: true,
    description: 'The most popular scooter in Mount Abu — light, fuel-efficient, perfect for hill rides.',
    tags: ['best-seller', 'scooter'],
    location: { address: 'Mount Abu Office', lat: 24.5925, lng: 72.7156 }
  },
  {
    name: 'TVS Jupiter',
    primaryImage: 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1200&q=85',
    images: ['https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?auto=format&fit=crop&w=1200&q=85'],
    type: 'scooter',
    brand: 'TVS',
    model: 'Jupiter Classic',
    year: 2022,
    registrationNumber: 'RJ01-B-2022',
    color: 'Mystic Gold',
    fuelType: 'petrol',
    transmission: 'automatic',
    seatingCapacity: 2,
    features: ['helmet', 'top-box', 'mobile-holder'],
    hourlyRate: 90,
    dailyRate: 550,
    weeklyRate: 3000,
    securityDeposit: 1000,
    isFeatured: true,
    description: 'Spacious family scooter with great comfort for sightseeing.',
    tags: ['family', 'scooter'],
    location: { address: 'Mount Abu Office', lat: 24.5925, lng: 72.7156 }
  },
  {
    name: 'Royal Enfield Classic 350',
    primaryImage: 'https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1200&q=85',
    images: ['https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1200&q=85'],
    type: 'bike',
    brand: 'Royal Enfield',
    model: 'Classic 350',
    year: 2023,
    registrationNumber: 'RJ01-C-2023',
    color: 'Chrome Maroon',
    fuelType: 'petrol',
    transmission: 'manual',
    seatingCapacity: 2,
    features: ['helmet', 'leg-guard', 'luggage'],
    hourlyRate: 180,
    dailyRate: 1200,
    weeklyRate: 7000,
    securityDeposit: 3000,
    isFeatured: true,
    description: 'Iconic thumper for mountain lovers — ride to Guru Shikhar in style.',
    tags: ['adventure', 'premium'],
    location: { address: 'Mount Abu Office', lat: 24.5925, lng: 72.7156 }
  },
  {
    name: 'Bajaj Pulsar NS200',
    primaryImage: 'https://images.unsplash.com/photo-1449426468159-d96dbf08f19f?auto=format&fit=crop&w=1200&q=85',
    images: ['https://images.unsplash.com/photo-1449426468159-d96dbf08f19f?auto=format&fit=crop&w=1200&q=85'],
    type: 'bike',
    brand: 'Bajaj',
    model: 'Pulsar NS200',
    year: 2022,
    registrationNumber: 'RJ01-D-2022',
    color: 'Mirage White',
    fuelType: 'petrol',
    transmission: 'manual',
    seatingCapacity: 2,
    features: ['helmet', 'leg-guard'],
    hourlyRate: 150,
    dailyRate: 950,
    weeklyRate: 5500,
    securityDeposit: 2500,
    description: 'Sporty streetfighter for the winding roads of Aravalli.',
    tags: ['sporty'],
    location: { address: 'Mount Abu Office', lat: 24.5925, lng: 72.7156 }
  },
  {
    name: 'Maruti Swift Dzire',
    primaryImage: 'https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&w=1200&q=85',
    images: ['https://images.unsplash.com/photo-1549317661-bd32c8ce0db2?auto=format&fit=crop&w=1200&q=85'],
    type: 'car',
    brand: 'Maruti Suzuki',
    model: 'Swift Dzire VXI',
    year: 2023,
    registrationNumber: 'RJ01-E-2023',
    color: 'Pearl Arctic White',
    fuelType: 'petrol',
    transmission: 'manual',
    seatingCapacity: 5,
    features: ['ac', 'music', '4-seater', 'first-aid'],
    hourlyRate: 350,
    dailyRate: 2500,
    weeklyRate: 14000,
    securityDeposit: 5000,
    withDriver: true,
    driverName: 'Ramesh Patidar',
    driverPhone: '+919414001234',
    isFeatured: true,
    description: 'Compact sedan ideal for couples and small families — comes with driver.',
    tags: ['with-driver', 'family'],
    location: { address: 'Mount Abu Office', lat: 24.5925, lng: 72.7156 }
  },
  {
    name: 'Toyota Innova Crysta',
    primaryImage: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=85',
    images: ['https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=85'],
    type: 'suv',
    brand: 'Toyota',
    model: 'Innova Crysta ZX',
    year: 2022,
    registrationNumber: 'RJ01-F-2022',
    color: 'Pearl White',
    fuelType: 'diesel',
    transmission: 'manual',
    seatingCapacity: 7,
    features: ['ac', 'music', '7-seater', 'charger', 'first-aid'],
    hourlyRate: 600,
    dailyRate: 4500,
    weeklyRate: 27000,
    securityDeposit: 8000,
    withDriver: true,
    driverName: 'Suresh Chauhan',
    driverPhone: '+919414005678',
    isFeatured: true,
    description: 'Premium SUV for group travel and airport transfers from Udaipur / Ahmedabad.',
    tags: ['with-driver', 'group', 'airport'],
    location: { address: 'Mount Abu Office', lat: 24.5925, lng: 72.7156 }
  },
  {
    name: 'Maruti Ertiga',
    primaryImage: 'https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=85',
    images: ['https://images.unsplash.com/photo-1503376780353-7e6692767b70?auto=format&fit=crop&w=1200&q=85'],
    type: 'car',
    brand: 'Maruti Suzuki',
    model: 'Ertiga VXI',
    year: 2022,
    registrationNumber: 'RJ01-G-2022',
    color: 'Magma Grey',
    fuelType: 'petrol',
    transmission: 'manual',
    seatingCapacity: 7,
    features: ['ac', 'music', '7-seater'],
    hourlyRate: 450,
    dailyRate: 3200,
    weeklyRate: 18000,
    securityDeposit: 6000,
    withDriver: true,
    driverName: 'Mohan Patel',
    driverPhone: '+919414009012',
    description: 'Affordable 7-seater with driver — ideal for family tour packages.',
    tags: ['with-driver', 'family'],
    location: { address: 'Mount Abu Office', lat: 24.5925, lng: 72.7156 }
  },
  {
    name: 'Honda Hornet 2.0',
    primaryImage: 'https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1200&q=85',
    images: ['https://images.unsplash.com/photo-1558981806-ec527fa84c39?auto=format&fit=crop&w=1200&q=85'],
    type: 'bike',
    brand: 'Honda',
    model: 'Hornet 2.0',
    year: 2023,
    registrationNumber: 'RJ01-H-2023',
    color: 'Sports Red',
    fuelType: 'petrol',
    transmission: 'manual',
    seatingCapacity: 2,
    features: ['helmet', 'leg-guard'],
    hourlyRate: 130,
    dailyRate: 850,
    weeklyRate: 5000,
    securityDeposit: 2000,
    description: 'Refined 180cc commuter — balanced for city and hills.',
    tags: ['commuter'],
    location: { address: 'Mount Abu Office', lat: 24.5925, lng: 72.7156 }
  }
];

const run = async () => {
  await connectDB();
  logger.info('Seeding…');

  // --- Admin user ---
  const adminEmail = 'admin@gotourntravels.com';
  let admin = await User.findOne({ email: adminEmail });
  if (!admin) {
    admin = await User.create({
      name: 'Go Tour Admin',
      email: adminEmail,
      phone: '9000000000',
      password: 'Admin@123',
      role: 'admin',
      isVerified: true
    });
    logger.info(`Admin created: ${adminEmail} / Admin@123`);
  } else {
    logger.info('Admin already exists');
  }

  // --- Demo customer ---
  let customer = await User.findOne({ email: 'demo@gotourntravels.com' });
  if (!customer) {
    customer = await User.create({
      name: 'Demo Customer',
      email: 'demo@gotourntravels.com',
      phone: '9111111111',
      password: 'Demo@123',
      role: 'customer',
      isVerified: true
    });
    logger.info('Demo customer created: demo@gotourntravels.com / Demo@123');
  }

  // --- Vehicles ---
  for (const v of vehicles) {
    const exists = await Vehicle.findOne({ registrationNumber: v.registrationNumber });
    if (!exists) {
      await Vehicle.create(v);
      logger.info(`Vehicle created: ${v.name}`);
    } else if (v.primaryImage) {
      await Vehicle.updateOne(
        { registrationNumber: v.registrationNumber },
        { $set: { primaryImage: v.primaryImage, images: v.images } }
      );
    }
  }
  const allVehicles = await Vehicle.find();

  // --- Business settings ---
  const settings = await BusinessSettings.findOne();
  if (!settings) {
    await BusinessSettings.create({
      name: 'Go Tour N Travels',
      tagline: 'Your trusted travel partner in Mount Abu',
      phone: '+919000000000',
      email: 'hello@gotourntravels.com',
      address: {
        line1: 'Main Road, Mount Abu',
        city: 'Mount Abu',
        state: 'Rajasthan',
        pincode: '307501',
        country: 'India',
        lat: 24.5925,
        lng: 72.7156
      },
      emergencyContacts: [
        { name: 'Owner — Manoj Ji', phone: '+919000000000', role: 'owner' },
        { name: 'Manager — Ravi Ji', phone: '+919414000999', role: 'manager' }
      ]
    });
    logger.info('Business settings created');
  }

  // --- Advertisements ---
  const adCount = await Advertisement.countDocuments();
  if (adCount === 0) {
    await Advertisement.create([
      {
        title: 'Explore Mount Abu on 2 wheels',
        subtitle: 'Scooters from ₹80/hr —Helmet included',
        imageUrl: 'https://images.unsplash.com/photo-1568772585407-9361f9bf3a87?w=1200',
        actionUrl: 'vehicle:type=scooter',
        actionLabel: 'Rent Now',
        placement: 'home_banner',
        endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
        isActive: true,
        order: 1
      },
      {
        title: 'Airport Transfers — Udaipur & Ahmedabad',
        subtitle: 'Innova / Ertiga with driver — flat fares',
        imageUrl: 'https://images.unsplash.com/photo-1547036967-23d11aacaee0?w=1200',
        actionUrl: 'vehicle:withDriver=true',
        actionLabel: 'Book Cab',
        placement: 'home_banner',
        endDate: new Date(Date.now() + 30 * 24 * 60 * 60 * 1000),
        isActive: true,
        order: 2
      }
    ]);
    logger.info('Advertisements seeded');
  }

  // --- Sample bookings ---
  const existingBookings = await Booking.countDocuments();
  if (existingBookings === 0) {
    const now = Date.now();
    const sample1 = await bookingService.createBooking(customer, {
      vehicle: allVehicles[0]._id,
      rentalType: 'daily',
      startDate: new Date(now - 5 * 24 * 60 * 60 * 1000),
      endDate: new Date(now - 4 * 24 * 60 * 60 * 1000),
      pickupLocation: allVehicles[0].location
    });
    sample1.status = 'completed';
    sample1.paymentStatus = 'paid';
    await sample1.save();

    const sample2 = await bookingService.createBooking(customer, {
      vehicle: allVehicles[4]._id,
      rentalType: 'daily',
      startDate: new Date(now + 1 * 24 * 60 * 60 * 1000),
      endDate: new Date(now + 2 * 24 * 60 * 60 * 1000),
      pickupLocation: allVehicles[4].location
    });
    sample2.status = 'confirmed';
    sample2.paymentStatus = 'paid';
    await sample2.save();

    logger.info('Sample bookings created');
  }

  logger.info('Seed complete!');
  mongoose.disconnect();
  process.exit(0);
};

run().catch((err) => {
  logger.error('Seed failed: ' + err.message);
  process.exit(1);
});
