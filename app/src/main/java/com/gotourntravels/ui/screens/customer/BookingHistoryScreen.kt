package com.gotourntravels.ui.screens.customer

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.ui.navigation.Dest
import com.gotourntravels.viewmodel.BookingViewModel

@Composable
fun BookingHistoryScreen(navController: NavController) {
    val vm: BookingViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val active by vm.active.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    val error by vm.error.collectAsStateWithLifecycle()
    var tab by remember { mutableStateOf(if (active.isEmpty()) "all" else "active") }

    LaunchedEffect(Unit) {
        vm.load()
        vm.loadActive()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "My Bookings")
        TabRow(selectedTabIndex = if (tab == "active") 0 else 1, containerColor = MaterialTheme.colorScheme.surface) {
            Tab(selected = tab == "active", onClick = { tab = "active" }, text = { Text("Active (${active.size})") })
            Tab(selected = tab == "all", onClick = { tab = "all" }, text = { Text("All (${items.size})") })
        }
        when {
            loading && items.isEmpty() -> LoadingBlock()
            error != null -> ErrorState(error!!, onRetry = { vm.load(); vm.loadActive() })
            tab == "active" && active.isEmpty() -> EmptyState("No active rentals", "Your active bookings will appear here", Icons.Default.DirectionsCar)
            tab == "all" && items.isEmpty() -> EmptyState("No bookings yet", "Book your first ride from the home screen", Icons.Default.ReceiptLong)
            else -> {
                val list = if (tab == "active") active else items
                LazyColumn(modifier = Modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(list) { b ->
                        Card(
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp),
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                navController.navigate(if (b.status == "active") Dest.activeBooking(b.id) else Dest.bookingSummary(b.id))
                            }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                    Text("#${b.bookingNumber}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    StatusChip(b.status)
                                }
                                Spacer(Modifier.height(4.dp))
                                b.vehicle?.let { Text(it.name, style = MaterialTheme.typography.bodyMedium) }
                                Text("${b.startDate.take(10)} → ${b.endDate.take(10)}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Spacer(Modifier.height(4.dp))
                                Row {
                                    Text("₹${b.pricing.total.toInt()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                                    Text(if (b.status == "active") "Tap to track →" else "Tap to view →", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
