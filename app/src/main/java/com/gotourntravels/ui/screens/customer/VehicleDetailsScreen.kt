package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.VehiclesViewModel

@Composable
fun VehicleDetailsScreen(navController: NavController, vehicleId: String) {
    val vm: VehiclesViewModel = hiltViewModel()
    val vehicle by vm.vehicle.collectAsStateWithLifecycle()
    val reviews by vm.reviews.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()

    LaunchedEffect(vehicleId) { vm.loadVehicle(vehicleId) }

    Scaffold(
        bottomBar = {
            vehicle?.let { v ->
                Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                    Row(modifier = Modifier.fillMaxWidth().padding(16.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("₹${v.dailyRate}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold, fontSize = 20.sp)
                            Text("per day  •  ₹${v.hourlyRate}/hr", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        PrimaryButton("Book Now", modifier = Modifier.weight(1.4f)) {
                            navController.navigate(Dest.bookVehicle(v.id))
                        }
                    }
                }
            }
        }
    ) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (loading && vehicle == null) { LoadingBlock(); return@Column }
            if (error != null && vehicle == null) { ErrorState(error ?: "") { vm.loadVehicle(vehicleId) }; return@Column }
            vehicle?.let { v ->
                val scroll = rememberScrollState()
                Column(modifier = Modifier.verticalScroll(scroll)) {
                    Box {
                        AsyncImage(
                            model = v.primaryImage.ifBlank { v.images.firstOrNull().orEmpty() },
                            contentDescription = v.name,
                            modifier = Modifier.fillMaxWidth().height(280.dp),
                            contentScale = ContentScale.Crop
                        )
                        IconButton(
                            onClick = { navController.popBackStack() },
                            modifier = Modifier.statusBarsPadding().padding(8.dp).clip(RoundedCornerShape(50)).background(Maroon)
                        ) {
                            Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                        }
                    }
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(v.name, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                                Text("${v.brand} ${v.model} • ${v.year}", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            StatusChip(status = v.status)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = Gold)
                            Text(" ${v.rating} ", fontWeight = FontWeight.SemiBold)
                            Text("(${v.reviewsCount} reviews)  •  ${v.totalBookings} bookings", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("About this vehicle", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(4.dp))
                        Text(v.description, style = MaterialTheme.typography.bodyMedium)
                        Spacer(Modifier.height(16.dp))
                        Text("Specifications", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        SpecRow("Fuel Type", v.fuelType.replaceFirstChar { it.uppercase() })
                        SpecRow("Transmission", v.transmission.replaceFirstChar { it.uppercase() })
                        SpecRow("Seating", "${v.seatingCapacity} persons")
                        SpecRow("Color", v.color.ifBlank { "—" })
                        if (v.withDriver) SpecRow("Driver", "${v.driverName} • ${v.driverPhone}")
                        SpecRow("Registration", v.registrationNumber)

                        if (v.features.isNotEmpty()) {
                            Spacer(Modifier.height(16.dp))
                            Text("Features", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                            Spacer(Modifier.height(8.dp))
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(v.features) { f -> FeatureChip(f) }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text("Pricing", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        PricingRow("Hourly", "₹${v.hourlyRate}/hr")
                        PricingRow("Daily", "₹${v.dailyRate}/day")
                        PricingRow("Weekly", "₹${v.weeklyRate}/week")
                        PricingRow("Security Deposit", "₹${v.securityDeposit} (refundable)")

                        if (reviews.isNotEmpty()) {
                            Spacer(Modifier.height(20.dp))
                            SectionHeader("Reviews", subtitle = "${reviews.size} customer reviews")
                            reviews.take(5).forEach { r ->
                                Surface(
                                    color = CreamDark,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                                ) {
                                    Column(modifier = Modifier.padding(12.dp)) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(r.user?.name ?: "Anonymous", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                            repeat(r.rating) { Icon(Icons.Default.Star, contentDescription = null, tint = Gold, modifier = Modifier.size(14.dp)) }
                                        }
                                        Text(r.comment, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                        Spacer(Modifier.height(40.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun SpecRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant, style = MaterialTheme.typography.bodyMedium)
        Text(value, fontWeight = FontWeight.Medium, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PricingRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
    }
}
