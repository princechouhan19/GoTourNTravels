package com.gotourntravels.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun GoTourNavHost(startDestination: String) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    val showBottomBar = currentRoute in setOf(
        Dest.CustomerHome.route,
        Dest.Bookings.route,
        Dest.TouristMap.route,
        Dest.Profile.route,
        Dest.AdminDashboard.route
    )

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = listOf(
                        Triple(Dest.CustomerHome.route, Icons.Default.Home, "Home"),
                        Triple(Dest.Bookings.route, Icons.Default.ReceiptLong, "Bookings"),
                        Triple(Dest.TouristMap.route, Icons.Default.Map, "Map"),
                        Triple(Dest.Profile.route, Icons.Default.Person, "Profile")
                    )
                    items.forEach { (route, icon, label) ->
                        NavigationBarItem(
                            selected = currentRoute == route,
                            onClick = {
                                navController.navigate(route) {
                                    popUpTo(Dest.CustomerHome.route) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(icon, contentDescription = label) },
                            label = { Text(label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = startDestination,
            modifier = Modifier.padding(padding)
        ) {
            // ---------- Auth ----------
            composable(Dest.Splash.route) { com.gotourntravels.ui.screens.auth.SplashScreen(navController) }
            composable(Dest.Onboarding.route) { com.gotourntravels.ui.screens.auth.OnboardingScreen(navController) }
            composable(Dest.Login.route) { com.gotourntravels.ui.screens.auth.LoginScreen(navController) }
            composable(Dest.Register.route) { com.gotourntravels.ui.screens.auth.RegisterScreen(navController) }
            composable(Dest.Otp.route) { com.gotourntravels.ui.screens.auth.OtpScreen(navController) }
            composable(Dest.ForgotPassword.route) { com.gotourntravels.ui.screens.auth.ForgotPasswordScreen(navController) }

            // ---------- Customer ----------
            composable(Dest.CustomerHome.route) { com.gotourntravels.ui.screens.customer.HomeScreen(navController) }
            composable(
                Dest.SearchVehicles.route,
                arguments = listOf(navArgument("type") { type = NavType.StringType; defaultValue = "" })
            ) { entry ->
                com.gotourntravels.ui.screens.customer.SearchVehiclesScreen(
                    navController,
                    entry.arguments?.getString("type") ?: ""
                )
            }
            composable(
                Dest.VehicleDetails.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                com.gotourntravels.ui.screens.customer.VehicleDetailsScreen(
                    navController,
                    entry.arguments?.getString("id") ?: ""
                )
            }
            composable(
                Dest.BookVehicle.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                com.gotourntravels.ui.screens.customer.BookVehicleScreen(
                    navController,
                    entry.arguments?.getString("id") ?: ""
                )
            }
            composable(
                Dest.BookingSummary.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                com.gotourntravels.ui.screens.customer.BookingSummaryScreen(
                    navController,
                    entry.arguments?.getString("id") ?: ""
                )
            }
            composable(
                Dest.ActiveBooking.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                com.gotourntravels.ui.screens.customer.ActiveBookingScreen(
                    navController,
                    entry.arguments?.getString("id") ?: ""
                )
            }
            composable(Dest.Bookings.route) { com.gotourntravels.ui.screens.customer.BookingHistoryScreen(navController) }
            composable(Dest.Payments.route) { com.gotourntravels.ui.screens.customer.PaymentsScreen(navController) }
            composable(Dest.TouristMap.route) { com.gotourntravels.ui.screens.customer.TouristMapScreen(navController) }
            composable(Dest.NearbyPetrol.route) { com.gotourntravels.ui.screens.customer.NearbyPlacesScreen(navController, "petrol") }
            composable(Dest.NearbyHospitals.route) { com.gotourntravels.ui.screens.customer.NearbyPlacesScreen(navController, "hospital") }
            composable(Dest.NearbyPolice.route) { com.gotourntravels.ui.screens.customer.NearbyPlacesScreen(navController, "police") }
            composable(Dest.TouristAttractions.route) { com.gotourntravels.ui.screens.customer.TouristAttractionsScreen(navController) }
            composable(Dest.Sos.route) { com.gotourntravels.ui.screens.customer.SosScreen(navController) }
            composable(Dest.Notifications.route) { com.gotourntravels.ui.screens.customer.NotificationsScreen(navController) }
            composable(Dest.Profile.route) { com.gotourntravels.ui.screens.customer.ProfileScreen(navController) }
            composable(Dest.Settings.route) { com.gotourntravels.ui.screens.customer.SettingsScreen(navController) }
            composable(Dest.HelpSupport.route) { com.gotourntravels.ui.screens.customer.HelpSupportScreen(navController) }
            composable(Dest.About.route) { com.gotourntravels.ui.screens.customer.AboutScreen(navController) }

            // ---------- Admin ----------
            composable(Dest.AdminDashboard.route) { com.gotourntravels.ui.screens.admin.AdminDashboardScreen(navController) }
            composable(Dest.AdminVehicles.route) { com.gotourntravels.ui.screens.admin.VehicleManagementScreen(navController) }
            composable(Dest.AdminAddVehicle.route) { com.gotourntravels.ui.screens.admin.AddEditVehicleScreen(navController, null) }
            composable(
                Dest.AdminEditVehicle.route,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { entry ->
                com.gotourntravels.ui.screens.admin.AddEditVehicleScreen(
                    navController,
                    entry.arguments?.getString("id")
                )
            }
            composable(Dest.AdminBookings.route) { com.gotourntravels.ui.screens.admin.BookingManagementScreen(navController) }
            composable(Dest.AdminCustomers.route) { com.gotourntravels.ui.screens.admin.CustomerManagementScreen(navController) }
            composable(Dest.AdminEarnings.route) { com.gotourntravels.ui.screens.admin.EarningsDashboardScreen(navController) }
            composable(Dest.AdminAnalytics.route) { com.gotourntravels.ui.screens.admin.AnalyticsScreen(navController) }
            composable(Dest.AdminSos.route) { com.gotourntravels.ui.screens.admin.SosRequestsScreen(navController) }
            composable(Dest.AdminReviews.route) { com.gotourntravels.ui.screens.admin.ReviewsScreen(navController) }
            composable(Dest.AdminAds.route) { com.gotourntravels.ui.screens.admin.AdvertisementManagementScreen(navController) }
            composable(Dest.AdminBusinessProfile.route) { com.gotourntravels.ui.screens.admin.BusinessProfileScreen(navController) }
        }
    }
}
