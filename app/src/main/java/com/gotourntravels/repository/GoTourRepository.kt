package com.gotourntravels.repository

import com.gotourntravels.datastore.UserPrefs
import com.gotourntravels.models.*
import com.gotourntravels.network.GoTourApi
import com.gotourntravels.network.dto.*
import okhttp3.MultipartBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoTourRepository @Inject constructor(
    private val api: GoTourApi,
    private val prefs: UserPrefs
) {

    // ---------- AUTH ----------
    suspend fun register(name: String, email: String, phone: String, password: String) =
        api.register(RegisterRequest(name, email, phone, password)).data.also {
            prefs.saveAuth(it.token, it.user)
        }

    suspend fun login(identifier: String, password: String) =
        api.login(LoginRequest(identifier, password)).data.also {
            prefs.saveAuth(it.token, it.user)
        }

    suspend fun adminLogin(email: String, password: String) =
        api.adminLogin(LoginRequest(email, password)).data.also {
            prefs.saveAuth(it.token, it.user)
        }

    suspend fun verifyOtp(code: String) =
        api.verifyOtp(OtpRequest(code)).data.also { prefs.saveAuth(it.token, it.user) }

    suspend fun resendOtp() = api.resendOtp().data
    suspend fun forgotPassword(identifier: String) = api.forgotPassword(ForgotRequest(identifier)).data
    suspend fun resetPassword(userId: String, code: String, newPassword: String) =
        api.resetPassword(ResetRequest(userId, code, newPassword)).data

    // ---------- USERS ----------
    suspend fun getProfile(): User = api.getProfile().data.user.also { prefs.updateUser(it) }
    suspend fun updateProfile(body: UpdateProfileRequest): User =
        api.updateProfile(body).data.user.also { prefs.updateUser(it) }
    suspend fun changePassword(current: String, newPass: String) =
        api.changePassword(ChangePasswordRequest(current, newPass)).data
    suspend fun updateFcmToken(token: String) = api.updateFcmToken(FcmTokenRequest(token)).data
    suspend fun logout() = prefs.clear()

    // ---------- VEHICLES ----------
    suspend fun listVehicles(type: String? = null, withDriver: Boolean? = null, search: String? = null, page: Int = 1) =
        api.listVehicles(type, withDriver, search, page).data
    suspend fun featuredVehicles(): List<Vehicle> = api.featuredVehicles().data["items"].orEmpty()
    suspend fun vehicleDetails(id: String) = api.vehicleDetails(id).data
    suspend fun createVehicle(body: CreateVehicleRequest) = api.createVehicle(body).data
    suspend fun updateVehicle(id: String, body: CreateVehicleRequest) = api.updateVehicle(id, body).data
    suspend fun deleteVehicle(id: String) = api.deleteVehicle(id).data
    suspend fun updateVehicleStatus(id: String, status: String) = api.updateVehicleStatus(id, StatusUpdate(status)).data

    // ---------- BOOKINGS ----------
    suspend fun listBookings(status: String? = null) = api.listBookings(status).data
    suspend fun activeBookings(): List<Booking> = api.activeBookings().data["items"].orEmpty()
    suspend fun bookingDetails(id: String) = api.bookingDetails(id).data.booking
    suspend fun createBooking(body: CreateBookingRequest) = api.createBooking(body).data
    suspend fun cancelBooking(id: String, reason: String) = api.cancelBooking(id, CancelBookingRequest(reason)).data
    suspend fun updateBookingLocation(id: String, lat: Double, lng: Double) =
        api.updateBookingLocation(id, UpdateLocationRequest(lat, lng)).data
    suspend fun submitReview(id: String, rating: Int, comment: String, title: String = "") =
        api.submitReview(id, ReviewRequest(rating, comment, title)).data
    suspend fun activateBooking(id: String) = api.activateBooking(id).data
    suspend fun completeBooking(id: String, endOdo: Int, endFuel: Int) =
        api.completeBooking(id, CompleteBooking(endOdo, endFuel)).data

    // ---------- PAYMENTS ----------
    suspend fun listPayments() = api.listPayments().data
    suspend fun createOrder(bookingId: String, type: String = "booking") =
        api.createOrder(CreateOrderRequest(bookingId, type)).data
    suspend fun verifyPayment(bookingId: String, orderId: String, paymentId: String, signature: String) =
        api.verifyPayment(VerifyPaymentRequest(bookingId, orderId, paymentId, signature)).data

    // ---------- REVIEWS ----------
    suspend fun listReviews(vehicle: String? = null) = api.listReviews(vehicle).data["items"].orEmpty()
    suspend fun updateReview(id: String, approved: Boolean, featured: Boolean, reply: String) =
        api.updateReview(id, ReviewUpdate(approved, featured, reply)).data
    suspend fun deleteReview(id: String) = api.deleteReview(id).data

    // ---------- NOTIFICATIONS ----------
    suspend fun listNotifications(unread: Boolean? = null) =
        api.listNotifications(if (unread == true) "true" else null).data
    suspend fun markAllRead() = api.markAllRead().data
    suspend fun markRead(id: String) = api.markRead(id).data
    suspend fun deleteNotification(id: String) = api.deleteNotification(id).data

    // ---------- SOS ----------
    suspend fun listAllSos(): List<SOSRequest> = api.listAllSos().data["items"].orEmpty()
    suspend fun mySos(): List<SOSRequest> = api.mySos().data["items"].orEmpty()
    suspend fun createSos(location: Location, type: String, description: String, phone: String, bookingId: String? = null) =
        api.createSos(SosRequest(location, type, description, phone, bookingId)).data
    suspend fun acknowledgeSos(id: String) = api.acknowledgeSos(id).data
    suspend fun resolveSos(id: String, notes: String) = api.resolveSos(id, ResolveSos(notes)).data

    // ---------- BUSINESS ----------
    suspend fun getBusiness(): BusinessSettings = api.getBusiness().data
    suspend fun updateBusiness(body: BusinessSettings) = api.updateBusiness(body).data

    // ---------- ADS ----------
    suspend fun listAds(placement: String? = null): List<Advertisement> =
        api.listAds(placement).data["items"].orEmpty()
    suspend fun listAllAds() = api.listAllAds().data
    suspend fun createAd(body: Advertisement) = api.createAd(body).data
    suspend fun updateAd(id: String, body: Advertisement) = api.updateAd(id, body).data
    suspend fun deleteAd(id: String) = api.deleteAd(id).data

    // ---------- PLACES ----------
    suspend fun listPlaces(category: String? = null): List<Place> =
        api.listPlaces(category).data["items"].orEmpty()

    // ---------- UPLOAD ----------
    suspend fun uploadImage(file: File): UploadResponse {
        val req = file.asRequestBody("image/*".toMediaTypeOrNull())
        val part = MultipartBody.Part.createFormData("file", file.name, req)
        return api.uploadImage(part).data
    }

    // ---------- ADMIN ----------
    suspend fun adminDashboard() = api.adminDashboard().data
    suspend fun adminBookingAnalytics(days: Int = 14) = api.adminBookingAnalytics(days).data["items"].orEmpty()
    suspend fun adminRevenueByType() = api.adminRevenueByType().data["items"].orEmpty()
    suspend fun adminTopVehicles() = api.adminTopVehicles().data["items"].orEmpty()
    suspend fun adminListUsers(search: String? = null) = api.adminListUsers(search).data
    suspend fun adminToggleBlock(id: String, block: Boolean) = api.adminToggleBlock(id, BlockRequest(block)).data
}
