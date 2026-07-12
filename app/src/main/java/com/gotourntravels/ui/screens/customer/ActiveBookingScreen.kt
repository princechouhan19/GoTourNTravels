package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.theme.*
import com.gotourntravels.viewmodel.BookingViewModel
import com.mappls.sdk.maps.geometry.LatLng

@Composable
fun ActiveBookingScreen(navController: NavController, bookingId: String) {
    val vm: BookingViewModel = hiltViewModel()
    val booking by vm.detail.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()

    LaunchedEffect(bookingId) { vm.loadDetail(bookingId) }

    val b = booking
    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Active Rental", onBack = { navController.popBackStack() })
        if (loading && b == null) { LoadingBlock(); return@Column }
        if (b == null) return@Column

        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp).verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Big status banner
            Surface(
                color = Green.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.DirectionsCar, contentDescription = null, tint = Green, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(8.dp))
                    Text("Rental in progress", color = Green, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Booking #${b.bookingNumber}", style = MaterialTheme.typography.bodySmall)
                }
            }
            Spacer(Modifier.height(16.dp))

            b.vehicle?.let { v ->
                Surface(color = CreamDark, shape = RoundedCornerShape(12.dp), modifier = Modifier.fillMaxWidth()) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(v.name, fontWeight = FontWeight.Bold)
                            Text(v.registrationNumber, style = MaterialTheme.typography.bodySmall)
                        }
                        StatusChip(b.status)
                    }
                }
            }
            Spacer(Modifier.height(16.dp))

            // Timing info
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Trip timings", fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    InfoRow("Started", b.tracking.startedAt?.take(16)?.replace("T", " ") ?: "Awaiting activation")
                    InfoRow("Ends", b.endDate.take(16).replace("T", " "))
                    InfoRow("Pickup", b.pickupLocation?.address ?: "Go Tour N Travels Office")
                }
            }
            Spacer(Modifier.height(16.dp))

            // Mappls Live Location Map
            Card(
                modifier = Modifier.fillMaxWidth().height(180.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                val mapLat = b.tracking.currentLat ?: b.pickupLocation?.lat ?: 24.5925
                val mapLng = b.tracking.currentLng ?: b.pickupLocation?.lng ?: 72.7156
                val center = LatLng(mapLat, mapLng)
                val mapMarkers = listOf(
                    com.gotourntravels.ui.components.MapMarker(
                        position = center,
                        title = b.vehicle?.name ?: "Your Rental Vehicle",
                        snippet = "Live tracking location"
                    )
                )
                com.gotourntravels.ui.components.MapplsMapView(
                    center = center,
                    zoom = 14.0,
                    markers = mapMarkers,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(16.dp))

            // SOS
            Surface(
                color = Red.copy(alpha = 0.1f),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp))
            ) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Warning, contentDescription = null, tint = Red)
                    Spacer(Modifier.width(12.dp))
                    Text("In trouble on the road?", modifier = Modifier.weight(1f), fontWeight = FontWeight.SemiBold, color = Red)
                    TextButton(onClick = { navController.navigate(com.gotourntravels.ui.navigation.Dest.Sos.route) }) {
                        Text("SOS", color = Red, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Spacer(Modifier.height(20.dp))

            PrimaryButton("Call Business for Help", onClick = { /* TODO: dial BusinessSettings.phone via ACTION_CALL intent */ })
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)) {
        Text(label, modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(value, fontWeight = FontWeight.Medium)
    }
}
