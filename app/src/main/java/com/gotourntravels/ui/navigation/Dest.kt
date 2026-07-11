package com.gotourntravels.ui.navigation

sealed class Dest(val route: String) {

    // ---- Auth ----
    data object Splash : Dest("splash")
    data object Onboarding : Dest("onboarding")
    data object Login : Dest("login")
    data object Register : Dest("register")
    data object Otp : Dest("otp")
    data object ForgotPassword : Dest("forgot-password")

    // ---- Customer main (bottom bar) ----
    data object CustomerHome : Dest("customer-home")
    data object Bookings : Dest("bookings")
    data object TouristMap : Dest("tourist-map")
    data object Profile : Dest("profile")
    data object EditProfile : Dest("profile/edit")
    data object ChangePassword : Dest("profile/password")
    data object SavedAddresses : Dest("profile/addresses")

    // ---- Customer sub ----
    data object SearchVehicles : Dest("search-vehicles?type={type}")
    data object VehicleDetails : Dest("vehicle/{id}")
    data object BookVehicle : Dest("book-vehicle/{id}")
    data object BookingSummary : Dest("booking-summary/{id}")
    data object ActiveBooking : Dest("active-booking/{id}")
    data object BookingHistory : Dest("booking-history")
    data object Payments : Dest("payments")
    data object NearbyPetrol : Dest("nearby/petrol")
    data object NearbyHospitals : Dest("nearby/hospital")
    data object NearbyPolice : Dest("nearby/police")
    data object TouristAttractions : Dest("attractions")
    data object Sos : Dest("sos")
    data object Notifications : Dest("notifications")
    data object Settings : Dest("settings")
    data object HelpSupport : Dest("help")
    data object About : Dest("about")

    // ---- Admin ----
    data object AdminDashboard : Dest("admin-dashboard")
    data object AdminVehicles : Dest("admin-vehicles")
    data object AdminAddVehicle : Dest("admin-vehicles/new")
    data object AdminEditVehicle : Dest("admin-vehicles/edit/{id}")
    data object AdminBookings : Dest("admin-bookings")
    data object AdminCustomers : Dest("admin-customers")
    data object AdminEarnings : Dest("admin-earnings")
    data object AdminAnalytics : Dest("admin-analytics")
    data object AdminSos : Dest("admin-sos")
    data object AdminReviews : Dest("admin-reviews")
    data object AdminAds : Dest("admin-ads")
    data object AdminBusinessProfile : Dest("admin-business-profile")

    // Helpers
    companion object {
        fun vehicleDetails(id: String) = "vehicle/$id"
        fun bookVehicle(id: String) = "book-vehicle/$id"
        fun bookingSummary(id: String) = "booking-summary/$id"
        fun activeBooking(id: String) = "active-booking/$id"
        fun searchVehicles(type: String = "") = if (type.isBlank()) "search-vehicles?type=" else "search-vehicles?type=$type"
        fun editVehicle(id: String) = "admin-vehicles/edit/$id"
    }
}
