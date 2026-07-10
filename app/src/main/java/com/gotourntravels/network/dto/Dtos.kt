package com.gotourntravels.network.dto

import com.gotourntravels.models.User

data class AuthResponse(
    val user: User,
    val token: String,
    val otpDev: String? = null
)

data class LoginRequest(val identifier: String, val password: String)
data class RegisterRequest(
    val name: String,
    val email: String,
    val phone: String,
    val password: String
)
data class OtpRequest(val code: String)
data class ForgotRequest(val identifier: String)
data class ResetRequest(val userId: String, val code: String, val newPassword: String)
data class UpdateProfileRequest(
    val name: String? = null,
    val email: String? = null,
    val phone: String? = null,
    val avatar: String? = null,
    val addresses: List<com.gotourntravels.models.Address>? = null,
    val drivingLicense: com.gotourntravels.models.DrivingLicense? = null,
    val preferences: com.gotourntravels.models.UserPreferences? = null,
    val fcmToken: String? = null
)
data class ChangePasswordRequest(val currentPassword: String, val newPassword: String)
data class FcmTokenRequest(val token: String)

data class CreateBookingRequest(
    val vehicle: String,
    val rentalType: String,
    val startDate: String,
    val endDate: String,
    val pickupLocation: com.gotourntravels.models.Location? = null,
    val dropLocation: com.gotourntravels.models.Location? = null,
    val withDriver: Boolean = false,
    val customerNotes: String = "",
    val couponCode: String = ""
)

data class CreateOrderRequest(val bookingId: String, val type: String = "booking")
data class VerifyPaymentRequest(
    val bookingId: String,
    val razorpayOrderId: String,
    val razorpayPaymentId: String,
    val razorpaySignature: String
)

data class CreateVehicleRequest(
    val name: String,
    val type: String,
    val brand: String,
    val model: String,
    val registrationNumber: String,
    val hourlyRate: Int,
    val dailyRate: Int,
    val weeklyRate: Int,
    val securityDeposit: Int,
    val fuelType: String,
    val transmission: String,
    val seatingCapacity: Int,
    val withDriver: Boolean,
    val driverName: String?,
    val driverPhone: String?,
    val features: List<String>,
    val description: String,
    val images: List<String>,
    val primaryImage: String,
    val isFeatured: Boolean,
    val isPublished: Boolean
)

data class UpdateLocationRequest(val lat: Double, val lng: Double)
data class SosRequest(
    val location: com.gotourntravels.models.Location,
    val type: String,
    val description: String,
    val contactPhone: String,
    val bookingId: String?
)
data class ReviewRequest(val rating: Int, val comment: String, val title: String)
data class CancelBookingRequest(val reason: String)

data class Paginated<T>(
    val items: List<T>,
    val total: Int,
    val page: Int,
    val pages: Int
)

data class ApiSuccess<T>(
    val success: Boolean,
    val message: String? = null,
    val data: T
)

data class ApiError(
    val success: Boolean = false,
    val message: String
)
