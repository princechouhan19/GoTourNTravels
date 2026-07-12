package com.gotourntravels.models

import com.google.gson.annotations.SerializedName

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val role: String = "customer",
    val avatar: String = "",
    val isVerified: Boolean = false,
    val isBlocked: Boolean = false,
    val drivingLicense: DrivingLicense? = null,
    val preferences: UserPreferences = UserPreferences(),
    val addresses: List<Address> = emptyList(),
    val createdAt: String = ""
)

data class DrivingLicense(
    val number: String = "",
    val imageUrl: String = "",
    val verified: Boolean = false
)

data class UserPreferences(
    val darkMode: Boolean = false,
    val notifications: Boolean = true
)

data class Address(
    val label: String = "",
    val line1: String = "",
    val city: String = "",
    val state: String = "",
    val pincode: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class Vehicle(
    val id: String = "",
    val name: String = "",
    val type: String = "scooter",
    val brand: String = "",
    val model: String = "",
    val year: Int = 2024,
    val registrationNumber: String = "",
    val color: String = "",
    val fuelType: String = "petrol",
    val transmission: String = "manual",
    val seatingCapacity: Int = 2,
    val images: List<String> = emptyList(),
    val primaryImage: String = "",
    val description: String = "",
    val features: List<String> = emptyList(),
    val hourlyRate: Int = 0,
    val dailyRate: Int = 0,
    val weeklyRate: Int = 0,
    val securityDeposit: Int = 0,
    val lateFeePerHour: Int = 50,
    val extraKmChargePerKm: Int = 5,
    val freeKmPerDay: Int = 0,
    val status: String = "available",
    val isFeatured: Boolean = false,
    val withDriver: Boolean = false,
    val driverName: String = "",
    val driverPhone: String = "",
    val location: Location? = null,
    val rating: Double = 0.0,
    val reviewsCount: Int = 0,
    val totalBookings: Int = 0,
    val tags: List<String> = emptyList()
)

data class Location(
    val address: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0
)

data class Booking(
    val id: String = "",
    val bookingNumber: String = "",
    val user: UserSummary? = null,
    val vehicle: Vehicle? = null,
    val rentalType: String = "daily",
    val startDate: String = "",
    val endDate: String = "",
    val durationHours: Int = 0,
    val pickupLocation: Location? = null,
    val dropLocation: Location? = null,
    val withDriver: Boolean = false,
    val verification: Verification = Verification(),
    val advanceAmount: Int = 200,
    val pricing: Pricing = Pricing(),
    val status: String = "pending",
    val paymentStatus: String = "pending",
    val tracking: Tracking = Tracking(),
    val customerNotes: String = "",
    val rating: Int = 0,
    val reviewText: String = "",
    val createdAt: String = ""
)

data class UserSummary(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val avatar: String = ""
)

data class Pricing(
    val baseAmount: Double = 0.0,
    val securityDeposit: Double = 0.0,
    val gstRate: Double = 5.0,
    val gstAmount: Double = 0.0,
    val extraKmCharge: Double = 0.0,
    val lateFee: Double = 0.0,
    val discount: Double = 0.0,
    val total: Double = 0.0,
    val refundableDeposit: Double = 0.0,
    val durationHours: Int = 0
)
data class Verification(val idType: String? = null, val idImageUrl: String = "", val status: String = "pending_office_check")

data class Tracking(
    val startedAt: String? = null,
    val endedAt: String? = null,
    val startOdometer: Int? = null,
    val endOdometer: Int? = null,
    val currentLat: Double? = null,
    val currentLng: Double? = null,
    val lastPingAt: String? = null
)

data class Payment(
    val id: String = "",
    val paymentNumber: String = "",
    val booking: BookingSummary? = null,
    val amount: Double = 0.0,
    val currency: String = "INR",
    val type: String = "booking",
    val method: String = "razorpay",
    val status: String = "created",
    val invoiceNumber: String = "",
    val razorpay: RazorpayRef = RazorpayRef(),
    val paidAt: String? = null
)

data class BookingSummary(val id: String = "", val bookingNumber: String = "")
data class RazorpayRef(val orderId: String = "", val paymentId: String = "", val signature: String = "")

data class Review(
    val id: String = "",
    val user: UserSummary? = null,
    val vehicle: Vehicle? = null,
    val booking: String = "",
    val rating: Int = 0,
    val title: String = "",
    val comment: String = "",
    val isApproved: Boolean = true,
    val isFeatured: Boolean = false,
    val adminReply: String = "",
    val createdAt: String = ""
)

data class Notification(
    val id: String = "",
    val title: String = "",
    val body: String = "",
    val type: String = "system",
    val data: Map<String, Any> = emptyMap(),
    val isRead: Boolean = false,
    val imageUrl: String = "",
    val actionUrl: String = "",
    val createdAt: String = ""
)

data class SOSRequest(
    val id: String = "",
    val sosNumber: String = "",
    val user: UserSummary? = null,
    val booking: String = "",
    val location: Location = Location(),
    val type: String = "other",
    val description: String = "",
    val contactPhone: String = "",
    val status: String = "open",
    val resolutionNotes: String = "",
    val createdAt: String = ""
)

data class BusinessSettings(
    val name: String = "Go Tour N Travels",
    val tagline: String = "",
    val logo: String = "",
    val coverImage: String = "",
    val phone: String = "",
    val email: String = "",
    val address: BusinessAddress = BusinessAddress(),
    val gstNumber: String = "",
    val workingHours: WorkingHours = WorkingHours(),
    val socialLinks: SocialLinks = SocialLinks(),
    val cancellationPolicy: String = "",
    val emergencyContacts: List<EmergencyContact> = emptyList()
)

data class BusinessAddress(
    val line1: String = "",
    val city: String = "Mount Abu",
    val state: String = "Rajasthan",
    val pincode: String = "307501",
    val country: String = "India",
    val lat: Double = 24.5925,
    val lng: Double = 72.7156
)

data class WorkingHours(val open: String = "06:00", val close: String = "22:00", val is24x7: Boolean = false)
data class SocialLinks(val facebook: String = "", val instagram: String = "", val whatsapp: String = "", val website: String = "")
data class EmergencyContact(val name: String = "", val phone: String = "", val role: String = "")

data class Advertisement(
    val id: String = "",
    val title: String = "",
    val subtitle: String = "",
    val imageUrl: String = "",
    val actionUrl: String = "",
    val actionLabel: String = "Book Now",
    val placement: String = "home_banner",
    val targetVehicle: String? = null,
    val isActive: Boolean = true,
    val order: Int = 0,
    val clicks: Int = 0,
    val impressions: Int = 0,
    val startDate: String = "",
    val endDate: String = ""
)

data class Place(
    val name: String = "",
    val category: String = "attraction",
    val description: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val address: String = "",
    val phone: String = "",
    val rating: Double = 0.0,
    val image: String = ""
)

data class DashboardStats(
    val totalBookings: Int = 0,
    val activeBookings: Int = 0,
    val todayBookings: Int = 0,
    val monthBookings: Int = 0,
    val totalCustomers: Int = 0,
    val totalVehicles: Int = 0,
    val availableVehicles: Int = 0,
    val openSos: Int = 0,
    val totalRevenue: Double = 0.0,
    val monthRevenue: Double = 0.0
)
