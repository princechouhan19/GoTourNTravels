package com.gotourntravels.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.GoTourTopBar
import com.gotourntravels.ui.components.LoadingBlock
import com.gotourntravels.ui.components.MapMarker
import com.gotourntravels.ui.components.MapplsMapView
import com.gotourntravels.viewmodel.BookingViewModel
import com.mappls.sdk.maps.geometry.LatLng

/** Admin fleet view. Locations are sent by the active-rental tracking service. */
@Composable
fun LiveVehicleTrackingScreen(navController: NavController) {
    val vm: BookingViewModel = hiltViewModel()
    val bookings by vm.active.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) { vm.loadActive() }

    val located = bookings.filter { it.tracking.currentLat != null && it.tracking.currentLng != null }
    val center = located.firstOrNull()?.let { LatLng(it.tracking.currentLat!!, it.tracking.currentLng!!) }
        ?: LatLng(24.5925, 72.7078)

    Column(Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Live Vehicle Tracking", onBack = { navController.popBackStack() }, actions = {
            IconButton(onClick = { vm.loadActive() }) {
                Icon(Icons.Default.Refresh, "Refresh live locations")
            }
        })
        if (loading && bookings.isEmpty()) {
            LoadingBlock()
            return@Column
        }
        Column(Modifier.fillMaxSize().padding(16.dp)) {
            Text("${located.size} vehicle(s) reporting live location", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(10.dp))
            Card(Modifier.fillMaxWidth().height(260.dp), shape = RoundedCornerShape(16.dp)) {
                MapplsMapView(
                    center = center,
                    zoom = if (located.size > 1) 11.0 else 14.0,
                    markers = located.map { booking ->
                        MapMarker(
                            LatLng(booking.tracking.currentLat!!, booking.tracking.currentLng!!),
                            booking.vehicle?.registrationNumber?.ifBlank { booking.vehicle?.name ?: "Vehicle" } ?: "Vehicle",
                            "${booking.vehicle?.name ?: "Vehicle"} • ${booking.user?.name ?: "Customer"}"
                        )
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
            Spacer(Modifier.height(14.dp))
            if (bookings.isEmpty()) {
                Text("No active rentals are being tracked.", style = MaterialTheme.typography.bodyMedium)
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(bookings, key = { it.id }) { booking ->
                        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                            Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.MyLocation, null, tint = MaterialTheme.colorScheme.primary)
                                Spacer(Modifier.width(12.dp))
                                Column(Modifier.weight(1f)) {
                                    Text(booking.vehicle?.registrationNumber?.ifBlank { booking.vehicle?.name ?: "Vehicle" } ?: "Vehicle", fontWeight = FontWeight.Bold)
                                    Text(booking.vehicle?.name ?: "Vehicle", style = MaterialTheme.typography.bodySmall)
                                    Text("Rider: ${booking.user?.name ?: "—"}", style = MaterialTheme.typography.bodySmall)
                                }
                                Text(
                                    if (booking.tracking.currentLat != null) "Live" else "Awaiting GPS",
                                    color = if (booking.tracking.currentLat != null) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
