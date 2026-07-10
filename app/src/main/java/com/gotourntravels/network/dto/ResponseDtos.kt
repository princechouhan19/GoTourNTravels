package com.gotourntravels.network.dto

import com.gotourntravels.models.Booking
import com.gotourntravels.models.Notification
import com.gotourntravels.models.Payment
import com.gotourntravels.models.Review
import com.gotourntravels.models.User
import com.gotourntravels.models.Vehicle

// ---- Convenience response/request wrappers used by GoTourApi & repository ----

data class UserResponse(val user: User)
data class VehicleDetailsResponse(val vehicle: Vehicle, val reviews: List<Review>)
data class BookingDetailsResponse(val booking: Booking)
data class OrderResponse(val payment: Payment, val order: RazorpayOrder)
data class RazorpayOrder(
    val id: String,
    val amount: Int,
    val currency: String,
    val entity: String,
    val status: String,
    val mock: Boolean = false
)
data class PaymentVerifyResponse(val payment: Payment, val booking: Booking)
data class NotificationsResponse(
    val items: List<Notification>,
    val total: Int,
    val unreadCount: Int
)
data class StatusUpdate(val status: String)
data class CompleteBooking(val endOdometer: Int, val endFuelLevel: Int)
data class ReviewUpdate(val isApproved: Boolean, val isFeatured: Boolean, val adminReply: String)
data class ResolveSos(val notes: String)
data class BlockRequest(val block: Boolean)
data class UploadResponse(val url: String, val publicId: String)
data class BookingDayStat(val _id: String, val count: Int, val revenue: Double)
data class RevenueByType(val _id: String, val revenue: Double, val count: Int)
