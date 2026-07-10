package com.gotourntravels.ui.screens.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.gotourntravels.ui.components.*
import com.gotourntravels.viewmodel.BookingViewModel

@Composable
fun BookingManagementScreen(navController: NavController) {
    val vm: BookingViewModel = hiltViewModel()
    val items by vm.items.collectAsStateWithLifecycle()
    val detail by vm.detail.collectAsStateWithLifecycle()
    val loading by vm.loading.collectAsStateWithLifecycle()
    var statusFilter by remember { mutableStateOf<String?>(null) }
    var selectedId by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) { vm.load() }

    Column(modifier = Modifier.fillMaxSize()) {
        GoTourTopBar(title = "Booking Management", onBack = { navController.popBackStack() })
        Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
            // Filter chips
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All" to null, "Pending" to "pending", "Confirmed" to "confirmed", "Active" to "active", "Completed" to "completed", "Cancelled" to "cancelled").forEach { (label, key) ->
                    FilterChip(selected = statusFilter == key, onClick = { statusFilter = key; vm.load(key) }, label = { Text(label) })
                }
            }
            Spacer(Modifier.height(12.dp))
            when {
                loading && items.isEmpty() -> LoadingBlock()
                items.isEmpty() -> EmptyState("No bookings", "Bookings will appear here", Icons.Default.Receipt)
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { b ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            onClick = {
                                selectedId = b.id
                                vm.loadDetail(b.id)
                            }
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text("#${b.bookingNumber}", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                                    StatusChip(b.status)
                                    StatusChip(b.paymentStatus)
                                }
                                Spacer(Modifier.height(4.dp))
                                b.vehicle?.let { Text(it.name, style = MaterialTheme.typography.bodyMedium) }
                                b.user?.let { Text("${it.name} • ${it.phone}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant) }
                                Text("${b.startDate.take(10)} → ${b.endDate.take(10)}", style = MaterialTheme.typography.bodySmall)
                                Text("₹${b.pricing.total.toInt()}", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)

                                // Admin actions
                                if (b.id == selectedId && detail?.id == b.id) {
                                    Spacer(Modifier.height(8.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        if (b.status == "pending" || b.status == "confirmed") {
                                            AssistChip(onClick = { vm.activate(b.id) }, label = { Text("Activate") }, leadingIcon = { Icon(Icons.Default.PlayArrow, contentDescription = null, modifier = Modifier.size(14.dp)) })
                                        }
                                        if (b.status == "active") {
                                            AssistChip(onClick = { vm.complete(b.id, 0, 0) }, label = { Text("Complete") }, leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp)) })
                                        }
                                        if (b.status != "cancelled" && b.status != "completed") {
                                            AssistChip(onClick = { vm.cancel(b.id, "Cancelled by admin") { vm.load(statusFilter) } }, label = { Text("Cancel") }, leadingIcon = { Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp)) })
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
