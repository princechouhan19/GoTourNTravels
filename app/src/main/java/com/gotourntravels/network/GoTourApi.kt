package com.gotourntravels.network

import com.gotourntravels.network.dto.*
import com.gotourntravels.models.*
import okhttp3.MultipartBody
import retrofit2.http.*

interface GoTourApi {

    // ---------- AUTH ----------
    @POST("auth/register")
    suspend fun register(@Body body: RegisterRequest): ApiSuccess<AuthResponse>

    @POST("auth/login")
    suspend fun login(@Body body: LoginRequest): ApiSuccess<AuthResponse>

    @POST("auth/admin/login")
    suspend fun adminLogin(@Body body: LoginRequest): ApiSuccess<AuthResponse>

    @GET("auth/me")
    suspend fun me(): ApiSuccess<Map<String, Any>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body body: OtpRequest): ApiSuccess<AuthResponse>

    @POST("auth/resend-otp")
    suspend fun resendOtp(): ApiSuccess<Map<String, Any>>

    @POST("auth/forgot-password")
    suspend fun forgotPassword(@Body body: ForgotRequest): ApiSuccess<Map<String, Any>>

    @POST("auth/reset-password")
    suspend fun resetPassword(@Body body: ResetRequest): ApiSuccess<Map<String, Any>>

    // ---------- USERS ----------
    @GET("users/me")
    suspend fun getProfile(): ApiSuccess<UserResponse>

    @PUT("users/me")
    suspend fun updateProfile(@Body body: UpdateProfileRequest): ApiSuccess<UserResponse>

    @PUT("users/me/password")
    suspend fun changePassword(@Body body: ChangePasswordRequest): ApiSuccess<Map<String, Any>>

    @PUT("users/me/fcm-token")
    suspend fun updateFcmToken(@Body body: FcmTokenRequest): ApiSuccess<Map<String, Any>>

    // ---------- VEHICLES ----------
    @GET("vehicles")
    suspend fun listVehicles(
        @Query("type") type: String? = null,
        @Query("withDriver") withDriver: Boolean? = null,
        @Query("search") search: String? = null,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 50
    ): ApiSuccess<Paginated<Vehicle>>

    @GET("vehicles/featured")
    suspend fun featuredVehicles(): ApiSuccess<Map<String, List<Vehicle>>>

    @GET("vehicles/{id}")
    suspend fun vehicleDetails(@Path("id") id: String): ApiSuccess<VehicleDetailsResponse>

    @POST("vehicles")
    suspend fun createVehicle(@Body body: CreateVehicleRequest): ApiSuccess<Vehicle>

    @PUT("vehicles/{id}")
    suspend fun updateVehicle(@Path("id") id: String, @Body body: CreateVehicleRequest): ApiSuccess<Vehicle>

    @DELETE("vehicles/{id}")
    suspend fun deleteVehicle(@Path("id") id: String): ApiSuccess<Map<String, Any>>

    @PUT("vehicles/{id}/status")
    suspend fun updateVehicleStatus(@Path("id") id: String, @Body body: StatusUpdate): ApiSuccess<Vehicle>

    // ---------- BOOKINGS ----------
    @GET("bookings")
    suspend fun listBookings(@Query("status") status: String? = null): ApiSuccess<Paginated<Booking>>

    @GET("bookings/active")
    suspend fun activeBookings(): ApiSuccess<Map<String, List<Booking>>>

    @GET("bookings/{id}")
    suspend fun bookingDetails(@Path("id") id: String): ApiSuccess<BookingDetailsResponse>

    @POST("bookings")
    suspend fun createBooking(@Body body: CreateBookingRequest): ApiSuccess<Booking>

    @POST("bookings/{id}/cancel")
    suspend fun cancelBooking(@Path("id") id: String, @Body body: CancelBookingRequest): ApiSuccess<Booking>

    @POST("bookings/{id}/location")
    suspend fun updateBookingLocation(@Path("id") id: String, @Body body: UpdateLocationRequest): ApiSuccess<Map<String, Any>>

    @PUT("bookings/{id}/review")
    suspend fun submitReview(@Path("id") id: String, @Body body: ReviewRequest): ApiSuccess<Booking>

    // Admin
    @POST("bookings/{id}/activate")
    suspend fun activateBooking(@Path("id") id: String): ApiSuccess<Booking>

    @POST("bookings/{id}/complete")
    suspend fun completeBooking(@Path("id") id: String, @Body body: CompleteBooking): ApiSuccess<Booking>

    // ---------- PAYMENTS ----------
    @GET("payments")
    suspend fun listPayments(): ApiSuccess<Paginated<Payment>>

    @POST("payments/create-order")
    suspend fun createOrder(@Body body: CreateOrderRequest): ApiSuccess<OrderResponse>

    @POST("payments/verify")
    suspend fun verifyPayment(@Body body: VerifyPaymentRequest): ApiSuccess<PaymentVerifyResponse>

    // ---------- REVIEWS ----------
    @GET("reviews")
    suspend fun listReviews(@Query("vehicle") vehicle: String? = null): ApiSuccess<Map<String, List<Review>>>

    @PUT("reviews/{id}")
    suspend fun updateReview(@Path("id") id: String, @Body body: ReviewUpdate): ApiSuccess<Review>

    @DELETE("reviews/{id}")
    suspend fun deleteReview(@Path("id") id: String): ApiSuccess<Map<String, Any>>

    // ---------- NOTIFICATIONS ----------
    @GET("notifications")
    suspend fun listNotifications(@Query("unread") unread: String? = null): ApiSuccess<NotificationsResponse>

    @PUT("notifications/read-all")
    suspend fun markAllRead(): ApiSuccess<Map<String, Any>>

    @PUT("notifications/{id}/read")
    suspend fun markRead(@Path("id") id: String): ApiSuccess<Map<String, Any>>

    @DELETE("notifications/{id}")
    suspend fun deleteNotification(@Path("id") id: String): ApiSuccess<Map<String, Any>>

    // ---------- SOS ----------
    @GET("sos")
    suspend fun listAllSos(): ApiSuccess<Map<String, List<SOSRequest>>>

    @GET("sos/me")
    suspend fun mySos(): ApiSuccess<Map<String, List<SOSRequest>>>

    @POST("sos")
    suspend fun createSos(@Body body: SosRequest): ApiSuccess<SOSRequest>

    @PUT("sos/{id}/acknowledge")
    suspend fun acknowledgeSos(@Path("id") id: String): ApiSuccess<SOSRequest>

    @PUT("sos/{id}/resolve")
    suspend fun resolveSos(@Path("id") id: String, @Body body: ResolveSos): ApiSuccess<SOSRequest>

    // ---------- BUSINESS ----------
    @GET("business")
    suspend fun getBusiness(): ApiSuccess<BusinessSettings>

    @PUT("business")
    suspend fun updateBusiness(@Body body: BusinessSettings): ApiSuccess<BusinessSettings>

    // ---------- ADS ----------
    @GET("ads")
    suspend fun listAds(@Query("placement") placement: String? = null): ApiSuccess<Map<String, List<Advertisement>>>

    @GET("ads/all")
    suspend fun listAllAds(): ApiSuccess<Paginated<Advertisement>>

    @POST("ads")
    suspend fun createAd(@Body body: Advertisement): ApiSuccess<Advertisement>

    @PUT("ads/{id}")
    suspend fun updateAd(@Path("id") id: String, @Body body: Advertisement): ApiSuccess<Advertisement>

    @DELETE("ads/{id}")
    suspend fun deleteAd(@Path("id") id: String): ApiSuccess<Map<String, Any>>

    @POST("ads/{id}/click")
    suspend fun recordAdClick(@Path("id") id: String): ApiSuccess<Map<String, Any>>

    // ---------- PLACES ----------
    @GET("places")
    suspend fun listPlaces(@Query("category") category: String? = null): ApiSuccess<Map<String, List<Place>>>

    // ---------- UPLOAD ----------
    @Multipart
    @POST("upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part): ApiSuccess<UploadResponse>

    // ---------- ADMIN ----------
    @GET("admin/dashboard")
    suspend fun adminDashboard(): ApiSuccess<DashboardStats>

    @GET("admin/analytics/bookings")
    suspend fun adminBookingAnalytics(@Query("days") days: Int = 14): ApiSuccess<Map<String, List<BookingDayStat>>>

    @GET("admin/analytics/revenue-by-type")
    suspend fun adminRevenueByType(): ApiSuccess<Map<String, List<RevenueByType>>>

    @GET("admin/analytics/top-vehicles")
    suspend fun adminTopVehicles(): ApiSuccess<Map<String, List<Vehicle>>>

    @GET("users")
    suspend fun adminListUsers(@Query("search") search: String? = null): ApiSuccess<Paginated<User>>

    @PUT("users/{id}/block")
    suspend fun adminToggleBlock(@Path("id") id: String, @Body body: BlockRequest): ApiSuccess<User>
}
